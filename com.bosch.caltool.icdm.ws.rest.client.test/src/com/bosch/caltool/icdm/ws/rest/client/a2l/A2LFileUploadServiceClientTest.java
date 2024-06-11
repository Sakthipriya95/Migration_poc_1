/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Test class for a2l file upload functionality
 *
 * @author pdh2cob
 */
public class A2LFileUploadServiceClientTest extends AbstractRestClientTest {

  private static final String MSG_NEW_A2L_GENERATED_FOR_TEST = "New A2L generated for test";
  private static final String MSG_INVALID_SDOM_PVER_DET = "Invalid Sdom PVER Details.";

  /**
   * a2l file name
   */
  private static final String A2L_FILE_NAME = "P1284_I10R2.a2l";
  /**
   * source a2l file path
   */
  private static final String SOURCE_A2L_FILE_PATH = TESTDATA_ROOT_DIR + "a2l" + File.separator + A2L_FILE_NAME;

  /**
   * Project ID
   */
  private static final Long PROJECT_ID = 3121659482L;
  /**
   * pidc version id
   */
  private static final Long PIDC_VERSION_ID = 3121659484L;
  /**
   * sdom pver nmae
   */
  private static final String SDOM_PVER_NAME = "P1284";
  /**
   * sdom pver variant
   */
  private static final String SDOM_PVER_VARIANT = "I10R0";

  /**
   * sdom pver revision
   */
  private static final Long SDOM_PVER_REVISION = 0L;

  private static final Long INVALID_PIDC_VERSION_ID = -872364L;
  private static final String INVALID_SDOM_PVER_NAME = "invalid_sdom_pver_name_12345";
  private static final String INVALID_SDOM_VARIANT = "invalid_sdom_pver_variant_123";
  private static final Long INVALID_SDOM_REVISION = -98234L;

