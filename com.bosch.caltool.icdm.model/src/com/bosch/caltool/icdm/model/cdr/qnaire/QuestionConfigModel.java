/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;


/**
 * @author nip4cob
 */
public class QuestionConfigModel {

  private Question question;
  private QuestionConfig questionConfig;

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

}
