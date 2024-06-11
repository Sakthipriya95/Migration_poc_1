/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.timezone;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * @author imi2si
 */
class DefaultCalTimeZone extends AbstractTimeZone {

  private Calendar calendar;

  /**
   * @param timeZone
   */
  public DefaultCalTimeZone(final TimeZone timeZone, final Calendar calendar) {
    super(timeZone);
    this.calendar = calendar;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void adjustTimeZoneFields() {
    this.calendar = super.adjustTimeZone(this.calendar);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getWsResponse() {
    return this.calendar;
  }
}
