/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class A2lParameterPropsServiceTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String SSD_CLASS_CUSTSPEC = "CUSTSPEC";

  /**
   * A2L file ID
   */
  // A2L : HON1793A1_V75S41_internal_withGroups_V01.a2l
  private static final Long A2L_FILE_ID = 911435001L;

  /**
   * Test A2L param props fetch
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testGetA2LParamProps() throws ApicWebServiceException {
    Map<String, ParamProperties> propMap = new A2LParamPropsServiceClient().getA2LParamProps(A2L_FILE_ID);

    LOG.info("Parameters = {}", propMap.size());

    assertEquals("Verify params count in A2L", 20801, propMap.size());

    // Screw
    assertParamProps("TDMILORALT", propMap, 379476265L, false, "S", SSD_CLASS_CUSTSPEC, false, false);
    // Compliance
    assertParamProps("TANSMNKH", propMap, 432845665L, false, "N", "COMPLIANCE", false, false);
    // Blacklist label
    assertParamProps("KFMRESKH", propMap, 388272065L, false, null, SSD_CLASS_CUSTSPEC, true, false);
    // qssd LABEL
    assertParamProps("OFMSNDKMN", propMap, 434832215L, false, null, SSD_CLASS_CUSTSPEC, false, true);
    // Codeword and QSSD
    assertParamProps("UEGO_stCJ135Ctl_C", propMap, 415341315L, true, "S", "COMPONENT", false, true);

  }

  private void assertParamProps(final String param, final Map<String, ParamProperties> propMap, final long id,
      final boolean isCodeWord, final String pClass, final String ssdClass, final boolean isBlackList,
      final boolean isQssdParameter) {

    final ParamProperties props = propMap.get(param);

    assertNotNull("ParamProperties not null", props);

    assertEquals(param + "'s property - Id", id, props.getId());
    assertEquals(param + "'s property - isCodeWord", isCodeWord, props.isCodeWord());
    assertEquals(param + "'s property - PClass", pClass, props.getPClass());
    assertEquals(param + "'s property - SSD Class", ssdClass, props.getSsdClass());
    assertEquals(param + "'s property - isBlackList", isBlackList, props.isBlackList());
    assertEquals(param + "'s property - isQssdParameter", isQssdParameter, props.isQssdParameter());
  }


}
