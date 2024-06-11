/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client.test.model;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeEvent;
import com.bosch.caltool.icdm.cns.client.CnsDataProducerServiceClient;

/**
 * @author bne4cob
 */
public enum ChangeEventGenerator {
                                  /**
                                   * Instance
                                   */
                                  INSTANCE;

  /**
   * @param eventId event ID
   * @param sessionId session Id
   */
  public void createAndSendEvent(final Long eventId, final String serviceId, final String sessionId) {
    CnsDataProducerServiceClient client = new CnsDataProducerServiceClient();
    client.setSessionId(sessionId);
    client.setChangeEvent(createSampleChangeEvent(eventId, serviceId));
    client.run();
  }

  /**
   * @return
   */
  private ChangeEvent createSampleChangeEvent(final Long eventId, final String serviceId) {
    ChangeEvent event = new ChangeEvent();
    event.setChangeId(eventId);
    event.setServiceId(serviceId);

    Map<String, Map<Long, ChangeData<?>>> changeDataMap = new HashMap<>();
    Map<Long, ChangeData<?>> aChMap = new HashMap<>();

    ChangeData<A> chDataA = new ChangeData<>();
    chDataA.setChangeEventId(eventId);
    chDataA.setChangeType(CHANGE_OPERATION.CREATE);
    chDataA.setNewData(new A());
    aChMap.put(eventId + 500L, chDataA);

    Map<Long, ChangeData<?>> bChMap = new HashMap<>();

    ChangeData<B> chDataB = new ChangeData<>();
    chDataB.setChangeEventId(eventId);
    chDataB.setChangeType(CHANGE_OPERATION.UPDATE);
    chDataB.setOldData(new B());
    chDataB.setNewData(new B());
    bChMap.put(600L, chDataB);

    changeDataMap.put("A", aChMap);
    changeDataMap.put("B", bChMap);

    event.setChangeDataMap(changeDataMap);

    return event;
  }
}
