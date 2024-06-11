/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import java.util.regex.Pattern;

import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * CDR Result Column filter matcher<br>
 *
 * @author adn1cob
 * @param <E> Object
 */
public class ReviewResultColumnFilterMatcher<E> implements Matcher<E> {

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
  private final ReviewResultClientBO resultData;

  /**
   * @param paramListPage Review Result Param List Page
   */
  public ReviewResultColumnFilterMatcher(final ReviewResultParamListPage paramListPage) {
    this.resultData = ((ReviewResultEditorInput) (paramListPage.getEditorInput())).getResultData();
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

    if (element instanceof CDRResultParameter) {
      final CDRResultParameter cdrResParam = (CDRResultParameter) element;

      if (matchText(cdrResParam.getName())) {
        return true;
      }
      if (matchText(this.resultData.getFunctionParameter(cdrResParam).getLongName())) {
        return true;
      }
      // 496339 - Enable sorting, text filtering in the WP, Resp columns in Review Result Editor
      if (matchText(this.resultData.getWpName(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getRespName(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getFunctionParameter(cdrResParam).getpClassText())) {
        return true;
      }
      if (matchText(this.resultData.getFunctionParameter(cdrResParam).getCodeWord())) {
        return true;
      }
      if (matchText(this.resultData.getFunctionParameter(cdrResParam).getIsBitWise())) {
        return true;
      }
      if (matchText(cdrResParam.getBitwiseLimit())) {
        return true;
      }
      if (matchText(cdrResParam.getHint())) {
        return true;
      }
      if (matchText(this.resultData.getFunctionName(cdrResParam))) {
        return true;
      }
      if (matchText(String.valueOf(cdrResParam.getLowerLimit()))) {
        return true;
      }
      if (matchText(String.valueOf(cdrResParam.getUpperLimit()))) {
        return true;
      }
      if (matchText(this.resultData.getRefValueString(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getParentRefValue(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getParentCheckedValue(cdrResParam))) {
        return true;
      }

      // ICDM-2151
      if (matchText(cdrResParam.getRefUnit())) {
        return true;
      }
      if (matchText(this.resultData.getReadyForSeriesStr(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getExactMatchUiStr(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getCheckedValueString(cdrResParam))) {
        return true;
      }

      // ICDM-2151
      if (matchText(cdrResParam.getCheckUnit())) {
        return true;
      }
      if (matchText(this.resultData.getResult(cdrResParam))) {
        return true;
      }
      if (matchText(cdrResParam.getRvwComment())) {
        return true;
      }
      if (matchText(this.resultData.getHistoryState(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getHistoryUser(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getHistoryDate(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getHistoryContext(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getHistoryProject(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getHistoryTargetVariant(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getHistoryTestObject(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getHistoryProgramIdentifier(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getHistoryDataIdentifier(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getHistoryRemark(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getScoreUIType(cdrResParam))) {
        return true;
      }
      if (matchText(this.resultData.getScoreDescription(cdrResParam))) {
        return true;
      }

    }
    return false;
  }

  /**
   * Match text
   *
   * @param input input text
   * @return true if match
   */
  private final boolean matchText(final String input) {
    String text = input;
    if (text == null) {
      return false;
    }
    text = text.replace("\n", "").replace("\r", "");
    final java.util.regex.Matcher matcher1 = this.pattern1.matcher(text);
    if (matcher1.matches()) {
      return true;
    }

    // Word level matching is not required if filter text starts with '*'.
    if (!this.pattern2Search) {
      return false;
    }

    final java.util.regex.Matcher matcher2 = this.pattern2.matcher(text);

    return matcher2.matches();
  }
}
