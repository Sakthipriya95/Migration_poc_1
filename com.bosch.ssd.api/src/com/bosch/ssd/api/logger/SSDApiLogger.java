/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.logger;

import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.ssd.api.util.SSDAPIUtil;

/**
 * @author VAU3COB
 */
public class SSDApiLogger extends Log4JLoggerAdapterImpl {

  private static SSDApiLogger ssdApiLogger;

  /**
   * @param logFilePath
   */
  private SSDApiLogger(final String logFilePath) {
    super(logFilePath);
  }

  /**
   * @return
   */
  public static SSDApiLogger getLoggerInstance() {
    if (ssdApiLogger == null) {
      ssdApiLogger = new SSDApiLogger(SSDAPIUtil.getInstance().getProperty("LOG_FILE"));
    }
    return ssdApiLogger;
  }

}
