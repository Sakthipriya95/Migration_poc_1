/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.WebflowElement;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class WebflowElementServiceClientTest extends AbstractRestClientTest {

  private final static Long WEBFLOW_ID = 1447001981L;
  private final static Long INVALID_WEBFLOW_ID = -100L;

  /**
   * Test method for {@link WebflowElementServiceClient#get(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetLong() throws ApicWebServiceException {
    WebflowElementServiceClient webflowServClient = new WebflowElementServiceClient();
    WebflowElement webFlowElement = webflowServClient.get(WEBFLOW_ID);
    assertNotNull("Response should not be null", webFlowElement);
    testOutput(webFlowElement);
  }


  /**
   * Testing with some invalid_webflow_id (Negative Test Case)
   *
   * @throws ApicWebServiceException 'TWebFlowElement with ID not found'
   */
  @Test
  public void testGetLongNegative() throws ApicWebServiceException {
    WebflowElementServiceClient webflowServClient = new WebflowElementServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("TWebFlowElement with ID '" + INVALID_WEBFLOW_ID + "' not found");
    webflowServClient.get(INVALID_WEBFLOW_ID);
    fail("Expected exception not thrown");
  }

  /**
   * @param webFlowElement
   */
  private void testOutput(final WebflowElement webFlowElement) {
    assertEquals("Element_Id is equal", Long.valueOf(1447002177), webFlowElement.getElementId());
    assertEquals("Variant_Id is equal", Long.valueOf(1433839979L), webFlowElement.getVariantId());
    assertNotNull("Created Date is not null", webFlowElement.getCreatedDate());
  }

  /**
   * Test method for {@link WebflowElementServiceClient#create(List)},
   * {@link WebflowElementServiceClient#update(WebflowElement)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdateWebFlowElement() throws ApicWebServiceException {
    WebflowElementServiceClient servClient = new WebflowElementServiceClient();

    WebflowElement webflowElement1 = new WebflowElement();
    webflowElement1.setElementId(1447002877L);
    webflowElement1.setVariantId(870561785L);
    webflowElement1.setIsDeleted(false);

    WebflowElement webflowElement2 = new WebflowElement();
    webflowElement2.setElementId(1458432477L);
    webflowElement2.setVariantId(1027853967L);
    webflowElement2.setIsDeleted(false);

    List<WebflowElement> wEList = new ArrayList<>();
    wEList.add(webflowElement1);
    wEList.add(webflowElement2);

    // invoke create method
    List<WebflowElement> createdWEList = servClient.create(wEList);
    LOG.info("size of the created webflow element list : {}", wEList.size());

    // validate create
    assertFalse("Created set should not be empty", createdWEList.isEmpty());

    // invoke update
    assertEquals("Deleted flag of webflowElement1 before updation", false, webflowElement1.getIsDeleted());
    createdWEList.get(0).setIsDeleted(true);
    createdWEList.get(1).setIsDeleted(true);
    WebflowElement updatedWebflowElement1 = servClient.update(createdWEList.get(0));

    // validate update
    assertNotNull("Updated object is not null", updatedWebflowElement1);
    assertEquals("Deleted flag of webflowElement1 after updation", true, updatedWebflowElement1.getIsDeleted());
  }

}
