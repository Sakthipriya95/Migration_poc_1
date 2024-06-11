/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author ##
 */
public class PidcSubVariantAttributeServiceClientTest extends AbstractRestClientTest {

  private static final long PIDCSUBVARIANTATTR_ID = 771258319;
  private static final long SUB_VARIANT_ID = 771258316;
  private static final long ATTR_ID = 243;
  private static final long INVALID_ID = -100;
  // @Table(name = "TABV_PROJ_SUB_VARIANTS_ATTR")

  /**
   * @throws ApicWebServiceException webservice exception
   */
  @Test
  public void getbyId() throws ApicWebServiceException {
    PidcSubVariantAttributeServiceClient servClient = new PidcSubVariantAttributeServiceClient();
    PidcSubVariantAttribute ret = servClient.getbyId(PIDCSUBVARIANTATTR_ID);
    assertFalse("Response should not be null", (ret == null));
    testOutput(ret);
  }

  /**
   * @throws ApicWebServiceException web service exception
   */
  @Test
  public void getSubVarAttrForSubVar() throws ApicWebServiceException {
    PidcSubVariantAttributeServiceClient servClient = new PidcSubVariantAttributeServiceClient();
    Map<Long, PidcSubVariantAttribute> retMap = servClient.getSubVarAttrForSubVar(SUB_VARIANT_ID);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    PidcSubVariantAttribute ret = retMap.get(ATTR_ID);
    System.out.println(ret);
    testOutput(ret);


    /*
     * for (Long var : retMap.keySet()) { System.out.println(retMap.keySet()); if (var == ATTR_ID) {
     * testOutput(retMap.get(var)); } }
     */

  }

  /**
   * @param subvariant
   */
  private void testOutput(final PidcSubVariantAttribute ret) {
    // TODO Auto-generated method stub
    assertEquals("variant is equal", Long.valueOf(279055), ret.getVariantId());
    assertEquals("subvariant_id is equal", Long.valueOf(771258316), ret.getSubVariantId());
    assertEquals("attr id is equal", Long.valueOf(243), ret.getAttrId());
    assertEquals("CreatedUser is equal", "HEF2FE", ret.getCreatedUser());
    assertNotNull("CreatedDate is not null", ret.getCreatedDate());
    assertEquals("value id is equal", Long.valueOf(4265), ret.getValueId());
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testgetbyIdNegative() throws ApicWebServiceException {
    PidcSubVariantAttributeServiceClient servClient = new PidcSubVariantAttributeServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(" ID '" + INVALID_ID + "' not found");
    servClient.getbyId(INVALID_ID);
    fail("Expected exception not thrown");
  }

  /**
   * @throws ApicWebServiceException webservice error
   */

  @Test
  // Internal server error occured
  public void testgetSubVarAttrForSubVarNegative() throws ApicWebServiceException {
    PidcSubVariantAttributeServiceClient servClient = new PidcSubVariantAttributeServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    // this.thrown.expectMessage("ID '" + INVALID_ID + "' not found");
    servClient.getSubVarAttrForSubVar(INVALID_ID);
    fail("Expected exception not thrown");
  }


}
