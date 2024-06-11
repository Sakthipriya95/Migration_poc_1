/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import com.bosch.caltool.cdr.ui.wizard.pages.resolver.ReviewCalDataPageResolver;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author adn1cob
 */
public class ReviewCalDataWizardPage extends WizardPage {

  /**
   * Title for the Wizard page
   */
  private static final String PAGE_TITLE = "Calibration Data Review Summary";
  /**
   * Description for the wizard page
   */
  private static final String PAGE_DESCRIPTION = "Click Finish to store the review results in the database.";


  private Button enableButton;


  private Label paramsNotReviewedInA2l;
  private Text projIDCardText;
  private Text variantText;
  private Text a2lFileText;
  private Text groupWrkPakgeText;
  private Text calEngineerText;
  private Text auditorText;
  private Text functionsReviewedText;
  private Text paramsReviewedText;
  // ICDM-1726
  private Text paramsNotReviewedInA2lText;

  // ICDM-2477
  private Label paramsNotRvwdInRuleset;
  private Text paramsNotRvwdInRulesetText;

  private Label paramsNotRvwdWithoutRule;
  private Text paramsNotRvwdWithoutRuleText;

  private ReviewCalDataPageResolver reviewCalDataPageResolver;

  /**
   * Instance of calDataReviewWizard
   */
  private CalDataReviewWizard calDataReviewWizard;
  private Button mailChkBox;
  private List toUserNameList;
  /**
   * Sorted set of selected participants
   */
  private Button userDelButton;
  private String[] selectedUserList;
  private Set<User> toUserList = new HashSet<>();
  private Button addUserBtn;


  /**
   * * CONSTANT FOR "null,null"
   */
  public static final String STR_NULL_NULL = "null,null";

