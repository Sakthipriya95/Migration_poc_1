package com.bosch.caltool.icdm.ws.rest.client.bc;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.bc.SdomFc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for SdomFc
 *
 * @author say8cob
 */
public class SdomFcServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public SdomFcServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_BC, WsCommonConstants.RWS_SDOMFC);
  }


  /**
   * Get SdomFc using its id
   *
   * @param objId object's id
   * @return SdomFc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public SdomFc getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, SdomFc.class);
  }

  /**
   * @param bcName
   * @return
   * @throws ApicWebServiceException
   */
  public List<String> getSDOMFcByBCName(final String bcName) throws ApicWebServiceException {

    LOGGER.debug("Get SdomFc records by BC Name");
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_SDOMFCBYBCNAME).queryParam(WsCommonConstants.RWS_QP_BC_NAME, bcName);
    GenericType<List<String>> type = new GenericType<List<String>>() {};
    List<String> sdomFcs = get(wsTarget, type);
    LOGGER.debug("SdomFc records loaded count = {}", sdomFcs.size());
    return sdomFcs;

  }

}
