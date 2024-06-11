/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDetailsQsr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author DJA7COB
 */
public class ReviewResultDetailsQsrServiceClientTest extends AbstractRestClientTest {

  /**
   * CDR result with variant CDR Result: Honda->Gasoline Engine->PC - Passenger Car->ME(D)17->HONDA XE1B 2KR (Version
   * 1)->REVIEWED_FILE->2021-05-14 10:52 - V130S70 - 2KR HK - test compare hex
   */
  private static final String CDR_LINK = "icdm:cdrid,13123390430-773517365-768673303";

  /**
   * CDR result (NO-VARIANT) CDR Result: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB,
   * Gen2, US (Version 1)->FUNCTION->2020-04-13 17:48 - 14A0 - AR4C NAFTA MY18 VS MY15
   */
  private static final String CDR_LINK_NO_VAR = "icdm:cdrid,3220771117-773519265";

  private static final String CDR_LINK_INVALID = "3220771117";

  /**
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByCdrLink() throws ApicWebServiceException {

    ReviewResultDetailsQsr reviewResultDetails = new ReviewResultDetailsQsrServiceClient().getbyCdrLink(CDR_LINK);

    assertNotNull(reviewResultDetails);
    assertTrue(CommonUtils.isNotEmpty(reviewResultDetails.getLinkedVarUrls()));
  }

  /**
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByCdrLinkNoVar() throws ApicWebServiceException {

    ReviewResultDetailsQsr reviewResultDetails =
        new ReviewResultDetailsQsrServiceClient().getbyCdrLink(CDR_LINK_NO_VAR);

    assertNotNull(reviewResultDetails);
    assertTrue(CommonUtils.isNullOrEmpty(reviewResultDetails.getLinkedVarUrls()));
  }

  /**
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByCdrLinkInvalidLink() throws ApicWebServiceException {
    this.thrown.expectMessage("Invalid format of iCDM hyperlink");

    new ReviewResultDetailsQsrServiceClient().getbyCdrLink(CDR_LINK_INVALID);
  }
}
