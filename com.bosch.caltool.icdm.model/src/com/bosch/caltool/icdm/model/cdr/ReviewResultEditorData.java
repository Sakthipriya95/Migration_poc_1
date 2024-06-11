/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;

/**
 * @author BRU2COB
 */
public class ReviewResultEditorData {

  /**
   * CDR result obj
   */
  private CDRReviewResult reviewResult;

  /**
   * Pidc version obj
   */
  private PidcVersion pidcVers;
  /**
   * Pidc a2l
   */
  private PidcA2l pidcA2l;
  /**
   * Pidc
   */
  private Pidc pidc;
  /**
   * Ruleset
   */
  private RuleSet ruleSet;
  /**
   * First pidc variant
   */
  private PidcVariant firstVariant;
  /**
   * Created user
   */
  private User createdUser;
  /**
   * Wp division
   */
  private WorkPackageDivision wpDivision;
  /**
   * Key - param id Values - corresponding param obj
   */
  private Map<Long, CDRResultParameter> paramMap = new ConcurrentHashMap<>();
  /**
   * Key - param id Values - corresponding param obj for delta review
   */
  private Map<Long, CDRResultParameter> parentParamMap = new ConcurrentHashMap<>();
  /**
   * Key - param id Values - corresponding param obj for proj delta review
   */
  private Map<Long, CDRResultParameter> projDeltaParentParamMap = new ConcurrentHashMap<>();
  /**
   * Key - func id Values - corresponding func obj
   */
  private Map<Long, CDRResultFunction> funcMap = new ConcurrentHashMap<>();
  /**
   * Key - func id Values - params belonging to the func
   */
  private Map<Long, List<CDRResultParameter>> funcParamMap = new ConcurrentHashMap<>();

  private Map<String, Parameter> paramMappingMap = new ConcurrentHashMap<>();

  private Map<String, Function> funcMappingMap = new ConcurrentHashMap<>();

  /**
   * Key - RespName Values - Set of A2lWpResponsibility
   */
  private Map<String, Set<RvwResultWPandRespModel>> a2lWpMap = new HashMap<>();
  /**
   * Set of A2lWpResponsibility obj
   */
  private Set<RvwResultWPandRespModel> a2lWpSet = new HashSet<>();

  // Set Contain both A2lWp and A2lResp 
  private Set<RvwResultWPandRespModel> a2lWpRespSet = new HashSet<>();

  private Map<Long, Map<Long, String>> paramIdAndWpAndRespMap = new HashMap<>();

  private Map<Long, A2lResponsibility> a2lResponsibilityMap = new HashMap<>();

  /**
   * Parent review result
   */
  private CDRReviewResult parentReviewResult;
  /**
   * Parent result name of project delta review
   */
  private String projDeltaParentPidcVersName;
  /**
   * Parent variant name of project delta review
   */
  private String projDeltaParentVariantName;

  /**
   * Key - file type , value - list of files belonging to that type
   */
  private Map<String, List<RvwFile>> icdmFiles = new ConcurrentHashMap<>();

  private Map<Long, RvwAttrValue> attrValMap = new ConcurrentHashMap<>();

  private Map<Long, RvwParticipant> participantsMap = new ConcurrentHashMap<>();

  private Map<Long, RvwVariant> variantsMap = new ConcurrentHashMap<>();

  private Map<Long, RvwResultsSecondary> secondayResultsMap = new ConcurrentHashMap<>();

  private Map<Long, List<RvwFile>> paramAdditionalFiles = new ConcurrentHashMap<>();

  private Map<Long, Map<Long, RvwParametersSecondary>> rvwParamSecondaryMap = new ConcurrentHashMap<>();

  private String pverVarName;

  private String a2lFileName;

  private Set<Long> readOnlyParamSet;

  private Map<Long, List<String>> depParamMap;

  private ReviewInfoWpDefDetails rvwInfoWpDefDetails;

  private Set<QnaireRespStatusData> qnaireDataForRvwSet;

  /**
   * Key -> A2lRESPID Value -> (Key -> A2WPID Value -> WPRESPStatus (FINISHED/NOTFINISHED))
   */
  private Map<Long, Map<Long, String>> respWpFinishedStatusMap = new HashMap<>();

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
   * @return the pverVarName
   */
  public String getPverVarName() {
    return this.pverVarName;
  }


