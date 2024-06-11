/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.WebFlowAttrValues;
import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowData;

/**
 * @author dmr1cob
 */
public class PidcWebFlowDataCreator extends AbstractSimpleBusinessObject {


  /**
   * adapter instance
   */
  private final PidcWebFlowAdapter adapter;

  // Story 221726
  private final Map<Long, Long> allAlternateAttrMap;

  private final WebFlowDataCreator webFlowDataCreator;

  /**
   * @param adapter adapter
   * @param allAlternateAttrMap finalAttr
   * @param webFlowDataCreator {@link WebFlowDataCreator}
   */
  public PidcWebFlowDataCreator(final PidcWebFlowAdapter adapter, final Map<Long, Long> allAlternateAttrMap,
      final WebFlowDataCreator webFlowDataCreator, final ServiceData serviceData) {
    super(serviceData);
    this.adapter = adapter;
    this.allAlternateAttrMap = allAlternateAttrMap;
    this.webFlowDataCreator = webFlowDataCreator;
  }

  /**
   * @param pidcVariant pidcVariant
   * @param aliasDef aliasDef
   * @param allAttributes allAttributes
   * @return the set of web flow attr
   * @throws DataException
   */
  public Set<WebFlowAttribute> createPidcAttrForVar(final PidcVariant pidcVariant, final AliasDef aliasDef,
      final Map<Long, PidcVariantAttribute> allAttributes)
      throws DataException {
    Set<WebFlowAttribute> webFlowAttrSet = new HashSet<>();
    // Itreate the attributes
    for (PidcVariantAttribute varAttr : allAttributes.values()) {
      if (isAttrAliasPresent(varAttr)) {
        WebFlowAttribute webFlowAttr = this.adapter.createWebFlowAttribute(varAttr, this.webFlowDataCreator);
        if (varAttr.isAtChildLevel()) {
          // Story 221726
          setValuesForSubVar(pidcVariant, allAttributes, varAttr, webFlowAttr);
        }
        else {
          // Story 221726
          setAttrValues(allAttributes, varAttr, webFlowAttr);
        }
        webFlowAttrSet.add(webFlowAttr);
      }
    }

    return webFlowAttrSet;
  }

  /**
   * @param aliasDef
   * @param allAttributes
   * @param varAttr
   * @param webFlowAttr
   * @throws DataException
   */
  private void setAttrValues(final Map<Long, PidcVariantAttribute> allAttributes, final PidcVariantAttribute varAttr,
      final WebFlowAttribute webFlowAttr)
      throws DataException {
    if (null != this.allAlternateAttrMap.get(varAttr.getAttrId())) {
      IProjectAttribute alternateAttrVar = allAttributes.get(this.allAlternateAttrMap.get(varAttr.getId()));
      populateAttrValue(varAttr, webFlowAttr, alternateAttrVar);
    }
    else {
      populateAttrValue(varAttr, webFlowAttr, varAttr);
    }
  }

  /**
   * @param pidcVariant
   * @param aliasDef
   * @param allAttributes
   * @param varAttr
   * @param webFlowAttr
   * @throws DataException
   */
  private void setValuesForSubVar(final PidcVariant pidcVariant, final Map<Long, PidcVariantAttribute> allAttributes,
      final PidcVariantAttribute varAttr, final WebFlowAttribute webFlowAttr)
      throws DataException {
    if (null != this.allAlternateAttrMap.get(varAttr.getAttrId())) {
      IProjectAttribute alternateAttrSubVar = allAttributes.get(this.allAlternateAttrMap.get(varAttr.getId()));
      populateValuesForSubVar(pidcVariant, webFlowAttr, varAttr, alternateAttrSubVar);
    }
    else {
      populateValuesForSubVar(pidcVariant, webFlowAttr, varAttr, null);
    }
  }


