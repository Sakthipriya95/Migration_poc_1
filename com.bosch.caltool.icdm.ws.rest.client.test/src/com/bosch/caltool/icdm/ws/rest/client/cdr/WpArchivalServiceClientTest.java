package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for WpArchival
 *
 * @author msp5cob
 */
public class WpArchivalServiceClientTest extends AbstractRestClientTest {


  /**
   *
   */
  private static final long INVALID_WP_ARCHIVAL_ID = -99999L;
  /**
   *
   */
  private static final String EXPECTED_EXCEPTION_NOT_THROWN = "Expected exception not thrown";
  /**
   *
   */
  private static final String A2L_FILE_NAME = "173074106A0_or.A2L";
  /**
   *
   */
  private static final String PIDC_NAME = "Prem_test_qnaire_link_2 (v1)";
  /**
   *
   */
  private static final String WP_1 = "wp_1";
  /**
  *
  */
  private static final Long RESP_ID = 1L;
  /**
   *
   */
  private static final String WORK_PACKAGE_NAME_SHOULD_BE_EQUAL = "Work Package Name should be equal";
  /**
   *
   */
  private static final String RESP_1 = "Resp_1";
  /**
   *
   */
  private static final String RESPONSIBILITY_NAME_SHOULD_BE_EQUAL = "Responsibility Name should be equal";
  /**
  *
  */
  private static final String VARIANT_ID_SHOULD_BE_EQUAL = "Variant Id Should be equal";
  /**
   *
   */
  private static final String JUNIT_TEST_1 = "Junit_Test 1";
  /**
  *
  */
  private static final String JUNIT_TEST_2 = "Junit_Test 2";
  /**
   *
   */
  private static final long VARIANT_ID = 33759425178L;
  /**
  *
  */
  private static final long PIDC_VERS_ID = 31259559181L;
  /**
   *
   */
  private static final long PIDC_A2L_ID = 31259559189L;
  /**
  *
  */
  private static final long PIDC_A2L_ID_INVALID = -1L;
  /**
  *
  */
  private static final long NO_VARIANT_ID = -1l;
  /**
  *
  */
  private static final long WP_ARCHIVAL_BASELINE_ID = -1L;
  /**
  *
  */
  private static final long WP_ARCHIVAL_BASELINE_ID_2 = -2L;

  /**
   *
   */
  private static final String ERROR_IN_WS_CALL = "Error in WS call";
  /**
   *
   */
  private static final String RESPONSE_SHOULD_NOT_BE_NULL = "Response should not be null";

