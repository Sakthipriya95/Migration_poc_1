/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseDataHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author pdh2cob
 */

public abstract class AbstractProjectObjectBO {

  protected boolean childrenLoaded;

  protected PidcDataHandler pidcDataHandler;

  /**
   * Project Object
   */
  protected final IProjectObject projectObject;

  /**
   * Logged in user information
   */
  protected final CurrentUserBO currentUser = new CurrentUserBO();

  /**
   * pidc version
   */
  protected PidcVersion pidcVersion;


  /**
   * @param projectObject
   * @param functionality
   * @param pidcVersion - pidc version
   */
  public AbstractProjectObjectBO(final PidcVersion pidcVersion, final IProjectObject projectObject,
      final PidcDataHandler pidcDataHandler) {
    super();
    this.projectObject = projectObject;
    if ((pidcVersion == null) && (projectObject instanceof PidcVersion)) {
      this.pidcVersion = (PidcVersion) projectObject;
    }
    else {
      this.pidcVersion = pidcVersion;
    }
    this.pidcDataHandler = pidcDataHandler;
  }

  /**
   * This method checks for whether quotation attribute is defined or not
   *
   * @param projAttr defines Project Attribute
   * @return boolean quotation attribute is defined or not
   */
  public <P extends IProjectAttribute> boolean isQuotationAttrDefined(final P projAttr) {

    AbstractProjectAttributeBO projAttrBo = getProjAttrBo(projAttr);

    // Check for attribute is not deleted, mandatory, not a variant
    return !(projAttrBo.isQuotationRelevant() &&
        !this.pidcDataHandler.getAttributeMap().get(projAttr.getAttrId()).isDeleted() &&
        projAttrBo.isProjAttrVisibleAtAllLevels(projAttr) && !projAttrBo.isValueDefined(projAttr));

  }

  /**
   * @param projAttr
   * @return
   */
  protected <P extends IProjectAttribute> AbstractProjectAttributeBO getProjAttrBo(final P projAttr) {
    ProjectAttributeUtil projAttrUtil = new ProjectAttributeUtil();
    IProjectObject projectObj = projAttrUtil.getProjectObject(getPidcDataHandler(), projAttr);
    String level = "";
    if (projAttr instanceof PidcVersionAttribute) {
      level = ApicConstants.LEVEL_PIDC_VERSION;
    }
    else if (projAttr instanceof PidcVariantAttribute) {
      level = ApicConstants.LEVEL_PIDC_VARIANT;
    }
    else if (projAttr instanceof PidcSubVariantAttribute) {
      level = ApicConstants.LEVEL_PIDC_SUB_VARIANT;
    }
    ProjectHandlerInit projectInit = new ProjectHandlerInit(getPidcVersion(), projectObj, getPidcDataHandler(), level);
    AbstractProjectObjectBO projectBo = projectInit.getProjectObjectBO();

    return projAttrUtil.getProjectAttributeHandler(projAttr, projectBo);
  }


  /**
   * @return if pidc is deleted
   */
  public boolean isDeleted() {
    return this.pidcDataHandler.getPidcVersionInfo().getPidc().isDeleted();
  }


  /**
   * Method to return pidc version object based on pidc version id
   *
   * @param pidcVersionId - pidc version id
   * @return PidcVersion object
   * @throws ApicWebServiceException
   */
  public PidcVersion getPidcVersion(final Long pidcVersionId) throws ApicWebServiceException {
    return new PidcVersionServiceClient().getById(pidcVersionId);
  }


  /**
   * @param pidcVers - Pidc Version instance
   * @return true if active version, false if not
   */
  public boolean isPidcVersionActive(final PidcVersion pidcVers) {
    return CommonUtils.isEqual(pidcVers.getProRevId(),
        getPidcDataHandler().getPidcVersionInfo().getPidc().getProRevId());
  }


