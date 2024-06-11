package com.bosch.rcputils.wbutils;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Utility methods for related to workbench. The methods should be accessed with the static reference.
 * 
 * @author bne4cob
 */
public final class WorkbenchUtils {

  /**
   * Prevents the instantiation of this class. The methods should be accessed only in a static way.
   */
  private WorkbenchUtils() {
    // No code here
  }

  /**
   * Retrieves the instance of the View for the given View ID.
   * 
   * @param viewID the unique ID of the view to be searched.
   * @param searchAll whether inactive workbenches are to be searched or not
   * @return the instance of the view, or <code>null</code> if the part was not instantiated.
   */
  public static IWorkbenchPart getView(final String viewID, final boolean searchAll) {
    IWorkbenchPart viewPart = null;
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
      level1: for (IWorkbenchWindow wbWindow : PlatformUI.getWorkbench().getWorkbenchWindows()) {
        for (IWorkbenchPage page : wbWindow.getPages()) {
          final IViewReference[] viewRefArray = page.getViewReferences();
          for (IViewReference viewReference : viewRefArray) {
            if (viewID.equals(viewReference.getId())) {
              viewPart = viewReference.getPart(false);
              break level1;
            }
          }
        }
      }
    }
    else {
      viewPart = getView(viewID);
    }
    return viewPart;
  }


  /**
   * Retrieves the instance of the View for the given View ID.
   * 
   * @param viewID the unique ID of the view to be searched.
   * @return the instance of the view, or <code>null</code> if the part was not instantiated.
   */
  public static IWorkbenchPart getView(final String viewID) {
    IWorkbenchPart viewPart = null;

    final IViewReference[] viewRefArray =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
    for (IViewReference viewReference : viewRefArray) {
      if (viewID.equals(viewReference.getId())) {
        viewPart = viewReference.getPart(false);
        break;
      }
    }

    return viewPart;
  }
}
