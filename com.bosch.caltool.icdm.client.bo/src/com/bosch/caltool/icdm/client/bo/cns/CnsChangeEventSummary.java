/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cns;

import java.util.Map;

import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeEvent;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.ApicUtil;

/**
 * @author elm1cob
 */
public class CnsChangeEventSummary implements Comparable<CnsChangeEventSummary> {

  /**
   * Event id
   */
  private final Long eventID;

  /**
   * Service id
   */
  private final String serviceID;

  /**
   * Event created date
   */
  private final String createdDate;

  /**
   * Size of the data changed
   */
  private final int dataSize;

  /**
   * Change Count
   */
  private final int changeCount;

  /**
   * Event summary
   */
  private final String summary;

  /**
   * Tooltip Text
   */
  private final String summaryToolTip;

  /**
   * @param event Change Event Details
   */
  public CnsChangeEventSummary(final ChangeEventDetails event) {
    this.eventID = event.getEvent().getChangeId();
    this.serviceID = event.getEvent().getServiceId();
    this.createdDate = event.getCreatedAt();
    this.dataSize = event.getDataLength();
    this.summary = CnsUtils.getChangeSummary(event.getEvent());
    this.changeCount = getTotalCount(event.getEvent());
    this.summaryToolTip = CnsUtils.getChangeDetails(event.getEvent());
  }


  /**
   * @return the eventID
   */
  public Long getEventID() {
    return this.eventID;
  }

  /**
   * @return the serviceID
   */
  public String getServiceID() {
    return this.serviceID;
  }


  /**
   * @return the createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @return the size
   */
  public int getSize() {
    return this.dataSize;
  }


  /**
   * @return the summary
   */
  public String getSummary() {
    return this.summary;
  }


  /**
   * @return the summaryToolTip
   */
  public String getSummaryToolTip() {
    return this.summaryToolTip;
  }

  /**
   * @return the changeCount
   */
  public int getChangeCount() {
    return this.changeCount;
  }

  private int getTotalCount(final ChangeEvent evntObj) {
    int count = 0;
    for (Map<Long, ChangeData<?>> entry : evntObj.getChangeDataMap().values()) {
      count += entry.size();
    }
    return count;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CnsChangeEventSummary arg0) {
    int compareResult = ApicUtil.compare(arg0.createdDate, this.createdDate);
    if (compareResult == 0) {
      compareResult = ApicUtil.compare(arg0.eventID, this.eventID);
    }
    return compareResult;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.eventID == null) ? 0 : this.eventID.hashCode());
    result = (prime * result) + ((this.summary == null) ? 0 : this.summary.hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CnsChangeEventSummary other = (CnsChangeEventSummary) obj;
    if (this.eventID == null) {
      if (other.eventID != null) {
        return false;
      }
    }
    else if (!this.eventID.equals(other.eventID)) {
      return false;
    }
    if (this.summary == null) {
      if (other.summary != null) {
        return false;
      }
    }
    else if (!this.summary.equals(other.summary)) {
      return false;
    }
    return true;
  }


}
