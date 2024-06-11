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

import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author AND4COB
 */
public class PidcVersionAttributeServiceClientTest extends AbstractRestClientTest {


  /**
   *
   */
  private static final String RESPONSE_SHOULD_NOT_BE_NULL = "Response should not be null";
  /**
   *
   */
  private static final String EXPECTED_EXCEPTION_NOT_THROWN = "Expected exception not thrown";
  /**
   *
   */
  private static final String NOT_FOUND = "' not found";
  private static final Long PIDC_VERS_ID = 773513515L;//
  private static final Long PRJ_ATTR_ID = 768267415L;
  private static final Long INVALID_ID = -100L;
  private static final Long ATTR_ID = 249297L;
  private static final Long VARIANT_ID = 783297769L;


  /**
   * Test method for {@link PidcVersionAttributeServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    PidcVersionAttributeServiceClient servClient = new PidcVersionAttributeServiceClient();
    PidcVersionAttribute pidcVersionAttribute = servClient.getById(PRJ_ATTR_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionAttribute);
    testPidcVersionAttr(pidcVersionAttribute);
  }

  /**
   * @param pidcVersionAttribute
   */
  private void testPidcVersionAttr(final PidcVersionAttribute pidcVersionAttribute) {
    assertEquals("Attr_Id is equal", Long.valueOf(249297), pidcVersionAttribute.getAttrId());
    assertEquals("Value_Id is equal", Long.valueOf(774368669), pidcVersionAttribute.getValueId());
    assertEquals("Used Flag is equal", "Y", pidcVersionAttribute.getUsedFlag());
    assertEquals("Created User is equal", "HEF2FE", pidcVersionAttribute.getCreatedUser());
    assertEquals("Pidc_Vers_Id is equal", Long.valueOf(773513515), pidcVersionAttribute.getPidcVersId());
  }


  /**
   * Negative Test method for {@link PidcVersionAttributeServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    PidcVersionAttributeServiceClient servClient = new PidcVersionAttributeServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Attribute with ID '" + INVALID_ID + NOT_FOUND);
    servClient.getById(INVALID_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }


  /**
   * Test method for {@link PidcVersionAttributeServiceClient#getPidcVersionAttribute(Long, Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void getPidcVersionAttribute() throws ApicWebServiceException {
    PidcVersionAttributeServiceClient servClient = new PidcVersionAttributeServiceClient();
    PidcVersionAttribute pidcVersionAttribute = servClient.getPidcVersionAttribute(PIDC_VERS_ID, ATTR_ID);
    assertFalse("Response should not be null or empty", (pidcVersionAttribute == null));
    testPidcVersionAttr(pidcVersionAttribute);
  }

  /**
   * Test method for {@link PidcVersionAttributeServiceClient#getQnaireConfigAttribute(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetDivisionAttribute() throws ApicWebServiceException {
    PidcVersionAttributeServiceClient servClient = new PidcVersionAttributeServiceClient();
    PidcVersionAttribute ret = servClient.getQnaireConfigAttribute(PIDC_VERS_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ret);
    testDivAttr(ret);
  }


  /**
   * @param ret
   */
  private void testDivAttr(final PidcVersionAttribute ret) {
    // TABV_COMMON_PARAMS, TABV_ATTRIBUTES
    assertEquals("Attribute Name is equal", "iCDM Questionnaire Config", ret.getName());
    assertEquals(
        "Defines which Questionnaires are used for data reviews. Despite the new PS organisation, the questionnaires are defined on base of the former orga structure (DS,DGS).",
        ret.getDescription());
    assertEquals("Attrinute_Id is equal", Long.valueOf(787372416), ret.getAttrId());
  }

  /**
   * Negative Test method for {@link PidcVersionAttributeServiceClient#getQnaireConfigAttribute(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetDivisionAttributeNegative() throws ApicWebServiceException {
    PidcVersionAttributeServiceClient servClient = new PidcVersionAttributeServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version with ID '" + INVALID_ID + NOT_FOUND);
    servClient.getQnaireConfigAttribute(INVALID_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVersionAttributeServiceClient#getPidcVersionAttrModel(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionAttrModel() throws ApicWebServiceException {
    PidcVersionAttributeServiceClient servClient = new PidcVersionAttributeServiceClient();
    PidcVersionWithDetails response = servClient.getPidcVersionAttrModel(PIDC_VERS_ID);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (response == null));
    Map<Long, PidcVariant> pidcVariantMap = response.getPidcVariantMap();
    assertFalse("Response should not be null or empty", pidcVariantMap.isEmpty());
    LOG.info("Size: {}", pidcVariantMap.size());
    PidcVariant pidcVariant = pidcVariantMap.get(VARIANT_ID);
    testVar(pidcVariant);
  }


  /**
   * @param pidcVariant
   */
  private void testVar(final PidcVariant pidcVariant) {
    assertEquals("Description is equal", "MQB-A0 (Polo)\r\n" + "110kW\r\n" + "05E.906.018D",
        pidcVariant.getDescription());
    // TabV_PROJECT_VARIANTS
    assertEquals("Created User is equal", "STP3SI", pidcVariant.getCreatedUser());
    assertNotNull("Created date is not null", pidcVariant.getCreatedDate());
  }


  /**
   * Negative Test method for {@link PidcVersionAttributeServiceClient#getPidcVersionAttrModel(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetPidcVersionAttrModelNegative() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version with ID '" + INVALID_ID + NOT_FOUND);

    new PidcVersionAttributeServiceClient().getPidcVersionAttrModel(INVALID_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Positive Test method for {@link PidcVersionAttributeServiceClient#getQnaireConfigAttribute(Long)}.
   *
   * @given pidcVersionId is 1677573856L and ValueId = 787372419L
   * @throws ApicWebServiceException
   */

  @Test
  public void testGetQnaireConfigAttribute() throws ApicWebServiceException {

    Long pidcVerId = 1507932231L;
    PidcVersionAttribute pidcVersionAttribute =
        new PidcVersionAttributeServiceClient().getQnaireConfigAttribute(pidcVerId);

    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, pidcVersionAttribute);
    assertNotNull("PidcVersId should not be null", pidcVersionAttribute.getPidcVersId());
    assertNotNull("ValueId should not be null", pidcVersionAttribute.getValueId());
    assertEquals("PidcVersId should be equals", pidcVerId, pidcVersionAttribute.getPidcVersId());
    assertEquals("ValueId should be equals", (Long) 787372419L, pidcVersionAttribute.getValueId());
  }

  /**
   * Negative Test method for {@link PidcVersionAttributeServiceClient#getQnaireConfigAttribute(Long)}.
   *
   * @given pidcVersionId is 1677573856L and ValueId = 787372419L
   * @throws ApicWebServiceException with message: Attribute 'iCDM Questionnaire Config' is not set in the project.
   */

  @Test
  public void testGetQnaireConfigAttributeInValidInput() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Attribute 'iCDM Questionnaire Config' is not set in the project");

    new PidcVersionAttributeServiceClient().getQnaireConfigAttribute(1677573856L);

    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Negative Test method for {@link PidcVersionAttributeServiceClient#getQnaireConfigAttribute(Long)}.
   *
   * @given pidcVersionId is -1L
   * @throws ApicWebServiceException with message: Attribute 'DATA_NOT_FOUND : PIDC Version with ID '-1' not found.
   */

  @Test
  public void testGetQnaireConfigAttributeInValidPidcVersId() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version with ID '-1' not found");

    new PidcVersionAttributeServiceClient().getQnaireConfigAttribute(-1L);

    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }
}
