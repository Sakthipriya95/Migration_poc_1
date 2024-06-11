/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand.EXECUTION_MODE;


/**
 * Keep utility methods for abstract command. The methods should not be called directly from other commands.
 *
 * @author bne4cob
 */
enum CommandUtils {
                   /**
                    * Unique instance for singleton implementation
                    */
                   INSTANCE;


  /**
   * Method name
   */
  public static final String METH_GET_CRE_USER = "getCreatedUser";
  /**
   * Method name
   */
  public static final String METH_GET_MOD_USER = "getModifiedUser";
  /**
   * Method name
   */
  public static final String METH_GET_CRE_DATE = "getCreatedDate";
  /**
   * Method name
   */
  public static final String METH_GET_MOD_DATE = "getModifiedDate";
  /**
   * Method name
   */
  public static final String METH_SET_CRE_USER = "setCreatedUser";
  /**
   * Method name
   */
  public static final String METH_SET_MOD_USER = "setModifiedUser";
  /**
   * Method name
   */
  public static final String METH_SET_CRE_DATE = "setCreatedDate";
  /**
   * Method name
   */
  public static final String METH_SET_MOD_DATE = "setModifiedDate";


  /**
   * Sets the user attributes to the DB entity. This include created user, updated user, creation date and modifiction
   * date
   *
   * @param command the command
   * @param cmdMode mode of execution for this entity. This might be different from the calling command's command mode
   * @param entity the entity to be modified
   * @param userAttributes entity user attributes
   */
  protected final void setUserDetails(final AbstractDataCommand command, final COMMAND_MODE cmdMode,
      final Object entity, final EntityUserAttributes userAttributes) {


    if (command.getExecutionMode() == EXECUTION_MODE.EXECUTION) {
      initialiseUserAtttObj(command, cmdMode, entity, userAttributes);
    }

    setUserAttrsInEntity(command, cmdMode, entity, userAttributes);

  }


  /**
   * Initialise the user attributes
   *
   * @param command command
   * @param commandMode mode of execution for this entity
   * @param entity entity
   * @param userAttributes user Attributes
   */
  private void initialiseUserAtttObj(final AbstractDataCommand command, final COMMAND_MODE commandMode,
      final Object entity, final EntityUserAttributes userAttributes) {

    switch (commandMode) {
      case INSERT:
        userAttributes.setNewCreatedUser(command.getDataCache().getAppUsername());
        userAttributes.setNewCreationDate(command.getCurrentTime());
        break;

      case UPDATE:
      case DELETE:
        caseCmdDel(command, entity, userAttributes);

        break;

      default:
        break;
    }

  }


  /**
   * @param command
   * @param entity
   * @param userAttributes
   */
  private void caseCmdDel(final AbstractDataCommand command, final Object entity,
      final EntityUserAttributes userAttributes) {
    userAttributes.setNewModifiedUser(command.getDataCache().getAppUsername());
    userAttributes.setNewModifiedDate(command.getCurrentTime());

    Object retObj = getValueFromEntity(command.getDataProvider().getLogger(), entity, METH_GET_CRE_USER);
    String retObjStr = null == retObj ? null : retObj.toString();
    userAttributes.setOldCreatedUser(retObjStr);

    retObj = getValueFromEntity(command.getDataProvider().getLogger(), entity, METH_GET_MOD_USER);
    retObjStr = null == retObj ? null : retObj.toString();
    userAttributes.setOldModifiedUser(retObjStr);

    userAttributes.setOldCreationDate(
        (Timestamp) getValueFromEntity(command.getDataProvider().getLogger(), entity, METH_GET_CRE_DATE));
    userAttributes.setOldModifiedDate(
        (Timestamp) getValueFromEntity(command.getDataProvider().getLogger(), entity, METH_GET_MOD_DATE));
  }

  /**
   * Sets the user attributes to the entity
   *
   * @param command command
   * @param commandMode command mode
   * @param entity entity object
   * @param userAttributes attributes object
   */
  private void setUserAttrsInEntity(final AbstractDataCommand command, final COMMAND_MODE commandMode,
      final Object entity, final EntityUserAttributes userAttributes) {

    switch (command.getExecutionMode()) {
      case EXECUTION:
      case REDO:
        // Set the attributes for insert/update
        setNewValuesToEntity(command, commandMode, entity, userAttributes);
        break;

      case UNDO:
        // For undo delete, set all the attributes
        setOldValuesToEntity(command, commandMode, entity, userAttributes);
        break;

      default:
        break;
    }

  }


