package com.bosch.caltool.icdm.model.cdr;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * DaQnaireResp Model class
 *
 * @author say8cob
 */
public class DaQnaireResp implements Cloneable, Comparable<DaQnaireResp>, IModel {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 118061486468603L;
  /**
   * Da Qnaire Resp Id
   */
  private Long id;
  /**
   * Da Wp Resp Id
   */
  private Long daWpRespId;
  /**
   * Qnaire Resp Id
   */
  private BigDecimal qnaireRespId;
  /**
   * Qnaire Resp Name
   */
  private String qnaireRespName;
  /**
   * Qnaire Resp Vers Id
   */
  private BigDecimal qnaireRespVersId;
  /**
   * Qnaire Resp Version Name
   */
  private String qnaireRespVersionName;
  /**
   * Ready For Production Flag
   */
  private String readyForProductionFlag;
  /**
   * Baseline Existing Flag
   */
  private String baselineExistingFlag;
  /**
   * Num Positive Answers
   */
  private BigDecimal numPositiveAnswers;
  /**
   * Num Neutral Answers
   */
  private BigDecimal numNeutralAnswers;
  /**
   * Num Negative Answers
   */
  private BigDecimal numNegativeAnswers;
  /**
   * Reviewed Date
   */
  private String reviewedDate;
  /**
   * Reviewed User
   */
  private String reviewedUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;

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
   * @return daWpRespId
   */
  public Long getDaWpRespId() {
    return this.daWpRespId;
  }

  /**
   * @param daWpRespId set daWpRespId
   */
  public void setDaWpRespId(final Long daWpRespId) {
    this.daWpRespId = daWpRespId;
  }

  /**
   * @return qnaireRespId
   */
  public BigDecimal getQnaireRespId() {
    return this.qnaireRespId;
  }

  /**
   * @param qnaireRespId set qnaireRespId
   */
  public void setQnaireRespId(final BigDecimal qnaireRespId) {
    this.qnaireRespId = qnaireRespId;
  }

  /**
   * @return qnaireRespName
   */
  public String getQnaireRespName() {
    return this.qnaireRespName;
  }

  /**
   * @param qnaireRespName set qnaireRespName
   */
  public void setQnaireRespName(final String qnaireRespName) {
    this.qnaireRespName = qnaireRespName;
  }

  /**
   * @return qnaireRespVersId
   */
  public BigDecimal getQnaireRespVersId() {
    return this.qnaireRespVersId;
  }

  /**
   * @param qnaireRespVersId set qnaireRespVersId
   */
  public void setQnaireRespVersId(final BigDecimal qnaireRespVersId) {
    this.qnaireRespVersId = qnaireRespVersId;
  }

  /**
   * @return qnaireRespVersionName
   */
  public String getQnaireRespVersionName() {
    return this.qnaireRespVersionName;
  }

  /**
   * @param qnaireRespVersionName set qnaireRespVersionName
   */
  public void setQnaireRespVersionName(final String qnaireRespVersionName) {
    this.qnaireRespVersionName = qnaireRespVersionName;
  }

  /**
   * @return readyForProductionFlag
   */
  public String getReadyForProductionFlag() {
    return this.readyForProductionFlag;
  }

  /**
   * @param readyForProductionFlag set readyForProductionFlag
   */
  public void setReadyForProductionFlag(final String readyForProductionFlag) {
    this.readyForProductionFlag = readyForProductionFlag;
  }

  /**
   * @return baselineExistingFlag
   */
  public String getBaselineExistingFlag() {
    return this.baselineExistingFlag;
  }

  /**
   * @param baselineExistingFlag set baselineExistingFlag
   */
  public void setBaselineExistingFlag(final String baselineExistingFlag) {
    this.baselineExistingFlag = baselineExistingFlag;
  }

  /**
   * @return numPositiveAnswers
   */
  public BigDecimal getNumPositiveAnswers() {
    return this.numPositiveAnswers;
  }

  /**
   * @param numPositiveAnswers set numPositiveAnswers
   */
  public void setNumPositiveAnswers(final BigDecimal numPositiveAnswers) {
    this.numPositiveAnswers = numPositiveAnswers;
  }

  /**
   * @return numNeutralAnswers
   */
  public BigDecimal getNumNeutralAnswers() {
    return this.numNeutralAnswers;
  }

  /**
   * @param numNeutralAnswers set numNeutralAnswers
   */
  public void setNumNeutralAnswers(final BigDecimal numNeutralAnswers) {
    this.numNeutralAnswers = numNeutralAnswers;
  }

  /**
   * @return numNegativeAnswers
   */
  public BigDecimal getNumNegativeAnswers() {
    return this.numNegativeAnswers;
  }

  /**
   * @param numNegativeAnswers set numNegativeAnswers
   */
  public void setNumNegativeAnswers(final BigDecimal numNegativeAnswers) {
    this.numNegativeAnswers = numNegativeAnswers;
  }

  /**
   * @return reviewedDate
   */
  public String getReviewedDate() {
    return this.reviewedDate;
  }

  /**
   * @param reviewedDate set reviewedDate
   */
  public void setReviewedDate(final String reviewedDate) {
    this.reviewedDate = reviewedDate;
  }

  /**
   * @return reviewedUser
   */
  public String getReviewedUser() {
    return this.reviewedUser;
  }

  /**
   * @param reviewedUser set reviewedUser
   */
  public void setReviewedUser(final String reviewedUser) {
    this.reviewedUser = reviewedUser;
  }

  /**
   * {@inheritDoc}
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * {@inheritDoc}
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * {@inheritDoc}
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
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
   * {@inheritDoc}
   */
  @Override
  public DaQnaireResp clone() {
    try {
      return (DaQnaireResp) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      // TODO
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final DaQnaireResp object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return ModelUtil.isEqual(getId(), ((DaQnaireResp) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
