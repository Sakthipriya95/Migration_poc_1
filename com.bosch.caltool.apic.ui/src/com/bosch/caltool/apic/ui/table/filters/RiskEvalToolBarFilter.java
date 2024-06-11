/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.apic.ui.editors.pages.RiskEvalNatTableSection;
import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author gge6cob
 */
public class RiskEvalToolBarFilter extends AbstractViewerFilter {

  boolean showAllEntries = false;


  boolean isRelevant = true;
  boolean isNotRelevant = true;
  boolean isRelevantNotDefined = true;


  private final RiskEvalNatTableSection natCtrl;

  private class RiskMgmtToolBarMatcher<E> implements Matcher<E> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final E element) {
      if (element instanceof PidcRMCharacterMapping) {
        return selectElement(element);
      }
      return true;

    }
  }

  /**
   * @param riskEvalNattableSection
   */
  public RiskEvalToolBarFilter(final RiskEvalNatTableSection riskEvalNattableSection) {
    this.natCtrl = riskEvalNattableSection;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    PidcRMCharacterMapping rowObject = (PidcRMCharacterMapping) element;
    if (!checkIsRelevant(rowObject)) {
      return false;
    }
    if (!showAllEntries(rowObject)) {
      return false;
    }
    return true;
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean showAllEntries(final PidcRMCharacterMapping rowObject) {
    /**
     * Show All Project Character:Yes flag Unchecked then do not show all entries - only show mapped entries
     */
    boolean visibility = getVisibility(rowObject);
    rowObject.setVisible(visibility);
    if (!isShowAllEntries()) {
      /*
       * If parent is NotRelevant, make child visible or vice-versa. Refer grouping logic combined with class :
       * Accumulator & ToolbarFilter
       */
      return visibility;
    }
    // Show all items to be visible
    return true;
  }


  /**
   * @param rowObject
   * @return
   */
  private boolean getVisibility(final PidcRMCharacterMapping rowObject) {
    Long parentId = rowObject.getParentProjCharId();
    while (parentId.longValue() != 0l) {
      PidcRMCharacterMapping parent = this.natCtrl.getParentDataMap().get(parentId);
      // If immediate-parent's relevancy is YES/NA -> hide child nodes
      if (!parent.isRelevantNo()) {
        return false;
      }
      // Else check super-parent's state
      parentId = parent.getParentProjCharId();
    }
    return true;
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean checkIsRelevant(final PidcRMCharacterMapping rowObject) {
    /**
     * Relevant:Yes flag Action Unchecked then do not show 'Relevant - Yes'
     */
    if ((!this.isRelevant) && (rowObject.isRelevantYes())) {
      return false;
    }
    /**
     * isRelevant:No flag Action Unchecked then do not show 'Relevant - No'
     */
    if ((!this.isNotRelevant) && (rowObject.isRelevantNo())) {
      return false;
    }
    /**
     * isRelevant:NotDefined flag Action Unchecked then do not show 'Relevant - Not Defined'
     */
    if ((!this.isRelevantNotDefined) && (rowObject.isRelevantNA())) {
      return false;
    }
    return true;
  }

  /**
   * @return Matcher
   */
  public Matcher getToolBarMatcher() {
    return new RiskMgmtToolBarMatcher<>();
  }


  /**
   * @return the isRelevant
   */
  public boolean isRelevant() {
    return this.isRelevant;
  }


  /**
   * @param isRelevant the isRelevant to set
   */
  public void setRelevant(final boolean isRelevant) {
    this.isRelevant = isRelevant;
  }


  /**
   * @return the isNotRelevant
   */
  public boolean isNotRelevant() {
    return this.isNotRelevant;
  }


  /**
   * @param isNotRelevant the isNotRelevant to set
   */
  public void setNotRelevant(final boolean isNotRelevant) {
    this.isNotRelevant = isNotRelevant;
  }


  /**
   * @return the isRelevantNotDefined
   */
  public boolean isRelevantNotDefined() {
    return this.isRelevantNotDefined;
  }


  /**
   * @param isRelevantNotDefined the isRelevantNotDefined to set
   */
  public void setRelevantNotDefined(final boolean isRelevantNotDefined) {
    this.isRelevantNotDefined = isRelevantNotDefined;
  }


  /**
   * @return the showAllEntries
   */
  public boolean isShowAllEntries() {
    return this.showAllEntries;
  }


  /**
   * @param showAllEntries the showAllEntries to set
   */
  public void setShowAllEntries(final boolean showAllEntries) {
    this.showAllEntries = showAllEntries;
  }
}
