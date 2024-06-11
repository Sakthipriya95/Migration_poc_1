/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.MonicaFileData;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.monicareportparser.MonitoringToolParser;
import com.bosch.caltool.monicareportparser.data.MonitoringToolOutput;
import com.bosch.caltool.monicareportparser.exception.MonicaRptParserException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author say8cob
 */
public class MonicaFolderSelectionDialog extends SelectionDialog {


  /**
   *
   */
  private static final String DCM_FILE_EXT = ".dcm";

  private static final String NEW_LINE = "\n";

  private static final String POSTFIX_MONICA_SHEET_NAME = "-iCDM_Check";

  private static final String BACK_SLASH = "\\";

  /**
   * EXtention for monica file
   */
  private static final String EXCEL_EXT1 = ".xlsx";

  private static final String EXCEL_EXT2 = ".xlx";


  private static final String TEMP_EXCEL_FILES_PREFIX = "~$";

  /**
   * File path selected
   */
  protected String filePath;

  private Text fileText;

  private ListViewer projectListViewer;

  private Button browseBtn;

  private Button selectAll;

  private Button deSelectAll;

  private final MonicaReviewDialog monicaReviewDialog;

  private final Set<String> notConsiderendExcelFileList = new HashSet<>();

  private final Set<String> notFoundDCMFileList = new HashSet<>();

  /**
   * Section label
   */
  private static final String DIALOG_SECTION_LABEL = "Select the MoniCa file(s):";

  /**
   * Button instance for save
   */
  protected Button addBtn;
  /**
   * Button instance for cancel
   */
  protected Button cancelBtn;

