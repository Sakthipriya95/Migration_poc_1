/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.client.utils.ZipUtils;
import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
import com.bosch.caltool.icdm.cns.common.model.EventInfoWithData;


/**
 * @author bne4cob
 */
public class CnsDataConsumerServiceClient extends AbstractCnsServiceClient {

  /**
   * Constructor
   */
  public CnsDataConsumerServiceClient() {
    super(CnsCommonConstants.RWS_DATA_CONSUMER);
  }

  /**
   * Get the events after the given event ID
   *
   * @param eventId eventId
   * @return Map of change events. Key - event ID, value - event as unzipped byte array
   * @throws CnsServiceClientException error while executing service
   */
  public Map<Long, byte[]> getEventsAfter(final Long eventId) throws CnsServiceClientException {

    getLogger().debug("Fetching events after {}", eventId);

    WebTarget wsTarget =
        getWsBase().path(CnsCommonConstants.RWS_EVENTS_AFTER).queryParam(CnsCommonConstants.QP_EVENT_ID, eventId);
    return getChangeEvents(wsTarget);
  }

  /**
   * @param value
   * @return
   * @throws CnsServiceClientException
   */
  private Map<Long, byte[]> getChangeEvents(final WebTarget wsTarget) throws CnsServiceClientException {
    GenericType<Map<Long, byte[]>> type = new GenericType<Map<Long, byte[]>>() {};
    Map<Long, byte[]> respMap = get(wsTarget, type);

    logEventIds(respMap);

    Map<Long, byte[]> retMap = new HashMap<>();
    respMap.forEach((evtId, data) -> retMap.put(evtId, ZipUtils.unzip(data)));

    return retMap;
  }

  private void logEventIds(final Map<Long, byte[]> respMap) {
    int eventCount = respMap.size();
    getLogger().debug("Number of change events retrieved = {}", eventCount);

    if ((eventCount > 0) && (getLogger().getLogLevel() <= ILoggerAdapter.LEVEL_INFO)) {
      StringJoiner eventIdStr = new StringJoiner(",");
      respMap.keySet().stream().map(String::valueOf).forEach(eventIdStr::add);
      getLogger().info("New change event IDs are : {}", eventIdStr);
    }
  }


  /**
   * Get all events in the current session
   *
   * @return Map of change events. Key - event ID, value - event as unzipped byte array
   * @throws CnsServiceClientException error while executing service
   */
  public Map<Long, EventInfoWithData> getEventsOfCurrentSession() throws CnsServiceClientException {
    return getEventsOfCurrentSession(null);
  }

  /**
   * Get all events in the current session
   *
   * @param eventIdStart start event ID
   * @return Map of change events. Key - event ID, value - event as unzipped byte array
   * @throws CnsServiceClientException error while executing service
   */
  public Map<Long, EventInfoWithData> getEventsOfCurrentSession(final Long eventIdStart)
      throws CnsServiceClientException {

    getLogger().debug("Getting events of current session. Start event ID = {}", eventIdStart);

    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_EVENTS_CUR_SESSION);
    if (eventIdStart != null) {
      wsTarget = wsTarget.queryParam(CnsCommonConstants.QP_EVENT_ID_START, eventIdStart);
    }

    Map<Long, EventInfoWithData> respMap = get(wsTarget, new GenericType<Map<Long, EventInfoWithData>>() {});

    getLogger().debug("Number of change events retrieved = {}", respMap.size());

    Map<Long, EventInfoWithData> retMap = new HashMap<>();
    respMap.forEach((evtId, data) -> retMap.put(evtId, createUnzippedEventInfoWithData(data)));

    return retMap;
  }

  private EventInfoWithData createUnzippedEventInfoWithData(final EventInfoWithData input) {
    EventInfoWithData ret = new EventInfoWithData(input);
    ret.setData(ZipUtils.unzip(input.getData()));
    return ret;
  }

  /**
   * Get the latest event ID
   *
   * @return latest registered event ID
   * @throws CnsServiceClientException error while executing service
   */
  public Long getLatestEventId() throws CnsServiceClientException {
    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_LATEST_EVENT_ID);
    Long eventId = get(wsTarget, Long.class);
    getLogger().debug("Latest event ID = {}", eventId);
    return eventId;
  }

  /**
   * Create a CNS session
   *
   * @param listenerPort listener port
   * @return session ID
   * @throws CnsServiceClientException error while executing service
   */
  public String createSession(final int listenerPort) throws CnsServiceClientException {
    WebTarget wsTarget =
        getWsBase().path(CnsCommonConstants.RWS_SESSION).queryParam(CnsCommonConstants.QP_LISTENING_PORT, listenerPort);
    String sessionID = post(wsTarget, null, String.class);
    getLogger().info("CNS session created. ID = {}", sessionID);
    return sessionID;
  }

  /**
   * Close the CNS session
   *
   * @param sessionId Session ID
   * @throws CnsServiceClientException error while executing service
   */
  public void closeSession(final String sessionId) throws CnsServiceClientException {
    WebTarget wsTarget =
        getWsBase().path(CnsCommonConstants.RWS_SESSION).queryParam(CnsCommonConstants.QP_SESSION_ID, sessionId);
    delete(wsTarget);
    getLogger().info("CNS session closed. ID = {}", sessionId);
  }

}
