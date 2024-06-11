/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDResultParam;
import com.bosch.caltool.icdm.bo.compli.CompliReviewData;
import com.bosch.caltool.icdm.bo.shapereview.ShapeReviewResult;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ParamRepeatExcelData;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespUpdationModel;

/**
 * @author bru2cob
 */
public class ReviewedInfo {

  private Map<String, byte[]> inputFileStreamMap;
  private Set<String> generatedCheckSSDFiles;
  private Map<String, CheckSSDResultParam> pimaryChkSSDResParamMap;
  private Map<String, CDRConstants.RESULT_FLAG> secResultMap = new HashMap<>();
  private Map<String, List<ReviewRule>> primarySSDRules;
  private Set<AttributeValueModel> attrValModel;
  private Map<String, String> paramFilesMap;

  /**
   * Key - label name, value - list of warnings
   */
  private Map<String, List<String>> parserWarningsMap = new HashMap<>();
  /**
   * mapping source id to identify a2l group mapping or not
   */
  private Long mappingSource;
  /**
   * Selected wp name
   */
  private String grpWorkPackageName;

  private Set<String> compliCheckSSDOutputFiles;
  private SortedSet<AttributeValueModel> attrWithoutMapping;
  /**
   * List of files which not does have a2l param
   */
  List<String> listOfInvalidInputFiles;

  List<ParamRepeatExcelData> paramsRepeated = new ArrayList<>();
  /**
   * Map holding parsed caldata objects
   */
  private Map<String, CalData> calDataMap;
  /**
   * Functions to be reviewed
   */
  private SortedSet<Function> cdrFunctionsList;
  /**
   * Map containing functions and params corresponding to it
   */
  private Map<Long, Set<Parameter>> reviewFuncParamMap;
  /**
   * Parameters list to be reviewed
   */
  private Set<?> cdrFuncParams;
  /**
   * unassigned params used in review.
   */
  private final Set<String> unassParaminReview = new HashSet<>();

  /**
   * Set to hold parameters which are not present in the rule set
   */
  private final Set<String> paramNotInRuleset = new TreeSet<>();
  private boolean isCompliParamsPresent = false;
  private boolean isQssdParamsPresent = false;
  private PidcVersionAttributeModel pidcDetails;
  private boolean noValidRuleFlag;
  /**
   * value not set Attr
   */
  private SortedSet<AttributeValueModel> valueNotSetAttr;
  /**
   * list of secondary rule set and data
   */
  private final List<ReviewRuleSetData> secRuleSetDataList = new ArrayList<>();

  /**
   * SSD file path
   */
  private String primarySSDFilePath;

  /**
   * Set to hold parameters which are present in the rule set and has no rule
   */
  private final Set<String> paramWithoutRule = new TreeSet<>();

  private A2LFileInfo a2lFileContents;
  /**
   * Compli review data
   */
  CompliReviewData compliData;
  /**
   * Ssd Software Version Id
   */
  private Long ssdSoftwareVersionId;

  /**
   * Map Contains Review Params and Wp Resp Map - Key Param Id and Value combination of WpRespId and A2lRespId
   */
  private Map<Long, RvwWpAndRespModel> rvwParamAndWpRespModelMap = new HashMap<>();

  /**
   * Map Contains Rvw Wp Resp Map and RvwWpRespId- Key combination of WpRespId and A2lRespId and Value RvwWpRespId
   */
  private Map<RvwWpAndRespModel, Long> rvwWpRespModelAndRvwWPRespIdMap = new HashMap<>();

  /**
   * set of wpRespId and A2lRespId
   */
  private Set<RvwWpAndRespModel> rvwWpAndRespModelSet = new HashSet<>();


  /**
   * Contains the general qnaire response to be created during official review
   */
  private Set<QnaireRespUpdationModel> rvwQnaireRespCreationModelSet = new HashSet<>();

  /**
   * Set of read-only parameters Id
   */
  private Set<String> readOnlyParamNameSet;

  /**
   * Dependent parameters - Key - Parameter model, value - List of Depends on characteristics name
   */
  private Map<String, List<String>> depParams;

  /**
   * @return the filesStreamMap
   */
  public Map<String, byte[]> getFilesStreamMap() {
    return this.inputFileStreamMap;
  }


  /**
   * @param filesStreamMap the filesStreamMap to set
   */
  public void setFilesStreamMap(final Map<String, byte[]> filesStreamMap) {
    this.inputFileStreamMap = filesStreamMap;
  }


  /**
   * @return the ssdSoftwareVersionId
   */
  public Long getSsdSoftwareVersionId() {
    return this.ssdSoftwareVersionId;
  }


  /**
   * @param ssdSoftwareVersionId the ssdSoftwareVersionId to set
   */
  public void setSsdSoftwareVersionId(final Long ssdSoftwareVersionId) {
    this.ssdSoftwareVersionId = ssdSoftwareVersionId;
  }


