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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author sng9kor
 */
public class ReviewInfoCommentDialog extends AbstractDialog {


  /**
   * Review Comment Dialog
   */
  private static final String REVIEW_COMMENT = "Set Review Comments";
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
   * String to store review comments
   */
  private String reviewComment;

  /**
   * Text field for review comments
   */
  private Text reviewCmtText;


  /**
   * Rvw comment text size in the dialg
   */
  private static final int RVW_COMMENT_TEXT_SIZE = 4000;
  private final ReviewResultClientBO resultData;
  private Button includeScoreBtn;
  private boolean includeScore;


  /**
   * @param parentShell
   * @param resultData
   */
  public ReviewInfoCommentDialog(final Shell parentShell, final ReviewResultClientBO resultData) {
    super(parentShell);
    this.resultData = resultData;
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
    setMessage("Enter the Review Comments", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(REVIEW_COMMENT);
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
   * create section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Review Comments");
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
    getFormToolkit().createLabel(this.form.getBody(), "Enter Review Comment here :");
    gridData.heightHint = 50;
    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, RVW_COMMENT_TEXT_SIZE, gridData);

    this.reviewCmtText = textBoxContentDisplay.getText();
    this.reviewCmtText.setLayoutData(gridData);
    this.reviewCmtText.setText(this.resultData.getResultBo().getCDRResult().getComments() == null ? ""
        : this.resultData.getResultBo().getCDRResult().getComments());


    final Composite mainComposite = getFormToolkit().createComposite(this.form.getBody());
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    mainComposite.setLayout(gridLayout);
    mainComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.form.getBody().setLayout(new GridLayout());
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    this.reviewComment = this.reviewCmtText.getText();

    if ((this.reviewComment == null) || this.reviewComment.isEmpty()) {
      if (MessageDialogUtils.getConfirmMessageDialogWithYesNo(REVIEW_COMMENT,
          "Comment is not provided. Click OK to save")) {
        super.okPressed();
      }
    }
    else {
      super.okPressed();
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    this.reviewComment = "";
    super.cancelPressed();
  }

  /**
   * Get the review comments entered by the user
   *
   * @return Review comment
   */
  public String getReviewComments() {
    return this.reviewComment;
  }


}

