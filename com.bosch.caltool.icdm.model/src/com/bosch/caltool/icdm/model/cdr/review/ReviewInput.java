/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;


/**
 * @author bru2cob
 */
public class ReviewInput {

  /**
   * Review description
   */
  private String description;
  /**
   * Review Version
   */
  private Long reviewVersion;
  /**
   * Selected a2l group name
   */
  private String a2lGroupName;
  /**
   * Selected a2l group name's list
   */
  private List<String> a2lGroupNameList;
  /**
   * Set of functions to be reviewed
   */
  private Set<String> selReviewFuncs;

  /**
   * Review type
   */
  private String reviewType;
  /**
   * User related info obj
   */
  private UserData userData;
  /**
   * Files related info obj
   */
  private FileData fileData;
  /**
   * Pidc related info obj
   */
  private PidcData pidcData;
  /**
   * Rules related info obj
   */
  private RulesData rulesData;
  /**
   * Result related info obj
   */
  private ResultData resultData;
  /**
   * is files to be reivewed selected
   */
  private boolean isFilesToBeReviewed;
  /**
   * is delta review
   */
  private boolean isDeltaReview;
  /**
   * delta review type ( P/D)
   */
  private String deltaReviewType;
  /**
   * FC2WP id
   */
  private Long wpDivId;

  /**
   * Selected wp resp name
   */
  private String wpRespName;

  /**
   * Selected wp Def Vers Id
   */
  private Long a2lWpDefVersId;

  /**
   * RvwWpAndRespModel for Pal
   */
  private Set<RvwWpAndRespModel> rvwWpAndRespModelSet = new HashSet<>();
  /**
   * function map contains param based on selected wp resp
   */
  private Map<String, Set<String>> functionMap = new HashMap<>();

  /**
   * true if the command is called from cancel operation
   */
  private boolean isFinish;

  /**
   * For HOlding CDR Review result during Update service call
   */
  private CDRReviewResult cdrReviewResult;

  private RvwVariant rvwVariant;

  private List<WpRespModel> selectedWpRespList = new ArrayList<>();
  /**
   * O - only OBD Gen Qnaire, N - only Simplified Qnaire, B - Both OBD and Simplified Qnaire
   */
  private String obdFlag;

  /**
   * @return the wpDivId
   */
  public Long getWpDivId() {
    return this.wpDivId;
  }

  /**
   * Source Type of Review
   */
  private String sourceType;


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
   * @param wpDivId the wpDivId to set
   */
  public void setWpDivId(final Long wpDivId) {
    this.wpDivId = wpDivId;
  }


  /**
   * @return the isFinish
   */
  public boolean isFinish() {
    return this.isFinish;
  }


  /**
   * @param isFinish the isFinish to set
   */
  public void setFinish(final boolean isFinish) {
    this.isFinish = isFinish;
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
    this.a2lGroupNameList = a2lGroupNameList == null ? null : new ArrayList<>(a2lGroupNameList);
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
   * @return the selReviewFuncs
   */
  public Set<String> getSelReviewFuncs() {
    return this.selReviewFuncs;
  }

  /**
   * @param selReviewFuncs the selReviewFuncs to set
   */
  public void setSelReviewFuncs(final Set<String> selReviewFuncs) {
    this.selReviewFuncs = selReviewFuncs == null ? null : new TreeSet<>(selReviewFuncs);
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
   * @return the userData
   */
  public UserData getUserData() {
    return this.userData;
  }

  /**
   * @param userData the userData to set
   */
  public void setUserData(final UserData userData) {
    this.userData = userData;
  }

  /**
   * @return the fileData
   */
  public FileData getFileData() {
    return this.fileData;
  }

  /**
   * @param fileData the fileData to set
   */
  public void setFileData(final FileData fileData) {
    this.fileData = fileData;
  }

  /**
   * @return the pidcData
   */
  public PidcData getPidcData() {
    return this.pidcData;
  }

  /**
   * @param pidcData the pidcData to set
   */
  public void setPidcData(final PidcData pidcData) {
    this.pidcData = pidcData;
  }

  /**
   * @return the rulesData
   */
  public RulesData getRulesData() {
    return this.rulesData;
  }

  /**
   * @param rulesData the rulesData to set
   */
  public void setRulesData(final RulesData rulesData) {
    this.rulesData = rulesData;
  }

  /**
   * @return the resultData
   */
  public ResultData getResultData() {
    return this.resultData;
  }

  /**
   * @param resultData the resultData to set
   */
  public void setResultData(final ResultData resultData) {
    this.resultData = resultData;
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
   * @return the a2lGroupId
   */
  public String getA2lGroupName() {
    return this.a2lGroupName;
  }


  /**
   * @param a2lGroupName the a2lGroupId to set
   */
  public void setA2lGroupName(final String a2lGroupName) {
    this.a2lGroupName = a2lGroupName;
  }

  /**
   * @return the isDeltaReview
   */
  public boolean isDeltaReview() {
    return this.isDeltaReview;
  }


  /**
   * @param isDeltaReview the isDeltaReview to set
   */
  public void setDeltaReview(final boolean isDeltaReview) {
    this.isDeltaReview = isDeltaReview;
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
   * @return the reviewVersion
   */
  public Long getReviewVersion() {
    return this.reviewVersion;
  }


  /**
   * @param reviewVersion the reviewVersion to set
   */
  public void setReviewVersion(final Long reviewVersion) {
    this.reviewVersion = reviewVersion;
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
