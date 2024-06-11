/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.util.Properties;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author bru2cob AbstractPerspectiveIntroAction class is used to open the corresponding perspective from the home
 *         page.
 */
public abstract class AbstractPerspectiveIntroAction implements IIntroAction {


  @Override
  public void run(final IIntroSite arg0, final Properties arg1) {
    // Get active workbench window
    final IWorkbenchWindow workbenchWindow = getActiveWorkBenchWindow();
    // Get the active perspective from the active workbench
    final IPerspectiveDescriptor activePerspective = getActivePerspective(getActiveWorkBenchWindow());

    if ((activePerspective == null) || !activePerspective.getId().equals(getPerspectiveId())) {
      showPerspective(workbenchWindow);
    }
    // Get intro page
    final IIntroPart introPart = getIntroPage();
    // close intro page
    closeIntroPage(introPart);

  }


  /**
   * @param workbenchWindow
   */
  private void showPerspective(final IWorkbenchWindow workbenchWindow) {
    Display.getCurrent().asyncExec(new Runnable() {

      @Override
      public void run() {
        // switch perspective
        try {
          workbenchWindow.getWorkbench().showPerspective(getPerspectiveId(), workbenchWindow);
        }
        catch (final WorkbenchException e) {
          CDMLogger.getInstance().error(e.getMessage(), e);
        }
      }
    });
  }

  /**
   * @param introPart
   */
  private void closeIntroPage(final IIntroPart introPart) {
    PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
  }


  /**
   * @return
   */
  private IIntroPart getIntroPage() {
    return PlatformUI.getWorkbench().getIntroManager().getIntro();
  }


  /**
   * @param workbenchWindow
   * @return
   */
  private IPerspectiveDescriptor getActivePerspective(final IWorkbenchWindow workbenchWindow) {
    return workbenchWindow.getActivePage().getPerspective();
  }


  /**
   * @return
   */
  private IWorkbenchWindow getActiveWorkBenchWindow() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
  }


  /**
   * @return perspectiveID
   */
  // Get the current perspective Id
  public abstract String getPerspectiveId();
}
