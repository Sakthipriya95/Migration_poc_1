/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.apic.pidc.AttributeV2;
import com.bosch.caltool.icdm.model.apic.pidc.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.AttributeWithValueTypeV2;
import com.bosch.caltool.icdm.model.apic.pidc.LevelAttrInfoV2;
import com.bosch.caltool.icdm.model.apic.pidc.PidcInfoTypeV2;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTypeV2;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantInfoTypeV2;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantTypeV2;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributesV2;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectObjectWithAttributes;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author dmr1cob
 */
public class PidcTypeV2Loader extends AbstractSimpleBusinessObject {

  /**
   * string constant for PIDC_EDITOR
   */
  private static final String PIDC_EDITOR_STR = "PIDC_EDITOR";

  /**
   * @param serviceData ServiceData
   */
  public PidcTypeV2Loader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param pidcWithAttrs {@link PidcVersionWithAttributesV2}
   * @return {@link PidcTypeV2}
   * @throws IcdmException Exception
   */
  public PidcTypeV2 getProjectIdCardV2(final PidcVersionWithAttributesV2 pidcWithAttrs) throws IcdmException {
    long minLevel = AttributeLoader.MIN_STRUCT_ATTR_LEVEL;
    long maxLevel = (new AttributeLoader(getServiceData())).getMaxStructAttrLevel();
    PidcTypeV2 pidcTypeV2 = new PidcTypeV2();
    pidcTypeV2.setPidcInfoTypeV2(getProjIdCardInfoV2(pidcWithAttrs, minLevel, maxLevel));
    pidcTypeV2.setAttributeWithValueTypeV2List(getPidcAttributesWithValuesV2(pidcWithAttrs));
    pidcTypeV2.setPidcVariantTypeV2List(getPidcVariantInfoV2(pidcWithAttrs));
    return pidcTypeV2;
  }

  private PidcInfoTypeV2 getProjIdCardInfoV2(final PidcVersionWithAttributesV2 pidcWithAttrs, final long minLevel,
      final long maxLevel) {

    // the response object
    PidcInfoTypeV2 wsProjIdCard = new PidcInfoTypeV2();

    // Set PIDC name
    wsProjIdCard.setName(pidcWithAttrs.getPidcVersionInfo().getPidc().getName());

    // set the PIDC ID from the Active Pidc version
    wsProjIdCard.setId(pidcWithAttrs.getPidcVersionInfo().getPidc().getId());

    // set the PIDC version number
    wsProjIdCard.setChangeNumber(pidcWithAttrs.getPidcVersionInfo().getPidc().getVersion());

    // get deleted flag
    wsProjIdCard.setDeleted(pidcWithAttrs.getPidcVersionInfo().getPidc().isDeleted());

    // get create user and date
    wsProjIdCard.setCreatedUser(pidcWithAttrs.getPidcVersionInfo().getPidc().getCreatedUser());
    wsProjIdCard.setCreateDate(pidcWithAttrs.getPidcVersionInfo().getPidc().getCreatedDate());

    // get modify user and date
    wsProjIdCard.setModifyUser(pidcWithAttrs.getPidcVersionInfo().getPidc().getModifiedUser());
    wsProjIdCard.setModifyDate(pidcWithAttrs.getPidcVersionInfo().getPidc().getModifiedDate());

    // clearing status
    CLEARING_STATUS clStatus =
        CLEARING_STATUS.getClearingStatus(pidcWithAttrs.getPidcVersionInfo().getPidc().getClearingStatus());
    wsProjIdCard.setClearingStatus(clStatus.getUiText());
    wsProjIdCard.setCleared(clStatus == CLEARING_STATUS.CLEARED);

    wsProjIdCard.setChangeNumber(pidcWithAttrs.getPidcVersionInfo().getPidc().getVersion());

    // level attribute info list
    wsProjIdCard.setLevelAttrInfoList(getLevelAttrInfoV2(pidcWithAttrs.getPidcVersionInfo(), minLevel, maxLevel));

    return wsProjIdCard;
  }

