/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOptionsModel;

/**
 * @author BNE4COB
 */
class QuestionDialgResultOptionsTextEditingSupport extends EditingSupport {

  /**
   *
   */
  private final QuestionDialog questionDialog;

  /**
   * @param viewer
   * @param questionDialog
   */
  QuestionDialgResultOptionsTextEditingSupport(final QuestionDialog questionDialog, final ColumnViewer viewer) {
    super(viewer);
    this.questionDialog = questionDialog;
  }

  @Override
  protected void setValue(final Object obj, final Object newResultObj) {
    if (obj instanceof QuestionResultOptionsModel) {
      QuestionResultOptionsModel resOptModel = (QuestionResultOptionsModel) obj;
      String newResult = newResultObj.toString();
      // to check whether result is changed
      if (!resOptModel.getResult().equals(newResult)) {
        // copy of model is created to check for duplicates
        QuestionResultOptionsModel resOptCopy = new QuestionResultOptionsModel();
        resOptCopy.setResult(newResult);
        resOptCopy.setAssesment(resOptModel.getAssesment());
        resOptCopy.setAllowFinishWP(resOptModel.isAllowFinishWP());
        // avoids duplicate result
        if (this.questionDialog.isDuplicateResult(resOptCopy)) {
          CDMLogger.getInstance().errorDialog("Multiple records with same result is not possible!",
              Activator.PLUGIN_ID);
          return;
        }

        addToEditList(resOptModel, newResult);
      }
      getViewer().refresh();
      this.questionDialog.checkSaveBtnEnable();
    }
  }

  private void addToEditList(final QuestionResultOptionsModel resOptModel, final String newResult) {
    // avoids adding multiple same entry into list
    if (this.questionDialog.getResultOptionsToBeEdited().contains(resOptModel)) {
      this.questionDialog.getResultOptionsToBeEdited().remove(resOptModel);
    }

    resOptModel.setResult(newResult);
    if (resOptModel.getQuestionResultOptId() != null) {
      this.questionDialog.getResultOptionsToBeEdited().add(resOptModel);
    }
  }

  @Override
  protected Object getValue(final Object element) {
    if (element instanceof QuestionResultOptionsModel) {
      QuestionResultOptionsModel results = (QuestionResultOptionsModel) element;
      return results.getResult();
    }
    return "";
  }

  @Override
  protected CellEditor getCellEditor(final Object arg0) {
    return new TextCellEditor((Composite) getViewer().getControl());
  }

  @Override
  protected boolean canEdit(final Object arg0) {
    return this.questionDialog.isEditable();
  }
}