  private final String currentTimeStr = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_17);

  /**
   * A2l file path for test
   */
  private final String testA2LFilePath = getUserTempDir() +
      A2L_FILE_NAME.substring(0, A2L_FILE_NAME.lastIndexOf(".a2l")) + "_" + this.currentTimeStr + ".a2l";


  /**
   * Delete the Temp A2L file, if created for the test
   *
   * @throws IOException error during file deletion
   */
  @After
  public void deleteTempA2L() throws IOException {
    File tempA2lFile = new File(this.testA2LFilePath);

    if (tempA2lFile.exists()) {
      // Delete File
      Files.delete(tempA2lFile.toPath());
    }
  }


  /**
   * @throws ApicWebServiceException exception
   * @throws IOException exception
   */
  @Test
  public void testUploadA2l() throws ApicWebServiceException, IOException {
    // create new a2l file
    createNewA2lFile(false);
    assertTrue(MSG_NEW_A2L_GENERATED_FOR_TEST, new File(this.testA2LFilePath).exists());

    A2LFileUploadServiceClient client = new A2LFileUploadServiceClient();
    PidcA2l pidcA2l = client.uploadA2LFile(this.testA2LFilePath, PIDC_VERSION_ID, SDOM_PVER_NAME, SDOM_PVER_VARIANT,
        SDOM_PVER_REVISION);

    assertNotNull("PIDC A2L returned", pidcA2l);
    assertEquals("Project Id is equal", PROJECT_ID, pidcA2l.getProjectId());
    validatePidcA2l(pidcA2l);
  }


  /**
   * Null SDOM PVER Name
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from service
   */

  @Test
  public void testUploadA2lNullSdomPverName() throws ApicWebServiceException, IOException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("SDOM PVER Name cannot be null.");
    A2LFileUploadServiceClient client = new A2LFileUploadServiceClient();
    client.uploadA2LFile(this.testA2LFilePath, PIDC_VERSION_ID, null, SDOM_PVER_VARIANT, SDOM_PVER_REVISION);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  /**
   * Null Sdom Pver Variant
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from service
   */

  @Test
  public void testUploadA2lNullSdomPverVariant() throws ApicWebServiceException, IOException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("SDOM PVER Variant cannot be null.");
    A2LFileUploadServiceClient client = new A2LFileUploadServiceClient();
    client.uploadA2LFile(this.testA2LFilePath, PIDC_VERSION_ID, SDOM_PVER_NAME, null, SDOM_PVER_REVISION);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Null Sdom Pver revision
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from service
   */
  @Test
  public void testUploadA2lNullSdomPverRevision() throws ApicWebServiceException, IOException {
    createNewA2lFile(false);
    assertTrue(MSG_NEW_A2L_GENERATED_FOR_TEST, new File(this.testA2LFilePath).exists());

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("SDOM PVER Revision cannot be null.");

    new A2LFileUploadServiceClient().uploadA2LFile(this.testA2LFilePath, PIDC_VERSION_ID, SDOM_PVER_NAME,
        SDOM_PVER_VARIANT, null);
  }

  /**
   * Null pidc version
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from service
   */
  @Test
  public void testUploadA2lNullPidcVersion() throws ApicWebServiceException, IOException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Pidc version id cannot be null.");

    new A2LFileUploadServiceClient().uploadA2LFile(this.testA2LFilePath, null, SDOM_PVER_NAME, SDOM_PVER_VARIANT,
        SDOM_PVER_REVISION);
    fail(EXPECTED_MESSAGE_NOT_THROWN);

  }

  /**
   * Invalid pidc version
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from service
   */
  @Test
  public void testUploadA2lInvalidPidcVersion() throws ApicWebServiceException, IOException {
    createNewA2lFile(false);
    assertTrue(MSG_NEW_A2L_GENERATED_FOR_TEST, new File(this.testA2LFilePath).exists());

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + INVALID_PIDC_VERSION_ID + "' is invalid for PIDC Version");

    new A2LFileUploadServiceClient().uploadA2LFile(this.testA2LFilePath, INVALID_PIDC_VERSION_ID, SDOM_PVER_NAME,
        SDOM_PVER_VARIANT, SDOM_PVER_REVISION);
  }

  /**
   * Null A2l file path
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from service
   */
  @Test
  public void testUploadA2lNullA2lFilePath() throws ApicWebServiceException, IOException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2L File path cannot be null.");

    new A2LFileUploadServiceClient().uploadA2LFile(null, PIDC_VERSION_ID, SDOM_PVER_NAME, SDOM_PVER_VARIANT,
        SDOM_PVER_REVISION);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Upload Existing a2l
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from service
   */
  @Test
  public void testUploadA2lExisting() throws ApicWebServiceException, IOException {
    PidcVersion pidcVersion = new PidcVersionServiceClient().getById(PIDC_VERSION_ID);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown
        .expectMessage("The A2L file is already assigned to the pidc version - " + pidcVersion.getVersionName() + "!");

    new A2LFileUploadServiceClient().uploadA2LFile(SOURCE_A2L_FILE_PATH, PIDC_VERSION_ID, SDOM_PVER_NAME,
        SDOM_PVER_VARIANT, SDOM_PVER_REVISION);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Invalid CPU Type
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from service
   */
  @Test
  public void testUploadA2lInvalidCPUType() throws ApicWebServiceException, IOException {
    // create new a2l file
    createNewA2lFile(true);
    assertTrue(MSG_NEW_A2L_GENERATED_FOR_TEST, new File(this.testA2LFilePath).exists());

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Error occurred from A2L Loader - Not a valid file for loading (INVALID_CPU)");

    new A2LFileUploadServiceClient().uploadA2LFile(this.testA2LFilePath, PIDC_VERSION_ID, SDOM_PVER_NAME,
        SDOM_PVER_VARIANT, SDOM_PVER_REVISION);
  }


  /**
   * Invalid a2l file
   *
   * @throws ApicWebServiceException exception from service
   * @throws IOException exception from service
   */
  @Test
  public void testUploadA2lInvalidFile() throws ApicWebServiceException, IOException {
    String invalidA2lFilePath = TESTDATA_ROOT_DIR + File.separator + "cdr" + File.separator + "acctl_demand_lab_1.lab";

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("The A2l file is in wrong format.");

    new A2LFileUploadServiceClient().uploadA2LFile(invalidA2lFilePath, PIDC_VERSION_ID, SDOM_PVER_NAME,
        SDOM_PVER_VARIANT, SDOM_PVER_REVISION);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  /**
   * @param PidcA2l ret
   * @throws ApicWebServiceException
   */
  private void validatePidcA2l(final PidcA2l pidcA2l) throws ApicWebServiceException {
    PidcA2lServiceClient pidcA2lClient = new PidcA2lServiceClient();

    Map<Long, PidcA2l> pidcA2lMap = pidcA2lClient.getA2LFileBySdom(PIDC_VERSION_ID, SDOM_PVER_NAME);
    assertTrue(CommonUtils.isNotEmpty(pidcA2lMap) && pidcA2lMap.containsKey(pidcA2l.getId()));

    Map<Long, PidcA2lFileExt> pidcA2lExtMap = pidcA2lClient.getAllA2lByPidc(PROJECT_ID);
    assertTrue(CommonUtils.isNotEmpty(pidcA2lExtMap) && pidcA2lExtMap.containsKey(pidcA2l.getA2lFileId()) &&
        pidcA2lExtMap.get(pidcA2l.getA2lFileId()).getPidcA2l().equals(pidcA2l));

    assertEquals("PIDC Version Id is equal", PIDC_VERSION_ID, pidcA2l.getPidcVersId());
    assertEquals("SDOM PVER NAME is equal", SDOM_PVER_NAME, pidcA2l.getSdomPverName());
    assertEquals("Created User is equal", "BNE4COB", pidcA2l.getCreatedUser());
    assertNull("VCDM A2L Name is null", pidcA2l.getVcdmA2lName());
    assertNull("VCDM A2L Date is null", pidcA2l.getVcdmA2lDate());
    assertTrue("Is Active", pidcA2l.isActive());
    assertFalse("WP mappings not available", pidcA2l.isWpParamPresentFlag());
    assertFalse("Active WP Defn Version with WP mappings not available", pidcA2l.isActiveWpParamPresentFlag());
  }


  /**
   * Create new file with a dummy comment to make the file unique
   */
  private void createNewA2lFile(final boolean invalidCpuType) {

    try (FileReader in = new FileReader(new File(SOURCE_A2L_FILE_PATH));
        BufferedReader br = new BufferedReader(in);
        FileWriter out = new FileWriter(new File(this.testA2LFilePath));
        BufferedWriter bw = new BufferedWriter(out)) {

      // add dummy comment
      bw.write("/* Current time for test case: " + this.currentTimeStr + "*/\n");

      String line;
      while ((line = br.readLine()) != null) {

        if (invalidCpuType && line.trim().startsWith("CPU_TYPE")) {
          bw.write("CPU_TYPE \"kjhasdf\"");
          bw.write("\n");

          continue;
        }

        bw.write(line);
        bw.write("\n");
      }
      LOG.debug("Temporary A2L file created for test : {}", this.testA2LFilePath);
    }
    catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }

  }


}
