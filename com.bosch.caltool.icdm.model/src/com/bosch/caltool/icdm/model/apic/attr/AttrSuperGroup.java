/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.util.Date;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author bne4cob
 */
public class AttrSuperGroup implements IModel, Comparable<AttrSuperGroup> {

  /**
   *
   */
  private static final long serialVersionUID = -3404404676421514398L;

  private Long id;

  private String name;
  private String nameEng;
  private String nameGer;
  private String description;
  private String descriptionEng;
  private String descriptionGer;
  private Date createdDate;
  private String createdUser;
  private Date modifiedDate;
  private String modifiedUser;
  private boolean deleted;
  private static final int OBJ_EQUAL_CHK_VAL = 0;
  private Long version;


  /**
   * @return the id
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }


  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the nameEng
   */
  public String getNameEng() {
    return this.nameEng;
  }


  /**
   * @param nameEng the nameEng to set
   */
  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }


  /**
   * @return the nameGer
   */
  public String getNameGer() {
    return this.nameGer;
  }


  /**
   * @param nameGer the nameGer to set
   */
  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the descriptionEng
   */
  public String getDescriptionEng() {
    return this.descriptionEng;
  }


  /**
   * @param descriptionEng the descriptionEng to set
   */
  public void setDescriptionEng(final String descriptionEng) {
    this.descriptionEng = descriptionEng;
  }


  /**
   * @return the descriptionGer
   */
  public String getDescriptionGer() {
    return this.descriptionGer;
  }


  /**
   * @param descriptionGer the descriptionGer to set
   */
  public void setDescriptionGer(final String descriptionGer) {
    this.descriptionGer = descriptionGer;
  }


  /**
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
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
   * @return the modifiedDate
   */
  public Date getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Date modifiedDate) {
    this.modifiedDate = modifiedDate;
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
   * @return the deleted
   */
  public boolean isDeleted() {
    return this.deleted;
  }


  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * @return the version
   */
  @Override
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttrSuperGroup grp) {

    int compResult = ModelUtil.compare(getName(), grp.getName());
    // If name is same, then compare by attribute id
    if (compResult == OBJ_EQUAL_CHK_VAL) {
      return ModelUtil.compare(getId(), grp.getId());
    }
    return compResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AttrSuperGroup clone() {
    AttrSuperGroup superGrp = null;
    try {
      superGrp = (AttrSuperGroup) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return superGrp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return ModelUtil.isEqual(getId(), ((AttrSuperGroup) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
