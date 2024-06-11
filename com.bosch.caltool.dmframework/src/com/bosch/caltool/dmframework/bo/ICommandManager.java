/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;


/**
 * Interface for command manager, that executes commands. The implmentation can internally use an instance of
 * <code>CommandStack</code> for this purpose.
 *
 * @author bne4cob
 */
@Deprecated
public interface ICommandManager {

  /**
   * Adds a command to the command stack and executes it.
   *
   * @param command command to add
   * @param sourceID the ID of the source(e.g. plug-in) from which the method is invoked. Used for logging.
   */
  void addCommand(AbstractDataCommand command, String sourceID);

  /**
   * Undo action.
   *
   * @param sourceID the ID of the source(e.g. plug-in) from which the method is invoked. Used for logging.
   */
  void undo(String sourceID);

  /**
   * Redo action.
   *
   * @param sourceID the ID of the source(e.g. plug-in) from which the method is invoked. Used for logging.
   */
  void redo(String sourceID);

}