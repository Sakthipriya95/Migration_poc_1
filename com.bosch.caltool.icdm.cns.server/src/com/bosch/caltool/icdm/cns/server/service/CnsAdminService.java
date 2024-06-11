/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import com.bosch.caltool.icdm.cns.server.annotations.CompressData;
import com.bosch.caltool.icdm.cns.server.bo.admin.stats.CnsStatisticsLoader;
import com.bosch.caltool.icdm.cns.server.exception.CnsServiceException;
import com.bosch.caltool.icdm.cns.server.utils.Utils;

/**
 * @author bne4cob
 */
@Path("/" + CnsCommonConstants.RWS_ADMIN)
public class CnsAdminService extends AbstractRestService {

  /**
   * Statistics summary
   *
   * @return Server Statistics Summary
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_STATISTICS)
  @CompressData
  public Response getStatistics() {
    CnsServerStatisticsSummary getStatisticsDetail = new CnsStatisticsLoader(getServiceData()).getSummary();
    return Response.ok(getStatisticsDetail).build();
  }

  /**
   * Statistics detailed
   *
   * @param sessStatus sessions with given status will be included. Separated by comma
   * @param fetchEvents fetch Events flag
   * @return Detailed Server Statistics
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_STATISTICS_DET)
  @CompressData
  public Response getStatisticsDetail(@QueryParam(value = CnsCommonConstants.QP_SESSION_TYPES) final String sessStatus,
      @QueryParam(value = CnsCommonConstants.QP_EVENT_DETAILS) final boolean fetchEvents) {

    Set<String> sessTypeSet = new HashSet<>();
    if (!Utils.isEmpty(sessStatus)) {
      sessTypeSet.addAll(Arrays.asList(sessStatus.toUpperCase(Locale.ENGLISH).split(",")));
    }

    CnsServerStatisticsDetail ret =
        new CnsStatisticsLoader(getServiceData()).getSummaryDetail(sessTypeSet, fetchEvents);

    return Response.ok(ret).build();
  }

  /**
   * Statistics of the given sessions
   *
   * @param sessIdSet set of session IDs
   * @param fetchEvents if true, events of the sessions are also fetched
   * @return Server Statistics of the sessions
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_STATISTICS_SESSION)
  @CompressData
  public Response getStatisticsSession(
      @QueryParam(value = CnsCommonConstants.QP_SESSION_ID) final Set<String> sessIdSet,
      @QueryParam(value = CnsCommonConstants.QP_EVENT_DETAILS) final boolean fetchEvents) {
    CnsServerStatisticsSession ret =
        new CnsStatisticsLoader(getServiceData()).getSummarySession(sessIdSet, fetchEvents);
    return Response.ok(ret).build();
  }

  /**
   * Statistics of the given producers
   *
   * @param prodIdSet set of producer IDs
   * @param fetchEvents if true, events of the producers are also fetched
   * @return Server Statistics of the producers
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_STATISTICS_PRODUCER)
  @CompressData
  public Response getStatisticsProducer(
      @QueryParam(value = CnsCommonConstants.QP_PRODUCER_ID) final Set<String> prodIdSet,
      @QueryParam(value = CnsCommonConstants.QP_EVENT_DETAILS) final boolean fetchEvents) {
    CnsServerStatisticsProducer ret =
        new CnsStatisticsLoader(getServiceData()).getSummaryProducer(prodIdSet, fetchEvents);
    return Response.ok(ret).build();
  }

  /**
   * Retrieve Event details, including producer and session details
   *
   * @param eventId Event ID
   * @return event details
   * @throws CnsServiceException error from BO layer
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_EVENT_DET)
  @CompressData
  public Response getEventDetails(@QueryParam(value = CnsCommonConstants.QP_EVENT_ID) final Long eventId)
      throws CnsServiceException {

    EventDetInfo ret = new CnsStatisticsLoader(getServiceData()).getEventDetails(eventId);
    return Response.ok(ret).build();

  }

  /**
   * Retrieve Producers page wise
   *
   * @param retSize return element size
   * @param startAt start index
   * @return Producers
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_PRODUCERS)
  @CompressData
  public Response getAllProducers(@QueryParam(value = CnsCommonConstants.QP_RET_SIZE) final int retSize,
      @QueryParam(value = CnsCommonConstants.QP_RET_START_AT) final int startAt) {

    PageResponse<ProducerInfo> ret = new CnsStatisticsLoader(getServiceData()).getProducers(retSize, startAt);
    return Response.ok(ret).build();
  }

  /**
   * Retrieve Consumers
   *
   * @param states session states
   * @param retSize return element size
   * @param startAt start index
   * @return Consumers
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_CONSUMERS)
  @CompressData
  public Response getConsumers(@QueryParam(value = CnsCommonConstants.QP_SESSION_TYPES) final String states,
      @QueryParam(value = CnsCommonConstants.QP_RET_SIZE) final int retSize,
      @QueryParam(value = CnsCommonConstants.QP_RET_START_AT) final int startAt) {

    Set<String> sessTypeSet = new HashSet<>();
    if (!Utils.isEmpty(states)) {
      sessTypeSet.addAll(Arrays.asList(states.toUpperCase(Locale.ENGLISH).split(",")));
    }

    PageResponse<SessionInfo> ret =
        new CnsStatisticsLoader(getServiceData()).getSessions(sessTypeSet, retSize, startAt);
    return Response.ok(ret).build();

  }

  /**
   * Retrieve Events
   *
   * @param retSize return element size
   * @param startAt start index
   * @return events
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_EVENTS)
  @CompressData
  public Response getEvents(@QueryParam(value = CnsCommonConstants.QP_RET_SIZE) final int retSize,
      @QueryParam(value = CnsCommonConstants.QP_RET_START_AT) final int startAt) {

    PageResponse<EventInfo2> ret = new CnsStatisticsLoader(getServiceData()).getEvents(retSize, startAt);
    return Response.ok(ret).build();

  }

  /**
   * Retrieve Events for the given session, with paging
   *
   * @param sessId session ID
   * @param retSize return element size
   * @param startAt start index
   * @return events
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_SESSION_EVENTS)
  @CompressData
  public Response getConsumerEvents(@QueryParam(value = CnsCommonConstants.QP_SESSION_ID) final String sessId,
      @QueryParam(value = CnsCommonConstants.QP_RET_SIZE) final int retSize,
      @QueryParam(value = CnsCommonConstants.QP_RET_START_AT) final int startAt) {

    PageResponse<EventInfo> ret =
        new CnsStatisticsLoader(getServiceData()).getEventsBySession(sessId, retSize, startAt);
    return Response.ok(ret).build();

  }

  /**
   * Retrieve Events for the given producer, with paging
   *
   * @param prodId producer ID
   * @param retSize return element size
   * @param startAt start index
   * @return events
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_PRODUCER_EVENTS)
  @CompressData
  public Response getProducerEvents(@QueryParam(value = CnsCommonConstants.QP_PRODUCER_ID) final String prodId,
      @QueryParam(value = CnsCommonConstants.QP_RET_SIZE) final int retSize,
      @QueryParam(value = CnsCommonConstants.QP_RET_START_AT) final int startAt) {

    PageResponse<EventInfo> ret =
        new CnsStatisticsLoader(getServiceData()).getEventsByProducer(prodId, retSize, startAt);
    return Response.ok(ret).build();

  }

  /**
   * Retrieve events for the given source state. Only states INVALID, CLOSED and NONE are supported
   *
   * @param sourceState data source State at the event time
   * @param retSize return element size
   * @param startAt start index
   * @return events
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_EVENTS_BY_SOURCE_STATE)
  @CompressData
  public Response getEventsBySourceState(
      @QueryParam(value = CnsCommonConstants.QP_SOURCE_STATE) final String sourceState,
      @QueryParam(value = CnsCommonConstants.QP_RET_SIZE) final int retSize,
      @QueryParam(value = CnsCommonConstants.QP_RET_START_AT) final int startAt) {

    PageResponse<EventInfo2> ret =
        new CnsStatisticsLoader(getServiceData()).getEventsBySourceState(sourceState, retSize, startAt);
    return Response.ok(ret).build();

  }

}

