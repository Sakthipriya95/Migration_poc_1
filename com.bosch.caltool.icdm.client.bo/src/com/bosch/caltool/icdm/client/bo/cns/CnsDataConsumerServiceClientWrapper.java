/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cns;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.datamodel.core.cns.ChangeEvent;
import com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.cns.common.model.EventInfoWithData;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author bne4cob
 */
public class CnsDataConsumerServiceClientWrapper {

  /**
   * @param eventId eventId
   * @return Map of change events. Key event ID, value event
   * @throws CnsServiceClientException error while executing service
   */
  public Map<Long, ChangeEvent> getEventsAfter(final Long eventId) throws CnsServiceClientException {
    Map<Long, byte[]> respMap = new CnsDataConsumerServiceClient().getEventsAfter(eventId);
    return convertChangeEvents(respMap);
  }

  /**
   * @param value
   * @return
   * @throws CnsServiceClientException
   */
  private Map<Long, ChangeEvent> convertChangeEvents(final Map<Long, byte[]> respMap) {
    Map<Long, ChangeEvent> retMap = new HashMap<>();
    respMap.forEach((evtId, data) -> {
      ChangeEvent event = deserializeChangeEvent(evtId, data);
      if (event != null) {
        retMap.put(evtId, event);
      }
    });
    return retMap;
  }

  /**
   * @param value
   * @return
   * @throws CnsServiceClientException
   */
  private Map<Long, ChangeEventDetails> convertChangeEvents2(final Map<Long, EventInfoWithData> respMap) {
    Map<Long, ChangeEventDetails> retMap = new HashMap<>();
    respMap.forEach((evtId, evtDet) -> retMap.put(evtId, createChangeEventDetails(evtDet)));
    return retMap;
  }

  private ChangeEventDetails createChangeEventDetails(final EventInfoWithData input) {
    ChangeEventDetails ret = new ChangeEventDetails();
    ret.setCreatedAt(input.getCreatedAt());
    ret.setDataAvailable(input.getDataAvailable());
    ret.setProducerId(input.getProducerId());
    ret.setSessionId(input.getSessionId());
    ret.setDataLength(input.getDataLength());
    ret.setEvent(deserializeChangeEvent(input.getEventId(), input.getData()));
    return ret;
  }

  /**
   * @return
   */
  private ILoggerAdapter getLogger() {
    return CDMLogger.getInstance();
  }

  /**
   * @param value
   * @return
   */
  private ChangeEvent deserializeChangeEvent(final Long eventId, final byte[] data) {

    ChangeEvent ret = null;

    try (ByteArrayInputStream byteInpStream = new ByteArrayInputStream(data);
        ObjectInputStream objInpStream = new ObjectInputStream(byteInpStream);) {

      getLogger().debug("Deserialize Change Event started: Event ID = {},  data size = {}", eventId, data.length);

      ret = (ChangeEvent) objInpStream.readObject();

    }
    catch (IOException | ClassNotFoundException e) {
      getLogger().error("Error while creating change event object for event ID " + eventId + " : " + e.getMessage(), e);
    }

    getLogger().debug("Deserialize Change Event completed: Event ID = {},  data size = {}", eventId, data.length);

    return ret;
  }


  /**
   * @return Map of change events. Key event ID, value event
   * @throws CnsServiceClientException error while executing service
   */
  public Map<Long, ChangeEventDetails> getEventsOfCurrentSession() throws CnsServiceClientException {
    Map<Long, EventInfoWithData> respMap = new CnsDataConsumerServiceClient().getEventsOfCurrentSession();
    return convertChangeEvents2(respMap);
  }

  /**
   * @param startEventId start Event ID
   * @return Map of change events. Key event ID, value event
   * @throws CnsServiceClientException error while executing service
   */
  public Map<Long, ChangeEventDetails> getEventsOfCurrentSession(final Long startEventId)
      throws CnsServiceClientException {
    Map<Long, EventInfoWithData> respMap = new CnsDataConsumerServiceClient().getEventsOfCurrentSession(startEventId);
    return convertChangeEvents2(respMap);
  }

  /**
   * @return latest registered event ID
   * @throws CnsServiceClientException error while executing service
   */
  public Long getLatestEventId() throws CnsServiceClientException {
    return new CnsDataConsumerServiceClient().getLatestEventId();
  }
}
