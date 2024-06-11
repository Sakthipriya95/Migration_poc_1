/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

// import static org.junit.Assert.*;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationDetails;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class PidcCreationServiceClientTest extends AbstractRestClientTest {

  private final static int ATTR_LEVEL = -1;
  private final static String AD_NAME = "DS-JLR";

  /**
   * Test method for {@link PidcCreationServiceClient#getPidcCreationDetails(int)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcCreationDetails() throws ApicWebServiceException {
    PidcCreationServiceClient servClient = new PidcCreationServiceClient();
    PidcCreationDetails pidcDetails = servClient.getPidcCreationDetails(ATTR_LEVEL);
    assertFalse("Response should not be null", pidcDetails == null);
    Map<String, AliasDef> retMap = pidcDetails.getAliasDefMap();
    // System.out.println(retMap.keySet());
    AliasDef aldef = retMap.get(AD_NAME);
    testOutput(aldef);
  }

  /**
   * @param aldef
   */
  private void testOutput(final AliasDef aldef) {
    assertEquals("AD_ID is equal", Long.valueOf(780309065), aldef.getId());
    assertEquals("Created user is equal", "HEF2FE", aldef.getCreatedUser());
    assertNotNull("Created date is not null", aldef.getCreatedDate());
  }

}
