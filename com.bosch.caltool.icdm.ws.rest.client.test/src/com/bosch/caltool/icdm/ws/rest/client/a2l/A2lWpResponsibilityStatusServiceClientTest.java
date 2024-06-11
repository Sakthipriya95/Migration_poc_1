package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.WP_RESP_STATUS_TYPE;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for A2LWPResponsibilityStatus
 *
 * @author UKT1COB
 */
public class A2lWpResponsibilityStatusServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final long WP_DEFN_VERS_ID = 30906474328L;

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityStatusServiceClient#updateWpFinStatus(A2lWpRespStatusUpdationModel)}
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testExistingRecordToBeCreatedAgain() throws ApicWebServiceException {

    A2lWpResponsibilityStatusServiceClient servClient = new A2lWpResponsibilityStatusServiceClient();

    Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusObjBeforeUpdMap = getA2lWPRespStatusToBeUpdated(servClient);

    A2lWpRespStatusUpdationModel a2lWPRespSttausUpdModel = new A2lWpRespStatusUpdationModel();
    a2lWPRespSttausUpdModel.setA2lWpRespStatusListToBeCreated(new ArrayList(a2lWPRespStatusObjBeforeUpdMap.values()));

    // Invoke update method
    A2lWpRespStatusUpdationModel updatedModel = servClient.updateWpFinStatus(a2lWPRespSttausUpdModel);

    updatedModel.getA2lWpRespStatusMapAfterUpdate().values().stream().forEach(a2lWPRespStatus -> {
      assertNotNull("object not null", a2lWPRespStatus);
      testUpdateOutput(a2lWPRespStatus, updatedModel.getA2lWpRespStatusToBeUpdatedMap());
    });
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityStatusServiceClient#updateWpFinStatus(A2lWpRespStatusUpdationModel)}
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testUpdateWPFinishedStatus() throws ApicWebServiceException {

    A2lWpResponsibilityStatusServiceClient servClient = new A2lWpResponsibilityStatusServiceClient();

    Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusObjBeforeUpdMap = getA2lWPRespStatusToBeUpdated(servClient);

    A2lWpRespStatusUpdationModel a2lWPRespSttausUpdModel = new A2lWpRespStatusUpdationModel();
    a2lWPRespSttausUpdModel.setA2lWpRespStatusToBeUpdatedMap(a2lWPRespStatusObjBeforeUpdMap);

    // Invoke update method
    A2lWpRespStatusUpdationModel updatedModel = servClient.updateWpFinStatus(a2lWPRespSttausUpdModel);

    updatedModel.getA2lWpRespStatusMapAfterUpdate().values().stream().forEach(a2lWPRespStatus -> {
      assertNotNull("object not null", a2lWPRespStatus);
      testUpdateOutput(a2lWPRespStatus, updatedModel.getA2lWpRespStatusToBeUpdatedMap());
    });
  }


  /**
   * @param servClient
   * @return
   * @throws ApicWebServiceException
   */
  private Map<Long, A2lWpResponsibilityStatus> getA2lWPRespStatusToBeUpdated(
      final A2lWpResponsibilityStatusServiceClient servClient)
      throws ApicWebServiceException {
    A2lWpResponsibilityStatus a2lWPRespStatusObj = servClient.get(30041849780L);
    A2lWpResponsibilityStatus a2lWPRespStatusObj1 = servClient.get(30041849781L);

    List<A2lWpResponsibilityStatus> a2lWPRespStatusObjToBeUpd = new ArrayList<>();
    a2lWPRespStatusObjToBeUpd.add(a2lWPRespStatusObj);
    a2lWPRespStatusObjToBeUpd.add(a2lWPRespStatusObj1);

    Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusObjBeforeUpdMap = new HashMap<>();
    a2lWPRespStatusObjToBeUpd.stream().forEach(a2lWPRespStatus -> {
      Long a2lWPRespStatusId = a2lWPRespStatus.getId();
      // Set new Status
      a2lWPRespStatus.setWpRespFinStatus(
          CommonUtils.isEqual(a2lWPRespStatus.getWpRespFinStatus(), WP_RESP_STATUS_TYPE.FINISHED.getDbType())
              ? WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType() : WP_RESP_STATUS_TYPE.FINISHED.getDbType());
      a2lWPRespStatusObjBeforeUpdMap.put(a2lWPRespStatusId, a2lWPRespStatus);
    });
    return a2lWPRespStatusObjBeforeUpdMap;
  }


  /**
   * test output data
   *
   * @param statusBeforeUpd
   * @param oldVersion
   */
  private void testUpdateOutput(final A2lWpResponsibilityStatus updatedA2lWPRespStatusObj,
      final Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusMapBeforeUpd) {

    Long expectedVersion = (a2lWPRespStatusMapBeforeUpd.get(updatedA2lWPRespStatusObj.getId()).getVersion()) + 1;
    String expectedStatus = a2lWPRespStatusMapBeforeUpd.get(updatedA2lWPRespStatusObj.getId()).getWpRespFinStatus();

    assertEquals("Version is Equal", expectedVersion, updatedA2lWPRespStatusObj.getVersion());
    assertEquals("WpRespFinStatus is equal", expectedStatus, updatedA2lWPRespStatusObj.getWpRespFinStatus());
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityStatusServiceClient#getWpStatusByVarAndWpDefnVersId(Long, Long)}
   *
   * @throws ApicWebServiceException Exception
   */
  @Test
  public void testGetWpStatusByVarAndWpDefnVersId() throws ApicWebServiceException {

    Map<Long, Map<Long, A2lWpResponsibilityStatus>> wpStatusMap =
        new A2lWpResponsibilityStatusServiceClient().getWpStatusByVarAndWpDefnVersId(null, WP_DEFN_VERS_ID);
    assertNotNull("Status should not be null", wpStatusMap);
  }

}
