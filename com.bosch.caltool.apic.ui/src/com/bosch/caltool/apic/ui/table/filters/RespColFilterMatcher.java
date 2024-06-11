/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author apj4cob
 */
public class RespColFilterMatcher implements Matcher<Object> {

  /**
   * First pattern
   */
  private Pattern pattern1;
  /**
   * Second pattern
   */
  private Pattern pattern2;
  /**
   * Pattern to search
   */
  private boolean pattern2Search;
  private final WorkPkgResponsibilityBO workPkgResponsibilityBO;

  /**
   * @param workPkgResponsibilityBO WorkPkgResponsibilityBO
   */
  public RespColFilterMatcher(final WorkPkgResponsibilityBO workPkgResponsibilityBO) {
    super();
    this.workPkgResponsibilityBO = workPkgResponsibilityBO;
  }

  /**
   * @param textToFilter String
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {
    if ((textToFilter == null) || "".equals(textToFilter) || !setPattern) {
      return;
    }

    String regExp = textToFilter.replace("\n", "").replace("\r", "");

    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition
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
  public boolean matches(final Object element) {
    if (element instanceof A2lResponsibility) {
      final A2lResponsibility a2lResp = (A2lResponsibility) element;
      // Responsibility Type
      if (matchText(WpRespType.getType(a2lResp.getRespType()).getDispName())) {
        return true;
      }
      // Responsible Name
      if (matchText(a2lResp.getName())) {
        return true;
      }
      // First Name
      if (matchText(this.workPkgResponsibilityBO.getRespFirstName(a2lResp))) {
        return true;
      }
      // Last Name
      if (matchText(this.workPkgResponsibilityBO.getRespLastName(a2lResp))) {
        return true;
      }
      // Department Name
      if (matchText(this.workPkgResponsibilityBO.getRespDeptName(a2lResp))) {
        return true;
      }
      // Alias Name
      if (matchText(a2lResp.getAliasName())) {
        return true;
      }
      // Created Date
      if (matchText(a2lResp.getCreatedDate())) {
        return true;
      }
      // Deleted Flag
      if (matchText(CommonUtils.getBooleanCode(a2lResp.isDeleted()))) {
        return true;
      }
      // Modified Date
      if (matchText(a2lResp.getModifiedDate())) {
        return true;
      }
      // Created User
      if (matchText(a2lResp.getCreatedUser())) {
        return true;
      }

    }
    return false;
  }

  /**
   * Method which checks whether the passed in text matches against the patterns created in this class
   *
   * @param text
   * @return boolean
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
