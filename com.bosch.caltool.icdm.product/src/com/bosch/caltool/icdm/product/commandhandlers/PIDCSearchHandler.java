/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.PIDCSearchEditor;
import com.bosch.caltool.apic.ui.editors.PIDCSearchEditorInput;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * Handler to open the search editor
 * 
 * @author bru2cob
 */
public class PIDCSearchHandler extends AbstractHandler {

  /**
   * Opens PIDC search editor {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent arg0) throws ExecutionException {
    // get editor input
    PIDCSearchEditorInput input = new PIDCSearchEditorInput();
    try {
      // open pidc search editor
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .openEditor(input, PIDCSearchEditor.SEARCH_EDITOR_ID);
    }
    catch (PartInitException exception) {
      CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception);
    }
    // return null if editor is not found
    return null;
  }

}
