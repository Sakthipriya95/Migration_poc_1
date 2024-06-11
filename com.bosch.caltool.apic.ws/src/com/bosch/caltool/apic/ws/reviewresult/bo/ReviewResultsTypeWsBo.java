/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.reviewresult.bo;

import java.util.Calendar;
import java.util.TimeZone;

import com.bosch.caltool.apic.ws.ReviewResultsType;
import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.apic.ws.timezone.TimeZoneFactory;


/**
 * @author imi2si
 */
public class ReviewResultsTypeWsBo extends ReviewResultsType {

  private final TimeZone timeZone;

  public ReviewResultsTypeWsBo(final Session session) {
    this.timeZone = session.getTimezone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setReviewDate(final Calendar calendar) {
    // TODO Auto-generated method stub
    super.setReviewDate(TimeZoneFactory.adjustTimeZone(this.timeZone, calendar));
  }
}
