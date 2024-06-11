/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo.events;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
import com.bosch.caltool.icdm.cns.common.model.EventInfo;
import com.bosch.caltool.icdm.cns.common.model.EventInfoWithData;
import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;
import com.bosch.caltool.icdm.cns.server.bo.CnsServiceData;
import com.bosch.caltool.icdm.cns.server.bo.session.SESSION_STATE;
import com.bosch.caltool.icdm.cns.server.bo.session.SessionManager;
import com.bosch.caltool.icdm.cns.server.exception.CnsServiceException;
import com.bosch.caltool.icdm.cns.server.utils.Utils;

/**
 * @author bne4cob
 */
public enum ChangeEventManager {
                                /**
                                 * Unique instance
                                 */
                                INSTANCE;

  private final Object syncLock = new Object();

  private final ConcurrentMap<Long, EventDet> eventMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, EventDet> archivedEventMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, Set<Long>> eventBySessionMap = new ConcurrentHashMap<>();
  /**
   * Key - Data Producer ID<br>
   * value - set of event IDs
   */
  private final ConcurrentMap<String, Set<Long>> eventByProducerMap = new ConcurrentHashMap<>();

  private Long latestEventId;

  private long totalDataSize;

  private long currentDataSize;

  private long peakDataSize;

  private Instant peakDataSizeTime = Instant.now();

  private final Set<Long> evntNoSessionSet = Collections.synchronizedSet(new HashSet<>());
  private final Set<Long> evntInvalidSessionSet = Collections.synchronizedSet(new HashSet<>());
  private final Set<Long> evntClosedSessionSet = Collections.synchronizedSet(new HashSet<>());


  /**
   * Add the change event
   *
   * @param eventId event ID
   * @param sessionId Client Session ID
   * @param producerId producer Id
   * @param serviceId service id
   * @param data change data as byte[]
   */
  public void addEvent(final Long eventId, final String sessionId, final String producerId, final String serviceId,
      final byte[] data) {
    synchronized (this.syncLock) {

      String sessId = sessionId;
      SESSION_STATE sessionState = SessionManager.INSTANCE.getState(sessionId);

      if (sessionState == SESSION_STATE.NO_SESSION) {
        getLogger().info("Event {} received without session ID for SID: {}. Event will be added to default session",
            eventId);
        sessId = CnsCommonConstants.DEFAULT_SESSION_ID;
        this.evntNoSessionSet.add(eventId);
      }
      else if (sessionState == SESSION_STATE.INVALID) {
        getLogger().warn("Invalid session ID - " + sessionId + ". Event " + eventId +
            " will be added to default session for SID: " + serviceId + ".");
        sessId = CnsCommonConstants.DEFAULT_SESSION_ID;
        this.evntInvalidSessionSet.add(eventId);
      }
      else if (sessionState == SESSION_STATE.CLOSED) {
        getLogger().warn("Event " + eventId + " received from closed session " + sessionId + " for SID: " + serviceId);
        this.evntClosedSessionSet.add(eventId);
      }

      EventDet eventDetail = new EventDet(eventId, sessId, producerId, serviceId, data, sessionState);
      getLogger().debug("Event Details created. Details : {} ", eventDetail);

      this.eventMap.put(eventId, eventDetail);
      this.eventBySessionMap.computeIfAbsent(sessId, k -> new HashSet<Long>()).add(eventId);
      this.eventByProducerMap.computeIfAbsent(producerId, k -> new HashSet<>()).add(eventId);

      getLogger().info(
          "Change Event added : ID = {}, SID = {}, Data size = {}, Session ID = {}, Total events count = {}; Total sessions = {}",
          eventId, serviceId, data.length, sessId, this.eventMap.size(), this.eventBySessionMap.size());

      if ((this.latestEventId == null) || (eventId > this.latestEventId)) {
        this.latestEventId = eventId;
      }

      long dataSize = data.length;
      this.totalDataSize += dataSize;
      this.currentDataSize += dataSize;
      if (this.currentDataSize > this.peakDataSize) {
        this.peakDataSize = this.currentDataSize;
        this.peakDataSizeTime = Instant.now();
      }

      SessionManager.INSTANCE.addToTotalDataSize(sessId, dataSize);

    }
  }

