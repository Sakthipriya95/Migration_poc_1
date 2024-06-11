package com.bosch.caltool.icdm.ws.rest.client.bc;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.bc.SdomFc2bc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for SdomFc2bc
 *
 * @author say8cob
 */
public class SdomFc2bcServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public SdomFc2bcServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_BC, WsCommonConstants.RWS_SDOMFC2BC);
  }


  /**
   * Get SdomFc2bc using its id
   *
   * @param objId object's id
   * @return SdomFc2bc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public SdomFc2bc getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, SdomFc2bc.class);
  }


}
