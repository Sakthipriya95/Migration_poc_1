/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmo5cob
 */
public class CharacteristicValueServiceClient extends AbstractRestServiceClient {


  /**
   * constructor
   */
  public CharacteristicValueServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_CHARACTERISTIC_VALUE);
  }

  /**
   * get all values for the given Characteristic
   *
   * @param characteristicId characteristicId
   * @return Map. Key - Characteristic Id; value - CharacteristicValue
   * @throws ApicWebServiceException error while retrieving data
   */
  public Map<Long, CharacteristicValue> getValuesByCharacteristic(final Long characteristicId)
      throws ApicWebServiceException {
    LOGGER.debug("Get all CharacteristicValue for characteristicId {}", characteristicId);
    Map<Long, CharacteristicValue> retMap = new HashMap<>();
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_CHARACTERISTIC, characteristicId);
    GenericType<Map<Long, CharacteristicValue>> type = new GenericType<Map<Long, CharacteristicValue>>() {};
    retMap.putAll(get(wsTarget, type));

    LOGGER.debug("CharacteristicValues loaded = {}", retMap.values().size());

    return retMap;
  }


}
