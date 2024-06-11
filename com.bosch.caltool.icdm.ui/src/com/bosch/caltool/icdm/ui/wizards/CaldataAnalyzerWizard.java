/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.JsonUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultFileModel;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultModel;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultSummary;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.pages.CaldataAnalyzerPage;
import com.bosch.caltool.icdm.ui.jobs.CalDataAnalyzerInvokationJob;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerCustFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerECUPlatWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerFunctionFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerParamFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerSysConFilterWizardPage;


/**
 * @author pdh2cob
 */
public class CaldataAnalyzerWizard extends Wizard {

  private final CaldataAnalyzerPage caldataAnalyzerPage;

  private CaldataAnalyzerECUPlatWizardPage ecuPlatformFilterWizardPage;

  private CaldataAnalyzerFunctionFilterWizardPage functionFilterWizardPage;

  private CaldataAnalyzerParamFilterWizardPage paramFilterWizardPage;

  private CaldataAnalyzerSysConFilterWizardPage systemConstantFilterWizardPage;

  private CaldataAnalyzerCustFilterWizardPage caldataAnalyzerCustFilterWizardPage;

  private final boolean isNewAnalysis;

  /**
   * @param caldataAnalyzerPage - instance of editor page
   * @param isNewAnalysis - flag to indicate if it is new analysis
   */
  public CaldataAnalyzerWizard(final CaldataAnalyzerPage caldataAnalyzerPage, final boolean isNewAnalysis) {
    super();
    this.caldataAnalyzerPage = caldataAnalyzerPage;
    this.isNewAnalysis = isNewAnalysis;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {

    boolean canFinish = false;
    // create model for service
    this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel()
        .setCustomerFilterModel(this.caldataAnalyzerCustFilterWizardPage.getCustomerFilterModel());
    this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel()
        .setFunctionFilterList(this.functionFilterWizardPage.getFunctions());
    this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel().setParamFilterList(this.paramFilterWizardPage.getLabels());
    this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel()
        .setPlatformFilterModel(this.ecuPlatformFilterWizardPage.getPlatformFilterModel());
    this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel()
        .setSysconFilterList(this.systemConstantFilterWizardPage.getSystemConstants());

    // get current date time for file name
    String currentDate = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss").format(new Date());


    // create outputdir if it doesnt exist
    new File(ApicConstants.CDA_OUTPUT_DIR).mkdirs();

    // construct output file name
    String outputFile = ApicConstants.CDA_OUTPUT_DIR + File.separator + ApicConstants.CDA_ZIPPED_FILE_NAME +
        currentDate + ApicConstants.ZIP_FILE_EXT;

    // construct output file directory
    String outputFileDir = outputFile.substring(0, outputFile.lastIndexOf("."));

    try {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .showView("org.eclipse.ui.views.ProgressView");
    }
    catch (PartInitException e1) {
      CDMLogger.getInstance().error(e1.getMessage(), e1, Activator.PLUGIN_ID);
    }

    enableUIElements(false);

    // create job
    Job caldataJob = new CalDataAnalyzerInvokationJob("Calibration Data Analysis",
        this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel(), outputFile);

    // add job listener
    caldataJob.addJobChangeListener(new JobChangeAdapter() {

      @Override
      public void done(final IJobChangeEvent event) {
        // if job is done, unzip result file and set model in page
        if (event.getResult().isOK()) {
          try {
            ZipUtils.unzip(outputFile, outputFileDir);
            createResultModel(outputFileDir);
          }
          catch (IOException e) {
            CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
        }
        else {
          Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
              if (CaldataAnalyzerWizard.this.caldataAnalyzerPage.getResultModel().getResultFiles().isEmpty()) {
                CaldataAnalyzerWizard.this.caldataAnalyzerPage.getButton1().setEnabled(true);
                CaldataAnalyzerWizard.this.caldataAnalyzerPage.getButton2().setEnabled(false);
              }
              else {
                CaldataAnalyzerWizard.this.caldataAnalyzerPage.getButton1().setEnabled(true);
                CaldataAnalyzerWizard.this.caldataAnalyzerPage.getButton2().setEnabled(true);
              }
            }
          });

        }
      }
    });
    canFinish = true;
    caldataJob.schedule();

