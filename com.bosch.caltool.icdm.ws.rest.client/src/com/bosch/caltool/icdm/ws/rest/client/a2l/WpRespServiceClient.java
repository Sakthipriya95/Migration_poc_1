package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.WpResp;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Service Client for WP Responsibility.
 *
 * @author apj4cob
 */
public class WpRespServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor.
   */
  public WpRespServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_WP_RESPONSIBILITY);
  }

  /**
   * Get all WP Responsibility records in system.
   *
   * @return Map. Key - id, Value - WpResp object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, WpResp> getAll() throws ApicWebServiceException {
    LOGGER.debug("Get all WP Responsibility records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<Long, WpResp>> type = new GenericType<Map<Long, WpResp>>() {};
    Map<Long, WpResp> retMap = get(wsTarget, type);
    LOGGER.debug("WP Responsibility records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Gets the wp responsibility.
   *
   * @param wpRespId the wp resp id
   * @return the wp responsibility
   * @throws ApicWebServiceException the apic web service exception
   */
  public WpResp getWpResponsibility(final Long wpRespId) throws ApicWebServiceException {
    LOGGER.debug("Get WP Responsibility record ");
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, wpRespId);
    return get(wsTarget, WpResp.class);
  }
}
