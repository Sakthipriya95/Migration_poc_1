/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.CombinedReviewResultExcelExportData;
import com.bosch.caltool.icdm.model.cdr.CombinedRvwExportInputModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 *
 */
public class ReviewResultCombinedExportDataServiceClient extends AbstractRestServiceClient{

  /**
   * 
   */
  public ReviewResultCombinedExportDataServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR,WsCommonConstants.RWS_RESULT_EDITOR_DATA);
  }

  /** 
   * @param exportInputModel contains input data for combined excel export
   * @return  CombinedReviewResultExcelExportData
   * @throws ApicWebServiceException as exception
   */
  public CombinedReviewResultExcelExportData getCombinedReviewAndQnaireExcelExport(CombinedRvwExportInputModel exportInputModel) throws ApicWebServiceException {
    LOGGER.debug("Started fetching combined review result editor data");
    CombinedReviewResultExcelExportData combinedReviewResultExcelExportData = post(getWsBase(), exportInputModel, CombinedReviewResultExcelExportData.class);
    LOGGER.debug("Fetching review combined result editor data completed");
    return combinedReviewResultExcelExportData;
  }
}
