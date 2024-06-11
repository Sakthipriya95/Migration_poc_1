/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author hnu1cob
 */
@XmlRootElement
public class CDRReportModel {

  private final Set<CDRReportParameter> cdrReportParams = new HashSet<>();

  private CDRParameterMetrics cdrParameterMetrics;

  private String versionName;

  private String a2lName;

  private String variantName;

  private String pidcName;


  /**
   * @return the pidcName
   */
  public String getPidcName() {
    return this.pidcName;
  }


  /**
   * @param pidcName the pidcName to set
   */
  public void setPidcName(final String pidcName) {
    this.pidcName = pidcName;
  }

  /**
   * @return the cdrReportParams
   */
  public Set<CDRReportParameter> getCdrReportParams() {
    return this.cdrReportParams;
  }

  /**
   * @return the versionName
   */
  public String getVersionName() {
    return this.versionName;
  }

  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }

  /**
   * @return the a2lName
   */
  public String getA2lName() {
    return this.a2lName;
  }

  /**
   * @param a2lName the a2lName to set
   */
  public void setA2lName(final String a2lName) {
    this.a2lName = a2lName;
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
   * @return the cdrParameterMetrics
   */
  public CDRParameterMetrics getCdrParameterMetrics() {
    return cdrParameterMetrics;
  }


  /**
   * @param cdrParameterMetrics the cdrParameterMetrics to set
   */
  public void setCdrParameterMetrics(CDRParameterMetrics cdrParameterMetrics) {
    this.cdrParameterMetrics = cdrParameterMetrics;
  }

}
