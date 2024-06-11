/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;
import com.bosch.caltool.icdm.cns.server.bo.CnsServiceData;
import com.bosch.caltool.icdm.cns.server.bo.ServerConstants;

/**
 * @author bne4cob
 */
public abstract class AbstractRestService {

  @Context
  private HttpServletRequest request;


  /**
   * @return HTTP request
   */
  protected final HttpServletRequest getRequest() {
    return this.request;
  }

  /**
   * @return session ID
   */
  protected final String getClientSessionIdFromRequest() {
    String reqSessId = this.request.getHeader(CnsCommonConstants.REQ_SESSION_ID);
    return (reqSessId == null) || reqSessId.isEmpty() ? CnsCommonConstants.DEFAULT_SESSION_ID : reqSessId;
  }

  /**
   * @return client IP
   */
  protected final String getClientIp() {
    return this.request.getRemoteAddr();
  }

  /**
   * @return client IP
   */
  protected final String getClientUser() {
    return this.request.getHeader(CnsCommonConstants.REQ_USER_ID);
  }

  /**
   * @return logger
   */
  protected ILoggerAdapter getLogger() {
    return CnsObjectStore.getLogger();
  }

  /**
   * @return Service Data
   */
  protected final CnsServiceData getServiceData() {
    return (CnsServiceData) this.request.getAttribute(ServerConstants.HTTPATTR_SERVICE_DATA);
  }

}
