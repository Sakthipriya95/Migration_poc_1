/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.cocwp.IProjectCoCWP;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author RDP2COB
 */
public class PIDCCocWpToolBarFilters extends AbstractViewerFilter {

  private boolean newCocWp = true;
  private boolean usedNotDefined = true;
  private boolean used = true;
  private boolean notUsed = true;
  private boolean variantLevel = true;
  private boolean pidcLevel = true;
  private boolean allCocWp = true;


  /**
   *
   */
  public PIDCCocWpToolBarFilters() {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /**
   * @return
   */
  public Matcher getToolBarMatcher() {
    return new PIDCToolBarParamMatcher<PidcNattableRowObject>();
  }

  private class PIDCToolBarParamMatcher<E> implements Matcher<E> {

    /** Singleton instance of TrueMatcher. */


    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof IProjectCoCWP) {
        return selectElement(element);
      }
      return true;
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    IProjectCoCWP cocWpRowObject = (IProjectCoCWP) element;
    boolean flag = true;

    if (!filterDeleteCocWPs(cocWpRowObject)) {
      return false;
    }

    if (!filterNewCoCWp(cocWpRowObject) || !filterUsedNotDefFlagVal(cocWpRowObject)) {
      flag = false;
    }

    if (!filterRelevantFlagVal(cocWpRowObject) || !filterNotRelevantFlagVal(cocWpRowObject)) {
      flag = false;
    }

    if (!filterVariantLevelFlagVal(cocWpRowObject)) {
      flag = false;
    }

    return flag;
  }


  private boolean filterDeleteCocWPs(final IProjectCoCWP coCWP) {
    return PIDCCocWpToolBarFilters.this.isAllCocWp() || !coCWP.isDeleted();
  }


  private boolean filterNewCoCWp(final IProjectCoCWP cocWpRowObject) {
    return (PIDCCocWpToolBarFilters.this.isNewCocWp() || CommonUtils.isNotNull(cocWpRowObject.getUsedFlag()));
  }

  private boolean filterUsedNotDefFlagVal(final IProjectCoCWP cocWpRowObject) {
    if (CommonUtils.isNotNull(cocWpRowObject.getUsedFlag())) {
      return (PIDCCocWpToolBarFilters.this.isWpUsedNotDefined() || !(cocWpRowObject.getUsedFlag().equals("?")));
    }
    return true;
  }

  private boolean filterRelevantFlagVal(final IProjectCoCWP cocWpRowObject) {
    if (CommonUtils.isNotNull(cocWpRowObject.getUsedFlag())) {
      return (PIDCCocWpToolBarFilters.this.isUsed() || !(cocWpRowObject.getUsedFlag().equals("Y")));
    }
    return true;
  }

  private boolean filterNotRelevantFlagVal(final IProjectCoCWP cocWpRowObject) {
    if (CommonUtils.isNotNull(cocWpRowObject.getUsedFlag())) {
      return (PIDCCocWpToolBarFilters.this.isNotUsed() || !(cocWpRowObject.getUsedFlag().equals("N")));
    }
    return true;
  }

  private boolean filterVariantLevelFlagVal(final IProjectCoCWP cocWpRowObject) {
    boolean flag = true;
    if ((!PIDCCocWpToolBarFilters.this.isPidcLevel() && !cocWpRowObject.isAtChildLevel()) ||
        (!PIDCCocWpToolBarFilters.this.isVariantLevel() && cocWpRowObject.isAtChildLevel())) {
      flag = false;
    }
    return flag;
  }

  /**
   * @return the newPackage
   */
  public boolean isNewCocWp() {
    return this.newCocWp;
  }

  /**
   * @return the attrUsedNotDefined
   */
  public boolean isWpUsedNotDefined() {
    return this.usedNotDefined;
  }


  /**
   * @param attrUsedNotDefined the attrUsedNotDefined to set
   */
  public void setWpUsedNotDefined(final boolean attrUsedNotDefined) {
    this.usedNotDefined = attrUsedNotDefined;
  }


  /**
   * @return the relevant
   */
  public boolean isUsed() {
    return this.used;
  }

  /**
   * @param relevant
   */
  public void setUsed(final boolean relevant) {
    this.used = relevant;
  }


  /**
   * @return the notRelevant
   */
  public boolean isNotUsed() {
    return this.notUsed;
  }

  /**
   * @param notRelevant
   */
  public void setNotUsed(final boolean notRelevant) {
    this.notUsed = notRelevant;
  }

  /**
   * @return the variantLevel
   */
  public boolean isVariantLevel() {
    return this.variantLevel;
  }

  /**
   * @param variantLevel
   */
  public void setVariantLevel(final boolean variantLevel) {
    this.variantLevel = variantLevel;
  }


  /**
   * @return the pidcLevel
   */
  public boolean isPidcLevel() {
    return this.pidcLevel;
  }

  /**
   * @param pidcLevel
   */
  public void setPidcLevel(final boolean pidcLevel) {
    this.pidcLevel = pidcLevel;
  }

  /**
   * @param newPackage the newPackage to set
   */
  public void setNewCocWp(final boolean newPackage) {
    this.newCocWp = newPackage;
  }


  /**
   * @return the allCocWp
   */
  public boolean isAllCocWp() {
    return this.allCocWp;
  }


  /**
   * @param allCocWp the allCocWp to set
   */
  public void setAllCocWp(final boolean allCocWp) {
    this.allCocWp = allCocWp;
  }

}
