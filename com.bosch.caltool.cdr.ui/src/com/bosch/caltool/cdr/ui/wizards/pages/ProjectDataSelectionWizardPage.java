/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;


import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.listeners.ProjectDataSelectionWizardPageListener;
import com.bosch.caltool.cdr.ui.wizard.page.validator.ProjectDataSelectionWizardPageValidator;
import com.bosch.caltool.cdr.ui.wizard.pages.resolver.ProjectDataSelectionPageResolver;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.providers.ComboViewerContentPropsalProvider;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.review.UserData;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author bru2cob
 */
public class ProjectDataSelectionWizardPage extends WizardPage

{

  /**
   * * CONSTANT FOR "null,null"
   */
  public static final String STR_NULL_NULL = "null,null";

  /**
   *
   */
  private static final String SELECT_THE_PARENT_REVIEW_DATA = "Select the Parent Review Data *";

  /**
   * Constant for mandatory msg
   */
  private static final String MSG_MANDATORY = "This field is mandatory.";

  /**
   * Text to display when user tries to modify the non-editable field.
   */
  private static final String EDIT_WARNING_TEXT = "Please use the browse button to modify the text";

  /**
   *
   */
  private static final int LIST_ROW_COUNT = 7;

  /**
   * shell title
   */
  private static final String SELC_USER = "Select User";

  /**
   * Title for the page
   */
  private static final String PAGE_TITLE = "Select Project Data";

  /**
   * Description for the page
   */
  private static final String PAGE_DESCRIPTION = "Please enter project data and review participants." + "\n" +
      "Values can be selected from the list of valid values when you click the selection buttons.";
  /**
   * Instance of variant name textbox
   */
  private Text variantName;

  /**
   * Instance of project name textbox
   */
  private Text projectName;
  /**
   * Instance of calibrationEngineer textbox
   */
  private Text calEngineer;
  /**
   * Instance of auditor name textbox
   */
  private Text auditor;
  /**
   * Variants selection browse button
   */
  private Button variantBrowse;
  /**
   * Decorator for varaint name mandatory field
   */
  private ControlDecoration txtVariantNameDec;
  /**
   * Decorator for auditor name mandatory field
   */
  private ControlDecoration txtAuditorNameDec;
  /**
   * Sorted set of selected participants
   */
  private final SortedSet<User> selParticipants = new TreeSet<>();
  /**
   * Instance of calData model
   */
  private CalDataReviewWizard calDataReviewWizard;

  /**
   * Array of selected other participants to be removed
   */
  private String[] selOthParts;

  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  /**
   * List of other participants
   */
  private List participantsList;


  /**
   * browse button image
   */
  private Image browseButtonImage;

  /**
   * Participants delete button
   */
  private Button partDelButton;

  private Button addPartsBtn;

  private boolean isDefaultUsrSel = true;

  /**
   * Text for description
   */
  private Text descriptions;


  /**
   * Decorator for description mandatory field
   */
  private ControlDecoration txtDescription;

  /**
   * Test Review Radio Button Icdm-874
   */

  private Button testRevRadio;

  /**
   * A2l Combo width
   */
  private static final int A2L_COMBO_WIDTH = 25;

  /**
   * (sdomPver.getPVERName() +file.getLabel())
   */
  private static final int A2L_COMBO_STR_ARR_LEN = 2;


  private Button offRevRadio;

  private ComboViewer a2lNameComboViewer;

  private Button pidcBrowse;

  private Button offRevCheckBox;

  private Button startRevCheckBox;

  private Button offLockedRevCheckBox;

  private Button startLockedRevCheckBox;

  private Button auditorBrowse;

  private Button calibrationBrowse;
  /**
   * radio button for start review type
   */
  private Button startRevRadio;

  private final CDRHandler cdrHandler = new CDRHandler();

  private final ProjectDataSelectionPageResolver projectDataSelectionPageResolver =
      new ProjectDataSelectionPageResolver();


  private ProjectDataSelectionWizardPageValidator projectDataSelectionWizardPageValidator;

  private ProjectDataSelectionWizardPageListener pageListener;

