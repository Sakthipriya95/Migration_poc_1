/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.sorter.EMRFileTableSorter;
import com.bosch.caltool.icdm.common.ui.listeners.GridTableEditingSupport;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.caltool.icdm.model.emr.EMRWizardData;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author mkl2cob
 */
public class EMRFileImportWizardPage extends WizardPage {

  /**
   * constant for showing number of rows
   */
  private static final int MIN_ROW_COUNT = 5;

  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * Composite
   */
  private Composite composite;
  /**
   * Section
   */
  private Section section;
  /**
   * Form
   */
  private Form form;
  /**
   * table viewer for files import
   */
  private GridTableViewer filesTabViewer;

  /**
   * EMRFileTableSorter
   */
  private EMRFileTableSorter codexFileTabSorter;

  /**
   * table input list
   */
  private final List<EMRWizardData> tableInput = new ArrayList<>();

  /**
   * delete action
   */
  private Action deleteLinkAction;

  /**
   * true if there is upload was done
   */
  private boolean uploadSuccess;

  /**
   * file name set
   */
  private final Set<String> fileNameSet = new HashSet<String>();

  /**
   * true if the variant assignment page is needed
   */
  private boolean isVarAssgnPageNeeded;
  /**
   * EMRFileUploadResponse
   */
  private EMRFileUploadResponse uploadResponse;

  /**
   * error message string
   */
  private String uploadErrorMsg;

  private boolean confirm;

  private Exception exceptionOnUpload;

  /**
   * @param pageName
   */
  protected EMRFileImportWizardPage(final String pageName) {
    super(pageName);
    setTitle("Import Codex Meaasurement Program Sheet");
    setDescription("Please select the files to import , add optional description and mention scope");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    final Composite workArea = new Composite(parent, SWT.NONE);
    createComposite(workArea);
    final GridLayout layout = new GridLayout();
    workArea.setLayout(layout);
    workArea.setLayoutData(GridDataUtil.getInstance().createGridData());
    workArea.pack();
    setControl(workArea);

  }

