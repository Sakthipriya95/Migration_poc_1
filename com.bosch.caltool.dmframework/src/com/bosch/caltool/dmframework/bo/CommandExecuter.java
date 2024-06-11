/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand.ERROR_CAUSE;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author bru2cob
 */
public class CommandExecuter {

  /**
   * Oracle error code for Unique Constraint violation
   */
  private static final String ORA_00001_ERR_CODE = "ORA-00001";
  private final ServiceData serviceData;

  private final List<AbstractSimpleCommand> mainCommandList = new ArrayList<>();

  private final List<AbstractSimpleCommand> childcommandList = new ArrayList<>();

  /**
   * @param serviceData ServiceData instance
   */
  public CommandExecuter(final ServiceData serviceData) {
    this.serviceData = serviceData;
  }

  /**
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * Add a new command to the command stack A new command will be executed immediately. If the most recently command is
   * not the latest in the stack (this is when another command has been undone), the tail of the command stack will be
   * removed because undone commands can not be redone. The new command will be added to the command stack only if if
   * could be executed without errors.
   *
   * @param commandList the commands to be added
   * @throws IcdmException validation failures or other errors
   */
  public final void execute(final List<AbstractSimpleCommand> commandList) throws IcdmException {

    getLogger().debug("Commands execution started");

    clearCommandList();

    // Ensures that privilege check is done only for main command
    if (CommonUtils.isNotEmpty(commandList) && !commandList.get(0).hasPrivileges()) {
      throw new UnAuthorizedAccessException("Insufficient privileges to do this operation");
    }

    getLogger().debug("Commands count = {}", commandList.size());

    try {
      // start a transaction
      JPAUtils.beginTransaction(this.serviceData.getEntMgr());

      for (AbstractSimpleCommand newCommand : commandList) {
        // execute the command
        newCommand.execute();
        this.mainCommandList.add(newCommand);
      }

      try {
        // commit the changes
        getLogger().debug("Commiting changes ...");
        JPAUtils.commitTransaction(this.serviceData.getEntMgr());
        getLogger().debug("Changes commited successfully");

        forwardChangeData();

        for (AbstractSimpleCommand command : commandList) {
          command.doPostCommit();
        }
      }
      catch (RollbackException rollbackExp) {
        getLogger().debug("Rollback exception", rollbackExp);
        // handle database problems
        decodeRollbackException(rollbackExp);
      }

    }
    catch (Exception exp) {
      JPAUtils.rollbackTransaction(this.serviceData.getEntMgr());
      getLogger().debug("Transaction rolled back since errors were encountered during command(s) execution");
      if (exp instanceof IcdmException) {
        throw (IcdmException) exp;
      }
      throw new IcdmException(exp.getMessage(), exp);
    }

    getLogger().debug("Command execution completed");

  }

  /**
   * Add a new command to the command stack A new command will be executed immediately. If the most recently command is
   * not the latest in the stack (this is when another command has been undone), the tail of the command stack will be
   * removed because undone commands can not be redone. The new command will be added to the command stack only if if
   * could be executed without errors.
   *
   * @param command the command to be added
   * @throws IcdmException validation failures or other errors
   */
  public final void execute(final AbstractSimpleCommand command) throws IcdmException {

    getLogger().debug("Main command execution started : {} ", command);

    clearCommandList();

    // Ensures that privilege check is done only for main command
    if (!command.hasPrivileges()) {
      throw new UnAuthorizedAccessException("Insufficient privileges to do this operation");
    }

    try {
      // start a transaction
      JPAUtils.beginTransaction(command.getEm());

      // execute the command
      command.execute();
      this.mainCommandList.add(command);

      // commit the changes
      commitChanges(command);
    }
    catch (Exception exp) {
      JPAUtils.rollbackTransaction(command.getEm());
      getLogger().debug("Transaction rolled back since errors were encountered during command execution");

      if (exp instanceof IcdmException) {
        throw (IcdmException) exp;
      }
      throw new IcdmException(exp.getMessage(), exp);
    }

    getLogger().debug("Main command execution completed : {}", command);

  }


  private void clearCommandList() {
    this.mainCommandList.clear();
    this.childcommandList.clear();
  }

  /**
   * executes a child command
   *
   * @param cmd the command to be executed
   * @throws IcdmException validation failures or other errors
   */
  public final void executeChildCommand(final AbstractSimpleCommand cmd) throws IcdmException {
    // execute the command
    cmd.execute();

    this.childcommandList.add(cmd);
  }

  /**
   * Commit the changes in the current transaction. In case of an error, first set the Error in the command, then set
   * the ErrorCause and only after this log the error. This sequence is necessary since the getString method in the
   * command uses the Error information.
   *
   * @param command the command
   * @throws IcdmException
   */
  private void commitChanges(final AbstractSimpleCommand command) throws IcdmException {
    try {
      // commit the changes
      getLogger().debug("Commiting changes ...");
      JPAUtils.commitTransaction(command.getEm());
      getLogger().debug("Changes commited successfully");

      forwardChangeData();

      command.doPostCommit();
    }
    catch (RollbackException rollbackExp) {
      getLogger().debug("Rollback exception", rollbackExp);
      // handle database problems
      decodeRollbackException(rollbackExp);
    }
  }

  /**
   * @param rollbackExp
   * @throws CommandException new wrapped exception for the input exception
   */
  private void decodeRollbackException(final RollbackException rollbackExp) throws CommandException {
    Throwable rollbackExCause = rollbackExp.getCause();

    if (rollbackExCause == null) {
      throw new CommandException("Error : " + rollbackExp.getMessage() + ". " + ERROR_CAUSE.OTHER_ROLLBACK_EXCEPTION,
          rollbackExCause);
    }

    if (rollbackExCause.getClass() == OptimisticLockException.class) {
      throw new CommandException(
          "Error : Parallel change detected in the object(s) being modified. " + ERROR_CAUSE.OPTIMISTIC_LOCK,
          rollbackExCause);
    }

    // Special handling of database exception messages to identify duplicate record validations
    // a DatabaseException can have several root causes
    if (rollbackExCause.getClass() == DatabaseException.class) {
      Throwable dbExCause = rollbackExCause.getCause();
      String causeMessage = CommonUtils.checkNull(dbExCause.getMessage());

      // If the cause of database exception is ORA-00001 (Unique constraint violation), then set the error message
      // in the command as 'Record already exists'
      if ((dbExCause.getClass() == SQLIntegrityConstraintViolationException.class) &&
          (causeMessage.contains(ORA_00001_ERR_CODE))) {
        throw new CommandException("Record already exists. " + ERROR_CAUSE.CONSTRAINT_VIOLATION, dbExCause);
      }
      throw new CommandException("Error : " + dbExCause.getMessage() + ". " + ERROR_CAUSE.OTHER_DB_EXCEPTION,
          dbExCause);
    }
  }

  /**
  *
  */
  private void forwardChangeData() {
    new ChangeEventMessageHandler(getServiceData()).send(this);
  }

  /**
   * @return the serviceData
   */
  ServiceData getServiceData() {
    return this.serviceData;
  }

  /**
   * @return list of main commands
   */
  List<AbstractSimpleCommand> getMainCommandList() {
    return new ArrayList<>(this.mainCommandList);
  }

  /**
   * @return list of child commands
   */
  List<AbstractSimpleCommand> getChildCommandList() {
    return new ArrayList<>(this.childcommandList);
  }

}