  /**
   * @return the compliData
   */
  public CompliReviewData getCompliData() {
    return this.compliData;
  }


  /**
   * @param compliData the compliData to set
   */
  public void setCompliData(final CompliReviewData compliData) {
    this.compliData = compliData;
  }


  /**
   * Check SSD Logger
   */
  private ILoggerAdapter checkSSDLoggr;
  private ShapeReviewResult srResult;


  /**
   * @return the srResult
   */
  public ShapeReviewResult getSrResult() {
    return this.srResult;
  }


  /**
   * @param srResult the srResult to set
   */
  public void setSrResult(final ShapeReviewResult srResult) {
    this.srResult = srResult;
  }

  /**
   * @param checkSSDLoggr CheckSSDLogger
   */
  public void setCheckSSDLogger(final ILoggerAdapter checkSSDLoggr) {
    this.checkSSDLoggr = checkSSDLoggr;

  }

  /**
   * @return CheckSSD Logger
   */
  public ILoggerAdapter getCheckSSDLogger() {
    return this.checkSSDLoggr;

  }

  /**
   * @return the a2lFileContents
   */
  public A2LFileInfo getA2lFileContents() {
    return this.a2lFileContents;
  }


  /**
   * @param a2lFileContents the a2lFileContents to set
   */
  public void setA2lFileContents(final A2LFileInfo a2lFileContents) {
    this.a2lFileContents = a2lFileContents;
  }


  /**
   * @return the paramWithoutRule
   */
  public Set<String> getParamWithoutRule() {
    return this.paramWithoutRule;
  }


  /**
   * @return the primarySSDFilePath
   */
  public String getPrimarySSDFilePath() {
    return this.primarySSDFilePath;
  }


  /**
   * @param primarySSDFilePath the primarySSDFilePath to set
   */
  public void setPrimarySSDFilePath(final String primarySSDFilePath) {
    this.primarySSDFilePath = primarySSDFilePath;
  }


  /**
   * @return the pimaryChkSSDResParamMap
   */
  public Map<String, CheckSSDResultParam> getPimaryChkSSDResParamMap() {
    return this.pimaryChkSSDResParamMap;
  }


  /**
   * @param pimaryChkSSDResParamMap the pimaryChkSSDResParamMap to set
   */
  public void setPimaryChkSSDResParamMap(final Map<String, CheckSSDResultParam> pimaryChkSSDResParamMap) {
    this.pimaryChkSSDResParamMap = pimaryChkSSDResParamMap;
  }


  /**
   * @return the noValidRuleFlag
   */
  public boolean isNoValidRuleFlag() {
    return this.noValidRuleFlag;
  }


  /**
   * @param noValidRuleFlag the noValidRuleFlag to set
   */
  public void setNoValidRuleFlag(final boolean noValidRuleFlag) {
    this.noValidRuleFlag = noValidRuleFlag;
  }


  /**
   * @return the ruleSetDataList
   */
  public List<ReviewRuleSetData> getSecRuleSetDataList() {
    return this.secRuleSetDataList;
  }


  /**
   * @return the valueNotSetAttr
   */
  public SortedSet<AttributeValueModel> getValueNotSetAttr() {
    return this.valueNotSetAttr;
  }


  /**
   * @param valueNotSetAttr the valueNotSetAttr to set
   */
  public void setValueNotSetAttr(final SortedSet<AttributeValueModel> valueNotSetAttr) {
    this.valueNotSetAttr = valueNotSetAttr;
  }


  /**
   * @return the pidcDetails
   */
  public PidcVersionAttributeModel getPidcDetails() {
    return this.pidcDetails;
  }


  /**
   * @param pidcDetails the pidcDetails to set
   */
  public void setPidcDetails(final PidcVersionAttributeModel pidcDetails) {
    this.pidcDetails = pidcDetails;
  }


  /**
   * @return the isCompliParamsPresent
   */
  public boolean isCompliParamsPresent() {
    return this.isCompliParamsPresent;
  }


  /**
   * @param isCompliParamsPresent the isCompliParamsPresent to set
   */
  public void setCompliParamsPresent(final boolean isCompliParamsPresent) {
    this.isCompliParamsPresent = isCompliParamsPresent;
  }

  /**
   * @return the paramNotInRuleset
   */
  public Set<String> getParamNotInRuleset() {
    return this.paramNotInRuleset;
  }

  /**
   * @return the unassParaminReview
   */
  public Set<String> getUnassParaminReview() {
    return this.unassParaminReview;
  }

  /**
   * @return the cdrFuncParams
   */
  public Set<?> getCdrFuncParams() {
    return this.cdrFuncParams;
  }


