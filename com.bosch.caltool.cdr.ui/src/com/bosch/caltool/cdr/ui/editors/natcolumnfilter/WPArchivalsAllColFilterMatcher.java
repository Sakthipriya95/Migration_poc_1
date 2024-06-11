/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.natcolumnfilter;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.WpArchival;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * All column matcher for WP Archivals List editor
 *
 * @author ukt1cob
 */
public class WPArchivalsAllColFilterMatcher<E extends WpArchival> implements Matcher<E> {

  private Pattern pattern1;
  private boolean pattern2Search;
  private Pattern pattern2;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final E arg0) {
    if (matchBaselinePidcVersVar(arg0)) {
      // return true if text matches with baseline Name, pidc Vers Name and variant Name
      return true;
    }
    // return true if status matches
    if (matchStatus(arg0)) {
      return true;
    }

    if (matchDateA2LNameVers(arg0)) {
      // return true if text matches with archival date,a2l name,variant name
      return true;
    }

    // return true if text matches with wp or resp name
    return (matchWPRespName(arg0));
  }


  /**
   * @param arg0
   * @return
   */
  private boolean matchStatus(final E arg0) {

    return matchText(getFileStatus(arg0));
  }

  /**
   * @param wpArchival
   * @return
   */
  private String getFileStatus(final WpArchival wpArchival) {

    if (CDRConstants.FILE_ARCHIVAL_STATUS.IN_PROGRESS.getDbType().equals(wpArchival.getFileArchivalStatus())) {
      return CDRConstants.FILE_ARCHIVAL_STATUS.IN_PROGRESS.getUiType();
    }
    else if (CDRConstants.FILE_ARCHIVAL_STATUS.COMPLETED.getDbType().equals(wpArchival.getFileArchivalStatus())) {
      return CDRConstants.FILE_ARCHIVAL_STATUS.COMPLETED.getUiType();
    }
    else if (CDRConstants.FILE_ARCHIVAL_STATUS.FAILED.getDbType().equals(wpArchival.getFileArchivalStatus())) {
      return CDRConstants.FILE_ARCHIVAL_STATUS.FAILED.getUiType();
    }
    else {
      return CDRConstants.FILE_ARCHIVAL_STATUS.NOT_AVAILABLE.getUiType();
    }
  }

  /**
   * @param arg0
   * @return true if text matches with wp and resp name
   */
  private boolean matchWPRespName(final E arg0) {
    return matchText(arg0.getWpName()) || matchText(arg0.getRespName());
  }

  /**
   * @param arg0
   * @return true if text matches with baseline Name, pidc Vers Name and variant Name
   */
  private boolean matchBaselinePidcVersVar(final E arg0) {
    return matchText(arg0.getPidcVersFullname()) || matchText(arg0.getVariantName()) ||
        matchText(arg0.getBaselineName());
  }

  /**
   * @param arg0
   * @return true if text matches with archival date,a2l name,variant name
   */
  private boolean matchDateA2LNameVers(final E arg0) {
    return matchText(arg0.getCreatedDate()) || matchText(arg0.getWpDefnVersName()) || matchText(arg0.getA2lFilename());
  }

  /**
   * @param textToFilter filter text
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if ((textToFilter == null) || CommonUIConstants.EMPTY_STRING.equals(textToFilter) || !setPattern) {
      return;
    }

    String regExp = textToFilter;

    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition.
    if (subTexts.length == 0) {
      return;
    }

    // All special characters in the filter text except '*' are escaped
    final StringBuilder sbRegExp = new StringBuilder();
    for (String curString : subTexts) {
      if (!CommonUIConstants.EMPTY_STRING.equals(curString)) {
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
   * @param text
   * @return true if the pattern matches the text
   */
  private final boolean matchText(final String text) {

    if (text == null) {
      return false;
    }

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
