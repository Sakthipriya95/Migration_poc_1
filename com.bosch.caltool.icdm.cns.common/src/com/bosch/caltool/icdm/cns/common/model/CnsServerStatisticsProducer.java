/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bne4cob
 */
public class CnsServerStatisticsProducer {

  private Map<String, ProducerInfo> producerInfo = new HashMap<>();
  private Map<String, List<EventInfo2>> producerEventInfo = new HashMap<>();

  /**
   * @return the producerInfo
   */
  public Map<String, ProducerInfo> getProducerInfo() {
    return this.producerInfo;
  }

  /**
   * @param producerInfo the producerInfo to set
   */
  public void setProducerInfo(final Map<String, ProducerInfo> producerInfo) {
    this.producerInfo = producerInfo;
  }

  /**
   * @return the producerEventsMap
   */
  public Map<String, List<EventInfo2>> getProducerEventInfo() {
    return this.producerEventInfo;
  }

  /**
   * @param producerEventInfoMap the producerEventsMap to set
   */
  public void setProducerEventInfo(final Map<String, List<EventInfo2>> producerEventInfoMap) {
    this.producerEventInfo = producerEventInfoMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [producerInfo=" + this.producerInfo + ", producerEvents=" + this.producerEventInfo +
        "]";
  }


}
