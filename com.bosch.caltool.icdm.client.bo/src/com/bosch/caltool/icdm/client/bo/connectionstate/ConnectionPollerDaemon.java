/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.connectionstate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.exception.GenericException;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;


/**
 * Checks the server connection in intervals. Invokes the extension point definitions on connection state changes
 * <p>
 * To set the polling interval, set the constant <code>POLL_INTERVAL</code>
 *
 * @author bne4cob
 */
enum ConnectionPollerDaemon implements Runnable {
                                                 /**
                                                  * Singleton instance
                                                  */
                                                 INSTANCE;


  /**
   * HTTP method to be used to check server connection
   */
  private static final String HTTP_CON_METHOD = "HEAD";

  /**
   * Connection timeout (in seconds)
   */
  // 3 seconds
  private static final int HTTP_CON_TIMEOUT = 3 * 1000;

  /**
   * Delay to start server connection polling, once the thread is started
   */
  // 10 seconds
  private static final long POLL_START_DELAY = 10 * 1000L;

  /**
   * Network port polling interval
   */
  // 10 seconds
  private static final long POLL_INTERVAL = 10 * 1000L;

  /**
   * Poller thread name
   */
  public static final String POLLER_THREAD_NAME = "Connection Poller Daemon";

  /**
   * Extension point name
   */
  private static final String CON_STATE_LSTNR_EXT_PT = "com.bosch.caltool.icdm.client.bo.connectionstate.listener";

  /**
   * If true, CNS session is ok
   */
  private boolean cnsServerConnected = true;

  /**
   * To enable going to disconnected mode when CNS server connection goes off
   */
  private boolean cnsServerDisconnectionChecked = true;

  /**
   * variable for Connection status
   */
  // Icdm-983
  private ConnectionState connectionState = ConnectionState.CONNECTED;

  /**
   * Poller runs if enabled is true
   */
  private boolean enabled = true;

  /**
   * @return the connectionStat connection status Icdm-983
   */
  public static ConnectionState getCurrentState() {
    return ConnectionPollerDaemon.INSTANCE.connectionState;
  }

  /**
   * Runs and infinite loop, that first waits until server connection goes off and then waits untils it goes back to
   * normal. On connection state changes, it triggers the extension point method. The execution can be stopped by
   * calling the <code>stop()</code> method
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void run() {
    getLogger().info("Connection polling daemon started");

    sleep(POLL_START_DELAY);

    getLogger().info("Connection polling started");

    while (isEnabled()) {

      // Wait till disconnection occurs
      pollForDisconnection();
      onConnectionStateChange(ConnectionState.DISCONNECTED);
      getLogger().warn("Server connection lost. Waiting until connection is restored...");

      // Once disconnected, wait till reconnects
      pollForConnection();
      onConnectionStateChange(ConnectionState.RECONNECTING);
      getLogger().info("Server reconnected. Performing post reconnection activities...");

      if (this.cnsServerConnected) {
        // ICDM-2565
        onConnectionStateChange(ConnectionState.RECONNECTED);
        ConnectionState.setReconnectedFlag(true);
      }
      else {
        onConnectionStateChange(ConnectionState.RECONN_INCOMPLETE);
      }
    }

    getLogger().info("Connection polling daemon stopped");

  }


  /**
   * Waits until the server connection is lost
   *
   * @return true, when network connection is not available
   */
  private boolean pollForDisconnection() {

    boolean nwDisconnected = false;

    while (isEnabled()) {
      sleep(POLL_INTERVAL);

      nwDisconnected = !pollServer(true);

      if (!this.cnsServerDisconnectionChecked) {
        this.cnsServerDisconnectionChecked = true;
        getLogger().error("CNS Server connection is invalid");
        nwDisconnected = true;
      }

      if (nwDisconnected) {
        getLogger().info("Network connection to iCDM server NOT available");
        break;
      }

    }

    return nwDisconnected;
  }

  /**
   * Waits until the database is connected. Checks for network connection and database connection
   *
   * @return true if database is connected
   */
  private boolean pollForConnection() {
    boolean nwConnected = false;

    while (!nwConnected) {
      sleep(POLL_INTERVAL);
      nwConnected = pollServer(false);
    }

    return true;
  }


  /**
   * Polls the network. Checks for network connection to iCDM server
   *
   * @param logError whether error is to be logged or not
   * @return true when connection is available
   */
  private boolean pollServer(final boolean logError) {
    boolean reachable = false;
    try {
      URL serverUrl = new URL(ClientConfiguration.getDefault().getBaseUri());
      HttpURLConnection httpCon = (HttpURLConnection) serverUrl.openConnection();
      httpCon.setRequestMethod(HTTP_CON_METHOD);
      httpCon.setConnectTimeout(HTTP_CON_TIMEOUT);

      httpCon.connect();
      if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
        reachable = true;
      }
    }
    catch (IOException ex) {
      getLogger().debug("Connection error : {}", ex.getMessage());
      if (logError) {
        getLogger().warn(ex.getMessage(), ex);
      }
    }

    return reachable;
  }

  /**
   * Sleeps the current thread for the given time
   *
   * @param milliSeconds time to sleep
   */
  private void sleep(final long milliSeconds) {
    try {
      Thread.sleep(milliSeconds);
    }
    catch (InterruptedException exp) {
      getLogger().warn(exp.getMessage(), exp);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Trigger the listeners about the connection change and also set the current Conection status
   *
   * @param state ConnectionState
   */
  private void onConnectionStateChange(final ConnectionState state) {
    this.connectionState = state;

    getLogger().debug("Connection state changed to {}", state);

    final IConfigurationElement[] configs =
        Platform.getExtensionRegistry().getConfigurationElementsFor(CON_STATE_LSTNR_EXT_PT);

    IConnectionStatusListener listener;

    for (IConfigurationElement config : configs) {
      try {
        listener = (IConnectionStatusListener) config.createExecutableExtension("class");
        listener.onConnectionChange(state);
      }
      catch (CoreException exp) {
        getLogger().error("Failed to load extension point", exp);
      }
      catch (GenericException exp) {
        getLogger().warn(exp.getMessage(), exp);
      }

    }


  }

  /**
   * Wrapper for ObjectStore.getInstance().getLogger()
   *
   * @return ObjectStore.getInstance().getLogger()
   */
  private static ILoggerAdapter getLogger() {
    return CDMLogger.getInstance();
  }

  /**
   * @return the enabled
   */
  public boolean isEnabled() {
    return this.enabled;
  }

  /**
   * @return the cnsServerConnected
   */
  public boolean isCnsServerConnected() {
    return this.cnsServerConnected;
  }

  /**
   * @param cnsServerConnected the cnsServerConnected to set
   */
  void setCnsServerConnected(final boolean cnsServerConnected) {
    this.cnsServerConnected = cnsServerConnected;
    this.cnsServerDisconnectionChecked = false;
  }


  /**
   * Stop the Poller daemon
   */
  public void stop() {
    this.enabled = false;

  }
}
