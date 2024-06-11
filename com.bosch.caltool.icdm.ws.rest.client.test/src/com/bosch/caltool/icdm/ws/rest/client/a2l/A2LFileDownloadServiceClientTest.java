/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class A2LFileDownloadServiceClientTest extends AbstractRestClientTest {


  /**
   * vCDM A2L file id for icdm:a2lid,1165057193-2068555001
   */
  private static final long VCDM_A2L_FILE_ID_DOWNLOAD = 20710813L;
  /**
   * A2L file name for icdm:a2lid,1165057193-2068555001
   */
  private static final String A2L_FILE_NAME_DOWNLOAD = "MMD114A0CC1788_MC50_DISCR.A2L";

  private static final Long VCDM_A2L_FILE_ID_INVALID = -1L;
  private static final String A2L_FILE_NAME_INVALID = "InvalidA2l.A2L";

  /**
   * Test method for {@link A2LFileDownloadServiceClient#getA2lFileById(Long,String,String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetA2LFileById() throws ApicWebServiceException {
    String a2lFileName = getRunId() + "_" + A2L_FILE_NAME_DOWNLOAD;
    String a2lFilePath = CommonUtils.getSystemUserTempDirPath() + File.separator + a2lFileName;

    new A2LFileDownloadServiceClient().getA2lFileById(VCDM_A2L_FILE_ID_DOWNLOAD, a2lFileName,
        CommonUtils.getSystemUserTempDirPath());

    assertTrue("A2L file downloaded successfully", new File(a2lFilePath).exists());
  }

  /**
   * Test method for {@link A2LFileDownloadServiceClient#getA2lFileById(Long,String,String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetA2LFileByInvalidId() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Invalid vCDM A2L File ID");

    new A2LFileDownloadServiceClient().getA2lFileById(VCDM_A2L_FILE_ID_INVALID, A2L_FILE_NAME_INVALID,
        CommonUtils.getSystemUserTempDirPath());
  }

  /**
   * Test method for {@link A2LFileDownloadServiceClient#getA2lFileById(Long,String,String)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetA2LFileByVcdmA2lFileIdNull() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("vCDM A2L File ID is mandatory");

    new A2LFileDownloadServiceClient().getA2lFileById(null, A2L_FILE_NAME_INVALID,
        CommonUtils.getSystemUserTempDirPath());
  }


}
