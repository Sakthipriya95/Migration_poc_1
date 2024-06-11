/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.dataassessment;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;


/**
 * @author TRL1COB
 */
public class DataAssessmentReport {

  /**
   * Id of the A2L File
   */
  private Long a2lFileId;
  /**
   * Name of the A2L File
   */
  private String a2lFileName;
  /**
   * Id of the PIDC
   */
  private Long pidcId;
  /**
   * Name of the PIDC
   */
  private String pidcName;
  /**
   * Id of the PIDC Version
   */
  private Long pidcVersId;
  /**
   * Name of the PIDC Version
   */
  private String pidcVersName;
  /**
   * Id of the PIDC Variant
   */
  private Long pidcVariantId;
  /**
   * Name of the PIDC Variant
   */
  private String pidcVariantName;
  /**
   * Mapping ID of PIDC and A2L
   */
  private Long pidcA2lId;
  /**
   * Name of the HEX file
   */
  private String hexFileName;
  /**
   * Source path of the hex file
   */
  private String srcHexFilePath;
  /**
   * Name of the baseline to be created
   */
  private String baselineName;
  /**
   * Type of assignment to be created
   */
  private String typeOfAssignment;
  /**
   * Description on the baseline
   */
  private String description;
  /**
   * Wp Definition Version ID
   */
  private Long wpDefnVersId;
  /**
   * Wp Definition Version name
   */
  private String wpDefnVersName;
  /**
   * vcdm dst source
   */
  private String vcdmDstSource;
  /**
   * vcdm dst version id
   */
  private Long vcdmDstVersId;

  /**
   * Flag to indicate if the HEX file data is equal with data reviews 'Filtered Parameter reviewed Not Equal' from
   * compHexStatistics should be 0
   */
  private boolean hexFileDataEqualWithDataReviews;

  private boolean considerRvwsOfPrevPidcVers;

  /**
   * @return the considerPreviousPidcVersions
   */
  public boolean getConsiderRvwsOfPrevPidcVers() {
    return this.considerRvwsOfPrevPidcVers;
  }


  /**
   * @param considerPreviousPidcVersions the considerPreviousPidcVersions to set
   */
  public void setConsiderRvwsOfPrevPidcVers(final boolean considerPreviousPidcVersions) {
    this.considerRvwsOfPrevPidcVers = considerPreviousPidcVersions;
  }


  /**
   * Label count that are not equal to the data review value
   */
  private int hexDataReviewNotEqualCount;
  /**
   * WPs not finished count
   */
  private int wpNotFinishedCount;
  /**
   * WPs in RB resp total count
   */
  private int wpRbRespTotalCount;
  /**
   * Flag to indicate if all parameters are reviewed
   */
  private boolean allParametersReviewed;
  /**
   * RB parameters not reviewed count
   */
  private int rbParamsNotRvdCount;
  /**
   * Flag to indicate if all Qnaires are answered
   */
  private boolean allQnairesAnswered;
  /**
   * Qnaires not answered count
   */
  private int qnairesNotAnsweredCount;
  /**
   * Qnaires in RB resp total count
   */
  private int qnairesRbRespTotalCount;
  /**
   * Flag to indicate if ready for series
   */
  private boolean readyForSeries;
  /**
   * Baseline created date
   */
  private String baselineCreatedDate;
  /**
   * Zipped Baseline File Data
   */
  private byte[] baselineFileData;
  /**
   * Temp path of the baseline directory
   */
  private String baselineFileName;
  /**
   * The DataAssessment CompareHexData
   */
  private DataAssessmentCompareHexData dataAssmntCompHexData;
  /**
   * Set of Data assessment workpackages
   */
  private Set<DaWpResp> dataAssmntWps = new HashSet<>();
  /**
   * Set of Data assessment Qnaires
   */
  private Set<DataAssessmentQuestionnaires> dataAssmntQnaires = new HashSet<>();
  /**
   * Set of Data assessment Baselines
   */
  private Set<DaDataAssessment> dataAssmntBaselines = new HashSet<>();
  /**
   * Compli parameters in A2L
   */
  private Integer compliParamInA2L;
  /**
   * Compli parameters passed
   */
  private Integer compliParamPassed;
  /**
   * Compli parameters failed(C-SSD)
   */
  private Integer compliParamCSSDFail;
  /**
   * Compli parameters failed(NO RULE)
   */
  private Integer compliParamNoRuleFail;
  /**
   * Compli parameters failed(SSD2RV)
   */
  private Integer compliParamSSD2RVFail;
  /**
   * Q-SSD parameters failed
   */
  private Integer qssdParamFail;

