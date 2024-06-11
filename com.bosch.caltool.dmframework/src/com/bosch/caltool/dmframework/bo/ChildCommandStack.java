/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;


import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractDataCommand.EXECUTION_MODE;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;

/**
 * Command stack to be used to store and execute child commands.
 *
 * @author bne4cob
 */
@Deprecated
public class ChildCommandStack {

  /**
   * the parent command
   */
  private final AbstractDataCommand parentCommand;
  /**
   * the command list
   */
  private final List<AbstractDataCommand> commandList;

  /**
   * the index of the command executed recently
   */
  private int curCmdIndex;


  /**
   * Create a new Command Stack
   *
   * @param parentCommand AbstractDataCommand
   */
  public ChildCommandStack(final AbstractDataCommand parentCommand) {

    this.parentCommand = parentCommand;

    // create an empty command list
    this.commandList = new ArrayList<AbstractDataCommand>();

    // initialize the most recently executed command
    this.curCmdIndex = -1;
  }

  /**
   * Add a new command to the command stack A new command will be executed immediately. The new command will be added to
   * the command stack only if if could be executed without errors.
   *
   * @param newCommand the command to be added
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  public final void addCommand(final AbstractDataCommand newCommand) throws CommandException {
    newCommand.setDce(this.parentCommand.getDce());
    // execute the command
    newCommand.execute();
    // Copy the changed data from the child command to the parent command
    this.parentCommand.getChangedData().putAll(newCommand.getChangedData());
    // ICDM 484
    // Add the db transactions of the child command to the parent command
    TransactionSummary parentSummary = this.parentCommand.getTransactionSummary();
    if (null != parentSummary) {
      TransactionSummary childSummary = newCommand.getTransactionSummary();
      if (null != childSummary) {
        // add child to parent
        parentSummary.getSummaryList().add(childSummary);
      }
    }

    // insert the new command after the current command
    this.commandList.add(++this.curCmdIndex, newCommand);

  }


  /**
   * Undo all the commands in the stack
   *
   * @throws CommandException when command level business validation fails
   */
  public final void undoAll() throws CommandException {

    // check if there are commands to UNDO
    while (canUndo()) {
      final AbstractDataCommand lastCommand = this.commandList.get(this.curCmdIndex);

      // undo the current command, decrement the current command index
      lastCommand.undo();

      this.curCmdIndex--;
    }
  }

  /**
   * Rollback all the commands execute. Depending on the mode of execution provided, the rollback operation will be done
   * on executed/undone/redone commands
   *
   * @param executionMode mode of execution
   */
  public void rollbackAll(final AbstractDataCommand.EXECUTION_MODE executionMode) {
    AbstractDataCommand command;
    if ((executionMode == AbstractDataCommand.EXECUTION_MODE.EXECUTION) ||
        (executionMode == AbstractDataCommand.EXECUTION_MODE.REDO)) {
      for (int index = this.curCmdIndex; index >= 0; index--) {
        command = this.commandList.get(index);
        command.rollBackDataModel();
      }
    }
    else if (executionMode == AbstractDataCommand.EXECUTION_MODE.UNDO) {
      for (int index = this.curCmdIndex; index < this.commandList.size(); index++) {
        command = this.commandList.get(index);
        command.rollBackDataModel();
      }
    }

  }

  /**
   * doPostCommit() Of all the child commands are called via this method
   */
  public void doPostCommit() {
    int index = 0;
    int sizeOfCommandList = this.commandList.size();
    if (this.parentCommand.getExecutionMode() == EXECUTION_MODE.UNDO) {
      // during UNDO
      while (sizeOfCommandList >= 0) {
        final AbstractDataCommand lastCommand = this.commandList.get(sizeOfCommandList);

        // call the doPostCommit of the child commands
        lastCommand.doPostCommit();

        sizeOfCommandList--;
      }
    }
    else {
      // during EXECUTE and REDO
      while (index < sizeOfCommandList) {
        final AbstractDataCommand lastCommand = this.commandList.get(index);

        // call the doPostCommit of the child commands
        lastCommand.doPostCommit();

        index++;
      }
    }
  }

  /**
   * Returns whether there are commands to be undone
   *
   * @return true/false
   */
  private boolean canUndo() {
    return this.curCmdIndex >= 0;
  }

  /**
   * Reset this stack. This will clear all the commands in this stack and set the pointer to the beginning.
   */
  public void clear() {
    this.curCmdIndex = -1;
    this.commandList.clear();
  }
}
