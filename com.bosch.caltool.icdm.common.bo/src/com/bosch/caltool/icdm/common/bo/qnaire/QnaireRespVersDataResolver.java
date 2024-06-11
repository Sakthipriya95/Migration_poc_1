/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.qnaire;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;

/**
 * @author mkl2cob
 */
public class QnaireRespVersDataResolver {


  /**
   * Key - question id, Value- RvwQnaireAnswer
   */
  private final Map<Long, RvwQnaireAnswer> allQAMap = new HashMap<>();

  /**
   * Key - question id, Value - parent question id
   */
  private final Map<Long, Long> parentQnMap = new HashMap<>();

  /**
   * key - answer id , value -RvwQnaireAnswer
   */
  private final Map<Long, RvwQnaireAnswer> allRvwAnsMap = new HashMap<>();

  /**
   * IQuesRespDataProvider with separate implementations for server and client
   */
  private final IQuesRespDataProvider quesRespDataProvider;

  /**
   * Constructor
   *
   * @param quesRespDataProvider IQuesRespDataProvider
   */
  public QnaireRespVersDataResolver(final IQuesRespDataProvider quesRespDataProvider) {
    this.quesRespDataProvider = quesRespDataProvider;

  }

  /**
   * @param qnaireRespModel RvwQnaireResponseModel
   */
  public void loadMainQuestions(final RvwQnaireResponseModel qnaireRespModel) {
    // clear all map
    this.allQAMap.clear();
    this.parentQnMap.clear();
    this.allRvwAnsMap.clear();
    // Defined Answers
    if (CommonUtils.isNotEmpty(qnaireRespModel.getRvwQnrAnswrMap())) {
      for (RvwQnaireAnswer ans : qnaireRespModel.getRvwQnrAnswrMap().values()) {
        this.allQAMap.put(ans.getQuestionId(), ans);
        this.allRvwAnsMap.put(ans.getId(), ans);
      }
    }
    // Check To load all the questions only for the working set versions
    QnaireRespDepnEvaluator qnEvaluator =
        new QnaireRespDepnEvaluator(qnaireRespModel.getPidcVersion(), qnaireRespModel.getPidcVariant());
    Set<Question> questions = this.quesRespDataProvider.getQuestionsSet();
    if (CommonUtils.isNotEmpty(questions)) {
      for (Question question : questions) {
        // iterate through all questions
        // check for working set
        if (qnaireRespModel.getRvwQnrRespVersion().getRevNum() == 0l) {
          addRvwQnaireAnswer(question);
          if (!(qnEvaluator.isQuestionApplicable(question, this.quesRespDataProvider) &&
              isQuestionVisible(this.allQAMap.get(question.getId()), this.quesRespDataProvider.isQuesDepChkNeeded()))) {
            // if the question is not applicable due to dependencies , remove the question entry from map
            this.allQAMap.remove(question.getId());
          }
          else {
            this.parentQnMap.put(question.getId(), question.getParentQId());
          }
        }
      }
    }
    else {
      // clear all the maps when there is no questions available for the input qnaire vers id
      this.allQAMap.clear();
      this.parentQnMap.clear();
      this.allRvwAnsMap.clear();
    }
  }

  /**
   * @param qnaireRespModel RvwQnaireResponseModel
   * @return Map<Long,Questions>
   */
  public Map<Long, Question> loadMainQuestionsForWorkingSet(final RvwQnaireResponseModel qnaireRespModel) {
    Map<Long, Question> questionsMap = new HashMap<>();
    this.allQAMap.clear();
    // Add all questions
    this.quesRespDataProvider.getQuestionsSet().forEach(question -> questionsMap.put(question.getId(), question));

    // Check To load all the questions only for the working set versions
    QnaireRespDepnEvaluator qnEvaluator =
        new QnaireRespDepnEvaluator(qnaireRespModel.getPidcVersion(), qnaireRespModel.getPidcVariant());
    Set<Question> questions = this.quesRespDataProvider.getQuestionsSet();
    for (Question question : questions) {
      // iterate through all questions
      addRvwQnaireAnswer(question);
      if (!(qnEvaluator.isQuestionApplicable(question, this.quesRespDataProvider) &&
          isQuestionVisible(this.allQAMap.get(question.getId()), this.quesRespDataProvider.isQuesDepChkNeeded()))) {
        // if the question is not applicable due to dependencies , remove the question entry from map
        this.allQAMap.remove(question.getId());
        questionsMap.remove(question.getId());
      }
    }
    return questionsMap;
  }

