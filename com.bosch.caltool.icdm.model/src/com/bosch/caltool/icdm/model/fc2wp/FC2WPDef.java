/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.fc2wp;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bne4cob
 */
public class FC2WPDef implements IModel, Comparable<FC2WPDef> {

  /**
   *
   */
  private static final long serialVersionUID = -3252966277529881199L;
  private Long id;
  private String name;
  private String nameEng;
  private String nameGer;
  private String description;
  private String descriptionEng;
  private String descriptionGer;

  private boolean relvForQnaire;

  private String divisionName;
  private Long divisionValId;


  /**
   * ID of reference FC-WP Definition, if new definition is created as a copy of another one.
   */
  private Long refFcwpDefId;

  private Long version;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * Columns for advanced sorting
   */
  public enum SortColumns {
                           /**
                            * fc2wp name
                            */
                           SORT_NAME,
                           /**
                            * division Name
                            */
                           SORT_DIV_NAME

  }


  /**
   * @return the divisionName
   */
  public String getDivisionName() {
    return this.divisionName;
  }


  /**
   * @param divisionName the divisionName to set
   */
  public void setDivisionName(final String divisionName) {
    this.divisionName = divisionName;
  }


  /**
   * @return the divisionValId
   */
  public Long getDivisionValId() {
    return this.divisionValId;
  }


  /**
   * @param divisionValId the divisionValId to set
   */
  public void setDivisionValId(final Long divisionValId) {
    this.divisionValId = divisionValId;
  }


  /**
   * @return the fcwpDefId
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @param fcwpDefId the fcwpDefId to set
   */
  @Override
  public void setId(final Long fcwpDefId) {
    this.id = fcwpDefId;
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
   * @return the relvForQnaire
   */
  public boolean isRelvForQnaire() {
    return this.relvForQnaire;
  }


  /**
   * @param relvForQnaire the relvForQnaire to set
   */
  public void setRelvForQnaire(final boolean relvForQnaire) {
    this.relvForQnaire = relvForQnaire;
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
   * @return the refFcwpDefId
   */
  public Long getRefFcwpDefId() {
    return this.refFcwpDefId;
  }


  /**
   * @param refFcwpDefId the refFcwpDefId to set
   */
  public void setRefFcwpDefId(final Long refFcwpDefId) {
    this.refFcwpDefId = refFcwpDefId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "FC2WPDef [fcwpDefId=" + this.id + ", name=" + this.name + ", nameEng=" + this.nameEng + ", nameGer=" +
        this.nameGer + ", description=" + this.description + ", descriptionEng=" + this.descriptionEng +
        ", descriptionGer=" + this.descriptionGer + ", relvForQnaire=" + this.relvForQnaire + ", divisionName=" +
        this.divisionName + ", divisionValId=" + this.divisionValId + ", version=" + this.version + "]";
  }

  /**
   * Compares this fc2wp with another fc2wp based on column to sort
   *
   * @param fc2wp2 the second fc2wp
   * @param sortColumn column selected for sorting
   * @return the int value based on String.compare() method
   */
  public int compareTo(final FC2WPDef fc2wp2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_NAME:
        // compare the user IDs
        compareResult = ModelUtil.compare(getName(), fc2wp2.getName());
        break;

      case SORT_DIV_NAME:
        // compare the first names
        compareResult = ModelUtil.compare(getDivisionName(), fc2wp2.getDivisionName());
        break;


      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the last name
      compareResult = ModelUtil.compare(getDivisionName(), fc2wp2.getDivisionName());
    }

    return compareResult;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FC2WPDef fc2wp2) {
    return ModelUtil.compare(getName(), fc2wp2.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getName() == null) ? 0 : getName().hashCode());
    return result;
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
    FC2WPDef other = (FC2WPDef) obj;
    return getName().equals(other.getName());
  }
}
