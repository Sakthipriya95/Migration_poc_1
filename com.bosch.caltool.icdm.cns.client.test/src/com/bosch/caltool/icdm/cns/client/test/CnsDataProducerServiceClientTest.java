/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.cns.client.CnsClientConfiguration;
import com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.cns.client.test.model.ChangeEventGenerator;
import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
import com.bosch.caltool.icdm.cns.common.model.EventInfoWithData;


/**
 * @author bne4cob
 */
public class CnsDataProducerServiceClientTest extends AbstractCnsServiceClientTest {


  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataProducerServiceClient#run()}.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testRun1() throws CnsServiceClientException {

    CnsDataConsumerServiceClient consClient = new CnsDataConsumerServiceClient();
    String sessId = consClient.createSession(0);

    ChangeEventGenerator.INSTANCE.createAndSendEvent(-9999L, "567849d2-6c47-424b-1234-3b708ede5f04", sessId);

    verifyEvent(sessId, -9999L, "ACTIVE", "567849d2-6c47-424b-1234-3b708ede5f04");

    consClient.closeSession(sessId);

  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataProducerServiceClient#run()}.
   * <p>
   * Use an invalid session ID
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testRun2() throws CnsServiceClientException {
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-9998L, "567849d2-1234-414b-b307-3b708ede5f04", "INVALID_SESS");

    verifyEvent(CnsCommonConstants.DEFAULT_SESSION_ID, -9998L, "INVALID", "567849d2-1234-414b-b307-3b708ede5f04");
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataProducerServiceClient#run()}.
   * <p>
   * Use a closed session ID
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testRun3() throws CnsServiceClientException {

    CnsDataConsumerServiceClient consClient = new CnsDataConsumerServiceClient();
    String sessId = consClient.createSession(0);
    consClient.closeSession(sessId);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-9997L, "567849d2-6c47-414b-1234-3b708ede5f04", sessId);

    this.thrown.expectMessage("CNS-2002");
    verifyEvent(sessId, -9997L, "CLOSED", "567849d2-6c47-414b-1234-3b708ede5f04");

  }

  private void verifyEvent(final String sessionId, final Long eventId, final String expectedEventSessState,
      final String serviceId)
      throws CnsServiceClientException {
    CnsDataConsumerServiceClient client = new CnsDataConsumerServiceClient();
    CnsClientConfiguration config = CnsClientConfiguration.getDefaultConfig().createCopy();
    config.setSessionId(sessionId);
    client.setClientConfiguration(config);

    Map<Long, EventInfoWithData> sessEvents = client.getEventsOfCurrentSession();

    assertFalse("Events current session not null or empty", (sessEvents == null) || sessEvents.isEmpty());
    EventInfoWithData event = sessEvents.get(eventId);
    assertNotNull("session contains event", event);
    assertEquals("Event's session state", expectedEventSessState, event.getSessionState());
    assertEquals("Event's service id", serviceId, event.getServiceId());
  }


}
