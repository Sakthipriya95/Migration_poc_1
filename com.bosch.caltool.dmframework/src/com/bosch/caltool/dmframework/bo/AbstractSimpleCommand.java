/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.sql.Timestamp;
import java.text.ParseException;

import javax.persistence.EntityManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateUtil;

/**
 * Abstract class as base for all commands using REST webservice
 *
 * @author bru2cob
 */
public abstract class AbstractSimpleCommand {


  /**
   * Instance of service data
   */
  private final ServiceData serviceData;


  /**
   * @param serviceData serviceData
   * @throws IcdmException any validations or errors
   */
  protected AbstractSimpleCommand(final ServiceData serviceData) throws IcdmException {
    this.serviceData = serviceData;
  }


  /**
   * @return the entity manager
   */
  protected final EntityManager getEm() {
    return this.serviceData.getEntMgr();
  }

  /**
   * @return logger
   */
  protected ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * The method to execute the command.
   * <p>
   * IMPORTANT : Do NOT invoke this method to execute the command. Use
   * {@link #executeChildCommand(AbstractSimpleCommand)} of the parent command or {@link CommandExecuter} instead.
   *
   * @throws IcdmException any validations or errors
   */
  protected abstract void execute() throws IcdmException;


  /**
   * Activities to be done after commiting the transaction. Note : database changes and validations should not be
   * included in this as this is outside the transaction's scope
   *
   * @throws IcdmException any validations or errors
   */
  protected abstract void doPostCommit() throws IcdmException;


  /**
   * @return true, if the user has privileges to execute this command
   * @throws IcdmException any validations or errors
   */
  protected abstract boolean hasPrivileges() throws IcdmException;


  /**
   * @return the serviceData
   */
  public ServiceData getServiceData() {
    return this.serviceData;
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
   * Executes a child command using the service data's command executor.
   *
   * @param child child command
   * @throws IcdmException validation failures, errors
   */
  protected final void executeChildCommand(final AbstractSimpleCommand child) throws IcdmException {
    CommandExecuter exec = child.getServiceData().getCommandExecutor();
    exec.executeChildCommand(child);
  }


  /**
   * Check, if an object has been changed. Takes care about NULL values.
   *
   * @param oldObj The original value of the property
   * @param newObj The new Value of the property
   * @return TRUE, if the object has been changed FALSE, if oldString and newString are equal
   */
  protected final boolean isObjectChanged(final Object oldObj, final Object newObj) {
    return !CommonUtils.isEqual(oldObj, newObj);
  }

  /**
   * Convert boolean to is equivalent code
   *
   * @param bool input
   * @return Y if input is true, else N
   */
  protected final String booleanToYorN(final boolean bool) {
    return CommonUtils.getBooleanCode(bool);
  }

  /**
   * Converts the string defined in default date time format, defined by <code>  DateFormat.DATE_FORMAT_15 </code>, to
   * SQL timestamp object
   *
   * @param sqlTimeStr input
   * @return Timestamp input as timestamp
   * @throws ParseException exception
   */
  protected final Timestamp string2timestamp(final String sqlTimeStr) throws ParseException {
    if (null == sqlTimeStr) {
      return null;
    }
    return DateUtil.convertStringToTimestamp(sqlTimeStr);
  }


  /**
   * Defines whether the changes by this command, as a child command, is relevant for CNS events. The method is
   * applicable only if the command is a child command. A command executed as a main command is always relevant for CNS
   * events.
   * <p>
   * Default implimentation is 'Relevant for CNS'
   *
   * @return if true, this command, as a child command, is relevant for CNS events
   */
  protected boolean isRelevantForCns() {
    return true;
  }

}
