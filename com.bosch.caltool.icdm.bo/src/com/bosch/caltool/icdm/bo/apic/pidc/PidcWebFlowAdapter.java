/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.WebFlowAttrValues;
import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;

/**
 * @author dmr1cob
 */
public class PidcWebFlowAdapter {


  /**
   * @param pidcAttr pidcAttr
   * @param aliasDef aliasDef
   * @return the webflow attr
   */
  // Method coneverts the pidcAttr to web flow attr
  public WebFlowAttribute createWebFlowAttribute(final IProjectAttribute pidcAttr,
      final WebFlowDataCreator webFlowDataCreator) {
    WebFlowAttribute webFlowAttr = new WebFlowAttribute();
    webFlowAttr.setAttrID(pidcAttr.getAttrId());
    webFlowAttr.setAttrNameEng(getAttrAliasForAliasDef(pidcAttr, webFlowDataCreator));
    webFlowAttr.setAttrDescEng(
        webFlowDataCreator.getPidcVersAttrModel().getAllAttrMap().get(pidcAttr.getAttrId()).getDescriptionEng());
    webFlowAttr.setAttrtype(
        webFlowDataCreator.getPidcVersAttrModel().getAllAttrMap().get(pidcAttr.getAttrId()).getValueType());
    webFlowAttr.setAliasPresent(isAttrAliasPresent(pidcAttr, webFlowDataCreator));
    return webFlowAttr;
  }

  /**
   * @param pidcAttr pidcAttr
   * @param attrValue
   * @param aliasDef aliasDef
   * @return the webflow attr
   */
  // Conevrets Pidc Attr val to Web flow attr val
  public WebFlowAttrValues createWebFlowAttrValue(final IProjectAttribute pidcAttr,
      final WebFlowDataCreator webFlowDataCreator, final AttributeValue attrValue) {
    WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
    webFlowAttrVal.setValueID(attrValue.getId());
    webFlowAttrVal.setAliasName(isValueAliasPresent(webFlowDataCreator, attrValue, pidcAttr));
    webFlowAttrVal.setValueDesc(attrValue.getDescription());
    webFlowAttrVal.setValueName(getValueAliasForAliasDef(pidcAttr, webFlowDataCreator, attrValue));
    return webFlowAttrVal;
  }

  private boolean isValueAliasPresent(final WebFlowDataCreator webFlowDataCreator, final AttributeValue attrValue,
      final IProjectAttribute pidcAttr) {
    boolean flag = true;
    // If alias def is null or attr val is null or value is not text type or Value alias is null.
    if ((webFlowDataCreator.getAliasDef() == null) || (attrValue == null) ||
        (attrValue.getValueType() != AttributeValueType.TEXT.getDisplayText()) ||
        (getEffValueAttrAliasName(pidcAttr, webFlowDataCreator) == null)) {
      flag = false;
    }
    return flag;
  }

  /**
   * @param attr attribute
   * @return the effective alias name for attribute
   */
  private String getEffAttrAliasName(final IProjectAttribute varAttr, final WebFlowDataCreator webFlowDataCreator) {
    for (AliasDetail aliasDetail : webFlowDataCreator.getAliasDetailMap().values()) {
      if ((null != aliasDetail.getAttrId()) && (null != varAttr) && (null != varAttr.getAttrId()) &&
          aliasDetail.getAttrId().equals(varAttr.getAttrId())) {
        return aliasDetail.getAliasName();
      }
    }
    return null;
  }

  /**
   * @param attr attribute
   * @return the effective alias name for attribute
   */
  private String getEffValueAttrAliasName(final IProjectAttribute varAttr,
      final WebFlowDataCreator webFlowDataCreator) {
    for (AliasDetail aliasDetail : webFlowDataCreator.getAliasDetailMap().values()) {
      if ((null != aliasDetail.getValueId()) && (null != varAttr) && (null != varAttr.getValueId()) &&
          aliasDetail.getValueId().equals(varAttr.getValueId())) {
        return aliasDetail.getAliasName();
      }
    }
    return null;
  }


  private String getValueAliasForAliasDef(final IProjectAttribute varAttr, final WebFlowDataCreator webFlowDataCreator,
      final AttributeValue attrValue) {
    String effValAliasName = null;
    // If alias Def is not null and attr Value is not null and type is text then take the alias otherwise take the attr
    // val eng
    if ((webFlowDataCreator.getAliasDef() != null) && (varAttr.getValueId() != null) &&
        (attrValue.getValueType() == AttributeValueType.TEXT.getDisplayText())) {
      effValAliasName = getEffValueAttrAliasName(varAttr, webFlowDataCreator);
    }

    return effValAliasName == null ? getStringValForAttrVal(attrValue) : effValAliasName;
  }

  /**
   * @param attrValue
   * @return
   */
  private String getStringValForAttrVal(final AttributeValue attrValue) {
    String stringValue = "";
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
    return stringValue;
  }

  public String getAttrAliasForAliasDef(final IProjectAttribute varAttr, final WebFlowDataCreator webFlowDataCreator) {
    String effAttrAliasName = null;
    if (CommonUtils.isNotNull(webFlowDataCreator.getAliasDef())) {
      effAttrAliasName = getEffAttrAliasName(varAttr, webFlowDataCreator);
    }
    Attribute attribute = webFlowDataCreator.getPidcVersAttrModel().getAllAttrMap().get(varAttr.getAttrId());
    return effAttrAliasName == null ? attribute.getNameEng() : effAttrAliasName;
  }


  /**
   * @param aliasDefinition aliasDefinition
   * @return true if the attr alias is present
   */
  public boolean isAttrAliasPresent(final IProjectAttribute varAttr, final WebFlowDataCreator webFlowDataCreator) {
    boolean flag = true;
    // if the alias def is null or attribute alias value is null
    if ((webFlowDataCreator.getAliasDef() == null) || (getEffAttrAliasName(varAttr, webFlowDataCreator) == null)) {
      flag = false;
    }
    return flag;
  }
}
