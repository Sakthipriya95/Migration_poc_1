/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
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
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditor;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditorInput;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpResponsibility;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.jobs.A2lWPDefnVersionFetchChildJob;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler.CALDATA_FILE_TYPE;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.review.PidcData;
import com.bosch.caltool.icdm.model.comphex.CompHexMetaData;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;
import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentCompareHexData;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.ws.rest.client.a2l.PidcA2lTreeStructureServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.DaDataAssessmentServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.jobs.AbstractChildJob;
import com.bosch.rcputils.jobs.ChildJobFamily;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * job to run during the Data Assessment Report generation
 *
 * @author ajk2cob
 */
public class DataAssessmentReportJob extends Job {

  /**
   * PIDC data
   */
  private final PidcTreeNode pidcTreeNode;
  /**
   * A2LFile
   */
  private final PidcA2LBO pidcA2lBO;
  /**
   * Selected PIDC variant
   */
  private final PidcVariant selctedPidcVariant;
  /**
   * The meta data of selected HEX file
   */
  private final CompHexMetaData hexFileMetaData;

  private static final String QUESTIONNAIRE_DELIMITER = "->";

  /**
   * Instantiates a new Data Assessment Report generation job.
   *
   * @param jobName Job name
   * @param pidcTreeNode PIDC data
   * @param pidcA2lBO PIDC A2L BO
   * @param selctedPidcVariant the selcted variant
   * @param hexFileMetaData the meta data of the HEX file
   */
  public DataAssessmentReportJob(final String jobName, final PidcTreeNode pidcTreeNode, final PidcA2LBO pidcA2lBO,
      final PidcVariant selctedPidcVariant, final CompHexMetaData hexFileMetaData) {
    super(jobName);
    this.pidcTreeNode = pidcTreeNode;
    this.pidcA2lBO = pidcA2lBO;
    this.selctedPidcVariant = selctedPidcVariant;
    this.hexFileMetaData = hexFileMetaData;
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
    A2LFileInfoBO a2lFileInfoBO = a2lDataProvider.getA2lFileInfoBO();
    A2LWPInfoBO a2lWpInfoBO = a2lDataProvider.getA2lWpInfoBO();

    monitor.worked(50);

    // check if any baseline has to be created via data assessment
    checkQnaireBaselines();

    // A2L WP Definition fetch job
    ChildJobFamily subJobFamily = new ChildJobFamily(this);
    subJobFamily.add(new A2lWPDefnVersionFetchChildJob(a2lDataProvider, false));

    // questionnaire status constructing jobs
    Long variantId = null == this.selctedPidcVariant ? null : this.selctedPidcVariant.getId();
    WPRespQnaireRespeVersStatusJob qnaireRespeVersStatusJob = new WPRespQnaireRespeVersStatusJob(
        this.pidcA2lBO.getPidcVersion().getId(), variantId, this.pidcA2lBO.getPidcA2l().getName());
    subJobFamily.add(qnaireRespeVersStatusJob);

    CDMLogger.getInstance().debug("Parsing Hex file ..");
    ConcurrentMap<String, CalData> calDataObjectsFromHex = null;
    try {
      calDataObjectsFromHex = fillCalDataMap(a2lFileInfoBO.getA2lFileInfo());
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
    if (this.selctedPidcVariant != null) {
      pidcData.setSelPIDCVariantId(this.selctedPidcVariant.getId());
    }
    pidcData.setSourcePidcVerId(this.pidcA2lBO.getPidcVersion().getId());
    this.hexFileMetaData.setPidcData(pidcData);
    File hexFile = new File(this.hexFileMetaData.getSrcHexFilePath());
    this.hexFileMetaData.setHexFileName(hexFile.getName());
    CompHexResponse resp = null;
    try {
      resp = new CompHexWithCDFxServiceClient().getCompHexResult(this.hexFileMetaData);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error occurred while running Compare Hex : " + e.getLocalizedMessage(), e,
          Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }

    CompHexWithCDFxDataHandler dataHandler = new CompHexWithCDFxDataHandler(this.hexFileMetaData.getSrcHexFilePath(),
        calDataObjectsFromHex, a2lDataProvider, this.selctedPidcVariant, resp);
    // open compare editor
    dataHandler.setReportQnaireRespWrapper(qnaireRespeVersStatusJob.getCdrReportQnaireRespWrapper());


    CDMLogger.getInstance().debug("Opening Data Assessment Report editor...");
    monitor.beginTask("Opening Data Assessment Report editor", IProgressMonitor.UNKNOWN);

    Set<DaDataAssessment> baselinesForPidcA2l = null;
    try {
      baselinesForPidcA2l = new DaDataAssessmentServiceClient().getBaselinesForPidcA2l(this.pidcA2lBO.getPidcA2lId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(
          "Error occurred while getting existing baselines : " + e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    CdrReportDataHandler cdrReportData = dataHandler.getCdrReportData();

    Set<A2lWPRespModel> wpRespModelSet = new HashSet<>();
    try {
      wpRespModelSet = new PidcA2lTreeStructureServiceClient()
          .getA2lWpRespModelsForVarGrpWpDefnVersId(cdrReportData.getVarGrpId(), a2lWpInfoBO.getActiveVers().getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error occurred while getting wp responsibility : " + e.getLocalizedMessage(),
          e, Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }


    // Calculate Latest 'Wp Finished' status and update in DB while generating compare Hex
    cdrReportData.calWpFinStatusAndFillWPFinStatusMap();

    DataAssessmentReport dataAssessmentReport = fillDataAssmntReportModel(a2lFileInfoBO, dataHandler, cdrReportData,
        qnaireRespeVersStatusJob.getCdrReportQnaireRespWrapper(), a2lWpInfoBO, wpRespModelSet);

    dataAssessmentReport.setDataAssmntBaselines(baselinesForPidcA2l);

    openDataAssessmentReportEditor(dataHandler, dataAssessmentReport, a2lDataProvider.getA2lFileInfoBO());

    monitor.worked(85);
    monitor.done();
    return Status.OK_STATUS;
  }


  /**
   * Check and create a new baseline for questionnaire resp if all questionnaires are answered and has no baseline
   */
  private void checkQnaireBaselines() {
    try {
      CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper = new RvwQnaireResponseServiceClient()
          .getQniareRespVersByPidcVersIdAndVarId(this.pidcA2lBO.getPidcVersion().getId(),
              (this.selctedPidcVariant == null) ? null : this.selctedPidcVariant.getId());
      Map<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModelMap =
          cdrReportQnaireRespWrapper.getRvwQnaireRespVersModelMap();

      // iterate through all the rvw qnaires and check if any baseline has to be created
      for (Entry<Long, RvwQnaireRespVersModel> rvwQnaireRespVersModel : rvwQnaireRespVersModelMap.entrySet()) {
        if (!rvwQnaireRespVersModel.getValue().isQnaireBaselineExisting() &&
            isQnaireAllAnswered(rvwQnaireRespVersModel.getValue().getQnaireVersStatus())) {

          RvwQnaireRespVersion qnaireRespVersion =
              new RvwQnaireRespVersionServiceClient().getById(rvwQnaireRespVersModel.getValue().getQnaireRespVersId());
          RvwQnaireRespVersion wsQnaireRespVersion = new RvwQnaireRespVersion();
          CommonUtils.shallowCopy(wsQnaireRespVersion, qnaireRespVersion);

          String versionName = CommonUtilConstants.BASELINE_VERS_NAME_CRE_FROM_DATA_ASSMNT;
          String desc =
              "Baseline has been created automatically before data assessment as all questionnaires are answered, but no baseline was created";

          wsQnaireRespVersion.setVersionName(versionName);
          wsQnaireRespVersion.setDescription(desc);
          wsQnaireRespVersion.setReviewedUser("DGS_ICDM");
          wsQnaireRespVersion.setReviewedDate(DateFormat.formatDateToString(new Date(), DateFormat.DATE_FORMAT_15));
          // create a new baseline for the rvw questionnaire
          new RvwQnaireRespVersionServiceClient().create(wsQnaireRespVersion);
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param qnaireStatus Questionnaire Response Version Status
   * @return true, if all the questionaires are answered
   */
  public boolean isQnaireAllAnswered(final String qnaireStatus) {
    return CommonUtils.isEqual(qnaireStatus, CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType()) ||
        CommonUtils.isEqual(qnaireStatus, CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType());
  }

  /**
   * @param dataAssessmentReport
   * @param cdrReportQnaireRespWrapper
   * @param wpRespModelSet
   * @param cdrReportData
   */
  private void fillDataAssmntQnaire(final DataAssessmentReport dataAssessmentReport,
      final CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper, final Set<A2lWPRespModel> wpRespModelSet,
      final CdrReportDataHandler cdrReportDataHandler) {

    Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allQnaireRespVersMap =
        cdrReportQnaireRespWrapper.getAllWpRespQnaireRespVersMap();
    for (A2lWPRespModel wpRespModel : wpRespModelSet) {

      Long a2lRespId = wpRespModel.getA2lRespId();
      A2lResponsibility a2lResp = cdrReportDataHandler.getA2lEditorDataProvider().getA2lWpInfoBO()
          .getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lRespId);
      String respName = cdrReportQnaireRespWrapper.getRespIdAndNameMap().get(a2lRespId);

      Map<Long, Set<RvwQnaireRespVersion>> wpIdAndQnaireMap = allQnaireRespVersMap.get(a2lRespId);
      Long a2lWpId = wpRespModel.getA2lWpId();
      String wpName = cdrReportQnaireRespWrapper.getWpIdAndNameMap().get(a2lWpId);

      if (CommonUtils.isNotEmpty(wpIdAndQnaireMap)) {
        Set<RvwQnaireRespVersion> rvwQnaireVerSet = wpIdAndQnaireMap.get(a2lWpId);

        if (CommonUtils.isNotEmpty(rvwQnaireVerSet)) {
          // rvwQnaireVerSet has null value if there is simplified Qnaire
          rvwQnaireVerSet.removeIf(Objects::isNull);
          rvwQnaireVerSet.forEach(rvwQnaireVers -> {
            DataAssessmentQuestionnaires daQnaire = new DataAssessmentQuestionnaires();

            daQnaire.setA2lRespId(a2lRespId);
            daQnaire.setA2lRespName(respName);
            daQnaire.setA2lRespType(a2lResp.getRespType());
            daQnaire.setA2lWpId(a2lWpId);
            daQnaire.setA2lWpName(wpName);

            daQnaire.setQnaireRespVersName(rvwQnaireVers.getVersionName());
            daQnaire.setQnaireRespVersId(rvwQnaireVers.getId());

            daQnaire.setQnaireRespId(rvwQnaireVers.getQnaireRespId());
            daQnaire.setQnaireRespName(
                cdrReportQnaireRespWrapper.getQnaireResponseMap().get(rvwQnaireVers.getId()).getName());

            daQnaire.setQnaireReviewedDate(rvwQnaireVers.getReviewedDate());
            daQnaire.setQnaireReviewedUser(rvwQnaireVers.getReviewedUser());

            RvwQnaireRespVersModel rvwQnaireRespVersModel =
                cdrReportQnaireRespWrapper.getRvwQnaireRespVersModelMap().get(rvwQnaireVers.getId());

            daQnaire.setQnairePositiveAnsCount(rvwQnaireRespVersModel.getQnairePositiveAnsCount());
            daQnaire.setQnaireNegativeAnsCount(rvwQnaireRespVersModel.getQnaireNegativeAnsCount());
            daQnaire.setQnaireNeutralAnsCount(rvwQnaireRespVersModel.getQnaireNeutralAnsCount());
            daQnaire.setQnaireBaselineLink(rvwQnaireRespVersModel.getRvwQnaireLink());
            daQnaire.setQnaireBaselineExisting(rvwQnaireRespVersModel.isQnaireBaselineExisting());
            daQnaire.setQnaireReadyForProd(rvwQnaireRespVersModel.isQnaireReadyForProd());
            calculateRbRespQnairesStat(dataAssessmentReport, daQnaire);

            dataAssessmentReport.getDataAssmntQnaires().add(daQnaire);
          });
        }
      }
    }
    DataAssmntReportDataHandler.fillQnairesRvdUserDisplayName(dataAssessmentReport);

    fillQnairesBaselineLinkDisplayText(dataAssessmentReport);
  }

  /**
   * @param dataAssessmentReport
   * @param daQnaire
   */
  private void calculateRbRespQnairesStat(final DataAssessmentReport dataAssessmentReport,
      final DataAssessmentQuestionnaires daQnaire) {
    if (CommonUtils.isEqual(WpRespType.RB, WpRespType.getType(daQnaire.getA2lRespType()))) {
      dataAssessmentReport.setQnairesRbRespTotalCount(dataAssessmentReport.getQnairesRbRespTotalCount() + 1);
      if (!daQnaire.isQnaireReadyForProd()) {
        dataAssessmentReport.setQnairesNotAnsweredCount(dataAssessmentReport.getQnairesNotAnsweredCount() + 1);
      }
    }
  }

  /**
   * @param dataAssessmentReport
   */
  private void fillQnairesBaselineLinkDisplayText(final DataAssessmentReport dataAssessmentReport) {
    if (CommonUtils.isNotEmpty(dataAssessmentReport.getDataAssmntQnaires())) {
      StringBuilder qnaireLinkBuilder = new StringBuilder();
      dataAssessmentReport.getDataAssmntQnaires().stream().forEach(qnaire -> {
        qnaire.setQnaireBaselineLinkDisplayText(qnaireLinkBuilder.append(qnaire.getA2lRespName())
            .append(QUESTIONNAIRE_DELIMITER).append(qnaire.getA2lWpName()).append(QUESTIONNAIRE_DELIMITER)
            .append(qnaire.getQnaireRespName()).toString());
        qnaireLinkBuilder.setLength(0);
      });
    }
  }

  /**
   * @param daWpResp
   * @param cdrReportData
   * @param paramIdAndDaCompParams
   */
  private void fillStatisticsForWpResp(final DaWpResp daWpResp, final CdrReportDataHandler cdrReportData,
      final Map<Long, DaCompareHexParam> paramIdAndDaCompParams, final DataAssessmentReport dataAssmntReport) {
    Map<Long, Set<Long>> respIdAndParamIds = cdrReportData.getWpRespParamMap().get(daWpResp.getA2lWpId().longValue());
    if (CommonUtils.isNotEmpty(respIdAndParamIds)) {
      calculateWpRespProjectStateFlags(daWpResp, cdrReportData, paramIdAndDaCompParams, dataAssmntReport,
          respIdAndParamIds);
    }
  }

  /**
   * @param daWpResp
   * @param cdrReportData
   * @param paramIdAndDaCompParams
   * @param dataAssmntReport
   * @param respIdAndParamIds
   */
  private void calculateWpRespProjectStateFlags(final DaWpResp daWpResp, final CdrReportDataHandler cdrReportData,
      final Map<Long, DaCompareHexParam> paramIdAndDaCompParams, final DataAssessmentReport dataAssmntReport,
      final Map<Long, Set<Long>> respIdAndParamIds) {
    long respId = daWpResp.getA2lRespId().longValue();
    Set<Long> paramIds = respIdAndParamIds.get(respId);
    // true, if all the parameters have Hex value equal to reviewed value
    boolean hexValEqualToRevVal = true;
    // true, if all parameters under Robert Bosch Responsibility type is reviewed
    boolean isReviewed = true;
    if (CommonUtils.isNotEmpty(paramIds)) {
      for (Long paramId : paramIds) {
        DaCompareHexParam daCompHexParam = paramIdAndDaCompParams.get(paramId);

        if (CommonUtils.isNotNull(daCompHexParam) &&
            CommonUtils.isEqual(WpRespType.RB, WpRespType.getTypeFromUI(daCompHexParam.getRespType()))) {
          hexValEqualToRevVal = hexValEqualToRevVal && daCompHexParam.isEqual();
          isReviewed = isReviewed && daCompHexParam.isReviewed();
          calculateHexDataReviewNotEqualCount(dataAssmntReport, daCompHexParam);
          calculateRbParamsTotalAndNotRvdCount(dataAssmntReport, daCompHexParam);
        }
      }

      long wpId = daWpResp.getA2lWpId().longValue();

      boolean anyQnaireRespNotBaselined = isAnyQnaireRespNotBaselined(cdrReportData, respId, wpId);
      String qnaireStatus = getQnaireStatus(cdrReportData, respId, wpId);
      String isQnaireAnsweredBaselined =
          checkQnaireAnsweredAndBaselined(cdrReportData, anyQnaireRespNotBaselined, qnaireStatus);

      boolean isWpFinished = CommonUtils.isEqual(
          CDRConstants.WP_RESP_STATUS_TYPE.getTypeByDbCode(getWpFinishedRespStatus(cdrReportData, respId, wpId)),
          CDRConstants.WP_RESP_STATUS_TYPE.FINISHED);

      setWpRespProjectStateFlags(daWpResp, hexValEqualToRevVal, isReviewed, isQnaireAnsweredBaselined, isWpFinished);
    }
  }

  /**
   * This method is to fetch the WpResp Status column value
   *
   * @param cdrReportData
   * @param respId
   * @param wpId
   */
  public String getWpFinishedRespStatus(final CdrReportDataHandler cdrReportData, final long respId, final long wpId) {
    Map<Long, Map<Long, String>> wpFinStatusMap = cdrReportData.getWpFinishedStatusMap();
    if (wpFinStatusMap.containsKey(wpId)) {
      return wpFinStatusMap.get(wpId).get(respId);
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @param cdrReportData
   * @param anyQnaireRespNotBaselined
   * @param qnaireStatus
   * @return
   */
  private String checkQnaireAnsweredAndBaselined(final CdrReportDataHandler cdrReportData,
      final boolean anyQnaireRespNotBaselined, final String qnaireStatus) {
    String qnaireAnsweredAndBaselined;
    if (anyQnaireRespNotBaselined) {
      qnaireAnsweredAndBaselined = CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.NO.getDbType();
    }
    else if (CommonUtils.isEmptyString(qnaireStatus)) {
      qnaireAnsweredAndBaselined = CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.N_A.getDbType();
    }
    else {
      qnaireAnsweredAndBaselined = CommonUtils.getBooleanCode(cdrReportData.isQnaireAllAnswered(qnaireStatus));
    }
    return qnaireAnsweredAndBaselined;
  }

  private boolean isAnyQnaireRespNotBaselined(final CdrReportDataHandler cdrReportData, final long respId,
      final long wpId) {

    Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> allWpRespQnaireRespVersMap =
        cdrReportData.getCdrReportQnaireRespWrapper().getAllWpRespQnaireRespVersMap();

    if (allWpRespQnaireRespVersMap.containsKey(respId)) {
      Map<Long, Set<RvwQnaireRespVersion>> wpQnaireRespVerMap = allWpRespQnaireRespVersMap.get(respId);

      if (wpQnaireRespVerMap.containsKey(wpId)) {
        Set<RvwQnaireRespVersion> rvwQniareRespVersSet = wpQnaireRespVerMap.get(wpId);
        // Iterate through Latest RvwQnaireRespVersions belonging to WP Resp Combination
        for (RvwQnaireRespVersion rvwQnaireRespVers : rvwQniareRespVersSet) {
          // rvwQnaireRespVersion is null for Simplified Qnaire
          // If latest RvwQnaireRespVersion is 'Working Set', then baseline is not available for the RvwQnaireResponse
          if (CommonUtils.isNotNull(rvwQnaireRespVers) &&
              cdrReportData.isRvwQnaireRespVersWorkingSet().test(rvwQnaireRespVers)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * @param cdrReportData
   * @param respId
   * @param wpId
   * @return
   */
  private String getQnaireStatus(final CdrReportDataHandler cdrReportData, final long respId, final long wpId) {
    Map<Long, Map<Long, Set<RvwQnaireRespVersion>>> wpRespQnaireRespVersMap =
        cdrReportData.getCdrReportQnaireRespWrapper().getWpRespQnaireRespVersMap();
    String qnaireRespVersStatus = "";
    if (wpRespQnaireRespVersMap.containsKey(respId)) {
      Map<Long, String> wpQnaireRespVerMap =
          cdrReportData.getCdrReportQnaireRespWrapper().getWpRespQnaireRespVersStatusMap().get(respId);
      if (wpQnaireRespVerMap.containsKey(wpId)) {
        qnaireRespVersStatus = wpQnaireRespVerMap.get(wpId);
      }
    }

    return qnaireRespVersStatus;
  }

  /**
   * @param dataAssmntReport
   * @param daCompHexParam
   */
  private void calculateRbParamsTotalAndNotRvdCount(final DataAssessmentReport dataAssmntReport,
      final DaCompareHexParam daCompHexParam) {
    dataAssmntReport.getDataAssmntCompHexData()
        .setDaRbRespParamTotalCount(dataAssmntReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount() + 1);
    if (!daCompHexParam.isReviewed()) {
      dataAssmntReport.setRbParamsNotRvdCount(dataAssmntReport.getRbParamsNotRvdCount() + 1);
    }
  }

  /**
   * @param dataAssmntReport
   * @param daCompHexParam
   */
  private void calculateHexDataReviewNotEqualCount(final DataAssessmentReport dataAssmntReport,
      final DaCompareHexParam daCompHexParam) {
    // Positive case : Parameter Review and Equal
    // Negative case : not of (Parameter Review and Equal)
    if (!(!daCompHexParam.isNeverReviewed() && daCompHexParam.isEqual())) {
      dataAssmntReport.setHexDataReviewNotEqualCount(dataAssmntReport.getHexDataReviewNotEqualCount() + 1);
    }
  }

  /**
   * @param daWpResp
   * @param hexValEqualToRevVal
   * @param isReviewed
   * @param qanireAnsweredFlag
   * @param isWpFinished
   */
  private void setWpRespProjectStateFlags(final DaWpResp daWpResp, final boolean hexValEqualToRevVal,
      final boolean isReviewed, final String qanireAnsweredFlag, final boolean isWpFinished) {
    boolean isQnaireAnsweredBaselined = checkQnaireAnsweredAndBaselined(qanireAnsweredFlag);
    boolean wpReadyForProduction = hexValEqualToRevVal && isReviewed && isQnaireAnsweredBaselined;

    daWpResp.setHexRvwEqualFlag(CommonUtils.getBooleanCode(hexValEqualToRevVal));
    daWpResp.setParameterReviewedFlag(CommonUtils.getBooleanCode(isReviewed));
    daWpResp.setQnairesAnsweredFlag(qanireAnsweredFlag);
    daWpResp.setWpFinishedFlag(CommonUtils.getBooleanCode(isWpFinished));
    daWpResp.setWpReadyForProductionFlag(CommonUtils.getBooleanCode(wpReadyForProduction));

  }


  /**
   * @param qnaireAnsweredFlag
   * @return
   */
  private boolean checkQnaireAnsweredAndBaselined(final String qnaireAnsweredFlag) {
    return CommonUtils.isEqual(CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.N_A.getDbType(), qnaireAnsweredFlag) ||
        CommonUtils.getBooleanType(qnaireAnsweredFlag);
  }


  /**
   * @param a2lFileInfoBO A2LFileInfoBO
   * @param resp CompHexResponse
   * @param cdrReportQnaireRespWrapper CdrReportQnaireRespWrapper
   * @param cdrReportDataHandler
   * @param cdrReportQnaireRespWrapper
   * @param a2lWpInfoBO
   * @param wpRespModelSet
   * @param paramIdAndDaCompParams
   */
  private DataAssessmentReport fillDataAssmntReportModel(final A2LFileInfoBO a2lFileInfoBO,
      final CompHexWithCDFxDataHandler compHexDataHandler, final CdrReportDataHandler cdrReportDataHandler,
      final CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper, final A2LWPInfoBO a2lWpInfoBO,
      final Set<A2lWPRespModel> wpRespModelSet) {
    DataAssessmentReport dataAssmntReport = new DataAssessmentReport();
    Map<Long, DaCompareHexParam> paramIdAndDaCompParams = new HashMap<>();

    fillPidcA2lDetails(dataAssmntReport);

    fillWpDefVerDetails(a2lWpInfoBO, dataAssmntReport);

    constructCompareHexData(dataAssmntReport, a2lFileInfoBO, compHexDataHandler, cdrReportDataHandler,
        paramIdAndDaCompParams);

    boolean hexValEqualToRevVal = true;
    dataAssmntReport.setHexDataReviewNotEqualCount(0);
    // true, if all parameters under Robert Bosch Responsibility type is reviewed
    boolean isReviewed = true;
    dataAssmntReport.setRbParamsNotRvdCount(0);
    dataAssmntReport.getDataAssmntCompHexData().setDaRbRespParamTotalCount(0);
    boolean isQnaireAnsweredBaselined = true;
    dataAssmntReport.setQnairesNotAnsweredCount(0);
    dataAssmntReport.setQnairesRbRespTotalCount(0);
    dataAssmntReport.setWpNotFinishedCount(0);
    dataAssmntReport.setWpRbRespTotalCount(0);
    boolean wpReadyForProduction = true;

    Set<DaWpResp> daWpRespSet = new HashSet<>();
    for (A2LParameter a2lParam : a2lFileInfoBO.getA2lParamMap(null).values()) {
      ParamWpResponsibility respObj = cdrReportDataHandler.getParamWpResp(a2lParam.getParamId());
      if (respObj != null) {
        A2lResponsibility a2lResp = cdrReportDataHandler.getA2lEditorDataProvider().getA2lWpInfoBO()
            .getA2lResponsibilityModel().getA2lResponsibilityMap().get(respObj.getRespId());

        String wpName = cdrReportDataHandler.getA2lEditorDataProvider().getA2lWpInfoBO().getA2lWpDefnModel()
            .getWpRespMap().get(respObj.getWpRespId()).getName();

        DaWpResp daWpResp = new DaWpResp();
        daWpResp.setA2lRespId(new BigDecimal(a2lResp.getId()));
        daWpResp.setA2lRespName(a2lResp.getName());
        daWpResp.setA2lRespType(a2lResp.getRespType());
        daWpResp.setA2lRespAliasName(a2lResp.getAliasName());
        daWpResp.setA2lWpId(new BigDecimal(respObj.getWpId()));
        daWpResp.setA2lWpName(wpName);

        if (!daWpRespSet.contains(daWpResp)) {
          fillStatisticsForWpResp(daWpResp, cdrReportDataHandler, paramIdAndDaCompParams, dataAssmntReport);
          daWpRespSet.add(daWpResp);
          if (CommonUtils.isEqual(WpRespType.RB, WpRespType.getType(daWpResp.getA2lRespType()))) {
            hexValEqualToRevVal = hexValEqualToRevVal && CommonUtils.getBooleanType(daWpResp.getHexRvwEqualFlag());
            isReviewed = isReviewed && CommonUtils.getBooleanType(daWpResp.getParameterReviewedFlag());
            // Questionnaire answered and baselined flag includes values Y, N, N/A
            isQnaireAnsweredBaselined =
                isQnaireAnsweredBaselined && checkQnaireAnsweredAndBaselined(daWpResp.getQnairesAnsweredFlag());
            calculateRbRespWpStat(dataAssmntReport, daWpResp);
            wpReadyForProduction =
                wpReadyForProduction && CommonUtils.getBooleanType(daWpResp.getWpReadyForProductionFlag());
          }
        }
      }
    }
    dataAssmntReport.setDataAssmntWps(daWpRespSet);
    dataAssmntReport.setHexFileDataEqualWithDataReviews(hexValEqualToRevVal);
    dataAssmntReport.setAllParametersReviewed(isReviewed);
    dataAssmntReport.setAllQnairesAnswered(isQnaireAnsweredBaselined);
    dataAssmntReport.setReadyForSeries(wpReadyForProduction);
    dataAssmntReport
        .setConsiderRvwsOfPrevPidcVers(cdrReportDataHandler.getCdrReport().getConsiderReviewsOfPrevPidcVers());

    fillDataAssmntQnaire(dataAssmntReport, cdrReportQnaireRespWrapper, wpRespModelSet, cdrReportDataHandler);

    return dataAssmntReport;
  }

  /**
   * @param dataAssmntReport
   * @param daWpResp
   */
  private void calculateRbRespWpStat(final DataAssessmentReport dataAssmntReport, final DaWpResp daWpResp) {
    if (CommonUtils.isEqual(WpRespType.RB, WpRespType.getType(daWpResp.getA2lRespType()))) {
      dataAssmntReport.setWpRbRespTotalCount(dataAssmntReport.getWpRbRespTotalCount() + 1);
      if (!CommonUtils.getBooleanType(daWpResp.getWpFinishedFlag())) {
        dataAssmntReport.setWpNotFinishedCount(dataAssmntReport.getWpNotFinishedCount() + 1);
      }
    }
  }

  /**
   * @param dataAssmntReport
   */
  private void fillPidcA2lDetails(final DataAssessmentReport dataAssmntReport) {
    dataAssmntReport.setSrcHexFilePath(this.hexFileMetaData.getSrcHexFilePath());

    File hexFile = new File(this.hexFileMetaData.getSrcHexFilePath());
    dataAssmntReport.setHexFileName(hexFile.getName());
    dataAssmntReport.setVcdmDstSource(this.hexFileMetaData.getVcdmDstSource());
    dataAssmntReport.setVcdmDstVersId(this.hexFileMetaData.getVcdmDstVersId());

    dataAssmntReport.setA2lFileId(this.pidcA2lBO.getA2lFile().getId());
    dataAssmntReport.setA2lFileName(this.pidcA2lBO.getA2LFileName());
    dataAssmntReport.setPidcA2lId(this.pidcA2lBO.getPidcA2lId());

    dataAssmntReport.setPidcVersId(this.pidcA2lBO.getPidcVersion().getId());
    dataAssmntReport.setPidcVersName(this.pidcA2lBO.getPidcVersion().getName());

    dataAssmntReport.setPidcVariantId(this.selctedPidcVariant != null ? this.selctedPidcVariant.getId() : null);
    dataAssmntReport.setPidcVariantName(this.selctedPidcVariant != null ? this.selctedPidcVariant.getName() : "");

    dataAssmntReport.setPidcId(this.pidcTreeNode.getPidc().getId());
    dataAssmntReport.setPidcName(this.pidcTreeNode.getPidc().getName());
  }

  /**
   * Get WpDefnVersId and WpDefVersName from a2lWpInfoBo
   *
   * @param a2lWpInfoBO
   * @param dataAssmntReport
   */
  private void fillWpDefVerDetails(final A2LWPInfoBO a2lWpInfoBO, final DataAssessmentReport dataAssmntReport) {
    A2lWpDefnVersion activeA2LWpDefnVers = a2lWpInfoBO.getActiveVers();
    if (a2lWpInfoBO.getActiveVers() != null) {
      dataAssmntReport.setWpDefnVersId(activeA2LWpDefnVers.getId() != null ? activeA2LWpDefnVers.getId() : null);
      dataAssmntReport.setWpDefnVersName(activeA2LWpDefnVers.getName());
    }
  }

  private void constructCompareHexData(final DataAssessmentReport dataAssmntReport, final A2LFileInfoBO a2lFileInfoBO,
      final CompHexWithCDFxDataHandler compHexDataHandler, final CdrReportDataHandler cdrReportDataHandler,
      final Map<Long, DaCompareHexParam> paramIdAndDaCompParams) {
    CompHexResponse compHexResponse = compHexDataHandler.getCompHexResponse();
    DataAssessmentCompareHexData daCompareHexData = new DataAssessmentCompareHexData();

    CompHexStatistics compHexStatistics = compHexResponse.getCompHexStatistics();
    compHexStatistics.setStatNumParamInBoschResp(compHexDataHandler.getParameterInBoschResp());
    compHexStatistics.setStatNumParamInBoschRespRvwed(compHexDataHandler.getParameterInBoschRespRvwed());
    compHexStatistics.setStatParamWithBoschRespRvw(
        CommonUiUtils.displayLangBasedPercentage(compHexDataHandler.getReviewedParameterWithBoschResp()));
    compHexStatistics.setStatParamWithBoschRespQnaireRvw(
        CommonUiUtils.displayLangBasedPercentage(compHexDataHandler.getRvwParamWithBoschRespForCompletedQnaire()));
    compHexStatistics.setStatQnaireNagativeAnswer(cdrReportDataHandler.getQnaireWithNegativeAnswersCount());

    daCompareHexData.setCompareHexStatics(compHexStatistics);

    daCompareHexData.setQSSDCheckFailed(compHexResponse.isQSSDCheckFailed());
    daCompareHexData.setCompliCheckFailed(compHexResponse.isCompliCheckFailed());
    daCompareHexData.setReferenceId(compHexResponse.getReferenceId());
    daCompareHexData.setErrorMsgSet(compHexResponse.getErrorMsgSet());


    fillDaCompHexParams(a2lFileInfoBO, daCompareHexData, compHexDataHandler, cdrReportDataHandler,
        paramIdAndDaCompParams);

    dataAssmntReport.setDataAssmntCompHexData(daCompareHexData);
  }

  private void fillDaCompHexParams(final A2LFileInfoBO a2lFileInfoBO,
      final DataAssessmentCompareHexData daCompareHexData, final CompHexWithCDFxDataHandler compHexDataHandler,
      final CdrReportDataHandler cdrReportDataHandler, final Map<Long, DaCompareHexParam> paramIdAndDaCompParams) {

    List<DaCompareHexParam> daCompHexParams = new ArrayList<>();

    for (CompHexWithCDFParam compHexParam : compHexDataHandler.getCompHexResponse().getCompHexResult()) {
      DaCompareHexParam daCompHexParam = new DaCompareHexParam();
      String paramName = compHexParam.getParamName();
      daCompHexParam.setCompli(compHexParam.isCompli());
      daCompHexParam.setBlackList(compHexParam.isBlackList());
      daCompHexParam.setDependantCharacteristic(compHexParam.isDependantCharacteristic());
      daCompHexParam.setReadOnly(compHexParam.isReadOnly());
      daCompHexParam.setQssdParameter(compHexParam.isQssdParameter());
      daCompHexParam.setDepCharsName(compHexParam.getDepCharsName());
      daCompHexParam.setParamType(compHexParam.getParamType());
      daCompHexParam.setParamName(paramName);
      daCompHexParam.setFuncName(compHexParam.getFuncName());
      daCompHexParam.setFuncVers(compHexParam.getFuncVers());
      daCompHexParam.setReviewed(compHexParam.isReviewed());
      daCompHexParam.setNeverReviewed(compHexParam.isNeverReviewed());
      daCompHexParam.setEqual(compHexParam.isEqual());
      daCompHexParam.setLatestFunctionVersion(compHexParam.getLatestFunctionVersion());
      daCompHexParam.setLatestA2lVersion(compHexParam.getLatestA2lVersion());
      daCompHexParam.setLatestReviewComments(compHexParam.getLatestReviewComments());
      daCompHexParam.setCompliResult(compHexParam.getCompliResult());
      daCompHexParam.setQssdResult(compHexParam.getQssdResult());
      daCompHexParam.setCompliTooltip(compHexParam.getCompliTooltip());
      daCompHexParam.setQssdTooltip(compHexParam.getQssdTooltip());
      daCompHexParam.setWpName(cdrReportDataHandler.getWpName(paramName));
      daCompHexParam.setRespName(cdrReportDataHandler.getRespName(paramName));
      daCompHexParam.setRespType(cdrReportDataHandler.getRespType(paramName));
      daCompHexParam.setWpFinishedStatus(cdrReportDataHandler.getWpFinishedRespStatuswithName(paramName));

      fillQnaireStatusForDaCompHexParam(cdrReportDataHandler, daCompHexParam, paramName);

      // Get the CalData Objects for Hex value and reviewed value and Set to DaCompareHexParam
      daCompHexParam.setHexValue(CalDataUtil
          .convertCalDataToZippedByteArr(compHexDataHandler.getHexCalData(compHexParam), CDMLogger.getInstance()));
      daCompHexParam.setReviewedValue(CalDataUtil
          .convertCalDataToZippedByteArr(compHexDataHandler.getCdfxCalData(compHexParam), CDMLogger.getInstance()));

      // Review Score in CompHexWithCDFParam is Display Score [Eg., 9[100%]]
      // Set the DB Type of Review Score to DaCompareHexParam [Eg., 9]
      DATA_REVIEW_SCORE reviewScore = cdrReportDataHandler.getReviewScore(paramName);
      daCompHexParam.setReviewScore(reviewScore == null ? "" : reviewScore.getDbType());
      daCompHexParam.setHundredPecReviewScore(compHexParam.getHundredPecReviewScore());

      CDRReviewResult cdrRvwRslt = cdrReportDataHandler.getReviewResult(paramName, 0);
      if (cdrRvwRslt != null) {
        daCompHexParam.setCdrResultId(cdrRvwRslt.getId());
        daCompHexParam.setRvwResultName(cdrRvwRslt.getName());
      }

      ParameterReviewDetails paramReviewDtls = cdrReportDataHandler.getParamReviewDetailsLatest(paramName);
      if (paramReviewDtls != null) {
        daCompHexParam.setRvwParamId(paramReviewDtls.getRvwParameterId());
      }
      daCompHexParams.add(daCompHexParam);

      A2LParameter a2lParameter = a2lFileInfoBO.getA2lParamMap(null).get(paramName);
      daCompHexParam.setParameterId(a2lParameter.getParamId());

      paramIdAndDaCompParams.put(a2lParameter.getParamId(), daCompHexParam);
    }
    daCompareHexData.setDaCompareHexParam(daCompHexParams);
  }

  private void fillQnaireStatusForDaCompHexParam(final CdrReportDataHandler cdrReportDataHandler,
      final DaCompareHexParam daCompHexParam, final String paramName) {
    String qnaireRespVersStatus = cdrReportDataHandler.getQnaireRespVersStatus(paramName, false);
    CDRConstants.DA_QS_STATUS_TYPE qnaireStatusType;
    switch (qnaireRespVersStatus) {
      case CDRConstants.NOT_BASELINED_QNAIRE_RESP:
        qnaireStatusType = CDRConstants.DA_QS_STATUS_TYPE.NO_BASELINE;
        break;
      case CDRConstants.NO_QNAIRE_STATUS:
        qnaireStatusType = CDRConstants.DA_QS_STATUS_TYPE.NO_QNAIRE;
        break;
      case CDRConstants.RVW_QNAIRE_STATUS_N_A:
        qnaireStatusType = CDRConstants.DA_QS_STATUS_TYPE.N_A;
        break;
      default:
        qnaireStatusType = CDRConstants.DA_QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus);
    }
    daCompHexParam.setQnaireStatus(qnaireStatusType != null ? qnaireStatusType.getDbType() : "");
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
    File hexFile = new File(this.hexFileMetaData.getSrcHexFilePath());
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

  /**
   * Open Data Assessment Report editor
   *
   * @param compHexDataHandler
   * @param userNotificationMsg
   * @param dataAssessmentReport
   * @param a2lFileInfoBO
   */
  private void openDataAssessmentReportEditor(final CompHexWithCDFxDataHandler compHexDataHandler,
      final DataAssessmentReport dataAssessmentReport, final A2LFileInfoBO a2lFileInfoBO) {
    final DataAssessmentReportEditorInput editorInput = new DataAssessmentReportEditorInput();
    editorInput.setPidcName(this.pidcTreeNode.getPidc().getName());
    editorInput.setA2lFileName(this.pidcA2lBO.getA2LFileName());
    editorInput.setSelctedPidcVariantName(
        CommonUtils.isNotNull(this.selctedPidcVariant) ? this.selctedPidcVariant.getName() : "<NO-VARIANT>");
    editorInput.setBaseline(false);

    DataAssmntReportDataHandler dataAssmntReportDataHandler = new DataAssmntReportDataHandler(dataAssessmentReport,
        compHexDataHandler, a2lFileInfoBO.getA2lParamMap(), a2lFileInfoBO.getCharacteristicsMap());
    editorInput.setDataAssmntReportDataHandler(dataAssmntReportDataHandler);

    Display.getDefault().asyncExec(() -> {

      try {
        IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .openEditor(editorInput, DataAssessmentReportEditor.EDITOR_ID);
        if (openEditor instanceof DataAssessmentReportEditor) {
          DataAssessmentReportEditor dataAssessmentReportEditor = (DataAssessmentReportEditor) openEditor;
          // set focus to the editor opened
          dataAssessmentReportEditor.setFocus();
          CDMLogger.getInstance().debug("Data Assessment Report is opened in the editor");

          // Download CSSD file
          CompHexCheckSSDReportAction excelRptAction = new CompHexCheckSSDReportAction(compHexDataHandler);
          excelRptAction.run();

          // Open check ssd report only if there are compli param status is 'NOT OK'
          if (CommonUtils.isFileAvailable(compHexDataHandler.getCssdExcelReportPath()) &&
              (dataAssessmentReport.getDataAssmntCompHexData().isCompliCheckFailed() ||
                  dataAssessmentReport.getDataAssmntCompHexData().isQSSDCheckFailed())) {
            showCompliFailureMsg();
          }

        }
      }
      catch (PartInitException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    });
  }

}