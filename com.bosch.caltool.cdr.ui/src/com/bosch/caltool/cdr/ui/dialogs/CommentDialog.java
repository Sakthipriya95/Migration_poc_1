/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.LinkProcessor;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * ICDM-826 This dialog is to enter the comment for a group of parameters
 *
 * @author mkl2cob
 */
public class CommentDialog extends AbstractDialog {

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
   * Result Parameter for which comment is to be updated - null in case of multiple params
   */
  private final CDRResultParameter rvwParam;
  private final List<CDRResultParameter> paramList = new ArrayList<>();

  /**
   * Rvw comment text size in the dialg
   */
  private static final int RVW_COMMENT_TEXT_SIZE = 4000;
  private final ReviewResultClientBO resultData;
  private Button includeScoreBtn;
  private Button overwriteCmtBtn;
  private Text linkText;
  private Button rvwCmtBtn;
  private String sourceResultLink;
  private boolean includeScore;
  private boolean overwriteComments;
  private boolean impCmtBtnSelected;

  /**
   * Define size for the cmd args
   */
  private static final int CMD_ARGS_SIZE = 2;

  /**
   * Define size for the cmd args
   */
  private static final int CMD_ARGS_SIZE_WITH_VAR = 3;
  
  private Combo prevCommentCombo;

