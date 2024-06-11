package com.bosch.caltool.icdm.bo.cdr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.cdr.TDaDataAssessment;
import com.bosch.caltool.icdm.database.entity.cdr.TDaParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TDaQnaireResp;
import com.bosch.caltool.icdm.database.entity.cdr.TDaWpResp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;
import com.bosch.caltool.icdm.model.cdr.QSSDResValues;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentCompareHexData;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;


/**
 * Loader class for DaDataAssessment
 *
 * @author say8cob
 */
public class DaDataAssessmentLoader extends AbstractBusinessObject<DaDataAssessment, TDaDataAssessment> {

  private static final String QUESTIONNAIRE_DELIMITER = "->";

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public DaDataAssessmentLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.DA_DATA_ASSESSMENT, TDaDataAssessment.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected DaDataAssessment createDataObject(final TDaDataAssessment entity) throws DataException {
    DaDataAssessment object = new DaDataAssessment();

    object.setId(entity.getDataAssessmentId());
    object.setBaselineName(entity.getBaselineName());
    object.setDescription(entity.getDescription());
    object.setTypeOfAssignment(entity.getTypeOfAssignment());
    object.setPidcVersId(entity.getPidcVersId());
    object.setPidcVersFullname(entity.getPidcVersFullname());
    object.setVariantId(entity.getVariantId());
    object.setVariantName(entity.getVariantName());
    object.setPidcA2lId(entity.getPidcA2lId());
    object.setA2lFilename(entity.getA2lFilename());
    object.setHexFileName(entity.getHexFileName());
    object.setWpDefnVersId(entity.getWpDefnVersId());
    object.setWpDefnVersName(entity.getWpDefnVersName());
    object.setVcdmDstSource(entity.getVcdmDstSource());
    object.setVcdmDstVersId(entity.getVcdmDstVersId());
    object.setFileArchivalStatus(entity.getFileArchivalStatus());
    object.setPreviousPidcVersConsidered(entity.getPreviousPidcVersConsidered());

    setCommonFields(object, entity);

    return object;
  }

  /**
   * @return Set<DaDataAssessment>
   * @throws DataException exception when data not found
   */
  public Set<DaDataAssessment> getBaselinesForPidcA2l(final Long pidcA2lId) throws DataException {
    TypedQuery<TDaDataAssessment> query =
        getEntMgr().createNamedQuery(TDaDataAssessment.GET_BASELINES_FOR_PIDC_A2L_ID, TDaDataAssessment.class);
    query.setParameter("pidcA2lId", pidcA2lId);
    List<TDaDataAssessment> resultList = query.getResultList();
    Set<DaDataAssessment> dataAssessmentSet = new HashSet<>();
    for (TDaDataAssessment dataAssesment : resultList) {
      dataAssessmentSet.add(createDataObject(dataAssesment));
    }
    return dataAssessmentSet;
  }


