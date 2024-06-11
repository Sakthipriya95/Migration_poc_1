/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Review Participant
 *
 * @author HNU1COB
 */
public class RvwParticipantServiceClientTest extends AbstractRestClientTest {

  private final static Long PARTICIPANT_ID = 766292016L;
  private final static Long INVALID_PARTICIPANT_ID = -766292016L;
  private final static Long RESULT_ID = 766291966L;
  private final static Long INVALID_RESULT_ID = -766291966L;
  private static Long PSET[] = { 766292016L, 766292015L, 769458893L };
  private final static Set<Long> PARTICIPANT_SET = new HashSet<>(Arrays.asList(PSET));
  private static Long INVALID_PSET[] = { -766292016L, -766292015L, -769458893L };
  private final static Set<Long> INVALID_PARTICIPANT_SET = new HashSet<>(Arrays.asList(INVALID_PSET));


  /**
   * Test method for {@link RvwParticipantServiceClient#getById(java.lang.Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    RvwParticipantServiceClient client = new RvwParticipantServiceClient();
    RvwParticipant ret = client.getById(PARTICIPANT_ID);
    assertFalse("Response should not be null", ret == null);
    testOutput(ret);
  }

  /**
   * Test method for {@link RvwParticipantServiceClient#getById(java.lang.Long)}. Negative test
   *
   * @throws ApicWebServiceException 'Review Participant with ID not found'
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    RvwParticipantServiceClient client = new RvwParticipantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Review Participant with ID '" + INVALID_PARTICIPANT_ID + "' not found");
    client.getById(INVALID_PARTICIPANT_ID);
    fail("Expected message not thrown");
  }

  /**
   * Test method for {@link RvwParticipantServiceClient#getByResultId(java.lang.Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByResultId() throws ApicWebServiceException {
    RvwParticipantServiceClient client = new RvwParticipantServiceClient();
    Map<Long, RvwParticipant> retMap = client.getByResultId(RESULT_ID);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    RvwParticipant ret = retMap.get(PARTICIPANT_ID);
    testOutput(ret);
  }

  /**
   * Test method for {@link RvwParticipantServiceClient#getByResultId(java.lang.Long)}. Negative test
   *
   * @throws ApicWebServiceException 'Internal server error occured.Please contact iCDM Hotline'
   */
  @Test
  public void testGetByResultIdNegative() throws ApicWebServiceException {
    RvwParticipantServiceClient client = new RvwParticipantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    client.getByResultId(INVALID_RESULT_ID);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link RvwParticipantServiceClient#getMultiple(java.util.Set)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetMultiple() throws ApicWebServiceException {
    RvwParticipantServiceClient client = new RvwParticipantServiceClient();
    Map<Long, RvwParticipant> retMap = client.getMultiple(PARTICIPANT_SET);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    RvwParticipant ret = retMap.get(PARTICIPANT_ID);
    testOutput(ret);
  }

  /**
   * Test method for {@link RvwParticipantServiceClient#getMultiple(java.util.Set)}.Negative Test
   *
   * @throws ApicWebServiceException 'Review Participant with ID not found'
   */
  @Test
  public void testGetMultipleNegative() throws ApicWebServiceException {
    RvwParticipantServiceClient client = new RvwParticipantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Review Participant with ID");
    client.getMultiple(INVALID_PARTICIPANT_SET);
    fail("Expected Exception not thrown");
  }

  /**
   * @param obj
   */
  private void testOutput(final RvwParticipant obj) {
    assertEquals("Partcipant ID is equal", Long.valueOf(766292016), obj.getId());
    assertEquals("Result ID is equal", Long.valueOf(766291966), obj.getResultId());
    assertEquals("User ID is equal", Long.valueOf(630016), obj.getUserId());
    assertEquals("Activity type is equal", "C", obj.getActivityType());
    assertEquals("Created User is equal", "ULGIPPNE", obj.getCreatedUser());
    assertNotNull("Created date is not null", obj.getCreatedDate());
  }

  /**
   * Test method for {@link RvwParticipantServiceClient#create(List)},
   * {@link RvwParticipantServiceClient#update(RvwParticipant)}, {@link RvwParticipantServiceClient#delete(Set)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdateDelete() throws ApicWebServiceException {
    RvwParticipant obj = new RvwParticipant();
    obj.setResultId(765594016l);
    obj.setActivityType("P");
    obj.setUserId(639916l);
    // create
    List<RvwParticipant> participants = new ArrayList<>();
    participants.add(obj);
    RvwParticipantServiceClient client = new RvwParticipantServiceClient();
    List<RvwParticipant> createdList = client.create(participants);
    assertFalse("Created list should not be empty", createdList.isEmpty());
    RvwParticipant createdObj = createdList.get(0);
    // validate create
    assertEquals("Result ID is equal", Long.valueOf(765594016), createdObj.getResultId());
    assertEquals("User ID is equal", Long.valueOf(639916), createdObj.getUserId());
    assertEquals("Activity type is equal", "P", createdObj.getActivityType());
    // update
    createdObj.setUserId(664016l);
    RvwParticipant updatedObj = client.update(createdObj);
    // validate update
    assertEquals("User ID is equal", Long.valueOf(664016), updatedObj.getUserId());
    // delete
    Set<RvwParticipant> updatedSet = new HashSet<>();
    updatedSet.add(updatedObj);
    client.delete(updatedSet);
  }
}