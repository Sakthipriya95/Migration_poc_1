package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * PidcDetailsStructure Model class
 *
 * @author pdh2cob
 */
public class PidcDetStructure implements Comparable<PidcDetStructure>, IModel {

  /**
  *
  */
  private static final long serialVersionUID = 4261654846244543887L;
  /**
   * Pds Id
   */
  private Long id;
  /**
   * Attr Id
   */
  private Long attrId;
  /**
   * Pid Attr Level
   */
  private Long pidAttrLevel;
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
   * Pidc Vers Id
   */
  private Long pidcVersId;

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
   * @return pidAttrLevel
   */
  public Long getPidAttrLevel() {
    return this.pidAttrLevel;
  }

  /**
   * @param pidAttrLevel set pidAttrLevel
   */
  public void setPidAttrLevel(final Long pidAttrLevel) {
    this.pidAttrLevel = pidAttrLevel;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcDetStructure object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((PidcDetStructure) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
