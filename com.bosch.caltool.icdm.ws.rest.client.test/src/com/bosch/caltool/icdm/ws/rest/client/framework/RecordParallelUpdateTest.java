/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class RecordParallelUpdateTest extends AbstractRestClientTest {


  // X_Test_002_P866_EA288 : 2747L

  // PIDC Version: X_Testcustomer->Diesel Engine->PC - Passenger Car->BCU Gen 1->X_Test_ICDM_RBEI_01 (Version 1)
  // URL:icdm:pidvid,773515265

  private static final Long PIDC_ID = 760420017L;

  /**
   * Parallel upate test
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void test01() throws ApicWebServiceException {

    PidcServiceClient servClient = new PidcServiceClient();

    Pidc pidc = servClient.getById(PIDC_ID);

    assertNotNull("Response should not be null or empty", pidc);

    Pidc pidcBkp = new Pidc();
    CommonUtils.shallowCopy(pidcBkp, pidc);

    pidc.setAprjId(100L);
    pidc.setVcdmTransferDate(DateUtil.getCurrentUtcTime().toString());
    pidc.setVcdmTransferUser("RGO7COB");

    Pidc updatedPidc = servClient.update(pidc);

    assertNotNull("Response should not be null or empty", updatedPidc);
    assertEquals("update vcdm tfr user", "RGO7COB", updatedPidc.getVcdmTransferUser());

    boolean testResult = true;
    try {
      // Try to update with an older version of the data
      servClient.update(pidcBkp);
      testResult = false;
    }
    catch (ApicWebServiceException ex) {
      LOG.warn(ex.getMessage(), ex);
      testResult = CommonUtils.isEqual(
          "Project ID Card with name 'X_Test_ICDM_RBEI_01' already updated by some other process. Please refresh and try again.",
          ex.getMessage());
    }

    // Revert the change for future tests
    updatedPidc.setAprjId(pidcBkp.getAprjId());
    updatedPidc.setVcdmTransferDate(pidcBkp.getVcdmTransferDate());
    updatedPidc.setVcdmTransferUser(pidcBkp.getVcdmTransferUser());

    Pidc reverted = servClient.update(updatedPidc);
    assertNotNull("Response should not be null or empty", reverted);
    assertEquals("update vcdm tfr user revert", pidcBkp.getVcdmTransferUser(), updatedPidc.getVcdmTransferUser());

    assertTrue("Test result parallel update", testResult);
  }


}
