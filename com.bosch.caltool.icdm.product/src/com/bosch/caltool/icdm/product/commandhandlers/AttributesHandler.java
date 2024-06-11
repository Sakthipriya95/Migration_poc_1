/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.editors.AttributesEditor;
import com.bosch.caltool.apic.ui.editors.AttributesEditorInput;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * Command handler for Attribute button in application toolbar
 * 
 * @author adn1cob
 */
public class AttributesHandler extends AbstractHandler {


  /**
   * Open Attribute Editor on clicking the toolbar button
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    AttributesEditorInput input = new AttributesEditorInput();

    try {
      // Open the Attribute editor
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, AttributesEditor.EDITOR_ID);
    }
    catch (PartInitException exeption) {
      CDMLogger.getInstance().warn("Error occured while sending notifications to UI", exeption, Activator.PLUGIN_ID);
    }

    // Nothing to return
    return null;
  }

}