  private List<LevelAttrInfoV2> getLevelAttrInfoV2(final PidcVersionInfo pidcVer, final long minLevel,
      final long maxLevel) {
    List<LevelAttrInfoV2> levelAttrInfo = new ArrayList<>();

    for (long level = minLevel; level <= maxLevel; level++) {
      LevelAttrInfoV2 levelAttrInfoV2 = new LevelAttrInfoV2();

      PidcVersionAttribute structAttr = pidcVer.getLevelAttrMap().get(level);

      levelAttrInfoV2.setLevelNo((int) level);
      levelAttrInfoV2.setLevelAttrId(structAttr.getAttrId());
      levelAttrInfoV2.setLevelAttrValueId(structAttr.getValueId());
      levelAttrInfoV2.setLevelName(structAttr.getValue());

      levelAttrInfo.add(levelAttrInfoV2);
    }
    return levelAttrInfo;
  }

  private List<AttributeWithValueTypeV2> getPidcAttributesWithValuesV2(final PidcVersionWithAttributesV2 pidcWithAttrs)
      throws IcdmException {

    // get the attributes of the PIDC
    Collection<PidcVersionAttribute> dbPidcAttrs = pidcWithAttrs.getPidcAttributeMap().values();
    // the response object
    List<AttributeWithValueTypeV2> wsPidcAttrs = new ArrayList<>();

    for (PidcVersionAttribute dbPidcAttr : dbPidcAttrs) {

      AttributeWithValueTypeV2 wsPidcAttr = new AttributeWithValueTypeV2();

      // set used information
      if (null == dbPidcAttr.getUsedFlag()) {
        // in case of hidden attributes
        wsPidcAttr.setUsed("");
      }
      else {
        PROJ_ATTR_USED_FLAG usedEnum;
        usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(dbPidcAttr.getUsedFlag());
        wsPidcAttr.setUsed(usedEnum.getUiType());
      }

      // set isVariant information
      wsPidcAttr.setVariant(dbPidcAttr.isAtChildLevel());
      // set attribute information
      com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
          pidcWithAttrs.getAttributeMap().get(dbPidcAttr.getAttrId());
      wsPidcAttr.setAttribute(
          getWsAttributeV2(attribute, pidcWithAttrs.getCharacteristicMap().get(attribute.getCharacteristicId())));

      // set attribute value information
      if ((dbPidcAttr.getValueId() != null) && !dbPidcAttr.isAtChildLevel()) {
        // Set attribute value information
        wsPidcAttr.setAttrValue(getWsAttrValueV2(pidcWithAttrs, dbPidcAttr));
      }

      // get optional attribute information
      wsPidcAttr.setSpecLink(dbPidcAttr.getSpecLink());
      wsPidcAttr.setPartNumber(dbPidcAttr.getPartNumber());
      wsPidcAttr.setDescription(dbPidcAttr.getAdditionalInfoDesc());

      // set PIDC attribute version
      if (null != dbPidcAttr.getVersion()) {
        wsPidcAttr.setChangeNumber(dbPidcAttr.getVersion());
      }
      wsPidcAttrs.add(wsPidcAttr);

    }


    // Task 242055
    // set the dummy attribute and value for last confirmation date
    AttributeWithValueTypeV2 wsPidcAttr = new AttributeWithValueTypeV2();
    wsPidcAttr.setAttribute(getWsAttrForConfrmDateV2());
    wsPidcAttr.setUsed("Y");
    wsPidcAttr.setVariant(false);
    wsPidcAttr.setAttrValue(getWSValueForConfrmDateV2(pidcWithAttrs));
    wsPidcAttr.setChangeNumber(1);
    wsPidcAttrs.add(wsPidcAttr);

    return wsPidcAttrs;
  }