  /**
   * @param cdrFuncParams the cdrFuncParams to set
   */
  public void setCdrFuncParams(final Set<?> cdrFuncParams) {
    this.cdrFuncParams = cdrFuncParams;
  }


  /**
   * @return the reviewFuncParamMap
   */
  public Map<Long, Set<Parameter>> getReviewFuncParamMap() {
    return this.reviewFuncParamMap;
  }


  /**
   * @param reviewFuncParamMap the reviewFuncParamMap to set
   */
  public void setReviewFuncParamMap(final Map<Long, Set<Parameter>> reviewFuncParamMap) {
    this.reviewFuncParamMap = reviewFuncParamMap;
  }


  /**
   * @param secResultMap the secResultMap to set
   */
  public void setSecResultMap(final Map<String, CDRConstants.RESULT_FLAG> secResultMap) {
    this.secResultMap = secResultMap;
  }


  /**
   * @return the cdrFunctionsList
   */
  public SortedSet<Function> getCdrFunctionsList() {
    return this.cdrFunctionsList;
  }


  /**
   * @param cdrFunctionsList the cdrFunctionsList to set
   */
  public void setCdrFunctionsList(final SortedSet<Function> cdrFunctionsList) {
    this.cdrFunctionsList = cdrFunctionsList;
  }


  /**
   * @return the generatedCheckSSDFiles
   */
  public Set<String> getGeneratedCheckSSDFiles() {
    return this.generatedCheckSSDFiles;
  }

  /**
   * @param generatedCheckSSDFiles the generatedCheckSSDFiles to set
   */
  public void setGeneratedCheckSSDFiles(final Set<String> generatedCheckSSDFiles) {
    this.generatedCheckSSDFiles = generatedCheckSSDFiles;
  }


  /**
   * @return the primarySSDRules
   */
  public Map<String, List<ReviewRule>> getPrimarySSDRules() {
    return this.primarySSDRules;
  }

  /**
   * @param primarySSDRules the primarySSDRules to set
   */
  public void setPrimarySSDRules(final Map<String, List<ReviewRule>> primarySSDRules) {
    this.primarySSDRules = primarySSDRules;
  }

  /**
   * @return the attrValModel
   */
  public Set<AttributeValueModel> getAttrValModel() {
    return this.attrValModel;
  }

  /**
   * @param attrValModel the attrValModel to set
   */
  public void setAttrValModel(final Set<AttributeValueModel> attrValModel) {
    this.attrValModel = attrValModel;
  }

  /**
   * @return the paramFilesMap
   */
  public Map<String, String> getParamFilesMap() {
    return this.paramFilesMap;
  }

  /**
   * @param paramFilesMap the paramFilesMap to set
   */
  public void setParamFilesMap(final Map<String, String> paramFilesMap) {
    this.paramFilesMap = paramFilesMap;
  }


  /**
   * @return the compliCheckSSDOutputFiles
   */
  public Set<String> getCompliCheckSSDOutputFiles() {
    return this.compliCheckSSDOutputFiles;
  }

  /**
   * @param compliCheckSSDOutputFiles the compliCheckSSDOutputFiles to set
   */
  public void setCompliCheckSSDOutputFiles(final Set<String> compliCheckSSDOutputFiles) {
    this.compliCheckSSDOutputFiles = compliCheckSSDOutputFiles;
  }

  /**
   * @return the attrWithoutMapping
   */
  public SortedSet<AttributeValueModel> getAttrWithoutMapping() {
    return this.attrWithoutMapping;
  }


  /**
   * @param attrWithoutMapping the attrWithoutMapping to set
   */
  public void setAttrWithoutMapping(final SortedSet<AttributeValueModel> attrWithoutMapping) {
    this.attrWithoutMapping = attrWithoutMapping;
  }

  /**
   * @return the secResultMap
   */
  public Map<String, CDRConstants.RESULT_FLAG> getSecResultMap() {
    return this.secResultMap;
  }

  /**
   * @return the listOfSelectedFiles
   */
  public List<String> getListOfInvalidInputFiles() {
    return this.listOfInvalidInputFiles;
  }


  /**
   * @param listOfInvalidInputFiles the listOfSelectedFiles to set
   */
  public void setListOfInvalidInputFiles(final List<String> listOfInvalidInputFiles) {
    this.listOfInvalidInputFiles = listOfInvalidInputFiles;
  }


  /**
   * @return the paramsRepeated
   */
  public List<ParamRepeatExcelData> getParamsRepeated() {
    return this.paramsRepeated;
  }


  /**
   * @param paramsRepeated the paramsRepeated to set
   */
  public void setParamsRepeated(final List<ParamRepeatExcelData> paramsRepeated) {
    this.paramsRepeated = paramsRepeated;
  }

  /**
   * @return the selWorkPackageId
   */
  public String getGrpWorkPackageName() {
    return this.grpWorkPackageName;
  }

