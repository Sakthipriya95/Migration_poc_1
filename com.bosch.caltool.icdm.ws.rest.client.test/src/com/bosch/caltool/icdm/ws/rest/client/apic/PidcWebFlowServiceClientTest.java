/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowElementRespType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowReponseType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmr1cob
 */
public class PidcWebFlowServiceClientTest extends AbstractRestClientTest {

  /**
   * PIDC Version:Audi V6Evo33 (Pilot)
   */
  private static final long TEST_PIDC_VERS_ID = 791409920L;
  /**
   * PIDC:Audi V6Evo33 (Pilot)/Variant:BXAZ
   */
  private static final long TEST_VARIANT_ID = 1203064067L;

  /**
   * Test method for {@link PidcWebFlowServiceClient#getPidcWebFlowData(Long)} with input as pidc version
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getPidcWebFlowDataUsingVersId() throws ApicWebServiceException {
    PidcWebFlowReponseType ret = new PidcWebFlowServiceClient().getPidcWebFlowData(TEST_PIDC_VERS_ID);

    assertNotNull("Response should not be null", ret);
    logResponse(ret);
    testResponse(ret, TEST_PIDC_VERS_ID, "Audi V6Evo33", null);

  }

  /**
   * Test method for {@link PidcWebFlowServiceClient#getPidcWebFlowData(Long)} with input as pidc version
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getPidcWebFlowDataUsingVarId() throws ApicWebServiceException {
    PidcWebFlowReponseType ret = new PidcWebFlowServiceClient().getPidcWebFlowData(TEST_VARIANT_ID);

    assertNotNull("Response should not be null", ret);
    logResponse(ret);
    testResponse(ret, TEST_VARIANT_ID, "Audi V6Evo33 (Pilot)", null);

  }

  /**
   * Test method for {@link PidcWebFlowServiceClient#getPidcWebFlowDataElement(Long)} with input as pidc variant
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getPidcWebFlowDataElementUsingVersId() throws ApicWebServiceException {
    PidcWebFlowElementRespType ret = new PidcWebFlowServiceClient().getPidcWebFlowDataElement(TEST_PIDC_VERS_ID);

    assertNotNull("Response should not be null", ret);
    logResponse(ret);
    testResponse(ret, TEST_PIDC_VERS_ID, "PIDC:Audi V6Evo33", null);
  }

  /**
   * Test method for {@link PidcWebFlowServiceClient#getPidcWebFlowDataElement(Long)} with input as variant
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getPidcWebFlowDataElementUsingVarId() throws ApicWebServiceException {
    PidcWebFlowElementRespType ret = new PidcWebFlowServiceClient().getPidcWebFlowDataElement(TEST_VARIANT_ID);

    assertNotNull("Response should not be null", ret);
    logResponse(ret);
    testResponse(ret, TEST_VARIANT_ID, "PIDC:Audi V6Evo33 (Pilot)/Variant:BXAZ", null);
  }

  private void logResponse(final PidcWebFlowReponseType resp) {
    LOG.info("PidcWebFlowReponseType : {}, Atributes count = {}", resp.getPidcDetailsType(),
        resp.getWebFlowAttr().size());
  }

  private void logResponse(final PidcWebFlowElementRespType resp) {
    LOG.info("PidcWebFlowElementRespType : {}, DetailsType count = {}, Atributes count = {}", resp.getSelectionType(),
        resp.getDetailsTypeSet().size(), resp.getWebflowAttrSet().size());
  }

  private void testResponse(final PidcWebFlowReponseType resp, final Long expElemId, final String expElemName,
      final String expVcdmElemName) {
    assertNotNull("Details type not null", resp.getPidcDetailsType());
    assertEquals("Check id = " + expElemId, expElemId, resp.getPidcDetailsType().getId());
    assertEquals("Check element name = " + expElemName, expElemName, resp.getPidcDetailsType().getName());

    assertEquals("Check vCDM element name = " + expVcdmElemName, expVcdmElemName,
        resp.getPidcDetailsType().getVcdmElementName());

    assertFalse("attributes available", resp.getWebFlowAttr().isEmpty());
  }

  private void testResponse(final PidcWebFlowElementRespType resp, final Long expElemId, final String expElemName,
      final String expVcdmElemName) {
    assertNotNull("selection type not null", resp.getSelectionType());
    assertEquals("Check id = " + expElemId, expElemId, resp.getSelectionType().getId());
    assertEquals("Check element name = " + expElemName, expElemName, resp.getSelectionType().getName());
    assertEquals("Check vCDM element name = " + expVcdmElemName, expVcdmElemName,
        resp.getSelectionType().getVcdmElementName());

    assertFalse("Details type available", resp.getDetailsTypeSet().isEmpty());
    assertFalse("attributes available", resp.getWebflowAttrSet().isEmpty());
  }
}
