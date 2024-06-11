/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l.compli;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.CompliParamOutput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.compli.CompliParamServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author EMS4KOR
 */
public class CompliParamServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link CompliParamServiceClient#getCompliParams()}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetCompliParamServiceClient() throws ApicWebServiceException {
    CompliParamServiceClient servClient = new CompliParamServiceClient();
    CompliParamOutput ret = servClient.getCompliParams();
    assertFalse("Response should not be null or empty", (ret == null) || ret.getCompliParamMap().isEmpty());
    LOG.info("Compliance parameters count : {}", ret.getCompliParamMap().size());

    checkParam(ret.getCompliParamMap(), "ACCI_nEngMax_C", "VALUE");
    checkParam(ret.getCompliParamMap(), "AFS_tiLlvChpHtg_CA", "VAL_BLK");
    checkParam(ret.getCompliParamMap(), "ASMod_ratHumEnv_CUR", "CURVE");
    checkParam(ret.getCompliParamMap(), "AirCtl_facAirWrmUpCorrn_MAP", "MAP");
  }

  /**
   * @param compliParamMap
   * @param param
   * @param type
   */
  private void checkParam(final Map<String, String> compliParamMap, final String param, final String type) {
    String actualType = compliParamMap.get(param);

    LOG.info("  \t{} = {}", param, actualType);
    assertEquals("Check parameter " + param + "=" + type, type, actualType);
  }
}
