/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.pidc;

import com.bosch.caltool.apic.ws.LevelAttrInfo;
import com.bosch.caltool.apic.ws.ProjectIdCardVersInfoType;
import com.bosch.caltool.apic.ws.db.IWebServiceResponse;
import com.bosch.caltool.icdm.common.exception.IcdmException;


/**
 * @author imi2si
 */
public abstract class AbstractPidc implements IWebServiceResponse {

  /**
   *
   */
  protected long pidcId;

  /**
   *
   */
  protected long pidVersionID;


  private ProjectIdCardVersInfoType wsResponse;

  /**
   * constructor
   */
  public AbstractPidc() {
    // Empty constructor

  }

  /**
   * @param pidcId pidc Id
   */
  public AbstractPidc(final long pidcId) {
    this.pidcId = pidcId;
  }

  /**
   * {@inheritDoc}
   *
   * @throws IcdmException error during webservice call
   */
  @Override
  public void createWsResponse() throws IcdmException {
    this.wsResponse = fetchPidc(this.pidcId);
    this.wsResponse.setLevelAttrInfoList(fetchLevelAttrInfo(this.pidcId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProjectIdCardVersInfoType getWsResponse() {
    return this.wsResponse;
  }

  protected abstract ProjectIdCardVersInfoType fetchPidc(final long pidcId) throws IcdmException;

  protected abstract LevelAttrInfo[] fetchLevelAttrInfo(final long pidcId) throws IcdmException;

  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final long pidcId) {
    this.pidcId = pidcId;
  }

  /**
   * @return the pidcId
   */
  public long getPidcId() {
    return this.pidcId;
  }

  /**
   * @return the pidVersionID
   */
  public long getPidVersionID() {
    return this.pidVersionID;
  }


  /**
   * @param pidVersionID the pidVersionID to set
   */
  public void setPidVersionID(final long pidVersionID) {
    this.pidVersionID = pidVersionID;
  }
}
