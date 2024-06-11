/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.CommandExecuter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.attr.PredefinedAttrValueLoader;
import com.bosch.caltool.icdm.bo.apic.attr.ProjectAttributesUpdationCommand;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.bo.apic.ProjAttrUtil;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueExtModel;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributeUpdateExternalInput;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author mkl2cob
 */
public class ProjectAttributeUpdateExternal extends AbstractSimpleBusinessObject {


  /**
   * @param serviceData ServiceData
   */
  public ProjectAttributeUpdateExternal(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * active pidc version
   */
  private PidcVersion activePidcVersion;
  /**
   * pidc variant
   */
  private PidcVariant pidcVariant;
  /**
   * value to be set as String
   */
  private String strVal;
  /**
   * PidcVersionAttribute
   */
  private PidcVersionAttribute pidcVersionAttr;
  /**
   * used flag database string
   */
  private String usedFlagDBStr;

  /**
   * update pidc/variant attribute with external service
   *
   * @param projAttrModelExt ProjectAttributeUpdationModelExt
   * @return success message
   * @throws IcdmException Exception during update
   */
  public String update(final ProjectAttributeUpdateExternalInput projAttrModelExt) throws IcdmException {
    // validate access rights
    checkAccessRights();
    // validate the given input
    validateInput(projAttrModelExt);
    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    // set input model
    setInputToCommand(updationModel, projAttrModelExt);
    ProjectAttributesUpdationCommand mainCmd = new ProjectAttributesUpdationCommand(getServiceData(), updationModel);
    CommandExecuter commandExecutor = getServiceData().getCommandExecutor();
    commandExecutor.execute(mainCmd);
    return "Attribute " + this.pidcVersionAttr.getName() + " updated in PIDC " + this.activePidcVersion.getName();
  }

  /**
   * checks if the user is authorised to update attribute value in PIDC
   *
   * @throws UnAuthorizedAccessException
   */
  private void checkAccessRights() throws UnAuthorizedAccessException {
    getLogger().debug("Checking Access Rights for project attribute update external service..");
    String inputUserName = getServiceData().getUsername();
    boolean validUser = false;
    String userIds = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ATTR_UPDATE_ACCESS_USERS);
    if (null != userIds) {
      String[] userIdsArray = userIds.split(",");
      // comma seperated users
      for (String userId : userIdsArray) {
        if (CommonUtils.isEqual(inputUserName, userId)) {
          validUser = true;
          break;
        }
      }
    }
    if (!validUser) {
      throw new UnAuthorizedAccessException("PIDC_ATTR_UPDATE_EXT.INVALID_USER");
    }
    getLogger().debug("Access Rights for project attribute update external service checked for user {}", inputUserName);
  }

  /**
   * @param updationModel
   * @param projAttrModelExt
   * @throws IcdmException
   */
  private void setInputToCommand(final ProjectAttributesUpdationModel updationModel,
      final ProjectAttributeUpdateExternalInput projAttrModelExt)
      throws IcdmException {
    boolean varLevel = projAttrModelExt.getVariantId() != null;
    Long pidcVersId = this.activePidcVersion.getId();

    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVersionWithDetails pidcVersWithDetails = pidcVersLoader.getPidcVersionWithDetails(pidcVersId);
    this.pidcVersionAttr = pidcVersWithDetails.getPidcVersionAttributeMap().get(projAttrModelExt.getAttributeId());
    // check if the attribute exists in right level
    checkIfAttrIsInRightLevel(varLevel, this.pidcVersionAttr);
    updationModel.setPidcVersion(pidcVersWithDetails.getPidcVersionInfo().getPidcVersion());

    // consider the value if the used flag is set to 'YES'
    if (!CommonUtils.isEqual(ApicConstants.CODE_YES, this.usedFlagDBStr)) {
      // otherwise value is not considered
      this.strVal = null;
    }
    if (varLevel) {
      checkIfVarAttrInvisible(projAttrModelExt, pidcVersWithDetails);
      setVarAttrForUpdate(updationModel, projAttrModelExt, pidcVersWithDetails);
    }
    else {
      // check if the attribute belongs to invisible attribute set
      checkIfPidcAttrInvisible(projAttrModelExt, pidcVersWithDetails);
      setProjAttrForUpdate(updationModel, projAttrModelExt, pidcVersWithDetails);
    }
    getLogger().debug("Input set for attribute {}", this.pidcVersionAttr.getName());
  }

