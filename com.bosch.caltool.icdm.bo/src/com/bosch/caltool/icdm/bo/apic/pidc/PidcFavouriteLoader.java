/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidFavorite;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author bne4cob
 */
public class PidcFavouriteLoader extends AbstractBusinessObject<PidcFavourite, TabvPidFavorite> {

  /**
   * @param serviceData Service Data
   */
  public PidcFavouriteLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC_FAVORITE, TabvPidFavorite.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcFavourite createDataObject(final TabvPidFavorite entity) throws DataException {
    PidcFavourite data = new PidcFavourite();
    data.setId(entity.getFavId());

    Pidc pidc = new PidcLoader(getServiceData()).getDataObjectByID(entity.getTabvProjectidcard().getProjectId());
    data.setName(pidc.getName());
    data.setDescription(pidc.getDescription());
    data.setPidcId(pidc.getId());

    data.setUserId(entity.getTabvApicUser().getUserId());

    data.setVersion(null);
    // TODO add version ID in db table

    return data;
  }

  /**
   * Get favourites of all users
   *
   * @return all favourites in the system. Key - user's NT ID, value Map - key - favourite id, value - favourite object
   * @throws DataException error while reading data
   */
  public Map<String, Map<Long, PidcFavourite>> getAllFavourites() throws DataException {
    final TypedQuery<TabvPidFavorite> qDbA2LFiles =
        getEntMgr().createNamedQuery(TabvPidFavorite.NQ_GET_ALL, TabvPidFavorite.class);

    return createRetObject(qDbA2LFiles);
  }

  /**
   * Get favourites of given users
   *
   * @param userSet set of user NT IDs
   * @return all favourites for the given users. Key - user's NT ID, value Map - key - favourite id, value - favourite
   *         object
   * @throws DataException error while reading data
   */
  public Map<String, Map<Long, PidcFavourite>> getAllFavouritesByUser(final Set<String> userSet) throws DataException {

    UserLoader userLoader = new UserLoader(getServiceData());

    Map<String, Map<Long, PidcFavourite>> retMap = new HashMap<>();

    for (String user : userSet) {
      Long apicUserId = userLoader.getUserIdByUserName(user);
      if (null != apicUserId) {
        TabvApicUser dbApicUser = userLoader.getEntityObject(apicUserId);

        Map<String, Map<Long, PidcFavourite>> map = createRetObjectMap(dbApicUser.getTabvPidFavorites());
        if (!map.isEmpty()) {
          retMap.putAll(map);
        }
      }
    }

    return retMap;
  }

  /**
   * @param tabvPidFavorites
   * @return
   * @throws DataException
   */
  private Map<String, Map<Long, PidcFavourite>> createRetObjectMap(final Set<TabvPidFavorite> tabvPidFavorites)
      throws DataException {
    Map<String, Map<Long, PidcFavourite>> retMap = new HashMap<>();

    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    UserLoader userLdr = new UserLoader(getServiceData());
    for (TabvPidFavorite entity : tabvPidFavorites) {
      PidcVersion activeVers = pidcVersLdr.getActivePidcVersion(entity.getTabvProjectidcard().getProjectId());
      if (pidcVersLdr.isHiddenToCurrentUser(activeVers.getId())) {
        continue;
      }
      PidcFavourite data = createDataObject(entity);

      String user = userLdr.getUsernameById(data.getUserId());

      Map<Long, PidcFavourite> userFavMap = retMap.get(user);
      if (userFavMap == null) {
        userFavMap = new HashMap<>();
        retMap.put(user, userFavMap);
      }
      userFavMap.put(data.getId(), data);
    }

    return retMap;
  }

  private Map<String, Map<Long, PidcFavourite>> createRetObject(final TypedQuery<TabvPidFavorite> qDbA2LFiles)
      throws DataException {

    Map<String, Map<Long, PidcFavourite>> retMap = new HashMap<>();
    final List<TabvPidFavorite> dbPidcFavs = qDbA2LFiles.getResultList();

    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    UserLoader userLdr = new UserLoader(getServiceData());
    for (TabvPidFavorite entity : dbPidcFavs) {
      PidcVersion activeVers = pidcVersLdr.getActivePidcVersion(entity.getTabvProjectidcard().getProjectId());
      if (pidcVersLdr.isHiddenToCurrentUser(activeVers.getId())) {
        continue;
      }
      PidcFavourite data = createDataObject(entity);

      String user = userLdr.getUsernameById(data.getUserId());

      Map<Long, PidcFavourite> userFavMap = retMap.get(user);
      if (userFavMap == null) {
        userFavMap = new HashMap<>();
        retMap.put(user, userFavMap);
      }
      userFavMap.put(data.getId(), data);
    }

    return retMap;
  }

  /**
   * Fetch favourites of the user, by User ID
   *
   * @param userId user Id
   * @return Map of Pidc version id, Pidc favourite
   * @throws DataException error while fetching data
   */
  public Map<Long, PidcFavourite> getFavouritePidcForUser(final Long userId) throws DataException {
    TabvApicUser dbApicUser = new UserLoader(getServiceData()).getEntityObject(userId);
    return createRetObject(dbApicUser.getTabvPidFavorites());
  }

  /**
   * @param tabvPidFavorites Set of TabvPidFavorite
   * @return Map of Long, PidcFavourite
   * @throws DataException
   */
  private Map<Long, PidcFavourite> createRetObject(final Set<TabvPidFavorite> tabvPidFavorites) throws DataException {
    Map<Long, PidcFavourite> retMap = new HashMap<>();

    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    for (TabvPidFavorite entity : tabvPidFavorites) {
      PidcVersion activeVers = pidcVersLdr.getActivePidcVersion(entity.getTabvProjectidcard().getProjectId());
      if (pidcVersLdr.isHiddenToCurrentUser(activeVers.getId())) {
        continue;
      }
      retMap.put(activeVers.getId(), createDataObject(entity));
    }

    return retMap;
  }


}
