/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.vcdminterface.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.apic.vcdminterface.VCDMInterfaceException;
import com.bosch.caltool.icdm.logger.A2LLogger;
import com.bosch.caltool.icdm.logger.EASEELogger;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;
import com.bosch.caltool.security.Decryptor;
import com.vector.easee.application.cdmservice.IParameterValue;


/**
 * @author BNE4COB
 */
public class VCDMInterfaceTest extends AbstractVcdmInterfaceTest {

  /**
   *
   */
  private static final String EXPECTED_MESSAGE_NOT_THROWN = "Expected message not thrown";

  private static final int HEX_FILE_LOADING_VERSION_NUM = 20598929;

  private static final String DST_ID = "20621103";

  /**
   * HEX_M1764VDAC866_M861_280214_cleaned.hex
   */
  private static final String ARTIFACT_HEX_VERSION_NUM = "18681185";

  /**
   * 159_HONDA_ME17956_2SV_4cyl_2.0l_PFI_FF_CR7511_internal.A2L
   */
  private static final String A2L_VERSION_NUM = "19593091";

  /**
   * Valid APRJ name
   */
  private static final String APRJ_NAME_VALID = "X_Test_Hn_PIDC_Demonstrator";
  private final static String APRJ_NAME_INVALID = "##";

  private static VCDMInterface vcdmIF;

