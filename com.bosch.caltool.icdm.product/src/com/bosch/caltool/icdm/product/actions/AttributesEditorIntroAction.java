/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.util.Properties;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import com.bosch.caltool.apic.ui.editors.AttributesEditor;
import com.bosch.caltool.apic.ui.editors.AttributesEditorInput;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author bru2cob
 */
public class AttributesEditorIntroAction implements IIntroAction {

  /**
   * opening attrbutes editor
   */
  @Override
  public void run(final IIntroSite site, final Properties params) {
    // Attributes Editor input
    final AttributesEditorInput input = new AttributesEditorInput();
    try {
      // Open the Attributes editor
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
          AttributesEditor.EDITOR_ID);
    }
    catch (PartInitException e) {
      // Logger
      CDMLogger.getInstance().error(e.getMessage(), e);
    }

    // Get intro/welcome page
    final IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
    // close intro/welcome page
    PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);

  }

}
