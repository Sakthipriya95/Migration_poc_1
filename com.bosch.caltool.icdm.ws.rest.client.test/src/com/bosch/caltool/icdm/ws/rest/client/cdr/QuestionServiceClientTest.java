/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfigModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionCreationData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOptionsModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionUpdationData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */


public class QuestionServiceClientTest extends AbstractRestClientTest {

  private final static Long QUESTION_ID = 1520298899L;
  private final static Long ATTR_ID = 1265L;
  private final static Long ATTR_ID1 = 1251L;
  private final static Long QNAIRE_VERS_ID = 1453419279L;


  /**
   * Test method for {@link QuestionServiceClient#getQuestionDependentAttributes(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetQuestionDependentAttributes() throws ApicWebServiceException {
    QuestionServiceClient qservclient = new QuestionServiceClient();
    Set<Attribute> retSet = qservclient.getQuestionDependentAttributes(QUESTION_ID);
    assertFalse("Response should not be null or empty", ((retSet == null) || retSet.isEmpty()));
  }


  /**
   * Test method for {@link QuestionServiceClient#getQuestionDepenAttrMap(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetQuestionDepenAttrMap() throws ApicWebServiceException {
    QuestionServiceClient qservclient = new QuestionServiceClient();
    Map<Long, QuestionDepenAttr> retMap = qservclient.getQuestionDepenAttrMap(QUESTION_ID);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    QuestionDepenAttr qdepattr = retMap.get(ATTR_ID);
    assertNotNull("Response should not be null", qdepattr);
    testQuestionDepenAttr(qdepattr);
  }

  /**
   * @param questionDepenAttr
   */
  private void testQuestionDepenAttr(final QuestionDepenAttr questionDepenAttr) {
    assertNotNull("CreatedDate is not null", questionDepenAttr.getCreatedDate());
    assertEquals("Attribute ID is equal", Long.valueOf(1265), questionDepenAttr.getAttrId());
    assertEquals("Created User is equal", "IMI2SI", questionDepenAttr.getCreatedUser());
    assertEquals("Modified user is equal", null, questionDepenAttr.getModifiedUser());
  }


  /**
   * Test method for {@link QuestionServiceClient#getQuestDependentAttrAndValModel(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetQuestDependentAttrAndValModel() throws ApicWebServiceException {
    QuestionServiceClient qservclient = new QuestionServiceClient();
    QuestAttrAndValDepModel avmodel = qservclient.getQuestDependentAttrAndValModel(QUESTION_ID);
    assertNotNull("Response should not be null", avmodel);
    testQuestAttrAndValDepModel(avmodel);
  }


  /**
   * @param avmodel
   */
  private void testQuestAttrAndValDepModel(final QuestAttrAndValDepModel avmodel) {
    assertNotNull("Response should not be null", avmodel.getAttributeMap());
    Map<Long, Attribute> attr = avmodel.getAttributeMap();
    Attribute attribute = attr.get(ATTR_ID);
    assertEquals("Attr_name_Eng is equal", "ABS", attribute.getNameEng());
    assertEquals("Attr_name_Ger is equal", "ABS", attribute.getNameGer());
    assertEquals("Attr_Desc_Eng is equal", "Antilock Braking System (ABS) installed", attribute.getDescriptionEng());
    assertEquals("Attr_Desc_Ger is equal", "Anti-Blockier-System (ABS) verbaut", attribute.getDescriptionGer());
    assertEquals("Group_ID is equal", Long.valueOf(249267), attribute.getAttrGrpId());
    assertEquals("Created user is equal", "tbd2si", attribute.getCreatedUser());

  }


