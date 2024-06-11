/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class MessageServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link MessageServiceClient#getAll()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    MessageServiceClient servClient = new MessageServiceClient();
    Map<String, String> retMap = servClient.getAll();
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    LOG.info("Size : {}", retMap.size());
  }

}
