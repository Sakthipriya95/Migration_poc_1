/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.caltool.icdm.cns.common.model.EventInfoWithData;
import com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager;
import com.bosch.caltool.icdm.cns.server.exception.CnsServiceException;


/**
 * @author bne4cob
 */
public class ChangeEventCacheTest {

  /**
   * Invoke add events
   */
  @BeforeClass
  public static void prepareData() {
    ChangeEventManager.INSTANCE.addEvent(1L, "A", "", "6cb051cf-9617-43c6-8c00-ae19ad2a3ae7", new byte[2]);
    ChangeEventManager.INSTANCE.addEvent(2L, "B", "", "6ca051cf-1234-43c6-8c00-ae19ad2a3ae7", new byte[1]);
    ChangeEventManager.INSTANCE.addEvent(3L, "A", "", "6ca051cf-5678-43c6-8c00-ae19ad2a3ae7", new byte[3]);
    ChangeEventManager.INSTANCE.addEvent(4L, "B", "", "6ca051cf-8765-43c6-8c00-ae19ad2a3ae7", new byte[6]);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager#getEventsAfter(java.lang.String, java.lang.Long, boolean)}.
   *
   * @throws CnsServiceException error from cache methods
   */
  @Test
  public void testGetEventsAfter1() throws CnsServiceException {
    Map<Long, byte[]> eventsAfter = ChangeEventManager.INSTANCE.getEventsAfter("A", 2L, false);
    assertEquals("Events after ", 2, eventsAfter.size());
    assertTrue("Events after contains element", eventsAfter.containsKey(3L));
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager#getEventsAfter(java.lang.String, java.lang.Long, boolean)}.
   *
   * @throws CnsServiceException error from cache methods
   */
  @Test
  public void testGetEventsAfter2() throws CnsServiceException {
    Map<Long, byte[]> eventsAfter = ChangeEventManager.INSTANCE.getEventsAfter("A", 1L, true);
    assertEquals("Events after ", 2, eventsAfter.size());
    assertTrue("Events after contains element", eventsAfter.containsKey(2L));

    eventsAfter = ChangeEventManager.INSTANCE.getEventsAfter("B", 1L, true);
    assertEquals("Events after ", 1, eventsAfter.size());
    assertTrue("Events after contains element", eventsAfter.containsKey(3L));

    eventsAfter = ChangeEventManager.INSTANCE.getEventsAfter("B", 3L, true);
    assertTrue("Events after ", eventsAfter.isEmpty());
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager#getEventsOfSession(CnsServiceData, String, Long) }.
   *
   * @throws CnsServiceException error from cache methods
   */
  @Test
  public void testGetEventsOfSession() throws CnsServiceException {
    CnsServiceData serviceData = new CnsServiceData();

    Map<Long, EventInfoWithData> eventsOfSession = ChangeEventManager.INSTANCE.getEventsOfSession(serviceData, "B", 0L);
    assertEquals("Events of session ", 2, eventsOfSession.size());
    assertTrue("Events of session contains element", eventsOfSession.containsKey(4L));

    eventsOfSession = ChangeEventManager.INSTANCE.getEventsOfSession(serviceData, "C", 0L);
    assertTrue("Events of session ", eventsOfSession.isEmpty());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager#getLatestEventId()}.
   */
  @Test
  public void testGetLatestEventId() {
    Long latestEventId = ChangeEventManager.INSTANCE.getLatestEventId();
    assertTrue("Latest event ID", latestEventId == 4L);
  }

}
