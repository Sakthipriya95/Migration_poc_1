/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.ApicUser;

@Deprecated

/**
 * POJO for representing FC/BC usage details retreived from DB
 *
 * @author jvi6cob
 */
public class FCBCUsage {

  private String name;
  private String version;
  private String customerName;
  private String vcdmAprj;
  private String createdUser;
  private final ApicDataProvider apicDataProvider;// ICDM 531

  /**
   * @param apicDataProvider Apicdataprovider instance
   */
  public FCBCUsage(final ApicDataProvider apicDataProvider) {
    this.apicDataProvider = apicDataProvider;
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
   * @return the version
   */
  public String getVersion() {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(final String version) {
    this.version = version;
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
    // ICDM 531
    final ApicUser apicUser = getApicDataProvider().getApicUser(this.createdUser);
    if (apicUser != null) {
      return apicUser.getDisplayName();
    }

    return this.createdUser;

  }

  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the apicDataProvider
   */
  public ApicDataProvider getApicDataProvider() {
    return this.apicDataProvider;
  }

}