  /**
   * @param selWorkPackageName the selWorkPackageId to set
   */
  public void setGrpWorkPackageName(final String selWorkPackageName) {
    this.grpWorkPackageName = selWorkPackageName;
  }

  /**
   * @return the calDataMap
   */
  public Map<String, CalData> getCalDataMap() {
    return this.calDataMap;
  }


  /**
   * @param calDataMap the calDataMap to set
   */
  public void setCalDataMap(final Map<String, CalData> calDataMap) {
    this.calDataMap = calDataMap;
  }

  /**
   * @return the mappingSource
   */
  public Long getMappingSource() {
    return this.mappingSource;
  }


  /**
   * @param mappingSource the mappingSource to set
   */
  public void setMappingSource(final Long mappingSource) {
    this.mappingSource = mappingSource;
  }


  /**
   * @return the rvwParamAndWpRespModelMap
   */
  public Map<Long, RvwWpAndRespModel> getRvwParamAndWpRespModelMap() {
    return this.rvwParamAndWpRespModelMap;
  }


  /**
   * @param rvwParamAndWpRespModelMap the rvwParamAndWpRespModelMap to set
   */
  public void setRvwParamAndWpRespModelMap(final Map<Long, RvwWpAndRespModel> rvwParamAndWpRespModelMap) {
    this.rvwParamAndWpRespModelMap = rvwParamAndWpRespModelMap;
  }


  /**
   * @return the rvwWpRespModelAndRvwWPRespIdMap
   */
  public Map<RvwWpAndRespModel, Long> getRvwWpRespModelAndRvwWPRespIdMap() {
    return this.rvwWpRespModelAndRvwWPRespIdMap;
  }


  /**
   * @param rvwWpRespModelAndRvwWPRespIdMap the rvwWpRespModelAndRvwWPRespIdMap to set
   */
  public void setRvwWpRespModelAndRvwWPRespIdMap(final Map<RvwWpAndRespModel, Long> rvwWpRespModelAndRvwWPRespIdMap) {
    this.rvwWpRespModelAndRvwWPRespIdMap = rvwWpRespModelAndRvwWPRespIdMap;
  }


  /**
   * @return the rvwWpAndRespModelSet
   */
  public Set<RvwWpAndRespModel> getRvwWpAndRespModelSet() {
    return this.rvwWpAndRespModelSet;
  }


  /**
   * @param rvwWpAndRespModelSet the rvwWpAndRespModelSet to set
   */
  public void setRvwWpAndRespModelSet(final Set<RvwWpAndRespModel> rvwWpAndRespModelSet) {
    this.rvwWpAndRespModelSet = rvwWpAndRespModelSet;
  }


  /**
   * @return the isQssdParamsPresent
   */
  public boolean isQssdParamsPresent() {
    return this.isQssdParamsPresent;
  }


  /**
   * @param isQssdParamsPresent the isQssdParamsPresent to set
   */
  public void setQssdParamsPresent(final boolean isQssdParamsPresent) {
    this.isQssdParamsPresent = isQssdParamsPresent;
  }


  /**
   * @return the rvwQnaireRespCreationModels
   */
  public Set<QnaireRespUpdationModel> getRvwQnaireRespCreationModelSet() {
    return this.rvwQnaireRespCreationModelSet;
  }


  /**
   * @param rvwQnaireRespCreationModelSet the rvwQnaireRespCreationModels to set
   */
  public void setRvwQnaireRespCreationModelSet(final Set<QnaireRespUpdationModel> rvwQnaireRespCreationModelSet) {
    this.rvwQnaireRespCreationModelSet = rvwQnaireRespCreationModelSet;
  }

  /**
   * @return the parserWarningsMap
   */
  public Map<String, List<String>> getParserWarningsMap() {
    return this.parserWarningsMap;
  }


  /**
   * @param parserWarningsMap the parserWarningsMap to set
   */
  public void setParserWarningsMap(final Map<String, List<String>> parserWarningsMap) {
    this.parserWarningsMap = parserWarningsMap;
  }

  /**
   * @return the readOnlyParams
   */
  public Set<String> getReadOnlyParamNameSet() {
    return this.readOnlyParamNameSet;
  }


  /**
   * @param readOnlyParamNameSet the readOnlyParamNameSet
   */
  public void setReadOnlyParamNameSet(final Set<String> readOnlyParamNameSet) {
    this.readOnlyParamNameSet = readOnlyParamNameSet;
  }


  /**
   * @return the depParams
   */
  public Map<String, List<String>> getDepParams() {
    return this.depParams;
  }


  /**
   * @param collect
   */
  public void setDepParams(final Map<String, List<String>> charDepCharListMap) {
    this.depParams = charDepCharListMap;
  }

}
