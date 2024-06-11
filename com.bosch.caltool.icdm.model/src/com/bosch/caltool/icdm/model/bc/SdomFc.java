package com.bosch.caltool.icdm.model.bc;

import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * SdomFc Model class
 *
 * @author say8cob
 */
public class SdomFc implements Comparable<SdomFc> {

  /**
   * Id
   */
  private Long id;
  /**
   * Name
   */
  private String name;
  /**
   * Variant
   */
  private String variant;
  /**
   * Revision
   */
  private String revision;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Class
   */
  private String fcClass;

  private String description;

  /**
   */
  public SdomFc() {

  }

  /**
   * @param fcName fcName
   * @param fcDescription description
   */
  public SdomFc(final String fcName, final String fcDescription) {
    this.name = fcName;
    this.description = fcDescription;
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
   * {@inheritDoc}
   */
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  public void setId(final Long id) {
    this.id = id;
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
   * @return variant
   */
  public String getVariant() {
    return this.variant;
  }

  /**
   * @param variant set variant
   */
  public void setVariant(final String variant) {
    this.variant = variant;
  }

  /**
   * @return revision
   */
  public String getRevision() {
    return this.revision;
  }

  /**
   * @param revision set revision
   */
  public void setRevision(final String revision) {
    this.revision = revision;
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
   * @return class
   */
  public String getFCClass() {
    return this.fcClass;
  }

  /**
   * @param class set class
   */
  public void setFCClass(final String fcClass) {
    this.fcClass = fcClass;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final SdomFc object) {
    return ModelUtil.compare(getName(), object.getName());
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
    SdomFc other = (SdomFc) obj;
    return ModelUtil.isEqual(getName(), other.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getName());
  }

}
