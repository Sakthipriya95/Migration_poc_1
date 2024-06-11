/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpParamMappingLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.bo.report.pdf.ReviewResultBO.SSD_CLASS;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpRespResolver;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpResponsibility;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_LOCK_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRParameterMetrics;
import com.bosch.caltool.icdm.model.cdr.CDRReportData;
import com.bosch.caltool.icdm.model.cdr.CDRReportModel;
import com.bosch.caltool.icdm.model.cdr.CDRReportParameter;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.CdrReportQnaireRespWrapper;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.ReviewDetails;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;

/**
 * @author hnu1cob
 */
public class CDRReportBO extends AbstractSimpleBusinessObject {

  private final CDRReportData reportData;

  /**
   * @param serviceData service Data
   * @param reportData CDRReportData
   */
  public CDRReportBO(final ServiceData serviceData, final CDRReportData reportData) {
    super(serviceData);
    this.reportData = reportData;
  }

  /**
   * @param variantIds
   * @return CDRReportModel
   * @throws IcdmException IcdmException
   */
  public CDRReportModel getCDRReport() throws IcdmException {

    CDRReportModel cdrReportModel = new CDRReportModel();

    String variantName = null;

    CdrReport cdrReport = new CDRReportLoader(getServiceData()).fetchCDRReportData(this.reportData);

    cdrReportModel.setVersionName(cdrReport.getPidcVersion().getVersionName());

    cdrReportModel.setA2lName(cdrReport.getPidcA2l().getName());

    cdrReportModel.setPidcName(cdrReport.getPidcVersion().getName());

    Collection<ReviewDetails> revDetails = cdrReport.getReviewDetMap().values();

    Iterator<ReviewDetails> iterator = revDetails.iterator();

    if (iterator.hasNext()) {
      ReviewDetails firstValue = iterator.next();
      // access the field data of the first value
      variantName = firstValue.getVariantName();
    }

    if (variantName == null) {
      cdrReportModel.setVariantName("<No-Variant>");
    }
    else {
      cdrReportModel.setVariantName(variantName);
    }

    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(this.reportData.getPidcA2lId());

    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(pidcA2l.getA2lFileId());
    A2LFileInfo a2lFileInfo = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(a2lFile);

    CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper = new RvwQnaireResponseLoader(getServiceData())
        .getQniareRespVersByPidcVersIdAndVarId(pidcA2l.getPidcVersId(), this.reportData.getVarId(), false);

    Map<Long, ParamWpResponsibility> wpRespForParam = getWpRespsForVarGrpAndActiveVers(pidcA2l);

    Map<String, ParamProperties> paramProps =
        new ParameterLoader(getServiceData()).fetchA2lWpRespParamProps(this.reportData);

    for (Characteristic characteristic : a2lFileInfo.getAllSortedLabels(true)) {

      ParamProperties paramProperties = paramProps.get(characteristic.getName());
      if (paramProperties != null) {
        CDRReportParameter cdrReportParam = createCDRReportParam(characteristic, paramProperties,
            wpRespForParam.get(paramProperties.getId()), cdrReport, cdrReportQnaireRespWrapper);
        cdrReportModel.getCdrReportParams().add(cdrReportParam);
      }
    }
    createCDRParameterMetrics(cdrReportModel);

    return cdrReportModel;
  }

