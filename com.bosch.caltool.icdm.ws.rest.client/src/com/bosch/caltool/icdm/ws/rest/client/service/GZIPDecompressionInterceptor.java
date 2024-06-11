package com.bosch.caltool.icdm.ws.rest.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;

/**
 * Reader interceptor to de-compress data
 *
 * @author bne4cob
 */
public class GZIPDecompressionInterceptor implements ReaderInterceptor {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException {

    List<String> encHdr = context.getHeaders().get(HttpHeaders.CONTENT_ENCODING);
    boolean gzip = (encHdr != null) && encHdr.contains(WsCommonConstants.ENCODING_GZIP);

    if (gzip) {
      // Use GZIP Input stream. Input is in GZIP compressed format
      final InputStream originalInputStream = context.getInputStream();
      context.setInputStream(new GZIPInputStream(originalInputStream));
    }

    return context.proceed();
  }
}