/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionStatisticsReport;


/**
 * @author rgo7cob
 */
public class PidcVersionStatRespCreator {


  /**
   * project Version id
   */
  private final PIDCVersion pidcVersion;
  /**
   * statisticsResponse
   */
  private PidcVersionStatisticsReport statisticResp;


  /**
   * @param pidcVersion pidcVersion
   */
  public PidcVersionStatRespCreator(final PIDCVersion pidcVersion) {
    super();
    this.pidcVersion = pidcVersion;
  }


  /**
   * @param statisticReport statisticReport
   * @param projectStatistics projectStatistics
   */
  private void peopulateResponse(final PidcVersionStatisticsReport statisticReport,
      final PIDCVersionStatistics projectStatistics) {
    statisticReport.setTotalAttributes(projectStatistics.getAllAttributesCount());
    statisticReport.setLastModifiedDate(projectStatistics.getLastModifiedDate().getTime());
    statisticReport.setNotDefinedAttribute(projectStatistics.getNotDefinedAttributesCount());
    statisticReport.setNotUsedAttributes(projectStatistics.getNotUsedAttributesCount());
    statisticReport.setProjectUseCases(projectStatistics.getProjectUseCaseCount());
    statisticReport.setUsedAttributes(projectStatistics.getUsedAttributesCount());
    statisticReport.setMandateOrProjectUcAttrUsedCount(projectStatistics.getUsedFlagSetImpAttrCount());
    statisticReport.setMandateOrProjectUcAttr(projectStatistics.getImportantAttrCount());
    statisticReport.setFocusMatrixApplicabeAttributes(projectStatistics.getFocusMatrixApplicableAttrCount());
    statisticReport.setFocusMatrixRatedAttributes(projectStatistics.getFocusMatrixRatedAttrCount());
    statisticReport.setNewAttributes(projectStatistics.getNewAttributesCount());
    statisticReport.setFocusMatrixUnratedAttributes(
        projectStatistics.getFocusMatrixApplicableAttrCount() - projectStatistics.getFocusMatrixRatedAttrCount());

    // Task 234241
    statisticReport.setTotalMandatoryAttrCountOnly(projectStatistics.getTotalMandatoryAttrCountOnly());
    statisticReport.setTotalMandtryAttrUsedCountOnly(projectStatistics.getTotalMandtryAttrUsedCountOnly());
  }

  /**
   * create the response content
   *
   * @return the repsonse
   * @throws DataException DataException
   */
  public PidcVersionStatisticsReport createStatResponse() throws DataException {
    PIDCVersionStatistics projectStatistics = this.pidcVersion.getProjectStatistics();
    this.statisticResp = new PidcVersionStatisticsReport();
    peopulateResponse(this.statisticResp, projectStatistics);
    return this.statisticResp;
  }
}
