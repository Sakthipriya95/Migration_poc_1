/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.jobs.ReviewRuleExcelExportJob;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractExcelExportDialog;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.report.common.ExcelCommonConstants;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * This class is used to create dialog for Review Rule
 *
 * @author svj7cob
 */
// ICDM-1539, Excel Export for Review Rule
public class ReviewRuleExcelExportDialog extends AbstractExcelExportDialog {

  /**
   * message for FILE already opened
   */
  private static final String FILE_ALREADY_OPEN_MSG = "Excel file already open, Please close the file and try again";
  /**
   * message for valid EXCEL file extension
   */
  private static final String VALID_EXCEL_FILE_EXTN_MSG = "Please choose valid Excel file extension (*.xls, *.xlsx)";

  /**
   * Selected Pidc
   */
  @SuppressWarnings("rawtypes")
  private final ParamCollection selectedObject;
  /**
   * complete
   */
  private Button complete;
  /**
   * filtered
   */
  private Button filtered;
  /**
   * filtered Flag to indicate filtered parameter or all
   */
  protected boolean filteredFlag;
  private final ParameterDataProvider paramDataProvider;
  private final ParamCollectionDataProvider paramColDataProvider;

  /**
   * initialise the fields
   *
   * @param parentShell parentShell
   * @param selectedObject selectedObject
   */
  public ReviewRuleExcelExportDialog(final Shell parentShell, final ParamCollection selectedObject,
      final ParameterDataProvider paramDataProvider, final ParamCollectionDataProvider paramColDataProvider) {
    super(parentShell);
    this.selectedObject = selectedObject;
    this.paramDataProvider = paramDataProvider;
    this.paramColDataProvider = paramColDataProvider;
    initialise();
  }

  /**
   * initialises the necessary fields
   */
  private void initialise() {
    TEMP_FILE_PATH = CommonUtils.getUserDirPath() + ExcelCommonConstants.SHARED_PATH;
    String type = this.paramColDataProvider.getObjectTypeName(this.selectedObject) + " Export";
    this.customTitle = type;
    this.customTitleMessage = "Export the parameters and rules to an excel file";
    this.customShellText = type;
  }

