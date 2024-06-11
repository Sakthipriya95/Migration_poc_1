/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class QnaireVersionServiceClientTest extends AbstractRestClientTest {

  private final static Long QNAIRE_VERSION_ID = 795413988L;
  private final static Long QUESTION_ID = 1520299250L;

  /**
   * Test method for {@link QnaireVersionServiceClient#getQnaireVersionWithDetails(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetQnaireVersionWithDetails() throws ApicWebServiceException {
    QnaireVersionServiceClient qverservclient = new QnaireVersionServiceClient();
    QnaireVersionModel qvermodel = qverservclient.getQnaireVersionWithDetails(QNAIRE_VERSION_ID);
    assertNotNull("Response should not be null", (qvermodel == null));
    if (qvermodel != null) {
      assertNotNull("QuestionResultOptions should not be null", (qvermodel.getQuesWithQuestionResultOptionsMap()));
      testOutput(qvermodel);
    }

  }


  /**
   * Test method for {@link QnaireVersionServiceClient#getQnaireVModelByQuesId(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetQnaireVModelByQuesId() throws ApicWebServiceException {
    QnaireVersionServiceClient qverservclient = new QnaireVersionServiceClient();
    QnaireVersionModel qvermodel = qverservclient.getQnaireVModelByQuesId(QUESTION_ID);
    assertNotNull("Response should not be null", (qvermodel == null));
    testOutput(qvermodel);
  }


  /**
   * @param qvermodel
   */
  private void testOutput(final QnaireVersionModel qvermodel) {
    Map<Long, Question> qmap = qvermodel.getQuestionMap();
    Question question = qmap.get(QUESTION_ID);
    assertEquals("QnameEng is equal", "Are there measures that solved the issues", question.getQNameEng());
    assertEquals("Q_Number is equal", Long.valueOf(3), question.getQNumber());
    assertEquals("QNameGer is equal", "Test", question.getQNameGer());
    assertEquals("DependentQuestionID is equal", Long.valueOf(1520299248), question.getDepQuesId());
    assertNotNull("CreatedDate is not null", question.getCreatedDate());
  }

  /**
   * Test method for {@link QnaireVersionServiceClient#create(QuestionnaireVersion)},
   * {@link QnaireVersionServiceClient#update(QuestionnaireVersion)}, {@link QnaireVersionServiceClient#delete(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdateDelete() throws ApicWebServiceException {
    QnaireVersionServiceClient servClient = new QnaireVersionServiceClient();

    QuestionnaireVersion qnaireVersion = new QuestionnaireVersion();
    qnaireVersion.setQnaireId(1611698951L);
    qnaireVersion.setMajorVersionNum(1L);
    qnaireVersion.setDescEng("Junit_" + getRunId() + " testCreateUpdateDelete");
    qnaireVersion.setActiveFlag("Y");

    // invoke create method
    QuestionnaireVersion createdObj = servClient.create(qnaireVersion);
    LOG.info("Questionnaire Version Description After Create : {} ", createdObj.getDescEng());

    // validate create
    assertNotNull("Created Object is not null", createdObj);
    assertEquals("DescEng is equal", "Junit_" + getRunId() + " testCreateUpdateDelete", createdObj.getDescEng());

    // invoke update method
    createdObj.setDescEng("Junit_" + getRunId() + " Updated");
    QuestionnaireVersion updatedObj = servClient.update(createdObj);
    LOG.info("Questionnaire Version Description After Update : {} ", updatedObj.getDescEng());

    // validate update
    assertNotNull("Updated object is not null", updatedObj);
    assertEquals("DescEng is equal", "Junit_" + getRunId() + " Updated", updatedObj.getDescEng());
    assertEquals("Active Flag is equal", "Y", updatedObj.getActiveFlag());

    // invoke delete method
    servClient.delete(updatedObj.getId());
  }
}
