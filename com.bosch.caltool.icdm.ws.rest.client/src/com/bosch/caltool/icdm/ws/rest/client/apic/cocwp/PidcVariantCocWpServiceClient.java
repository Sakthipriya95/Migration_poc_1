package com.bosch.caltool.icdm.ws.rest.client.apic.cocwp;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for PidcVariantCocWp
 *
 * @author UKT1COB
 */
public class PidcVariantCocWpServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public PidcVariantCocWpServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDCVARIANTCOCWP);
  }


  /**
   * Get PidcVariantCocWp using its id
   *
   * @param pidcVarCocWpId pidcVarCocWp id
   * @return pidcVarCocWp object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVariantCocWp getbyId(final Long pidcVarCocWpId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcVarCocWpId);
    return get(wsTarget, PidcVariantCocWp.class);
  }
}
