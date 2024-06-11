/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.interceptor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
import com.bosch.caltool.icdm.cns.server.annotations.CompressData;
import com.bosch.caltool.icdm.cns.server.utils.Utils;


/**
 * Enables compression of response using GZIP
 *
 * @author bne4cob
 */
@Provider
@CompressData
public class GZIPCompressionInterceptor implements WriterInterceptor {

  @Context
  private HttpServletRequest request;

  /**
   * Compress the web service response
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void aroundWriteTo(final WriterInterceptorContext wiCntxt) throws IOException {
    String acceptEncHdr = this.request.getHeader(HttpHeaders.ACCEPT_ENCODING);
    boolean gzip = !Utils.isEmpty(acceptEncHdr) &&
        Arrays.asList(acceptEncHdr.split(",")).contains(CnsCommonConstants.ENCODING_GZIP);

    if (gzip) {
      // Add the information to response header that the data is compressed in GZIP format. This information is used by
      // browsers like Mozilla and other clients to automatically decompress the data to normal output.
      MultivaluedMap<String, Object> headers = wiCntxt.getHeaders();
      headers.add(HttpHeaders.CONTENT_ENCODING, CnsCommonConstants.ENCODING_GZIP);

      // Use GZIPOutputStream to compress the data
      final OutputStream opStream = wiCntxt.getOutputStream();
      wiCntxt.setOutputStream(new GZIPOutputStream(opStream));
    }

    wiCntxt.proceed();
  }
}
