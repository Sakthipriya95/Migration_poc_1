/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVcdmTransferInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;

/**
 * @author IKI1COB
 */
public class PidcVcdmTransferServiceClientTest extends AbstractRestClientTest {

  /**
   * User for vCDM transfer tests
   */
  private static final String PIDC_VCDM_TFR_TEST_USER = "HFZ2SI";

  /**
   * Valid APRJ name
   */
  private static final String APRJ_NAME_VALID = "X_Test_Hn_PIDC_Demonstrator";

  /**
   * Invalid APRJ name
   */
  private static final String APRJ_NAME_INVALID = "##";

  /**
   * PIDC : X_Testcustomer->Gasoline Engine->PC - Passenger Car->ME(D)17->PIDC_APRJ_TFR_01_Jenkins_Tests
   */
  private static final Long PIDC_ID_01 = 2863011729L;

  /**
   * PIDC : X_Testcustomer->Gasoline Engine->PC - Passenger Car->ME(D)17->PIDC_APRJ_TFR_02_Jenkins_Tests
   */
  private static final Long PIDC_ID_02 = 2863011739L;

  /**
   * PIDC : X_Testcustomer->Gasoline Engine->PC - Passenger Car->ME(D)17->PIDC_APRJ_TFR_03_Jenkins_Tests
   */
  private static final Long PIDC_ID_03 = 3170133596L;

  /**
   * PIDC : X_Testcustomer->Gasoline Engine->PC - Passenger Car->ME(D)17->PIDC_APRJ_TFR_05_Jenkins_Tests_Invalid_APRJ
   */
  private static final Long PIDC_ID_04 = 19089577779L;

  /**
   * PIDC : Ducati->Gasoline Engine->2W - 2 wheeler->ME(D)17->ducati_pd
   */
  private static final long PIDC_ID_MISSING_APRJ = 2259385880L;

  /**
   * PIDC : X_Testcustomer->Gasoline Engine->PC - Passenger Car->ME(D)17->PIDC_APRJ_TFR_04_Jenkins_Tests
   */
  private static final Long PIDC_ID_DUP_VARATTRVALS = 3902598579L;
  /**
   * PIDC : X_Testcustomer->Diesel Engine->CV - Commercial Vehicle->EDC17->test_cns_refresh9
   */
  private static final Long PIDC_ID_TFR_SUB_VAR = 2995515637L;

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#findAPRJId(String)}.
   *
   * @throws ApicWebServiceException web server error
   */
  @Test
  public void testfindAPRJId() throws ApicWebServiceException {
    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    String ret = client.findAPRJId(APRJ_NAME_VALID);
    assertFalse("Response should not be null or empty", (ret == null) || (ret.isEmpty()));
    assertEquals("Aprj Id is equal", String.valueOf(21168051), ret);
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#findAPRJId(String)}.<BR>
   * Negative test, try with an invalid APRJ name
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testfindAPRJIdNegative() throws ApicWebServiceException {
    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    String ret = client.findAPRJId(APRJ_NAME_INVALID);
    assertTrue("Response should be null or empty", (ret == null) || (ret.isEmpty()));
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * PIDC without variants
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   * @throws IcdmException date formatting error
   */
  @Test
  public void testTransferPidcWithoutVariantsNegative()
      throws ApicWebServiceException, PasswordNotFoundException, IcdmException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Transfer to vCDM cannot be done since there are no variants in PIDC");

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser(PIDC_VCDM_TFR_TEST_USER));

    client.transferPidc(createInputForTestUser(PIDC_ID_01));
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * PIDC without variants
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   * @throws IcdmException date formatting error
   */
  @Test
  public void testTransferPidcWithDeletedVariantsNegative()
      throws ApicWebServiceException, PasswordNotFoundException, IcdmException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Transfer to vCDM cannot be done since all variants are deleted in PIDC");

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser(PIDC_VCDM_TFR_TEST_USER));

    client.transferPidc(createInputForTestUser(PIDC_ID_03));
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * PIDC With variants
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   * @throws IcdmException date formatting error
   */
  @Test
  public void testTransferPidcWithVariants() throws ApicWebServiceException, PasswordNotFoundException, IcdmException {
    Pidc pidcBefore = new PidcServiceClient().getById(PIDC_ID_02);

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser(PIDC_VCDM_TFR_TEST_USER));

    Pidc pidcAfter = client.transferPidc(createInputForTestUser(PIDC_ID_02));

