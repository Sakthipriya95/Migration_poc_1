/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.compli;


import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TCompliRvwA2l;
import com.bosch.caltool.icdm.database.entity.cdr.TCompliRvwHex;
import com.bosch.caltool.icdm.model.cdr.compli.CompliReviewResultHex;
import com.bosch.caltool.icdm.model.cdr.compli.CompliRvwHexParam;


/**
 * @author dmo5cob
 */
public class CompliReviewResultHexCommand extends AbstractCommand<CompliReviewResultHex, CompliReviewResultHexLoader> {


  private Map<String, CompliRvwHexParam> compliRvwHexParamMap;

  /**
   * Constructor to create an system access to the user
   *
   * @param serviceData Service Data
   * @param inputData system access right to create
   * @throws IcdmException any error
   */
  public CompliReviewResultHexCommand(final ServiceData serviceData, final CompliReviewResultHex inputData)
      throws IcdmException {
    super(serviceData, inputData, new CompliReviewResultHexLoader(serviceData), COMMAND_MODE.CREATE);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    TCompliRvwHex entity = new TCompliRvwHex();
    entity.setCompliNoRule(getInputData().getCompliNoRule());
    entity.setCssdFail(getInputData().getCssdFail());
    entity.setResultOk(getInputData().getResultOk());
    entity.setSsd2rvFail(getInputData().getSsd2rvFail());
    entity.setQssdFail(null == getInputData().getQssdFail() ? 0L : getInputData().getQssdFail());
    entity.setQssdOk(null == getInputData().getQssdOk() ? 0L : getInputData().getQssdOk());
    entity.setQssdNoRule(null == getInputData().getQssdNoRule() ? 0L : getInputData().getQssdNoRule());
    entity.setHexFileName(getInputData().getHexFileName());
    entity.setIndexNum(getInputData().getIndexNum());
    entity.setVcdmDstId(getInputData().getVcdmDstId());
    CompliReviewResultLoader loader = new CompliReviewResultLoader(getServiceData());
    TCompliRvwA2l entityObject = loader.getEntityObject(getInputData().getResultId());
    entity.setTCompliRvwA2l(entityObject);
    entity.setVariantVersionId(getInputData().getVariantId());
    entity.setSsdFileName(getInputData().getSsdFileName());
    entity.setCheckSsdReportName(getInputData().getCheckSsdReportName());
    entity.setReleaseId(getInputData().getReleaseId());
    entity.setHexChecksum(getInputData().getHexChecksum());
    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    if (this.compliRvwHexParamMap != null) {
      for (CompliRvwHexParam compliRvwHexParam : this.compliRvwHexParamMap.values()) {
        compliRvwHexParam.setCompliRvwHexId(entity.getCompliRvwHexId());
        if ((compliRvwHexParam.getParamId() != null) && (compliRvwHexParam.getParamId() > 0)) {
          CompliRvwHexParamCommand compliRvwHexParamCommand =
              new CompliRvwHexParamCommand(getServiceData(), compliRvwHexParam);
          executeChildCommand(compliRvwHexParamCommand);
        }
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    // NA
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // NA
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // NA

  }

  /**
   * @param compliRvwHexParamMap the compliRvwHexParamMap to set
   */
  public void setCompliRvwHexParamMap(final Map<String, CompliRvwHexParam> compliRvwHexParamMap) {
    this.compliRvwHexParamMap = compliRvwHexParamMap;
  }

}