  /**
   * @param updationModel
   * @param projAttrModelExt
   * @param pidcVersWithDetails
   * @throws IcdmException
   */
  private void setVarAttrForUpdate(final ProjectAttributesUpdationModel updationModel,
      final ProjectAttributeUpdateExternalInput projAttrModelExt, final PidcVersionWithDetails pidcVersWithDetails)
      throws IcdmException {
    // get the variant attribute
    PidcVariantAttribute varAttr = pidcVersWithDetails.getPidcVariantAttributeMap().get(projAttrModelExt.getVariantId())
        .get(projAttrModelExt.getAttributeId());
    // check if the attribute exists in right level
    if (varAttr.isAtChildLevel()) {
      throw new IcdmException("PIDC_ATTR_UPDATE_EXT.ATTR_IN_SUB_VAR_LEVEL");
    }
    // create the variant map
    Map<Long, PidcVariantAttribute> varAttrMap = new HashMap<>();
    varAttrMap.put(varAttr.getAttrId(), varAttr);
    if (null == this.strVal) {
      varAttr.setValueId(null);
      varAttr.setValue(this.strVal);
      setUpdationModelMapForVarAttr(updationModel, projAttrModelExt, varAttr, null, varAttrMap);
      return;
    }
    // check if attr value already exists
    // if so use the attr value id, else create new attr value
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    AttributeValue attrValue = attrValLoader.getValueByName(projAttrModelExt.getAttributeId(), this.strVal);


    if (null == attrValue) {
      getLogger().debug("Attribute Value - {} to be created", this.strVal);
      // when attribute value needs to be created
      varAttr.setValue(this.strVal);
      varAttr.setUsedFlag(ApicConstants.CODE_YES);
      ProjAttrUtil.setValueToUsedFlag(varAttr, attrValue);
      if (varAttr.getId() == null) {
        // when var attr needs to be created
        updationModel.getPidcVarAttrsToBeCreatedWithNewVal().put(projAttrModelExt.getVariantId(), varAttrMap);
        getLogger().debug("Variant attribute to be created");
      }
      else {
        updationModel.getPidcVarAttrsToBeUpdatedWithNewVal().put(projAttrModelExt.getVariantId(), varAttrMap);
        getLogger().debug("Variant attribute to be updated");
      }
    }
    else {
      // when attribute value already exists
      getLogger().debug("Attribute Value - {} already exists", this.strVal);
      varAttr.setValueId(attrValue.getId());
      varAttr.setValue(attrValue.getName());
      setUpdationModelMapForVarAttr(updationModel, projAttrModelExt, varAttr, attrValue, varAttrMap);
    }
  }

  /**
   * @param updationModel
   * @param projAttrModelExt
   * @param varAttr
   * @param attrValue
   * @param varAttrMap
   */
  private void setUpdationModelMapForVarAttr(final ProjectAttributesUpdationModel updationModel,
      final ProjectAttributeUpdateExternalInput projAttrModelExt, final PidcVariantAttribute varAttr,
      final AttributeValue attrValue, final Map<Long, PidcVariantAttribute> varAttrMap) {
    varAttr.setUsedFlag(ApicConstants.CODE_YES);
    ProjAttrUtil.setValueToUsedFlag(varAttr, attrValue);
    if (varAttr.getId() == null) {
      // when vers attr needs to be created
      updationModel.getPidcVarAttrsToBeCreated().put(projAttrModelExt.getVariantId(), varAttrMap);
      getLogger().debug("Variant attribute to be created");
    }
    else {
      updationModel.getPidcVarAttrsToBeUpdated().put(projAttrModelExt.getVariantId(), varAttrMap);
      getLogger().debug("Variant attribute to be updated");
    }
  }

  /**
   * check if the attribute belongs to invisible attribute set
   *
   * @param projAttrModelExt
   * @param pidcVersWithDetails
   * @throws IcdmException
   */
  private void checkIfPidcAttrInvisible(final ProjectAttributeUpdateExternalInput projAttrModelExt,
      final PidcVersionWithDetails pidcVersWithDetails)
      throws IcdmException {
    boolean isInvisible = pidcVersWithDetails.getPidcVersInvisibleAttrSet().contains(projAttrModelExt.getAttributeId());
    if (isInvisible) {
      throw new IcdmException("PIDC_ATTR_UPDATE_EXT.PROJ_ATTR_INVISIBLE");
    }
    getLogger().debug("Attribute {} is visible in PIDC", projAttrModelExt.getAttributeId());
  }

  /**
   * check if the attribute belongs to invisible attribute set
   *
   * @param projAttrModelExt
   * @param pidcVersWithDetails
   * @throws IcdmException
   */
  private void checkIfVarAttrInvisible(final ProjectAttributeUpdateExternalInput projAttrModelExt,
      final PidcVersionWithDetails pidcVersWithDetails)
      throws IcdmException {
    boolean isInvisible = false;
    Set<Long> varInvisibleAttrSet =
        pidcVersWithDetails.getVariantInvisbleAttributeMap().get(projAttrModelExt.getVariantId());
    if (null != varInvisibleAttrSet) {
      isInvisible = varInvisibleAttrSet.contains(projAttrModelExt.getAttributeId());
    }
    if (isInvisible) {
      throw new IcdmException("PIDC_ATTR_UPDATE_EXT.VAR_ATTR_INVISIBLE");
    }
    getLogger().debug("Attribute {} is visible in variant", projAttrModelExt.getAttributeId());
  }

