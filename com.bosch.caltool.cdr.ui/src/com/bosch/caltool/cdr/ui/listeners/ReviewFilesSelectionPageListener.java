/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.listeners;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizard.page.validator.ReviewFilesSelectionWizardPageValidator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.ReviewFilesSelectionWizardPage;
import com.bosch.caltool.icdm.common.ui.dialogs.FileDialogHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author say8cob
 */
public class ReviewFilesSelectionPageListener {


  private final CalDataReviewWizard calDataReviewWizard;
  private final ReviewFilesSelectionWizardPageValidator reviewFilesSelectionWizardPageValidator;
  private final ReviewFilesSelectionWizardPage reviewFilesSelectionWizardPageNew;

  /**
   * @param calDataReviewWizard
   * @param reviewFilesSelectionWizardPageValidator
   */
  public ReviewFilesSelectionPageListener(final CalDataReviewWizard calDataReviewWizard,
      final ReviewFilesSelectionWizardPageValidator reviewFilesSelectionWizardPageValidator) {
    this.calDataReviewWizard = calDataReviewWizard;
    this.reviewFilesSelectionWizardPageValidator = reviewFilesSelectionWizardPageValidator;
    this.reviewFilesSelectionWizardPageNew = calDataReviewWizard.getFilesSelWizPage();
  }


  /**
   * @return the calDataReviewWizard
   */
  public CalDataReviewWizard getCalDataReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @return the reviewFilesSelectionWizardPageValidator
   */
  public ReviewFilesSelectionWizardPageValidator getReviewFilesSelectionWizardPageValidator() {
    return this.reviewFilesSelectionWizardPageValidator;
  }


  /**
   * @return the reviewFilesSelectionWizardPageNew
   */
  public ReviewFilesSelectionWizardPage getReviewFilesSelectionWizardPage() {
    return this.reviewFilesSelectionWizardPageNew;
  }


