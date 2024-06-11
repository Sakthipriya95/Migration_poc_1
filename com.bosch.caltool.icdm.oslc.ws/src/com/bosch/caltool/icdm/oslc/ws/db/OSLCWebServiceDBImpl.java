/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.oslc.ws.db;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.jpa.CDMDataProvider;
import com.bosch.caltool.icdm.jpa.CDMSession;

/**
 * @author mkl2cob
 */
public class OSLCWebServiceDBImpl {

  // the logger
  private final ILoggerAdapter logger;


  // the attributes data provider
  private ApicDataProvider attrDataProvider;

  /**
   * @param logger2 ILoggerAdapter
   * @throws IcdmException ICDMException
   */
  public OSLCWebServiceDBImpl(final ILoggerAdapter logger2) throws IcdmException {
    super();

    this.logger = logger2;

    initJPA();

  }

  private void initJPA() throws IcdmException {

    this.logger.info("initializing database connection ...");
    CDMSession.getInstance().setProductVersion("1.17.0");
    CDMSession.getInstance().initialize("OSLCWebService");

    this.logger.info("getting the data provider ...");
    this.attrDataProvider = CDMDataProvider.getInstance().getApicDataProvider();

  }

  /**
   * @return ApicDataProvider
   */
  public ApicDataProvider getAttrDataProvider() {
    return this.attrDataProvider;
  }

}
