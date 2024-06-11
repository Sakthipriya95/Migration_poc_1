/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVcdmTransferServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceRuntimeException;

/**
 * @author sng9kor
 */
public class AbstarctRestServiceClientTest extends AbstractRestClientTest {

  private static final String PIDC_VCDM_TFR_TEST_USER = "HFZ2SI";

  /**
   * Test method for {@link PidcVcdmTransferServiceClient#setClientConfiguration(ClientConfiguration)) }
   */
  @Test
  public void testsetClientConfiguration() {

    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    this.thrown.expect(ApicWebServiceRuntimeException.class);
    this.thrown.expectMessage("Client configuration cannot be null");
    client.setClientConfiguration(null);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link ClientConfiguration#setLanguage(String) }
   */
  @Test
  public void testsetLanguage() {
    ClientConfiguration.getDefault().setLanguage("English");
    assertEquals("English", ClientConfiguration.getDefault().getLanguage());
  }


  /**
   * Test method for {@link InitializationProperties#setIcdmTempDirectory(String)}
   */
  @Test
  public void testsetIcdmtempDirectory() {
    InitializationProperties initialProps = new InitializationProperties();
    String path = CommonUtils.getICDMTmpFileDirectoryPath();
    initialProps.setIcdmTempDirectory(path);
    assertEquals(path, initialProps.getIcdmTempDirectory());
  }

  /**
   * Test method for {@link InitializationProperties#setCnsEnabled(boolean) }
   */
  @Test
  public void testsetCnsEnabled() {
    InitializationProperties initialProps = new InitializationProperties();
    initialProps.setCnsEnabled(false);
    assertEquals(false, initialProps.isCnsEnabled());
  }

  /**
   * Test method for {@link InitializationProperties#setLanguage(String)}
   */
  @Test
  public void testsetLanguage1() {
    InitializationProperties initialProps = new InitializationProperties();
    String language = "German";
    initialProps.setLanguage(language);
    assertEquals(language, initialProps.getLanguage());
  }

  /**
   * Test method for {@link InitializationProperties#setUserTempDirectory(String) }
   */
  @Test
  public void testsetUserTempDirectory() {
    InitializationProperties initialProps = new InitializationProperties();
    String path = "C:\\User";
    initialProps.setUserTempDirectory(path);
    assertEquals(path, initialProps.getUserTempDirectory());
  }
}
