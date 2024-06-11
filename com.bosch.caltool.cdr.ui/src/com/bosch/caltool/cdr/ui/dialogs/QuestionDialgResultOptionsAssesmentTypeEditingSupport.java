/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_ASSESMENT_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOptionsModel;

final class QuestionDialgResultOptionsAssesmentTypeEditingSupport extends EditingSupport {

  /**
   *
   */
  private final QuestionDialog questionDialog;
  private ComboBoxViewerCellEditor cellEditor = null;
  private final List<String> assesmentTypes;

  /**
   * @param questionDialog Question Dialog
   * @param viewer column viewer for Results option table
   */
  public QuestionDialgResultOptionsAssesmentTypeEditingSupport(final QuestionDialog questionDialog,
      final ColumnViewer viewer) {
    super(viewer);
    this.questionDialog = questionDialog;
    this.cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
    this.cellEditor.setContentProvider(new ArrayContentProvider());
    this.cellEditor.setLabelProvider(new LabelProvider());
    this.assesmentTypes = new ArrayList<>();
    this.assesmentTypes.add(CDRConstants.QS_ASSESMENT_TYPE.POSITIVE.getUiType());
    this.assesmentTypes.add(CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE.getUiType());
    this.assesmentTypes.add(CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL.getUiType());
    this.cellEditor.setInput(this.assesmentTypes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CellEditor getCellEditor(final Object obj) {
    return this.cellEditor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean canEdit(final Object obj) {
    return this.questionDialog.isEditable();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getValue(final Object resOptModel) {
    if (resOptModel instanceof QuestionResultOptionsModel) {
      return QS_ASSESMENT_TYPE.getTypeByDbCode(((QuestionResultOptionsModel) resOptModel).getAssesment()).getUiType();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setValue(final Object resOptObj, final Object assesUIString) {
    if (resOptObj instanceof QuestionResultOptionsModel) {
      QuestionResultOptionsModel resOptModel = (QuestionResultOptionsModel) resOptObj;
      String dbType = CDRConstants.QS_ASSESMENT_TYPE.getTypeByUiText(assesUIString.toString()).getDbType();
      if (!resOptModel.getAssesment().equals(dbType)) {
        if (this.questionDialog.getResultOptionsToBeEdited().contains(resOptModel)) {
          this.questionDialog.getResultOptionsToBeEdited().remove(resOptModel);
        }
        resOptModel.setAssesment(dbType);
        if (resOptModel.getQuestionResultOptId() != null) {
          this.questionDialog.getResultOptionsToBeEdited().add(resOptModel);
        }
      }
      getViewer().refresh();
      this.questionDialog.checkSaveBtnEnable();
    }
  }
}