  /**
   * Test method for {@link QuestionServiceClient#getAllQnDepnAttrValModelByVersion(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAllQnDepnAttrValModelByVersion() throws ApicWebServiceException {
    QuestionServiceClient qservclient = new QuestionServiceClient();
    QuestAttrAndValDepModel avmodel = qservclient.getAllQnDepnAttrValModelByVersion(QNAIRE_VERS_ID);
    assertNotNull("Response should not be null", avmodel);
    testQuestAttrAndValDepModelNew(avmodel);
  }


  /**
   * @param avmodel
   */
  private void testQuestAttrAndValDepModelNew(final QuestAttrAndValDepModel avmodel) {
    assertNotNull("Response should not be null", avmodel.getAttributeMap());
    Map<Long, Attribute> attr = avmodel.getAttributeMap();
    Attribute attribute = attr.get(ATTR_ID1);
    assertEquals("Attr_name_Eng is equal", "ACC", attribute.getNameEng());
    assertEquals("Attr_name_Ger is equal", "ACC", attribute.getNameGer());
    assertEquals("Attr_Desc_Eng is equal", "Adaptive Cruise Control (ACC) installed", attribute.getDescriptionEng());
    assertEquals("Attr_Desc_Ger is equal",
        "Adaptive Cruise Control (ACC): Adaptive Fahrgeschwindigkeitsregelung verbaut", attribute.getDescriptionGer());
    assertEquals("Group_ID is equal", Long.valueOf(3183), attribute.getAttrGrpId());
    assertEquals("Created user is equal", "tbd2si", attribute.getCreatedUser());
  }


