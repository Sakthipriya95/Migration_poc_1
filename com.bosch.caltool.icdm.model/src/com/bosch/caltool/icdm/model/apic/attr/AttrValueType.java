package com.bosch.caltool.icdm.model.apic.attr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * AttrValueType Model class
 *
 * @author dmo5cob
 */
public class AttrValueType implements Comparable<AttrValueType>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = -923370617804907286L;
  /**
   * Value Type Id
   */
  private Long id;
  /**
   * Value Type
   */
  private String valueType;

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
   * @return valueType
   */
  public String getValueType() {
    return this.valueType;
  }

  /**
   * @param valueType set valueType
   */
  public void setValueType(final String valueType) {
    this.valueType = valueType;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttrValueType object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((AttrValueType) obj).getId());
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
