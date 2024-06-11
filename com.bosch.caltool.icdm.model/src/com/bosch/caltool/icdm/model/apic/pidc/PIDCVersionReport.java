/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Class used to define PIDC for JSON output
 *
 * @author jvi6cob
 */
public class PIDCVersionReport {

  private String projectName;
  private String projectDescription;
  private Long pidcID;
  private String pidcVersion;
  private Long pidcVersionID;

  private Map<String, String> pidcAttrMap = new TreeMap<>();
  private List<VariantReport> variants;

  /**
   * @return the pidcAttrMap
   */
  public Map<String, String> getPidcAttrMap() {
    return this.pidcAttrMap;
  }


  /**
   * @param pidcAttrMap the pidcAttrMap to set
   */
  public void setPidcAttrMap(final Map<String, String> pidcAttrMap) {
    this.pidcAttrMap = pidcAttrMap;
  }

  /**
   * @return the variants
   */
  public List<VariantReport> getVariants() {
    return this.variants;
  }


  /**
   * @param variants the variants to set
   */
  public void setVariants(final List<VariantReport> variants) {
    if (variants != null) {
      this.variants = new ArrayList<>(variants);
    }
  }


  /**
   * @return the projectName
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
   * @return the projectDescription
   */
  public String getProjectDescription() {
    return this.projectDescription;
  }


  /**
   * @param projectDescription the projectDescription to set
   */
  public void setProjectDescription(final String projectDescription) {
    this.projectDescription = projectDescription;
  }


  /**
   * @return the pidcVersion
   */
  public String getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final String pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @return the pidcID
   */
  public Long getPidcID() {
    return this.pidcID;
  }


  /**
   * @param pidcID the pidcID to set
   */
  public void setPidcID(final Long pidcID) {
    this.pidcID = pidcID;
  }


  /**
   * @return the pidcVersionID
   */
  public Long getPidcVersionID() {
    return this.pidcVersionID;
  }


  /**
   * @param pidcVersionID the pidcVersionID to set
   */
  public void setPidcVersionID(final Long pidcVersionID) {
    this.pidcVersionID = pidcVersionID;
  }


}
