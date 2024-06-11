package com.bosch.caltool.icdm.bo.cdr;

import java.math.BigDecimal;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TDaDataAssessment;
import com.bosch.caltool.icdm.database.entity.cdr.TDaWpResp;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DaFile;
import com.bosch.caltool.icdm.model.cdr.DaParameter;
import com.bosch.caltool.icdm.model.cdr.DaQnaireResp;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;


/**
 * Command class for DaDataAssessment
 *
 * @author say8cob
 */
public class DaDataAssessmentCommand extends AbstractCommand<DaDataAssessment, DaDataAssessmentLoader> {

  private final DataAssessmentReport dataAssessmentReportModel;

  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @param dataAssessmentReportModel Common model
   * @param isUpdate Flag to indicate update/create
   * @throws IcdmException error when initializing
   */
  public DaDataAssessmentCommand(final ServiceData serviceData, final DaDataAssessment input,
      final DataAssessmentReport dataAssessmentReportModel, final boolean isUpdate) throws IcdmException {
    super(serviceData, input, new DaDataAssessmentLoader(serviceData), resolveCommandModeU(isUpdate));
    this.dataAssessmentReportModel = dataAssessmentReportModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TDaDataAssessment entity = new TDaDataAssessment();

    entity.setBaselineName(getInputData().getBaselineName());
    entity.setDescription(getInputData().getDescription());
    entity.setTypeOfAssignment(getInputData().getTypeOfAssignment());
    entity.setPidcVersId(getInputData().getPidcVersId());
    entity.setPidcVersFullname(getInputData().getPidcVersFullname());
    entity.setVariantId(getInputData().getVariantId());
    entity.setVariantName(getInputData().getVariantName());
    entity.setPidcA2lId(getInputData().getPidcA2lId());
    entity.setA2lFilename(getInputData().getA2lFilename());
    entity.setHexFileName(getInputData().getHexFileName());
    entity.setWpDefnVersId(getInputData().getWpDefnVersId());
    entity.setWpDefnVersName(getInputData().getWpDefnVersName());
    entity.setVcdmDstSource(getInputData().getVcdmDstSource());
    entity.setVcdmDstVersId(getInputData().getVcdmDstVersId());
    entity.setFileArchivalStatus(getInputData().getFileArchivalStatus());
    entity.setPreviousPidcVersConsidered(getInputData().getPreviousPidcVersConsidered());
    entity.setCompliParamInA2L(getInputData().getCompliParamInA2L());
    entity.setCompliParamPassed(getInputData().getCompliParamPassed());
    entity.setCompliParamCSSDFail(getInputData().getCompliParamCSSDFail());
    entity.setCompliParamNoRuleFail(getInputData().getCompliParamNoRuleFail());
    entity.setCompliParamSSD2RVFail(getInputData().getCompliParamSSD2RVFail());
    entity.setQssdParamFail(getInputData().getQssdParamFail());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    // Persist the DaDataAssessment entity to get data Assessment id
    persistEntity(entity);

    // Get the created DaDataAssessment object
    TDaDataAssessment tDaDataAssessment =
        new DaDataAssessmentLoader(getServiceData()).getEntityObject(entity.getDataAssessmentId());

    DaDataAssessment daBaseline = new DaDataAssessmentLoader(getServiceData()).createDataObject(tDaDataAssessment);
    // add newly created baseline to Data Assessment Report model, which will be used in Data Assessment Report PDF
    // Export
    this.dataAssessmentReportModel.getDataAssmntBaselines().add(daBaseline);
    this.dataAssessmentReportModel.setBaselineCreatedDate(daBaseline.getCreatedDate());

    // To save Wp Resp details into TDaWpResp entity
    createDaWpRespDetails(tDaDataAssessment);

    // To save Qnaire Resp details into TDaQnaireResp entity
    createDaQnaireRespDetails(tDaDataAssessment);

    // To save Parameter details into TDaParameter entity
    createDaParameterDetails(tDaDataAssessment);

  }


