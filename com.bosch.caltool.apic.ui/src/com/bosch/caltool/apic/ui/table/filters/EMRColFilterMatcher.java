/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * The Class EMRColFilterMatcher.
 *
 * @author gge6cob
 * @param <E> the element type
 */
public class EMRColFilterMatcher<E> implements Matcher<E> {

  /** Pattern. */
  private Pattern pattern1;

  /** Pattern to search. */
  private boolean pattern2Search;

  /** Pattern. */
  private Pattern pattern2;

  /**
   * Set filter text.
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

    this.pattern1 = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);

    // Word level matching is not required if filter text starts with '*'.
    if (regExp.startsWith(".*")) {
      this.pattern2Search = false;
    }
    else {
      this.pattern2Search = true;
      this.pattern2 = Pattern.compile(".* " + regExp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final E element) {
    if (element instanceof EmrFileMapping) {
      // get the row object
      EmrFileMapping data = (EmrFileMapping) element;
      if (matchText(convertToText(data.getEmrFile()))) {
        return true;
      }
      if (matchText(getDateFormat(data.getEmrFile().getCreatedDate()))) {
        return true;
      }
      // to match with the string in nat table variants column
      if (matchText(getPIDCVariantUIString(data.getEmrFile().getIsVariant(), data.getVariantSet()))) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param isVariant is applicable for variant/pidc
   * @param variantSet variants
   * @return ui string
   */
  public String getPIDCVariantUIString(final boolean isVariant, final Set<PidcVariant> variantSet) {
    if (!isVariant) {
      return "<valid for all variants>";
    }
    if (CommonUtils.isNotEmpty(variantSet)) {
      return variantSet.stream().map(PidcVariant::getName).sorted().collect(Collectors.joining(","));
    }
    return null;
  }

  /**
   * Converts class fields to text for filter search matching
   *
   * @param emrFile
   * @return
   */
  private String convertToText(final EmrFile emrFile) {
    return emrFile.getName() + "|" + emrFile.getDescription() + "|" + getValue(emrFile.getDeletedFlag()) + "|" +
        getValue(!emrFile.getLoadedWithoutErrorsFlag());
  }

  /**
   * Match text.
   *
   * @param text String
   * @return true if the text matches with the entered string
   */
  private final boolean matchText(final String text) {
    if (text == null) {
      return false;
    }

    // check if the pattern 1 matches with the text
    final java.util.regex.Matcher matcher1 = this.pattern1.matcher(text);
    if (matcher1.matches()) {
      return true;
    }

    // Word level matching is not required if filter text starts with '*'.
    if (!this.pattern2Search) {
      return false;
    }

    // check if the pattern 2 matches with the text
    final java.util.regex.Matcher matcher2 = this.pattern2.matcher(text);
    return matcher2.matches();
  }

  /**
   * Returns string for boolean
   *
   * @param isVariant
   * @return
   */
  private String getValue(final boolean data) {
    return data ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * Returns string for date
   *
   * @param agreeWithCocDate
   * @return
   */
  private String getDateFormat(final Date date) {
    String formattedDate = "";
    if (date != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, cal);
    }
    return formattedDate;
  }
}