  /**
   * @param qnaireRespModel
   * @param question
   */
  private void addRvwQnaireAnswer(final Question question) {
    if (!this.allQAMap.containsKey(question.getId())) {
      // create RvwQnaireAnswer instance for all questions available
      RvwQnaireAnswer value = new RvwQnaireAnswer();
      value.setId(question.getId());
      value.setQuestionId(question.getId());
      this.allQAMap.put(question.getId(), value);
    }
  }

  /**
   * Checks if is question visible.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @param isQDepChkNeeded true if this check is needed
   * @return true if the question is visible
   */
  public boolean isQuestionVisible(final RvwQnaireAnswer rvwQnaireAns, final boolean isQDepChkNeeded) {

    // not a valid case when review qnaire answer is null
    if (null == rvwQnaireAns) {
      return false;
    }

    // If not a Question then return true
    if (isQDepChkNeeded && (this.quesRespDataProvider.getQuestion(rvwQnaireAns.getQuestionId()) != null)) {
      // get the parent question of the current question
      Question currQues = this.quesRespDataProvider.getQuestion(rvwQnaireAns.getQuestionId());
      Question parentQuestion = this.quesRespDataProvider.getQuestion(currQues.getParentQId());

      // If parent question is null then goto current Question
      while (parentQuestion != null) {
        // Get the parent question dep
        // If the parent question dep is not matching return false
        if (!checkQuesVisible(parentQuestion.getDepQuesId(), parentQuestion)) {
          return false;
        }
        // Now get the parent of the parent
        parentQuestion = this.quesRespDataProvider.getQuestion(parentQuestion.getParentQId());
      }
      // check for current question dep.
      if (currQues.getDepQuesId() != null) {
        Question dependentQuestion = this.quesRespDataProvider.getQuestion(currQues.getDepQuesId());
        while (dependentQuestion != null) {
          if (dependentQuestion.getDeletedFlag()) {
            return true;
          }
          if (!checkQuesVisible(currQues.getDepQuesId(), currQues)) {
            return false;
          }
          dependentQuestion = this.quesRespDataProvider.getQuestion(dependentQuestion.getDepQuesId());
        }
      }
    }
    return true;
  }

  /**
   * Check ques visible.
   *
   * @param depQnId the question id
   * @param defBo the def data bo
   * @return true, if successful
   */
  private boolean checkQuesVisible(final Long depQnId, final Question currentQuestion) {

    boolean visiblityFlag = true;
    // If the dep question is null then return true
    if ((depQnId != null) && (this.quesRespDataProvider.getQuestion(depQnId) != null)) {
      RvwQnaireAnswer depQuesRvwAns = null;
      for (Entry<Long, RvwQnaireAnswer> entry : this.allQAMap.entrySet()) {
        if (depQnId.longValue() == entry.getKey().longValue()) {
          depQuesRvwAns = entry.getValue();
          break;
        }
      }
      // if rvw qnaire answer is null the return false
      visiblityFlag = false;
      // Answer to the question same as dependency constraint
      if (depQuesRvwAns != null) {
        if (CommonUtils.isNotNull(currentQuestion.getDepQuesResp())) {
          visiblityFlag = currentQuestion.getDepQuesResp().equals(getResult(depQuesRvwAns));
        }
        else {
          visiblityFlag = currentQuestion.getDepQResultOptId().equals(depQuesRvwAns.getSelQnaireResultOptID());
        }
      }
    }
    return visiblityFlag;

  }

  /**
   * Gets the result.
   *
   * @param rvwQnaireAns the rvw qnaire ans id
   * @return Result answer
   */
  public String getResult(final RvwQnaireAnswer rvwQnaireAns) {
    long questionId = rvwQnaireAns.getQuestionId();
    if (this.quesRespDataProvider.isHeading(questionId)) {
      return "";
    }
    return CommonUtils.isNotNull(rvwQnaireAns.getResult()) ? rvwQnaireAns.getResult() : "";
  }

  /**
   * @return the allQAMap
   */
  public Map<Long, RvwQnaireAnswer> getAllQAMap() {
    return this.allQAMap;
  }


  /**
   * @return the parentQnMap
   */
  public Map<Long, Long> getParentQnMap() {
    return this.parentQnMap;
  }


  /**
   * @return the allRvwAnsMap
   */
  public Map<Long, RvwQnaireAnswer> getAllRvwAnsMap() {
    return this.allRvwAnsMap;
  }
}
