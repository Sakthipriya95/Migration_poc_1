/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class CharacteristicServiceClientTest extends AbstractRestClientTest {

  private final static Long CHAR_ID = 789778865L;
  private final static Long INVALID_CHAR_ID = -100L;

  /**
   * Test method for {@link CharacteristicServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    CharacteristicServiceClient charServClient = new CharacteristicServiceClient();
    Characteristic ret = charServClient.getById(CHAR_ID);
    assertFalse("Response should not be null", ret == null);
    testOutput(ret);
  }


  /**
   * Test method for testing with some invalid char_id (negative test case)
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    CharacteristicServiceClient charServClient = new CharacteristicServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Attribute Characteristic with ID '" + INVALID_CHAR_ID + "' not found");
    charServClient.getById(INVALID_CHAR_ID);
    fail("Expected exception not thrown");
  }

  /**
   * @param ret
   */
  private void testOutput(final Characteristic ret) {
    assertEquals("Char_Name_Eng is equal", "FeatFunc", ret.getCharNameEng());
    assertEquals("Char_Name_Ger is equal", null, ret.getCharNameGer());
    assertEquals("Desc_Eng is equal",
        "Feature or Function, usually established through interaction of SW and/or HW (system function or effect chain, e.g. CVO or scavenging); the SW is the driving element",
        ret.getDescEng());
    assertEquals("Created user is equal", "DGS_ICDM", ret.getCreatedUser());
    assertNotNull("Created date is not null", ret.getCreatedDate());
  }

  /**
   * Test method for {@link CharacteristicServiceClient#getAll()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    CharacteristicServiceClient charServClient = new CharacteristicServiceClient();
    Set<Characteristic> charSet = charServClient.getAll();
    assertNotNull("Response should not be null", charSet);
    boolean charAvailable = false;
    for (Characteristic characteristic : charSet) {
      if (characteristic.getId().equals(CHAR_ID)) {
        testOutput(characteristic);
        charAvailable = true;
        break;
      }
    }
    assertTrue("Charactersitics is available", charAvailable);
  }
}
