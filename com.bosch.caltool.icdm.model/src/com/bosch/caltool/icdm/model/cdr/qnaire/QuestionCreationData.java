/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nip4cob
 */
public class QuestionCreationData {

  private Question question;

  private List<QuestionDepenAttr> qDepAttr;

  private Map<String, QuestionDepenAttrValue> qDepValCombMap = new HashMap<>();

  private QuestionConfig questionConfig;

  /** list of possible results for questionnaire **/
  private List<QuestionResultOptionsModel> qnaireResultOptionModel = new ArrayList<>();
  /**
   * Flag to indicate whether there is a change in question number without changing question level
   */
  private boolean isReorderWithinParent;


  /**
   * @return the question
   */
  public Question getQuestion() {
    return this.question;
  }


  /**
   * @param question the question to set
   */
  public void setQuestion(final Question question) {
    this.question = question;
  }


  /**
   * @return the attributes
   */
  public List<QuestionDepenAttr> getAttributes() {
    return this.qDepAttr;
  }


  /**
   * @param attributes the attributes to set
   */
  public void setAttributes(final List<QuestionDepenAttr> attributes) {
    this.qDepAttr = attributes == null ? null : new ArrayList<>(attributes);
  }


  /**
   * @return the questionConfig
   */
  public QuestionConfig getQuestionConfig() {
    return this.questionConfig;
  }


  /**
   * @param questionConfig the questionConfig to set
   */
  public void setQuestionConfig(final QuestionConfig questionConfig) {
    this.questionConfig = questionConfig;
  }


  /**
   * @return the qDepValCombMap
   */
  public Map<String, QuestionDepenAttrValue> getqDepValCombMap() {
    return this.qDepValCombMap;
  }


  /**
   * @param qDepValCombMap the qDepValCombMap to set
   */
  public void setqDepValCombMap(final Map<String, QuestionDepenAttrValue> qDepValCombMap) {
    this.qDepValCombMap = qDepValCombMap;
  }

  /**
   * @return the isReorderWithinParent
   */
  public boolean isReorderWithinParent() {
    return this.isReorderWithinParent;
  }


  /**
   * @param isReorderWithinParent the isReorderWithinParent to set
   */
  public void setReorderWithinParent(final boolean isReorderWithinParent) {
    this.isReorderWithinParent = isReorderWithinParent;
  }

  /**
   * @return the qnaireResultOptionModel
   */
  public List<QuestionResultOptionsModel> getQnaireResultOptionModel() {
    return this.qnaireResultOptionModel;
  }


  /**
   * @param qnaireResultOptionModel the qnaireResultOptionModel to set
   */
  public void setQnaireResultOptionModel(final List<QuestionResultOptionsModel> qnaireResultOptionModel) {
    this.qnaireResultOptionModel = qnaireResultOptionModel;
  }

}
