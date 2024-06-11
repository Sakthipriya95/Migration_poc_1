/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import java.io.File;

import org.junit.Test;

/**
 * @author rgo7cob
 */
public class TestVersionStat extends AbstractSoapClientTest {

  private static final String REPORT_DEST_PATH =
      System.getProperty("java.io.tmpdir") + File.separator + "PIDC_Statistics_Report_" + getRunId() + ".xlsx";

  private final APICWebServiceClient stub = new APICWebServiceClient();

  @Test
  public void verstat() throws Exception {
    // Commented for testing
    // this.stub.getPidcStatisticResult(REPORT_DEST_PATH, this.stub.login());
    // assertTrue("Report created", new File(REPORT_DEST_PATH).exists());
  }

}