  /**
   * Save WpResp details into DaWpResp table
   *
   * @param l
   */
  private void createDaWpRespDetails(final TDaDataAssessment tDaDataAssessment) {

    getLogger().debug("Creating DaWpResp details in DB started..");

    // Set of WP Resp info to be saved into DB
    this.dataAssessmentReportModel.getDataAssmntWps().stream().forEach(dataAssmntWpResp -> {
      DaWpResp wpResp = new DaWpResp();
      wpResp.setDataAssessmentId(tDaDataAssessment.getDataAssessmentId());
      wpResp.setA2lWpId(dataAssmntWpResp.getA2lWpId());
      wpResp.setA2lWpName(dataAssmntWpResp.getA2lWpName());
      wpResp.setA2lRespId(dataAssmntWpResp.getA2lRespId());
      wpResp.setA2lRespAliasName(dataAssmntWpResp.getA2lRespAliasName());
      wpResp.setA2lRespName(dataAssmntWpResp.getA2lRespName());
      wpResp.setA2lRespType(dataAssmntWpResp.getA2lRespType());
      wpResp.setWpReadyForProductionFlag(dataAssmntWpResp.getWpReadyForProductionFlag());
      wpResp.setWpFinishedFlag(dataAssmntWpResp.getWpFinishedFlag());
      wpResp.setQnairesAnsweredFlag(dataAssmntWpResp.getQnairesAnsweredFlag());
      wpResp.setParameterReviewedFlag(dataAssmntWpResp.getParameterReviewedFlag());
      wpResp.setHexRvwEqualFlag(dataAssmntWpResp.getHexRvwEqualFlag());

      try {
        // Command to persist DaWpResp details into TDaWpResp entity
        DaWpRespCommand daWpRespCmd = new DaWpRespCommand(getServiceData(), wpResp);
        executeChildCommand(daWpRespCmd);
      }
      catch (IcdmException e) {
        getLogger().error("An exception occured while saving data in TDaWpResp entity", e);
      }
    });

    getLogger().info("Creating DaWpResp details in DB completed");

  }

  /**
   * Save Qnaire Resp details into DaQnaireResp table
   *
   * @param tDaDataAssessment
   * @param dataAssmntReportModel
   * @param daWpRespMap
   * @throws IcdmException
   */
  private void createDaQnaireRespDetails(final TDaDataAssessment tDaDataAssessment) {
    getLogger().info("Creating DaQnaireResp details in DB started");

    if (CommonUtils.isNotEmpty(tDaDataAssessment.getTDaWpResps())) {

      tDaDataAssessment.getTDaWpResps().stream().forEach(wpResp -> {
        for (DataAssessmentQuestionnaires dataAssmntQnaireResp : this.dataAssessmentReportModel
            .getDataAssmntQnaires()) {
          if (qnaireMtchesWithWpResp(wpResp, dataAssmntQnaireResp)) {
            DaQnaireResp qnaireResp = new DaQnaireResp();
            qnaireResp.setDaWpRespId(wpResp.getDaWpRespId());
            qnaireResp.setQnaireRespId(CommonUtils.getBigdecimalFromLong(dataAssmntQnaireResp.getQnaireRespId()));
            qnaireResp.setQnaireRespName(dataAssmntQnaireResp.getQnaireRespName());
            qnaireResp
                .setQnaireRespVersId(CommonUtils.getBigdecimalFromLong(dataAssmntQnaireResp.getQnaireRespVersId()));
            qnaireResp.setQnaireRespVersionName(dataAssmntQnaireResp.getQnaireRespVersName());
            qnaireResp
                .setReadyForProductionFlag(CommonUtils.getBooleanCode(dataAssmntQnaireResp.isQnaireReadyForProd()));
            qnaireResp
                .setBaselineExistingFlag(CommonUtils.getBooleanCode(dataAssmntQnaireResp.isQnaireBaselineExisting()));
            qnaireResp.setNumPositiveAnswers(BigDecimal.valueOf(dataAssmntQnaireResp.getQnairePositiveAnsCount()));
            qnaireResp.setNumNeutralAnswers(BigDecimal.valueOf(dataAssmntQnaireResp.getQnaireNeutralAnsCount()));
            qnaireResp.setNumNegativeAnswers(BigDecimal.valueOf(dataAssmntQnaireResp.getQnaireNegativeAnsCount()));
            qnaireResp.setReviewedDate(dataAssmntQnaireResp.getQnaireReviewedDate());
            qnaireResp.setReviewedUser(dataAssmntQnaireResp.getQnaireReviewedUser());

            try {
              // Execute command to persist DaQnaireResp details into TDaQnaireResp entity
              DaQnaireRespCommand daQnaireRespCmd = new DaQnaireRespCommand(getServiceData(), qnaireResp);
              executeChildCommand(daQnaireRespCmd);
            }
            catch (IcdmException e) {
              getLogger().error("An exception occured while saving data in TDaQnaireResp entity", e);
            }
          }
        }
      });
    }

    getLogger().info("Creating DaQnaireResp details in DB completed");
  }

