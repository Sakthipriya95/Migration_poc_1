/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l.precal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.icdm.model.a2l.precal.PRECAL_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalAttrValResponse;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalData;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalInputData;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.a2l.precal.PreCalDataServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 */
public class PreCalDataWizardDataHandler {

  private final List<A2LParameter> paramList;
  private final PidcA2LBO pidcA2LBO;
  private final A2LFileInfoBO a2lFileInfoBO;


  /**
   * The sys conts.
   */
  private SortedSet<A2LSystemConstantValues> sysConts;

  /**
   * The fcs.
   */
  private SortedSet<Function> fcs;

  /**
   * exception occured or not.
   */
  private Exception wsException;

  /**
   * boolean value exact Match Only filter selection.
   */
  private boolean exactMatchOnly;

  /**
   * The rule set parameter list.
   */
  private final List<String> ruleSetParameterList = new ArrayList<>();

  private PreCalAttrValResponse projAttrValDetails;

  private PidcVariant selVariant;

  /**
   * The selected rule set.
   */
  private RuleSet selectedRuleSet;

  /**
   * The is load to A 2 l selected.
   */
  private boolean isLoadToA2lSelected = false;

  private PRECAL_SOURCE_TYPE preCalSourceType = PRECAL_SOURCE_TYPE.COMMON_RULES;

  /**
   * flag changed or not.
   */
  private boolean flagChanged;


  /**
   * CalData map.
   */
  private final Map<String, CalData> calDataMap = new HashMap<>();

  /**
   * @param paramList selected lsit of a2l parameters
   * @param pidcA2LBO PIDC A2L BO
   * @param a2lFileInfoBO A2L Editor data BO
   */
  public PreCalDataWizardDataHandler(final List<A2LParameter> paramList, final PidcA2LBO pidcA2LBO,
      final A2LFileInfoBO a2lFileInfoBO) {
    this.paramList = paramList;
    this.pidcA2LBO = pidcA2LBO;
    this.a2lFileInfoBO = a2lFileInfoBO;
  }

  /**
   * Checks if is load to A 2 l selected.
   *
   * @return the isLoadToA2lSelected
   */
  public boolean isLoadToA2lSelected() {
    return this.isLoadToA2lSelected;
  }


  /**
   * Sets the load to A 2 l selected.
   *
   * @param isLoadToA2lSelected the isLoadToA2lSelected to set
   */
  public void setLoadToA2lSelected(final boolean isLoadToA2lSelected) {
    this.isLoadToA2lSelected = isLoadToA2lSelected;
  }


  /**
   * Gets the selected rule set.
   *
   * @return the selectedRuleSet
   */
  public RuleSet getSelectedRuleSet() {
    return this.selectedRuleSet;
  }


  /**
   * Sets the selected rule set.
   *
   * @param selectedRuleSet the selectedRuleSet to set
   */
  public void setSelectedRuleSet(final RuleSet selectedRuleSet) {
    this.selectedRuleSet = selectedRuleSet;
  }


  /**
   * Gets the rule set parameter list.
   *
   * @return the ruleSetParameterList
   */
  public List<String> getRuleSetParameterList() {
    // Icdm-1371- store the RuleSetparamList
    return this.ruleSetParameterList;
  }


  /**
   * Gets the ws exception.
   *
   * @return the wsException
   */
  public Exception getWsException() {
    return this.wsException;
  }


  /**
   * Sets the ws exception.
   *
   * @param wsException the wsException to set
   */
  public void setWsException(final Exception wsException) {
    this.wsException = wsException;
  }


  /**
   * Checks if is ws exp occured.
   *
   * @return the wsExpOccured
   */
  public boolean isWsExpOccured() {
    return this.wsException != null;
  }

  /**
   * Gets the sys conts.
   *
   * @return the sysConts
   */
  public SortedSet<A2LSystemConstantValues> getSysConts() {
    if ((this.sysConts == null) || this.sysConts.isEmpty()) {
      this.sysConts = getA2lFileInfoBO().getSystemConstantDetails(getA2lFileInfoBO().getSystemConstants());

    }
    return this.sysConts;
  }

  /**
   * Gets the f cs.
   *
   * @return the bcs
   */
  public SortedSet<Function> getFCs() {
    if ((this.fcs == null) || this.fcs.isEmpty()) {
      this.fcs = getA2lFileInfoBO().getFunctionsOfLabelType(null);

    }
    return this.fcs;
  }

