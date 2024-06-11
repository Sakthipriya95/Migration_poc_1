/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * The Class WPLabelAssignToolBarFilter.
 *
 * @author apj4cob
 */
public class WPLabelAssignToolBarFilter extends AbstractViewerFilter {

  /** The is responsible assigned. */
  private boolean isResponsibleAssigned = true;

  /** The is responsible not assigned. */
  private boolean isResponsibleNotAssigned = true;

  /** The is responsible inherited. */
  private boolean isResponsibleInherited = true;

  /** The is wp name at cust assigned. */
  private boolean isWpNameAtCustAssigned = true;

  /** The is wp name at cust not assigned. */
  private boolean isWpNameAtCustNotAssigned = true;

  /** The is wp name at cust inherited. */
  private boolean isWpNameAtCustInherited = true;

  /** The is WP assigned. */
  private boolean isWPAssigned = true;

  /** The is WP not assigned. */
  private boolean isWPNotAssigned = true;

  /** The with lab param. */
  private boolean withLabParam = true;

  /** The without lab param. */
  private boolean withoutLabParam = true;
  /** The compliance flag. */
  private boolean complianceFlag = true;
  /** The non compliance flag. */
  private boolean nonComplianceFlag = true;
  /**
   * Black List Flag initially True since the tool bar action is checked
   */
  private boolean blackListFlag = true;
  /**
   * Non Black List Flag initially True since the tool bar action is checked
   */
  private boolean nonBlackListFlag = true;
  /** is Variant Group Filter. */
  private boolean isVariantGrp = true;
  /** is not Variant Group Filter. */
  private boolean isNotVariantGrp = true;
  /** The qSSD Flag. */
  private boolean qSSDFlag = true;
  /** The non qSSD Flag. */
  private boolean nonQSSDFlag = true;

  /** The a 2 l WP info BO. */
  private final A2LWPInfoBO a2lWPInfoBO;

  /**
   * true if read only flag is applied
   */
  private boolean readOnlyFlag = true;

  /**
   * true if not read only flag is applied
   */
  private boolean notReadOnlyFlag = true;

  private boolean dependentParam = true;

  private boolean notDepnParam = true;


  /**
   * Instantiates a new WP label assign tool bar filter.
   *
   * @param a2lWPInfoBO A2LWPInfoBO
   */
  public WPLabelAssignToolBarFilter(final A2LWPInfoBO a2lWPInfoBO) {
    super();
    this.a2lWPInfoBO = a2lWPInfoBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    A2LWpParamInfo rowObject = (A2LWpParamInfo) element;
    if (!filterComplianceParam(rowObject)) {
      return false;
    }
    if (!filterNonComplianceParam(rowObject)) {
      return false;
    }
    if (!filterQSSDParam(rowObject)) {
      return false;
    }
    if (!filterNonQSSDParam(rowObject)) {
      return false;
    }
    if (!filterBlackListParam(rowObject)) {
      return false;
    }
    if (!filterNonBlackListParam(rowObject)) {
      return false;
    }
    if (!filterWpAssigned(rowObject)) {
      return false;
    }
    if (!filterRespAssigned(rowObject)) {
      return false;
    }
    if (!filterNameAtCustomerAssigned(rowObject)) {
      return false;
    }
    if (!filterLABfile(rowObject)) {
      return false;
    }
    if (!filterVariantGrp(rowObject)) {
      return false;
    }
    if (filterReadOnly(rowObject)) {
      return false;
    }
    if (filterNotReadOnly(rowObject)) {
      return false;
    }
    if (filterDepnParam(rowObject)) {
      return false;
    }
    if (filterNonDepnParam(rowObject)) {
      return false;
    }
    return filterHideVariantGrp(rowObject);
  }


