/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.PidcVariantSelectionDialog;
import com.bosch.caltool.cdr.ui.dialogs.ReviewWrkPkgSelectionDialog;
import com.bosch.caltool.cdr.ui.wizards.CDFXExportWizard;
import com.bosch.caltool.cdr.ui.wizards.CDFXExportWizardData;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportInput;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportOutput;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CdfxExportServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;

/**
 * @author say8cob
 */
public class CDFXExportInputWizardPage extends WizardPage {

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Composite instance
   */
  private Composite composite;

  /**
   * Section instance
   */
  private Section section;

  /**
   * Form instance
   */
  private Form form;

  private Button workPackageRadio;

  private Button boschRadio;

  private Button customerRadio;

  private Button othersRadio;

  private Button workPkgBrowse;

  private List wrkPkgList;


  /**
   * zip export file path
   */
  private Text zipExportPathText;

  private final Image browseImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);

  private final Color whiteColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

  private CDFXExportWizard wizard;

  private CDFXExportWizardData cdfxExportWizardData;

  private boolean isVariantChanged = false;

  private final CdfxExportInput cdfxExportInputModel = new CdfxExportInput();

  private Button readinessChkBox;

  private String outputZipFileName;

  private String outputFileNameStr;

  private String directoryPath;

  private Button oneFilePerWPChkBox;

  /**
   * @param pageName as input
   */
  public CDFXExportInputWizardPage(final String pageName) {
    super(pageName);
    setTitle(pageName);
    setDescription("");

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

    this.composite = new Composite(scrollComp, SWT.NULL);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);
    this.composite.setBackground(this.whiteColor);

    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);

    createSection();
    scrollComp.setContent(this.composite);
    scrollComp.setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);

    this.wizard = (CDFXExportWizard) getWizard();
    this.cdfxExportWizardData = this.wizard.getCdfxExportWizardData();
  }


  /**
   * create section
   *
   * @param parent
   */
  private void createSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
    this.section =
        SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), gridData, "100% CDFx Export ");
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    // create form
    createForm();
    // set the client
    this.section.setClient(this.form);
  }


  /**
   * @param parent
   */
  private void createForm() {

    this.form = getFormToolkit().createForm(this.section);


    // Create the new Group for the Radio Buttons
    final Group mainGroup = new Group(this.composite, SWT.NONE);
    mainGroup.setLayout(new GridLayout());
    GridData gridDataWp = new GridData(SWT.FILL, SWT.FILL, true, true);
    mainGroup.setLayoutData(gridDataWp);
    // Icdm-715 Source for the Lab fun file
    mainGroup.setText("Fill the Details : ");
    mainGroup.setBackground(this.whiteColor);

    final Composite maincomposite = new Composite(mainGroup, SWT.NONE);
    GridData gridDataMain = new GridData(SWT.FILL, SWT.FILL, true, true);
    maincomposite.setLayout(new GridLayout(1, true));
    maincomposite.setLayoutData(gridDataMain);
    maincomposite.setBackground(this.whiteColor);

    this.cdfxExportWizardData = ((CDFXExportWizard) getWizard()).getCdfxExportWizardData();

    // create the controls for variant selection
    createVariantControls(maincomposite);
    // create the controls for scope selection
    createScopeControls(maincomposite);
    // create the controls for readiness selection
    createReadinessControls(maincomposite);
    // create the controls for export selection
    createExportFileControls(maincomposite);

    final GridLayout gridLayout = new GridLayout();
    // 3 columns for the layout
    gridLayout.numColumns = 3;
    gridLayout.verticalSpacing = 9;
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

  }

  /**
   *
   */
  private void createInputModel() {
    Set<PidcVariant> pidcVariants = new HashSet<>();
    // Setting the selected variants to the input model
    Set<PidcVariant> selectedVariants = this.cdfxExportWizardData.getPidcVariants();
    // If the selected variant is single
    PidcVariant selectedVariant = this.cdfxExportWizardData.getPidcVariant();
    if (CommonUtils.isNullOrEmpty(selectedVariants) && CommonUtils.isNotNull(selectedVariant)) {
      pidcVariants.add(selectedVariant);
    }
    // This condition will be executed in case of multiple variants
    else if (CommonUtils.isNotEmpty(selectedVariants)) {
      pidcVariants.addAll(selectedVariants);
    }
    this.cdfxExportInputModel.setVariantsList(pidcVariants);

    String selectedScope = null;
    if (this.boschRadio.getSelection()) {
      selectedScope = WpRespType.RB.getCode();
    }
    else if (this.customerRadio.getSelection()) {
      selectedScope = WpRespType.CUSTOMER.getCode();
    }
    else if (this.othersRadio.getSelection()) {
      selectedScope = WpRespType.OTHERS.getCode();
    }
    // Setting the selected scope to input model
    this.cdfxExportInputModel.setScope(selectedScope);

    // Setting the selected wp list to input model
    this.cdfxExportInputModel.getWpRespModelList().addAll(this.cdfxExportWizardData.getWpRespModels());
    this.cdfxExportInputModel.setPidcA2lId(this.cdfxExportWizardData.getPidcA2l().getId());
    this.cdfxExportInputModel.setReadinessFlag(this.readinessChkBox.getSelection());
    this.cdfxExportInputModel.setExportFileName(this.outputFileNameStr);
    this.cdfxExportInputModel.setOneFilePerWpFlag(this.oneFilePerWPChkBox.getSelection());
  }


  /**
   * create variant controls
   */
  private void createReadinessControls(final Composite maincomposite) {
    // composite for displaying radio buttons

    final Group readinessGroup = new Group(maincomposite, SWT.NONE);
    readinessGroup.setLayout(new GridLayout());
    GridData gridDataWp = new GridData(SWT.FILL, SWT.FILL, true, false);
    readinessGroup.setLayoutData(gridDataWp);
    // Icdm-715 Source for the Lab fun file
    readinessGroup.setText("Readiness : ");
    readinessGroup.setBackground(this.whiteColor);

    Composite readinessComposite = getFormToolkit().createComposite(readinessGroup);
    GridLayout layout = new GridLayout(1, false);
    readinessComposite.setLayout(layout);

    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.widthHint = 400;
    data.heightHint = 100;
    readinessComposite.setLayoutData(data);

    Browser browser = new Browser(readinessComposite, SWT.NONE);
    browser.setText(this.cdfxExportWizardData.getCdfxReadinessConditionStr());
    GridData gridDataBrowser = new GridData(SWT.FILL, SWT.FILL, true, true);
    browser.setLayoutData(gridDataBrowser);

    this.readinessChkBox = new Button(readinessGroup, SWT.CHECK);
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    this.readinessChkBox.setBackground(this.whiteColor);
    this.readinessChkBox.setLayoutData(gridData);
    this.readinessChkBox.setText("Readiness fulfilled");
    // add the listener
    this.readinessChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        getWizard().getContainer().updateButtons();
      }
    });
  }

  /**
   * create variant controls
   */
  private void createExportFileControls(final Composite maincomposite) {

    // composite for displaying radio buttons
    Composite exportComposite = getFormToolkit().createComposite(maincomposite);
    GridLayout gridLayoutForComp = new GridLayout();
    gridLayoutForComp.numColumns = 3;
    GridData exportGridData = new GridData();
    exportGridData.grabExcessHorizontalSpace = true;
    exportGridData.horizontalAlignment = GridData.FILL;
    exportComposite.setLayout(gridLayoutForComp);
    exportComposite.setLayoutData(exportGridData);
    getFormToolkit().createLabel(exportComposite, "Output File : ");
    this.zipExportPathText = getFormToolkit().createText(exportComposite, null, SWT.SINGLE | SWT.BORDER);

    this.zipExportPathText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.zipExportPathText.setEditable(false);

    ControlDecoration zipExportPathTextDec = new ControlDecoration(this.zipExportPathText, SWT.LEFT | SWT.TOP);
    new Decorators().showReqdDecoration(zipExportPathTextDec, "This field is mandatory.");

    this.zipExportPathText.addModifyListener(evnt -> Validator.getInstance().validateNDecorate(zipExportPathTextDec,
        this.zipExportPathText, null, null, true));

    createBrowseBtn(exportComposite);
  }


  /**
   * @param exportComposite
   */
  private void createBrowseBtn(final Composite exportComposite) {
    final Button browseBtn = getFormToolkit().createButton(exportComposite, "", SWT.PUSH);

    browseBtn.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {

        FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
        fileDialog.setFilterPath(CommonUtils.getUserDirPath());
        fileDialog.setOverwrite(true);
        fileDialog.setFilterExtensions(ApicUiConstants.ZIP_FILE_FILTER_EXTN);
        StringBuilder fileNameSb = buildFileNameString();

        fileNameSb.append(ApicConstants.ZIP_FILE_EXT);
        CDFXExportInputWizardPage.this.outputZipFileName = fileNameSb.toString();

        fileDialog.setFileName(CDFXExportInputWizardPage.this.outputZipFileName);
        if (null != fileDialog.open()) {

          CDFXExportInputWizardPage.this.directoryPath = fileDialog.getFilterPath();
          if (!CommonUtils.isEmptyString(CDFXExportInputWizardPage.this.directoryPath)) {
            if (fileDialog.getFileName().endsWith(ApicConstants.ZIP_FILE_EXT)) {
              CDFXExportInputWizardPage.this.outputZipFileName = fileDialog.getFileName();
            }
            else {
              CDFXExportInputWizardPage.this.outputZipFileName = fileDialog.getFileName() + ApicConstants.ZIP_FILE_EXT;
            }
            CDFXExportInputWizardPage.this.zipExportPathText.setText(
                CDFXExportInputWizardPage.this.directoryPath + "\\" + CDFXExportInputWizardPage.this.outputZipFileName);
            CDFXExportInputWizardPage.this.cdfxExportWizardData
                .setOutputDirecPath(CDFXExportInputWizardPage.this.directoryPath);

            CDFXExportInputWizardPage.this.outputFileNameStr =
                CDFXExportInputWizardPage.this.outputZipFileName.replace(".zip", "");
            // When 100% cdfx context menu is chosen from the workpackage under the A2L structure view and if the use
            // does not change the variant in cdfx export wizard, then the default variant would be assigned to the
            // selected pidc variant
            setSelectedVariant();
            getWizard().getContainer().updateButtons();
          }
        }
      }
    });
    browseBtn.setImage(this.browseImage);
    browseBtn.setToolTipText("Select zip export filepath");
  }

  /**
   * @return
   */
  private StringBuilder buildFileNameString() {
    StringBuilder fileNameSb = new StringBuilder();
    fileNameSb.append(CDFXExportInputWizardPage.this.cdfxExportWizardData.getPidc().getName())
        .append(CommonUIConstants.UNDERSCORE);
    fileNameSb.append(CDFXExportInputWizardPage.this.cdfxExportWizardData.getPidcA2l().getSdomPverVarName())
        .append(CommonUIConstants.UNDERSCORE);
    fileNameSb.append(DateFormat.formatDateToString(new Date(), DateFormat.DATE_FORMAT_19));
    return fileNameSb;
  }

  /**
   * create variant controls
   */
  private void createScopeControls(final Composite maincomposite) {
    // Create the new Group for the Radio Buttons
    final Group wpSelGroup = new Group(maincomposite, SWT.NONE);
    wpSelGroup.setLayout(new GridLayout());
    GridData gridDataWp = new GridData(SWT.FILL, SWT.FILL, true, false);
    wpSelGroup.setLayoutData(gridDataWp);
    // Icdm-715 Source for the Lab fun file
    wpSelGroup.setText("Scope : ");
    wpSelGroup.setBackground(this.whiteColor);
    final Composite radioRowComposite = createRadioRowComp(wpSelGroup);
    createRadioControls(radioRowComposite);

    radioRowComposite.layout();
    // Composite to hold workpage label,text and browse button
    final Composite firstRowComposite = createFirstRowComp(wpSelGroup);

    createWrkPackageControl(firstRowComposite);

    // create one file per work package checkbox selection
    createOneFilePerWpSelection(wpSelGroup);

    // If wp is selected under A2L structure view,set default radio button to Work Package
    if (CDFXExportInputWizardPage.this.cdfxExportWizardData.isContainPreSelectedWP()) {
      setDefaultRadioBtnToWP();
    }
  }

  /**
   * @param wpSelGrp
   */
  private void createOneFilePerWpSelection(final Group wpSelGrp) {
    // One File Per Work Package selection
    this.oneFilePerWPChkBox = new Button(wpSelGrp, SWT.CHECK);
    final GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
    this.oneFilePerWPChkBox.setBackground(this.whiteColor);
    this.oneFilePerWPChkBox.setLayoutData(gridData);
    this.oneFilePerWPChkBox.setText("One File Per Work Package");
    this.oneFilePerWPChkBox.setSelection(false);
    // add the listener
    this.oneFilePerWPChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        getWizard().getContainer().updateButtons();
      }
    });
  }

  /**
   *
   */
  private void setDefaultRadioBtnToWP() {
    this.workPackageRadio.setSelection(CDFXExportInputWizardPage.this.cdfxExportWizardData.isContainPreSelectedWP());
    CDFXExportInputWizardPage.this.workPkgBrowse
        .setEnabled(CDFXExportInputWizardPage.this.workPackageRadio.getSelection());
    java.util.List<WpRespModel> selectedWpResp = CDFXExportInputWizardPage.this.cdfxExportWizardData.getWpRespModels();
    selectedWpResp.forEach(wpRespModel -> {
      if (CommonUtils.isNotNull(wpRespModel.getA2lResponsibility())) {
        StringBuilder wpRespStrBuild = new StringBuilder();
        wpRespStrBuild.append(wpRespModel.getWpName());
        wpRespStrBuild.append(CDRConstants.OPEN_BRACES);
        wpRespStrBuild.append(wpRespModel.getA2lResponsibility().getName());
        wpRespStrBuild.append(CDRConstants.CLOSE_BRACES);
        CDFXExportInputWizardPage.this.wrkPkgList.removeAll();
        CDFXExportInputWizardPage.this.wrkPkgList.add(wpRespStrBuild.toString());
      }
    });
  }

  /**
   * @param firstRowComposite
   */
  private void createWrkPackageControl(final Composite firstRowComposite) {
    final Label lblWrkPkg = getFormToolkit().createLabel(firstRowComposite, "Work Package(s): ");
    lblWrkPkg.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    this.wrkPkgList = new List(firstRowComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);

    final GridData data = new GridData(GridData.FILL_HORIZONTAL);
    this.wrkPkgList.setLayoutData(data);
    this.wrkPkgList.setEnabled(false);
    // composite for aligining the selection and delete buttons
    final Composite wrkPkgBtComp = new Composite(firstRowComposite, SWT.NONE);
    final GridLayout layoutWrkPkgComp = new GridLayout();
    layoutWrkPkgComp.numColumns = 1;
    layoutWrkPkgComp.makeColumnsEqualWidth = false;
    layoutWrkPkgComp.marginWidth = 0;
    layoutWrkPkgComp.marginTop = 0;
    wrkPkgBtComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    wrkPkgBtComp.setBackground(this.whiteColor);
    wrkPkgBtComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    wrkPkgBtComp.setLayout(layoutWrkPkgComp);
    // add and delete questionnnaire buttons

    this.workPkgBrowse = getFormToolkit().createButton(wrkPkgBtComp, "", SWT.PUSH);
    this.workPkgBrowse.setImage(this.browseImage);
    this.workPkgBrowse.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.workPkgBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    this.workPkgBrowse.setEnabled(false);
    this.workPkgBrowse.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), () -> {
          if ((CDFXExportInputWizardPage.this.cdfxExportWizardData.variantsAvailable()) &&
              (CDFXExportInputWizardPage.this.cdfxExportWizardData.getPidcVariant() == null) &&
              (CommonUtils.isNullOrEmpty(CDFXExportInputWizardPage.this.cdfxExportWizardData.getPidcVariants()))) {
            CDMLogger.getInstance().warnDialog("Please select a variant !", Activator.PLUGIN_ID);
            return;
          }

          final ReviewWrkPkgSelectionDialog wrkPkgSelDialog =
              new ReviewWrkPkgSelectionDialog(Display.getCurrent().getActiveShell(),
                  CDFXExportInputWizardPage.this.cdfxExportWizardData.getA2lFileInfo(),
                  CDFXExportInputWizardPage.this.wizard.getWorkPackageRespMap());
          wrkPkgSelDialog.open();
          final java.util.List selectedElement = wrkPkgSelDialog.getSelectedElement();
          if ((selectedElement != null) && (selectedElement.get(0) instanceof WpRespModel)) {
            CDFXExportInputWizardPage.this.wrkPkgList.removeAll();
            java.util.List<WpRespModel> selectedWpResp = selectedElement;
            selectedWpResp.forEach(wpRespModel -> {
              StringBuilder wpRespStrBuild = new StringBuilder();
              wpRespStrBuild.append(wpRespModel.getWpName());
              wpRespStrBuild.append(CDRConstants.OPEN_BRACES);
              wpRespStrBuild.append(wpRespModel.getA2lResponsibility().getName());
              wpRespStrBuild.append(CDRConstants.CLOSE_BRACES);
              CDFXExportInputWizardPage.this.wrkPkgList.add(wpRespStrBuild.toString());

            });
            CDFXExportInputWizardPage.this.cdfxExportWizardData.setWpRespModels(selectedWpResp);
            getWizard().getContainer().updateButtons();
          }

        });
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA

      }
    });
  }


  /**
   * @param radioComposite as input
   */
  private Composite createRadioRowComp(final Composite radioComposite) {
    final Composite radioRowComposite = new Composite(radioComposite, SWT.NONE);
    radioRowComposite.setLayout(new GridLayout(4, true));

    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
    radioRowComposite.setLayoutData(gridData);
    radioRowComposite.setBackground(this.whiteColor);
    return radioRowComposite;
  }

  /**
   * @param radioFirstRowComposite as input
   * @return
   */
  private Composite createFirstRowComp(final Composite radioFirstRowComposite) {
    final Composite firstRowComposite = new Composite(radioFirstRowComposite, SWT.NONE);
    firstRowComposite.setLayout(new GridLayout(3, false));
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
    firstRowComposite.setLayoutData(gridData);
    firstRowComposite.setBackground(this.whiteColor);
    return firstRowComposite;
  }

  /**
   * @param firstRowComposite // Icdm-715 UI Changes to Wizard
   */
  private void createRadioControls(final Composite firstRowComposite) {
    // ICDM-2032
    this.boschRadio = new Button(firstRowComposite, SWT.RADIO);
    this.boschRadio.setText("Bosch");
    this.boschRadio.setSelection(false);
    this.boschRadio.setBackground(this.whiteColor);
    this.boschRadio.setSelection(true);
    if (CDFXExportInputWizardPage.this.cdfxExportWizardData.isContainPreSelectedWP()) {
      this.boschRadio.setSelection(false);
    }
    this.boschRadio.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        setScopeRadioSelectionStatus();
        CDFXExportInputWizardPage.this.wrkPkgList.removeAll();
        CDFXExportInputWizardPage.this.cdfxExportWizardData.setWpRespModels(new ArrayList<>());
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });

    this.customerRadio = new Button(firstRowComposite, SWT.RADIO);
    this.customerRadio.setText("Customer");
    this.customerRadio.setSelection(false);
    this.customerRadio.setBackground(this.whiteColor);
    this.customerRadio.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        setScopeRadioSelectionStatus();
        CDFXExportInputWizardPage.this.wrkPkgList.removeAll();
        CDFXExportInputWizardPage.this.cdfxExportWizardData.setWpRespModels(new ArrayList<>());
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });

    this.othersRadio = new Button(firstRowComposite, SWT.RADIO);
    this.othersRadio.setText("Others");
    this.othersRadio.setSelection(false);
    this.othersRadio.setBackground(this.whiteColor);
    this.othersRadio.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        setScopeRadioSelectionStatus();
        CDFXExportInputWizardPage.this.wrkPkgList.removeAll();
        CDFXExportInputWizardPage.this.cdfxExportWizardData.setWpRespModels(new ArrayList<>());
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });

    this.workPackageRadio = new Button(firstRowComposite, SWT.RADIO);
    this.workPackageRadio.setText("Work Package");
    this.workPackageRadio.setSelection(false);
    this.workPackageRadio.setBackground(this.whiteColor);

    this.workPackageRadio.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        CDFXExportInputWizardPage.this.wrkPkgList
            .setEnabled(CDFXExportInputWizardPage.this.workPackageRadio.getSelection());
        CDFXExportInputWizardPage.this.workPkgBrowse
            .setEnabled(CDFXExportInputWizardPage.this.workPackageRadio.getSelection());
        setScopeRadioSelectionStatus();
        getWizard().getContainer().updateButtons();

        if (CDFXExportInputWizardPage.this.workPkgBrowse.getEnabled() &&
            !CDFXExportInputWizardPage.this.isVariantChanged) {
          java.util.List<WpRespModel> selectedWpResp =
              CDFXExportInputWizardPage.this.cdfxExportWizardData.getWpRespModels();

          selectedWpResp.forEach(wpRespModel -> {
            if (CommonUtils.isNotNull(wpRespModel.getA2lResponsibility())) {
              StringBuilder wpRespStrBuild = new StringBuilder();
              wpRespStrBuild.append(wpRespModel.getWpName());
              wpRespStrBuild.append(CDRConstants.OPEN_BRACES);
              wpRespStrBuild.append(wpRespModel.getA2lResponsibility().getName());
              wpRespStrBuild.append(CDRConstants.CLOSE_BRACES);
              CDFXExportInputWizardPage.this.wrkPkgList.removeAll();
              CDFXExportInputWizardPage.this.wrkPkgList.add(wpRespStrBuild.toString());
            }
          });
        }

      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });
  }

  private void setScopeRadioSelectionStatus() {
    CDFXExportInputWizardPage.this.cdfxExportWizardData
        .setBoschScope(CDFXExportInputWizardPage.this.boschRadio.getSelection());
    CDFXExportInputWizardPage.this.cdfxExportWizardData
        .setCustomerScope(CDFXExportInputWizardPage.this.customerRadio.getSelection());
    CDFXExportInputWizardPage.this.cdfxExportWizardData
        .setOtherScope(CDFXExportInputWizardPage.this.othersRadio.getSelection());
    CDFXExportInputWizardPage.this.cdfxExportWizardData
        .setWpRespScope(CDFXExportInputWizardPage.this.workPackageRadio.getSelection());
  }

  /**
   * create variant controls
   */
  private void createVariantControls(final Composite maincomposite) {
    // composite for displaying radio buttons
    Composite variantComposite = getFormToolkit().createComposite(maincomposite);
    GridLayout gridLayoutForComp = new GridLayout();
    gridLayoutForComp.numColumns = 3;
    GridData variantGridData = new GridData();
    variantGridData.grabExcessHorizontalSpace = true;
    variantGridData.horizontalAlignment = GridData.FILL;
    variantComposite.setLayout(gridLayoutForComp);
    variantComposite.setLayoutData(variantGridData);

    Label varLabel = getFormToolkit().createLabel(variantComposite, "Variant : ");
    varLabel.setEnabled(false);

    final Text varText = getFormToolkit().createText(variantComposite, null, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    varText.setLayoutData(txtGridData);
    varText.setEnabled(false);

    Button btnVariant = new Button(variantComposite, SWT.NONE);
    btnVariant.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));// image for browse
                                                                                                // button
    setSelectedVariant();
    btnVariant.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        // open dialog for selecting variant
        final PidcVariantSelectionDialog variantSelDialog =
            new PidcVariantSelectionDialog(Display.getCurrent().getActiveShell(),
                CDFXExportInputWizardPage.this.cdfxExportWizardData.getPidcA2l().getId());
        variantSelDialog.setMultiSel(true);
        variantSelDialog.open();

        final Set<PidcVariant> selectedVariants = variantSelDialog.getSelectedPidcVariants();// variant selected from
                                                                                             // dailog
        PidcVariant selectedVariant = variantSelDialog.getSelectedVariant();

        if (CommonUtils.isNullOrEmpty(selectedVariants) && (CommonUtils.isNotNull(selectedVariant))) {
          varText.setText(selectedVariant.getName());
          checkIsVariantChanged(selectedVariant.getName());
          // for single variant the variant selection field is selectedVariant
          CDFXExportInputWizardPage.this.cdfxExportWizardData.setPidcVariant(selectedVariant);
        }
        else {
          StringJoiner variantJoiner = new StringJoiner(", ");
          selectedVariants.forEach(pidcvariant -> {
            StringBuilder varSb = new StringBuilder();
            varSb.append(pidcvariant.getName());
            variantJoiner.add(varSb.toString());
          });
          varText.setText(variantJoiner.toString());
          checkIsVariantChanged(variantJoiner.toString());
          // for multiple variants the variant selection field is selectedVariants
          CDFXExportInputWizardPage.this.cdfxExportWizardData.setPidcVariants(selectedVariants);
        }
        getWizard().getContainer().updateButtons();
      }

      /**
       * @param string
       */
      private void checkIsVariantChanged(final String variantName) {
        if ((CommonUtils.isNotNull(CDFXExportInputWizardPage.this.cdfxExportWizardData.getDefaultPidcVariant())) &&
            !CommonUtils.isEqualIgnoreCase(variantName,
                CDFXExportInputWizardPage.this.cdfxExportWizardData.getDefaultPidcVariant().getName())) {
          CDFXExportInputWizardPage.this.isVariantChanged = true;
          CDFXExportInputWizardPage.this.wrkPkgList.removeAll();
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
        // NA
      }
    });

    btnVariant.setEnabled(false);
    if (this.cdfxExportWizardData.variantsAvailable()) {
      // disable the button and the text field if the variants are not available
      varLabel.setEnabled(true);
      btnVariant.setEnabled(true);
      varText.setEnabled(true);

      ControlDecoration varTextDec = new ControlDecoration(varText, SWT.LEFT | SWT.TOP);
      new Decorators().showReqdDecoration(varTextDec, "This field is mandatory.");

      varText
          .addModifyListener(evnt -> Validator.getInstance().validateNDecorate(varTextDec, varText, null, null, true));
    }

    if (null != this.cdfxExportWizardData.getDefaultPidcVariant()) {
      varText.setText(this.cdfxExportWizardData.getDefaultPidcVariant().getName());
    }
  }


  /**
   * @return the formToolkit
   */
  public FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      // create the form toolkit if its not available
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  /**
   * @param formToolkit the formToolkit to set
   */
  public void setFormToolkit(final FormToolkit formToolkit) {
    this.formToolkit = formToolkit;
  }


  /**
   * @return the composite
   */
  public Composite getComposite() {
    return this.composite;
  }


  /**
   * @param composite the composite to set
   */
  public void setComposite(final Composite composite) {
    this.composite = composite;
  }


  /**
   * @return the section
   */
  public Section getSection() {
    return this.section;
  }


  /**
   * @param section the section to set
   */
  public void setSection(final Section section) {
    this.section = section;
  }


  /**
   * @return the form
   */
  public Form getForm() {
    return this.form;
  }


  /**
   * @param form the form to set
   */
  public void setForm(final Form form) {
    this.form = form;
  }


  /**
   * @return the cdfxExportWizardData
   */
  public CDFXExportWizardData getCdfxExportWizardData() {
    return this.cdfxExportWizardData;
  }

  @Override
  public boolean canFlipToNextPage() {
    return this.wizard.validateFields() && this.readinessChkBox.getSelection() &&
        (null != this.cdfxExportWizardData.getOutputDirecPath());
  }


  /**
   *
   */
  public void exportPressed() {


    ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Exporting CDFX report ...", 100);
        monitor.worked(20);
        try {
          Thread.sleep(5000);
          Display display = PlatformUI.getWorkbench().getDisplay();
          // runnable method call
          display.syncExec(() -> {
            createInputModel();
            try {
              fillCDFxReportData();

              // Set note message for statistics page
              if (this.cdfxExportInputModel.getVariantsList().size() > 1) {
                CDFXExportInputWizardPage.this.wizard.getReviewStatisticsWizardPage().setMessage(
                    "NOTE: Statistics details of the selected multiple variants, can be referred in exported json files.",
                    IMessageProvider.INFORMATION);
              }
              // set page complete to true ,in order to navigate to statistics page,if after getting exception export is
              // performed again
              CDFXExportInputWizardPage.this.wizard.getCdfxExportInputWizardPage().setPageComplete(true);
            }
            catch (ApicWebServiceException exp) {
              // Dispose Progrees dialog and show error in new Dialog
              display.getActiveShell().dispose();
              CDFXExportInputWizardPage.this.wizard.getCdfxExportInputWizardPage().setPageComplete(false);
              CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
            }
          });
        }
        catch (Exception e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
        monitor.done();
      });

    }
    catch (Exception e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }


  }


  /**
   * @param outputFileName
   * @throws ApicWebServiceException
   */
  private void fillCDFxReportData() throws ApicWebServiceException {

    CdfxExportOutput cdfxExportOutput;
    cdfxExportOutput =
        new CdfxExportServiceClient().exportCdfx(this.cdfxExportInputModel, this.directoryPath, this.outputZipFileName);
    this.cdfxExportWizardData.setCdfxExportOutputModel(cdfxExportOutput);
    this.cdfxExportWizardData.getCdfxExportOutput().setExportFilePath(this.zipExportPathText.getText());
    CDMLogger.getInstance().info(
        "CDFx exported successfully to file  " + this.cdfxExportWizardData.getCdfxExportOutput().getExportFilePath(),
        Activator.PLUGIN_ID);
    this.wizard.getReviewStatisticsWizardPage().fillDetails();
  }


  /**
   *
   */
  private void setSelectedVariant() {
    if (CommonUtils.isNull(CDFXExportInputWizardPage.this.cdfxExportWizardData.getPidcVariant()) &&
        CommonUtils.isNotNull(CDFXExportInputWizardPage.this.cdfxExportWizardData.getDefaultPidcVariant())) {
      CDFXExportInputWizardPage.this.cdfxExportWizardData
          .setPidcVariant(CDFXExportInputWizardPage.this.cdfxExportWizardData.getDefaultPidcVariant());
    }
  }

  /**
   * @return the workPackageRadio
   */
  public Button getWorkPackageRadio() {
    return this.workPackageRadio;
  }

  /**
   * @return the wrkPkgList
   */
  public List getWrkPkgList() {
    return this.wrkPkgList;
  }
}
