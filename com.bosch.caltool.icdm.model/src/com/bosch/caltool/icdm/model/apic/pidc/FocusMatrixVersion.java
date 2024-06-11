package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Focus Matrix Version Model class
 *
 * @author MKL2COB
 */
public class FocusMatrixVersion implements Cloneable, Comparable<FocusMatrixVersion>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = 5489057474945477873L;
  /**
   * Fm Vers Id
   */
  private Long id;
  /**
   * Pidc Vers Id
   */
  private Long pidcVersId;
  /**
   * Name
   */
  private String name;
  /**
   * Rev Num
   */
  private Long revNum;
  /**
   * Status
   */
  private String status;
  /**
   * Reviewed User
   */
  private Long reviewedUser;
  /**
   * Reviewed Date
   */
  private String reviewedDate;
  /**
   * Link
   */
  private String link;
  /**
   * Remark
   */
  private String remark;
  /**
   * Rvw Status
   */
  private String rvwStatus;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified User
   */
  private String modifiedUser;
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
   * @return pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }

  /**
   * @param pidcVersId set pidcVersId
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  /**
   * @return name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name set name
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return revNum
   */
  public Long getRevNum() {
    return this.revNum;
  }

  /**
   * @param revNum set revNum
   */
  public void setRevNum(final Long revNum) {
    this.revNum = revNum;
  }

  /**
   * @return status
   */
  public String getStatus() {
    return this.status;
  }

  /**
   * @param status set status
   */
  public void setStatus(final String status) {
    this.status = status;
  }

  /**
   * @return reviewedUser
   */
  public Long getReviewedUser() {
    return this.reviewedUser;
  }

  /**
   * @param reviewedUser set reviewedUser
   */
  public void setReviewedUser(final Long reviewedUser) {
    this.reviewedUser = reviewedUser;
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
   * @return rvwStatus
   */
  public String getRvwStatus() {
    return this.rvwStatus;
  }

  /**
   * @param rvwStatus set rvwStatus
   */
  public void setRvwStatus(final String rvwStatus) {
    this.rvwStatus = rvwStatus;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
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
   * {@inheritDoc}
   */
  @Override
  public FocusMatrixVersion clone() {

    FocusMatrixVersion fMatrixVersion = null;
    try {
      fMatrixVersion = (FocusMatrixVersion) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return fMatrixVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FocusMatrixVersion other) {
    int compResult = ModelUtil.compare(getName(), other.getName());
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ModelUtil.compare(getId(), other.getId());
    }
    return compResult;
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((FocusMatrixVersion) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
