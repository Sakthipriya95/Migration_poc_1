/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;

/**
 * @author say8cob
 */
public class CDRWizardUIModel {

  /**
   * Paths of file to be reviewed
   */
  private Set<String> selFilesPath;

  private Map<String, byte[]> selectedInputFiles;

  /**
   * Source type
   */
  private String sourceType;
  /**
   * Funlab file path
   */
  private String funLabFilePath;

  private byte[] funLabFiles;
  /**
   * Label list
   */
  private List<String> labelList;

  /**
   * Selected a2l group name
   */
  private String a2lGroupName;
  /**
   * Selected a2l group name's list
   */
  private List<String> a2lGroupNameList;

  // Icdm-715 UI Changes to Wizard

  private boolean exceptioninWizard;


  private Long ssdSWVersionId;

  private List<String> compliLabelList;

  private SortedSet<String> unassignedParamsInReview;

  // For PIDC Data
  /**
   * Pidc a2l id
   */
  private Long pidcA2lId;

  private Long a2lFileId;

  private Long selectedPidcVerId;

  /**
   * selected source pidc vers id
   */
  private Long sourcePidcVerId;
  /**
   * selected source pidc variant id
   */
  private Long sourcePIDCVariantId;


  // END For PIDC Data

  // For Project Data Selection page

  private java.util.List<String> participantUserNameList = new ArrayList<>();

  private String auditorUserName;

  private String calEngUserName;

  private final java.util.List<String> participantUserFullNameList = new ArrayList<>();

  private String auditorUserFullName;

  private String calEngUserFullName;

  private Long selectedPidcVariantId;

  private String selectedPidcVariantName;

  private String projectName;

  private Long reviewVersion;

  /**
   * Review type
   */
  private String reviewType;

  /**
   * Review description
   */
  private String description;

  private Long selCalEngineerId;

  private Long selAuditorId;

  private Set<Long> selParticipantsIds;


  // For Rules Data

  /**
   * is common rules secondary
   */
  private boolean commonRulesSecondary;
  /**
   * is common rules primary
   */
  private boolean commonRulesPrimary;
  /**
   * ssd release id
   */
  private Long ssdReleaseId;
  /**
   * ssd rules file path
   */
  private String ssdRuleFilePath;
  /**
   * primary ruleset id
   */
  private Long primaryRuleSetId;
  /**
   * Set of secondary rule set ids
   */
  private Set<Long> secondaryRuleSetIds;

  private Map<Long, String> secRuleSetMap;

  private RuleSet primaryRuleSet;

  private Set<RuleSet> secondaryRuleSets;

  private SortedSet<String> selReviewFuncs;

  /**
   * is files to be reivewed selected
   */
  private boolean isFilesToBeReviewed;

  private SortedSet<String> availableFunctions;

  private Long parentCDRResultId;

  // Added For Delta Review Alone
  private String selectedReviewPverName;


  private Long parentResultId;

  List<SSDReleaseIcdmModel> mappedSSDReleases;


  // Added for Project Delta Review

  /**
   * is official review
   */
  private boolean offReviewType;
  /**
   * is start review
   */
  private boolean startReviewType;
  /**
   * is only locked official review
   */
  private boolean onlyLockedOffReview;
  /**
   * is only locked start review
   */
  private boolean onlyLockedStartResults;

  // Added for MoniCa Review

  private Set<String> selMonicaFilesPath;

  private Map<String, byte[]> selectedMonicaFiles;

  // Added for sending mail to hotline
  private Set<AttributeValueModel> attrWithoutMapping = new TreeSet<>();


  private String reviewStatus;

  /**
   * FC2WP id
   */
  private Long wpDivId;

  /**
   * Selected wp resp name
   */
  private String wpRespName;

  private String mulWPRespNames;

  private Set<RvwWpAndRespModel> rvwWpAndRespModelSet = new HashSet<>();

  private Map<String, Set<String>> functionMap = new HashMap<>();

  private String deltaReviewType;

  private Long cancelledResultId;