  /**
   * @return true/false
   */
  public boolean isModifiable() {
    NodeAccess nodeAccess;
    try {
      nodeAccess = this.currentUser.getNodeAccessRight(this.pidcVersion.getPidcId());
      if ((nodeAccess != null) && nodeAccess.isWrite()) {
        return !this.pidcVersion.isDeleted();
      }
      return false;
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;

  }

  /**
   * This method returns a2l files mapped to sdom pver of specifc pidc version
   *
   * @param pVERName PVER name
   * @param pidcVersID PIDC ID
   * @return pidc version- sdom pver's A2Lfiles
   */
  public synchronized Map<Long, PidcA2l> getMappedA2LFiles(final String pVERName, final Long pidcVersID) {

    // load all the a2l files
    Map<Long, PidcA2l> pidcA2lFiles = new HashMap<Long, PidcA2l>();
    try {
      pidcA2lFiles = new PidcA2lServiceClient().getA2LFileBySdom(pidcVersID, pVERName);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    return pidcA2lFiles;
  }

  /**
   * @return sorted set of PVERs
   */
  public synchronized SortedSet<SdomPVER> getPVerSet() {

    SortedSet<SdomPVER> resultSet = new TreeSet<SdomPVER>();

    PidcVersionAttribute pidcVersionAttribute =
        this.pidcDataHandler.getAllLevelAttrMap().get(new Long(ApicConstants.SDOM_PROJECT_NAME_ATTR));

    Long attrId = pidcVersionAttribute.getAttrId();
    PidcVersionAttribute pidcPVerAttribute = this.pidcDataHandler.getPidcVersAttrMap().get(attrId);

    if (pidcPVerAttribute.isAtChildLevel()) {
      // PVER attribute is variant => many SDOM PVER
      for (PidcVariant variant : this.pidcDataHandler.getVariantMap().values()) {


        Map<Long, PidcVariantAttribute> varAttrs = this.pidcDataHandler.getVariantAttributeMap().get(variant.getId());

        PidcVariantAttribute pVerAttr = varAttrs.get(attrId);

        if (pVerAttr == null) {
          // PVER attribute not defined
          continue;
        }

        AttributeValue pVerValue = this.pidcDataHandler.getAttributeValueMap().get(pVerAttr.getValueId());

        if (pVerValue == null) {
          // SDOM PVER attribute not defined in variant
          continue;
        }
        // add SDOM PVER attribute if not existing in list
        resultSet.add(createSdomPverDataObject(this.pidcVersion, pVerValue));

      }

    }
    else {
      // PVER attribute is not variant => only one SDOM PVER
      AttributeValue pVerValue = this.pidcDataHandler.getAttributeValueMap().get(pidcPVerAttribute.getValueId());
      if (pVerValue != null) {
        resultSet.add(createSdomPverDataObject(this.pidcVersion, pVerValue));
      }
    }

    return resultSet;
  }

  /**
   * @param pidcVersion
   * @param pidcversionLoader
   * @param attrVal
   * @return
   * @throws DataException
   */
  private SdomPVER createSdomPverDataObject(final PidcVersion pidcVersion, final AttributeValue attrVal) {
    SdomPVER sdomPver = new SdomPVER();
    sdomPver.setPverName(attrVal.getName());
    sdomPver.setDescription(attrVal.getDescription());
    sdomPver.setPidcVersion(pidcVersion);
    sdomPver.setSdomPverAttrVal(attrVal);
    return sdomPver;
  }


  /**
   * @param refresh
   * @return
   */
  public abstract <V extends IProjectAttribute> Map<Long, V> getAttributes(final boolean refresh);

  /**
   * @return
   */
  public abstract <V extends IProjectAttribute> Map<Long, V> getAttributes();

  /**
   * @param refresh
   * @param includeDeleted
   * @return
   */
  public abstract <V extends IProjectAttribute> Map<Long, V> getAttributes(final boolean refresh,
      final boolean includeDeleted);


  /**
   * @param sortColumn
   * @return
   */
  public abstract <E extends IProjectAttribute> SortedSet<E> getAttributes(final int sortColumn);


  /**
   * @return
   */
  public abstract <V extends IProjectAttribute> Map<Long, V> getAttributesAll();


  /**
   * @return
   */
  public abstract <V extends IProjectAttribute> Map<Long, V> getAttributesNotDefined();


  /**
   * @return
   */
  public abstract <V extends IProjectAttribute> Map<Long, V> getAttributesNotUsed();

  /**
   * @return
   */
  public abstract <V extends IProjectAttribute> Map<Long, V> getAttributesUsed();


  /**
   * @return
   */
  public abstract String getToolTip();

  /**
   * @return
   */
  public abstract boolean hasInvalidAttrValues();


  /**
   * @return
   */
  public abstract boolean isAllMandatoryAttrDefined();


  /**
   * @param selectedObj
   * @param copiedObj
   * @return
   */
  public abstract boolean isPasteAllowed(final Object selectedObj, final Object copiedObj);


  /**
   *
   */
  public abstract void removeFromMap();

  /**
   *
   */
  public abstract void removeInvisibleAttributes();

  /**
   * @param <A>
   * @param projAttr
   * @return
   */
  public abstract <A extends IProjectAttribute> boolean isValueDefined(final A projAttr);

  /**
   * @param projAttr - project attribute
   * @return ApicConstants.PROJ_ATTR_USED_FLAG
   */
  public <A extends IProjectAttribute> ApicConstants.PROJ_ATTR_USED_FLAG getIsUsedEnum(final A projAttr) {
    PROJ_ATTR_USED_FLAG returnValue;
    if (projAttr.getAttrId() == null) {
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR;
    }
    else {
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.getType(projAttr.getUsedFlag());
    }
    return returnValue;
  }


  /**
   * @return the projectObject
   */
  public IProjectObject getProjectObject() {
    return this.projectObject;
  }


  /**
   * @return the pidcDataHandler
   */
  public PidcDataHandler getPidcDataHandler() {
    return this.pidcDataHandler;
  }


  /**
   * @param pidcDataHandler the pidcDataHandler to set
   */
  public void setPidcDataHandler(final PidcDataHandler pidcDataHandler) {
    this.pidcDataHandler = pidcDataHandler;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @param ucDataHandler usecase datahandler
   * @param focusMatrixDataHandler
   * @return project object statistics
   */
  public abstract ProjectObjectStatistics<?> getProjectStatistics(final UseCaseDataHandler ucDataHandler,
      FocusMatrixDataHandler focusMatrixDataHandler);


  /**
   * Compares two project attributes
   *
   * @param pAttr1 project attribute 1
   * @param pAttr2 project attribute 2
   * @param sortColumn sort column
   * @return compare result
   */
  public int compare(final IProjectAttribute pAttr1, final IProjectAttribute pAttr2, final int sortColumn) {

    int ret = 0;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
      case ApicConstants.SORT_ATTRDESCR:
      case ApicConstants.SORT_SUPERGROUP:
      case ApicConstants.SORT_GROUP:
      case ApicConstants.SORT_LEVEL:
      case ApicConstants.SORT_UNIT:
      case ApicConstants.SORT_VALUETYPE:
      case ApicConstants.SORT_CHAR:
        // use compare method of Attribute class
        ret = compare(getPidcDataHandler().getAttribute(pAttr1.getAttrId()),
            getPidcDataHandler().getAttribute(pAttr2.getAttrId()), sortColumn);
        break;

      // ICDM-179
      // ICDM-2485
      case ApicConstants.SORT_ICONS:
        // TODO refer old framework code
        break;
      case ApicConstants.SORT_VALUE:
        // compare default values
        ret = ApicUtil.compare(pAttr1.getValue(), pAttr2.getValue());
        break;

      case ApicConstants.SORT_USED:
        // use compare method for Strings
        ret = ApicUtil.compare(pAttr1.getUsedFlag(), pAttr2.getUsedFlag());
        break;
      case ApicConstants.SORT_USED_NOT_DEF:
        // compare only NOT_DEFINED used information
        ret = ApicUtil.compareBoolean(
            pAttr1.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()),
            pAttr2.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()));
        break;

      case ApicConstants.SORT_USED_NO:
        // compare only NO used information
        ret = ApicUtil.compareBoolean(pAttr1.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType()),
            pAttr2.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType()));
        break;

