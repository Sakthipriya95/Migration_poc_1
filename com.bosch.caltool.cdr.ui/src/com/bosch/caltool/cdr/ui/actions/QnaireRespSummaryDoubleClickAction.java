/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.QnaireAnswerEditDialog;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespSummaryPage;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;

/**
 * @author say8cob
 */
public class QnaireRespSummaryDoubleClickAction implements IMouseClickAction {

  QnaireRespSummaryPage qnaireRespSummaryPage;


  QnaireRespEditorDataHandler dataHandler;

  /**
   * @param qnaireRespSummaryPage as qnaireRespSummaryPage
   * @param dataHandler as datahandler
   */
  public QnaireRespSummaryDoubleClickAction(final QnaireRespSummaryPage qnaireRespSummaryPage,
      final QnaireRespEditorDataHandler dataHandler) {
    this.qnaireRespSummaryPage = qnaireRespSummaryPage;
    this.dataHandler = dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final NatTable natTable, final MouseEvent event) {
    if (this.dataHandler.isModifiable()) {
      RvwQnaireAnswer rvwQnaireAnswer = this.qnaireRespSummaryPage.getCurrentSelection();
      // rvwQnaireAnswer is null for the rows that have open issues
      if (rvwQnaireAnswer != null) {
        QnaireResponseEditor qnaireResponseEditor = (QnaireResponseEditor) this.qnaireRespSummaryPage.getEditor();
        // open the edit dialog
        QnaireAnswerEditDialog dialog = new QnaireAnswerEditDialog(Display.getCurrent().getActiveShell(),
            rvwQnaireAnswer, this.dataHandler, this.qnaireRespSummaryPage, qnaireResponseEditor);
        dialog.open();
        this.qnaireRespSummaryPage.refreshNatFilters();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isExclusive() {
    return false;
  }

}
