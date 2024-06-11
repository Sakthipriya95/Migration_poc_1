/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.ui.editors.CompareFC2WPRowObject;
import com.bosch.caltool.icdm.ui.editors.FC2WPColumnDataMapper;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * CDR Result Column filter matcher<br>
 *
 * @author adn1cob
 * @param <E> Object
 */
public class FC2WPColumnFilterMatcher<E> implements Matcher<E> {

  /**
   * Number of dynamic cols
   */
  private static final int DYNAMIC_COL_COUNT = 13;
  /**
   * Start index of fc2wp mapping objects
   */
  private static final int START_INDEX = 2;
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
  private FC2WPMappingResult handler;


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
    if (element instanceof CompareFC2WPRowObject) {
      int colIndex = START_INDEX;
      FC2WPColumnDataMapper columnDataMapper = ((CompareFC2WPRowObject) element).getColumnDataMapper();
      while (colIndex < columnDataMapper.getColumnIndexFC2WPMap().size()) {
        FC2WPColumnFilterMatcher.this.handler = columnDataMapper.getColumnIndexFC2WPMapResult().get(colIndex);
        FC2WPMapping dataObj = columnDataMapper.getColumnIndexFC2WPMap().get(colIndex);
        if ((dataObj != null) && matchFilterText(dataObj)) {
          return true;
        }
        colIndex = colIndex + DYNAMIC_COL_COUNT;
      }

    }
    return false;
  }

  /**
   * @param dataObj FC2WPMapping
   * @return true is text matches
   */
  public boolean matchFilterText(final FC2WPMapping dataObj) {
    if (matchText(dataObj.getFunctionName())) {
      return true;
    }
    if (matchText(dataObj.getFunctionDesc())) {
      return true;
    }
    String key = dataObj.getFunctionName();
    if (matchText(this.handler.getWorkpackage(key))) {
      return true;
    }
    if (matchText(this.handler.getResource(key))) {
      return true;
    }
    if (matchText(this.handler.getWpIdMCR(key))) {
      return true;
    }
    if (matchText(this.handler.getBC(key))) {
      return true;
    }
    if (matchText(this.handler.getPTtypeUIString(key))) {
      return true;
    }
    if (matchContactAndCocText(dataObj, key)) {
      return true;
    }
    if (matchText(dataObj.getComments())) {
      return true;
    }
    if (matchText(dataObj.getFc2wpInfo())) {
      return true;
    }
    if (matchText(this.handler.getIsInICDMA2LUIString(key))) {
      return true;
    }
    if (matchText(this.handler.getIsDeletedUIString(key))) {
      return true;
    }
    if (matchText(this.handler.isFcWithParams(key))) {
      return true;
    }
    if (matchText(this.handler.setDateFormat(dataObj.getCreatedDate()))) {
      return true;
    }
    if (null != dataObj.getModifiedDate()) {
      return (matchText(this.handler.setDateFormat(dataObj.getModifiedDate())));
    }
    return false;
  }

  /**
   * @param dataObj
   * @param key
   */
  private boolean matchContactAndCocText(final FC2WPMapping dataObj, final String key) {
    if (matchText(this.handler.getFirstContactEffective(key))) {
      return true;
    }
    if (matchText(this.handler.getSecondContactEffective(key))) {
      return true;
    }
    if (matchText(this.handler.getIsCoCAgreedUIString(key))) {
      return true;
    }
    if ((dataObj.getAgreeWithCocDate() != null) && matchText(DateUtil.getFormattedDate(DateFormat.DATE_FORMAT_05,
        DateUtil.getCalendarFromUTCDate(dataObj.getAgreeWithCocDate())))) {
      return true;
    }
    return (matchText(this.handler.getAgreeWithCocRespUserDisplay(key)));
  }

  /**
   * Match text
   *
   * @param text text
   * @return true if match
   */
  private final boolean matchText(String text) {
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