    return canFinish;
  }


  /**
   * Method to enable/disable toolbars
   *
   * @param enable
   */
  private void enableUIElements(final boolean enable) {

    if (!enable) {
      this.caldataAnalyzerPage.getFileTabViewer().setInput(new ArrayList<CaldataAnalyzerResultFileModel>());
      this.caldataAnalyzerPage.getFileTabViewer().refresh();
      this.caldataAnalyzerPage.getResultModel().setSummaryModel(new CaldataAnalyzerResultSummary());
      this.caldataAnalyzerPage.refreshSummary();
    }
    this.caldataAnalyzerPage.getToolBarManager().getControl().setEnabled(enable);
    this.caldataAnalyzerPage.getFileToolbarManager().getControl().setEnabled(enable);
    this.caldataAnalyzerPage.getActionSet().getSaveFilesAction().setEnabled(false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performCancel() {
    if (this.caldataAnalyzerPage.getResultModel().getResultFiles().isEmpty()) {
      this.caldataAnalyzerPage.getButton1().setEnabled(true);
      this.caldataAnalyzerPage.getButton2().setEnabled(false);
    }
    else {
      this.caldataAnalyzerPage.getButton1().setEnabled(true);
      this.caldataAnalyzerPage.getButton2().setEnabled(true);
    }
    return super.performCancel();
  }


  /**
   * Method to create result model
   *
   * @param outputDir
   */
  private void createResultModel(final String outputDir) {
    CaldataAnalyzerResultModel resultModel = new CaldataAnalyzerResultModel();

    List<CaldataAnalyzerResultFileModel> resultFileList = new ArrayList<>();
    List<CaldataAnalyzerResultFileModel> jsonFileList = new ArrayList<>();

    File folder = new File(outputDir);

    if (new File(outputDir).exists() && (folder.listFiles().length > 0)) {
      resultModel.setOutputDirectory(outputDir);
      for (File file : folder.listFiles()) {
        CaldataAnalyzerResultFileModel resultFile =
            new CaldataAnalyzerResultFileModel(file.getName(), file.getAbsolutePath(), (file.length()) / 1000);
        if (resultFile.getFileName().endsWith(".json")) {
          jsonFileList.add(resultFile);
        }
        else {
          resultFileList.add(resultFile);
        }
      }
      resultModel.setResultFiles(resultFileList);
      resultModel.setJsonFiles(jsonFileList);
    }

    // create summary model from json
    for (CaldataAnalyzerResultFileModel caldataAnalyzerResultFileModel : jsonFileList) {
      if (caldataAnalyzerResultFileModel.getFileName().equalsIgnoreCase(ApicConstants.CALDATA_JSON_FILE_NAME)) {
        try {
          CaldataAnalyzerResultSummary summary = JsonUtil.toModel(
              new File(caldataAnalyzerResultFileModel.getFilePath()), CaldataAnalyzerResultSummary.class);
          if (summary != null) {
            resultModel.setSummaryModel(summary);
          }
        }
        catch (InvalidInputException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }

    this.caldataAnalyzerPage.setResultModel(resultModel);

    // update ui
    updateEditorPage(resultFileList);

    // add output files dir to list
    this.caldataAnalyzerPage.getOutputDirectoryList().add(resultModel.getOutputDirectory());

  }


  /**
   * Update page with results
   *
   * @param resultFileList - result files
   */
  private void updateEditorPage(final List<CaldataAnalyzerResultFileModel> resultFileList) {
    this.caldataAnalyzerPage.setActive(true);
    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        CaldataAnalyzerWizard.this.caldataAnalyzerPage.getFileTabViewer().setInput(resultFileList);
        CaldataAnalyzerWizard.this.caldataAnalyzerPage.getButton1().setEnabled(true);
        CaldataAnalyzerWizard.this.caldataAnalyzerPage.getButton2().setEnabled(true);
        CaldataAnalyzerWizard.this.enableUIElements(true);
        CaldataAnalyzerWizard.this.caldataAnalyzerPage.getActionSet().getSaveFilterAction().setEnabled(true);
        CaldataAnalyzerWizard.this.caldataAnalyzerPage.refreshSummary();
        if (resultFileList.isEmpty()) {
          CDMLogger.getInstance().infoDialog(
              "For the given filter criteria, no result files were generated. Please try again.", Activator.PLUGIN_ID);
        }
      }
    });

  }


  @Override
  public void addPages() {

    setWindowTitle("Calibration Data Analysis");

    this.paramFilterWizardPage = new CaldataAnalyzerParamFilterWizardPage(CommonUIConstants.CALDATA_PARAM_FILTER_PAGE);

    this.functionFilterWizardPage =
        new CaldataAnalyzerFunctionFilterWizardPage(CommonUIConstants.CALDATA_FUNCTION_FILTER_PAGE);

    this.systemConstantFilterWizardPage =
        new CaldataAnalyzerSysConFilterWizardPage(CommonUIConstants.CALDATA_SYSCON_FILTER_PAGE);

    this.caldataAnalyzerCustFilterWizardPage =
        new CaldataAnalyzerCustFilterWizardPage(CommonUIConstants.CALDATA_CUSTOMER_FILTER_PAGE);

    this.ecuPlatformFilterWizardPage =
        new CaldataAnalyzerECUPlatWizardPage(CommonUIConstants.CALDATA_PLATFORM_FILTER_PAGE);

    if (!this.isNewAnalysis) {
      // set param filters
      this.paramFilterWizardPage
          .setLabels(this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel().getParamFilterList());
      // set function filters
      this.functionFilterWizardPage
          .setFunctions(this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel().getFunctionFilterList());
      // set syscon filters
      this.systemConstantFilterWizardPage
          .setSystemConstants(this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel().getSysconFilterList());
      // set customer filters
      this.caldataAnalyzerCustFilterWizardPage
          .setCustomerFilterModel(this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel().getCustomerFilterModel());
      // set platform filters
      this.ecuPlatformFilterWizardPage
          .setPlatformFilterModel(this.caldataAnalyzerPage.getCaldataAnalyzerFilterModel().getPlatformFilterModel());
    }


    // add pages
    addPage(this.paramFilterWizardPage);
    addPage(this.functionFilterWizardPage);
    addPage(this.systemConstantFilterWizardPage);
    addPage(this.caldataAnalyzerCustFilterWizardPage);
    addPage(this.ecuPlatformFilterWizardPage);
  }

  @Override
  public boolean canFinish() {
    return true;
  }

  @Override
  public IWizardPage getNextPage(final IWizardPage currentPage) {
    if (currentPage instanceof CaldataAnalyzerParamFilterWizardPage) {
      CaldataAnalyzerParamFilterWizardPage myPage = (CaldataAnalyzerParamFilterWizardPage) currentPage;
      if (!myPage.isPageComplete()) {
        return currentPage;
      }
    }
    else if (currentPage instanceof CaldataAnalyzerFunctionFilterWizardPage) {
      CaldataAnalyzerFunctionFilterWizardPage myPage = (CaldataAnalyzerFunctionFilterWizardPage) currentPage;
      if (!myPage.isPageComplete()) {
        return currentPage;
      }
    }

    else if (currentPage instanceof CaldataAnalyzerSysConFilterWizardPage) {
      CaldataAnalyzerSysConFilterWizardPage myPage = (CaldataAnalyzerSysConFilterWizardPage) currentPage;
      if (!myPage.isPageComplete()) {
        return currentPage;
      }
    }

    else if (currentPage instanceof CaldataAnalyzerCustFilterWizardPage) {
      CaldataAnalyzerCustFilterWizardPage myPage = (CaldataAnalyzerCustFilterWizardPage) currentPage;
      if (!myPage.isPageComplete()) {
        return currentPage;
      }
    }


    else if (currentPage instanceof CaldataAnalyzerECUPlatWizardPage) {
      CaldataAnalyzerECUPlatWizardPage myPage = (CaldataAnalyzerECUPlatWizardPage) currentPage;
      if (!myPage.isPageComplete()) {
        return currentPage;
      }
    }
    return super.getNextPage(currentPage);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    this.caldataAnalyzerPage.getButton1().setEnabled(true);
    this.caldataAnalyzerPage.getButton2().setEnabled(true);
    super.dispose();
  }
}
