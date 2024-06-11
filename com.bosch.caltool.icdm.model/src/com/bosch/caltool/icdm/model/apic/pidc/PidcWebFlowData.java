/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;


/**
 * @author rgo7cob
 */
public class PidcWebFlowData {


  /**
   * element id variant or PIDC
   */
  private final Long elementID;

  /**
   * Pidc name
   */
  private final String name;

  /**
   * varaint or Pidc name
   */
  private final String varName;


  /**
   * web flow attr
   */
  private final Set<WebFlowAttribute> webFlowAttr = new TreeSet<>();


  /**
   * vcdm element name
   */
  private String vcdmElementName;

  /**
   * chnageNumber of Pidc or varaint
   */
  private final Long changeNum;


  /**
   * @param elementID elementID
   * @param name name
   * @param varName varName
   * @param changeNum changeNum
   */
  public PidcWebFlowData(final Long elementID, final String name, final String varName, final Long changeNum) {
    super();
    this.elementID = elementID;
    this.name = name;
    this.varName = varName;
    this.changeNum = changeNum;

  }

  /**
   * @return the webFlowAttr
   */
  public Set<WebFlowAttribute> getWebFlowAttr() {
    return this.webFlowAttr;
  }


  /**
   * @return the id
   */
  public Long getElemementID() {
    return this.elementID;
  }


  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return the vcdmElementName
   */
  public String getVcdmElementName() {
    return this.vcdmElementName;
  }

  /**
   * @param vcdmElementName the vcdmElementName to set
   */
  public void setVcdmElementName(final String vcdmElementName) {
    this.vcdmElementName = vcdmElementName;
  }


  /**
   * @return the changeNum
   */
  public Long getChangeNum() {
    return this.changeNum;
  }

  /**
   * @return the varName
   */
  public String getVarName() {
    return this.varName;
  }


}
