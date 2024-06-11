/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultFileModel;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.pages.CaldataAnalyzerPage;
import com.bosch.caltool.icdm.ui.jobs.CaldataLoadFilterJob;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author pdh2cob
 */
public class CaldataAnalyzerEditorActionSet {

  private Action saveFilterAction;

  private Action loadFilterAction;

  private Action saveFilesAction;

  private Action openHelpLinkAction;

  private Action resetEditorAction;

  private final CaldataAnalyzerPage page;


  /**
   * @param page instance
   */
  public CaldataAnalyzerEditorActionSet(final CaldataAnalyzerPage page) {
    this.page = page;
  }


  /**
   * Method for reset editor action
   */
  public void resetEditorAction() {
    this.resetEditorAction = new Action() {

      @Override
      public void run() {
        CaldataAnalyzerEditorActionSet.this.page.setToDefaultState();
      }
    };
    final ImageDescriptor reset = ImageManager.getImageDescriptor(ImageKeys.CLEAR_HIST_16X16);
    this.resetEditorAction.setImageDescriptor(reset);
    this.resetEditorAction.setToolTipText("Reset All");
    CaldataAnalyzerEditorActionSet.this.page.getToolBarManager().add(this.resetEditorAction);
  }


  /**
   * Method for save all action
   */
  public void saveFilesAction() {
    this.saveFilesAction = new Action() {

      @Override
      public void run() {
        List<CaldataAnalyzerResultFileModel> selectedFiles =
            CaldataAnalyzerEditorActionSet.this.page.getSelectedResultFiles();
        if (!selectedFiles.isEmpty()) {
          String destDir = getSelectedFolderPath();
          String sourceDir = CaldataAnalyzerEditorActionSet.this.page.getResultModel().getOutputDirectory();
          if ((destDir != null) && (sourceDir != null) && new File(sourceDir).isDirectory()) {
            for (CaldataAnalyzerResultFileModel fileModel : selectedFiles) {
              if (new File(fileModel.getFilePath()).exists()) {
                try {
                  FileUtils.copyFileToDirectory(new File(fileModel.getFilePath()), new File(destDir));
                }
                catch (IOException e) {
                  CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
                }
              }
            }

          }
          CaldataAnalyzerEditorActionSet.this.saveFilesAction.setEnabled(false);
        }
      }
    };

    final ImageDescriptor save = ImageManager.getImageDescriptor(ImageKeys.DOWNLOAD_16X16);
    this.saveFilesAction.setImageDescriptor(save);
    this.saveFilesAction.setToolTipText("Download");
    this.saveFilesAction.setEnabled(false);
    CaldataAnalyzerEditorActionSet.this.page.getFileToolbarManager().add(this.saveFilesAction);
  }


  /**
   * Load filter action
   */
  public void loadFilterAction() {
    this.loadFilterAction = new Action() {

      @Override
      public void run() {


        String filePath = getSelectedFilePath(false);

        if (filePath != null) {
          CaldataLoadFilterJob job =
              new CaldataLoadFilterJob("Load Filter", CaldataAnalyzerEditorActionSet.this.page, filePath);

          job.schedule();

          // add job listener
          job.addJobChangeListener(new JobChangeAdapter() {

            @Override
            public void done(final IJobChangeEvent event) {
              // if job is done, open wziard with filters filled
              if (event.getResult().isOK()) {
                CaldataAnalyzerEditorActionSet.this.page.startAnalysisWizard(false);
              }
            }
          });
        }

      }
    };

    final ImageDescriptor loadFilter = ImageManager.getImageDescriptor(ImageKeys.FILE_IMPORT_16X16);
    this.loadFilterAction.setImageDescriptor(loadFilter);
    this.loadFilterAction.setToolTipText("Load Filter Criteria");
    CaldataAnalyzerEditorActionSet.this.page.getToolBarManager().add(this.loadFilterAction);
  }


