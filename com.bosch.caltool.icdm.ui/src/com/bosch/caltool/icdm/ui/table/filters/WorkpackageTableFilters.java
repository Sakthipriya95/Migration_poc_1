/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import java.util.Map;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.ui.dialogs.PasteQuestionnaireResponseDialog;

/**
 * @author say8cob
 */
public class WorkpackageTableFilters extends AbstractViewerFilter {

  private final PasteQuestionnaireResponseDialog multiplePasteQnaireResponseDialog;

  /**
   * Instantiates a new questionnaire name table filters.
   *
   * @param multiplePasteQnaireResponseDialog the workpackage sel dialog
   */
  public WorkpackageTableFilters(final PasteQuestionnaireResponseDialog multiplePasteQnaireResponseDialog) {
    this.multiplePasteQnaireResponseDialog = multiplePasteQnaireResponseDialog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // check WorkPkg instance
    Map<Long, A2lWorkPackage> a2lWorkPackageMap = this.multiplePasteQnaireResponseDialog.getA2lWpMap();
    if (element instanceof A2lWorkPackage) {
      A2lWorkPackage a2lWorkPackage = (A2lWorkPackage) element;
      // match with wp name
      if (matchText(a2lWorkPackage.getName())) {
        return true;
      }
      // match with wp group name
      if (matchText(a2lWorkPackageMap.get(a2lWorkPackage.getId()).getName())) {
        return true;
      }
    }
    return false;
  }

}
