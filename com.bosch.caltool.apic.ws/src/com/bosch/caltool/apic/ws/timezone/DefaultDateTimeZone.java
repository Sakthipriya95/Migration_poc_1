/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.timezone;

import java.util.Date;
import java.util.TimeZone;


/**
 * @author imi2si
 */
class DefaultDateTimeZone extends AbstractTimeZone {

  private Date date;

  /**
   * @param timeZone
   */
  public DefaultDateTimeZone(final TimeZone timeZone, final Date date) {
    super(timeZone);
    this.date = date;
    adjustTimeZoneFields();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void adjustTimeZoneFields() {
    this.date = new Date(super.adjustTimeZone(this.date).getTime());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getWsResponse() {
    // TODO Auto-generated method stub
    return this.date != null ? this.date.clone() : null;
  }
}
