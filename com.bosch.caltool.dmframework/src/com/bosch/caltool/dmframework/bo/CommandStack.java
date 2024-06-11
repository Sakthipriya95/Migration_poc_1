/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand.ERROR_CAUSE;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayChangeEvent;
import com.bosch.caltool.dmframework.notification.GlobalCQNNotifier;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * The CommandStack class is used to manage a command list with the features UNDO and REDO
 *
 * @author hef2fe
 */
@Deprecated
public class CommandStack {

  /**
   * Oracle error code for Unique Constraint violation
   */
  private static final String ORA_00001_ERR_CODE = "ORA-00001";

  /**
   * the command list
   */
  private final List<AbstractDataCommand> commandList;

  /**
   * the index of the command executed recently
   */
  private int curCmdIndex;

  /**
   * CQN Notifier
   */
  private final GlobalCQNNotifier cqnNotifier;

  /**
   * Create a new CommandStack
   */
  public CommandStack() {

    this.cqnNotifier = new GlobalCQNNotifier();

    // create an empty command list
    this.commandList = new ArrayList<AbstractDataCommand>();

    // initialize the most recently executed command
    this.curCmdIndex = -1;
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
   * @param newCommand the command to be added
   */
  public final void addCommand(final AbstractDataCommand newCommand) {
    final AbstractDataProvider dataProvider = newCommand.getDataProvider();
    if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getLogger().debug("CommandStack : Add command to stack : " + newCommand);
    }

    final DisplayChangeEvent dce = this.cqnNotifier.refreshDataModel();
    try {
      // Deprecated call so the blocker issue code is commented

      // start a transaction
      dataProvider.getEntityProvider().startTransaction();

      newCommand.setDce(dce);
      // execute the command
      newCommand.execute();

      // commit the changes
      commitChanges(newCommand);

    }
    catch (DatabaseException dbe) {
      handleConnectionClosedException(newCommand, dbe);
      rollbackCmdDataModel(newCommand);
      rollbackTransaction(newCommand);
    }
    catch (CommandException cmdException) {
      handleCommandException(newCommand, cmdException);
      rollbackTransaction(newCommand);
    }
    finally {
      dataProvider.getEntityProvider().endTransaction();
    }


    if (newCommand.getErrorCause() == ERROR_CAUSE.NONE) {
      // if the current command is not the last command
      // (because there are UNDOne commands)
      // remove the UNDOne commands

      if ((this.curCmdIndex + 1) < this.commandList.size()) {
        for (int i = this.curCmdIndex + 1; i < this.commandList.size(); i++) {
          this.commandList.remove(this.curCmdIndex + 1);
        }
      }

      // insert the new command after the current command
      this.commandList.add(++this.curCmdIndex, newCommand);

      // iCDM-484 , add this transaction to summary, when error NONE error cause
      TransactionSummary tranSummary = newCommand.getTransactionSummary();
      if (tranSummary != null) {
        getLogger().debug("CommandStack : Adding transaction to summary : " + newCommand);
        ObjectStore.getInstance().getSummaryHandler().addToTransactionSummary(tranSummary);
      }

      // Add the changes from the command for GUI refresh
      dce.getChangedData().putAll(newCommand.getChangedData());
    }

    if (ObjectStore.getInstance().getCqnMode() != ObjectStore.CQN_MODE_NO) {
      this.cqnNotifier.notifyUI(dce);
    }

    if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
      getLogger().debug("CommandStack : Add command to stack completed : " + newCommand);
    }

  }

  /**
   * @param newCommand the command
   * @param cmdException command exception
   */
  private void handleCommandException(final AbstractDataCommand command, final CommandException cmdException) {
    command.setError(cmdException);
    command.setErrorCause(ERROR_CAUSE.COMMAND_EXCEPTION);
    getLogger().error(cmdException.getMessage(), cmdException);
  }

  /**
   * Rollback data model changes done by the command
   */
  private void rollbackCmdDataModel(final AbstractDataCommand command) {
    getLogger().debug("Rolling back the changes...");
    // Undo the commands after exception
    command.rollBackDataModel();
    getLogger().debug("Data model changes rolled back");
  }

  /**
   * handle Connection Closed Exception
   *
   * @param command command
   * @param except Exception
   */
  private void handleConnectionClosedException(final AbstractDataCommand command, final Exception except) {
    Throwable exceptToSet = except;
    if (except.getCause() instanceof SQLRecoverableException) {
      exceptToSet = except.getCause();
      command.setErrorCause(ERROR_CAUSE.DB_CON_LOST_EXCEPTION);
    }
    else {
      command.setErrorCause(ERROR_CAUSE.OTHER_DB_EXCEPTION);
    }
    command.setError(exceptToSet);
    if (exceptToSet != null) {
      getLogger().error(exceptToSet.getMessage(), exceptToSet);
    }
  }

  /**
   * Commit the changes in the current transaction. In case of an error, first set the Error in the command, then set
   * the ErrorCause and only after this log the error. This sequence is necessary since the getString method in the
   * command uses the Error information.
   *
   * @param command the command
   */
  private void commitChanges(final AbstractDataCommand command) {
    try {
      final AbstractDataProvider dataProvider = command.getDataProvider();
      // commit the changes
      getLogger().debug("Commiting changes ...");
      dataProvider.getEntityProvider().commitChanges();
      getLogger().debug("Changes commited successfully");
      command.doPostCommit();
    }
    catch (RollbackException rollbackExp) {
      getLogger().debug("Rolling back the changes...");
      // Undo the commands after exception
      command.rollBackDataModel();
      getLogger().debug("Data model changes rolled back");

      // handle database problems
      decodeRollbackException(rollbackExp, command);
    }
  }

  /**
   * Decode the exception thrown during commit the transaction and set the details in the command
   *
   * @param rollbackExp RollbackException
   * @param command AbstractDataCommand
   */
  private void decodeRollbackException(final RollbackException rollbackExp, final AbstractDataCommand command) {
    Throwable rollbackExCause = rollbackExp.getCause();

    if (rollbackExCause == null) {
      command.setError(rollbackExCause);
      command.setErrorCause(ERROR_CAUSE.OTHER_ROLLBACK_EXCEPTION);
      getLogger().error("RollbackException: " + command.getString(), rollbackExp);
    }
    else {
      if (rollbackExCause.getClass() == OptimisticLockException.class) {
        command.setError(rollbackExCause);
        command.setErrorCause(ERROR_CAUSE.OPTIMISTIC_LOCK);
        getLogger().error("OptimisticLockException: " + command.getString(), rollbackExp);

      }
      // Special handling of database exception messages to identify duplicate record validations
      // a DatabaseException can have several root causes
      else if (rollbackExCause.getClass() == DatabaseException.class) {
        Throwable dbExCause = rollbackExCause.getCause();
        String causeMessage = CommonUtils.checkNull(dbExCause.getMessage());

        command.setError(dbExCause);

        // If the cause of database exception is ORA-00001 (Unique constraint violation), then set the error message
        // in
        // the command as 'Record already exists'
        if ((dbExCause.getClass() == SQLIntegrityConstraintViolationException.class) &&
            (causeMessage.contains(ORA_00001_ERR_CODE))) {

          command.setErrorCause(ERROR_CAUSE.CONSTRAINT_VIOLATION);
          getLogger().error(causeMessage + " : " + command.getString(), rollbackExp);

        }
        else {
          command.setErrorCause(ERROR_CAUSE.OTHER_DB_EXCEPTION);
          getLogger().error("DatabaseException: " + command.getString(), rollbackExp);

        }
      }
    }
  }

  /**
   * Undo the recently executed command Returns the last command even if the command could not been UNDOne, returns NULL
   * only if there is no command to UNDO
   *
   * @return the command which has been UNDOne returns NULL if no command could be UNDOne
   */
  public final AbstractDataCommand undo() {
    getLogger().debug("CommandStack : Undo command stack invoked");


    // check if there are commands to UNDO
    if (canUndo()) {

      final AbstractDataCommand lastCommand = this.commandList.get(this.curCmdIndex--);
      final AbstractDataProvider dataProvider = lastCommand.getDataProvider();

      final DisplayChangeEvent dce = this.cqnNotifier.refreshDataModel();

      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("CommandStack : Undo command stack : " + lastCommand);
      }
      try {

        // start a new transaction
        dataProvider.getEntityProvider().startTransaction();

        // undo the current command, decrement the current command index
        lastCommand.undo();

        // commit the changes
        commitChanges(lastCommand);

      }
      catch (DatabaseException dbe) {
        handleConnectionClosedException(lastCommand, dbe);
        rollbackCmdDataModel(lastCommand);
        rollbackTransaction(lastCommand);
      }
      catch (CommandException cmdException) {
        handleCommandException(lastCommand, cmdException);
        rollbackTransaction(lastCommand);
      }
      finally {
        dataProvider.getEntityProvider().endTransaction();
      }


      if (lastCommand.getErrorCause() == ERROR_CAUSE.NONE) {
        dce.getChangedData().putAll(lastCommand.getChangedData());

      }
      else {
        // command could not been UNDOne
        // move the current command pointer forward
        this.curCmdIndex++;
      }

      if (ObjectStore.getInstance().getCqnMode() != ObjectStore.CQN_MODE_NO) {
        this.cqnNotifier.notifyUI(dce);
      }

      return lastCommand;
    }

    getLogger().debug("CommandStack : Undo command stack finished");

    // no command to UNDO
    return null;

  }

  /**
   * Execute the most recently UNDOne command again If the command could not be executed, the command stack will not be
   * modified.
   *
   * @return the command which has been REDOne returns NULL if no command to be REDOne
   */
  public final AbstractDataCommand redo() {
    getLogger().debug("CommandStack : Redo command stack invoked");


    // check, if a next command is available
    if (canRedo()) {

      final AbstractDataCommand redoCommand = this.commandList.get(this.curCmdIndex + 1);

      final AbstractDataProvider dataProvider = redoCommand.getDataProvider();

      final DisplayChangeEvent dce = this.cqnNotifier.refreshDataModel();

      // get the command to REDO
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("CommandStack : Undo command stack : " + redoCommand);
      }

      try {

        // start a transaction
        dataProvider.getEntityProvider().startTransaction();

        // execute the command
        redoCommand.redo();

        // commit the changes
        commitChanges(redoCommand);

      }
      catch (DatabaseException dbe) {
        handleConnectionClosedException(redoCommand, dbe);
        rollbackCmdDataModel(redoCommand);
        rollbackTransaction(redoCommand);
      }
      catch (CommandException cmdException) {
        handleCommandException(redoCommand, cmdException);
        rollbackTransaction(redoCommand);
      }
      finally {
        dataProvider.getEntityProvider().endTransaction();
      }


      if (redoCommand.getErrorCause() == ERROR_CAUSE.NONE) {
        dce.getChangedData().putAll(redoCommand.getChangedData());

        // move the command index if the command could be executed
        this.curCmdIndex++;
      }

      if (ObjectStore.getInstance().getCqnMode() != ObjectStore.CQN_MODE_NO) {
        this.cqnNotifier.notifyUI(dce);
      }

      return redoCommand;

    }

    getLogger().debug("CommandStack : Redo command stack finished");

    return null;
  }

  /**
   * Get the command stack as a String The most recently executed command is marked with a '*' UNDOne commands are
   * marked with a 'U'
   *
   * @return The current command stack as a list of commands
   */
  public final List<String> getStringCommandStack() {
    final List<String> strCmdStack = new ArrayList<String>();

    StringBuilder commandString;

    for (int i = 0; i < this.commandList.size(); i++) {
      final AbstractDataCommand command = this.commandList.get(i);

      commandString = new StringBuilder(); // NOPMD by bne4cob on 6/26/13 2:02 PM

      if (i < this.curCmdIndex) {
        commandString.append("   ");
      }
      else if (i == this.curCmdIndex) {
        commandString.append(" * ");
      }
      else {
        commandString.append(" U ");
      }

      commandString.append(command.getString());

      strCmdStack.add(commandString.toString());

    }

    return strCmdStack;
  }

  /**
   * Validate whether the record to change, requested by this command, is already modified by another client (received
   * through notification).
   *
   * @param event notification event
   * @param command command to execute
   * @return true, if data is not stale, else false.
   * @throws CommandException During parallel changes
   */
  private boolean validateStaleDataInNotifications(final DisplayChangeEvent event, final AbstractDataCommand command)
      throws CommandException {
    final Long primObjID = command.getPrimaryObjectID();
    if ((primObjID != null) && event.getChangedData().containsKey(primObjID)) {
      final ChangedData chData = event.getChangedData().get(primObjID);

      final CommandException exeption = new CommandException(
          "Parallel change detected in " + chData.getEntityClass().getSimpleName(), DMErrorCodes.ERR_STALE_DATA);


      throw exeption;
    }
    getLogger().debug("CommandStack : Stale data validation completed for received CQNs.");
    return true;
  }

  /**
   * Returns whether there are commands to be redone
   *
   * @return true/false
   */
  public boolean canRedo() {
    return (this.curCmdIndex > 0) && ((this.curCmdIndex + 1) < this.commandList.size());
  }

  /**
   * Returns whether there are commands to be undone
   *
   * @return true/false
   */
  public boolean canUndo() {
    return this.curCmdIndex >= 0;
  }


  /**
   * command used in the transaction rollback the transaction in case of any error.
   *
   * @param newCommand
   */
  private void rollbackTransaction(final AbstractDataCommand newCommand) {
    try {
      getLogger().debug("rolling back transaction...");
      newCommand.getDataProvider().getEntityProvider().rollBackTransaction();
      getLogger().debug("rollback complete for the transaction");
    }
    catch (Exception exp) {
      getLogger().error("Exception during rolling back transaction ", exp);
    }
  }
}
