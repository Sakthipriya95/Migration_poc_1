/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.timezone;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * @author imi2si
 */
public class TimeZoneFactory {

  public static Date adjustTimeZone(final TimeZone timeZone, final Date date) {
    AbstractTimeZone tz = new DefaultDateTimeZone(timeZone, date);
    tz.adjustTimeZoneFields();
    return (Date) tz.getWsResponse();
  }

  public static Calendar adjustTimeZone(final TimeZone timeZone, final Calendar cal) {
    AbstractTimeZone tz = new DefaultCalTimeZone(timeZone, cal);
    tz.adjustTimeZoneFields();
    return (Calendar) tz.getWsResponse();
  }

  private TimeZoneFactory() {}
}
