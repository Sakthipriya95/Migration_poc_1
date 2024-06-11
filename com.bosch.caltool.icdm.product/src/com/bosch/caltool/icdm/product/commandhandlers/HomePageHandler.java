/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author bru2cob
 */
// Handler for homepage
public class HomePageHandler extends AbstractHandler {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    try {
      // Get the intro manager
      // Show the intro page
      PlatformUI.getWorkbench().getIntroManager().showIntro(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
          false);
    }
    catch (Exception e) {
      CDMLogger.getInstance().error(e.getMessage(), e);
    }
    return null;
  }
}
