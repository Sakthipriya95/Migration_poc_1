/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author apj4cob
 */
public class A2lWPDefToolBarFilter extends AbstractViewerFilter {

  /** is Variant Group Filter. */
  private boolean isSelVariantGrp = true;
  /** is not Variant Group Filter. */
  private boolean isOtherVariantGrp = true;
  /** not mapped to VG. */
  private boolean notAtVGLevel = true;
  /** The a 2 l WP info BO. */
  private final A2LWPInfoBO a2lWPInfoBO;

  /**
   * @param a2lWPInfoBO A2LWPInfoBO
   */
  public A2lWPDefToolBarFilter(final A2LWPInfoBO a2lWPInfoBO) {
    super();
    this.a2lWPInfoBO = a2lWPInfoBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    A2lWpResponsibility rowObject = (A2lWpResponsibility) element;
    if (!filterSelVarGrpWp(rowObject)) {
      return false;
    }
    if (!filterOtherVarGrpWp(rowObject)) {
      return false;
    }
    return filterNotMappedToWp(rowObject);
  }

  /**
   * @return
   */
  private boolean filterNotMappedToWp(final A2lWpResponsibility rowObject) {
    return !(!isNotAtVGLevel() && (null != this.a2lWPInfoBO.getSelectedA2lVarGroup()) &&
        (null == rowObject.getVariantGrpId()));
  }

  /**
   * @return
   */
  private boolean filterOtherVarGrpWp(final A2lWpResponsibility rowObject) {
    return !(!isOtherVariantGrp() && (null != rowObject.getVariantGrpId()) &&
        !this.a2lWPInfoBO.isMappedToSelectedVarGrp(rowObject));
  }


  /**
   * @return
   */
  private boolean filterSelVarGrpWp(final A2lWpResponsibility rowObject) {
    return !(!isSelVariantGrp() && (null != this.a2lWPInfoBO.getSelectedA2lVarGroup()) &&
        this.a2lWPInfoBO.isMappedToSelectedVarGrp(rowObject));
  }

  /**
   * The Class A2lWPDefToolbarMatcher.
   *
   * @param <E> the element type
   */
  private class A2lWPDefToolbarMatcher<E> implements Matcher<E> {

    /**
     * Singleton instance of TrueMatcher.
     *
     * @param element the element
     * @return true, if successful
     */
    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof A2lWpResponsibility) {
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
  public Matcher<A2lWpResponsibility> getToolBarMatcher() {
    return new A2lWPDefToolbarMatcher<>();
  }

  /**
   * @return boolean
   */
  public boolean isSelVariantGrp() {
    return this.isSelVariantGrp;
  }

  /**
   * @param isSelVariantGrp boolean
   */
  public void setSelVariantGrp(final boolean isSelVariantGrp) {
    this.isSelVariantGrp = isSelVariantGrp;
  }

  /**
   * @return boolean
   */
  public boolean isOtherVariantGrp() {
    return this.isOtherVariantGrp;
  }

  /**
   * @param isOtherVariantGrp boolean
   */
  public void setOtherVariantGrp(final boolean isOtherVariantGrp) {
    this.isOtherVariantGrp = isOtherVariantGrp;
  }

  /**
   * @return boolean
   */
  public boolean isNotAtVGLevel() {
    return this.notAtVGLevel;
  }

  /**
   * @param notAtVGLevel boolean
   */
  public void setNotAtVGLevel(final boolean notAtVGLevel) {
    this.notAtVGLevel = notAtVGLevel;
  }

}
