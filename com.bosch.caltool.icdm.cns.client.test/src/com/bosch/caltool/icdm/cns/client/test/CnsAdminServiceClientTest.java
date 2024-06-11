/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient;
import com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.cns.client.test.model.ChangeEventGenerator;
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
public class CnsAdminServiceClientTest extends AbstractCnsServiceClientTest {

  /**
   *
   */
  private static final long EVENT_ID = -7999L;
  private static final String SERVICE_ID = UUID.randomUUID().toString();
  private static String sessId;
  private static String prodId;

  /**
   * Create some sample events
   *
   * @throws CnsServiceClientException error from service
   */
  @BeforeClass
  public static void createEvents() throws CnsServiceClientException {
    CnsDataConsumerServiceClient consClient = new CnsDataConsumerServiceClient();
    sessId = consClient.createSession(0);

    ChangeEventGenerator.INSTANCE.createAndSendEvent(EVENT_ID, SERVICE_ID, sessId);

    ChangeEventGenerator.INSTANCE.createAndSendEvent(-7998L, "icea7d87-ee8b-4857-9cb6-78a39347c350", sessId);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-7997L, "ycea7d87-ee8b-1234-9cb6-78a39347c350", sessId);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-7996L, "ucea7d87-ee8b-5678-9cb6-78a39347c350", sessId);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-7995L, "bcea7d87-ee8b-0987-9cb6-78a39347c350", sessId);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-7994L, "acea7d87-ee8b-4857-9cb6-78a39347c350", sessId);

    consClient.closeSession(sessId);

    // Create an event with closed session ID
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-7993L, "abea7d87-ee8b-1234-9cb6-78a39347c350", sessId);

    // Create an event with invalid session id
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-7992L, "abea7d87-ee8b-5678-9cb6-78a39347c350", "INVALID_SESSION");

    // Create an event with invalid session id
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-7991L, "abea7d87-ee8b-8765-9cb6-78a39347c350", null);


    // Get the producer ID created
    EventDetInfo lastEvt = new CnsAdminServiceClient().getEventDetails(EVENT_ID);
    prodId = lastEvt.getEventInfo().getProducerId();
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getStatistics()}.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetStatistics() throws CnsServiceClientException {
    CnsServerStatisticsSummary ret = new CnsAdminServiceClient().getStatistics();

    assertNotNull("Statistics summary not null", ret);
    getLogger().debug("Result - {}", ret);

  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getStatisticsDetail()}.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetStatisticsDetail() throws CnsServiceClientException {
    CnsServerStatisticsDetail ret = new CnsAdminServiceClient().getStatisticsDetail();

    assertNotNull("Statistics Detail not null", ret);
    getLogger().debug("Result - {}", ret);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getStatisticsSession(Set, boolean) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetStatisticsSession() throws CnsServiceClientException {

    CnsDataConsumerServiceClient consClient = new CnsDataConsumerServiceClient();
    String sessId1 = consClient.createSession(0);
    String sessId2 = consClient.createSession(1);
    Set<String> sidSet = new HashSet<>(Arrays.asList(sessId1, sessId2));

    CnsServerStatisticsSession ret = new CnsAdminServiceClient().getStatisticsSession(sidSet, true);

    assertNotNull("Statistics session not null", ret);
    getLogger().debug("Result - {}", ret);
    assertFalse("Session Info available", (ret.getSessionInfo() == null) || ret.getSessionInfo().isEmpty());
    SessionInfo sInfo = ret.getSessionInfo().get(sessId1);
    getLogger().debug("Session Info - {}", sInfo);
    assertNotNull("sess info not null", sInfo);

    consClient.closeSession(sessId1);
    consClient.closeSession(sessId2);

  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getStatisticsProducer(Set, boolean) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetStatisticsProducer() throws CnsServiceClientException {
    Set<String> pidSet = new HashSet<>(Arrays.asList(prodId));

    CnsServerStatisticsProducer ret = new CnsAdminServiceClient().getStatisticsProducer(pidSet, true);

    assertNotNull("Statistics producer not null", ret);
    getLogger().debug("Result - {}", ret);
    assertFalse("Producer Info available", (ret.getProducerInfo() == null) || ret.getProducerInfo().isEmpty());
    ProducerInfo pInfo = ret.getProducerInfo().get(prodId);
    getLogger().debug("Producer Info - {}", pInfo);
    assertNotNull("producer info not null", pInfo);
    assertFalse("Producer events available", ret.getProducerEventInfo().get(prodId).isEmpty());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getAllProducers(int, int) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetAllProducers() throws CnsServiceClientException {

    CnsAdminServiceClient client = new CnsAdminServiceClient();
    PageResponse<ProducerInfo> ret = client.getAllProducers(3, 0);

    assertNotNull("Ret not null ", ret);
    assertNotNull("Page Info not null ", ret.getPageInfo());
    assertFalse("Producers not null or empty", (ret.getElementList() == null) || ret.getElementList().isEmpty());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getEventDetails(long) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetEventDetails() throws CnsServiceClientException {
    EventDetInfo ret = new CnsAdminServiceClient().getEventDetails(EVENT_ID);

    assertNotNull("Ret not null ", ret);
    assertNotNull("Event Info not null ", ret.getEventInfo());
    assertNotNull("Producer Info not null ", ret.getProducerInfo());
    assertNotNull("Session Info not null ", ret.getSessionInfo());

    getLogger().debug("Event Info  - {}", ret.getEventInfo());
    getLogger().debug("Producer Info  - {}", ret.getProducerInfo());
    getLogger().debug("Session Info  - {}", ret.getSessionInfo());

  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getConsumers(int, int) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetConsumers() throws CnsServiceClientException {
    CnsAdminServiceClient client = new CnsAdminServiceClient();
    PageResponse<SessionInfo> ret = client.getConsumers(3, 0);

    assertNotNull("Ret not null ", ret);
    assertNotNull("Page Info not null ", ret.getPageInfo());
    assertFalse("Consumers not null or empty", (ret.getElementList() == null) || ret.getElementList().isEmpty());

  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getEvents(int, int) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetEvents() throws CnsServiceClientException {
    CnsAdminServiceClient client = new CnsAdminServiceClient();
    PageResponse<EventInfo2> ret = client.getEvents(3, 0);

    assertNotNull("Ret not null ", ret);
    assertNotNull("Page Info not null ", ret.getPageInfo());
    assertFalse("Events not null or empty", (ret.getElementList() == null) || ret.getElementList().isEmpty());

  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getConsumerEvents(String, int, int) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetConsumerEvents() throws CnsServiceClientException {
    CnsAdminServiceClient client = new CnsAdminServiceClient();
    PageResponse<EventInfo> ret = client.getConsumerEvents(sessId, 3, 0);

    assertNotNull("Ret not null ", ret);
    assertNotNull("Page Info not null ", ret.getPageInfo());
    assertFalse("Events not null or empty", (ret.getElementList() == null) || ret.getElementList().isEmpty());

  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getProducerEvents(String, int, int) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetProducerEvents() throws CnsServiceClientException {
    CnsAdminServiceClient client = new CnsAdminServiceClient();
    PageResponse<EventInfo2> ret = client.getProducerEvents(prodId, 3, 0);

    assertNotNull("Ret not null ", ret);
    assertNotNull("Page Info not null ", ret.getPageInfo());
    assertFalse("Events not null or empty", (ret.getElementList() == null) || ret.getElementList().isEmpty());

  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.client.CnsAdminServiceClient#getEventsBySourceState(String, int, int) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetEventsBySourceState() throws CnsServiceClientException {
    CnsAdminServiceClient client = new CnsAdminServiceClient();
    PageResponse<EventInfo2> ret;

    // Check session state 'CLOSED'
    ret = client.getEventsBySourceState("CLOSED", 3, 0);
    assertNotNull("Ret not null ", ret);
    assertNotNull("Page Info not null ", ret.getPageInfo());
    assertFalse("Events not null or empty", (ret.getElementList() == null) || ret.getElementList().isEmpty());

    // Check session state 'INVALID'
    ret = client.getEventsBySourceState("INVALID", 3, 0);
    assertNotNull("Ret not null ", ret);
    assertNotNull("Page Info not null ", ret.getPageInfo());
    assertFalse("Events not null or empty", (ret.getElementList() == null) || ret.getElementList().isEmpty());

    // Check session state 'NONE'
    ret = client.getEventsBySourceState("NONE", 3, 0);
    assertNotNull("Ret not null ", ret);
    assertNotNull("Page Info not null ", ret.getPageInfo());
    assertFalse("Events not null or empty", (ret.getElementList() == null) || ret.getElementList().isEmpty());
  }

}
