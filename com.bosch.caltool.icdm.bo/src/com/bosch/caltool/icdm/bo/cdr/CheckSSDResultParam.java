/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.checkssd.reports.reportmodel.ReportModel;


/**
 * Stores the result of parameter from CheckSSD
 *
 * @author adn1cob
 */
public class CheckSSDResultParam {

  private final String paramName;

  private final CalData calData;

  private final ReportModel compliReportModel;

  private final String result;

  private final ReportModel qssdReportModel;

  /**
   * @param paramName paramName
   * @param calData value against rule is checked
   * @param result result(ok,high, low)
   * @param compliReportValueModel check ssd result model
   * @param qssdReportModel
   */
  public CheckSSDResultParam(final String paramName, final CalData calData, final String result,
      final ReportModel compliReportValueModel, final ReportModel qssdReportModel) {
    this.paramName = paramName;
    this.calData = calData;
    this.result = result;
    this.compliReportModel = compliReportValueModel;
    this.qssdReportModel = qssdReportModel;
  }

  /**
   * @return checked value
   */
  public CalData getCheckedValue() {
    return this.calData;
  }

  /**
   * @return Resukt
   */
  public String getResult() {
    return this.result;
  }

  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }

  /**
   * @return Unit
   */
  public String getUnit() {
    if (this.calData != null) {
      return this.calData.getCalDataPhy().getUnit();
    }
    return "";
  }


  /**
   * @return the reportValueModel
   */
  public ReportModel getCompliReportModel() {
    return this.compliReportModel;
  }


  /**
   * @return the qssdReportModel
   */
  public ReportModel getQssdReportModel() {
    return this.qssdReportModel;
  }

}
