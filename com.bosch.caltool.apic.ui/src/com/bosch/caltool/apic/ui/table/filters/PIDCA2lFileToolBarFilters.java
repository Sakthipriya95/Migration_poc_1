/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;

/**
 * @author TRL1COB
 */
public class PIDCA2lFileToolBarFilters extends AbstractViewerFilter {

  /**
   * Defines active a2l file filter is selected or not
   */
  /* By default this flag will switched on */
  private boolean activeA2lFile = true;

  /**
   * Defines Inactive a2l file filter is selected or not
   */
  /* By default this flag will switched off */
  private boolean inActiveA2lFile = false;

  /**
   * Constructor for the attribute tool bar filter
   */
  public PIDCA2lFileToolBarFilters() {
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
    if (element instanceof PidcA2lFileExt) {
      PidcA2lFileExt a2lFile = (PidcA2lFileExt) element;
      // To display active A2L files
      if (checkActiveA2lFiles(a2lFile)) {
        return true;
      }

      // To display Inactive A2L files
      if (checkInactiveA2lFiles(a2lFile)) {
        return true;
      }
    }
    return false;
  }


  /**
   * @param a2lFile
   * @return
   */
  private boolean checkActiveA2lFiles(final PidcA2lFileExt a2lFile) {
    return (isActiveA2lFile() && CommonUtils.isNotNull(a2lFile.getPidcA2l()) && a2lFile.getPidcA2l().isActive());
  }

  /**
   * @param a2lFile
   * @return
   */
  private boolean checkInactiveA2lFiles(final PidcA2lFileExt a2lFile) {
    return (isInActiveA2lFile() && (CommonUtils.isNull(a2lFile.getPidcA2l()) ||
        (CommonUtils.isNotNull(a2lFile.getPidcA2l()) && !a2lFile.getPidcA2l().isActive())));
  }

  /**
   * @return the activeA2lFile
   */
  public boolean isActiveA2lFile() {
    return this.activeA2lFile;
  }


  /**
   * @return the inActiveA2lFile
   */
  public boolean isInActiveA2lFile() {
    return this.inActiveA2lFile;
  }


  /**
   * @param activeA2lFile the activeA2lFile to set
   */
  public void setActiveA2lFile(final boolean activeA2lFile) {
    this.activeA2lFile = activeA2lFile;
  }


  /**
   * @param inActiveA2lFile the inActiveA2lFile to set
   */
  public void setInActiveA2lFile(final boolean inActiveA2lFile) {
    this.inActiveA2lFile = inActiveA2lFile;
  }

}
