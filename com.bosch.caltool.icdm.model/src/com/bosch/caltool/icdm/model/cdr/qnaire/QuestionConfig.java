package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * QuestionConfig Model class
 *
 * @author NIP4COB
 */
public class QuestionConfig implements Comparable<QuestionConfig>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 212555800857017L;
  /**
   * Qconfig Id
   */
  private Long id;
  /**
   * Q Id
   */
  private Long qId;
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
   * Link
   */
  private String link;
  /**
   * Open Points
   */
  private String openPoints;
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
   * Measure
   */
  private String measure;
  /**
   * Responsible
   */
  private String responsible;
  /**
   * Completion Date
   */
  private String completionDate;

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
   * @return qId
   */
  public Long getQId() {
    return this.qId;
  }

  /**
   * @param qId set qId
   */
  public void setQId(final Long qId) {
    this.qId = qId;
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
   * @return link
   */
  public String getLink() {
    return this.link;
  }

  /**
   * @param link set link
   */
  public void setLink(final String link) {
    this.link = link;
  }

  /**
   * @return openPoints
   */
  public String getOpenPoints() {
    return this.openPoints;
  }

  /**
   * @param openPoints set openPoints
   */
  public void setOpenPoints(final String openPoints) {
    this.openPoints = openPoints;
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
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
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
   * @return measure
   */
  public String getMeasure() {
    return this.measure;
  }

  /**
   * @param measure set measure
   */
  public void setMeasure(final String measure) {
    this.measure = measure;
  }

  /**
   * @return responsible
   */
  public String getResponsible() {
    return this.responsible;
  }

  /**
   * @param responsible set responsible
   */
  public void setResponsible(final String responsible) {
    this.responsible = responsible;
  }

  /**
   * @return completionDate
   */
  public String getCompletionDate() {
    return this.completionDate;
  }

  /**
   * @param completionDate set completionDate
   */
  public void setCompletionDate(final String completionDate) {
    this.completionDate = completionDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QuestionConfig object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((QuestionConfig) obj).getId());
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
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // TODO Auto-generated method stub

  }

}
