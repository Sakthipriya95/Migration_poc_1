/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author and4cob
 */
public class TakeOverCdfxRemarksDialog extends AbstractDialog {

  /**
   * Review Comment Dialog
   */
  private static final String TAKE_OVER_FROM_CDFX = "Options for Take over from CDFX_Remarks to Comment";

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
   * section instance
   */
  private Section section;

  /**
   * Form instance
   */
  private Form form;
  private Button ignoreBtn;
  private Button appendBtn;
  private Button replaceBtn;

  /**
   * Flag used to store Ignore Remark selection
   */
  protected boolean ignoreRemark;

  /**
   * Flag used to store Append Remark selection
   */
  protected boolean appendRemark;

  /**
   * Flag used to store Replace Remark selection
   */
  protected boolean replaceRemark;

  /**
   * @param parentShell parent shell
   */
  public TakeOverCdfxRemarksDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(TAKE_OVER_FROM_CDFX);

    // Set the message
    setMessage("Select any one of the three options", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(TAKE_OVER_FROM_CDFX);
    newShell.setDragDetect(true);

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = true;
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
    newShell.setMinimumSize(600, 300);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX);
    setBlockOnOpen(false);
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
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.top.setLayoutData(gridData);
    createComposite();
    return this.top;
  }

  /**
   * create composite
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setLayoutData(gridData);
    createSection();
  }

  /**
   *
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), TAKE_OVER_FROM_CDFX);
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    createForm();
    this.section.setClient(this.form);

  }

  /**
   *
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    final GridData gridData = GridDataUtil.getInstance().getTextGridData();


    this.ignoreBtn = new Button(this.form.getBody(), SWT.RADIO);
    this.ignoreBtn.setText("Ignore");
    this.ignoreBtn.setLayoutData(GridDataUtil.getInstance().getGridData());
    // grid data to remove the extra space at the end of the dialog
    gridData.heightHint = 50;
    getFormToolkit().createLabel(this.form.getBody(), " Existing comments will not be overwritten.");


    this.appendBtn = new Button(this.form.getBody(), SWT.RADIO);
    this.appendBtn.setText("Append");
    this.appendBtn.setLayoutData(GridDataUtil.getInstance().getGridData());

    getFormToolkit().createLabel(this.form.getBody(), " Remarks will get appended to the existing comments. ");

    this.replaceBtn = new Button(this.form.getBody(), SWT.RADIO);
    this.replaceBtn.setText("Replace");
    this.replaceBtn.setLayoutData(GridDataUtil.getInstance().getGridData());

    getFormToolkit().createLabel(this.form.getBody(), " Remarks will overwrite the existing comments. ");

    this.form.getBody().setLayout(new GridLayout());
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    this.ignoreRemark = this.ignoreBtn.getSelection();
    this.appendRemark = this.appendBtn.getSelection();
    this.replaceRemark = this.replaceBtn.getSelection();
    super.okPressed();
  }

  /**
   * @return the ignoreRemark
   */
  public boolean isIgnoreRemark() {
    return this.ignoreRemark;
  }


  /**
   * @param ignoreRemark the ignoreRemark to set
   */
  public void setIgnoreRemark(final boolean ignoreRemark) {
    this.ignoreRemark = ignoreRemark;
  }

  /**
   * @return the appendRemark
   */
  public boolean isAppendRemark() {
    return this.appendRemark;
  }


  /**
   * @param appendRemark the appendRemark to set
   */
  public void setAppendRemark(final boolean appendRemark) {
    this.appendRemark = appendRemark;
  }

  /**
   * @return the replaceRemark
   */
  public boolean isReplaceRemark() {
    return this.replaceRemark;
  }


  /**
   * @param replaceRemark the replaceRemark to set
   */
  public void setReplaceRemark(final boolean replaceRemark) {
    this.replaceRemark = replaceRemark;
  }


}
