/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.RuleBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author bru2cob
 */
public class ComplexRuleEditDialog extends AbstractDialog {

  /**
   * Top composite instance
   */
  private Composite top;


  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  private final RuleInfoSection ruleInfoSection;

  private Text advFormulaText;

  private final boolean isBitWiseParam;


  /**
   * @param parentShell parentShell
   * @param ruleInfoSection RuleInfoSection object
   */
  public ComplexRuleEditDialog(final Shell parentShell, final RuleInfoSection ruleInfoSection) {
    super(parentShell);
    this.ruleInfoSection = ruleInfoSection;
    this.isBitWiseParam = ruleInfoSection.getParamEditSection().getBitwiseRuleChkBox().getSelection();
  }

  /**
   * creating contents for the dialog {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Complex Rule");
    setMessage("On selection of OK, Rule will be validated. Error if any, will be shown!",
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * configring the shell of the dialog {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Complex Rule");
    // set the shell layout
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
    super.configureShell(newShell);
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    // create a top composite
    this.top = (Composite) super.createDialogArea(parent);
    // set layout and layout data
    this.top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.top.setLayoutData(gridData);
    // create composite
    createComposite();
    return this.top;
  }

  /**
   * create composite
   */
  private void createComposite() {

    Composite composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    composite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    composite.setLayoutData(gridData);

    new Label(composite, SWT.NONE).setText("Advanced Formula");

    this.advFormulaText =
        getFormToolkit().createText(composite, null, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    gridData.heightHint = 330;
    gridData.widthHint = 60;
    this.advFormulaText.setLayoutData(gridData);

    this.advFormulaText.setEditable(!this.isBitWiseParam);

    String formulaDesc = this.ruleInfoSection.getAdvancedFormula();

    if (formulaDesc != null) {
      this.advFormulaText.setText(formulaDesc);
    }

    Button clearButton = new Button(composite, SWT.CENTER);
    GridData btnGridData = new GridData();
    btnGridData.widthHint = 70;
    clearButton.setLayoutData(btnGridData);
    clearButton.setText("Clear");
    clearButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        ComplexRuleEditDialog.this.advFormulaText.setText("");
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // No Implementation
      }
    });
    clearButton.setVisible(!this.isBitWiseParam);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    if (!this.isBitWiseParam) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
    }
    else {
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }


  /**
   * @return the advFormulaText
   */
  public Text getAdvFormulaText() {
    return this.advFormulaText;
  }


  /**
   * @param advFormulaText the advFormulaText to set
   */
  public void setAdvFormulaText(final Text advFormulaText) {
    this.advFormulaText = advFormulaText;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.advFormulaText == null) {
      this.ruleInfoSection.setAdvancedFormula(null);
    }
    else {
      String ruleErrorMessage = RuleBO.validateComplexRule(this.advFormulaText.getText());
      if (!CommonUtils.isEmptyString(ruleErrorMessage)) {
        CDMLogger.getInstance().errorDialog(ruleErrorMessage, null, Activator.PLUGIN_ID);
      }
      this.ruleInfoSection.setAdvancedFormula(this.advFormulaText.getText());
    }
    this.ruleInfoSection.enableSave();
    super.okPressed();
  }
}

