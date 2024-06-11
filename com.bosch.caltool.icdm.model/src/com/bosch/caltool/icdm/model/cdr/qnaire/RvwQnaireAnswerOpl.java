package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * RvwQnaireAnswerOpl Model class
 *
 * @author gge6cob
 */
public class RvwQnaireAnswerOpl implements Comparable<RvwQnaireAnswerOpl>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 50332498220283L;
  /**
   * Open Points Id
   */
  private Long id;
  /**
   * Open Points
   */
  private String openPoints;
  /**
   * Measure
   */
  private String measure;
  /**
   * Responsible
   */
  private Long responsible;
  /**
   * REsponsible display name
   */
  private String responsibleName;
  /**
   * Completion Date
   */
  private String completionDate;
  /**
   * Result
   */
  private String result;
  /**
   * Rvw Answer Id
   */
  private Long rvwAnswerId;
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
  public Long getResponsible() {
    return this.responsible;
  }

  /**
   * @param responsible set responsible
   */
  public void setResponsible(final Long responsible) {
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
   * @return rvwAnswerId
   */
  public Long getRvwAnswerId() {
    return this.rvwAnswerId;
  }

  /**
   * @param rvwAnswerId set rvwAnswerId
   */
  public void setRvwAnswerId(final Long rvwAnswerId) {
    this.rvwAnswerId = rvwAnswerId;
  }


  /**
   * @return the responsibleName
   */
  public String getResponsibleName() {
    return this.responsibleName;
  }


  /**
   * @param responsibleName the responsibleName to set
   */
  public void setResponsibleName(final String responsibleName) {
    this.responsibleName = responsibleName;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RvwQnaireAnswerOpl object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((RvwQnaireAnswerOpl) obj).getId());
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
