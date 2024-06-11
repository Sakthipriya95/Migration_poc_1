/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

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

import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * This dialog is to enter the remarks
 *
 * @author dmo5cob
 */
public class CommentsDialog extends AbstractDialog {

  /**
   * Comment Dialog
   */
  private static final String REMARKS = "Remarks";
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
   * Section title
   */
  private String sectionTitle;
  /**
   * Form instance
   */
  private Form form;

  /**
   * String to store review comments
   */
  private String comment;

  /**
   * Text field for review comments
   */
  private Text reviewCmtText;
  /**
   * title
   */
  private String titleText;
  /**
   * title message
   */
  private String titleMessageText;

  /**
   * remark label text
   */
  private String remarkLableText;
  /**
   * The maximum character a user can enter in the Text box
   */
  private final int textBoxMaxChars;
  /**
   * text already set
   */
  private final String setText;

  /**
   * @param parentShell parent shell
   * @param textBoxMaxChars max number of characters
   * @param setText text already set
   */
  public CommentsDialog(final Shell parentShell, final int textBoxMaxChars, final String setText) {
    super(parentShell);
    this.textBoxMaxChars = textBoxMaxChars;
    this.setText = setText;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(null == getTitleText() ? REMARKS : getTitleText());

    // Set the message
    setMessage(null == getTitleMessageText() ? "Enter the remarks " : getTitleMessageText(),
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(null == getTitleText() ? REMARKS : getTitleText());
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
        GridDataUtil.getInstance().getGridData(), null == getSectionTitle() ? REMARKS : getSectionTitle());
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

    getFormToolkit().createLabel(this.form.getBody(),
        null == getRemarkLableText() ? "Remark : " : getRemarkLableText());

    // grid data to remove the extra space at the end of the dialog
    gridData.heightHint = 50;
    gridData.verticalAlignment = GridData.END;

    // ICDM-2007 (Parent task : ICDM-1774)
    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, this.textBoxMaxChars, gridData);

    this.reviewCmtText = textBoxContentDisplay.getText();
    if ((this.setText != null) && !this.setText.isEmpty()) {
      this.reviewCmtText.setText(this.setText);
    }
    this.reviewCmtText.setLayoutData(gridData);


    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(gridData);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    this.comment = this.reviewCmtText.getText();
    if (this.reviewCmtText.getEnabled()) {
      super.okPressed();
    }
    // For Users with no access if Ok pressed.
    else {
      this.comment = "";
      super.cancelPressed();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    this.comment = "";
    super.cancelPressed();
  }

  /**
   * Get the review comments entered by the user
   *
   * @return Review comment
   */
  public String getComments() {
    return this.comment;
  }


  /**
   * @return the sectionTitle
   */
  public String getSectionTitle() {
    return this.sectionTitle;
  }


  /**
   * @param sectionTitle the sectionTitle to set
   */
  public void setSectionTitle(final String sectionTitle) {
    this.sectionTitle = sectionTitle;
  }


  /**
   * @return the titleText
   */
  public String getTitleText() {
    return this.titleText;
  }


  /**
   * @param titleText the titleText to set
   */
  public void setTitleText(final String titleText) {
    this.titleText = titleText;
  }


  /**
   * @return the titleMessageText
   */
  public String getTitleMessageText() {
    return this.titleMessageText;
  }


  /**
   * @param titleMessageText the titleMessageText to set
   */
  public void setTitleMessageText(final String titleMessageText) {
    this.titleMessageText = titleMessageText;
  }


  /**
   * @return the remarkLableText
   */
  public String getRemarkLableText() {
    return this.remarkLableText;
  }


  /**
   * @param remarkLableText the remarkLableText to set
   */
  public void setRemarkLableText(final String remarkLableText) {
    this.remarkLableText = remarkLableText;
  }

}
