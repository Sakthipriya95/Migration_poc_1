/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class QnaireDefinitionModel.
 *
 * @author nip4cob
 */
public class QnaireVersionModel {

  /** The questionnaire. */
  private Questionnaire questionnaire;

  /** The questionnaire version. */
  private QuestionnaireVersion questionnaireVersion;

  /** Key : QuestionId, value = Object */
  private Map<Long, Question> questionMap = new HashMap<>();

  /** Key : QuestionId, value = question config. */
  private Map<Long, QuestionConfig> questionConfigMap = new HashMap<>();

  /** Key : QuestionId, value = child question id list. */
  private Map<Long, List<Long>> childQuestionIdMap = new HashMap<>();

  /** Key : QuestionId, value = child question results set. */
  private Map<Long, Map<Long, QuestionResultOption>> questionWithQuestionResultsOptMap = new HashMap<>();

  /**
   * Gets the questionnaire.
   *
   * @return the questionnaire
   */
  public Questionnaire getQuestionnaire() {
    return this.questionnaire;
  }

  /**
   * Sets the questionnaire.
   *
   * @param questionnaire the questionnaire to set
   */
  public void setQuestionnaire(final Questionnaire questionnaire) {
    this.questionnaire = questionnaire;
  }

  /**
   * Gets the questionnaire version.
   *
   * @return the questionnaireVersion
   */
  public QuestionnaireVersion getQuestionnaireVersion() {
    return this.questionnaireVersion;
  }

  /**
   * Sets the questionnaire version.
   *
   * @param questionnaireVersion the questionnaireVersion to set
   */
  public void setQuestionnaireVersion(final QuestionnaireVersion questionnaireVersion) {
    this.questionnaireVersion = questionnaireVersion;
  }

  /**
   * Gets the question map.
   *
   * @return the questionMap
   */
  public Map<Long, Question> getQuestionMap() {
    return this.questionMap;
  }

  /**
   * Sets the question map.
   *
   * @param questionMap the questionMap to set
   */
  public void setQuestionMap(final Map<Long, Question> questionMap) {
    this.questionMap = questionMap;
  }

  /**
   * Gets the question config.
   *
   * @return the questionConfig
   */
  public Map<Long, QuestionConfig> getQuestionConfigMap() {
    return this.questionConfigMap;
  }

  /**
   * Sets the question config.
   *
   * @param questionConfig the questionConfig to set
   */
  public void setQuestionConfigMap(final Map<Long, QuestionConfig> questionConfigMap) {
    this.questionConfigMap = questionConfigMap;
  }

  /**
   * Gets the child question id map.
   *
   * @return the childQuestionIdMap
   */
  public Map<Long, List<Long>> getChildQuestionIdMap() {
    return this.childQuestionIdMap;
  }

  /**
   * Sets the child question id map.
   *
   * @param childQuestionIdMap the childQuestionIdMap to set
   */
  public void setChildQuestionIdMap(final Map<Long, List<Long>> childQuestionIdMap) {
    this.childQuestionIdMap = childQuestionIdMap;
  }


  /**
   * @return the questionWithResultsMap
   */
  public Map<Long, Map<Long, QuestionResultOption>> getQuesWithQuestionResultOptionsMap() {
    return this.questionWithQuestionResultsOptMap;
  }


  /**
   * @param questionWithResultsMap the questionWithResultsMap to set
   */
  public void setQuesWithQuestionResultOptionsMap(
      final Map<Long, Map<Long, QuestionResultOption>> questionWithResultsMap) {
    this.questionWithQuestionResultsOptMap = questionWithResultsMap;
  }

}
