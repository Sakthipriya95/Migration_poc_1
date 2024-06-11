/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.filter;


import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.CloseableThreadContext;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.dmframework.bo.ServiceData;

/**
 * @author rgo7cob
 */
public class InitializationFilter implements javax.servlet.Filter {


  // Method to filter the incoming request
  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filter)
      throws IOException, ServletException {

    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpReqst = (HttpServletRequest) request;
      // Get the Http request methid id
      String methodId = httpReqst.getMethod() + httpReqst.getPathInfo().replace("/", ".");

      String requestId = UUID.randomUUID().toString();

      HttpServletResponse httpResp = (HttpServletResponse) response;
      // Set the reequest id to the response header
      httpResp.addHeader(WsCommonConstants.RESP_HEADER_SERVICE_ID, requestId);

      try (
          CloseableThreadContext.Instance loggerThrdCtxt =
              CloseableThreadContext.put("method", methodId).put("requestId", requestId);
          ServiceData serviceData = new ServiceData();) {

        long startTime = System.currentTimeMillis();

        initializeServiceData(httpReqst, serviceData, requestId);

        // Log beginning of service
        getLogger().info("Starting service {}. Request ID : {}, CNS Session ID : {}", methodId, requestId,
            serviceData.getCnsSessionId());
        // Executes service
        filter.doFilter(request, httpResp);

        // Log end of service
        getLogger().info("Service completed {}. Request ID : {}. Time Taken = {}", methodId, requestId,
            System.currentTimeMillis() - startTime);

      }

    }
  }

  /**
   * Initializes the given service data and sets it to request object
   *
   * @param httpReqst HttpServletRequest
   * @param sData Service Data
   */
  private void initializeServiceData(final HttpServletRequest httpReqst, final ServiceData sData,
      final String requestId) {
    sData.setLanguage(httpReqst.getHeader(WsCommonConstants.REQ_LANGUAGE));
    // Set the timezone to client timezone, if found.
    sData.setTimezone(httpReqst.getHeader(WsCommonConstants.REQ_TIMEZONE));
    sData.setServiceId(requestId);
    sData.setCnsSessionId(httpReqst.getHeader(WsCommonConstants.REQ_CNS_SESSION_ID));

    httpReqst.setAttribute(WsCommonConstants.RWS_REQUEST_ATTR_COMMON_SER_DATA, sData);
  }


  /**
   * {@inheritDoc}
   */
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

  private ILoggerAdapter getLogger() {
    return WSObjectStore.getLogger();
  }


}