/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;

import java.util.Set;
import java.util.TreeSet;


/**
 * @author rgo7cob
 */
public class WebFlowAttribute implements Comparable<WebFlowAttribute> {

  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;
  /**
   * attribute id
   */
  private Long attrID;

  /**
   * atribute english name
   */
  private String attrNameEng;


  /**
   * attribute desc english
   */
  private String attrDescEng;

  /**
   * attribute type
   */
  private String attrtype;

  /**
   * is alias name available
   */
  private boolean aliasPresent;

  /**
   * web flow attr Value
   */
  private final Set<WebFlowAttrValues> webFlowAttrValues = new TreeSet<>();


  /**
  
   */
  public WebFlowAttribute() {
    super();

  }


  /**
   * @return the webFlowAttrValues
   */
  public Set<WebFlowAttrValues> getWebFlowAttrValues() {
    return this.webFlowAttrValues;
  }

  /**
   * @return the attrID
   */
  public Long getAttrID() {
    return this.attrID;
  }


  /**
   * @return the attrDescEng
   */
  public String getAttrDescEng() {
    return this.attrDescEng;
  }


  /**
   * @return the attrtype
   */
  public String getAttrtype() {
    return this.attrtype;
  }

  /**
   * @return the attrNameEng
   */
  public String getAttrNameEng() {
    return this.attrNameEng;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WebFlowAttribute obj) {
    return getAttrNameEng().compareTo(obj.getAttrNameEng());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    WebFlowAttribute webFlowAttr = (WebFlowAttribute) obj;
    if (getAttrNameEng() == null) {
      return null == webFlowAttr.getAttrNameEng();
    }
    return getAttrNameEng().equals(webFlowAttr.getAttrNameEng());
  }


  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getAttrNameEng() == null) ? 0 : getAttrNameEng().hashCode());
    return result;
  }

  /**
   * @return the aliasPresent
   */
  public boolean isAliasPresent() {
    return this.aliasPresent;
  }


  /**
   * @param aliasPresent the aliasPresent to set
   */
  public void setAliasPresent(final boolean aliasPresent) {
    this.aliasPresent = aliasPresent;
  }


  /**
   * @param attrID the attrID to set
   */
  public void setAttrID(final Long attrID) {
    this.attrID = attrID;
  }


  /**
   * @param attrNameEng the attrNameEng to set
   */
  public void setAttrNameEng(final String attrNameEng) {
    this.attrNameEng = attrNameEng;
  }


  /**
   * @param attrDescEng the attrDescEng to set
   */
  public void setAttrDescEng(final String attrDescEng) {
    this.attrDescEng = attrDescEng;
  }


  /**
   * @param attrtype the attrtype to set
   */
  public void setAttrtype(final String attrtype) {
    this.attrtype = attrtype;
  }

}
