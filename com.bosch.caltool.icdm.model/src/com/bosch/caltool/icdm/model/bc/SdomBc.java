package com.bosch.caltool.icdm.model.bc;

import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * SdomBc Model class
 *
 * @author say8cob
 */
public class SdomBc implements Comparable<SdomBc> {

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
  private String bcClass;
  /**
   * Description
   */
  private String description;
  /**
   * Lifecycle State
   */
  private String lifecycleState;

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
  public String getBcClass() {
    return this.bcClass;
  }

  /**
   * @param class set class
   */
  public void setBcClass(final String bcClass) {
    this.bcClass = bcClass;
  }

  /**
   * @return description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description set description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return lifecycleState
   */
  public String getLifecycleState() {
    return this.lifecycleState;
  }

  /**
   * @param lifecycleState set lifecycleState
   */
  public void setLifecycleState(final String lifecycleState) {
    this.lifecycleState = lifecycleState;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final SdomBc object) {
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
    SdomBc other = (SdomBc) obj;
    return getName().equals(other.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getName());
  }

}