  /**
   * Test method for {@link WpArchivalServiceClient#get(Long)}
   */
  @Test
  public void testGet() {
    try {
      WpArchival ret = new WpArchivalServiceClient().get(WP_ARCHIVAL_BASELINE_ID);
      assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (ret == null));
      assertEquals("BaselineName should be equal", ret.getBaselineName(), JUNIT_TEST_1);
      testCommonFields(ret);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }
  }

  /**
   * Test method for {@link WpArchivalServiceClient#getWpArchivalsMap(Set)}
   */
  @Test
  public void testGetWpArchivalsMap() {
    try {
      Set<Long> idSet = new HashSet<>();
      idSet.add(WP_ARCHIVAL_BASELINE_ID);
      idSet.add(WP_ARCHIVAL_BASELINE_ID_2);

      Map<Long, WpArchival> retMap = new WpArchivalServiceClient().getWpArchivalsMap(idSet);

      WpArchival wpArchival1 = retMap.get(WP_ARCHIVAL_BASELINE_ID);
      assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (wpArchival1 == null));
      assertEquals("BaselineName should be equal", wpArchival1.getBaselineName(), JUNIT_TEST_1);
      testCommonFields(wpArchival1);

      WpArchival wpArchival2 = retMap.get(WP_ARCHIVAL_BASELINE_ID_2);
      assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (wpArchival2 == null));
      assertEquals("BaselineName should be equal", wpArchival2.getBaselineName(), JUNIT_TEST_2);
      testCommonFields(wpArchival2);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }
  }

  /**
   * Test method for {@link WpArchivalServiceClient#getWpArchivalsMap(Set)}
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetWpArchivalsMapNegative() throws ApicWebServiceException {

    Set<Long> idSet = new HashSet<>();
    idSet.add(INVALID_WP_ARCHIVAL_ID);
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("WpArchival Model with ID '" + INVALID_WP_ARCHIVAL_ID + "' not found");

    new WpArchivalServiceClient().getWpArchivalsMap(idSet);

    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link WpArchivalServiceClient#getBaselinesForPidcA2l(Long)}
   */
  @Test
  public void getBaselinesForPidcA2l() {
    try {
      Set<WpArchival> ret = new WpArchivalServiceClient().getBaselinesForPidcA2l(PIDC_A2L_ID);
      assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (ret == null));
      WpArchival wpArchival = ret.iterator().next();
      testCommonFields(wpArchival);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }
  }

  /**
   * Test method for {@link WpArchivalServiceClient#getBaselinesForPidcA2l(Long)}
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void getBaselinesForPidcA2lNegative() throws ApicWebServiceException {
    this.thrown.expectMessage("Input Parameter 'pidca2lid' is mandatory");
    new WpArchivalServiceClient().getBaselinesForPidcA2l(null);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link WpArchivalServiceClient#getBaselinesForPidcA2l(Long)}
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void getBaselinesForPidcA2lInvalid() throws ApicWebServiceException {
    this.thrown.expectMessage("No Work Package Baselines Found for given PIDC A2L ID : " + PIDC_A2L_ID_INVALID);
    new WpArchivalServiceClient().getBaselinesForPidcA2l(PIDC_A2L_ID_INVALID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testWpArchivalPidcNode() throws ApicWebServiceException {
    String nodeName = "ACTIVE_PIDC_VERSION";

    Set<WpArchival> ret =
        new WpArchivalServiceClient().getFilteredBaselinesForPidc(PIDC_VERS_ID, null, null, null, null, nodeName);

    WpArchival wpArchival = ret.iterator().next();
    assertEquals("PidcVersId should be equal", wpArchival.getPidcVersId(), (Long) 31259559181L);
  }

  /**
   *
   */
  @Test
  public void testWpArchivalPidcA2lNode() {
    String nodeName = "PIDC_A2L";

    try {
      Set<WpArchival> ret = new WpArchivalServiceClient().getFilteredBaselinesForPidc(PIDC_VERS_ID, PIDC_A2L_ID, null,
          null, null, nodeName);

      WpArchival wpArchival = ret.iterator().next();
      testCommonFields(wpArchival);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }

  }

  /**
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testWpArchivalRespWpNode() throws ApicWebServiceException {
    String nodeName = "REV_RES_WP_GRP_NODE";

    try {
      Set<WpArchival> ret =
          new WpArchivalServiceClient().getFilteredBaselinesForPidc(null, null, VARIANT_ID, null, WP_1, nodeName);

      WpArchival wpArchival = ret.iterator().next();
      testCommonFields(wpArchival);
      assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) VARIANT_ID);
      assertEquals(WORK_PACKAGE_NAME_SHOULD_BE_EQUAL, wpArchival.getWpName(), WP_1);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }

  }

  /**
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testWpArchivalPidcA2lIWpNode() throws ApicWebServiceException {
    String nodeName = "PIDC_A2L_WP_NODE";

    try {
      Set<WpArchival> ret = new WpArchivalServiceClient().getFilteredBaselinesForPidc(null, PIDC_A2L_ID, VARIANT_ID,
          RESP_ID, WP_1, nodeName);

      WpArchival wpArchival = ret.iterator().next();
      testCommonFields(wpArchival);
      assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) VARIANT_ID);
      assertEquals(RESPONSIBILITY_NAME_SHOULD_BE_EQUAL, wpArchival.getRespName(), RESP_1);
      assertEquals(WORK_PACKAGE_NAME_SHOULD_BE_EQUAL, wpArchival.getWpName(), WP_1);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }

  }

  /**
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testWpArchivalPidcA2lIWpNodeNoVar() throws ApicWebServiceException {
    String nodeName = "PIDC_A2L_WP_NODE";

    try {
      Set<WpArchival> ret = new WpArchivalServiceClient().getFilteredBaselinesForPidc(null, PIDC_A2L_ID, NO_VARIANT_ID,
          RESP_ID, WP_1, nodeName);

      WpArchival wpArchival = ret.iterator().next();
      testCommonFields(wpArchival);
      assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) NO_VARIANT_ID);
      assertEquals(RESPONSIBILITY_NAME_SHOULD_BE_EQUAL, wpArchival.getRespId(), RESP_ID);
      assertEquals(WORK_PACKAGE_NAME_SHOULD_BE_EQUAL, wpArchival.getWpName(), WP_1);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }

  }

  /**
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testWpArchivalPidcA2lIRespNode() throws ApicWebServiceException {
    String nodeName = "PIDC_A2L_RESPONSIBILITY_NODE";
    try {
      Set<WpArchival> ret = new WpArchivalServiceClient().getFilteredBaselinesForPidc(null, PIDC_A2L_ID, VARIANT_ID,
          RESP_ID, null, nodeName);

      WpArchival wpArchival = ret.iterator().next();
      testCommonFields(wpArchival);
      assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) VARIANT_ID);
      assertEquals(RESPONSIBILITY_NAME_SHOULD_BE_EQUAL, wpArchival.getRespId(), RESP_ID);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }

  }

  /**
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testWpArchivalPidcA2lIRespNodeNoVar() throws ApicWebServiceException {
    String nodeName = "PIDC_A2L_RESPONSIBILITY_NODE";
    try {
      Set<WpArchival> ret = new WpArchivalServiceClient().getFilteredBaselinesForPidc(null, PIDC_A2L_ID, VARIANT_ID,
          RESP_ID, null, nodeName);

      WpArchival wpArchival = ret.iterator().next();
      testCommonFields(wpArchival);
      assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) VARIANT_ID);
      assertEquals(RESPONSIBILITY_NAME_SHOULD_BE_EQUAL, wpArchival.getRespId(), RESP_ID);
    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
    }

  }

  /**
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetFilteredQueryForPidcNoA2lRespAndVariantId() throws ApicWebServiceException {
    String nodeName = "PIDC_A2L_VAR_NODE";

    Set<WpArchival> ret =
        new WpArchivalServiceClient().getFilteredBaselinesForPidc(null, PIDC_A2L_ID, VARIANT_ID, null, null, nodeName);

    WpArchival wpArchival = ret.iterator().next();
    testCommonFields(wpArchival);
    assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) VARIANT_ID);

  }

  /**
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetFilteredQueryForPidcNoA2lNoVariantId() throws ApicWebServiceException {
    String nodeName = "RVW_RESPONSIBILITY_NODE";

    Set<WpArchival> ret =
        new WpArchivalServiceClient().getFilteredBaselinesForPidc(null, null, VARIANT_ID, RESP_ID, null, nodeName);

    WpArchival wpArchival = ret.iterator().next();
    assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) VARIANT_ID);
    assertEquals(RESPONSIBILITY_NAME_SHOULD_BE_EQUAL, wpArchival.getRespId(), RESP_ID);
  }

  /**
   * Sara new 2
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetFilteredQueryForPidcNoA2lVariantIdOnly() throws ApicWebServiceException {
    String nodeName = "RVW_WORKPACAKGES_TITLE_NODE";

    Set<WpArchival> ret =
        new WpArchivalServiceClient().getFilteredBaselinesForPidc(PIDC_VERS_ID, null, VARIANT_ID, null, null, nodeName);

    WpArchival wpArchival = ret.iterator().next();
    testCommonFields(wpArchival);
    assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) VARIANT_ID);

  }


  /**
   * Sara new 1
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetWpArchivalForNoVariantWp() throws ApicWebServiceException {
    String nodeName = "RVW_RESP_WP_NODE";

    Set<WpArchival> ret =
        new WpArchivalServiceClient().getFilteredBaselinesForPidc(null, null, VARIANT_ID, RESP_ID, WP_1, nodeName);

    WpArchival wpArchival = ret.iterator().next();
    assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) VARIANT_ID);
    assertEquals(RESPONSIBILITY_NAME_SHOULD_BE_EQUAL, wpArchival.getRespId(), RESP_ID);
    assertEquals(WORK_PACKAGE_NAME_SHOULD_BE_EQUAL, wpArchival.getWpName(), WP_1);

  }

  /**
   * Sara new 1
   *
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testGetWpArchivalForNoVarResp() throws ApicWebServiceException {
    String nodeName = "RVW_RESPONSIBILITIES_TITLE_NODE";

    Set<WpArchival> ret = new WpArchivalServiceClient().getFilteredBaselinesForPidc(PIDC_VERS_ID, null, NO_VARIANT_ID,
        null, null, nodeName);

    WpArchival wpArchival = ret.iterator().next();
    testCommonFields(wpArchival);
    assertEquals(VARIANT_ID_SHOULD_BE_EQUAL, wpArchival.getVariantId(), (Long) NO_VARIANT_ID);

  }

  /**
   * Test method for {@link WpArchivalServiceClient#getFilteredBaselinesForPidc(Long,Long,Long,Long,String, String)}
   *
   * @throws ApicWebServiceException Web Service Exception
   */
  @Test
  public void getFilteredBaselinesForPidcA2lInvalid() throws ApicWebServiceException {
    this.thrown.expectMessage("No Work Package Baselines Found for given input parameters");
    new WpArchivalServiceClient().getFilteredBaselinesForPidc(null, PIDC_A2L_ID_INVALID, 11223L, null, null,
        "RVW_WORKPACAKGES_TITLE_NODE");
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * test output data
   */
  private void testCommonFields(final WpArchival obj) {

    assertEquals("PidcVersId should be equal", obj.getPidcVersId(), (Long) 31259559181L);
    assertEquals("PidcVersFullname should be equal", obj.getPidcVersFullname(), PIDC_NAME);

    assertEquals("PidcA2lId should be equal", obj.getPidcA2lId(), (Long) PIDC_A2L_ID);
    assertEquals("A2lFilename should be equal", obj.getA2lFilename(), A2L_FILE_NAME);
  }

}
