/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author bru2cob
 */
public class A2lCompareToolBarFilters extends AbstractViewerFilter {

  /** The compliance flag. */
  private boolean complianceFlag = true;
  /** The non compliance flag. */
  private boolean nonComplianceFlag = true;
  /** The qSSD Flag. */
  private boolean qSSDFlag = true;
  /** The non qSSD Flag. */
  private boolean nonQSSDFlag = true;
  /** The param diff Flag. */
  private boolean paramDiff = true;
  /** The param non diff Flag. */
  private boolean paramNotDiff = true;
  private boolean readOnlyParam = true;

  private boolean notReadOnlyParam = true;
  /**
   * Black List Flag initially True since the tool bar action is checked
   */
  private boolean blackListFlag = true;
  /**
   * Non Black List Flag initially True since the tool bar action is checked
   */
  private boolean nonBlackListFlag = true;

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    A2lParamCompareRowObject compareRowObject = (A2lParamCompareRowObject) element;
    int colIndex;
    int totalColCount = compareRowObject.getA2lColumnDataMapper().getColumnIndexA2lWpDefVersIdMap().size() + 3;
    A2LWpParamInfo a2lParamInfoBo = null;
    for (colIndex = 3; colIndex < totalColCount; colIndex = colIndex + 6) {
      a2lParamInfoBo = compareRowObject.getParamInfo(colIndex);
      if (a2lParamInfoBo != null) {
        break;
      }
    }
    // filter compliance params
    if (!isComplianceFlag() && a2lParamInfoBo.isComplianceParam()) {
      return false;
    }
    // filter non compliance params
    if (!isNonComplianceFlag() && !a2lParamInfoBo.isComplianceParam()) {
      return false;
    }
    // filter qssd params
    if (!isqSSDFlag() && a2lParamInfoBo.isQssdParameter()) {
      return false;
    }
    // filter non qssd params
    if (!isNonQSSDFlag() && !a2lParamInfoBo.isQssdParameter()) {
      return false;
    }
    // filter param diff
    if (!isParamDiff() && compareRowObject.isComputedDiff()) {
      return false;
    }
    // filter param not diff
    if (!isParamNotDiff() && !compareRowObject.isComputedDiff()) {
      return false;
    }
    if (!isBlackListFlag() && a2lParamInfoBo.isBlackList()) {
      return false;
    }
    if (!isReadOnlyParam() && a2lParamInfoBo.isReadOnly()) {
      return false;
    }
    if (!isNotReadOnlyParam() && !a2lParamInfoBo.isReadOnly()) {
      return false;
    }
    if (!isNonBlackListFlag() && !a2lParamInfoBo.isBlackList()) {
      return false;
    }
    return true;
  }


  /**
   * Gets the tool bar matcher.
   *
   * @return Matcher
   */
  public Matcher<A2LWpParamInfo> getToolBarMatcher() {
    return new A2lCompareToolbarMatcher<>();
  }

  /**
   * The Class WpLabelAssignNatToolbarMatcher.
   *
   * @param <E> the element type
   */
  private class A2lCompareToolbarMatcher<E> implements Matcher<E> {

    /**
     * Singleton instance of TrueMatcher.
     *
     * @param element the element
     * @return true, if successful
     */
    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof A2lParamCompareRowObject) {
        return selectElement(element);
      }
      return true;
    }
  }


  /**
   * @return the complianceFlag
   */
  public boolean isComplianceFlag() {
    return this.complianceFlag;
  }


  /**
   * @param complianceFlag the complianceFlag to set
   */
  public void setComplianceFlag(final boolean complianceFlag) {
    this.complianceFlag = complianceFlag;
  }


  /**
   * @return the nonComplianceFlag
   */
  public boolean isNonComplianceFlag() {
    return this.nonComplianceFlag;
  }


  /**
   * @param nonComplianceFlag the nonComplianceFlag to set
   */
  public void setNonComplianceFlag(final boolean nonComplianceFlag) {
    this.nonComplianceFlag = nonComplianceFlag;
  }

  /**
   * @param qSSDFlag boolean
   */
  public void setqSSDFlag(final boolean qSSDFlag) {
    this.qSSDFlag = qSSDFlag;
  }

  /**
   * @return boolean
   */
  public boolean isqSSDFlag() {
    return this.qSSDFlag;
  }


  /**
   * @return boolean
   */
  public boolean isNonQSSDFlag() {
    return this.nonQSSDFlag;
  }


  /**
   * @param nonQSSDFlag boolean
   */
  public void setNonQSSDFlag(final boolean nonQSSDFlag) {
    this.nonQSSDFlag = nonQSSDFlag;
  }


  /**
   * @return the paramDiff
   */
  public boolean isParamDiff() {
    return this.paramDiff;
  }


  /**
   * @param paramDiff the paramDiff to set
   */
  public void setParamDiff(final boolean paramDiff) {
    this.paramDiff = paramDiff;
  }


  /**
   * @return the paramNotDiff
   */
  public boolean isParamNotDiff() {
    return this.paramNotDiff;
  }


  /**
   * @param paramNotDiff the paramNotDiff to set
   */
  public void setParamNotDiff(final boolean paramNotDiff) {
    this.paramNotDiff = paramNotDiff;
  }


  /**
   * @return the readOnlyParam
   */
  public boolean isReadOnlyParam() {
    return this.readOnlyParam;
  }


  /**
   * @return the notReadOnlyParam
   */
  public boolean isNotReadOnlyParam() {
    return this.notReadOnlyParam;
  }


  /**
   * @return the blackListFlag
   */
  public boolean isBlackListFlag() {
    return this.blackListFlag;
  }


  /**
   * @return the nonBlackListFlag
   */
  public boolean isNonBlackListFlag() {
    return this.nonBlackListFlag;
  }


  /**
   * @param readOnlyParam the readOnlyParam to set
   */
  public void setReadOnlyParam(final boolean readOnlyParam) {
    this.readOnlyParam = readOnlyParam;
  }


  /**
   * @param notReadOnlyParam the notReadOnlyParam to set
   */
  public void setNotReadOnlyParam(final boolean notReadOnlyParam) {
    this.notReadOnlyParam = notReadOnlyParam;
  }


  /**
   * @param blackListFlag the blackListFlag to set
   */
  public void setBlackListFlag(final boolean blackListFlag) {
    this.blackListFlag = blackListFlag;
  }


  /**
   * @param nonBlackListFlag the nonBlackListFlag to set
   */
  public void setNonBlackListFlag(final boolean nonBlackListFlag) {
    this.nonBlackListFlag = nonBlackListFlag;
  }

}