  /**
   * @param dataAssessmentId Data Assessment Report Id
   * @return DataAssessmentReport model with details for displaying entire Baseline Data
   * @throws ClassNotFoundException ClassNotFoundException while getting CalData Object
   * @throws IOException IOException while getting CalData Object
   * @throws IcdmException Exception in service
   */
  public DataAssessmentReport getDataAssessmentReportDetails(final Long dataAssessmentId)
      throws ClassNotFoundException, IOException, IcdmException {

    DataAssessmentReport daReport = new DataAssessmentReport();
    DaDataAssessment daDataAssessment = getDataObjectByID(dataAssessmentId);

    PidcA2l pidcA2l =
        new PidcA2lLoader(getServiceData()).getDataObjectByID(daDataAssessment.getPidcA2lId().longValue());
    daReport.setA2lFileId(pidcA2l.getA2lFileId());
    daReport.setA2lFileName(daDataAssessment.getA2lFilename());
    daReport.setPidcA2lId(pidcA2l.getId());

    daReport.setWpDefnVersId(daDataAssessment.getWpDefnVersId().longValue());
    daReport.setWpDefnVersName(daDataAssessment.getWpDefnVersName());

    TPidcVersion pidcVers =
        new PidcVersionLoader(getServiceData()).getEntityObject(daDataAssessment.getPidcVersId().longValue());

    Pidc pidc = new PidcLoader(getServiceData()).getDataObjectByID(pidcVers.getTabvProjectidcard().getProjectId());
    daReport.setPidcId(pidc.getId());
    daReport.setPidcName(pidc.getName());

    daReport.setPidcVersId(daDataAssessment.getPidcVersId().longValue());
    daReport.setPidcVersName(daDataAssessment.getPidcVersFullname());

    daReport
        .setPidcVariantId(daDataAssessment.getVariantId() != null ? daDataAssessment.getVariantId().longValue() : null);
    daReport.setPidcVariantName(daDataAssessment.getVariantName());

    daReport.setHexFileName(daDataAssessment.getHexFileName());
    daReport.setBaselineName(daDataAssessment.getBaselineName());
    daReport.setBaselineCreatedDate(daDataAssessment.getCreatedDate());
    daReport.setTypeOfAssignment(daDataAssessment.getTypeOfAssignment());
    daReport.setDescription(daDataAssessment.getDescription());
    daReport.setVcdmDstSource(daDataAssessment.getVcdmDstSource());
    daReport.setVcdmDstVersId(
        daDataAssessment.getVcdmDstVersId() != null ? daDataAssessment.getVcdmDstVersId().longValue() : null);
    daReport
        .setConsiderRvwsOfPrevPidcVers(CommonUtils.getBooleanType(daDataAssessment.getPreviousPidcVersConsidered()));

    TDaDataAssessment tDaDataAssessment = getEntityObject(dataAssessmentId);

    fillDaWpRespParamQnaireDetails(tDaDataAssessment, daReport);

    Set<DaDataAssessment> dataAssessmentSet = getBaselinesForPidcA2l(daReport.getPidcA2lId());
    daReport.setDataAssmntBaselines(dataAssessmentSet);

    fillStatistics(daReport);
    daReport.setCompliParamInA2L(tDaDataAssessment.getCompliParamInA2L());
    daReport.setCompliParamPassed(tDaDataAssessment.getCompliParamPassed());
    daReport.setCompliParamCSSDFail(tDaDataAssessment.getCompliParamCSSDFail());
    daReport.setCompliParamNoRuleFail(tDaDataAssessment.getCompliParamNoRuleFail());
    daReport.setCompliParamSSD2RVFail(tDaDataAssessment.getCompliParamSSD2RVFail());
    daReport.setQssdParamFail(tDaDataAssessment.getQssdParamFail());
    return daReport;
  }

