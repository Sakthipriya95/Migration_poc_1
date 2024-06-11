/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
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


/**
 * @author bne4cob
 */
public class CnsAdminServiceClient extends AbstractCnsServiceClient {

  /**
   * Constructor
   */
  public CnsAdminServiceClient() {
    super(CnsCommonConstants.RWS_ADMIN);
  }


  /**
   * Statistics summary
   *
   * @return Server Statistics Summary
   * @throws CnsServiceClientException error while executing service
   */
  public CnsServerStatisticsSummary getStatistics() throws CnsServiceClientException {
    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_STATISTICS);
    CnsServerStatisticsSummary ret = get(wsTarget, CnsServerStatisticsSummary.class);
    getLogger().debug("CNS Server statistics (summary) = {}", ret);
    return ret;
  }

  /**
   * Statistics detailed
   *
   * @return Detailed Server Statistics
   * @throws CnsServiceClientException error while executing service
   */
  public CnsServerStatisticsDetail getStatisticsDetail() throws CnsServiceClientException {
    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_STATISTICS_DET);
    CnsServerStatisticsDetail ret = get(wsTarget, CnsServerStatisticsDetail.class);
    getLogger().debug("CNS Server statistics (detailed)  = {}", ret);
    return ret;
  }

  /**
   * Statistics of the given sessions
   *
   * @param sessIdSet set of session IDs
   * @param fetchEvents if true, events of the sessions are also fetched
   * @return Server Statistics of the sessions
   * @throws CnsServiceClientException error while executing service
   */
  public CnsServerStatisticsSession getStatisticsSession(final Set<String> sessIdSet, final boolean fetchEvents)
      throws CnsServiceClientException {
    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_STATISTICS_SESSION);
    for (String sid : sessIdSet) {
      wsTarget = wsTarget.queryParam(CnsCommonConstants.QP_SESSION_ID, sid);
    }
    wsTarget = wsTarget.queryParam(CnsCommonConstants.QP_EVENT_DETAILS, fetchEvents);

    CnsServerStatisticsSession ret = get(wsTarget, CnsServerStatisticsSession.class);
    getLogger().debug("CNS Server statistics (session)  = {}", ret);
    return ret;
  }

  /**
   * Statistics of the given producers
   *
   * @param prodIdSet set of producer IDs
   * @param fetchEvents if true, events of the producers are also fetched
   * @return Server Statistics of the producers
   * @throws CnsServiceClientException error while executing service
   */
  public CnsServerStatisticsProducer getStatisticsProducer(final Set<String> prodIdSet, final boolean fetchEvents)
      throws CnsServiceClientException {
    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_STATISTICS_PRODUCER);
    for (String pid : prodIdSet) {
      wsTarget = wsTarget.queryParam(CnsCommonConstants.QP_PRODUCER_ID, pid);
    }
    wsTarget = wsTarget.queryParam(CnsCommonConstants.QP_EVENT_DETAILS, fetchEvents);

    CnsServerStatisticsProducer ret = get(wsTarget, CnsServerStatisticsProducer.class);
    getLogger().debug("CNS Server statistics (producer)  = {}", ret);
    return ret;
  }

  /**
   * Retrieve data producers, page wise
   *
   * @param retSize return element size
   * @param startAt start index
   * @return producers with the page info
   * @throws CnsServiceClientException error while executing service
   */
  public PageResponse<ProducerInfo> getAllProducers(final int retSize, final int startAt)
      throws CnsServiceClientException {

    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_PRODUCERS)
        .queryParam(CnsCommonConstants.QP_RET_SIZE, retSize).queryParam(CnsCommonConstants.QP_RET_START_AT, startAt);

    GenericType<PageResponse<ProducerInfo>> type = new GenericType<PageResponse<ProducerInfo>>() {};
    PageResponse<ProducerInfo> ret = get(wsTarget, type);

    getLogger().debug("Producers  = {}", ret);

    return ret;

  }


  /**
   * Retrieve Event details, including producer and session details
   *
   * @param eventId Event ID
   * @return event details
   * @throws CnsServiceClientException error while executing service
   */
  public EventDetInfo getEventDetails(final long eventId) throws CnsServiceClientException {
    WebTarget wsTarget =
        getWsBase().path(CnsCommonConstants.RWS_EVENT_DET).queryParam(CnsCommonConstants.QP_EVENT_ID, eventId);
    EventDetInfo ret = get(wsTarget, EventDetInfo.class);
    getLogger().debug("Event Details  = {}", ret);
    return ret;
  }

  /**
   * Retrieve Consumers
   *
   * @param retSize return element size
   * @param startAt start index
   * @return consumers with the page info
   * @throws CnsServiceClientException error while executing service
   */
  public PageResponse<SessionInfo> getConsumers(final int retSize, final int startAt) throws CnsServiceClientException {

    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_CONSUMERS)
        .queryParam(CnsCommonConstants.QP_RET_SIZE, retSize).queryParam(CnsCommonConstants.QP_RET_START_AT, startAt);

    GenericType<PageResponse<SessionInfo>> type = new GenericType<PageResponse<SessionInfo>>() {};
    PageResponse<SessionInfo> ret = get(wsTarget, type);

    getLogger().debug("Consumers  = {}", ret);

    return ret;
  }

  /**
   * Retrieve Events
   *
   * @param retSize return element size
   * @param startAt start index
   * @return events with the page info
   * @throws CnsServiceClientException error while executing service
   */
  public PageResponse<EventInfo2> getEvents(final int retSize, final int startAt) throws CnsServiceClientException {

    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_EVENTS)
        .queryParam(CnsCommonConstants.QP_RET_SIZE, retSize).queryParam(CnsCommonConstants.QP_RET_START_AT, startAt);

    GenericType<PageResponse<EventInfo2>> type = new GenericType<PageResponse<EventInfo2>>() {};
    PageResponse<EventInfo2> ret = get(wsTarget, type);

    getLogger().debug("Events  = {}", ret);

    return ret;
  }

  /**
   * Retrieve Events for the given session, with paging
   *
   * @param sessId session ID
   * @param retSize return element size
   * @param startAt start index
   * @return events with the page info
   * @throws CnsServiceClientException error while executing service
   */
  public PageResponse<EventInfo> getConsumerEvents(final String sessId, final int retSize, final int startAt)
      throws CnsServiceClientException {

    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_SESSION_EVENTS)
        .queryParam(CnsCommonConstants.QP_SESSION_ID, sessId).queryParam(CnsCommonConstants.QP_RET_SIZE, retSize)
        .queryParam(CnsCommonConstants.QP_RET_START_AT, startAt);

    GenericType<PageResponse<EventInfo>> type = new GenericType<PageResponse<EventInfo>>() {};
    PageResponse<EventInfo> ret = get(wsTarget, type);

    getLogger().debug("Events for session {} = {}", sessId, ret);

    return ret;
  }

  /**
   * Retrieve Events for the given producer, with paging
   *
   * @param prodId producer ID
   * @param retSize return element size
   * @param startAt start index
   * @return events with the page info
   * @throws CnsServiceClientException error while executing service
   */
  public PageResponse<EventInfo2> getProducerEvents(final String prodId, final int retSize, final int startAt)
      throws CnsServiceClientException {

    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_PRODUCER_EVENTS)
        .queryParam(CnsCommonConstants.QP_PRODUCER_ID, prodId).queryParam(CnsCommonConstants.QP_RET_SIZE, retSize)
        .queryParam(CnsCommonConstants.QP_RET_START_AT, startAt);

    GenericType<PageResponse<EventInfo2>> type = new GenericType<PageResponse<EventInfo2>>() {};
    PageResponse<EventInfo2> ret = get(wsTarget, type);

    getLogger().debug("Events for producer {} = {}", prodId, ret);

    return ret;
  }

  /**
   * Retrieve events for the given source state. Only states INVALID, CLOSED and NONE are supported
   *
   * @param sourceState data source State at the event time
   * @param retSize return element size
   * @param startAt start index
   * @return events with the page info
   * @throws CnsServiceClientException error while executing service
   */
  public PageResponse<EventInfo2> getEventsBySourceState(final String sourceState, final int retSize, final int startAt)
      throws CnsServiceClientException {

    WebTarget wsTarget = getWsBase().path(CnsCommonConstants.RWS_EVENTS_BY_SOURCE_STATE)
        .queryParam(CnsCommonConstants.QP_SOURCE_STATE, sourceState).queryParam(CnsCommonConstants.QP_RET_SIZE, retSize)
        .queryParam(CnsCommonConstants.QP_RET_START_AT, startAt);

    GenericType<PageResponse<EventInfo2>> type = new GenericType<PageResponse<EventInfo2>>() {};
    PageResponse<EventInfo2> ret = get(wsTarget, type);

    getLogger().debug("Events for source State {} = {}", sourceState, ret);

    return ret;
  }


}
