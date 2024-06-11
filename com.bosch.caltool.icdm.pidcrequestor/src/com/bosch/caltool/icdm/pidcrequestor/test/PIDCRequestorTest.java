/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.pidcrequestor.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;
import com.bosch.caltool.icdm.pidcrequestor.PIDCRequestor;


/**
 * Test class for the PIDC Requestor
 */
public class PIDCRequestorTest {

  /**
   * Directory were the PIDC Requester Template is stored
   */
  private final static String TEMPLATE_FILE_NAME = "C:\\Archiv\\PIDC-Requester_V_4.0_Template.xlsm";

  /**
   * Main method. Can be modified to explorer functionalities.
   *
   * @param args Default parameter for main method
   */
  public static void main(final String[] args) {

    try (FileInputStream stream = new FileInputStream(new File(PIDCRequestorTest.TEMPLATE_FILE_NAME));) {
      PIDCRequestor pidc = new PIDCRequestor(PIDCRequestorLogger.INSTANCE.getLogger(), new APICWebServiceClient());
      pidc.createExcelFile(stream, PIDCRequestor.NEW_ATTRIBUTE);
      pidc.writeExcelFile(pidc.getExcelFilename("C:/Archiv/"));
    }
    catch (IOException e) {
      // In GUI show an error message
      PIDCRequestorLogger.INSTANCE.getLogger().error("Error reading the InputStream", e);
    }
    catch (InvalidFormatException e) {
      // In GUI show an error message
      PIDCRequestorLogger.INSTANCE.getLogger().error("Wrong FileFormat", e);
    }
  }
}
