/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.a2l.A2LFile;

/**
 * @author gge6cob
 */
public class PidcA2lFileExt implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = 6534751572963048010L;
  PidcA2l pidcA2l;
  A2LFile a2lFile;
  PidcVersion pidcVersion;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return (this.pidcA2l == null) ? 0l : this.pidcA2l.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    if (null != this.pidcA2l) {
      this.pidcA2l.setId(objId);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.pidcA2l == null ? 0 : this.pidcA2l.getVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    if (null != this.pidcA2l) {
      this.pidcA2l.setVersion(version);
    }

  }


  /**
   * @return the pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final PidcA2l pidcA2l) {
    this.pidcA2l = pidcA2l;
  }


  /**
   * @return the a2lFile
   */
  public A2LFile getA2lFile() {
    return this.a2lFile;
  }


  /**
   * @param a2lFile the a2lFile to set
   */
  public void setA2lFile(final A2LFile a2lFile) {
    this.a2lFile = a2lFile;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

}
