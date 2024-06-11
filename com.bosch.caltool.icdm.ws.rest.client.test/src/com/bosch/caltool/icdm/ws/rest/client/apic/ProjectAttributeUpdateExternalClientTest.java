/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValueExtModel;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributeUpdateExternalInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.Matchers;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mkl2cob
 */
public class ProjectAttributeUpdateExternalClientTest extends AbstractRestClientTest {

  private static final String TEST_USER_INVALID = "BNE4COB";
  private static final String TEST_USER_VALID = "PSRC_SYS";

  private static final String TESTDATA_VALID_LINK = "https://bzo.bosch.com/bzo/en/start_page.html";

  private static final String LOG_OP_MESSAGE = "Output Message : {}";
  private static final String MSG_EXPECTED_EXCEPTION_NOT_THROWN = "Expected exception not thrown";
  private static final String CHECK_SUCCESS_RESPONSE = "Check success response";

  /**
   * TEST PIDC ID (test_monica_copy)
   */
  private static final long PIDC_ID = 2263535179L;
  private static final long VARIANT_ID = 2784210061L;
  private static final String PIDC_VERSION_NAME_1 = "test_monica_copy (v1)";
  private static final String PIDC_VERSION_NAME_2 = "test_PSRC_var (v1)";

  private static final String DESC_GER = "JUNIT TEST GER";
  private static final String DESC_ENG = "JUNIT test";

  /**
   * Review Document Subproject Calibration/PSR-C Link -attribute id
   */
  private static final long PSRC_LINK_ATTR_ID = 249307L;
  private static final String REVIEW_DOC_PSR_C_LINK_ATTR_NAME = "Review Document Subproject Calibration";

  private static final String SUCCESS_STRING_FOR_PIDC_LEVEL =
      "Attribute " + REVIEW_DOC_PSR_C_LINK_ATTR_NAME + " updated in PIDC " + PIDC_VERSION_NAME_1;

  private static final String SUCCESS_STRING_FOR_VAR_LEVEL =
      "Attribute " + REVIEW_DOC_PSR_C_LINK_ATTR_NAME + " updated in PIDC " + PIDC_VERSION_NAME_2;


  private ProjectAttributeUpdateExternalServiceClient servClient;

  /**
   * Creates a valid service client
   *
   * @throws ApicWebServiceException error during client creation
   */
  @Before
  public void createValidClient() throws ApicWebServiceException {
    this.servClient = new ProjectAttributeUpdateExternalServiceClient();
    this.servClient.setClientConfiguration(createClientConfigTestUser(TEST_USER_VALID));
  }

