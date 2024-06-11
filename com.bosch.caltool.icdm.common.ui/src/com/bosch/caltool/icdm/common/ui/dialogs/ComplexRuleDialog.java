/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author bru2cob
 */
public class ComplexRuleDialog extends AbstractDialog {

  /**
   * Top composite instance
   */
  private Composite top;
  /**
   * composite instance
   */
  private Composite composite;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * text already set
   */
  private final String advFormula;
  /**
   * formula String
   */
  private final String formula;

  /**
   * Constructor
   *
   * @param parentShell parent shell
   * @param formulaDesc text already set
   * @param formula String
   */
  public ComplexRuleDialog(final Shell parentShell, final String formulaDesc, final String formula) {
    super(parentShell);
    this.advFormula = formulaDesc;
    this.formula = formula;
  }


  /**
   * creating contents for the dialog {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Complex Rule");
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

    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.composite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setLayoutData(gridData);

    new Label(this.composite, SWT.NONE).setText("Formula");

    Text formulaText = getFormToolkit().createText(this.composite, null, SWT.NONE);
    GridData grid = new GridData();
    grid.horizontalAlignment = GridData.FILL;
    grid.grabExcessHorizontalSpace = true;
    formulaText.setLayoutData(grid);
    formulaText.setEditable(false);
    if (this.formula != null) {
      formulaText.setText(this.formula);
    }


    new Label(this.composite, SWT.NONE).setText("Advanced Formula");

    Text advFormulaText =
        getFormToolkit().createText(this.composite, null, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    gridData.heightHint = 130;
    gridData.widthHint = 30;
    advFormulaText.setLayoutData(gridData);
    advFormulaText.setEditable(false);
    if (this.advFormula != null) {
      advFormulaText.setText(this.advFormula);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

}

