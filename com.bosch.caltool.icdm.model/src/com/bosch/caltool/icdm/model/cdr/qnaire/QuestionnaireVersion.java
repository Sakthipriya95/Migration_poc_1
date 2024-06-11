/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Questionnaire Version Model class
 *
 * @author bru2cob
 */
public class QuestionnaireVersion implements Cloneable, Comparable<QuestionnaireVersion>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = -3686082471401820202L;
  /**
   * Qnaire Vers Id
   */
  private Long id;
  /**
   * Name
   */
  private String name;
  /**
   * Qnaire Id
   */
  private Long qnaireId;
  /**
   * Active Flag
   */
  private String activeFlag;
  /**
   * Inwork Flag
   */
  private String inworkFlag;
  /**
   * Result Relevant Flag
   */
  private String resultRelevantFlag;
  /**
   * Result Hidden Flag
   */
  private String resultHiddenFlag;
  /**
   * Measurement Relevant Flag
   */
  private String measurementRelevantFlag;
  /**
   * Measurement Hidden Flag
   */
  private String measurementHiddenFlag;
  /**
   * Series Relevant Flag
   */
  private String seriesRelevantFlag;
  /**
   * Series Hidden Flag
   */
  private String seriesHiddenFlag;
  /**
   * Link Relevant Flag
   */
  private String linkRelevantFlag;
  /**
   * Link Hidden Flag
   */
  private String linkHiddenFlag;
  /**
   * Open Points Relevant Flag
   */
  private String openPointsRelevantFlag;
  /**
   * Open Points Hidden Flag
   */
  private String openPointsHiddenFlag;
  /**
   * Remark Relevant Flag
   */
  private String remarkRelevantFlag;
  /**
   * Remarks Hidden Flag
   */
  private String remarksHiddenFlag;
  /**
   * Major Version Num
   */
  private Long majorVersionNum;
  /**
   * Minor Version Num
   */
  private Long minorVersionNum;
  /**
   * Desc Eng
   */
  private String descEng;
  /**
   * Desc Ger
   */
  private String descGer;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Version
   */
  private Long version;
  /**
   * Measure Relavent Flag
   */
  private String measureRelaventFlag;
  /**
   * Measure Hidden Flag
   */
  private String measureHiddenFlag;
  /**
   * Responsible Relavent Flag
   */
  private String responsibleRelaventFlag;
  /**
   * Responsible Hidden Flag
   */
  private String responsibleHiddenFlag;
  /**
   * Completion Date Relavent Flag
   */
  private String completionDateRelaventFlag;
  /**
   * Completion Date Hidden Flag
   */
  private String completionDateHiddenFlag;

  private String description;

  private String genQuesEquivalent;
  
  private String noNegativeAnsAllowedFlag;


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return qnaireId
   */
  public Long getQnaireId() {
    return this.qnaireId;
  }

  /**
   * @param qnaireId set qnaireId
   */
  public void setQnaireId(final Long qnaireId) {
    this.qnaireId = qnaireId;
  }

  /**
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return activeFlag
   */
  public String getActiveFlag() {
    return this.activeFlag;
  }

  /**
   * @param activeFlag set activeFlag
   */
  public void setActiveFlag(final String activeFlag) {
    this.activeFlag = activeFlag;
  }

  /**
   * @return inworkFlag
   */
  public String getInworkFlag() {
    return this.inworkFlag;
  }

  /**
   * @param inworkFlag set inworkFlag
   */
  public void setInworkFlag(final String inworkFlag) {
    this.inworkFlag = inworkFlag;
  }

  /**
   * @return resultRelevantFlag
   */
  public String getResultRelevantFlag() {
    return this.resultRelevantFlag;
  }

  /**
   * @param resultRelevantFlag set resultRelevantFlag
   */
  public void setResultRelevantFlag(final String resultRelevantFlag) {
    this.resultRelevantFlag = resultRelevantFlag;
  }

  /**
   * @return resultHiddenFlag
   */
  public String getResultHiddenFlag() {
    return this.resultHiddenFlag;
  }

  /**
   * @param resultHiddenFlag set resultHiddenFlag
   */
  public void setResultHiddenFlag(final String resultHiddenFlag) {
    this.resultHiddenFlag = resultHiddenFlag;
  }

  /**
   * @return measurementRelevantFlag
   */
  public String getMeasurementRelevantFlag() {
    return this.measurementRelevantFlag;
  }

  /**
   * @param measurementRelevantFlag set measurementRelevantFlag
   */
  public void setMeasurementRelevantFlag(final String measurementRelevantFlag) {
    this.measurementRelevantFlag = measurementRelevantFlag;
  }

  /**
   * @return measurementHiddenFlag
   */
  public String getMeasurementHiddenFlag() {
    return this.measurementHiddenFlag;
  }

  /**
   * @param measurementHiddenFlag set measurementHiddenFlag
   */
  public void setMeasurementHiddenFlag(final String measurementHiddenFlag) {
    this.measurementHiddenFlag = measurementHiddenFlag;
  }

  /**
   * @return seriesRelevantFlag
   */
  public String getSeriesRelevantFlag() {
    return this.seriesRelevantFlag;
  }

  /**
   * @param seriesRelevantFlag set seriesRelevantFlag
   */
  public void setSeriesRelevantFlag(final String seriesRelevantFlag) {
    this.seriesRelevantFlag = seriesRelevantFlag;
  }

  /**
   * @return seriesHiddenFlag
   */
  public String getSeriesHiddenFlag() {
    return this.seriesHiddenFlag;
  }

  /**
   * @param seriesHiddenFlag set seriesHiddenFlag
   */
  public void setSeriesHiddenFlag(final String seriesHiddenFlag) {
    this.seriesHiddenFlag = seriesHiddenFlag;
  }

  /**
   * @return linkRelevantFlag
   */
  public String getLinkRelevantFlag() {
    return this.linkRelevantFlag;
  }

  /**
   * @param linkRelevantFlag set linkRelevantFlag
   */
  public void setLinkRelevantFlag(final String linkRelevantFlag) {
    this.linkRelevantFlag = linkRelevantFlag;
  }

  /**
   * @return linkHiddenFlag
   */
  public String getLinkHiddenFlag() {
    return this.linkHiddenFlag;
  }

  /**
   * @param linkHiddenFlag set linkHiddenFlag
   */
  public void setLinkHiddenFlag(final String linkHiddenFlag) {
    this.linkHiddenFlag = linkHiddenFlag;
  }

  /**
   * @return openPointsRelevantFlag
   */
  public String getOpenPointsRelevantFlag() {
    return this.openPointsRelevantFlag;
  }

  /**
   * @param openPointsRelevantFlag set openPointsRelevantFlag
   */
  public void setOpenPointsRelevantFlag(final String openPointsRelevantFlag) {
    this.openPointsRelevantFlag = openPointsRelevantFlag;
  }

  /**
   * @return openPointsHiddenFlag
   */
  public String getOpenPointsHiddenFlag() {
    return this.openPointsHiddenFlag;
  }


  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @param openPointsHiddenFlag set openPointsHiddenFlag
   */
  public void setOpenPointsHiddenFlag(final String openPointsHiddenFlag) {
    this.openPointsHiddenFlag = openPointsHiddenFlag;
  }

  /**
   * @return remarkRelevantFlag
   */
  public String getRemarkRelevantFlag() {
    return this.remarkRelevantFlag;
  }

  /**
   * @param remarkRelevantFlag set remarkRelevantFlag
   */
  public void setRemarkRelevantFlag(final String remarkRelevantFlag) {
    this.remarkRelevantFlag = remarkRelevantFlag;
  }

  /**
   * @return remarksHiddenFlag
   */
  public String getRemarksHiddenFlag() {
    return this.remarksHiddenFlag;
  }

  /**
   * @param remarksHiddenFlag set remarksHiddenFlag
   */
  public void setRemarksHiddenFlag(final String remarksHiddenFlag) {
    this.remarksHiddenFlag = remarksHiddenFlag;
  }

  /**
   * @return majorVersionNum
   */
  public Long getMajorVersionNum() {
    return this.majorVersionNum;
  }

  /**
   * @param majorVersionNum set majorVersionNum
   */
  public void setMajorVersionNum(final Long majorVersionNum) {
    this.majorVersionNum = majorVersionNum;
  }

  /**
   * @return minorVersionNum
   */
  public Long getMinorVersionNum() {
    return this.minorVersionNum;
  }

  /**
   * @param minorVersionNum set minorVersionNum
   */
  public void setMinorVersionNum(final Long minorVersionNum) {
    this.minorVersionNum = minorVersionNum;
  }

  /**
   * @return descEng
   */
  public String getDescEng() {
    return this.descEng;
  }

  /**
   * @param descEng set descEng
   */
  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  /**
   * @return descGer
   */
  public String getDescGer() {
    return this.descGer;
  }

  /**
   * @param descGer set descGer
   */
  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }

  /**
   * @return createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return measureRelaventFlag
   */
  public String getMeasureRelaventFlag() {
    return this.measureRelaventFlag;
  }

  /**
   * @param measureRelaventFlag set measureRelaventFlag
   */
  public void setMeasureRelaventFlag(final String measureRelaventFlag) {
    this.measureRelaventFlag = measureRelaventFlag;
  }

  /**
   * @return measureHiddenFlag
   */
  public String getMeasureHiddenFlag() {
    return this.measureHiddenFlag;
  }

  /**
   * @param measureHiddenFlag set measureHiddenFlag
   */
  public void setMeasureHiddenFlag(final String measureHiddenFlag) {
    this.measureHiddenFlag = measureHiddenFlag;
  }

  /**
   * @return responsibleRelaventFlag
   */
  public String getResponsibleRelaventFlag() {
    return this.responsibleRelaventFlag;
  }

  /**
   * @param responsibleRelaventFlag set responsibleRelaventFlag
   */
  public void setResponsibleRelaventFlag(final String responsibleRelaventFlag) {
    this.responsibleRelaventFlag = responsibleRelaventFlag;
  }

  /**
   * @return responsibleHiddenFlag
   */
  public String getResponsibleHiddenFlag() {
    return this.responsibleHiddenFlag;
  }

  /**
   * @param responsibleHiddenFlag set responsibleHiddenFlag
   */
  public void setResponsibleHiddenFlag(final String responsibleHiddenFlag) {
    this.responsibleHiddenFlag = responsibleHiddenFlag;
  }

  /**
   * @return completionDateRelaventFlag
   */
  public String getCompletionDateRelaventFlag() {
    return this.completionDateRelaventFlag;
  }

  /**
   * @param completionDateRelaventFlag set completionDateRelaventFlag
   */
  public void setCompletionDateRelaventFlag(final String completionDateRelaventFlag) {
    this.completionDateRelaventFlag = completionDateRelaventFlag;
  }

  /**
   * @return completionDateHiddenFlag
   */
  public String getCompletionDateHiddenFlag() {
    return this.completionDateHiddenFlag;
  }

  /**
   * @param completionDateHiddenFlag set completionDateHiddenFlag
   */
  public void setCompletionDateHiddenFlag(final String completionDateHiddenFlag) {
    this.completionDateHiddenFlag = completionDateHiddenFlag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuestionnaireVersion clone() {
    try {
      return (QuestionnaireVersion) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QuestionnaireVersion object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((QuestionnaireVersion) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * @return the genQuesEquivalent
   */
  public String getGenQuesEquivalent() {
    return this.genQuesEquivalent;
  }

  /**
   * @param genQuesEquivalent the genQuesEquivalent to set
   */
  public void setGenQuesEquivalent(final String genQuesEquivalent) {
    this.genQuesEquivalent = genQuesEquivalent;
  }


  /**
   * @return the noNegativeAnsAllowedFlag
   */
  public String getNoNegativeAnsAllowedFlag() {
    return noNegativeAnsAllowedFlag;
  }

  
  /**
   * @param noNegativeAnsAllowedFlag the noNegativeAnsAllowedFlag to set
   */
  public void setNoNegativeAnsAllowedFlag(String noNegativeAnsAllowedFlag) {
    this.noNegativeAnsAllowedFlag = noNegativeAnsAllowedFlag;
  }
}
