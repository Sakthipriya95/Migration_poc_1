/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.MandatoryAttr;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author pdh2cob
 */
public abstract class AbstractProjectAttributeBO {

  /**
   * Instance variable for attribute
   */
  protected final IProjectAttribute projectAttr;

  /**
   * Project Handler
   */
  protected final AbstractProjectObjectBO projObjBO;

  /**
   * Curren tUser BO
   */
  protected CurrentUserBO currentUser = new CurrentUserBO();

  /**
   * @param projectAttr - selected attribute
   * @param projObjBO Project object Handler
   */
  public AbstractProjectAttributeBO(final IProjectAttribute projectAttr, final AbstractProjectObjectBO projObjBO) {
    super();
    this.projectAttr = projectAttr;
    this.projObjBO = projObjBO;
  }


  /**
   * @return true if project attribute is quotation relevant
   */
  public boolean isQuotationRelevant() {
    return this.projObjBO.getPidcDataHandler().getProjectUsecaseModel().getQuotationRelevantUcAttrIdSet()
        .contains(this.projectAttr.getAttrId());
  }

  /**
   * @return true if the object is modifiable
   */
  public boolean isModifiable() {

    boolean isModifiable = false;
    NodeAccess curUserAccRight;
    try {
      curUserAccRight = this.currentUser.getNodeAccessRight(this.projObjBO.getPidcVersion().getPidcId());

      // the attribute can be modified if the user can modify the PIDC
      if ((curUserAccRight != null) && curUserAccRight.isWrite() &&
          new ApicDataBO().isPidcUnlockedInSession(this.projObjBO.getPidcVersion())) {
        // structure attributes can not be modified
        isModifiable = !((this.projObjBO.getPidcDataHandler().getAttributeMap().get(this.projectAttr.getAttrId())
            .getLevel() > 0) || this.projObjBO.isDeleted() ||
            (this.projObjBO.getPidcVersion().getPidStatus().equals(PidcVersionStatus.LOCKED.getDbStatus())));

      }
    }
    catch (ApicWebServiceException ex) {

      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    return isModifiable;
  }


  /**
   * This method returns the corresponding error message when this attribute is not modifiable
   *
   * @return Error condition describing why it is not modifiable
   */
  public String isModifiableWithError() {
    StringBuilder errorMsg = new StringBuilder();
    NodeAccess curUserAccRight;
    try {
      curUserAccRight = this.currentUser.getNodeAccessRight(this.projObjBO.getPidcVersion().getPidcId());

      // the attribute can be modified if the user can modify the PIDC
      if ((curUserAccRight != null) && curUserAccRight.isWrite()) {

        if (this.projObjBO.getPidcDataHandler().getAttributeMap().get(this.projectAttr.getAttrId()).getLevel() > 0) {
          errorMsg.append("Structure Attribute cannot be modified;");
        }
        if (this.projObjBO.getPidcVersion().isDeleted()) {
          errorMsg.append("Deleted Attribute cannot be modified;");
        }
        if (this.projObjBO.getPidcVersion().getPidStatus().equals(PidcVersionStatus.LOCKED.getDbStatus())) {
          errorMsg.append("Attribute for a locked PIDC cannot be modified;");
        }
      }
      else {
        errorMsg.append("Insufficient Privilege");
      }
    }
    catch (Exception e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return errorMsg.toString();
  }


  /**
   * @return alias name for the project attribute
   */
  public String getAliasName() {
    String aliasName = "";
    for (AliasDetail aliasDetail : this.projObjBO.getPidcDataHandler().getAliasDefModel().getAliasDetailsMap()
        .values()) {
      if ((CommonUtils.isEqual(getProjectAttr().getAttrId(), aliasDetail.getAttrId())) ||
          ((getProjectAttr().getValueId() != null) &&
              CommonUtils.isEqual(getProjectAttr().getValueId(), aliasDetail.getValueId()))) {
        aliasName = aliasDetail.getAliasName();
      }
    }
    return aliasName;
  }


  /**
   * @return the displayed value of this project attribute as string
   */
  public abstract String getDefaultValueDisplayName();

  /**
   * @param showUnit shows unit for numeric values, if set to <code>true</code>
   * @return the displayed value of this project attribute as string
   */
  public abstract String getDefaultValueDisplayName(boolean showUnit);

  /**
   * Icdm-883 Say whether the attr is strucured or not
   *
   * @return the boolean saying if the attr is structured
   */
  public boolean isStructuredAttr() {

    for (PidcDetStructure detStruct : this.projObjBO.getPidcDataHandler().getPidcDetStructureMap().values()) {
      if (detStruct.getAttrId().longValue() == this.projectAttr.getAttrId().longValue()) {
        return true;
      }

    }
    return false;
  }

  /**
   * @return Returns whether the read access to PIDC is available
   */
  public boolean isReadable() {
    NodeAccess curUserAccRight;
    try {
      curUserAccRight = this.currentUser.getNodeAccessRight(this.projObjBO.getPidcVersion().getPidcId());
      return (curUserAccRight != null) && curUserAccRight.isRead();
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }

  }


  /**
   * @return This method returns whether the value set is not cleared or deleted
   */
  public boolean isValueInvalid() {
    return getIsUsed().equals(ApicConstants.USED_YES_DISPLAY) && (this.projectAttr.getValueId() != null) &&
        (this.projObjBO.getPidcDataHandler().getAttributeValueMap().get(this.projectAttr.getValueId()) != null) &&
        (this.projObjBO.getPidcDataHandler().getAttributeValueMap().get(this.projectAttr.getValueId()).isDeleted());
  }

  /**
   * //ICDM 656 : This method checks for whether the attribute can be moved to variant/subvar levels
   *
   * @return moveEnabled
   */
  public abstract boolean canMoveDown();


  /**
   * ICDM-2234 checks whether the attribute can be transfered to vcdm
   *
   * @return true/false
   */
  public abstract boolean canTransferToVcdm();


  /**
   * iCDM-957 <br>
   * Get the tooltip text, if any value is set for the pidCard attribute.<br>
   * Returns null (to disable tool tip) if value is not set!
   *
   * @return the tooltip for the value set along with description, <Variant> as tooltip if moved to variant and as
   *         <sub-variant> if moved to sub-variant.
   */
  public String getValueTooltip() {

    if (!getDefaultValueDisplayName().isEmpty()) {
      StringBuilder tooltip = new StringBuilder("Value : ");
      // iCDM-2094
      String defaultValueDisplayName = getDefaultValueDisplayName(true);
      tooltip.append(defaultValueDisplayName);
      String valDesc = getValueDescription();
      if (!CommonUtils.isEmptyString(valDesc)) {
        if (this.projObjBO.getPidcDataHandler().getAttributeValueMap().get(this.projectAttr.getValueId()).getValueType()
            .equals(AttributeValueType.ICDM_USER.toString())) {
          tooltip.append("\nDescription : \n").append(valDesc);
        }
        else {
          tooltip.append("\nDescription : ").append(valDesc);
        }
      }
      return tooltip.toString();
    }
    // return null , to disable tooltip when no value is set
    return null;

  }

  /**
   * @return the description of the attribute value set
   */
  public String getValueDescription() {
    if (this.projectAttr.isAtChildLevel()) {
      return "";
    }
    String desc = "";
    if (isHiddenToUser()) {
      desc = ApicConstants.HIDDEN_VALUE;
    }
    else {
      if (ApicConstants.USED_YES_DISPLAY.equals(getIsUsed())) {
        AttributeValue val =
            this.projObjBO.getPidcDataHandler().getAttributeValueMap().get(this.projectAttr.getValueId());
        if (val != null) {
          desc = val.getDescription();
        }
      }
    }

    return desc;
  }

  /**
   * Checks whether value is hidden to current user, based on attribute hidden status and user rights
   *
   * @return true, if value is not visible to current user
   */
  // iCDM-2094
  public boolean isHiddenToUser() {
    try {
      boolean pidcRights = false;
      NodeAccess nodeAccess = this.currentUser.getNodeAccessRight(this.projObjBO.getPidcVersion().getPidcId());
      if (nodeAccess != null) {
        pidcRights = nodeAccess.isRead();
      }
      return this.projectAttr.isAttrHidden() &&
          !(pidcRights || this.currentUser.hasApicWriteAccess() || this.currentUser.hasApicReadAllAccess());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }
  }

  /**
   * @param projAttr IProjectAttribute
   * @return if the project attribute is visible to current user
   */
  public boolean isProjAttrVisibleAtAllLevels(final IProjectAttribute projAttr) {
    if (projAttr instanceof PidcVersionAttribute) {

      if (projAttr.isAtChildLevel()) {
        return isVisibleAtVarLevel((PidcVersionAttribute) projAttr);
      }

      return !isProjAttrInvisible(projAttr);
    }
    else if (projAttr instanceof PidcVariantAttribute) {

      if (projAttr.isAtChildLevel()) {
        return isVisibleAtSubvarLevel((PidcVariantAttribute) projAttr);
      }

      return !isProjAttrInvisible(projAttr);

    }
    else if (projAttr instanceof PidcSubVariantAttribute) {

      return !isProjAttrInvisible(projAttr);

    }

    return false;

  }


  private boolean isVisibleAtSubvarLevel(final PidcVariantAttribute varAttr) {
    PidcVariant var = getProjectObjectBO().getPidcDataHandler().getVariantMap().get(varAttr.getVariantId());

    Collection<PidcSubVariant> unDeletedSubVariants =
        new PidcVariantBO(getProjectObjectBO().getPidcVersion(), var, getProjectObjectBO().getPidcDataHandler())
            .getSubVariantsMap(false).values();
    if (CommonUtils.isNotEmpty(unDeletedSubVariants)) {
      for (PidcSubVariant pidcSubVariant : unDeletedSubVariants) {
        Map<Long, PidcSubVariantAttribute> subVarAttrMap =
            getProjectObjectBO().getPidcDataHandler().getSubVariantAttributeMap().get(pidcSubVariant.getId());
        if ((subVarAttrMap.get(varAttr.getAttrId()) != null)) {
          boolean isVisible = isProjAttrVisibleAtAllLevels(subVarAttrMap.get(varAttr.getAttrId()));
          if (!isVisible) {
            return false;
          }
        }

      }
    }

    return true;
  }

  /**
   * @param projAttr IProjectAttribute
   * @return true if visible at var level
   */
  private boolean isVisibleAtVarLevel(final PidcVersionAttribute projAttr) {

    Collection<PidcVariant> unDeletedVariants =
        new PidcVersionBO(getProjectObjectBO().getPidcVersion(), getProjectObjectBO().getPidcDataHandler())
            .getVariantsMap(false).values();
    if (CommonUtils.isNotEmpty(unDeletedVariants)) {

      for (PidcVariant pidcVariant : unDeletedVariants) {

        Map<Long, PidcVariantAttribute> varAttrMap =
            getProjectObjectBO().getPidcDataHandler().getVariantAttributeMap().get(pidcVariant.getId());

        PidcVariantAttribute varAttr = varAttrMap.get(projAttr.getAttrId());

        if (varAttr != null) {
          boolean isVisible = isProjAttrVisibleAtAllLevels(varAttr);
          if (!isVisible) {
            return false;
          }
        }
      }
    }
    return true;

  }


  /**
   * @param projAttr IProjectAttribute
   * @return true if proj attr is part of invisible set
   */
  private boolean isProjAttrInvisible(final IProjectAttribute projAttr) {
    if (projAttr instanceof PidcVersionAttribute) {
      return getProjectObjectBO().getPidcDataHandler().getPidcVersInvisibleAttrSet().contains(projAttr.getAttrId());
    }
    else if (projAttr instanceof PidcVariantAttribute) {
      PidcVariantAttribute varAttr = (PidcVariantAttribute) projAttr;

      Set<Long> attrIdSet =
          getProjectObjectBO().getPidcDataHandler().getVariantInvisbleAttributeMap().get(varAttr.getVariantId());

      return (attrIdSet != null) && attrIdSet.contains(varAttr.getAttrId());

    }
    else if (projAttr instanceof PidcSubVariantAttribute) {
      PidcSubVariantAttribute subVarAttr = (PidcSubVariantAttribute) projAttr;
      Set<Long> attrIdSet = getProjectObjectBO().getPidcDataHandler().getSubVariantInvisbleAttributeMap()
          .get(subVarAttr.getSubVariantId());
      return (attrIdSet != null) && attrIdSet.contains(subVarAttr.getAttrId());
    }
    return false;
  }


  /**
   * @return boolean
   */
  public abstract boolean isVisible();

  /**
   * @param attrId Attribute id
   * @return boolean
   */
  public boolean isNotInvisibleAttr(final Long attrId, final IProjectAttribute grpdAttr) {
    return !getProjectObjectBO().getPidcDataHandler().getPidcVersInvisibleAttrSet().contains(attrId) &&
        isNotInvisibleVarAttr(attrId, grpdAttr) && isNotInvisibleSubVarAttr(attrId, grpdAttr);
  }

  /**
   * @param attrId attribute id
   * @return boolean
   */
  public boolean isNotInvisibleSubVarAttr(final Long attrId, final IProjectAttribute grpdAttr) {
    if ((grpdAttr instanceof PidcSubVariantAttribute) &&
        (null != ((PidcSubVariantAttribute) grpdAttr).getSubVariantId())) {
      Set<Long> invisibleSubVarAttrSet = getProjectObjectBO().getPidcDataHandler().getSubVariantInvisbleAttributeMap()
          .get(((PidcSubVariantAttribute) grpdAttr).getSubVariantId());
      return CommonUtils.isNullOrEmpty(invisibleSubVarAttrSet) || !invisibleSubVarAttrSet.contains(attrId);
    }
    return true;
  }

  /**
   * @param attrId attribute id
   * @return boolean
   */
  public boolean isNotInvisibleVarAttr(final Long attrId, final IProjectAttribute grpdAttr) {
    if ((grpdAttr instanceof PidcVariantAttribute) && (null != ((PidcVariantAttribute) grpdAttr).getVariantId())) {
      Set<Long> varInvisibleAttrSet = getProjectObjectBO().getPidcDataHandler().getVariantInvisbleAttributeMap()
          .get(((PidcVariantAttribute) grpdAttr).getVariantId());
      return CommonUtils.isNullOrEmpty(varInvisibleAttrSet) || !varInvisibleAttrSet.contains(attrId);
    }
    return true;
  }

  /**
   * Computes the condtion for the visible / mandatory for sorting
   *
   * @param pidcAttribute2 the pidc attribute
   * @return 1 or 0
   */
  public int computeAttrVisibMandConditions(final IProjectAttribute pidcAttribute2) {
    int compareResult;
    // required sorting order Visible & Mandatory, Visible & Attr Dependencies, Invisible, Other Attribute
    // attribute-1 fetching info
    boolean isAttrOneVisible = isVisible();
    boolean isAttrOneVisibleAndMandatory = isAttrOneVisible && isMandatory();
    boolean isAttrOneVisblAndHasAttrDepnd = isAttrOneVisible && hasAttrDependencies();
    boolean isAttrOneInvisible = !isAttrOneVisible;

    // attribute-2 fetching info
    AbstractProjectAttributeBO projAttrHandler = getProjAttrHandler(pidcAttribute2);
    boolean isAttrTwoVisible = projAttrHandler.isVisible();
    boolean isAttrTwoVisibleAndMandatory = isAttrTwoVisible && projAttrHandler.isMandatory();
    boolean isAttrTwoVisblAndHasAttrDepnd = isAttrTwoVisible && projAttrHandler.hasAttrDependencies();
    boolean isAttrTwoInvisible = !isAttrTwoVisible;


    // ICDM-2409
    if (isAttrOneVisibleAndMandatory) {
      if (isAttrTwoVisibleAndMandatory) {
        compareResult = 0;
      }
      else {
        compareResult = -1;
      }
    }
    else if (isAttrOneVisblAndHasAttrDepnd) {
      if (isAttrTwoVisibleAndMandatory) {
        compareResult = 1;
      }
      else if (isAttrTwoVisblAndHasAttrDepnd) {
        compareResult = 0;
      }
      else {
        compareResult = -1;
      }
    }
    else if (isAttrOneInvisible) {
      if (isAttrTwoVisibleAndMandatory || isAttrTwoVisblAndHasAttrDepnd) {
        compareResult = 1;
      }
      else if (isAttrTwoInvisible) {
        compareResult = 0;
      }
      else {
        compareResult = -1;
      }
    }
    else {
      if (isAttrTwoVisibleAndMandatory || isAttrTwoVisblAndHasAttrDepnd || isAttrTwoInvisible) {
        compareResult = 1;
      }
      else {
        compareResult = 0;
      }
    }
    return compareResult;
  }


  /**
   * Method to return projectattribute handler based on project attribute
   *
   * @param projAttr
   * @return project attribute handler
   */
  private AbstractProjectAttributeBO getProjAttrHandler(final IProjectAttribute projAttr) {
    AbstractProjectAttributeBO handler = null;
    if (projAttr instanceof PidcVersionAttribute) {
      handler = new PidcVersionAttributeBO((PidcVersionAttribute) projAttr, (PidcVersionBO) this.projObjBO);
    }
    else if (projAttr instanceof PidcVariantAttribute) {
      handler = new PidcVariantAttributeBO((PidcVariantAttribute) projAttr, (PidcVariantBO) this.projObjBO);
    }
    else if (projAttr instanceof PidcSubVariantAttribute) {
      handler = new PidcSubVariantAttributeBO((PidcSubVariantAttribute) projAttr, (PidcSubVariantBO) this.projObjBO);
    }
    return handler;
  }

  /**
   * @return true if the attribute is mandatory in that PIDCard
   */
  public boolean isMandatory() {

    String lvlAttrID = null;
    try {
      lvlAttrID = new CommonDataBO().getParameterValue(CommonParamKey.MANDATORY_LEVEL_ATTR);
      Long mandLvlAttrValId =
          this.projObjBO.getPidcDataHandler().getPidcVersAttrMap().get(Long.parseLong(lvlAttrID)).getValueId();
      Map<Long, MandatoryAttr> mandAttrMap =
          this.projObjBO.getPidcDataHandler().getMandatoryAttrMap().get(mandLvlAttrValId);
      if (CommonUtils.isNotEmpty(mandAttrMap)) {
        for (MandatoryAttr mandAttr : mandAttrMap.values()) {
          if (mandAttr.getAttrId().equals(this.projectAttr.getAttrId())) {
            return true;
          }
        }
      }
      else {
        return this.projObjBO.getPidcDataHandler().getAttributeMap().get(this.projectAttr.getAttrId()).isMandatory();
      }
      return false;
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }
  }

  /**
   * ICDM-1604 Check if the attr has any ATTRIBUTE dependencies, this skips value dependencies
   *
   * @return true if the attr has ATTRIBUTE dependencies
   */
  public boolean hasAttrDependencies() {
    Set<AttrNValueDependency> depenAttr =
        this.projObjBO.getPidcDataHandler().getAttrRefDependenciesMap().get(this.projectAttr.getAttrId());
    if (CommonUtils.isNotEmpty(depenAttr)) {
      for (AttrNValueDependency attrDependency : depenAttr) {
        if ((attrDependency != null) && (attrDependency.getAttributeId() != null)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return String value based on whether it is used,defined
   */
  public String getIsUsed() {

    if (isHiddenToUser()) {
      return "";
    }
    String returnValue;
    // ICDM-179
    if (this.projectAttr.isAtChildLevel()) {
      // variant and sub-variant attributes are always "used"
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType();
    }
    else if (getIsUsedEnum() != null) {
      returnValue = getIsUsedEnum().getUiType();
    }
    else {
      returnValue = ApicConstants.USED_INVALID_DISPLAY;
    }

    return returnValue;
  }

  /**
   * ICDM-2227
   *
   * @return ApicConstants.PROJ_ATTR_USED_FLAG
   */
  public ApicConstants.PROJ_ATTR_USED_FLAG getIsUsedEnum() {
    PROJ_ATTR_USED_FLAG returnValue;
    if (this.projectAttr.getId() == null) {
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR;
    }
    else {
      String dbUsedInfo = this.projectAttr.getUsedFlag();
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.getType(dbUsedInfo);
    }
    return returnValue;
  }


  /**
   * This method checks for whether PIDCAttribute value is hyper link value or not
   *
   * @return boolean
   */
  // ICDM-322
  public boolean isHyperLinkValue() {
    boolean isHyperLinkValue = false;
    if (!this.projObjBO.getPidcDataHandler().getAttributeMap().get(this.projectAttr.getAttrId()).isDeleted() &&
        (this.projectAttr.getValue() != null) && (AttributeValueType.HYPERLINK.getDisplayText() == this.projObjBO
            .getPidcDataHandler().getAttributeMap().get(this.projectAttr.getAttrId()).getValueType())) {
      isHyperLinkValue = true;
    }
    return isHyperLinkValue;
  }

  /**
   * An attribute is considered as defined if 1. it is used (Used = Yes) and a value is set 2. it is not set (Used = No)
   *
   * @param projAttr - project attribute
   * @return true if project attribute has value defined at same level or child level
   */
  public boolean isValueDefined(final IProjectAttribute projAttr) {

    if (projAttr instanceof PidcVersionAttribute) {
      PidcVersionAttribute versAttr = (PidcVersionAttribute) projAttr;
      if (versAttr.isAtChildLevel()) {
        return isValueDefinedAtVariantLevel(projAttr);
      }
      return isDefined(projAttr);
    }

    else if (projAttr instanceof PidcVariantAttribute) {
      PidcVariantAttribute varAttr = (PidcVariantAttribute) projAttr;
      if (varAttr.isAtChildLevel()) {
        return isValueDefinedAtSubvarLevel(varAttr);
      }
      return isDefined(projAttr);
    }

    else if (projAttr instanceof PidcSubVariantAttribute) {
      return isDefined(projAttr);
    }
    return false;
  }

  /**
   * @param usedFlag
   * @param projAttr
   * @return
   */
  public boolean isDefined(final IProjectAttribute projAttr) {

    String usedFlag = getProjectObjectBO().getIsUsedEnum(projAttr).getUiType();

    return !(isAttrNotDefOrNew(usedFlag) || isAttrUsedAndHasNoVal(projAttr, usedFlag) || isAttrValAvailable(projAttr));
  }


  /**
   * @param projAttr
   * @return
   */
  private boolean isAttrValAvailable(final IProjectAttribute projAttr) {
    return (projAttr.getValue() != null) &&
        (this.projObjBO.getPidcDataHandler().getAttributeValueMap().get(projAttr.getValueId()) != null) &&
        this.projObjBO.getPidcDataHandler().getAttributeValueMap().get(projAttr.getValueId()).isDeleted();
  }


  /**
   * @param projAttr
   * @param usedFlag
   * @return
   */
  private boolean isAttrUsedAndHasNoVal(final IProjectAttribute projAttr, final String usedFlag) {
    return ApicConstants.USED_YES_DISPLAY.equals(usedFlag) && (projAttr.getValue() == null);
  }


  /**
   * @param usedFlag
   * @return
   */
  private boolean isAttrNotDefOrNew(final String usedFlag) {
    return ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType().equals(usedFlag) ||
        ApicConstants.USED_NOTDEF_DISPLAY.equals(usedFlag);
  }


  /**
   * @param projAttr
   * @return
   */
  private boolean isValueDefinedAtVariantLevel(final IProjectAttribute projAttr) {

    Collection<PidcVariant> unDeletedVariants =
        new PidcVersionBO(getProjectObjectBO().getPidcVersion(), getProjectObjectBO().getPidcDataHandler())
            .getVariantsMap(false).values();
    if (CommonUtils.isNotEmpty(unDeletedVariants)) {
      for (PidcVariant pidcVariant : unDeletedVariants) {

        Map<Long, PidcVariantAttribute> varAttrMap =
            getProjectObjectBO().getPidcDataHandler().getVariantAttributeMap().get(pidcVariant.getId());

        if (varAttrMap.get(projAttr.getAttrId()) != null) {
          boolean result = isValueDefined(varAttrMap.get(projAttr.getAttrId()));
          if (!result) {
            return false;
          }
        }
      }

    }
    else {
      return false;
    }
    return true;

  }


  private boolean isValueDefinedAtSubvarLevel(final PidcVariantAttribute varAttr) {
    PidcVariant var = getProjectObjectBO().getPidcDataHandler().getVariantMap().get(varAttr.getVariantId());

    Collection<PidcSubVariant> unDeletedSubVariants =
        new PidcVariantBO(getProjectObjectBO().getPidcVersion(), var, getProjectObjectBO().getPidcDataHandler())
            .getSubVariantsMap(false).values();
    if (CommonUtils.isNotEmpty(unDeletedSubVariants)) {
      for (PidcSubVariant pidcSubVariant : unDeletedSubVariants) {
        Map<Long, PidcSubVariantAttribute> subVarAttrMap =
            getProjectObjectBO().getPidcDataHandler().getSubVariantAttributeMap().get(pidcSubVariant.getId());
        if ((subVarAttrMap.get(varAttr.getAttrId()) != null)) {
          boolean result = isValueDefined(subVarAttrMap.get(varAttr.getAttrId()));
          if (!result) {
            return false;
          }
        }

      }
    }
    else {
      return false;
    }
    return true;
  }

  /**
   * Checks whether the focus matrix defintion is applicable for this attribue in this project
   *
   * @return <code>true</code> if focus matrix definition is applicable
   */
  public abstract boolean isFocusMatrixApplicable();


  /**
   * @return the projectAttr
   */
  public IProjectAttribute getProjectAttr() {
    return this.projectAttr;
  }


  /**
   * @return the currentUser
   */
  public CurrentUserBO getCurrentUser() {
    return this.currentUser;
  }


  /**
   * @return the project object BO
   */
  public AbstractProjectObjectBO getProjectObjectBO() {
    return this.projObjBO;
  }


}
