/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.WebFlowAttrValues;
import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowData;

/**
 * @author rgo7cob
 */
public class PidcWebFlowDataCreator {


  /**
   * adapter instance
   */
  private final PidcWebFlowAdapter adapter;


  /**
   * apic User for the request
   */
  private final ApicUser apicUser;
  // Story 221726
  private final ConcurrentMap<Long, Long> allAlternateAttrMap;

  /**
   * @param adapter adapter
   * @param allAlternateAttrMap finalAttr
   * @param user apicUser from request
   */
  public PidcWebFlowDataCreator(final PidcWebFlowAdapter adapter, final ConcurrentMap<Long, Long> allAlternateAttrMap,
      final ApicUser user) {
    this.adapter = adapter;
    this.allAlternateAttrMap = allAlternateAttrMap;
    this.apicUser = user;
  }

  /**
   * @param pidcVariant pidcVariant
   * @param aliasDef aliasDef
   * @param allAttributes allAttributes
   * @return the set of web flow attr
   */
  public Set<WebFlowAttribute> createPidcAttrForVar(final PIDCVariant pidcVariant, final AliasDefinition aliasDef,
      final Map<Long, PIDCAttributeVar> allAttributes) {
    Set<WebFlowAttribute> webFlowAttrSet = new HashSet<>();
    // Itreate the attributes
    for (PIDCAttributeVar varAttr : allAttributes.values()) {
      if (varAttr.isAttrAliasPresent(aliasDef)) {
        WebFlowAttribute webFlowAttr = this.adapter.createWebFlowAttribute(varAttr, aliasDef);
        if (varAttr.isVariant()) {
          // Story 221726
          setValuesForSubVar(pidcVariant, aliasDef, allAttributes, varAttr, webFlowAttr);
        }
        else {
          // Story 221726
          setAttrValues(aliasDef, allAttributes, varAttr, webFlowAttr);
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
   */
  private void setAttrValues(final AliasDefinition aliasDef, final Map<Long, PIDCAttributeVar> allAttributes,
      final PIDCAttributeVar varAttr, final WebFlowAttribute webFlowAttr) {
    if (null != this.allAlternateAttrMap.get(varAttr.getAttribute().getID())) {
      IPIDCAttribute alternateAttrVar = allAttributes.get(this.allAlternateAttrMap.get(varAttr.getID()));
      populateAttrValue(aliasDef, varAttr, webFlowAttr, alternateAttrVar);
    }
    else {
      populateAttrValue(aliasDef, varAttr, webFlowAttr, varAttr);
    }
  }

  /**
   * @param pidcVariant
   * @param aliasDef
   * @param allAttributes
   * @param varAttr
   * @param webFlowAttr
   */
  private void setValuesForSubVar(final PIDCVariant pidcVariant, final AliasDefinition aliasDef,
      final Map<Long, PIDCAttributeVar> allAttributes, final PIDCAttributeVar varAttr,
      final WebFlowAttribute webFlowAttr) {
    if (null != this.allAlternateAttrMap.get(varAttr.getAttribute().getID())) {
      IPIDCAttribute alternateAttrSubVar = allAttributes.get(this.allAlternateAttrMap.get(varAttr.getID()));
      populateValuesForSubVar(pidcVariant, aliasDef, webFlowAttr, varAttr, alternateAttrSubVar);
    }
    else {
      populateValuesForSubVar(pidcVariant, aliasDef, webFlowAttr, varAttr, null);
    }
  }


  /**
   * @param aliasDef aliasDef
   * @param allAttributes allAttributes
   * @param allAlternateAttrMap2
   * @return the set of web flow attr
   */
  public Set<WebFlowAttribute> createPidcAtrrForPidc(final AliasDefinition aliasDef,
      final Map<Long, PIDCAttribute> allAttributes, final Map<Long, Long> allAlternateAttrMap) {
    Set<WebFlowAttribute> webFlowAttrSet = new HashSet<>();
    for (PIDCAttribute pidcAttr : allAttributes.values()) {
      // Do the changes only if the attr alias is present
      if (pidcAttr.isAttrAliasPresent(aliasDef)) {

        WebFlowAttribute attr = this.adapter.createWebFlowAttribute(pidcAttr, aliasDef);
        // Story 221726
        if (null != allAlternateAttrMap.get(pidcAttr.getAttribute().getID())) {
          populateAttrValue(aliasDef, pidcAttr, attr,
              allAttributes.get(allAlternateAttrMap.get(pidcAttr.getAttribute().getID())));
        }
        else {
          populateAttrValue(aliasDef, pidcAttr, attr, allAttributes.get(pidcAttr.getEffectiveAttrAlias()));
        }
        webFlowAttrSet.add(attr);
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
   */
  private void populateAttrValue(final AliasDefinition aliasDef, final IPIDCAttribute pidcAttr,
      final WebFlowAttribute attr, final IPIDCAttribute pidcFinalAttr) {
    if ((this.allAlternateAttrMap != null) && (pidcFinalAttr != null) && (pidcFinalAttr.getAttributeValue() != null)) {
      setValueForAlternateAttr(aliasDef, attr, pidcFinalAttr);
    }
    else {
      setAttrValue(aliasDef, pidcAttr, attr);
    }
  }


  /**
   * @param aliasDef
   * @param attr
   * @param pidcFinalAttr
   */
  private void setValueForAlternateAttr(final AliasDefinition aliasDef, final WebFlowAttribute attr,
      final IPIDCAttribute pidcFinalAttr) {
    if (!pidcFinalAttr.isVisible()) {
      // Task 232488
      WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
      webFlowAttrVal.setValueID(0l);
      webFlowAttrVal.setAliasName(false);
      webFlowAttrVal.setValueDesc("Not defined in PIDC (Missing dependencies)");
      webFlowAttrVal.setValueName("n.a.");
      attr.getWebFlowAttrValues().add(webFlowAttrVal);
    }
    else if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcFinalAttr.getUsedInfo())) {
      WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
      webFlowAttrVal.setValueID(0l);
      webFlowAttrVal.setAliasName(false);
      webFlowAttrVal.setValueDesc("Not defined in PIDC (Used = No)");
      webFlowAttrVal.setValueName("n.a.");
      attr.getWebFlowAttrValues().add(webFlowAttrVal);
    }
    else {
      if (pidcFinalAttr instanceof PIDCAttributeVar) {
        // for (PIDCVariant var : pidcFinalAttr.getPidcVersion().getVariantsMap().values()) {
        // PIDCAttributeVar varAttr = var.getAttributesAll().get(pidcFinalAttr.getAttribute().getID());
        PIDCAttributeVar varAttr = (PIDCAttributeVar) pidcFinalAttr;
        if (null != varAttr.getAttributeValue()) {
          WebFlowAttrValues webFlowAttrValVar = new WebFlowAttrValues();
          webFlowAttrValVar.setValueID(varAttr.getAttributeValue().getID());
          webFlowAttrValVar.setAliasName(false);
          webFlowAttrValVar.setValueDesc(varAttr.getAttributeValue().getDescription());
          webFlowAttrValVar.setValueName(varAttr.getValueAliasForAliasDef(aliasDef));
          attr.getWebFlowAttrValues().add(webFlowAttrValVar);
        }
        // }
      }
      else {
        WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
        webFlowAttrVal.setValueID(pidcFinalAttr.getAttributeValue().getID());
        webFlowAttrVal.setAliasName(false);
        webFlowAttrVal.setValueDesc(pidcFinalAttr.getAttributeValue().getDescription());
        webFlowAttrVal.setValueName(pidcFinalAttr.getValueAliasForAliasDef(aliasDef));
        attr.getWebFlowAttrValues().add(webFlowAttrVal);
      }
    }
  }


  /**
   * @param aliasDef
   * @param pidcAttr
   * @param attr
   */
  private void setAttrValue(final AliasDefinition aliasDef, final IPIDCAttribute pidcAttr,
      final WebFlowAttribute attr) {
    // Attr value not null
    if (pidcAttr.getAttribute().getID() == 224L) {
      System.out.println();
    }
    if (pidcAttr instanceof PIDCAttributeVar) {
      Set<WebFlowAttrValues> webFlowAttrValSet = new HashSet<>();
      // for (PIDCVariant var : pidcAttr.getPidcVersion().getVariantsMap().values()) {
      // PIDCAttributeVar varAttr = var.getAttributesAll().get(pidcAttr.getAttribute().getID());
      PIDCAttributeVar varAttr = (PIDCAttributeVar) pidcAttr;
      if (null != varAttr.getAttributeValue()) {
        WebFlowAttrValues webFlowAttrValVar = new WebFlowAttrValues();
        webFlowAttrValVar.setValueID(varAttr.getAttributeValue().getID());
        webFlowAttrValVar.setAliasName(false);
        webFlowAttrValVar.setValueDesc(varAttr.getAttributeValue().getDescription());
        webFlowAttrValVar.setValueName(varAttr.getValueAliasForAliasDef(aliasDef));
        webFlowAttrValSet.add(webFlowAttrValVar);
      }
      // }
      attr.getWebFlowAttrValues().addAll(webFlowAttrValSet);
    }
    else if (CommonUtils.isNotNull(pidcAttr.getAttributeValue())) {
      WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();

      if (pidcAttr.isHiddenToUser(this.apicUser)) {
        webFlowAttrVal.setValueID(0l);
        webFlowAttrVal.setValueDesc(pidcAttr.getAttributeValue().getDescription());
        webFlowAttrVal.setValueName("Hidden");
      }
      else {
        webFlowAttrVal = this.adapter.createWebFlowAttrValue(pidcAttr, aliasDef);
      }
      attr.getWebFlowAttrValues().add(webFlowAttrVal);
    }
    else if (!pidcAttr.isVisible()) {
      // Task 232488
      WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
      webFlowAttrVal.setValueID(0l);
      webFlowAttrVal.setAliasName(false);
      webFlowAttrVal.setValueDesc("Not defined in PIDC (Missing dependencies)");
      webFlowAttrVal.setValueName("n.a.");
      attr.getWebFlowAttrValues().add(webFlowAttrVal);
    }
    // Used flag no.
    else if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(pidcAttr.getUsedInfo())) {
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
   */
  private void populateValuesForSubVar(final PIDCVariant pidcVariant, final AliasDefinition aliasDef,
      final WebFlowAttribute attr, final PIDCAttributeVar varAttr, final IPIDCAttribute pidcFinalAttr) {
    // get all sub varaiants
    Map<Long, PIDCSubVariant> subVariantsMap = pidcVariant.getSubVariantsMap();
    // Itreate each sub var
    for (PIDCSubVariant subVar : subVariantsMap.values()) {
      // get all the attrs of the sub var
      Map<Long, PIDCAttributeSubVar> allAttr = subVar.getAttributesAll();
      // Ireate the sub var attributes
      for (IPIDCAttribute subVarAttr : allAttr.values()) {
        if (subVarAttr.isAttrAliasPresent(aliasDef) &&
            varAttr.getAttribute().getAttributeID().equals(subVarAttr.getAttribute().getAttributeID())) {
          populateAttrValue(aliasDef, subVarAttr, attr, pidcFinalAttr);
        }
      }
    }
  }


  /**
   * @param pidcRemAttr pidcRemAttr
   * @param aliasDef aliasDef
   * @param pidcWebFlowData pidcWebFlowData
   * @param allAttributes
   */
  public void populateRemAliasAttrinResp(final Set<IPIDCAttribute> pidcRemAttr, final AliasDefinition aliasDef,
      final PidcWebFlowData pidcWebFlowData, final Map<Long, PIDCAttributeVar> allAttributes) {
    for (IPIDCAttribute pidcAttribute : pidcRemAttr) {
      if ((pidcAttribute != null) && pidcAttribute.isAttrAliasPresent(aliasDef)) {
        WebFlowAttribute webFlowAttr = this.adapter.createWebFlowAttribute(pidcAttribute, aliasDef);
        // Story 221726
        if (null != this.allAlternateAttrMap.get(pidcAttribute.getAttribute().getID())) {
          final IPIDCAttribute pidcAlternateAttr =
              allAttributes.get(this.allAlternateAttrMap.get(pidcAttribute.getAttribute().getID()));

          populateAttrValue(aliasDef, pidcAttribute, webFlowAttr, pidcAlternateAttr);
        }
        else {

          populateAttrValue(aliasDef, pidcAttribute, webFlowAttr, pidcAttribute);
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
   */
  public Set<WebFlowAttribute> createPidcAtrrForPidcVar(final PIDCVariant pidcVariant, final AliasDefinition aliasDef,
      final Map<Long, PIDCAttribute> allPidcAttr, final ConcurrentHashMap<Long, Long> allAlternateAttrMap) {
    Set<WebFlowAttribute> webFlowAttrSet = new HashSet<>();
    for (PIDCAttribute pidcAttr : allPidcAttr.values()) {
      // Do the changes only if the attr alias is present
      if (pidcAttr.isAttrAliasPresent(aliasDef)) {

        WebFlowAttribute attr = this.adapter.createWebFlowAttribute(pidcAttr, aliasDef);
        // Story 221726
        if (null != allAlternateAttrMap.get(pidcAttr.getAttribute().getID())) {
          PIDCAttribute finalAttrPidc = allPidcAttr.get(allAlternateAttrMap.get(pidcAttr.getAttribute().getID()));

          setAttrValue(pidcVariant, aliasDef, allPidcAttr, allAlternateAttrMap, pidcAttr, attr, finalAttrPidc);
        }
        else {
          populateAttrValue(aliasDef, pidcAttr, attr, allPidcAttr.get(pidcAttr.getEffectiveAttrAlias()));
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
   */
  private void setAttrValue(final PIDCVariant pidcVariant, final AliasDefinition aliasDef,
      final Map<Long, PIDCAttribute> allPidcAttr, final ConcurrentHashMap<Long, Long> allAlternateAttrMap,
      final PIDCAttribute pidcAttr, final WebFlowAttribute attr, final PIDCAttribute finalAttrPidc) {
    if (finalAttrPidc.isVariant()) {
      PIDCAttributeVar varAttr = pidcVariant.getAttributes().get(finalAttrPidc.getAttribute().getID());
      populateAttrValue(aliasDef, pidcAttr, attr, varAttr);
    }
    else {
      populateAttrValue(aliasDef, pidcAttr, attr,
          allPidcAttr.get(allAlternateAttrMap.get(pidcAttr.getAttribute().getID())));
    }
  }
}
