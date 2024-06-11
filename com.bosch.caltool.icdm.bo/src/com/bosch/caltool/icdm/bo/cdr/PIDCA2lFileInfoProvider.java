/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * @author say8cob
 */
public class PIDCA2lFileInfoProvider extends AbstractSimpleBusinessObject {

  private final PidcA2l pidcA2l;

  private final A2LFile a2lFile;

  private final A2LFileInfo a2lFileInfo;


  /**
   * @param serviceData serviceData
   * @param pidcA2lId pidcA2lId
   * @throws IcdmException error while loading data during initialization
   */
  public PIDCA2lFileInfoProvider(final ServiceData serviceData, final Long pidcA2lId) throws IcdmException {
    super(serviceData);
    this.pidcA2l = new PidcA2lLoader(serviceData).getDataObjectByID(pidcA2lId);
    this.a2lFile = new A2LFileInfoLoader(serviceData).getDataObjectByID(this.pidcA2l.getA2lFileId());
    this.a2lFileInfo = new A2LFileInfoProvider(serviceData).fetchA2LFileInfo(this.a2lFile);
  }


  /**
   * @return the pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * @return the a2lFile
   */
  public A2LFile getA2lFile() {
    return this.a2lFile;
  }


  /**
   * @return the a2lFileInfo
   */
  public A2LFileInfo getA2lFileInfo() {
    return this.a2lFileInfo;
  }


}
