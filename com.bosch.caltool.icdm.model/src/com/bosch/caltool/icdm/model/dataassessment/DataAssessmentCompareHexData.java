/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.dataassessment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;

/**
 * @author hnu1cob
 */
public class DataAssessmentCompareHexData {

  /** Holds folder name where results are stored. Filled only during Data Assesment Report. */
  private String referenceId = "";

  /**
   * The comp hex statistics. Calculated in Client Side
   */
  private CompHexStatistics compareHexStatics;

  /**
   * List of Data Assessment CompareHex Parameter
   */
  private List<DaCompareHexParam> daCompareHexParam = new ArrayList<>();

  /**
   * Total Count of parameters/labels in RB responsibility
   */
  private int daRbRespParamTotalCount;

  /** The is compli check failed. Filled only during Data Assesment Report. */
  private boolean isCompliCheckFailed;

  /** The is QSSD check failed. Filled only during Data Assesment Report. */
  private boolean isQSSDCheckFailed;

  /** The error msg set. Filled only during Data Assesment Report. */
  private Set<String> errorMsgSet = new HashSet<>();

  /**
   * @return the referenceId
   */
  public String getReferenceId() {
    return this.referenceId;
  }

  /**
   * @param referenceId the referenceId to set
   */
  public void setReferenceId(final String referenceId) {
    this.referenceId = referenceId;
  }

  /**
   * @return the compareHexStatics
   */
  public CompHexStatistics getCompareHexStatics() {
    return this.compareHexStatics;
  }

  /**
   * @param compareHexStatics the compareHexStatics to set
   */
  public void setCompareHexStatics(final CompHexStatistics compareHexStatics) {
    this.compareHexStatics = compareHexStatics;
  }

  /**
   * @param daCompareHexParam the daCompareHexParam to set
   */
  public void setDaCompareHexParam(final List<DaCompareHexParam> daCompareHexParam) {
    this.daCompareHexParam = daCompareHexParam;
  }

  /**
   * @return the daCompareHexParam
   */
  public List<DaCompareHexParam> getDaCompareHexParam() {
    return this.daCompareHexParam;
  }

  /**
   * @return the daRbRespParamTotalCount
   */
  public int getDaRbRespParamTotalCount() {
    return this.daRbRespParamTotalCount;
  }


  /**
   * @param daRbRespParamTotalCount the daRbRespParamTotalCount to set
   */
  public void setDaRbRespParamTotalCount(final int daRbRespParamTotalCount) {
    this.daRbRespParamTotalCount = daRbRespParamTotalCount;
  }

  /**
   * @param isCompliCheckFailed the isCompliCheckFailed to set
   */
  public void setCompliCheckFailed(final boolean isCompliCheckFailed) {
    this.isCompliCheckFailed = isCompliCheckFailed;
  }

  /**
   * @return the isCompliCheckFailed
   */
  public boolean isCompliCheckFailed() {
    return this.isCompliCheckFailed;
  }

  /**
   * @return the isQSSDCheckFailed
   */
  public boolean isQSSDCheckFailed() {
    return this.isQSSDCheckFailed;
  }

  /**
   * @param isQSSDCheckFailed the isQSSDCheckFailed to set
   */
  public void setQSSDCheckFailed(final boolean isQSSDCheckFailed) {
    this.isQSSDCheckFailed = isQSSDCheckFailed;
  }

  /**
   * @param errorMsgSet the errorMsgSet to set
   */
  public void setErrorMsgSet(final Set<String> errorMsgSet) {
    this.errorMsgSet = errorMsgSet;
  }

  /**
   * @return the errorMsgSet
   */
  public Set<String> getErrorMsgSet() {
    return this.errorMsgSet;
  }

}
