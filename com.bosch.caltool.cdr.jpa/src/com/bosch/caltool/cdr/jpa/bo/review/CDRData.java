/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.a2l.jpa.A2LEditorDataProvider;
import com.bosch.caltool.a2l.jpa.bo.A2LGroup;
import com.bosch.caltool.a2l.jpa.bo.WorkPackage;
import com.bosch.caltool.apic.jpa.bo.A2LFile;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.AttributeValueModel;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.cdr.jpa.bo.CDRFunction;
import com.bosch.caltool.cdr.jpa.bo.CDRResult;
import com.bosch.caltool.cdr.jpa.bo.CheckSSDResultParam;
import com.bosch.caltool.cdr.jpa.bo.IcdmWorkPackage;
import com.bosch.caltool.cdr.jpa.bo.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.monicareportparser.data.MonitoringToolOutput;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.SSDRelease;


/**
 * @author rgo7cob
 */
public class CDRData {

  /**
   * Selected a2l file for review
   */
  private A2LFile selA2LFile;

  /**
   * Selected PID card of the a2l file slected
   */
  private PIDCVersion selPidcVer;

  /**
   * Selected PIDC variant from the pid card
   */
  private PIDCVariant selPIDCVariant;

  /**
   * Selected Calibration Engineer, Current user by default
   */
  private ApicUser selCalEngineer;

  /**
   * Selected auditor, any icdm user
   */
  private ApicUser selAuditor;

  /**
   * ICDM 658 Review Description
   */
  private String description;

  /**
   * Selected participants, set of icdm users
   */
  private SortedSet<ApicUser> selParticipants;

  /**
   * Selected workpackage // Requires A2L JPA plugin!
   */
  private WorkPackage selWorkPackage;

  /**
   * Selected review functions name of the wp // Requires A2L JPA plugin!
   */
  private SortedSet<String> selReviewFuncs = new TreeSet<String>();


  /**
   * Selected files path
   */
  private Set<String> selFilesPath;

  /**
   * A2lFile contents
   */
  private A2LFileInfo a2lFileContents;
  /**
   *
   */
  private A2LEditorDataProvider a2lEditorDP;
  /**
  *
  */
  private A2LGroup a2lGroup;

  /**
   * Map holding parsed caldata objects
   */
  private Map<String, CalData> calDataMap;

  /**
   * Functions to be reviewed
   */
  private SortedSet<CDRFunction> cdrFunctionsList;

  /**
   * Parameters list to be reviewed
   */
  private Set<?> cdrFuncParams;

  // ICDM-2063
  /**
   * set of Questionnaire Version attached
   */
  private SortedSet<QuestionnaireVersion> qnaireVersSet = new TreeSet<QuestionnaireVersion>();

  /**
   * Icdm-874 set and get the value of Review Type review Type test or official
   */

  private CDRConstants.REVIEW_TYPE reviewType;

  /**
   * SSD file path
   */
  private String primarySSDFilePath;
  // ICDM-1720
  /**
   * Map containing params and file corresponding to it
   */
  private Map<String, String> paramFilesMap;


  /**
   * Map containing functions and params corresponding to it
   */
  private Map<Long, Set<CDRFuncParameter>> reviewFuncParamMap;

  /**
   * Map holding parsed CheckSSDResultParam objects
   */
  private Map<String, CheckSSDResultParam> pimaryChkSSDResParamMap;

  /**
   * CheckSSD output files location
   */
  private Set<String> generatedCheckSSDFiles;

  /**
   * Review source type
   */
  private CDRConstants.CDR_SOURCE_TYPE sourceType;

  private Map<String, List<CDRRule>> primarySSDRules;

  /**
   * Icdm-1215 - Set for Storing the Attr Value Model
   */
  private Set<AttributeValueModel> attrValModel;

  private final Map<String, A2LFile> a2lFileMap = new ConcurrentHashMap<>();

  /** Fun file or lab file selected Icdm-715 */
  private String funLabFilePath = "";

  private A2LGroup selA2LGroup;

  private Long mappingSource;

  /**
   * Instance of cdr result
   */
  private CDRResult cdrParentResult;

  private CDRResult canceledCDRResult;


  private List<String> labelList = new ArrayList<String>();


  private List<ParamRepeatExcelData> paramsRepeated;


