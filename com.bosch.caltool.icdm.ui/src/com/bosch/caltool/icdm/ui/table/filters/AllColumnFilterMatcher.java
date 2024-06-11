/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.icdm.model.a2l.A2LWpRespExt;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * {@link AllColumnFilterMatcher} is used by the Filter Text widget present above the NatTable This class provides an
 * improved performance when compared with the default Text Matcher Editor provided by NatTable which creates
 * TextMatcherEditor for each column whereas this class creates matcher editor once for each row
 *
 * @author jvi6cob
 */
public class AllColumnFilterMatcher<E> implements Matcher<E> {


  private Pattern pattern1;
  private boolean pattern2Search;
  private Pattern pattern2;
  private final A2LFileInfoBO a2lFileInfoBO;

  /**
   * @param a2lFileInfoBO A2LFileInfoBO
   */
  public AllColumnFilterMatcher(final A2LFileInfoBO a2lFileInfoBO) {
    this.a2lFileInfoBO = a2lFileInfoBO;
  }

  /**
   * @param textToFilter text entered in type filter
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if ((textToFilter == null) || "".equals(textToFilter) || !setPattern) {
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
  public boolean matches(final E element) {

    if (element instanceof A2LParameter) {
      final A2LParameter paramElement = (A2LParameter) element;
      if (checkParamFunction(paramElement)) {
        return true;
      }
      return checkParamProps(paramElement);
    }
    else if (element instanceof A2LSystemConstantValues) {
      final A2LSystemConstantValues sysConstElement = (A2LSystemConstantValues) element;

      if (matchText(sysConstElement.getSysconName())) {
        return true;
      }
      if (matchText(sysConstElement.getSysconLongName())) {
        return true;
      }
      if (matchText(sysConstElement.getValue())) {
        return true;
      }
      if (matchText(sysConstElement.getValueDescription())) {
        return true;
      }

    }

    else if (element instanceof A2lWpObj) {
      return checkWorkPg((A2lWpObj) element);
    }

    else if (element instanceof A2LGroup) {
      return checkGrp((A2LGroup) element);
    }

    else if (element instanceof A2LWpRespExt) {
      if (((A2LWpRespExt) element).isA2lGrp()) {
        return checkGrpResp((A2LWpRespExt) element);
      }
      return checkWpResp((A2LWpRespExt) element);
    }
    return false;
  }

  /**
   * @param element
   * @return
   */
  private boolean checkWpResp(final A2LWpRespExt a2lWpResp) {
    if (a2lWpResp.getWorkPackage() != null) {
      return matchText(a2lWpResp.getWpResource()) || (matchText(a2lWpResp.getWorkPackage().getName())) ||
          (matchText(a2lWpResp.getWpNumMap().get(a2lWpResp.getWorkPackage().getName())) ||
              (matchText(a2lWpResp.getA2lWpResp().getWpRespEnum().getDispName())));
    }
    return false;
  }

  /**
   * @param element
   * @return
   */
  private boolean checkGrpResp(final A2LWpRespExt grpResp) {
    return (matchText(grpResp.getIcdmA2lGroup().getGrpName()) ||
        (matchText(grpResp.getIcdmA2lGroup().getGrpLongName())) ||
        (matchText(Integer.toString(
            this.a2lFileInfoBO.getA2lWpMapping().getA2lWpRespGrpLabelMap().containsKey(grpResp.getA2lWpResp().getId())
                ? this.a2lFileInfoBO.getA2lWpMapping().getA2lWpRespGrpLabelMap().get(grpResp.getA2lWpResp().getId())
                    .size()
                : 0)))) ||
        (matchText(grpResp.getA2lWpResp().getWpRespEnum().getDispName()));
  }

  /**
   * @param element
   * @return
   */
  private boolean checkGrp(final A2LGroup grp) {

    return matchText(grp.getGroupName()) || (matchText(grp.getGroupLongName())) ||
        (matchText(Integer.toString(grp.getLabelMap().size())));
  }

  /**
   * @param element
   * @return
   */
  private boolean checkWorkPg(final A2lWpObj wkpg) {
    return matchText(wkpg.getWpName()) || matchText(wkpg.getWpGroupName()) || matchText(wkpg.getWpNumber());
  }

  /**
   * check for the param def char name
   *
   * @param paramElement
   */
  private boolean checkParamFunction(final A2LParameter paramElement) {
    if (paramElement.getDefFunction() == null) {
      if (matchText(ApicConstants.UNASSIGNED_PARAM)) {
        return true;
      }
    }
    else if (matchText(paramElement.getDefFunction().getName())) {
      return true;
    }
    return false;
  }

  private boolean checkParamProps(final A2LParameter paramElement) {
    if (matchText(paramElement.getName())) {
      return true;
    }
    if (matchText(paramElement.getType())) {
      return true;
    }
    if (matchText(paramElement.getUnit())) {
      return true;
    }
    // Icdm-586
    if (matchText(paramElement.getPclassString())) {
      return true;
    }
    // Icdm-633
    if (matchText(paramElement.getCodeWordString())) {
      return true;
    }
    if (matchText(paramElement.getLongIdentifier())) {
      return true;
    }
    if ((paramElement.getCalData() != null) && (paramElement.getCalData().getCalDataPhy() != null) &&
        matchText(paramElement.getCalData().getCalDataPhy().getSimpleDisplayValue())) {
      return true;
    }
    return (paramElement.getStatus() != null) && matchText(paramElement.getStatus());
  }

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
