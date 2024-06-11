/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.Future;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.bosch.caltool.datamodel.core.cns.ChangeEvent;
import com.bosch.caltool.datamodel.core.cns.ICnsAsyncMessage;
import com.bosch.caltool.icdm.cns.client.utils.ZipUtils;
import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;

/**
 * @author bne4cob
 */
public class CnsDataProducerServiceClient extends AbstractCnsServiceClient implements ICnsAsyncMessage {

  private ChangeEvent event;

  /**
   * Constructor
   */
  // Note : Empty constructor MANDATORY for dyamic instantiation
  public CnsDataProducerServiceClient() {
    super(CnsCommonConstants.RWS_DATA_PRODUCER);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    getLogger().info("Sending change event to CNS server. Event ID = {}, Consumer-Source ID = {}",
        this.event.getChangeId(), getSessionId());

    WebTarget target = getWsBase().path(CnsCommonConstants.RWS_EVENT);
    getLogger().debug("Asynchronous service : target - {}", target);

    try {
      Builder request = target.request().header(CnsCommonConstants.REQ_CHANGE_EVENT_ID, this.event.getChangeId())
          .header(CnsCommonConstants.REQ_SERVICE_ID, this.event.getServiceId());
      Future<Response> responseFuture = createFuture(request, serializeEvent(), new InvocationCallback<Response>() {

        @Override
        public void completed(final Response response) {
          getLogger().info("CNS event registered. response status code = {}", response.getStatus());
        }

        @Override
        public void failed(final Throwable throwable) {
          getLogger().error("CNS event registation failed. " + throwable.getMessage(), throwable);
        }
      });

      // get() waits for the response to be ready
      String response = getResponse(responseFuture, String.class);

      getLogger().info("Change event sent to CNS server. Event Id = {}, Response received. {}",
          this.event.getChangeId(), response);
    }
    catch (CnsServiceClientException e) {
      getLogger().error("Error while adding CNS event : " + e.getMessage(), e);
    }

  }


  /**
   * @return serialized data
   * @throws CnsServiceClientException
   */
  private byte[] serializeEvent() throws CnsServiceClientException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      getLogger().debug("Serialize change event started. Event ID = {}, Consumer-Source ID = {}",
          this.event.getChangeId(), this.event.getClientSessionId());

      outputStm.writeObject(this.event);
      byte[] dataArr = out.toByteArray();

      getLogger().debug("  Serialization completed. Data size = {}", dataArr.length);

      byte[] zipDataArr = ZipUtils.zip(dataArr);

      getLogger().debug(
          "  Zipping serialized change event completed. Event ID = {}, Consumer-Source ID = {}, Data size = {}",
          this.event.getChangeId(), this.event.getClientSessionId(), zipDataArr.length);

      return zipDataArr;
    }
    catch (IOException e) {
      throw new CnsServiceClientException("2000", e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setChangeEvent(final ChangeEvent event) {
    this.event = event;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSessionId(final String sessionId) {
    createLocalConfiguration().setSessionId(sessionId);
  }

}
