/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class FavouritesServiceClient extends AbstractRestServiceClient {

  /**
   */
  public FavouritesServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_USER_FAV);
  }

  /**
   * @return Map of favourites
   * @throws ApicWebServiceException error during service invocation
   */
  public Map<String, Map<Long, PidcFavourite>> getFavourites() throws ApicWebServiceException {
    LOGGER.debug("Loading Favourites map for all users");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);

    GenericType<Map<String, Map<Long, PidcFavourite>>> type =
        new GenericType<Map<String, Map<Long, PidcFavourite>>>() {};

    Map<String, Map<Long, PidcFavourite>> response = get(wsTarget, type);

    LOGGER.debug("Favourites loaded. No. of users in return : {}", response.size());

    return response;
  }

  /**
   * @param users users
   * @return Map of favourites
   * @throws ApicWebServiceException error during service invocation
   */
  public Map<String, Map<Long, PidcFavourite>> getFavouritesByUser(final String... users)
      throws ApicWebServiceException {
    LOGGER.debug("Loading Favourites map for users");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_USER_NT_ID);

    for (String user : users) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_USER, user);
    }

    GenericType<Map<String, Map<Long, PidcFavourite>>> type =
        new GenericType<Map<String, Map<Long, PidcFavourite>>>() {};

    Map<String, Map<Long, PidcFavourite>> response = get(wsTarget, type);

    LOGGER.debug("Favourites loaded. No. of users in return : {}", response.size());

    return response;
  }

  /**
   * @return Map of favourites
   * @throws ApicWebServiceException error during service invocation
   */
  public Map<Long, PidcFavourite> getFavouritePidcForUser(final Long userId) throws ApicWebServiceException {
    LOGGER.debug("Loading Favourites map for user");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_USER_ID);

    wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_USER_ID, userId);
    GenericType<Map<Long, PidcFavourite>> type = new GenericType<Map<Long, PidcFavourite>>() {};
    Map<Long, PidcFavourite> response = get(wsTarget, type);
    LOGGER.debug("Favourites loaded. No. of user in return : {}", response.size());
    return response;
  }

  /**
   * Get a Pidc favourite by its ID
   *
   * @param objId Pidcfavourite ID
   * @return version
   * @throws ApicWebServiceException any exception
   */
  public PidcFavourite getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);

    return get(wsTarget, PidcFavourite.class);
  }

  /**
   * @param pidcFav
   * @return
   * @throws ApicWebServiceException
   */
  public PidcFavourite createPidcFavourite(final PidcFavourite pidcFav) throws ApicWebServiceException {
    LOGGER.debug("Creating Pidc Favourite for user");
    return create(getWsBase(), pidcFav);
  }

  /**
   * @param pidcFav
   * @throws ApicWebServiceException
   */
  public void deletePidcFavourite(final PidcFavourite pidcFav) throws ApicWebServiceException {
    LOGGER.debug("Deleting Pidc Favourite for user");
    delete(getWsBase(), pidcFav);
  }

}
