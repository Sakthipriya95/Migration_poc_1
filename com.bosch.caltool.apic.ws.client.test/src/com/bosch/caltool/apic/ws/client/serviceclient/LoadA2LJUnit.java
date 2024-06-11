/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.bosch.caltool.apic.ws.client.A2LCallbackHandler;


/**
 * @author imi2si
 */
@RunWith(Parameterized.class)
public class LoadA2LJUnit extends AbstractSoapClientTest {

  private final Integer id;

  /**
   * Constructor that sets the file for the test cases
   *
   * @param vcdmID vcdm ID
   */
  public LoadA2LJUnit(final Integer vcdmID) {
    // Set the file(s) the test should be performed for
    super();
    this.id = vcdmID;
    LOG.info("vCDM ID = {}", vcdmID);
  }

  // Set the parameters for the file name. The parameters are passed to the constructor.
  // The first dimension of the array is the testcase, the second the parameters passed to the constructor
  // In this case two testcases with three parameters each
  @Parameters
  public static Collection values() {
    Integer args[][] = new Integer[][] { { 18716923 }, { 18671994 } };
    return Arrays.asList(args);
  }


  /**
   * method for getting A2L info
   *
   * @throws Exception error in service
   */
  @Test
  public void loadA2LFile() throws Exception {

    // Example for a synchronus call. Create an instance of APICWebServiceClient
    APICWebServiceClient apicWsClient = new APICWebServiceClient();

    // Example for a synchronus call. Call the method loadA2LFileData to start the sync call of the webservice
    LOG.info("Sync Output: " + apicWsClient.loadA2LFileData(this.id));

    // Example for an asynchronus Call. Call the method loadA2LFileDataAsync to start the sync call of the webservice
    A2LCallbackHandler a2lCallbackHandler = (A2LCallbackHandler) apicWsClient.loadA2LFileDataAsync(this.id);

    /*
     * Wait until the async call is finished. It is either finished when the webservice call is broken (an error
     * appeared) or the file info is available
     */
    while (!a2lCallbackHandler.isBroken() && !a2lCallbackHandler.isA2LFileInfoAvailable()) {
      Thread.sleep(1000);
    }

    // Output of the result or the exception
    if (a2lCallbackHandler.isA2LFileInfoAvailable()) {
      LOG.info("Async Output: " + a2lCallbackHandler.getA2LFileID());
    }
    else if (a2lCallbackHandler.isBroken()) {
      LOG.info("Async Exception: " + a2lCallbackHandler.getA2LException().getLocalizedMessage());
    }

    assertFalse(a2lCallbackHandler.isBroken());
    assertTrue(a2lCallbackHandler.isA2LFileInfoAvailable());

  }

}