  /**
   * labels of multiple files selected
   */
  private List<String> multiFilelabels = new ArrayList<String>();

  /**
   * a2l Char map
   */
  private Map<String, Characteristic> a2lCharMap;


  private final ReviewRuleSetData primaryReviewRuleSetData = new ReviewRuleSetData();

  private boolean noValidRuleFlag;

  /**
   * true, if review is delta review
   */
  private boolean deltaReview;

  /**
   * Check SSD Logger
   */
  private ILoggerAdapter checkSSDLoggr;

  /**
   * value not set Attr
   */
  private SortedSet<AttributeValueModel> valueNotSetAttr;


  /**
   * attr mapping not available
   */
  private SortedSet<AttributeValueModel> attrWithoutMapping;
  private boolean wpChanged;

  private List<A2LGroup> multiGrp;

  /**
   * MoniCa file path
   */
  private String monicaFilePath;

  /**
   * monicaOp tool output
   */
  private MonitoringToolOutput monicaOutput;

  private boolean offReviewType;

  private boolean startReviewType;

  private boolean onlyLockedOffReview;

  private boolean onlyLockedStartResults;

  private SortedSet<IcdmWorkPackage> questionnaireWps = new TreeSet<IcdmWorkPackage>();

  private SortedSet<IcdmWorkPackage> questionnairesInUse = new TreeSet<IcdmWorkPackage>();

  private boolean pidcChanged;

  /**
   * Selected PID card of the a2l file slected
   */
  private PIDCVersion sourcePidcVer;

  /**
   * Selected PIDC variant from the pid card
   */
  private PIDCVariant sourcePIDCVariant;

  private AttributeValue division;

  /**
   * unassigned params used in review.
   */
  private final List<String> unassParaminReview = new ArrayList<>();

  /**
   * Task 231284
   */
  private boolean isCommonRulesNeeded;

  /**
   * true if common rule is primary
   */
  private boolean commonRulesPrimary;

  /**
   * list of secondary rule set and data
   */
  private final List<ReviewRuleSetData> ruleSetDataList = new ArrayList<>();


  private final Map<String, CDRConstants.RESULT_FLAG> secResultMap = new ConcurrentHashMap<>();

  private List<String> compliLabels = new ArrayList<>();

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

  private boolean isCompliParamsPresent = false;

  private SSDRelease selSSDRelease;

  /**
   * @return the division
   */
  public AttributeValue getDivision() {
    return this.division;
  }


  /**
   * @param division the division to set
   */
  public void setDivision(final AttributeValue division) {
    this.division = division;
  }


  /**
   * @return the isPidcChanged
   */
  public boolean isPidcChanged() {
    return this.pidcChanged;
  }


  /**
   * @param isPidcChanged the isPidcChanged to set
   */
  public void setPidcChanged(final boolean isPidcChanged) {
    this.pidcChanged = isPidcChanged;
  }


  /**
   * @return the sourcePidcVer
   */
  public PIDCVersion getSourcePidcVer() {
    return this.sourcePidcVer;
  }


  /**
   * @param sourcePidcVer the sourcePidcVer to set
   */
  public void setSourcePidcVer(final PIDCVersion sourcePidcVer) {
    this.sourcePidcVer = sourcePidcVer;
  }


  /**
   * @return the sourcePIDCVariant
   */
  public PIDCVariant getSourcePIDCVariant() {
    return this.sourcePIDCVariant;
  }


  /**
   * @param sourcePIDCVariant the sourcePIDCVariant to set
   */
  public void setSourcePIDCVariant(final PIDCVariant sourcePIDCVariant) {
    this.sourcePIDCVariant = sourcePIDCVariant;
  }


  /**
   * @return the questionnaireWps
   */
  public SortedSet<IcdmWorkPackage> getQuestionnaireWps() {
    return this.questionnaireWps;
  }


  /**
   * @param questionnaireWps the questionnaireWps to set
   */
  public void setQuestionnaireWps(final SortedSet<IcdmWorkPackage> questionnaireWps) {
    this.questionnaireWps = questionnaireWps;
  }


  /**
   * @return the monicaOutput
   */
  public MonitoringToolOutput getMonicaOutput() {
    return this.monicaOutput;
  }


