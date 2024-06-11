package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Characteristic
 *
 * @author dmo5cob
 */
public class CharacteristicServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public CharacteristicServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_CHARACTERISTIC);
  }

  /**
   * Get Characteristic using its id
   *
   * @param objId object's id
   * @return Characteristic object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Characteristic getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, Characteristic.class);
  }

  /**
   * Fetch all Characteristics
   *
   * @return data
   * @throws ApicWebServiceException error during service call
   */
  public Set<Characteristic> getAll() throws ApicWebServiceException {

    LOGGER.debug("Loading all Characteristic definitions");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Set<Characteristic>> type = new GenericType<Set<Characteristic>>() {};

    Set<Characteristic> response = get(wsTarget, type);

    LOGGER.debug("Characteristic definitions loaded. No. of definitions : {}", response.size());

    return response;

  }
}
