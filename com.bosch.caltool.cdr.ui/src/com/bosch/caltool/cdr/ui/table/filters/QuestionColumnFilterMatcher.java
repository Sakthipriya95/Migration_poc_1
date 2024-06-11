/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import java.util.regex.Pattern;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author dmo5cob
 * @param <E> element
 */
public class QuestionColumnFilterMatcher<E> implements Matcher<E> {

  /**
   * Pattern
   */
  private Pattern pattern1;
  /**
   * Pattern to search
   */
  private boolean pattern2Search;
  /**
   * Pattern
   */
  private Pattern pattern2;
  /**
   * Data handler for Review Qnaire Answer
   */
  private final QnaireRespEditorDataHandler respDataHandler;
  /**
   * Data handler for Question
   */
  private final QnaireDefBO qnaireDataBo;

  /**
   * @param qnaireDefBo QnaireDefBo
   * @param respDataHandler QnaireRespEditorDataHandler
   */
  public QuestionColumnFilterMatcher(final QnaireDefBO qnaireDefBo, final QnaireRespEditorDataHandler respDataHandler) {
    this.qnaireDataBo = qnaireDefBo;
    this.respDataHandler = respDataHandler;
  }

  /**
   * Set filter text
   *
   * @param textToFilter filter text
   * @param setPattern pattern
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if ((textToFilter == null) || "".equals(textToFilter) || !setPattern) {
      return;
    }

    String regExp = textToFilter.replace("\n", "").replace("\r", "");

    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition.
    if (subTexts.length == 0) {
      return;
    }

    // All special characters in the filter text except '*' are escaped
    final StringBuilder sbRegExp = new StringBuilder();
    for (String curString : subTexts) {
      if (!"".equals(curString)) {
        sbRegExp.append(".*");
        sbRegExp.append(Pattern.quote(curString));
      }
      sbRegExp.append(".*");
    }
    regExp = sbRegExp.toString();

    this.pattern1 = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);

    // Word level matching is not required if filter text starts with '*'.
    if (regExp.startsWith(".*")) {
      this.pattern2Search = false;
    }
    else {
      this.pattern2Search = true;
      this.pattern2 = Pattern.compile(".* " + regExp, Pattern.CASE_INSENSITIVE);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final E element) {
    if (element instanceof Question) {
      Question question = (Question) element;
      return matchQuestion(question) || matchQuestionFlags(question);
    }
    else if (element instanceof RvwQnaireAnswer) {
      RvwQnaireAnswer rvwQnaireAns = (RvwQnaireAnswer) element;
      return matchResponseQuestion(this.respDataHandler.getQuestion(rvwQnaireAns.getQuestionId())) ||
          matchAnswer(rvwQnaireAns);
    }
    return false;
  }

  /**
   * @param element
   * @return
   */
  private boolean matchQuestionFlags(final Question ques) {
    if (this.qnaireDataBo.showMeasurement() && matchText(this.qnaireDataBo.getMeasurementUIString(ques.getId()))) {
      return true;
    }
    if (this.qnaireDataBo.showSeries() && matchText(this.qnaireDataBo.getSeriesUIString(ques.getId()))) {
      return true;
    }
    if (this.qnaireDataBo.showRemark() && matchText(this.qnaireDataBo.getRemarkUIString(ques.getId()))) {
      return true;
    }
    if (this.qnaireDataBo.showResult() && matchText(this.qnaireDataBo.getResultUIString(ques.getId()))) {
      return true;
    }

    return this.qnaireDataBo.showMeasure() && matchText(this.qnaireDataBo.getMeasureUIString(ques.getId()));
  }

  /**
   * @param rvwQnaireAns
   * @return
   */
  private boolean matchAnswer(final RvwQnaireAnswer rvwQnaireAns) {
    if (matchText(this.respDataHandler.getQuestionNumber(rvwQnaireAns.getQuestionId()))) {
      return true;
    }
    // ICDM-1987
    if (this.respDataHandler.showQnaireVersSeriesMaturity() &&
        matchText(this.respDataHandler.getSeriesUIString(rvwQnaireAns))) {
      return true;
    }
    if (this.respDataHandler.showQnaireVersMeasurement() &&
        matchText(this.respDataHandler.getMeasurementUIString(rvwQnaireAns))) {
      return true;
    }
    if (this.respDataHandler.showQnaireVersLinks() && matchText(this.respDataHandler.getLinkUIString(rvwQnaireAns))) {
      return true;
    }
    try {
      if (this.respDataHandler.showQnaireVersOpenPoints() &&
          matchText(this.respDataHandler.getOpenPointsUIString(rvwQnaireAns))) {
        return true;
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    if (this.respDataHandler.showQnaireVersRemarks() &&
        matchText(this.respDataHandler.getRemarksUIString(rvwQnaireAns))) {
      return true;
    }

    return this.respDataHandler.showQnaireVersResult() && matchText(this.respDataHandler
        .getQuestionResultOptionUIString(rvwQnaireAns.getQuestionId(), rvwQnaireAns.getSelQnaireResultOptID()));
  }

  private boolean matchResponseQuestion(final Question question) {
    return (null != question) && (matchText(question.getName()) || matchText(question.getDescription()));
  }

  /**
   * @param ques
   */
  private boolean matchQuestion(final Question ques) {
    if (matchText(this.qnaireDataBo.getQuestionNumber(ques.getId())) || matchText(ques.getName()) ||
        matchText(ques.getDescription())) {
      return true;
    }
    if (this.qnaireDataBo.showOpenPoints() && matchText(this.qnaireDataBo.getOpenPointsUIString(ques.getId()))) {
      return true;
    }
    if (this.qnaireDataBo.showLink() && matchText(this.qnaireDataBo.getLinkUIString(ques.getId()))) {
      return true;
    }
    if (this.qnaireDataBo.showResponsible() && matchText(this.qnaireDataBo.getResponsibleUIString(ques.getId()))) {
      return true;
    }

    return this.qnaireDataBo.showCompletionDate() &&
        matchText(this.qnaireDataBo.getCompletionDateUIString(ques.getId()));
  }

  /**
   * Match text
   *
   * @param text text
   * @return true if match
   */
  private final boolean matchText(final String text) {
    if (text == null) {
      return false;
    }
    String temp = text.replace("\n", "").replace("\r", "");
    final java.util.regex.Matcher matcher1 = this.pattern1.matcher(temp);
    if (matcher1.matches()) {
      return true;
    }
    // Word level matching is not required if filter text starts with '*'.
    if (!this.pattern2Search) {
      return false;
    }
    final java.util.regex.Matcher matcher2 = this.pattern2.matcher(temp);
    return matcher2.matches();
  }
}
