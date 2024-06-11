/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.usecase.bo;

import java.util.Calendar;
import java.util.TimeZone;

import com.bosch.caltool.apic.ws.UseCaseSectionType;
import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.apic.ws.timezone.TimeZoneFactory;

/**
 * @author svj7cob
 *
 */
public class UseCaseSectionTypeWsBo extends UseCaseSectionType{



  private final TimeZone timeZone;

  public UseCaseSectionTypeWsBo(final Session session) {

    this.timeZone = session.getTimezone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final Calendar param) {
    // TimeZone is being adjusted and set
    super.setCreatedDate(TimeZoneFactory.adjustTimeZone(this.timeZone, param));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final Calendar param) {
    // TimeZone is being adjusted and set
    super.setModifiedDate(TimeZoneFactory.adjustTimeZone(this.timeZone, param));
  }

}
