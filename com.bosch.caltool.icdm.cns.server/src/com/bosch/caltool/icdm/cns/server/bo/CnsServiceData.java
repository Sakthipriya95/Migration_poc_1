/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo;

import java.time.ZoneId;
import java.time.ZoneOffset;

import com.bosch.caltool.icdm.cns.server.utils.Utils;

/**
 * @author BNE4COB
 */
public class CnsServiceData {

  private ZoneId timeZoneId = ZoneOffset.UTC;

  /**
   * @return the timezoneId
   */
  public ZoneId getTimeZoneId() {
    return this.timeZoneId;
  }

  /**
   * @param timezoneStr the timezoneStr to set
   */
  public void setTimeZone(final String timezoneStr) {
    if (Utils.isInteger(timezoneStr)) {
      int offset = Integer.parseInt(timezoneStr) * 60;
      this.timeZoneId = ZoneOffset.ofTotalSeconds(offset);
    }
  }
}
