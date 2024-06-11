/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.ReviewWrkPkgSelectionDialog;
import com.bosch.caltool.cdr.ui.listeners.WorkPackageSelectionWizardPageListener;
import com.bosch.caltool.cdr.ui.wizard.page.validator.WorkPackageSelectionWizardPageValidator;
import com.bosch.caltool.cdr.ui.wizard.pages.resolver.WorkpackageSelectionPageResolver;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * @author bru2cob
 */
public class WorkpackageSelectionWizardPage extends WizardPage {

  public static final int SING_WORK_PACK_SEL_SIZE = 1;
  /**
   *
   */
  private static final String NUMBER_OF_PARAMETERS_IS_TOO_HIGH = "Number of parameters to be reviewed is too high(> ";
  /**
   * Title for the page
   */
  private static final String PAGE_TITLE = "Select WorkPackage";
  /**
   * Description for the page
   */
  private static final String PAGE_DESCRIPTION = "Please select the workpackage to be reviewed." + "\n" +
      "List of functions are filled with all functions of the workpackage." + "\n" +
      "Functions can be either removed or added.";
  /**
   * constant for file names
   */
  private static final String[] fileNames =
      new String[] { "LAB files(*.LAB)", "FUN files(*.FUN)", "LAB/FUN files(*.LAB,*.FUN)" };

  /**
   * The comparator for the ignore case
   */
  // Task 233412
  private static final Comparator<String> IGNORE_CASE_COMPARATOR = ApicUtil::compare;

  /**
   * The wp function set
   */
  // Task 233412
  private final SortedSet<String> wpFuncsSet = new TreeSet<>(IGNORE_CASE_COMPARATOR);

  /**
   * The review funciton set
   */
  // Task 233412
  private final SortedSet<String> reviewFuncsSet = new TreeSet<>(IGNORE_CASE_COMPARATOR);
  /**
   * List of all functions in selected work package
   */
  private ListViewer wrkPkgFuncsList;

  /**
   * List of all functions to be reviewed
   */
  private ListViewer reviewFuncList;
  /**
   * Instance of wizard
   */
  private CalDataReviewWizard calDataReviewWizard;
  /**
   * Instance of working package text
   */
  private Text workingPkg;
  /**
   * Instance of selected work package
   */
  private WpRespModel selectedWrkPkg;
  /**
   * Array of selected functions in wrkPkgFuncsList
   */
  private String[] selectedWrkPkgFuncs;
  /**
   * Array of selected functions in reviewFuncList
   */
  private String[] selectedFuncsToReview;
  /**
   * set of wselected workpackage functions
   */
  private Set<String> functions;
  /**
   * add function button
   */
  private Button addButton;
  /**
   * remove function button
   */
  private Button removeButton;

  private Button workingPkgBrowse;

  private WorkpackageSelectionPageResolver workpackageSelectionPageResolver;

  private WorkPackageSelectionWizardPageValidator workPackageSelectionWizardPageValidator;

  private WorkPackageSelectionWizardPageListener workPackageSelectionWizardPageListener;

  private final CDRHandler cdrHandler = new CDRHandler();

  /**
   * constant for file extensions
   */
  private final String[] filExtensions = new String[] { "*.lab", "*.fun", "*.lab;*.fun" };
  /**
   * Get the eclipse preference store
   */
  private IPreferenceStore preference = PlatformUI.getPreferenceStore();

  private ReviewFilesSelectionWizardPage reviewFileSelWizPage;
  // Icdm-715 UI Changes to Wizard
  private Label wpLabel;
  private String fileName;
  private String funLabPath = "";
  private final SortedSet<String> unAvialFuncs = new TreeSet<>();
  private final SortedSet<String> unAvialParams = new TreeSet<>();
  private Button workPackageRadio;
  private Button paramRadio;
  private Button labFunRadio;
  private Button commonRuleRadio;
  private Button ruleSetRadio;
  private Button a2lRadio;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  private Text wpFilterTxt;
  private FunsFilter wpFilters;
  private Text reviewFunFilterTxt;
  private FunsFilter reviewFunFilters;
  private Button secondaryRulesButton;
  private Button compliRadio;
  private Button ssdRuleButton;
  private DropFileListener labFunFileDropFileListener;
  private Composite parentComp;
  /*
   * Radio Buttons to select Content of the review - OBD options
   */
  private Group rvwCntSelCtrlGrp;
  private Button onlyOBDRadio;
  private Button noOBDRadio;
  private Button bothOBDNonOBDRadio;


