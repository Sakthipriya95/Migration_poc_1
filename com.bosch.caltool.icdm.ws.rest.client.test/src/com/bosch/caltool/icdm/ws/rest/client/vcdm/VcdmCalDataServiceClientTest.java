/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.vcdm;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.vcdm.VcdmCalDataInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;

/**
 * @author dja7cob
 */
public class VcdmCalDataServiceClientTest extends AbstractRestClientTest {

  /**
   * Extension for serialized file
   */
  private static final String SER_EXT = ".ser";

  /**
   * Extension for CDFx file
   */
  private static final String CDFX_EXT = ".cdfx";

  /**
   * Error message when the user doesnot have access to vCDM
   */
  private static final String INVALID_LOGIN_MESSAGE = "Error in vCDM Login. Web service login failed";

  /**
   * Error message when the DST id is invalid/ user does not have access to the DST
   */
  private static final String INVALID_DST_MESSAGE = "Error retrieving parameter values from vCDM.";

  /**
   * A2L file: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788_1
   * (V1_0)->MMD114A0CC1788->MMD114A0CC1788_MC50_DISCR.A2L
   */
  private static final Long A2L_FILE_ID = 2189855001L;

  /**
   * vCDM DST id from the below A2L<br>
   * A2L file: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788_1
   * (V1_0)->MMD114A0CC1788->MMD114A0CC1788_MC50_DISCR.A2L
   */
  private static final Long VCDM_DST_ID = 17353060L;

  /**
   * vCDM DST id
   */
  private static final Long INVALID_VCDM_DST_ID = -100L;

  /**
   * Directory to download cdfx/serialized file
   */
  private static final String DOWNLOAD_DIR = CommonUtils.getSystemUserTempDirPath();

  /**
   * Test user
   */
  private static final String VCDM_TEST_USER = "HFZ2SI";


  /**
   * @throws ApicWebServiceException exception from service
   * @throws PasswordNotFoundException exception in getting password for the test user
   */
  @Test
  public void testFetchVcdmCalDataSerialized() throws ApicWebServiceException, PasswordNotFoundException {

    String serFilePath = DOWNLOAD_DIR + File.separator + VCDM_DST_ID + "_JUnit_" + System.currentTimeMillis() + SER_EXT;

    VcdmCalDataServiceClient vcdmCalDataServiceClient = new VcdmCalDataServiceClient();
    vcdmCalDataServiceClient.setClientConfiguration(createClientConfigTestUser(VCDM_TEST_USER));

    VcdmCalDataInput vcdmCalDataInput = createInputForTestUser(VCDM_DST_ID);

    vcdmCalDataServiceClient.fetchVcdmCalDataSerialized(vcdmCalDataInput, serFilePath);

    assertTrue("Serialized caldata fetched from vCDM successfully", new File(serFilePath).exists());
  }

  /**
   * @throws ApicWebServiceException exception from service
   * @throws PasswordNotFoundException exception in getting password for the test user
   */
  @Test
  public void testFetchVcdmCalDataSerializedInvalid() throws ApicWebServiceException, PasswordNotFoundException {
    String serFilePath = DOWNLOAD_DIR + File.separator + INVALID_VCDM_DST_ID + System.currentTimeMillis() + SER_EXT;

    this.thrown.expectMessage(startsWith(INVALID_DST_MESSAGE));

    VcdmCalDataServiceClient vcdmCalDataServiceClient = new VcdmCalDataServiceClient();
    vcdmCalDataServiceClient.setClientConfiguration(createClientConfigTestUser(VCDM_TEST_USER));

    VcdmCalDataInput vcdmCalDataInput = createInputForTestUser(INVALID_VCDM_DST_ID);

    vcdmCalDataServiceClient.fetchVcdmCalDataSerialized(vcdmCalDataInput, serFilePath);
  }

