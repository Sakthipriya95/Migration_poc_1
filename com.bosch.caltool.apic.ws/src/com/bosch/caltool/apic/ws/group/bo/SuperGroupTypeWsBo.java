/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.group.bo;

import java.util.Calendar;
import java.util.TimeZone;

import com.bosch.caltool.apic.ws.SuperGroupType;
import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.apic.ws.timezone.TimeZoneFactory;


/**
 * @author imi2si
 */
public class SuperGroupTypeWsBo extends SuperGroupType {

  private final TimeZone timeZone;

  public SuperGroupTypeWsBo(final Session session) {
    this.timeZone = session.getTimezone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreateDate(final Calendar param) {
    // TODO Auto-generated method stub
    super.setCreateDate(TimeZoneFactory.adjustTimeZone(this.timeZone, param));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifyDate(final Calendar param) {
    // TODO Auto-generated method stub
    super.setModifyDate(TimeZoneFactory.adjustTimeZone(this.timeZone, param));
  }
}