  /**
   * Test method for {@link QuestionServiceClient#create(QuestionCreationData)},
   * {@link QuestionServiceClient#update(QuestionUpdationData)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdateQuestion() throws ApicWebServiceException {
    QuestionServiceClient servClient = new QuestionServiceClient();

    QnaireVersionServiceClient qverservclient = new QnaireVersionServiceClient();
    QnaireVersionModel qverModel = qverservclient.getQnaireVersionWithDetails(1516041731L);
    assertNotNull("Response should be null", qverModel);

    Map<Long, Question> questionMap = qverModel.getQuestionMap();
    Long newQnumber = questionMap.size() + 1l;

    // Question object
    Question question = new Question();
    question.setQnaireVersId(1516041731L);
    question.setQNumber(newQnumber);
    question.setQNameEng("Junit_" + getRunId() + " testCreateQuestion");// Test_Q
    // question.setQNameGer("Question 1");
    question.setQHintEng("Test_Q");
    question.setParentQId(1520292389L);
    question.setDeletedFlag(false);
    question.setResultRelevantFlag(true);

    // creating a list of QuestionDepenAttr objects
    QuestionDepenAttr qDepAttr1 = new QuestionDepenAttr();
    qDepAttr1.setQId(1520292389L);
    qDepAttr1.setAttrId(1265L);

    QuestionDepenAttr qDepAttr2 = new QuestionDepenAttr();
    qDepAttr2.setQId(1534823323L);
    qDepAttr2.setAttrId(391L);

    List<QuestionDepenAttr> qDepAttrList = new ArrayList<QuestionDepenAttr>();
    qDepAttrList.add(qDepAttr1);
    qDepAttrList.add(qDepAttr2);

    // creation of a map of QuestionDepenAttrValue objects
    QuestionDepenAttrValue questionDepenAttrValue = new QuestionDepenAttrValue();
    // questionDepenAttrValue.setQAttrDepId(qAttrDepId); handled by the service
    questionDepenAttrValue.setQCombiNum(1L);
    questionDepenAttrValue.setValueId(1453194378L);
    Map<String, QuestionDepenAttrValue> qDepValCombMap = new HashMap<>();
    qDepValCombMap.put("152029238912651", questionDepenAttrValue);


    // QuestionConfig object
    QuestionConfig questionConfig = new QuestionConfig();
    // questionConfig.setQId(1453190728L);//showing null
    questionConfig.setMeasure("O");
    questionConfig.setMeasurement("O");
    questionConfig.setResponsible("O");
    questionConfig.setResult("O");
    questionConfig.setRemark("O");
    questionConfig.setSeries("O");
    questionConfig.setCompletionDate("O");

    // Add questionResultOptions are added to create model
    List<QuestionResultOptionsModel> createQnaireResultOptionModel = new ArrayList<>();
    QuestionResultOptionsModel qnaireResultModel1 = new QuestionResultOptionsModel();
    qnaireResultModel1.setResult("Junit-Test");
    qnaireResultModel1.setAssesment("P");
    createQnaireResultOptionModel.add(qnaireResultModel1);

    // creation of QuestionCreationData object
    QuestionCreationData questionCreationData = new QuestionCreationData();
    questionCreationData.setQuestion(question);
    questionCreationData.setAttributes(qDepAttrList);
    questionCreationData.setqDepValCombMap(qDepValCombMap);
    questionCreationData.setQuestionConfig(questionConfig);
    // Adding QuestionResultOption Model
    questionCreationData.setQnaireResultOptionModel(createQnaireResultOptionModel);

    // invoke create method
    QuestionConfigModel createdQuesConfigModel = servClient.create(questionCreationData);
    assertNotNull("Created Model is not null", createdQuesConfigModel);
    assertTrue("Result Relevant flag is true", createdQuesConfigModel.getQuestion().getResultRelevantFlag());
    // validate create
    assertEquals("Q_Name_Eng is equal", "Junit_" + getRunId() + " testCreateQuestion",
        createdQuesConfigModel.getQuestion().getQNameEng());


    // Update the quesionn
    Question updateQuestion = createdQuesConfigModel.getQuestion();
    // updateQuestion.setQNameEng("JUnit_" + getRunId() + " Updated");
    // updateQuestion.setQHintEng("Test_Q Updated");
    updateQuestion.setDeletedFlag(true);
    QuestionUpdationData updateModel = new QuestionUpdationData();
    updateModel.setQuestion(updateQuestion);
    updateModel.setOnlyDepChange(true);

    updateModel.getQuestion().setDepQuesId(1584644196L);
    updateModel.getQuestion().setDepQuesResp("P");
    updateModel.getQuestion().setDepQResultOptId(null);
    // updateModel.getQuestion().setDeletedFlag(question.getDeletedFlag());
    // For adding QuestionResultOptions during Update
    List<QuestionResultOptionsModel> qnaireResultOptionAddModel = new ArrayList<>();
    QuestionResultOptionsModel qnaireResultAddModel1 = new QuestionResultOptionsModel();
    qnaireResultAddModel1.setResult("Positive");
    qnaireResultAddModel1.setAssesment("P");
    qnaireResultOptionAddModel.add(qnaireResultAddModel1);
    // Add and edit questionResultOptions are added to update model
    updateModel.setQnaireResultOptionsToBeAdd(qnaireResultOptionAddModel);

    // invoke update
    QuestionConfigModel updatedQuesConfigModel = servClient.update(updateModel);
    assertNotNull("Updated Model is not null", updatedQuesConfigModel);
    // validate update
    assertEquals("Dep_Ques_Resp is equal", "P", updatedQuesConfigModel.getQuestion().getDepQuesResp());
    assertNull("Dependency question response options is null",
        updatedQuesConfigModel.getQuestion().getDepQResultOptId());
  }

  /**
   * Test method for {@link QuestionServiceClient#create(QuestionCreationData)} with headingFlag as true,
   * {@link QuestionServiceClient#update(QuestionUpdationData)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdateQuestionWithHeadingFlag() throws ApicWebServiceException {
    QuestionServiceClient servClient = new QuestionServiceClient();

    QnaireVersionServiceClient qverservclient = new QnaireVersionServiceClient();
    QnaireVersionModel qverModel = qverservclient.getQnaireVersionWithDetails(1516041731L);
    assertNotNull("Response should be null", qverModel);

    Map<Long, Question> questionMap = qverModel.getQuestionMap();
    Long newQnumber = questionMap.size() + 1l;

    // Question object
    Question question = new Question();
    question.setQnaireVersId(1516041731L);
    question.setQNumber(newQnumber);
    question.setQNameEng("Junit_" + getRunId() + " testCreateQuestion");// Test_Q
    // question.setQNameGer("Question 1");
    question.setQHintEng("Test_Q");
    question.setParentQId(1520292389L);
    question.setDeletedFlag(false);
    question.setResultRelevantFlag(true);
    // Setting the Heading Flag as true
    question.setHeadingFlag(true);

    // creating a list of QuestionDepenAttr objects
    QuestionDepenAttr qDepAttr1 = new QuestionDepenAttr();
    qDepAttr1.setQId(1520292389L);
    qDepAttr1.setAttrId(1265L);

    QuestionDepenAttr qDepAttr2 = new QuestionDepenAttr();
    qDepAttr2.setQId(1534823323L);
    qDepAttr2.setAttrId(391L);

    List<QuestionDepenAttr> qDepAttrList = new ArrayList<QuestionDepenAttr>();
    qDepAttrList.add(qDepAttr1);
    qDepAttrList.add(qDepAttr2);

    // creation of a map of QuestionDepenAttrValue objects
    QuestionDepenAttrValue questionDepenAttrValue = new QuestionDepenAttrValue();
    // questionDepenAttrValue.setQAttrDepId(qAttrDepId); handled by the service
    questionDepenAttrValue.setQCombiNum(1L);
    questionDepenAttrValue.setValueId(1453194378L);
    Map<String, QuestionDepenAttrValue> qDepValCombMap = new HashMap<>();
    qDepValCombMap.put("152029238912651", questionDepenAttrValue);


    // QuestionConfig object
    QuestionConfig questionConfig = new QuestionConfig();
    // questionConfig.setQId(1453190728L);//showing null
    questionConfig.setMeasure("O");
    questionConfig.setMeasurement("O");
    questionConfig.setResponsible("O");
    questionConfig.setResult("O");
    questionConfig.setRemark("O");
    questionConfig.setSeries("O");
    questionConfig.setCompletionDate("O");

    // Add questionResultOptions are added to create model
    List<QuestionResultOptionsModel> createQnaireResultOptionModel = new ArrayList<>();
    QuestionResultOptionsModel qnaireResultModel1 = new QuestionResultOptionsModel();
    qnaireResultModel1.setResult("Junit-Test");
    qnaireResultModel1.setAssesment("P");
    createQnaireResultOptionModel.add(qnaireResultModel1);

    // creation of QuestionCreationData object
    QuestionCreationData questionCreationData = new QuestionCreationData();
    questionCreationData.setQuestion(question);
    questionCreationData.setAttributes(qDepAttrList);
    questionCreationData.setqDepValCombMap(qDepValCombMap);
    questionCreationData.setQuestionConfig(questionConfig);
    // Adding QuestionResultOption Model
    questionCreationData.setQnaireResultOptionModel(createQnaireResultOptionModel);

    // invoke create method
    QuestionConfigModel createdQuesConfigModel = servClient.create(questionCreationData);
    assertNotNull("Created Model is not null", createdQuesConfigModel);
    assertTrue("Result Relevant flag is true", createdQuesConfigModel.getQuestion().getResultRelevantFlag());
    // validate create
    assertEquals("Q_Name_Eng is equal", "Junit_" + getRunId() + " testCreateQuestion",
        createdQuesConfigModel.getQuestion().getQNameEng());


    // Update the quesionn
    Question updateQuestion = createdQuesConfigModel.getQuestion();
    // updateQuestion.setQNameEng("JUnit_" + getRunId() + " Updated");
    // updateQuestion.setQHintEng("Test_Q Updated");
    updateQuestion.setDeletedFlag(true);
    QuestionUpdationData updateModel = new QuestionUpdationData();
    updateModel.setQuestion(updateQuestion);
    updateModel.setOnlyDepChange(true);

    updateModel.getQuestion().setDepQuesId(1584644196L);
    updateModel.getQuestion().setDepQuesResp("P");
    updateModel.getQuestion().setDepQResultOptId(null);
    // updateModel.getQuestion().setDeletedFlag(question.getDeletedFlag());
    // For adding QuestionResultOptions during Update
    List<QuestionResultOptionsModel> qnaireResultOptionAddModel = new ArrayList<>();
    QuestionResultOptionsModel qnaireResultAddModel1 = new QuestionResultOptionsModel();
    qnaireResultAddModel1.setResult("Positive");
    qnaireResultAddModel1.setAssesment("P");
    qnaireResultOptionAddModel.add(qnaireResultAddModel1);
    // Add and edit questionResultOptions are added to update model
    updateModel.setQnaireResultOptionsToBeAdd(qnaireResultOptionAddModel);

    // invoke update
    QuestionConfigModel updatedQuesConfigModel = servClient.update(updateModel);
    assertNotNull("Updated Model is not null", updatedQuesConfigModel);
    // validate update
    assertEquals("Dep_Ques_Resp is equal", "P", updatedQuesConfigModel.getQuestion().getDepQuesResp());
    assertNull("Dependency question response options is null",
        updatedQuesConfigModel.getQuestion().getDepQResultOptId());
  }

}

