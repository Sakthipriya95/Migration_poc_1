/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.process.listener;

import java.io.IOException;
import java.net.ServerSocket;

import com.bosch.calcomp.externallink.utils.LinkLoggerUtil;

/**
 * Listens for TCP requests from ICDMURIClient and delegates action to the URIServerThread.
 */
public class URIListeningServer extends Thread {

  /**
   * Constructor
   */
  // ICDM Uri Server
  public URIListeningServer() {
    super("URIListeningServer");
  }

  /**
   * Start the URI Server, listen to the port number set in <code>ICDMURIConstants.PORT_NUMBER</code>. When a link is
   * received in the listening port, it is sent to uri server thread for processing
   */
  @Override
  public void run() {

    LinkLoggerUtil.getLogger().info("External URI Listener started");
    // Server socket for Icdm Uri
    // Tested with Java 7 or more
    try (ServerSocket serverSocket = new ServerSocket(ListenerConstants.PORT_NUMBER)) {
      // Create new server socket for Icdm Uri
      while (true) {
        // Start the server socket
        new URIActionClient(serverSocket.accept()).start();
      }

    }
    catch (IOException exp) {
      LinkLoggerUtil.getLogger().error(exp.getMessage(), exp);
    }

  }


}
