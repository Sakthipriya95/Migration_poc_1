/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.usecase.bo;

import java.util.Calendar;
import java.util.TimeZone;

import com.bosch.caltool.apic.ws.UseCaseSectionItemType;
import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.apic.ws.timezone.TimeZoneFactory;

/**
 * @author svj7cob
 *
 */
public class UseCaseSectionItemTypeWsBo extends UseCaseSectionItemType{


  private final TimeZone timeZone;

  public UseCaseSectionItemTypeWsBo(final Session session) {
    this.timeZone = session.getTimezone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final Calendar param) {
    // TODO Auto-generated method stub
    super.setCreatedDate(TimeZoneFactory.adjustTimeZone(this.timeZone, param));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final Calendar param) {
    // TODO Auto-generated method stub
    super.setModifiedDate(TimeZoneFactory.adjustTimeZone(this.timeZone, param));
  }

}
