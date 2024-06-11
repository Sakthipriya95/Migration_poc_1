/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditor;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditorInput;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.QSSDResValues;
import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.ws.rest.client.cdr.DaDataAssessmentServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author AJK2COB
 */
public class DataAssmntBaselineHandler {

  /**
   * Data Assessment Id
   */
  private final Long dataAssessmentBaselineId;
  /**
   * Baseline editor flag
   */
  private boolean isBaselineOpen;
  /**
   * Data Assessment Report data
   */
  private DataAssessmentReport dataAssessmentReport;
  /**
   * The stat param reviewed.
   **/
  private int statParamReviewed = 0;
  /**
   * The rvwd and equal count.
   **/
  private int rvwdAndEqualCount = 0;
  /**
   * The stat param rvwd not equal.
   **/
  private int statParamRvwdNotEqual = 0;
  /**
   * The stat param with RB resp, qnaire rvwd.
   **/
  private int paramWithBoschRespQnaireRvw = 0;
  /**
   * Compare hex statistics
   */
  private final CompHexStatistics compHexStats = new CompHexStatistics();

  /**
   * @param dataAssessmentBaselineId Data Assessment Id
   */
  public DataAssmntBaselineHandler(final Long dataAssessmentBaselineId) {
    super();
    this.dataAssessmentBaselineId = dataAssessmentBaselineId;
  }

