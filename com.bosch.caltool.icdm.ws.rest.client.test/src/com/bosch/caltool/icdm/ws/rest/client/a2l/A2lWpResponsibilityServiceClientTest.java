package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lWpDefinitionModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for A2lWpResponsibility
 *
 * @author pdh2cob
 */
public class A2lWpResponsibilityServiceClientTest extends AbstractRestClientTest {

  private final static Long WP_RESP_ID = 1534834977L;

  private final static Long A2L_WP_DEFN_VERS_ID = 1577851779L;

  private final static Long PIDC_A2L_ID = 1525386229L;

  private final static Long VAR_ID = 1511509381L;

  private final static Long VAR_ID2 = 1558894390L;

  // icdm:pidvarid,1507932231-1558894390

  /**
   * Test method for {@link A2lWpResponsibilityServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    A2lWpResponsibilityServiceClient servClient = new A2lWpResponsibilityServiceClient();
    A2lWpResponsibility ret = servClient.get(WP_RESP_ID);
    assertNotNull("Response should not be null", ret);
    assertEquals("Wp definition version id is equal", Long.valueOf(1534834927), ret.getWpDefnVersId());
    assertEquals("Pidc Wp Resp id is equal", Long.valueOf(1986640927), ret.getA2lRespId());
    assertNull("Variant group id is null", ret.getVariantGrpId());
    assertEquals("A2l Wp Pal id is equal", Long.valueOf(1748479477), ret.getA2lWpId());
  }

  /**
   * Test method for {@link A2lWpResponsibilityServiceClient#get(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    A2lWpResponsibilityServiceClient servClient = new A2lWpResponsibilityServiceClient();
    this.thrown.expectMessage("A2l WP Responsibility with ID '" + -1l + "' not found");
    servClient.get(-1l);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link A2lWpResponsibilityServiceClient#getWpResp(Long, Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetWpResp() throws ApicWebServiceException {
    A2lWpResponsibilityServiceClient servClient = new A2lWpResponsibilityServiceClient();
    List<WpRespLabelResponse> wpRespLabList1 = servClient.getWpResp(PIDC_A2L_ID, VAR_ID);
    assertFalse("Response should not be empty", wpRespLabList1.isEmpty());
    resolveWpRespLabels(wpRespLabList1);

    List<WpRespLabelResponse> wpRespLabList2 = servClient.getWpResp(PIDC_A2L_ID, VAR_ID2);
    assertFalse("Response should not be empty", wpRespLabList2.isEmpty());
    resolveWpRespLabels(wpRespLabList2);
  }

  /**
   * Test method for {@link A2lWpResponsibilityServiceClient#getA2lWpRespForWpDefnVers(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetA2lWpRespForWpDefnVers() throws ApicWebServiceException {
    A2lWpResponsibilityServiceClient servClient = new A2lWpResponsibilityServiceClient();
    A2lWpDefinitionModel model = servClient.getA2lWpRespForWpDefnVers(A2L_WP_DEFN_VERS_ID);
    assertNotNull("Response should not be null", model);
    LOG.info("Number of WP Resp Pal for {}: {} ", model.getSelectedWpDefnVersionId(), model.getWpRespMap().size());
  }

  /**
   * Test method for {@link A2lWpResponsibilityServiceClient#getA2lWpRespForWpDefnVers(Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetA2lWpRespForWpDefnVersNegative() throws ApicWebServiceException {
    A2lWpResponsibilityServiceClient servClient = new A2lWpResponsibilityServiceClient();
    A2lWpDefinitionModel model = servClient.getA2lWpRespForWpDefnVers(-1l);
    assertNull("Response should be null", model.getSelectedWpDefnVersionId());
  }


  private void resolveWpRespLabels(final List<WpRespLabelResponse> wpRespLabResponse) {

    Map<WpRespModel, List<Long>> wpRespLabMap = new HashMap<>();


    for (WpRespLabelResponse wpRespLabelResponse : wpRespLabResponse) {
      long paramId = wpRespLabelResponse.getParamId();
      WpRespModel wpRespModel = wpRespLabelResponse.getWpRespModel();

      List<Long> paramIdList = new ArrayList<>();
      if (wpRespLabMap.get(wpRespModel) == null) {
        wpRespLabMap.put(wpRespModel, paramIdList);
      }
      else {
        paramIdList = wpRespLabMap.get(wpRespModel);
      }
      paramIdList.add(paramId);
    }

  }


  /**
   * Test method for {@link A2lWpResponsibilityServiceClient#create(List, PidcA2l)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdateDelete() throws ApicWebServiceException {
    A2lWpResponsibilityServiceClient servClient = new A2lWpResponsibilityServiceClient();
    A2lWpResponsibility obj = new A2lWpResponsibility();
    obj.setWpDefnVersId(1539963177L);
    obj.setA2lRespId(1620286681L);
    obj.setVariantGrpId(1684346480L);
    obj.setA2lWpId(1748443427L);
    PidcA2lServiceClient client = new PidcA2lServiceClient();

    // create
    PidcA2l pidcA2l = client.getById(1539957178L);
    Set<A2lWpResponsibility> createdObjSet = servClient.create(Arrays.asList(obj), pidcA2l);
    assertNotNull("Response should not be null", createdObjSet);
    assertFalse("Response should not be empty", createdObjSet.isEmpty());
    // validate
    A2lWpResponsibility createdObj = createdObjSet.iterator().next();
    assertEquals("Wp definition version id is equal", Long.valueOf(1539963177L), createdObj.getWpDefnVersId());
    assertEquals("Pidc Wp Resp id is equal", Long.valueOf(1620286681L), createdObj.getA2lRespId());
    assertEquals("Variant group id is equal", Long.valueOf(1684346480L), createdObj.getVariantGrpId());
    assertEquals("A2l Wp Pal id is equal", Long.valueOf(1748443427L), createdObj.getA2lWpId());
    // update
    createdObj.setA2lRespId(1558900327L);
    Set<A2lWpResponsibility> updatedSet = servClient.update(Arrays.asList(createdObj), pidcA2l);
    assertFalse("Response should not be null", updatedSet.isEmpty());
    A2lWpResponsibility updatedObj = updatedSet.iterator().next();
    // validate
    assertEquals("Pidc Wp Resp id is equal", Long.valueOf(1558900327L), updatedObj.getA2lRespId());
    // delete
    Set<Long> idSet = new HashSet<>();
    idSet.add(updatedObj.getId());
    servClient.delete(idSet, pidcA2l);

  }
}