  /**
   * test project attribte updation from external- positive test case
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal01() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setPidcId(PIDC_ID);
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    input.setValue(attrValExt);

    String outputString = this.servClient.updateProjectAttributeExternal(input);
    LOG.info(LOG_OP_MESSAGE, outputString);
    assertEquals(CHECK_SUCCESS_RESPONSE, SUCCESS_STRING_FOR_PIDC_LEVEL, outputString);
  }

  /**
   * test project attribte updation from external- negative test case
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal10() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    attrValExt.setDescriptionEng(DESC_ENG);
    attrValExt.setDescriptionGer(DESC_GER);
    input.setValue(attrValExt);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("PIDC_ATTR_UPDATE_EXT.NO_PIDC_ID_OR_VAR_ID"));

    this.servClient.updateProjectAttributeExternal(input);

    fail(MSG_EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test project attribte updation from external -negative test case, attr id is null
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal11() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setPidcId(PIDC_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    attrValExt.setDescriptionEng(DESC_ENG);
    attrValExt.setDescriptionGer(DESC_GER);
    input.setValue(attrValExt);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("PIDC_ATTR_UPDATE_EXT.ATTR_ID_NULL"));

    this.servClient.updateProjectAttributeExternal(input);

    fail(MSG_EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test project attribte updation from external- NEGATIVE test case , invalid hyperlink
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal12() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setPidcId(PIDC_ID);
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue("bzo.bosch.com/bzo/en/start_page.html");
    attrValExt.setDescriptionEng(DESC_ENG);
    attrValExt.setDescriptionGer(DESC_GER);
    input.setValue(attrValExt);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("PIDC_ATTR_UPDATE_EXT.INVALID_HYPERLINK"));

    this.servClient.updateProjectAttributeExternal(input);

    fail(MSG_EXPECTED_EXCEPTION_NOT_THROWN);

  }

  /**
   * test project attribte updation from external -negative test case, attr id is different
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal02() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setPidcId(PIDC_ID);
    input.setAttributeId(36L);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    attrValExt.setDescriptionEng(DESC_ENG);
    attrValExt.setDescriptionGer(DESC_GER);
    input.setValue(attrValExt);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("PIDC_ATTR_UPDATE_EXT.ATTR_NOT_SUPPORTED"));

    this.servClient.updateProjectAttributeExternal(input);

    fail(MSG_EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test project attribte updation from external -negative test case, invalid user
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal03() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalServiceClient invalidClient = new ProjectAttributeUpdateExternalServiceClient();
    invalidClient.setClientConfiguration(createClientConfigTestUser(TEST_USER_INVALID));

    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setPidcId(PIDC_ID);
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    attrValExt.setDescriptionEng(DESC_ENG);
    attrValExt.setDescriptionGer(DESC_GER);
    input.setValue(attrValExt);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("PIDC_ATTR_UPDATE_EXT.INVALID_USER"));

    invalidClient.updateProjectAttributeExternal(input);

    fail(MSG_EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test project attribte updation from external -negative test case, invalid variant
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal04() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setPidcId(PIDC_ID);
    input.setVariantId(VARIANT_ID);
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    attrValExt.setDescriptionEng(DESC_ENG);
    attrValExt.setDescriptionGer(DESC_GER);
    input.setValue(attrValExt);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("PIDC_ATTR_UPDATE_EXT.INVALID_VARIANT"));

    this.servClient.updateProjectAttributeExternal(input);

    fail(MSG_EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test project attribte updation from external -negative test case with wrong level
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal05() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setPidcId(9071946079L);
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    attrValExt.setDescriptionEng(DESC_ENG);
    attrValExt.setDescriptionGer(DESC_GER);
    input.setValue(attrValExt);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("PIDC_ATTR_UPDATE_EXT.PROJ_ATTR_NOT_IN_RIGHT_LEVEL"));

    this.servClient.updateProjectAttributeExternal(input);

    fail(MSG_EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test project attribte updation from external -positive test case in variant level
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal06() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setVariantId(9071946096L);
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    attrValExt.setDescriptionEng(DESC_ENG);
    attrValExt.setDescriptionGer(DESC_GER);
    input.setValue(attrValExt);

    String outputString = this.servClient.updateProjectAttributeExternal(input);

    LOG.info(LOG_OP_MESSAGE, outputString);
    assertTrue(SUCCESS_STRING_FOR_VAR_LEVEL.equals(outputString));
  }


  /**
   * test project attribte updation from external -negative test case with wrong level
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal07() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setVariantId(2263535191L);
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    input.setValue(attrValExt);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("PIDC_ATTR_UPDATE_EXT.VAR_ATTR_NOT_IN_RIGHT_LEVEL"));

    this.servClient.updateProjectAttributeExternal(input);

    fail(MSG_EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test project attribte updation from external -negative test case with wrong used flag
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal08() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setVariantId(2263535191L);
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(TESTDATA_VALID_LINK);
    input.setUsedFlag("Correct");
    input.setValue(attrValExt);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expect(Matchers.matchesErrorCode("PIDC_ATTR_UPDATE_EXT.NOT_VALID_USED_FLAG"));

    this.servClient.updateProjectAttributeExternal(input);

    fail(MSG_EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test project attribte updation from external- positive test case with null value
   *
   * @throws ApicWebServiceException Exception during web service
   */
  @Test
  public void testProjectAttrUpdationExternal09() throws ApicWebServiceException {
    ProjectAttributeUpdateExternalInput input = new ProjectAttributeUpdateExternalInput();
    input.setPidcId(PIDC_ID);
    input.setAttributeId(PSRC_LINK_ATTR_ID);
    AttributeValueExtModel attrValExt = new AttributeValueExtModel();
    attrValExt.setHyperLinkValue(null);
    input.setUsedFlag("YES");
    input.setValue(attrValExt);

    String outputString = this.servClient.updateProjectAttributeExternal(input);

    LOG.info(LOG_OP_MESSAGE, outputString);
    assertEquals(CHECK_SUCCESS_RESPONSE, SUCCESS_STRING_FOR_PIDC_LEVEL, outputString);
  }


}
