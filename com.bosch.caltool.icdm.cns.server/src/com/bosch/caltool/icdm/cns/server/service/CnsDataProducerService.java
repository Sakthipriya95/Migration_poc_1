/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
import com.bosch.caltool.icdm.cns.server.annotations.CompressData;
import com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager;
import com.bosch.caltool.icdm.cns.server.bo.session.DataProducerManager;
import com.bosch.caltool.icdm.cns.server.utils.Utils;

/**
 * @author bne4cob
 */
@Path("/" + CnsCommonConstants.RWS_DATA_PRODUCER)
public class CnsDataProducerService extends AbstractRestService {

  /**
   * @param changeData change data as byte array
   * @param asyncResponse response
   * @deprecated use {@link #addChangeEvent(byte[], AsyncResponse)} instead
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  @Deprecated
  public void addChangeData(final byte[] changeData, @Suspended final AsyncResponse asyncResponse) {
    addChangeEvent(changeData, asyncResponse);
  }

  /**
   * @param eventBytes change data as byte array
   * @param asyncResponse response
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  @Path("/" + CnsCommonConstants.RWS_EVENT)
  public void addChangeEvent(final byte[] eventBytes, @Suspended final AsyncResponse asyncResponse) {

    final int chLength = eventBytes == null ? 0 : eventBytes.length;

    if (eventBytes == null) {
      getLogger().warn("Change event received. Change Data Size = null");
    }
    else if (chLength == 0) {
      getLogger().warn("Change event received. Change Data Size = 0");
    }
    else {
      getLogger().debug("Change event received. Change Data Size = {}", chLength);
    }

    asyncResponse.register(new CompletionCallback() {

      @Override
      public void onComplete(final Throwable throwable) {
        if (throwable != null) {
          getLogger().error(throwable.getMessage(), throwable);
        }
      }
    });

    // Add the event details
    new Thread(() -> {
      Long eventId = getEventIdFromRequest();
      String serviceId = getServiceIdFromRequest();
      String producerId = DataProducerManager.INSTANCE.getOrCreate(getClientIp(), getProducerPort(), chLength);

      getLogger().debug("Change Event ID = {}, Producer ID = {}, SID = {}, Change Data Size = {}", eventId, producerId,
          serviceId, chLength);

      ChangeEventManager.INSTANCE.addEvent(eventId, getClientSessionIdFromRequest(), producerId, serviceId, eventBytes);

      asyncResponse.resume("OK");
    }).start();

  }

  /**
   * @return remote port number
   */
  private int getProducerPort() {
    String hVal = getRequest().getHeader(CnsCommonConstants.REQHDR_PRODUCER_PORT);
    return Utils.isInteger(hVal) ? Integer.parseInt(hVal) : 0;
  }

  private Long getEventIdFromRequest() {
    String parameter = getRequest().getHeader(CnsCommonConstants.REQ_CHANGE_EVENT_ID);
    if ((parameter == null) || parameter.isEmpty()) {
      throw new IllegalArgumentException("Change Event ID is missing");
    }
    return Long.valueOf(parameter);
  }

  private String getServiceIdFromRequest() {
    String parameter = getRequest().getHeader(CnsCommonConstants.REQ_SERVICE_ID);
    if ((parameter == null) || parameter.isEmpty()) {
      throw new IllegalArgumentException("Service ID is missing");
    }
    return parameter;
  }
}

