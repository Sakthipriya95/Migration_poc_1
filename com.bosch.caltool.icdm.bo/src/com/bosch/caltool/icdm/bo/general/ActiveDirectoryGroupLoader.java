package com.bosch.caltool.icdm.bo.general;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroup;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroup;

/**
 * Loader class for ActiveDirectoryGroup
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupLoader extends AbstractBusinessObject<ActiveDirectoryGroup, TActiveDirectoryGroup> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public ActiveDirectoryGroupLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ACTIVE_DIRECTORY_GROUP, TActiveDirectoryGroup.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ActiveDirectoryGroup createDataObject(final TActiveDirectoryGroup entity) throws DataException {
    ActiveDirectoryGroup object = new ActiveDirectoryGroup();

    setCommonFields(object, entity);

    object.setGroupName(entity.getGroupName());
    object.setGroupSid(entity.getGroupSid());

    return object;
  }

  /**
   * Get all ActiveDirectoryGroup records in system
   *
   * @return Map. Key - id, Value - ActiveDirectoryGroup object
   * @throws DataException error while retrieving data
   */
  public Map<Long, ActiveDirectoryGroup> getAll() throws DataException {
    Map<Long, ActiveDirectoryGroup> objMap = new ConcurrentHashMap<>();
    TypedQuery<TActiveDirectoryGroup> tQuery =
        getEntMgr().createNamedQuery(TActiveDirectoryGroup.NQ_GET_ALL, TActiveDirectoryGroup.class);
    List<TActiveDirectoryGroup> dbObj = tQuery.getResultList();
    for (TActiveDirectoryGroup entity : dbObj) {
      objMap.put(entity.getAdGroupId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * @param groupName name / SID
   * @return group
   * @throws DataException e
   * @throws NoResultException e
   */
  public ActiveDirectoryGroup getByGroupName(final String groupName) throws DataException, NoResultException {
    TypedQuery<TActiveDirectoryGroup> tQuery =
        getEntMgr().createNamedQuery(TActiveDirectoryGroup.NQ_GET_BY_GRP_SID_NAME, TActiveDirectoryGroup.class);
    tQuery.setParameter("grpName", groupName);
    return createDataObject(tQuery.getSingleResult());
  }
}
