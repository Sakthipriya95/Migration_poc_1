/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;

import com.bosch.caltool.cdr.ui.table.filters.QuesRespStatusToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;

/**
 * @author mkl2cob
 */
public class QuesRespStatusToolBarActionSet {

  /**
   * status - not answered filter action
   */
  private Action notAnsweredFilterAction;
  /**
   * status - negative answered
   */
  private Action negAnsweredFilterAction;
  /**
   * status - positive answered
   */
  private Action positiveAnsweredFilterAction;
  /**
   * status - positive answered
   */
  private Action notBaselinedQnaireRespFilterAction;

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer table viewer
   */
  public void createNotAnsweredFilterAction(final ToolBarManager toolBarManager,
      final QuesRespStatusToolBarFilters toolBarFilters, final GridTableViewer viewer) {


    this.notAnsweredFilterAction = new Action(CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getUiType(), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotAnsFlag(QuesRespStatusToolBarActionSet.this.notAnsweredFilterAction.isChecked());
        viewer.refresh();
      }
    };
    // Set the image for non defined filter action
    this.notAnsweredFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REMOVE_16X16));

    this.notAnsweredFilterAction.setChecked(true);
    toolBarManager.add(this.notAnsweredFilterAction);

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer table viewer
   * @param showOnlyUnfilledQues uncheck filter if this flag is true
   */
  public void createNegativeAnsweredAction(final ToolBarManager toolBarManager,
      final QuesRespStatusToolBarFilters toolBarFilters, final GridTableViewer viewer,
      final boolean showOnlyUnfilledQues) {

    this.negAnsweredFilterAction = new Action(CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getUiType(), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNegativeAnsFlag(QuesRespStatusToolBarActionSet.this.negAnsweredFilterAction.isChecked());
        viewer.refresh();
      }
    };
    // Set the image for non defined filter action
    this.negAnsweredFilterAction
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ORANGE_EXCLAMATION_ICON_16X16));

    this.negAnsweredFilterAction.setChecked(!showOnlyUnfilledQues);
    this.negAnsweredFilterAction.run();
    toolBarManager.add(this.negAnsweredFilterAction);

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer table viewer
   * @param showOnlyUnfilledQues uncheck filter if this flag is true
   */
  public void createPositiveAnsweredFilterAction(final ToolBarManager toolBarManager,
      final QuesRespStatusToolBarFilters toolBarFilters, final GridTableViewer viewer,
      final boolean showOnlyUnfilledQues) {

    this.positiveAnsweredFilterAction = new Action(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getUiType(), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setPositiveAnsFlag(QuesRespStatusToolBarActionSet.this.positiveAnsweredFilterAction.isChecked());
        viewer.refresh();
      }
    };
    // Set the image for non defined filter action
    this.positiveAnsweredFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALL_16X16));

    this.positiveAnsweredFilterAction.setChecked(!showOnlyUnfilledQues);
    this.positiveAnsweredFilterAction.run();
    toolBarManager.add(this.positiveAnsweredFilterAction);

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param viewer table viewer
   */
  public void createNotBaselinedQnaireRespFilterAction(final ToolBarManager toolBarManager,
      final QuesRespStatusToolBarFilters toolBarFilters, final GridTableViewer viewer) {
    this.notBaselinedQnaireRespFilterAction = new Action(CDRConstants.NOT_BASELINED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotBaselinedQnaireRespFlag(
            QuesRespStatusToolBarActionSet.this.notBaselinedQnaireRespFilterAction.isChecked());
        viewer.refresh();
      }
    };
    // Set the image for non defined filter action
    this.notBaselinedQnaireRespFilterAction
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_BASELINED_16X16));

    this.notBaselinedQnaireRespFilterAction.setChecked(true);
    toolBarManager.add(this.notBaselinedQnaireRespFilterAction);

  }

  /**
   * @return the notBaselinedQnaireRespFilterAction
   */
  public Action getNotBaselinedQnaireRespFilterAction() {
    return this.notBaselinedQnaireRespFilterAction;
  }

  /**
   * @param notBaselinedQnaireRespFilterAction the notBaselinedQnaireRespFilterAction to set
   */
  public void setNotBaselinedQnaireRespFilterAction(final Action notBaselinedQnaireRespFilterAction) {
    this.notBaselinedQnaireRespFilterAction = notBaselinedQnaireRespFilterAction;
  }

}