  /**
   * @param cdrReportModel
   */
  private void createCDRParameterMetrics(final CDRReportModel cdrReportModel) {
    int noOfParamRvwinA2L = 0;
    int noOfParaminBoschResp = 0;
    int noOfParamRvwinBoschResp = 0;
    int noofParaminBoschRespWithComplQuesStatus = 0;
    int noOfQueswithNegAnswer = 0;

    for (CDRReportParameter parameter : cdrReportModel.getCdrReportParams()) {
      if ("reviewed".equalsIgnoreCase(parameter.getReviewedStatus())) {
        noOfParamRvwinA2L++;
      }

      if (CDRConstants.RB_RESP_TYPE_DISP_NAME.equalsIgnoreCase(parameter.getRespType())) {
        noOfParaminBoschResp++;

        if ("reviewed".equalsIgnoreCase(parameter.getReviewedStatus())) {
          noOfParamRvwinBoschResp++;
        }

        if ("All questions are answered. No negative answers.".equals(parameter.getQnaireStatus())) {
          noofParaminBoschRespWithComplQuesStatus++;
        }
      }

      if ("All answered, but with negative answers.".equals(parameter.getQnaireStatus())) {
        noOfQueswithNegAnswer++;
      }
    }

    // Set the metrics in the CDRParameterMetrics object
    CDRParameterMetrics cdrParameterMetrics = new CDRParameterMetrics();
    cdrParameterMetrics.setNoOfParamRvwinA2L(noOfParamRvwinA2L + " of " + cdrReportModel.getCdrReportParams().size());
    cdrParameterMetrics
        .setNoOfParamRvwinSelection(noOfParamRvwinA2L + " of " + cdrReportModel.getCdrReportParams().size());
    cdrParameterMetrics.setNoOfParaminBoschResp(Integer.toString(noOfParaminBoschResp));
    cdrParameterMetrics.setNoOfParamRvwinBoschResp(Integer.toString(noOfParamRvwinBoschResp));
    cdrParameterMetrics.setPercOfParamRvwinBoschResp(
        String.format("%.2f%%", ((double) noOfParamRvwinBoschResp / noOfParaminBoschResp) * 100));
    cdrParameterMetrics.setPercOfParamRvwinBoschRespwithComplQues(
        String.format("%.2f%%", ((double) noofParaminBoschRespWithComplQuesStatus / noOfParaminBoschResp) * 100));
    cdrParameterMetrics.setNoOfQueswithNegAnswer(Integer.toString(noOfQueswithNegAnswer));

    // Set the CDRParameterMetrics object in the CDRReportModel
    cdrReportModel.setCdrParameterMetrics(cdrParameterMetrics);
  }


  /**
   * @param characteristic
   * @param paramProperties
   * @param paramWpResponsibility
   * @param cdrReport
   * @param a2lFileContents
   * @param cdrReport
   * @param wpDefnVersion
   * @param cdrReportQnaireRespWrapper
   * @return
   * @throws DataException
   */
  private CDRReportParameter createCDRReportParam(final Characteristic characteristic,
      final ParamProperties paramProperties, final ParamWpResponsibility paramWpResponsibility,
      final CdrReport cdrReport, final CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper)
      throws DataException {


    CDRReportParameter cdrReportParam = new CDRReportParameter();

    setParameterDetails(characteristic, paramProperties, paramWpResponsibility, cdrReportParam);
    setReviewDetails(cdrReportParam, cdrReport);

    if (CommonUtils.isNotNull(paramWpResponsibility)) {
      setQnaireDetails(paramWpResponsibility, cdrReportParam, cdrReportQnaireRespWrapper);
      calculateAndSetWPFinStatus(cdrReportParam, paramWpResponsibility, cdrReport);
    }

    return cdrReportParam;
  }

  /**
   * @param cdrReportParam
   * @param paramWpResponsibility
   * @param cdrReport
   */
  private void calculateAndSetWPFinStatus(final CDRReportParameter cdrReportParam,
      final ParamWpResponsibility paramWpResponsibility, final CdrReport cdrReport) {

    String wpRespStatus = CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getUiType();
    Map<Long, A2lWpResponsibilityStatus> respIdAndStatusMap =
        cdrReport.getWpIdRespIdAndStatusMap().get(paramWpResponsibility.getWpId());

    if (CommonUtils.isNotEmpty(respIdAndStatusMap) &&
        (respIdAndStatusMap.get(paramWpResponsibility.getRespId()) != null)) {
      wpRespStatus = respIdAndStatusMap.get(paramWpResponsibility.getRespId()).getWpRespFinStatus();
    }

    cdrReportParam.setWpFinishedStatus(wpRespStatus);
  }

