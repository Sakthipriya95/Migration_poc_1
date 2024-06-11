/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.process.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.eclipse.swt.widgets.Display;

import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.LinkProcessor;
import com.bosch.calcomp.externallink.utils.LinkLoggerUtil;


/**
 * Handles the details of the received TCP request and opening the respective business object.
 */
class URIActionClient extends Thread {

  /**
   * Client socket
   */
  private final Socket clientSocket;


  /**
   * Constructor
   *
   * @param socket Socket
   */
  public URIActionClient(final Socket socket) {
    super("URIActionClient");
    this.clientSocket = socket;
  }


  /**
   * Receive the link url and process it
   *
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {

    // Now client is connected to the server

    try (
        // initialize the PrintWriter to send messages to the client
        PrintWriter outWrtr = new PrintWriter(this.clientSocket.getOutputStream(), true);

        // Read the client socket's input stream
        InputStreamReader inStrmRdr = new InputStreamReader(this.clientSocket.getInputStream());
        // initialize the BufferedReader to recieve messages from the client.
        BufferedReader inBufRdr = new BufferedReader(inStrmRdr)) {


      // initiate conversation with client

      String outputLine = ListenerConstants.SEND_REQUEST;
      outWrtr.println(outputLine);

      LinkLoggerUtil.getLogger().info("Reading the external link arguments");

      String inputLine = inBufRdr.readLine();
      if (inputLine != null) {
        openLink(inputLine);
      }

    }
    catch (IOException exp) {
      LinkLoggerUtil.getLogger().error(exp.getMessage(), exp);
    }
    finally {
      try {
        this.clientSocket.close();
      }
      catch (IOException exp) {
        LinkLoggerUtil.getLogger().error(exp.getMessage(), exp);
      }
    }

  }

  /**
   * Parses input arugents into name value pairs
   *
   * @param linkText link line
   */
  private void openLink(final String linkText) {
    Display.getDefault().asyncExec(new Runnable() {

      /**
       * Opens the input entity in the appropriate editor
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // ICDM-1649
        LinkProcessor linkOpener = new LinkProcessor(linkText);

        try {
          linkOpener.openLink();
        }
        catch (ExternalLinkException exp) {
          LinkLoggerUtil.showErrorDialog(exp.getMessage(), exp);
        }
      }
    });
  }

}
