/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author dja7cob
 */
public class ShapeCheckResDialog extends AbstractDialog {

  /**
   * AddValidityAttrValDialog Title
   */
  private static final String DIALOG_TITLE = "Shape Check Result";
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  private final com.bosch.caltool.icdm.model.cdr.CDRResultParameter cdrResultParameter;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form form
   */
  private Form form;

  /**
   * @param parentShell
   * @param cdrResultParameter
   */
  public ShapeCheckResDialog(final Shell parentShell,
      final com.bosch.caltool.icdm.model.cdr.CDRResultParameter cdrResultParameter) {
    super(parentShell);
    this.cdrResultParameter = cdrResultParameter;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    Control contents = super.createContents(parent);
    // Set the title
    setTitle("Shape Check Result for : " + this.cdrResultParameter.getName());
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);
    newShell.layout(true, true);
    super.configureShell(newShell);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    // create buttons
    parent.layout(true, true);
    return this.top;
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
   * This method initializes composite
   */
  private void createComposite() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout();
    gridLayout.makeColumnsEqualWidth = true;
    this.composite.setLayout(gridLayout);
    this.composite.setLayoutData(gridData);
    // create table section
    createTextSection();
  }

  /**
   *
   */
  private void createTextSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.section = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, DIALOG_TITLE);
    this.section.setLayoutData(gridData);
    // create table form
    createTextForm();
    this.section.setClient(this.form);
  }

  /**
   *
   */
  private void createTextForm() {
    // Create Text form
    this.form = this.formToolkit.createForm(this.section);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form.getBody().setLayout(gridLayout);

    LabelUtil.getInstance().createLabel(this.form.getBody(), "Error Details:");

    // Text to display thr Shape review error details
    Text errDetailsText =
        getFormToolkit().createText(this.form.getBody(), null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    errDetailsText.setLayoutData(getTextAreaGridData());
    errDetailsText.setEditable(false);
    if (null != this.cdrResultParameter.getSrErrorDetails()) {
      errDetailsText.setText(this.cdrResultParameter.getSrErrorDetails());
    }

    // Label to deisplay whether the error during shape review is accepted
    LabelUtil.getInstance().createLabel(this.form.getBody(), "");
    LabelUtil.getInstance().createLabel(this.form.getBody(), "");
    LabelUtil.getInstance().createLabel(this.form.getBody(), "");
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Error Accepted:");
    String isErrAccepted = CDRConstants.SR_ACCEPTED_FLAG.NO.getUiType();
    if ((null != this.cdrResultParameter.getSrAcceptedFlag()) &&
        this.cdrResultParameter.getSrAcceptedFlag().equalsIgnoreCase(CDRConstants.SR_ACCEPTED_FLAG.YES.getUiType())) {
      isErrAccepted = CDRConstants.SR_ACCEPTED_FLAG.YES.getUiType();
    }
    LabelUtil.getInstance().createLabel(this.form.getBody(), isErrAccepted);
  }

  /**
   * @return
   */
  private GridData getTextAreaGridData() {
    // Grid data for the text box
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.FILL;
    gridData2.verticalSpan = 2;
    gridData2.heightHint = 100;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
  }
}
