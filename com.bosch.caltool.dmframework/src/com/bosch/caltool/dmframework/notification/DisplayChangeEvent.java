/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * This class which stores the details of the data change triggered by a CQN or a command
 *
 * @author bne4cob
 */
public class DisplayChangeEvent {

  /**
   * number for Hash code generation
   */
  private static final int HASHCODE_NUM = 32;

  /**
   * Prime number for Hash code generation
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * Unique ID for this event
   */
  private final long eventID = System.nanoTime();
  /**
   * collection of changed data
   */
  private final ConcurrentMap<Long, ChangedData> changedData = new ConcurrentHashMap<Long, ChangedData>();

  /**
   * @return the eventID
   */
  public final long getEventID() {
    return this.eventID;
  }

  /**
   * @return the changedData
   */
  public final Map<Long, ChangedData> getChangedData() {
    return this.changedData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = HASHCODE_PRIME;
    int result = 1;
    result = (prime * result) + (int) (this.eventID ^ (this.eventID >>> HASHCODE_NUM));
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
    final DisplayChangeEvent other = (DisplayChangeEvent) obj;
    return this.eventID == other.eventID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "DisplayChangeEvent [ID=" + this.eventID + ", changedData=" + this.changedData + "]";
  }

  /**
   * Change event is empty if there are no change data in it.
   *
   * @return whether this event is empty or not
   */
  public boolean isEmpty() {
    return this.changedData.isEmpty();
  }

}