  /**
   * Checks if is flag changed.
   *
   * @return the flagChanged
   */
  public boolean isFlagChanged() {
    return this.flagChanged;
  }


  /**
   * Sets the flag changed.
   *
   * @param flagChanged the flagChanged to set
   */
  public void setFlagChanged(final boolean flagChanged) {
    this.flagChanged = flagChanged;
  }


  /**
   * Gets the cal data map.
   *
   * @return the calDataMap
   */
  public Map<String, CalData> getCalDataMap() {
    return this.calDataMap;
  }


  /**
   * @return the paramList
   */
  public List<A2LParameter> getParamList() {
    return this.paramList;
  }


  /**
   * @return the pidcA2LBO
   */
  public PidcA2LBO getPidcA2LBO() {
    return this.pidcA2LBO;
  }


  /**
   * @return the a2lFileInfoBO
   */
  public A2LFileInfoBO getA2lFileInfoBO() {
    return this.a2lFileInfoBO;
  }

  /**
   * Sets the Exact Match Only filter selection state.
   *
   * @param selectionState the Exact Match Only state to set
   */
  public void setExactMatchOnly(final boolean selectionState) {
    this.exactMatchOnly = selectionState;
  }

  /**
   * @return the projAttrValDetails
   */
  public PreCalAttrValResponse getProjAttrValDetails() {
    return this.projAttrValDetails;
  }


  /**
   * @param projAttrValDetails the projAttrValDetails to set
   */
  public void setProjAttrValDetails(final PreCalAttrValResponse projAttrValDetails) {
    this.projAttrValDetails = projAttrValDetails;
  }


  /**
   * @return the selVariant
   */
  public PidcVariant getSelVariant() {
    return this.selVariant;
  }


  /**
   * @param selVariant the selVariant to set
   */
  public void setSelVariant(final PidcVariant selVariant) {
    this.selVariant = selVariant;
  }


  /**
   * @return the preCalSourceType
   */
  public PRECAL_SOURCE_TYPE getPreCalSourceType() {
    return this.preCalSourceType;
  }


  /**
   * @param preCalSourceType the preCalSourceType to set
   */
  public void setPreCalSourceType(final PRECAL_SOURCE_TYPE preCalSourceType) {
    this.preCalSourceType = preCalSourceType;
  }

