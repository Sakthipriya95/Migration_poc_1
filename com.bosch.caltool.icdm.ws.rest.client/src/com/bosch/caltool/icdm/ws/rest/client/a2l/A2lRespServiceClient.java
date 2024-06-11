package com.bosch.caltool.icdm.ws.rest.client.a2l;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2lResp;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


// TODO: Auto-generated Javadoc
/**
 * Service Client for A2L Responsibility.
 *
 * @author apj4cob
 */
public class A2lRespServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor.
   */
  public A2lRespServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_RESP);
  }


  /**
   * Get A2L Responsibility object.
   *
   * @param pidcA2lId the pidc A2l id
   * @param wpTypeId the wp type id
   * @param wpRootId the wp root id
   * @return the a2l responsibility
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lResp getA2lResponsibility(final Long pidcA2lId, final Long wpTypeId, final Long wpRootId)
      throws ApicWebServiceException {
    LOGGER.debug("Get A2L Responsibility ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_A2LRESP_BY_PIDCA2L_WP)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId)
        .queryParam(WsCommonConstants.RWS_QP_WP_TYPE_ID, wpTypeId)
        .queryParam(WsCommonConstants.RWS_QP_WP_ROOT_ID, wpRootId);
    A2lResp a2lResp = get(wsTarget, A2lResp.class);
    LOGGER.debug("A2L Responsibility record = {}", a2lResp.getId());
    return a2lResp;
  }
}