  /**
   * @param entity
   */
  private void createDaParameterDetails(final TDaDataAssessment tDaDataAssessment) {
    getLogger().info("Creating DaParameter details in DB started");

    tDaDataAssessment.getTDaWpResps().forEach(wpResp -> this.dataAssessmentReportModel.getDataAssmntCompHexData()
        .getDaCompareHexParam().forEach(compHexParam -> {
          if (matchesWithWpResp(wpResp, compHexParam)) {
            DaParameter parameter = new DaParameter();
            parameter.setDaWpRespId(wpResp.getDaWpRespId());
            parameter.setParameterId(CommonUtils.getBigdecimalFromLong(compHexParam.getParameterId()));
            parameter.setParameterName(compHexParam.getParamName());
            parameter.setParameterType(compHexParam.getParamType().getText());
            parameter.setFunctionName(compHexParam.getFuncName());
            parameter.setFunctionVersion(compHexParam.getFuncVers());
            parameter.setRvwA2lVersion(compHexParam.getLatestA2lVersion());
            parameter.setRvwFuncVersion(compHexParam.getLatestFunctionVersion());
            parameter.setQuestionnaireStatus(compHexParam.getQnaireStatus());
            parameter.setReviewedFlag(CommonUtils.getBooleanCode(compHexParam.isReviewed()));
            parameter.setEqualsFlag(CommonUtils.getBooleanCode(compHexParam.isEqual()));
            parameter.setReviewScore(compHexParam.getReviewScore());
            parameter.setReviewRemark(compHexParam.getLatestReviewComments());
            parameter.setRvwParamId(CommonUtils.getBigdecimalFromLong(compHexParam.getRvwParamId()));
            parameter.setRvwResultName(compHexParam.getRvwResultName());
            parameter.setHexValue(compHexParam.getHexValue());
            parameter.setReviewedValue(compHexParam.getReviewedValue());
            parameter.setCompliFlag(CommonUtils.getBooleanCode(compHexParam.isCompli()));
            parameter.setQssdFlag(CommonUtils.getBooleanCode(compHexParam.isQssdParameter()));
            parameter.setReadOnlyFlag(CommonUtils.getBooleanCode(compHexParam.isReadOnly()));
            parameter
                .setDependentCharacteristicFlag(CommonUtils.getBooleanCode(compHexParam.isDependantCharacteristic()));
            parameter.setBlackListFlag(CommonUtils.getBooleanCode(compHexParam.isBlackList()));
            parameter.setNeverReviewedFlag(CommonUtils.getBooleanCode(compHexParam.isNeverReviewed()));
            parameter
                .setQssdResult(compHexParam.getQssdResult() != null ? compHexParam.getQssdResult().getUiValue() : null);
            parameter.setCompliResult(
                compHexParam.getCompliResult() != null ? compHexParam.getCompliResult().getUiValue() : null);
            parameter.setCompliTooltip(compHexParam.getCompliTooltip());
            parameter.setQssdTooltip(compHexParam.getQssdTooltip());
            parameter.setResultId(CommonUtils.getBigdecimalFromLong(compHexParam.getCdrResultId()));

            try {
              // Execute command to persist DaParameter details into TDaParameter entity
              DaParameterCommand daParamCmd = new DaParameterCommand(getServiceData(), parameter);
              executeChildCommand(daParamCmd);

            }
            catch (IcdmException e) {
              getLogger().error("An exception occured while saving data in TDaQnaireResp entity", e);
            }
          }
        }));

    getLogger().info("Creating DaParameter details in DB completed");

  }

  /**
   * @param wpResp
   * @param compHexParam
   * @return
   */
  private boolean matchesWithWpResp(final TDaWpResp wpResp, final DaCompareHexParam compHexParam) {
    return wpResp.getA2lWpName().equalsIgnoreCase(compHexParam.getWpName()) &&
        wpResp.getA2lRespName().equalsIgnoreCase(compHexParam.getRespName());
  }

  /**
   * fetch matching Qnaire resp coresponding to WP-Resp ID
   *
   * @param wpResp
   * @param dataAssmntQnaireResp
   * @return
   */
  private boolean qnaireMtchesWithWpResp(final TDaWpResp wpResp,
      final DataAssessmentQuestionnaires dataAssmntQnaireResp) {
    return (((null != wpResp.getA2lWpId()) &&
        (CommonUtils.getBigdecimalFromLong(dataAssmntQnaireResp.getA2lWpId()).equals(wpResp.getA2lWpId()))) &&
        ((null != wpResp.getA2lRespId()) &&
            (CommonUtils.getBigdecimalFromLong(dataAssmntQnaireResp.getA2lRespId()).equals(wpResp.getA2lRespId()))));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    DaDataAssessmentLoader dataAssessmentLoader = new DaDataAssessmentLoader(getServiceData());
    TDaDataAssessment entity = dataAssessmentLoader.getEntityObject(getInputData().getId());

    if (isObjectChanged(getOldData().getFileArchivalStatus(), getInputData().getFileArchivalStatus())) {
      entity.setFileArchivalStatus(getInputData().getFileArchivalStatus());
    }

    getLogger().debug("Updating the file archival status into tdadataassesment table");
    setUserDetails(COMMAND_MODE.UPDATE, entity);

    if (CommonUtils.isNotNull(this.dataAssessmentReportModel.getBaselineFileData())) {
      getLogger().debug("Inserting the baseline file details into tdafiles table");
      DaFile daFile = new DaFile();
      daFile.setDataAssessmentId(getInputData().getId());
      daFile.setFileData(this.dataAssessmentReportModel.getBaselineFileData());
      daFile.setFileName(this.dataAssessmentReportModel.getBaselineFileName());
      DaFileCommand fileCmd = new DaFileCommand(getServiceData(), daFile);
      executeChildCommand(fileCmd);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Not Applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not Applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // Not Applicable
  }

}
