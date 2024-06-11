/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.attribute.bo;

import java.util.Calendar;
import java.util.TimeZone;

import com.bosch.caltool.apic.ws.Attribute;
import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.apic.ws.timezone.TimeZoneFactory;


/**
 * @author imi2si
 */
public class AttributeWsBo extends Attribute {

  private final TimeZone timeZone;

  public AttributeWsBo(final Session session) {
    this.timeZone = session.getTimezone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreateDate(final Calendar param) {
    // Set created date
    super.setCreateDate(TimeZoneFactory.adjustTimeZone(this.timeZone, param));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifyDate(final Calendar param) {
    // Set modified date
    // TODO Auto-generated method stub
    super.setModifyDate(TimeZoneFactory.adjustTimeZone(this.timeZone, param));
  }
}
