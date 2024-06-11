/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
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

import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * ICDM-826 This dialog is to enter the comment for a group of parameters
 *
 * @author mkl2cob
 */
public class FocusMatrixRemarksDialog extends AbstractDialog {

  /**
   * Review Comment Dialog
   */
  private static final String REVIEW_COMMENT = "Review Comment";
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

  /**
   * String to store focus matrix remarks
   */
  private String fmRemarks;

  /**
   * Text field for focus matrix remarks
   */
  private Text fmRemarksText;


  /**
   * Rvw comment text size in the dialg
   */
  private static final int RVW_COMMENT_TEXT_SIZE = 4000;

  /**
   *
   */
  private final FocusMatrixAttributeClientBO fmAttr;

  /**
   * @param parentShell parent shell
   * @param fmAttr FocusMatrixAttributeClientBO
   */
  public FocusMatrixRemarksDialog(final Shell parentShell, final FocusMatrixAttributeClientBO fmAttr) {
    super(parentShell);
    this.fmAttr = fmAttr;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(REVIEW_COMMENT);

    // Set the message
    setMessage("Enter the review comments for the selected parameters", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Review Comments Dialog");
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = true;
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
    super.configureShell(newShell);
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
   * create section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), REVIEW_COMMENT);
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    createForm();

    this.section.setClient(this.form);

  }

  /**
   * create form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    if (CommonUtils.isNotNull(this.fmAttr)) {
      getFormToolkit().createLabel(this.form.getBody(), "Attribute:");
      getFormToolkit().createLabel(this.form.getBody(), this.fmAttr.getAttribute().getName());
    }
    getFormToolkit().createLabel(this.form.getBody(), "Remarks:");

    // grid data to remove the extra space at the end of the dialog
    gridData.heightHint = 50;
    gridData.verticalAlignment = GridData.END;

    // ICDM-2007 (Parent task : ICDM-1774)
    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, RVW_COMMENT_TEXT_SIZE, gridData);

    this.fmRemarksText = textBoxContentDisplay.getText();
    this.fmRemarksText.setLayoutData(gridData);
    String fmAttrRemark = this.fmAttr.getRemarksDisplay();
    if (CommonUtils.isNotNull(this.fmAttr) && CommonUtils.isNotNull(fmAttrRemark)) {
      // Preset the previous remarks
      this.fmRemarksText.setText(fmAttrRemark);
    }
    // Check for Enabling editing
    enableEdititng();

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(gridData);
  }

  /**
   * Called to disable or enable the Comment text box
   */
  private void enableEdititng() {
    this.fmRemarksText.setEnabled(true);
    this.fmRemarksText.setEditable(true);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    this.fmRemarks = this.fmRemarksText.getText();
    if (this.fmRemarksText.getEnabled()) {
      super.okPressed();
    }
    // For Users with no access if Ok pressed.
    else {
      super.cancelPressed();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    this.fmRemarks = "";
    super.cancelPressed();
  }

  /**
   * Get the review comments entered by the user
   *
   * @return Review comment
   */
  public String getReviewComments() {
    return this.fmRemarks;
  }

}
