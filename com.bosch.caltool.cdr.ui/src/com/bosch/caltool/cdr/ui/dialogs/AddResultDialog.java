/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOptionsModel;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;

/**
 * @author hnu1cob
 */
public class AddResultDialog extends AbstractDialog {

  private Button okBtn;

  private FormToolkit formToolkit;

  private Text resultText;

  private Combo assesmentCombo;

  private QuestionResultOptionsModel resultModel;

  private Button allowFinishWPChkBox;

  /**
   * @param parentShell Shell of QuestionDialog
   */
  public AddResultDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // modify title
    newShell.setText("Add Result");
    newShell.setSize(350, 200);

    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(GridDataUtil.getInstance().getGridData());
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.None);
    initializeDialogUnits(composite);

    composite.setLayout(new GridLayout());
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    parent.setLayout(new GridLayout());
    parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    createComposite(composite);
    createButtonBar(parent);

    return composite;
  }

  /**
   * This method initializes composite
   *
   * @param composite
   */
  private void createComposite(final Composite topComposite) {

    Composite composite = getFormToolkit().createComposite(topComposite);
    composite.setLayout(new GridLayout(2, true));
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    LabelUtil.getInstance().createLabel(composite, "Result");
    

    this.resultText = new Text(composite, SWT.SINGLE | SWT.BORDER);
    this.resultText.setEnabled(true);
    this.resultText.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(130));
    LabelUtil.getInstance().createLabel(composite, "Assesment");
    
    this.assesmentCombo = new Combo(composite, SWT.READ_ONLY);
    this.assesmentCombo.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(100));
    this.assesmentCombo.add(CDRConstants.QS_ASSESMENT_TYPE.POSITIVE.getUiType());
    this.assesmentCombo.add(CDRConstants.QS_ASSESMENT_TYPE.NEGATIVE.getUiType());
    this.assesmentCombo.add(CDRConstants.QS_ASSESMENT_TYPE.NEUTRAL.getUiType());
    this.assesmentCombo.select(0);
    
    LabelUtil.getInstance().createLabel(composite, "Answer allowed to finish WP");
    
    this.allowFinishWPChkBox = new Button(composite, SWT.CHECK);

    allowFinishWPChkBox.setSelection(true);

    addSelectionListeners();

  }


  /**
   *
   */
  private void addSelectionListeners() {

    this.resultText.addModifyListener(arg0 -> validateSaveButton());


    this.assesmentCombo.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        validateSaveButton();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        validateSaveButton();
      }
    });
    
    this.allowFinishWPChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        validateSaveButton();
      }
    });

  }

  /**
   *
   */
  protected void validateSaveButton() {
    if (!CommonUtils.isEmptyString(this.resultText.getText()) &&
        !CommonUtils.isEmptyString(this.assesmentCombo.getText())) {
      this.okBtn.setEnabled(true);
    }
    else {
      this.okBtn.setEnabled(false);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    QuestionResultOptionsModel result = new QuestionResultOptionsModel();
    result.setResult(this.resultText.getText());
    result.setAssesment(CDRConstants.QS_ASSESMENT_TYPE.getTypeByUiText(this.assesmentCombo.getText()).getDbType());
    result.setAllowFinishWP(allowFinishWPChkBox.getSelection());
    setResultModel(result);
    super.okPressed();
  }


  /**
   * @return the resultModel
   */
  public QuestionResultOptionsModel getResultModel() {
    return this.resultModel;
  }

  /**
   * @param resultModel the resultModel to set
   */
  public void setResultModel(final QuestionResultOptionsModel resultModel) {
    this.resultModel = resultModel;
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Save", false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

  }

}
