/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.general.blobcompression;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.utilitytools.util.ToolLogger;

/**
 * @author bne4cob
 */
class WriteHandler implements CompletionHandler<Integer, Attachment> {

  /**
   * {@inheritDoc}
   */
  @Override
  public void completed(final Integer result, final Attachment attach) {
    getLogger().debug("WriteHandler: {} bytes written  to {}{}", result, attach.getPath().toAbsolutePath());
    try {
      attach.getAsyncChannel().close();
    }
    catch (IOException e) {
      getLogger().error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void failed(final Throwable err, final Attachment attach) {
    getLogger().error(err.getMessage(), err);
    try {
      attach.getAsyncChannel().close();
    }
    catch (IOException exp) {
      getLogger().error(exp.getMessage(), exp);
    }
  }

  /**
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return ToolLogger.getLogger();
  }
}