  /**
   * @param aliasDef aliasDef
   * @param allAttributes allAttributes
   * @param allAlternateAttrMap2
   * @return the set of web flow attr
   * @throws DataException
   */
  public Set<WebFlowAttribute> createPidcAtrrForPidc(final Map<Long, PidcVersionAttribute> allAttributes,
      final Map<Long, Long> allAlternateAttrMap)
      throws DataException {
    Set<WebFlowAttribute> webFlowAttrSet = new HashSet<>();
    for (PidcVersionAttribute pidcAttr : allAttributes.values()) {
      // Do the changes only if the attr alias is present
      if (isAttrAliasPresent(pidcAttr)) {

        WebFlowAttribute attr = this.adapter.createWebFlowAttribute(pidcAttr, this.webFlowDataCreator);
        // Story 221726
        if (null != allAlternateAttrMap.get(pidcAttr.getAttrId())) {
          populateAttrValue(pidcAttr, attr, allAttributes.get(allAlternateAttrMap.get(pidcAttr.getAttrId())));
        }
        else {
          populateAttrValue(pidcAttr, attr, allAttributes.get(pidcAttr.getAttrId()));
        }
        webFlowAttrSet.add(attr);
      }
    }

    return webFlowAttrSet;
  }

  /**
   * 5050260 - Defect fix - Validate issue in getPidcWebFlowData() service when moving attribute to variant level
   *
   * @param aliasDef aliasDef
   * @param allAttributes allAttributes
   * @param allAlternateAttrMap2
   * @return the set of web flow attr
   * @throws DataException
   */
  public Map<Long, WebFlowAttribute> createPidcAtrrMapForPidc(final Map<Long, PidcVersionAttribute> allAttributes,
      final Map<Long, Long> allAlternateAttrMap)
      throws DataException {
    Map<Long, WebFlowAttribute> webFlowAttrSet = new HashMap<>();
    for (PidcVersionAttribute pidcAttr : allAttributes.values()) {
      // Do the changes only if the attr alias is present
      if (isAttrAliasPresent(pidcAttr)) {

        WebFlowAttribute attr = this.adapter.createWebFlowAttribute(pidcAttr, this.webFlowDataCreator);
        // Story 221726
        if (null != allAlternateAttrMap.get(pidcAttr.getAttrId())) {
          populateAttrValue(pidcAttr, attr, allAttributes.get(allAlternateAttrMap.get(pidcAttr.getAttrId())));
        }
        else {
          populateAttrValue(pidcAttr, attr, allAttributes.get(pidcAttr.getAttrId()));
        }
        webFlowAttrSet.put(attr.getAttrID(), attr);
      }
    }

    return webFlowAttrSet;
  }


  /**
   * @param aliasDef
   * @param pidcAttr,
   * @param attr
   * @param pidcFinalAttr
   * @param allAlternateAttrMap
   * @param allAttributes
   * @param user
   * @throws DataException
   */
  private void populateAttrValue(final IProjectAttribute pidcAttr, final WebFlowAttribute attr,
      final IProjectAttribute pidcFinalAttr)
      throws DataException {
    if ((this.allAlternateAttrMap != null) && (pidcFinalAttr != null) && (pidcFinalAttr.getValueId() != null)) {
      setValueForAlternateAttr(attr, pidcFinalAttr);
    }
    else {
      setAttrValue(pidcAttr, attr);
    }
  }