  /**
   * @param updationModel ProjectAttributesUpdationModel
   * @param projAttrModelExt ProjectAttributeUpdationModelExt
   * @param pidcVersWithDetails PidcVersionWithDetails
   * @throws IcdmException
   */
  private void setProjAttrForUpdate(final ProjectAttributesUpdationModel updationModel,
      final ProjectAttributeUpdateExternalInput projAttrModelExt, final PidcVersionWithDetails pidcVersWithDetails)
      throws IcdmException {
    if (null == this.strVal) {
      this.pidcVersionAttr.setValue(null);
      this.pidcVersionAttr.setValueId(null);
      this.pidcVersionAttr.setUsedFlag(this.usedFlagDBStr);
      setUpdationMapsForProjAttr(updationModel, null);
      return;
    }
    // check if attr value already exists
    // if so use the attr value id, else create new attr value
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    AttributeValue attrValue = attrValLoader.getValueByName(projAttrModelExt.getAttributeId(), this.strVal);
    if (null == attrValue) {
      // when attribute value needs to be created
      getLogger().debug("Attribute value - {} to be created", this.strVal);
      this.pidcVersionAttr.setValue(this.strVal);
      this.pidcVersionAttr.setUsedFlag(this.usedFlagDBStr);
      ProjAttrUtil.setValueToUsedFlag(this.pidcVersionAttr, attrValue);
      if (this.pidcVersionAttr.getId() == null) {
        // when vers attr needs to be created
        updationModel.getPidcAttrsToBeCreatedwithNewVal().put(this.pidcVersionAttr.getAttrId(), this.pidcVersionAttr);
        getLogger().debug("Pidc attribute to be created");
      }
      else {
        updationModel.getPidcAttrsToBeUpdatedwithNewVal().put(this.pidcVersionAttr.getAttrId(), this.pidcVersionAttr);
        getLogger().debug("Pidc attribute to be updated");
      }
    }
    else {
      // when attribute value already exists
      this.pidcVersionAttr.setValueId(attrValue.getId());
      this.pidcVersionAttr.setValue(attrValue.getName());
      setUpdationMapsForProjAttr(updationModel, attrValue);
    }

  }

  /**
   * @param updationModel
   * @param attrValue
   */
  private void setUpdationMapsForProjAttr(final ProjectAttributesUpdationModel updationModel,
      final AttributeValue attrValue) {
    // TODO shall be extracted to a common place for server and client
    ProjAttrUtil.setValueToUsedFlag(this.pidcVersionAttr, attrValue);
    if (this.pidcVersionAttr.getId() == null) {
      // when vers attr needs to be created
      updationModel.getPidcAttrsToBeCreated().put(this.pidcVersionAttr.getAttrId(), this.pidcVersionAttr);
      getLogger().debug("Pidc attribtue to be created");
    }
    else {
      updationModel.getPidcAttrsToBeUpdated().put(this.pidcVersionAttr.getAttrId(), this.pidcVersionAttr);
      getLogger().debug("Pidc attribtue to be updated");
    }
  }

  /**
   * @param varLevel
   * @param pidcVersionAttr
   * @throws IcdmException
   */
  private void checkIfAttrIsInRightLevel(final boolean varLevel, final PidcVersionAttribute pidcVersionAttr)
      throws IcdmException {
    if ((pidcVersionAttr.isAtChildLevel() && !varLevel)) {
      throw new IcdmException("PIDC_ATTR_UPDATE_EXT.PROJ_ATTR_NOT_IN_RIGHT_LEVEL");
    }
    if (!pidcVersionAttr.isAtChildLevel() && varLevel) {
      throw new IcdmException("PIDC_ATTR_UPDATE_EXT.VAR_ATTR_NOT_IN_RIGHT_LEVEL");
    }
  }


