/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;


/**
 * @author imi2si
 */
public class TimeZoneChecker {

  /**
   * Logger for this class
   */
  private final ILoggerAdapter logger;

  /**
   * @param logger ILoggerAdapter
   */
  public TimeZoneChecker(final ILoggerAdapter logger) {
    this.logger = logger;
  }

  public void assertDate(final Date databaseValue, final Date webserviceDate, final String clientTimezone) {
    SimpleDateFormat dateFormat = new SimpleDateFormat();
    this.logger.info("Comparing DB-date " + dateFormat.format(databaseValue) + ", webservice date " +
        dateFormat.format(webserviceDate) + " for timezone " + clientTimezone);

    TimeZone timeZone = TimeZone.getTimeZone(clientTimezone);
    Date dbDateWithOffset = new Date(databaseValue.getTime() + timeZone.getOffset(databaseValue.getTime()));

    assertEquals(dateFormat.format(dbDateWithOffset), dateFormat.format(webserviceDate));
  }

  public Date createDate(final int year, final int month, final int day, final int hour, final int minute,
      final int sec) {
    Calendar cal = Calendar.getInstance();
    // In Calendar class, month is 0-based. The other parameters are 1-based
    cal.set(year, month - 1, day, hour, minute, sec);

    return cal.getTime();
  }
}
