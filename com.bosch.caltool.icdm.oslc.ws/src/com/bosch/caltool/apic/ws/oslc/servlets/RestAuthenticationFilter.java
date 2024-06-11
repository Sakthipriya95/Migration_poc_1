/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.servlets;


import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.CloseableThreadContext;

import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.oslc.ws.db.OSLCObjectStore;

/**
 * @author rgo7cob
 */
public class RestAuthenticationFilter implements javax.servlet.Filter {

  // Method to filter the incoming request
  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filter)
      throws IOException, ServletException {

    if (request instanceof HttpServletRequest) {

      HttpServletRequest httpReqst = (HttpServletRequest) request;

      String method = httpReqst.getMethod() + httpReqst.getPathInfo().replace("/", ".");
      String requestId = UUID.randomUUID().toString();

      try (
          CloseableThreadContext.Instance loggerThrdCtxt =
              CloseableThreadContext.put("method", method).put("requestId", requestId);
          ServiceData serviceData = new ServiceData();) {


        String userName = httpReqst.getHeader(WebServiceConstants.USER_NAME_REQ);
        String passWord = httpReqst.getHeader(WebServiceConstants.PASSWRD_REQ);

        ApicDataProvider attrDataProvider = OSLCObjectStore.getOslcWebServiceDBImpl().getAttrDataProvider();
        ApicUser apicUser = attrDataProvider.getApicUser(userName, passWord);
        // TODO remove apic user object usage, as it is not threadsafe. Use ServiceData.getUsername() instead
        httpReqst.setAttribute(WebServiceConstants.RWS_APIC_USER_REQUEST_ATTR, apicUser);

        serviceData.setUsername(userName);
        serviceData.setPassword(passWord);
        serviceData.setLanguage(httpReqst.getHeader(WsCommonConstants.REQ_LANGUAGE));
        // Set the timezone to client timezone, if found.
        serviceData.setTimezone(httpReqst.getHeader(WsCommonConstants.REQ_TIMEZONE));

        UserLoader userLdr = new UserLoader(serviceData);
        userLdr.authenticateCurrentUser();

        httpReqst.setAttribute(WsCommonConstants.RWS_REQUEST_ATTR_COMMON_SER_DATA, serviceData);

        // Executes service
        filter.doFilter(request, response);
      }

    }
  }

  @Override
  public void destroy() {
    // No implementation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final FilterConfig arg0) throws ServletException {
    // No implementation
  }

}