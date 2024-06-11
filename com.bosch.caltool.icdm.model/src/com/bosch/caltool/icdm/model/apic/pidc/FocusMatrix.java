package com.bosch.caltool.icdm.model.apic.pidc;


import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Focus Matrix Model class
 *
 * @author MKL2COB
 */
public class FocusMatrix implements Cloneable, Comparable<FocusMatrix>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = -2513659356835186894L;
  /**
   * Fm Id
   */
  private Long id;
  /**
   * Ucpa Id
   */
  private Long ucpaId;
  /**
   * Color Code
   */
  private String colorCode;
  /**
   * Comments
   */
  private String comments;
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
   * Link
   */
  private String link;
  /**
   * Use Case Id
   */
  private Long useCaseId;
  /**
   * Section Id
   */
  private Long sectionId;
  /**
   * Attr Id
   */
  private Long attrId;
  /**
   * Is Deleted
   */
  private boolean isDeleted;
  /**
   * Fm Vers Id
   */
  private Long fmVersId;

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
   * @return ucpaId
   */
  public Long getUcpaId() {
    return this.ucpaId;
  }

  /**
   * @param ucpaId set ucpaId
   */
  public void setUcpaId(final Long ucpaId) {
    this.ucpaId = ucpaId;
  }

  /**
   * @return colorCode
   */
  public String getColorCode() {
    return this.colorCode;
  }

  /**
   * @param colorCode set colorCode
   */
  public void setColorCode(final String colorCode) {
    this.colorCode = colorCode;
  }

  /**
   * @return comments
   */
  public String getComments() {
    return this.comments;
  }

  /**
   * @param comments set comments
   */
  public void setComments(final String comments) {
    this.comments = comments;
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
   * @return useCaseId
   */
  public Long getUseCaseId() {
    return this.useCaseId;
  }

  /**
   * @param useCaseId set useCaseId
   */
  public void setUseCaseId(final Long useCaseId) {
    this.useCaseId = useCaseId;
  }

  /**
   * @return sectionId
   */
  public Long getSectionId() {
    return this.sectionId;
  }

  /**
   * @param sectionId set sectionId
   */
  public void setSectionId(final Long sectionId) {
    this.sectionId = sectionId;
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
   * @return isDeleted
   */
  public boolean getIsDeleted() {
    return this.isDeleted;
  }

  /**
   * @param isDeleted set isDeleted
   */
  public void setIsDeleted(final boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  /**
   * @return fmVersId
   */
  public Long getFmVersId() {
    return this.fmVersId;
  }

  /**
   * @param fmVersId set fmVersId
   */
  public void setFmVersId(final Long fmVersId) {
    this.fmVersId = fmVersId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FocusMatrix clone() {
    FocusMatrix fMatrix = null;
    try {
      fMatrix = (FocusMatrix) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return fMatrix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FocusMatrix object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((FocusMatrix) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
