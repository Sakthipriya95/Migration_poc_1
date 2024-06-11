/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.a2ldata.Activator;
import com.bosch.caltool.icdm.client.bo.caldataimport.CalDataImporterHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizard;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * First page od the caldata import wizard. Imports the caldata file
 *
 * @author bru2cob
 */
public class CalDataFileImpWizardPage<D extends IParameterAttribute, P extends IParameter> extends WizardPage {

  /**
   * Main comp columns
   */
  private static final int COMP_COLS = 3;
  /**
   * textbox which hols the selected file name
   */
  private List filePathText;
  /**
   * Hold the name of the file which is selected
   */
  private String fileSelected;
  /**
   * instance of wizard
   */
  private CalDataFileImportWizard wizard;

  /**
   * Get the eclipse preference store
   */
  private final transient IPreferenceStore preference = PlatformUI.getPreferenceStore();
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Text
   */
  private Text baseCommentText;
  /**
   * maaturity level combo
   */
  private Combo maturityCombo;
  /**
   * check button for exact match
   */
  private Button btnParamRefValMatch;

  /**
   * radio button for ready for series
   */
  private Button btnReadySeries;

  /**
   * maturity level combo width
   */
  private static final int MAT_COMBO_WIDTH = 190;

  /**
   * maturity value combo height
   */
  private static final int MAT_COMBO_HEIGHT = 150;

  /**
   * list of selected file names
   */
  private final Set<String> fileNames = new HashSet<>();
  /**
   * button to over take values
   */
  private Button btnOverTakeValues;
  private DropFileListener calDataDropFileListener;