  /**
   * @param paramWpResponsibility
   * @param cdrReportParam
   * @param cdrReportQnaireRespWrapper
   */
  private void setQnaireDetails(final ParamWpResponsibility paramWpResponsibility,
      final CDRReportParameter cdrReportParam, final CdrReportQnaireRespWrapper cdrReportQnaireRespWrapper) {

    String qnaireStatus = ApicConstants.EMPTY_STRING;
    if (cdrReportQnaireRespWrapper.getWpRespQnaireRespVersMap().containsKey(paramWpResponsibility.getRespId())) {
      Map<Long, String> wpQnaireRespVerMap =
          cdrReportQnaireRespWrapper.getWpRespQnaireRespVersStatusMap().get(paramWpResponsibility.getRespId());
      if (wpQnaireRespVerMap.containsKey(paramWpResponsibility.getWpId())) {
        qnaireStatus = wpQnaireRespVerMap.get(paramWpResponsibility.getWpId());
      }
    }
    if (CommonUtils.isNotEmptyString(qnaireStatus)) {
      qnaireStatus = CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireStatus).getUiType();
    }
    else {
      qnaireStatus = CDRConstants.NOT_APPLICABLE_DISP_NAME;
    }
    cdrReportParam.setQnaireStatus(qnaireStatus);

