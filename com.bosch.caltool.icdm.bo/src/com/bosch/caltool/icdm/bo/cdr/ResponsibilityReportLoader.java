/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.CDRReportData;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.ResponsibiltyRvwData;
import com.bosch.caltool.icdm.model.cdr.ResponsibiltyRvwDataReport;


/**
 * @author rgo7cob
 */
public class ResponsibilityReportLoader extends AbstractSimpleBusinessObject {

  private final Map<String, ResponsibiltyRvwData> reportMap = new HashMap<>();

  private CdrReport reportData;

  /**
   * @param serviceData Service Data
   */
  public ResponsibilityReportLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @return the ResponsibiltyRvwDataReport
   */
  public ResponsibiltyRvwDataReport getReport() {
    ResponsibiltyRvwDataReport ret = new ResponsibiltyRvwDataReport();
    ret.getDataReportSet().addAll(this.reportMap.values());
    return ret;
  }

  /**
   * @param pidcA2lId PIDC A2L ID
   * @param variantId Variant ID
   * @throws IcdmException error while fetching data
   */
  public void createReportOutput(final Long pidcA2lId, final Long variantId) throws IcdmException {

    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId);
    getLogger().info("Generating data responsiblity review details for PIDC A2L ID  - {}", pidcA2l.getId());

    // call the CDR report loader.
    CDRReportLoader repDataLoader = new CDRReportLoader(getServiceData());

    CDRReportData cdrRprtData = new CDRReportData();
    cdrRprtData.setPidcA2lId(pidcA2l.getId());
    cdrRprtData.setMaxResults(1);
    cdrRprtData.setFetchCheckVal(false);
    cdrRprtData.setVarId(variantId);
    this.reportData = repDataLoader.fetchCDRReportData(cdrRprtData);
    getParamResp(pidcA2lId, variantId);
    getLogger().info("Generating data responsiblity review details completed. Responsibility data count = {}",
        this.reportMap.size());
  }


  /**
   * @param reportData
   * @param reportMap
   * @param paramName
   * @param responsibility
   */
  private void addReportObj(final String paramName, final String responsibility) {
    DATA_REVIEW_SCORE score = getReviewScore(paramName);
    if (this.reportMap.get(responsibility) == null) {
      ResponsibiltyRvwData report = new ResponsibiltyRvwData();
      report.setResponsibility(responsibility);
      // if the resp name is null put -
      if (responsibility == null) {
        report.setResponsibility("-");
      }
      // Add params and no of Checked params (score 8 & 9)
      report.setNumOfParams(1);
      if ((score != null) && score.isChecked()) {
        report.setNumOfRvwdParams(1);
      }
      this.reportMap.put(responsibility, report);
    }
    else {
      // take already existing value
      ResponsibiltyRvwData report = this.reportMap.get(responsibility);
      // Add params and no of Checked params (score 8 & 9)
      report.setNumOfParams(report.getNumOfParams() + 1);
      if ((score != null) && score.isChecked()) {
        report.setNumOfRvwdParams(report.getNumOfRvwdParams() + 1);
      }
    }
  }

  private DATA_REVIEW_SCORE getReviewScore(final String paramName) {
    String reviewScore = null;

    ParameterReviewDetails paramReviewDetailsLatest = null;
    List<ParameterReviewDetails> paramRvwDetList = this.reportData.getParamRvwDetMap().get(paramName);
    if ((paramRvwDetList != null) && !paramRvwDetList.isEmpty()) {
      paramReviewDetailsLatest = paramRvwDetList.get(0);
    }
    if (paramReviewDetailsLatest != null) {
      reviewScore = paramReviewDetailsLatest.getReviewScore();
    }

    if ((reviewScore == null) || reviewScore.isEmpty()) {
      return null;
    }
    return DATA_REVIEW_SCORE.getType(reviewScore);
  }

  // 487754 - Changes in external service - Responsibility Report Service
  private void getParamResp(final Long pidcA2lId, final Long variantId) throws IcdmException {
    A2lWpResponsibilityLoader a2lWpResponsibilityLoader = new A2lWpResponsibilityLoader(getServiceData());
    ParameterLoader paramLoader = new ParameterLoader(getServiceData());
    List<WpRespLabelResponse> wpRespList = a2lWpResponsibilityLoader.getWpResp(pidcA2lId, variantId);
    for (WpRespLabelResponse wpRespLabelResponse : wpRespList) {
      addReportObj(paramLoader.getDataObjectByID(wpRespLabelResponse.getParamId()).getName(),
          WpRespType.getType(wpRespLabelResponse.getWpRespModel().getA2lResponsibility().getRespType()).getDispName());
    }
  }
}
