/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * The Class A2lWpVariantGrpSelectionDialog.
 */
public class A2lWpVariantGrpSelectionDialog extends AbstractDialog {

  /** Number of Columns of the grid layout. */
  private static final int GRID_NUM_COLS = 2;

  /** Top composite. */
  private Composite top;

  /** Composite instance for the dialog. */
  private Composite composite;

  /** FormToolkit instance. */
  private FormToolkit formToolkit;

  /** Section instance. */
  private Section section;

  /** Form instance. */
  private Form form;

  private final Map<Long, A2lVariantGroup> map;

  private Combo variantGrpCombo;

  /**
   * Selected Variant Group's Id
   */
  protected Long variantGrpId;


  /**
   * Instantiates a new a 2 l wp variant grp selection dialog.
   *
   * @param parentShell Shell
   * @param map the variant-group map
   */
  public A2lWpVariantGrpSelectionDialog(final Shell parentShell, final Map<Long, A2lVariantGroup> map) {
    super(parentShell);
    this.map = map;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Variant Group Selection - FC2WP Import");
    super.configureShell(newShell);
  }

  /**
   * Creates the dialog's contents.
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the message
    setMessage("Select Variant Group applicable for FC2WP Import");
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite.
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
  }

  /**
   * This method initializes section.
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form.
   */
  private void createForm() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = GRID_NUM_COLS;

    GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;
    this.section.setLayoutData(gridData1);
    this.form = this.formToolkit.createForm(this.section);

    gridLayout.numColumns = 3;
    new Label(this.form.getBody(), SWT.NONE).setText("Variant Group : ");

    this.variantGrpCombo = new Combo(this.form.getBody(), SWT.READ_ONLY);
    GridData gridData2 = new GridData();
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalSpan = 2;
    this.variantGrpCombo.setLayoutData(gridData2);
    int index = 1;
    // Add Pidc level variant group
    addPidcLevelA2lVariantGroup();
    for (A2lVariantGroup vg : this.map.values()) {
      this.variantGrpCombo.add(vg.getName(), index);
      this.variantGrpCombo.setData(Integer.toString(index++), vg);
    }
    this.variantGrpCombo.select(0);
    this.form.getBody().setLayout(gridLayout);
    new Label(this.form.getBody(), SWT.NONE);
  }

  /**
   * @param variantGrpCombo2
   */
  private void addPidcLevelA2lVariantGroup() {
    A2lVariantGroup vg = new A2lVariantGroup();
    vg.setId(null);
    vg.setName("<DEFAULT>");
    this.variantGrpCombo.add(vg.getName(), 0);
    this.variantGrpCombo.setData(Integer.toString(0), vg);
  }

  /**
   * This method initializes formToolkit.
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
   * Creates the buttons for the button bar.
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    Button okBtn = createButton(parent, IDialogConstants.OK_ID, "OK", false);
    okBtn.setEnabled(true);
  }

  /**
   * after clicking ok in dialog
   */
  @Override
  protected void okPressed() {
    int selection = A2lWpVariantGrpSelectionDialog.this.variantGrpCombo.getSelectionIndex();
    A2lVariantGroup data =
        (A2lVariantGroup) A2lWpVariantGrpSelectionDialog.this.variantGrpCombo.getData(Integer.toString(selection));
    A2lWpVariantGrpSelectionDialog.this.variantGrpId = data.getId();
    super.okPressed();
  }

  /**
   * Gets the variant grp id.
   *
   * @return the variant grp id
   */
  public Long getVariantGrpId() {
    return this.variantGrpId;
  }
}