  /**
   * Action to save filter as json file
   */
  public void saveFilterAction() {
    this.saveFilterAction = new Action() {

      @Override
      public void run() {
        String filePath = getSelectedFilePath(true);
        if ((filePath != null) && !CaldataAnalyzerEditorActionSet.this.page.getResultModel().getJsonFiles().isEmpty()) {
          for (CaldataAnalyzerResultFileModel fileModel : CaldataAnalyzerEditorActionSet.this.page.getResultModel()
              .getJsonFiles()) {
            if (fileModel.getFileName().equals(ApicConstants.CALDATA_FILTER_JSON_NAME)) {
              File file = new File(fileModel.getFilePath());
              try (BufferedReader br = new BufferedReader(new FileReader(file));
                  BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                  bw.write(line);
                }
                br.close();
                bw.close();
              }
              catch (IOException e) {
                CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
              }
            }
          }
        }

      }
    };

    final ImageDescriptor saveFilter = ImageManager.getImageDescriptor(ImageKeys.SAVE_28X30);
    this.saveFilterAction.setImageDescriptor(saveFilter);
    this.saveFilterAction.setEnabled(false);
    this.saveFilterAction.setToolTipText("Save Filter Criteria");
    CaldataAnalyzerEditorActionSet.this.page.getToolBarManager().add(this.saveFilterAction);
  }

  /**
   * @return file path from file dialog
   */
  private String getSelectedFilePath(final boolean isSave) {
    String currentDate = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss").format(new Date());

    String fileName = "CDA_Filter_" + currentDate;
    FileDialog fileDialog;
    if (isSave) {
      fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
      fileDialog.setFileName(fileName);
    }
    else {
      fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
    }
    fileDialog.setText("Save Filter");
    fileDialog.setFilterExtensions(new String[] { "*.json" });
    fileDialog.setFilterNames(new String[] { "JSON (.json)" });
    fileDialog.setOverwrite(true);
    fileDialog.setFilterIndex(0);

    String fileSelected = fileDialog.open();
    if ((fileSelected != null) && CommonUtils.checkIfFileOpen(new File(fileSelected))) {
      MessageDialogUtils.getInfoMessageDialog("", "File Already Open!");
    }
    return fileSelected;
  }


  /**
   * @return folder path from directory dialog
   */
  private String getSelectedFolderPath() {
    DirectoryDialog directoryDialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.Selection);
    directoryDialog.setText("Save All");
    String dirSelected = directoryDialog.open();
    return dirSelected;
  }

  /**
   * @return the saveFilterAction
   */
  public Action getSaveFilterAction() {
    return this.saveFilterAction;
  }


  /**
   * @param saveFilterAction the saveFilterAction to set
   */
  public void setSaveFilterAction(final Action saveFilterAction) {
    this.saveFilterAction = saveFilterAction;
  }


  /**
   * @return the loadFilterAction
   */
  public Action getLoadFilterAction() {
    return this.loadFilterAction;
  }


  /**
   * @param loadFilterAction the loadFilterAction to set
   */
  public void setLoadFilterAction(final Action loadFilterAction) {
    this.loadFilterAction = loadFilterAction;
  }


  /**
   * @return the saveFilesAction
   */
  public Action getSaveFilesAction() {
    return this.saveFilesAction;
  }


  /**
   * @param saveFilesAction the saveFilesAction to set
   */
  public void setSaveFilesAction(final Action saveFilesAction) {
    this.saveFilesAction = saveFilesAction;
  }


  /**
   * @return the openHelpLinkAction
   */
  public Action getOpenHelpLinkAction() {
    return this.openHelpLinkAction;
  }


  /**
   * @param openHelpLinkAction the openHelpLinkAction to set
   */
  public void setOpenHelpLinkAction(final Action openHelpLinkAction) {
    this.openHelpLinkAction = openHelpLinkAction;
  }


}