  /**
   * @throws ApicWebServiceException exception from service
   * @throws PasswordNotFoundException exception in getting password for the test user
   */
  @Test
  public void testFetchVcdmCalDataSerializedAccessDenied() throws ApicWebServiceException, PasswordNotFoundException {
    String serFilePath = DOWNLOAD_DIR + File.separator + VCDM_DST_ID + System.currentTimeMillis() + SER_EXT;

    this.thrown.expectMessage(INVALID_LOGIN_MESSAGE);

    VcdmCalDataServiceClient vcdmCalDataServiceClient = new VcdmCalDataServiceClient();

    VcdmCalDataInput vcdmCalDataInput = createInputForTestUser(VCDM_DST_ID);

    vcdmCalDataServiceClient.fetchVcdmCalDataSerialized(vcdmCalDataInput, serFilePath);
  }


  /**
   * @throws ApicWebServiceException exception from service
   * @throws PasswordNotFoundException exception in getting password for the test user
   */
  @Test
  public void testFetchVcdmCalDataCdfx() throws ApicWebServiceException, PasswordNotFoundException {
    String cdfxFilePath = DOWNLOAD_DIR + File.separator + VCDM_DST_ID + System.currentTimeMillis() + CDFX_EXT;

    VcdmCalDataServiceClient vcdmCalDataServiceClient = new VcdmCalDataServiceClient();
    vcdmCalDataServiceClient.setClientConfiguration(createClientConfigTestUser(VCDM_TEST_USER));

    VcdmCalDataInput vcdmCalDataInput = createInputForTestUser(VCDM_DST_ID);

    vcdmCalDataServiceClient.fetchVcdmCalDataCdfx(vcdmCalDataInput, cdfxFilePath);

    assertTrue("Caldata fetched from vCDM successfully as CDFx", new File(cdfxFilePath).exists());
  }

  /**
   * @throws ApicWebServiceException exception from service
   * @throws PasswordNotFoundException exception in getting password for the test user
   */
  @Test
  public void testFetchVcdmCalDataCdfxInvalid() throws ApicWebServiceException, PasswordNotFoundException {
    String cdfxFilePath = DOWNLOAD_DIR + File.separator + INVALID_VCDM_DST_ID + System.currentTimeMillis() + CDFX_EXT;

    this.thrown.expectMessage(startsWith(INVALID_DST_MESSAGE));

    VcdmCalDataServiceClient vcdmCalDataServiceClient = new VcdmCalDataServiceClient();
    vcdmCalDataServiceClient.setClientConfiguration(createClientConfigTestUser(VCDM_TEST_USER));

    VcdmCalDataInput vcdmCalDataInput = createInputForTestUser(INVALID_VCDM_DST_ID);

    vcdmCalDataServiceClient.fetchVcdmCalDataCdfx(vcdmCalDataInput, cdfxFilePath);
  }

  /**
   * @throws ApicWebServiceException exception from service
   * @throws PasswordNotFoundException exception in getting password for the test user
   */
  @Test
  public void testFetchVcdmCalDataCdfxAccessDenied() throws ApicWebServiceException, PasswordNotFoundException {
    String cdfxFilePath = DOWNLOAD_DIR + File.separator + VCDM_DST_ID + System.currentTimeMillis() + CDFX_EXT;

    this.thrown.expectMessage(INVALID_LOGIN_MESSAGE);

    VcdmCalDataServiceClient vcdmCalDataServiceClient = new VcdmCalDataServiceClient();

    VcdmCalDataInput vcdmCalDataInput = createInputForTestUser(VCDM_DST_ID);

    vcdmCalDataServiceClient.fetchVcdmCalDataCdfx(vcdmCalDataInput, cdfxFilePath);
  }

  /**
   * @param dstId
   * @return VcdmCalDataInput for the VcdmCalDataService
   * @throws PasswordNotFoundException exception in getting password for the test user
   */
  private VcdmCalDataInput createInputForTestUser(final Long dstId) throws PasswordNotFoundException {
    VcdmCalDataInput vcdmCalDataInput = new VcdmCalDataInput();
    vcdmCalDataInput.setA2lFileId(A2L_FILE_ID);
    vcdmCalDataInput.setVcdmDstId(dstId);
    vcdmCalDataInput.setEncPwd(new PasswordService().getPassword("ICDM.LDAP_MGR_PASSWORD"));
    return vcdmCalDataInput;
  }
}
