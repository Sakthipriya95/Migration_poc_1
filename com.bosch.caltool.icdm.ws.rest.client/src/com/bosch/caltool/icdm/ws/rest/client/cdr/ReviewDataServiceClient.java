/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewDataInputModel;
import com.bosch.caltool.icdm.model.cdr.ReviewDataParamResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class ReviewDataServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public ReviewDataServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CONTEXT_REVIEW_DATA);
  }


  /**
   * @param ReviewDataInputModel dataReviewInputModel
   * @return ReviewDataParamResponse
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public ReviewDataParamResponse getReviewData(final ReviewDataInputModel reviewDataInputModel)
      throws ApicWebServiceException {
    return post(getWsBase(), reviewDataInputModel, ReviewDataParamResponse.class);
    
  }
    
    
    
    
    
    
    
    
    
}