    if (CommonUtils.isNotEmpty(cdrReportQnaireRespWrapper.getAllWpRespQnaireRespVersMap()) && CommonUtils.isNotEmpty(
        cdrReportQnaireRespWrapper.getAllWpRespQnaireRespVersMap().get(paramWpResponsibility.getRespId()))) {
      for (Entry<Long, Set<RvwQnaireRespVersion>> wpQnaireVersionsEntrySet : cdrReportQnaireRespWrapper
          .getAllWpRespQnaireRespVersMap().get(paramWpResponsibility.getRespId()).entrySet()) {
        if (wpQnaireVersionsEntrySet.getKey().equals(paramWpResponsibility.getWpId())) {
          Set<RvwQnaireRespVersion> nonNullVersions =
              wpQnaireVersionsEntrySet.getValue().stream().filter(Objects::nonNull).collect(Collectors.toSet());
          cdrReportParam.getRvwQnaireVersSet().addAll(nonNullVersions);
        }
      }
    }

  }

  /**
   * @param cdrReportParam
   * @param cdrReport
   */
  private void setReviewDetails(final CDRReportParameter cdrReportParam, final CdrReport cdrReport) {

    Map<Long, ReviewDetails> reviewDetMap = cdrReport.getReviewDetMap();

    ParameterReviewDetails paramRvwDetails = getParamReviewDetails(cdrReportParam.getParamName(), 0, cdrReport);

    if (paramRvwDetails != null) {
      ReviewDetails reviewDetails = reviewDetMap.get(paramRvwDetails.getRvwID());

      if (CommonUtils.isNotNull(reviewDetails)) {
        cdrReportParam.setLatestA2lVersion(reviewDetails.getSdomPverVariant());
        cdrReportParam.setReviewedStatus(isReviewedStatus(paramRvwDetails, reviewDetails));
      }

      Map<Long, String> rvwFuncMap = cdrReport.getRvwFuncMap();
      if (CommonUtils.isNotEmpty(rvwFuncMap)) {
        cdrReportParam.setLatestFuncVersion(rvwFuncMap.get(paramRvwDetails.getFuncID()));
      }
      cdrReportParam.setLatestReviewComment(paramRvwDetails.getRvwComment());

      String reviewScore = DATA_REVIEW_SCORE.getType(paramRvwDetails.getReviewScore()).getScoreDisplay();
      cdrReportParam.setReviewScore(reviewScore);
    }
    else {
      cdrReportParam.setReviewScore(DATA_REVIEW_SCORE.getType("0").getScoreDisplay());
    }

    for (int rvwIndex = 0; rvwIndex < cdrReport.getMaxParamReviewCount(); rvwIndex++) {
      ParameterReviewDetails paramRvwDetailsBasedOnRvwIndex =
          getParamReviewDetails(cdrReportParam.getParamName(), rvwIndex, cdrReport);

      if (paramRvwDetailsBasedOnRvwIndex != null) {
        long rvwId = paramRvwDetailsBasedOnRvwIndex.getRvwID();
        ReviewDetails reviewDetails = reviewDetMap.get(rvwId);
        cdrReportParam.getRvwDetails().add(reviewDetails);
        cdrReportParam.getParamRvwDetailsMap().put(rvwId, paramRvwDetailsBasedOnRvwIndex);
      }
    }

  }

  /**
   * Returns true if the a2l parameter is reviewed and official and locked
   *
   * @param reviewDetails the given a2l param name
   * @return true if the a2l parameter is reviewed and official and locked
   */
  // ICDM-2585 (Parent Task ICDM-2412)-2
  private boolean isOfficialAndLocked(final ReviewDetails reviewDetails) {
    return ((CDRConstants.REVIEW_TYPE.OFFICIAL == CDRConstants.REVIEW_TYPE.getType(reviewDetails.getReviewType())) &&
        (REVIEW_LOCK_STATUS.YES == REVIEW_LOCK_STATUS.getType(reviewDetails.getLockStatus())));
  }

  /**
   * Returns whether the parameter is reviewed
   *
   * @param paramRvwDetails ParameterReviewDetails
   * @param reviewDetails selc param name
   * @return 'Reviewed' or 'Not Reviewed' or '' (Empty String)
   */
  private String isReviewedStatus(final ParameterReviewDetails paramRvwDetails, final ReviewDetails reviewDetails) {
    if (CommonUtils.isNotEmptyString(paramRvwDetails.getReviewScore())) {
      if (DATA_REVIEW_SCORE.getType(paramRvwDetails.getReviewScore()).isReviewed() &&
          isOfficialAndLocked(reviewDetails)) {
        return ApicConstants.REVIEWED;
      }
      return ApicConstants.NOT_REVIEWED;
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * Get the parameter review detail object for the given parameter and column index.
   *
   * @param paramName name of parameter
   * @param colIndex review result number, identified by column index
   * @return ParameterReviewDetails
   */
  private ParameterReviewDetails getParamReviewDetails(final String paramName, final int colIndex,
      final CdrReport cdrReport) {
    List<ParameterReviewDetails> paramRvwDetList = cdrReport.getParamRvwDetMap().get(paramName);
    if ((paramRvwDetList != null) && (colIndex < paramRvwDetList.size())) {
      return paramRvwDetList.get(colIndex);
    }
    return null;
  }

  private void setParameterDetails(final Characteristic characteristic, final ParamProperties paramProperties,
      final ParamWpResponsibility paramWpResponsibility, final CDRReportParameter cdrReportParam)
      throws DataException {
    cdrReportParam.setParamName(characteristic.getName());
    cdrReportParam.setParamType(ParameterType.valueOf(characteristic.getType()));

    if (CommonUtils.isNotNull(characteristic.getDefFunction())) {
      cdrReportParam.setFuncName(characteristic.getDefFunction().getName());
      cdrReportParam.setFuncVersion(characteristic.getDefFunction().getFunctionVersion());
    }

    cdrReportParam.setParamId(paramProperties.getId());

    setSSDClassOfParameter(paramProperties, cdrReportParam, characteristic);

    cdrReportParam.setCodeWordStr(CommonUtils.getDisplayText(paramProperties.isCodeWord()));

    if (CommonUtils.isNotNull(paramWpResponsibility)) {
      cdrReportParam.setWpRespId(paramWpResponsibility.getWpRespId());
      A2lWorkPackage a2lWorkPackage =
          new A2lWorkPackageLoader(getServiceData()).getDataObjectByID(paramWpResponsibility.getWpId());
      cdrReportParam.setA2lWpName(a2lWorkPackage.getName());
      cdrReportParam.setA2lWpId(a2lWorkPackage.getId());
      A2lResponsibility a2lResponsibility =
          new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(paramWpResponsibility.getRespId());
      cdrReportParam.setA2lRespName(a2lResponsibility.getName());
      cdrReportParam.setA2lRespId(a2lResponsibility.getId());
      cdrReportParam.setRespType(WpRespType.getType(a2lResponsibility.getRespType()).getDispName());
    }
  }

  private void setSSDClassOfParameter(final ParamProperties paramProperties, final CDRReportParameter cdrReportParam,
      final Characteristic characteristic) {
    cdrReportParam.setCompliParam(isComplianceParam(paramProperties));
    cdrReportParam.setQssdParam(paramProperties.isQssdParameter());
    cdrReportParam.setBlackListParam(paramProperties.isBlackList());
    cdrReportParam.setReadOnlyParam(characteristic.isReadOnly());
    cdrReportParam.setDependentParam(characteristic.isDependentCharacteristic());
    if (characteristic.getDependentCharacteristic() != null) {
      cdrReportParam.setDependentCharacteristics(characteristic.getDependentCharacteristic().getCharacteristicName());
    }
  }

  /**
   * @param paramProperties ParamProperties
   * @return if the param is compliant
   */
  private boolean isComplianceParam(final ParamProperties paramProperties) {
    SSD_CLASS ssdClass = SSD_CLASS.getSsdClass(paramProperties.getSsdClass());
    if (ssdClass == null) {
      return false;
    }
    return ssdClass.isCompliant();
  }

  private Map<Long, ParamWpResponsibility> getWpRespsForVarGrpAndActiveVers(final PidcA2l pidcA2l)
      throws IcdmException {
    TA2lWpDefnVersion activeA2lWpDefnVersion =
        new A2lWpDefnVersionLoader(getServiceData()).getActiveA2lWPDefnVersionEntityFromA2l(pidcA2l.getId());

    // fetch the matching variant group for the pidcVaraint
    A2lVariantGroup a2lVarGrp = new WorkPackageStatusHandler(getServiceData()).fetchVariantGroup(activeA2lWpDefnVersion,
        this.reportData.getVarId());

    Map<Long, A2lVariantGroup> a2lVarGrpMap = new HashMap<>();
    Map<Long, A2lWpParamMapping> a2lWParamInfoMap = new HashMap<>();
    Map<Long, A2lWpResponsibility> wpRespMap = new HashMap<>();

    A2lWpResponsibilityLoader wpResponsibilityLoader = new A2lWpResponsibilityLoader(getServiceData());
    A2lWpParamMappingLoader wpParamMappingLoader = new A2lWpParamMappingLoader(getServiceData());

    // Map of Param Id and A2l WP Param Mapping
    Map<Long, A2lWpParamMapping> wpParamMappingMap = new HashMap<>();

    for (TA2lWpResponsibility tA2lWPRespPal : activeA2lWpDefnVersion.getTA2lWpResponsibility()) {
      wpRespMap.put(tA2lWPRespPal.getWpRespId(), wpResponsibilityLoader.getDataObjectByID(tA2lWPRespPal.getWpRespId()));
      for (TA2lWpParamMapping tA2lParamMapping : tA2lWPRespPal.getTA2lWpParamMappings()) {
        A2lWpParamMapping wpParamMapping =
            wpParamMappingLoader.getDataObjectByID(tA2lParamMapping.getWpParamMappingId());
        a2lWParamInfoMap.put(tA2lParamMapping.getWpParamMappingId(), wpParamMapping);
        wpParamMappingMap.put(wpParamMapping.getParamId(), wpParamMapping);
      }
    }

    ParamWpRespResolver resolver = new ParamWpRespResolver(a2lVarGrpMap, wpRespMap, a2lWParamInfoMap);

    return resolver.getRespForParam(a2lVarGrp != null ? a2lVarGrp.getId() : null);
  }

}