  private String selMoniCaSheetName;

  /**
   * For HOlding CDR Review result during Update service call
   */
  private CDRReviewResult cdrReviewResult;

  private RvwVariant rvwVariant;

  private Long a2lWpDefVersId;

  private List<WpRespModel> selectedWpRespList = new ArrayList<>();
  /**
   * return true if Division id in PIDC is mapped to common Param DIVISION_WITH_OBD_OPTION
   */
  private boolean isPidcDivAppForOBDOpt;
  /**
   * O - only OBD Gen Qnaire, N - only Simplified Qnaire, B - Both OBD and Simplified Qnaire
   */
  private String obdFlag;

  /**
   * /**
   * 
   * @return the sourceReviewPverName
   */
  public String getSelectedReviewPverName() {
    return this.selectedReviewPverName;
  }


  /**
   * @param sourceReviewPverName the sourceReviewPverName to set
   */
  public void setSelectedReviewPverName(final String sourceReviewPverName) {
    this.selectedReviewPverName = sourceReviewPverName;
  }


  /**
   * @return the isFilesToBeReviewed
   */
  public boolean isFilesToBeReviewed() {
    return this.isFilesToBeReviewed;
  }


  /**
   * @param isFilesToBeReviewed the isFilesToBeReviewed to set
   */
  public void setFilesToBeReviewed(final boolean isFilesToBeReviewed) {
    this.isFilesToBeReviewed = isFilesToBeReviewed;
  }


  /**
   * @return the selReviewFuncs
   */
  public SortedSet<String> getSelReviewFuncs() {
    return this.selReviewFuncs;
  }


