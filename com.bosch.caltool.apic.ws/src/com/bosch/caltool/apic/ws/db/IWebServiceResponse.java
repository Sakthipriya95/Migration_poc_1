/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.db;

import java.io.IOException;

import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author imi2si
 */
public interface IWebServiceResponse {

  /**
   * @throws IcdmException Error during webservice call
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public void createWsResponse() throws IcdmException, ClassNotFoundException, IOException;

  public Object getWsResponse();
}
