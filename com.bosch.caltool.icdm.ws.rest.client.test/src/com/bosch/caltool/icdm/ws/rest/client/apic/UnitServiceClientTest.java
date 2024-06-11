/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.Unit;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class UnitServiceClientTest extends AbstractRestClientTest {

  private final static String UNIT_NAME = "(km/h)/rpm";

  /**
   * Test method for {@link UnitServiceClient#getAll()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    UnitServiceClient servClient = new UnitServiceClient();
    SortedSet<Unit> unitSet = servClient.getAll();
    assertNotNull("Response should not be null", unitSet);
    LOG.info("Size : {}", unitSet.size());
    boolean unitAvailable = false;
    for (Unit unit : unitSet) {
      if (unit.getUnitName().equals(UNIT_NAME)) {
        LOG.debug("Validated");
        unitAvailable = true;
        break;
      }
    }
    assertTrue("Unit is available", unitAvailable);
  }

  /**
   * {@link UnitServiceClient#create(Set)}
   *
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testCreate() throws ApicWebServiceException {
    // Data to create
    Unit unitToCreate = new Unit();
    unitToCreate.setUnitName("Junit_Unit_" + getRunId());
    Set<Unit> unitsToCreate = new HashSet<>();
    unitsToCreate.add(unitToCreate);
    Set<Unit> createdUnits = new UnitServiceClient().create(unitsToCreate);
    for (Unit createdUnit : createdUnits) {
      assertNotNull("Created unit is not null", createdUnit);
      assertEquals("Unit created should be equal to input", "Junit_Unit_" + getRunId(), createdUnit.getUnitName());
      assertEquals("Version should be 1", Long.valueOf(1), createdUnit.getVersion());
    }
  }
}
