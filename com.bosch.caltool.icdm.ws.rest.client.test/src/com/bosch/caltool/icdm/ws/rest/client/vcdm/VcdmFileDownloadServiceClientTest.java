/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.vcdm;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class VcdmFileDownloadServiceClientTest extends AbstractRestClientTest {

  /**
   * PIDC ID (X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_Henze_Maserati)
   */
  private static final Long PIDC_ID = 768673117L;

  /**
   * A2L file to download
   */
  private static final Long A2L_FILE_ID = 20570508L;
  private static final String A2L_NAME = "P1337_M240_Wline_VM_CY_customer_patch_mm3.a2l";

  /**
   * Invalid PIDC ID
   */
  private static final Long INVALID_PIDC_ID = -1L;

  /**
   * Invalid A2L file ID
   */
  private static final Long INVALID_A2L_FILE_ID = -1L;

  /**
   * HEX file
   */
  private static final Long HEX_FILE_ID = 20570508L;
  private static final String HEX_NAME = "EA56HPE6M240LG032KAHA.hex";

  /**
   * DALB file
   */
  private static final Long DALB_FILE_ID = 20679793L;
  private static final String DALB_NAME = "SCR_M240_CAT.XML";

  private static final String DOWNLOAD_DIR = CommonUtils.getSystemUserTempDirPath();

  private static final String FILE_DWNLD_USER_INVALID = "MGA1COB";

  /**
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception in file handling
   */
  @Test
  public void testGetA2L() throws ApicWebServiceException, IOException {
    new VcdmFileDownloadServiceClient().get(PIDC_ID, A2L_FILE_ID, A2L_NAME, DOWNLOAD_DIR);

    String vcdmFilePath = DOWNLOAD_DIR + File.separator + A2L_NAME;
    assertTrue("Check if Output vCDM A2L file exists", new File(vcdmFilePath).exists());
  }

  /**
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception in file handling
   */
  @Test
  public void testGetHex() throws ApicWebServiceException, IOException {
    new VcdmFileDownloadServiceClient().get(PIDC_ID, HEX_FILE_ID, HEX_NAME, DOWNLOAD_DIR);

    String vcdmFilePath = DOWNLOAD_DIR + File.separator + HEX_NAME;
    assertTrue("Check if Output vCDM HEX file exists", new File(vcdmFilePath).exists());
  }

  /**
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception in file handling
   */
  @Test
  public void testGetDALB() throws ApicWebServiceException, IOException {
    new VcdmFileDownloadServiceClient().get(PIDC_ID, DALB_FILE_ID, DALB_NAME, DOWNLOAD_DIR);

    String vcdmFilePath = DOWNLOAD_DIR + File.separator + DALB_NAME;
    assertTrue("Check if Output vCDM DALB file exists", new File(vcdmFilePath).exists());
  }

  /**
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception in file handling
   */
  @Test
  public void testGetA2LInvalidPidc() throws ApicWebServiceException, IOException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + INVALID_PIDC_ID + "' is invalid for Project ID Card");

    new VcdmFileDownloadServiceClient().get(INVALID_PIDC_ID, A2L_FILE_ID, A2L_NAME, DOWNLOAD_DIR);
  }

  /**
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception in file handling
   */
  @Test
  public void testGetA2LInvalidA2lVcdmFileId() throws ApicWebServiceException, IOException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Error while getting vCDM File");

    new VcdmFileDownloadServiceClient().get(PIDC_ID, INVALID_A2L_FILE_ID, A2L_NAME, DOWNLOAD_DIR);
  }

  /**
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception in file handling
   */
  @Test
  public void testGetA2LInsufficientRights() throws ApicWebServiceException, IOException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Insufficient privileges to download file from vCDM");

    VcdmFileDownloadServiceClient serviceClient = new VcdmFileDownloadServiceClient();
    serviceClient.setClientConfiguration(createClientConfigTestUser(FILE_DWNLD_USER_INVALID));
    serviceClient.get(PIDC_ID, INVALID_A2L_FILE_ID, A2L_NAME, DOWNLOAD_DIR);
  }
}