    LOG.info("Transfer result : {}", CommonUtils.isNotNull(pidcAfter));
    assertNotNull("Transfer user not null", pidcAfter.getVcdmTransferUser());
    validatePidcPropsAfterTfr(pidcBefore, pidcAfter);
  }

  /**
   * @param pidcBefore
   * @param pidcAfter
   * @throws IcdmException
   */
  private void validatePidcPropsAfterTfr(final Pidc pidcBefore, final Pidc pidcAfter) throws IcdmException {
    LOG.info("  Project info before \t: vCDM Transfer user = {} \t, transfer date = {}",
        pidcBefore.getVcdmTransferUser(), pidcBefore.getVcdmTransferDate());
    LOG.info("  Project info after \t: vCDM Transfer user = {} \t, transfer date = {}", pidcAfter.getVcdmTransferUser(),
        pidcAfter.getVcdmTransferDate());

    assertNotNull("Transfer date not null", pidcAfter.getVcdmTransferDate());

    assertEquals("Transfer user check", PIDC_VCDM_TFR_TEST_USER, pidcAfter.getVcdmTransferUser());

    String dateBforeStr =
        pidcBefore.getVcdmTransferDate() == null ? "1980-01-01 00:00:00 000" : pidcBefore.getVcdmTransferDate();
    Date dateBfore = DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, dateBforeStr).getTime();

    Date dateAfter =
        DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, pidcAfter.getVcdmTransferDate()).getTime();
    assertEquals("Transfer date check", -1, ApicUtil.compare(dateBfore, dateAfter));
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * Negative test, for validating error when service called with null PIDC ID as input
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   */
  @Test
  public void testTransferPidcNullPidcId() throws ApicWebServiceException, PasswordNotFoundException {
    this.thrown.expect(ApicWebServiceException.class);

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser(PIDC_VCDM_TFR_TEST_USER));

    // null PIDC ID
    this.thrown.expectMessage("Project ID Card with ID 'null' not found");
    client.transferPidc(createInputForTestUser(null));
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * Negative test, for validating error service called with invalid PIDC ID as input
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   */
  @Test
  public void testTransferPidcInvalidPidcId() throws ApicWebServiceException, PasswordNotFoundException {
    this.thrown.expect(ApicWebServiceException.class);

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser(PIDC_VCDM_TFR_TEST_USER));

    // Invalid PIDC ID
    this.thrown.expectMessage("Project ID Card with ID '-1' not found");
    client.transferPidc(createInputForTestUser(-1L));
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * Negative test, for validating error service called with PIDC ID as input for which vCDM project is not available.
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   */
  @Test
  public void testTransferPidcForUnavailablevCDM() throws ApicWebServiceException, PasswordNotFoundException {
    this.thrown.expect(ApicWebServiceException.class);

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser(PIDC_VCDM_TFR_TEST_USER));

    // Invalid PIDC ID
    this.thrown.expectMessage(
        "Could not find a vCDM project with the given APRJ name 'PIDC_APRJ_TFR_Invalid_APRJ' configured in the PIDC");
    client.transferPidc(createInputForTestUser(PIDC_ID_04));
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * Negative test, for validating error when multiple variants with same value combination are found
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   */
  @Test
  public void testTransferPidcDuplicateVariantAttrVals() throws ApicWebServiceException, PasswordNotFoundException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(
        "Error(s) occured while transferring to vCDM : Unable to set state of product attributes: The following Product Keys have duplicate attribute values: [001_Samand_EFD_EU5_MT_BT01, 001_Samand_EFD_EU5_MT] Please modify the attribute values or remove the defective Product Keys.");

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser(PIDC_VCDM_TFR_TEST_USER));

    // service call throws exception
    client.transferPidc(createInputForTestUser(PIDC_ID_DUP_VARATTRVALS));
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * Negative test, for validating error when APRJ attribute is not set in PIDC
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   */
  @Test
  public void testTransferPidcMissingAprj() throws ApicWebServiceException, PasswordNotFoundException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown
        .expectMessage("Transfer to vCDM cannot be done since the APRJ name attribute is not set in the project");

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser(PIDC_VCDM_TFR_TEST_USER));

    // service call throws exception
    client.transferPidc(createInputForTestUser(PIDC_ID_MISSING_APRJ));
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * Negative test, for validating error when invalid enc password is given
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   */
  @Test
  public void testTransferInvalidEncPwd() throws ApicWebServiceException, PasswordNotFoundException {
    PidcVcdmTransferInput input = createInputForTestUser(PIDC_ID_MISSING_APRJ);
    input.setEncPwd(new PasswordService().getPassword("ICDM.LDAP_MGR_PASSWORD"));

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("EASEE Service login failed for user");

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser("BNE4COB"));

    // service call throws exception
    client.transferPidc(input);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  private PidcVcdmTransferInput createInputForTestUser(final Long pidcId)
      throws PasswordNotFoundException, ApicWebServiceException {
    PidcVcdmTransferInput ret = new PidcVcdmTransferInput();
    ret.setPidc(new PidcServiceClient().getById(pidcId));
    ret.setEncPwd(new PasswordService().getPassword("ICDM.LDAP_MGR_PASSWORD"));

    return ret;
  }

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#transferPidc(PidcVcdmTransferInput) }.
   * <p>
   * PIDC With Sub variants Attribute
   *
   * @throws ApicWebServiceException web service error
   * @throws PasswordNotFoundException password service error
   * @throws IcdmException date formatting error
   */
  @Test
  public void testTransferPidcWithSubVariants()
      throws ApicWebServiceException, PasswordNotFoundException, IcdmException {

    Pidc pidcBefore = new PidcServiceClient().getById(PIDC_ID_TFR_SUB_VAR);

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    client.setClientConfiguration(createClientConfigTestUser(PIDC_VCDM_TFR_TEST_USER));
    PidcVcdmTransferInput input = createInputForTestUser(PIDC_ID_TFR_SUB_VAR);

    // service call throws exception
    Pidc pidcAfter = client.transferPidc(input);
    assertNotNull("Transfer result not null", pidcAfter);

    LOG.info("Transfer result : {}", CommonUtils.isNotNull(pidcAfter));
    assertNotNull("Transfer user not null", pidcAfter.getVcdmTransferUser());
    validatePidcPropsAfterTfr(pidcBefore, pidcAfter);

  }
}