  /**
   * Fill WpResp, CompareHex Parameter and Questionnaire details
   *
   * @param tDaDataAssessment
   * @param daReport
   * @throws ClassNotFoundException
   * @throws IOException
   * @throws IcdmException
   */
  private void fillDaWpRespParamQnaireDetails(final TDaDataAssessment tDaDataAssessment,
      final DataAssessmentReport daReport)
      throws ClassNotFoundException, IOException, IcdmException {
    List<TDaWpResp> tDaWpResps = tDaDataAssessment.getTDaWpResps();
    Set<DaWpResp> wpRespSet = new HashSet<>();
    DaWpRespLoader daWpRespLoader = new DaWpRespLoader(getServiceData());

    List<DaCompareHexParam> daCompareHexParams = new ArrayList<>();
    DataAssessmentCompareHexData daCompHexData = new DataAssessmentCompareHexData();
    daReport.setDataAssmntCompHexData(daCompHexData);
    for (TDaWpResp tDaWpResp : tDaWpResps) {
      wpRespSet.add(daWpRespLoader.createDataObject(tDaWpResp));
      for (TDaParameter tDaParam : tDaWpResp.getTDaParameters()) {
        DaCompareHexParam daCompHexParam = constructDaCompareHexParam(tDaParam, tDaWpResp);
        if (CommonUtils.isEqual(WpRespType.RB, WpRespType.getType(daCompHexParam.getRespType()))) {
          calculateHexDataReviewNotEqualCount(daReport, daCompHexParam);
          calculateRbParamsTotalAndNotRvdCount(daReport, daCompHexParam);
        }
        daCompareHexParams.add(daCompHexParam);
      }
      for (TDaQnaireResp tDaQnaireResp : tDaWpResp.getTDaQnaireResps()) {
        constructQnaireRespDetail(daReport, tDaQnaireResp, tDaWpResp);
      }
      if (CommonUtils.isEqual(WpRespType.RB, WpRespType.getType(tDaWpResp.getA2lRespType()))) {
        daReport.setWpRbRespTotalCount(daReport.getWpRbRespTotalCount() + 1);
        if (!CommonUtils.getBooleanType(tDaWpResp.getWpFinishedFlag())) {
          daReport.setWpNotFinishedCount(daReport.getWpNotFinishedCount() + 1);
        }
      }
    }
    daCompHexData.setDaCompareHexParam(daCompareHexParams);
    daReport.setDataAssmntWps(wpRespSet);
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
   * @param daReport
   * @param tDaQnaireResp
   * @param tDaWpResp
   * @return
   * @throws IcdmException
   */
  private void constructQnaireRespDetail(final DataAssessmentReport daReport, final TDaQnaireResp tDaQnaireResp,
      final TDaWpResp tDaWpResp)
      throws IcdmException {
    RvwQnaireResponseLoader rvwQnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());
    DataAssessmentQuestionnaires daQnaire = new DataAssessmentQuestionnaires();

    daQnaire.setA2lRespId(tDaWpResp.getA2lRespId().longValue());
    daQnaire.setA2lRespName(tDaWpResp.getA2lRespName());
    daQnaire.setA2lRespType(tDaWpResp.getA2lRespType());
    daQnaire.setA2lWpId(tDaWpResp.getA2lWpId().longValue());
    daQnaire.setA2lWpName(tDaWpResp.getA2lWpName());
    daQnaire.setId(tDaQnaireResp.getDaQnaireRespId());
    daQnaire.setQnaireBaselineExisting(CommonUtils.getBooleanType(tDaQnaireResp.getBaselineExistingFlag()));
    long rvwQnaireRespId = tDaQnaireResp.getQnaireRespId().longValue();
    ExternalLinkInfo qnaireExternaLink = rvwQnaireResponseLoader.fetchQnaireExternaLink(
        rvwQnaireResponseLoader.getDataObjectByID(rvwQnaireRespId), daReport.getPidcVariantId());
    daQnaire.setQnaireBaselineLink(qnaireExternaLink);
    daQnaire.setQnaireNegativeAnsCount(tDaQnaireResp.getNumNegativeAnswers().intValue());
    daQnaire.setQnaireNeutralAnsCount(tDaQnaireResp.getNumNeutralAnswers().intValue());
    daQnaire.setQnairePositiveAnsCount(tDaQnaireResp.getNumPositiveAnswers().intValue());
    daQnaire.setQnaireReadyForProd(CommonUtils.getBooleanType(tDaQnaireResp.getReadyForProductionFlag()));
    if (CommonUtils.isEqual(WpRespType.RB, WpRespType.getType(daQnaire.getA2lRespType()))) {
      daReport.setQnairesRbRespTotalCount(daReport.getQnairesRbRespTotalCount() + 1);
      if (!daQnaire.isQnaireReadyForProd()) {
        daReport.setQnairesNotAnsweredCount(daReport.getQnairesNotAnsweredCount() + 1);
      }
    }
    daQnaire.setQnaireRespId(rvwQnaireRespId);
    daQnaire.setQnaireRespName(tDaQnaireResp.getQnaireRespName());
    StringBuilder qnaireLinkBuilder = new StringBuilder();

    daQnaire.setQnaireBaselineLinkDisplayText(qnaireLinkBuilder.append(daQnaire.getA2lRespName())
        .append(QUESTIONNAIRE_DELIMITER).append(daQnaire.getA2lWpName()).append(QUESTIONNAIRE_DELIMITER)
        .append(daQnaire.getQnaireRespName()).toString());
    daQnaire.setQnaireRespVersId(tDaQnaireResp.getQnaireRespVersId().longValue());
    daQnaire.setQnaireRespVersName(tDaQnaireResp.getQnaireRespVersionName());
    daQnaire.setQnaireReviewedDate(timestamp2String(tDaQnaireResp.getReviewedDate()));
    daQnaire.setQnaireReviewedUser(tDaQnaireResp.getReviewedUser());

    daReport.getDataAssmntQnaires().add(daQnaire);
  }