  /**
   * This method initializes composite
   *
   * @param workArea
   */
  private void createComposite(final Composite workArea) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(workArea);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");
    createForm();
    this.section.setClient(this.form);
  }


  /**
   * This method initializes form
   */
  private void createForm() {

    this.codexFileTabSorter = new EMRFileTableSorter();
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);

    GridData gridData = GridDataUtil.getInstance().getGridData();

    this.filesTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);

    this.filesTabViewer.setItemCount(MIN_ROW_COUNT);
    createToolBarAction();
    createTabColumns();

    this.filesTabViewer.setContentProvider(new IStructuredContentProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void dispose() {
        // Not applicable
      }


      /**
       * Sorted elements
       * <p>
       * {@inheritDoc}
       */
      @Override
      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof ArrayList<?>) {
          return ((ArrayList) inputElement).toArray();
        }
        return null;
      }

    });


    // Invoke TableViewer Column sorters
    this.filesTabViewer.setComparator(this.codexFileTabSorter);

  }

  /**
   * creates the columns of codex files table viewer
   */
  private void createTabColumns() {
    createFileNameColumn();

    createFileDescColumn();

    EMRImportWizard wizard = (EMRImportWizard) getWizard();
    if (wizard.isVariantsAvailable()) {
      createValidityColumn();
    }

    this.filesTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        EMRFileImportWizardPage.this.deleteLinkAction.setEnabled(true);
      }
    });
  }


  /**
   * create validity column
   */
  private void createValidityColumn() {
    final GridViewerColumn scopeColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerCheckStyleColumn(this.filesTabViewer, "Scope - Partial PIDC", 200);

    scopeColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof EMRWizardData) {
          EMRWizardData emrWizardData = (EMRWizardData) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), emrWizardData.isPartialPIDCScope());

          gridItem.setCheckable(cell.getVisualIndex(), true);
        }
      }
    });
    scopeColumn.setEditingSupport(new CheckEditingSupport(scopeColumn.getViewer()) {

      @Override
      public void setValue(final Object element, final Object value) {
        if (element instanceof EMRWizardData) {
          EMRWizardData emrWizardData = (EMRWizardData) element;
          boolean isScopePartial = (boolean) value;
          emrWizardData.setPartialPIDCScope(isScopePartial);
        }
      }


    });
    scopeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(scopeColumn.getColumn(), 2, this.codexFileTabSorter, this.filesTabViewer));

  }

  /**
   * create file description column
   */
  private void createFileDescColumn() {
    final GridViewerColumn fileDescColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.filesTabViewer, "Description (click into cell to edit)", 400);
    fileDescColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String result = "";
        if (element instanceof EMRWizardData) {

          EMRWizardData emrData = (EMRWizardData) element;
          result = emrData.getDescripton();

        }
        return result;
      }


    });

    fileDescColumn.setEditingSupport(new GridTableEditingSupport(this.filesTabViewer, 1));

    fileDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(fileDescColumn.getColumn(), 1, this.codexFileTabSorter, this.filesTabViewer));

  }

  /**
   * create file name column
   */
  private void createFileNameColumn() {
    final GridViewerColumn fileNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.filesTabViewer, "File Name", 500);

    fileNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String result = "";
        if (element instanceof EMRWizardData) {

          EMRWizardData linkData = (EMRWizardData) element;
          result = linkData.getFileName();

        }
        return result;
      }


    });

    fileNameColumn.setEditingSupport(new GridTableEditingSupport(this.filesTabViewer, 0));
    fileNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(fileNameColumn.getColumn(), 0, this.codexFileTabSorter, this.filesTabViewer));

  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.section);

    addNewFileAction(toolBarManager);

    addDeleteFileActionToSection(toolBarManager);


    toolBarManager.update(true);

    this.section.setTextClient(toolbar);
  }

  /**
   * @param toolBarManager
   */
  private void addDeleteFileActionToSection(final ToolBarManager toolBarManager) {

    this.deleteLinkAction = new Action("Delete Files", SWT.NONE) {

      @Override
      public void run() {

        IStructuredSelection tableSelection = EMRFileImportWizardPage.this.filesTabViewer.getStructuredSelection();
        EMRWizardData selectedData = (EMRWizardData) tableSelection.getFirstElement();

        if (null != selectedData) {
          // if there is a selection in the table
          EMRFileImportWizardPage.this.tableInput.remove(selectedData);
          EMRFileImportWizardPage.this.fileNameSet.remove(selectedData.getFileName());
          EMRFileImportWizardPage.this.filesTabViewer.refresh();
          getWizard().getContainer().updateButtons();
        }
      }
    };
    // Set the image for delete the user
    this.deleteLinkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteLinkAction.setEnabled(false);
    toolBarManager.add(this.deleteLinkAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   */
  protected void addNewFileAction(final ToolBarManager toolBarManager) {// Create an action to add new link
    Action newCodexFileAction = new Action("Add Codex Sheet", SWT.NONE) {

      @Override
      public void run() {
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
        fileDialog.setText("Select CODEX file");
        fileDialog.setFilterExtensions(new String[] { "*.xlsx;*.xls;*.xlsm", "*.xlsx", "*.xls", "*.xlsm" });
        fileDialog.setFilterNames(
            new String[] { "xlsx,xls,xlsm(*.xlsx,*.xls,*.xlsm)", "xlsx(*.xlsx)", "xls(*.xls)", "xlsm(*.xlsm" });
        fileDialog.open();
        final String[] filesSelected = fileDialog.getFileNames();
        // store preferences
        if (filesSelected == null) {
          return;
        }
        String filterPath = fileDialog.getFilterPath();
        for (String file : filesSelected) {
          if (EMRFileImportWizardPage.this.fileNameSet.contains(file)) {
            CDMLogger.getInstance().errorDialog(
                "The file name : " + file + " is not unique! \n Upload cannot be done for this file. ",
                Activator.PLUGIN_ID);
            continue;
          }

          EMRFileImportWizardPage.this.fileNameSet.add(file);

          EMRWizardData codexData = new EMRWizardData();
          codexData.setFilePath(filterPath + "/" + file);
          codexData.setFileName(file);
          codexData.setDescripton("");
          EMRFileImportWizardPage.this.tableInput.add(codexData);

        }
        EMRFileImportWizardPage.this.filesTabViewer.setInput(EMRFileImportWizardPage.this.tableInput);
        getWizard().getContainer().updateButtons();
      }
    };
    // Set the image for add link action
    newCodexFileAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    newCodexFileAction.setEnabled(true);
    toolBarManager.add(newCodexFileAction);
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
   * when next button is pressed
   */
  public void nextPressed() {
    EMRImportWizard wizard = (EMRImportWizard) getWizard();

    try {
      wizard.getContainer().run(true, true, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) {
          callUploadService(wizard, monitor);
        }
      });
    }

    catch (InvocationTargetException | InterruptedException exp) {
      setPageComplete(false);
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }


    if (this.confirm) {
      if (null == this.uploadResponse) {
        setPageComplete(false);
        CDMLogger.getInstance().errorDialog(this.uploadErrorMsg,exceptionOnUpload, Activator.PLUGIN_ID);
      }
      else {
        moveToNextPage(wizard);
      }
    }


  }

  /**
   * @param wizard
   */
  private void moveToNextPage(final EMRImportWizard wizard) {
    // if there had been a response
    EMRResultWizardPage resultPage = (EMRResultWizardPage) wizard.getPage("Result Page");
    EMRVariantSelectionWizardPage variantPage =
        (EMRVariantSelectionWizardPage) wizard.getPage("Variant Selection Page");

    if (null != variantPage) {
      // if variants are not available , variantPage will be null
      variantPage.setInput(this.uploadResponse);
    }

    if (CommonUtils.isNotEmpty(this.uploadResponse.getEmrFileErrorMap())) {
      // in case there are some upload errors
      resultPage.setInput(this.uploadResponse);
    }
    else {
      // in case the upload was completely successful
      resultPage.showSuccessControls();
    }
    resultPage.setPageComplete(true);

    if (wizard.isVariantsAvailable()) {
      // variant assignment page
      showOrHideVarAssgnPage();
    }
  }

  /**
   * @param wizard EMRImportWizard
   * @param monitor IProgressMonitor
   */
  private void callUploadService(final EMRImportWizard wizard, final IProgressMonitor monitor) {
    EmrFileServiceClient emrFileClient = new EmrFileServiceClient();
    // starting the progress monitor
    monitor.beginTask("Uploading files", 100);
    monitor.worked(40);

    List<String> notFoundFiles = computeNotFoundFiles();

    this.confirm = true;
    // if there are files that cannot be found , then show confirm dialog
    if (CommonUtils.isNotEmpty(notFoundFiles)) {
      StringBuilder strBuilder = buildStringForConfirmMsg(notFoundFiles);

      EMRFileImportWizardPage.this.confirm =
          MessageDialogUtils.getConfirmMessageDialogWithYesNo("Confirm to continue upload", strBuilder.toString());


    }
    if (this.confirm) {
      // if the user presses 'Yes'

      // Link to pidcVersion - for mail sent incase of errors
      LinkCreator linkCreator = new LinkCreator(wizard.getPidcVersion());
      String pidcVersionLink = linkCreator.getHtmlText();
      // call the web service for the EMR files upload
      try {
        EMRFileImportWizardPage.this.uploadResponse = emrFileClient.uploadEMRFilesForPidcVersion(
            wizard.getPidcVersion().getId(), pidcVersionLink, EMRFileImportWizardPage.this.tableInput, notFoundFiles);
        EMRFileImportWizardPage.this.uploadSuccess = true;

      }
      catch (IOException | ApicWebServiceException exp) {
        // when there is exception due to IO or from server side
        EMRFileImportWizardPage.this.uploadSuccess = false;
        EMRFileImportWizardPage.this.uploadErrorMsg = exp.getMessage();
        EMRFileImportWizardPage.this.exceptionOnUpload = exp;
      }

    }
    else {
      EMRFileImportWizardPage.this.uploadSuccess = false;
    }
    monitor.worked(100);
    monitor.done();
  }

  /**
   * @param notFoundFiles
   * @return
   */
  private StringBuilder buildStringForConfirmMsg(final List<String> notFoundFiles) {
    StringBuilder strBuilder = new StringBuilder(120);
    strBuilder.append("The following files are not found in the file system.\n");
    for (String filepath : notFoundFiles) {
      strBuilder.append(filepath).append("\n");
    }
    strBuilder.append("Do you wish to skip these files and continue upload?");
    return strBuilder;
  }

  /**
   * @return
   */
  private List<String> computeNotFoundFiles() {
    // list to add file not found cases
    List<String> notFoundFiles = new ArrayList<>();
    // check if the files exist in the file system
    for (EMRWizardData wizardData : this.tableInput) {
      String filePath = wizardData.getFilePath();
      File file = new File(filePath);
      if (!file.exists()) {
        notFoundFiles.add(filePath);
      }
    }
    return notFoundFiles;
  }

  /**
   * show or hide 3rd page based on check box selection
   *
   * @param wizard
   */
  private void showOrHideVarAssgnPage() {
    for (EMRWizardData emrData : this.tableInput) {

      if (emrData.isPartialPIDCScope()) {
        // if the variant
        this.isVarAssgnPageNeeded = true;
        break;
      }
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPageComplete() {
    return !this.tableInput.isEmpty();
  }

  /**
   * @return boolean
   */
  public boolean uploadSuccess() {
    return this.uploadSuccess;
  }

  /**
   * @return the isVarAssgnPageNeeded
   */
  public boolean isVarAssgnPageNeeded() {
    return this.isVarAssgnPageNeeded;
  }

}
