/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class FavouritesServiceClientTest extends AbstractRestClientTest {


  private final static String USER1 = "BNE4COB";
  private final static String USER2 = "HEF2FE";
  private final static Long FAV_ID = 768979966L;
  private final static Long INVALID_FAV_ID = -100L;
  private final static Long USER_ID = 224265L;
  private final static Long PIDC_VERS_ID = 773510565L;

  /**
   * Test retrieval of all users {@link FavouritesServiceClient#getFavourites()}}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetFavourites() throws ApicWebServiceException {
    FavouritesServiceClient servClient = new FavouritesServiceClient();
    Map<String, Map<Long, PidcFavourite>> retMap = servClient.getFavourites();
    assertFalse("Response should not be null or empty", (retMap == null) || retMap.isEmpty());
    Map<Long, PidcFavourite> pidcFavMap = retMap.get(USER2);
    assertFalse("Response should not be null or empty", (pidcFavMap == null) || pidcFavMap.isEmpty());
    LOG.info("Size: {}", pidcFavMap.size());
    PidcFavourite pidcFavourite = pidcFavMap.get(FAV_ID);
    assertNotNull("Response should not be null", pidcFavourite);
    testPidcFav(pidcFavourite);
  }


  /**
   * @param pidcFavourite
   */
  private void testPidcFav(final PidcFavourite pidcFavourite) {
    assertEquals("User_Id is equal", Long.valueOf(224265), pidcFavourite.getUserId());
    assertEquals("Project_Id is equal", Long.valueOf(762), pidcFavourite.getPidcId());
    assertEquals("Description is equal", "Audi EA888-Gen3-BZyklus", pidcFavourite.getDescription());
  }


  /**
   * Test retrieval of given user test method for {@link FavouritesServiceClient#getFavouritesByUser(String[])}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetFavouritesByUser() throws ApicWebServiceException {
    FavouritesServiceClient servClient = new FavouritesServiceClient();
    Map<String, Map<Long, PidcFavourite>> retMap = servClient.getFavouritesByUser(USER1, USER2);
    assertFalse("Response should not be null or empty", (retMap == null) || retMap.isEmpty());
    Map<Long, PidcFavourite> pidcFavMap = retMap.get(USER2);
    LOG.info("Size: {}", pidcFavMap.size());
    PidcFavourite pidcFavourite = pidcFavMap.get(FAV_ID);
    assertNotNull("Response should not be null", pidcFavourite);
    testPidcFav(pidcFavourite);
  }


  /**
   * Test retrieval of given user test method for {@link FavouritesServiceClient#getFavouritePidcForUser(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetFavouritePidcForUser() throws ApicWebServiceException {
    FavouritesServiceClient servClient = new FavouritesServiceClient();
    Map<Long, PidcFavourite> pidcFavMap = servClient.getFavouritePidcForUser(USER_ID);
    assertFalse("Response should not be null or empty", (pidcFavMap == null) || pidcFavMap.isEmpty());
    LOG.info("Size: {}", pidcFavMap.size());
    PidcFavourite pidcFavourite = pidcFavMap.get(PIDC_VERS_ID);// PIDC_VERS_ID is the key () for some active pidc
                                                               // for FAV_ID=768979966, PROJECT_ID=762;
                                                               // for PROJECT_ID=762, PIDC_VERS_ID = 773510565L
    assertNotNull("Response should not be null", pidcFavourite);
    testPidcFav(pidcFavourite);
  }

  /**
   * Test method for {@link FavouritesServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    FavouritesServiceClient servClient = new FavouritesServiceClient();
    PidcFavourite pidcFavourite = servClient.getById(FAV_ID);
    assertNotNull("Response should not be null", pidcFavourite);
    testPidcFav(pidcFavourite);
  }

  /**
   * For some invalid fav_id Neagtive Test method for {@link FavouritesServiceClient#getById(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    FavouritesServiceClient servClient = new FavouritesServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Favourite with ID '" + INVALID_FAV_ID + "' not found");
    servClient.getById(INVALID_FAV_ID);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link FavouritesServiceClient#createPidcFavourite(PidcFavourite)},
   * {@link FavouritesServiceClient#deletePidcFavourite(PidcFavourite)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateDelPidcFav() throws ApicWebServiceException {
    FavouritesServiceClient servClient = new FavouritesServiceClient();

    PidcFavourite pidcFavourite = new PidcFavourite();
    pidcFavourite.setPidcId(766243217L);
    pidcFavourite.setUserId(224065L);

    // invoke createPidcFavourite method
    PidcFavourite createdObj = servClient.createPidcFavourite(pidcFavourite);
    LOG.info("Created Pidc Favourite Id: {}", createdObj.getId());

    // validate create
    assertNotNull("Created object is not null", createdObj);
    assertEquals("Pidc Id is equal", Long.valueOf(766243217), createdObj.getPidcId());
    assertEquals("User Id is equal", Long.valueOf(224065), createdObj.getUserId());

    // invoke deletePidcFavourite method
    servClient.deletePidcFavourite(createdObj);
  }

}