  /**
   * Initialize vCDM interface
   *
   * @throws Exception error from vCDM interface
   */
  @BeforeClass
  public static void initInterface() throws Exception {
    vcdmIF = new VCDMInterface(EASEELogger.getInstance(), A2LLogger.getInstance());
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#getSerializedA2LFileInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
   *
   * @throws Exception error from vCDM interface
   */
  @Test
  public void testGetSerializedA2LFileInfo() throws Exception {
    String serA2lFileName = getRunId();

    TESTER_LOGGER.debug("Serialized A2L retrieval : first retrieval - downloads from vCDM --------");
    File serFile = vcdmIF.getSerializedA2LFileInfo("159_HONDA_ME17956_2SV_4cyl_2.0l_PFI_FF_CR7511_internal.A2L",
        getTempPathWithSep(), serA2lFileName, A2L_VERSION_NUM);

    assertNotNull("File is not null", serFile);
    assertEquals("Serialized file name", serA2lFileName + ".ser", serFile.getName());
    assertTrue("SER file exists", serFile.exists());

    TESTER_LOGGER.debug("Serialized A2L retrieval : Invoke again - download will not happen ------");
    serFile = vcdmIF.getSerializedA2LFileInfo("159_HONDA_ME17956_2SV_4cyl_2.0l_PFI_FF_CR7511_internal.A2L",
        getTempPathWithSep(), serA2lFileName, A2L_VERSION_NUM);

    assertNotNull("File is not null", serFile);
    assertEquals("Serialized file name", serA2lFileName + ".ser", serFile.getName());
    assertTrue("SER file exists", serFile.exists());
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#getvCDMPSTArtifacts(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor, java.lang.String)}.
   *
   * @throws Exception vCDM error
   */
  @Test
  public void testGetvCDMPSTArtifactsStringStringIProgressMonitorString() throws Exception {
    TESTER_LOGGER.debug("Retrieving HEX file version num . {} from vCDM", ARTIFACT_HEX_VERSION_NUM);
    boolean retStatus = vcdmIF.getvCDMPSTArtifacts("TestHEX.hex", ARTIFACT_HEX_VERSION_NUM, null, getTempPathWithSep());
    assertTrue("Artifact Retrieved return", retStatus);
    assertTrue("Artifact Downloaded", new File(getTempPathWithSep() + "TestHEX.hex").exists());
  }


  /**
   * Test method for
   * {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#getvCDMPSTArtifacts(java.lang.String, java.lang.String, java.lang.String)}.
   *
   * @throws Exception vCDM error
   */
  @Test
  public void testGetvCDMPSTArtifactsStringStringString() throws Exception {
    TESTER_LOGGER.debug("Retrieving HEX file version num . {} from vCDM", ARTIFACT_HEX_VERSION_NUM);
    boolean retStatus = vcdmIF.getvCDMPSTArtifacts("TestHEX.hex", ARTIFACT_HEX_VERSION_NUM, getTempPathWithSep());
    assertTrue("Artifact Retrieved return", retStatus);
    assertTrue("Artifact Downloaded", new File(getTempPathWithSep() + "TestHEX.hex").exists());
  }

  /**
   * Test method for {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#getAPRJID(java.lang.String)}.
   *
   * @throws Exception vCDM error
   */
  @Test
  public void testGetAPRJID() throws Exception {
    String ret = vcdmIF.getAPRJID(APRJ_NAME_VALID);

    TESTER_LOGGER.debug("APRJ ID returned from vCDM = {}", ret);

    assertFalse("Response should not be null or empty", (ret == null) || (ret.isEmpty()));
    assertEquals("Aprj Id is equal", String.valueOf(21168051), ret);
  }

  /**
   * Test method for {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#getAPRJID(java.lang.String)}.
   *
   * @throws Exception vCDM error
   */
  @Test
  public void testGetAPRJIDInvalid() throws Exception {
    String ret = vcdmIF.getAPRJID(APRJ_NAME_INVALID);

    TESTER_LOGGER.debug("APRJ ID returned from vCDM = {}", ret);

    assertTrue("Response should be null or empty", (ret == null) || ret.isEmpty());
  }

  /**
   * Test method for {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#loadHexFile(int)}.
   *
   * @throws VCDMInterfaceException error from vCDM interface
   */
  @Test
  public void testLoadHexFile() throws VCDMInterfaceException {
    TESTER_LOGGER.debug("Fetching HEX file version num {} from vCDM", HEX_FILE_LOADING_VERSION_NUM);

    String hexDownloadPath = vcdmIF.loadHexFile(HEX_FILE_LOADING_VERSION_NUM);

    assertFalse("Artifact Retrieved return", (hexDownloadPath == null) || hexDownloadPath.isEmpty());
    assertTrue("Artifact Downloaded", new File(hexDownloadPath).exists());
  }

  /**
   * Test method for {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#getParameterValues(java.lang.String)}.
   *
   * @throws VCDMInterfaceException vCDM interface error
   * @throws PasswordNotFoundException error while fetching user's password
   */
  @Test
  public void testGetParameterValues() throws VCDMInterfaceException, PasswordNotFoundException {
    TESTER_LOGGER.debug("Fetching paramter values of the DST '{}' from vCDM", DST_ID);

    String pswdEnc = new PasswordService().getPassword("ICDM.LDAP_MGR_PASSWORD");
    final String pswd = Decryptor.getInstance().decrypt(pswdEnc, AUT_LOGGER);

    VCDMInterface vcdmUserIF = new VCDMInterface("HFZ2SI", pswd, EASEELogger.getInstance());
    Set<IParameterValue> paramValSet = vcdmUserIF.getParameterValues(DST_ID);
    assertFalse("Parameter Value retrieved", (paramValSet == null) || paramValSet.isEmpty());

    TESTER_LOGGER.debug("Param Value count  = {}", paramValSet.size());

    IParameterValue paramValFirst = paramValSet.iterator().next();

    TESTER_LOGGER.debug("Scalar Value = {}", paramValFirst.getCalibrationRemark());
  }


  /**
   * Test method for {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#getParameterValues(java.lang.String)}.
   *
   * @throws VCDMInterfaceException vCDM interface error
   * @throws PasswordNotFoundException error while fetching user's password
   */
  @Test
  public void testGetParameterValuesIncorrectInterface() throws VCDMInterfaceException, PasswordNotFoundException {
    TESTER_LOGGER.debug("Test invoking getParameterValues() with incorrect constructor");

    this.thrown.expect(VCDMInterfaceException.class);
    this.thrown.expectMessage("vCDM interface not initialized");

    vcdmIF.getParameterValues(DST_ID);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#getVersionAttributes(java.lang.String)}.
   *
   * @throws VCDMInterfaceException vCDM interface error
   */
  @Test
  public void testGetVersionAttributes() throws VCDMInterfaceException {
    TESTER_LOGGER.debug("Fetching A2L attributes of A2L version '{}' from vCDM", A2L_VERSION_NUM);

    Map<String, List<String>> versionAttrs = vcdmIF.getVersionAttributes(A2L_VERSION_NUM);

    assertNotNull("Version Attribute return not null", versionAttrs);

    TESTER_LOGGER.debug("Version Attribute count  = {}, Attributes = {}", versionAttrs.size(), versionAttrs);

    assertFalse("Version Attribute retrieved", versionAttrs.isEmpty());
    assertEquals("PST check", Arrays.asList("CR7511_internal"), versionAttrs.get("PST"));
    assertEquals("ORIGINAL FILE check", Arrays.asList("CR7511_internal.a2l"), versionAttrs.get("ORIGINAL FILE"));
    assertEquals("ORIGINAL DATE check", Arrays.asList("2015-11-12 17:54:17"), versionAttrs.get("ORIGINAL DATE"));
  }


  /**
   * Test method for {@link com.bosch.caltool.apic.vcdminterface.VCDMInterface#getParameterValues(java.lang.String)}.
   *
   * @throws VCDMInterfaceException vCDM interface error
   * @throws PasswordNotFoundException error while fetching user's password
   */
  @Test
  public void testVCDMInterfaceInvalidCred() throws VCDMInterfaceException, PasswordNotFoundException {
    TESTER_LOGGER.debug("Test initialization with invalid credentials");

    this.thrown.expect(VCDMInterfaceException.class);
    this.thrown.expectMessage("Web service login failed");

    new VCDMInterface("HFZ2SI", "Invalid credential", EASEELogger.getInstance());
    fail(EXPECTED_MESSAGE_NOT_THROWN);

  }

  private String getTempPathWithSep() {
    return System.getProperty("java.io.tmpdir") + File.separator;
  }


}