  /**
   * Constructor
   *
   * @param pageName title
   */
  public WorkpackageSelectionWizardPage(final String pageName) {
    super(pageName);
    setTitle(PAGE_TITLE);
    setDescription(PAGE_DESCRIPTION);
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG2_67X57));
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {

    // Create Parent Composite for WrkPackageSelWizardPage
    ScrolledComposite scrollComp = createParentComposite(parent);

    // Need to intialize before creating other controls
    this.calDataReviewWizard = (CalDataReviewWizard) WorkpackageSelectionWizardPage.this.getWizard();

    // Create the Radio Controls for rule type selection
    createRuleRadioControls();

    // Create the Radio Controls for Review Content Selection
    createRvwContSelRadioCtrls();

    // Create the Radio Controls for Review Param Selection
    createFuncParamRadioControls();

    // Composite to hold workpage functions
    final Composite funcComp = createFuncComposite(this.parentComp);

    // composite to hold wrkPkgfunctions list
    final Composite wrkPkgFuncComp = createWrkPkgFuncComp(funcComp);

    // composite to hold add and remove buttons
    final Composite buttonComp = createButtonComp(funcComp);

    // composite to hold reviewfunctions list
    final Composite reviewFuncComp = createReviewFuncComp(funcComp);

    createWrkPkgFuncsFilter(wrkPkgFuncComp);
    createWrkPkgFuncsList(wrkPkgFuncComp, "Available Functions :");
    createReviewFuncsFilter(reviewFuncComp);
    createReviewFuncsList(reviewFuncComp, "Functions to be Reviewed :");
    addFilters();
    createButtonBar(buttonComp);

    // Code need to be removed added for enabling next button
    WorkpackageSelectionWizardPage.this.setPageComplete(true);
    this.workPackageSelectionWizardPageValidator = new WorkPackageSelectionWizardPageValidator(this.calDataReviewWizard,
        this.calDataReviewWizard.getWpSelWizPage());
    this.workPackageSelectionWizardPageListener = new WorkPackageSelectionWizardPageListener(this.calDataReviewWizard,
        this.workPackageSelectionWizardPageValidator);
    this.workpackageSelectionPageResolver = new WorkpackageSelectionPageResolver(this.calDataReviewWizard);
    this.workPackageSelectionWizardPageListener.createActionListeners();
    scrollComp.setContent(this.parentComp);
    scrollComp.setMinSize(this.parentComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);
  }


  /**
   * @param parent
   * @return
   */
  private ScrolledComposite createParentComposite(final Composite parent) {
    initializeDialogUnits(parent);
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

    this.parentComp = new Composite(scrollComp, SWT.NULL);

    final GridLayout layout = new GridLayout();
    this.parentComp.setLayout(layout);
    this.parentComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    return scrollComp;
  }


  private void createRuleRadioControls() {

    final Group ruleSelGroup = new Group(this.parentComp, SWT.NONE);
    ruleSelGroup.setLayout(new GridLayout());
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    ruleSelGroup.setLayoutData(gridData);
    // Icdm-715 Source for the Lab fun file
    ruleSelGroup.setText("Select the source for fetching the rules");
    final Composite ruleCtrlsComp = createRadioRowComp(ruleSelGroup);

    this.commonRuleRadio = new Button(ruleCtrlsComp, SWT.RADIO);
    this.commonRuleRadio.setText("Common rules");
    this.commonRuleRadio.setSelection(true);

    this.ruleSetRadio = new Button(ruleCtrlsComp, SWT.RADIO);
    this.ruleSetRadio.setText("Rule Set Rules");
    this.ruleSetRadio.setSelection(false);
    new Label(ruleCtrlsComp, SWT.NONE);

    // Task 231283
    this.secondaryRulesButton = new Button(ruleCtrlsComp, SWT.CHECK);
    this.secondaryRulesButton.setText(" include RuleSet Rules");
    this.secondaryRulesButton.setLayoutData(GridDataUtil.getInstance().getGridData());

    createSSDRuleButton(ruleCtrlsComp);

    ruleCtrlsComp.layout();
    ruleCtrlsComp.pack();
  }

  /**
   * @param firstRowComposite
   * @param cdrReviewData
   */
  private void createSSDRuleButton(final Composite firstRowComposite) {

    this.ssdRuleButton = new Button(firstRowComposite, SWT.CHECK);
    this.ssdRuleButton.setText(" include SSD Rules");
    this.ssdRuleButton.setLayoutData(GridDataUtil.getInstance().getGridData());
  }


  /**
  *
  */
  private void createRvwContSelRadioCtrls() {
    this.rvwCntSelCtrlGrp = new Group(this.parentComp, SWT.NONE);
    this.rvwCntSelCtrlGrp.setLayout(new GridLayout());
    this.rvwCntSelCtrlGrp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    this.rvwCntSelCtrlGrp.setText("Select the content of the review");

    final Composite ruleCtrlsComp = createRadioRowComp(this.rvwCntSelCtrlGrp);

    this.noOBDRadio = new Button(ruleCtrlsComp, SWT.RADIO);
    this.noOBDRadio.setText("No OBD labels");
    this.noOBDRadio.setSelection(true);

    this.onlyOBDRadio = new Button(ruleCtrlsComp, SWT.RADIO);
    this.onlyOBDRadio.setText("Only OBD Labels");

    this.bothOBDNonOBDRadio = new Button(ruleCtrlsComp, SWT.RADIO);
    this.bothOBDNonOBDRadio.setText("Both, OBD and Non-OBD Labels");
  }


  private void createFuncParamRadioControls() {

    // Create the new Group for the Radio Buttons
    final Group wpSelGroup = new Group(this.parentComp, SWT.NONE);
    wpSelGroup.setLayout(new GridLayout());
    GridData gridDataWp = new GridData(SWT.FILL, SWT.FILL, true, true);
    wpSelGroup.setLayoutData(gridDataWp);
    // Icdm-715 Source for the Lab fun file
    wpSelGroup.setText("Select the functions/parameters from");
    final Composite funcParamCtrlsComp = createRadioRowComp(wpSelGroup);

    this.workPackageRadio = new Button(funcParamCtrlsComp, SWT.RADIO);
    this.workPackageRadio.setText("Work Package");
    this.workPackageRadio.setSelection(true);

    // ICDM-2032
    this.a2lRadio = new Button(funcParamCtrlsComp, SWT.RADIO);
    this.a2lRadio.setText("A2l Functions");

    this.labFunRadio = new Button(funcParamCtrlsComp, SWT.RADIO);
    this.labFunRadio.setText("LAB/FUN file");
    this.labFunRadio.setSelection(false);

    this.paramRadio = new Button(funcParamCtrlsComp, SWT.RADIO);
    this.paramRadio.setText("Files to be Reviewed");
    this.paramRadio.setSelection(false);

    // Task 237131
    this.compliRadio = new Button(funcParamCtrlsComp, SWT.RADIO);
    this.compliRadio.setText("COMPLIANCE and Q-SSD parameters");
    this.compliRadio.setSelection(false);

    // create workpakage control to hold workpage label,text and browse button
    createWrkPackageControl(createFirstRowComp(wpSelGroup));
  }

  /**
   * @param firstRowComposite
   */
  private void createWrkPackageControl(final Composite firstRowComposite) {
    this.wpLabel = new Label(firstRowComposite, SWT.NONE);
    this.wpLabel.setText("WorkPackage : ");
    this.workingPkg = new Text(firstRowComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
    this.workingPkg.setEditable(false);
    // Adding Drop Listener for Lab fun file path textbox
    this.labFunFileDropFileListener = new DropFileListener(this.workingPkg, this.filExtensions);
    this.labFunFileDropFileListener.addDropFileListener(false);
    this.labFunFileDropFileListener.setEditable(false);

    final GridData workingPkgData = new GridData(SWT.FILL, SWT.NONE, true, false);
    workingPkgData.widthHint = new PixelConverter(this.workingPkg).convertWidthInCharsToPixels(25);
    workingPkgData.heightHint = new PixelConverter(this.workingPkg).convertHeightInCharsToPixels(2);
    this.workingPkg.setLayoutData(workingPkgData);

    this.workingPkgBrowse = new Button(firstRowComposite, SWT.PUSH);
    final Image browseImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);
    this.workingPkgBrowse.setImage(browseImage);
    this.workingPkgBrowse.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.workingPkgBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
  }

  /**
   * This method adds the filter instance to addNewUserTableViewer
   */
  private void addFilters() {
    this.wpFilters = new FunsFilter();
    this.wrkPkgFuncsList.addFilter(this.wpFilters);
    this.reviewFunFilters = new FunsFilter();
    this.reviewFuncList.addFilter(this.reviewFunFilters);
  }

  /**
   * @param reviewFuncComp
   */
  private void createReviewFuncsFilter(final Composite reviewFuncComp) {
    this.reviewFunFilterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), reviewFuncComp,
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
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
   * @param wrkPkgFuncComp
   */
  private void createWrkPkgFuncsFilter(final Composite wrkPkgFuncComp) {
    this.wpFilterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), wrkPkgFuncComp,
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
  }


  // Story 221802
  /**
   * @param cdrReviewData
   */
  public void commonRuleSelection(final CDRWizardUIModel cdrWizardUIModel) {
    WorkpackageSelectionWizardPage.this.workPackageRadio.setEnabled(true);
    WorkpackageSelectionWizardPage.this.a2lRadio.setEnabled(true);
    WorkpackageSelectionWizardPage.this.labFunRadio.setEnabled(true);
    WorkpackageSelectionWizardPage.this.paramRadio.setEnabled(true);
    WorkpackageSelectionWizardPage.this.compliRadio.setEnabled(true);

    cdrWizardUIModel.setPrimaryRuleSetId(null);
    WorkpackageSelectionWizardPage.this.wpFilterTxt.setEnabled(true);
    WorkpackageSelectionWizardPage.this.reviewFunFilterTxt.setEnabled(true);
    WorkpackageSelectionWizardPage.this.workingPkg.getText();
    WorkpackageSelectionWizardPage.this.calDataReviewWizard
        .setContentChanged(cdrWizardUIModel.getPrimaryRuleSetId() != null);
    if (!this.a2lRadio.getSelection() && validateLabFunWPRadio() && validateParamCompliRadio()) {
      setPageComplete(false);
    }
  }


  /**
   * @return
   */
  private boolean validateParamCompliRadio() {
    return !this.paramRadio.getSelection() && !this.compliRadio.getSelection();
  }


  /**
   * @return
   */
  private boolean validateLabFunWPRadio() {
    return !this.labFunRadio.getSelection() && !this.workPackageRadio.getSelection();
  }


  /**
   * @param funcComp
   * @return
   */
  private Composite createReviewFuncComp(final Composite funcComp) {
    final Composite reviewfuncComposite = new Composite(funcComp, SWT.NONE);
    final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.widthHint = convertWidthInCharsToPixels(40);
    reviewfuncComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    final GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    reviewfuncComposite.setLayout(gridLayout);
    return reviewfuncComposite;
  }

  /**
   * @param functionsComposite
   * @return
   */
  private Composite createButtonComp(final Composite functionsComposite) {

    final Composite buttonComp = new Composite(functionsComposite, SWT.NONE);
    final GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginTop = 100;
    buttonComp.setLayout(gridLayout);
    buttonComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    return buttonComp;
  }

  /**
   * @param functionsComposite
   * @return
   */
  private Composite createWrkPkgFuncComp(final Composite functionsComposite) {
    final Composite wrkPkgFuncComp = new Composite(functionsComposite, SWT.NONE);
    final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.widthHint = convertWidthInCharsToPixels(40);
    wrkPkgFuncComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    final GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    wrkPkgFuncComp.setLayout(gridLayout);
    return wrkPkgFuncComp;
  }

  /**
   * @param composite
   * @return
   */
  private Composite createFuncComposite(final Composite composite) {
    final Composite functionsComposite = new Composite(composite, SWT.NONE);
    final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.heightHint = convertHeightInCharsToPixels(20);
    functionsComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    final GridLayout gridLayout = new GridLayout(3, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    functionsComposite.setLayout(gridLayout);
    return functionsComposite;
  }

  /**
   * @param composite
   * @return
   */
  private Composite createFirstRowComp(final Composite composite) {
    final Composite firstRowComposite = new Composite(composite, SWT.NONE);
    firstRowComposite.setLayout(new GridLayout(3, false));
    firstRowComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    return firstRowComposite;
  }


  /**
   * @param composite
   * @return composite // Icdm-715 UI Changes to Wizard
   */
  private Composite createRadioRowComp(final Composite composite) {
    final Composite radioCtrlsRowComp = new Composite(composite, SWT.NONE);
    // ICDM-2032
    radioCtrlsRowComp.setLayout(new GridLayout(3, true));
    radioCtrlsRowComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    return radioCtrlsRowComp;
  }


  /**
   * @param wpName work package name to set
   */
  public void setWPName(final String wpName) {
    this.workingPkg.setText(wpName);
  }

  private void createWrkPkgFuncsList(final Composite parent, final String lableText) {
    final Label label = new Label(parent, SWT.WRAP);
    label.setText(lableText);
    label.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.wrkPkgFuncsList = new ListViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    GridData gridDataFunc = new GridData(SWT.FILL, SWT.FILL, true, false);
    gridDataFunc.heightHint = 250;
    this.wrkPkgFuncsList.getList().setLayoutData(gridDataFunc);
    this.wrkPkgFuncsList.setContentProvider(new ArrayContentProvider());
    this.wrkPkgFuncsList.setInput(this.wpFuncsSet);

  }

  /**
   * @return the wrkPkgFuncsList
   */
  public SortedSet<String> getWrkPkgFuncsSet() {
    return this.wpFuncsSet;
  }

  public ListViewer getWrkPkgListViewer() {
    return this.wrkPkgFuncsList;
  }

  private void createReviewFuncsList(final Composite parent, final String lableText) {
    final Label label = new Label(parent, SWT.WRAP);
    label.setText(lableText);
    label.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.reviewFuncList = new ListViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    GridData gridDataFunc = new GridData(SWT.FILL, SWT.FILL, true, false);
    gridDataFunc.heightHint = 250;
    this.reviewFuncList.getList().setLayoutData(gridDataFunc);
    this.reviewFuncList.setContentProvider(new ArrayContentProvider());
    this.reviewFuncList.setInput(this.reviewFuncsSet);

  }

  private void createButtonBar(final Composite parent) {

    this.addButton = new Button(parent, SWT.PUSH);
    final GridData addData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    this.addButton.setLayoutData(addData);
    this.addButton.setEnabled(false);
    final Image addImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.FORWARD_16X16);
    this.addButton.setImage(addImage);

    this.removeButton = new Button(parent, SWT.PUSH);
    this.removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.removeButton.setEnabled(false);
    final Image removeImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BACKWARD_16X16);
    this.removeButton.setImage(removeImage);
    this.removeButton.setEnabled(false);

  }


  /**
   * @param reviewFuncs funcs to be reviewed
   */
  public void setReviewFuncs(final String[] reviewFuncs) {
    for (String func : reviewFuncs) {
      this.reviewFuncsSet.add(func);
    }
    this.reviewFuncList.refresh();
  }

  /**
   * @param wrkPkgFuncs funcs to be reviewed
   */
  public void setWrkPkgFuncs(final String[] wrkPkgFuncs) {
    if (wrkPkgFuncs != null) {
      for (String wp : wrkPkgFuncs) {
        this.wpFuncsSet.add(wp);
      }
      this.wrkPkgFuncsList.refresh();
    }
  }


  /**
   *
   */
  public void backPressed() {
    this.workpackageSelectionPageResolver.processBackPressed();
    this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);
  }

  /**
   * Set the selected review functions in the caldata model
   */
  public void nextPressed() {

    // Calling setinput method in resolver
    WorkpackageSelectionWizardPage.this.workpackageSelectionPageResolver.setInput(this.calDataReviewWizard);
    WorkpackageSelectionWizardPage.this.workpackageSelectionPageResolver.processNextPressed();
    if (this.calDataReviewWizard.isDeltaReview() && !this.calDataReviewWizard.isProjectDataDeltaReview()) {
      this.calDataReviewWizard.getSsdRuleSelPage().getSsdRuleSelectionPageResolver()
          .fillUIData(this.calDataReviewWizard);
    }
  }


  /**
   * set the review functions for Review.
   */
  public void setReviewFunctions() {
    this.calDataReviewWizard.getCdrWizardUIModel().setSelReviewFuncs(this.reviewFuncsSet);
  }


  /**
   * Icdm-739 set the invalid params/labels to the next page
   */
  public void setInvalidFunParam() {

    this.unAvialParams.clear();
    this.unAvialFuncs.clear();
    try {
      checkForInvalidFunctions();
      checkForInvalidParams();
    }
    catch (NumberFormatException | ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    if ((this.reviewFileSelWizPage != null) && (this.reviewFileSelWizPage.getInvalidFileList() != null)) {
      this.reviewFileSelWizPage.getInvalidFileList().removeAll();
    }
    // Add the Check for Invalid entries when selecting the Workpkg option also
    if (validateParamAndFuncAvailabiltiy() && validateRadioButtons()) {
      showErrorForInvalidFun();
    }
    else if (getNextPage() instanceof ReviewFilesSelectionWizardPage) {
      this.reviewFileSelWizPage = (ReviewFilesSelectionWizardPage) getNextPage();
      this.reviewFileSelWizPage.getTabComp().setVisible(true);
    }
  }


  /**
   * @return
   */
  private boolean validateParamAndFuncAvailabiltiy() {
    return !this.unAvialParams.isEmpty() || !this.unAvialFuncs.isEmpty();
  }


  /**
   * @return
   */
  private boolean validateRadioButtons() {
    return this.labFunRadio.getSelection() || this.a2lRadio.getSelection() || this.workPackageRadio.getSelection();
  }


  /**
   *
   */
  private void showErrorForInvalidFun() {
    CDMLogger.getInstance().warnDialog("There are invalid functions/parameters in the input file selected.",
        Activator.PLUGIN_ID);
    this.reviewFileSelWizPage = this.calDataReviewWizard.getFilesSelWizPage();
    this.reviewFileSelWizPage.getTabComp().setVisible(true);
    final SortedSet<String> invalidNames = (!this.unAvialParams.isEmpty()) ? this.unAvialParams : this.unAvialFuncs;

    for (String str : invalidNames) {
      this.reviewFileSelWizPage.getInvalidFileList().add(str);
    }
  }


  /**
   * Check for Invalid Parameters // Icdm-739 Handling invalid params and functions
   *
   * @throws ApicWebServiceException
   * @throws NumberFormatException
   */
  private void checkForInvalidParams() throws ApicWebServiceException {

    final java.util.List<String> labelList = this.calDataReviewWizard.getCdrWizardUIModel().getLabelList();

    final Map<String, Characteristic> a2lCharMap =
        this.calDataReviewWizard.getA2lEditiorDataHandler().getCharacteristicsMap();

    // If the label list size is very high then error message is given.
    if (labelList.size() > Integer.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.CDR_MAX_PARAM_COUNT))) {

      final String dialogMessage = NUMBER_OF_PARAMETERS_IS_TOO_HIGH +
          new CommonDataBO().getParameterValue(CommonParamKey.CDR_MAX_PARAM_COUNT) + ").Review cannot be performed.";
      CDMLogger.getInstance().errorDialog(dialogMessage, Activator.PLUGIN_ID);
      setPageComplete(false);
      getContainer().updateButtons();

    }
    else if (!labelList.isEmpty()) {
      setInvalidLabels(labelList, a2lCharMap);
    }


  }


  /**
   * Data for cancelled Review
   */
  public void setDataForCancelPressed() {
    this.workpackageSelectionPageResolver.setInput(this.calDataReviewWizard);
  }


  /**
   * @param labelList
   * @param a2lCharMap
   */
  private void setInvalidLabels(final java.util.List<String> labelList, final Map<String, Characteristic> a2lCharMap) {
    for (String label : labelList) {
      final Characteristic charObj = a2lCharMap.get(label);
      if (charObj != null) {
        final Function func = charObj.getDefFunction();
        if (func == null) {
          // If the label is not having a def function then add it to the unavialable list
          this.unAvialParams.add(label);
          CDMLogger.getInstance().warn(
              "Parameter :" + label + " has not defined function. The parameter is not included in review process!",
              Activator.PLUGIN_ID);
        }
      }
      else {
        // If the label is not present in the Char Map then add it to the unavialable list
        this.unAvialParams.add(label);
      }
    }
    try {
      Set<String> missingParams = new CDRHandler().getMissingParams(new HashSet<>(labelList), new HashSet<>());
      for (String invalidLabels : missingParams) {
        this.unAvialParams.add(invalidLabels);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }


  /**
   * Check for Invalid functions // Icdm-739 Handling invalid params and functions
   *
   * @throws ApicWebServiceException
   * @throws NumberFormatException
   */
  private void checkForInvalidFunctions() throws ApicWebServiceException {
    final SortedSet<String> selReviewFuncs = this.calDataReviewWizard.getCdrWizardUIModel().getSelReviewFuncs();
    SortedSet<Function> selFuncSetAvailInA2l = new TreeSet<>();

    List<A2LGroup> a2lGroupList = this.calDataReviewWizard.getA2lEditiorDataHandler().getA2LGroupList();

    if ((this.calDataReviewWizard.getCdrWizardUIModel().getA2lGroupName() != null) &&
        (this.calDataReviewWizard.getCdrWizardUIModel().getA2lGroupNameList() == null)) {
      getA2lwithGroupFuncSet(selReviewFuncs, selFuncSetAvailInA2l, a2lGroupList);
    }
    else {
      getA2lWithoutGrpFuncSet(selReviewFuncs, selFuncSetAvailInA2l);
    }


    if (!selReviewFuncs.isEmpty()) {
      setInvalidFunctions(selReviewFuncs);
    }
  }


  /**
   * @param selReviewFuncs
   * @param selFuncSetAvailInA2l
   * @param a2lGroupList
   * @throws ApicWebServiceException
   */
  private void getA2lwithGroupFuncSet(final SortedSet<String> selReviewFuncs,
      final SortedSet<Function> selFuncSetAvailInA2l, final List<A2LGroup> a2lGroupList)
      throws ApicWebServiceException {
    for (A2LGroup a2lGroup : a2lGroupList) {
      if (a2lGroup.getGroupName().equals(this.calDataReviewWizard.getCdrWizardUIModel().getA2lGroupName())) {

        Map<String, Function> allFunctionMap = this.calDataReviewWizard.getA2lEditiorDataHandler().getAllFunctionMap();
        for (String func : selReviewFuncs) {
          if (allFunctionMap.containsKey(func)) {
            selFuncSetAvailInA2l.add(allFunctionMap.get(func));
          }
        }
        Map<String, String> labelMapForGrp = a2lGroup.getLabelMap();
        int paramCount = getParamCountForA2lWithGrps(selFuncSetAvailInA2l, labelMapForGrp);
        if (paramCount > Integer.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.CDR_MAX_PARAM_COUNT))) {
          showParamTooHighErrDialog();
        }
        else {
          break;
        }
      }
    }
  }

  /**
   * @param selFuncSetAvailInA2l
   * @param labelMapForGrp
   * @return
   */
  private int getParamCountForA2lWithGrps(final SortedSet<Function> selFuncSetAvailInA2l,
      final Map<String, String> labelMapForGrp) {
    int paramCount = 0;
    for (Function function : selFuncSetAvailInA2l) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      if (defCharRefList != null) {
        for (DefCharacteristic defCharacteristic : defCharRefList) {
          if (labelMapForGrp.containsKey(defCharacteristic.getObj().getName())) {
            paramCount++;
          }
        }
      }
    }
    return paramCount;
  }

  /**
   * @param selReviewFuncs
   * @param selFuncSetAvailInA2l
   * @throws ApicWebServiceException
   */
  private void getA2lWithoutGrpFuncSet(final SortedSet<String> selReviewFuncs,
      final SortedSet<Function> selFuncSetAvailInA2l)
      throws ApicWebServiceException {
    Map<String, Function> allFunctionMap = this.calDataReviewWizard.getA2lEditiorDataHandler().getAllFunctionMap();
    for (String func : selReviewFuncs) {
      if (allFunctionMap.containsKey(func)) {
        selFuncSetAvailInA2l.add(allFunctionMap.get(func));
      }
    }
    int paramCount = getParamCountForA2lWithoutGrps(selFuncSetAvailInA2l);
    if (paramCount > Integer.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.CDR_MAX_PARAM_COUNT))) {
      showParamTooHighErrDialog();
    }
  }

  /**
   * @param selFuncSetAvailInA2l
   * @return
   */
  private int getParamCountForA2lWithoutGrps(final SortedSet<Function> selFuncSetAvailInA2l) {
    int paramCount = 0;
    for (Function function : selFuncSetAvailInA2l) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      if (defCharRefList != null) {
        paramCount = paramCount + defCharRefList.size();
      }
    }
    return paramCount;
  }


  /**
   * @param paramCount
   * @throws ApicWebServiceException
   */
  private void showParamTooHighErrDialog() throws ApicWebServiceException {

    final String dialogMessage = NUMBER_OF_PARAMETERS_IS_TOO_HIGH +
        new CommonDataBO().getParameterValue(CommonParamKey.CDR_MAX_PARAM_COUNT) + "). Review cannot be performed.";

    CDMLogger.getInstance().errorDialog(dialogMessage, Activator.PLUGIN_ID);
    setPageComplete(false);
    getContainer().updateButtons();
  }


  /**
   * @param selReviewFuncs
   */
  private void setInvalidFunctions(final SortedSet<String> selReviewFuncs) {

    try {
      Map<String, com.bosch.caltool.icdm.model.a2l.Function> functionByName =
          this.cdrHandler.getFunctionByName(selReviewFuncs);


      for (String selectedFun : selReviewFuncs) {
        if (!functionByName.containsKey(selectedFun) &&
            CommonUtils.isNotEqual(selectedFun, ApicConstants.NOT_ASSIGNED)) {
          this.unAvialFuncs.add(selectedFun);
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * Clear the fields when different a2l file is selected
   */
  public void clearFields() {
    if (!this.workingPkg.getText().isEmpty()) {
      this.workingPkg.setText("");
    }
    this.workPackageSelectionWizardPageListener.clearWPFuncs();
    this.workPackageSelectionWizardPageListener.clearReviewFuncs();
  }


  /**
   * @param buttonState the workingPkgBrowse to set
   */
  public void setWorkingPkgBrowse(final boolean buttonState) {
    this.workingPkgBrowse.setEnabled(buttonState);
  }

  /**
   *
   */
  public void setAllFucntions() {
    if (this.workPackageRadio.getSelection() || this.a2lRadio.getSelection()) {
      final ReviewWrkPkgSelectionDialog wrkPkgSelDialog =
          new ReviewWrkPkgSelectionDialog(Display.getCurrent().getActiveShell(),
              this.calDataReviewWizard.getA2lEditiorDataHandler().getA2lFileInfo(), null);
      final String[] a2lFunctions = wrkPkgSelDialog.getAllFunctions();
      for (String wp : a2lFunctions) {
        getWrkPkgFuncsSet().add(wp);
      }

      if ((this.calDataReviewWizard.getA2lEditiorDataHandler().getUnassignedParams() != null) &&
          (!this.calDataReviewWizard.getA2lEditiorDataHandler().getUnassignedParams().isEmpty())) {
        getWrkPkgFuncsSet().add(ApicConstants.NOT_ASSIGNED);

      }

      this.wrkPkgFuncsList.refresh();
      getContainer().updateButtons();
    }

  }


  /**
   * @return the workingPkg
   */
  public Text getWorkingPkg() {
    return this.workingPkg;
  }


  /**
   * @return the reviewFuncList
   */
  public ListViewer getReviewFuncListViewer() {
    return this.reviewFuncList;
  }


  /**
   * enable the review function lists
   */
  public void enableReviewFunctionLists() {
    this.workPackageSelectionWizardPageListener.setWrkPkgFuncList(!this.wpFuncsSet.isEmpty());

    this.workPackageSelectionWizardPageListener
        .setReviewFuncList(!WorkpackageSelectionWizardPage.this.reviewFuncsSet.isEmpty());
    WorkpackageSelectionWizardPage.this.addButton.setEnabled(false);
    WorkpackageSelectionWizardPage.this.removeButton.setEnabled(false);

  }

  /**
   * @param enbOnlyOBDRadioBtn
   * @param enbNoOBDRadioBtn
   * @param enbBothOBDAndNonOBDRadioBtn
   */
  public void setRvwContentCtrls(final boolean enbOnlyOBDRadioBtn, final boolean enbNoOBDRadioBtn,
      final boolean enbBothOBDAndNonOBDRadioBtn) {
    this.onlyOBDRadio.setSelection(enbOnlyOBDRadioBtn);
    this.noOBDRadio.setSelection(enbNoOBDRadioBtn);
    this.bothOBDNonOBDRadio.setSelection(enbBothOBDAndNonOBDRadioBtn);
  }

  /**
   * @return boolean
   */
  public boolean isNextPageRuleSetPage() {
    return getRuleSetRadio().getSelection() || this.secondaryRulesButton.getSelection();
  }


  /**
   * @return Button
   */
  public Button getSecondaryRuleBtn() {
    return this.secondaryRulesButton;
  }


  /**
   * @return Button
   */
  public Button getCompliRadio() {
    return this.compliRadio;
  }


  /**
   * @return the ssdRuleButton
   */
  public Button getSsdRuleButton() {
    return this.ssdRuleButton;
  }


  /**
   * @param cdrReviewData Not modified method
   */
  public void removeRuleSetData(final CDRWizardUIModel cdrWizardUIModel) {
    // TODO
  }


  /**
   * @return the wpLabel
   */
  public Label getWpLabel() {
    return this.wpLabel;
  }


  /**
   * @param preference the preference to set
   */
  public void setPreference(final IPreferenceStore preference) {
    this.preference = preference;
  }


  /**
   * @return the wrkPkgFuncsList
   */
  public ListViewer getWrkPkgFuncsList() {
    return this.wrkPkgFuncsList;
  }


  /**
   * @param wrkPkgFuncsList the wrkPkgFuncsList to set
   */
  public void setWrkPkgFuncsList(final ListViewer wrkPkgFuncsList) {
    this.wrkPkgFuncsList = wrkPkgFuncsList;
  }


  /**
   * @return the reviewFuncList
   */
  public ListViewer getReviewFuncList() {
    return this.reviewFuncList;
  }


  /**
   * @param reviewFuncList the reviewFuncList to set
   */
  public void setReviewFuncList(final ListViewer reviewFuncList) {
    this.reviewFuncList = reviewFuncList;
  }


  /**
   * @return the calDataReviewWizard
   */
  public CalDataReviewWizard getCalDataReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @param calDataReviewWizard the calDataReviewWizard to set
   */
  public void setCalDataReviewWizard(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;
  }

  /**
   * @return the reviewFileSelWizPage
   */
  public ReviewFilesSelectionWizardPage getReviewFileSelWizPage() {
    return this.reviewFileSelWizPage;
  }


  /**
   * @param reviewFileSelWizPage the reviewFileSelWizPage to set
   */
  public void setReviewFileSelWizPage(final ReviewFilesSelectionWizardPage reviewFileSelWizPage) {
    this.reviewFileSelWizPage = reviewFileSelWizPage;
  }


  /**
   * @return the fileName
   */
  public String getFileName() {
    return this.fileName;
  }


  /**
   * @param fileName the fileName to set
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }


  /**
   * @return the a2lRadio
   */
  public Button getA2lRadio() {
    return this.a2lRadio;
  }

  /**
   * @return the ruleSetRadio
   */
  public Button getRuleSetRadio() {
    return this.ruleSetRadio;
  }


  /**
   * @return the unAvialParams
   */
  public SortedSet<String> getUnAvialParams() {
    return this.unAvialParams;
  }


  /**
   * @return the unAvialFuncs
   */
  public SortedSet<String> getUnAvialFuncs() {
    return this.unAvialFuncs;
  }


  /**
   * @return the paramRadio
   */
  public Button getParamRadio() {
    return this.paramRadio;
  }

  /**
   * @return the commonRuleRadio
   */
  public Button getCommonRuleRadio() {
    return this.commonRuleRadio;
  }

  /**
   * @return the setFunLabPath
   */
  public String getFunLabPath() {
    return this.funLabPath;
  }


  /**
   * @param the setFunLabPath to set
   */
  public void setFunLabPath(final String setFunLabPath) {
    this.funLabPath = setFunLabPath;
  }


  /**
   * @return the workPackageRadio
   */
  public Button getWorkPackageRadio() {
    return this.workPackageRadio;
  }


  /**
   * @return the labFunRadio
   */
  public Button getLabFunRadio() {
    return this.labFunRadio;
  }

  /**
   * @return the wpFilterTxt
   */
  public Text getWpFilterTxt() {
    return this.wpFilterTxt;
  }


  /**
   * @return the reviewFunFilterTxt
   */
  public Text getReviewFunFilterTxt() {
    return this.reviewFunFilterTxt;
  }


  /**
   * @return the selectedWrkPkg
   */
  public WpRespModel getSelectedWrkPkg() {
    return this.selectedWrkPkg;
  }


  /**
   * @param selectedWrkPkg the selectedWrkPkg to set
   */
  public void setSelectedWrkPkg(final WpRespModel selectedWrkPkg) {
    this.selectedWrkPkg = selectedWrkPkg;
  }


  /**
   * @return the selectedWrkPkgFuncs
   */
  public String[] getSelectedWrkPkgFuncs() {
    return this.selectedWrkPkgFuncs;
  }


  /**
   * @param selectedWrkPkgFuncs the selectedWrkPkgFuncs to set
   */
  public void setSelectedWrkPkgFuncs(final String[] selectedWrkPkgFuncs) {
    this.selectedWrkPkgFuncs = selectedWrkPkgFuncs;
  }


  /**
   * @return the selectedFuncsToReview
   */
  public String[] getSelectedFuncsToReview() {
    return this.selectedFuncsToReview;
  }


  /**
   * @param selectedFuncsToReview the selectedFuncsToReview to set
   */
  public void setSelectedFuncsToReview(final String[] selectedFuncsToReview) {
    this.selectedFuncsToReview = selectedFuncsToReview;
  }


  /**
   * @return the functions
   */
  public Set<String> getFunctions() {
    return this.functions;
  }


  /**
   * @param functions the functions to set
   */
  public void setFunctions(final Set<String> functions) {
    this.functions = functions;
  }


  /**
   * @return the addButton
   */
  public Button getAddButton() {
    return this.addButton;
  }


  /**
   * @param addButton the addButton to set
   */
  public void setAddButton(final Button addButton) {
    this.addButton = addButton;
  }


  /**
   * @return the removeButton
   */
  public Button getRemoveButton() {
    return this.removeButton;
  }


  /**
   * @param removeButton the removeButton to set
   */
  public void setRemoveButton(final Button removeButton) {
    this.removeButton = removeButton;
  }


  /**
   * @return the workingPkgBrowse
   */
  public Button getWorkingPkgBrowse() {
    return this.workingPkgBrowse;
  }

  /**
   * @return the wpFilters
   */
  public FunsFilter getWpFilters() {
    return this.wpFilters;
  }


  /**
   * @param wpFilters the wpFilters to set
   */
  public void setWpFilters(final FunsFilter wpFilters) {
    this.wpFilters = wpFilters;
  }


  /**
   * @return the reviewFunFilters
   */
  public FunsFilter getReviewFunFilters() {
    return this.reviewFunFilters;
  }


  /**
   * @param reviewFunFilters the reviewFunFilters to set
   */
  public void setReviewFunFilters(final FunsFilter reviewFunFilters) {
    this.reviewFunFilters = reviewFunFilters;
  }


  /**
   * @return the secondaryRulesButton
   */
  public Button getSecondaryRulesButton() {
    return this.secondaryRulesButton;
  }


  /**
   * @param secondaryRulesButton the secondaryRulesButton to set
   */
  public void setSecondaryRulesButton(final Button secondaryRulesButton) {
    this.secondaryRulesButton = secondaryRulesButton;
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
   * @return the workpackageSelectionPageResolver
   */
  public WorkpackageSelectionPageResolver getWorkpackageSelectionPageResolver() {
    return this.workpackageSelectionPageResolver;
  }


  /**
   * @return the ignoreCaseComparator
   */
  public static Comparator<String> getIgnoreCaseComparator() {
    return IGNORE_CASE_COMPARATOR;
  }


  /**
   * @return the wpFuncsSet
   */
  public SortedSet<String> getWpFuncsSet() {
    return this.wpFuncsSet;
  }


  /**
   * @return the reviewFuncs
   */
  public SortedSet<String> getReviewFuncsSet() {
    return this.reviewFuncsSet;
  }

  /**
   * @return the filExtensions
   */
  public String[] getFilExtensions() {
    return this.filExtensions;
  }


  /**
   * @return the filenames
   */
  public static String[] getFilenames() {
    return fileNames;
  }


  /**
   * @return the singWorkPackSelSize
   */
  public static int getSingWorkPackSelSize() {
    return SING_WORK_PACK_SEL_SIZE;
  }


  /**
   * @return the preference
   */
  public IPreferenceStore getPreference() {
    return this.preference;
  }


  /**
   * @param workingPkg the workingPkg to set
   */
  public void setWorkingPkg(final Text workingPkg) {
    this.workingPkg = workingPkg;
  }


  /**
   * @param workingPkgBrowse the workingPkgBrowse to set
   */
  public void setWorkingPkgBrowse(final Button workingPkgBrowse) {
    this.workingPkgBrowse = workingPkgBrowse;
  }


  /**
   * @param wpLabel the wpLabel to set
   */
  public void setWpLabel(final Label wpLabel) {
    this.wpLabel = wpLabel;
  }


  /**
   * @param labFunRadio the labFunRadio to set
   */
  public void setLabFunRadio(final Button labFunRadio) {
    this.labFunRadio = labFunRadio;
  }


  /**
   * @param workPackageRadio the workPackageRadio to set
   */
  public void setWorkPackageRadio(final Button workPackageRadio) {
    this.workPackageRadio = workPackageRadio;
  }


  /**
   * @param paramRadio the paramRadio to set
   */
  public void setParamRadio(final Button paramRadio) {
    this.paramRadio = paramRadio;
  }


  /**
   * @param commonRuleRadio the commonRuleRadio to set
   */
  public void setCommonRuleRadio(final Button commonRuleRadio) {
    this.commonRuleRadio = commonRuleRadio;
  }


  /**
   * @param ruleSetRadio the ruleSetRadio to set
   */
  public void setRuleSetRadio(final Button ruleSetRadio) {
    this.ruleSetRadio = ruleSetRadio;
  }


  /**
   * @param a2lRadio the a2lRadio to set
   */
  public void setA2lRadio(final Button a2lRadio) {
    this.a2lRadio = a2lRadio;
  }


  /**
   * @param formToolkit the formToolkit to set
   */
  public void setFormToolkit(final FormToolkit formToolkit) {
    this.formToolkit = formToolkit;
  }


  /**
   * @param wpFilterTxt the wpFilterTxt to set
   */
  public void setWpFilterTxt(final Text wpFilterTxt) {
    this.wpFilterTxt = wpFilterTxt;
  }


  /**
   * @param reviewFunFilterTxt the reviewFunFilterTxt to set
   */
  public void setReviewFunFilterTxt(final Text reviewFunFilterTxt) {
    this.reviewFunFilterTxt = reviewFunFilterTxt;
  }


  /**
   * @param compliRadio the compliRadio to set
   */
  public void setCompliRadio(final Button compliRadio) {
    this.compliRadio = compliRadio;
  }


  /**
   * @param ssdRuleButton the ssdRuleButton to set
   */
  public void setSsdRuleButton(final Button ssdRuleButton) {
    this.ssdRuleButton = ssdRuleButton;
  }


  /**
   * @return the cdrHandler
   */
  public CDRHandler getCdrHandler() {
    return this.cdrHandler;
  }


  /**
   * @return the workPackageSelectionWizardPageValidator
   */
  public WorkPackageSelectionWizardPageValidator getWorkPackageSelectionWizardPageValidator() {
    return this.workPackageSelectionWizardPageValidator;
  }


  /**
   * @param workPackageSelectionWizardPageValidator the workPackageSelectionWizardPageValidator to set
   */
  public void setWorkPackageSelectionWizardPageValidator(
      final WorkPackageSelectionWizardPageValidator workPackageSelectionWizardPageValidator) {
    this.workPackageSelectionWizardPageValidator = workPackageSelectionWizardPageValidator;
  }


  /**
   * @return the workPackageSelectionWizardPageListener
   */
  public WorkPackageSelectionWizardPageListener getWorkPackageSelectionWizardPageListener() {
    return this.workPackageSelectionWizardPageListener;
  }


  /**
   * @param workPackageSelectionWizardPageListener the workPackageSelectionWizardPageListener to set
   */
  public void setWorkPackageSelectionWizardPageListener(
      final WorkPackageSelectionWizardPageListener workPackageSelectionWizardPageListener) {
    this.workPackageSelectionWizardPageListener = workPackageSelectionWizardPageListener;
  }


  /**
   * @param workpackageSelectionPageResolver the workpackageSelectionPageResolver to set
   */
  public void setWorkpackageSelectionPageResolver(
      final WorkpackageSelectionPageResolver workpackageSelectionPageResolver) {
    this.workpackageSelectionPageResolver = workpackageSelectionPageResolver;
  }


  /**
   * @return the labFunFileDropFileListener
   */
  public DropFileListener getLabFunFileDropFileListener() {
    return this.labFunFileDropFileListener;
  }


  /**
   * @param labFunFileDropFileListener the labFunFileDropFileListener to set
   */
  public void setLabFunFileDropFileListener(final DropFileListener labFunFileDropFileListener) {
    this.labFunFileDropFileListener = labFunFileDropFileListener;
  }


  /**
   * @return the parentComp
   */
  public Composite getParentComp() {
    return this.parentComp;
  }


  /**
   * @param parentComp the parentComp to set
   */
  public void setParentComp(final Composite parentComp) {
    this.parentComp = parentComp;
  }

  /**
   * @return the rvwCntSelCtrlGrp
   */
  public Group getRvwCntSelCtrlGrp() {
    return this.rvwCntSelCtrlGrp;
  }


  /**
   * @param rvwCntSelCtrlGrp the rvwCntSelCtrlGrp to set
   */
  public void setRvwCntSelCtrlGrp(final Group rvwCntSelCtrlGrp) {
    this.rvwCntSelCtrlGrp = rvwCntSelCtrlGrp;
  }

  /**
   * @return the onlyOBDRadio
   */
  public Button getOnlyOBDRadio() {
    return this.onlyOBDRadio;
  }


  /**
   * @param onlyOBDRadio the onlyOBDRadio to set
   */
  public void setOnlyOBDRadio(final Button onlyOBDRadio) {
    this.onlyOBDRadio = onlyOBDRadio;
  }


  /**
   * @return the noOBDRadio
   */
  public Button getNoOBDRadio() {
    return this.noOBDRadio;
  }


  /**
   * @param noOBDRadio the noOBDRadio to set
   */
  public void setNoOBDRadio(final Button noOBDRadio) {
    this.noOBDRadio = noOBDRadio;
  }


  /**
   * @return the bothOBDNonOBDRadio
   */
  public Button getBothOBDNonOBDRadio() {
    return this.bothOBDNonOBDRadio;
  }


  /**
   * @param bothOBDNonOBDRadio the bothOBDNonOBDRadio to set
   */
  public void setBothOBDNonOBDRadio(final Button bothOBDNonOBDRadio) {
    this.bothOBDNonOBDRadio = bothOBDNonOBDRadio;
  }

}
