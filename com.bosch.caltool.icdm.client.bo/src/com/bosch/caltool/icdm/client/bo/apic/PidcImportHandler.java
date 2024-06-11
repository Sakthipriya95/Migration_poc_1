/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.pidc.PidcImportParser;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportCompareData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportExcelData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcImportServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.ProjectAttributesUpdationServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PidcImportHandler {

  /**
   * Import pidc excel.
   *
   * @param selPidcVersion the sel pidc version
   * @param excelFilePath the excel file path
   * @return the pidc import compare data
   * @throws ApicWebServiceException the apic web service exception
   * @throws InvalidInputException the invalid input exception
   */
  public PidcImportCompareData importPidcExcel(final PidcVersion selPidcVersion, final String excelFilePath)
      throws ApicWebServiceException, InvalidInputException {
    PidcImportParser pidcImportParser = new PidcImportParser(selPidcVersion, excelFilePath);
    PidcImportExcelData pidcImportExcelData = pidcImportParser.parseExcelAndFetchAttr();
    PidcImportServiceClient importServiceClient = new PidcImportServiceClient();
    return importServiceClient.validateExcelData(pidcImportExcelData);
  }

  /**
   * Update pidc attr.
   *
   * @param updationModel the updation model
   * @return true, if successful
   */
  public boolean updatePidcAttr(final ProjectAttributesUpdationModel updationModel) {
    ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();
    try {
      upClient.updatePidcAttrs(updationModel);
      return true;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }
}
