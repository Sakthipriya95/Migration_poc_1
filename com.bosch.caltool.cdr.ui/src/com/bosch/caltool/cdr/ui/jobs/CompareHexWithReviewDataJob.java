/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CompHexCheckSSDReportAction;
import com.bosch.caltool.cdr.ui.editors.CompHexWithCDFxEditor;
import com.bosch.caltool.cdr.ui.editors.CompHexWithCDFxEditorInput;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.jobs.A2lWPDefnVersionFetchChildJob;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler.CALDATA_FILE_TYPE;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.review.PidcData;
import com.bosch.caltool.icdm.model.comphex.CompHexMetaData;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;
import com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.jobs.AbstractChildJob;
import com.bosch.rcputils.jobs.ChildJobFamily;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * ICDM-2496 class that carries out the job during comparison of Hex file with review data
 *
 * @author mkl2cob
 */
public class CompareHexWithReviewDataJob extends Job {

  /**
   * PIDCVariant
   */
  private final PidcVariant selctedVar;
  /**
   * A2LFile
   */
  private final PidcA2LBO pidcA2lBO;

  /**
   * The meta data.
   */
  private final CompHexMetaData metaData;

  /**
   * Instantiates a new compare hex with review data job.
   *
   * @param jobName Job name
   * @param pidcA2lBO the pidc A 2 l BO
   * @param selctedVar the selcted var
   * @param metaData the meta data
   */
  public CompareHexWithReviewDataJob(final String jobName, final PidcA2LBO pidcA2lBO, final PidcVariant selctedVar,
      final CompHexMetaData metaData) {

    super(jobName);
    this.pidcA2lBO = pidcA2lBO;
    this.selctedVar = selctedVar;
    this.metaData = metaData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {

    // ICDM-2608
    Job.getJobManager().resume();

    CDMLogger.getInstance().debug("Comparing HEX file with Review Data ...");
    monitor.beginTask("", AbstractChildJob.JOB_TOTAL);
    monitor.worked(AbstractChildJob.JOB_BEGIN);

    CDMLogger.getInstance().debug("Fetching A2L information ...");

    // A2L file download Job
    A2LEditorDataProvider a2lDataProvider = null;
    try {
      a2lDataProvider = new A2LEditorDataProvider(this.pidcA2lBO.getPidcA2lId(), true);
    }
    catch (IcdmException e1) {
      CDMLogger.getInstance().error(e1.getLocalizedMessage(), e1);
    }
    if ((a2lDataProvider == null) || (a2lDataProvider.getA2lFileInfoBO().getA2lFileInfo() == null)) {
      CDMLogger.getInstance().debug("Error in retrieving A2l File details");
      return Status.CANCEL_STATUS;
    }
    monitor.worked(50);
    // A2L WP Definition fetch job
    ChildJobFamily subJobFamily = new ChildJobFamily(this);
    subJobFamily.add(new A2lWPDefnVersionFetchChildJob(a2lDataProvider, false));

    // questionnaire status constructing jobs
    Long variantId = null == this.selctedVar ? null : this.selctedVar.getId();
    WPRespQnaireRespeVersStatusJob qnaireRespeVersStatusJob = new WPRespQnaireRespeVersStatusJob(
        this.pidcA2lBO.getPidcVersion().getId(), variantId, this.pidcA2lBO.getPidcA2l().getName());
    subJobFamily.add(qnaireRespeVersStatusJob);

    CDMLogger.getInstance().debug("Parsing Hex file ..");
    ConcurrentMap<String, CalData> calDataObjectsFromHex = null;
    try {
      calDataObjectsFromHex = fillCalDataMap(a2lDataProvider.getA2lFileInfoBO().getA2lFileInfo());
    }
    catch (IcdmException e1) {
      CDMLogger.getInstance().errorDialog(e1.getMessage(), e1, Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }

    monitor.worked(60);

    try {
      subJobFamily.execute(monitor);
    }
    catch (OperationCanceledException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
      return Status.CANCEL_STATUS;
    }
    catch (InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
      Thread.currentThread().interrupt();
      return Status.CANCEL_STATUS;
    }

    if (!subJobFamily.isResultOK()) {
      return Status.CANCEL_STATUS;
    }


    CDMLogger.getInstance().debug("Compare hex process initiated ...");
    // Compare Hex Webservice call
    PidcData pidcData = new PidcData();
    pidcData.setA2lFileId(this.pidcA2lBO.getA2lFile().getId());
    pidcData.setPidcA2lId(this.pidcA2lBO.getPidcA2lId());
    if (this.selctedVar != null) {
      pidcData.setSelPIDCVariantId(this.selctedVar.getId());
    }
    pidcData.setSourcePidcVerId(this.pidcA2lBO.getPidcVersion().getId());
    this.metaData.setPidcData(pidcData);
    File hexFile = new File(this.metaData.getSrcHexFilePath());
    this.metaData.setHexFileName(hexFile.getName());
    CompHexResponse resp = null;
    try {
      resp = new CompHexWithCDFxServiceClient().getCompHexResult(this.metaData);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error occurred while running Compare Hex : " + e.getLocalizedMessage(), e,
          Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.worked(80);

    CDMLogger.getInstance().debug("Opening compare editor ...");
    monitor.beginTask("Opening compare editor", IProgressMonitor.UNKNOWN);
    CompHexWithCDFxDataHandler dataHandler = new CompHexWithCDFxDataHandler(this.metaData.getSrcHexFilePath(),
        calDataObjectsFromHex, a2lDataProvider, this.selctedVar, resp);
    // open compare editor
    dataHandler.setReportQnaireRespWrapper(qnaireRespeVersStatusJob.getCdrReportQnaireRespWrapper());
    openCompareEditor(dataHandler);
    monitor.worked(85);
    if (CommonUtils.isNotEmpty(dataHandler.getCompHexResponse().getErrorMsgSet())) {
      dataHandler.getCompHexResponse().getErrorMsgSet()
          .forEach(e -> CDMLogger.getInstance().error(e, Activator.PLUGIN_ID));
    }
    monitor.done();
    return Status.OK_STATUS;
  }


  /**
   * Open compare editor.
   *
   * @param calDataObjectsFromHex the cal data objects from hex
   * @param reportData the report data
   * @param result the result
   * @param cdrReport the cdr report
   * @param compliReviewSummary the compli review summary
   * @param a2lEditorDP A2LEditorDataProvider
   */
  private void openCompareEditor(final CompHexWithCDFxDataHandler compHexDataHdlr) {
    // Calculate Latest 'Wp Finished' status and update in DB while generating compare Hex
    compHexDataHdlr.getCdrReportData().calWpFinStatusAndFillWPFinStatusMap();
    final CompHexWithCDFxEditorInput editorInput = new CompHexWithCDFxEditorInput();
    editorInput.setCompHexDataHdlr(compHexDataHdlr);

    Display.getDefault().asyncExec(() -> {

      try {
        IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .openEditor(editorInput, CompHexWithCDFxEditor.EDITOR_ID);
        if (openEditor instanceof CompHexWithCDFxEditor) {
          CompHexWithCDFxEditor cdrReportEditor = (CompHexWithCDFxEditor) openEditor;
          // set focus to the editor opened
          cdrReportEditor.setFocus();
          CDMLogger.getInstance().debug("Compare HEX with Review Data Editor opened in editor");

          // Download CSSD file
          CompHexCheckSSDReportAction excelRptAction = new CompHexCheckSSDReportAction(compHexDataHdlr);
          excelRptAction.run();

          // Open check ssd report only if there are compli param status is 'NOT OK'
          if (CommonUtils.isFileAvailable(compHexDataHdlr.getCssdExcelReportPath()) &&
              (compHexDataHdlr.getCompHexResponse().isCompliCheckFailed() ||
                  compHexDataHdlr.getCompHexResponse().isQSSDCheckFailed())) {
            showCompliFailureMsg();
          }
        }
      }
      catch (PartInitException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    });
  }

  /**
   *
   */
  private void showCompliFailureMsg() {
    String message;
    try {
      message = new CommonDataBO().getMessage(CDRConstants.HEX_COMPARE, ApicConstants.HEX_COMPARE_COMPLI_CHECK_MSG);
      // Open dialog
      MessageDialogUtils.getInfoMessageDialog("Hex Comparison", message);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Fill cal data map.
   *
   * @param a2lFileInfo the a 2 l file info
   * @return the concurrent map
   * @throws IcdmException the icdm exception
   */
  private ConcurrentMap<String, CalData> fillCalDataMap(final A2LFileInfo a2lFileInfo) throws IcdmException {
    ConcurrentMap<String, CalData> calDataObjectsFromHex;
    // New method to get the Caldata Hex file map.
    File hexFile = new File(this.metaData.getSrcHexFilePath());
    try (FileInputStream hexInputStream = new FileInputStream(hexFile)) {
      CALDATA_FILE_TYPE fileType = CALDATA_FILE_TYPE.getTypeFromFileName(hexFile.getName());
      CaldataFileParserHandler parserHandler = new CaldataFileParserHandler(ParserLogger.getInstance(), a2lFileInfo);
      calDataObjectsFromHex = new ConcurrentHashMap<>(parserHandler.getCalDataObjects(fileType, hexInputStream));

      CDMLogger.getInstance().debug("Number of Caldata objects in the file {}", calDataObjectsFromHex.size());
    }
    // For now Keep it as exception.throw the Exception from Hex parser
    catch (Exception exp) {
      throw new IcdmException(exp.getMessage() + " - \n" + hexFile.getName(), exp);
    }
    return calDataObjectsFromHex;
  }


}
