/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo.admin.stats;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.common.model.CnsServerStatisticsDetail;
import com.bosch.caltool.icdm.cns.common.model.CnsServerStatisticsProducer;
import com.bosch.caltool.icdm.cns.common.model.CnsServerStatisticsSession;
import com.bosch.caltool.icdm.cns.common.model.CnsServerStatisticsSummary;
import com.bosch.caltool.icdm.cns.common.model.EventDetInfo;
import com.bosch.caltool.icdm.cns.common.model.EventInfo;
import com.bosch.caltool.icdm.cns.common.model.EventInfo2;
import com.bosch.caltool.icdm.cns.common.model.PageResponse;
import com.bosch.caltool.icdm.cns.common.model.ProducerInfo;
import com.bosch.caltool.icdm.cns.common.model.SessionInfo;
import com.bosch.caltool.icdm.cns.server.bo.AbstractDataProvider;
import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;
import com.bosch.caltool.icdm.cns.server.bo.CnsServiceData;
import com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager;
import com.bosch.caltool.icdm.cns.server.bo.session.DataProducerManager;
import com.bosch.caltool.icdm.cns.server.bo.session.SESSION_STATE;
import com.bosch.caltool.icdm.cns.server.bo.session.SessionManager;
import com.bosch.caltool.icdm.cns.server.exception.CnsServiceException;
import com.bosch.caltool.icdm.cns.server.utils.DataPaginator;
import com.bosch.caltool.icdm.cns.server.utils.Utils;

/**
 * @author bne4cob
 */
public class CnsStatisticsLoader extends AbstractDataProvider {