  /**
   * Constructor
   *
   * @param pageName title
   */
  public ProjectDataSelectionWizardPage(final String pageName) {
    super(pageName);
    setTitle(PAGE_TITLE);
    setDescription(PAGE_DESCRIPTION);
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG1_67X57));
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
    // create layout for composite
    createGridLayout(workArea);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    this.calDataReviewWizard = (CalDataReviewWizard) ProjectDataSelectionWizardPage.this.getWizard();


    this.browseButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);

    // Icdm -874 new group for Review Type radio
    createReviewTypeGrp(workArea);

    createDescriptionControl(workArea);
    // create project control
    createPrjControl(workArea);

    // create variant control
    createVariantControl(workArea);

    // Icdm-1286- Parent result can be null if the Review is started from home page.
    if (this.calDataReviewWizard.isDeltaReview()) {
      // create control
      createA2lSelControl(workArea);
      if (this.calDataReviewWizard.isProjectDataDeltaReview()) {
        createParentReviewTypeSelControl(workArea);
      }
    }


    // create calibration eng control
    createCalibrationEngControl(workArea);


    // create auditor control
    createAuditorControl(workArea);

    // create participants control
    createParticipantsControl(workArea);
    this.projectDataSelectionWizardPageValidator = new ProjectDataSelectionWizardPageValidator(this.calDataReviewWizard,
        this.calDataReviewWizard.getProjectSelWizPage());
    this.pageListener = new ProjectDataSelectionWizardPageListener(this.calDataReviewWizard,
        this.projectDataSelectionWizardPageValidator);
    this.pageListener.createActionListeners();
    scrollComp.setContent(workArea);
    scrollComp.setMinSize(workArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);
  }

  /**
   * @param workArea
   */
  private void createParentReviewTypeSelControl(final Composite workArea) {

    final Group reviewTypeGrp = new Group(workArea, SWT.NONE);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    reviewTypeGrp.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.verticalSpan = 1;
    gridData.horizontalSpan = 2;
    gridData.grabExcessHorizontalSpace = false;
    gridData.grabExcessVerticalSpace = false;
    reviewTypeGrp.setLayoutData(gridData);
    reviewTypeGrp.setText(SELECT_THE_PARENT_REVIEW_DATA);

    offParentReview(reviewTypeGrp);
    offLockedParentReview(reviewTypeGrp);
    startParentReview(reviewTypeGrp);
    startLockedParentReview(reviewTypeGrp);
    getNewLabel(workArea, SWT.NONE);


  }


  /**
   * @param parent lable
   * @param style style
   * @return the label of given style
   */
  private Label getNewLabel(final Composite parent, final int style) {
    return new Label(parent, style);
  }


  /**
   * @param reviewTypeGrp
   */
  private void startLockedParentReview(final Group reviewTypeGrp) {
    this.startLockedRevCheckBox = new Button(reviewTypeGrp, SWT.CHECK);
    this.startLockedRevCheckBox.setEnabled(false);
    this.startLockedRevCheckBox.setText("Only locked results      ");
    this.startLockedRevCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (ProjectDataSelectionWizardPage.this.startLockedRevCheckBox.getSelection()) {
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setOnlyLockedStartResults(true);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.setContentChanged(true);
        }
        else {
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel()
              .setOnlyLockedStartResults(false);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.setContentChanged(false);
        }
      }
    });
  }


  /**
   * @param reviewTypeGrp
   */
  private void startParentReview(final Group reviewTypeGrp) {
    this.startRevCheckBox = new Button(reviewTypeGrp, SWT.CHECK);
    this.startRevCheckBox.setText("Start Review       ");

    this.startRevCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (ProjectDataSelectionWizardPage.this.startRevCheckBox.getSelection()) {

          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setStartReviewType(true);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.setContentChanged(true);
          ProjectDataSelectionWizardPage.this.startLockedRevCheckBox.setEnabled(true);
          reviewTypeGrp.setText("Select the Parent Review Data ");
        }
        else {
          if (!ProjectDataSelectionWizardPage.this.offRevCheckBox.getSelection()) {
            reviewTypeGrp.setText(SELECT_THE_PARENT_REVIEW_DATA);
          }
          ProjectDataSelectionWizardPage.this.startLockedRevCheckBox.setEnabled(false);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setStartReviewType(false);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel()
              .setOnlyLockedStartResults(false);
        }
        ProjectDataSelectionWizardPage.this.projectDataSelectionWizardPageValidator.checkNextBtnEnable();
      }
    });
  }


  /**
   * @param reviewTypeGrp
   */
  private void offLockedParentReview(final Group reviewTypeGrp) {
    this.offLockedRevCheckBox = new Button(reviewTypeGrp, SWT.CHECK);
    this.offLockedRevCheckBox.setEnabled(false);
    this.offLockedRevCheckBox.setText("Only locked results      ");
    this.offLockedRevCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (ProjectDataSelectionWizardPage.this.offLockedRevCheckBox.getSelection()) {
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setOnlyLockedOffReview(true);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.setContentChanged(true);
        }
        else {
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setOnlyLockedOffReview(false);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.setContentChanged(false);
        }
      }
    });
  }


  /**
   * @param reviewTypeGrp
   */
  private void offParentReview(final Group reviewTypeGrp) {
    this.offRevCheckBox = new Button(reviewTypeGrp, SWT.CHECK);
    this.offRevCheckBox.setText("Official Review       ");
    this.offRevCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {


        if (ProjectDataSelectionWizardPage.this.offRevCheckBox.getSelection()) {
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setOffReviewType(true);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.setContentChanged(true);
          ProjectDataSelectionWizardPage.this.offLockedRevCheckBox.setEnabled(true);
          reviewTypeGrp.setText("Select the Parent Review Data ");
        }
        else {
          if (!ProjectDataSelectionWizardPage.this.startRevCheckBox.getSelection()) {
            reviewTypeGrp.setText(SELECT_THE_PARENT_REVIEW_DATA);
          }
          ProjectDataSelectionWizardPage.this.offLockedRevCheckBox.setEnabled(false);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setOffReviewType(false);
          ProjectDataSelectionWizardPage.this.calDataReviewWizard.getCdrWizardUIModel().setOnlyLockedOffReview(false);
        }
        ProjectDataSelectionWizardPage.this.projectDataSelectionWizardPageValidator.checkNextBtnEnable();
      }
    });
  }

  /**
   * enable filtering A2lFile name when A2lFile name is typed on the combo
   */
  public void setA2lComboContentProposal() {
    IContentProposalProvider provider =
        new ComboViewerContentPropsalProvider(this.a2lNameComboViewer.getCombo().getItems());
    ContentProposalAdapter adapter =
        new ContentProposalAdapter(this.a2lNameComboViewer.getCombo(), new ComboContentAdapter(), provider, null, null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.setProposalPopupFocus();
  }

  /**
   * @param workArea
   */
  private void createA2lSelControl(final Composite workArea) {
    new Label(workArea, SWT.NONE).setText("Select the A2l File");
    this.a2lNameComboViewer = new ComboViewer(workArea, SWT.DROP_DOWN);
    this.a2lNameComboViewer.getCombo().setLayoutData(GridDataUtil.getInstance().getGridData());

    final GridData descData = new GridData(SWT.FILL, SWT.NONE, true, false);
    descData.widthHint =
        new PixelConverter(this.a2lNameComboViewer.getCombo()).convertWidthInCharsToPixels(A2L_COMBO_WIDTH);
    this.a2lNameComboViewer.getCombo().setLayoutData(descData);
    getNewLabel(workArea, SWT.NONE);
  }


  /**
   * @param workArea
   * @throws ApicWebServiceException
   */

  private void createReviewTypeGrp(final Composite workArea) {
    final Group reviewTypeGrp = new Group(workArea, SWT.NONE);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    reviewTypeGrp.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.verticalSpan = 1;
    gridData.horizontalSpan = 2;
    gridData.grabExcessHorizontalSpace = false;
    gridData.grabExcessVerticalSpace = false;
    reviewTypeGrp.setLayoutData(gridData);
    reviewTypeGrp.setText("Select the Review Type");

    getNewLabel(reviewTypeGrp, SWT.NONE);
    getNewLabel(reviewTypeGrp, SWT.NONE);
    getNewLabel(reviewTypeGrp, SWT.NONE);

    String startRevMsg = "";
    String offRevMsg = "";
    String testRevMsg = "";
    try {
      startRevMsg = new CommonDataBO().getMessage(CDRConstants.REVIEW_TYPE_GROUP_NAME, "TOOLTIP_START_RVW");
      offRevMsg = new CommonDataBO().getMessage(CDRConstants.REVIEW_TYPE_GROUP_NAME, "TOOLTIP_OFFICIAL_RVW");
      testRevMsg = new CommonDataBO().getMessage(CDRConstants.REVIEW_TYPE_GROUP_NAME, "TOOLTIP_TEST_RVW");
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    // ICDM-1801
    this.startRevRadio = new Button(reviewTypeGrp, SWT.RADIO);
    this.startRevRadio.setText("Start Review       ");

    this.startRevRadio.setToolTipText(startRevMsg);

    this.offRevRadio = new Button(reviewTypeGrp, SWT.RADIO);
    this.offRevRadio.setText("Official Review      ");
    this.offRevRadio.setToolTipText(offRevMsg);

    this.testRevRadio = new Button(reviewTypeGrp, SWT.RADIO);
    this.testRevRadio.setText("Test Review       ");
    this.testRevRadio.setToolTipText(testRevMsg);
    this.testRevRadio.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
    getNewLabel(workArea, SWT.NONE);

  }


  /**
   * ICDM 658 This method creates the description control
   *
   * @param workArea
   */
  private void createDescriptionControl(final Composite workArea) {
    new Label(workArea, SWT.NONE).setText("Description : ");
    this.descriptions = new Text(workArea, SWT.BORDER);
    this.descriptions.setEditable(true);

    this.txtDescription = new ControlDecoration(this.descriptions, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtDescription, MSG_MANDATORY);

    final GridData descData = new GridData(SWT.FILL, SWT.NONE, true, false);
    descData.widthHint = new PixelConverter(this.descriptions).convertWidthInCharsToPixels(25);
    this.descriptions.setLayoutData(descData);
    getNewLabel(workArea, SWT.NONE);
  }


  /**
   * @param workArea
   */
  private void createGridLayout(final Composite workArea) {
    final GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.makeColumnsEqualWidth = false;
    layout.marginWidth = 0;
    layout.verticalSpacing = 20;

    workArea.setLayout(layout);

    workArea.setLayoutData(GridDataUtil.getInstance().getGridData());

  }


  /**
   * @param workArea parent Composite
   */
  private void createParticipantsControl(final Composite workArea) {
    final Label participants = new Label(workArea, SWT.NONE);
    participants.setText("Other Participants : ");
    participants.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    this.participantsList = new List(workArea, SWT.V_SCROLL | SWT.MULTI);
    final GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.heightHint = LIST_ROW_COUNT * this.participantsList.getItemHeight();
    this.participantsList.setLayoutData(data);

    // participantBtComposite for aligining the participants selection and delete buttons
    final Composite participantBtComp = new Composite(workArea, SWT.NONE);
    final GridLayout layoutPartComp = new GridLayout();
    layoutPartComp.numColumns = 1;
    layoutPartComp.makeColumnsEqualWidth = false;
    layoutPartComp.marginWidth = 0;
    layoutPartComp.marginTop = 0;
    participantBtComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    participantBtComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    participantBtComp.setLayout(layoutPartComp);
    // add and delete participants button
    createParticipantsButtons(participantBtComp);

  }


  /**
   * Data for cancelled Review
   */
  public void setDataForCancelPressed() {

    this.projectDataSelectionPageResolver.setInput(this.calDataReviewWizard);
  }


  /**
   * @param participantsList
   * @param participantBtComp child composite
   */
  private void createParticipantsButtons(final Composite participantBtComp) {


    addButton(participantBtComp);

    delButton(participantBtComp);
  }


  /**
   * @param participantBtComp
   */
  private void delButton(final Composite participantBtComp) {
    final Image deleteButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETE_16X16);
    this.partDelButton = new Button(participantBtComp, SWT.PUSH);
    this.partDelButton.setImage(deleteButtonImage);
    this.partDelButton.setEnabled(false);

  }


  /**
   * @param participantBtComp
   * @param addButtonImage
   */
  private void addButton(final Composite participantBtComp) {
    final Image addButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.ADD_16X16);
    this.addPartsBtn = new Button(participantBtComp, SWT.PUSH);
    this.addPartsBtn.setImage(addButtonImage);

  }


  /**
   * @param workArea parent composite
   */
  private void createAuditorControl(final Composite workArea) {
    new Label(workArea, SWT.NONE).setText("Auditor : ");
    this.auditor = new Text(workArea, SWT.BORDER | SWT.READ_ONLY);
    // ICDM-1534
    this.auditor.addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyEvent) {
        if (!(((keyEvent.stateMask & SWT.CTRL) == SWT.CTRL) && (keyEvent.keyCode == 262144))) {
          MessageDialogUtils.getWarningMessageDialog(SELC_USER, EDIT_WARNING_TEXT);
        }

      }

      @Override
      public void keyPressed(final KeyEvent keyEvent) {
        // Implementation in KeyReleased() method is sufficient
      }
    });

    this.txtAuditorNameDec = new ControlDecoration(this.auditor, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtAuditorNameDec, MSG_MANDATORY);
    final GridData auditorData = new GridData(SWT.FILL, SWT.NONE, true, false);
    auditorData.widthHint = new PixelConverter(this.auditor).convertWidthInCharsToPixels(25);
    this.auditor.setLayoutData(auditorData);
    this.auditor.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    this.auditor.setEditable(false);
    this.auditorBrowse = new Button(workArea, SWT.PUSH);
    this.auditorBrowse.setImage(this.browseButtonImage);
    this.auditorBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    this.auditorBrowse.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
  }


  /**
   * @param workArea parent composite
   * @throws ApicWebServiceException
   */
  private void createCalibrationEngControl(final Composite workArea) {
    new Label(workArea, SWT.NONE).setText("Calibration Engineer : ");
    this.calEngineer = new Text(workArea, SWT.BORDER);
    this.calEngineer.setEditable(false);
    try {
      this.calEngineer.setText(ProjectDataSelectionWizardPage.this.cdrHandler.getCurrentApicUser().getDescription());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    final GridData calibrationData = new GridData(SWT.FILL, SWT.NONE, true, false);
    calibrationData.widthHint = new PixelConverter(this.calEngineer).convertWidthInCharsToPixels(25);
    this.calEngineer.setLayoutData(calibrationData);
    this.calEngineer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    this.calibrationBrowse = new Button(workArea, SWT.PUSH);
    this.calibrationBrowse.setImage(this.browseButtonImage);
    this.calibrationBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));


    // Story 221806
    // added because at times when default user is left unchanged in the calbeng feild it is not set in the object
    if (this.isDefaultUsrSel && (this.calDataReviewWizard.getCdrWizardUIModel().getCalEngUserName() == null))

    {
      try {
        UserData userData = new UserData();
        userData.setSelCalEngineerId(ProjectDataSelectionWizardPage.this.cdrHandler.getCurrentApicUser().getId());
        this.calDataReviewWizard.getCdrWizardUIModel()
            .setCalEngUserName(ProjectDataSelectionWizardPage.this.cdrHandler.getCurrentApicUser().getName());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * @param workArea parent composite
   */
  private void createVariantControl(final Composite workArea) {
    new Label(workArea, SWT.NONE).setText("Variant : ");
    this.variantName = new Text(workArea, SWT.BORDER);
    this.variantName.setEditable(false);
    // ICDM-1534
    this.variantName.addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyEvent) {
        if (!(((keyEvent.stateMask & SWT.CTRL) == SWT.CTRL) && (keyEvent.keyCode == 262144))) {
          MessageDialogUtils.getWarningMessageDialog("Select variant", EDIT_WARNING_TEXT);
        }

      }

      @Override
      public void keyPressed(final KeyEvent keyEvent) {
        // Implementation in KeyReleased() method is sufficient
      }
    });

    final GridData variantData = new GridData(SWT.FILL, SWT.NONE, true, false);
    variantData.widthHint = new PixelConverter(this.variantName).convertWidthInCharsToPixels(25);
    this.variantName.setLayoutData(variantData);

    this.variantBrowse = new Button(workArea, SWT.PUSH);
    this.variantBrowse.setImage(this.browseButtonImage);
    this.variantBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));


  }


  /**
   * This method sets decorator for variant field to make it mandatory based on projectID card
   *
   * @param setDecorator to set the field mandatory
   */
  public void setVariantDecorator(final boolean setDecorator) {
    if (this.txtVariantNameDec == null) {
      this.txtVariantNameDec = new ControlDecoration(this.variantName, SWT.LEFT | SWT.TOP);
    }
    // set decorator for variant field if the project card has variants
    if (setDecorator && ("".equalsIgnoreCase(this.variantName.getText()))) {
      this.variantBrowse.setEnabled(true);
      this.variantName.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
      this.decorators.showReqdDecoration(this.txtVariantNameDec, MSG_MANDATORY);
    }
    else {
      if (!setDecorator) {
        this.txtVariantNameDec.hide();
        this.variantBrowse.setEnabled(false);
      }

    }
  }


  /**
   * Create project control
   */
  private void createPrjControl(final Composite workArea) {
    new Label(workArea, SWT.NONE).setText("Project : ");
    this.projectName = new Text(workArea, SWT.BORDER);
    this.projectName.setEditable(false);
    final GridData projectData = new GridData(SWT.FILL, SWT.NONE, true, false);
    projectData.widthHint = new PixelConverter(this.projectName).convertWidthInCharsToPixels(25);
    this.projectName.setLayoutData(projectData);
    if (this.calDataReviewWizard.isDeltaReview()) {
      createPidcBrowseButton(workArea);
    }
    else {
      getNewLabel(workArea, SWT.NONE);
    }
  }

  /**
   * @param workArea
   */
  private void createPidcBrowseButton(final Composite workArea) {
    this.pidcBrowse = new Button(workArea, SWT.PUSH);
    this.pidcBrowse.setImage(this.browseButtonImage);
    this.pidcBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

  }


  @Override
  public boolean canFlipToNextPage() {
    boolean canProceed = false;
    if (this.projectDataSelectionWizardPageValidator.validateFields() &&
        !this.calDataReviewWizard.getCdrWizardUIModel().isExceptioninWizard()) {
      canProceed = true;
    }
    return canProceed;
  }


  /**
   * Text box is cleared on backPressed
   */
  public void backPressed() {
    this.projectDataSelectionPageResolver.processBackPressed();
    clearFields();
    this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);
  }


  /**
   * @param projectName the projectName to set
   */
  public void setProjectName(final String projectName) {
    this.projectName.setText(projectName);
  }

  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final String variantName) {
    this.variantName.setText(variantName);
  }


  /**
   * @param calibrationEng the calibrationEng to set
   */
  public void setCalibrationEng(final String calibrationEng) {
    this.calEngineer.setText(calibrationEng);
  }

  /**
   * @param auditorName the auditorName to set
   */
  public void setAuditor(final String auditorName) {
    this.auditor.setText(auditorName);
  }

  /**
   * ICDM 658
   *
   * @param desc Description to set
   */
  public void setDesc(final String desc) {
    this.descriptions.setText(desc);
  }

  /**
   * @param participants the participants to set
   */
  public void setOtherParticipants(final String[] participants) {
    this.participantsList.setItems(participants);
  }

  /**
   * Method to invoke pages when next button is pressed
   */
  public void nextPressed() {
    // Calling setinput method in resolver
    ProjectDataSelectionWizardPage.this.projectDataSelectionPageResolver.setInput(this.calDataReviewWizard);
    ProjectDataSelectionWizardPage.this.projectDataSelectionPageResolver.processNextPressed();
    if (this.calDataReviewWizard.getProjectSelWizPage().isPageComplete()) {
      if (this.calDataReviewWizard.isDeltaReview() && !this.calDataReviewWizard.isProjectDataDeltaReview()) {
        this.calDataReviewWizard.getWpSelWizPage().getWorkpackageSelectionPageResolver()
            .fillUIData(this.calDataReviewWizard);
      }
      this.calDataReviewWizard.getWpSelWizPage().getWorkPackageSelectionWizardPageValidator().checkNextBtnEnable();
    }

  }


  /**
   * Clear the fields when different a2l file is selected
   */
  public void clearFields() {
    if (!this.variantName.getText().isEmpty()) {
      this.variantName.setText("");
    }
    if (!this.auditor.getText().isEmpty()) {
      this.auditor.setText("");
    }
    if (this.participantsList.getItemCount() != 0) {
      this.participantsList.removeAll();
    }
    if (!this.descriptions.getText().isEmpty()) {
      this.descriptions.setText("");
    }
  }


  /**
   * @return the selParticipants
   */
  public SortedSet<User> getSelParticipants() {
    return this.selParticipants;
  }


  /**
   * @return the startRevRadio
   */
  public Button getStartRevRadio() {
    return this.startRevRadio;
  }


  /**
   * @return the comboA2lName
   */
  public Combo getA2lNameCombo() {
    return this.a2lNameComboViewer.getCombo();
  }


  /**
   * @return the a2lNameComboViewer
   */
  public ComboViewer getA2lNameComboViewer() {
    return this.a2lNameComboViewer;
  }


  /**
   * @param a2lNameComboViewer the a2lNameComboViewer to set
   */
  public void setA2lNameComboViewer(final ComboViewer a2lNameComboViewer) {
    this.a2lNameComboViewer = a2lNameComboViewer;
  }


  /**
   * @return the offRevRadio
   */
  public Button getOffRevRadio() {
    return this.offRevRadio;
  }

  /**
   * @return the testRevRadio
   */
  public Button getTestRevRadio() {
    return this.testRevRadio;
  }


  /**
   * @return the variantName
   */
  public Text getVariantName() {
    return this.variantName;
  }


  /**
   * @return the variantBrowse
   */
  public Button getVariantBrowse() {
    return this.variantBrowse;
  }


  /**
   * @return the projectName
   */
  public Text getProjectName() {
    return this.projectName;
  }


  /**
   * @param projectName the projectName to set
   */
  public void setProjectName(final Text projectName) {
    this.projectName = projectName;
  }


  /**
   * @return the txtVariantNameDec
   */
  public ControlDecoration getTxtVariantNameDec() {
    return this.txtVariantNameDec;
  }


  /**
   * @param txtVariantNameDec the txtVariantNameDec to set
   */
  public void setTxtVariantNameDec(final ControlDecoration txtVariantNameDec) {
    this.txtVariantNameDec = txtVariantNameDec;
  }


  /**
   * @return the txtAuditorNameDec
   */
  public ControlDecoration getTxtAuditorNameDec() {
    return this.txtAuditorNameDec;
  }


  /**
   * @param txtAuditorNameDec the txtAuditorNameDec to set
   */
  public void setTxtAuditorNameDec(final ControlDecoration txtAuditorNameDec) {
    this.txtAuditorNameDec = txtAuditorNameDec;
  }


  /**
   * @return the txtDescription
   */
  public ControlDecoration getTxtDescription() {
    return this.txtDescription;
  }


  /**
   * @return the calEngineer
   */
  public Text getCalEngineer() {
    return this.calEngineer;
  }


  /**
   * @param calEngineer the calEngineer to set
   */
  public void setCalEngineer(final Text calEngineer) {
    this.calEngineer = calEngineer;
  }


  /**
   * @return the auditor
   */
  public Text getAuditor() {
    return this.auditor;
  }


  /**
   * @param auditor the auditor to set
   */
  public void setAuditor(final Text auditor) {
    this.auditor = auditor;
  }


  /**
   * @return the participantsList
   */
  public List getParticipantsList() {
    return this.participantsList;
  }


  /**
   * @param participantsList the participantsList to set
   */
  public void setParticipantsList(final List participantsList) {
    this.participantsList = participantsList;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final Text description) {
    this.descriptions = description;
  }


  /**
   * @return the descriptions
   */
  public Text getDescriptions() {
    return this.descriptions;
  }


  /**
   * @param descriptions the descriptions to set
   */
  public void setDescriptions(final Text descriptions) {
    this.descriptions = descriptions;
  }


  /**
   * @param txtDescription the txtDescription to set
   */
  public void setTxtDescription(final ControlDecoration txtDescription) {
    this.txtDescription = txtDescription;
  }


  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final Text variantName) {
    this.variantName = variantName;
  }


  /**
   * @param variantBrowse the variantBrowse to set
   */
  public void setVariantBrowse(final Button variantBrowse) {
    this.variantBrowse = variantBrowse;
  }


  /**
   * @return the calDtReviewWizard
   */
  public CalDataReviewWizard getCalDtReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @param calDtReviewWizard the calDtReviewWizard to set
   */
  public void setCalDtReviewWizard(final CalDataReviewWizard calDtReviewWizard) {
    this.calDataReviewWizard = calDtReviewWizard;
  }


  /**
   * @return the selOthParts
   */
  public String[] getSelOthParts() {
    return this.selOthParts;
  }


  /**
   * @param selOthParts the selOthParts to set
   */
  public void setSelOthParts(final String[] selOthParts) {
    this.selOthParts = selOthParts;
  }


  /**
   * @return the browseButtonImage
   */
  public Image getBrowseButtonImage() {
    return this.browseButtonImage;
  }


  /**
   * @param browseButtonImage the browseButtonImage to set
   */
  public void setBrowseButtonImage(final Image browseButtonImage) {
    this.browseButtonImage = browseButtonImage;
  }


  /**
   * @return the partDelButton
   */
  public Button getPartDelButton() {
    return this.partDelButton;
  }


  /**
   * @param partDelButton the partDelButton to set
   */
  public void setPartDelButton(final Button partDelButton) {
    this.partDelButton = partDelButton;
  }


  /**
   * @return the isDefaultUsrSel
   */
  public boolean isDefaultUsrSel() {
    return this.isDefaultUsrSel;
  }


  /**
   * @param isDefaultUsrSel the isDefaultUsrSel to set
   */
  public void setDefaultUsrSel(final boolean isDefaultUsrSel) {
    this.isDefaultUsrSel = isDefaultUsrSel;
  }


  /**
   * @return the pidcBrowse
   */
  public Button getPidcBrowse() {
    return this.pidcBrowse;
  }


  /**
   * @param pidcBrowse the pidcBrowse to set
   */
  public void setPidcBrowse(final Button pidcBrowse) {
    this.pidcBrowse = pidcBrowse;
  }


  /**
   * @return the offRevCheckBox
   */
  public Button getOffRevCheckBox() {
    return this.offRevCheckBox;
  }


  /**
   * @param offRevCheckBox the offRevCheckBox to set
   */
  public void setOffRevCheckBox(final Button offRevCheckBox) {
    this.offRevCheckBox = offRevCheckBox;
  }


  /**
   * @return the startRevCheckBox
   */
  public Button getStartRevCheckBox() {
    return this.startRevCheckBox;
  }


  /**
   * @param startRevCheckBox the startRevCheckBox to set
   */
  public void setStartRevCheckBox(final Button startRevCheckBox) {
    this.startRevCheckBox = startRevCheckBox;
  }


  /**
   * @return the offLockedRevCheckBox
   */
  public Button getOffLockedRevCheckBox() {
    return this.offLockedRevCheckBox;
  }


  /**
   * @param offLockedRevCheckBox the offLockedRevCheckBox to set
   */
  public void setOffLockedRevCheckBox(final Button offLockedRevCheckBox) {
    this.offLockedRevCheckBox = offLockedRevCheckBox;
  }


  /**
   * @return the startLockedRevCheckBox
   */
  public Button getStartLockedRevCheckBox() {
    return this.startLockedRevCheckBox;
  }


  /**
   * @param startLockedRevCheckBox the startLockedRevCheckBox to set
   */
  public void setStartLockedRevCheckBox(final Button startLockedRevCheckBox) {
    this.startLockedRevCheckBox = startLockedRevCheckBox;
  }


  /**
   * @return the strNullNull
   */
  public static String getStrNullNull() {
    return STR_NULL_NULL;
  }


  /**
   * @return the selectTheParentReviewData
   */
  public static String getSelectTheParentReviewData() {
    return SELECT_THE_PARENT_REVIEW_DATA;
  }


  /**
   * @return the msgMandatory
   */
  public static String getMsgMandatory() {
    return MSG_MANDATORY;
  }


  /**
   * @return the editWarningText
   */
  public static String getEditWarningText() {
    return EDIT_WARNING_TEXT;
  }


  /**
   * @return the listRowCount
   */
  public static int getListRowCount() {
    return LIST_ROW_COUNT;
  }


  /**
   * @return the selcUser
   */
  public static String getSelcUser() {
    return SELC_USER;
  }


  /**
   * @return the pageTitle
   */
  public static String getPageTitle() {
    return PAGE_TITLE;
  }


  /**
   * @return the pageDescription
   */
  public static String getPageDescription() {
    return PAGE_DESCRIPTION;
  }


  /**
   * @return the decorators
   */
  public Decorators getDecorators() {
    return this.decorators;
  }


  /**
   * @return the a2lComboWidth
   */
  public static int getA2lComboWidth() {
    return A2L_COMBO_WIDTH;
  }


  /**
   * @return the a2lComboStrArrLen
   */
  public static int getA2lComboStrArrLen() {
    return A2L_COMBO_STR_ARR_LEN;
  }


  /**
   * @return the cdrHandler
   */
  public CDRHandler getCdrHandler() {
    return this.cdrHandler;
  }


  /**
   * @return the projectDataSelectionPageResolver
   */
  public ProjectDataSelectionPageResolver getProjectDataSelectionPageResolver() {
    return this.projectDataSelectionPageResolver;
  }


  /**
   * @param testRevRadio the testRevRadio to set
   */
  public void setTestRevRadio(final Button testRevRadio) {
    this.testRevRadio = testRevRadio;
  }


  /**
   * @param offRevRadio the offRevRadio to set
   */
  public void setOffRevRadio(final Button offRevRadio) {
    this.offRevRadio = offRevRadio;
  }

  /**
   * @param startRevRadio the startRevRadio to set
   */
  public void setStartRevRadio(final Button startRevRadio) {
    this.startRevRadio = startRevRadio;
  }


  /**
   * @return the addPartsBtn
   */
  public Button getAddPartsBtn() {
    return this.addPartsBtn;
  }


  /**
   * @param addPartsBtn the addPartsBtn to set
   */
  public void setAddPartsBtn(final Button addPartsBtn) {
    this.addPartsBtn = addPartsBtn;
  }


  /**
   * @return the auditorBrowse
   */
  public Button getAuditorBrowse() {
    return this.auditorBrowse;
  }


  /**
   * @param auditorBrowse the auditorBrowse to set
   */
  public void setAuditorBrowse(final Button auditorBrowse) {
    this.auditorBrowse = auditorBrowse;
  }


  /**
   * @return the calibrationBrowse
   */
  public Button getCalibrationBrowse() {
    return this.calibrationBrowse;
  }


  /**
   * @param calibrationBrowse the calibrationBrowse to set
   */
  public void setCalibrationBrowse(final Button calibrationBrowse) {
    this.calibrationBrowse = calibrationBrowse;
  }


  /**
   * @return the projectDataSelectionWizardPageValidator
   */
  public ProjectDataSelectionWizardPageValidator getProjectDataSelectionWizardPageValidator() {
    return this.projectDataSelectionWizardPageValidator;
  }


  /**
   * @return the pageListener
   */
  public ProjectDataSelectionWizardPageListener getPageListener() {
    return this.pageListener;
  }

}