  /**
   * @param pverVarName the pverVarName to set
   */
  public void setPverVarName(final String pverVarName) {
    this.pverVarName = pverVarName;
  }


  /**
   * @return the createdUser
   */
  public User getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final User createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the paramMap
   */
  public Map<Long, CDRResultParameter> getParamMap() {
    return this.paramMap;
  }


  /**
   * @param paramMap the paramMap to set
   */
  public void setParamMap(final Map<Long, CDRResultParameter> paramMap) {
    this.paramMap = paramMap;
  }


  /**
   * @return the funcMap
   */
  public Map<Long, CDRResultFunction> getFuncMap() {
    return this.funcMap;
  }


  /**
   * @param funcMap the funcMap to set
   */
  public void setFuncMap(final Map<Long, CDRResultFunction> funcMap) {
    this.funcMap = funcMap;
  }


  /**
   * @return the funcParamMap
   */
  public Map<Long, List<CDRResultParameter>> getFuncParamMap() {
    return this.funcParamMap;
  }


  /**
   * @param funcParamMap the funcParamMap to set
   */
  public void setFuncParamMap(final Map<Long, List<CDRResultParameter>> funcParamMap) {
    this.funcParamMap = funcParamMap;
  }


  /**
   * @return the reviewResult
   */
  public CDRReviewResult getReviewResult() {
    return this.reviewResult;
  }


  /**
   * @param reviewResult the reviewResult to set
   */
  public void setReviewResult(final CDRReviewResult reviewResult) {
    this.reviewResult = reviewResult;
  }


  /**
   * @return the paramMappingMap
   */
  public Map<String, Parameter> getParamMappingMap() {
    return this.paramMappingMap;
  }


  /**
   * @param paramMappingMap the paramMappingMap to set
   */
  public void setParamMappingMap(final Map<String, Parameter> paramMappingMap) {
    this.paramMappingMap = paramMappingMap;
  }


  /**
   * @return the funcMappingMap
   */
  public Map<String, Function> getFuncMappingMap() {
    return this.funcMappingMap;
  }


  /**
   * @param funcMappingMap the funcMappingMap to set
   */
  public void setFuncMappingMap(final Map<String, Function> funcMappingMap) {
    this.funcMappingMap = funcMappingMap;
  }


  /**
   * @return the pidcVers
   */
  public PidcVersion getPidcVers() {
    return this.pidcVers;
  }


  /**
   * @param pidcVers the pidcVers to set
   */
  public void setPidcVers(final PidcVersion pidcVers) {
    this.pidcVers = pidcVers;
  }


  /**
   * @return the pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final PidcA2l pidcA2l) {
    this.pidcA2l = pidcA2l;
  }


  /**
   * @return the pidc
   */
  public Pidc getPidc() {
    return this.pidc;
  }


  /**
   * @param pidc the pidc to set
   */
  public void setPidc(final Pidc pidc) {
    this.pidc = pidc;
  }


  /**
   * @return the parentReviewResult
   */
  public CDRReviewResult getParentReviewResult() {
    return this.parentReviewResult;
  }


  /**
   * @param parentReviewResult the parentReviewResult to set
   */
  public void setParentReviewResult(final CDRReviewResult parentReviewResult) {
    this.parentReviewResult = parentReviewResult;
  }


  /**
   * @return the parentParamMap
   */
  public Map<Long, CDRResultParameter> getParentParamMap() {
    return this.parentParamMap;
  }


  /**
   * @param parentParamMap the parentParamMap to set
   */
  public void setParentParamMap(final Map<Long, CDRResultParameter> parentParamMap) {
    this.parentParamMap = parentParamMap;
  }


  /**
   * @return the projDeltaParentParamMap
   */
  public Map<Long, CDRResultParameter> getProjDeltaParentParamMap() {
    return this.projDeltaParentParamMap;
  }


  /**
   * @param projDeltaParentParamMap the projDeltaParentParamMap to set
   */
  public void setProjDeltaParentParamMap(final Map<Long, CDRResultParameter> projDeltaParentParamMap) {
    this.projDeltaParentParamMap = projDeltaParentParamMap;
  }


  /**
   * @return the icdmFiles
   */
  public Map<String, List<RvwFile>> getIcdmFiles() {
    return this.icdmFiles;
  }


  /**
   * @param icdmFiles the icdmFiles to set
   */
  public void setIcdmFiles(final Map<String, List<RvwFile>> icdmFiles) {
    this.icdmFiles = icdmFiles;
  }


