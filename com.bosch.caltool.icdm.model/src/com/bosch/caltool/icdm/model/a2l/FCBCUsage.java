/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IModel;

/**
 * @author apj4cob
 */
public class FCBCUsage implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = 6901501139499843632L;
  private String name;
  private String funcVersion;
  private String customerName;
  private String vcdmAprj;
  private String createdUser;

  private String vcdmAprjId;

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
   * @return the version
   */
  public String getFuncVersion() {
    return this.funcVersion;
  }

  /**
   * @param version the version to set
   */
  public void setFuncVersion(final String version) {
    this.funcVersion = version;
  }

  /**
   * @return the customerName
   */
  public String getCustomerName() {
    return this.customerName;
  }

  /**
   * @param customerName the customerName to set
   */
  public void setCustomerName(final String customerName) {
    this.customerName = customerName;
  }

  /**
   * @return the vcdmAprj
   */
  public String getVcdmAprj() {
    return this.vcdmAprj;
  }

  /**
   * @param vcdmAprj the vcdmAprj to set
   */
  public void setVcdmAprj(final String vcdmAprj) {
    this.vcdmAprj = vcdmAprj;
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
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    // Id to be set
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // version to be set
  }


  /**
   * @return the vcdmAprjId
   */
  public String getVcdmAprjId() {
    return this.vcdmAprjId;
  }


  /**
   * @param vcdmAprjId the vcdmAprjId to set
   */
  public void setVcdmAprjId(final String vcdmAprjId) {
    this.vcdmAprjId = vcdmAprjId;
  }
}