  /**
   * @param model input
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  private void validateInput(final ProjectAttributeUpdateExternalInput model) throws DataException {

    getLogger().debug("Validating input {}", model);

    // validate if atleast one of pidc id /variant id is not null
    if ((model.getPidcId() == null) && (model.getVariantId() == null)) {
      throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.NO_PIDC_ID_OR_VAR_ID");
    }

    // validate attribute id
    validateAttrId(model);

    // validate pidc id
    validatePidcId(model);

    // validate variant id
    validateVariantId(model);

    // validate value
    validateValue(model);

    // validate used flas
    validateUsedFlag(model);

  }

  /**
   * @param model ProjectAttributeUpdationModelExt
   * @throws InvalidInputException
   */
  private void validateUsedFlag(final ProjectAttributeUpdateExternalInput model) throws InvalidInputException {
    String usedFlag = model.getUsedFlag();
    if (null == usedFlag) {
      this.usedFlagDBStr = ApicConstants.CODE_YES;
    }
    else {
      if (CommonUtils.isEqual(ApicConstants.USED_YES_DISPLAY, usedFlag.toUpperCase(Locale.getDefault())) ||
          CommonUtils.isEqual(ApicConstants.USED_NO_DISPLAY, usedFlag.toUpperCase(Locale.getDefault())) ||
          CommonUtils.isEqual(ApicConstants.USED_NOTDEF_DISPLAY, usedFlag.toUpperCase(Locale.getDefault()))) {
        this.usedFlagDBStr = ApicConstants.PROJ_ATTR_USED_FLAG.getDbType(usedFlag.toUpperCase(Locale.getDefault()));
        getLogger().debug("Used flag {} is valid", usedFlag);
      }
      else {
        // When used flag string is not of expected values
        throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.NOT_VALID_USED_FLAG");
      }
    }
  }

  /**
   * @param model
   * @throws IcdmException
   */
  private void validateValue(final ProjectAttributeUpdateExternalInput model) throws InvalidInputException {
    AttributeValueExtModel value = model.getValue();
    this.strVal = value.getHyperLinkValue();
    if (null == this.strVal) {
      return;
    }
    // TODO shall be extracted in a common place to server and client
    if (CommonUtils.isValidURLFormat(this.strVal.trim())) {
      this.strVal = CommonUtils.formatUrl(this.strVal.trim());
    }
    else if (!CommonUtils.isValidHyperlinkFormat(this.strVal)) {
      // invalid input
      throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.INVALID_HYPERLINK");
    }
    getLogger().debug("Value {} is valid", this.strVal);
  }

  /**
   * @param model
   * @throws DataException
   * @throws IcdmException
   */
  private void validatePidcId(final ProjectAttributeUpdateExternalInput model) throws DataException {
    if (model.getPidcId() != null) {
      PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
      this.activePidcVersion = pidcVersionLoader.getActivePidcVersion(model.getPidcId());
      if (this.activePidcVersion.isDeleted()) {
        throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.PIDC_DELETED");
      }
      getLogger().debug("PIDC id {} is valid", model.getPidcId());
    }
  }

  /**
   * @param model
   * @throws DataException
   * @throws IcdmException
   */
  private void validateVariantId(final ProjectAttributeUpdateExternalInput model) throws DataException {
    if (model.getVariantId() != null) {
      PidcVariantLoader pidcVarLoader = new PidcVariantLoader(getServiceData());
      this.pidcVariant = pidcVarLoader.getDataObjectByID(model.getVariantId());
      // check if variant is deleted
      if (this.pidcVariant.isDeleted()) {
        throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.VARIANT_DELETED");
      }
      // check if variant belongs to active version if pidc id is given
      if ((null != this.activePidcVersion) &&
          (this.pidcVariant.getPidcVersionId().longValue() != this.activePidcVersion.getId().longValue())) {
        throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.INVALID_VARIANT");
      }
      else if (null == this.activePidcVersion) {
        // initialise active version
        PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
        this.activePidcVersion = pidcVersionLoader.getDataObjectByID(this.pidcVariant.getPidcVersionId());
      }
      getLogger().debug("Variant id {} is valid", model.getVariantId());
    }
  }

  /**
   * @param model ProjectAttributeUpdationModelExt
   * @throws DataException
   * @throws IcdmException Exception
   */
  private void validateAttrId(final ProjectAttributeUpdateExternalInput model) throws DataException {
    Long attributeId = model.getAttributeId();
    if (attributeId == null) {
      throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.ATTR_ID_NULL");
    }
    String validAttrIdStr =
        new CommonParamLoader(getServiceData()).getValue(CommonParamKey.REVIEWDOC_PSRC_LINK_ATTR_ID);
    Long validAttrId = Long.parseLong(validAttrIdStr);
    if (attributeId.longValue() != validAttrId.longValue()) {
      throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.ATTR_NOT_SUPPORTED");
    }
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Attribute attribute = attrLoader.getDataObjectByID(attributeId);
    if (attribute.isDeleted()) {
      throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.ATTR_DELETED");
    }
    if (attribute.isGroupedAttr()) {
      throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.GROUPED_ATTR");
    }
    if (new PredefinedAttrValueLoader(getServiceData()).isPredefinedAttr(attribute.getId())) {
      throw new InvalidInputException("PIDC_ATTR_UPDATE_EXT.PREDEFINED_ATTR");
    }
    getLogger().debug("Attribute Id {} is valid", attributeId);
  }
}