  /**
   * @return the multiGrp
   */
  public List<A2LGroup> getMultiGrp() {
    return this.multiGrp;
  }


  /**
   * @param multiGrp the multiGrp to set
   */
  public void setMultiGrp(final List<A2LGroup> multiGrp) {
    this.multiGrp = multiGrp;
  }


  /**
   * @return the wpChanged
   */
  public boolean isWpChanged() {
    return this.wpChanged;
  }


  /**
   * @return the valueNotSetAttr
   */
  public SortedSet<AttributeValueModel> getValueNotSetAttr() {
    return this.valueNotSetAttr;
  }


  /**
   * @return the isDeltaReview
   */
  public boolean isDeltaReview() {
    return this.deltaReview;
  }


  /**
   * @param isDeltaReview the isDeltaReview to set
   */
  public void setDeltaReview(final boolean isDeltaReview) {
    this.deltaReview = isDeltaReview;
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
   * @return the reviewRuleSetData
   */
  public ReviewRuleSetData getPrimaryReviewRuleSetData() {
    return this.primaryReviewRuleSetData;
  }


  /**
   * @param a2lCharMap the a2lCharMap to set
   */
  public void setA2lCharMap(final Map<String, Characteristic> a2lCharMap) {
    this.a2lCharMap = a2lCharMap;
  }


  /**
   * @return the a2lCharMap
   */
  public Map<String, Characteristic> getA2lCharMap() {
    return this.a2lCharMap;
  }


  /**
   * @param multiFilelabels the multiFilelabels to set
   */
  public void setMultiFilelabels(final List<String> multiFilelabels) {
    this.multiFilelabels = multiFilelabels;
  }


  /**
   * @return the multiFilelabels
   */
  public List<String> getMultiFilelabels() {
    return this.multiFilelabels;
  }


  /**
   * @return the paramsRepeated
   */
  public List<ParamRepeatExcelData> getParamsRepeated() {
    return this.paramsRepeated;
  }


  /**
   * @param labelList the labelList to set
   */
  public void setLabelList(final List<String> labelList) {
    this.labelList = labelList;
  }


  /**
   * @return the labelList
   */
  public List<String> getLabelList() {
    return this.labelList;
  }


  /**
   * @return the canceledCDRResult
   */
  public CDRResult getCanceledCDRResult() {
    return this.canceledCDRResult;
  }


  /**
   * @param canceledCDRResult the canceledCDRResult to set
   */
  public void setCanceledCDRResult(final CDRResult canceledCDRResult) {
    this.canceledCDRResult = canceledCDRResult;
  }


  /**
   * @return the secResultMap
   */
  public Map<String, CDRConstants.RESULT_FLAG> getSecResultMap() {
    return this.secResultMap;
  }


  /**
   * @return the cdrResult
   */
  public CDRResult getParentCdrResult() {
    return this.cdrParentResult;
  }


  /**
   * @param cdrResult the cdrResult to set
   */
  public void setParentCdrResult(final CDRResult cdrResult) {
    this.cdrParentResult = cdrResult;
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
   * @return the selA2LGroup
   */
  public A2LGroup getSelA2LGroup() {
    return this.selA2LGroup;
  }


  /**
   * @param selA2LGroup the selA2LGroup to set
   */
  public void setSelA2LGroup(final A2LGroup selA2LGroup) {
    this.selA2LGroup = selA2LGroup;
  }


  /**
   * @param funLabFilePath the funLabFilePath to set
   */
  public void setFunLabFilePath(final String funLabFilePath) {
    this.funLabFilePath = funLabFilePath;
  }


  /**
   * @return the funLabFilePath
   */
  public String getFunLabFilePath() {
    return this.funLabFilePath;
  }


  /**
   * @return the selA2LFile
   */
  public A2LFile getSelA2LFile() {
    return this.selA2LFile;
  }


  /**
   * @return the selPIDCard version
   */
  public PIDCVersion getSelPidcVersion() {
    return this.selPidcVer;
  }


  /**
   * @return the selPIDCVariant
   */
  public PIDCVariant getSelPIDCVariant() {
    return this.selPIDCVariant;
  }


  /**
   * @return the selCalEngineer
   */
  public ApicUser getSelCalEngineer() {
    return this.selCalEngineer;
  }


  /**
   * @return the selAuditor
   */
  public ApicUser getSelAuditor() {
    return this.selAuditor;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }


  /**
   * @return the selParticipants
   */
  public SortedSet<ApicUser> getSelParticipants() {
    return this.selParticipants;
  }


  /**
   * @return the selWorkPackage
   */
  public WorkPackage getSelWorkPackage() {
    return this.selWorkPackage;
  }

  /**
   * @param selReviewFuncs the selReviewFuncs to set
   */
  public void setSelReviewFuncs(final SortedSet<String> selReviewFuncs) {
    this.selReviewFuncs = selReviewFuncs;
  }

  /**
   * @return the selReviewFuncs
   */
  public SortedSet<String> getSelReviewFuncs() {
    return this.selReviewFuncs;
  }


  /**
   * @return the selFilesPath
   */
  public Set<String> getSelFilesPath() {
    return this.selFilesPath;
  }


  /**
   * @return the a2lFileContents
   */
  public A2LFileInfo getA2lFileContents() {
    return this.a2lFileContents;
  }


  /**
   * @return the a2lEditorDP
   */
  public A2LEditorDataProvider getA2lEditorDP() {
    return this.a2lEditorDP;
  }


  /**
   * @return the a2lGroup
   */
  public A2LGroup getA2lGroup() {
    return this.a2lGroup;
  }


  /**
   * @return the calDataMap
   */
  public Map<String, CalData> getCalDataMap() {
    return this.calDataMap;
  }


  /**
   * @return the cdrFunctionsList
   */
  public SortedSet<CDRFunction> getCdrFunctionsList() {
    return this.cdrFunctionsList;
  }


  /**
   * @return the cdrFuncParams
   */
  public Set<?> getCdrFuncParams() {
    return this.cdrFuncParams;
  }


  /**
   * @return the reviewType
   */
  public CDRConstants.REVIEW_TYPE getReviewType() {
    return this.reviewType;
  }


  /**
   * @return the ssdFilePath
   */
  public String getPrimarySsdFilePath() {
    return this.primarySSDFilePath;
  }


  /**
   * @return the reviewFuncParamMap
   */
  public Map<Long, Set<CDRFuncParameter>> getReviewFuncParamMap() {
    return this.reviewFuncParamMap;
  }


  /**
   * @return the checkSSDResultParamMap
   */
  public Map<String, CheckSSDResultParam> getPrimaryCheckSSDResultParamMap() {
    return this.pimaryChkSSDResParamMap;
  }


  /**
   * @return the generatedCheckSSDFiles
   */
  public Set<String> getGeneratedCheckSSDFiles() {
    return this.generatedCheckSSDFiles;
  }


  /**
   * @return the source type
   */
  public CDRConstants.CDR_SOURCE_TYPE getSourceType() {
    return this.sourceType;
  }


  /**
   * @return the ssdRules
   */
  public Map<String, List<CDRRule>> getPrimarySsdRules() {
    return this.primarySSDRules;
  }


  /**
   * @return the attrValModel
   */
  public Set<AttributeValueModel> getAttrValModel() {
    return this.attrValModel;
  }


  /**
   * @return the a2lFileMap
   */
  public Map<String, A2LFile> getA2lFileMap() {
    return this.a2lFileMap;
  }


  /**
   * @param selA2LFile the selA2LFile to set
   */
  public void setSelA2LFile(final A2LFile selA2LFile) {
    this.selA2LFile = selA2LFile;
  }


  /**
   * @param selPidcVer the selPIDCard version to set
   */
  public void setSelPIDCard(final PIDCVersion selPidcVer) {
    this.selPidcVer = selPidcVer;
  }


  /**
   * @param selPIDCVariant the selPIDCVariant to set
   */
  public void setSelPIDCVariant(final PIDCVariant selPIDCVariant) {
    this.selPIDCVariant = selPIDCVariant;
  }


  /**
   * @param selCalEngineer the selCalEngineer to set
   */
  public void setSelCalEngineer(final ApicUser selCalEngineer) {
    this.selCalEngineer = selCalEngineer;
  }


  /**
   * @param selAuditor the selAuditor to set
   */
  public void setSelAuditor(final ApicUser selAuditor) {
    this.selAuditor = selAuditor;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @param selParticipants the selParticipants to set
   */
  public void setSelParticipants(final SortedSet<ApicUser> selParticipants) {
    this.selParticipants = selParticipants;
  }


  /**
   * @param selWorkPackage the selWorkPackage to set
   */
  public void setSelWorkPackage(final WorkPackage selWorkPackage) {
    this.selWorkPackage = selWorkPackage;
  }


  /**
   * @param selFilesPath the selFilesPath to set
   */
  public void setSelFilesPath(final Set<String> selFilesPath) {
    this.selFilesPath = selFilesPath;
  }


  /**
   * @param a2lFileContents the a2lFileContents to set
   */
  public void setA2lFileContents(final A2LFileInfo a2lFileContents) {
    this.a2lFileContents = a2lFileContents;
  }


  /**
   * @param a2lEditorDP the a2lEditorDP to set
   */
  public void setA2lEditorDP(final A2LEditorDataProvider a2lEditorDP) {
    this.a2lEditorDP = a2lEditorDP;
  }


  /**
   * @param a2lGroup the a2lGroup to set
   */
  public void setA2lGroup(final A2LGroup a2lGroup) {
    this.a2lGroup = a2lGroup;
  }


  /**
   * @param calDataMap the calDataMap to set
   */
  public void setCalDataMap(final Map<String, CalData> calDataMap) {
    this.calDataMap = calDataMap;
  }


  /**
   * @param cdrFunctionsList the cdrFunctionsList to set
   */
  public void setCdrFunctionsList(final SortedSet<CDRFunction> cdrFunctionsList) {
    this.cdrFunctionsList = cdrFunctionsList;
  }


  /**
   * @param cdrFuncParams the cdrFuncParams to set
   */
  public void setCdrFuncParams(final Set<?> cdrFuncParams) {
    this.cdrFuncParams = cdrFuncParams;
  }


  /**
   * @param reviewType the reviewType to set
   */
  public void setReviewType(final CDRConstants.REVIEW_TYPE reviewType) {
    this.reviewType = reviewType;
  }


  /**
   * @param ssdFilePath the ssdFilePath to set
   */
  public void setPrimarySsdFilePath(final String ssdFilePath) {
    this.primarySSDFilePath = ssdFilePath;
  }


  /**
   * @param reviewFuncParamMap the reviewFuncParamMap to set
   */
  public void setReviewFuncParamMap(final Map<Long, Set<CDRFuncParameter>> reviewFuncParamMap) {
    this.reviewFuncParamMap = reviewFuncParamMap;
  }


  /**
   * @param checkSSDResultParamMap the checkSSDResultParamMap to set
   */
  public void setPrimaryCheckSSDResultParamMap(final Map<String, CheckSSDResultParam> checkSSDResultParamMap) {
    this.pimaryChkSSDResParamMap = checkSSDResultParamMap;
  }


  /**
   * @param generatedCheckSSDFiles the generatedCheckSSDFiles to set
   */
  public void setGeneratedCheckSSDFiles(final Set<String> generatedCheckSSDFiles) {
    this.generatedCheckSSDFiles = generatedCheckSSDFiles;
  }


  /**
   * @param sourceType the source Type to set
   */
  public void setSourceType(final CDRConstants.CDR_SOURCE_TYPE sourceType) {
    this.sourceType = sourceType;
  }


  /**
   * @param ssdRules the ssdRules to set
   */
  public void setPrimarySsdRules(final Map<String, List<CDRRule>> ssdRules) {
    this.primarySSDRules = ssdRules;
  }


  /**
   * @param attrValModel the attrValModel to set
   */
  public void setAttrValModel(final Set<AttributeValueModel> attrValModel) {
    this.attrValModel = attrValModel;
  }


  /**
   * @param paramsRepeated ParamRepeatExcelData
   */
  public void setParamsRepeated(final List<ParamRepeatExcelData> paramsRepeated) {
    this.paramsRepeated = paramsRepeated;

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
   * @param valueNotSetAttr valueNotSetAttr
   */
  public void setValueNotSetAttr(final SortedSet<AttributeValueModel> valueNotSetAttr) {
    this.valueNotSetAttr = valueNotSetAttr;

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
   * @param wpChanged wpChanged
   */
  public void setWpChanged(final boolean wpChanged) {
    this.wpChanged = wpChanged;

  }


  /**
   * @param monicaFilePath monicaFilePath
   */
  public void setMonicaFilePath(final String monicaFilePath) {
    this.monicaFilePath = monicaFilePath;

  }


  /**
   * @return MoniCa file path
   */
  public String getMonicaFilePath() {
    return this.monicaFilePath;
  }


  /**
   * @param monicaOp monicaOp
   */
  public void setMonicaOutput(final MonitoringToolOutput monicaOp) {
    this.monicaOutput = monicaOp;

  }


  /**
   * @return the set of Questionnaire Version. Can be <code>null</code>
   */
  public SortedSet<QuestionnaireVersion> getQuestionnaireVersions() {
    return this.qnaireVersSet;
  }


  /**
   * @param qnaireVersSet the Questionnaire Versions to set
   */
  public void setQuestionnaireVersions(final SortedSet<QuestionnaireVersion> qnaireVersSet) {
    this.qnaireVersSet = qnaireVersSet;
  }


  /**
   * @param offReviewType
   */
  public void setOffParentReviewType(final boolean offReviewType) {
    this.offReviewType = offReviewType;

  }


  /**
   * @param startReviewType
   */
  public void setStartParentReviewType(final boolean startReviewType) {
    this.startReviewType = startReviewType;

  }


  /**
   * @param onlyLockedOffReview
   */
  public void setOnlyLockedOffReview(final boolean onlyLockedOffReview) {
    this.onlyLockedOffReview = onlyLockedOffReview;

  }


  /**
   * @param onlyLockedStartResults
   */
  public void setOnlyLockedStartReview(final boolean onlyLockedStartResults) {
    this.onlyLockedStartResults = onlyLockedStartResults;

  }


  /**
   * @return
   */
  public boolean isOffParentReviewType() {
    return this.offReviewType;
  }


  /**
   * @return
   */
  public boolean isOnlyLockedOffReview() {
    return this.onlyLockedOffReview;
  }


  /**
   * @return
   */
  public boolean isStartParentReviewType() {
    return this.startReviewType;
  }


  /**
   * @return
   */
  public boolean isOnlyLockedStartReview() {
    return this.onlyLockedStartResults;
  }


  /**
   * @return the questionnairesInUse
   */
  public SortedSet<IcdmWorkPackage> getQuestionnairesInUse() {
    return this.questionnairesInUse;
  }


  /**
   * @param questionnairesInUse the questionnairesInUse to set
   */
  public void setQuestionnairesInUse(final SortedSet<IcdmWorkPackage> questionnairesInUse) {
    this.questionnairesInUse = questionnairesInUse;
  }

  /**
   * @return the unassParaminReview
   */
  public List<String> getUnassParaminReview() {
    return this.unassParaminReview;
  }


  /**
   * @return boolean
   */
  public boolean isCommonRulesNeeded() {
    return this.isCommonRulesNeeded;
  }


  /**
   * @param isCommonRulesNeeded boolean
   */
  public void setCommonRulesNeeded(final boolean isCommonRulesNeeded) {
    this.isCommonRulesNeeded = isCommonRulesNeeded;
  }


  /**
   * @param selection boolean
   */
  public void setCommonRulesPrimary(final boolean selection) {
    this.commonRulesPrimary = selection;
  }


  /**
   * @return the ruleSetDataList
   */
  public List<ReviewRuleSetData> getRuleSetDataList() {
    return this.ruleSetDataList;
  }


  /**
   * @return boolean
   */
  public boolean isCommonRulesPrimary() {
    return this.commonRulesPrimary;
  }


  /**
   * @param compliLabels compliLabels
   */
  public void setCompliLabels(final List<String> compliLabels) {
    this.compliLabels = compliLabels;

  }

  /**
   * @return the compliLabels
   */
  public List<String> getCompliLabels() {
    return this.compliLabels;
  }


  /**
   * @param ssdRelease ssdRelease
   */
  public void setSelSSDRelease(final SSDRelease selSSDRelease) {
    this.selSSDRelease = selSSDRelease;

  }


  /**
   * @return the selSSDRelease
   */
  public SSDRelease getSelSSDRelease() {
    return this.selSSDRelease;
  }
}
