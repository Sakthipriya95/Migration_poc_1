/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.filter;


import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.CloseableThreadContext;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;
import com.bosch.caltool.icdm.cns.server.bo.CnsServiceData;
import com.bosch.caltool.icdm.cns.server.bo.ServerConstants;

/**
 * @author rgo7cob
 */
public class CnsInitializationFilter implements javax.servlet.Filter {

  // Method to filter the incoming request
  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filter)
      throws IOException, ServletException {

    if (request instanceof HttpServletRequest) {

      HttpServletRequest httpReqst = (HttpServletRequest) request;

      CnsServiceData serviceData = new CnsServiceData();
      serviceData.setTimeZone(httpReqst.getHeader(CnsCommonConstants.REQHDR_TIMEZONE));
      httpReqst.setAttribute(ServerConstants.HTTPATTR_SERVICE_DATA, serviceData);

      String methodId = httpReqst.getMethod() + httpReqst.getPathInfo().replace("/", ".");
      String requestId = UUID.randomUUID().toString();

      try (CloseableThreadContext.Instance loggerThrdCtxt =
          CloseableThreadContext.put("method", methodId).put("requestId", requestId);) {

        long startTime = System.currentTimeMillis();

        // Log beginning of service
        getLogger().info("Starting service {}. Request ID : {}", methodId, requestId);

        // Executes service
        filter.doFilter(request, response);

        // Log end of service
        getLogger().info("Service completed {}. Request ID : {}. Time Taken = {}", methodId, requestId,
            System.currentTimeMillis() - startTime);

      }

    }
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
    return CnsObjectStore.getLogger();
  }


}