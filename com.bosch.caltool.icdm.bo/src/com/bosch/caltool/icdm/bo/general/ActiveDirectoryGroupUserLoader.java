package com.bosch.caltool.icdm.bo.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroup;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroup;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;

/**
 * Loader class for ActiveDirectoryGroupUser
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupUserLoader
    extends AbstractBusinessObject<ActiveDirectoryGroupUser, TActiveDirectoryGroupUser> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public ActiveDirectoryGroupUserLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ACTIVE_DIRECTORY_GROUP_USER, TActiveDirectoryGroupUser.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ActiveDirectoryGroupUser createDataObject(final TActiveDirectoryGroupUser entity) throws DataException {
    ActiveDirectoryGroupUser object = new ActiveDirectoryGroupUser();
    ActiveDirectoryGroupLoader grpLoader = new ActiveDirectoryGroupLoader(getServiceData());
    setCommonFields(object, entity);

    object.setAdGroup(grpLoader.createDataObject(entity.getActiveDirectoryGroup()));
    object.setUsername(entity.getUserName());
    object.setIsIcdmUser(entity.getIsIcdmUser());

    return object;
  }
  
  /**
   * @param activeDirGrp ActiveDirectoryGroup
   * @param userName User Name String
   * @return ActiveDirectoryGroup object
   */
  public ActiveDirectoryGroupUser createDataObjectFromFields(ActiveDirectoryGroup activeDirGrp, String userName) {
    ActiveDirectoryGroupUser obj = new ActiveDirectoryGroupUser();
    obj.setAdGroup(activeDirGrp);
    obj.setUsername(userName.toUpperCase());
    TabvApicUser icdmUser = (new UserLoader(getServiceData())).getEntityObjectByUserName(userName);
    obj.setIsIcdmUser(icdmUser == null ? CommonUtilConstants.CODE_NO : CommonUtilConstants.CODE_YES);
    return obj;
  }

  /**
   * Get all ActiveDirectoryGroupUser records in system
   *
   * @return Map. Key - id, Value - ActiveDirectoryGroupUser object
   * @throws DataException error while retrieving data
   */
  public Map<Long, ActiveDirectoryGroupUser> getAll() throws DataException {
    Map<Long, ActiveDirectoryGroupUser> objMap = new ConcurrentHashMap<>();
    TypedQuery<TActiveDirectoryGroupUser> tQuery =
        getEntMgr().createNamedQuery(TActiveDirectoryGroupUser.NQ_GET_ALL, TActiveDirectoryGroupUser.class);
    List<TActiveDirectoryGroupUser> dbObj = tQuery.getResultList();
    for (TActiveDirectoryGroupUser entity : dbObj) {
      objMap.put(entity.getGroupUsersId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * Get all TActiveDirectoryGroupUser records in system
   *
   * @param adGroupId inputId
   * @return Map. Key - id, Value - TActiveDirectoryGroupUser object
   * @throws DataException error while retrieving data
   */
  public List<ActiveDirectoryGroupUser> getByADGroupId(final long adGroupId) throws DataException {
    TypedQuery<TActiveDirectoryGroupUser> tQuery =
        getEntMgr().createNamedQuery(TActiveDirectoryGroupUser.NQ_GET_BY_GROUP_ID, TActiveDirectoryGroupUser.class);
    TActiveDirectoryGroup adGroup = new ActiveDirectoryGroupLoader(getServiceData()).getEntityObject(adGroupId);
    tQuery.setParameter("groupId", adGroup);
    List<TActiveDirectoryGroupUser> dbObj = tQuery.getResultList();
    List<ActiveDirectoryGroupUser> grpUsers = new ArrayList<>();
    for (TActiveDirectoryGroupUser entity : dbObj) {
      grpUsers.add(createDataObject(entity));
    }
    return grpUsers;
  }

  /**
   * Get all TActiveDirectoryGroup records user is memberof
   *
   * @param userId inputId
   * @return Set TActiveDirectoryGroup object
   * @throws DataException error while retrieving data
   */
  public Set<TActiveDirectoryGroup> getByUser(final String userId) throws DataException {
    TypedQuery<TActiveDirectoryGroupUser> tQuery =
        getEntMgr().createNamedQuery(TActiveDirectoryGroupUser.NQ_GET_BY_USER_ID, TActiveDirectoryGroupUser.class);
    tQuery.setParameter("userId", userId);
    List<TActiveDirectoryGroupUser> dbObj = tQuery.getResultList();
    Set<TActiveDirectoryGroup> grpUsers = new HashSet<>();
    for (TActiveDirectoryGroupUser entity : dbObj) {
      grpUsers.add(entity.getActiveDirectoryGroup());
    }
    return grpUsers;
  }
  
  
  /**
   * Get Map with Key - User Name, Value - ActiveDirectoryGroupUser object for given AD Grp ID
   *
   * @param adGroupId inputId
   * @return Map. Key - User Name, Value - ActiveDirectoryGroupUser object
   * @throws DataException error while retrieving data
   */
  public Map<String,ActiveDirectoryGroupUser> getUsersMapByADGroupId(final long adGroupId) throws DataException {
    TypedQuery<TActiveDirectoryGroupUser> tQuery =
        getEntMgr().createNamedQuery(TActiveDirectoryGroupUser.NQ_GET_BY_GROUP_ID, TActiveDirectoryGroupUser.class);
    TActiveDirectoryGroup adGroup = new ActiveDirectoryGroupLoader(getServiceData()).getEntityObject(adGroupId);
    tQuery.setParameter("groupId", adGroup);
    List<TActiveDirectoryGroupUser> dbObj = tQuery.getResultList();
    Map<String,ActiveDirectoryGroupUser> userNameUserMap = new HashMap<>();
    for (TActiveDirectoryGroupUser entity : dbObj) {
      userNameUserMap.put(entity.getUserName(), createDataObject(entity));
    }
    return userNameUserMap;
  }
  
}