  /**
   * Get the after the given event ID
   *
   * @param sessionId session ID
   * @param eventId event ID
   * @param excludeCurSessData if true, events from input session is excluded
   * @return Map - Key - event ID, value - data as byte[]
   * @throws CnsServiceException if event ID is not provided
   */
  public Map<Long, byte[]> getEventsAfter(final String sessionId, final Long eventId, final boolean excludeCurSessData)
      throws CnsServiceException {

    assertSessionIdNotNull(sessionId);
    SessionManager.INSTANCE.assertNotClosed(sessionId);

    Map<Long, byte[]> retMap;

    synchronized (this.syncLock) {
      if (excludeCurSessData) {
        retMap = this.eventMap.entrySet().stream().filter(entry -> entry.getKey() > eventId)
            .filter(entry -> !this.eventBySessionMap.getOrDefault(sessionId, new HashSet<>()).contains(entry.getKey()))
            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getData()));
      }
      else {
        retMap = this.eventMap.entrySet().stream().filter(entry -> entry.getKey() > eventId)
            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getData()));
      }

      SessionManager.INSTANCE.updateLastContactedAt(sessionId);
    }
    getLogger().debug("Event ID = {}, Session ID = {}, exclude flag = {} : Event count = {}", eventId, sessionId,
        excludeCurSessData, retMap.size());

    return retMap;
  }


  /**
   * Get the events of the given client session
   *
   * @param serviceData servcie data
   * @param sessionId client session ID
   * @param eventIdStart start Event ID
   * @return Map - Key - event ID, value - data as byte[]
   * @throws CnsServiceException if session ID is not provided
   */
  public Map<Long, EventInfoWithData> getEventsOfSession(final CnsServiceData serviceData, final String sessionId,
      final Long eventIdStart)
      throws CnsServiceException {

    assertSessionIdNotNull(sessionId);
    SessionManager.INSTANCE.assertNotClosed(sessionId);

    Map<Long, EventInfoWithData> retMap = new HashMap<>();
    synchronized (this.syncLock) {
      Set<Long> sessEventIdSet = this.eventBySessionMap.get(sessionId);
      if (sessEventIdSet != null) {
        sessEventIdSet.stream().filter(evtId -> eventIdStart == null ? true : evtId >= eventIdStart)
            .forEach(evntId -> retMap.put(evntId, getEventInfoWithData(serviceData, evntId)));
      }
    }

    getLogger().debug("Session : {} : Event count = {}", sessionId, retMap.size());

    return retMap;
  }

  private EventInfoWithData getEventInfoWithData(final CnsServiceData serviceData, final Long evntId) {
    EventInfo evt = getEventInfoWithoutEx(serviceData, evntId);
    EventInfoWithData ret = new EventInfoWithData(evt);
    ret.setData(this.eventMap.get(evntId).getData());
    return ret;
  }

  private void assertSessionIdNotNull(final String sessionId) throws CnsServiceException {
    if (Utils.isEmpty(sessionId)) {
      throw new CnsServiceException("CNS-1001", "Session ID is mandatory");
    }
  }


  /**
   * @return the latestEventId
   */
  public Long getLatestEventId() {
    synchronized (this.syncLock) {
      Long ret = this.latestEventId == null ? 0L : this.latestEventId;

      getLogger().debug("Latest change event ID = {}", ret);

      return this.latestEventId == null ? 0L : this.latestEventId;
    }
  }

  /**
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return CnsObjectStore.getLogger();
  }

  /**
   * @return total events
   */
  public int getTotalEventCount() {
    return this.eventMap.size() + this.archivedEventMap.size();
  }

  /**
   * @return all event IDs
   */
  public List<Long> getAllEventIds() {
    List<Long> retList = new ArrayList<>(this.eventMap.keySet());
    retList.addAll(this.archivedEventMap.keySet());
    Collections.sort(retList, Comparator.reverseOrder());

    return retList;
  }

  /**
   * @return events by session ID
   */
  public Map<String, Set<Long>> getEventsBySession() {
    Map<String, Set<Long>> retMap = new HashMap<>();
    this.eventBySessionMap.forEach((sessId, evtSet) -> retMap.put(sessId, new HashSet<>(evtSet)));
    return retMap;
  }

  /**
   * @return Total Data Size
   */
  public long getTotalDataSize() {
    return this.totalDataSize;
  }

  /**
   * @return Current Data Size
   */
  public long getCurrentDataSize() {
    return this.currentDataSize;
  }

  /**
   * @param sessIdSet set of session Ids
   * @param serviceData Service Data
   * @return map. Key - session ID, value - list of events sorted desc of event ID
   */
  public Map<String, List<EventInfo>> getEventsBySession(final Set<String> sessIdSet,
      final CnsServiceData serviceData) {
    return sessIdSet.stream().collect(Collectors.toMap(id -> id, id -> getEventsBySession(id, serviceData)));
  }


  /**
   * @param sessionId session ID
   * @param serviceData Service Data
   * @return list of events sorted desc of event ID
   */
  public List<EventInfo> getEventsBySession(final String sessionId, final CnsServiceData serviceData) {
    Set<Long> evSet = this.eventBySessionMap.get(sessionId);
    return evSet == null ? new ArrayList<>() : evSet.stream().sorted(Comparator.reverseOrder())
        .map(evId -> toEventInfo(getWithoutEx(evId), serviceData)).collect(Collectors.toList());
  }

  /**
   * Get the event IDs of the given producer
   *
   * @param producerId Producer ID
   * @return event IDs, sorted reverse order
   */
  public List<Long> getEventIdsByProducer(final String producerId) {
    Set<Long> evSet = this.eventByProducerMap.get(producerId);
    return evSet == null ? new ArrayList<>()
        : evSet.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
  }

  /**
   * Retrieve events for the given source state. Only states INVALID, CLOSED and NONE are supported
   *
   * @param sourceState source state
   * @param serviceData Service Data
   * @return list of events IDs sorted desc of event ID
   */
  public List<Long> getEventIdsBySourceState(final String sourceState, final CnsServiceData serviceData) {
    List<Long> retList;
    switch (SESSION_STATE.getType(sourceState)) {
      case CLOSED:
        synchronized (this.evntClosedSessionSet) {
          retList = new ArrayList<>(this.evntClosedSessionSet);
        }
        break;
      case INVALID:
        synchronized (this.evntInvalidSessionSet) {
          retList = new ArrayList<>(this.evntInvalidSessionSet);
        }
        break;
      case NO_SESSION:
        synchronized (this.evntNoSessionSet) {
          retList = new ArrayList<>(this.evntNoSessionSet);
        }
        break;
      default:
        getLogger().error("Given source type '" + sourceState + "' is invalid or not supported for this query");
        retList = new ArrayList<>();
        break;
    }

    Collections.sort(retList, Comparator.reverseOrder());

    return retList;
  }

  /**
   * @param eventDet
   * @return
   */
  private EventInfo toEventInfo(final EventDet eventDet, final CnsServiceData serviceData) {
    EventInfo ret = new EventInfo();
    ret.setEventId(eventDet.getEventId());
    ret.setServiceId(eventDet.getServiceId());
    ret.setSessionId(eventDet.getSessionId());
    ret.setProducerId(eventDet.getProducerId());
    ret.setCreatedAt(Utils.instantToString(eventDet.getCreatedAt(), serviceData.getTimeZoneId()));
    ret.setDataLength(eventDet.getDataSize());
    ret.setDataAvailable(eventDet.isDataAvailable() ? "Yes" : "No");
    ret.setSessionState(eventDet.getSessionState().getCode());
    return ret;
  }

  /**
   * @param olderThan older than
   */
  public void clearOldEventData(final Instant olderThan) {
    getLogger().debug("Clearing data older than {}. Total data size before clean-up = {} bytes", olderThan,
        this.currentDataSize);

    synchronized (this.syncLock) {
      new ArrayList<>(this.eventMap.values()).stream().filter(evt -> evt.getCreatedAt().isBefore(olderThan))
          .forEach(evt -> {
            this.archivedEventMap.put(evt.getEventId(), evt);
            this.eventMap.remove(evt.getEventId());
            evt.clearData();
            this.currentDataSize -= evt.getDataSize();
          });
    }

    getLogger().debug("Clearing data COMPLETED. Total data size after clean-up = {} bytes", this.currentDataSize);
  }

  /**
   * Get the event info of the given event ID
   *
   * @param serviceData Cns Service Data
   * @param eventId Event ID
   * @return Event Info
   * @throws CnsServiceException if event ID is invalid
   */
  public EventInfo getEventInfo(final CnsServiceData serviceData, final Long eventId) throws CnsServiceException {

    if (eventId == null) {
      throw new CnsServiceException("CNS-1002", "Event ID cannot be null" + eventId);
    }

    EventDet event = getWithoutEx(eventId);
    if (event == null) {
      throw new CnsServiceException("CNS-1003", "Invalid event ID " + eventId);
    }

    return toEventInfo(event, serviceData);
  }

  /**
   * Get the event info of the given event ID
   *
   * @param serviceData Cns Service Data
   * @param eventId Event ID
   * @return Event Info
   */
  public EventInfo getEventInfoWithoutEx(final CnsServiceData serviceData, final Long eventId) {
    return toEventInfo(getWithoutEx(eventId), serviceData);
  }


  private EventDet getWithoutEx(final Long eventId) {
    EventDet event = this.eventMap.get(eventId);
    return (event == null) ? this.archivedEventMap.get(eventId) : event;
  }

  /**
   * @return peak data size
   */
  public long getPeakDataSize() {
    return this.peakDataSize;
  }

  /**
   * Get the peak data size reached time as string, in requested timezone
   *
   * @param serviceData service data
   * @return peak data size time as string, in the given timezone
   */
  public String getPeakDataSizeTime(final CnsServiceData serviceData) {
    return Utils.instantToString(this.peakDataSizeTime, serviceData.getTimeZoneId());
  }

  /**
   * Get the event count of the given session
   *
   * @param sessionId producer ID
   * @return event count
   */
  public int getEventCountSession(final String sessionId) {
    Set<Long> evtSet = this.eventBySessionMap.get(sessionId);
    return evtSet == null ? 0 : evtSet.size();
  }

  /**
   * Get the event count of the given producer
   *
   * @param producerId producer ID
   * @return event count
   */
  public int getEventCountProducer(final String producerId) {
    Set<Long> evtSet = this.eventByProducerMap.get(producerId);
    return evtSet == null ? 0 : evtSet.size();
  }


  /**
   * @return the evntNoSessionCount
   */
  public int getEvntNoSessionCount() {
    return this.evntNoSessionSet.size();
  }


  /**
   * @return the evntInvalidSessionCount
   */
  public int getEvntInvalidSessionCount() {
    return this.evntInvalidSessionSet.size();
  }


  /**
   * @return the evntClosedSessionCount
   */
  public int getEvntClosedSessionCount() {
    return this.evntClosedSessionSet.size();
  }


}