  /**
   * Load project attr value details
   */
  public void loadProjAttrValDetails() {
    PreCalInputData input = new PreCalInputData();
    input.setPidcA2lId(getPidcA2LBO().getPidcA2lId());
    input.setVariantId(getSelVariant() == null ? null : getSelVariant().getId());
    input.setSourceType(this.preCalSourceType.getTypeCode());

    if (getSelectedRuleSet() == null) {
      input.setParamSet(getParamList().stream().map(A2LParameter::getName).collect(Collectors.toSet()));
    }
    else {
      input.setRuleSetId(getSelectedRuleSet().getId());
      input.setParamSet(new HashSet<>(getRuleSetParameterList()));
    }

    PreCalAttrValResponse prjAttrValDet = null;
    try {
      prjAttrValDet = new PreCalDataServiceClient().getPreCalAttrVals(input);
      setProjAttrValDetails(prjAttrValDet);

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @throws ApicWebServiceException error from service call
   * @throws ClassNotFoundException error while converting caldata in byte array to java object
   * @throws IOException error while converting caldata in byte array to java object
   */
  public void fetchPreCalData() throws ApicWebServiceException, ClassNotFoundException, IOException {

    PreCalInputData input = new PreCalInputData();
    input.setPidcA2lId(getPidcA2LBO().getPidcA2lId());
    input.setVariantId(getSelVariant() == null ? null : getSelVariant().getId());

    if (getSelectedRuleSet() == null) {
      input.setSourceType(PRECAL_SOURCE_TYPE.COMMON_RULES.getTypeCode());
      input.setParamSet(getParamList().stream().map(A2LParameter::getName).collect(Collectors.toSet()));
    }
    else {
      input.setSourceType(PRECAL_SOURCE_TYPE.RULESET_RULES.getTypeCode());
      input.setRuleSetId(getSelectedRuleSet().getId());
      input.setParamSet(new HashSet<>(getRuleSetParameterList()));
    }

    input.setRefValues(true);// Only available option
    input.setOnlyExactMatch(this.exactMatchOnly);

    PreCalData ret = new PreCalDataServiceClient().getPreCalData(input);
    getCalDataMap().clear();
    for (Entry<String, byte[]> entry : ret.getPreCalDataMap().entrySet()) {
      CalData calData = CalDataUtil.getCalDataObj(entry.getValue());
      getCalDataMap().put(entry.getKey(), calData);
    }
  }

  /**
   * Project attribute value text to be displayed for the given attribute
   *
   * @param attr selected attribute
   * @return display text
   */
  public String getProjAttrValDisplayTxt(final Attribute attr) {
    PidcVersionAttribute pAttr = this.projAttrValDetails.getPidcAttrMap().get(attr.getId());
    if (pAttr == null) {
      return ApicConstants.ATTR_NOT_DEFINED;
    }
    if (pAttr.isAtChildLevel()) {
      return getProjAttrValDisplayTxtIfVariant(attr);
    }
    if (ApicConstants.CODE_NO.equals(pAttr.getUsedFlag())) {
      return ApicConstants.NOT_USED;
    }

    return pAttr.getValue() == null ? ApicConstants.ATTR_NOT_DEFINED : pAttr.getValue();
  }

  /**
   * Project attribute value Tool Tip to be displayed for the given attribute
   *
   * @param attr selected attribute
   * @return Tool Tip
   */
  public String getProjAttrValDisplayToolTip(final Attribute attr) {

    String dispVal = getProjAttrValDisplayTxt(attr);

    String tooltip;
    switch (dispVal) {
      case ApicConstants.ATTR_NOT_DEFINED:
        tooltip = "Value not defined in Project ID Card/variant";
        break;
      case ApicConstants.HIDDEN_VALUE:
        tooltip = "Value is hidden. Insufficient access rights in the Project ID Card.";
        break;
      case ApicConstants.NOT_USED:
        tooltip = "Attribute is not used in the Project ID Card/variant";
        break;
      case ApicConstants.VARIANT_SELECT:
        tooltip = "Select a variant to set the value";
        break;
      case ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME:
        tooltip = "Value is defined at Sub-Variant level. This is not supported. Select a different variant.";
        break;
      case ApicConstants.VARIANT_ATTR_DISPLAY_NAME:
        tooltip = "Value is defined at variant level. Select a variant to continue";
        break;
      default:
        tooltip = getTooltipFromValue(attr);
    }
    return tooltip;
  }

  /**
   * @param attr
   * @return
   */
  private String getTooltipFromValue(final Attribute attr) {
    String tooltip = null;
    IProjectAttribute projAttr = this.projAttrValDetails.getPidcVarAttrMap().get(attr.getId());
    projAttr = projAttr == null ? this.projAttrValDetails.getPidcAttrMap().get(attr.getId()) : projAttr;
    if ((projAttr != null) && (projAttr.getValueId() != null)) {
      AttributeValue val = this.projAttrValDetails.getAttrValMap().get(projAttr.getValueId());
      tooltip = "Value : " + projAttr.getValue() + "\nDescription : " + val.getDescription();
    }
    return tooltip;
  }

  /**
   * @param attr
   * @return
   */
  private String getProjAttrValDisplayTxtIfVariant(final Attribute attr) {
    if (this.selVariant != null) {
      PidcVariantAttribute varAttr = this.projAttrValDetails.getPidcVarAttrMap().get(attr.getId());
      if (varAttr == null) {
        return ApicConstants.ATTR_NOT_DEFINED;
      }
      if (ApicConstants.CODE_NO.equals(varAttr.getUsedFlag())) {
        return ApicConstants.NOT_USED;
      }
      return varAttr.getValue() == null ? ApicConstants.ATTR_NOT_DEFINED : varAttr.getValue();
    }
    return ApicConstants.VARIANT_SELECT;
  }

  /**
   * Check whether the ruleset selected is restricted to the user
   *
   * @param ruleSet rule set selected
   * @return true, if restricted
   */
  public boolean isRuleSetRestricted(final RuleSet ruleSet) {
    try {
      if (!ruleSet.isDeleted() && !ruleSet.isReadAccessFlag()) {
        return false;
      }
      NodeAccess access = new CurrentUserBO().getNodeAccessRight(ruleSet.getId());
      return (access == null) || !access.isRead();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }

}