  private AttributeV2 getWsAttributeV2(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute,
      final Characteristic characteristic) {
    AttributeV2 wsAttribute = new AttributeV2();

    // Set attribute id
    wsAttribute.setId(attribute.getId());
    // Set attribute English name
    wsAttribute.setNameEng(attribute.getNameEng());
    // Set attribute German name
    String attrNameG = attribute.getNameGer();
    if ((null == attrNameG) || "".equals(attrNameG)) {
      wsAttribute.setNameGer(attribute.getNameEng());
    }
    else {
      wsAttribute.setNameGer(attrNameG);
    }
    // Set attribute English description
    wsAttribute.setDescEng(attribute.getDescriptionEng());
    // Set attribute German description
    String attrDescG = attribute.getDescriptionGer();
    if (CommonUtils.isNotEmptyString(attrDescG)) {
      wsAttribute.setDescGer(attrDescG);
    }
    else {
      wsAttribute.setDescGer(attribute.getDescriptionEng());
    }
    // Set attribute deleted information
    wsAttribute.setDeleted(attribute.isDeleted());
    // Set attribute normalized information
    wsAttribute.setNormalized(attribute.isNormalized());
    // Set attribute mandatory information
    wsAttribute.setMandatory(attribute.isMandatory());
    // Set attribute unit information
    wsAttribute.setUnit(attribute.getUnit());
    // Set attribute format information
    if (null == attribute.getFormat()) {
      wsAttribute.setFormat("");
    }
    else {
      wsAttribute.setFormat(attribute.getFormat());
    }
    // Set attribute created date information
    wsAttribute.setCreateDate(attribute.getCreatedDate());
    // Set attribute created user information
    wsAttribute.setCreateUser(attribute.getCreatedUser());
    // Set attribute modified date information
    wsAttribute.setModifyDate(attribute.getModifiedDate());
    // Set attribute modified user information
    wsAttribute.setModifyUser(attribute.getModifiedUser());
    // Set attribute value type id
    String valueType = attribute.getValueType();
    wsAttribute.setTypeId(AttributeValueType.getType(valueType).getValueTypeID());
    // Set attribute group id
    wsAttribute.setGroupId(attribute.getAttrGrpId());
    // Set attribute version
    wsAttribute.setChangeNumber(attribute.getVersion());

    if (null != characteristic) {
      wsAttribute.setCharacterId(characteristic.getId());
      wsAttribute.setCharacterName(characteristic.getName());
    }
    return wsAttribute;
  }

  private AttributeValue getWsAttrValueV2(final PidcVersionWithAttributesV2 pidcWithAttrs,
      final PidcVersionAttribute dbPidcAttr) {

    // create the response object
    com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
        pidcWithAttrs.getAttributeValueMap().get(dbPidcAttr.getValueId());

    return createWsAttributeValueV2(attributeValue);
  }

  private AttributeValue createWsAttributeValueV2(final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attrVal) {

    AttributeValue respAttrVal = new AttributeValue();

    // Set Attribute Id
    respAttrVal.setAttrId(attrVal.getAttributeId());

    // set the English attribute value depending on the value type
    respAttrVal.setValueEng(attrVal.getNameRaw());
    // set the German attribute value depending on the value type

    if (null == attrVal.getTextValueGer()) {
      // if text value German is not found
      respAttrVal.setValueGer("");
    }
    else {
      respAttrVal.setValueGer(attrVal.getTextValueGer());
    }

    // Set attribute value id
    respAttrVal.setValueId(attrVal.getId());

    // Set attribute value deleted information
    respAttrVal.setDeleted(attrVal.isDeleted());

    // Attribute value created date
    respAttrVal.setCreatedDate(attrVal.getCreatedDate());

    // Attribute value created user
    respAttrVal.setCreatedUser(attrVal.getCreatedUser());

    // Attribute value modified date
    respAttrVal.setModifyDate(attrVal.getModifiedDate());

    // Attribute value modified user
    respAttrVal.setModifyUser(attrVal.getModifiedUser());

    // Attribute value version
    respAttrVal.setChangeNumber(attrVal.getVersion());

    // Clearing Status
    String clearingStatus = attrVal.getClearingStatus();
    CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
    respAttrVal.setClearingStatus(clStatus.getUiText());

    // Attribute isCleraed()
    respAttrVal.setCleared(clStatus == CLEARING_STATUS.CLEARED);

    // iCDM-1273
    respAttrVal.setValueDescEng(attrVal.getDescriptionEng());
    if (null == attrVal.getDescriptionGer()) {
      respAttrVal.setValueDescGer("");
    }
    else {
      respAttrVal.setValueDescGer(attrVal.getDescriptionGer());
    }
    return respAttrVal;
  }

