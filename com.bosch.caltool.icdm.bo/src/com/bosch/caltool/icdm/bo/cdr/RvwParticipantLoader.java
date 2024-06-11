/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParticipant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Loader class for Review Paticipants
 *
 * @author bru2cob
 */
public class RvwParticipantLoader extends AbstractBusinessObject<RvwParticipant, TRvwParticipant> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwParticipantLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_PARTICIPANT, TRvwParticipant.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwParticipant createDataObject(final TRvwParticipant entity) throws DataException {
    RvwParticipant object = new RvwParticipant();

    setCommonFields(object, entity);
    User user = new UserLoader(getServiceData()).getDataObjectByID(entity.getTabvApicUser().getUserId());
    object.setResultId(entity.getTRvwResult().getResultId());
    object.setUserId(entity.getTabvApicUser().getUserId());
    object.setActivityType(entity.getActivityType());
    object.setName(user.getDescription());
    object.setDescription(user.getName());
    object.setEditFlag(CommonUtils.getBooleanType(entity.getEditFlag()));
    return object;
  }

  /**
   * Get Review Paticipants records using ResultId
   *
   * @param entityObject review result
   * @return Map. Key - id, Value - RvwParticipant object
   * @throws DataException error while retrieving data
   */
  public Map<Long, RvwParticipant> getByResultObj(final TRvwResult entityObject) throws DataException {
    Map<Long, RvwParticipant> objMap = new ConcurrentHashMap<>();
    Set<TRvwParticipant> dbObj = entityObject.getTRvwParticipants();
    for (TRvwParticipant entity : dbObj) {
      objMap.put(entity.getParticipantId(), createDataObject(entity));
    }
    return objMap;
  }


  /**
   * Gets the given user's participation object list based on the by review result obj.
   *
   * @param entityObject the entity object
   * @param userId the user id
   * @return the by result obj for user
   * @throws DataException the data exception
   */
  public Set<RvwParticipant> getByResultObjForUser(final TRvwResult entityObject, final long userId)
      throws DataException {
    Set<RvwParticipant> objMap = new HashSet<>();
    Set<TRvwParticipant> dbObj = entityObject.getTRvwParticipants();
    for (TRvwParticipant entity : dbObj) {
      if (entity.getTabvApicUser().getUserId() == userId) {
        objMap.add(createDataObject(entity));
      }
    }
    return objMap;
  }

}
