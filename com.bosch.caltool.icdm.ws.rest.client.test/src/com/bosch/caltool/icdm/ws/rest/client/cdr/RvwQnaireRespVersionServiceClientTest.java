package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for RvwQnaireRespVersion
 *
 * @author say8cob
 */
public class RvwQnaireRespVersionServiceClientTest extends AbstractRestClientTest {

  /**
   * pidc- JUNIT_Ques_Resp_Version (v1), qnaire - _test_Monica
   */
  private static final long QNAIRE_WORKING_SET2 = 12699240773L;


  /**
   * version name for creation
   */
  private static final String QNAIRE_RESP_VERSION_NAME = "JUNIT Version";

  /**
   * working set version
   */
  private static final Long RVWQNAIRERESPVERSION_ID = 12676790730L;

  /**
   * pidc -PIDC_For_Questionnaire_Response (v1), qnaire - ACT -> Sathyamurthy, Sharavan Pravin -> Wp1_Test -> AirDvp
   */
  private static final Long RVWQNAIRERESP_ID = 12676790728L;
  /**
   * id for question 1.1
   */
  private static final long ID_QUESTION1_1 = 12683034504l;

  /**
   * id for question 2.1
   */
  private static final Long ID_QUESTION2_1 = 12683034508L;

  /**
   * id for question 2.1.1
   */
  private static final Long ID_QUESTION2_1_1 = 12683034513L;

  /**
   * id for question 3.1
   */
  private static final Long ID_QUESTION3_1 = 12683034518L;

  /**
   * id for 3.1.1
   */
  private static final Long ID_QUESTION3_1_1 = 12699240730L;


  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.cdr.RvwQnaireRespVersionServiceClient#getById()}
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    RvwQnaireRespVersionServiceClient servClient = new RvwQnaireRespVersionServiceClient();
    RvwQnaireRespVersion ret = servClient.getById(RVWQNAIRERESPVERSION_ID);
    assertFalse("Response should not be null", (ret == null));
    testOutput(ret);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.cdr.RvwQnaireRespVersionServiceClient#getAllQnaireRespVersion()}
   *
   * @throws ApicWebServiceException Exception from server during creation
   */
  @Test
  public void testGetQnaireRespVersionsByRespId() throws ApicWebServiceException {
    RvwQnaireRespVersionServiceClient servClient = new RvwQnaireRespVersionServiceClient();
    Map<Long, RvwQnaireRespVersion> allQnaireRespVersMap = servClient.getQnaireRespVersionsByRespId(RVWQNAIRERESP_ID);
    assertFalse("Response should not be null or empty",
        ((allQnaireRespVersMap == null) || allQnaireRespVersMap.isEmpty()));
    testOutput(allQnaireRespVersMap.values().iterator().next());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.cdr.RvwQnaireRespVersionServiceClient#update()}
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testRvwQnaireRespVersionsUpdateStatus() throws ApicWebServiceException {
    RvwQnaireRespVersionServiceClient servClient = new RvwQnaireRespVersionServiceClient();
    RvwQnaireRespVersion ret = servClient.getById(RVWQNAIRERESPVERSION_ID);
    ret.setQnaireRespVersStatus(CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType());
    RvwQnaireRespVersion newRet = servClient.update(ret);
    assertFalse("Response should not be null", (newRet == null));
    testOutput(newRet);
  }

  /**
   * test creation of RvwQnaireRespVersion
   *
   * @throws ApicWebServiceException Exception from server during creation
   */
  @Test
  public void testCreateQnaireRespVersionWithDependencies() throws ApicWebServiceException {
    RvwQnaireRespVersionServiceClient servClient = new RvwQnaireRespVersionServiceClient();
    RvwQnaireRespVersion workingSet = servClient.getById(QNAIRE_WORKING_SET2);
    RvwQnaireRespVersion newRespVers = new RvwQnaireRespVersion();
    newRespVers.setQnaireRespId(workingSet.getQnaireRespId());
    newRespVers.setQnaireVersionId(workingSet.getQnaireVersionId());
    newRespVers.setVersionName(QNAIRE_RESP_VERSION_NAME);
    newRespVers.setDescription("Remarks");
    newRespVers
        .setCreatedDate(DateFormat.formatDateToString(Calendar.getInstance().getTime(), DateFormat.DATE_FORMAT_15));
    newRespVers.setCreatedUser("MKL2COB");
    newRespVers.setQnaireRespVersStatus(CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType());
    RvwQnaireRespVersion createdRespVersion = servClient.create(newRespVers);

    // get the ques version
    assertEquals("QnaireRespId is equal", createdRespVersion.getQnaireRespId(), workingSet.getQnaireRespId());
    assertEquals("QnaireResp version name is equal", createdRespVersion.getVersionName(), QNAIRE_RESP_VERSION_NAME);
    // initialising resolver
    RvwQnaireResponseModel quesRespModel = retrieveDataModel(createdRespVersion.getId());
    if (null != quesRespModel) {
      Map<Long, RvwQnaireAnswer> allQAMap = new HashMap<>();
      if (CommonUtils.isNotEmpty(quesRespModel.getRvwQnrAnswrMap())) {
        for (RvwQnaireAnswer ans : quesRespModel.getRvwQnrAnswrMap().values()) {
          allQAMap.put(ans.getQuestionId(), ans);
        }
      }
      // rvw answers to questions that should have been created
      assertNotNull("question 1.1 is not null", allQAMap.get(ID_QUESTION1_1));
      assertNotNull("question 2.1 is not null", allQAMap.get(ID_QUESTION2_1));
      assertNotNull("question 2.1.1 is not null", allQAMap.get(ID_QUESTION2_1_1));
      // rvw answers to questions that should not have been created
      assertNull("question 3.1 is null", allQAMap.get(ID_QUESTION3_1));
      assertNull("question 3.1.1 is null", allQAMap.get(ID_QUESTION3_1_1));
    }
  }


  /**
   * Initialize data.
   *
   * @param qnaireRespVersionId the qnaire resp version id
   * @return
   * @throws ApicWebServiceException
   */
  private RvwQnaireResponseModel retrieveDataModel(final long qnaireRespVersionId) throws ApicWebServiceException {
    return new RvwQnaireResponseServiceClient().getAllMappingModel(qnaireRespVersionId);
  }

  /**
   * test output data
   */
  private void testOutput(final RvwQnaireRespVersion obj) {
    assertEquals("QnaireRespId is equal", obj.getQnaireRespId(), RVWQNAIRERESP_ID);
  }


}