  private AttributeV2 getWsAttrForConfrmDateV2() {
    AttributeV2 wsAttribute = new AttributeV2();
    MessageLoader messageLoader = new MessageLoader(getServiceData());
    CommonParamLoader commonParamLoader = new CommonParamLoader(getServiceData());
    // Set attribute id
    wsAttribute.setId(Long.valueOf("-1"));
    // Set attribute English name
    String attrNameEng = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_TEXT_ENG");
    wsAttribute.setNameEng(attrNameEng);
    // Set attribute German name
    String attrNameGer = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_TEXT_GER");
    wsAttribute.setNameGer(attrNameGer);

    // Set attribute English description
    String attrDescEng = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_DESC_ENG");
    wsAttribute.setDescEng(attrDescEng);
    // Set attribute German description
    String attrDescGer = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_DESC_GER");
    wsAttribute.setDescGer(attrDescGer);

    // Set attribute deleted information
    wsAttribute.setDeleted(false);
    // Set attribute normalized information
    wsAttribute.setNormalized(false);
    // Set attribute mandatory information
    wsAttribute.setMandatory(false);
    // Set attribute unit information
    wsAttribute.setUnit("");
    // Set attribute format information
    wsAttribute.setFormat(DateFormat.DATE_FORMAT_09);
    // Set attribute created date information
    Calendar createdDate = getCreatedDateForDummyAttr();
    wsAttribute.setCreateDate(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, createdDate));
    // Set attribute created user information
    wsAttribute.setCreateUser("DGS_ICDM");

    // Set attribute value type id
    wsAttribute.setTypeId(AttributeValueType.DATE.getValueTypeID());
    // Set attribute group id
    String attrGroupId = commonParamLoader.getValue(CommonParamKey.PIDC_UP_TO_DATE_ATTR_GROUP_ID);
    wsAttribute.setGroupId(Long.parseLong(attrGroupId));

    // Set attribute version
    wsAttribute.setChangeNumber(Long.valueOf(1));

