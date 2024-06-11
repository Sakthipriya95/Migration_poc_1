/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.cdfx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;

/**
 * @author pdh2cob
 */
public class CdfxExportInput {

  private Long pidcA2lId;

  /**
   * Resp type
   */
  private String scope;

  /**
   * List to store WPRespModels selected in the CDFx Export Input Wizard Page
   */
  private final List<WpRespModel> wpRespModelList = new ArrayList<>();

  private boolean readinessFlag;

  private String exportFileName;

  private boolean oneFilePerWpFlag;

  private Set<PidcVariant> variantsList = new HashSet<>();

  /**
   * @return the scope
   */
  public String getScope() {
    return this.scope;
  }


  /**
   * @param scope the scope to set
   */
  public void setScope(final String scope) {
    this.scope = scope;
  }


  /**
   * @return the readinessFlag
   */
  public boolean isReadinessFlag() {
    return this.readinessFlag;
  }


  /**
   * @param readinessFlag the readinessFlag to set
   */
  public void setReadinessFlag(final boolean readinessFlag) {
    this.readinessFlag = readinessFlag;
  }


  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }


  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }


  /**
   * @return the wpRespModelList
   */
  public List<WpRespModel> getWpRespModelList() {
    return this.wpRespModelList;
  }


  /**
   * @return the exportFileName
   */
  public String getExportFileName() {
    return this.exportFileName;
  }


  /**
   * @param exportFileName the exportFileName to set
   */
  public void setExportFileName(final String exportFileName) {
    this.exportFileName = exportFileName;
  }


  /**
   * @return the oneFilePerWpFlag
   */
  public boolean isOneFilePerWpFlag() {
    return this.oneFilePerWpFlag;
  }


  /**
   * @param oneFilePerWpFlag the oneFilePerWpFlag to set
   */
  public void setOneFilePerWpFlag(final boolean oneFilePerWpFlag) {
    this.oneFilePerWpFlag = oneFilePerWpFlag;
  }


  /**
   * @return the variantsList
   */
  public Set<PidcVariant> getVariantsList() {
    return this.variantsList;
  }


  /**
   * @param variantsList the variantsList to set
   */
  public void setVariantsList(final Set<PidcVariant> variantsList) {
    this.variantsList = variantsList;
  }
}