  /**
   * @param serviceData Service Data
   */
  public CnsStatisticsLoader(final CnsServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @return Statistics summary
   */
  public CnsServerStatisticsSummary getSummary() {
    CnsServerStatisticsSummary ret = new CnsServerStatisticsSummary();

    ret.setStartTime(SessionManager.INSTANCE.getStartTimeStr(getServiceData()));
    ret.setCurrentTime(Utils.instantToString(Instant.now(), getServiceData().getTimeZoneId()));

    ret.setActiveSessions(SessionManager.INSTANCE.getActiveSessionCount());
    ret.setPeakActiveSession(SessionManager.INSTANCE.getPeakActiveSessionCount());
    ret.setPeakActiveSessionAt(SessionManager.INSTANCE.getPeakActiveSessionTime(getServiceData()));
    ret.setClosedSessions(SessionManager.INSTANCE.getClosedSessionCount());
    ret.setInactiveSessions(SessionManager.INSTANCE.getInactiveSessionCount());
    ret.setTotalSessions(SessionManager.INSTANCE.getTotalSessionCount());


    ret.setLatestEventId(ChangeEventManager.INSTANCE.getLatestEventId());
    ret.setTotalEvents(ChangeEventManager.INSTANCE.getTotalEventCount());
    ret.setEvntClosedSessionCount(ChangeEventManager.INSTANCE.getEvntClosedSessionCount());
    ret.setEvntInvalidSessionCount(ChangeEventManager.INSTANCE.getEvntInvalidSessionCount());
    ret.setEvntNoSessionCount(ChangeEventManager.INSTANCE.getEvntNoSessionCount());

    ret.setTotalDataSize(ChangeEventManager.INSTANCE.getTotalDataSize());
    ret.setCurrentDataSize(ChangeEventManager.INSTANCE.getCurrentDataSize());
    ret.setPeakDataSize(ChangeEventManager.INSTANCE.getPeakDataSize());
    ret.setPeakDataSizeAt(ChangeEventManager.INSTANCE.getPeakDataSizeTime(getServiceData()));

    ret.setTotalProducers(DataProducerManager.INSTANCE.getTotalProducersCount());

    getLogger().info("CNS Statistics(summary) - {}", ret);

    return ret;
  }

  /**
   * @param sessTypeSet set of session types
   * @param addEvents if true, event ids are also added
   * @return Statistics Detail
   */
  public CnsServerStatisticsDetail getSummaryDetail(final Set<String> sessTypeSet, final boolean addEvents) {
    CnsServerStatisticsDetail ret = new CnsServerStatisticsDetail();

    ret.setSummary(getSummary());

    ret.setSessionInfo(getSessionInfo(sessTypeSet));

    if (addEvents) {
      ret.setEventIds(ChangeEventManager.INSTANCE.getAllEventIds());
      ret.setSessionEvents(ChangeEventManager.INSTANCE.getEventsBySession());
    }

    getLogger().info("CNS Statistics(detail) - {}", ret);

    return ret;
  }

  private List<SessionInfo> getSessionInfo(final Set<String> sessTypeSet) {
    boolean addActive = true;
    boolean addInactive = true;
    boolean addClosed = true;
    if ((sessTypeSet != null) && !sessTypeSet.isEmpty()) {
      addActive = sessTypeSet.contains(SESSION_STATE.ACTIVE.name());
      addInactive = sessTypeSet.contains(SESSION_STATE.INACTIVE.name());
      addClosed = sessTypeSet.contains(SESSION_STATE.CLOSED.name());
    }

    SortedSet<SessionInfo> sessionInfoSet = new TreeSet<>((o1, o2) -> {
      int compRes = o1.getCreatedAt().compareTo(o2.getCreatedAt());
      return compRes == 0 ? o1.getSessionId().compareTo(o2.getSessionId()) : compRes;
    });

    if (addActive) {
      sessionInfoSet.addAll(SessionManager.INSTANCE.getActiveSessionsInfo(getServiceData()));
    }
    if (addClosed) {
      sessionInfoSet.addAll(SessionManager.INSTANCE.getClosedSessionsInfo(getServiceData()));
    }
    if (addInactive) {
      sessionInfoSet.addAll(SessionManager.INSTANCE.getInactiveSessionsInfo(getServiceData()));
    }
    return new ArrayList<>(sessionInfoSet);
  }


  /**
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return CnsObjectStore.getLogger();
  }

  /**
   * @param sessIdSet set of session Ids
   * @param fetchEvents if true, events of the sessions are also fetched
   * @return statistics of the given sessions
   */
  public CnsServerStatisticsSession getSummarySession(final Set<String> sessIdSet, final boolean fetchEvents) {
    CnsServerStatisticsSession ret = new CnsServerStatisticsSession();
    ret.setSessionInfo(SessionManager.INSTANCE.getSessionInfoMap(sessIdSet, getServiceData()));
    if (fetchEvents) {
      ret.setSessionEventInfo(ChangeEventManager.INSTANCE.getEventsBySession(sessIdSet, getServiceData()));
    }
    return ret;
  }

  /**
   * @param prodIdSet set of producer Ids
   * @param fetchEvents if true, events of the producers are also fetched
   * @return statistics of the given producers
   */
  public CnsServerStatisticsProducer getSummaryProducer(final Set<String> prodIdSet, final boolean fetchEvents) {
    CnsServerStatisticsProducer ret = new CnsServerStatisticsProducer();
    ret.setProducerInfo(DataProducerManager.INSTANCE.getProducerInfoMap(prodIdSet, getServiceData()));
    if (fetchEvents) {
      ret.setProducerEventInfo(getEventsByProducer(prodIdSet, getServiceData()));
    }
    return ret;
  }

  /**
   * @param prodIdSet set of producer Ids
   * @param serviceData Service Data
   * @return map. Key - producer ID, value - list of events sorted desc of event ID
   */
  public Map<String, List<EventInfo2>> getEventsByProducer(final Set<String> prodIdSet,
      final CnsServiceData serviceData) {
    return prodIdSet.stream().collect(Collectors.toMap(id -> id, id -> getEventsByProducer(id, serviceData)));
  }

  /**
   * @param producerId producer ID
   * @param serviceData Service Data
   * @return list of events sorted desc of event ID
   */
  public List<EventInfo2> getEventsByProducer(final String producerId, final CnsServiceData serviceData) {
    return ChangeEventManager.INSTANCE.getEventIdsByProducer(producerId).stream().map(this::getEventInfo2)
        .collect(Collectors.toList());

  }

  /**
   * @return all data producers
   */
  public List<ProducerInfo> getStatisticsAllProducers() {
    return DataProducerManager.INSTANCE.getAllProducers(getServiceData());
  }

  /**
   * Retrieve data producers as pages
   *
   * @param retSize return element size
   * @param startAt start index
   * @return elements with page info
   **/
  public PageResponse<ProducerInfo> getProducers(final int retSize, final int startAt) {
    return new DataPaginator<>(getStatisticsAllProducers()).createPage(startAt, retSize);
  }

  /**
   * Retrieve Event details, including producer and session details
   *
   * @param eventId Event ID
   * @return event details
   * @throws CnsServiceException error while retrieving details
   */
  public EventDetInfo getEventDetails(final Long eventId) throws CnsServiceException {
    EventDetInfo ret = new EventDetInfo();

    EventInfo evt = ChangeEventManager.INSTANCE.getEventInfo(getServiceData(), eventId);
    ret.setEventInfo(evt);
    ret.setSessionInfo(SessionManager.INSTANCE.getSessionInfo(getServiceData(), evt.getSessionId()));
    ret.setProducerInfo(DataProducerManager.INSTANCE.getProducerInfo(getServiceData(), evt.getProducerId()));

    getLogger().debug("Event {} : Event details - {}", eventId, ret);

    return ret;
  }

  /**
   * Retrieve events as pages
   *
   * @param retSize return element size
   * @param startAt start index
   * @return elements with page info
   */
  public PageResponse<EventInfo2> getEvents(final int retSize, final int startAt) {
    return new DataPaginator<>(ChangeEventManager.INSTANCE.getAllEventIds()).createPage(startAt, retSize,
        this::getEventInfo2);
  }

  private EventInfo2 getEventInfo2(final Long evtId) {
    EventInfo evt = ChangeEventManager.INSTANCE.getEventInfoWithoutEx(getServiceData(), evtId);

    EventInfo2 ret = new EventInfo2(evt);
    try {
      ret.setUser(SessionManager.INSTANCE.getSessionInfo(getServiceData(), evt.getSessionId()).getUser());
    }
    catch (CnsServiceException e) {
      getLogger().warn(e.getMessage(), e);
    }

    return ret;
  }

  /**
   * Retrieve sessions as pages
   *
   * @param sessTypeSet session types
   * @param retSize return size
   * @param startAt start index
   * @return elements with page info
   */
  public PageResponse<SessionInfo> getSessions(final Set<String> sessTypeSet, final int retSize, final int startAt) {
    return new DataPaginator<>(getSessionInfo(sessTypeSet)).createPage(startAt, retSize);
  }

  /**
   * Retrieve events for the given sesison ID, as pages
   *
   * @param sessId session ID
   * @param retSize return size
   * @param startAt start index
   * @return elements with page info
   */
  public PageResponse<EventInfo> getEventsBySession(final String sessId, final int retSize, final int startAt) {
    return new DataPaginator<>(ChangeEventManager.INSTANCE.getEventsBySession(sessId, getServiceData()))
        .createPage(startAt, retSize);
  }

  /**
   * Retrieve events for the given Producer ID, as pages
   *
   * @param prodId Producer ID
   * @param retSize return size
   * @param startAt start index
   * @return elements with page info
   */
  public PageResponse<EventInfo> getEventsByProducer(final String prodId, final int retSize, final int startAt) {
    return new DataPaginator<>(ChangeEventManager.INSTANCE.getEventIdsByProducer(prodId)).createPage(startAt, retSize,
        this::getEventInfo2);
  }

  /**
   * Retrieve events for the given source state. Only states INVALID, CLOSED and NONE are supported
   *
   * @param sourceState data source State at the time of the event
   * @param retSize return size
   * @param startAt start index
   * @return elements with page info
   */
  public PageResponse<EventInfo2> getEventsBySourceState(final String sourceState, final int retSize,
      final int startAt) {
    return new DataPaginator<>(ChangeEventManager.INSTANCE.getEventIdsBySourceState(sourceState, getServiceData()))
        .createPage(startAt, retSize, this::getEventInfo2);
  }

}
