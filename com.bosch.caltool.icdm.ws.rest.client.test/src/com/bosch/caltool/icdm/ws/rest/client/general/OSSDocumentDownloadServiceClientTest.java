/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author NDV4KOR
 */
public class OSSDocumentDownloadServiceClientTest extends AbstractRestClientTest {

  /**
   * Constant for invalid path
   */
  public static final String INVALID_ID = "####";

  /**
   * test to download the iCDM OSS Document when correct Path is given.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testGetOSSDocument() throws ApicWebServiceException {
    byte[] files = new OSSDocumentDownloadServiceClient().getOSSDocument(CommonUtils.getSystemUserTempDirPath());
    assertFalse("Response should not be null or empty", (files == null) || (files.length == 0));
  }

  /**
   * Negative testcase to download the iCDM OSS Document in invalid path.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testGetOSSDocumentInvalid() throws ApicWebServiceException {
    OSSDocumentDownloadServiceClient servClient = new OSSDocumentDownloadServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(
        "Error while downloading file OSS_Document.zip : " + OSSDocumentDownloadServiceClientTest.INVALID_ID +
            "\\OSS_Document.zip (The system cannot find the path specified)");
    servClient.getOSSDocument(OSSDocumentDownloadServiceClientTest.INVALID_ID);
    fail("Expected exception not thrown");
  }

}