  /**
   * @param parentShell parent shell
   * @param rvwParam2 Result Parameter for which comment is to be updated - null in case of multiple params
   * @param paramList CDR result parameter List
   * @param resultData Review Result Client BO
   */
  public CommentDialog(final Shell parentShell, final CDRResultParameter rvwParam2,
      final List<CDRResultParameter> paramList, final ReviewResultClientBO resultData) {
    super(parentShell);
    this.rvwParam = rvwParam2;
    this.resultData = resultData;
    if (null != paramList) {
      this.paramList.addAll(paramList);
    }
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
    setMessage("Enter/Import the Review Comments for the selected Parameter(s)", IMessageProvider.INFORMATION);
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


    if (CommonUtils.isNotNull(this.rvwParam)) {
      final Composite firstComposite = getFormToolkit().createComposite(this.form.getBody());
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      firstComposite.setLayout(gridLayout);
      firstComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
      getFormToolkit().createLabel(firstComposite, "Review Parameter:");
      getFormToolkit().createLabel(firstComposite, this.rvwParam.getName());
    }

    this.rvwCmtBtn = new Button(this.form.getBody(), SWT.RADIO);
    this.rvwCmtBtn.setText("Enter Review Comment here :");
    this.rvwCmtBtn.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.rvwCmtBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        CommentDialog.this.linkText.setEnabled(false);
        CommentDialog.this.includeScoreBtn.setEnabled(false);
        CommentDialog.this.overwriteCmtBtn.setEnabled(false);
        CommentDialog.this.reviewCmtText.setEnabled(true);
        CommentDialog.this.reviewCmtText.setFocus();
        CommentDialog.this.reviewCmtText.setSelection(CommentDialog.this.reviewCmtText.getText().length());
      }
    });
    // grid data to remove the extra space at the end of the dialog
    gridData.heightHint = 50;

    // ICDM-2007 (Parent task : ICDM-1774)
    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, RVW_COMMENT_TEXT_SIZE, gridData);

    this.reviewCmtText = textBoxContentDisplay.getText();
    this.reviewCmtText.setLayoutData(gridData);
    if (CommonUtils.isNotNull(this.rvwParam) && CommonUtils.isNotNull(this.rvwParam.getRvwComment())) {
      // Preset the previous comment of the parameter - ICDM-1147
      this.reviewCmtText.setText(this.rvwParam.getRvwComment());
    }

    getFormToolkit().createLabel(this.form.getBody(), "Choose a previously entered comment:");
    loadCommentsInCombo();
    
    Button impCmtBtn = new Button(this.form.getBody(), SWT.RADIO);
    impCmtBtn.setText("Import from another review :");
    impCmtBtn.setLayoutData(GridDataUtil.getInstance().getGridData());
    impCmtBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        setImpCmtBtnSelected(true);
        CommentDialog.this.linkText.setEnabled(true);
        CommentDialog.this.includeScoreBtn.setEnabled(true);
        CommentDialog.this.overwriteCmtBtn.setEnabled(true);
        CommentDialog.this.reviewCmtText.setEnabled(false);
      }
    });

    getFormToolkit().createLabel(this.form.getBody(),
        " In order to import review comment(s) from another review, paste the review result link url below.\n Right-click on the result and click 'Copy Review Result Link'.");
    final Composite mainComposite = getFormToolkit().createComposite(this.form.getBody());
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    mainComposite.setLayout(gridLayout);

    mainComposite.setLayoutData(GridDataUtil.getInstance().getGridData());


    getFormToolkit().createLabel(mainComposite, "Source Review Link :");
    this.linkText = new Text(mainComposite, SWT.BORDER);
    this.linkText.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.linkText.setEnabled(false);
    this.linkText.setMessage("icdm:cdrid,100");

    getFormToolkit().createLabel(mainComposite, "");
    this.includeScoreBtn = new Button(mainComposite, SWT.CHECK);
    this.includeScoreBtn.setText("Include source review comment(s) with score >= 7");
    this.includeScoreBtn.setEnabled(false);

    getFormToolkit().createLabel(mainComposite, "");
    this.overwriteCmtBtn = new Button(mainComposite, SWT.CHECK);
    this.overwriteCmtBtn.setText("Overwrite existing review comment(s)");
    this.overwriteCmtBtn.setEnabled(false);
    // Check for Enabling editing
    enableEdititng();

    this.form.getBody().setLayout(new GridLayout());
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * prevoius entered comments combo box
   * @param versionsCombo
   */
  private void loadCommentsInCombo() {

    this.prevCommentCombo = new Combo(this.form.getBody(), SWT.READ_ONLY);
    GridData comboGridData = new GridData();
    comboGridData.grabExcessHorizontalSpace = true;
    comboGridData.horizontalAlignment = GridData.FILL;
    this.prevCommentCombo.setLayoutData(comboGridData);
    Map<Long, RvwUserCmntHistory> rvwCommentHistoryForUser;
    SortedSet<RvwCommentTemplate> allRvwCommentTemplate;
    try {
      rvwCommentHistoryForUser = new CommonDataBO().getRvwCommentHistoryForUser();
      allRvwCommentTemplate = new CommonDataBO().getAllRvwCommentTemplate();

      List<RvwUserCmntHistory> rvwCmntHistoryList = new ArrayList<>(rvwCommentHistoryForUser.values());

      Collections.sort(rvwCmntHistoryList);
      Collections.reverse(rvwCmntHistoryList);

      for (RvwUserCmntHistory rvwCmntHistory : rvwCmntHistoryList) {
        prevCommentCombo.add(rvwCmntHistory.getName());
      }

      for (RvwCommentTemplate rvwCommentTemplate : allRvwCommentTemplate) {
        prevCommentCombo.add(rvwCommentTemplate.getName());
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }


    this.prevCommentCombo.addSelectionListener(new SelectionListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(SelectionEvent arg0) {
        reviewCmtText.setText(prevCommentCombo.getText());
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent arg0) {
        // No implementation needed.
      }
    });
  }

  /**
   * Called to disable or enable the Comment text box
   */
  private void enableEdititng() {
    // Check if the User has access for the Result. If so Allow to edit the Comments. for Single Comment
    // Check if the User has access for the Result. If so Allow to edit the Comments. for Multiple Comment
    if (validateSingleParam() || validateParamList()) {
      this.reviewCmtText.setEnabled(true);
      this.reviewCmtText.setEditable(true);
    }
    // Disable the Review Comment text
    else {
      this.reviewCmtText.setEnabled(false);
      this.reviewCmtText.setEditable(false);
    }
  }


  /**
   * @return
   */
  private boolean validateParamList() {
    return CommonUtils.isNotNull(this.paramList) && !this.paramList.isEmpty() &&
        CommonUtils.isNotNull(this.resultData.getResultBo()) && this.resultData.getResultBo().isModifiable();
  }


  /**
   * @return
   */
  private boolean validateSingleParam() {
    return CommonUtils.isNotNull(this.rvwParam) &&
        CommonUtils.isNotNull(this.resultData.getResultBo().getCDRResult()) &&
        this.resultData.getResultBo().isModifiable();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.rvwCmtBtn.getSelection()) {
      this.reviewComment = this.reviewCmtText.getText();
      this.sourceResultLink = null;
      this.includeScore = false;
      this.overwriteComments = false;
    }
    else {
      this.reviewComment = null;
      this.sourceResultLink = this.linkText.getText();
      this.includeScore = this.includeScoreBtn.getSelection();
      this.overwriteComments = this.overwriteCmtBtn.getSelection();
    }
    if (this.linkText.getEnabled()) {
      LinkProcessor linkProcessor = new LinkProcessor(this.sourceResultLink);
      try {
        linkProcessor.validateLink();
        ConcurrentMap<String, String> linkProperties = linkProcessor.getLinkProperties();
        String cdrID = linkProperties.get(EXTERNAL_LINK_TYPE.CDR_RESULT.getKey());
        String[] strIds = cdrID.split("-");

        // Check for variant id in the input for checking the cdr result
        if ((strIds == null) || ((strIds.length != CMD_ARGS_SIZE) && (strIds.length != CMD_ARGS_SIZE_WITH_VAR))) {
          CDMLogger.getInstance().errorDialog("Invalid hyperlink for CDR Result!", Activator.PLUGIN_ID);
          return;
        }
        this.sourceResultLink = strIds[0];
        super.okPressed();
      }
      catch (ExternalLinkException e) {
        CDMLogger.getInstance().errorDialog("Invalid hyperlink for CDR Result!", e, Activator.PLUGIN_ID);
      }
    }
    else {
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
  }


  /**
   * @return the rvwCmtBtn
   */
  public Button getRvwCmtBtn() {
    return this.rvwCmtBtn;
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


  /**
   * @return the overwriteComments
   */
  public boolean isOverwriteComments() {
    return this.overwriteComments;
  }


  /**
   * @param overwriteComments the overwriteComments to set
   */
  public void setOverwriteComments(final boolean overwriteComments) {
    this.overwriteComments = overwriteComments;
  }

  /**
   * @return the overwriteCmtBtn
   */
  public Button getOverwriteCmtBtn() {
    return this.overwriteCmtBtn;
  }


  /**
   * @param overwriteCmtBtn the overwriteCmtBtn to set
   */
  public void setOverwriteCmtBtn(final Button overwriteCmtBtn) {
    this.overwriteCmtBtn = overwriteCmtBtn;
  }


  /**
   * @return the sourceResultLink
   */
  public String getSourceResultLink() {
    return this.sourceResultLink;
  }


  /**
   * @param sourceResultLink the sourceResultLink to set
   */
  public void setSourceResultLink(final String sourceResultLink) {
    this.sourceResultLink = sourceResultLink;
  }


  /**
   * @return the includeScore
   */
  public boolean isIncludeScore() {
    return this.includeScore;
  }


  /**
   * @param includeScore the includeScore to set
   */
  public void setIncludeScore(final boolean includeScore) {
    this.includeScore = includeScore;
  }


  /**
   * @return the impCmtBtnSelected
   */
  public boolean isImpCmtBtnSelected() {
    return this.impCmtBtnSelected;
  }


  /**
   * @param impCmtBtnSelected the impCmtBtnSelected to set
   */
  public void setImpCmtBtnSelected(final boolean impCmtBtnSelected) {
    this.impCmtBtnSelected = impCmtBtnSelected;
  }
}
