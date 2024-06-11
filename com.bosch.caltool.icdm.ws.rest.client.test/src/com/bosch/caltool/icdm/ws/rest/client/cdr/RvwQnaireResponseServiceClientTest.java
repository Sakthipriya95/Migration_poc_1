/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespUpdationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVarRespWpLink;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class RvwQnaireResponseServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String NOT_FOUND = "' not found";
  /**
   *
   */
  private static final String EXPECTED_EXCEPTION_NOT_THROWN = "Expected Exception not thrown";
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1)
   */
  private static final long PIDC_VER_ID = 22309004031L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1)
   */
  private static final long PIDC_ID = 22309004029L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1)
   */
  private static final long A2L_WP_ID = 22453516007L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1)
   */
  private static final long A2L_RESPONSE_ID = 22309004041L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1), qnaire - <NO-VARIANT>
   * -> Robert Bosch -> Test_1 -> General Questions (Version 2.1)
   */
  private static final long QUESTION_RESP_ID_1 = 22456130545L;
  private static final String QUESTION_RESP_NAME_1 = "General Questions (Version 2.1)";
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1), qnaire - <NO-VARIANT>
   * -> Robert Bosch -> Test_1 -> Active surge damper (Version 0.1)
   */
  private static final long QUESTION_RESP_ID_2 = 22456130551L;
  private static final String QUESTION_RESP_NAME_2 = "Active surge damper (Version 0.1)";
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1), qnaire - <NO-VARIANT>
   * -> Robert Bosch -> Test_1 -> Cruise control (Version 0.4)
   */
  private static final long QUESTION_RESP_ID_3 = 22456130554L;
  private static final String QUESTION_RESP_NAME_3 = "Cruise control (Version 0.4)";

  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_WP_Dont_Modify (v1)
   */
  private static final long PIDC_VER_ID_WITH_WP = 22575662631L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_WP_Dont_Modify (v1)
   */
  private static final long PIDC_ID_WITH_WP = 22575662629L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_WP_Dont_Modify (v1)
   */
  private static final long A2L_WP_ID_WITH_WP = 22575662645L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_WP_Dont_Modify (v1)
   */
  private static final long A2L_RESPONSE_ID_WITH_WP = 22575662641L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_WP_Dont_Modify (v1), qnaire -
   * <NO-VARIANT> -> Robert Bosch -> _DEFAULT_WP_ -> General Questions (Version 2.1)
   */
  private static final long QUESTION_RESP_ID_WITH_WP_1 = 22599307796L;
  private static final String QUESTION_RESP_NAME_WITH_WP_1 = "General Questions (Version 2.1)";
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_WP_Dont_Modify (v1), qnaire -
   * <NO-VARIANT> -> Robert Bosch -> _DEFAULT_WP_ -> _Test_Aniket_2 (Version 0.1)
   */
  private static final long QUESTION_RESP_ID_WITH_WP_2 = 22599307978L;
  private static final String QUESTION_RESP_NAME_WITH_WP_2 = "_Test_Aniket_2 (Version 0.1)";
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_WP_Dont_Modify (v1), qnaire -
   * <NO-VARIANT> -> Robert Bosch -> _DEFAULT_WP_ -> _Test_Aniket_2 (Version 0.1)
   */
  private static final long QUESTION_WP_ID = 1027673015l;
  private static final String QUESTION_WP_NAME = "08/09 Diesel Diagnosis PCR system (incl. CAC)_EU/US";
  private static final String QUESTION_WP_DESC = "08/09 Diesel Diagnosis PCR system (incl. CAC)_EU/US";

  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1)
   */
  private static final long PIDC_VER_ID_WITH_VAR_LINK = 22599307802L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1)
   */
  private static final long PIDC_ID_WITH_VAR_LINK = 22648321782L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1)
   */
  private static final long A2L_WP_ID_WITH_VAR_LINK = 22622957828L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1)
   */
  private static final long A2L_RESPONSE_ID_WITH_VAR_LINK = 22599307813L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1), qnaire
   * - 00003B57_G11_B57D30O0_AWD_EU6_AT -> Customer -> Tesp_prem_1 -> General Questions (Version 2.1)
   */
  private static final long QUESTION_RESP_ID_WITH_VAR_LINK_1 = 22624674795L;
  private static final String QUESTION_RESP_NAME_WITH_VAR_LINK_1 = "General Questions (Version 2.1)";
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1), qnaire
   * - 00003B57_G11_B57D30O0_AWD_EU6_AT -> Customer -> Tesp_prem_1 -> Diesel Misfired detection_EU/US (Version 1.0)
   */
  private static final long QUESTION_RESP_ID_WITH_VAR_LINK_2 = 22648321779L;
  private static final String QUESTION_RESP_NAME_WITH_VAR_LINK_2 = "21 Diesel Misfired detection_EU/US (Version 1.0)";
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1), qnaire
   * - <00003B57_G11_B57D30O0_AWD_EU6_AT
   */
  private static final long QUESTION_VAR_LINK_ID = 22648321782L;
  private static final String QUESTION_VAR_LINK_NAME = "00003B57_G11_B57D30O0_AWD_EU6_AT";
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1), qnaire
   * - <00003B57_G11_B57D30O0_AWD_EU6_AT_Test
   */
  private static final long QUESTION_SEC_VAR_LINK_ID = 22648321783L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1), Resp -
   * Customer
   */
  private static final long QUESTION_SEC_RESP_LINK_ID = 22599307813L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_with_VAR_LINK_Dont_Modify (v1), WP -
   * Test_prem_1
   */
  private static final long QUESTION_SEC_WP_LINK_ID = 22622957828L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1)
   */
  private static final long PIDC_NO_VAR_ID = -1L;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1)
   */
  private static final long QUEST_RESP_ID = A2L_RESPONSE_ID;
  /**
   * PIDC : AUDI->Gasoline Engine->PC - Passenger Car->ME(D)17->Test_Junit_Qnaire_Dont_Modify (1)
   */
  private static final long QUEST_WP_ID = 22354431229L;
  /**
   * Invalid Quest Work Package ID
   */
  private static final long INVALID_QUEST_WP_ID = -1L;
  /**
   * Empty Response warning Message
   */
  private static final String RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY = "Response should not be null or empty";
  /**
   * User Id for Created User
   */
  private static final String USER_ID = "MSP5COB";
  /**
   * Created Date for Qnaire
   */
  private static final String CREATED_DATE = "2021-11-11 18:40:00 555";
  /**
   *
   */
  private static final long A2L_RESP_ID = 2677225632l;
  /**
   *
   */
  private static final long PID = 2230061942l;
  private static final long PIDC_VERS_ID_ACTIVE = 10827299181L;
  // X_Test_002_P866_EA288 : Version 4
  /**
   * pidc -PIDC_For_Questionnaire_Response (v1), qnaire - ACT -> Sathyamurthy, Sharavan Pravin -> Wp1_Test -> AirDvp
   */
  private static final long RVW_QNAIRE_RESPONSE_ID = 12676790728L;
  private static final long INV_RVW_QNAIRE_RESPONSE_ID = -12111147642l;

  /**
   * pidc -PIDC_For_Questionnaire_Response (v1), qnaire - ACT -> Sathyamurthy, Sharavan Pravin -> Wp1_Test -> AirDvp ->
   * Working set
   */
  private static final long RVW_QNAIRE_RESPONSE_VERS_ID = 12676790730L;
  private static final long INV_RVW_QNAIRE_RESPONSE_VERS_ID = -1l;


  /**
   * Testcase create.
   */
  @Test
  public void testUpdate() {
    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();
    try {
      RvwQnaireResponse obj = servClient.getById(RVW_QNAIRE_RESPONSE_ID);
      obj.setDeletedFlag(!obj.isDeletedFlag());
      // Invoke create method
      RvwQnaireResponse updatedObj = servClient.update(obj);

      assertNotNull("object not null", updatedObj);
      testOutput(updatedObj);
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }


  /**
   * Test method for {@link RvwQnaireResponseServiceClient#getPidcQnaireVariants(java.lang.Long)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPidcQnaireVariants() throws ApicWebServiceException {
    PidcQnaireInfo ret = new RvwQnaireResponseServiceClient().getPidcQnaireVariants(PIDC_VERS_ID_ACTIVE);

    LOG.info("VarIdVarMap size = {}", ret.getVarIdVarMap().size());
    LOG.info("NoVarQnaireInfoMap size = {}", ret.getNoVarQnaireInfoMap().size());

    assertFalse("VarIdVarMap not empty", ret.getVarIdVarMap().isEmpty());
    assertTrue("VarQnaireInfoMap empty", ret.getNoVarQnaireInfoMap().isEmpty());
  }

  /**
   * @throws ApicWebServiceException as excpetion
   */
  @Test
  public void testGetQniareRespVersByPidcVersIdAndVersId() throws ApicWebServiceException {
    CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper =
        new RvwQnaireResponseServiceClient().getQniareRespVersByPidcVersIdAndVarId(5096065840l, 5219758533l);


    for (Entry<Long, Map<Long, Set<RvwQnaireRespVersion>>> respWpQnaireRespVersEntry : cdrReportQnaireRespWrapper
        .getWpRespQnaireRespVersMap().entrySet()) {
      for (Entry<Long, Set<RvwQnaireRespVersion>> wpQnaireRespVersEntry : respWpQnaireRespVersEntry.getValue()
          .entrySet()) {
        for (RvwQnaireRespVersion qnaireRespVersion : wpQnaireRespVersEntry.getValue()) {
          LOG.info("Responsible = {},Workpackage = {},QnaireRespId = {},QnaireRespVersion Stauts = {} ",
              respWpQnaireRespVersEntry.getKey(), wpQnaireRespVersEntry.getKey(), qnaireRespVersion.getQnaireRespId(),
              qnaireRespVersion.getQnaireRespVersStatus());
        }
      }
    }
  }

  /**
   * test output data.
   *
   * @param obj the obj
   */
  private void testOutput(final RvwQnaireResponse obj) {
    assertEquals("PIDC Version ID is equal", Long.valueOf(10827299181L), obj.getPidcVersId());
    assertEquals("PIDC Variant ID is equal", Long.valueOf(12270681209L), obj.getVariantId());
    assertNotNull("Created Date is not null", obj.getCreatedDate());
    assertEquals("Created User is equal", "SAY8COB", obj.getCreatedUser());

  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetById() throws ApicWebServiceException {

    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();
    RvwQnaireResponse ret = servClient.getById(RVW_QNAIRE_RESPONSE_ID);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#getById(Long)}.Negative Test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {

    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();
    this.thrown.expectMessage("Questionnaire Response with ID '" + INV_RVW_QNAIRE_RESPONSE_ID + NOT_FOUND);
    servClient.getById(INV_RVW_QNAIRE_RESPONSE_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#getAllMappingModel(Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAllMappingModel() throws ApicWebServiceException {
    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();
    RvwQnaireResponseModel retModel = servClient.getAllMappingModel(RVW_QNAIRE_RESPONSE_VERS_ID);
    RvwQnaireResponse ret = retModel.getRvwQnrResponse();
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#validateQnaireAccess(Long,Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testvalidateQnaireAccess() throws ApicWebServiceException {
    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();
    boolean accessFlag = servClient.validateQnaireAccess(PID, A2L_RESP_ID);
    assertTrue("User having access for the  selected response node", accessFlag);
  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#getAllMappingModel(Long)}.Negative Test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAllMappingModelNegative() throws ApicWebServiceException {
    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();
    this.thrown.expectMessage("Questionnaire Response Version with ID '" + INV_RVW_QNAIRE_RESPONSE_VERS_ID + NOT_FOUND);
    servClient.getAllMappingModel(INV_RVW_QNAIRE_RESPONSE_VERS_ID);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#getWorkpackageId(Set)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWorkpackageId() throws ApicWebServiceException {
    Set<Long> quesRespIds = new HashSet<>();
    quesRespIds.add(QUESTION_RESP_ID_1);
    quesRespIds.add(QUESTION_RESP_ID_2);
    quesRespIds.add(QUESTION_RESP_ID_3);
    List<Long> workPackageId = new RvwQnaireResponseServiceClient().getWorkpackageId(quesRespIds);

    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, workPackageId);
  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#isGenQuesNotRequired(Long,Long,Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testIsGenQuesNotRequired() throws ApicWebServiceException {
    boolean result =
        new RvwQnaireResponseServiceClient().isGenQuesNotRequired(QUEST_WP_ID, QUEST_RESP_ID, PIDC_NO_VAR_ID);

    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, result);
  }

  /**
   * Negative Test method for {@link RvwQnaireResponseServiceClient#isGenQuesNotRequired(Long,Long,Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testIsGenQuesNotRequiredNegative() throws ApicWebServiceException {
    this.thrown.expectMessage("A2l Work Package Definition with ID '" + INVALID_QUEST_WP_ID + NOT_FOUND);

    new RvwQnaireResponseServiceClient().isGenQuesNotRequired(INVALID_QUEST_WP_ID, QUEST_RESP_ID, PIDC_NO_VAR_ID);

    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#createQnaireResp(QnaireRespUpdationModel)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateQnaireResp() throws ApicWebServiceException {
    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();

    QnaireRespUpdationModel qnaireRespInputData = new QnaireRespUpdationModel();
    qnaireRespInputData.setPidcVersionId(PIDC_VER_ID);
    qnaireRespInputData.setSelRespId(A2L_RESPONSE_ID);
    qnaireRespInputData.setSelWpId(A2L_WP_ID);

    RvwQnaireResponse rvwQnaireResponse = new RvwQnaireResponse();
    long version1 = 1L;
    if (servClient.getById(QUESTION_RESP_ID_1).getVersion() != null) {
      version1 = servClient.getById(QUESTION_RESP_ID_1).getVersion();
    }
    rvwQnaireResponse.setA2lRespId(A2L_RESPONSE_ID);
    rvwQnaireResponse.setA2lWpId(A2L_WP_ID);
    rvwQnaireResponse.setCreatedDate(CREATED_DATE);
    rvwQnaireResponse.setCreatedUser(USER_ID);
    rvwQnaireResponse.setDeletedFlag(false);
    rvwQnaireResponse.setId(QUESTION_RESP_ID_1);
    rvwQnaireResponse.setName(QUESTION_RESP_NAME_1);
    rvwQnaireResponse.setPidcId(PIDC_ID);
    rvwQnaireResponse.setPidcVersId(PIDC_VER_ID);
    rvwQnaireResponse.setVersion(version1);

    RvwQnaireResponse rvwQnaireResponse2 = new RvwQnaireResponse();
    long version2 = 1L;
    if (servClient.getById(QUESTION_RESP_ID_2).getVersion() != null) {
      version2 = servClient.getById(QUESTION_RESP_ID_2).getVersion();
    }
    rvwQnaireResponse2.setA2lRespId(A2L_RESPONSE_ID);
    rvwQnaireResponse2.setA2lWpId(A2L_WP_ID);
    rvwQnaireResponse2.setCreatedDate(CREATED_DATE);
    rvwQnaireResponse2.setCreatedUser(USER_ID);
    rvwQnaireResponse2.setDeletedFlag(false);
    rvwQnaireResponse2.setId(QUESTION_RESP_ID_2);
    rvwQnaireResponse2.setName(QUESTION_RESP_NAME_2);
    rvwQnaireResponse2.setPidcId(PIDC_ID);
    rvwQnaireResponse2.setPidcVersId(PIDC_VER_ID);
    rvwQnaireResponse2.setVersion(version2);

    SortedSet<RvwQnaireResponse> rvwQnaireResponseSet = new TreeSet<>();
    rvwQnaireResponseSet.add(rvwQnaireResponse);
    rvwQnaireResponseSet.add(rvwQnaireResponse2);

    qnaireRespInputData.setOldQnaireRespSet(rvwQnaireResponseSet);

    QnaireRespUpdationModel response = servClient.createQnaireResp(qnaireRespInputData);

    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, response);
  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#createQnaireResp(QnaireRespUpdationModel)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateQnaireRespWithWorkPkg() throws ApicWebServiceException {
    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();

    QnaireRespUpdationModel qnaireRespInputData = new QnaireRespUpdationModel();
    qnaireRespInputData.setPidcVersionId(PIDC_VER_ID_WITH_WP);
    qnaireRespInputData.setSelRespId(A2L_RESPONSE_ID_WITH_WP);
    qnaireRespInputData.setSelWpId(A2L_WP_ID_WITH_WP);

    RvwQnaireResponse rvwQnaireResponse = new RvwQnaireResponse();
    long version1 = 1L;
    if (servClient.getById(QUESTION_RESP_ID_WITH_WP_1).getVersion() != null) {
      version1 = servClient.getById(QUESTION_RESP_ID_WITH_WP_1).getVersion();
    }
    rvwQnaireResponse.setA2lRespId(A2L_RESPONSE_ID_WITH_WP);
    rvwQnaireResponse.setA2lWpId(A2L_WP_ID_WITH_WP);
    rvwQnaireResponse.setCreatedDate(CREATED_DATE);
    rvwQnaireResponse.setCreatedUser(USER_ID);
    rvwQnaireResponse.setDeletedFlag(false);
    rvwQnaireResponse.setId(QUESTION_RESP_ID_WITH_WP_1);
    rvwQnaireResponse.setName(QUESTION_RESP_NAME_WITH_WP_1);
    rvwQnaireResponse.setPidcId(PIDC_ID_WITH_WP);
    rvwQnaireResponse.setPidcVersId(PIDC_VER_ID_WITH_WP);
    rvwQnaireResponse.setVersion(version1);

    RvwQnaireResponse rvwQnaireResponse2 = new RvwQnaireResponse();
    long version2 = 1L;
    if (servClient.getById(QUESTION_RESP_ID_WITH_WP_2).getVersion() != null) {
      version2 = servClient.getById(QUESTION_RESP_ID_WITH_WP_2).getVersion();
    }
    rvwQnaireResponse2.setA2lRespId(A2L_RESPONSE_ID_WITH_WP);
    rvwQnaireResponse2.setA2lWpId(A2L_WP_ID_WITH_WP);
    rvwQnaireResponse2.setCreatedDate(CREATED_DATE);
    rvwQnaireResponse2.setCreatedUser(USER_ID);
    rvwQnaireResponse2.setDeletedFlag(false);
    rvwQnaireResponse2.setId(QUESTION_RESP_ID_WITH_WP_2);
    rvwQnaireResponse2.setName(QUESTION_RESP_NAME_WITH_WP_2);
    rvwQnaireResponse2.setPidcId(PIDC_ID_WITH_WP);
    rvwQnaireResponse2.setPidcVersId(PIDC_VER_ID_WITH_WP);
    rvwQnaireResponse2.setVersion(version2);

    SortedSet<RvwQnaireResponse> rvwQnaireResponseSet = new TreeSet<>();
    rvwQnaireResponseSet.add(rvwQnaireResponse);
    rvwQnaireResponseSet.add(rvwQnaireResponse2);
    qnaireRespInputData.setOldQnaireRespSet(rvwQnaireResponseSet);

    SortedSet<WorkPkg> workPkgSet = new TreeSet<>();
    WorkPkg workPkg = new WorkPkg();
    workPkg.setId(QUESTION_WP_ID);
    workPkg.setName(QUESTION_WP_NAME);
    workPkg.setDescription(QUESTION_WP_DESC);
    workPkg.setWpNameEng(QUESTION_WP_NAME);
    workPkg.setWpDescEng(QUESTION_WP_DESC);
    workPkg.setVersion(2L);
    qnaireRespInputData.setWorkPkgSet(workPkgSet);

    QnaireRespUpdationModel response = servClient.createQnaireResp(qnaireRespInputData);

    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, response);
  }


  /**
   * Test method for {@link RvwQnaireResponseServiceClient#createQnaireResp(QnaireRespUpdationModel)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateQnaireRespWithVariantLinking() throws ApicWebServiceException {
    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();

    QnaireRespUpdationModel qnaireRespInputData = new QnaireRespUpdationModel();
    qnaireRespInputData.setPidcVersionId(PIDC_VER_ID_WITH_VAR_LINK);
    qnaireRespInputData.setSelRespId(A2L_RESPONSE_ID_WITH_VAR_LINK);
    qnaireRespInputData.setSelWpId(A2L_WP_ID_WITH_VAR_LINK);

    RvwQnaireResponse rvwQnaireResponse = new RvwQnaireResponse();
    long version1 = 1L;
    if (servClient.getById(QUESTION_RESP_ID_WITH_VAR_LINK_1).getVersion() != null) {
      version1 = servClient.getById(QUESTION_RESP_ID_WITH_VAR_LINK_1).getVersion();
    }
    rvwQnaireResponse.setA2lRespId(A2L_RESPONSE_ID_WITH_VAR_LINK);
    rvwQnaireResponse.setA2lWpId(A2L_WP_ID_WITH_VAR_LINK);
    rvwQnaireResponse.setCreatedDate(CREATED_DATE);
    rvwQnaireResponse.setCreatedUser(USER_ID);
    rvwQnaireResponse.setDeletedFlag(false);
    rvwQnaireResponse.setId(QUESTION_RESP_ID_WITH_VAR_LINK_1);
    rvwQnaireResponse.setName(QUESTION_RESP_NAME_WITH_VAR_LINK_1);
    rvwQnaireResponse.setPidcId(PIDC_ID_WITH_VAR_LINK);
    rvwQnaireResponse.setPidcVersId(PIDC_VER_ID_WITH_VAR_LINK);
    rvwQnaireResponse.setVariantId(QUESTION_VAR_LINK_ID);
    rvwQnaireResponse.setVariantName(QUESTION_VAR_LINK_NAME);
    rvwQnaireResponse.setVersion(version1);

    RvwQnaireResponse rvwQnaireResponse2 = new RvwQnaireResponse();
    long version2 = 1L;
    if (servClient.getById(QUESTION_RESP_ID_WITH_VAR_LINK_2).getVersion() != null) {
      version2 = servClient.getById(QUESTION_RESP_ID_WITH_VAR_LINK_2).getVersion();
    }
    rvwQnaireResponse2.setA2lRespId(A2L_RESPONSE_ID_WITH_VAR_LINK);
    rvwQnaireResponse2.setA2lWpId(A2L_WP_ID_WITH_VAR_LINK);
    rvwQnaireResponse2.setCreatedDate(CREATED_DATE);
    rvwQnaireResponse2.setCreatedUser(USER_ID);
    rvwQnaireResponse2.setDeletedFlag(false);
    rvwQnaireResponse2.setId(QUESTION_RESP_ID_WITH_VAR_LINK_2);
    rvwQnaireResponse2.setName(QUESTION_RESP_NAME_WITH_VAR_LINK_2);
    rvwQnaireResponse2.setPidcId(PIDC_ID_WITH_VAR_LINK);
    rvwQnaireResponse2.setPidcVersId(PIDC_VER_ID_WITH_VAR_LINK);
    rvwQnaireResponse2.setVariantId(QUESTION_VAR_LINK_ID);
    rvwQnaireResponse2.setVariantName(QUESTION_VAR_LINK_NAME);
    rvwQnaireResponse2.setVersion(version2);

    SortedSet<RvwQnaireResponse> rvwQnaireResponseSet = new TreeSet<>();
    rvwQnaireResponseSet.add(rvwQnaireResponse);
    rvwQnaireResponseSet.add(rvwQnaireResponse2);
    qnaireRespInputData.setOldQnaireRespSet(rvwQnaireResponseSet);

    SortedSet<QnaireRespVarRespWpLink> qnaireRespVarLinkSet = new TreeSet<>();
    QnaireRespVarRespWpLink qnaireRespVarLink = new QnaireRespVarRespWpLink();
    qnaireRespVarLink.setDetails("");
    qnaireRespVarLink.setLinked(true);
    qnaireRespVarLink.setSelQnaireRespId(QUESTION_RESP_ID_WITH_VAR_LINK_1);

    PidcVariant pidcVariant = new PidcVariantServiceClient().get(QUESTION_SEC_VAR_LINK_ID);
    A2lResponsibility a2lResponsibility = new A2lResponsibilityServiceClient().get(QUESTION_SEC_RESP_LINK_ID);
    A2lWorkPackage a2lWorkPackage = new A2lWorkPackageServiceClient().getById(QUESTION_SEC_WP_LINK_ID);

    qnaireRespVarLink.setPidcVariant(pidcVariant);
    qnaireRespVarLink.setA2lResponsibility(a2lResponsibility);
    qnaireRespVarLink.setA2lWorkPackage(a2lWorkPackage);

    qnaireRespVarLinkSet.add(qnaireRespVarLink);

    qnaireRespInputData.setQnaireRespVarLinkSet(qnaireRespVarLinkSet);
    QnaireRespUpdationModel response = servClient.createQnaireResp(qnaireRespInputData);

    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, response);

    // For Unlinking of qnaire with variant
    Set<RvwQnaireRespVariant> resultRespVariantSet = response.getCreatedQnaireRespVariantSet();
    Long qnaireRespVarIdToDel = 1L;
    for (RvwQnaireRespVariant rvwQnaireRespVariantTmp : resultRespVariantSet) {
      qnaireRespVarIdToDel = rvwQnaireRespVariantTmp.getId();
    }

    rvwQnaireResponse.setVariantId(QUESTION_VAR_LINK_ID);
    Map<Long, Map<Long, Set<Long>>> secondaryQnaireLinkMap = new HashMap<>();
    Map<Long, Set<Long>> respWpSetMap = new HashMap<>();
    respWpSetMap.put(QUESTION_SEC_RESP_LINK_ID, new HashSet<>(Arrays.asList(QUESTION_SEC_WP_LINK_ID)));
    secondaryQnaireLinkMap.put(QUESTION_SEC_VAR_LINK_ID, respWpSetMap);

    qnaireRespVarLink.setQnaireRespVarIdToDel(qnaireRespVarIdToDel);
    qnaireRespVarLink.setLinked(false);
    SortedSet<QnaireRespVarRespWpLink> qnaireRespVarLinkSetUnlink = new TreeSet<>();
    qnaireRespVarLinkSetUnlink.add(qnaireRespVarLink);
    qnaireRespInputData.setQnaireRespVarLinkSet(qnaireRespVarLinkSetUnlink);

    SortedSet<RvwQnaireResponse> rvwQnaireResponseSetUnlink = new TreeSet<>();
    rvwQnaireResponseSetUnlink.add(rvwQnaireResponse);
    rvwQnaireResponseSetUnlink.add(rvwQnaireResponse2);
    qnaireRespInputData.setOldQnaireRespSet(rvwQnaireResponseSetUnlink);

    QnaireRespUpdationModel responseUnlink = servClient.createQnaireResp(qnaireRespInputData);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL_OR_EMPTY, responseUnlink);

  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#deleteUndeleteQuesResp(RvwQnaireResponse)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testDeleteUndeleteQuesRespForDelete() throws ApicWebServiceException {
    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();

    long version = 1L;
    if (servClient.getById(QUESTION_RESP_ID_3).getVersion() != null) {
      version = servClient.getById(QUESTION_RESP_ID_3).getVersion();
    }

    RvwQnaireResponse rvwQnaireResponse = new RvwQnaireResponse();
    rvwQnaireResponse.setA2lRespId(A2L_RESPONSE_ID);
    rvwQnaireResponse.setA2lWpId(A2L_WP_ID);
    rvwQnaireResponse.setCreatedDate(CREATED_DATE);
    rvwQnaireResponse.setCreatedUser(USER_ID);
    rvwQnaireResponse.setDeletedFlag(true); // Setting deleted flag as true
    rvwQnaireResponse.setId(QUESTION_RESP_ID_3);
    rvwQnaireResponse.setName(QUESTION_RESP_NAME_3);
    rvwQnaireResponse.setPidcId(PIDC_ID);
    rvwQnaireResponse.setPidcVersId(PIDC_VER_ID);
    rvwQnaireResponse.setVersion(version);

    servClient.deleteUndeleteQuesResp(rvwQnaireResponse);

    assertTrue("The Qnaire Response is Deleted successfully", servClient.getById(QUESTION_RESP_ID_3).isDeletedFlag());

  }

  /**
   * Test method for {@link RvwQnaireResponseServiceClient#deleteUndeleteQuesResp(RvwQnaireResponse)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testDeleteUndeleteQuesRespForUnDelete() throws ApicWebServiceException {
    RvwQnaireResponseServiceClient servClient = new RvwQnaireResponseServiceClient();

    long version = 1L;
    if (servClient.getById(QUESTION_RESP_ID_3).getVersion() != null) {
      version = servClient.getById(QUESTION_RESP_ID_3).getVersion();
    }

    RvwQnaireResponse rvwQnaireResponse = new RvwQnaireResponse();
    rvwQnaireResponse.setA2lRespId(A2L_RESPONSE_ID);
    rvwQnaireResponse.setA2lWpId(A2L_WP_ID);
    rvwQnaireResponse.setCreatedDate(CREATED_DATE);
    rvwQnaireResponse.setCreatedUser(USER_ID);
    rvwQnaireResponse.setDeletedFlag(false); // Setting deleted flag as false
    rvwQnaireResponse.setId(QUESTION_RESP_ID_3);
    rvwQnaireResponse.setName(QUESTION_RESP_NAME_3);
    rvwQnaireResponse.setPidcId(PIDC_ID);
    rvwQnaireResponse.setPidcVersId(PIDC_VER_ID);
    rvwQnaireResponse.setVersion(version);

    servClient.deleteUndeleteQuesResp(rvwQnaireResponse);

    assertFalse("The Qnaire Response is UnDeleted successfully",
        servClient.getById(QUESTION_RESP_ID_3).isDeletedFlag());

  }


}
