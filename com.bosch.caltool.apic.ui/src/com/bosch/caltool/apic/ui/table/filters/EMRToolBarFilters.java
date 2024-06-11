/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.function.Predicate;

import com.bosch.caltool.apic.ui.editors.pages.EMRNatPage;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * The Class EMRToolBarFilters.
 *
 * @author gge6cob
 */
public class EMRToolBarFilters extends AbstractViewerFilter {

  private boolean isDeletedFlag = true;
  private boolean isNotDeletedFlag = true;
  private boolean hasErrors = true;
  private boolean hasNoErrors = true;
  private boolean showItemsOfCurrentVariant = false;
  private final EMRNatPage natPage;

  /**
   * Instantiates a new EMR tool bar filters.
   *
   * @param pidcVersion PIDCVersion
   * @param page natpage
   */
  public EMRToolBarFilters(final PidcVersion pidcVersion, final EMRNatPage page) {
    super();
    this.natPage = page;
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    EmrFileMapping rowObject = (EmrFileMapping) element;
    if (!checkHasErrors(rowObject)) {
      return false;
    }
    if (!checkIsDeletedFlag(rowObject)) {
      return false;
    }
    return showAllEntries(rowObject);
  }

  /**
   * @param fnElement
   * @return
   */
  private boolean checkIsDeletedFlag(final EmrFileMapping fnElement) {
    /**
     * isDeletedFlag flag Action Unchecked then do not show items that are deleted
     */
    if ((!this.isDeletedFlag) && fnElement.getEmrFile().getDeletedFlag()) {
      return false;
    }
    /**
     * isNotDeletedFlag flag Action Unchecked then do not show items that are not deleted
     */
    return !((!this.isNotDeletedFlag) && !fnElement.getEmrFile().getDeletedFlag());
  }

  /**
   * @param fnElement
   * @return
   */
  private boolean checkHasErrors(final EmrFileMapping fnElement) {
    /**
     * hasErrors flag Action Unchecked then do not show items that are have no errors
     */
    if ((!this.hasErrors) && hasErrors(fnElement)) {
      return false;
    }
    /**
     * hasNoErrors flag Action Unchecked then do not show items that have errors
     */
    return !((!this.hasNoErrors) && !hasErrors(fnElement));
  }

  /**
   * @param fnElement
   * @return
   */
  private boolean hasErrors(final EmrFileMapping fnElement) {
    return !fnElement.getEmrFile().getLoadedWithoutErrorsFlag();
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean showAllEntries(final EmrFileMapping rowObject) {
    if (!this.showItemsOfCurrentVariant || CommonUtils.isNull(this.natPage.getSelectedVariant())) {
      return true;
    }
    if (CommonUtils.isNotEmpty(rowObject.getVariantSet())) {
      Predicate<PidcVariant> p = s -> s.getId().equals(this.natPage.getSelectedVariant());
      // check for this.showItemsOfCurrentVariant is removed . beacuse it is true here always

      return rowObject.getVariantSet().stream().anyMatch(p);
    }
    return false;
  }

  /**
   * Gets the tool bar matcher.
   *
   * @return the tool bar matcher
   */
  public Matcher<EmrFileMapping> getToolBarMatcher() {
    return new EMRToolBarMatcher<>();
  }

  /**
   * Sets the deleted flag.
   *
   * @param b the new deleted flag
   */
  public void setDeletedFlag(final boolean b) {
    this.isDeletedFlag = b;
  }

  /**
   * Sets the not deleted flag.
   *
   * @param b the new not deleted flag
   */
  public void setNotDeletedFlag(final boolean b) {
    this.isNotDeletedFlag = b;
  }

  /**
   * Sets the checks if is loaded without errors.
   *
   * @param b the new checks if is loaded without errors
   */
  public void setIsLoadedWithoutErrors(final boolean b) {
    this.hasNoErrors = b;
  }

  /**
   * Sets the checks if is loaded with errors.
   *
   * @param b the new checks if is loaded with errors
   */
  public void setIsLoadedWithErrors(final boolean b) {
    this.hasErrors = b;
  }

  /**
   * Sets the show items of current variant.
   *
   * @param b the new show items of current variant
   */
  public void setShowItemsOfCurrentVariant(final boolean b) {
    this.showItemsOfCurrentVariant = b;
  }

  private class EMRToolBarMatcher<E> implements Matcher<E> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final E element) {
      if (element instanceof EmrFileMapping) {
        return selectElement(element);
      }
      return true;
    }
  }
}
