/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

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

  private final ReportModel reportModel;

  private final String result;

  /**
   * @param paramName paramName
   * @param calData value against rule is checked
   * @param result result(ok,high, low)
   * @param reportValueModel check ssd result model
   */
  public CheckSSDResultParam(final String paramName, final CalData calData, final String result,
      final ReportModel reportValueModel) {
    this.paramName = paramName;
    this.calData = calData;
    this.result = result;
    this.reportModel = reportValueModel;
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
  public ReportModel getReportModel() {
    return this.reportModel;
  }
}
