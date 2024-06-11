/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.GetParameterReviewResultResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.ReviewResultsType;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;
import com.bosch.caltool.apic.ws.client.reviewresult.ReviewDetail;
import com.bosch.caltool.apic.ws.client.reviewresult.ReviewParameter;
import com.bosch.caltool.apic.ws.client.reviewresult.ReviewResult;
import com.bosch.caltool.apic.ws.client.reviewresult.output.StringRevParamOutput;

/**
 * @author rgo7cob
 */
public class TestReviewResult extends AbstractSoapClientTest {

  private final APICWebServiceClient stub = new APICWebServiceClient();


  /**
   * test a parameter
   *
   * @throws Exception response error
   */
  @Test
  public void testReviewDetails() throws Exception {


    GetParameterReviewResultResponseType parameterReviewResults = this.stub.getParameterReviewResults(381214565L);

    assertNotNull(parameterReviewResults);
    LOG.info("Parmeter Id:{}", parameterReviewResults.getParameterId());
    LOG.info("Parmeter Name:{}", parameterReviewResults.getParameterName());

    int counter = 0;
    for (ReviewResultsType resultType : parameterReviewResults.getReviewDetails()) {
      if (counter >= 5) {
        break;
      }
      LOG.info("Check Value {}", resultType.getCheckeValueString());
      LOG.info(" Review Description {}", resultType.getReviewDescription());
      LOG.info(" Review Name {}", resultType.getReviewName());
      counter++;
    }
  }

  /**
   * test a parameter with variant encoding
   *
   * @throws Exception response error
   */
  @Test
  public void testReviewDetailsVariantEncoding() throws Exception {
    GetParameterReviewResultResponseType[] response =
        this.stub.getParameterReviewResults(381214565L, 399624665L, 432182565L, 433642515L);
    assertNotNull(response);

    ReviewParameter rvwParam = new ReviewParameter(response, "PT_drRmpTransP[0].Neg_C");
    logParameter(rvwParam);
    logReviewResult(rvwParam);
  }

  private void logParameter(final ReviewParameter response) {
    LOG.info("Parameter Details");
    LOG.info("  Parameter-ID: {}", response.getParameterId());
    LOG.info("  Parameter-Name: {}", response.getParameterName());
    LOG.info("  Parameter-Longname: {}", response.getParameterLongname());
  }

  private void logReviewResult(final ReviewParameter response) {
    int counter = 0;
    for (ReviewResult result : response.getReviewResults()) {
      if (counter >= 3) {// log only first 'n' records
        break;
      }
      // Show the Review Results, there are 1:n Results
      LOG.info("  Review Result");
      LOG.info("    Number of Reviews: {}", result.getNumberOfRecords());
      LOG.info("    Check Value: {}", result.getCheckedValueString());
      LOG.info("    Check Unit: {}", result.getUnit());

      // For each Review Result, there are 1:n PIDCs
      int counter2 = 0;
      for (ReviewDetail detail : result.getReviewDetails()) {
        if (counter2 >= 2) {// log only first 'n' records
          break;
        }
        LOG.info("    PIDC Details");
        LOG.info("      PIDC-ID: {}", detail.getPidcId());
        LOG.info("      PIDC-Name: {}", detail.getPidcName());
        LOG.info("      Variant-ID: {}", detail.getVariantId());
        LOG.info("      Variant-Name: {}", detail.getVariantName());
        LOG.info("      Review-ID: {}", detail.getReviewResultId());
        LOG.info("      Review-Name: {}", detail.getReviewName());
        LOG.info("      Review-Date: {}", detail.getDateOfReviewString("yyyy-MM-dd HH:mm:ss"));
        LOG.info("      Review-Comment: {}", detail.getReviewComment());
        LOG.info("      Review-Result: {}", detail.getReviewResult());
        counter2++;
      }
      counter++;
    }
  }

  /**
   * @throws Exception response error
   */
  @Test
  public void testReviewDetailsForManyParams() throws Exception {

    long[] paramId = { 381214565l, 399624665l, 432182565l, 433642515l };
    GetParameterReviewResultResponseType[] parameterReviewResults = this.stub.getParameterReviewResults(paramId);

    assertNotNull(parameterReviewResults);

    for (GetParameterReviewResultResponseType getParameterReviewResultResponseType : parameterReviewResults) {
      LOG.info("Parmeter Id:{}", getParameterReviewResultResponseType.getParameterId());
      LOG.info(" Parmeter Name:{}", getParameterReviewResultResponseType.getParameterName());

      int counter = 0;
      for (ReviewResultsType resultType : getParameterReviewResultResponseType.getReviewDetails()) {
        if (counter >= 3) {
          break;
        }
        LOG.info("   Check Value {}", resultType.getCheckeValueString());
        LOG.info("     Review Description {}", resultType.getReviewDescription());
        LOG.info("     Review Name {}", resultType.getReviewName());
        counter++;
      }
    }

  }


  /**
   * @throws Exception response error
   */
  @Test
  public void testReviewDetailsCreateReviewResultObject() throws Exception {
    GetParameterReviewResultResponseType response = this.stub.getParameterReviewResults(379425215L);
    try {
      ReviewParameter param = new ReviewParameter(response);
      assertNotNull(param.getReviewResults());
    }
    catch (ClassNotFoundException | IOException e) {
      assertNull("Error when creating ReviewParameter", e);
    }
  }

  /**
   * @throws Exception response error
   */
  @Test
  public void testReviewDetailsShowReviewResultOutput() throws Exception {
    GetParameterReviewResultResponseType response = this.stub.getParameterReviewResults(384849415L);
    try {
      AbstractStringOutput out = new StringRevParamOutput(new ReviewParameter(response));
      out.createOutput();
      LOG.info("\n" + out.getOutput());
    }
    catch (ClassNotFoundException | IOException e) {
      assertNull("Error when creating ReviewParameter", e);
    }
  }

  /**
   * @throws Exception response error
   */
  @Test
  public void testReviewDetailsResponseShouldNotBeNull() throws Exception {
    GetParameterReviewResultResponseType response = this.stub.getParameterReviewResults(379425215L);
    assertNotNull("Response not null", response);
  }

  /**
   * @throws Exception response error
   */
  @Test
  public void testReviewDetailsResponseShouldReturnParamInformation() throws Exception {
    GetParameterReviewResultResponseType response = this.stub.getParameterReviewResults(379425215L);
    assertNotNull("Response not null", response);
    assertTrue("Verify parameter name. Excepted: FPVMXST, got: " + response.getParameterName(),
        response.getParameterName().equalsIgnoreCase("FPVMXST"));
  }


}
