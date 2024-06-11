package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * QuestionDepenAttribute Model class
 *
 * @author NIP4COB
 */
public class QuestionDepenAttr implements Comparable<QuestionDepenAttr>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 215437544388166L;
  /**
   * Qattr Depen Id
   */
  private Long id;
  /**
   * Q Id
   */
  private Long qId;
  /**
   * Attr Id
   */
  private Long attrId;
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
   * @return attrId
   */
  public Long getAttrId() {
    return this.attrId;
  }

  /**
   * @param attrId set attrId
   */
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
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
  public int compareTo(final QuestionDepenAttr object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((QuestionDepenAttr) obj).getId());
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
