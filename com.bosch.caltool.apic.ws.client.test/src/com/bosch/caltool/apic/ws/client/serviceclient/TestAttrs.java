/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;
import com.bosch.caltool.apic.ws.client.output.StringAttributesOutput;


/**
 * @author ELM1COB
 */
public class TestAttrs extends AbstractSoapClientTest {

  private final APICWebServiceClient stub = new APICWebServiceClient();

  /**
   * Test GetAllAttributes service
   *
   * @throws Exception service exception
   */
  @Test
  public void testGetAllAttributes() throws Exception {
    Attribute[] allAttributes = this.stub.getAllAttributes();

    assertNotNull(allAttributes);

    LOG.info("Number of attributes = {}", allAttributes.length);
    assertTrue("Valid output", allAttributes.length > 100);

    AbstractStringOutput output = new StringAttributesOutput(allAttributes);
    output.setNoOfRowsToInclude(5);
    showOutput(output);
  }


}
