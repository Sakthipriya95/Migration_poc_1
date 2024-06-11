/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;


/**
 * @author hnu1cob
 */
public class QuestionResultOptionsModel {

  /** custom results **/
  private String result;

  /** assesment - positive, negative or neutral **/
  private String assesment;

  /** resultId needed incase of Edit/delete of question result  option  **/
  private Long quesResultOptId;
  
  /** allowFinishWp - true or false **/
  private boolean allowFinishWP;

  /**
   * @return the result
   */
  public String getResult() {
    return this.result;
  }

  /**
   * @param result the result to set
   */
  public void setResult(final String result) {
    this.result = result;
  }

  /**
   * @return the assesment
   */
  public String getAssesment() {
    return this.assesment;
  }

  /**
   * @param assesment the assesment to set
   */
  public void setAssesment(final String assesment) {
    this.assesment = assesment;
  }


  /**
   * @return the resultId
   */
  public Long getQuestionResultOptId() {
    return this.quesResultOptId;
  }


  /**
   * @param resultId the resultId to set
   */
  public void setQuestionResultOptId(final Long resultId) {
    this.quesResultOptId = resultId;
  }

  
  /**
   * @return the allowFinishWP
   */
  public boolean isAllowFinishWP() {
    return allowFinishWP;
  }

  
  /**
   * @param allowFinishWP the allowFinishWP to set
   */
  public void setAllowFinishWP(boolean allowFinishWP) {
    this.allowFinishWP = allowFinishWP;
  }


}
