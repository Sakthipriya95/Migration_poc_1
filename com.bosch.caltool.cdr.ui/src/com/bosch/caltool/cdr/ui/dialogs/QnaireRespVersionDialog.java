/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CalendarDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author mkl2cob
 */
public class QnaireRespVersionDialog extends AbstractDialog {

  /**
   * mandatory field info text
   */
  private static final String MANDATORY_FIELD_INFO_TEXT = "This field is mandatory.";
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * text field for 'Reviewed By'
   */
  private Text rvwByTextField;

  /**
   * text field for 'Reviewed On'
   */
  private Text rvwonTextField;

  /**
   * text field for remarks
   */
  private TextBoxContentDisplay remarkTextField;

  private Text versionNameTxt;
  /**
   * SortedSet<RvwQnaireAnswer>
   */
  private final QnaireRespEditorDataHandler dataHandler;

  /**
   * NT user name of Reviewer
   */
  private String ntUsername;

  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

  private Button okBtn;

  private Date reviewDate;

  private final String title;

  private final String message;

  private final String headingTxt;

  private final boolean isCreateVersFlag;

  private Button rvwdByBrowseBtn;

  private Button rvwTimeSelBtn;

  /**
   * @param parentShell Shell
   * @param dataHandler SortedSet<RvwQnaireAnswer>
   * @param title
   * @param message as input
   * @param headingTxt
   * @param isCreateVersFlag
   */
  public QnaireRespVersionDialog(final Shell parentShell, final QnaireRespEditorDataHandler dataHandler,
      final String title, final String message, final String headingTxt, final boolean isCreateVersFlag) {
    super(parentShell);
    this.dataHandler = dataHandler;
    this.title = title;
    this.message = message;
    this.isCreateVersFlag = isCreateVersFlag;
    this.headingTxt = headingTxt;
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(this.title);
    // Set the message
    setMessage(this.message, IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText(this.headingTxt);
    // calling parent
    super.configureShell(newShell);

  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());

    // create composite on parent comp
    createComposite();
    return this.top;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout(9, true);
    this.composite.setLayout(gridLayout);
    // create section
    createQuesStateSection();
    createDetailsSection();
    createReviewSection();
    // Load the version details only for show details case
    if (!this.isCreateVersFlag) {
      loadDetailsForShowVersion();
    }
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    if (this.isCreateVersFlag) {
      this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Create", false);
      enableOkButton();
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
    }
    else {
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
    }
  }

