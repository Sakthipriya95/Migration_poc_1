/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.sql.Timestamp;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayChangeEvent;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateUtil;


/**
 * Abstract class as base for all commands
 *
 * @author hef2fe
 */
@Deprecated
public abstract class AbstractDataCommand {

  /**
   * string constant for failed
   */
  private static final String STR_FAILED = " failed";

  /**
   * Dupliate ID given to store the Entity User Attributes
   */
  private static final int ERR_CODE_DUP_ID = 1001;

  /**
   * Insufficient privileges
   */
  private static final int ERR_CODE_NO_PRIV = 1002;

  /**
   * enumerator to define all possible error states
   *
   * @author hef2fe
   */
  public enum ERROR_CAUSE {
                           /**
                            * No errors
                            */
                           NONE,
                           /**
                            * Locking error
                            */
                           OPTIMISTIC_LOCK,
                           /**
                            * Constraint violated
                            */
                           CONSTRAINT_VIOLATION,
                           /**
                            * DB connection lost exception
                            */
                           DB_CON_LOST_EXCEPTION,
                           /**
                            * Other DB exceptions
                            */
                           OTHER_DB_EXCEPTION,
                           /**
                            * Other roll back exceptions
                            */
                           OTHER_ROLLBACK_EXCEPTION,
                           /**
                            * Command exception
                            */
                           COMMAND_EXCEPTION
  }

  /**
   * the possible modes of the command
   *
   * @author hef2fe
   */
  public enum COMMAND_MODE {
                            /**
                             * Insert mode
                             */
                            INSERT,
                            /**
                             * Update mode
                             */
                            UPDATE,
                            /**
                             * Delete mode
                             */
                            DELETE
  }

  /**
   * The type of exection of this command. Identifies whether this is execution/undo/redo
   *
   * @author bne4cob
   */
  protected enum EXECUTION_MODE {
                                 /**
                                  * Execution
                                  */
                                 EXECUTION,
                                 /**
                                  * Undo
                                  */
                                 UNDO,
                                 /**
                                  * Redo
                                  */
                                 REDO;
  }

  /**
   * Mode of execution
   */
  private EXECUTION_MODE executionMode;

  /**
   * Variable to store the error state of the command.
   */
  protected ERROR_CAUSE errorCause = ERROR_CAUSE.NONE;

  /**
   * the exception which has been caused an error.
   */
  protected Throwable errorException;

  /**
   * Whether to validate the stale data during update/delete
   */
  private boolean staleDataValidate = true;

  /**
   * Update the timestamp of the record. Can be set and used, if only timestamp of the entity is to be updated (ie,
   * MODIFIED_DATE)
   */
  private boolean updateTimestamp;

  /**
   * If true, the user privileges is enabled
   */
  private boolean privChkEnabled = true;

  /**
   * the current mode of the command.
   */
  protected COMMAND_MODE commandMode;

  /**
   * data provider
   */
  private final AbstractDataProvider dataProvider;

  /**
   * Stores the entity's user attributes
   */
  private final Map<String, EntityUserAttributes> userAttrMap = new ConcurrentHashMap<String, EntityUserAttributes>();

  /**
   * collection of changed data
   */
  private final Map<Long, ChangedData> changedData = new ConcurrentHashMap<Long, ChangedData>();


  /**
   * collection of changed data
   */
  private DisplayChangeEvent dispChangeEvnt;


