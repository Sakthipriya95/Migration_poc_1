/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author AND4COB
 */
public class UserServiceClientTest extends AbstractRestClientTest {

  private static final Long USER_ID = 1533211123L;
  private static final Long INVALID_USER_ID = -100L;
  private static final String USER_NAME1 = "AND4COB";
  private static final String USER_NAME2 = "SAY8COB";
  // USER is not in icdm , but available in ldap
  private static final String USER_NAME_IN_LDAP = "NHA1KOR";
  private static final String INVALID_USER_NAME = "INVALID_USER";
  private static final Long NODE_ID1 = 445134665L;
  private static final Long NODE_ID2 = 445603265L;
  private static final Long NODE_ID3 = 445136215L;

  /**
   * Test method for {@link UserServiceClient#getApicUserById(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetApicUserById() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();
    User user = servClient.getApicUserById(USER_ID);
    assertNotNull("Response should not be null", user);
    testUser(user);
  }


  /**
   * Negative Test method for {@link UserServiceClient#getApicUserById(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetApicUserByIdNegative() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("User with ID '" + INVALID_USER_ID + "' not found");
    servClient.getApicUserById(INVALID_USER_ID);
    fail("Expected exception not thrown");
  }

  /**
   * @param user
   */
  private void testUser(final User user) {
    assertEquals("UserName is equal", "AND4COB", user.getName());
    assertEquals("FirstName is equal", "Dutta", user.getFirstName());
    assertEquals("LastName is equal", "Aniket", user.getLastName());
    assertEquals("Department is equal", "MS/ETB53-PS", user.getDepartment());
  }

  /**
   * Test method for {@link UserServiceClient#getAll(boolean)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();
    SortedSet<User> userSet = servClient.getAll(true);
    assertNotNull("Response should not be null", userSet);
    LOG.info("Size: {}", userSet.size());
    boolean userAvailable = false;
    for (User user : userSet) {
      if (user.getId().equals(USER_ID)) {
        testUser(user);
        userAvailable = true;
        break;
      }
    }
    assertTrue("User is available", userAvailable);
  }


  /**
   * Test method for {@link UserServiceClient#getAllByNode(boolean, boolean, MODEL_TYPE, Long[])}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testgetAllByNode() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();
    SortedSet<User> userSet =
        servClient.getAllByNode(true, true, MODEL_TYPE.CDR_FUNCTION, NODE_ID1, NODE_ID2, NODE_ID3);
    assertNotNull("Response should not be null", userSet);
    LOG.info("Size: {}", userSet.size());

    boolean userAvailable = false;
    for (User user : userSet) {
      if (user.getId().equals(USER_ID)) {
        testUser(user);
        userAvailable = true;
        break;
      }
    }
    assertTrue("User is available", userAvailable);
  }


  /**
   * Test method for {@link UserServiceClient#getApicUserByUsername(List)} with empty inputList
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetApicUserByUserNameEmpty() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();
    List<String> userNameList = new ArrayList<>();
    Map<String, User> userMap = servClient.getApicUserByUsername(userNameList);
    assertTrue("Response should be empty", userMap.isEmpty());
  }

  /**
   * Test method for {@link UserServiceClient#getApicUserByUsername(List)} with list cantaing multiple UserNames
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetApicUserByUserNameMultiple() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();
    List<String> userNameList = new ArrayList<>();
    userNameList.add(USER_NAME1);
    userNameList.add(USER_NAME2);
    Map<String, User> userMap = servClient.getApicUserByUsername(userNameList);
    assertFalse("Response should not be null or empty", (userMap == null) || userMap.isEmpty());
    boolean userAvailable = false;
    for (User user : userMap.values()) {
      if (user.getId().equals(USER_ID)) {
        testUser(user);
        userAvailable = true;
        break;
      }
    }
    assertTrue("User is available", userAvailable);
  }

  /**
   * Test method for {@link UserServiceClient#getApicUserByUsername(String)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetApicUserByUserName() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();
    User user = servClient.getApicUserByUsername(USER_NAME1);
    assertNotNull("Response should not be null", user);
    testUser(user);
  }


  /**
   * Negative Test method for {@link UserServiceClient#getApicUserByUsername(String)} with some invalid ntid
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetApicUserByUserNameNegative() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("User with NT ID '" + INVALID_USER_NAME + "' not found");
    servClient.getApicUserByUsername(INVALID_USER_NAME);
    fail("Expected exception not thrown");
  }


  /**
   * Negative Test method for {@link UserServiceClient#getCurrentApicUser()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetCurrentApicUser() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();
    User user = servClient.getCurrentApicUser();
    assertNotNull("Response should not be null", user);
    testCurrentApicUser(user);
  }


  /**
   * @param user
   */
  private void testCurrentApicUser(final User user) {
    assertEquals("User_Id is equal", Long.valueOf(230016), user.getId());
    assertEquals("UserName is equal", "BNE4COB", user.getName());
    assertEquals("FirstName is equal", "Bebith", user.getFirstName());
    assertEquals("LastName is equal", "Nelson", user.getLastName());
    assertEquals("Department is equal", "MS/ETB5-PS", user.getDepartment());
  }

  /**
   * Test method for {@link UserServiceClient#create(User)}, {@link UserServiceClient#update(User)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdateUser() throws ApicWebServiceException {
    UserServiceClient servClient = new UserServiceClient();

    // create an User object
    User user = servClient.getApicUserById(USER_ID);
    Calendar date = DateUtil.getCurrentUtcCalendar();
    String format = DateUtil.getCurrentTime(DateFormat.DATE_FORMAT_15);

    String updatedDisclAccDate = DateUtil.getFormattedDate(format, date);
    user.setDisclaimerAcceptedDate(updatedDisclAccDate);
    User updatedObj = servClient.update(user);

    // validate update
    assertNotNull("Updated object is not null", updatedObj);
    // assertEquals("Disclaimer Accepted Date is equal", updatedDisclAccDate, updatedObj.getDisclaimerAcceptedDate());
  }

}

