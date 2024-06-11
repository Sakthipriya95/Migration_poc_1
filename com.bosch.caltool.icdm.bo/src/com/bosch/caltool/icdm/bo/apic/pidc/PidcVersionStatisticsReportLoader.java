/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseDetailsModelLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader;
import com.bosch.caltool.icdm.common.bo.uc.FavUseCaseItemCommon;
import com.bosch.caltool.icdm.common.bo.uc.FavUseCaseItemNodeCommon;
import com.bosch.caltool.icdm.common.bo.uc.IUsecaseItemCommonBO;
import com.bosch.caltool.icdm.common.bo.uc.UseCaseCommonDataHandler;
import com.bosch.caltool.icdm.common.bo.uc.UseCaseFavNodesMgrCommon;
import com.bosch.caltool.icdm.common.bo.uc.UseCaseGroupCommonBO;
import com.bosch.caltool.icdm.common.bo.uc.UsecaseCommonBO;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.FM_VERS_STATUS;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.apic.attr.MandatoryAttr;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionStatisticsReport;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.uc.UsecaseDetailsModel;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;

/**
 * @author dmr1cob
 */
public class PidcVersionStatisticsReportLoader extends AbstractSimpleBusinessObject {

  private PidcVersionWithDetails pidcVersionWithDetails;
  private final PidcVersion pidcVersion;
  private UseCaseCommonDataHandler useCaseCommonDataHandler;
  /**
   * Number of use project case items (last node)
   */
  private final Set<IUsecaseItemCommonBO> endUcItems = new HashSet<>();

  /**
   * @param servData serviceData
   */
  public PidcVersionStatisticsReportLoader(final ServiceData servData, final PidcVersion pidcVersion) {
    super(servData);
    this.pidcVersion = pidcVersion;
  }


  /**
   * @return {@link PidcVersionStatisticsReport}
   * @throws ParseException Exception
   * @throws IcdmException Exception
   */
  public PidcVersionStatisticsReport createStatResponse() throws ParseException, IcdmException {

    this.pidcVersionWithDetails =
        new PidcVersionLoader(getServiceData()).getPidcVersionWithDetails(this.pidcVersion.getId());

    PidcVersionStatisticsReport statisticReport = new PidcVersionStatisticsReport();
    statisticReport.setTotalAttributes(this.pidcVersionWithDetails.getPidcVersionAttributeDefinedMap().size());
    statisticReport.setLastModifiedDate(getLastModifiedDate());
    statisticReport.setUsedAttributes(getUsedAttributesCount().size());
    statisticReport.setNotUsedAttributes(getNotUsedAttributesCount().size());
    statisticReport.setNotDefinedAttribute(getNotDefinedAttributesCount().size());
    fillStatisticsData(statisticReport);

    return statisticReport;
  }

  /**
   * @param pidcVariant PidcVariant
   * @return {@link PidcVersionStatisticsReport}
   * @throws ParseException Exception
   * @throws IcdmException Exception
   */
  public PidcVersionStatisticsReport createVarStatResponse(final PidcVariant pidcVariant)
      throws ParseException, IcdmException {

    this.pidcVersionWithDetails =
        new PidcVersionLoader(getServiceData()).getPidcVersionWithDetails(this.pidcVersion.getId());

    PidcVersionStatisticsReport statisticReport = new PidcVersionStatisticsReport();
    statisticReport.setTotalAttributes(this.pidcVersionWithDetails.getPidcVersionAttributeDefinedMap().size());
    statisticReport.setLastModifiedDate(getLastModifiedDateForVariant(pidcVariant));
    statisticReport.setUsedAttributes(getUsedAttributesCountForVariant(pidcVariant));
    statisticReport.setNotUsedAttributes(getNotUsedAttributesCountForVariant(pidcVariant));
    statisticReport.setNotDefinedAttribute(getNotDefinedAttributesCountForVariant(pidcVariant));
    fillStatisticsDataForVariant(statisticReport, pidcVariant);

    return statisticReport;
  }


