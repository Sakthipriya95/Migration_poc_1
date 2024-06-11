/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
import com.bosch.caltool.icdm.cns.server.annotations.CompressData;
import com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager;
import com.bosch.caltool.icdm.cns.server.bo.session.SessionManager;
import com.bosch.caltool.icdm.cns.server.exception.CnsServiceException;

/**
 * @author bne4cob
 */
@Path("/" + CnsCommonConstants.RWS_DATA_CONSUMER)
public class CnsDataConsumerService extends AbstractRestService {


  /**
   * Get all events after the given event ID
   *
   * @param eventId Change event ID
   * @return Map - Key - event ID, value data as byte[]. The events from the current session are excluded
   * @throws CnsServiceException error while executing service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_EVENTS_AFTER)
  @CompressData
  public Response getEventsAfter(@QueryParam(value = CnsCommonConstants.QP_EVENT_ID) final Long eventId)
      throws CnsServiceException {
    return Response.ok(ChangeEventManager.INSTANCE.getEventsAfter(getClientSessionIdFromRequest(), eventId, true))
        .build();
  }

  /**
   * @return latest event ID
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_LATEST_EVENT_ID)
  @CompressData
  public Response getLatestEventId() {
    return Response.ok(ChangeEventManager.INSTANCE.getLatestEventId()).build();
  }

  /**
   * Get all events for current client session
   *
   * @param eventIdStart start event ID
   * @return Map - Key - event ID, value data as byte[]
   * @throws CnsServiceException error while executing service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_EVENTS_CUR_SESSION)
  @CompressData
  public Response getEventsOfCurrentSession(
      @QueryParam(value = CnsCommonConstants.QP_EVENT_ID_START) final Long eventIdStart)
      throws CnsServiceException {
    return Response.ok(
        ChangeEventManager.INSTANCE.getEventsOfSession(getServiceData(), getClientSessionIdFromRequest(), eventIdStart))
        .build();
  }

  /**
   * Create a CNS session
   *
   * @param listenPort client's CNS listener port
   * @return session ID
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_SESSION)
  @CompressData
  public Response createSession(@QueryParam(value = CnsCommonConstants.QP_LISTENING_PORT) final int listenPort) {
    return Response.ok(SessionManager.INSTANCE.createSession(getClientUser(), getClientIp(), listenPort)).build();
  }

  /**
   * Close a CNS session
   *
   * @param sessionId session ID
   * @return OK
   */
  @DELETE
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("/" + CnsCommonConstants.RWS_SESSION)
  @CompressData
  public Response closeSession(@QueryParam(value = CnsCommonConstants.QP_SESSION_ID) final String sessionId) {
    SessionManager.INSTANCE.closeSession(sessionId);
    return Response.ok().build();
  }
}
