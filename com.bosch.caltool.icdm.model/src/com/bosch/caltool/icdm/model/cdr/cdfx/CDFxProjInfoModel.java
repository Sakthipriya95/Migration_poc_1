/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.cdfx;

import java.util.HashMap;
import java.util.Map;

/**
 * @author and4cob
 */
public class CDFxProjInfoModel {

  private String projectName;

  private String variantName;

  /**
   * softwareName = <SDOM_PVER_NAME> - <SDOM_PVER_VARIANT>
   */
  private String softwareName;
  /**
   * key-attribute name, val - attribute value(s) concatenated by ','
   */
  private final Map<String, String> attrValueMap = new HashMap<>();

  /**
   * @return the projectname
   */
  public String getProjectName() {
    return this.projectName;
  }

  /**
   * @param projectName the projectName to set
   */
  public void setProjectName(final String projectName) {
    this.projectName = projectName;
  }

  /**
   * @return the variantName
   */
  public String getVariantName() {
    return this.variantName;
  }

  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }

  /**
   * @return the softwarename
   */
  public String getSoftwareName() {
    return this.softwareName;
  }

  /**
   * @param softwareName the softwareName to set
   */
  public void setSoftwareName(final String softwareName) {
    this.softwareName = softwareName;
  }

  /**
   * @return the attributeWithValuesMap
   */
  public Map<String, String> getAttrValueMap() {
    return this.attrValueMap;
  }

}
