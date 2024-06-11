/**
 * PwdWsSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.6.2 Built on : Apr 17, 2012
 * (05:33:49 IST)
 */
package com.bosch.caltool.pwdservice.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;

import com.bosch.caltool.pwdservice.PasswordFault;
import com.bosch.caltool.pwdservice.PasswordFaultException;
import com.bosch.caltool.pwdservice.PasswordRequest;
import com.bosch.caltool.pwdservice.PasswordResponse;
import com.bosch.caltool.pwdservice.PasswordResponseType;
import com.bosch.caltool.pwdservice.PwdWsSkeletonInterface;
import com.bosch.caltool.pwdservice.util.PasswordReader;

/**
 * PwdWsSkeleton java skeleton for the axisService
 */
public class PasswordService implements PwdWsSkeletonInterface {


  /**
   * {@inheritDoc}
   */
  @Override
  public PasswordResponse getPassword(final PasswordRequest passwordRequest) throws PasswordFaultException {
    String passwordName = passwordRequest.getPasswordRequest().getPasswordName();

    
    PasswordResponse response = new PasswordResponse();
    PasswordResponseType responseType = new PasswordResponseType();
    response.setPasswordResponse(responseType);
    String password = PasswordReader.getPassword(passwordName);
    if (null != password && !password.isEmpty()) {
      responseType.setPassword(password);
    }
    else {
      PasswordFaultException exception =
          new PasswordFaultException("PasswordName is not valid. Please Check");
      PasswordFault fault = new PasswordFault();
      fault.setPasswordFault("PasswordName is not valid. Please Check");

      throw exception;
    }

    MessageContext context = MessageContext.getCurrentMessageContext();
    HttpServletRequest req = (HttpServletRequest) context.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
    System.out.println(req.getRemoteUser());
    return response;
  }

}
