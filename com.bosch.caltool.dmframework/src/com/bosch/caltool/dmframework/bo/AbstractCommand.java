/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.datamodel.core.IMasterRefreshable;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.StaleDataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * @author bru2cob Abstract class as base for all commands using REST webservice
 * @param <D> data object
 * @param <B> business object
 */
public abstract class AbstractCommand<D extends IModel, B extends AbstractBusinessObject<D, ?>>
    extends AbstractSimpleCommand {

  /**
   * the possible modes of the command
   */
  public enum COMMAND_MODE {
                            /**
                             * Insert mode
                             */
                            CREATE,
                            /**
                             * Update mode
                             */
                            UPDATE,
                            /**
                             * Delete mode
                             */
                            DELETE
  }


  private final D inputData;

  private D oldData;

  /**
   * ID of the object.
   * <p>
   * During create, value should be set by calling setObjId() by implementations<br>
   * During upate/delete, value is set by constructure
   */
  private Long objId;

  /**
   * Update the timestamp of the record. Can be set and used, if only timestamp of the entity is to be updated (ie,
   * MODIFIED_DATE)
   */
  private boolean updateTimestamp;

  /**
   * the current mode of the command.
   */
  private final COMMAND_MODE cmdMode;

  /**
   * Business object
   */
  private final B busObj;
  /**
   * true if master refresh is applicable
   */
  private boolean isMasterRefreshApplicable;

  /**
   * @param serviceData serviceData
   * @param inputData   input data from service calls
   * @param busObj      business object
   * @param mode        command mode
   * @throws IcdmException any validations or errors
   */
  protected AbstractCommand(final ServiceData serviceData, final D inputData, final B busObj, final COMMAND_MODE mode)
      throws IcdmException {

    super(serviceData);
    this.inputData = inputData;
    this.busObj = busObj;
    this.cmdMode = mode;

    setOldData();
  }

  /**
   * @param inputData
   * @param busObj
   * @param mode
   * @throws DataException
   * @throws StaleDataException
   */
  private void setOldData() throws DataException, StaleDataException {
    if ((this.cmdMode == COMMAND_MODE.UPDATE) ||
        ((this.cmdMode == COMMAND_MODE.DELETE) && (this.busObj.getEntityObject(this.inputData.getId()) != null))) {

      // For delete, if entity is already deleted(entity object is null), no further action is required. Here old data
      // will also be null. Then the execute() method during delete can skip the delete operation

      D oldDta = this.busObj.getDataObjectByID(this.inputData.getId());
      if (!CommonUtils.isEqual(oldDta.getVersion(), this.inputData.getVersion())) {
        throw buildStaleDataException();
      }

      this.oldData = oldDta;
      this.objId = this.inputData.getId();
    }
  }

  private StaleDataException buildStaleDataException() {
    StringBuilder msg = new StringBuilder(this.busObj.getTypeName());

    if (this.inputData instanceof IBasicObject) {
      IBasicObject basObj = (IBasicObject) this.inputData;
      msg.append(" with name '").append(basObj.getName()).append('\'');
    }
    else {
      msg.append(" with ID '").append(this.inputData.getId()).append('\'');
    }

    msg.append(" already updated by some other process. Please refresh and try again.");

    return new StaleDataException(this.cmdMode.name(), msg.toString());
  }


  /**
   * The method to execute the command.
   * <p>
   * IMPORTANT : Do NOT invoke this method to execute the command. Use
   * {@link #executeChildCommand(AbstractSimpleCommand)} of the parent command or {@link CommandExecuter} instead.
   *
   * @throws IcdmException any validations or errors
   */
  @Override
  protected final void execute() throws IcdmException {

    getLogger().debug("Command execution started :{} ; Mode={} ", this, this.getCmdMode());

    validateInput();

    // Executes an insert command
    if (this.getCmdMode() == COMMAND_MODE.CREATE) {
      create();
    }
    // Executes update command
    else if ((this.getCmdMode() == COMMAND_MODE.UPDATE) && (dataChanged() || this.updateTimestamp)) {
      update();
    }
    // Executes delete command
    else if ((this.getCmdMode() == COMMAND_MODE.DELETE) && (this.oldData != null)) {
      // Delete is performed only if old data is null. If old data is null, this means that delete was done already(in
      // same or different session), and no further action is required
      delete();
    }

    getLogger().debug("Command execution finished : {} ; Mode={}", this, this.getCmdMode());

  }

  /**
   * Performs the steps required to create new record(s) for this command. Implementations of this class should provide
   * the steps for creating a new record.
   *
   * @throws IcdmException any validations or errors
   */
  protected abstract void create() throws IcdmException;

  /**
   * Performs the steps required to update record(s) for this command. Implementations of this class should provide the
   * steps for modifying the record(s).
   *
   * @throws IcdmException any validations or errors
   */
  protected abstract void update() throws IcdmException;

  /**
   * Performs the steps required to delete record(s) for this command. Implementations of this class should provide the
   * steps for deleting the record(s).
   *
   * @throws IcdmException any validations or errors
   */
  protected abstract void delete() throws IcdmException;

  /**
   * Check, if any data have been changed All attributes which can be updated must be checked. Based on this
   * information, the changes will be committed.
   *
   * @return whether data changed or not
   * @throws IcdmException any validations or errors
   */
  protected abstract boolean dataChanged() throws IcdmException;

  /**
   * Sets whether the user details and modification time is to be set forcefully in the update mode. Setting this to
   * true will update the database record even if there are not other changes to be made
   *
   * @param status status to set
   */
  public void setUpdateTimestamp(final boolean status) {
    this.updateTimestamp = status;
  }

  /**
   * Sets the user attributes to the DB entity. This include created user, updated user, creation date and modifiction
   * date. <br>
   * Note:- 1. The command mode indicates the mode of operation of this entity, not the command. ie, whether the record
   * is for insert/update/delete.<br>
   * 2. The entity ID should not be the id of the entity object. It should be unique for an entity inside a command.
   * Same entity Id SHOULD be passed for same object for execute and undo operations. Same entity ID can be passed to
   * the same entity for insert, update, delete operations
   *
   * @param cmdMode mode of operation for this entity
   * @param entity  the entity to be modified
   */
  protected final void setUserDetails(final COMMAND_MODE cmdMode, final Object entity) {
    CommandUtils.INSTANCE.setValuesToEntity(this, cmdMode, entity);
  }


  /**
   * @return the dataToSet
   */
  protected final D getInputData() {
    return this.inputData;
  }

  /**
   * Retrieve the created/modified data
   *
   * @return the newData
   * @throws DataException if new data cannot be retrieved
   */
  public final D getNewData() throws DataException {

    // D dataObjectByID = this.busObj.getDataObjectByID(this.getObjId());
    // // set is master refresh applicable after creating new data object , if valid
    // if (dataObjectByID instanceof IMasterRefreshable) {
    // ((IMasterRefreshable) dataObjectByID).setIsMasterRefreshApplicable(this.isMasterRefreshApplicable);
    // }
    // return this.getCmdMode() == COMMAND_MODE.DELETE ? null : dataObjectByID;

    D newData = null;
    if (this.getCmdMode() != COMMAND_MODE.DELETE) {
      newData = this.busObj.getDataObjectByID(this.getObjId());
      // set is master refresh applicable after creating new data object , if valid
      if (newData instanceof IMasterRefreshable) {
        ((IMasterRefreshable) newData).setIsMasterRefreshApplicable(this.isMasterRefreshApplicable);
      }
    }
    return newData;


  }

  /**
   * @return the oldData
   */
  protected final D getOldData() {
    return this.oldData;
  }

  /**
   * @return the cmdMode
   */
  public COMMAND_MODE getCmdMode() {
    return this.cmdMode;
  }

  /**
   * validates the input data
   *
   * @throws IcdmException validation failures
   */
  protected abstract void validateInput() throws IcdmException;


  /**
   * Persist the entity. Also stores the primary key, for retrieval of new data using getNewData() method
   *
   * @param entity entity
   */
  protected final void persistEntity(final Object entity) {
    getEm().persist(entity);
    Object id = getEm().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
    if (id instanceof Long) {
      this.objId = (Long) id;
    }
  }


  /**
   * Returns the object id of the object being changed/created. <br>
   * Note : For create() operations, the id will be available only after persistEntity() call.
   *
   * @return the objId
   */
  public Long getObjId() {
    return this.objId;
  }

  /**
   * @param isMasterRefreshApplicable the isMasterRefreshApplicable to set
   */
  protected final void setMasterRefreshApplicable(final boolean isMasterRefreshApplicable) {
    this.isMasterRefreshApplicable = isMasterRefreshApplicable;
  }

  /**
   * Resolve command mode from the constructor input flags
   *
   * @param isDelete if <code>true</code>, mode is delete
   * @param isUpdate if <code>true</code>, mode is updated, provided <code>isDelete</code> flag is <code>false</code>
   * @return command mode
   */
  protected static final COMMAND_MODE resolveCommandModeA(final boolean isDelete, final boolean isUpdate) {
    if (isDelete) {
      return COMMAND_MODE.DELETE;
    }
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }

  /**
   * Resolve command mode from the constructor input flag(s)
   *
   * @param isUpdate if <code>true</code>, mode is update else mode is create
   * @return command mode
   */
  protected static final COMMAND_MODE resolveCommandModeU(final boolean isUpdate) {
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }

  /**
   * Resolve command mode from the constructor input flag(s)
   *
   * @param isDelete if <code>true</code>, mode is update else mode is create
   * @return command mode
   */
  protected static final COMMAND_MODE resolveCommandModeD(final boolean isDelete) {
    return isDelete ? COMMAND_MODE.DELETE : COMMAND_MODE.CREATE;
  }
}