  /**
  *
  */
  public void createActionListeners() {

    this.reviewFilesSelectionWizardPageNew.getInvalidFileList().addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {
        if ((event.stateMask == SWT.CTRL) && (event.keyCode == 'c')) {
          final String[] selection = ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew
              .getInvalidFileList().getSelection();
          final Clipboard clipboard = new Clipboard(Display.getCurrent());

          StringBuilder tempStr = new StringBuilder();
          for (String string : selection) {
            tempStr.append(string);
            tempStr.append('\n');
          }
          String textData = tempStr.toString();
          final TextTransfer textTransfer = TextTransfer.getInstance();
          final Transfer[] transfers = new Transfer[] { textTransfer };
          final Object[] data = new Object[] { textData };
          clipboard.setContents(data, transfers);
          clipboard.dispose();
        }
      }
    });

    this.reviewFilesSelectionWizardPageNew.getDeleteFileBt().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if ((ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getDeletedFiles() != null) &&
            (ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getDeletedFiles().length != 0)) {
          for (String funcName : ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew
              .getDeletedFiles()) {
            // ICDM-2355
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.tableRemove(funcName,
                ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getFilesList());

            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getSelectedFilesPath()
                .remove(funcName);
            // Icdm-729 if the Hex file is removed then make the Hex file flag as false
            if (funcName.contains(".hex")) {
              ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setHexFileSel(false);
            }
            // ICDM-687
            ReviewFilesSelectionPageListener.this.calDataReviewWizard.setContentChanged(true);
          }
          ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setDeletedFiles(null);
        }
        // ICDM-2355
        if ((null != ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getFilesList()) &&
            (ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getFilesList()
                .getItemCount() == 0)) {
          ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setErrorMessage(null);
          ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getAddFileButton().setEnabled(true);
          ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getDeleteFileBt().setEnabled(false);
          ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getAddFileButton().setEnabled(true);
        }

        ReviewFilesSelectionPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);
        // ICDM-2355
        ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setPageComplete(true);
        TableItem[] items =
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getFilesList().getItems();
        for (TableItem tableItem : items) {
          String filePath = tableItem.getText();
          if (!CommonUtils.isEmptyString(FilenameUtils.getFullPath(filePath)) &&
              !CommonUtils.isFileAvailable(filePath)) {
            ReviewFilesSelectionPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(true);
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setPageComplete(false);
            tableItem.setForeground(ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getTabComp()
                .getDisplay().getSystemColor(SWT.COLOR_RED));
          }
        }
        ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageValidator.canFlipToNextPage();
        ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageValidator.checkNextBtnEnable();
      }
    });

    this.reviewFilesSelectionWizardPageNew.getAddFileButton().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        // ICDM-1048
        FileDialogHandler fileDialog =
            new FileDialogHandler(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
        fileDialog.setText("Import File to be reviewed");


        CommonUtils.swapArrayElement(ReviewFilesSelectionWizardPage.getFileextensions(),
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getPreference()
                .getString(CommonUtils.CDR_RVW_FILE_EXTN),
            0);

        CommonUtils.swapArrayElement(ReviewFilesSelectionWizardPage.getFilenames(),
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getPreference()
                .getString(CommonUtils.CDR_RVW_FILE_NAME),
            0);
        // ICDM-1131
        // set filter names
        fileDialog.setFilterNames(ReviewFilesSelectionWizardPage.getFilenames());
        // set the extensions order based on user previous selection
        fileDialog.setFilterExtensions(ReviewFilesSelectionWizardPage.getFileextensions());
        String result = fileDialog.open();
        String selectedFileName = fileDialog.getFileName();
        // store preferences
        ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getPreference().setValue(
            CommonUtils.CDR_RVW_FILE_EXTN,
            ReviewFilesSelectionWizardPage.getFileextensions()[fileDialog.getFilterIndex()]);

        ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getPreference().setValue(
            CommonUtils.CDR_RVW_FILE_NAME, ReviewFilesSelectionWizardPage.getFilenames()[fileDialog.getFilterIndex()]);

        ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setErrorMessage(null);

        if ((null != result) && !result.isEmpty()) {


          String[] fileNameExtn = selectedFileName.split("\\.");
          ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew
              .setSelFileExtension(fileNameExtn[fileNameExtn.length - 1]);

          String[] filenames = fileDialog.getFileNames();

          String[] fileNamesWithPath = new String[filenames.length];
          int iCounter = 0;
          for (String string : filenames) {
            fileNamesWithPath[iCounter] = fileDialog.getFilterPath() + "\\" + string;
            iCounter++;
          }
          ReviewFilesSelectionPageListener.this.addFilesToTable(filenames, fileNamesWithPath);
        }
      }

    });


    this.reviewFilesSelectionWizardPageNew.getTable().addListener(SWT.Selection, new Listener() {

      @Override
      public void handleEvent(final Event event) {
        ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getDeleteFileBt().setEnabled(true);
        TableItem[] selection =
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getTable().getSelection();
        ArrayList<String> list = new ArrayList<String>();
        for (TableItem tableItem : selection) {
          list.add(tableItem.getText());
        }
        ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew
            .setDeletedFiles(list.toArray(new String[] {}));
      }

    });

    this.reviewFilesSelectionWizardPageNew.getTable().addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {

        char eventChar = event.character;
        if (eventChar == SWT.DEL) {
          if ((ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getDeletedFiles() != null) &&
              (ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getDeletedFiles().length != 0)) {
            for (String funcName : ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew
                .getDeletedFiles()) {
              TableItem[] items =
                  ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getTable().getItems();
              int index = -1;
              for (TableItem tableItem : items) {
                index++;
                if (funcName.equals(tableItem.getText())) {
                  ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getTable().remove(index);
                  break;
                }
              }

              ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getSelectedFilesPath()
                  .remove(funcName);
              // ICDM-687
              ReviewFilesSelectionPageListener.this.calDataReviewWizard.setContentChanged(true);
            }
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setDeletedFiles(null);
          }
          if ((null != ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getTable()) &&
              (ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getTable()
                  .getItemCount() == 0)) {
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setErrorMessage(null);
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getAddFileButton().setEnabled(true);
            ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getDeleteFileBt().setEnabled(false);
          }
          ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageValidator.checkNextBtnEnable();
        }

      }
    });

  }

  /**
   * @param filenames
   * @param fileNamesWithPath
   */
  public void addFilesToTable(final String[] filenames, final String[] fileNamesWithPath) {
    ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setRepeatedFiles("");
    // selected file names
    ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.setSelectedFile(fileNamesWithPath);

    if ((ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getSelectedFile() != null) &&
        (ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageNew.getSelectedFile().length != 0)) {
      // ICDM-1311
      /**
       * Check no hex file is included when multiple caldata files are selected together
       */
      boolean flag = true;
      if (filenames.length > ReviewFilesSelectionWizardPage.SIZE_ONE) {
        for (String filename : filenames) {
          if (filename.toLowerCase(Locale.getDefault()).endsWith(ReviewFilesSelectionWizardPage.HEX)) {
            CDMLogger.getInstance().errorDialog(ReviewFilesSelectionWizardPage.PAGE_DESCRIPTION, Activator.PLUGIN_ID);
            flag = false;
            break;
          }
        }
      }
      if (flag) {
        addMultipleFiles();
      }
    }
    ReviewFilesSelectionPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(false);

    ReviewFilesSelectionPageListener.this.reviewFilesSelectionWizardPageValidator.checkNextBtnEnable();
  }


  /**
  *
  */
  private void addMultipleFiles() {
    if (this.reviewFilesSelectionWizardPageNew.getSelFileExtension()
        .equalsIgnoreCase(ReviewFilesSelectionWizardPage.HEX)) {

      // if one hex file is selected and no other files are selected previously, the file is added to the list
      if ((this.reviewFilesSelectionWizardPageNew.getSelectedFile().length == 1) &&
          // ICDM-2355
          ((null != this.reviewFilesSelectionWizardPageNew.getFilesList()) &&
              (this.reviewFilesSelectionWizardPageNew.getFilesList().getItemCount() == 0))) {
        TableItem tableItem = new TableItem(this.reviewFilesSelectionWizardPageNew.getFilesList(), SWT.NONE);
        tableItem.setText(this.reviewFilesSelectionWizardPageNew.getSelectedFile()[0]);
        this.reviewFilesSelectionWizardPageNew.getSelectedFilesPath()
            .add(this.reviewFilesSelectionWizardPageNew.getSelectedFile()[0]);
        this.reviewFilesSelectionWizardPageNew.getAddFileButton().setEnabled(false);
        this.reviewFilesSelectionWizardPageNew.setHexFileSel(true);
        this.calDataReviewWizard.setContentChanged(true);
      }

      // if multiple hex files are selected error is displayed to user
      else if (this.reviewFilesSelectionWizardPageNew
          .getSelectedFile().length > ReviewFilesSelectionWizardPage.NUM_FILES_SELECTED) {
        CDMLogger.getInstance().errorDialog("Multiple hex files cannot be selected", Activator.PLUGIN_ID);
      }
      else {
        CDMLogger.getInstance().errorDialog(ReviewFilesSelectionWizardPage.PAGE_DESCRIPTION, Activator.PLUGIN_ID);
      }
    }

    // other extension files
    else {
      for (String fileName : this.reviewFilesSelectionWizardPageNew.getSelectedFile()) {
        // ICDM-2355
        boolean isNotHaving = true;
        if (null != this.reviewFilesSelectionWizardPageNew.getFilesList()) {
          for (TableItem tableIte : this.reviewFilesSelectionWizardPageNew.getFilesList().getItems()) {
            if (fileName.equals(tableIte.getText())) {
              isNotHaving = false;
              break;
            }
          }
        }

        // file is added to the list if its not added before
        if (isNotHaving) {
          TableItem tableItem = new TableItem(this.reviewFilesSelectionWizardPageNew.getFilesList(), SWT.NONE);
          tableItem.setText(fileName);
          this.reviewFilesSelectionWizardPageNew.getSelectedFilesPath().add(fileName);
          // ICDM-687
          this.calDataReviewWizard.setContentChanged(true);

          if (!CommonUtils.isEmptyString(FilenameUtils.getFullPath(fileName)) &&
              !CommonUtils.isFileAvailable(fileName)) {
            this.calDataReviewWizard.getCdrWizardUIModel().setExceptioninWizard(true);
            this.reviewFilesSelectionWizardPageNew.setPageComplete(false);
            this.reviewFilesSelectionWizardPageNew.updatePageContainer();
            tableItem.setForeground(
                this.reviewFilesSelectionWizardPageNew.getTabComp().getDisplay().getSystemColor(SWT.COLOR_RED));
          }
        }

        // file already present in list are not added
        else {
          this.reviewFilesSelectionWizardPageNew
              .setRepeatedFiles(new StringBuilder().append(this.reviewFilesSelectionWizardPageNew.getRepeatedFiles())
                  .append("\n").append(fileName).toString());
        }
      }
      if (!this.reviewFilesSelectionWizardPageNew.getRepeatedFiles().isEmpty()) {
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information",
            "Selected File is already added to the list" + this.reviewFilesSelectionWizardPageNew.getRepeatedFiles());
      }
    }
  }

}