  /**
   * reviewed date modify listener
   */
  private void reviewedDateModifiedListener() {
    if (this.isCreateVersFlag && !this.rvwonTextField.getText().isEmpty()) {
      SimpleDateFormat df12 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_12,
          Locale.getDefault(Locale.Category.FORMAT));
      SimpleDateFormat df15 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_15,
          Locale.getDefault(Locale.Category.FORMAT));
      try {
        this.reviewDate = df12.parse(this.rvwonTextField.getText());
        String existingDateStr = null;
        Date existingDate = null;
        Object[] qnaireVersArray =
            new TreeSet<>(this.dataHandler.getQnaireVersions(this.dataHandler.getQuesResponse().getId())).toArray();
        // get latest qnaire version
        RvwQnaireRespVersion latestQnaireRespVersion = null;
        if (qnaireVersArray.length > 1) {
          latestQnaireRespVersion = (RvwQnaireRespVersion) qnaireVersArray[1];
        }
        if (null == latestQnaireRespVersion) {
          // get working set
          latestQnaireRespVersion = (RvwQnaireRespVersion) qnaireVersArray[0];
        }
        if (null != latestQnaireRespVersion.getCreatedDate()) {
          existingDateStr = latestQnaireRespVersion.getCreatedDate();
          existingDate = df15.parse(existingDateStr);
          existingDateStr = df12.format(existingDate);
        }
        if ((null != existingDate) && !CommonUtils.isEqualIgnoreCase(existingDateStr, this.rvwonTextField.getText()) &&
            this.reviewDate.before(existingDate)) {
          // Review date should not be less than the previous review date
          MessageDialogUtils.getErrorMessageDialog("InValid",
              "Review date cannot be lesser than the previous set date");
          this.rvwonTextField.setText("");
        }
        else if (this.reviewDate.after(new Date())) {
          // review date should not be greter than current date
          MessageDialogUtils.getErrorMessageDialog("InValid", "Review date cannot be greater than the current date");
          this.rvwonTextField.setText("");
        }
        else {
          enableOkButton();
        }
      }
      catch (ParseException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        this.rvwonTextField.setText("");
      }
    }
  }

  /**
   * enable ok button based on conditions
   */
  private void enableOkButton() {
    // check needed only for version creation
    if (this.isCreateVersFlag) {
      final String nameEng = this.versionNameTxt.getText();
      final String reviewer = this.rvwByTextField.getText();
      final String reviewDateStr = this.rvwonTextField.getText();
      boolean validateFields = CommonUtils.isNotEmptyString(nameEng.trim()) &&
          CommonUtils.isNotEmptyString(reviewer.trim()) && CommonUtils.isNotEmptyString(reviewDateStr.trim());
      this.okBtn.setEnabled(validateFields);
    }
  }

  /**
   * This method initializes Details section
   */
  private void createDetailsSection() {
    Section sectionDetails = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "Details",
        ExpandableComposite.TITLE_BAR);
    // create form
    Form formDetails = getFormToolkit().createForm(sectionDetails);
    getFormToolkit().createLabel(formDetails.getBody(), "Version Name");
    this.versionNameTxt = getFormToolkit().createText(formDetails.getBody(), null, SWT.SINGLE | SWT.BORDER);
    GridData textGridData = GridDataUtil.getInstance().getTextGridData();
    textGridData.horizontalSpan = 2;
    this.versionNameTxt.setLayoutData(textGridData);
    // adding mandatory decorators
    createDecorators(this.versionNameTxt);
    this.versionNameTxt.addModifyListener(event -> enableOkButton());

    getFormToolkit().createLabel(formDetails.getBody(), "Remarks");
    GridData gridData1 = GridDataUtil.getInstance().getGridData();
    gridData1.horizontalSpan = 2;
    this.remarkTextField = new TextBoxContentDisplay(formDetails.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL, gridData1, true, 4000);


    // set the layout
    GridLayout gridLayoutForForm = new GridLayout();
    gridLayoutForForm.numColumns = 3;
    formDetails.getBody().setLayout(gridLayoutForForm);
    formDetails.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    GridData gridDataForSection = GridDataUtil.getInstance().getGridData();
    gridDataForSection.horizontalSpan = 5;
    gridDataForSection.verticalSpan = 2;
    sectionDetails.setLayoutData(gridDataForSection);
    sectionDetails.setLayout(new GridLayout());
    sectionDetails.setClient(formDetails);
  }

  /**
   *
   */
  private void createDecorators(final Text txtbox) {
    ControlDecoration controlDecrators = new ControlDecoration(txtbox, SWT.LEFT | SWT.TOP);
    if (this.isCreateVersFlag) {
      this.decorators.showReqdDecoration(controlDecrators, MANDATORY_FIELD_INFO_TEXT);
    }
  }

  /**
   * This method initializes Questionnaire state section
   */
  private void createQuesStateSection() {
    Section sectionQuesState = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        "State of Questionnaire", ExpandableComposite.TITLE_BAR);
    // create form
    Form formQuesState = getFormToolkit().createForm(sectionQuesState);

    String qnaireRespVersStatus =
        this.dataHandler.getQnaireRespModel().getRvwQnrRespVersion().getQnaireRespVersStatus();

    fillQuesStateSection(formQuesState, qnaireRespVersStatus);

    fillerLabels(formQuesState.getBody(), 15);

    // set the layout
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 15;
    formQuesState.getBody().setLayout(gridLayout);
    GridData sectionGridData = GridDataUtil.getInstance().getGridData();
    sectionGridData.horizontalSpan = 4;
    sectionQuesState.setLayoutData(sectionGridData);
    sectionQuesState.setLayout(new GridLayout());
    sectionQuesState.setClient(formQuesState);
  }

  /**
   * @param formQuesState
   * @param statusType
   */
  private void fillQuesStateSection(final Form formQuesState, final String statusType) {
    Label imgLabel = getFormToolkit().createLabel(formQuesState.getBody(), "", SWT.NONE);

    if (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(statusType)) {
      Image tempImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16);
      imgLabel.setImage(tempImage);
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(statusType)) {
      Image tempImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.EXCLAMATION_ICON_16X16);
      imgLabel.setImage(tempImage);
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType().equals(statusType)) {
      Image tempImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.REMOVE_16X16);
      imgLabel.setImage(tempImage);
    }

    getFormToolkit().createLabel(formQuesState.getBody(),
        CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(statusType).getUiType());
  }

  /**
   * @param grp
   * @param limit
   */
  private void fillerLabels(final Composite grp, final int limit) {
    for (int i = 0; i < limit; i++) {
      new Label(grp, SWT.NONE);
    }
  }

  /**
   * This method initializes Review section
   */
  private void createReviewSection() {
    Section sectionReview = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        "Review of Questionnaire", ExpandableComposite.TITLE_BAR);
    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    gridData.horizontalSpan = 2;
    Form formReview = getFormToolkit().createForm(sectionReview);
    getFormToolkit().createLabel(formReview.getBody(), "Reviewed By");
    this.rvwByTextField = getFormToolkit().createText(formReview.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.rvwByTextField.setLayoutData(gridData);
    this.rvwByTextField.setEditable(false);
    // adding mandatory decorators
    createDecorators(this.rvwByTextField);

    this.rvwByTextField.addModifyListener(event -> enableOkButton());
    this.rvwdByBrowseBtn = getFormToolkit().createButton(formReview.getBody(), "", SWT.PUSH);
    this.rvwdByBrowseBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE));
    this.rvwdByBrowseBtn.setToolTipText("Select User");
    this.rvwdByBrowseBtn.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {

        final UserSelectionDialog partsDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
            "Add User", "Add user", "Add user", "Add", true, false);
        partsDialog.setSelectedMultipleUser(null);
        int openDialogStatus = partsDialog.open();
        List<User> selectedUsers = partsDialog.getSelectedMultipleUser();
        if ((selectedUsers != null) && !selectedUsers.isEmpty() && (openDialogStatus == 0)) {
          User selectedUser = selectedUsers.get(0);
          QnaireRespVersionDialog.this.ntUsername = selectedUser.getName();
          QnaireRespVersionDialog.this.rvwByTextField.setText(selectedUser.getDescription());
        }
      }

    });

    getFormToolkit().createLabel(formReview.getBody(), "Reviewed On");

    this.rvwonTextField = getFormToolkit().createText(formReview.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.rvwonTextField.setLayoutData(gridData);
    this.rvwonTextField.setEditable(false);
    // adding mandatory decorators
    createDecorators(this.rvwonTextField);

    this.rvwonTextField.addModifyListener(event -> reviewedDateModifiedListener());
    this.rvwTimeSelBtn = getFormToolkit().createButton(formReview.getBody(), "", SWT.PUSH);
    this.rvwTimeSelBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CALENDAR_16X16));
    this.rvwTimeSelBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        CalendarDialog calDailog = new CalendarDialog();
        calDailog.addCalendarDialog(formReview.getBody(), QnaireRespVersionDialog.this.rvwonTextField,
            DateFormat.DATE_FORMAT_12);
      }
    });


    // set the layout
    GridLayout gridLayoutForForm = new GridLayout();
    gridLayoutForForm.numColumns = 4;
    formReview.getBody().setLayout(gridLayoutForForm);
    GridData sectionGridData = GridDataUtil.getInstance().getGridData();
    sectionGridData.horizontalSpan = 4;
    formReview.getBody().setLayoutData(sectionGridData);
    sectionReview.setLayoutData(sectionGridData);
    sectionReview.setClient(formReview);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    RvwQnaireRespVersionServiceClient client = new RvwQnaireRespVersionServiceClient();

    RvwQnaireRespVersion qnaireRespVersion = new RvwQnaireRespVersion();
    qnaireRespVersion.setVersionName(this.versionNameTxt.getText());
    qnaireRespVersion.setDescription(this.remarkTextField.getText().getText());
    qnaireRespVersion.setReviewedDate(DateFormat.formatDateToString(this.reviewDate, DateFormat.DATE_FORMAT_15));
    qnaireRespVersion.setReviewedUser(this.ntUsername);
    qnaireRespVersion.setQnaireRespId(this.dataHandler.getQuesResponse().getId());
    qnaireRespVersion.setQnaireVersionId(this.dataHandler.getQnaireDefHandler().getQnaireVersion().getId());
    qnaireRespVersion.setQnaireRespVersStatus(this.dataHandler.findQnaireRespStatus().getDbType());

    try {
      client.create(qnaireRespVersion);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    super.okPressed();
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

  private void loadDetailsForShowVersion() {
    try {
      RvwQnaireRespVersion rvwQnrRespVersion = this.dataHandler.getQnaireRespModel().getRvwQnrRespVersion();
      String remarks =
          CommonUtils.isNotNull(rvwQnrRespVersion.getDescription()) ? rvwQnrRespVersion.getDescription() : "";

      this.remarkTextField.getText().setText(remarks);
      this.remarkTextField.getText().setToolTipText(remarks);

      this.versionNameTxt.setText(rvwQnrRespVersion.getVersionName());

      User apicUserByUsername = new UserServiceClient().getApicUserByUsername(rvwQnrRespVersion.getReviewedUser());
      this.rvwByTextField.setText(apicUserByUsername.getDescription());

      SimpleDateFormat df12 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_10,
          Locale.getDefault(Locale.Category.FORMAT));

      SimpleDateFormat df15 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_15,
          Locale.getDefault(Locale.Category.FORMAT));

      Date date = df15.parse(rvwQnrRespVersion.getReviewedDate());

      this.rvwonTextField.setText(df12.format(date));

      // Diabling the fields
      this.remarkTextField.setEnabled(false);
      this.remarkTextField.setEditable(false);
      this.versionNameTxt.setEditable(false);
      this.rvwByTextField.setEditable(false);
      this.rvwonTextField.setEditable(false);
      this.rvwdByBrowseBtn.setEnabled(false);
      this.rvwTimeSelBtn.setEnabled(false);
    }
    catch (ApicWebServiceException | ParseException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }
}