      case ApicConstants.SORT_USED_YES:
        // compare only YES used information
        ret = ApicUtil.compareBoolean(pAttr1.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType()),
            pAttr2.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType()));
        break;
      case ApicConstants.SORT_PART_NUMBER:
        // use compare method for Strings
        ret = ApicUtil.compare(pAttr1.getPartNumber(), pAttr2.getPartNumber());
        break;
      case ApicConstants.SORT_SPEC_LINK:
        // use compare method for Strings
        ret = ApicUtil.compare(pAttr1.getSpecLink(), pAttr2.getSpecLink());
        break;
      case ApicConstants.SORT_DESC:
      case ApicConstants.SORT_SUMMARY_DESC:
        // use compare method for Strings
        ret = ApicUtil.compare(pAttr1.getAdditionalInfoDesc(), pAttr2.getAdditionalInfoDesc());
        break;
      case ApicConstants.SORT_MODIFIED_DATE:
        // compare last modified date information
        String sDate1 = getProjAttrLastModifiedDate(pAttr1);
        String sDate2 = getProjAttrLastModifiedDate(pAttr2);
        ret = ApicUtil.compare(parseDate(sDate1), parseDate(sDate2));
        break;
      // Sort for Char Value Icdm-956
      case ApicConstants.SORT_CHAR_VAL:
        ret = ApicUtil.compare(getCharValStr(pAttr1), getCharValStr(pAttr2));
        break;
      case ApicConstants.SORT_ATTR_CREATED_DATE_PIDC:
        // compare created date information
        ret = ApicUtil.compare(pAttr1.getCreatedDate(), pAttr2.getCreatedDate());
        break;
      default:
        ret = 0;
        break;
    }
    if (ret == 0) {
      // compare result is equal, compare the attribute name
      ret = compare(getPidcDataHandler().getAttribute(pAttr1.getAttrId()),
          getPidcDataHandler().getAttribute(pAttr2.getAttrId()), ApicConstants.SORT_ATTRNAME);
    }

    return ret;

  }

  private Date parseDate(final String dateStr) {
    Date date = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");
    if (dateStr != null) {
      try {
        date = dateFormat.parse(dateStr);
      }
      catch (ParseException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    return date;
  }

  /**
   * @return
   */
  private String getCharValStr(final IProjectAttribute pAttr) {
    String ret = null;
    if (pAttr.getValueId() != null) {
      AttributeValue value = getPidcDataHandler().getAttributeValue(pAttr.getValueId());
      Long chrValId = value.getCharacteristicValueId();
      if (chrValId != null) {
        CharacteristicValue charVal = getPidcDataHandler().getCharacteristicValue(chrValId);
        ret = charVal.getName();
      }
    }
    return ret;
  }

  /**
   * @return
   */
  private String getProjAttrLastModifiedDate(final IProjectAttribute pAttr) {
    return pAttr.getModifiedDate() == null ? pAttr.getCreatedDate() : pAttr.getModifiedDate();
  }

  private int compare(final Attribute attr1, final Attribute attr2, final int sortColumn) {
    int compareResult;
    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
        compareResult = ApicUtil.compare(attr1.getName(), attr2.getName());
        break;
      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(attr1.getDescription(), attr2.getDescription());
        break;
      case ApicConstants.SORT_SUPERGROUP:
        compareResult = compareAttrSuperGroup(attr1, attr2);
        break;
      case ApicConstants.SORT_GROUP:
        compareResult = compareAttrGroup(attr1, attr2);
        break;
      case ApicConstants.SORT_LEVEL:
        compareResult = ApicUtil.compare(attr1.getLevel(), attr2.getLevel());
        break;
      case ApicConstants.SORT_UNIT:
        compareResult = ApicUtil.compare(attr1.getUnit(), attr2.getUnit());
        break;
      case ApicConstants.SORT_VALUETYPE:
        compareResult = ApicUtil.compare(attr1.getValueType(), attr2.getValueType());
        break;
      case ApicConstants.SORT_CHAR:
      default:
        compareResult = ApicUtil.compare(attr1.getName(), attr2.getName());
        break;
    }

    if (compareResult == 0) {
      compareResult = ApicUtil.compare(attr1.getName(), attr2.getName());
    }
    return compareResult;
  }

  /**
   * @param attr1
   * @param attr2
   * @return
   */
  private int compareAttrGroup(final Attribute attr1, final Attribute attr2) {
    AttrGroup grp1 = getPidcDataHandler().getAttrGroup(attr1.getAttrGrpId());
    AttrGroup grp2 = getPidcDataHandler().getAttrGroup(attr2.getAttrGrpId());
    return ApicUtil.compare(grp1.getName(), grp2.getName());
  }

  /**
   * @param attr1
   * @param attr2
   * @return
   */
  private int compareAttrSuperGroup(final Attribute attr1, final Attribute attr2) {
    AttrGroup grp1 = getPidcDataHandler().getAttrGroup(attr1.getAttrGrpId());
    AttrGroup grp2 = getPidcDataHandler().getAttrGroup(attr2.getAttrGrpId());
    AttrSuperGroup sgrp1 = getPidcDataHandler().getAttrSuperGroup(grp1.getSuperGrpId());
    AttrSuperGroup sgrp2 = getPidcDataHandler().getAttrSuperGroup(grp2.getSuperGrpId());

    int ret = ApicUtil.compare(sgrp1.getName(), sgrp2.getName());
    return ret == 0 ? ApicUtil.compare(grp1.getName(), grp2.getName()) : ret;
  }


}
