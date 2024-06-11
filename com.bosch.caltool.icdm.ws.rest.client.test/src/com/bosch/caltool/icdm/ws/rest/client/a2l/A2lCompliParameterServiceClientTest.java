/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map.Entry;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lCompliParameterServiceResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * JUnit class to test the fetching of compliance parameter in an a2l file
 *
 * @author svj7cob
 */
// Task 263282
public class A2lCompliParameterServiceClientTest extends AbstractRestClientTest {


  /**
   * A2L file path with direct a2l file
   */
  private static final String A2L_FILE_PATH_01 = "testdata/cdr/A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3.a2l";
  // "D:\\RBEI\\iCDM\\common\\CompliReviewService\\Story_273414_Compli_Review_with_new_data_model_framework_test_data\\a2l-file\\A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3.a2l";

  /**
   * A2L file path with zipped a2l file
   */
  private static final String A2L_FILE_PATH_02 = "testdata/cdr/A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3.zip";
  // "D:\\RBEI\\iCDM\\common\\CompliReviewService\\Story_273414_Compli_Review_with_new_data_model_framework_test_data\\a2l-file\\A2L_P1337_M240_Wline_VM_CY_customer_patch_mm3.zip";

  /**
   * Given web flow Id
   */
  private static final String WEBFLOW_ID = "WF3248";


  private static final String OUTPUT_PATH = System.getProperty("java.io.tmpdir") + "/A2LCompliParam__" +
      (new SimpleDateFormat("yyyyMMdd_hhmmss")).format(Calendar.getInstance().getTime()) + ".zip";

  /**
   * Test : response zip - both PDF and json as content
   */
  @Test
  public void test01() {
    try {
      LOG.info("=====================================================================");
      LOG.info("Test 01 response zip - both PDF and json as content");
      LOG.info("=====================================================================");
      A2lCompliParameterServiceClient servClient = new A2lCompliParameterServiceClient();

      servClient.getA2lCompliParamFiles(A2L_FILE_PATH_01, WEBFLOW_ID, OUTPUT_PATH);

      File file = new File(OUTPUT_PATH);
      assertTrue(file.exists());

    }
    catch (

    Exception exp) {
      LOG.error(exp.getMessage(), exp);
      assertNull("Error when fetching A2l compli params", exp);
    }

  }

  /**
   * @throws ApicWebServiceException
   */
  @Test
  public void testNegativegetA2lCompliParamFiles01() throws ApicWebServiceException {
    A2lCompliParameterServiceClient servClient = new A2lCompliParameterServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getA2lCompliParamFiles("", WEBFLOW_ID, OUTPUT_PATH);
    this.thrown.expectMessage("Error while retrieving output file - java.io.FileNotFoundException");
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * @throws ApicWebServiceException
   */
  @Test
  public void testNegativegetA2lCompliParamFiles02() throws ApicWebServiceException {
    A2lCompliParameterServiceClient servClient = new A2lCompliParameterServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getA2lCompliParamFiles(A2L_FILE_PATH_01, "", OUTPUT_PATH);
    this.thrown.expectMessage("Error while retrieving output file - Input parameter webflowid is mandatory");
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  /**
   * @throws ApicWebServiceException
   */
  @Test
  public void testNegativegetA2lCompliParamFiles03() throws ApicWebServiceException {
    A2lCompliParameterServiceClient servClient = new A2lCompliParameterServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getA2lCompliParamFiles(A2L_FILE_PATH_01, WEBFLOW_ID, "");
    this.thrown.expectMessage("The process cannot access the file because it is being used by another process.");
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test with direct A2L file. Response model
   */
  @Test
  public void test02() {

    try {
      LOG.info("=====================================================================");
      LOG.info("Test 01 with direct a2l file");
      LOG.info("=====================================================================");
      A2lCompliParameterServiceClient servClient = new A2lCompliParameterServiceClient();
      A2lCompliParameterServiceResponse a2lCompliParams = servClient.getA2lCompliParams(A2L_FILE_PATH_01, WEBFLOW_ID);
      assertNotNull("Response should not be null", a2lCompliParams);
      LOG.info("No. of Compli parameter in the a2l file : " + a2lCompliParams.getA2lCompliParamMap().size());
    }
    catch (Exception exp) {
      LOG.error(exp.getMessage(), exp);
      assertNull("Error when fetching A2l compli params", exp);
    }

  }

  /**
   * Test with zipped A2L file
   */
  @Test
  public void test03() {

    try {
      LOG.info("=====================================================================");
      LOG.info("Test 02 with zipped a2l file");
      LOG.info("=====================================================================");
      A2lCompliParameterServiceClient servClient = new A2lCompliParameterServiceClient();
      A2lCompliParameterServiceResponse a2lCompliParams = servClient.getA2lCompliParams(A2L_FILE_PATH_02, WEBFLOW_ID);
      assertNotNull("Response should not be null", a2lCompliParams);
      LOG.info("No. of Compli parameter in the a2l file : " + a2lCompliParams.getA2lCompliParamMap().size());
      for (Entry<String, String> element : a2lCompliParams.getA2lCompliParamMap().entrySet()) {
        Entry<String, String> entry = element;
        LOG.info(" : " + entry.getKey() + " : " + entry.getValue());
      }
    }
    catch (Exception exp) {
      LOG.error(exp.getMessage(), exp);
      assertNull("Error when fetching A2l compli params", exp);
    }

  }


}
