/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.CompliReviewDialog;
import com.bosch.caltool.cdr.ui.views.providers.HexFileSelectionRowProvider;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.JsonUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewResponse;
import com.bosch.caltool.icdm.model.cdr.ExcelReportTypeEnum;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CompliReviewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Class for generate button selection adapter of compliance review
 *
 * @author svj7cob
 */
public class CompliRvwGenBtnSelAdapter {

  /**
   * String constant for error
   */
  private static final String ERROR = "Error - ";
  /**
   * the compli review dialog
   */
  private final CompliReviewDialog compliReviewDialog;

  /**
   * @param compliReviewDialog compliReviewDialog
   */
  public CompliRvwGenBtnSelAdapter(final CompliReviewDialog compliReviewDialog) {
    this.compliReviewDialog = compliReviewDialog;
  }

  /**
   * @return the selection adapter for the compli review dialog
   */
  public SelectionAdapter getCompliReviewSelectionAdapter() {
    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        try {
          if (!CompliRvwGenBtnSelAdapter.this.compliReviewDialog.isProcessFlagSet()) {
            final String a2lFilePath = CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getA2lNameText().getText();
            final String zipFilePath =
                CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getZipExportPathText().getText();
            CompliReviewInputMetaData jsonInput = getJsonInput();
            ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
            dialog.run(true, true, new IRunnableWithProgress() {

              @Override
              public void run(final IProgressMonitor monitor) {

                CompliReviewServiceClient client = new CompliReviewServiceClient();
                monitor.beginTask("Fetching compliance review...", 100);
                monitor.setTaskName("Checking compliance review");
                monitor.worked(20);
                try {
                  if (CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getPidcA2l() != null) {
                    client.executeCompliReviewUsingPidcA2lId(
                        String.valueOf(CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getPidcA2l().getId()),
                        a2lFilePath, jsonInput, getHexFilePathSet(), zipFilePath);
                  }
                  else {
                    client.executeCompliReview(a2lFilePath, jsonInput, getHexFilePathSet(), zipFilePath);
                  }
                  CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setSid(client.getLastServiceId());
                  CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setExecutionId(client.getLastExecutionId());
                  if (Files.exists(Paths.get(zipFilePath))) {
                    Display.getDefault().asyncExec(new Runnable() {

                      @Override
                      public void run() {
                        try {
                          ZipUtils.unzip(
                              CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getZipExportPathText().getText(), null);
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setProcessFlagSet(true);
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getGenerateBtn().setEnabled(false);
                          String zipFolderPath = ZipUtils.getZipFolderPath(
                              CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getZipExportPathText().getText());
                          String path = zipFolderPath.replace(".zip", "");
                          File file = new File(path);

                          File[] listFiles = file.listFiles();
                          CompliReviewResponse response = null;
                          for (File file2 : listFiles) {
                            if (file2.getName().contains("SSDError")) {
                              writeFileContents(file2);
                            }
                            if ("CompliReviewResult.json".equals(file2.getName())) {
                              response = JsonUtil.toModel(file2, CompliReviewResponse.class);
                            }
                          }
                          if (response != null) {
                            CommonUiUtils.openFile(zipFolderPath + File.separator + response.getReportFileName());
                          }
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getOpenZipBtn().setEnabled(true);
                        }
                        catch (IOException | InvalidInputException exp) {
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setExecutionId(client.getLastExecutionId());
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setSid(client.getLastServiceId());
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setErrMsg(exp.getLocalizedMessage());
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setProcessFlagSet(true);
                          CDMLogger.getInstance().error("Error in unzipping the file - " + exp.getMessage(), exp);
                          CDMLogger.getInstance().errorDialog("Error in unzipping the file - " + exp.getMessage(), exp,
                              Activator.PLUGIN_ID);
                          CDMLogger.getInstance().error(exp.getMessage(), exp);
                          disableGenButtonInError();
                          return;
                        }
                      }

                      private void writeFileContents(final File ssdFile) {

                        try (BufferedReader reader = new BufferedReader(new FileReader(ssdFile))) {

                          StringBuilder stringBuilder = new StringBuilder();
                          String line = null;
                          String ls = System.getProperty("line.separator");
                          while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            stringBuilder.append(ls);
                          }
                          // delete the last new line separator
                          stringBuilder.deleteCharAt(stringBuilder.length() - 1);


                          String content = stringBuilder.toString();
                          CDMLogger.getInstance().error(content, Activator.PLUGIN_ID);

                        }
                        catch (IOException exp) {
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setExecutionId(client.getLastExecutionId());
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setSid(client.getLastServiceId());
                          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setErrMsg(exp.getLocalizedMessage());
                          CDMLogger.getInstance().errorDialog("Error in reading error file - " + exp.getMessage(), exp,
                              Activator.PLUGIN_ID);
                        }

                      }

                    });
                  }
                  else {
                    disableGenButtonInError();
                  }
                }
                catch (ApicWebServiceException exp) {
                  CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setErrCode(exp.getErrorCode());
                  CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setProcessFlagSet(true);
                  CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setExecutionId(client.getLastExecutionId());
                  CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setSid(client.getLastServiceId());
                  CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setErrMsg(exp.getLocalizedMessage());
                  Set<String> pathSet = new TreeSet<>();
                  String errorMsg = exp.getMessage();
                  if ("ICDM_ERR".equalsIgnoreCase(exp.getErrorCode()) && (errorMsg != null) &&
                      errorMsg.contains(".ssd")) {
                    String[] filePaths = errorMsg.split("\\|");
                    for (String path : filePaths) {
                      if (!path.contains("Error in ssd file")) {
                        pathSet.add(path.trim());
                      }
                    }

                  }
                  if (!pathSet.isEmpty()) {
                    try {
                      String dirPath = new ReviewServiceClient().downloadFilesFromServer(pathSet,
                          CommonUtils.getICDMTmpFileDirectoryPath());
                      CDMLogger.getInstance().errorDialog(
                          "Syntax errror in SSD file. Please check " + dirPath + " to get more information", exp,
                          Activator.PLUGIN_ID);
                    }
                    catch (ApicWebServiceException ex) {
                      CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setErrMsg(ex.getLocalizedMessage());
                      CDMLogger.getInstance().errorDialog(ex.getLocalizedMessage(), ex, Activator.PLUGIN_ID);
                    }
                  }
                  else {
                    CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setErrMsg(exp.getLocalizedMessage());
                    CDMLogger.getInstance().error(ERROR + exp.getMessage(), exp);
                    CDMLogger.getInstance().errorDialog(ERROR + exp.getMessage(), exp, Activator.PLUGIN_ID);
                  }

                  disableGenButtonInError();
                  return;
                }
                monitor.worked(70);
                monitor.setTaskName("Exporting zip report");
                monitor.worked(100);
                monitor.done();
              }
            });
          }
        }
        catch (InvocationTargetException | InterruptedException exp) {
          CompliRvwGenBtnSelAdapter.this.compliReviewDialog.setProcessFlagSet(true);
          CDMLogger.getInstance().error(ERROR + exp.getLocalizedMessage(), exp);
          disableGenButtonInError();
          return;
        }
      }


    };
  }

  private void disableGenButtonInError() {
    if ((null != CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getGenerateBtn()) &&
        !CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getGenerateBtn().isDisposed()) {
      this.compliReviewDialog.getGenerateBtn().setText(CompliReviewDialog.GENERATE_BUTTON_CONSTANT);
      this.compliReviewDialog.getGenerateBtn().setEnabled(false);
    }
  }

  /**
   * @return
   */
  private CompliReviewInputMetaData getJsonInput() {
    CompliReviewInputMetaData compliReviewInputMetaData = new CompliReviewInputMetaData();
    compliReviewInputMetaData.setA2lFileName(FilenameUtils.getName(CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getA2lFilePath()));
    compliReviewInputMetaData.setPverName(CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getPverNameText().getText());
    compliReviewInputMetaData.setPverVariant(CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getPverVariantText().getText());
    compliReviewInputMetaData.setPverRevision(CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getPverRevisionText().getText());
    compliReviewInputMetaData
        .setPredecessorCheck(CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getPredecessorCheckbox().getSelection());
    if (CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getOneFilePerCheckRadio().getSelection()) {
      compliReviewInputMetaData.setDatafileoption(ExcelReportTypeEnum.ONEFILECHECK);
    }
    else if (CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getSingleFileWithSumRadio().getSelection()) {
      compliReviewInputMetaData.setDatafileoption(ExcelReportTypeEnum.SINGLEFILEWITHSUMMARY);
    }
    else if (CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getSingleFileWithRedSumRadio().getSelection()) {
      compliReviewInputMetaData.setDatafileoption(ExcelReportTypeEnum.SINGLEFILEWITHREDUCTIONSUMMARY);
    }
    if(CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getPidcA2l() != null) {
    compliReviewInputMetaData.setPidcA2L(CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getPidcA2l().getId());
    }
    Map<Long, String> hexfileIdxMap = new HashMap<>();
    Map<Long, String> dstNameMap = new HashMap<>();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();

    Iterator<HexFileSelectionRowProvider> iterator =
        CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getHexFileRowProviderList().iterator();
    while (iterator.hasNext()) {
      HexFileSelectionRowProvider prov = iterator.next();
      Long indexId = Long.valueOf(prov.getIndex());
      Long pidcObjectId = (null == prov.getPidcVariantId()) ? prov.getPidcVersId() : prov.getPidcVariantId();
      if (null != pidcObjectId) {
        hexFilePidcElement.put(indexId, pidcObjectId);
      }

      String dstName = CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getDstDetailsMap().get(indexId.intValue());
      if (dstName != null) {
        dstNameMap.put(indexId, dstName);
      }
      hexfileIdxMap.put(indexId, prov.getHexFileName());

    }
    compliReviewInputMetaData.setHexfileIdxMap(hexfileIdxMap);
    compliReviewInputMetaData.setDstMap(dstNameMap);
    compliReviewInputMetaData.setHexFilePidcElement(hexFilePidcElement);
    compliReviewInputMetaData.setWebflowID(CompliRvwGenBtnSelAdapter.this.compliReviewDialog.getWebFlowIdText().getText());
    return compliReviewInputMetaData;
  }

  private Set<String> getHexFilePathSet() {
    Set<String> set = new HashSet<>();
    for (HexFileSelectionRowProvider hexFileSelectionRowProvider : CompliRvwGenBtnSelAdapter.this.compliReviewDialog
        .getHexFileRowProviderList()) {
      set.add(hexFileSelectionRowProvider.getHexFilePath());
    }
    return set;
  }
}