  /**
   * @param aliasDef
   * @param attr
   * @param pidcFinalAttr
   * @throws DataException
   */
  private void setValueForAlternateAttr(final WebFlowAttribute attr, final IProjectAttribute pidcFinalAttr)
      throws DataException {
    if (!isVisible(pidcFinalAttr)) {
      // Task 232488
      WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
      webFlowAttrVal.setValueID(0l);
      webFlowAttrVal.setAliasName(false);
      webFlowAttrVal.setValueDesc("Not defined in PIDC (Missing dependencies)");
      webFlowAttrVal.setValueName("n.a.");
      attr.getWebFlowAttrValues().add(webFlowAttrVal);
    }
    else if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcFinalAttr.getUsedFlag())) {
      WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
      webFlowAttrVal.setValueID(0l);
      webFlowAttrVal.setAliasName(false);
      webFlowAttrVal.setValueDesc("Not defined in PIDC (Used = No)");
      webFlowAttrVal.setValueName("n.a.");
      attr.getWebFlowAttrValues().add(webFlowAttrVal);
    }
    else {
      AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
      AttributeValue attrValue = attrValueLoader.getDataObjectByID(pidcFinalAttr.getValueId());
      if (null != attrValue) {
        WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
        webFlowAttrVal.setValueID(attrValue.getId());
        webFlowAttrVal.setAliasName(isValueAliasPresent(attrValue, pidcFinalAttr));
        webFlowAttrVal.setValueDesc(attrValue.getDescription());
        String valueName = setValueName(pidcFinalAttr, attrValue);
        webFlowAttrVal.setValueName(valueName);
        attr.getWebFlowAttrValues().add(webFlowAttrVal);
      }
    }
  }

  /**
   * @param pidcFinalAttr
   * @param attrValue
   * @return
   */
  private String setValueName(final IProjectAttribute pidcFinalAttr, final AttributeValue attrValue) {
    String valueName = null;
    if (attrValue.getValueType() == AttributeValueType.TEXT.getDisplayText()) {
      valueName = getValueAliasForAliasDef(pidcFinalAttr);
    }
    String stringValue;
    switch (AttributeValueType.getType(attrValue.getValueType())) {
      case TEXT:
        stringValue = attrValue.getTextValueEng();
        break;

      case NUMBER:
        stringValue = attrValue.getNumValue().toString();
        break;

      case DATE:
        stringValue = attrValue.getDateValue();
        break;

      case BOOLEAN:
        stringValue = attrValue.getBoolvalue();
        break;

      // iCDM-321
      case HYPERLINK:
        stringValue = attrValue.getTextValueEng();
        break;
      case ICDM_USER:
        stringValue = attrValue.getTextValueEng();
        break;
      default:
        stringValue = "ERROR";
        break;
    }
    return valueName == null ? stringValue : valueName;
  }


  /**
   * @param aliasDef
   * @param pidcAttr
   * @param attr
   * @throws DataException
   */
  private void setAttrValue(final IProjectAttribute pidcAttr, final WebFlowAttribute attr) throws DataException {

    // Attr value not null
    if (pidcAttr instanceof PidcVariantAttribute) {
      Set<WebFlowAttrValues> webFlowAttrValSet = new HashSet<>();
      PidcVariantAttribute varAttr = (PidcVariantAttribute) pidcAttr;
      if (null != pidcAttr.getValueId()) {
        AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
        AttributeValue attrValue = attrValueLoader.getDataObjectByID(pidcAttr.getValueId());
        if (null != attrValue) {
          WebFlowAttrValues webFlowAttrValVar = new WebFlowAttrValues();
          webFlowAttrValVar.setValueID(attrValue.getId());
          webFlowAttrValVar.setAliasName(false);
          webFlowAttrValVar.setValueDesc(attrValue.getDescription());
          String valueName = setValueName(varAttr, attrValue);
          webFlowAttrValVar.setValueName(valueName);
          webFlowAttrValSet.add(webFlowAttrValVar);
        }
      }
      attr.getWebFlowAttrValues().addAll(webFlowAttrValSet);
    }
    else if (CommonUtils.isNotNull(pidcAttr.getValueId())) {
      AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
      AttributeValue attrValue = attrValueLoader.getDataObjectByID(pidcAttr.getValueId());
      if (null != attrValue) {
        WebFlowAttrValues webFlowAttrVal =
            this.adapter.createWebFlowAttrValue(pidcAttr, this.webFlowDataCreator, attrValue);
        attr.getWebFlowAttrValues().add(webFlowAttrVal);
      }
    }
    else if (!isVisible(pidcAttr)) {
      // Task 232488
      WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
      webFlowAttrVal.setValueID(0l);
      webFlowAttrVal.setAliasName(false);
      webFlowAttrVal.setValueDesc("Not defined in PIDC (Missing dependencies)");
      webFlowAttrVal.setValueName("n.a.");
      attr.getWebFlowAttrValues().add(webFlowAttrVal);
    }
    // Used flag no.
    else if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedFlag())) {
      WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
      webFlowAttrVal.setValueID(0l);
      webFlowAttrVal.setAliasName(false);
      webFlowAttrVal.setValueDesc("Not defined in PIDC (Used = No)");
      webFlowAttrVal.setValueName("n.a.");
      attr.getWebFlowAttrValues().add(webFlowAttrVal);
    }

  }


  /**
   * @param pidcVariant pidcVariant
   * @param aliasDef aliasDef
   * @param attr attr
   * @param varAttr
   * @param pidcFinalAttr
   * @param user
   * @throws DataException
   */
  private void populateValuesForSubVar(final PidcVariant pidcVariant, final WebFlowAttribute attr,
      final PidcVariantAttribute varAttr, final IProjectAttribute pidcFinalAttr)
      throws DataException {
    // get all sub varaiants
    Map<Long, PidcSubVariant> subVariantsMap =
        this.webFlowDataCreator.getPidcVersAttrModel().getSubVariantMap(pidcVariant.getId());
    // Itreate each sub var
    for (PidcSubVariant subVar : subVariantsMap.values()) {
      if (!subVar.isDeleted()) {
        // get all the attrs of the sub var
        Map<Long, PidcSubVariantAttribute> allAttr =
            this.webFlowDataCreator.getPidcVersAttrModel().getAllSubVariantAttrMap().get(subVar.getId());
        // Ireate the sub var attributes
        for (IProjectAttribute subVarAttr : allAttr.values()) {
          if (isAttrAliasPresent(varAttr) && varAttr.getAttrId().equals(subVarAttr.getAttrId())) {
            populateAttrValue(subVarAttr, attr, pidcFinalAttr);
          }
        }
      }
    }
  }


  /**
   * @param pidcRemAttr pidcRemAttr
   * @param aliasDef aliasDef
   * @param pidcWebFlowData pidcWebFlowData
   * @param allAttributes
   * @throws DataException
   */
  public void populateRemAliasAttrinResp(final Set<IProjectAttribute> pidcRemAttr, final AliasDef aliasDef,
      final PidcWebFlowData pidcWebFlowData, final Map<Long, PidcVariantAttribute> allAttributes)
      throws DataException {
    for (IProjectAttribute pidcAttribute : pidcRemAttr) {
      if ((pidcAttribute != null) && isAttrAliasPresent(pidcAttribute)) {
        WebFlowAttribute webFlowAttr = this.adapter.createWebFlowAttribute(pidcAttribute, this.webFlowDataCreator);
        // Story 221726
        if (null != this.allAlternateAttrMap.get(pidcAttribute.getAttrId())) {
          final IProjectAttribute pidcAlternateAttr =
              allAttributes.get(this.allAlternateAttrMap.get(pidcAttribute.getAttrId()));

          populateAttrValue(pidcAttribute, webFlowAttr, pidcAlternateAttr);
        }
        else {

          populateAttrValue(pidcAttribute, webFlowAttr, pidcAttribute);
        }
        pidcWebFlowData.getWebFlowAttr().add(webFlowAttr);
      }
    }
  }

  // Review 238922
  /**
   * @param pidcVariant
   * @param aliasDef
   * @param allPidcAttr
   * @param allAlternateAttrMap2
   * @return
   * @throws DataException
   */
  public Set<WebFlowAttribute> createPidcAtrrForPidcVar(final PidcVariant pidcVariant, final AliasDef aliasDef,
      final Map<Long, PidcVersionAttribute> allPidcAttr, final Map<Long, Long> allAlternateAttrMap)
      throws DataException {
    Set<WebFlowAttribute> webFlowAttrSet = new HashSet<>();
    for (PidcVersionAttribute pidcAttr : allPidcAttr.values()) {
      // Do the changes only if the attr alias is present
      if (isAttrAliasPresent(pidcAttr)) {

        WebFlowAttribute attr = this.adapter.createWebFlowAttribute(pidcAttr, this.webFlowDataCreator);
        // Story 221726
        if (null != allAlternateAttrMap.get(pidcAttr.getAttrId())) {
          PidcVersionAttribute finalAttrPidc = allPidcAttr.get(allAlternateAttrMap.get(pidcAttr.getAttrId()));

          setAttrValue(pidcVariant, allPidcAttr, allAlternateAttrMap, pidcAttr, attr, finalAttrPidc);
        }
        else {
          populateAttrValue(pidcAttr, attr, allPidcAttr.get(pidcAttr.getAttrId()));
        }
        webFlowAttrSet.add(attr);
      }
    }

    return webFlowAttrSet;
  }

  /**
   * @param pidcVariant
   * @param aliasDef
   * @param allPidcAttr
   * @param allAlternateAttrMap
   * @param pidcAttr
   * @param attr
   * @param finalAttrPidc
   * @throws DataException
   */
  private void setAttrValue(final PidcVariant pidcVariant, final Map<Long, PidcVersionAttribute> allPidcAttr,
      final Map<Long, Long> allAlternateAttrMap, final PidcVersionAttribute pidcAttr, final WebFlowAttribute attr,
      final PidcVersionAttribute finalAttrPidc)
      throws DataException {
    if ((null != finalAttrPidc) && finalAttrPidc.isAtChildLevel()) {
      PidcVariantAttribute varAttr = this.webFlowDataCreator.getPidcVersAttrModel()
          .getVariantAttributeMap(pidcVariant.getId()).get(finalAttrPidc.getAttrId());
      populateAttrValue(pidcAttr, attr, varAttr);
    }
    else {
      populateAttrValue(pidcAttr, attr, allPidcAttr.get(allAlternateAttrMap.get(pidcAttr.getAttrId())));
    }
  }

  /**
   * @param aliasDefinition aliasDefinition
   * @return true if the attr alias is present
   */
  private boolean isAttrAliasPresent(final IProjectAttribute varAttr) {
    // if the alias def is null or attribute alias value is null
    return ((this.webFlowDataCreator.getAliasDef() != null) && (getEffAttrAliasName(varAttr) != null));
  }

  /**
   * @param attr attribute
   * @return the effective alias name for attribute
   */
  private String getEffAttrAliasName(final IProjectAttribute varAttr) {
    for (AliasDetail aliasDetail : this.webFlowDataCreator.getAliasDetailMap().values()) {
      if ((null != aliasDetail.getAttrId()) && (null != varAttr) && (null != varAttr.getAttrId()) &&
          aliasDetail.getAttrId().equals(varAttr.getAttrId())) {
        return aliasDetail.getAliasName();
      }
    }
    return null;
  }

  private boolean isVisible(final IProjectAttribute pidcFinalAttr) {
    return !this.webFlowDataCreator.getPidcVersAttrModel().getPidcVersInvisibleAttrSet()
        .contains(pidcFinalAttr.getAttrId());
  }

  private String getValueAliasForAliasDef(final IProjectAttribute varAttr) {
    String effValAliasName = null;
    // If alias Def is not null and attr Value is not null and type is text then take the alias otherwise take the attr
    // val eng
    if ((this.webFlowDataCreator.getAliasDef() != null) && (varAttr.getValueId() != null)) {
      effValAliasName = getEffValueAttrAliasName(varAttr);
    }
    return effValAliasName;
  }

  /**
   * @param attr attribute
   * @return the effective alias name for attribute
   */
  private String getEffValueAttrAliasName(final IProjectAttribute varAttr) {
    for (AliasDetail aliasDetail : this.webFlowDataCreator.getAliasDetailMap().values()) {
      if ((null != aliasDetail.getValueId()) && (null != varAttr) && (null != varAttr.getValueId()) &&
          aliasDetail.getValueId().equals(varAttr.getValueId())) {
        return aliasDetail.getAliasName();
      }
    }
    return null;
  }

  private boolean isValueAliasPresent(final AttributeValue attrValue, final IProjectAttribute pidcAttr) {
    boolean flag = true;
    // If alias def is null or attr val is null or value is not text type or Value alias is null.
    if ((this.webFlowDataCreator.getAliasDef() == null) || (attrValue == null) ||
        (attrValue.getValueType() != AttributeValueType.TEXT.getDisplayText()) ||
        (getEffValueAttrAliasName(pidcAttr) == null)) {
      flag = false;
    }
    return flag;
  }
}
