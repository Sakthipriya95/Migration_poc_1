package com.bosch.caltool.icdm.ws.rest.client.apic.cocwp;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for PidcSubVarCocWp
 *
 * @author UKT1COB
 */
public class PidcSubVarCocWpServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public PidcSubVarCocWpServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDCSUBVARCOCWP);
  }


  /**
   * Get PidcSubVarCocWp using its id
   *
   * @param pidcSubVarCocWpId pidcSubVarCocWp id
   * @return pidcSubVarCocWp object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcSubVarCocWp getbyId(final Long pidcSubVarCocWpId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcSubVarCocWpId);
    return get(wsTarget, PidcSubVarCocWp.class);
  }

}
