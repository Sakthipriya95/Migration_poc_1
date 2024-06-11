/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.bosch.caltool.cdr.ui.dialogs.QuestionnaireNameSelDialog;
import com.bosch.caltool.icdm.model.wp.WorkPkg;

/**
 * The Class QuestionnaireNameTableLabelProvider.
 *
 * @author bru2cob
 */
public class QuestionnaireNameTableLabelProvider extends ColumnLabelProvider {

  /** WP inuse column number. */
  private static final int WP_INUSE = 2;

  /** WP name column number. */
  private static final int WP_NAME = 1;

  /** WP group name column number. */
  private static final int WP_GROUP_NAME = 0;

  /** Col index. */
  private final int columnIndex;

  /** The questionnaire name sel dialog. */
  private final QuestionnaireNameSelDialog questionnaireNameSelDialog;

  /**
   * Instantiates a new questionnaire name table label provider.
   *
   * @param columnIndex columnIndex
   * @param questionnaireNameSelDialog the questionnaire name sel dialog
   * @param selectedAttrVal the selected attr val
   */
  public QuestionnaireNameTableLabelProvider(final int columnIndex,
      final QuestionnaireNameSelDialog questionnaireNameSelDialog) {
    this.columnIndex = columnIndex;
    this.questionnaireNameSelDialog = questionnaireNameSelDialog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    final Object element = cell.getElement();
    String result;
    // get the open point table values
    if (element instanceof WorkPkg) {
      WorkPkg icdmWp = (WorkPkg) element;
      result = getColumnText(cell, icdmWp);
      // set the cell text
      cell.setText(result);
    }
  }

  /**
   * Gets the column text.
   *
   * @param cell the cell
   * @param icdmWp the icdm wp
   * @return the column text
   */
  private String getColumnText(final ViewerCell cell, final WorkPkg icdmWp) {
    String result = null;
    Map<Long, String> workPkgResp = this.questionnaireNameSelDialog.getWorkPkgResp();
    switch (this.columnIndex) {
      case WP_GROUP_NAME:
        result = workPkgResp.get(icdmWp.getId());
        break;
      case WP_NAME:
        result = icdmWp.getName();
        break;
      default:
        result = "";
        break;
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    String result = null;
    if (element instanceof WorkPkg) {
      WorkPkg icdmWp = (WorkPkg) element;
      result = getColumnText(null, icdmWp);
    }
    return result;
  }


}
