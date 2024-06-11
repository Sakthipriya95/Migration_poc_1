/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.uc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author HNU1COB
 */
public class UsecaseFavoriteServiceClientTest extends AbstractRestClientTest {

  private static final Long UC_FAV_ID = 1458417499l;
  private static final Long INVALID_UC_FAV_ID = -7l;
  private static final Long USER_ID = 398116l;
  private static final Long INVALID_USER_ID = -9l;
  private static final Long PDRJ_ID = 1458417489l;
  private static final Long INVALID_PDRJ_ID = -11l;


  /**
   * Test method for {@link UsecaseFavoriteServiceClient#getById(java.lang.Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    UsecaseFavoriteServiceClient servClient = new UsecaseFavoriteServiceClient();
    UsecaseFavorite ret = servClient.getById(UC_FAV_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link UsecaseFavoriteServiceClient#getById(java.lang.Long)} -Negative test. Expects Favourite Use
   * case ID not found error
   *
   * @throws ApicWebServiceException -Favourite Use case with ID not found
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    UsecaseFavoriteServiceClient servClient = new UsecaseFavoriteServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Favourite Use case with ID '" + INVALID_UC_FAV_ID + "' not found");
    servClient.getById(INVALID_UC_FAV_ID);
    fail("Expected Exception not thrown");
  }


  /**
   * Test method for {@link UsecaseFavoriteServiceClient#getFavoriteUseCases(java.lang.Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetFavoriteUseCases() throws ApicWebServiceException {
    UsecaseFavoriteServiceClient servClient = new UsecaseFavoriteServiceClient();
    Map<Long, UsecaseFavorite> retMap = servClient.getFavoriteUseCases(USER_ID);
    assertFalse("Response should not be Empty or null", ((retMap == null) || retMap.isEmpty()));
    UsecaseFavorite ret = retMap.get(UC_FAV_ID);
    testOutput(ret);
  }


  /**
   * Test method for {@link UsecaseFavoriteServiceClient#getFavoriteUseCases(java.lang.Long)}. -negative test. Expects
   * Favourite Use case User ID not found error
   *
   * @throws ApicWebServiceException exception
   */

  // ICDM internal Error occured*


  @Test
  public void testGetFavoriteUseCasesNegative() throws ApicWebServiceException {
    UsecaseFavoriteServiceClient servClient = new UsecaseFavoriteServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    // this.thrown.expectMessage("Favourite Use case with User ID '" + INVALID_USER_ID + "' not found");
    // this.thrown.expectMessage("Internal server error occured.Please contact iCDM Hotline");
    servClient.getFavoriteUseCases(INVALID_USER_ID);
    fail("Expected Exception not thrown");
  }


  /**
   * Test method for {@link UsecaseFavoriteServiceClient#getProjectFavoriteUseCases(java.lang.Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetProjectFavoriteUseCases() throws ApicWebServiceException {
    UsecaseFavoriteServiceClient servClient = new UsecaseFavoriteServiceClient();
    Map<Long, UsecaseFavorite> retMap = servClient.getProjectFavoriteUseCases(PDRJ_ID);
    assertFalse("Response should not be null or Empty", ((retMap == null) || retMap.isEmpty()));
    UsecaseFavorite ret = retMap.get(UC_FAV_ID);
    testOutput(ret);
  }

  /**
   * Test method for {@link UsecaseFavoriteServiceClient#getProjectFavoriteUseCases(java.lang.Long)}. -negative test.
   * Response should not be null or empty .
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetProjectFavoriteUseCasesNegative() throws ApicWebServiceException {
    UsecaseFavoriteServiceClient servClient = new UsecaseFavoriteServiceClient();
    Map<Long, UsecaseFavorite> retMap = servClient.getProjectFavoriteUseCases(INVALID_PDRJ_ID);
    assertTrue("Response should be null or empty", ((retMap == null) || (retMap.isEmpty())));
  }

  /**
   * @param obj
   */

  private void testOutput(final UsecaseFavorite obj) {
    assertEquals("Usecase Favorite Id is equal", Long.valueOf(1458417499L), obj.getId());
    assertEquals("GroupId is equal", Long.valueOf(469537), obj.getGroupId());
    assertNull("Usecase Id is not null ", obj.getUseCaseId());
    assertNull("SectionId is equal", obj.getSectionId());
    assertEquals("User Id is equal", Long.valueOf(398116), obj.getUserId());
    assertEquals("Project Id is equal", Long.valueOf(1458417489), obj.getProjectId());
    assertEquals("Created User is equal", "IMI2SI", obj.getCreatedUser());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
  }

  /**
   * Test method for {@link UsecaseFavoriteServiceClient#create(UsecaseFavorite)},
   * {@link UsecaseFavoriteServiceClient#delete(UsecaseFavorite)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateDelete() throws ApicWebServiceException {
    UsecaseFavorite obj = new UsecaseFavorite();
    obj.setGroupId(768261466l);
    obj.setUseCaseId(null);
    obj.setSectionId(null);
    obj.setUserId(398116l);
    obj.setProjectId(1458417489l);
    // create
    UsecaseFavoriteServiceClient servClient = new UsecaseFavoriteServiceClient();
    UsecaseFavorite createdObj = servClient.create(obj).getNewlyCreatedUcFav();

    // validate
    assertNotNull("Created object should not be null", createdObj);
    assertEquals("GroupId is equal", Long.valueOf(768261466), obj.getGroupId());
    assertNull("Usecase Id is not null ", obj.getUseCaseId());
    assertNull("SectionId is equal", obj.getSectionId());
    assertEquals("User Id is equal", Long.valueOf(398116), obj.getUserId());
    assertEquals("Project Id is equal", Long.valueOf(1458417489), obj.getProjectId());
    // delete
    servClient.delete(createdObj);
  }
}
