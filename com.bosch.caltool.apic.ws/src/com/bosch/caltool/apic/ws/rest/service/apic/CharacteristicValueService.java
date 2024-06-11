/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.attr.CharacteristicValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;

/**
 * Rest service for attribute value
 *
 * @author dmo5cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_CHARACTERISTIC_VALUE)
public class CharacteristicValueService extends AbstractRestService {

  /**
   * get values for given Characteristic
   *
   * @param characteristicId Characteristic id
   * @return Rest response
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getValuesByCharacteristic(
      @QueryParam(value = WsCommonConstants.RWS_CHARACTERISTIC) final Long characteristicId) throws IcdmException {
    CharacteristicValueLoader valueLoader = new CharacteristicValueLoader(getServiceData());

    // fetch CharacteristicValue
    Map<Long, CharacteristicValue> retMap = valueLoader.getValuesByCharacteristic(characteristicId);
    getLogger().info("CharacteristicValueService.getValuesByCharacteristic() completed");

    return Response.ok(retMap).build();
  }


}
