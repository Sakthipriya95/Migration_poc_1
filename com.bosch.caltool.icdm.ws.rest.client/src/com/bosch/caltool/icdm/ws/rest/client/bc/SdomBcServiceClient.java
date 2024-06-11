package com.bosch.caltool.icdm.ws.rest.client.bc;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.bc.SdomBc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for SdomBc
 *
 * @author say8cob
 */
public class SdomBcServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public SdomBcServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_BC, WsCommonConstants.RWS_SDOMBC);
  }

  /**
   * Get all distinct SdomBc records in system
   *
   * @return Map. Key - id, Value - SdomBc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Set<SdomBc> getAllDistinctBcName() throws ApicWebServiceException {
    LOGGER.debug("Get all SdomBc records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_DISTINCT);
    GenericType<Set<SdomBc>> type = new GenericType<Set<SdomBc>>() {};
    Set<SdomBc> retMap = get(wsTarget, type);
    LOGGER.debug("SdomBc records loaded count = {}", retMap.size());
    return retMap;
  }


}