  /**
   *
   */
  public DataAssessmentReport() {
    super();
  }

  /**
   * @return the a2lFileId
   */
  public Long getA2lFileId() {
    return this.a2lFileId;
  }

  /**
   * @param a2lFileId the a2lFileId to set
   */
  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }

  /**
   * @return the a2lFileName
   */
  public String getA2lFileName() {
    return this.a2lFileName;
  }

  /**
   * @param a2lFileName the a2lFileName to set
   */
  public void setA2lFileName(final String a2lFileName) {
    this.a2lFileName = a2lFileName;
  }

  /**
   * @return the pidcId
   */
  public Long getPidcId() {
    return this.pidcId;
  }

  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
  }

  /**
   * @return the pidcName
   */
  public String getPidcName() {
    return this.pidcName;
  }

  /**
   * @param pidcName the pidcName to set
   */
  public void setPidcName(final String pidcName) {
    this.pidcName = pidcName;
  }

  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }

  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  /**
   * @return the pidcVersName
   */
  public String getPidcVersName() {
    return this.pidcVersName;
  }

  /**
   * @param pidcVersName the pidcVersName to set
   */
  public void setPidcVersName(final String pidcVersName) {
    this.pidcVersName = pidcVersName;
  }

  /**
   * @return the pidcVariantId
   */
  public Long getPidcVariantId() {
    return this.pidcVariantId;
  }

  /**
   * @param pidcVariantId the pidcVariantId to set
   */
  public void setPidcVariantId(final Long pidcVariantId) {
    this.pidcVariantId = pidcVariantId;
  }

  /**
   * @return the pidcVariantName
   */
  public String getPidcVariantName() {
    return this.pidcVariantName;
  }

  /**
   * @param pidcVariantName the pidcVariantName to set
   */
  public void setPidcVariantName(final String pidcVariantName) {
    this.pidcVariantName = pidcVariantName;
  }

  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }

  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

  /**
   * @return the hexFileName
   */
  public String getHexFileName() {
    return this.hexFileName;
  }

  /**
   * @param hexFileName the hexFileName to set
   */
  public void setHexFileName(final String hexFileName) {
    this.hexFileName = hexFileName;
  }


  /**
   * @return the srcHexFilePath
   */
  public String getSrcHexFilePath() {
    return this.srcHexFilePath;
  }


  /**
   * @param srcHexFilePath the srcHexFilePath to set
   */
  public void setSrcHexFilePath(final String srcHexFilePath) {
    this.srcHexFilePath = srcHexFilePath;
  }

  /**
   * @return the baselineName
   */
  public String getBaselineName() {
    return this.baselineName;
  }


  /**
   * @param baselineName the baselineName to set
   */
  public void setBaselineName(final String baselineName) {
    this.baselineName = baselineName;
  }


  /**
   * @return the typeOfAssignment
   */
  public String getTypeOfAssignment() {
    return this.typeOfAssignment;
  }


  /**
   * @param typeOfAssignment the typeOfAssignment to set
   */
  public void setTypeOfAssignment(final String typeOfAssignment) {
    this.typeOfAssignment = typeOfAssignment;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the wpDefnVersId
   */
  public Long getWpDefnVersId() {
    return this.wpDefnVersId;
  }


  /**
   * @param wpDefnVersId the wpDefnVersId to set
   */
  public void setWpDefnVersId(final Long wpDefnVersId) {
    this.wpDefnVersId = wpDefnVersId;
  }


  /**
   * @return the wpDefnVersName
   */
  public String getWpDefnVersName() {
    return this.wpDefnVersName;
  }


  /**
   * @param wpDefnVersName the wpDefnVersName to set
   */
  public void setWpDefnVersName(final String wpDefnVersName) {
    this.wpDefnVersName = wpDefnVersName;
  }

  /**
   * @return the hexFileDataEqualWithDataReviews
   */
  public boolean isHexFileDataEqualWithDataReviews() {
    return this.hexFileDataEqualWithDataReviews;
  }

  /**
   * @param hexFileDataEqualWithDataReviews the hexFileDataEqualWithDataReviews to set
   */
  public void setHexFileDataEqualWithDataReviews(final boolean hexFileDataEqualWithDataReviews) {
    this.hexFileDataEqualWithDataReviews = hexFileDataEqualWithDataReviews;
  }


  /**
   * @return the hexDataReviewNotEqualCount
   */
  public int getHexDataReviewNotEqualCount() {
    return this.hexDataReviewNotEqualCount;
  }


  /**
   * @param hexDataReviewNotEqualCount the hexDataReviewNotEqualCount to set
   */
  public void setHexDataReviewNotEqualCount(final int hexDataReviewNotEqualCount) {
    this.hexDataReviewNotEqualCount = hexDataReviewNotEqualCount;
  }


  /**
   * @return the wpNotFinishedCount
   */
  public int getWpNotFinishedCount() {
    return this.wpNotFinishedCount;
  }


  /**
   * @param wpNotFinishedCount the wpNotFinishedCount to set
   */
  public void setWpNotFinishedCount(final int wpNotFinishedCount) {
    this.wpNotFinishedCount = wpNotFinishedCount;
  }


  /**
   * @return the wpRbRespTotalCount
   */
  public int getWpRbRespTotalCount() {
    return this.wpRbRespTotalCount;
  }


  /**
   * @param wpRbRespTotalCount the wpRbRespTotalCount to set
   */
  public void setWpRbRespTotalCount(final int wpRbRespTotalCount) {
    this.wpRbRespTotalCount = wpRbRespTotalCount;
  }

  /**
   * @return the allParametersReviewed
   */
  public boolean isAllParametersReviewed() {
    return this.allParametersReviewed;
  }

  /**
   * @param allParametersReviewed the allParametersReviewed to set
   */
  public void setAllParametersReviewed(final boolean allParametersReviewed) {
    this.allParametersReviewed = allParametersReviewed;
  }

  /**
   * @return the rbParamsNotRvdCount
   */
  public int getRbParamsNotRvdCount() {
    return this.rbParamsNotRvdCount;
  }


  /**
   * @param rbParamsNotRvdCount the rbParamsNotRvdCount to set
   */
  public void setRbParamsNotRvdCount(final int rbParamsNotRvdCount) {
    this.rbParamsNotRvdCount = rbParamsNotRvdCount;
  }

  /**
   * @return the allQnairesAnswered
   */
  public boolean isAllQnairesAnswered() {
    return this.allQnairesAnswered;
  }

  /**
   * @param allQnairesAnswered the allQnairesAnswered to set
   */
  public void setAllQnairesAnswered(final boolean allQnairesAnswered) {
    this.allQnairesAnswered = allQnairesAnswered;
  }

  /**
   * @return the qnairesNotAnsweredCount
   */
  public int getQnairesNotAnsweredCount() {
    return this.qnairesNotAnsweredCount;
  }


  /**
   * @param qnairesNotAnsweredCount the qnairesNotAnsweredCount to set
   */
  public void setQnairesNotAnsweredCount(final int qnairesNotAnsweredCount) {
    this.qnairesNotAnsweredCount = qnairesNotAnsweredCount;
  }


  /**
   * @return the qnairesRbRespTotalCount
   */
  public int getQnairesRbRespTotalCount() {
    return this.qnairesRbRespTotalCount;
  }


  /**
   * @param qnairesRbRespTotalCount the qnairesRbRespTotalCount to set
   */
  public void setQnairesRbRespTotalCount(final int qnairesRbRespTotalCount) {
    this.qnairesRbRespTotalCount = qnairesRbRespTotalCount;
  }

  /**
   * @return the readyForSeries
   */
  public boolean isReadyForSeries() {
    return this.readyForSeries;
  }

  /**
   * @param readyForSeries the readyForSeries to set
   */
  public void setReadyForSeries(final boolean readyForSeries) {
    this.readyForSeries = readyForSeries;
  }

  /**
   * @return the dataAssmntWps
   */
  public Set<DaWpResp> getDataAssmntWps() {
    return this.dataAssmntWps;
  }

  /**
   * @param dataAssmntWps the dataAssmntWps to set
   */
  public void setDataAssmntWps(final Set<DaWpResp> dataAssmntWps) {
    this.dataAssmntWps = dataAssmntWps;
  }

  /**
   * @return the dataAssmntQnaires
   */
  public Set<DataAssessmentQuestionnaires> getDataAssmntQnaires() {
    return this.dataAssmntQnaires;
  }

  /**
   * @param dataAssmntQnaires the dataAssmntQnaires to set
   */
  public void setDataAssmntQnaires(final Set<DataAssessmentQuestionnaires> dataAssmntQnaires) {
    this.dataAssmntQnaires = dataAssmntQnaires;
  }

  /**
   * @return the dataAssmntBaselines
   */
  public Set<DaDataAssessment> getDataAssmntBaselines() {
    return this.dataAssmntBaselines;
  }

  /**
   * @param dataAssmntBaselines the dataAssmntBaselines to set
   */
  public void setDataAssmntBaselines(final Set<DaDataAssessment> dataAssmntBaselines) {
    this.dataAssmntBaselines = dataAssmntBaselines;
  }

  /**
   * @return the dataAssmntCompHexData
   */
  public DataAssessmentCompareHexData getDataAssmntCompHexData() {
    return this.dataAssmntCompHexData;
  }

  /**
   * @param dataAssmntCompHexData the dataAssmntCompHexData to set
   */
  public void setDataAssmntCompHexData(final DataAssessmentCompareHexData dataAssmntCompHexData) {
    this.dataAssmntCompHexData = dataAssmntCompHexData;
  }

  /**
   * @return the baselineCreatedDate
   */
  public String getBaselineCreatedDate() {
    return this.baselineCreatedDate;
  }

  /**
   * @param baselineCreatedDate the baselineCreatedDate to set
   */
  public void setBaselineCreatedDate(final String baselineCreatedDate) {
    this.baselineCreatedDate = baselineCreatedDate;
  }


  /**
   * @return the baselineFileData
   */
  public byte[] getBaselineFileData() {
    return this.baselineFileData;
  }


  /**
   * @param baselineFileData the baselineFileData to set
   */
  public void setBaselineFileData(final byte[] baselineFileData) {
    this.baselineFileData = baselineFileData;
  }


  /**
   * @return the baselineFileName
   */
  public String getBaselineFileName() {
    return this.baselineFileName;
  }


  /**
   * @param baselineFileName the baselineFileName to set
   */
  public void setBaselineFileName(final String baselineFileName) {
    this.baselineFileName = baselineFileName;
  }

  /**
   * @return the vcdmDstSource
   */
  public String getVcdmDstSource() {
    return this.vcdmDstSource;
  }

  /**
   * @param vcdmDstSource the vcdmDstSource to set
   */
  public void setVcdmDstSource(final String vcdmDstSource) {
    this.vcdmDstSource = vcdmDstSource;
  }

  /**
   * @return the vcdmDstVersId
   */
  public Long getVcdmDstVersId() {
    return this.vcdmDstVersId;
  }

  /**
   * @param vcdmDstVersId the vcdmDstVersId to set
   */
  public void setVcdmDstVersId(final Long vcdmDstVersId) {
    this.vcdmDstVersId = vcdmDstVersId;
  }


  /**
   * @return the compliParamInA2L
   */
  public Integer getCompliParamInA2L() {
    return this.compliParamInA2L;
  }


  /**
   * @param compliParamInA2L the compliParamInA2L to set
   */
  public void setCompliParamInA2L(final Integer compliParamInA2L) {
    this.compliParamInA2L = compliParamInA2L;
  }


  /**
   * @return the compliParamPassed
   */
  public Integer getCompliParamPassed() {
    return this.compliParamPassed;
  }


  /**
   * @param compliParamPassed the compliParamPassed to set
   */
  public void setCompliParamPassed(final Integer compliParamPassed) {
    this.compliParamPassed = compliParamPassed;
  }


  /**
   * @return the compliParamCSSDFail
   */
  public Integer getCompliParamCSSDFail() {
    return this.compliParamCSSDFail;
  }


  /**
   * @param compliParamCSSDFail the compliParamCSSDFail to set
   */
  public void setCompliParamCSSDFail(final Integer compliParamCSSDFail) {
    this.compliParamCSSDFail = compliParamCSSDFail;
  }


  /**
   * @return the compliParamNoRuleFail
   */
  public Integer getCompliParamNoRuleFail() {
    return this.compliParamNoRuleFail;
  }


  /**
   * @param compliParamNoRuleFail the compliParamNoRuleFail to set
   */
  public void setCompliParamNoRuleFail(final Integer compliParamNoRuleFail) {
    this.compliParamNoRuleFail = compliParamNoRuleFail;
  }


  /**
   * @return the compliParamSSD2RVFail
   */
  public Integer getCompliParamSSD2RVFail() {
    return this.compliParamSSD2RVFail;
  }


  /**
   * @param compliParamSSD2RVFail the compliParamSSD2RVFail to set
   */
  public void setCompliParamSSD2RVFail(final Integer compliParamSSD2RVFail) {
    this.compliParamSSD2RVFail = compliParamSSD2RVFail;
  }


  /**
   * @return the qssdParamFail
   */
  public Integer getQssdParamFail() {
    return this.qssdParamFail;
  }


  /**
   * @param qssdParamFail the qssdParamFail to set
   */
  public void setQssdParamFail(final Integer qssdParamFail) {
    this.qssdParamFail = qssdParamFail;
  }

}
