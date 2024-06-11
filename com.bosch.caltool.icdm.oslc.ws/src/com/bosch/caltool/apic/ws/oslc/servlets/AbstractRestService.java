/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.servlets;

import javax.servlet.http.HttpServletRequest;

import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandExecuter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;

/**
 * @author rgo7cob
 */
public abstract class AbstractRestService {

  /**
   * @param httpServletRequest httpServletRequest
   * @return the apic User from the request
   */
  public ApicUser getApicUserFromRequest(final HttpServletRequest httpServletRequest) {

    ApicUser userFromRequest = null;

    Object apicUserAttr = httpServletRequest.getAttribute(WebServiceConstants.RWS_APIC_USER_REQUEST_ATTR);

    if (apicUserAttr instanceof ApicUser) {
      userFromRequest = (ApicUser) apicUserAttr;
    }
    return userFromRequest;
  }

  /**
   * @param request httpServletRequest
   * @return the apic User from the request
   * @throws UnAuthorizedAccessException when there are not sufficient access rights to perform the operation
   */
  protected ServiceData getServiceData(final HttpServletRequest request) throws UnAuthorizedAccessException {

    String reqMeth = request.getMethod();
    if (("PUT".equals(reqMeth) || "POST".equals(reqMeth) || "DELETE".equals(reqMeth)) &&
        (request.getAttribute(WebServiceConstants.RWS_APIC_USER_REQUEST_ATTR) == null)) {
      // Should be a valid user to perform the data change operations
      throw new UnAuthorizedAccessException("Insufficient privileges to do this operation");
    }

    return (ServiceData) request.getAttribute(WsCommonConstants.RWS_REQUEST_ATTR_COMMON_SER_DATA);

  }

  /**
   * @param command to execute
   * @throws IcdmException error in executing command
   */
  protected final void executeCommand(final AbstractCommand<?, ?> command) throws IcdmException {
    CommandExecuter cmdExecute = new CommandExecuter(command.getServiceData());
    cmdExecute.execute(command);
  }
}
