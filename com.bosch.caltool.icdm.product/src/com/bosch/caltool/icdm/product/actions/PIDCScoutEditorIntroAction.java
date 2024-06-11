/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.util.Properties;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;

import com.bosch.caltool.apic.ui.editors.PIDCSearchEditor;
import com.bosch.caltool.apic.ui.editors.PIDCSearchEditorInput;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.product.util.ICDMConstants;


/**
 * This class activated after clicking in the PIDC Scout icon in the welcome page screen
 * 
 * @author svj7cob
 */
// ICDM-1859
public class PIDCScoutEditorIntroAction extends AbstractPerspectiveIntroAction {

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final IIntroSite site, final Properties params) {
    // get editor input
    PIDCSearchEditorInput input = new PIDCSearchEditorInput();
    try {
      // open pidc search editor
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .openEditor(input, PIDCSearchEditor.SEARCH_EDITOR_ID);
      final IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
      PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
    }
    catch (PartInitException exception) {
      // log the exception if it throws partinit exception
      CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPerspectiveId() {
    // pidc perspective
    return ICDMConstants.ID_PERSP_PIDC;
  }

}
