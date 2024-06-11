package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2lWpImportProfile;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2lWpImportProfile
 *
 * @author and4cob
 */
public class A2lWpImportProfileServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public A2lWpImportProfileServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_WP_IMPORT_PROFILE);
  }

  /**
   * Get all A2lWpImportProfile records in system
   *
   * @return Map. Key - id, Value - A2lWpImportProfile object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, A2lWpImportProfile> getAll() throws ApicWebServiceException {
    LOGGER.debug("Get all A2lWpImportProfile records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<Long, A2lWpImportProfile>> type = new GenericType<Map<Long, A2lWpImportProfile>>() {};
    Map<Long, A2lWpImportProfile> retMap = get(wsTarget, type);
    LOGGER.debug("A2lWpImportProfile records loaded count = {}", retMap.size());
    return retMap;
  }

}
