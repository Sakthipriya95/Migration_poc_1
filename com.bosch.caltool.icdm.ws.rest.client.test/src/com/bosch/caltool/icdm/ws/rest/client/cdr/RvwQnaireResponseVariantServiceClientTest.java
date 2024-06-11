/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.qnaire.DefineQnaireRespInputData;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmr1cob
 */
public class RvwQnaireResponseVariantServiceClientTest extends AbstractRestClientTest {

  /*
   * pidc -PIDC_For_Questionnaire_Response (v1), qnaire - ACT -> Sathyamurthy, Sharavan Pravin -> Wp1_Test -> AirDvp
   */
  private static final Long RVW_QNAIRE_RESP_ID_1 = 12676790728L;
  /*
   * Qnaire resp id without varinat
   */
  private static final Long RVW_QNAIRE_RESP_ID_2 = 29128258263L;
  /*
   * Pidc vers id
   */
  private static final Long PIDC_VERS_ID_1 = 10827299181L;
  /*
   * pidc -PIDC_For_Questionnaire_Response (v1), qnaire - ACT -> Sathyamurthy, Sharavan Pravin -> Wp1_Test -> AirDvp ->
   * var id
   */
  private static final Long QNAIRE_RESP_VAR_ID_1 = 12676790729L;
  /**
   *
   */
  private static final String ERROR_IN_WS_CALL = "Error in WS call";

  /**
   * Test get review qnaire response varinat using qnaire response var id
   */
  @Test
  public void testGetRvwQnaireRespVariant() {
    RvwQnaireRespVariantServiceClient client = new RvwQnaireRespVariantServiceClient();
    try {
      RvwQnaireRespVariant rvwQnaireRespVariant = client.getRvwQnaireRespVariant(QNAIRE_RESP_VAR_ID_1);
      assertNotNull(rvwQnaireRespVariant);
    }
    catch (ApicWebServiceException excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
      assertNull(ERROR_IN_WS_CALL, excep);
    }
  }


  /**
   * Test get review qnaire response varinat using qnaire response id
   */
  @Test
  public void testGetRvwQnaireRespVariantList() {
    RvwQnaireRespVariantServiceClient client = new RvwQnaireRespVariantServiceClient();
    List<RvwQnaireRespVariant> rvwQnaireRespVariant = null;
    try {
      rvwQnaireRespVariant = client.getRvwQnaireRespVariantList(RVW_QNAIRE_RESP_ID_1);
      assertNotNull(rvwQnaireRespVariant);
      assertNotNull(rvwQnaireRespVariant.get(0).getVariantId());
    }
    catch (ApicWebServiceException excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
      assertNull(ERROR_IN_WS_CALL, excep);
    }
  }

  /**
   * Test get review qnaire response varinat using qnaire response id for no variant
   */
  @Test
  public void testGetRvwQnaireRespVariantNoVariant() {
    RvwQnaireRespVariantServiceClient client = new RvwQnaireRespVariantServiceClient();
    List<RvwQnaireRespVariant> rvwQnaireRespVariant = null;
    try {
      rvwQnaireRespVariant = client.getRvwQnaireRespVariantList(RVW_QNAIRE_RESP_ID_2);
      assertNotNull(rvwQnaireRespVariant);
      // For no variant variant id will be null
      assertNull(rvwQnaireRespVariant.get(0).getVariantId());
    }
    catch (ApicWebServiceException excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
      assertNull(ERROR_IN_WS_CALL, excep);
    }
  }

  /**
   * Test get define qnaire input data service
   */
  @Test
  public void testGetDefineQnaireRespInputData() {
    RvwQnaireRespVariantServiceClient client = new RvwQnaireRespVariantServiceClient();
    try {
      DefineQnaireRespInputData defineQnaireRespInputData = client.getDefineQnaireRespInputData(PIDC_VERS_ID_1);
      assertNotNull("Qnaire Info for selected pidc version should not be null",
          defineQnaireRespInputData.getQnaireInfo());
      assertNotNull("Variant contianing qnaire response should not be null",
          defineQnaireRespInputData.getAllVariantMap());
      assertNotNull("Qnaire resp variant map should not be null", defineQnaireRespInputData.getQnaireRespVarMap());
    }
    catch (ApicWebServiceException excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
      assertNull(ERROR_IN_WS_CALL, excep);
    }
  }


}
