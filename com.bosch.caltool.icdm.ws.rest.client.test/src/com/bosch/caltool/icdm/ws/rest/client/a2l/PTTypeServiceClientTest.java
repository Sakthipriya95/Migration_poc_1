/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.PTType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class PTTypeServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link PTTypeServiceClient#getAllPTtypes()}.
   * 
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAllPTtypes() throws ApicWebServiceException {
    PTTypeServiceClient PTtype = new PTTypeServiceClient();
    Set<PTType> retTypes = PTtype.getAllPTtypes();
    assertFalse("Response should not be null or empty", ((retTypes == null) || retTypes.isEmpty()));
    LOG.info("Size : {}", retTypes.size());
  }
}