  /**
   * @param rowObject
   * @return
   */
  private boolean filterNonDepnParam(final A2LWpParamInfo rowObject) {
    return !this.notDepnParam && !rowObject.isDependentParameter();
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean filterDepnParam(final A2LWpParamInfo rowObject) {
    return !this.dependentParam && rowObject.isDependentParameter();
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean filterNotReadOnly(final A2LWpParamInfo rowObject) {
    return !this.notReadOnlyFlag && !rowObject.isReadOnly();
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean filterReadOnly(final A2LWpParamInfo rowObject) {
    return !this.readOnlyFlag && rowObject.isReadOnly();
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean filterQSSDParam(final A2LWpParamInfo rowObject) {
    /**
     * QSSD Parameter filter Action Unchecked then do not show QSSD parameters
     */
    return !(!isqSSDFlag() && rowObject.isQssdParameter());
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean filterNonQSSDParam(final A2LWpParamInfo rowObject) {
    /**
     * QSSD Parameter filter Action Unchecked then do not show non QSSD parameters
     */
    return !(!isNonQSSDFlag() && !rowObject.isQssdParameter());
  }

  /**
   * @param rowObject A2LWpParamInfo
   * @return boolean
   */
  public boolean filterNonComplianceParam(final A2LWpParamInfo rowObject) {
    // ICDM-2439
    /**
     * Non Compliance filter Action Unchecked then do not show compliance parameters
     */
    return !(!isNonComplianceFlag() && !rowObject.isComplianceParam());
  }

  /**
   * @param rowObject A2LWpParamInfo
   * @return boolean
   */
  public boolean filterComplianceParam(final A2LWpParamInfo rowObject) {
    // ICDM-2439
    /**
     * Compliance filter Action Unchecked then do not show compliance parameters
     */
    return !(!isComplianceFlag() && rowObject.isComplianceParam());
  }

  /**
   * @param rowObject A2LWpParamInfo
   * @return boolean
   */
  public boolean filterNonBlackListParam(final A2LWpParamInfo rowObject) {
    return !(!isNonBlackListFlag() && !rowObject.isBlackList());
  }

  /**
   * @param rowObject A2LWpParamInfo
   * @return boolean
   */
  public boolean filterBlackListParam(final A2LWpParamInfo rowObject) {
    return !(!isBlackListFlag() && rowObject.isBlackList());
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean filterNameAtCustomerAssigned(final A2LWpParamInfo rowObject) {

    if (!isWpNameAtCustAssigned() && (rowObject.hasNameAtCustAssigned())) {
      return false;
    }

    if (!isWpNameAtCustNotAssigned() && (rowObject.getWpNameCust() == null)) {
      return false;
    }

    return !(!isWpNameAtCustInherited() && (rowObject.isWpNameInherited()) && (rowObject.getWpNameCust() != null));
  }

  /**
   * Filter LA bfile.
   *
   * @param element the element
   * @return true, if successful
   */
  private boolean filterLABfile(final A2LWpParamInfo element) {
    /**
     * isWithLabParam flag unchecked then do not show labels in LAB file
     */
    if (!isWithLabParam() && (element.isLABParam())) {
      return false;
    }

    /**
     * isWithoutLabParam flag unchecked then do not show labels not in LAB file
     */
    return !(!isWithoutLabParam() && !(element.isLABParam()));
  }

  /**
   * Filter wp assigned.
   *
   * @param rowObject the row object
   * @return true, if successful
   */
  private boolean filterWpAssigned(final A2LWpParamInfo rowObject) {
    A2lWpResponsibility a2lWpResponsibility =
        this.a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap().get(rowObject.getWpRespId());

    if (!isWPAssigned() && (rowObject.getWpRespId() != null) &&
        !ApicConstants.DEFAULT_A2L_WP_NAME.equals(a2lWpResponsibility.getName())) {
      return false;
    }
    return !(!isWPNotAssigned() && ApicConstants.DEFAULT_A2L_WP_NAME.equals(a2lWpResponsibility.getName()));
  }

  /**
   * Filter resp assigned.
   *
   * @param rowObject the row object
   * @return true, if successful
   */
  private boolean filterRespAssigned(final A2LWpParamInfo rowObject) {

    if (!isResponsibleAssigned() && (rowObject.hasRespAssigned())) {
      return false;
    }

    A2lResponsibility a2lResponsibility =
        this.a2lWPInfoBO.getA2lResponsibilityModel().getA2lResponsibilityMap().get(rowObject.getA2lRespId());
    boolean defaultResponsibility = A2lResponsibilityCommon.isDefaultResponsibility(a2lResponsibility);

    if (!isResponsibleNotAssigned() && rowObject.isWpRespInherited() && !(rowObject.hasRespAssigned()) &&
        defaultResponsibility) {
      return false;
    }
    return !(!isResponsibleInherited() && (rowObject.isWpRespInherited()) && !defaultResponsibility);
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean filterVariantGrp(final A2LWpParamInfo rowObject) {
    return !(!isVariantGrp() && ((null != this.a2lWPInfoBO.getSelectedA2lVarGroup()) &&
        this.a2lWPInfoBO.isParamMappedToSelectedVarGrp(rowObject)));
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean filterHideVariantGrp(final A2LWpParamInfo rowObject) {
    return !(!isNotVariantGrp() && (null != this.a2lWPInfoBO.getSelectedA2lVarGroup()) &&
        !this.a2lWPInfoBO.isParamMappedToSelectedVarGrp(rowObject));

  }

  /**
   * The Class WpLabelAssignNatToolbarMatcher.
   *
   * @param <E> the element type
   */
  private class WpLabelAssignNatToolbarMatcher<E> implements Matcher<E> {

    /**
     * Singleton instance of TrueMatcher.
     *
     * @param element the element
     * @return true, if successful
     */
    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof A2LWpParamInfo) {
        return selectElement(element);
      }
      return true;
    }
  }

  /**
   * Gets the tool bar matcher.
   *
   * @return Matcher
   */
  public Matcher<A2LWpParamInfo> getToolBarMatcher() {
    return new WpLabelAssignNatToolbarMatcher<>();
  }

  /**
   * Checks if is responsible assigned.
   *
   * @return the isResponsibleAssigned
   */
  public boolean isResponsibleAssigned() {
    return this.isResponsibleAssigned;
  }

  /**
   * Sets the responsible assigned.
   *
   * @param isResponsibleAssigned the isResponsibleAssigned to set
   */
  public void setResponsibleAssigned(final boolean isResponsibleAssigned) {
    this.isResponsibleAssigned = isResponsibleAssigned;
  }

  /**
   * Checks if is responsible not assigned.
   *
   * @return the isResponsibleNotAssigned
   */
  public boolean isResponsibleNotAssigned() {
    return this.isResponsibleNotAssigned;
  }

  /**
   * Sets the responsible not assigned.
   *
   * @param isResponsibleNotAssigned the isResponsibleNotAssigned to set
   */
  public void setResponsibleNotAssigned(final boolean isResponsibleNotAssigned) {
    this.isResponsibleNotAssigned = isResponsibleNotAssigned;
  }

  /**
   * Checks if is WP assigned.
   *
   * @return the isWPAssigned
   */
  public boolean isWPAssigned() {
    return this.isWPAssigned;
  }

  /**
   * Sets the WP assigned.
   *
   * @param isWPAssigned the isWPAssigned to set
   */
  public void setWPAssigned(final boolean isWPAssigned) {
    this.isWPAssigned = isWPAssigned;
  }

  /**
   * Checks if is WP not assigned.
   *
   * @return the isWPNotAssigned
   */
  public boolean isWPNotAssigned() {
    return this.isWPNotAssigned;
  }

  /**
   * Sets the WP not assigned.
   *
   * @param isWPNotAssigned the isWPNotAssigned to set
   */
  public void setWPNotAssigned(final boolean isWPNotAssigned) {
    this.isWPNotAssigned = isWPNotAssigned;
  }

  /**
   * Checks if is responsible inherited.
   *
   * @return the isResponsibleInherited
   */
  public boolean isResponsibleInherited() {
    return this.isResponsibleInherited;
  }


  /**
   * Sets the responsible inherited.
   *
   * @param isResponsibleInherited the isResponsibleInherited to set
   */
  public void setResponsibleInherited(final boolean isResponsibleInherited) {
    this.isResponsibleInherited = isResponsibleInherited;
  }

  /**
   * Checks if is without lab param.
   *
   * @return the withoutLabParam flag
   */
  public boolean isWithoutLabParam() {
    return this.withoutLabParam;
  }


  /**
   * Sets the without lab param.
   *
   * @param withoutLabParam the withoutLabParam to set
   */
  public void setWithoutLabParam(final boolean withoutLabParam) {
    this.withoutLabParam = withoutLabParam;
  }


  /**
   * Checks if is with lab param.
   *
   * @return the withLabParam flag
   */
  public boolean isWithLabParam() {
    return this.withLabParam;
  }


  /**
   * Sets the with lab param.
   *
   * @param withLabParam the withLabParam to set
   */
  public void setWithLabParam(final boolean withLabParam) {
    this.withLabParam = withLabParam;
  }


  /**
   * Checks if is wp name at cust assigned.
   *
   * @return the isWpNameAtCustAssigned
   */
  public boolean isWpNameAtCustAssigned() {
    return this.isWpNameAtCustAssigned;
  }


  /**
   * Sets the wp name at cust assigned.
   *
   * @param isWpNameAtCustAssigned the isWpNameAtCustAssigned to set
   */
  public void setWpNameAtCustAssigned(final boolean isWpNameAtCustAssigned) {
    this.isWpNameAtCustAssigned = isWpNameAtCustAssigned;
  }


  /**
   * Checks if is wp name at cust not assigned.
   *
   * @return the isWpNameAtCustNotAssigned
   */
  public boolean isWpNameAtCustNotAssigned() {
    return this.isWpNameAtCustNotAssigned;
  }


  /**
   * Sets the wp name at cust not assigned.
   *
   * @param isWpNameAtCustNotAssigned the isWpNameAtCustNotAssigned to set
   */
  public void setWpNameAtCustNotAssigned(final boolean isWpNameAtCustNotAssigned) {
    this.isWpNameAtCustNotAssigned = isWpNameAtCustNotAssigned;
  }


  /**
   * Checks if is wp name at cust inherited.
   *
   * @return the isWpNameAtCustInherited
   */
  public boolean isWpNameAtCustInherited() {
    return this.isWpNameAtCustInherited;
  }


  /**
   * Sets the wp name at cust inherited.
   *
   * @param isWpNameAtCustInherited the isWpNameAtCustInherited to set
   */
  public void setWpNameAtCustInherited(final boolean isWpNameAtCustInherited) {
    this.isWpNameAtCustInherited = isWpNameAtCustInherited;
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

  /**
   * @return the isVariantGrp
   */
  public boolean isVariantGrp() {
    return this.isVariantGrp;
  }


  /**
   * @param isVariantGrp the isVariantGrp to set
   */
  public void setVariantGrp(final boolean isVariantGrp) {
    this.isVariantGrp = isVariantGrp;
  }

  /**
   * @return boolean
   */
  public boolean isNotVariantGrp() {
    return this.isNotVariantGrp;
  }


  /**
   * @param isNotVariantGrp boolean
   */
  public void setNotVariantGrp(final boolean isNotVariantGrp) {
    this.isNotVariantGrp = isNotVariantGrp;
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
   * @param readOnlyFlag boolean
   */
  public void setReadOnlyParam(final boolean readOnlyFlag) {
    this.readOnlyFlag = readOnlyFlag;
  }

  /**
   * @param notReadOnlyFlag boolean
   */
  public void setNotReadOnlyParam(final boolean notReadOnlyFlag) {
    this.notReadOnlyFlag = notReadOnlyFlag;
  }

  /**
   * @param dependentParam boolean
   */
  public void setDependentParam(final boolean dependentParam) {
    this.dependentParam = dependentParam;
  }

  /**
   * @param notDepnParam boolean
   */
  public void setNotDependentParam(final boolean notDepnParam) {
    this.notDepnParam = notDepnParam;
  }

}