    return wsAttribute;
  }

  private AttributeValue getWSValueForConfrmDateV2(final PidcVersionWithAttributesV2 pidcWithAttrs)
      throws IcdmException {
    CommonParamLoader commonParamLoader = new CommonParamLoader(getServiceData());

    // create the response object
    AttributeValue attrVal = new AttributeValue();

    // Set Attribute Id
    attrVal.setAttrId(Long.parseLong("-1"));

    // set the English attribute value depending on the confirmation date
    Calendar lastConfirmationDate = DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
        pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getLastConfirmationDate());
    if (null == lastConfirmationDate) {
      // Review 253218
      attrVal.setValueEng("Not Defined");
    }
    else {
      attrVal.setValueEng(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, lastConfirmationDate));
      // Task 271557
      // create a new instance for calculating the last valid date from last confirmation date
      Calendar lastValidDate = Calendar.getInstance();
      lastValidDate.setTimeInMillis(lastConfirmationDate.getTimeInMillis());
      // get the interval days between last confirmation and valid date from TABV_COMMON_PARAMS
      Long intervalDays = Long.valueOf(commonParamLoader.getValue(CommonParamKey.PIDC_UP_TO_DATE_INTERVAL));
      // add the interval days to find out the last valid date
      lastValidDate.add(Calendar.DAY_OF_MONTH, intervalDays.intValue());
      // set the last valid date
      attrVal.setValueGer(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, lastValidDate));
      // set the interval days
      attrVal.setValueDescEng(intervalDays.toString());
    }


    // Set attribute value id
    attrVal.setValueId(Long.parseLong("-1"));

    // Set attribute value deleted information
    attrVal.setDeleted(false);


    // Attribute value created date
    Calendar createdDate = getCreatedDateForDummyAttr();
    attrVal.setCreatedDate(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, createdDate));

    // Attribute value created user
    attrVal.setCreatedUser("DGS_ICDM");


    // Attribute value version
    attrVal.setChangeNumber(Long.valueOf(1));

    // Clearing Status
    attrVal.setClearingStatus("Y");

    return attrVal;

  }

  /**
   * @return 01-NOV-2017 as Calendar
   */
  private Calendar getCreatedDateForDummyAttr() {
    Calendar createdDate = Calendar.getInstance();
    createdDate.set(2017, 11, 1);
    return createdDate;
  }

  private List<PidcVariantTypeV2> getPidcVariantInfoV2(final PidcVersionWithAttributesV2 pidcWithAttrs) {

    // the response object
    List<PidcVariantTypeV2> wsPidcVariants = new ArrayList<>();

    // Get project id card variants
    Collection<ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> dbPidcVariants =
        pidcWithAttrs.getVariantMap().values();


    for (ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant : dbPidcVariants) {

      PidcVariantTypeV2 wsPidcVariant = new PidcVariantTypeV2();

      // Set project id card variant information
      PidcVariantInfoTypeV2 wsVariantInfoV2 = getWsVariantInfoV2(dbPidcVariant.getProjectObject());
      wsVariantInfoV2.setVersionNumber(pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getProRevId());
      wsPidcVariant.setPidcVariantInfoTypeV2(wsVariantInfoV2);

      // Get variant attributes
      Map<Long, PidcVariantAttribute> dbVariantAttrs = dbPidcVariant.getProjectAttrMap();

      if ((dbVariantAttrs != null) && (dbVariantAttrs.size() > 0)) {
        fillVaraintInfoForProjectV2(dbPidcVariant, wsPidcVariant, dbVariantAttrs, pidcWithAttrs);
      }

      wsPidcVariants.add(wsPidcVariant);

    }

    return wsPidcVariants;
  }

  private PidcVariantInfoTypeV2 getWsVariantInfoV2(final PidcVariant pidcVar) {

    // the result object
    PidcVariantInfoTypeV2 wsVariantInfo = new PidcVariantInfoTypeV2();

    // get the variant information

    if (pidcVar.getName() != null) {
      // Set variant ID
      wsVariantInfo.setId(pidcVar.getId());

      wsVariantInfo.setName(pidcVar.getName());

      wsVariantInfo.setChangeNumber(pidcVar.getVersion());

      wsVariantInfo.setDeleted(pidcVar.isDeleted());

      wsVariantInfo.setCreateUser(pidcVar.getCreatedUser());
      wsVariantInfo.setCreateDate(pidcVar.getCreatedDate());

      wsVariantInfo.setModifyUser(pidcVar.getModifiedUser());
      wsVariantInfo.setModifyDate(pidcVar.getModifiedDate());
    }

    return wsVariantInfo;
  }

  private void fillVaraintInfoForProjectV2(
      final ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant,
      final PidcVariantTypeV2 wsPidcVariant, final Map<Long, PidcVariantAttribute> dbVariantAttrs,
      final PidcVersionWithAttributesV2 pidcWithAttrs) {
    List<AttributeWithValueTypeV2> wsVariantAttrs = new ArrayList<>();


    for (PidcVariantAttribute varAttr : dbVariantAttrs.values()) {

      AttributeWithValueTypeV2 wsVariantAttr = new AttributeWithValueTypeV2();

      // set used information
      if (null == varAttr.getUsedFlag()) {
        // in case of hidden attributes
        wsVariantAttr.setUsed("");
      }
      else {
        PROJ_ATTR_USED_FLAG usedEnum;
        usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(varAttr.getUsedFlag());
        wsVariantAttr.setUsed(usedEnum.getUiType());
      }


      // set isVariant information
      wsVariantAttr.setVariant(varAttr.isAtChildLevel());

      // get attribute info
      com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
          pidcWithAttrs.getAttributeMap().get(varAttr.getAttrId());
      Characteristic characteristic = pidcWithAttrs.getCharacteristicMap().get(varAttr.getAttrId());
      wsVariantAttr.setAttribute(getWsAttributeV2(attribute, characteristic));

      // get optional attribute information
      wsVariantAttr.setSpecLink(varAttr.getSpecLink());
      wsVariantAttr.setPartNumber(varAttr.getPartNumber());
      wsVariantAttr.setDescription(varAttr.getAdditionalInfoDesc());

      // get PIDC attribute version
      wsVariantAttr.setChangeNumber(varAttr.getVersion());

      // Task 262097
      if ((varAttr.getValueId() != null) && !varAttr.isAtChildLevel()) {
        com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
            pidcWithAttrs.getAttributeValueMap().get(varAttr.getValueId());
        // set fields for variant attribute value
        wsVariantAttr.setAttrValue(createWsAttributeValueV2(attributeValue));
      }

      wsVariantAttrs.add(wsVariantAttr);

    }

    wsPidcVariant.setAttributeWithValueTypeV2List(wsVariantAttrs);
    if (CommonUtils.isNotEmpty(pidcWithAttrs.getVarWithSubVarIds().get(dbPidcVariant.getProjectObject().getId()))) {
      wsPidcVariant.setPidcSubVarTypeV2List(getSubVariantInfoV2(dbPidcVariant, pidcWithAttrs));
    }
  }

  private List<PidcVariantTypeV2> getSubVariantInfoV2(
      final ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant,
      final PidcVersionWithAttributesV2 pidcWithAttrs) {

    // the response object
    List<PidcVariantTypeV2> wsSubVariants = new ArrayList<>();

    // Get sub-variants of the variant
    Collection<Long> dbSubVariants = pidcWithAttrs.getVarWithSubVarIds().get(dbPidcVariant.getProjectObject().getId());

    for (Long subVariantId : dbSubVariants) {

      PidcVariantTypeV2 wsSubVariant = new PidcVariantTypeV2();

      ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute> subVarProjObjWithAttr =
          pidcWithAttrs.getSubVariantMap().get(subVariantId);
      // Set sub-variant information
      PidcVariantInfoTypeV2 wsSubVariantInfoV2 = getWsSubVariantInfoV2(subVarProjObjWithAttr.getProjectObject());
      wsSubVariantInfoV2.setVersionNumber(pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getProRevId());
      wsSubVariant.setPidcVariantInfoTypeV2(wsSubVariantInfoV2);

      // Get variant attributes
      Map<Long, PidcSubVariantAttribute> dbSubVariantAttrs = subVarProjObjWithAttr.getProjectAttrMap();

      if ((dbSubVariantAttrs != null) && (dbSubVariantAttrs.size() > 0)) {
        List<AttributeWithValueTypeV2> wsSubVariantAttrs = new ArrayList<>();


        setAttrWithValueType(pidcWithAttrs, dbSubVariantAttrs, wsSubVariantAttrs);

        wsSubVariant.setAttributeWithValueTypeV2List(wsSubVariantAttrs);
      }

      wsSubVariants.add(wsSubVariant);

    }

    return wsSubVariants;
  }

  /**
   * @param pidcWithAttrs
   * @param dbSubVariantAttrs
   * @param wsSubVariantAttrs
   * @throws IcdmException
   */
  private void setAttrWithValueType(final PidcVersionWithAttributesV2 pidcWithAttrs,
      final Map<Long, PidcSubVariantAttribute> dbSubVariantAttrs,
      final List<AttributeWithValueTypeV2> wsSubVariantAttrs) {
    for (PidcSubVariantAttribute svarAttr : dbSubVariantAttrs.values()) {

      AttributeWithValueTypeV2 wsVariantAttr = new AttributeWithValueTypeV2();
      // set used information
      if (null == svarAttr.getUsedFlag()) {
        // in case of hidden attributes
        wsVariantAttr.setUsed("");
      }
      else {
        PROJ_ATTR_USED_FLAG usedEnum;
        usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(svarAttr.getUsedFlag());
        wsVariantAttr.setUsed(usedEnum.getUiType());
      }

      // set isVariant information
      wsVariantAttr.setVariant(svarAttr.isAtChildLevel());

      // get attribute info
      com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
          pidcWithAttrs.getAttributeMap().get(svarAttr.getAttrId());
      Characteristic characteristic = pidcWithAttrs.getCharacteristicMap().get(svarAttr.getAttrId());
      wsVariantAttr.setAttribute(getWsAttributeV2(attribute, characteristic));

      // get optional attribute information
      wsVariantAttr.setSpecLink(svarAttr.getSpecLink());
      wsVariantAttr.setPartNumber(svarAttr.getPartNumber());
      wsVariantAttr.setDescription(svarAttr.getAdditionalInfoDesc());

      // get PIDC attribute version
      wsVariantAttr.setChangeNumber(svarAttr.getVersion());

      if ((svarAttr.getValueId() != null) && !svarAttr.isAtChildLevel()) {

        com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
            pidcWithAttrs.getAttributeValueMap().get(svarAttr.getValueId());

        // set fields for variant attribute value
        wsVariantAttr.setAttrValue(createWsAttributeValueV2(attributeValue));
      }

      wsSubVariantAttrs.add(wsVariantAttr);

    }
  }

  private PidcVariantInfoTypeV2 getWsSubVariantInfoV2(final PidcSubVariant subVar) {

    // the result object
    PidcVariantInfoTypeV2 wsVariantInfo = new PidcVariantInfoTypeV2();

    // get the variant information

    if (subVar.getName() != null) {
      // Set variant ID
      wsVariantInfo.setId(subVar.getId());

      wsVariantInfo.setName(subVar.getName());

      wsVariantInfo.setChangeNumber(subVar.getVersion());


      wsVariantInfo.setDeleted(subVar.isDeleted());

      wsVariantInfo.setCreateUser(subVar.getCreatedUser());
      wsVariantInfo.setCreateDate(subVar.getCreatedDate());

      wsVariantInfo.setModifyUser(subVar.getModifiedUser());
      wsVariantInfo.setModifyDate(subVar.getModifiedDate());
    }
    return wsVariantInfo;
  }
}