  /**
   * @param pidcVersion
   * @return Returns Modified Date of {@link PidcVersion}
   * @throws ParseException Exception
   */
  public Date getLastModifiedDate() throws ParseException {
    Date modifiedDate = null;
    if (this.pidcVersion.getModifiedDate() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_15, Locale.getDefault(Locale.Category.FORMAT));
      modifiedDate = sdf.parse(this.pidcVersion.getModifiedDate());
    }
    return modifiedDate;
  }

  /**
   * @param pidcVariant PidcVariant
   * @return Returns Modified Date of {@link PidcVersion}
   * @throws ParseException Exception
   */
  private Date getLastModifiedDateForVariant(final PidcVariant pidcVariant) throws ParseException {
    Date modifiedDate = null;
    if (pidcVariant.getModifiedDate() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_15, Locale.getDefault(Locale.Category.FORMAT));
      modifiedDate = sdf.parse(pidcVariant.getModifiedDate());
    }
    return modifiedDate;
  }

  /**
   * @param pidcVersionWithDetails {@link PidcVersionWithDetails}object
   * @return {@link PidcVersionAttribute}Map
   */
  public Map<Long, PidcVersionAttribute> getUsedAttributesCount() {
    Map<Long, PidcVersionAttribute> usedPIDCAttrMap = new HashMap<>();
    for (PidcVersionAttribute attribute : this.pidcVersionWithDetails.getPidcVersionAttributeMap().values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()))) {
        usedPIDCAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return usedPIDCAttrMap;
  }

  /**
   * @param pidcVariant {@link PidcVariant}object
   * @return int
   * @throws IcdmException
   */
  private int getUsedAttributesCountForVariant(final PidcVariant pidcVariant) throws IcdmException {
    int usedAttrForVariant = 0;

    for (PidcVersionAttribute attribute : this.pidcVersionWithDetails.getPidcVersionAttributeMap().values()) {
      if (attribute.isAtChildLevel()) {
        // If the attribute is at variant level , check if it is used in variant
        PidcVariantAttribute pidcVarAttr = getVariantAttrMap(false, pidcVariant).get(attribute.getAttrId());
        // if attr is visible at pidc level and invisble at variant level, then attr is not relevant and cannot be
        // considered for count
        if ((null != pidcVarAttr) && isVisible(pidcVarAttr) && isAttributeUsedInVariant(pidcVarAttr)) {
          usedAttrForVariant++;
        }
      }
      else if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()))) {
        usedAttrForVariant++;
      }
    }
    return usedAttrForVariant;
  }


  /**
   * @param pidcVarAttr
   * @return boolean
   */
  private boolean isAttributeUsedInVariant(final PidcVariantAttribute pidcVarAttr) {
    return ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(pidcVarAttr.getUsedFlag()) &&
        !isValueInvalid(this.pidcVersionWithDetails.getAttributeValueMap().get(pidcVarAttr.getValueId()));
  }

  /**
   * @return {@link PidcVersionAttribute}Map
   */
  public Map<Long, PidcVersionAttribute> getNotUsedAttributesCount() {
    Map<Long, PidcVersionAttribute> notUsedPIDCAttrMap = new HashMap<>();
    for (PidcVersionAttribute attribute : this.pidcVersionWithDetails.getPidcVersionAttributeMap().values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()))) {
        notUsedPIDCAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return notUsedPIDCAttrMap;
  }

  /**
   * @param pidcVariant {@link PidcVariant}object
   * @return int
   * @throws IcdmException
   */
  private int getNotUsedAttributesCountForVariant(final PidcVariant pidcVariant) throws IcdmException {
    int notUsedAttrForVariant = 0;
    for (PidcVersionAttribute attribute : this.pidcVersionWithDetails.getPidcVersionAttributeMap().values()) {
      if (attribute.isAtChildLevel()) {
        // If the attribute is at variant level , check if it is used in variant
        PidcVariantAttribute pidcVarAttr = getVariantAttrMap(false, pidcVariant).get(attribute.getAttrId());
        // if attr is visible at pidc level and invisble at variant level, then attr is not relevant and cannot be
        // considered for count
        if ((null != pidcVarAttr) && isVisible(pidcVarAttr) && isAttributeNotUsedInVariant(pidcVarAttr)) {
          notUsedAttrForVariant++;
        }
      }
      else if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(attribute.getUsedFlag()) &&
          !isValueInvalid(this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()))) {
        notUsedAttrForVariant++;
      }
    }
    return notUsedAttrForVariant;
  }


  /**
   * @param attribute
   * @return boolean
   */
  private boolean isAttributeNotUsedInVariant(final PidcVariantAttribute attribute) {
    return ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(attribute.getUsedFlag()) &&
        !isValueInvalid(this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()));
  }

  /**
   * @param pidcVersionWithDetails
   * @return {@link PidcVersionAttribute}Map
   */
  public Map<Long, PidcVersionAttribute> getNotDefinedAttributesCount() {
    Map<Long, PidcVersionAttribute> usedNotDefindPIDCAttrMap = new HashMap<>();
    // Get all used not defined PIDC attributes
    for (PidcVersionAttribute attribute : this.pidcVersionWithDetails.getPidcVersionAttributeMap().values()) {
      if (CommonUtils.isEqual(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType(), attribute.getUsedFlag()) ||
          isValueInvalid(this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()))) {
        usedNotDefindPIDCAttrMap.put(attribute.getAttrId(), attribute);
      }
    }
    return usedNotDefindPIDCAttrMap;
  }

  /**
   * @param pidcVariant {@link PidcVariant}object
   * @return int
   * @throws IcdmException
   */
  private int getNotDefinedAttributesCountForVariant(final PidcVariant pidcVariant) throws IcdmException {
    int notDefinedAttrForVariant = 0;
    for (PidcVersionAttribute attribute : this.pidcVersionWithDetails.getPidcVersionAttributeMap().values()) {
      if (attribute.isAtChildLevel()) {
        // If the attribute is at variant level , check if it is used in variant
        PidcVariantAttribute pidcVarAttr = getVariantAttrMap(false, pidcVariant).get(attribute.getAttrId());
        // if attr is visible at pidc level and invisble at variant level, then attr is not relevant and cannot be
        // considered for count
        if ((null != pidcVarAttr) && isVisible(pidcVarAttr) && isAttributeNotDefinedInVariant(pidcVarAttr)) {
          notDefinedAttrForVariant++;
        }
      }
      else if (CommonUtils.isEqual(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType(),
          attribute.getUsedFlag()) ||
          isValueInvalid(this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()))) {
        notDefinedAttrForVariant++;
      }
    }
    return notDefinedAttrForVariant;
  }


  /**
   * @param attribute
   * @return
   */
  private boolean isAttributeNotDefinedInVariant(final PidcVariantAttribute attribute) {
    return ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType().equals(attribute.getUsedFlag()) &&
        !isValueInvalid(this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()));
  }


  /**
   * @param attributeValue
   * @return boolean
   */
  private boolean isValueInvalid(final AttributeValue attrValue) {
    return CommonUtils.isNotNull(attrValue) && attrValue.isDeleted();
  }

  /**
   * @param pidcVersionWithDetails
   * @param pidcVersionWithDetails
   * @param statisticReport
   * @throws IcdmException
   */
  private void fillStatisticsData(final PidcVersionStatisticsReport statisticReport) throws IcdmException {
    int newAttrCount = 0;
    int totalMandatoryAttrCountOnly = 0;
    int totalMandtryAttrUsedCountOnly = 0;
    int importantAttrCount = 0;
    int usedFlagSetImpAttrCount = 0;
    int focusMatrixApplicableAttrCount = 0;
    int focusMatrixRatedAttrCount = 0;
    this.useCaseCommonDataHandler = new UseCaseCommonDataHandler(this.pidcVersion);
    UsecaseDetailsModelLoader ucModelLoader = new UsecaseDetailsModelLoader(getServiceData());
    UsecaseDetailsModel ucDetailsModel = ucModelLoader.getUsecaseDetailsModel();
    this.useCaseCommonDataHandler.setUseCaseDetailsModel(ucDetailsModel);
    UsecaseFavoriteLoader useCaseFavLoader = new UsecaseFavoriteLoader(getServiceData());
    Map<Long, UsecaseFavorite> ucMap = useCaseFavLoader.getProjFavoriteUseCases(this.pidcVersion.getPidcId());
    UseCaseFavNodesMgrCommon useCaseFavNodeMgr =
        new UseCaseFavNodesMgrCommon(this.pidcVersion, this.useCaseCommonDataHandler, ucMap);
    this.useCaseCommonDataHandler.setUcFavMgr(useCaseFavNodeMgr);
    Set<FavUseCaseItemNodeCommon> rootUcFavNodes = new HashSet<>(this.useCaseCommonDataHandler.getRootUcFavNodes());
    Set<IUsecaseItemCommonBO> mappableFavUcItemSet = getMappableFavouriteUcItem(ucMap);
    Map<PidcVersionAttribute, Attribute> fmApplAttrMap = getFocusMatrixApplicableAttrMap();

    for (Entry<Long, PidcVersionAttribute> projAttrEntry : this.pidcVersionWithDetails
        .getPidcVersionAttributeDefinedMap().entrySet()) {
      PidcVersionAttribute pidcAttr = projAttrEntry.getValue();
      newAttrCount = getNewAttributeCount(newAttrCount, pidcAttr);

      // Relevant attributes (Mandatory OR attributes mapped to project use case items
      boolean mappedToProjAttr = isMappedToProjAttr(rootUcFavNodes, pidcAttr);

      boolean isVisible = isVisible(pidcAttr);

      boolean isMandatoryAttrFlag = isMandatory(pidcAttr);
      boolean isValueDefinedFlag = isValueDefinedVariant(pidcAttr);
      if (isMandatoryAttrFlag) {
        totalMandatoryAttrCountOnly++;
        totalMandtryAttrUsedCountOnly =
            getVisibleAndValueDefinedAttrCount(totalMandtryAttrUsedCountOnly, isVisible, isValueDefinedFlag);
      }
      if (isMandatoryAttrFlag || mappedToProjAttr) {
        importantAttrCount++;
        usedFlagSetImpAttrCount =
            getVisibleAndValueDefinedAttrCount(usedFlagSetImpAttrCount, isVisible, isValueDefinedFlag);
      }
      // Attributes marked as focus matrix relevant
      if (fmApplAttrMap.containsKey(pidcAttr) && pidcAttr.isFocusMatrixApplicable()) {
        focusMatrixApplicableAttrCount++;
        focusMatrixRatedAttrCount = checkRatedAttribute(mappableFavUcItemSet, pidcAttr, focusMatrixRatedAttrCount);
      }
    }
    statisticReport.setNewAttributes(newAttrCount);
    statisticReport.setTotalMandatoryAttrCountOnly(totalMandatoryAttrCountOnly);
    statisticReport.setTotalMandtryAttrUsedCountOnly(totalMandtryAttrUsedCountOnly);
    statisticReport.setMandateOrProjectUcAttrUsedCount(usedFlagSetImpAttrCount);
    statisticReport.setMandateOrProjectUcAttr(importantAttrCount);
    statisticReport.setProjectUseCases(this.endUcItems.size());
    statisticReport.setFocusMatrixApplicabeAttributes(focusMatrixApplicableAttrCount);
    statisticReport.setFocusMatrixRatedAttributes(focusMatrixRatedAttrCount);
    statisticReport.setFocusMatrixUnratedAttributes(focusMatrixApplicableAttrCount - focusMatrixRatedAttrCount);
  }


  /**
   * @param totalMandtryAttrUsedCountOnly
   * @param isVisible
   * @param isValueDefinedFlag
   * @return
   */
  private int getVisibleAndValueDefinedAttrCount(int count, final boolean isVisible, final boolean isValueDefinedFlag) {
    if (isVisible && isValueDefinedFlag) {
      count++;
    }
    return count;
  }

  /**
   * @param statisticReport
   * @param pidcVariant
   * @throws IcdmException
   */
  private void fillStatisticsDataForVariant(final PidcVersionStatisticsReport statisticReport,
      final PidcVariant pidcVariant)
      throws IcdmException {
    int newAttrCount = 0;
    int totalMandatoryAttrCountOnly = 0;
    int totalMandtryAttrUsedCountOnly = 0;
    int importantAttrCount = 0;
    int usedFlagSetImpAttrCount = 0;
    int focusMatrixApplicableAttrCount = 0;
    int focusMatrixRatedAttrCount = 0;
    this.useCaseCommonDataHandler = new UseCaseCommonDataHandler(this.pidcVersion);
    UsecaseDetailsModelLoader ucModelLoader = new UsecaseDetailsModelLoader(getServiceData());
    UsecaseDetailsModel ucDetailsModel = ucModelLoader.getUsecaseDetailsModel();
    this.useCaseCommonDataHandler.setUseCaseDetailsModel(ucDetailsModel);
    UsecaseFavoriteLoader useCaseFavLoader = new UsecaseFavoriteLoader(getServiceData());
    Map<Long, UsecaseFavorite> ucMap = useCaseFavLoader.getProjFavoriteUseCases(this.pidcVersion.getPidcId());
    UseCaseFavNodesMgrCommon useCaseFavNodeMgr =
        new UseCaseFavNodesMgrCommon(this.pidcVersion, this.useCaseCommonDataHandler, ucMap);
    this.useCaseCommonDataHandler.setUcFavMgr(useCaseFavNodeMgr);
    Set<FavUseCaseItemNodeCommon> rootUcFavNodes = new HashSet<>(this.useCaseCommonDataHandler.getRootUcFavNodes());
    Set<IUsecaseItemCommonBO> mappableFavUcItemSet = getMappableFavouriteUcItem(ucMap);
    Map<PidcVersionAttribute, Attribute> fmApplAttrMap = getFocusMatrixApplicableAttrMap();

    for (Entry<Long, PidcVersionAttribute> projAttrEntry : this.pidcVersionWithDetails
        .getPidcVersionAttributeDefinedMap().entrySet()) {
      PidcVersionAttribute pidcAttr = projAttrEntry.getValue();
      // New Attributes
      newAttrCount = getNewAttributeCount(newAttrCount, pidcAttr);

      // Relevant attributes (Mandatory OR attributes mapped to project use case items
      boolean mappedToProjAttr = isMappedToProjAttr(rootUcFavNodes, pidcAttr);

      boolean isVisible = isVisible(pidcAttr);

      boolean isMandatoryAttrFlag = isMandatory(pidcAttr);
      boolean isValueDefinedFlag = isValueDefinedForCurrentVariant(pidcAttr, pidcVariant);
      if (isMandatoryAttrFlag) {
        totalMandatoryAttrCountOnly++;
        totalMandtryAttrUsedCountOnly =
            getVisibleAndValueDefinedAttrCount(totalMandtryAttrUsedCountOnly, isVisible, isValueDefinedFlag);
      }
      if (isMandatoryAttrFlag || mappedToProjAttr) {
        importantAttrCount++;
        usedFlagSetImpAttrCount =
            getVisibleAndValueDefinedAttrCount(usedFlagSetImpAttrCount, isVisible, isValueDefinedFlag);
      }
      // Attributes marked as focus matrix relevant
      if (fmApplAttrMap.containsKey(pidcAttr) && pidcAttr.isFocusMatrixApplicable()) {
        focusMatrixApplicableAttrCount++;
        focusMatrixRatedAttrCount = checkRatedAttribute(mappableFavUcItemSet, pidcAttr, focusMatrixRatedAttrCount);
      }
    }
    statisticReport.setNewAttributes(newAttrCount);
    statisticReport.setTotalMandatoryAttrCountOnly(totalMandatoryAttrCountOnly);
    statisticReport.setTotalMandtryAttrUsedCountOnly(totalMandtryAttrUsedCountOnly);
    statisticReport.setMandateOrProjectUcAttrUsedCount(usedFlagSetImpAttrCount);
    statisticReport.setMandateOrProjectUcAttr(importantAttrCount);
    statisticReport.setProjectUseCases(this.endUcItems.size());
    statisticReport.setFocusMatrixApplicabeAttributes(focusMatrixApplicableAttrCount);
    statisticReport.setFocusMatrixRatedAttributes(focusMatrixRatedAttrCount);
    statisticReport.setFocusMatrixUnratedAttributes(focusMatrixApplicableAttrCount - focusMatrixRatedAttrCount);
  }


  /**
   * @param newAttrCount
   * @param pidcAttr
   * @return
   */
  private int getNewAttributeCount(int newAttrCount, final PidcVersionAttribute pidcAttr) {
    if (pidcAttr.getId() == null) {
      newAttrCount++;
    }
    return newAttrCount;
  }


  /**
   * @param pidcAttr {@link PidcVersionAttribute} object
   * @return boolean
   */
  public boolean isMandatory(final PidcVersionAttribute pidcAttr) {

    String lvlAttrID = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.MANDATORY_LEVEL_ATTR);
    Long mandLvlAttrValId =
        this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(Long.parseLong(lvlAttrID)).getValueId();
    Map<Long, MandatoryAttr> mandAttrMap = this.pidcVersionWithDetails.getMandatoryAttrMap().get(mandLvlAttrValId);
    if (CommonUtils.isNotEmpty(mandAttrMap)) {
      for (MandatoryAttr mandAttr : mandAttrMap.values()) {
        if (mandAttr.getAttrId().equals(pidcAttr.getAttrId())) {
          return true;
        }
      }
    }
    else {
      return this.pidcVersionWithDetails.getAllAttributeMap().get(pidcAttr.getAttrId()).isMandatory();
    }
    return false;
  }

  /**
   * @param pidcAttr
   * @param pidcVersionWithDetails
   * @return
   * @throws IcdmException
   */
  private boolean isValueDefinedVariant(final PidcVersionAttribute pidcAttr) throws IcdmException {
    if (pidcAttr.isAtChildLevel()) {
      for (PidcVariant variant : getVariantsMap().values()) {
        PidcVariantAttribute pidcVarAttr = getVariantAttrMap(false, variant).get(pidcAttr.getAttrId());
        // if attr is visible at pidc level and invisble at variant level, then attr is not relevant and cannot be
        // considered as 'Not defined'
        if ((null == pidcVarAttr) || !isVisible(pidcVarAttr)) {
          continue;
        }
        if (!isValueDefinedSubVariant(pidcVarAttr, variant)) {
          return false;
        }
      }
      return true;
    }
    return ((ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(pidcAttr.getUsedFlag()) &&
        (pidcAttr.getValueId() != null)) ||
        (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedFlag())));
  }

  /**
   * @param pidcAttr
   * @param pidcVersionWithDetails
   * @return
   * @throws IcdmException
   */
  private boolean isValueDefinedForCurrentVariant(final PidcVersionAttribute pidcAttr, final PidcVariant pidcVariant)
      throws IcdmException {
    if (pidcAttr.isAtChildLevel()) {
      PidcVariantAttribute pidcVarAttr = getVariantAttrMap(false, pidcVariant).get(pidcAttr.getAttrId());
      // if attr is visible at pidc level and invisble at variant level, then attr is not relevant and cannot be
      // considered as 'Not defined'
      if ((null == pidcVarAttr) || !isVisible(pidcVarAttr)) {
        return false;
      }
      else if (isValueDefinedSubVariant(pidcVarAttr, pidcVariant)) {
        return true;
      }
      return false;
    }
    return ((ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(pidcAttr.getUsedFlag()) &&
        (pidcAttr.getValueId() != null)) ||
        (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedFlag())));
  }

  private boolean isValueDefinedSubVariant(final PidcVariantAttribute pidcAttributeVar, final PidcVariant variant)
      throws IcdmException {
    if (pidcAttributeVar.isAtChildLevel()) {
      for (PidcSubVariant subVar : getSubVariantsMap(variant).values()) {
        PidcSubVariantAttribute pidcSubVarAttr = getSubVariantAttrMap(false, subVar).get(pidcAttributeVar.getAttrId());
        // if attr is visible at variant and invisble at sub variant, then attr is not relevant and cannot be considered
        // as 'Not defined'
        if ((null == pidcSubVarAttr) || !isVisible(pidcSubVarAttr)) {
          continue;
        }
        if (!isValueDefined(pidcSubVarAttr)) {
          return false;
        }
      }
      return true;
    }
    return ((ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(pidcAttributeVar.getUsedFlag()) &&
        (pidcAttributeVar.getValue() != null)) ||
        (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttributeVar.getUsedFlag())));
  }

  /**
   * @param pidcSubVarAttr
   * @return
   */
  private boolean isValueDefined(final PidcSubVariantAttribute pidcSubVarAttr) {
    PidcSubVariantAttribute subVarAttr = pidcSubVarAttr;
    boolean ret = false;
    if ((ApicConstants.PROJ_ATTR_USED_FLAG.YES.equals(getIsUsedEnum(pidcSubVarAttr)) &&
        (subVarAttr.getValueId() != null)) ||
        (ApicConstants.PROJ_ATTR_USED_FLAG.NO.equals(getIsUsedEnum(pidcSubVarAttr)))) {
      ret = true;
    }
    return ret;
  }

  public ApicConstants.PROJ_ATTR_USED_FLAG getIsUsedEnum(final PidcSubVariantAttribute pidcSubVarAttr) {
    PROJ_ATTR_USED_FLAG returnValue;
    if (pidcSubVarAttr.getId() == null) {
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR;
    }
    else {
      String dbUsedInfo = pidcSubVarAttr.getUsedFlag();
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.getType(dbUsedInfo);
    }
    return returnValue;
  }


  private Map<Long, PidcSubVariantAttribute> getSubVariantAttrMap(final boolean includeDeleted,
      final PidcSubVariant subVariant)
      throws IcdmException {
    Map<Long, PidcSubVariantAttribute> pidcSubVarAttrMap = new HashMap<>();
    PidcSubVariantAttribute pidcSubVariantAttr;
    for (Attribute attribute : this.pidcVersionWithDetails.getAllAttributeMap().values()) {
      if (attribute != null) {
        // skip attributes marked as deleted
        if (!includeDeleted && attribute.isDeleted()) {
          continue;
        }
        Map<Long, PidcSubVariantAttribute> subvarAttrMap =
            this.pidcVersionWithDetails.getPidcSubVariantAttributeMap().get(subVariant.getId());
        if (null == subvarAttrMap) {
          PidcSubVariantAttributeLoader pidcVariantLoader = new PidcSubVariantAttributeLoader(getServiceData());
          subvarAttrMap = pidcVariantLoader.getSubVarAttrForSubVarId(subVariant.getId());
          this.pidcVersionWithDetails.getPidcSubVariantAttributeMap().put(subVariant.getId(), subvarAttrMap);

        }
        pidcSubVariantAttr = subvarAttrMap.get(attribute.getId());
        if (pidcSubVariantAttr != null) {
          pidcSubVarAttrMap.put(pidcSubVariantAttr.getAttrId(), pidcSubVariantAttr);
        }
      }
    }

    return pidcSubVarAttrMap;
  }

  private Map<Long, PidcVariantAttribute> getVariantAttrMap(final boolean includeDeleted, final PidcVariant variant)
      throws IcdmException {
    Map<Long, PidcVariantAttribute> pidcVarAttrMap = new HashMap<>();
    PidcVariantAttribute pidcVariantAttr;
    for (Attribute attribute : this.pidcVersionWithDetails.getAllAttributeMap().values()) {
      if (attribute != null) {
        // skip attributes marked as deleted
        if (!includeDeleted && attribute.isDeleted()) {
          continue;
        }
        Map<Long, PidcVariantAttribute> varAttrMap =
            this.pidcVersionWithDetails.getPidcVariantAttributeMap().get(variant.getId());

        if (null == varAttrMap) {
          PidcVariantAttributeLoader pidcVariantAttrLoader = new PidcVariantAttributeLoader(getServiceData());
          varAttrMap = pidcVariantAttrLoader.getVarAttrForVariant(variant.getId());
          this.pidcVersionWithDetails.getPidcVariantAttributeMap().put(variant.getId(), varAttrMap);
        }
        pidcVariantAttr = varAttrMap.get(attribute.getId());
        if (pidcVariantAttr != null) {
          pidcVarAttrMap.put(pidcVariantAttr.getAttrId(), pidcVariantAttr);
        }
      }
    }
    return pidcVarAttrMap;
  }

  /**
   * @param pidcVersionWithDetails
   * @param b
   * @param c
   * @return
   */
  private Map<Long, PidcVariant> getVariantsMap() {
    final Map<Long, PidcVariant> pidcVariantsMapIterate = this.pidcVersionWithDetails.getPidcVariantMap();
    final Map<Long, PidcVariant> pidcVariantsMap = new ConcurrentHashMap<>();
    for (PidcVariant pidcVariant : pidcVariantsMapIterate.values()) {
      if (!pidcVariant.isDeleted()) {
        pidcVariantsMap.put(pidcVariant.getId(), pidcVariant);
      }
    }
    return pidcVariantsMap;
  }

  private Map<Long, PidcSubVariant> getSubVariantsMap(final PidcVariant variant) {
    Map<Long, PidcSubVariant> pidcSubVariantsMapIterate = new ConcurrentHashMap<>();
    for (PidcSubVariant subVar : this.pidcVersionWithDetails.getPidcSubVariantMap().values()) {
      if (subVar.getPidcVariantId().equals(variant.getId())) {
        pidcSubVariantsMapIterate.put(subVar.getId(), subVar);
      }
    }
    final Map<Long, PidcSubVariant> pidcSubVariantMap = new ConcurrentHashMap<>();
    for (PidcSubVariant pidcSubVariant : pidcSubVariantsMapIterate.values()) {
      if (!pidcSubVariant.isDeleted()) {
        pidcSubVariantMap.put(pidcSubVariant.getId(), pidcSubVariant);
      }
    }
    return pidcSubVariantMap;
  }

  private Set<IUsecaseItemCommonBO> getMappableFavouriteUcItem(final Map<Long, UsecaseFavorite> ucMap) {
    Set<IUsecaseItemCommonBO> projFavUcMappableItemSet = new HashSet<>();
    for (FavUseCaseItemCommon projFavUcItem : fillProjFavMap(ucMap).values()) {
      IUsecaseItemCommonBO ucItemCommonBo = projFavUcItem.getUseCaseItem(this.useCaseCommonDataHandler);
      if (ucItemCommonBo instanceof UseCaseGroupCommonBO) {
        addChildItems(ucItemCommonBo, projFavUcMappableItemSet);
      }
      else {

        if (ucItemCommonBo instanceof UsecaseCommonBO) {
          ((UsecaseCommonBO) ucItemCommonBo)
              .setUsecaseEditorModel(this.useCaseCommonDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap()
                  .get(projFavUcItem.getUseCaseItem(this.useCaseCommonDataHandler).getUcItem().getId()));
        }
        projFavUcMappableItemSet.addAll(ucItemCommonBo.getMappableItems());
        this.endUcItems.addAll(ucItemCommonBo.getMappableItems());
      }
    }

    return projFavUcMappableItemSet;
  }

  private Map<Long, FavUseCaseItemCommon> fillProjFavMap(final Map<Long, UsecaseFavorite> ucMap) {
    Map<Long, FavUseCaseItemCommon> favMap = new HashMap<>();
    for (Entry<Long, UsecaseFavorite> useCaseFavEntry : ucMap.entrySet()) {
      favMap.put(useCaseFavEntry.getKey(),
          new FavUseCaseItemCommon(useCaseFavEntry.getValue(), this.useCaseCommonDataHandler));
    }
    return favMap;
  }

  /**
   * Get child items and add relevant usecase items to the model
   *
   * @param projFavUcMappableItemSet
   * @param endUcItems Set to hold the number of project use case items(last node is considered)
   */
  private void addChildItems(final IUsecaseItemCommonBO useCaseGrp,
      final Set<IUsecaseItemCommonBO> projFavUcMappableItemSet) {
    UseCaseGroupCommonBO useCaseGrpCommonBo = (UseCaseGroupCommonBO) useCaseGrp;
    SortedSet<UseCaseGroupCommonBO> childUcSet = useCaseGrpCommonBo.getChildGroupSet(false);
    if (CommonUtils.isNullOrEmpty(childUcSet)) {

      // add child usecase items if there are any
      for (IUsecaseItemCommonBO useCaseItem : useCaseGrpCommonBo.getChildUCItems()) {
        if (useCaseItem instanceof UsecaseCommonBO) {
          ((UsecaseCommonBO) useCaseItem).setUsecaseEditorModel(this.useCaseCommonDataHandler.getUseCaseDetailsModel()
              .getUsecaseDetailsModelMap().get(useCaseItem.getUcItem().getId()));
        }
        for (IUsecaseItemCommonBO uc : useCaseItem.getMappableItems()) {
          // ICDM-2461
          this.endUcItems.add(uc);
          addProjFavUcMapping(projFavUcMappableItemSet, uc);
        }
      }
    }
    else {
      UseCaseGroupCommonBO useCaseGrpClientBo2 = (UseCaseGroupCommonBO) useCaseGrp;
      for (UseCaseGroupCommonBO grpChild : useCaseGrpClientBo2.getChildGroupSet(false)) {
        // add child items of usecase groups
        addChildItems(grpChild, projFavUcMappableItemSet);
      }
    }

  }


  /**
   * @param projFavUcMappableItemSet
   * @param uc
   */
  private void addProjFavUcMapping(final Set<IUsecaseItemCommonBO> projFavUcMappableItemSet,
      final IUsecaseItemCommonBO uc) {
    if (uc.isFocusMatrixRelevant(true)) {
      // add to the list in the model only in case of focus matrix relevant & project specific use case
      projFavUcMappableItemSet.add(uc);
    }
  }


  /**
   * Checkes whether the PIDC attribute is mapped to any of the given project use case nodes
   *
   * @param mappableFavUcItemSet use case items
   * @param pidcAttr PIDC Attribute
   * @return true if mapped
   */
  private boolean isMappedToProjAttr(final Set<FavUseCaseItemNodeCommon> rootUcFavNodes,
      final PidcVersionAttribute pidcAttr) {
    boolean mapped = false;
    for (FavUseCaseItemNodeCommon node : rootUcFavNodes) {
      if (node.isMapped(this.pidcVersionWithDetails.getAllAttributeMap().get(pidcAttr.getAttrId()))) {
        mapped = true;
        break;
      }
    }
    return mapped;
  }

  /**
   * @return the set of attrs which can be marked as relevant for focus matrix. Visible attrs which are not deleted and
   *         do not belong to the attribute class "doc" and "sthr" are considered. <br>
   *         Key - Attribute ID <br>
   *         Value - Attribute
   */
  public ConcurrentMap<PidcVersionAttribute, Attribute> getFocusMatrixApplicableAttrMap() {
    Map<Long, PidcVersionAttribute> allPIDCAttrs = fillAllAttributes();
    List<Long> excludeAttrClassesList = getExcludeAttrClassesList();

    ConcurrentMap<PidcVersionAttribute, Attribute> fMatrixAttrMap = new ConcurrentHashMap<>();

    for (PidcVersionAttribute projAttr : allPIDCAttrs.values()) {
      Attribute attr = this.pidcVersionWithDetails.getAllAttributeMap().get(projAttr.getAttrId());
      if (excludeAttrClassesList.contains(attr.getCharacteristicId()) || !isVisible(projAttr) || attr.isDeleted()) {
        continue;
      }
      fMatrixAttrMap.put(projAttr, attr);
    }

    return fMatrixAttrMap;

  }

  private Map<Long, PidcVersionAttribute> fillAllAttributes() {

    PidcVersionAttribute pidcVersionAttr;
    Map<Long, PidcVersionAttribute> allAttrMap = new HashMap<>();

    // add attributes to the list
    for (Attribute attribute : this.pidcVersionWithDetails.getAllAttributeMap().values()) {

      if (attribute != null) {

        // skip attributes which are marked as deleted ,and also attributes
        // having levels -1,-2 and -3 which are
        // attributes Project Name,Variant Code and VariantCoding Code respectively.
        if (attribute.isDeleted() || (attribute.getLevel() == ApicConstants.PROJECT_NAME_ATTR) ||
            (attribute.getLevel() == ApicConstants.VARIANT_CODE_ATTR) ||
            (attribute.getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR)) {
          if (allAttrMap.containsKey(attribute.getId())) {
            allAttrMap.remove(attribute.getId());
          }
          continue;
        }

        pidcVersionAttr = this.pidcVersionWithDetails.getPidcVersionAttributeMap().get(attribute.getId());

        allAttrMap.put(attribute.getId(), pidcVersionAttr);
      }

    }
    return allAttrMap;
  }

  /**
   * @return the list of attr char classes which are excluded from focus matrix
   */
  private List<Long> getExcludeAttrClassesList() {
    // ICDM-1611
    List<Long> excludeAttrClassesList = new ArrayList<>();

    for (Characteristic attrClass : this.pidcVersionWithDetails.getAllCharacteristicMap().values()) {

      if (CommonUtils.isEqual(attrClass.getFocusMatrixYn(), ApicConstants.CODE_NO)) {
        excludeAttrClassesList.add(attrClass.getId());
      }

    }
    return excludeAttrClassesList;
  }

  /**
   * @param projAttr
   * @return
   */
  private boolean isVisible(final IProjectAttribute projAttr) {

    if (projAttr instanceof PidcVersionAttribute) {
      return !this.pidcVersionWithDetails.getPidcVersInvisibleAttrSet().contains(projAttr.getAttrId());
    }
    else if (projAttr instanceof PidcVariantAttribute) {

      return !this.pidcVersionWithDetails.getVariantInvisbleAttributeMap()
          .get(((PidcVariantAttribute) projAttr).getVariantId()).contains(projAttr.getAttrId());
    }
    else if (projAttr instanceof PidcSubVariantAttribute) {

      return !this.pidcVersionWithDetails.getSubVariantInvisbleAttributeMap()
          .get(((PidcSubVariantAttribute) projAttr).getSubVariantId()).contains(projAttr.getAttrId());
    }
    return false;
  }


  /**
   * Finds attributes with focus matrix definition(color != WHITE), among the attributes marked as FM relevant
   *
   * @param mappableFavUcItemSet mappable favourite use case items
   * @param pidcAttr PIDC attribute being checked
   * @param focusMatrixRatedAttrCount
   * @throws DataException
   */
  private int checkRatedAttribute(final Set<IUsecaseItemCommonBO> mappableFavUcItemSet,
      final PidcVersionAttribute pidcAttr, int focusMatrixRatedAttrCount)
      throws DataException {
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    Map<Long, FocusMatrixVersion> focusMatrixVersionsMap =
        pidcVersLoader.getFocusMatrixVersions(this.pidcVersion.getId());
    ConcurrentMap<Long, ConcurrentMap<Long, FocusMatrix>> projFocusMatrixMap = new ConcurrentHashMap<>();
    Long attrID;
    Long ucItemID;
    ConcurrentMap<Long, FocusMatrix> childMap = new ConcurrentHashMap<>();
    FocusMatrixVersion selFmVersion = null;
    for (FocusMatrixVersion fmVersion : focusMatrixVersionsMap.values()) {
      if (ApicConstants.FM_VERS_STATUS.getStatus(fmVersion.getStatus()) == FM_VERS_STATUS.WORKING_SET) {
        selFmVersion = fmVersion;
      }
    }
    FocusMatrixVersionLoader focusMatricVersLoader = new FocusMatrixVersionLoader(getServiceData());
    for (FocusMatrix focusMatrix : focusMatricVersLoader.getFocusMatrixForVersion(selFmVersion.getId()).values()) {

      attrID = focusMatrix.getAttrId();
      projFocusMatrixMap.computeIfAbsent(attrID, k -> new ConcurrentHashMap<>());
      // Here it is assumed that primary key is unique across use case ID and uc section id.
      if (focusMatrix.getSectionId() == null) {
        ucItemID = focusMatrix.getUseCaseId();
      }
      else {
        ucItemID = focusMatrix.getSectionId();
      }
      childMap.put(ucItemID, focusMatrix);
    }


    for (IUsecaseItemCommonBO ucItem : mappableFavUcItemSet) {

      if (ucItem.isFocusMatrixRelevant(true) && (projFocusMatrixMap.get(pidcAttr.getAttrId()) != null) &&
          (projFocusMatrixMap.get(pidcAttr.getAttrId()).get(ucItem.getID()) != null)) {

        FocusMatrix projFmItem = projFocusMatrixMap.get(pidcAttr.getAttrId()).get(ucItem.getID());
        if (!projFmItem.getIsDeleted() &&
            (FocusMatrixColorCode.getColor(projFmItem.getColorCode()) != FocusMatrixColorCode.NOT_DEFINED)) {
          focusMatrixRatedAttrCount++;
          break;
        }
      }
    }
    return focusMatrixRatedAttrCount;
  }
}