  /**
   * Constructor
   *
   * @param pageName title
   */
  public ReviewCalDataWizardPage(final String pageName) {
    super(pageName);
    setTitle(PAGE_TITLE);
    setDescription(PAGE_DESCRIPTION);
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG4_67X57));
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    final Composite workArea = new Composite(scrollComp, SWT.NONE);

    workArea.setLayout(new GridLayout());
    workArea.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    this.calDataReviewWizard = (CalDataReviewWizard) ReviewCalDataWizardPage.this.getWizard();

    Group reviewInfoGroup = new Group(workArea, SWT.NONE);
    reviewInfoGroup.setFont(workArea.getFont());
    reviewInfoGroup.setText("Reviewed info:");
    reviewInfoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    reviewInfoGroup.setLayout(new GridLayout(2, false));


    new Label(reviewInfoGroup, SWT.NONE).setText("Project ID card :");

    this.projIDCardText = new Text(reviewInfoGroup, SWT.SINGLE);
    this.projIDCardText.setEditable(false);
    final GridData projIDCardTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.projIDCardText.setLayoutData(projIDCardTextData);

    new Label(reviewInfoGroup, SWT.NONE).setText("Variant :");

    this.variantText = new Text(reviewInfoGroup, SWT.SINGLE);
    this.variantText.setEditable(false);
    final GridData variantTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.variantText.setLayoutData(variantTextData);

    new Label(reviewInfoGroup, SWT.NONE).setText("A2L file name :");
    this.a2lFileText = new Text(reviewInfoGroup, SWT.SINGLE);
    this.a2lFileText.setEditable(false);
    final GridData a2lFileTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.a2lFileText.setLayoutData(a2lFileTextData);


    new Label(reviewInfoGroup, SWT.NONE).setText("Work package / Group :");
    this.groupWrkPakgeText = new Text(reviewInfoGroup, SWT.SINGLE);
    this.groupWrkPakgeText.setEditable(false);
    final GridData groupWrkPakgeTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.groupWrkPakgeText.setLayoutData(groupWrkPakgeTextData);

    new Label(reviewInfoGroup, SWT.NONE).setText("Calibration Engineer :");
    this.calEngineerText = new Text(reviewInfoGroup, SWT.SINGLE);
    this.calEngineerText.setEditable(false);
    final GridData calEngineerTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.calEngineerText.setLayoutData(calEngineerTextData);

    new Label(reviewInfoGroup, SWT.NONE).setText("Auditor :");
    this.auditorText = new Text(reviewInfoGroup, SWT.SINGLE);
    this.auditorText.setEditable(false);
    GridData auditorTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.auditorText.setLayoutData(auditorTextData);

    Group resultInfoGroup = new Group(workArea, SWT.NONE);
    resultInfoGroup.setFont(workArea.getFont());
    resultInfoGroup.setText("Result info:");
    resultInfoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    resultInfoGroup.setLayout(new GridLayout(2, false));


    new Label(resultInfoGroup, SWT.NONE).setText("No of functions reviewed");
    this.functionsReviewedText = new Text(resultInfoGroup, SWT.SINGLE);
    this.functionsReviewedText.setEditable(false);
    GridData functionsReviewedTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.functionsReviewedText.setLayoutData(functionsReviewedTextData);


    new Label(resultInfoGroup, SWT.NONE).setText("No of parameters reviewed");
    this.paramsReviewedText = new Text(resultInfoGroup, SWT.SINGLE);
    this.paramsReviewedText.setEditable(false);
    GridData paramsReviewedTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.paramsReviewedText.setLayoutData(paramsReviewedTextData);

    // ICDM-1726
    this.paramsNotReviewedInA2l = new Label(resultInfoGroup, SWT.NONE);
    this.paramsNotReviewedInA2l.setText("No of parameters not reviewed (not in A2L file)");
    this.paramsNotReviewedInA2lText = new Text(resultInfoGroup, SWT.SINGLE);
    this.paramsNotReviewedInA2lText.setEditable(false);
    GridData paramsNotRvwdA2lTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.paramsNotReviewedInA2lText.setLayoutData(paramsNotRvwdA2lTextData);

    // ICDM-2477
    this.paramsNotRvwdWithoutRule = new Label(resultInfoGroup, SWT.NONE);
    this.paramsNotRvwdWithoutRule.setText("No of parameters without Rule/Incomplete rule");
    this.paramsNotRvwdWithoutRuleText = new Text(resultInfoGroup, SWT.SINGLE);
    this.paramsNotRvwdWithoutRuleText.setEditable(false);
    GridData paramsNotRvwdNoRuleTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.paramsNotRvwdWithoutRuleText.setLayoutData(paramsNotRvwdNoRuleTextData);

    // ICDM-2477
    this.paramsNotRvwdInRuleset = new Label(resultInfoGroup, SWT.NONE);
    this.paramsNotRvwdInRuleset.setText("No of parameters not reviewed (not in Ruleset)");
    this.paramsNotRvwdInRulesetText = new Text(resultInfoGroup, SWT.SINGLE);
    this.paramsNotRvwdInRulesetText.setEditable(false);
    GridData paramsNotRvwdRulesetTextData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.paramsNotRvwdInRulesetText.setLayoutData(paramsNotRvwdRulesetTextData);

    Group addOptGroup = new Group(workArea, SWT.NONE);
    addOptGroup.setFont(workArea.getFont());
    addOptGroup.setText("Additional Options");
    addOptGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    addOptGroup.setLayout(new GridLayout(1, false));

    Composite addOptGroupComposite = new Composite(addOptGroup, SWT.NONE);
    addOptGroupComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    int numColumn = 3;
    addOptGroupComposite.setLayout(new GridLayout(numColumn, false));

    this.enableButton = new Button(addOptGroupComposite, SWT.CHECK);
    this.enableButton.setText("Open Review Editor");
    GridData enableData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    enableData.horizontalSpan = numColumn;
    this.enableButton.setLayoutData(enableData);
    this.enableButton.setSelection(true);
    this.enableButton.setEnabled(true);

    this.mailChkBox = new Button(addOptGroupComposite, SWT.CHECK);
    this.mailChkBox.setText("Send Mail to below Users");
    GridData mailChkBoxGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    mailChkBoxGridData.horizontalSpan = numColumn;
    this.mailChkBox.setLayoutData(mailChkBoxGridData);
    this.mailChkBox.setSelection(false);
    this.mailChkBox.setEnabled(true);

    createUserSelFieldControl(addOptGroupComposite);

    this.reviewCalDataPageResolver = new ReviewCalDataPageResolver(this.calDataReviewWizard);
    scrollComp.setContent(workArea);
    scrollComp.setMinSize(workArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);
  }

  /**
   * @param workArea parent Composite
   */
  private void createUserSelFieldControl(final Composite workArea) {
    this.toUserNameList = new List(workArea, SWT.V_SCROLL | SWT.MULTI);
    final GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.heightHint = 7 * this.toUserNameList.getItemHeight();
    this.toUserNameList.setLayoutData(data);

    // userBtnActionComp for aligining the participants selection and delete buttons
    final Composite userBtnActionComp = new Composite(workArea, SWT.NONE);
    final GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginWidth = 0;
    gridLayout.marginTop = 0;
    userBtnActionComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    userBtnActionComp.setLayout(gridLayout);

    // add and delete participants button
    addButton(userBtnActionComp);
    delButton(userBtnActionComp);
    enableDisableMailUsrActionBtns();
    addActionListener();
  }


  /**
   * @param userBtnActionComp
   * @param addButtonImage
   */
  private void addButton(final Composite userBtnActionComp) {
    final Image addButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.ADD_16X16);
    this.addUserBtn = new Button(userBtnActionComp, SWT.PUSH);
    this.addUserBtn.setImage(addButtonImage);


    this.addUserBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final UserSelectionDialog userDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
            "Add User", "Add user", "Add user", "Add", true, false);
        userDialog.setSelectedMultipleUser(null);
        userDialog.open();
        ArrayList<User> selectedUsers = (ArrayList<User>) userDialog.getSelectedMultipleUser();
        if (CommonUtils.isNotEmpty(selectedUsers)) {
          for (User user : selectedUsers) {
            String selUserName = user.getDescription();
            if ((ReviewCalDataWizardPage.this.toUserNameList.indexOf(selUserName) == -1) &&
                (!ReviewCalDataWizardPage.STR_NULL_NULL.equalsIgnoreCase(selUserName))) {

              ReviewCalDataWizardPage.this.getToUserNameList().add(selUserName);
              ReviewCalDataWizardPage.this.getToUserList().add(user);
            }
          }
        }
      }
    });
  }


  /**
   * @param userBtnActionComp
   */
  private void delButton(final Composite userBtnActionComp) {
    final Image deleteButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETE_16X16);
    this.userDelButton = new Button(userBtnActionComp, SWT.PUSH);
    this.userDelButton.setImage(deleteButtonImage);
    this.userDelButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // Remove selected Users from Mail Sender List
        rmvSelUsrFromSndrList();
      }
    });
  }


  private void addActionListener() {

    // Selection Listener for Send mail check box
    getMailChkBox().addListener(SWT.Selection, (final Event event) -> enableDisableMailUsrActionBtns());

    // Selection Listener for User List Text Area field
    List toUsrList = ReviewCalDataWizardPage.this.getToUserNameList();

    if (CommonUtils.isNotEmpty(Arrays.asList(toUsrList))) {
      toUsrList.addListener(SWT.Selection, (final Event event) -> {
        ReviewCalDataWizardPage.this.setSelectedUserList(toUsrList.getSelection());
        enableDisableMailUsrActionBtns();
      });

      toUsrList.addKeyListener(new KeyAdapter() {

        @Override
        public void keyPressed(final KeyEvent event) {

          if (CommonUtils.isEqual(event.character, SWT.DEL)) {
            rmvSelUsrFromSndrList();
          }
        }
      });
    }
  }


  /**
   * @param b
   */
  private void enableDisableMailUsrActionBtns() {
    boolean isToEnable = getMailChkBox().getSelection();
    getToUserNameList().setEnabled(isToEnable);
    this.addUserBtn.setEnabled(isToEnable);
    if ((null != getSelectedUserList()) && (getSelectedUserList().length > 0)) {
      this.userDelButton.setEnabled(isToEnable);
    }
    else if (this.userDelButton.isEnabled()) {
      this.userDelButton.setEnabled(false);
    }
  }

  /**
   *
   */
  private void rmvSelUsrFromSndrList() {
    Arrays.asList(ReviewCalDataWizardPage.this.getSelectedUserList()).stream().forEach(userName -> {
      ReviewCalDataWizardPage.this.getToUserNameList().remove(userName);
      Set<User> toUsrList = ReviewCalDataWizardPage.this.getToUserList();
      toUsrList = toUsrList.stream().filter(user -> CommonUtils.isNotEqual(user.getDescription(), userName))
          .collect(Collectors.toSet());
      ReviewCalDataWizardPage.this.setToUserList(toUsrList);
    });

    // Clearing the selected user array after delete action
    setSelectedUserList(new String[0]);
    enableDisableMailUsrActionBtns();
  }

  /**
   * Method used to UI Data
   */
  public void fillData() {
    Display.getDefault().syncExec(() -> {
      createReviewedInfoGroup();
      createResultInfoGroup();
    });
  }

  private void createReviewedInfoGroup() {
    ReviewOutput reviewOutput = this.calDataReviewWizard.getCdrWizardUIModel().getReviewOutput();
    if (reviewOutput != null) {


      String pidcVersionName = "";
      PidcVersion pidcVersion = reviewOutput.getPidcVersion();
      if (CommonUtils.isNotNull(pidcVersion)) {
        pidcVersionName = pidcVersion.getName();
      }
      this.projIDCardText.setText(pidcVersionName);

      String variantName = "";
      if (reviewOutput.getPidcVariantName() != null) {
        variantName = reviewOutput.getPidcVariantName();
      }
      this.variantText.setText(variantName);

      String a2lName = "";
      if (reviewOutput.getA2lFileName() != null) {
        a2lName = reviewOutput.getA2lFileName();
      }
      this.a2lFileText.setText(a2lName);

      String workPackageGroupName = CDRConstants.CDR_SOURCE_TYPE.A2L_FILE.getUIType();
      if (reviewOutput.getWorkPackageGroupName() != null) {
        workPackageGroupName = reviewOutput.getWorkPackageGroupName();
      }
      this.groupWrkPakgeText.setText(workPackageGroupName);

      String calEngineerName = "";
      if (reviewOutput.getCalEngineerName() != null) {
        calEngineerName = reviewOutput.getCalEngineerName();
      }
      this.calEngineerText.setText(calEngineerName);

      String auditorName = "";
      if (reviewOutput.getAuditorName() != null) {
        auditorName = reviewOutput.getAuditorName();
      }
      this.auditorText.setText(auditorName);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    return false;
  }

  /**
   *
   */
  private void createResultInfoGroup() {

    CalDataReviewWizard calDataRvwWizard = (CalDataReviewWizard) getWizard();
    CDRWizardUIModel cdrWizardUIModel = calDataRvwWizard.getCdrWizardUIModel();
    ReviewOutput reviewOutput = cdrWizardUIModel.getReviewOutput();

    if (CommonUtils.isNotNull(reviewOutput)) {

      this.functionsReviewedText.setText(Integer.toString(reviewOutput.getNoOfReviewedFunctions()));
      this.paramsReviewedText.setText(Integer.toString(reviewOutput.getNoOfReviewedParam()));

      // ICDM-1726
      String sourceType = cdrWizardUIModel.getSourceType();
      if (CommonUtils.isNotNull(sourceType) &&
          sourceType.equals(CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType())) {
        showParamNotRvwMsg(true);

        // ICDM-2477
        if (!calDataRvwWizard.getWpSelWizPage().getRuleSetRadio().getSelection()) {
          this.paramsNotRvwdInRuleset.setVisible(false);
          this.paramsNotRvwdInRulesetText.setVisible(false);
        }

        setNotRvwParaminA2lList(reviewOutput);

        // ICDM-2477
        setNotRvwdParamInRuleSetList(reviewOutput);

        // ICDM-2477
        int noOfNotRvwdParamsWithoutRule = reviewOutput.getParamsNotRvwdWithoutRule();
        if (noOfNotRvwdParamsWithoutRule > 0) {
          this.paramsNotRvwdWithoutRule.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
          this.paramsNotRvwdWithoutRuleText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        }
        else {
          this.paramsNotRvwdWithoutRule.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
          this.paramsNotRvwdWithoutRuleText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }
        this.paramsNotRvwdWithoutRuleText.setText(Integer.toString(noOfNotRvwdParamsWithoutRule));
      }
      else {
        showParamNotRvwMsg(false);
      }
    }
  }


  /**
   * @param reviewOutput
   */
  private void setNotRvwdParamInRuleSetList(final ReviewOutput reviewOutput) {
    int noOfNotRvwdParamsInRuleset = reviewOutput.getParamsNotRvwdInRuleset();
    if (noOfNotRvwdParamsInRuleset > 0) {
      this.paramsNotRvwdInRuleset.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      this.paramsNotRvwdInRulesetText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    }
    else {
      this.paramsNotRvwdInRuleset.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
      this.paramsNotRvwdInRulesetText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
    }
    this.paramsNotRvwdInRulesetText.setText(Integer.toString(noOfNotRvwdParamsInRuleset));
  }


  /**
   * @param reviewOutput
   */
  private void setNotRvwParaminA2lList(final ReviewOutput reviewOutput) {
    int noOfNotRvwdParamsInA2l = reviewOutput.getParamsNotReviewedInA2l();
    if (noOfNotRvwdParamsInA2l > 0) {
      this.paramsNotReviewedInA2l.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      this.paramsNotReviewedInA2lText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    }
    else {
      this.paramsNotReviewedInA2l.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
      this.paramsNotReviewedInA2lText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
    }
    this.paramsNotReviewedInA2lText.setText(Integer.toString(noOfNotRvwdParamsInA2l));
  }


  /**
   *
   */
  private void showParamNotRvwMsg(final boolean showMsg) {
    this.paramsNotReviewedInA2l.setVisible(showMsg);
    this.paramsNotReviewedInA2lText.setVisible(showMsg);
    this.paramsNotRvwdInRuleset.setVisible(showMsg);
    this.paramsNotRvwdInRulesetText.setVisible(showMsg);
    this.paramsNotRvwdWithoutRule.setVisible(showMsg);
    this.paramsNotRvwdWithoutRuleText.setVisible(showMsg);
  }


  /**
   * @return the enableButton
   */
  public Button getEnableButton() {
    return this.enableButton;
  }


  /**
   * @param enableButton the enableButton to set
   */
  public void setEnableButton(final Button enableButton) {
    this.enableButton = enableButton;
  }


  /**
   * @return the mailButton
   */
  public Button getMailChkBox() {
    return this.mailChkBox;
  }

  /**
   *
   */
  public void backPressed() {

    MessageDialog.openInformation(getShell(), CDRConstants.INFORMATION_DIALOG, CDRConstants.REVIEW_DISCARD_MESSAGE);
    this.reviewCalDataPageResolver.processBackPressed();

    if (this.calDataReviewWizard.getCdrWizardUIModel().getCancelledResultId() != null) {
      this.calDataReviewWizard.getCdrWizardUIModel().setCancelledResultId(null);
    }

  }


  /**
   * @return the toUserNameList
   */
  public List getToUserNameList() {
    return this.toUserNameList;
  }


  /**
   * @param toUserNameList the mail receiver Name List
   */
  public void setMailSenderList(final List toUserNameList) {
    this.toUserNameList = toUserNameList;
  }

  /**
   * @return the selectedUserList
   */
  public String[] getSelectedUserList() {
    return this.selectedUserList;
  }


  /**
   * @param selectedUserList the selectedUserList to set
   */
  public void setSelectedUserList(final String[] selectedUserList) {
    this.selectedUserList = selectedUserList;
  }


  /**
   * @return the toUserList
   */
  public Set<User> getToUserList() {
    return this.toUserList;
  }


  /**
   * @param toUserList the toUserList to set
   */
  public void setToUserList(final Set<User> toUserList) {
    this.toUserList = toUserList;
  }

}
