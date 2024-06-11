/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.review;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.cdr.jpa.bo.RuleSetParameter;
import com.bosch.caltool.icdm.common.util.DelimiterJoinerCustomArrayList;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_EXCEPTION_TYPE;


/**
 * @author rgo7cob
 */
public class CaldataReviewSummary {

  /**
   * Review Exception Object
   */
  private Exception reviewExceptionObj;

  /**
   * Cdr data obj.
   */
  private final CDRData cdrData;

  /**
   * Boolean indicating review having exception
   */
  private boolean reviewHasExp;

  // ICDM-2477
  // Set to hold parameters which are not present in the rule set
  /**
   *
   */
  private final Set<String> paramNotInRuleset = new TreeSet<String>();

  // ICDM-2477
  // Set to hold parameters which are present in the rule set and has no rule
  /**
   *
   */
  private final Set<String> paramWithoutRule = new TreeSet<String>();

  /**
   * Cdr Exception type
   */
  private CDR_EXCEPTION_TYPE cdrException;

  /**
   * list of invalid cdr input files in the UI
   */
  @SuppressWarnings("unchecked")
  private List<String> listOfSelectedFiles = new DelimiterJoinerCustomArrayList(true, "\n");

  /**
   * @param cdrData cdrData
   */
  public CaldataReviewSummary(final CDRData cdrData) {
    this.cdrData = cdrData;
  }

  /**
   * @return the reviewHasExp
   */
  public boolean isReviewHasExp() {
    return this.reviewHasExp;
  }


  /**
   * @param reviewHasExp the reviewHasExp to set
   */
  public void setExceptioninReview(final boolean reviewHasExp) {
    this.reviewHasExp = reviewHasExp;
  }


  /**
   * @return the cdrException
   */
  public CDR_EXCEPTION_TYPE getCdrException() {
    return this.cdrException;
  }


  /**
   * @param cdrException the cdrException to set
   */
  public void setCdrException(final CDR_EXCEPTION_TYPE cdrException) {
    this.cdrException = cdrException;
  }


  /**
   * @return the reviewExceptionObj
   */
  public Exception getReviewExceptionObj() {
    return this.reviewExceptionObj;
  }


  /**
   * @param reviewExceptionObj the reviewExceptionObj to set
   */
  public void setReviewExceptionObj(final Exception reviewExceptionObj) {
    this.reviewExceptionObj = reviewExceptionObj;
  }


  /**
   * @return the noOfReviewedFunctions
   */
  public int getNoOfReviewedParam() {
    int parametersReviewed = 0;
    Set<?> cdrFuncParams = this.cdrData.getCdrFuncParams();
    if (cdrFuncParams == null) {
      return parametersReviewed;
    }

    return cdrFuncParams.size();
  }


  /**
   * @return the noOfReviewedParam
   */
  public int getNoOfReviewedFunctions() {
    int fucnsReviewed = 0;
    SortedSet<?> cdrFunctionsList = this.cdrData.getCdrFunctionsList();
    if ((cdrFunctionsList == null) || cdrFunctionsList.isEmpty()) {
      return fucnsReviewed;
    }
    fucnsReviewed = cdrFunctionsList.size();
    return fucnsReviewed;
  }

  // ICDM-1726
  /**
   * @return no of not reviewd params
   */
  public int getNoOfNotReviewedParams() {
    int paramsToBeReviewed = this.cdrData.getCalDataMap().size();
    int paramsReviewed = getNoOfReviewedParam();
    return paramsToBeReviewed - paramsReviewed;
  }

  // ICDM-2477
  /**
   * @return no of not reviewd params
   */
  public int getNoOfNotRvwdParamsInA2l() {

    Set<String> paramsRvwd = new TreeSet<String>();
    Set<String> paramsNotRvwd = new TreeSet<String>();
    Set<String> a2lParams = new TreeSet<String>();
    Set<String> paramsNotInA2L = new TreeSet<String>();

    for (Object obj : this.cdrData.getCdrFuncParams()) {
      if (obj instanceof RuleSetParameter) {
        paramsRvwd.add(((RuleSetParameter) obj).getName());
      }
    }
    for (String paramtoBeRvwd : this.cdrData.getCalDataMap().keySet()) {
      if (!paramsRvwd.contains(paramtoBeRvwd)) {
        paramsNotRvwd.add(paramtoBeRvwd);
      }
    }
    for (Characteristic a2lParam : this.cdrData.getA2lEditorDP().getA2lFileInfo().getAllSortedLabels(true)) {
      a2lParams.add(a2lParam.getName());
    }

    for (String ntRvwdParam : paramsNotRvwd) {
      if (!a2lParams.contains(ntRvwdParam.trim())) {
        paramsNotInA2L.add(ntRvwdParam);
      }
    }
    return paramsNotInA2L.size();
  }

  // ICDM-2477
  /**
   * @return no of not reviewd params which are not in rule set
   */
  public int getNoOfNotRvwdParamsInRuleset() {
    return this.paramNotInRuleset.size();

  }

  /**
   * @return the listOfSelectedFiles
   */
  public List<String> getListOfSelectedFiles() {
    return this.listOfSelectedFiles;
  }

  /**
   * @param listOfSelectedFiles the listOfSelectedFiles to set
   */
  public void setListOfSelectedFiles(final List<String> listOfSelectedFiles) {
    this.listOfSelectedFiles = listOfSelectedFiles;
  }

  // ICDM-2477
  /**
   * @return the paramNotInRuleset
   */
  public Set<String> getParamNotInRuleset() {
    return this.paramNotInRuleset;
  }

  // ICDM-2477
  /**
   * @return the paramWithoutRule
   */
  public Set<String> getParamWithoutRule() {
    return this.paramWithoutRule;
  }

  // ICDM-2477
  /**
   * @return no of not reviewd params which which does not have a rule
   */
  public int getNoOfNotRvwdParamsNoRule() {
    return this.paramWithoutRule.size();
  }
}
