/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * Global CQN notifier
 *
 * @author BNE4COB
 */
public class GlobalCQNNotifier {

  /**
   * Extension point name for GUI notification
   */
  private static final String GUI_NOTIF_EXT_PT = "com.bosch.caltool.dmframework.uinotification.listeners";

  /**
   * if true, notification is successful.
   */
  private boolean successful = true;

  /**
   * List of exceptions occured during the notification
   */
  private final List<Exception> exceptionList = new ArrayList<>();

  /**
   * Refresh the data cache (and UI) for the change notifications received. Exceptions need to be taken care externally
   *
   * @param refreshUI whether UI is to be refreshed
   */
  public final void refreshNotificationChanges(final boolean refreshUI) {
    final DisplayChangeEvent dce = ObjectStore.getInstance().getChangeNotificationCache().getDCE();

    doRefreshDM(dce);

    if (refreshUI) {
      doNotifyUI(dce);
    }
  }

  /**
   * Refreshes the data model. Any error occured is written to the logger
   *
   * @return the display change event used
   */
  public final DisplayChangeEvent refreshDataModel() {
    final DisplayChangeEvent dce = ObjectStore.getInstance().getChangeNotificationCache().getDCE();
    try {
      doRefreshDM(dce);
    }
    // All exceptions are caught here to prevent the notification system from going down.
    catch (Exception exeption) {
      setFailed(exeption);
      getLogger().warn("Error occured while processing change notification", exeption);
    }
    return dce;
  }

  /**
   * Refresh the data model
   *
   * @param dce the DCE to use
   */
  private void doRefreshDM(final DisplayChangeEvent dce) {

    if (!dce.isEmpty()) {
      for (ICacheRefresher cre : ObjectStore.getInstance().getCacheRefreshers()) {
        cre.refreshDataCache(dce);
      }
      // Prevents the refresh of data during next iteration of refresh.
      ObjectStore.getInstance().getChangeNotificationCache().removeUsed();
    }
  }

  /**
   * Notify the UI controls aboutn the display change event. Errors are written to logger.
   *
   * @param dce Display Change Event
   */
  public final void notifyUI(final DisplayChangeEvent dce) {
    try {
      doNotifyUI(dce);
    }
    // All exceptions are caught here to prevent the notification system from going down.
    catch (Exception exeption) {
      setFailed(exeption);
      getLogger().warn("Error occured while sending notifications to UI", exeption);
    }
  }

  /**
   * Notify the UI controls about the display change event. Uses eclipse's extension point feature
   *
   * @param dce Display Change Event
   */
  private void doNotifyUI(final DisplayChangeEvent dce) {
    if (dce.isEmpty() || CommonUtils.isStartedFromWebService()) {
      return;
    }

    final IConfigurationElement[] configs =
        Platform.getExtensionRegistry().getConfigurationElementsFor(GUI_NOTIF_EXT_PT);

    INotificationListener listener;

    for (IConfigurationElement config : configs) {
      try {
        listener = (INotificationListener) config.createExecutableExtension("class");
        listener.onDataModelChange(dce);
      }
      catch (CoreException exp) {
        setFailed(exp);
        getLogger().error("Failed to load extension point", exp);
      }

    }

  }

  /**
   * @return Logger
   */
  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * @return true, if notification is completed without errors
   */
  public boolean isSuccessful() {
    return this.successful;
  }

  /**
   * Set failure details, if any error occurs during CQN notification
   *
   * @param exeption exception occured
   */
  private void setFailed(final Exception exeption) {
    this.successful = false;
    this.exceptionList.add(exeption);
  }

  /**
   * @return the exceptions occured during the notification process
   */
  public List<Exception> getExceptionList() {
    return new ArrayList<>(this.exceptionList);
  }

}
