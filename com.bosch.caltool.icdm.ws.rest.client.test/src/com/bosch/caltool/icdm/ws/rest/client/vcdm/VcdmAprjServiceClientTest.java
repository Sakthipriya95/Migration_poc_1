/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.vcdm;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class VcdmAprjServiceClientTest extends AbstractRestClientTest {

  private static final String VCDM_APRJ_ID = "22069678";

  /**
   * vCDM APRJ name
   */
  private static final String VCDM_APRJ_NAME = "X_Test_iCDM_IntegrationTest";

  /**
   * Invalid vCDM APRJ name
   */
  private static final String INVALID_VCDM_APRJ_NAME = "Invalid_vCDM_APRJ_name";

  /**
   * Test method for {@link VcdmAprjServiceClient#getAprjId(String)}
   *
   * @throws ApicWebServiceException Exception in vcdm login
   */
  @Test
  public void testVcdmAprjIdByName() throws ApicWebServiceException {
    String aprjId = new VcdmAprjServiceClient().getAprjId(VCDM_APRJ_NAME);
    assertTrue("Check APRJ ID returned", CommonUtils.isEqual(VCDM_APRJ_ID, aprjId));
  }

  /**
   * Negative test<br>
   * Test method for {@link VcdmAprjServiceClient#getAprjId(String)}
   *
   * @throws ApicWebServiceException Exception in vcdm login
   */
  @Test
  public void testVcdmAprjIdByNameInvalid() throws ApicWebServiceException {
    String aprjId = new VcdmAprjServiceClient().getAprjId(INVALID_VCDM_APRJ_NAME);
    assertTrue("Check APRJ ID returned for invalid name", CommonUtils.isEmptyString(aprjId));
  }
}
