package com.bosch.caltool.icdm.product;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.exception.IcdmRuntimeException;

/**
 * @author rgo7cob Application Workbench Advisor for the Product
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

  /**
   * Method to override the default behaviour of the event loop Exception.
   */
  @Override
  public void eventLoopException(final Throwable exception) {
    // Icdm-983 Check if Exception instance of ICDM run time Exception.
    // If the Exception is not part of ICDM run time exception then call the defualt method.
    // If exception is instance of ICDM run time Exception then leave it.
    if (!(exception instanceof IcdmRuntimeException)) {
      super.eventLoopException(exception);
    }
  }

  /**
   * Reference to the Main Perspective ID
   */
  private static final String PERSPECTIVE_ID = "com.bosch.caltool.icdm.product.perspectives.CDMPerspective"; //$NON-NLS-1$

  /**
   * {@inheritDoc} creation of the WorkBenchAdvisor
   */
  @Override
  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
    return new ApplicationWorkbenchWindowAdvisor(configurer);
  }

  /**
   * {@inheritDoc} return the prespective ID
   */
  @Override
  public String getInitialWindowPerspectiveId() {
    return PERSPECTIVE_ID;
  }

  @Override
  public void initialize(final IWorkbenchConfigurer configurer) {
    super.initialize(configurer);
    configurer.setSaveAndRestore(true);
    closeEditors();

  }


  /**
   * Method to close editors
   */
  private void closeEditors() {
    // Listener to check if workbench window has opened, if there are open editors close them

    PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {

      @Override
      public void windowOpened(final IWorkbenchWindow window) {

        if (window.getActivePage() != null) {
          window.getActivePage().closeAllEditors(true);
        }

      }

      @Override
      public void windowDeactivated(final IWorkbenchWindow window) {
        // No action
      }

      @Override
      public void windowClosed(final IWorkbenchWindow window) {
        // No action
      }

      @Override
      public void windowActivated(final IWorkbenchWindow window) {
        // No action
      }
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean preShutdown() {
    // This will close the pidc change history view part on close of application
    if (null != PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()) {
      // Get all the views
      IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
      for (IViewReference view : views) {
        // If history view
        if (view.getId().equals(ApicUiConstants.PIDC_HISTORY_VIEW_ID)) {
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);
        }
      }
    }
    return super.preShutdown();
  }
}
