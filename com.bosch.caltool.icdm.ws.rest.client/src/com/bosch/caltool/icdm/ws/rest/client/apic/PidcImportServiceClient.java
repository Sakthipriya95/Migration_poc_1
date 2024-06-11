/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportCompareData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportExcelData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PidcImportServiceClient extends AbstractRestServiceClient {

  /**
   * @param moduleBase
   * @param serviceBase
   */
  public PidcImportServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_IMPORT);
  }

  /**
   * @param importServiceData input
   * @return validation result with additional attribute details
   * @throws ApicWebServiceException error in service call
   */
  public PidcImportCompareData validateExcelData(final PidcImportExcelData pidcImportExcelData)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_COMPARE_EXCEL_PIDC);

    PidcImportCompareData ret = post(wsTarget, pidcImportExcelData, PidcImportCompareData.class);

    LOGGER.debug("PIDC Import validation result = {} ", ret.toString());

    return ret;
  }

}
