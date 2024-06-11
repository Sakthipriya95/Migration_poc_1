/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.test;


import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.model.Participant;

/**
 * @author TUD1COB
 */
public class ParticipantTest {

  /**
   * Test getters and setters of ParticipantTypeId
   */
  @Test
  public void testGetSetParticipantTypeId() {
    Participant participant = new Participant();
    participant.setParticipantTypeId(1);
    Assert.assertEquals(1, participant.getParticipantTypeId());
  }

  /**
   * Test getters and setters of ParticipantUserNTID
   */
  @Test
  public void testGetSetParticipantUserNTID() {
    Participant participant = new Participant();
    participant.setParticipantUserNTID("user123");
    Assert.assertEquals("user123", participant.getParticipantUserNTID());
  }

  /**
   * Test DefaultConstructor
   */
  @Test
  public void testDefaultConstructor() {
    Participant participant = new Participant();
    Assert.assertEquals(0, participant.getParticipantTypeId());
    Assert.assertNull(participant.getParticipantUserNTID());
  }

}
