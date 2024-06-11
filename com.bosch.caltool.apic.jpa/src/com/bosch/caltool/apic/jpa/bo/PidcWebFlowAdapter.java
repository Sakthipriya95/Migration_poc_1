/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.icdm.model.apic.WebFlowAttrValues;
import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;

/**
 * @author rgo7cob
 */
public class PidcWebFlowAdapter {


  /**
   * @param pidcAttr pidcAttr
   * @param aliasDef aliasDef
   * @return the webflow attr
   */
  // Method coneverts the pidcAttr to web flow attr
  public WebFlowAttribute createWebFlowAttribute(final IPIDCAttribute pidcAttr, final AliasDefinition aliasDef) {
    WebFlowAttribute webFlowAttr = new WebFlowAttribute();
    webFlowAttr.setAttrID(pidcAttr.getAttribute().getAttributeID());
    webFlowAttr.setAttrNameEng(pidcAttr.getAttrAliasForAliasDef(aliasDef));
    webFlowAttr.setAttrDescEng(pidcAttr.getAttribute().getAttributeDescEng());
    webFlowAttr.setAttrtype(pidcAttr.getAttribute().getValueType().getDisplayText());
    webFlowAttr.setAliasPresent(pidcAttr.isAttrAliasPresent(aliasDef));
    return webFlowAttr;
  }

  /**
   * @param pidcAttr pidcAttr
   * @param aliasDef aliasDef
   * @return the webflow attr
   */
  // Conevrets Pidc Attr val to Web flow attr val
  public WebFlowAttrValues createWebFlowAttrValue(final IPIDCAttribute pidcAttr, final AliasDefinition aliasDef) {
    WebFlowAttrValues webFlowAttrVal = new WebFlowAttrValues();
    webFlowAttrVal.setValueID(pidcAttr.getAttributeValue().getID());
    webFlowAttrVal.setAliasName(pidcAttr.isValueAliasPresent(aliasDef));
    webFlowAttrVal.setValueDesc(pidcAttr.getAttributeValue().getDescription());
    webFlowAttrVal.setValueName(pidcAttr.getValueAliasForAliasDef(aliasDef));
    return webFlowAttrVal;
  }


}