  /**
   * @param tDaParam
   * @param tDaWpResp
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private DaCompareHexParam constructDaCompareHexParam(final TDaParameter tDaParam, final TDaWpResp tDaWpResp)
      throws ClassNotFoundException, IOException {
    DaCompareHexParam daCompHexParam = new DaCompareHexParam();
    daCompHexParam.setBlackList(CommonUtils.getBooleanType(tDaParam.getBlackListFlag()));
    daCompHexParam.setCdrResultId(tDaParam.getResultId() != null ? tDaParam.getResultId().longValue() : null);
    daCompHexParam.setCompli(CommonUtils.getBooleanType(tDaParam.getCompliFlag()));
    daCompHexParam.setCompliResult(CompliResValues.getCompliResValue(tDaParam.getCompliResult()));
    daCompHexParam.setCompliTooltip(tDaParam.getCompliTooltip());
    daCompHexParam.setDependantCharacteristic(CommonUtils.getBooleanType(tDaParam.getDependentCharacteristicFlag()));
    daCompHexParam.setEqual(CommonUtils.getBooleanType(tDaParam.getEqualsFlag()));
    daCompHexParam.setFuncName(tDaParam.getFunctionName());
    daCompHexParam.setFuncVers(tDaParam.getFunctionVersion());
    daCompHexParam.setHexValue(tDaParam.getHexValue());
    daCompHexParam.setLatestA2lVersion(tDaParam.getRvwA2lVersion());
    daCompHexParam.setLatestFunctionVersion(tDaParam.getRvwFuncVersion());
    daCompHexParam.setLatestReviewComments(tDaParam.getReviewRemark());
    daCompHexParam.setNeverReviewed(CommonUtils.getBooleanType(tDaParam.getNeverReviewedFlag()));
    daCompHexParam.setParamName(tDaParam.getParameterName());
    daCompHexParam.setParamType(ParameterType.valueOf(tDaParam.getParameterType()));
    daCompHexParam.setParameterId(tDaParam.getParameterId().longValue());
    daCompHexParam.setQnaireStatus(tDaParam.getQuestionnaireStatus());
    daCompHexParam.setQssdParameter(CommonUtils.getBooleanType(tDaParam.getQssdFlag()));
    daCompHexParam.setQssdResult(QSSDResValues.getQSSDResValue(tDaParam.getQssdResult()));
    daCompHexParam.setQssdTooltip(tDaParam.getQssdTooltip());
    daCompHexParam.setReadOnly(CommonUtils.getBooleanType(tDaParam.getReadOnlyFlag()));
    daCompHexParam.setRespName(tDaWpResp.getA2lRespName());
    daCompHexParam.setRespType(tDaWpResp.getA2lRespType());
    daCompHexParam.setReviewed(CommonUtils.getBooleanType(tDaParam.getReviewedFlag()));
    String reviewScoreDbType = tDaParam.getReviewScore();
    daCompHexParam.setRvwResultName(tDaParam.getRvwResultName());
    daCompHexParam.setRvwParamId(tDaParam.getRvwParamId() != null ? tDaParam.getRvwParamId().longValue() : null);
    daCompHexParam.setReviewedValue(tDaParam.getReviewedValue());
    daCompHexParam.setReviewScore(reviewScoreDbType);
    daCompHexParam.setHundredPecReviewScore(DATA_REVIEW_SCORE.getType(reviewScoreDbType).getHundredPercScoreDisplay());
    daCompHexParam.setWpFinishedStatus(tDaWpResp.getWpFinishedFlag());
    daCompHexParam.setWpName(tDaWpResp.getA2lWpName());


    return daCompHexParam;
  }

  /**
   * Fill hexFileDataEqualWithDataReviews, allWpsFinished, allParametersReviewed, allQnairesAnswered, readyForSeries in
   * DataAssessmentReport model
   *
   * @param daReport
   */
  private void fillStatistics(final DataAssessmentReport daReport) {
    boolean hexValEqualToRevVal = true;
    boolean isReviewed = true;
    boolean isQnaireAnsweredBaselined = true;

    boolean wpReadyForProduction = true;
    for (DaWpResp daWpResp : daReport.getDataAssmntWps()) {
      if (CommonUtils.isEqual(WpRespType.RB, WpRespType.getType(daWpResp.getA2lRespType()))) {
        hexValEqualToRevVal = hexValEqualToRevVal && CommonUtils.getBooleanType(daWpResp.getHexRvwEqualFlag());
        isReviewed = isReviewed && CommonUtils.getBooleanType(daWpResp.getParameterReviewedFlag());
        isQnaireAnsweredBaselined = isQnaireAnsweredBaselined &&
            !CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.NO.getDbType().equals(daWpResp.getQnairesAnsweredFlag());
        wpReadyForProduction =
            wpReadyForProduction && CommonUtils.getBooleanType(daWpResp.getWpReadyForProductionFlag());
      }
    }
    daReport.setAllParametersReviewed(isReviewed);
    daReport.setAllQnairesAnswered(isQnaireAnsweredBaselined);
    daReport.setHexFileDataEqualWithDataReviews(hexValEqualToRevVal);
    daReport.setReadyForSeries(wpReadyForProduction);
  }

}
