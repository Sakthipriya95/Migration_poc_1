package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Review Comment History Model class
 *
 * @author PDH2COB
 */
public class RvwUserCmntHistory implements Cloneable, Comparable<RvwUserCmntHistory>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 143246093543437L;
  /**
   * Rvw Cmnt History Id
   */
  private Long id;
  /**
   * Rvw Comment
   */
  private String rvwComment;

  /**
   * Rvw Cmnt User
   */
  private Long rvwCmntUserId;
  /**
   * Version
   */
  private Long version;
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
   * @return rvwComment
   */
  public String getRvwComment() {
    return this.rvwComment;
  }

  /**
   * @param rvwComment set rvwComment
   */
  public void setRvwComment(final String rvwComment) {
    this.rvwComment = rvwComment;
  }


  /**
   * @return the rvwCmntUserId
   */
  public Long getRvwCmntUserId() {
    return this.rvwCmntUserId;
  }


  /**
   * @param rvwCmntUserId the rvwCmntUserId to set
   */
  public void setRvwCmntUserId(final Long rvwCmntUserId) {
    this.rvwCmntUserId = rvwCmntUserId;
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
  public RvwUserCmntHistory clone() {
    try {
      return (RvwUserCmntHistory) super.clone();
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
  public int compareTo(final RvwUserCmntHistory object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((RvwUserCmntHistory) obj).getId());
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
    return this.rvwComment;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
//
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // unused
  }

}
