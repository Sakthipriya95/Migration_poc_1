/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import java.util.Map;

import com.bosch.caltool.cdr.ui.dialogs.QuestionnaireNameSelDialog;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.wp.WorkPkg;

/**
 * filter class for questionnaire name selection table.
 *
 * @author bru2cob
 */
public class QuestionnaireNameTableFilters extends AbstractViewerFilter {

  /** The questionnaire name sel dialog. */
  private final QuestionnaireNameSelDialog questionnaireNameSelDialog;

  /**
   * Instantiates a new questionnaire name table filters.
   *
   * @param division the division
   * @param questionnaireNameSelDialog the questionnaire name sel dialog
   */
  public QuestionnaireNameTableFilters(final QuestionnaireNameSelDialog questionnaireNameSelDialog) {
    this.questionnaireNameSelDialog = questionnaireNameSelDialog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // check WorkPkg instance
    Map<Long, String> workPkgResp = this.questionnaireNameSelDialog.getWorkPkgResp();
    if (element instanceof WorkPkg) {
      WorkPkg icdmWp = (WorkPkg) element;
      // match with wp name
      if (matchText(icdmWp.getName())) {
        return true;
      }
      // match with wp group name
      if (matchText(workPkgResp.get(icdmWp.getId()))) {
        return true;
      }
    }
    return false;
  }
}
