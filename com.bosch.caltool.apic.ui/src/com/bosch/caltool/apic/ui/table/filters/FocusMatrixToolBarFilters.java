/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.Set;

import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author mkl2cob
 */
public class FocusMatrixToolBarFilters {

  /**
   * By default this flag will switched off
   */
  private boolean allAttrFilter;
  // ICDM-1624
  /**
   * By default this flag will switched on
   */
  private boolean mappedAttrFilter;
  /**
   * By default this flag will switched on
   */
  private boolean unMappedAttrFilter;
  /**
   * By default this flag will switched on
   */
  private boolean relavantAttrFilter;


  private class FocusMatrixToolBarMatcher<E> implements Matcher<E> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final E arg0) {
      FocusMatrixAttributeClientBO focusMatrixAttr = (FocusMatrixAttributeClientBO) arg0;
      if (!filterFocusMatrixAttrs(focusMatrixAttr)) {
        return false;
      }
      // ICDM-1624
      return filterMappedAttrs(focusMatrixAttr);
    }

    /**
     * @param focusMatrixAttr FocusMatrixAttribute
     * @return boolean true if the attr can be displayed in the NAT table
     */
    private boolean filterFocusMatrixAttrs(final FocusMatrixAttributeClientBO focusMatrixAttr) {
      return !(!isAllAttrFilter() && !focusMatrixAttr.isFocusMatrixApplicable());
    }
  }

  /**
   * @param visible boolean
   */
  public void setAttrVisibility(final boolean visible) {
    this.allAttrFilter = visible;

  }


  /**
   * @param focusMatrixAttr FocusMatrixAttribute
   * @return true is attr is mapped to usecase
   */
  private boolean isAttrMapped(final FocusMatrixAttributeClientBO focusMatrixAttr) {
    Set<FocusMatrixUseCaseItem> fmUseCaseItemsSet = focusMatrixAttr.getFmUseCaseItemsSet();
    boolean isAttrMapped = false;
    for (FocusMatrixUseCaseItem usItem : fmUseCaseItemsSet) {
      if (usItem.isUcpaMapped(focusMatrixAttr.getAttribute())) {
        isAttrMapped = true;
        break;
      }
    }
    return isAttrMapped;
  }


  /**
   * filter Reference
   *
   * @param pidcAttr
   */
  private boolean filterMappedAttrs(final FocusMatrixAttributeClientBO focusMatrixAttr) {
    if (!isMappedAttrFilter() && isAttrMapped(focusMatrixAttr) && !isAttrRelavant(focusMatrixAttr)) {
      return false;
    }
    if (!isUnMappedAttrFilter() && !isAttrMapped(focusMatrixAttr) && !isAttrRelavant(focusMatrixAttr)) {
      return false;
    }
    return filterRelavantAttr(focusMatrixAttr);
  }


  /**
   * @param focusMatrixAttr focusMatrixAttr
   * @return boolean
   */
  public boolean filterRelavantAttr(final FocusMatrixAttributeClientBO focusMatrixAttr) {
    if (!isRelavantAttrFilter() && isAttrRelavant(focusMatrixAttr)) {
      // to display attr which is mapped and relavant
      return (isAttrMapped(focusMatrixAttr) && isMappedAttrFilter());
    }
    return true;
  }


  /**
   * @param focusMatrixAttr
   * @return
   */
  private boolean isAttrRelavant(final FocusMatrixAttributeClientBO focusMatrixAttr) {
    Set<FocusMatrixUseCaseItem> fmUseCaseItemsSet = focusMatrixAttr.getFmUseCaseItemsSet();
    boolean isAttrMapped = false;
    for (FocusMatrixUseCaseItem usItem : fmUseCaseItemsSet) {
      if (null == usItem.getAttributeMapping().get(focusMatrixAttr.getAttribute())) {
        isAttrMapped = usItem.isMapped(focusMatrixAttr.getAttribute());
        if (isAttrMapped) {
          break;
        }
      }
    }
    return isAttrMapped;
  }

  /**
   * @return the allAttrFilter
   */
  private boolean isAllAttrFilter() {
    return this.allAttrFilter;
  }

  /**
   * @return Matcher
   */
  public Matcher getToolBarMatcher() {
    return new FocusMatrixToolBarMatcher<FocusMatrixAttributeClientBO>();
  }

  /**
   * @return the mappedAttrFilter
   */
  private boolean isMappedAttrFilter() {
    return this.mappedAttrFilter;
  }


  /**
   * @param mappedAttrFilter the mappedAttrFilter to set
   */
  public void setMappedAttrFilter(final boolean mappedAttrFilter) {
    this.mappedAttrFilter = mappedAttrFilter;
  }

  /**
   * @return the unMappedAttrFilter
   */
  private boolean isUnMappedAttrFilter() {
    return this.unMappedAttrFilter;
  }

  /**
   * @param unMappedAttrFilter the unMappedAttrFilter to set
   */
  public void setUnMappedAttrFilter(final boolean unMappedAttrFilter) {
    this.unMappedAttrFilter = unMappedAttrFilter;
  }

  /**
   * @return the relavantAttrFilter
   */
  private boolean isRelavantAttrFilter() {
    return this.relavantAttrFilter;
  }

  /**
   * @param relavantAttrFilter the relavantAttrFilter to set
   */
  public void setRelavantAttrFilter(final boolean relavantAttrFilter) {
    this.relavantAttrFilter = relavantAttrFilter;
  }


}