  /**
   * @return the attrValMap
   */
  public Map<Long, RvwAttrValue> getAttrValMap() {
    return this.attrValMap;
  }


  /**
   * @param attrValMap the attrValMap to set
   */
  public void setAttrValMap(final Map<Long, RvwAttrValue> attrValMap) {
    this.attrValMap = attrValMap;
  }


  /**
   * @return the participantsMap
   */
  public Map<Long, RvwParticipant> getParticipantsMap() {
    return this.participantsMap;
  }


  /**
   * @param participantsMap the participantsMap to set
   */
  public void setParticipantsMap(final Map<Long, RvwParticipant> participantsMap) {
    this.participantsMap = participantsMap;
  }


  /**
   * @return the variantsMap
   */
  public Map<Long, RvwVariant> getVariantsMap() {
    return this.variantsMap;
  }


  /**
   * @param variantsMap the variantsMap to set
   */
  public void setVariantsMap(final Map<Long, RvwVariant> variantsMap) {
    this.variantsMap = variantsMap;
  }


  /**
   * @return the secondayResultsMap
   */
  public Map<Long, RvwResultsSecondary> getSecondayResultsMap() {
    return this.secondayResultsMap;
  }


  /**
   * @param secondayResultsMap the secondayResultsMap to set
   */
  public void setSecondayResultsMap(final Map<Long, RvwResultsSecondary> secondayResultsMap) {
    this.secondayResultsMap = secondayResultsMap;
  }


  /**
   * @return the wpDivision
   */
  public WorkPackageDivision getWpDivision() {
    return this.wpDivision;
  }


  /**
   * @param wpDivision the wpDivision to set
   */
  public void setWpDivision(final WorkPackageDivision wpDivision) {
    this.wpDivision = wpDivision;
  }


  /**
   * @return the firstVariant
   */
  public PidcVariant getFirstVariant() {
    return this.firstVariant;
  }


  /**
   * @param firstVariant the firstVariant to set
   */
  public void setFirstVariant(final PidcVariant firstVariant) {
    this.firstVariant = firstVariant;
  }


  /**
   * @return the ruleSet
   */
  public RuleSet getRuleSet() {
    return this.ruleSet;
  }


  /**
   * @param ruleSet the ruleSet to set
   */
  public void setRuleSet(final RuleSet ruleSet) {
    this.ruleSet = ruleSet;
  }


  /**
   * @return the paramAdditionalFiles
   */
  public Map<Long, List<RvwFile>> getParamAdditionalFiles() {
    return this.paramAdditionalFiles;
  }


  /**
   * @param paramAdditionalFiles the paramAdditionalFiles to set
   */
  public void setParamAdditionalFiles(final Map<Long, List<RvwFile>> paramAdditionalFiles) {
    this.paramAdditionalFiles = paramAdditionalFiles;
  }


  /**
   * @return the rvwParamSecondaryMap
   */
  public Map<Long, Map<Long, RvwParametersSecondary>> getRvwParamSecondaryMap() {
    return this.rvwParamSecondaryMap;
  }


  /**
   * @param rvwParamSecondaryMap the rvwParamSecondaryMap to set
   */
  public void setRvwParamSecondaryMap(final Map<Long, Map<Long, RvwParametersSecondary>> rvwParamSecondaryMap) {
    this.rvwParamSecondaryMap = rvwParamSecondaryMap;
  }


  /**
   * @return the projDeltaParentPidcVersName
   */
  public String getProjDeltaParentPidcVersName() {
    return this.projDeltaParentPidcVersName;
  }


  /**
   * @param projDeltaParentPidcVersName the projDeltaParentPidcVersName to set
   */
  public void setProjDeltaParentPidcVersName(final String projDeltaParentPidcVersName) {
    this.projDeltaParentPidcVersName = projDeltaParentPidcVersName;
  }


  /**
   * @return the projDeltaParentVariantName
   */
  public String getProjDeltaParentVariantName() {
    return this.projDeltaParentVariantName;
  }


  /**
   * @param projDeltaParentVariantName the projDeltaParentVariantName to set
   */
  public void setProjDeltaParentVariantName(final String projDeltaParentVariantName) {
    this.projDeltaParentVariantName = projDeltaParentVariantName;
  }


  /**
   * @return the readOnlyParamSet
   */
  public Set<Long> getReadOnlyParamSet() {
    return this.readOnlyParamSet;
  }


