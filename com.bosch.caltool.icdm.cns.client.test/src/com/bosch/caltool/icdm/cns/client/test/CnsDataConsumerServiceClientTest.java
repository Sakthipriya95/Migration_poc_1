/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.datamodel.core.cns.ChangeEvent;
import com.bosch.caltool.icdm.cns.client.CnsClientConfiguration;
import com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.cns.client.test.model.ChangeEventGenerator;
import com.bosch.caltool.icdm.cns.common.model.EventInfoWithData;


/**
 * @author bne4cob
 */
public class CnsDataConsumerServiceClientTest extends AbstractCnsServiceClientTest {


  /**
   *
   */
  private static final String INVALID_SESS_ID = "INVALID_SESS";
  private static String sessId1Open;// Open session
  private static String sessId2Closed;// Closed session


  /**
   * Expected exceptions
   */
  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataProducerServiceClient#run()}.
   *
   * @throws CnsServiceClientException error from service
   */
  @BeforeClass
  public static void createEvents() throws CnsServiceClientException {

    CnsDataConsumerServiceClient consClient = new CnsDataConsumerServiceClient();
    sessId1Open = consClient.createSession(0);
    sessId2Closed = consClient.createSession(1);

    ChangeEventGenerator.INSTANCE.createAndSendEvent(-8999L, "751149d2-6c47-414b-1234-3b708ede5f04", sessId1Open);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-8998L, "751149d2-6c47-414b-5678-3b708ede5f04", sessId2Closed);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-8997L, "751149d2-6c47-3456-b307-3b708ede5f04", sessId1Open);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-8996L, "123449d2-6c47-414b-b307-3b708ede5f04", sessId2Closed);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-8995L, "567849d2-6c47-414b-b307-3b708ede5f04", sessId1Open);
    ChangeEventGenerator.INSTANCE.createAndSendEvent(-8994L, "567849d2-6c47-1234-b307-3b708ede5f04", sessId2Closed);

    consClient.closeSession(sessId2Closed);

  }


  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient#getEventsAfter(java.lang.Long)}.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetEventsAfter1() throws CnsServiceClientException {
    CnsDataConsumerServiceClient client = new CnsDataConsumerServiceClient();

    CnsClientConfiguration config = CnsClientConfiguration.getDefaultConfig().createCopy();
    config.setSessionId(sessId1Open);
    client.setClientConfiguration(config);

    Map<Long, byte[]> eventsAfter = client.getEventsAfter(-10000L);

    assertNotNull("events after not null", eventsAfter);
    assertFalse("Events after not empty", eventsAfter.isEmpty());

    Entry<Long, byte[]> firstElement = eventsAfter.entrySet().iterator().next();
    testEvent("testGetEventsAfter", firstElement.getKey(), firstElement.getValue());
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient#getEventsAfter(java.lang.Long)}.
   * <p>
   * Try with a closed session
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetEventsAfter2() throws CnsServiceClientException {
    CnsDataConsumerServiceClient client = new CnsDataConsumerServiceClient();

    CnsClientConfiguration config = CnsClientConfiguration.getDefaultConfig().createCopy();
    config.setSessionId(sessId2Closed);
    client.setClientConfiguration(config);

    // Set expected exception details
    this.thrown.expect(CnsServiceClientException.class);
    this.thrown.expectMessage("CNS-2002");

    client.getEventsAfter(-10000L);

  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient#getEventsAfter(java.lang.Long)}.
   * <p>
   * Try with a invalid session
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetEventsAfter3() throws CnsServiceClientException {
    CnsDataConsumerServiceClient client = new CnsDataConsumerServiceClient();

    CnsClientConfiguration config = CnsClientConfiguration.getDefaultConfig().createCopy();
    config.setSessionId(INVALID_SESS_ID);
    client.setClientConfiguration(config);

    // Set expected exception details
    this.thrown.expect(CnsServiceClientException.class);
    this.thrown.expectMessage("CNS-2001");

    client.getEventsAfter(-10000L);

  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient#getEventsOfCurrentSession()}.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetEventsOfCurrentSession1() throws CnsServiceClientException {
    CnsDataConsumerServiceClient client = new CnsDataConsumerServiceClient();
    CnsClientConfiguration config = CnsClientConfiguration.getDefaultConfig().createCopy();
    config.setSessionId(sessId1Open);
    client.setClientConfiguration(config);

    Map<Long, EventInfoWithData> curSessEvents = client.getEventsOfCurrentSession();

    assertNotNull("events current session not null", curSessEvents);
    assertFalse("Events current session not empty", curSessEvents.isEmpty());

    Entry<Long, EventInfoWithData> firstElement = curSessEvents.entrySet().iterator().next();
    testEvent("testGetEventsOfCurrentSession", firstElement.getKey(), firstElement.getValue());

    // Test events of current session with start event ID
    Long eventIdStart = Collections.min(curSessEvents.keySet()) + 1L;
    Map<Long, EventInfoWithData> curSessEventsAfter = client.getEventsOfCurrentSession(eventIdStart);

    assertNotNull("events current session 2 not null", curSessEvents);
    assertFalse("Events current session 2 not empty", curSessEvents.isEmpty());

    assertTrue("Retrieved only events from start session ID",
        Collections.min(curSessEventsAfter.keySet()) >= eventIdStart);

    firstElement = curSessEvents.entrySet().iterator().next();
    testEvent("testGetEventsOfCurrentSession", firstElement.getKey(), firstElement.getValue());


  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient#getEventsOfCurrentSession()}.
   * <p>
   * Try with a closed session
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetEventsOfCurrentSession2() throws CnsServiceClientException {
    CnsDataConsumerServiceClient client = new CnsDataConsumerServiceClient();
    CnsClientConfiguration config = CnsClientConfiguration.getDefaultConfig().createCopy();
    config.setSessionId(sessId2Closed);
    client.setClientConfiguration(config);

    // Set expected exception details
    this.thrown.expect(CnsServiceClientException.class);
    this.thrown.expectMessage("CNS-2002");

    client.getEventsOfCurrentSession();

  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient#getEventsOfCurrentSession()}.
   * <p>
   * Try with a invalid session
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetEventsOfCurrentSession3() throws CnsServiceClientException {
    CnsDataConsumerServiceClient client = new CnsDataConsumerServiceClient();
    CnsClientConfiguration config = CnsClientConfiguration.getDefaultConfig().createCopy();
    config.setSessionId(INVALID_SESS_ID);
    client.setClientConfiguration(config);

    // Set expected exception details
    this.thrown.expect(CnsServiceClientException.class);
    this.thrown.expectMessage("CNS-2001");

    client.getEventsOfCurrentSession();

  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient#getLatestEventId()}.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testGetLatestEventId() throws CnsServiceClientException {
    Long latestEventId = new CnsDataConsumerServiceClient().getLatestEventId();
    assertNotNull("latest event ID not null", latestEventId);

  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient#createSession(int) }.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testCreateSession() throws CnsServiceClientException {
    String sessionID = new CnsDataConsumerServiceClient().createSession(0);
    assertNotNull("sessionID not null", sessionID);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.client.CnsDataConsumerServiceClient#closeSession(String)}.
   *
   * @throws CnsServiceClientException error from service
   */
  @Test
  public void testCloseSession() throws CnsServiceClientException {
    CnsDataConsumerServiceClient client = new CnsDataConsumerServiceClient();
    String sessionID = client.createSession(0);
    assertNotNull("sessionID not null", sessionID);
    client.closeSession(sessionID);
  }


  /**
   * @param event change event
   */
  private void testEvent(final String source, final Long eventId, final EventInfoWithData evtInfo) {

    assertNotNull(source + "- Event Info not empty", evtInfo);

    getLogger().debug("{} - Event ID = {}", source, evtInfo.getCreatedAt());
    getLogger().debug("{} - Event ID = {}", source, evtInfo.getDataAvailable());
    getLogger().debug("{} - Event ID = {}", source, evtInfo.getDataLength());
    getLogger().debug("{} - Event ID = {}", source, evtInfo.getProducerId());
    getLogger().debug("{} - Event ID = {}", source, evtInfo.getSessionId());

    assertNotNull(source + " - Change data not empty", evtInfo.getData());

    ChangeEvent event = convertChangeEvent(eventId, evtInfo.getData());
    getLogger().debug("{} - Event ID = {}", source, event.getChangeId());


    assertFalse(source + " - Change data not empty", event.getChangeDataMap().isEmpty());
    getLogger().debug("{} - First change data = {}", source, event.getChangeDataMap().values().iterator().next());

  }

  /**
   * @param event change event
   */
  private void testEvent(final String source, final Long eventId, final byte[] data) {
    ChangeEvent event = convertChangeEvent(eventId, data);
    getLogger().debug("{} - Event ID = {}", source, event.getChangeId());

    assertFalse(source + " - Change data not empty", event.getChangeDataMap().isEmpty());
    getLogger().debug("{} - First change data = {}", source, event.getChangeDataMap().values().iterator().next());

  }

  /**
   * @param value
   * @return
   */
  private ChangeEvent convertChangeEvent(final Long eventId, final byte[] data) {

    ChangeEvent ret = null;

    try (ByteArrayInputStream byteInpStream = new ByteArrayInputStream(data);
        ObjectInputStream objInpStream = new ObjectInputStream(byteInpStream);) {

      ret = (ChangeEvent) objInpStream.readObject();

    }
    catch (IOException | ClassNotFoundException e) {
      getLogger().error("Error while creating change event object for event ID " + eventId + " : " + e.getMessage(), e);
    }

    return ret;
  }

}
