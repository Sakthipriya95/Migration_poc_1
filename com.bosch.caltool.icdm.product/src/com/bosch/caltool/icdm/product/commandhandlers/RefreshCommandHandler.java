/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


/**
 * Command handler to refresh the UI for the CQN changes.
 *
 * @author bne4cob
 */
public class RefreshCommandHandler extends AbstractHandler {

  //
  // Command definition in plugin.xml
  //
  // <command
  // commandId="com.bosch.caltool.icdm.product.commandhandlers.RefreshCommandHandler"
  // icon="platform:/plugin/com.bosch.caltool.icdm.common.ui/icons/refresh.gif"
  // label="Refresh"
  // style="push"
  // tooltip="Refresh the UI">
  // </command>


  /**
   * Refresh the data model and the UI. Failure in refresh is logged to the Error Log view of the application
   *
   * @param event ExecutionEvent
   */
  @Override
  public final Object execute(final ExecutionEvent event) throws ExecutionException {
    return null;
  }

}
