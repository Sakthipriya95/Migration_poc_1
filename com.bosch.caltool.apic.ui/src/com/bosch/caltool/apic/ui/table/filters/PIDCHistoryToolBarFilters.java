/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.apic.ui.views.PIDCHistoryViewPart;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType;


/**
 * @author dmo5cob
 */
public class PIDCHistoryToolBarFilters extends AbstractViewerFilter {

  /**
   * Defines the flag to syncronise with the selected pidc attr in pidc editor
   */
  private boolean attrSyncFlag;
  /**
   * Defines the flag to syncronise with the selected node (pidc/variant/subvariant) in pidc details viewer
   */
  private boolean levelSyncFlag;
  /**
   * Defines the flag to show the pidc related changes
   */
  private boolean pidcChangesFlag = true;
  /**
   * Defines the flag to show the pidc attrs related changes
   */
  private boolean attrChangesFlag = true;

  /**
   * Defines the flag to show the focus matrix attrs related changes
   */
  // iCDM-2614
  private boolean fmChangesFlag = true;
  /**
   * Pidc history view instance
   */
  private final PIDCHistoryViewPart viewPart;

  /**
   * @param viewPart History viewpart instance
   */
  public PIDCHistoryToolBarFilters(final PIDCHistoryViewPart viewPart) {
    super();
    this.viewPart = viewPart;
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

    final AttrDiffType attrDiff = (AttrDiffType) element;
    // synchronise attribute in PIDC editor
    if (!syncAttrs(attrDiff)) {
      return false;
    }
    // synchronise pidc/varaiant/sub var levels
    if (!syncLevels(attrDiff)) {
      return false;
    }
    // pidc attr changes
    if (!syncPidcAttrChanges(attrDiff)) {
      return false;
    }
    // pidc changes
    if (!syncPidcChanges(attrDiff)) {
      return false;
    }
    // fm changes
    if (!syncFocusMatrixAttrChanges(attrDiff)) {
      return false;
    }

    return true;
  }

  /**
   * @param pidcAttr
   * @return
   */
  private boolean syncAttrs(final AttrDiffType attrDiff) {

    if ((this.attrSyncFlag && (null != this.viewPart.getSelectedPidcAttr()) &&
        this.viewPart.getSelectedPidcAttr().getAttrId().equals(attrDiff.getAttribute().getId())) ||
        !this.attrSyncFlag) {
      return true;
    }
    return false;
  }

  /**
   * @param pidcAttr
   * @return
   */
  private boolean syncPidcAttrChanges(final AttrDiffType attrDiff) {

    if (!this.attrChangesFlag && (attrDiff.isAttributeChange())) {
      return false;
    }
    return true;
  }

  /**
   * @param pidcAttr
   * @return
   */
  // iCDM-2614
  private boolean syncFocusMatrixAttrChanges(final AttrDiffType attrDiff) {

    if (!this.fmChangesFlag && (attrDiff.isFocusMatrixChange())) {
      return false;
    }
    return true;
  }

  /**
   * @param pidcAttr
   * @return
   */
  private boolean syncPidcChanges(final AttrDiffType attrDiff) {

    // ICDM-2614, adding focus matrix changes condition
    if (!this.pidcChangesFlag && (!attrDiff.isAttributeChange() && !attrDiff.isFocusMatrixChange())) {
      return false;
    }
    return true;
  }

  /**
   * @param pidcAttr
   * @return
   */
  private boolean syncLevels(final AttrDiffType attrDiff) {

    boolean flag = false;
    flag = checkForPIDCLevel(attrDiff, flag);

    flag = checkForVarLevel(attrDiff, flag);

    flag = checkForSubVarLevel(attrDiff, flag);

    if (!this.levelSyncFlag) {
      return true;
    }

    return flag;
  }

  /**
   * @param attrDiff
   * @param flag
   */
  private boolean checkForSubVarLevel(final AttrDiffType attrDiff, final boolean flag) {
    if (this.levelSyncFlag && this.viewPart.isSubVaraintNodeSelected() &&
        this.viewPart.getSelectedPIDCSubVariant().getId().equals(attrDiff.getSubVariantId())) {
      return (null != attrDiff.getSubVariantId());
    }
    return flag;
  }

  /**
   * @param attrDiff
   * @param flag2
   */
  private boolean checkForVarLevel(final AttrDiffType attrDiff, final boolean flag) {

    if (this.levelSyncFlag && this.viewPart.isVaraintNodeSelected() &&
        this.viewPart.getSelectedPIDCVariant().getId().equals(attrDiff.getVariantId())) {
      return (null != attrDiff.getVariantId()) && (null == attrDiff.getSubVariantId());
    }
    return flag;
  }

  /**
   * @param attrDiff
   * @param flag
   */
  private boolean checkForPIDCLevel(final AttrDiffType attrDiff, final boolean flag) {

    if (this.levelSyncFlag && !this.viewPart.isVaraintNodeSelected() && !this.viewPart.isSubVaraintNodeSelected()) {
      return (null != attrDiff.getPidcId()) && (null == attrDiff.getVariantId()) &&
          (null == attrDiff.getSubVariantId());
    }
    return flag;
  }

  /**
   * @return the attrSyncFlag
   */
  public boolean isAttrSyncFlag() {
    return this.attrSyncFlag;
  }


  /**
   * @param attrSyncFlag the attrSyncFlag to set
   */
  public void setAttrSyncFlag(final boolean attrSyncFlag) {
    this.attrSyncFlag = attrSyncFlag;
  }


  /**
   * @return the levelSyncFlag
   */
  public boolean isLevelSyncFlag() {
    return this.levelSyncFlag;
  }


  /**
   * @param levelSyncFlag the levelSyncFlag to set
   */
  public void setLevelSyncFlag(final boolean levelSyncFlag) {
    this.levelSyncFlag = levelSyncFlag;
  }


  /**
   * @return the pidcChangesFlag
   */
  public boolean isPidcChangesFlag() {
    return this.pidcChangesFlag;
  }


  /**
   * @param pidcChangesFlag the pidcChangesFlag to set
   */
  public void setPidcChangesFlag(final boolean pidcChangesFlag) {
    this.pidcChangesFlag = pidcChangesFlag;
  }


  /**
   * @return the attrChangesFlag
   */
  public boolean isAttrChangesFlag() {
    return this.attrChangesFlag;
  }


  /**
   * @param attrChangesFlag the attrChangesFlag to set
   */
  public void setAttrChangesFlag(final boolean attrChangesFlag) {
    this.attrChangesFlag = attrChangesFlag;
  }


  /**
   * @return the fmChangesFlag
   */
  // iCDM-2614
  public boolean isFmChangesFlag() {
    return this.fmChangesFlag;
  }

  /**
   * @param fmChangesFlag the fmChangesFlag to set
   */
  // iCDM-2614
  public void setFmChangesFlag(final boolean fmChangesFlag) {
    this.fmChangesFlag = fmChangesFlag;
  }

}
