package com.bosch.caltool.icdm.bo.general;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroup;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;

/**
 * Loader class for ActiveDirectoryGroupNodeAccess
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupNodeAccessLoader
    extends AbstractBusinessObject<ActiveDirectoryGroupNodeAccess, TActiveDirectoryGroupNodeAccess> {

  /**
  *
  */
  private static final String SESSKEY_CUR_USER_NODE_ACCESS = "CUR_USER_NODE_ACCESS";

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public ActiveDirectoryGroupNodeAccessLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ACTIVE_DIRECTORY_GROUP_NODE_ACCES, TActiveDirectoryGroupNodeAccess.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ActiveDirectoryGroupNodeAccess createDataObject(final TActiveDirectoryGroupNodeAccess entity)
      throws DataException {
    ActiveDirectoryGroupNodeAccess object = new ActiveDirectoryGroupNodeAccess();
    ActiveDirectoryGroupLoader loader = new ActiveDirectoryGroupLoader(getServiceData());
    setCommonFields(object, entity);

    object.setNodeId(entity.getNodeId());
    object.setNodeType(entity.getNodeType());
    object.setAdGroup(loader.createDataObject(entity.getActiveDirectoryGroup()));
    if (CommonUtilConstants.CODE_YES.equals(entity.getOwner())) {
      // TODO Change from UTILS
      object.setOwner(true);
      object.setGrant(true);
      object.setWrite(true);
      object.setRead(true);
    }
    else if (CommonUtilConstants.CODE_YES.equals(entity.getGrantright())) {
      object.setOwner(false);
      object.setGrant(true);
      object.setWrite(true);
      object.setRead(true);
    }
    else if (CommonUtilConstants.CODE_YES.equals(entity.getWriteright())) {
      object.setOwner(false);
      object.setGrant(false);
      object.setWrite(true);
      object.setRead(true);
    }
    else if (CommonUtilConstants.CODE_YES.equals(entity.getReadright())) {
      object.setOwner(false);
      object.setGrant(false);
      object.setWrite(false);
      object.setRead(true);
    }

    return object;
  }

  /**
   * Get all ActiveDirectoryGroupNodeAccess records in system
   *
   * @return Map. Key - id, Value - TActiveDirectoryGroupNodeAccess object
   * @throws DataException error while retrieving data
   */
  public Map<Long, List<ActiveDirectoryGroupNodeAccess>> getByNodeId(final long nodeId) throws DataException {
    Map<Long, List<ActiveDirectoryGroupNodeAccess>> objMap = new ConcurrentHashMap<>();
    TypedQuery<TActiveDirectoryGroupNodeAccess> tQuery = getEntMgr()
        .createNamedQuery(TActiveDirectoryGroupNodeAccess.NQ_GET_BY_NODE_ID, TActiveDirectoryGroupNodeAccess.class);
    tQuery.setParameter("nodeId", nodeId);
    List<TActiveDirectoryGroupNodeAccess> dbObj = tQuery.getResultList();
    List<ActiveDirectoryGroupNodeAccess> retList = new ArrayList<>();
    for (TActiveDirectoryGroupNodeAccess entity : dbObj) {
      retList.add(createDataObject(entity));
    }
    objMap.put(nodeId, retList);
    return objMap;
  }

  /**
   * @param reqUserSet User NT Id set
   * @param pidcIdSet Set of PIDC Ids
   * @param nodeType PIDC
   * @return Map of Key = NodeId, Value = Set ActiveDirectoryGroupNodeAccess
   * @throws DataException DataException
   */
  public Map<Long, Set<ActiveDirectoryGroupNodeAccess>> getAdGrpNodeAccessByUsernames(final Set<String> reqUserSet,
      final Set<Long> pidcIdSet, final String nodeType)
      throws DataException {
    Map<Long, Set<ActiveDirectoryGroupNodeAccess>> objMap = new ConcurrentHashMap<>();
    TypedQuery<TActiveDirectoryGroupNodeAccess> tQuery = getEntMgr().createNamedQuery(
        TActiveDirectoryGroupNodeAccess.NQ_GET_BY_USERNAME_SET, TActiveDirectoryGroupNodeAccess.class);
    tQuery.setParameter("userName", reqUserSet);
    tQuery.setParameter("nodeType", nodeType);

    for (TActiveDirectoryGroupNodeAccess adGrpNodeAccess : tQuery.getResultList()) {
      Long nodeId = adGrpNodeAccess.getNodeId();
      if (pidcIdSet.isEmpty() || (!pidcIdSet.isEmpty() && pidcIdSet.contains(nodeId))) {
        if (objMap.containsKey(nodeId)) {
          Set<ActiveDirectoryGroupNodeAccess> adSet = objMap.get(nodeId);
          adSet.add(createDataObject(adGrpNodeAccess));
          objMap.put(nodeId, adSet);
        }
        else {
          Set<ActiveDirectoryGroupNodeAccess> adSet = new HashSet<>();
          adSet.add(createDataObject(adGrpNodeAccess));
          objMap.put(nodeId, adSet);
        }
      }
    }
    return objMap;
  }

  /**
   * @param nodeType PIDC
   * @return Map of Key = NodeId, Value = Set ActiveDirectoryGroupNodeAccess
   * @throws DataException DataException
   */
  public Map<Long, Set<ActiveDirectoryGroupNodeAccess>> getAllAdGrpNodeAccess(final String nodeType)
      throws DataException {
    Map<Long, Set<ActiveDirectoryGroupNodeAccess>> objMap = new ConcurrentHashMap<>();
    TypedQuery<TActiveDirectoryGroupNodeAccess> tQuery = getEntMgr()
        .createNamedQuery(TActiveDirectoryGroupNodeAccess.NQ_GET_BY_NODE_TYPE, TActiveDirectoryGroupNodeAccess.class);
    tQuery.setParameter("nodeType", nodeType);

    for (TActiveDirectoryGroupNodeAccess adGrpNodeAccess : tQuery.getResultList()) {
      Long nodeId = adGrpNodeAccess.getNodeId();
      if (objMap.containsKey(nodeId)) {
        Set<ActiveDirectoryGroupNodeAccess> adSet = objMap.get(nodeId);
        adSet.add(createDataObject(adGrpNodeAccess));
        objMap.put(nodeId, adSet);
      }
      else {
        Set<ActiveDirectoryGroupNodeAccess> adSet = new HashSet<>();
        adSet.add(createDataObject(adGrpNodeAccess));
        objMap.put(nodeId, adSet);
      }

    }
    return objMap;
  }

  /**
   * @param pidcIdSet Set of PIDC Ids
   * @return Map of Key = NodeId, Value = Set ActiveDirectoryGroupNodeAccess
   * @throws DataException DataException
   */
  public Map<Long, Set<ActiveDirectoryGroupNodeAccess>> getAdGrpNodeAccessByPidcIds(final Set<Long> pidcIdSet)
      throws DataException {
    Map<Long, Set<ActiveDirectoryGroupNodeAccess>> objMap = new ConcurrentHashMap<>();
    TypedQuery<TActiveDirectoryGroupNodeAccess> tQuery = getEntMgr()
        .createNamedQuery(TActiveDirectoryGroupNodeAccess.NQ_GET_BY_NODE_ID_SET, TActiveDirectoryGroupNodeAccess.class);
    tQuery.setParameter("nodeIdSet", pidcIdSet);
    for (TActiveDirectoryGroupNodeAccess adGrpNodeAccess : tQuery.getResultList()) {
      Long nodeId = adGrpNodeAccess.getNodeId();
      if (objMap.containsKey(nodeId)) {
        Set<ActiveDirectoryGroupNodeAccess> adSet = objMap.get(nodeId);
        adSet.add(createDataObject(adGrpNodeAccess));
        objMap.put(nodeId, adSet);
      }
      else {
        Set<ActiveDirectoryGroupNodeAccess> adSet = new HashSet<>();
        adSet.add(createDataObject(adGrpNodeAccess));
        objMap.put(nodeId, adSet);
      }
    }
    return objMap;
  }

  /**
   * Get all ActiveDirectoryGroupNodeAccess records in system for the given group IDs
   *
   * @return Map. Key - id, Value - TActiveDirectoryGroupNodeAccess object
   * @throws DataException error while retrieving data
   */
  public List<ActiveDirectoryGroupNodeAccess> getByGroupIdSet(final Set<TActiveDirectoryGroup> groupSet)
      throws DataException {
    TypedQuery<TActiveDirectoryGroupNodeAccess> tQuery = getEntMgr().createNamedQuery(
        TActiveDirectoryGroupNodeAccess.NQ_GET_BY_GROUP_ID_SET, TActiveDirectoryGroupNodeAccess.class);
    tQuery.setParameter("groupIdSet", groupSet);
    List<TActiveDirectoryGroupNodeAccess> dbObj = tQuery.getResultList();
    List<ActiveDirectoryGroupNodeAccess> retList = new ArrayList<>();
    for (TActiveDirectoryGroupNodeAccess entity : dbObj) {
      retList.add(createDataObject(entity));
    }
    return retList;
  }

  /**
   * Get group node access for a node and given group
   *
   * @param nodeId node
   * @param adGroupId group
   * @return access
   * @throws DataException ex
   */
  public ActiveDirectoryGroupNodeAccess getByNodeIdAndGroupId(final long nodeId, final long adGroupId)
      throws DataException {
    TypedQuery<TActiveDirectoryGroupNodeAccess> tQuery = getEntMgr().createNamedQuery(
        TActiveDirectoryGroupNodeAccess.NQ_GET_BY_NODE_ID_GROUP_ID, TActiveDirectoryGroupNodeAccess.class);
    tQuery.setParameter("nodeId", nodeId);
    TActiveDirectoryGroup grp = (new ActiveDirectoryGroupLoader(getServiceData())).getEntityObject(adGroupId);
    tQuery.setParameter("groupId", grp);
    return createDataObject(tQuery.getSingleResult());
  }

  /**
   * Resets current user's node access details
   */
  protected void resetNodeAccessCache() {
    getServiceData().clearData(getClass(), SESSKEY_CUR_USER_NODE_ACCESS);
  }
}