  /**
   * @return true - baseline editor opened; false - baseline editor not opened
   */
  public boolean open() {
    ProgressMonitorDialog progressDialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
    try {
      progressDialog.run(true, true, monitor -> {
        try {
          monitor.beginTask("Fetching Data Assessment Baseline data...", 100);
          monitor.worked(20);
          this.dataAssessmentReport = getDataAssessmentBaselineData();
          DataAssmntReportDataHandler.fillQnairesRvdUserDisplayName(this.dataAssessmentReport);
          monitor.setTaskName("Data Assessment Baseline data has been fetched...");
          monitor.worked(40);
          A2LEditorDataProvider a2lDataProvider = getA2lDataProvider();
          if ((a2lDataProvider == null) || (a2lDataProvider.getA2lFileInfoBO() == null) ||
              (a2lDataProvider.getA2lFileInfoBO().getA2lFileInfo() == null)) {
            CDMLogger.getInstance().debug("Error in retrieving A2l File details");
          }
          else {
            monitor.setTaskName("Filling required statistics...");
            monitor.worked(60);
            fillDepCharsNameAndCalculateCompHexStats(
                a2lDataProvider.getA2lFileInfoBO().getA2lFileInfo().getAllModulesLabels());
            fillAdditionalCompareHexStatics(a2lDataProvider.getA2lFileInfoBO().getA2lFileInfo().getAllModulesLabels());
            monitor.setTaskName("Opening Baseline...");
            monitor.worked(80);
            openDataAssessmentReportBaselineEditor(a2lDataProvider.getA2lFileInfoBO());
          }
          monitor.setTaskName("Opening Baseline...");
          monitor.worked(100);
          monitor.done();
        }
        catch (ApicWebServiceException ex) {
          CDMLogger.getInstance().errorDialog(CommonUIConstants.EXCEPTION + ex.getMessage(), Activator.PLUGIN_ID);
        }
      });
    }
    catch (InvocationTargetException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    catch (InterruptedException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      Thread.currentThread().interrupt();
    }
    return this.isBaselineOpen;
  }

  /**
   * Call service to get the baseline data
   *
   * @return
   * @throws ApicWebServiceException
   */
  private DataAssessmentReport getDataAssessmentBaselineData() throws ApicWebServiceException {
    return new DaDataAssessmentServiceClient().getDataAssessmentBaseline(this.dataAssessmentBaselineId);
  }

  /**
   * Get the A2lDataProvider
   *
   * @return
   */
  private A2LEditorDataProvider getA2lDataProvider() {
    A2LEditorDataProvider a2lDataProvider = null;
    try {
      a2lDataProvider = new A2LEditorDataProvider(this.dataAssessmentReport.getPidcA2lId(), true);
    }
    catch (IcdmException e1) {
      CDMLogger.getInstance().error(e1.getLocalizedMessage(), e1);
    }
    return a2lDataProvider;
  }

  /**
   * Fill dependency chars name and calculate comp hex statistics
   *
   * @param allLabelsMap
   */
  private void fillDepCharsNameAndCalculateCompHexStats(final Map<String, Characteristic> allLabelsMap) {
    if (CommonUtils.isNotEmpty(this.dataAssessmentReport.getDataAssmntCompHexData().getDaCompareHexParam()) &&
        CommonUtils.isNotEmpty(allLabelsMap)) {
      this.dataAssessmentReport.getDataAssmntCompHexData().getDaCompareHexParam().stream()
          .forEach(daCompareHexParam -> {
            if (allLabelsMap.containsKey(daCompareHexParam.getParamName())) {
              Characteristic characteristic = allLabelsMap.get(daCompareHexParam.getParamName());
              if (CommonUtils.isNotNull(characteristic.getDependentCharacteristic()) &&
                  CommonUtils.isNotNull(characteristic.getDependentCharacteristic().getCharacteristicName())) {
                daCompareHexParam.setDepCharsName(characteristic.getDependentCharacteristic().getCharacteristicName());
              }
            }
            computeCompliStat(daCompareHexParam);
            computeQssdFailure(daCompareHexParam);
            addParamCdrResultStat(daCompareHexParam);
            setRvwParamWithBoschRespForCompletedQnaire(daCompareHexParam);
          });
    }
  }

  /**
   * Calculate compliance param stats
   *
   * @param daCompHexParam
   */
  private void computeCompliStat(final DaCompareHexParam daCompHexParam) {
    if (CommonUtils.isNotNull(this.dataAssessmentReport.getCompliParamInA2L()) &&
        CommonUtils.isNotNull(this.dataAssessmentReport.getCompliParamPassed()) &&
        CommonUtils.isNotNull(this.dataAssessmentReport.getCompliParamCSSDFail()) &&
        CommonUtils.isNotNull(this.dataAssessmentReport.getCompliParamNoRuleFail()) &&
        CommonUtils.isNotNull(this.dataAssessmentReport.getCompliParamSSD2RVFail())) {
      this.compHexStats.setStatCompliParamInA2L(this.dataAssessmentReport.getCompliParamInA2L());
      this.compHexStats.setStatCompliParamPassed(this.dataAssessmentReport.getCompliParamPassed());
      this.compHexStats.setStatCompliCssdFailed(this.dataAssessmentReport.getCompliParamCSSDFail());
      this.compHexStats.setStatCompliNoRuleFailed(this.dataAssessmentReport.getCompliParamNoRuleFail());
      this.compHexStats.setStatCompliSSDRvFailed(this.dataAssessmentReport.getCompliParamSSD2RVFail());
    }
    else {
      if (daCompHexParam.isCompli()) {
        this.compHexStats.setStatCompliParamInA2L(this.compHexStats.getStatCompliParamInA2L() + 1);
        if (CommonUtils.isNotNull(daCompHexParam.getCompliResult())) {
          if (daCompHexParam.getCompliResult().getUiValue().equals(CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType())) {
            this.compHexStats.setStatCompliParamPassed(this.compHexStats.getStatCompliParamPassed() + 1);
          }
          else if (daCompHexParam.getCompliResult().getUiValue()
              .equals(CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType())) {
            this.compHexStats.setStatCompliCssdFailed(this.compHexStats.getStatCompliCssdFailed() + 1);
          }
          else if (daCompHexParam.getCompliResult().getUiValue()
              .equals(CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getUiType())) {
            this.compHexStats.setStatCompliNoRuleFailed(this.compHexStats.getStatCompliNoRuleFailed() + 1);
          }
          else if (daCompHexParam.getCompliResult().getUiValue()
              .equals(CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType())) {
            this.compHexStats.setStatCompliSSDRvFailed(this.compHexStats.getStatCompliSSDRvFailed() + 1);
          }
        }
      }
    }
  }

  /**
   * QSSD failure param
   *
   * @param daCompHexParam
   */
  private void computeQssdFailure(final DaCompareHexParam daCompHexParam) {
    if (CommonUtils.isNotNull(this.dataAssessmentReport.getQssdParamFail())) {
      this.compHexStats.setStatQSSDParamFailed(this.dataAssessmentReport.getQssdParamFail());
    }
    else {
      if (daCompHexParam.isQssdParameter() && QSSDResValues.QSSD.equals(daCompHexParam.getQssdResult())) {
        this.compHexStats.setStatQSSDParamFailed(this.compHexStats.getStatQSSDParamFailed() + 1);
      }
    }
  }

  /**
   * Sets the param result stat.
   *
   * @param param the param
   * @param isEqual the is equal
   */
  private void addParamCdrResultStat(final DaCompareHexParam daCompHexParam) {
    if (daCompHexParam.isReviewed()) {
      this.statParamReviewed++;
      // count parameter that are equal and reviewed
      if (daCompHexParam.isEqual()) {
        this.rvwdAndEqualCount++;
      }
      else {
        this.statParamRvwdNotEqual++;
      }
    }
  }

  /**
   * Review param with Bosch Resp for completed qnaire
   *
   * @param daCompHexParam
   */
  private void setRvwParamWithBoschRespForCompletedQnaire(final DaCompareHexParam daCompHexParam) {
    String qnaireStatus =
        CommonUtils.isNotEmptyString(daCompHexParam.getQnaireStatus()) ? daCompHexParam.getQnaireStatus() : "";
    if (WpRespType.RB.getCode().equals(daCompHexParam.getRespType()) && daCompHexParam.isReviewed() &&
        (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(qnaireStatus) ||
            CDRConstants.DA_QS_STATUS_TYPE.NO_QNAIRE.getDbType().equals(qnaireStatus) ||
            CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(qnaireStatus))) {
      this.paramWithBoschRespQnaireRvw++;
    }
  }

  /**
   * Compare hex statistics
   *
   * @param allLabelsMap
   */
  private void fillAdditionalCompareHexStatics(final Map<String, Characteristic> allLabelsMap) {
    fillCompHexStats(allLabelsMap);
    if (this.dataAssessmentReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount() > 0) {
      this.compHexStats.setStatParamWithBoschRespRvw(findPercentage(
          (double) this.dataAssessmentReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount() -
              this.dataAssessmentReport.getRbParamsNotRvdCount(),
          this.dataAssessmentReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount()));

      this.compHexStats.setStatNumParamInBoschResp(
          this.dataAssessmentReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount());

      this.compHexStats.setStatParamWithBoschRespQnaireRvw(findPercentage(this.paramWithBoschRespQnaireRvw,
          this.dataAssessmentReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount()));

      this.compHexStats.setStatNumParamInBoschRespRvwed(
          this.dataAssessmentReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount() -
              this.dataAssessmentReport.getRbParamsNotRvdCount());

    }
    else {
      this.compHexStats.setStatParamWithBoschRespRvw(String.format("%.3f",
          (double) this.dataAssessmentReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount()));
      this.compHexStats.setStatParamWithBoschRespQnaireRvw(String.format("%.3f",
          (double) this.dataAssessmentReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount()));
    }
    long qnaireWithNegativeAnswers = this.dataAssessmentReport.getDataAssmntQnaires().stream()
        .filter(qnaire -> qnaire.getQnaireNegativeAnsCount() > 0).count();
    this.compHexStats.setStatQnaireNagativeAnswer((int) qnaireWithNegativeAnswers);
    this.dataAssessmentReport.getDataAssmntCompHexData().setCompareHexStatics(this.compHexStats);
  }

  /**
   * Gets the comp hex stats.
   *
   * @param allLabelsMap
   */
  private void fillCompHexStats(final Map<String, Characteristic> allLabelsMap) {
    this.compHexStats.setStatFilteredParam(this.statParamReviewed);
    this.compHexStats.setStatFilteredParamRvwd(this.statParamRvwdNotEqual);
    this.compHexStats.setStatFilteredParamRvwdNotEqual(this.statParamRvwdNotEqual);
    this.compHexStats.setStatParamReviewed(this.statParamReviewed);
    this.compHexStats.setStatParamRvwdEqual(this.rvwdAndEqualCount);
    this.compHexStats.setStatParamRvwdNotEqual(this.statParamRvwdNotEqual);
    this.compHexStats.setStatTotalParamInA2L(CommonUtils.isNotEmpty(allLabelsMap) ? allLabelsMap.size() : 0);
    this.compHexStats.setStatEqualParCount(this.rvwdAndEqualCount);
  }

  /**
   * Find percentage in string format
   *
   * @param obtainedParam
   * @param totalParam
   * @return
   */
  private String findPercentage(final double obtainedParam, final double totalParam) {
    double number = (obtainedParam * 100) / totalParam;
    return CommonUiUtils.displayLangBasedPercentage(String.valueOf(number));
  }

  /**
   * Open Data Assessment Report Baseline editor
   *
   * @param compHexDataHandler
   * @param userNotificationMsg
   * @param dataAssessmentReport
   * @param a2lFileInfoBO
   */
  private void openDataAssessmentReportBaselineEditor(final A2LFileInfoBO a2lFileInfoBO) {
    final DataAssessmentReportEditorInput editorInput = new DataAssessmentReportEditorInput();
    editorInput.setPidcName(this.dataAssessmentReport.getPidcName());
    editorInput.setA2lFileName(this.dataAssessmentReport.getA2lFileName());
    editorInput.setSelctedPidcVariantName(CommonUtils.isNotEmptyString(this.dataAssessmentReport.getPidcVariantName())
        ? this.dataAssessmentReport.getPidcVariantName() : "<NO-VARIANT>");
    editorInput.setBaseline(true);
    editorInput.setBaselineId(this.dataAssessmentBaselineId);

    DataAssmntReportDataHandler dataAssmntReportDataHandler = new DataAssmntReportDataHandler(this.dataAssessmentReport,
        null, a2lFileInfoBO.getA2lParamMap(), a2lFileInfoBO.getCharacteristicsMap());
    editorInput.setDataAssmntReportDataHandler(dataAssmntReportDataHandler);

    Display.getDefault().asyncExec(() -> {
      try {
        IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .openEditor(editorInput, DataAssessmentReportEditor.EDITOR_ID);
        if (openEditor instanceof DataAssessmentReportEditor) {
          DataAssessmentReportEditor dataAssessmentReportEditor = (DataAssessmentReportEditor) openEditor;
          // set focus to the editor opened
          dataAssessmentReportEditor.setFocus();
          this.isBaselineOpen = true;
          CDMLogger.getInstance().debug("Data Assessment Report baseline is opened in the editor");
        }
      }
      catch (PartInitException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    });
  }

}