  /**
   * @param selReviewFuncs the selReviewFuncs to set
   */
  public void setSelReviewFuncs(final SortedSet<String> selReviewFuncs) {
    this.selReviewFuncs = selReviewFuncs;
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
   * @return the reviewType
   */
  public String getReviewType() {
    return this.reviewType;
  }


  /**
   * @param reviewType the reviewType to set
   */
  public void setReviewType(final String reviewType) {
    this.reviewType = reviewType;
  }


  /**
   * @return the reviewOutput
   */
  public ReviewOutput getReviewOutput() {
    return this.reviewOutput;
  }


  /**
   * @param reviewOutput the reviewOutput to set
   */
  public void setReviewOutput(final ReviewOutput reviewOutput) {
    this.reviewOutput = reviewOutput;
  }

  private ReviewOutput reviewOutput;


  /**
   * @return the ssdReleaseId
   */
  public Long getSsdReleaseId() {
    return this.ssdReleaseId;
  }


  /**
   * @param ssdReleaseId the ssdReleaseId to set
   */
  public void setSsdReleaseId(final Long ssdReleaseId) {
    this.ssdReleaseId = ssdReleaseId;
  }


  /**
   * @return the ssdRuleFilePath
   */
  public String getSsdRuleFilePath() {
    return this.ssdRuleFilePath;
  }


  /**
   * @param ssdRuleFilePath the ssdRuleFilePath to set
   */
  public void setSsdRuleFilePath(final String ssdRuleFilePath) {
    this.ssdRuleFilePath = ssdRuleFilePath;
  }


  /**
   * @return the ssdSWVersionId
   */
  public Long getSsdSWVersionId() {
    return this.ssdSWVersionId;
  }


  /**
   * @param ssdSWVersionId the ssdSWVersionId to set
   */
  public void setSsdSWVersionId(final Long ssdSWVersionId) {
    this.ssdSWVersionId = ssdSWVersionId;
  }


  /**
   * @return the participantUserNameList
   */
  public java.util.List<String> getParticipantUserNameList() {
    return this.participantUserNameList;
  }


  /**
   * @param participantUserNameList the participantUserNameList to set
   */
  public void setParticipantUserNameList(final java.util.List<String> participantUserNameList) {
    this.participantUserNameList = participantUserNameList;
  }


  /**
   * @return the auditorUserName
   */
  public String getAuditorUserName() {
    return this.auditorUserName;
  }


  /**
   * @param auditorUserName the auditorUserName to set
   */
  public void setAuditorUserName(final String auditorUserName) {
    this.auditorUserName = auditorUserName;
  }


  /**
   * @return the calEngUserName
   */
  public String getCalEngUserName() {
    return this.calEngUserName;
  }


  /**
   * @param calEngUserName the calEngUserName to set
   */
  public void setCalEngUserName(final String calEngUserName) {
    this.calEngUserName = calEngUserName;
  }


  /**
   * @return the selectedPidcVariantId
   */
  public Long getSelectedPidcVariantId() {
    return this.selectedPidcVariantId;
  }


  /**
   * @param selectedPidcVariantId the selectedPidcVariantId to set
   */
  public void setSelectedPidcVariantId(final Long selectedPidcVariantId) {
    this.selectedPidcVariantId = selectedPidcVariantId;
  }


  /**
   * @return the exceptioninWizard
   */
  public boolean isExceptioninWizard() {
    return this.exceptioninWizard;
  }


  /**
   * @param exceptioninWizard the exceptioninWizard to set
   */
  public void setExceptioninWizard(final boolean exceptioninWizard) {
    this.exceptioninWizard = exceptioninWizard;
  }


  /**
   * @return the a2lGroupName
   */
  public String getA2lGroupName() {
    return this.a2lGroupName;
  }


  /**
   * @param a2lGroupName the a2lGroupName to set
   */
  public void setA2lGroupName(final String a2lGroupName) {
    this.a2lGroupName = a2lGroupName;
  }


  /**
   * @return the a2lGroupNameList
   */
  public List<String> getA2lGroupNameList() {
    return this.a2lGroupNameList;
  }


  /**
   * @param a2lGroupNameList the a2lGroupNameList to set
   */
  public void setA2lGroupNameList(final List<String> a2lGroupNameList) {
    this.a2lGroupNameList = a2lGroupNameList;
  }


  /**
   * @return the labelList
   */
  public List<String> getLabelList() {
    return this.labelList;
  }


  /**
   * @param labelList the labelList to set
   */
  public void setLabelList(final List<String> labelList) {
    this.labelList = labelList;
  }


  /**
   * @return the selFilesPath
   */
  public Set<String> getSelFilesPath() {
    return this.selFilesPath;
  }


  /**
   * @param selFilesPath the selFilesPath to set
   */
  public void setSelFilesPath(final Set<String> selFilesPath) {
    this.selFilesPath = selFilesPath;
  }


  /**
   * @return the sourceType
   */
  public String getSourceType() {
    return this.sourceType;
  }


  /**
   * @param sourceType the sourceType to set
   */
  public void setSourceType(final String sourceType) {
    this.sourceType = sourceType;
  }


  /**
   * @return the funLabFilePath
   */
  public String getFunLabFilePath() {
    return this.funLabFilePath;
  }


  /**
   * @param funLabFilePath the funLabFilePath to set
   */
  public void setFunLabFilePath(final String funLabFilePath) {
    this.funLabFilePath = funLabFilePath;
  }

  /**
   * @return the primaryRuleSetId
   */
  public Long getPrimaryRuleSetId() {
    return this.primaryRuleSetId;
  }

  /**
   * @param primaryRuleSetId the primaryRuleSetId to set
   */
  public void setPrimaryRuleSetId(final Long primaryRuleSetId) {
    this.primaryRuleSetId = primaryRuleSetId;
  }

  /**
   * @return the secondaryRuleSetIds
   */
  public Set<Long> getSecondaryRuleSetIds() {
    return this.secondaryRuleSetIds;
  }

  /**
   * @param secondaryRuleSetIds the secondaryRuleSetIds to set
   */
  public void setSecondaryRuleSetIds(final Set<Long> secondaryRuleSetIds) {
    this.secondaryRuleSetIds = secondaryRuleSetIds;
  }

  /**
   * @return the commonRulesSecondary
   */
  public boolean isCommonRulesSecondary() {
    return this.commonRulesSecondary;
  }

  /**
   * @param commonRulesSecondary the commonRulesSecondary to set
   */
  public void setCommonRulesSecondary(final boolean commonRulesSecondary) {
    this.commonRulesSecondary = commonRulesSecondary;
  }

  /**
   * @return the commonRulesPrimary
   */
  public boolean isCommonRulesPrimary() {
    return this.commonRulesPrimary;
  }

  /**
   * @param commonRulesPrimary the commonRulesPrimary to set
   */
  public void setCommonRulesPrimary(final boolean commonRulesPrimary) {
    this.commonRulesPrimary = commonRulesPrimary;
  }


  /**
   * @return the compliLabelList
   */
  public List<String> getCompliLabelList() {
    return this.compliLabelList;
  }


  /**
   * @param compliLabelList the compliLabelList to set
   */
  public void setCompliLabelList(final List<String> compliLabelList) {
    this.compliLabelList = compliLabelList;
  }


  /**
   * @return the unassignedParamsInReview
   */
  public SortedSet<String> getUnassignedParamsInReview() {
    return this.unassignedParamsInReview;
  }


  /**
   * @param unassignedParamsInReview the unassignedParamsInReview to set
   */
  public void setUnassignedParamsInReview(final SortedSet<String> unassignedParamsInReview) {
    this.unassignedParamsInReview = unassignedParamsInReview;
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
   * @return the sourcePidcVerId
   */
  public Long getSelectedPidcVerId() {
    return this.selectedPidcVerId;
  }


  /**
   * @param sourcePidcVerId the sourcePidcVerId to set
   */
  public void setSelectedPidcVerId(final Long selectedPidcVerId) {
    this.selectedPidcVerId = selectedPidcVerId;
  }


  /**
   * @return the sourcePIDCVariantId
   */
  public Long getSourcePIDCVariantId() {
    return this.sourcePIDCVariantId;
  }


  /**
   * @param sourcePIDCVariantId the sourcePIDCVariantId to set
   */
  public void setSourcePIDCVariantId(final Long sourcePIDCVariantId) {
    this.sourcePIDCVariantId = sourcePIDCVariantId;
  }


  /**
   * @return the selCalEngineerId
   */
  public Long getSelCalEngineerId() {
    return this.selCalEngineerId;
  }


  /**
   * @param selCalEngineerId the selCalEngineerId to set
   */
  public void setSelCalEngineerId(final Long selCalEngineerId) {
    this.selCalEngineerId = selCalEngineerId;
  }


  /**
   * @return the selAuditorId
   */
  public Long getSelAuditorId() {
    return this.selAuditorId;
  }


  /**
   * @param selAuditorId the selAuditorId to set
   */
  public void setSelAuditorId(final Long selAuditorId) {
    this.selAuditorId = selAuditorId;
  }


  /**
   * @return the selParticipantsIds
   */
  public Set<Long> getSelParticipantsIds() {
    return this.selParticipantsIds;
  }


  /**
   * @param selParticipantsIds the selParticipantsIds to set
   */
  public void setSelParticipantsIds(final Set<Long> selParticipantsIds) {
    this.selParticipantsIds = selParticipantsIds;
  }


  /**
   * @return the selectedPidcVariantName
   */
  public String getSelectedPidcVariantName() {
    return this.selectedPidcVariantName;
  }


  /**
   * @param selectedPidcVariantName the selectedPidcVariantName to set
   */
  public void setSelectedPidcVariantName(final String selectedPidcVariantName) {
    this.selectedPidcVariantName = selectedPidcVariantName;
  }


  /**
   * @return the projectName
   */
  public String getProjectName() {
    return this.projectName;
  }


  /**
   * @param projectName the projectName to set
   */
  public void setProjectName(final String projectName) {
    this.projectName = projectName;
  }

  /**
   * @return the availableFunctions
   */
  public SortedSet<String> getAvailableFunctions() {
    return this.availableFunctions;
  }


  /**
   * @param availableFunctions the availableFunctions to set
   */
  public void setAvailableFunctions(final SortedSet<String> availableFunctions) {
    this.availableFunctions = availableFunctions;
  }

  /**
   * @return the selectedInputFiles
   */
  public Map<String, byte[]> getSelectedInputFiles() {
    return this.selectedInputFiles;
  }


  /**
   * @param selectedInputFiles the selectedInputFiles to set
   */
  public void setSelectedInputFiles(final Map<String, byte[]> selectedInputFiles) {
    this.selectedInputFiles = selectedInputFiles;
  }


  /**
   * @return the funLabFiles
   */
  public byte[] getFunLabFiles() {
    return this.funLabFiles;
  }


  /**
   * @param funLabFiles the funLabFiles to set
   */
  public void setFunLabFiles(final byte[] funLabFiles) {
    this.funLabFiles = funLabFiles;
  }


  /**
   * @return the primaryRuleSet
   */
  public RuleSet getPrimaryRuleSet() {
    return this.primaryRuleSet;
  }


  /**
   * @param primaryRuleSet the primaryRuleSet to set
   */
  public void setPrimaryRuleSet(final RuleSet primaryRuleSet) {
    this.primaryRuleSet = primaryRuleSet;
  }


  /**
   * @return the secondaryRuleSets
   */
  public Set<RuleSet> getSecondaryRuleSets() {
    return this.secondaryRuleSets;
  }


  /**
   * @param secondaryRuleSets the secondaryRuleSets to set
   */
  public void setSecondaryRuleSets(final Set<RuleSet> secondaryRuleSets) {
    this.secondaryRuleSets = secondaryRuleSets;
  }


  /**
   * @return the auditorUserFullName
   */
  public String getAuditorUserFullName() {
    return this.auditorUserFullName;
  }


  /**
   * @param auditorUserFullName the auditorUserFullName to set
   */
  public void setAuditorUserFullName(final String auditorUserFullName) {
    this.auditorUserFullName = auditorUserFullName;
  }


  /**
   * @return the calEngUserFullName
   */
  public String getCalEngUserFullName() {
    return this.calEngUserFullName;
  }


  /**
   * @param calEngUserFullName the calEngUserFullName to set
   */
  public void setCalEngUserFullName(final String calEngUserFullName) {
    this.calEngUserFullName = calEngUserFullName;
  }


  /**
   * @return the participantUserFullNameList
   */
  public java.util.List<String> getParticipantUserFullNameList() {
    return this.participantUserFullNameList;
  }


  /**
   * @return the parentCDRResultId
   */
  public Long getParentCDRResultId() {
    return this.parentCDRResultId;
  }


  /**
   * @param parentCDRResultId the parentCDRResultId to set
   */
  public void setParentCDRResultId(final Long parentCDRResultId) {
    this.parentCDRResultId = parentCDRResultId;
  }

  /**
   * @return the sourcePidcVerId
   */
  public Long getSourcePidcVerId() {
    return this.sourcePidcVerId;
  }


  /**
   * @param sourcePidcVerId the sourcePidcVerId to set
   */
  public void setSourcePidcVerId(final Long sourcePidcVerId) {
    this.sourcePidcVerId = sourcePidcVerId;
  }


  /**
   * @return the parentResultId
   */
  public Long getParentResultId() {
    return this.parentResultId;
  }


  /**
   * @param parentResultId the parentResultId to set
   */
  public void setParentResultId(final Long parentResultId) {
    this.parentResultId = parentResultId;
  }


  /**
   * @return the offReviewType
   */
  public boolean isOffReviewType() {
    return this.offReviewType;
  }


  /**
   * @param offReviewType the offReviewType to set
   */
  public void setOffReviewType(final boolean offReviewType) {
    this.offReviewType = offReviewType;
  }


  /**
   * @return the startReviewType
   */
  public boolean isStartReviewType() {
    return this.startReviewType;
  }


  /**
   * @param startReviewType the startReviewType to set
   */
  public void setStartReviewType(final boolean startReviewType) {
    this.startReviewType = startReviewType;
  }


  /**
   * @return the onlyLockedOffReview
   */
  public boolean isOnlyLockedOffReview() {
    return this.onlyLockedOffReview;
  }


  /**
   * @param onlyLockedOffReview the onlyLockedOffReview to set
   */
  public void setOnlyLockedOffReview(final boolean onlyLockedOffReview) {
    this.onlyLockedOffReview = onlyLockedOffReview;
  }


  /**
   * @return the onlyLockedStartResults
   */
  public boolean isOnlyLockedStartResults() {
    return this.onlyLockedStartResults;
  }


  /**
   * @param onlyLockedStartResults the onlyLockedStartResults to set
   */
  public void setOnlyLockedStartResults(final boolean onlyLockedStartResults) {
    this.onlyLockedStartResults = onlyLockedStartResults;
  }


  /**
   * @return the mappedSSDReleases
   */
  public List<SSDReleaseIcdmModel> getMappedSSDReleases() {
    return this.mappedSSDReleases;
  }


  /**
   * @param mappedSSDReleases the mappedSSDReleases to set
   */
  public void setMappedSSDReleases(final List<SSDReleaseIcdmModel> mappedSSDReleases) {
    this.mappedSSDReleases = mappedSSDReleases;
  }


  /**
   * @return the selMonicaFilesPath
   */
  public Set<String> getSelMonicaFilesPath() {
    return this.selMonicaFilesPath;
  }


  /**
   * @param selMonicaFilesPath the selMonicaFilesPath to set
   */
  public void setSelMonicaFilesPath(final Set<String> selMonicaFilesPath) {
    this.selMonicaFilesPath = selMonicaFilesPath;
  }


  /**
   * @return the selectedMonicaFiles
   */
  public Map<String, byte[]> getSelectedMonicaFiles() {
    return this.selectedMonicaFiles;
  }


  /**
   * @param selectedMonicaFiles the selectedMonicaFiles to set
   */
  public void setSelectedMonicaFiles(final Map<String, byte[]> selectedMonicaFiles) {
    this.selectedMonicaFiles = selectedMonicaFiles;
  }


  /**
   * @return the attrWithoutMapping
   */
  public Set<AttributeValueModel> getAttrWithoutMapping() {
    return this.attrWithoutMapping;
  }


  /**
   * @param attrWithoutMapping the attrWithoutMapping to set
   */
  public void setAttrWithoutMapping(final Set<AttributeValueModel> attrWithoutMapping) {
    this.attrWithoutMapping = attrWithoutMapping;
  }


  /**
   * @return the wpDivId
   */
  public Long getWpDivId() {
    return this.wpDivId;
  }


  /**
   * @param wpDivId the wpDivId to set
   */
  public void setWpDivId(final Long wpDivId) {
    this.wpDivId = wpDivId;
  }


  /**
   * @return the reviewStatus
   */
  public String getReviewStatus() {
    return this.reviewStatus;
  }


  /**
   * @param reviewStatus the reviewStatus to set
   */
  public void setReviewStatus(final String reviewStatus) {
    this.reviewStatus = reviewStatus;
  }


  /**
   * @return the secRuleSetMap
   */
  public Map<Long, String> getSecRuleSetMap() {
    return this.secRuleSetMap;
  }


  /**
   * @param secRuleSetMap the secRuleSetMap to set
   */
  public void setSecRuleSetMap(final Map<Long, String> secRuleSetMap) {
    this.secRuleSetMap = secRuleSetMap;
  }


  /**
   * @return the deltaReviewType
   */
  public String getDeltaReviewType() {
    return this.deltaReviewType;
  }


  /**
   * @param deltaReviewType the deltaReviewType to set
   */
  public void setDeltaReviewType(final String deltaReviewType) {
    this.deltaReviewType = deltaReviewType;
  }


  /**
   * @return the cancelledResultId
   */
  public Long getCancelledResultId() {
    return this.cancelledResultId;
  }


  /**
   * @param cancelledResultId the cancelledResultId to set
   */
  public void setCancelledResultId(final Long cancelledResultId) {
    this.cancelledResultId = cancelledResultId;
  }


  /**
   * @return the resultVersion
   */
  public Long getReviewVersion() {
    return this.reviewVersion;
  }


  /**
   * @param resultVersion the resultVersion to set
   */
  public void setReviewVersion(final Long resultVersion) {
    this.reviewVersion = resultVersion;
  }


  /**
   * @return the selMoniCaSheetName
   */
  public String getSelMoniCaSheetName() {
    return this.selMoniCaSheetName;
  }


  /**
   * @param selMoniCaSheetName the selMoniCaSheetName to set
   */
  public void setSelMoniCaSheetName(final String selMoniCaSheetName) {
    this.selMoniCaSheetName = selMoniCaSheetName;
  }


  /**
   * @return the cdrReviewResult
   */
  public CDRReviewResult getCdrReviewResult() {
    return this.cdrReviewResult;
  }


  /**
   * @param cdrReviewResult the cdrReviewResult to set
   */
  public void setCdrReviewResult(final CDRReviewResult cdrReviewResult) {
    this.cdrReviewResult = cdrReviewResult;
  }


  /**
   * @return the rvwVariant
   */
  public RvwVariant getRvwVariant() {
    return this.rvwVariant;
  }


  /**
   * @param rvwVariant the rvwVariant to set
   */
  public void setRvwVariant(final RvwVariant rvwVariant) {
    this.rvwVariant = rvwVariant;
  }

  /**
   * @return the functionMap
   */
  public Map<String, Set<String>> getFunctionMap() {
    return this.functionMap;
  }


  /**
   * @param functionMap the functionMap to set
   */
  public void setFunctionMap(final Map<String, Set<String>> functionMap) {
    this.functionMap = functionMap;
  }


  /**
   * @return the wpRespName
   */
  public String getWpRespName() {
    return this.wpRespName;
  }


  /**
   * @param wpRespName the wpRespName to set
   */
  public void setWpRespName(final String wpRespName) {
    this.wpRespName = wpRespName;
  }


  /**
   * @return the mulWPRespNames
   */
  public String getMulWPRespNames() {
    return this.mulWPRespNames;
  }


  /**
   * @param mulWPRespNames the mulWPRespNames to set
   */
  public void setMulWPRespNames(final String mulWPRespNames) {
    this.mulWPRespNames = mulWPRespNames;
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
   * @return the a2lWpDefVersId
   */
  public Long getA2lWpDefVersId() {
    return this.a2lWpDefVersId;
  }


  /**
   * @param a2lWpDefVersId the a2lWpDefVersId to set
   */
  public void setA2lWpDefVersId(final Long a2lWpDefVersId) {
    this.a2lWpDefVersId = a2lWpDefVersId;
  }


  /**
   * @return the selectedWpResp
   */
  public List<WpRespModel> getSelectedWpRespList() {
    return this.selectedWpRespList;
  }


  /**
   * @param selectedWpResp the selectedWpResp to set
   */
  public void setSelectedWpRespList(final List<WpRespModel> selectedWpRespList) {
    this.selectedWpRespList = selectedWpRespList;
  }


  /**
   * @return the isPidcDivAppForOBDOpt
   */
  public boolean isPidcDivAppForOBDOpt() {
    return this.isPidcDivAppForOBDOpt;
  }


  /**
   * @param isPidcDivAppForOBDOpt the isPidcDivAppForOBDOpt to set
   */
  public void setPidcDivAppForOBDOpt(final boolean isPidcDivAppForOBDOpt) {
    this.isPidcDivAppForOBDOpt = isPidcDivAppForOBDOpt;
  }


  /**
   * @return the obdFlag
   */
  public String getObdFlag() {
    return this.obdFlag;
  }


  /**
   * @param obdFlag the obdFlag to set
   */
  public void setObdFlag(final String obdFlag) {
    this.obdFlag = obdFlag;
  }


}