  /**
   * Constructor
   *
   * @param dataProvider the data provider
   */
  protected AbstractDataCommand(final AbstractDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  /**
   * @return the command mode of this command
   */
  public COMMAND_MODE getCommandMode() {
    return this.commandMode;
  }


  /**
   * @return the dce
   */
  protected DisplayChangeEvent getDce() {
    return this.dispChangeEvnt;
  }


  /**
   * @param dce the dce to set
   */
  protected void setDce(final DisplayChangeEvent dce) {
    this.dispChangeEvnt = dce;
  }

  /**
   * @return the changedData
   */
  protected Map<Long, ChangedData> getChangedData() {
    return this.changedData;
  }

  /**
   * The method to execute the command.
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  public final void execute() throws CommandException {

    if (getDataProvider().getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getDataProvider().getLogger()
          .debug(CommonUtils.concatenate("Command execution started : ", toString(), "; Mode=", this.commandMode));
    }

    this.executionMode = EXECUTION_MODE.EXECUTION;
    this.changedData.clear();

    // Validate whether the user has necessary rights to execute this command. Undo and redo operations do not perform
    // this check, as the validation has already happend during execute()
    if (isPrivChkEnabled() && !hasPrivileges()) {
      throw new CommandException("No sufficient privileges to do this operation", ERR_CODE_NO_PRIV);
    }

    // Removed since the code is deprecated
    // icdm-943

    doExecute();
    getDataProvider().getLogger()
        .debug(CommonUtils.concatenate("Command execution finished : ", toString(), "; Mode=", this.commandMode));

  }

  /**
   * Execute the command
   *
   * @throws CommandException when command level business validation fails
   */
  private void doExecute() throws CommandException {
    // Executes an insert command
    try {
      if (this.commandMode == COMMAND_MODE.INSERT) {
        executeInsertCommand();
      }
      // Executes update command
      else if ((this.commandMode == COMMAND_MODE.UPDATE) && (dataChanged() || this.updateTimestamp)) {
        executeUpdateCommand();
      }
      // Executes delete command
      else if (this.commandMode == COMMAND_MODE.DELETE) {
        executeDeleteCommand();
      }
    }
    catch (CommandException exp) {
      rollBackDataModel();
      // set the exception
      setError(exp);
      // set cause related to command exception
      setErrorCause(ERROR_CAUSE.COMMAND_EXCEPTION);
      throw exp;
    }
  }

  /**
   * Performs the steps required to insert new record(s) for this command. Implementations of this class should provide
   * the steps for inserting a new record.
   *
   * @throws CommandException for any business level validations
   */
  protected abstract void executeInsertCommand() throws CommandException;

  /**
   * Performs the steps required to update record(s) for this command. Implementations of this class should provide the
   * steps for modifying the record(s).
   *
   * @throws CommandException for any business level validations
   */
  protected abstract void executeUpdateCommand() throws CommandException;

  /**
   * Performs the steps required to delete record(s) for this command. Implementations of this class should provide the
   * steps for deleting the record(s).
   *
   * @throws CommandException for any business level validations
   */
  protected abstract void executeDeleteCommand() throws CommandException;

  /**
   * The method to undo the command.
   *
   * @throws CommandException when command level business validation fails
   */
  public final void undo() throws CommandException {
    if (getDataProvider().getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getDataProvider().getLogger()
          .debug(CommonUtils.concatenate(toString(), " : Undo started, Mode=", this.commandMode));
    }

    this.executionMode = EXECUTION_MODE.UNDO;
    this.changedData.clear();

    try {
      if (this.commandMode == COMMAND_MODE.INSERT) {
        undoInsertCommand();
      }
      else if ((this.commandMode == COMMAND_MODE.UPDATE) && (dataChanged() || this.updateTimestamp)) {
        undoUpdateCommand();
      }
      else if (this.commandMode == COMMAND_MODE.DELETE) {
        undoDeleteCommand();
      }
    }
    catch (CommandException exp) {
      // set the exception
      setError(exp);
      // set cause related to command exception
      setErrorCause(ERROR_CAUSE.COMMAND_EXCEPTION);
      throw exp;
    }

    getDataProvider().getLogger().debug("Undo completed");
  }

  /**
   * The method to redo the command.
   *
   * @throws CommandException when command level business validation fails
   */
  public final void redo() throws CommandException {
    if (getDataProvider().getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getDataProvider().getLogger()
          .debug(CommonUtils.concatenate(toString(), " : Redo started, Mode=", this.commandMode));
    }

    this.executionMode = EXECUTION_MODE.REDO;
    this.changedData.clear();
    doExecute();

    getDataProvider().getLogger().debug("Redo completed");
  }

  /**
   * Provides the steps to undo an insert command.
   *
   * @throws CommandException for various validations
   */
  protected abstract void undoInsertCommand() throws CommandException;

  /**
   * Provides the steps to undo an update command.
   *
   * @throws CommandException for any validations
   */
  protected abstract void undoUpdateCommand() throws CommandException;

  /**
   * Provides the steps to undo a delete command.
   *
   * @throws CommandException for various validations
   */
  protected abstract void undoDeleteCommand() throws CommandException;

  /**
   * Check, if any data have been changed All attributes which can be updated must be checked. Based on this
   * information, the changes will be committed.
   *
   * @return whether data changed or not
   */
  protected abstract boolean dataChanged();

  /**
   * Get a String which describes the command (what has the command done) This is mostly used to display the command in
   * the CommandStack
   *
   * @return the command description
   */
  public String getString() {
    // ICDM-2612
    return getString(getPrimaryObjectType(), getPrimaryObjectIdentifier());
  }


  /**
   * Get a summary of the change done in this command (what details has been changed) This is mostly used to display in
   * the transaction summary
   *
   * @return the command description
   */
  public abstract TransactionSummary getTransactionSummary();

  /**
   * Get a qualified String which describes the command including the commandMode
   *
   * @param objTypeName The name of the object on which the command works. If this is <code>null</code> then result of
   *          <code>getPrimaryObjectType()</code> is set as type.
   * @param objectIdentifier The unique identifier of the object. If not required, then set as <code>null</code>.
   * @return a qualified command description
   */
  protected final String getString(final String objTypeName, final String objectIdentifier) {
    final StringBuilder message = new StringBuilder();
    final String objType = CommonUtils.isEmptyString(objTypeName) ? getPrimaryObjectType() : objTypeName;
    final String objIdentifier =
        CommonUtils.isEmptyString(objectIdentifier) ? "" : CommonUtils.concatenate("(", objectIdentifier, ")");

    if ((this.executionMode != null) && (this.executionMode != EXECUTION_MODE.EXECUTION)) {
      message.append(this.executionMode.toString()).append(' ');
    }


    switch (getErrorCause()) {
      case NONE:
        message.append(this.commandMode).append(' ').append(objType).append(' ').append(objectIdentifier)
            .append(" completed");
        break;

      case COMMAND_EXCEPTION:
      case OTHER_DB_EXCEPTION:
      case OTHER_ROLLBACK_EXCEPTION:
        message.append(objType).append(' ').append(this.commandMode).append(STR_FAILED).append(objIdentifier)
            .append(". Cause - ").append(getErrorMessage());
        break;

      case CONSTRAINT_VIOLATION:
        message.append(objType).append(' ').append(this.commandMode).append(STR_FAILED).append(objIdentifier)
            .append(". Record already exists.");
        break;

      case OPTIMISTIC_LOCK:
        message.append(objType).append(' ').append(this.commandMode).append(STR_FAILED).append(objIdentifier)
            .append(". Parallel change detected in the object(s) being modified.");
        break;

      default:
        message.append(this.commandMode).append(' ').append(objType).append(' ').append(objectIdentifier);

    }

    return message.toString();

  }

  /**
   * Set the exception which caused an error
   *
   * @param exception exception
   */
  protected final void setError(final Throwable exception) {

    this.errorException = exception;

  }

  /**
   * Set the root cause of the error
   *
   * @param cause the error cause
   */
  protected final void setErrorCause(final ERROR_CAUSE cause) {

    this.errorCause = cause;

  }

  /**
   * Get the root cause for the error
   *
   * @return the error cause
   */
  public final ERROR_CAUSE getErrorCause() {

    return this.errorCause;
  }

  /**
   * Get the error message If no error occured, the error message is an empty String Method should be overridden in all
   * commands
   *
   * @return the error message
   */
  public String getErrorMessage() {

    String errorMessage = "";

    String expMsg = this.errorException == null ? "" : this.errorException.getMessage();

    switch (this.errorCause) {
      case NONE:
        break;

      case COMMAND_EXCEPTION:
      case DB_CON_LOST_EXCEPTION:
        errorMessage = CommonUtils.concatenate("Error : ", expMsg);
        break;

      default:
        errorMessage = CommonUtils.concatenate("Error : ", expMsg, ' ', this.errorCause);
        break;
    }

    return errorMessage;
  }

  /**
   * Check, if a String object has been changed Takes care about NULL values.
   *
   * @param oldString The original value of the String
   * @param newString The new Value of the String
   * @return TRUE, if the Sting has been changed FALSE, if oldString and newString are equal
   */
  protected final boolean isStringChanged(final String oldString, final String newString) {
    return isObjectChanged(oldString, newString);
  }

  /**
   * Check, if an object has been changed. Takes care about NULL values.
   *
   * @param oldObj The original value of the property
   * @param newObj The new Value of the property
   * @return TRUE, if the object has been changed FALSE, if oldString and newString are equal
   */
  // ICDM-2612
  protected final boolean isObjectChanged(final Object oldObj, final Object newObj) {
    return !CommonUtils.isEqual(oldObj, newObj);
  }

  /**
   * This method checks whether an item is changed or not and add accordingly to the transaction summary
   *
   * @param detailsList existing set to add more details
   * @param oldval value before command execution
   * @param newVal value after command execution
   * @param modifiedItem item which is modified
   * @return Set of updated TransactionSummaryDetails
   */
  protected SortedSet<TransactionSummaryDetails> addTransactionSummaryDetails(
      final SortedSet<TransactionSummaryDetails> detailsList, final String oldval, final String newVal,
      final String modifiedItem) {
    TransactionSummaryDetails details;
    final String checkedNewVal = (newVal == null) ? "" : newVal;
    final String checkedOldVal = (oldval == null) ? "" : oldval;

    if (isStringChanged(checkedOldVal, checkedNewVal)) {
      details = new TransactionSummaryDetails();
      details.setOldValue(checkedOldVal);
      details.setNewValue(checkedNewVal);
      details.setModifiedItem(modifiedItem);
      detailsList.add(details);
    }
    return detailsList;
  }

  /**
   * ICDM-484 <br>
   * This method sets sets common params of TransactionSummary data
   *
   * @param summaryData summaryData data from command
   * @return TransactionSummary obje filled with common details
   */
  protected final TransactionSummary getTransactionSummary(final TransactionSummary summaryData) {
    // fill db transaction summary only if transaction is success
    if (ERROR_CAUSE.NONE == getErrorCause()) {
      // set the details
      if (null == summaryData.getObjectName()) {
        summaryData.setObjectName(getPrimaryObjectIdentifier());
      }
      if (null == summaryData.getObjectType()) {
        summaryData.setObjectType(getPrimaryObjectType());
      }
      if (null == summaryData.getOperation()) {
        summaryData.setOperation(this.commandMode.toString());
      }
    }
    return summaryData;
  }

  /**
   * Utility method to get the current time as Timestamp object
   *
   * @return the current time
   */
  protected final Timestamp getCurrentTime() {
    return DateUtil.getCurrentUtcTime();
  }

  /**
   * Sets the flag for validating the stale data
   *
   * @param validate true/false
   */
  protected void setStaleDataValidate(final boolean validate) {
    this.staleDataValidate = validate;
  }

  /**
   * @param entity entity for which the stale data check is to be performed
   * @throws CommandException if the data is stale
   */
  protected final void validateStaleData(final Object entity) throws CommandException {
    CommandUtils.INSTANCE.validateStaleData(this, entity);
  }

  /**
   * @return the dataProvider
   */
  protected AbstractDataProvider getDataProvider() {
    return this.dataProvider;
  }

  /**
   * Activities to be done after commiting the transaction. Note : database changes and validations should not be
   * included in this as this is outside the transaction's scope
   */
  protected abstract void doPostCommit();


  /**
   * Undo the Commands in case of any Exception eg: If the command is for Insert do Undo insert icdm-177
   */
  protected abstract void rollBackDataModel();

  /**
   * @return the execution Mode
   */
  protected final EXECUTION_MODE getExecutionMode() {
    return this.executionMode;
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
   * @param entity the entity to be modified
   * @param entityID a unique ID for this entity.
   */
  protected final void setUserDetails(final COMMAND_MODE cmdMode, final Object entity, final String entityID) {
    EntityUserAttributes userAttributes;

    if (getExecutionMode() == EXECUTION_MODE.EXECUTION) {
      if (this.userAttrMap.containsKey(entityID)) {
        throw new DMRuntimeException("Dupliate ID given to store the Entity User Attributes", ERR_CODE_DUP_ID);
      }
      userAttributes = new EntityUserAttributes();
    }
    else {
      userAttributes = this.userAttrMap.get(entityID);
    }

    CommandUtils.INSTANCE.setUserDetails(this, cmdMode, entity, userAttributes);

    if (getExecutionMode() == EXECUTION_MODE.EXECUTION) {
      this.userAttrMap.put(entityID, userAttributes);
    }

  }

  /**
   * @return the staleDataValidate
   */
  protected final boolean isStaleDataValidate() {
    return this.staleDataValidate;
  }

  /**
   * @return the dataLoader
   */
  protected AbstractDataLoader getDataLoader() {
    return this.dataProvider.getDataLoader();
  }

  /**
   * @return the dataCache
   */
  protected AbstractDataCache getDataCache() {
    return this.dataProvider.getDataCache();
  }

  /**
   * @return the entityProvider
   */
  protected AbstractEntityProvider getEntityProvider() {
    return this.dataProvider.getEntityProvider();
  }

  /**
   * Returns the ID (key) of the primary object being maintained by this command.
   * <p>
   * Note : If invoked before the command's main operation [execute()/undo()/redo()], the method returns
   * <code>null</code> for insert operations.
   *
   * @return the primary object's ID
   */
  public abstract Long getPrimaryObjectID();

  /**
   * Gets the type of object on which the command works as string. This method is used while creating log messages.
   *
   * @return the object type
   */
  public abstract String getPrimaryObjectType();

  /**
   * Gets object identifier on which the command worked. This method is used for transaction summary.
   *
   * @return the command description
   */
  public abstract String getPrimaryObjectIdentifier();

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
   * icdm-943 Validate whether the record to change, requested by this command, is already modified by another client
   * (received through notification).
   *
   * @param dce2 notification event
   * @param command command to execute
   * @return true, if data is not stale, else false.
   * @throws CommandException icdm-943
   */
  private boolean validateStaleDataInNotifications() throws CommandException {
    final Long primObjID = getPrimaryObjectID();
    if ((primObjID != null) && this.dispChangeEvnt.getChangedData().containsKey(primObjID)) {
      final ChangedData chData = this.dispChangeEvnt.getChangedData().get(primObjID);
      if (chData.getEntityType().stopCommandForEntityUpdate()) {
        final CommandException exeption = new CommandException(
            "Parallel change detected in " + chData.getEntityClass().getSimpleName(), DMErrorCodes.ERR_STALE_DATA);
        setError(exeption);
        setErrorCause(ERROR_CAUSE.COMMAND_EXCEPTION);
        throw exeption;
      }
    }
    ObjectStore.getInstance().getLogger()
        .debug("AbstractDataCommand : Stale data validation completed for received CQNs.");
    return true;
  }

  /**
   * @return true, if the user has privileges to execute this command
   */
  protected boolean hasPrivileges() {
    return true;
  }


  /**
   * @return <code>true</code> if the privilege check is enabled for this command.
   */
  public final boolean isPrivChkEnabled() {
    return this.privChkEnabled;
  }

  /**
   * Set this flag to false, to disable user privilege check before executing the command
   *
   * @param privChkEnabled the privChkEnabled to set
   */
  public final void setPrivChkEnabled(final boolean privChkEnabled) {
    this.privChkEnabled = privChkEnabled;
  }


}
