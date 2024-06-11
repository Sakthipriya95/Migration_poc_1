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
public class QuestionUpdationData {

  private Question question;

  private List<QuestionDepenAttr> qDepAttrToAdd = new ArrayList<>();

  private List<Long> qDepAttrIdToDelete = new ArrayList<>();

  private Map<String, QuestionDepenAttrValue> qDepValToAdd = new HashMap<>();

  private Map<String, QuestionDepenAttrValue> qDepValToEdit = new HashMap<>();

  private Map<String, QuestionDepenAttrValue> qDepValToDelete = new HashMap<>();

  private QuestionConfig questionConfig;

  /** list of possible results for questionnaire creation list **/
  private List<QuestionResultOptionsModel> qnaireResultOptionsToBeAdd = new ArrayList<>();
  /** list of possible results for questionnaire edit **/
  private List<QuestionResultOptionsModel> qnaireResultOptionsToBeEdit = new ArrayList<>();
  /** list of possible results for questionnaire delete **/
  private List<QuestionResultOptionsModel> qnaireResultOptionsToBeDelete = new ArrayList<>();
  /**
   * Flag to check whether there is a change in question number without changing question level
   */
  private boolean isReorderWithinParent;
  /**
   * Flag to check level change
   */
  private boolean isLevelChange;
  /**
   * Field to store new Question Number obtained from number combo in Question Dialog
   */
  private Long newQuesNo;

  private int newLevel;
  /**
   * Field to store level of current question before editing the question to new level
   */
  private int oldQuesLevel;

  private boolean onlyDepChange;

  private Question newParent;


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
   * @return the newQuesNo
   */
  public Long getNewQuesNo() {
    return this.newQuesNo;
  }

  /**
   * @param newQuesNo the newQuesNo to set
   */
  public void setNewQuesNo(final Long newQuesNo) {
    this.newQuesNo = newQuesNo;
  }


  /**
   * @return the qDepAttrToAdd
   */
  public List<QuestionDepenAttr> getqDepAttrToAdd() {
    return this.qDepAttrToAdd;
  }


  /**
   * @param qDepAttrToAdd the qDepAttrToAdd to set
   */
  public void setqDepAttrToAdd(final List<QuestionDepenAttr> qDepAttrToAdd) {
    this.qDepAttrToAdd = qDepAttrToAdd == null ? null : new ArrayList<>(qDepAttrToAdd);
  }


  /**
   * @return the qDepAttrToEdit
   */
  public List<Long> getqDepAttrToDelete() {
    return this.qDepAttrIdToDelete;
  }


  /**
   * @param qDepAttrToEdit the qDepAttrToEdit to set
   */
  public void setqDepAttrToDelete(final List<Long> qDepAttrToEdit) {
    this.qDepAttrIdToDelete = qDepAttrToEdit == null ? null : new ArrayList<>(qDepAttrToEdit);
  }


  /**
   * @return the qDepValToAdd
   */
  public Map<String, QuestionDepenAttrValue> getqDepValToAdd() {
    return this.qDepValToAdd;
  }


  /**
   * @param qDepValToAdd the qDepValToAdd to set
   */
  public void setqDepValToAdd(final Map<String, QuestionDepenAttrValue> qDepValToAdd) {
    this.qDepValToAdd = qDepValToAdd;
  }


  /**
   * @return the qDepValToEdit
   */
  public Map<String, QuestionDepenAttrValue> getqDepValToEdit() {
    return this.qDepValToEdit;
  }


  /**
   * @param qDepValToEdit the qDepValToEdit to set
   */
  public void setqDepValToEdit(final Map<String, QuestionDepenAttrValue> qDepValToEdit) {
    this.qDepValToEdit = qDepValToEdit;
  }


  /**
   * @return the qDepValToDelete
   */
  public Map<String, QuestionDepenAttrValue> getqDepValToDelete() {
    return this.qDepValToDelete;
  }


  /**
   * @param qDepValToDelete the qDepValToDelete to set
   */
  public void setqDepValToDelete(final Map<String, QuestionDepenAttrValue> qDepValToDelete) {
    this.qDepValToDelete = qDepValToDelete;
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
   * @return the isLevelChange
   */
  public boolean isLevelChange() {
    return this.isLevelChange;
  }


  /**
   * @param isLevelChange the isLevelChange to set
   */
  public void setLevelChange(final boolean isLevelChange) {
    this.isLevelChange = isLevelChange;
  }


  /**
   * @return the newLevel
   */
  public int getNewLevel() {
    return this.newLevel;
  }


  /**
   * @param newLevel the newLevel to set
   */
  public void setNewLevel(final int newLevel) {
    this.newLevel = newLevel;
  }


  /**
   * @return the onlyDepChange
   */
  public boolean isOnlyDepChange() {
    return this.onlyDepChange;
  }


  /**
   * @param onlyDepChange the onlyDepChange to set
   */
  public void setOnlyDepChange(final boolean onlyDepChange) {
    this.onlyDepChange = onlyDepChange;
  }


  /**
   * @return the newParent
   */
  public Question getNewParent() {
    return this.newParent;
  }


  /**
   * @param newParent the newParent to set
   */
  public void setNewParent(final Question newParent) {
    this.newParent = newParent;
  }

  /**
   * @return the currQuesLevel
   */
  public int getOldQuesLevel() {
    return this.oldQuesLevel;
  }

  /**
   * @param currQuesLevel the currQuesLevel to set
   */
  public void setOldQuesLevel(final int currQuesLevel) {
    this.oldQuesLevel = currQuesLevel;
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
   * @return the qnaireResultOptionsToBeAdd
   */
  public List<QuestionResultOptionsModel> getQnaireResultOptionsToBeAdd() {
    return this.qnaireResultOptionsToBeAdd;
  }


  /**
   * @param qnaireResultOptionsToBeAdd the qnaireResultOptionsToBeAdd to set
   */
  public void setQnaireResultOptionsToBeAdd(final List<QuestionResultOptionsModel> qnaireResultOptionsToBeAdd) {
    this.qnaireResultOptionsToBeAdd = qnaireResultOptionsToBeAdd;
  }


  /**
   * @return the qnaireResultOptionsToBeEdit
   */
  public List<QuestionResultOptionsModel> getQnaireResultOptionsToBeEdit() {
    return this.qnaireResultOptionsToBeEdit;
  }


  /**
   * @param qnaireResultOptionsToBeEdit the qnaireResultOptionsToBeEdit to set
   */
  public void setQnaireResultOptionsToBeEdit(final List<QuestionResultOptionsModel> qnaireResultOptionsToBeEdit) {
    this.qnaireResultOptionsToBeEdit = qnaireResultOptionsToBeEdit;
  }


  /**
   * @return the qnaireResultOptionsToBeDelete
   */
  public List<QuestionResultOptionsModel> getQnaireResultOptionsToBeDelete() {
    return this.qnaireResultOptionsToBeDelete;
  }


  /**
   * @param qnaireResultOptionsToBeDelete the qnaireResultOptionsToBeDelete to set
   */
  public void setQnaireResultOptionsToBeDelete(final List<QuestionResultOptionsModel> qnaireResultOptionsToBeDelete) {
    this.qnaireResultOptionsToBeDelete = qnaireResultOptionsToBeDelete;
  }


}