  /**
   * This method initializes Export Options area like All or filtered attributes
   */
  @Override
  protected void createDialogArea() {
    // construct a new layout for dialog area
    final GridLayout gridLayout = new GridLayout();

    // assign the no of columns
    gridLayout.numColumns = NO_OF_COLUMNS;

    // creating button group
    final Group buttGroup = new Group(this.form.getBody(), SWT.NONE);
    buttGroup.setText("Select the range of rules to be exported");
    buttGroup.setLayout(gridLayout);
    buttGroup.setLayoutData(GridDataUtil.getInstance().getGridData());

    // creating new Radio button i.e All and Filtered attributes
    this.complete = new Button(buttGroup, SWT.RADIO);
    this.complete.setText("All rules");

    // "All attributes" selected by default
    this.complete.setSelection(true);

    this.filtered = new Button(buttGroup, SWT.RADIO);
    this.filtered.setText("Filtered rules");


    this.filtered.setEnabled(isEditorOpen());

    // adding Selection listener to "All attributes" button
    this.complete.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        ReviewRuleExcelExportDialog.this.filteredFlag = false;
        setListenerFileName();
      }

    });

    // adding Selection listener to "Filtered attributes" button
    this.filtered.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        ReviewRuleExcelExportDialog.this.filteredFlag = true;
        setListenerFileName();
      }
    });
    // create export feature like File path, browse button
    createExportFeature();
  }

  /**
   * This method sets the formated file name in dialog based on selection of All or Filtered attributes in dialog
   */
  private void setListenerFileName() {
    // gets the text from the File path in the dialog
    String localFilePath = this.filePathText.getText();

    // if file path is null, then call the setInitialFileName() method
    if (CommonUtils.isEmptyString(localFilePath)) {
      setInitialFileName();
    }
    else {
      String fullPath = FilenameUtils.getFullPath(localFilePath);
      String baseName = FilenameUtils.getBaseName(localFilePath);
      String extension = FilenameUtils.getExtension(localFilePath);
      String[] splittedFileName = baseName.split(CommonUIConstants.UNDERSCORE);
      int lastIndex = splittedFileName.length - 1;

      // check File Name last index has "Filtered" or "Complete"
      if (CommonUIConstants.FILTERED.equals(splittedFileName[lastIndex]) ||
          CommonUIConstants.COMPLETE.equals(splittedFileName[lastIndex])) {

        // assigning the last index based on filtered flag
        splittedFileName[lastIndex] = this.filteredFlag ? CommonUIConstants.FILTERED : CommonUIConstants.COMPLETE;

        StringBuilder stringBuilder = new StringBuilder();// Create StringBuilder instance to re-build the file name
        for (String string : splittedFileName) {
          stringBuilder.append(string);
          stringBuilder.append(CommonUIConstants.UNDERSCORE);
        }

        // removing unwanted '_' in last character position
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        baseName = String.valueOf(stringBuilder);
      }
      else {
        baseName = CommonUtils.concatenate(baseName, CommonUIConstants.UNDERSCORE,
            this.filteredFlag ? CommonUIConstants.FILTERED : CommonUIConstants.COMPLETE);
      }

      // combining the path ,file_name and extension
      this.filePath = fullPath + baseName + CommonUIConstants.DOT + extension;

      // set the full file path in File path in UI Dialog
      this.filePathText.setText(this.filePath);
    }
  }

  /**
   * This method set File Name during start up (i.e at first time)
   */
  @Override
  protected void setInitialFileName() {

    // assigning '.xlsx' extension by default
    this.fileExtn = ExcelConstants.FILTER_EXTNS[0];
    String baseFileNameWithoutExtension = this.selectedObject.getName();

    // special character replaced with '_' for naming the file
    baseFileNameWithoutExtension =
        baseFileNameWithoutExtension.replaceAll("[^a-zA-Z0-9._]", CommonUIConstants.UNDERSCORE);

    // adding suffix 'Filtered' or 'Complete' at the end
    baseFileNameWithoutExtension = CommonUtils.concatenate(baseFileNameWithoutExtension, CommonUIConstants.UNDERSCORE,
        this.filteredFlag ? CommonUIConstants.FILTERED : CommonUIConstants.COMPLETE);

    // adding file path, file name and file extension
    this.filePath = TEMP_FILE_PATH + baseFileNameWithoutExtension + CommonUIConstants.DOT + this.fileExtn;

    this.filePathText.setText(this.filePath);
  }

  /**
   * checks if the Editor has opened
   *
   * @return if the selected Review rule is opened in editor this method would return true
   */
  @Override
  public boolean isEditorOpen() {
    List<IEditorReference> editors = new ArrayList<>();
    for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
      for (IWorkbenchPage page : window.getPages()) {
        for (IEditorReference editor : page.getEditorReferences()) {
          editors.add(editor);
        }
      }
    }
    for (IEditorReference iEditorReference : editors) {
      if (iEditorReference.getEditor(false) instanceof ReviewParamEditor) {// checks if editor is an instance of Review
                                                                           // Param Editor i.e Review Rules
        return true;
      }
    }
    return false;
  }


  /**
   * after user click OK button in the dialog
   */
  @Override
  protected void okPressed() {
    final File file = new File(CommonUtils.getCompleteFilePath(this.filePath, this.fileExtn));

    // checking if the file already open
    if (CommonUtils.checkIfFileOpen(file)) {
      MessageDialogUtils.getInfoMessageDialog(IMessageConstants.EXCEL_ALREADY_OPEN, FILE_ALREADY_OPEN_MSG);
      this.filePathText.forceFocus();
    }
    else {

      // checks whether given excel file extensions is valid i.e xls, xlsx
      if (Arrays.asList(ExcelConstants.FILTER_EXTNS).contains(this.fileExtn)) {
        Job job = new ReviewRuleExcelExportJob(new MutexRule(), this.selectedObject, this.filePath, this.fileExtn,
            this.openAutomatically, this.filteredFlag, this.paramColDataProvider, this.paramDataProvider);
        CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
        job.schedule();
        super.okPressed();
      }
      else {
        // Throwing error message for invalid excel file extension
        MessageDialogUtils.getInfoMessageDialog(IMessageConstants.INVALID_EXCEL_FILE_EXTENSION,
            VALID_EXCEL_FILE_EXTN_MSG);
        this.filePathText.forceFocus();
      }
    }
  }
}