  /**
   * @param pageName page name
   */
  public CalDataFileImpWizardPage(final String pageName) {
    super(pageName);
    // set title
    setTitle("Import Calibration Data File");
    // set description
    setDescription("Please select the file to import\n(Supported file formats are CDFX, DCM, XML, XLS, XLXS)");
    // disable next btn intially
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {

    this.wizard = (CalDataFileImportWizard) getWizard();

    // create scrooled composite
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

    // Create project comp
    Composite projectGroup = new Composite(scrollComp, SWT.NONE);
    projectGroup.setLayout(new GridLayout());
    projectGroup.setLayoutData(GridDataUtil.getInstance().getGridData());

    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);


    // section for file selection
    createSectionForFileSelection(projectGroup);
    // section for default values
    createSectionForDefaultValues(projectGroup);
    // set layout
    projectGroup.layout(true);

    scrollComp.setContent(projectGroup);
    scrollComp.setMinSize(projectGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    // set control to composite
    setControl(scrollComp);

    //resized the dialog with wizard size
    getShell().setSize(parent.getSize());
  }

  /**
   * ICDM-1811
   *
   * @param projectGroup Composite
   */
  private void createSectionForDefaultValues(final Composite projectGroup) {
    Section sectionDefaultValues = SectionUtil.getInstance().createSection(projectGroup, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Default Values", false);

    // set layout data for section
    GridData sectionGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
    sectionDefaultValues.setLayoutData(sectionGridData);

    Form formDefaultValues = getFormToolkit().createForm(sectionDefaultValues);
    GridLayout gridLayout = new GridLayout();
    // dividing into 2 columns
    gridLayout.numColumns = 2;
    gridLayout.verticalSpacing = 20;
    formDefaultValues.getBody().setLayout(gridLayout);

    // create label
    new Label(formDefaultValues.getBody(), SWT.NONE).setText("Base Comment ");

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.CENTER;
    // base comment for import
    this.baseCommentText = TextUtil.getInstance().createText(formDefaultValues.getBody(), gridData, true, "");
    this.baseCommentText.setToolTipText("The entered comment is loaded as prefix for the loaded comment.");
    // default maturity level
    // create label
    new Label(formDefaultValues.getBody(), SWT.NONE).setText("Maturity Level ");
    this.maturityCombo = new Combo(formDefaultValues.getBody(), SWT.READ_ONLY);
    this.maturityCombo.setLayout(new GridLayout());
    this.maturityCombo.setLayoutData(new GridData(MAT_COMBO_WIDTH, MAT_COMBO_HEIGHT));
    // add the values to combo
    this.maturityCombo.add(RuleMaturityLevel.NONE.getICDMMaturityLevel());
    this.maturityCombo.add(RuleMaturityLevel.START.getICDMMaturityLevel());
    this.maturityCombo.add(RuleMaturityLevel.STANDARD.getICDMMaturityLevel());
    this.maturityCombo.add(RuleMaturityLevel.FIXED.getICDMMaturityLevel());
    // select the START as default one
    this.maturityCombo.select(1);
    this.maturityCombo.setToolTipText(
        "The default maturity level which is initially used for all the imported rules.Can be modified for each rule seperately in the next page of the dialog.");

    // create label
    String labelName = "";

    try {
      labelName = new CommonDataBO().getMessage("CDR_RULE", "READY_FOR_SERIES", "");
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);

    }

    new Label(formDefaultValues.getBody(), SWT.NONE).setText(labelName);

    this.btnReadySeries = new Button(formDefaultValues.getBody(), SWT.CHECK);

    // default value for exact match
    // create label
    new Label(formDefaultValues.getBody(), SWT.NONE).setText("Exact Match");
    this.btnParamRefValMatch = new Button(formDefaultValues.getBody(), SWT.CHECK);
    this.btnParamRefValMatch
        .setToolTipText("The default exact match which is initially used for all the imported rules");

    // ICDM-1927
    if (this.wizard.getWizardData().getImportObject() instanceof RuleSet) {
      this.btnParamRefValMatch.setSelection(true);
    }


    // instance check removed now available for both Functions and Rule sets

    new Label(formDefaultValues.getBody(), SWT.NONE).setText("Take Values from Import file");
    this.btnOverTakeValues = new Button(formDefaultValues.getBody(), SWT.CHECK);
    this.btnOverTakeValues.setToolTipText("Option allows Users to overtake values from the imported file");


    sectionDefaultValues.setClient(formDefaultValues);

  }

  /**
   * @param projectGroup Composite
   */
  private void createSectionForFileSelection(final Composite projectGroup) {
    Section sectionFileSelection = SectionUtil.getInstance().createSection(projectGroup, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "File Selection", false);

    // set layout data for section
    GridData sectionGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
    sectionFileSelection.setLayoutData(sectionGridData);

    // create form
    Form formFileSelection = getFormToolkit().createForm(sectionFileSelection);
    GridLayout layout = new GridLayout();
    layout.numColumns = COMP_COLS;

    // set layout
    formFileSelection.getBody().setLayout(layout);
    formFileSelection.getBody().setLayoutData(new GridData());
    sectionFileSelection.setLayout(new GridLayout());

    // create label
    new Label(formFileSelection.getBody(), SWT.NONE).setText("Import files: ");
    // text box to hold the file name
    this.filePathText = new List(formFileSelection.getBody(), SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    GridData directoryPathData = GridDataUtil.getInstance().getGridData();
    directoryPathData.verticalSpan = 18;
    this.filePathText.setLayoutData(directoryPathData);

    // Drag drop caldata file
    this.calDataDropFileListener = new DropFileListener(this.filePathText, CommonUIConstants.FILE_EXTENSIONS_IMPORT);
    this.calDataDropFileListener.addDropFileListener(false);
    this.calDataDropFileListener.setEditable(true);
    this.filePathText.addMouseMoveListener(new MouseMoveListener() {

      @Override
      public void mouseMove(final MouseEvent arg0) {
        String dropFilePath = CalDataFileImpWizardPage.this.calDataDropFileListener.getDropFilePath();
        if (null != dropFilePath) {
          CalDataFileImpWizardPage.this.calDataDropFileListener.setDropFilePath(null);
          if (!chkIfThereIsExlFileAlreadyAdded()) {
            String[] fileExtn = dropFilePath.split("\\.");
            if (CalDataFileImpWizardPage.this.fileNames.contains(dropFilePath)) {
              // if the file name is already added in the list
              showErrorDialogForAlreadyAddedFiles(Arrays.asList(dropFilePath), null);
              CalDataFileImpWizardPage.this.filePathText.remove(dropFilePath);
            }
            else if ((CalDataFileImpWizardPage.this.fileNames.size() > 1) && ((null != fileExtn[1]) &&
                ((CommonUtils.isEqual(fileExtn[1], "xls")) || (CommonUtils.isEqual(fileExtn[1], "xlsx"))))) {
              // if the file is excel file
              showErrorDialogForAlreadyAddedFiles(null, Arrays.asList(dropFilePath));
              CalDataFileImpWizardPage.this.filePathText.remove(dropFilePath);
            }
            else {
              // in other cases add it to the list
              CalDataFileImpWizardPage.this.fileNames.add(dropFilePath);
            }
          }
          else {
            CalDataFileImpWizardPage.this.filePathText.remove(dropFilePath);
          }
        }
        validateNextButton();
      }
    });

    createBrowseAndAddFileBtn(formFileSelection.getBody());

    LabelUtil.getInstance().createEmptyLabel(formFileSelection.getBody());

    createDeleteButton(formFileSelection.getBody());
    sectionFileSelection.setClient(formFileSelection);
  }

  /**
   * ICDM-2352 create Delete button
   *
   * @param body
   */
  private void createDeleteButton(final Composite body) {
    // create file browse btn
    final Button button = new Button(body, SWT.PUSH);
    button.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    button.setToolTipText("Delete files");
    button.addSelectionListener(new SelectionAdapter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (CalDataFileImpWizardPage.this.filePathText.getSelectionCount() > 0) {

          int[] selectionIndices = CalDataFileImpWizardPage.this.filePathText.getSelectionIndices();
          for (int selectedIndex : selectionIndices) {
            CalDataFileImpWizardPage.this.fileNames
                .remove(CalDataFileImpWizardPage.this.filePathText.getItem(selectedIndex));
          }
          CalDataFileImpWizardPage.this.filePathText.remove(selectionIndices);
          validateNextButton();
        }
      }
    });
    // set button layout data
    setButtonLayoutData(button);

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
   * file browse btn
   *
   * @param projectGroup
   */
  private void createBrowseAndAddFileBtn(final Composite projectGroup) {
    // create file browse btn
    final Button button = new Button(projectGroup, SWT.PUSH);
    button.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    button.setToolTipText("Add files");
    button.addSelectionListener(new SelectionAdapter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
        fileDialog.setText("Import CalData file");
        // get the previously sorted extension from preference store
        CommonUtils.swapArrayElement(CommonUIConstants.FILE_EXTENSIONS_IMPORT,
            CalDataFileImpWizardPage.this.preference.getString(CommonUIConstants.IMPORT_CALDATA_FILE_EXTN), 0);

        CommonUtils.swapArrayElement(CommonUIConstants.FILE_NAMES_RULE_IMPORT,
            CalDataFileImpWizardPage.this.preference.getString(CommonUIConstants.IMPORT_CALDATA_FILE_NAME), 0);
        // set file dialog extensions
        fileDialog.setFilterExtensions(CommonUIConstants.FILE_EXTENSIONS_IMPORT);
        fileDialog.setFilterNames(CommonUIConstants.FILE_NAMES_RULE_IMPORT);
        fileDialog.open();

        String filterPath = fileDialog.getFilterPath();
        String[] nameList = fileDialog.getFileNames();
        java.util.List<String> nonDuplicateList = new ArrayList<String>();
        java.util.List<String> duplicateFileList = new ArrayList<>();
        java.util.List<String> plausibelFileList = new ArrayList<>();

        if (!chkIfThereIsExlFileAlreadyAdded()) {

          for (String selectedFile : nameList) {
            // get the file extension
            String[] fileExtn = selectedFile.split("\\.");
            // get the full file name
            String fullFileName = filterPath + "\\" + selectedFile;

            if (CalDataFileImpWizardPage.this.fileNames.contains(fullFileName)) {
              // if the file name is already added in the list
              duplicateFileList.add(fullFileName);
            }
            else if (((CalDataFileImpWizardPage.this.fileNames.size() > 1) || (nameList.length > 1)) &&
                ((null != fileExtn[1]) &&
                    ((CommonUtils.isEqual(fileExtn[1], "xls")) || (CommonUtils.isEqual(fileExtn[1], "xlsx"))))) {
              // if the file is excel file
              plausibelFileList.add(fullFileName);
            }
            else {
              // in other cases add it to the list
              CalDataFileImpWizardPage.this.fileNames.add(fullFileName);
              nonDuplicateList.add(fullFileName);
            }

          }

          showErrorDialogForAlreadyAddedFiles(duplicateFileList, plausibelFileList);
        }
        // store preferences
        CalDataFileImpWizardPage.this.preference.setValue(CommonUIConstants.IMPORT_CALDATA_FILE_EXTN,
            CommonUIConstants.FILE_EXTENSIONS_IMPORT[fileDialog.getFilterIndex()]);

        CalDataFileImpWizardPage.this.preference.setValue(CommonUIConstants.IMPORT_CALDATA_FILE_NAME,
            CommonUIConstants.FILE_NAMES_RULE_IMPORT[fileDialog.getFilterIndex()]);

        // disable finish button when user browses to another file
        WizardPage compareWizardPage = (WizardPage) CalDataFileImpWizardPage.this.getWizard().getPages()[1];
        compareWizardPage.setPageComplete(false);

        // ICDM-2352 add the file names to the list
        for (String fileName : nonDuplicateList) {
          CalDataFileImpWizardPage.this.filePathText.add(fileName);
        }

        validateNextButton();


      }


    });
    // set button layout data
    setButtonLayoutData(button);
  }

  /**
   * ICDM-2352
   *
   * @param duplicateFileList
   * @param plausibelFileList
   */
  private void showErrorDialogForAlreadyAddedFiles(final java.util.List<String> duplicateFileList,
      final java.util.List<String> plausibelFileList) {
    if (CommonUtils.isNotEmpty(duplicateFileList)) {
      StringBuilder message = new StringBuilder();
      message.append("The following files are already added to the import. \n");
      for (String fileName : duplicateFileList) {
        message.append(fileName).append("\n");
      }
      MessageDialogUtils.getErrorMessageDialog("File Already Existing", message.toString());
    }

    if (CommonUtils.isNotEmpty(plausibelFileList)) {
      StringBuilder message = new StringBuilder();
      message.append("The Plausibel files cannot be included for multiple files import. \n");
      for (String fileName : plausibelFileList) {
        message.append(fileName).append("\n");
      }
      MessageDialogUtils.getErrorMessageDialog("Plausibel files cannot be included", message.toString());
    }

  }

  /**
   * enable disable next button
   *
   * @return
   */
  private boolean validateNextButton() {
    boolean enableNext;
    // check if the list has atleast one item
    if (CalDataFileImpWizardPage.this.filePathText.getItemCount() > 0) {
      getContainer().updateButtons();
      CalDataFileImpWizardPage.this.setPageComplete(true);
      enableNext = true;
    }
    else {
      getContainer().updateButtons();
      CalDataFileImpWizardPage.this.setPageComplete(false);
      enableNext = false;
    }

    return enableNext;
  }

  /**
   * @return the filePathText
   */
  public List getFilePathText() {
    return this.filePathText;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPageComplete() {
    return getWizard().getPages()[1].isPageComplete();
  }

  /**
   *
   */
  public void nextPressed() {

    try {
      // run the progress monitor inside the container
      getContainer().run(true, true, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) {
          monitor.beginTask("Importing rules from file..", 100);
          monitor.worked(10);
          Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
              CalDataFileImpWizardPage.this.wizard.getAttrValWizardPage().getTableSec().checkAllValsSet();

              CalDataFileImpWizardPage.this.wizard.getWizardData()
                  .setFileSelected(CalDataFileImpWizardPage.this.fileNames);

              CalDataImporterHandler calDataImportHandler =
                  CalDataFileImpWizardPage.this.wizard.getCalDataImportHandler();

              ParamCollection importObject = CalDataFileImpWizardPage.this.wizard.getWizardData().getImportObject();
              ParamCollectionDataProvider paramColDataProvider =
                  CalDataFileImpWizardPage.this.wizard.getWizardData().getParamColDataProvider();
              try {
                CalDataImportData calImportData =
                    calDataImportHandler.parseFile(CalDataFileImpWizardPage.this.fileNames,
                        importObject.getId().toString(), paramColDataProvider.getObjectTypeName(importObject),
                        CalDataFileImpWizardPage.this.wizard.getWizardData().getFuncVersion());

                CalDataFileImpWizardPage.this.wizard.setCalImportData(calImportData);


                if (CalDataFileImpWizardPage.this.wizard.getWizardData().isExceptionOccured()) {
                  return;
                }
                // store default values
                storeDefaultVal();
                // set the table input
                setDependencyTableInput(calImportData);
              }
              catch (ApicWebServiceException e) {
                CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
                CalDataFileImpWizardPage.this.wizard.getWizardData().setExceptionOccured(true);

              }


            }
          });
          if (CalDataFileImpWizardPage.this.wizard.getWizardData().isExceptionOccured()) {
            return;
          }
          monitor.worked(40);
          boolean skipAttrValPage = CalDataFileImpWizardPage.this.wizard.getWizardData().getAttrValMap().isEmpty();
          if (skipAttrValPage) {
            // if attr value page has to be skipped, load and import cal data
            CalDataFileImpWizardPage.this.wizard.getAttrValWizardPage().loadAndImportData(monitor);
          }
          monitor.done();
        }
      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().errorDialog(e.getCause().getMessage(), e, Activator.PLUGIN_ID);
      // set the excpetion occured flag to true
      this.wizard.getWizardData().setExceptionOccured(true);
    }
    if (CalDataFileImpWizardPage.this.wizard.getWizardData().getAttrValMap().isEmpty() &&
        !CalDataFileImpWizardPage.this.wizard.getWizardData().isExceptionOccured()) {
      // set the shell size
      CalDataFileImpWizardPage.this.wizard.getShell().setMaximized(true);
    }

  }

  /**
   * ICDM-1811 Storing default value
   */
  private void storeDefaultVal() {
    // store default base comment
    this.wizard.getWizardData().setDefaultBaseComment(this.baseCommentText.getText());
    // store default maturity level
    this.wizard.getWizardData()
        .setDefaultMaturityLevel(this.maturityCombo.getItem(this.maturityCombo.getSelectionIndex()));
    // store ready for series
    if (this.btnReadySeries.getSelection()) {
      this.wizard.getWizardData().setDefaultReadyForSer(READY_FOR_SERIES.YES);
    }
    else {
      this.wizard.getWizardData().setDefaultReadyForSer(READY_FOR_SERIES.NO);
    }
    // store default exact match flag
    this.wizard.getWizardData().setDefaultExactMatchFlag(this.btnParamRefValMatch.getSelection());
  }

  /**
   * set table input
   *
   * @param calImportData
   * @throws ApicWebServiceException
   */
  private void setDependencyTableInput(final CalDataImportData calImportData) throws ApicWebServiceException {

    // ICDM-2028
    // clearing the map before creating dependency attribute
    this.wizard.getWizardData().getAttrValMap().clear();

    ParamCollectionDataProvider paramColDataProvider = this.wizard.getWizardData().getParamColDataProvider();
    IParamRuleResponse paramRuleResponse = this.wizard.getWizardData().getParamRuleResponse();
    Map<String, java.util.List<D>> attrMap = new HashMap<String, java.util.List<D>>();
    Map<Long, Attribute> attrObjMap = new HashMap<Long, Attribute>();
    if (paramRuleResponse != null) {
      attrMap.putAll(paramRuleResponse.getAttrMap());
      attrObjMap.putAll(paramRuleResponse.getAttrObjMap());
      if (this.wizard.getWizardData().getImportObject() instanceof Function) {
        for (Function func : this.wizard.getCalImportData().getParamFuncObjMap().values()) {
          paramRuleResponse = this.wizard.getCalImportData().getFuncParamRespMap().get(func.getName());
          attrMap.putAll(paramRuleResponse.getAttrMap());
          attrObjMap.putAll(paramRuleResponse.getAttrObjMap());
        }
      }


      // in case of comppkg , the attr dependencies are mapped to comppkg
      // in case of rule set the attr depns are corresponding to the parameters in the rule set. Among these parameters
      // in
      // the rule set , only the ones which are there in the input file are considered
      for (Entry<String, java.util.List<D>> mappedObj : attrMap.entrySet()) {
        boolean flag = paramColDataProvider.isInputParamChecked(this.wizard.getWizardData().getImportObject()) &&
            (calImportData.getInputDataMap().keySet().contains(mappedObj.getKey()));
        if (flag || !paramColDataProvider.isInputParamChecked(this.wizard.getWizardData().getImportObject())) {
          for (IParameterAttribute paramAttr : mappedObj.getValue()) {
            Attribute attribute = attrObjMap.get(paramAttr.getAttrId());
            this.wizard.getWizardData().getAttrValMap().put(attribute, null);
          }
        }
      }
      // set table input
      this.wizard.getAttrValWizardPage().getAttrsTableViewer()
          .setInput(new TreeSet<>(this.wizard.getWizardData().getAttrValMap().keySet()));
    }
  }

  /**
   * @return
   */
  private boolean chkIfThereIsExlFileAlreadyAdded() {
    if (CalDataFileImpWizardPage.this.fileNames.size() == 1) {
      String[] fileNameExtn = (CalDataFileImpWizardPage.this.fileNames.iterator().next()).split("\\.");
      if (((null != fileNameExtn[1]) && (CommonUtils.isEqual(fileNameExtn[1], "xls"))) ||
          (CommonUtils.isEqual(fileNameExtn[1], "xlsx"))) {
        MessageDialogUtils.getErrorMessageDialog("Multiple File Import not possible",
            "Only one plausibel file is allowed for the import. Remove the plausibel file to import other types of file.");
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the file entered is exists , if so next button is enabled {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    boolean canProceed = CalDataFileImpWizardPage.this.filePathText.getItemCount() > 0;
    return canProceed;
  }

  /**
   * @return the fileSelected
   */
  public String getFileSelected() {
    return this.fileSelected;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getNextPage() {
    if (this.wizard.getWizardData().isExceptionOccured()) {
      return null;
    }
    return super.getNextPage();
  }


  /**
   * @return the btnOverTakeValues
   */
  public Button getBtnOverTakeValues() {
    return this.btnOverTakeValues;
  }
}
