/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.Unit;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class UnitServiceClient extends AbstractRestServiceClient {

  /**
   * @param moduleBase
   * @param serviceBase
   */
  public UnitServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_UNIT);
  }


  /**
   * Get all attributes, including deleted
   *
   * @return Map of attributes. Key - attribute ID, value - attribute
   * @throws ApicWebServiceException error during service call
   */
  public SortedSet<Unit> getAll() throws ApicWebServiceException {

    LOGGER.debug("Units fetching started");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_ALL_UNITS);


    GenericType<SortedSet<Unit>> type = new GenericType<SortedSet<Unit>>() {};
    SortedSet<Unit> unitSet = get(wsTarget, type);

    LOGGER.debug("Units fetched. Number of items = {}", unitSet.size());

    return unitSet;
  }

  /**
   * @param unitsToBeCreated input Units
   * @return created Units
   * @throws ApicWebServiceException error from service
   */
  public Set<Unit> create(final Set<Unit> unitsToBeCreated) throws ApicWebServiceException {
    GenericType<Set<Unit>> type = new GenericType<Set<Unit>>() {};
    return create(getWsBase(), unitsToBeCreated, type);
  }
}