  /**
   * Sets the old values to the entity
   *
   * @param command
   * @param commandMode
   * @param entity
   * @param userAttributes
   */
  private void setOldValuesToEntity(final AbstractDataCommand command, final COMMAND_MODE commandMode,
      final Object entity, final EntityUserAttributes userAttributes) {
    if (commandMode == COMMAND_MODE.DELETE) {
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_CRE_USER, String.class,
          userAttributes.getOldCreatedUser());
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_CRE_DATE, Timestamp.class,
          userAttributes.getOldCreationDate());
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_MOD_USER, String.class,
          userAttributes.getOldModifiedUser());
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_MOD_DATE, Timestamp.class,
          userAttributes.getOldModifiedDate());

    }
    else if (commandMode == COMMAND_MODE.UPDATE) {
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_MOD_USER, String.class,
          userAttributes.getOldModifiedUser());
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_MOD_DATE, Timestamp.class,
          userAttributes.getOldModifiedDate());

    }
  }


  /**
   * Sets the new values to entity
   *
   * @param command
   * @param commandMode
   * @param entity
   * @param userAttributes
   */
  private void setNewValuesToEntity(final AbstractDataCommand command, final COMMAND_MODE commandMode,
      final Object entity, final EntityUserAttributes userAttributes) {
    if (commandMode == COMMAND_MODE.INSERT) {
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_CRE_USER, String.class,
          userAttributes.getNewCreatedUser());
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_CRE_DATE, Timestamp.class,
          userAttributes.getNewCreationDate());

    }
    else if (commandMode == COMMAND_MODE.UPDATE) {
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_MOD_USER, String.class,
          userAttributes.getNewModifiedUser());
      setValueToEntity(command.getDataProvider().getLogger(), entity, METH_SET_MOD_DATE, Timestamp.class,
          userAttributes.getNewModifiedDate());

    }
  }


  /**
   * Sets the value to the entity
   *
   * @param logger logger
   * @param entity entity object
   * @param method to invoke s
   * @param valueToSet value to set
   */
  public void setValueToEntity(final ILoggerAdapter logger, final Object entity, final String method,
      final Class<?> valueType, final Object valueToSet) {

    try {
      final Method methodToInvoke = entity.getClass().getMethod(method, valueType);
      methodToInvoke.invoke(entity, valueToSet);
    }
    catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException exp) {
      logger.warn(exp.getMessage(), exp);
    }

  }

  /**
   * Sets the new values to entity
   *
   * @param command command
   * @param cmdMode cmdMode
   * @param entity entity
   */
  public void setValuesToEntity(final AbstractCommand command,
      final com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE cmdMode, final Object entity) {
    if (cmdMode == com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE.CREATE) {
      setValueToEntity(command.getLogger(), entity, METH_SET_CRE_USER, String.class,
          command.getServiceData().getUsername());
      setValueToEntity(command.getLogger(), entity, METH_SET_CRE_DATE, Timestamp.class, command.getCurrentTime());

    }
    else if (cmdMode == com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE.UPDATE) {
      setValueToEntity(command.getLogger(), entity, METH_SET_MOD_USER, String.class,
          command.getServiceData().getUsername());
      setValueToEntity(command.getLogger(), entity, METH_SET_MOD_DATE, Timestamp.class, command.getCurrentTime());

    }
  }

  /**
   * Gets the value from the entity
   *
   * @param logger logger
   * @param entity entity object
   * @param method to invoke
   * @return the value
   */
  public Object getValueFromEntity(final ILoggerAdapter logger, final Object entity, final String method) {

    try {
      final Method methodToInvoke = entity.getClass().getMethod(method);
      return methodToInvoke.invoke(entity);
    }
    catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException
        | InvocationTargetException exp) {
      logger.warn(exp.getMessage(), exp);
    }
    return null;

  }

  /**
   * this method checks whether the data is valid considering the parallel changes that can be done by other clients
   *
   * @param command the command
   * @param entity the entity for which the stale data check is to be performed
   */
  protected final void validateStaleData(final AbstractDataCommand command, final Object entity) {
    // ICDM-943 Not applicable as validateStaleDataInNotifications() method handles parallel change
  }
}
