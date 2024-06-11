package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2lSyscon Model class
 *
 * @author pdh2cob
 */
public class A2lSysconst implements Comparable<A2lSysconst>, IModel {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 68687200744764L;
  /**
   * Syscon Id
   */
  private Long id;
  /**
   * Name
   */
  private String name;
  /**
   * Value
   */
  private String value;
  /**
   * Module Id
   */
  private Long moduleId;

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
   * @return value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * @param value set value
   */
  public void setValue(final String value) {
    this.value = value;
  }

  /**
   * @return moduleId
   */
  public Long getModuleId() {
    return this.moduleId;
  }

  /**
   * @param moduleId set moduleId
   */
  public void setModuleId(final Long moduleId) {
    this.moduleId = moduleId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lSysconst object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lSysconst) obj).getId());
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
  public Long getVersion() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // TODO Auto-generated method stub

  }

}
