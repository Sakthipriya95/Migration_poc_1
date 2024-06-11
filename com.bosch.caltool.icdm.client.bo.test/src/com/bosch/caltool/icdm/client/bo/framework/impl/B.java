package com.bosch.caltool.icdm.client.bo.framework.impl;

import com.bosch.caltool.datamodel.core.IModel;

public class B implements IModel {

  private Long objId;

  private Long version;

  private String name;

  private String desc;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.objId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.objId = objId;
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
   * @return the desc
   */
  public String getDesc() {
    return this.desc;
  }

  /**
   * @param desc the desc to set
   */
  public void setDesc(final String desc) {
    this.desc = desc;
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

}