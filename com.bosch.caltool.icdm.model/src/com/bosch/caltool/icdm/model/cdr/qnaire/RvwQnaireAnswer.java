package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * RvwQnaireAnswer Model class
 *
 * @author gge6cob
 */
public class RvwQnaireAnswer implements Cloneable, Comparable<RvwQnaireAnswer>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 50360483193178L;
  /**
   * Rvw Answer Id
   */
  private Long id;
  /**
   * Question Id
   */
  private Long questionId;
  /**
   * Result
   */
  private String result;
  /**
   * Measurement
   */
  private String measurement;
  /**
   * Series
   */
  private String series;
  /**
   * Remark
   */
  private String remark;
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
   * Open Points
   */
  private Set<Long> oplId = new HashSet<>();
  /**
   * qnaire response version id
   */
  private Long qnaireRespVersId;
  /**
   * 493630 - Questionare name should appear instead of id in Log info
   * ----------------------------------------------------------------------------------------------------------------------------------------------------------
   * Qnaire Name
   */
  private String qnaireName;

  /**
   * Qnaire Result Option Id
   */
  private Long selQnaireResultOptID;

  /**
   * Qnaire Result Option Id
   */
  private String selQnaireResultAssement;
  
  /**
   * Review Questionnaire Answer is allowed to mark WP as finished.
   */
  private boolean resultAlwFinishWPFlag = true;


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
   * @return result
   */
  public String getResult() {
    return this.result;
  }

  /**
   * @param result set result
   */
  public void setResult(final String result) {
    this.result = result;
  }

  /**
   * @return measurement
   */
  public String getMeasurement() {
    return this.measurement;
  }

  /**
   * @param measurement set measurement
   */
  public void setMeasurement(final String measurement) {
    this.measurement = measurement;
  }

  /**
   * @return series
   */
  public String getSeries() {
    return this.series;
  }

  /**
   * @param series set series
   */
  public void setSeries(final String series) {
    this.series = series;
  }

  /**
   * @return remark
   */
  public String getRemark() {
    return this.remark;
  }

  /**
   * @param remark set remark
   */
  public void setRemark(final String remark) {
    this.remark = remark;
  }

  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
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
   * @return the questionId
   */
  public Long getQuestionId() {
    return this.questionId;
  }


  /**
   * @param questionId the questionId to set
   */
  public void setQuestionId(final Long questionId) {
    this.questionId = questionId;
  }


  /**
   * @return the oplId
   */
  public Set<Long> getOplId() {
    return this.oplId;
  }


  /**
   * @param oplId the oplId to set
   */
  public void setOplId(final Set<Long> oplId) {
    this.oplId = oplId == null ? null : new HashSet<>(oplId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RvwQnaireAnswer clone() {
    try {
      return (RvwQnaireAnswer) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RvwQnaireAnswer object) {
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
    if ((getId() == -1l) || ((obj.getClass() == this.getClass()) && (((RvwQnaireAnswer) obj).getId() == -1l))) {
      return false;
    }
    if ((obj.getClass() == this.getClass())) {
      return ModelUtil.isEqual(getId(), ((RvwQnaireAnswer) obj).getId());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.qnaireName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.qnaireName = name;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // NA
  }


  /**
   * @return the selQnaireResultOptID
   */
  public Long getSelQnaireResultOptID() {
    return this.selQnaireResultOptID;
  }


  /**
   * @param selQnaireResultOptID the selQnaireResultOptID to set
   */
  public void setSelQnaireResultOptID(final Long selQnaireResultOptID) {
    this.selQnaireResultOptID = selQnaireResultOptID;
  }

  /**
   * @return the qnaireRespVersId
   */
  public Long getQnaireRespVersId() {
    return this.qnaireRespVersId;
  }

  /**
   * @param qnaireRespVersId the qnaireRespVersId to set
   */
  public void setQnaireRespVersId(final Long qnaireRespVersId) {
    this.qnaireRespVersId = qnaireRespVersId;
  }


  /**
   * @return the selQnaireResultAssement
   */
  public String getSelQnaireResultAssement() {
    return this.selQnaireResultAssement;
  }


  /**
   * @param selQnaireResultAssement the selQnaireResultAssement to set
   */
  public void setSelQnaireResultAssement(final String selQnaireResultAssement) {
    this.selQnaireResultAssement = selQnaireResultAssement;
  }

  
  /**
   * @return the resultAlwFinishWPFlag
   */
  public boolean isResultAlwFinishWPFlag() {
    return resultAlwFinishWPFlag;
  }

  
  /**
   * @param resultAlwFinishWPFlag the resultAlwFinishWPFlag to set
   */
  public void setResultAlwFinishWPFlag(boolean resultAlwFinishWPFlag) {
    this.resultAlwFinishWPFlag = resultAlwFinishWPFlag;
  }


}
