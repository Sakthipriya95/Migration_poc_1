/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.apic.PIDCScoutResult;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;


/**
 * @author bru2cob ICDM-1158
 */
public class PIDCSearchResultToolBarFilters extends AbstractViewerFilter {


  /**
   * Pidc with A2l Files and no Review.
   */
  private boolean pidcWithA2l = true;
  /**
   * pidc with A2l file and review
   */
  private boolean pidcWithRvw = true;
  /**
   * pidc with No A2l
   */
  private boolean pidcWithNoA2L = true;

  /**
   * pidc with No focus matrix
   */
  // ICDM-2255
  private boolean pidcWithNoFocusMatrix = true;

  /**
   * pidc with focus matrix
   */
  // ICDM-2255
  private boolean pidcWithFocusMatrix = true;

  /**
   *
   */
  public PIDCSearchResultToolBarFilters() {
    super();

    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    setFilterText(filterText, false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof PIDCScoutResult) {
      final PIDCScoutResult result = (PIDCScoutResult) element;
      if (!filterPidcWithA2l(result)) {
        return false;
      }
      if (!filterPidcWithRvw(result)) {
        return false;
      }

      if (!filterWithNoA2l(result)) {
        return false;
      }
      // ICDM-2255
      if (!filterWithFocusMatrix(result)) {
        return false;
      }

    }
    return true;
  }

  /**
   * @param result
   * @return
   */
  // ICDM-2255
  private boolean filterWithFocusMatrix(final PIDCScoutResult result) {
    // If the pidc non focus matrix is selected
    if (!this.pidcWithNoFocusMatrix && !result.hasFocusMatrix()) {
      return false;
    }
    // If the pidc focus matrix is selected
    if (!this.pidcWithFocusMatrix && result.hasFocusMatrix()) {
      return false;
    }
    return true;
  }

  /**
   * @param result
   * @return
   */
  private boolean filterPidcWithRvw(final PIDCScoutResult result) {
    if (!this.pidcWithRvw && result.hasReviews()) {
      return false;
    }
    return true;

  }

  /**
   * @param result
   * @return
   */
  private boolean filterPidcWithA2l(final PIDCScoutResult result) {
    if (!this.pidcWithA2l && result.hasA2lFiles() && !result.hasReviews()) {
      return false;
    }
    return true;
  }

  /**
   * @param result
   * @return
   */
//ICDM-2255
  private boolean filterWithNoA2l(final PIDCScoutResult result) {
    if (!this.pidcWithNoA2L && !result.hasA2lFiles()) {
      return false;
    }
    return true;
  }


  /**
   * @param pidcWithA2l pidcWithA2l
   */
  public void setShowPIDCA2lFiles(final boolean pidcWithA2l) {
    this.pidcWithA2l = pidcWithA2l;

  }

  /**
   * @param pidcWithRvw pidcWithRvw
   */
  public void setShowPIDCRrvwFiles(final boolean pidcWithRvw) {
    this.pidcWithRvw = pidcWithRvw;

  }

  /**
   * @param pidcWithNoA2L pidcWithNoA2L
   */
  public void setShowPIDCwithNoA2L(final boolean pidcWithNoA2L) {
    this.pidcWithNoA2L = pidcWithNoA2L;

  }

  /**
   * @param pidcWithNoFocusMatrix pidcWithNoFocusMatrix
   */
//ICDM-2255
  public void setShowPIDCwithNoFocusMatrix(final boolean pidcWithNoFocusMatrix) {
    this.pidcWithNoFocusMatrix = pidcWithNoFocusMatrix;
  }

  /**
   * @param pidcWithFocusMatrix pidcWithFocusMatrix
   */
//ICDM-2255
  public void setShowPIDCwithFocusMatrix(final boolean pidcWithNoFocusMatrix) {
    this.pidcWithFocusMatrix = pidcWithNoFocusMatrix;
  }
}
