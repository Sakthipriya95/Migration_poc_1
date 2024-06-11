/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.timezone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * @author imi2si
 */
public abstract class AbstractTimeZone {

  private final SimpleDateFormat dateFormat =
      new SimpleDateFormat("dd.MM.yyyy HH:mm:ss ZZZ", Locale.getDefault(Locale.Category.FORMAT));

  private final TimeZone timeZone;

  public static void setDefaultTimeZone(final String timeZoneId) {
    TimeZone systemTimeZone = TimeZone.getDefault();
    systemTimeZone.setID(timeZoneId);
    TimeZone.setDefault(systemTimeZone);
  }

  public AbstractTimeZone(final TimeZone timeZone) {
    this.timeZone = timeZone;
  }

  public abstract void adjustTimeZoneFields();

  public abstract Object getWsResponse();

  protected Date adjustTimeZone(final Date date) {
    if (date == null) {
      return null;
    }

    Date dateTz = new Date(date.getTime());
    dateTz.setTime(addOffset(dateTz.getTime()));

    return dateTz;
  }

  protected Calendar adjustTimeZone(final Calendar date) {
    if (date == null) {
      return null;
    }

    /*
     * Timezone of date object is always UTC. Date object does not know that, because it is created with
     * Default-TimeZone of server. Database just stores in UTC format. BO objects of service does not know this, because
     * olf packages are used. The following logic must be checked when using iCDM packages > 1.10
     */
    Calendar dateTz = Calendar.getInstance();
    dateTz.setTimeZone(TimeZone.getTimeZone("UTC"));
    dateTz.setTimeInMillis(date.getTimeInMillis());

    this.dateFormat.setTimeZone(dateTz.getTimeZone());

    dateTz.setTimeZone(this.timeZone);

    this.dateFormat.setTimeZone(dateTz.getTimeZone());

    return dateTz;
  }

  private long addOffset(final long date) {

    long offset = this.timeZone.getOffset(date);

    return date + offset;
  }
}
