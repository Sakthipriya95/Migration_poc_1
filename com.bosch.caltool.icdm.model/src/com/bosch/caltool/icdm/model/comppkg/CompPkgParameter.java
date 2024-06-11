/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comppkg;

import com.bosch.caltool.icdm.model.cdr.AbstractParameter;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmr1cob
 */
public class CompPkgParameter extends AbstractParameter implements Comparable<CompPkgParameter> {

  /**
   *
   */
  private static final long serialVersionUID = 8333046215562841567L;
  private Long id;
  private String name;
  private String description;
  private Long version;
  private String createdDate;
  private String createdUser;
  private String modifiedUser;
  private String modifiedDate;
  private String pClassText;
  private String custPrm;
  private CompPackage compPackage;


  /**
   * @return the id
   */
  public Long getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
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
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final Long version) {
    this.version = version;
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
   * @return the custPrm
   */
  public String getCustPrm() {
    return this.custPrm;
  }


  /**
   * @param custPrm the custPrm to set
   */
  public void setCustPrm(final String custPrm) {
    this.custPrm = custPrm;
  }

  /**
   * @return the compPackage
   */
  public CompPackage getCompPackage() {
    return this.compPackage;
  }


  /**
   * @param compPackage the compPackage to set
   */
  public void setCompPackage(final CompPackage compPackage) {
    this.compPackage = compPackage;
  }


  /**
   * @return the pClassText
   */
  public String getpClassText() {
    return this.pClassText;
  }

  /**
   * @param pClassText the pClassText to set
   */
  public void setpClassText(final String pClassText) {
    this.pClassText = pClassText;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompPkgParameter other) {
    return ModelUtil.compare(getId(), other.getId());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    // If the object is not saved in the database then adding to set has problems
    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(getId(), ((CompPkgParamAttr) obj).getId()) &&
          ModelUtil.isEqual(getName(), ((CompPkgParamAttr) obj).getName());
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

}
