package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.IcdmA2lGroup;


/**
 * Loader class for A2L GROUP
 *
 * @author gge6cob
 */
public class IcdmA2lGroupLoader extends AbstractBusinessObject<IcdmA2lGroup, TA2lGroup> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public IcdmA2lGroupLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ICDM_A2L_GROUP, TA2lGroup.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IcdmA2lGroup createDataObject(final TA2lGroup entity) throws DataException {
    IcdmA2lGroup object = new IcdmA2lGroup();

    setCommonFields(object, entity);

    object.setGrpName(entity.getGrpName());
    object.setGrpLongName(entity.getGrpLongName());
    object.setWpRootId(entity.getTabvAttrValue().getValueId());
    object.setA2lId(entity.getA2lId());

    return object;
  }

  /**
   * Get all A2L GROUP records in system
   *
   * @param a2lFileId Long
   * @return Map. Key - id, Value - IcdmA2lGroup object
   * @throws DataException error while retrieving data
   */
  public Map<Long, IcdmA2lGroup> getByA2lId(final Long a2lFileId) throws DataException {
    Map<Long, IcdmA2lGroup> objMap = new ConcurrentHashMap<>();
    TypedQuery<TA2lGroup> tQuery = getEntMgr().createNamedQuery(TA2lGroup.GET_BY_A2LFILEID, TA2lGroup.class);
    tQuery.setParameter("a2lId", a2lFileId);
    List<TA2lGroup> dbObj = tQuery.getResultList();
    for (TA2lGroup entity : dbObj) {
      objMap.put(entity.getGroupId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * Fetch db A 2 l groups.
   *
   * @param rootId the root id
   * @param a2lId the a 2 l id
   * @return the map
   */
  public Map<String, Long> fetchDbA2lGroups(final Long rootId, final Long a2lId) {
    Map<String, Long> groupNameIdMap = new ConcurrentHashMap<>();
    final StringBuilder query = new StringBuilder();
    query.append(
        "Select ta2lGroup from TA2lGroup ta2lGroup,GttParameter  temp where ta2lGroup.grpName=temp.paramName and ta2lGroup.tabvAttrValue.valueId='" +
            rootId + "' and ta2lGroup.a2lId='" + a2lId + "'");
    final TypedQuery<TA2lGroup> typeQuery = getEntMgr().createQuery(query.toString(), TA2lGroup.class);
    for (TA2lGroup ta2lGroup : typeQuery.getResultList()) {
      if (ta2lGroup.getGrpName() != null) {
        groupNameIdMap.put(ta2lGroup.getGrpName(), ta2lGroup.getGroupId());
      }
    }
    return groupNameIdMap;
  }


  /**
   * Gets the a 2 l group by A 2 l id and root id.
   *
   * @param a2lFileId the a 2 l file id
   * @param wpRootId the wp root id
   * @return the a 2 l group by A 2 l id and root id
   * @throws DataException the data exception
   */
  public Set<IcdmA2lGroup> getA2lGroupByA2lIdAndRootId(final Long a2lFileId, final Long wpRootId) throws DataException {
    Set<IcdmA2lGroup> groupIdSet = new HashSet<>();
    TypedQuery<TA2lGroup> tQuery = getEntMgr().createNamedQuery(TA2lGroup.GET_BY_A2LFILE_ATTR_VAL_ID, TA2lGroup.class);
    tQuery.setParameter("a2lId", a2lFileId);
    tQuery.setParameter("valueId", wpRootId);
    for (TA2lGroup ta2lGroup : tQuery.getResultList()) {
      groupIdSet.add(createDataObject(ta2lGroup));
    }
    return groupIdSet;
  }
}
