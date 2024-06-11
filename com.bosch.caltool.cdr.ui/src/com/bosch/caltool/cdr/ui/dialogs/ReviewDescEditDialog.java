/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

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

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * ICDM-974
 *
 * @author bru2cob
 */
public class ReviewDescEditDialog extends AbstractDialog {

  /**
   * Height of the dialog
   */
  private static final int SHELL_HEIGHT = 50;
  /**
   * no of columns
   */
  private static final int NO_OF_COLS = 2;
  /**
   * Review Description Dialog
   */
  private static final String REVIEW_DESCRIPTION = "Review Description";
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
   * String to store review description
   */
  private String reviewDescription;

  /**
   * Text field for review description
   */
  private Text reviewDescText;
  /**
   * String to store the description before editing
   */
  private final String initialDesc;

  /**
   * @param parentShell
   */
  public ReviewDescEditDialog(final Shell parentShell, final String initialDesc) {
    super(parentShell);
    this.initialDesc = initialDesc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(REVIEW_DESCRIPTION);

    // Set the message
    setMessage("Enter the review description for the review result", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Review Description Dialog");
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    newShell.setLayout(new GridLayout());
    gridData.heightHint = SHELL_HEIGHT;
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
    gridData.grabExcessVerticalSpace = true;
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
        GridDataUtil.getInstance().getGridData(), REVIEW_DESCRIPTION);
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
    getFormToolkit().createLabel(this.form.getBody(), REVIEW_DESCRIPTION + ":");
    this.reviewDescText = getFormToolkit().createText(this.form.getBody(), this.initialDesc, SWT.BORDER | SWT.V_SCROLL);
    gridData.grabExcessVerticalSpace = true;
    this.reviewDescText.setLayoutData(gridData);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NO_OF_COLS;
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(gridData);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    this.reviewDescription = this.reviewDescText.getText();
    super.okPressed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    this.reviewDescription = "";
    super.cancelPressed();
  }

  /**
   * Get the review description entered by the user
   *
   * @return Review description
   */
  public String getReviewDescription() {
    return this.reviewDescription;
  }

}