  /**
   * @param parentShell constructor
   * @param dialog
   */
  public MonicaFolderSelectionDialog(final Shell parentShell, final MonicaReviewDialog dialog) {
    super(parentShell);
    this.monicaReviewDialog = dialog;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {

    createFolderSelection(parent);

    createTableViewer(parent);

    createSelectionButton(parent);

    addSelectionListener();


    return super.createContents(parent);
  }


  /**
   * @param parent
   */
  private void createFolderSelection(final Composite parent) {
    org.eclipse.swt.layout.GridLayout gridLayout = new org.eclipse.swt.layout.GridLayout();
    parent.setLayout(gridLayout);
    Group grp = new Group(parent, SWT.NONE);
    org.eclipse.swt.layout.GridLayout gridLayou1t = new org.eclipse.swt.layout.GridLayout();
    grp.setLayout(gridLayou1t);
    gridLayou1t.numColumns = 3;
    LabelUtil.getInstance().createLabel(grp, "MoniCa Excel Directory :");
    this.fileText = TextUtil.getInstance().createText(grp, false, "");
    this.fileText.setEditable(false);
    this.fileText.setEnabled(true);
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    txtGridData.widthHint = 350;
    this.fileText.setLayoutData(txtGridData);
    this.fileText.setEditable(false);

    this.browseBtn = new Button(grp, SWT.NONE);
    this.browseBtn.setToolTipText("Select MoniCa Excel Directory");
    // image for browse button
    this.browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
  }


  /**
   * @param parent
   */
  private void createTableViewer(final Composite parent) {
    Composite compositeTableViewer = new Composite(parent, SWT.NONE);
    org.eclipse.swt.layout.GridLayout gridLayou1tTableViewer = new org.eclipse.swt.layout.GridLayout();
    compositeTableViewer.setLayout(gridLayou1tTableViewer);
    compositeTableViewer.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

    final Label label = new Label(compositeTableViewer, SWT.WRAP);
    label.setText(DIALOG_SECTION_LABEL);
    label.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.projectListViewer = new ListViewer(compositeTableViewer, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    this.projectListViewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

    this.projectListViewer.setContentProvider(ArrayContentProvider.getInstance());

  }


  /**
   * @param parent
   */
  private void createSelectionButton(final Composite parent) {
    Composite compositeBtn = new Composite(parent, SWT.NONE);
    org.eclipse.swt.layout.GridLayout gridLayoutForButton = new org.eclipse.swt.layout.GridLayout();
    gridLayoutForButton.numColumns = 2;
    compositeBtn.setLayout(gridLayoutForButton);
    compositeBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true));
    this.selectAll = new Button(compositeBtn, SWT.CENTER);
    this.selectAll.setText("Select-All");
    GridData btnSize = GridDataUtil.getInstance().createGridData(100, 27);
    this.selectAll.setLayoutData(btnSize);
    this.deSelectAll = new Button(compositeBtn, SWT.CENTER);
    this.deSelectAll.setText("Deselect-All");
    this.deSelectAll.setLayoutData(btnSize);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.addBtn = createButton(parent, IDialogConstants.OK_ID, "Add", true);
    this.addBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   *
   */
  private void addSelectionListener() {

    this.projectListViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        validateAddButton();
      }
    });

    this.browseBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell());
        dialog.setFilterPath(CommonUtils.getUserDirPath());
        String seletedPath = dialog.open();
        if (!CommonUtils.isEmptyString(seletedPath)) {
          MonicaFolderSelectionDialog.this.filePath = seletedPath;
          MonicaFolderSelectionDialog.this.fileText.setText(MonicaFolderSelectionDialog.this.filePath);
          SortedSet<String> monicaFile = getMonicaFile();
          if (monicaFile.isEmpty()) {
            CDMLogger.getInstance().errorDialog("There are no MoniCa Excel files in the selected folder.",
                Activator.PLUGIN_ID);
            return;
          }
          MonicaFolderSelectionDialog.this.projectListViewer.setInput(monicaFile);
          validateAddButton();
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // NA
      }


    });
    this.selectAll.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        MonicaFolderSelectionDialog.this.projectListViewer.getList().selectAll();
        validateAddButton();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });

    this.deSelectAll.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        MonicaFolderSelectionDialog.this.projectListViewer.getList().deselectAll();
        validateAddButton();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA
      }
    });
  }

  private SortedSet<String> getMonicaFile() {
    SortedSet<String> fileSet = new TreeSet<>();
    if ((this.filePath != null) && !this.filePath.isEmpty()) {
      File folder = new File(this.filePath);
      if (folder.exists()) {
        String[] files = folder.list();

        for (String file : files) {
          if ((file.contains(EXCEL_EXT1) || file.contains(EXCEL_EXT2)) && !file.contains(TEMP_EXCEL_FILES_PREFIX)) {
            fileSet.add(file);
          }
        }
      }
    }
    return fileSet;
  }

  private void validateAddButton() {

    this.addBtn.setEnabled((this.filePath != null) && (this.projectListViewer.getList().getSelection().length != 0));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    boolean duplicateDataFound = false;
    List<MonicaFileData> monicaFileDataList = parseMonicaFile();
    errorMessageBuilder();
    for (MonicaFileData newMonicaFileData : monicaFileDataList) {
      newMonicaFileData.setIndex(this.monicaReviewDialog.getMonicaFileDataSet().size() + 1);
      if (!this.monicaReviewDialog.getMonicaFileDataSet().add(newMonicaFileData)) {
        duplicateDataFound = true;
      }
    }
    if (duplicateDataFound) {
      CDMLogger.getInstance().warnDialog(
          "Duplicate entry not allowed in MoniCa Review, So it is not added for MoniCa Review.", Activator.PLUGIN_ID);
      return;
    }
    this.monicaReviewDialog.getTabViewer().setInput(this.monicaReviewDialog.getMonicaFileDataSet());
    this.monicaReviewDialog.getTabViewer().refresh();
    this.monicaReviewDialog.getSaveBtn().setEnabled(false);
    super.okPressed();
  }


  /**
   *
   */
  private void errorMessageBuilder() {
    // Error Message Builder
    StringBuilder errorMessage = new StringBuilder();
    if (!this.notConsiderendExcelFileList.isEmpty()) {
      errorMessage.append(!this.notFoundDCMFileList.isEmpty() ? "Error Info 1:" : "Error Info :");
      errorMessage.append(NEW_LINE);
      errorMessage
          .append("Below Excel file doesn't contains valid MoniCa Reports. They have not been considered." + NEW_LINE);
      for (String excelFile : this.notConsiderendExcelFileList) {
        errorMessage.append(excelFile + NEW_LINE);
      }
    }
    if (!this.notFoundDCMFileList.isEmpty()) {
      errorMessage.append(errorMessage.length() != 0 ? NEW_LINE + "Error Info 2 :" : "Error Info :");
      errorMessage.append(NEW_LINE);
      errorMessage.append(
          "Below DCM file cannot be found at the location given in the Upload Sheet.DCM file need to be Uploaded Manually." +
              NEW_LINE);
      errorMessage.append(NEW_LINE);
      for (String dcmFile : this.notFoundDCMFileList) {
        errorMessage.append(dcmFile + NEW_LINE);
      }
    }
    if (errorMessage.length() != 0) {
      CDMLogger.getInstance().errorDialog(errorMessage.toString(), Activator.PLUGIN_ID);
    }
  }

  private List<MonicaFileData> parseMonicaFile() {
    this.notConsiderendExcelFileList.clear();
    this.notFoundDCMFileList.clear();
    MonitoringToolParser monitoringToolParser = new MonitoringToolParser(CDMLogger.getInstance());

    List<MonicaFileData> monicaFileDataList = new ArrayList<>();

    for (String selectedFile : this.projectListViewer.getList().getSelection()) {
      String monicaFilePath = this.filePath.concat(BACK_SLASH + selectedFile);

      List<String> allSheetNames = monitoringToolParser.getAllSheetNames(monicaFilePath);

      allSheetNames = allSheetNames.stream().filter(sheetName -> sheetName.contains(POSTFIX_MONICA_SHEET_NAME))
          .collect(Collectors.toList());
      if (allSheetNames.isEmpty()) {
        this.notConsiderendExcelFileList.add(selectedFile);
        continue;
      }

      for (String sheetName : allSheetNames) {
        if (sheetName.contains(POSTFIX_MONICA_SHEET_NAME)) {
          MonicaFileData monicaFileData = new MonicaFileData();
          try (FileInputStream monicaExcelStream = new FileInputStream(monicaFilePath)) {

            monitoringToolParser.setInputStream(monicaExcelStream);
            // parse the sheet
            monitoringToolParser.parse(sheetName);
            // get the output
            MonitoringToolOutput output = monitoringToolParser.getOutput();


            String dcmFilePath = output.getDcmFilePath();

            validateAndAddDCMFileData(selectedFile, sheetName, monicaFileData, dcmFilePath);
            // Adding Monica File details
            monicaFileData.setMonicaFilePath(monicaFilePath);
            monicaFileData.setMonicaFileName(new File(monicaFilePath).getName());
            monicaFileData.setSheetName(sheetName);
            monicaFileData.setAllMonicaSheets(allSheetNames);
            monicaFileDataList.add(monicaFileData);

          }
          catch (MonicaRptParserException | IOException exp) {
            CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
            this.notConsiderendExcelFileList.add(selectedFile);
          }
          catch (NoSuchElementException exp1) {
            CDMLogger.getInstance().errorDialog(
                "There's an empty/invalid cell in the excel report. If you can't find an error, contact the iCDM hotline.",
                exp1, Activator.PLUGIN_ID);
          }
        }

      }
    }
    return monicaFileDataList;
  }


  /**
   * @param selectedFile
   * @param sheetName
   * @param monicaFileData
   * @param dcmFilePath
   */
  private void validateAndAddDCMFileData(final String selectedFile, final String sheetName,
      final MonicaFileData monicaFileData, final String dcmFilePath) {
    File dcmFile = new File(dcmFilePath);
    if (dcmFile.exists() && dcmFile.getPath().toLowerCase(Locale.getDefault()).contains(DCM_FILE_EXT)) {
      // Adding DCM File details
      monicaFileData.setDcmFIlePath(dcmFilePath);
      monicaFileData.setDcmFileName(dcmFile.getName());
    }
    else {
      StringBuilder tempDCMError = new StringBuilder();
      tempDCMError.append("ExcelFile: " + selectedFile);
      tempDCMError.append(NEW_LINE);
      tempDCMError.append("SheetName: " + sheetName);
      tempDCMError.append(NEW_LINE);
      tempDCMError.append("DCMFile: " + dcmFilePath);
      tempDCMError.append(NEW_LINE);

      this.notFoundDCMFileList.add(tempDCMError.toString());
    }
  }


}
