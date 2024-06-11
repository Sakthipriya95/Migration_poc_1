/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;

/**
 * @author bne4cob
 * @param <D> data object
 * @param <E> entity object
 */
public abstract class AbstractBusinessObject<D, E> extends AbstractSimpleBusinessObject {


  private static final String METH_GET_VERSION = "getVersion";

  private static final String METH_SET_VERSION = "setVersion";

  private static final String METH_SET_ID = "setId";

  /**
   * class type of the entity being handled by the BO
   */
  private final Class<E> entityType;

  /**
   * Object type display name
   */
  private String typeName;

  private IModelType modelType;

  /**
   * @param serviceData ServiceData
   * @param typeName type Name
   * @param entityType entity type class
   */
  protected AbstractBusinessObject(final ServiceData serviceData, final String typeName, final Class<E> entityType) {
    super(serviceData);
    this.typeName = typeName;
    this.entityType = entityType;
  }

  /**
   * @param serviceData ServiceData
   * @param modelType Model type
   * @param entityType entity type class
   */
  protected AbstractBusinessObject(final ServiceData serviceData, final IModelType modelType,
      final Class<E> entityType) {
    super(serviceData);
    this.modelType = modelType;
    this.entityType = entityType;
  }

  /**
   * Find the entity with the given primary key
   *
   * @param objId entity object ID
   * @return data object
   */
  public E getEntityObject(final Long objId) {
    return objId == null ? null : getEntMgr().find(this.entityType, objId);
  }

  /**
   * Refresh the entity with the given primary key
   *
   * @param objId entity object ID
   * @return refreshed entity if found, else null
   */
  public E refreshEntity(final Long objId) {
    E entity = getEntityObject(objId);
    if (entity != null) {
      getEntMgr().refresh(entity);
    }
    return entity;
  }

  /**
   * Create a new Data object from the entity
   *
   * @param entity database entity
   * @return new data object
   * @throws DataException data exception
   */
  protected abstract D createDataObject(E entity) throws DataException;


  /**
   * Get the data object using the ID
   *
   * @param objId primary key of the object
   * @return data object
   * @throws DataException if object ID is invalid
   */
  public D getDataObjectByID(final Long objId) throws DataException {
    E entity = getEntityObject(objId);
    if (entity == null) {
      throw new DataNotFoundException(this.getTypeName() + " with ID '" + objId + "' not found");
    }
    return createDataObject(entity);
  }

  /**
   * @param idSet Set of object ID
   * @return Map of objects. Key - object ID. Value - object
   * @throws DataException if any Id is invalid in the input
   */
  public Map<Long, D> getDataObjectByID(final Set<Long> idSet) throws DataException {
    Map<Long, D> retMap = new HashMap<>();
    for (Long objId : idSet) {
      retMap.put(objId, getDataObjectByID(objId));
    }
    return retMap;
  }

  /**
   * @param objId object ID
   * @return true if id is valid
   */
  public final boolean isValidId(final Long objId) {
    return (objId != null) && (getEntMgr().find(this.entityType, objId) != null);
  }

  /**
   * Validate the object ID
   *
   * @param objId object ID
   * @throws InvalidInputException if id is invalid
   */
  public final void validateId(final Long objId) throws InvalidInputException {
    if (!isValidId(objId)) {
      throw new InvalidInputException("ID '" + objId + "' is invalid for " + this.getTypeName());
    }
  }

  /**
   * @param dataObj data object
   * @param entityObj entity object
   */
  protected final void setCommonFields(final D dataObj, final E entityObj) {

    setVersionIdFields(dataObj, entityObj);
    setAuditFields(dataObj, entityObj);

  }

  /**
   * @param dataObj data object
   * @param entityObj entity object
   */
  protected final void setVersionIdFields(final D dataObj, final E entityObj) {

    // setting id
    Object id = getEntMgr().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entityObj);
    CommandUtils.INSTANCE.setValueToEntity(getLogger(), dataObj, METH_SET_ID, Long.class, id);


    // set version
    Object object = CommandUtils.INSTANCE.getValueFromEntity(getLogger(), entityObj, METH_GET_VERSION);
    CommandUtils.INSTANCE.setValueToEntity(getLogger(), dataObj, METH_SET_VERSION, Long.class, object);

  }

  /**
   * @param dataObj data object
   * @param entityObj entity object
   */
  protected final void setAuditFields(final D dataObj, final E entityObj) {


    // set created date
    Object object = CommandUtils.INSTANCE.getValueFromEntity(getLogger(), entityObj, CommandUtils.METH_GET_CRE_DATE);
    String dateUserStr = timestamp2String((Timestamp) object);
    CommandUtils.INSTANCE.setValueToEntity(getLogger(), dataObj, CommandUtils.METH_SET_CRE_DATE, String.class,
        dateUserStr);


    // set created user
    object = CommandUtils.INSTANCE.getValueFromEntity(getLogger(), entityObj, CommandUtils.METH_GET_CRE_USER);
    CommandUtils.INSTANCE.setValueToEntity(getLogger(), dataObj, CommandUtils.METH_SET_CRE_USER, String.class, object);


    // set modified date
    object = CommandUtils.INSTANCE.getValueFromEntity(getLogger(), entityObj, CommandUtils.METH_GET_MOD_DATE);
    dateUserStr = timestamp2String((Timestamp) object);
    CommandUtils.INSTANCE.setValueToEntity(getLogger(), dataObj, CommandUtils.METH_SET_MOD_DATE, String.class,
        dateUserStr);


    // set modified user
    object = CommandUtils.INSTANCE.getValueFromEntity(getLogger(), entityObj, CommandUtils.METH_GET_MOD_USER);
    CommandUtils.INSTANCE.setValueToEntity(getLogger(), dataObj, CommandUtils.METH_SET_MOD_USER, String.class, object);


  }

  /**
   * @return the typeName
   */
  public String getTypeName() {
    return this.modelType == null ? this.typeName : this.modelType.getTypeName();
  }

}
