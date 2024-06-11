package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * RvwQnaireRespVersion Model class
 *
 * @author say8cob
 */
public class RvwQnaireRespVersion implements Cloneable, Comparable<RvwQnaireRespVersion>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = 2131271906400433232L;
  /**
   * Qnaire Resp Vers Id
   */
  private Long id;
  /**
   * Qnaire Resp Id
   */
  private Long qnaireRespId;

  /**
   * Qnaire Version Id
   */
  private Long qnaireVersionId;
  /**
   * Rev Number - Name created Date(Display name)
   */
  private String name;

  /**
   * Original Ver Name (from DB name)
   */
  private String versionName;
  /**
   * Description
   */
  private String description;
  /**
   * Response Version Status
   */
  private String qnaireRespVersStatus;
  /**
   * Rev Num
   */
  private Long revNum;
  /**
   * reviewed user
   */
  private String reviewedUser;
  /**
   * reviewed date
   */
  private String reviewedDate;

  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
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
   * @return qnaireRespId
   */
  public Long getQnaireRespId() {
    return this.qnaireRespId;
  }

  /**
   * @param qnaireRespId set qnaireRespId
   */
  public void setQnaireRespId(final Long qnaireRespId) {
    this.qnaireRespId = qnaireRespId;
  }

  /**
   * @return name
   */

  @Override
  public String getName() {
    return this.name;
  }

  /**
   * @param name set name
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return description
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description set description
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
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
  public RvwQnaireRespVersion clone() {
    try {
      return (RvwQnaireRespVersion) super.clone();
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
  public int compareTo(final RvwQnaireRespVersion respVers) {
    Long thisRevNo = getRevNum();
    if (thisRevNo != 0) {
      thisRevNo = 100000000 - thisRevNo;
    }

    long otherRevNo = respVers.getRevNum();
    if (otherRevNo != 0) {
      otherRevNo = 100000000 - otherRevNo;
    }

    int compareResult = thisRevNo.compareTo(otherRevNo);

    if (compareResult == 0) {
      compareResult = getId().compareTo(respVers.getId());
    }
    return compareResult;
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
    if ((getId() == -1l) || ((obj.getClass() == this.getClass()) && (((RvwQnaireRespVersion) obj).getId() == -1l))) {
      return false;
    }
    if ((obj.getClass() == this.getClass())) {
      return ModelUtil.isEqual(getId(), ((RvwQnaireRespVersion) obj).getId());
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
   * @return the verName
   */
  public String getVersionName() {
    return this.versionName;
  }


  /**
   * @param verName the verName to set
   */
  public void setVersionName(final String verName) {
    this.versionName = verName;
  }


  /**
   * @return the qnaireVersionId
   */
  public Long getQnaireVersionId() {
    return this.qnaireVersionId;
  }


  /**
   * @param qnaireVersionId the qnaireVersionId to set
   */
  public void setQnaireVersionId(final Long qnaireVersionId) {
    this.qnaireVersionId = qnaireVersionId;
  }


  /**
   * @return the qnaireRespVersStatus
   */
  public String getQnaireRespVersStatus() {
    return this.qnaireRespVersStatus;
  }


  /**
   * @param qnaireRespVersStatus the qnaireRespVersStatus to set
   */
  public void setQnaireRespVersStatus(final String qnaireRespVersStatus) {
    this.qnaireRespVersStatus = qnaireRespVersStatus;
  }


  /**
   * @return the reviewedUser
   */
  public String getReviewedUser() {
    return this.reviewedUser;
  }


  /**
   * @param reviewedUser the reviewedUser to set
   */
  public void setReviewedUser(final String reviewedUser) {
    this.reviewedUser = reviewedUser;
  }


  /**
   * @return the reviewedDate
   */
  public String getReviewedDate() {
    return this.reviewedDate;
  }


  /**
   * @param reviewedDate the reviewedDate to set
   */
  public void setReviewedDate(final String reviewedDate) {
    this.reviewedDate = reviewedDate;
  }

}
