/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.general.ErrorCode;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author ukt1cob
 */
public class ErrorCodeServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String ERROR_MESSAGE = "Could not find the details for the given error code ";

  /**
   * Test method for {@link ErrorCodeServiceClient#getAll()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    Map<String, ErrorCode> retMap = new ErrorCodeServiceClient().getAll();
    assertFalse("Response should not be empty", retMap.isEmpty());
    LOG.info("Size : {}", retMap.size());
  }


  /**
   * Test method for {@link ErrorCodeServiceClient#getErrorCodeByID(String code)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetgetErrorCodeByIDCase() throws ApicWebServiceException {
    ErrorCodeServiceClient servClient = new ErrorCodeServiceClient();
    ErrorCode errorcode = servClient.getErrorCodeByID("COMPLI_REVIEW.READ_ERROR");
    assertNotNull("Response should not be null", errorcode);
  }

  /**
   * Test method for {@link ErrorCodeServiceClient#getErrorCodeByID(String code)}. Test method to test whether
   * customized error message is thrown while passing error code without seperator '.'
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetgetErrorCodeByIDCase1() throws ApicWebServiceException {
    ErrorCodeServiceClient servClient = new ErrorCodeServiceClient();
    String code = "COMPLI_REVIEWREAD_ERROR";
    this.thrown.expectMessage(ERROR_MESSAGE + code);
    servClient.getErrorCodeByID(code);
  }

  /**
   * Test method for {@link ErrorCodeServiceClient#getErrorCodeByID(String code)}. Test method to test whether
   * customized error message is thrown while passing code which is not an error code
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetgetErrorCodeByIDCase2() throws ApicWebServiceException {
    ErrorCodeServiceClient servClient = new ErrorCodeServiceClient();
    String code = "RVW_WORK_PKG_SEL_DIALOG.INFO_MSG";
    this.thrown.expectMessage(ERROR_MESSAGE + code);
    servClient.getErrorCodeByID(code);
  }

  /**
   * Test method for {@link ErrorCodeServiceClient#getErrorCodeByID(String code)}. Test method to test whether
   * customized error message is thrown while passing empty string
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetgetErrorCodeByIDCase3() throws ApicWebServiceException {
    ErrorCodeServiceClient servClient = new ErrorCodeServiceClient();
    String code = "";
    this.thrown.expectMessage(ERROR_MESSAGE + code);
    servClient.getErrorCodeByID(code);
  }

  /**
   * Test method for {@link ErrorCodeServiceClient#getErrorCodeByID(String code)}. Test method to test whether
   * customized error message is thrown while passing error code as null
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetgetErrorCodeByIDCase4() throws ApicWebServiceException {
    ErrorCodeServiceClient servClient = new ErrorCodeServiceClient();
    String code = null;
    this.thrown.expectMessage(ERROR_MESSAGE + code);
    servClient.getErrorCodeByID(code);
  }


}