  /**
   * @param readOnlyParamSet the readOnlyParamSet to set
   */
  public void setReadOnlyParamSet(final Set<Long> readOnlyParamSet) {
    this.readOnlyParamSet = readOnlyParamSet;
  }


  /**
   * @return the depParamSet
   */
  public Map<Long, List<String>> getDepParamMap() {
    return this.depParamMap;
  }


  /**
   * @param depParamSet the depParamSet to set
   */
  public void setDepParamMap(final Map<Long, List<String>> depParamMap) {
    this.depParamMap = depParamMap;
  }


  /**
   * @return the a2lWpRespMap
   */
  public Map<String, Set<RvwResultWPandRespModel>> getA2lWpMap() {
    return this.a2lWpMap;
  }


  /**
   * @param a2lWpMap the a2lWpMap to set
   */
  public void setA2lWpMap(final Map<String, Set<RvwResultWPandRespModel>> a2lWpMap) {
    this.a2lWpMap = a2lWpMap;
  }


  /**
   * @return the a2lWpSet
   */
  public Set<RvwResultWPandRespModel> getA2lWpSet() {
    return this.a2lWpSet;
  }


  /**
   * @param a2lWpSet the a2lWpSet to set
   */
  public void setA2lWpSet(final Set<RvwResultWPandRespModel> a2lWpSet) {
    this.a2lWpSet = a2lWpSet;
  }

  /**
   * @return the a2lWpRespSet
   */
  public Set<RvwResultWPandRespModel> getA2lWpRespSet() {
    return this.a2lWpRespSet;
  }


  /**
   * @param a2lWpRespSet the a2lWpRespSet to set
   */
  public void setA2lWpRespSet(final Set<RvwResultWPandRespModel> a2lWpRespSet) {
    this.a2lWpRespSet = a2lWpRespSet;
  }

  /**
   * @return the paramIdAndWpAndRespMap
   */
  public Map<Long, Map<Long, String>> getParamIdAndWpAndRespMap() {
    return this.paramIdAndWpAndRespMap;
  }


  /**
   * @param paramIdAndWpAndRespMap the paramIdAndWpAndRespMap to set
   */
  public void setParamIdAndWpAndRespMap(final Map<Long, Map<Long, String>> paramIdAndWpAndRespMap) {
    this.paramIdAndWpAndRespMap = paramIdAndWpAndRespMap;
  }


  /**
   * @return the a2lResponsibilityMap
   */
  public Map<Long, A2lResponsibility> getA2lResponsibilityMap() {
    return this.a2lResponsibilityMap;
  }


  /**
   * @param a2lResponsibilityMap the a2lResponsibilityMap to set
   */
  public void setA2lResponsibilityMap(final Map<Long, A2lResponsibility> a2lResponsibilityMap) {
    this.a2lResponsibilityMap = a2lResponsibilityMap;
  }


  /**
   * @return the rvwInfoWpDefDetails
   */
  public ReviewInfoWpDefDetails getRvwInfoWpDefDetails() {
    return this.rvwInfoWpDefDetails;
  }


  /**
   * @param rvwInfoWpDefDetails the rvwInfoWpDefDetails to set
   */
  public void setRvwInfoWpDefDetails(final ReviewInfoWpDefDetails rvwInfoWpDefDetails) {
    this.rvwInfoWpDefDetails = rvwInfoWpDefDetails;
  }


  /**
   * @return the qnaireDataForRvwSet
   */
  public Set<QnaireRespStatusData> getQnaireDataForRvwSet() {
    return this.qnaireDataForRvwSet;
  }


  /**
   * @param qnaireDataForRvwSet the qnaireDataForRvwSet to set
   */
  public void setQnaireDataForRvwSet(final Set<QnaireRespStatusData> qnaireDataForRvwSet) {
    this.qnaireDataForRvwSet = qnaireDataForRvwSet;
  }


  /**
   * @return the respWpFinishedStatusMap
   */
  public Map<Long, Map<Long, String>> getRespWpFinishedStatusMap() {
    return this.respWpFinishedStatusMap;
  }


  /**
   * @param respWpFinishedStatusMap the respWpFinishedStatusMap to set
   */
  public void setRespWpFinishedStatusMap(final Map<Long, Map<Long, String>> respWpFinishedStatusMap) {
    this.respWpFinishedStatusMap = respWpFinishedStatusMap;
  }


}
