package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for A2L Responsibility Bosch Group User
 *
 * @author PDH2COB
 */
public class A2lRespBoschGroupUserServiceClientTest extends AbstractRestClientTest {

  private static final Long USER_ID_PDH2COB = 1066590416L;
  private static final Long USER_ID_BNE4COB = 230016L;
  private static final Long A2LRESPBOSCHGROUPUSER_ID = 25067351536L;
  private static final Long RESP_ID = 23560017078L;


  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    A2lRespBoschGroupUserServiceClient servClient = new A2lRespBoschGroupUserServiceClient();
    A2lRespBoschGroupUser ret = servClient.get(A2LRESPBOSCHGROUPUSER_ID);
    assertFalse("Response should not be null", (ret == null));
    testOutput(ret, USER_ID_BNE4COB);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetInvalidInput() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2L Responsibility Bosch Group User with ID '123' not found");
    new A2lRespBoschGroupUserServiceClient().get(123L);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetBoschGrpUserForInvalidResp() throws ApicWebServiceException {
    Long invalidId = 123L;
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + invalidId + "' is invalid for A2L Responsibility Definition");
    new A2lRespBoschGroupUserServiceClient().getA2lRespBoschGrpUsersForRespId(invalidId);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetBoschGrpUserForResp() throws ApicWebServiceException {
    Map<Long, A2lRespBoschGroupUser> a2lRespBoschGroupUsers =
        new A2lRespBoschGroupUserServiceClient().getA2lRespBoschGrpUsersForRespId(RESP_ID);
    assertNotNull(a2lRespBoschGroupUsers);
    assertFalse(a2lRespBoschGroupUsers.isEmpty());
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testCreateDelete() throws ApicWebServiceException {
    A2lRespBoschGroupUserServiceClient servClient = new A2lRespBoschGroupUserServiceClient();
    A2lRespBoschGroupUser obj = new A2lRespBoschGroupUser();
    obj.setA2lRespId(RESP_ID);
    obj.setUserId(USER_ID_PDH2COB);
    // Invoke create method
    A2lRespBoschGroupUser createdObj = servClient.create(obj);
    assertNotNull("object not null", createdObj);
    testOutput(createdObj, USER_ID_PDH2COB);

    // delete
    servClient.delete(createdObj);
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2L Responsibility Bosch Group User with ID \'" + createdObj.getId() + "\' not found");
    // validate deletion
    servClient.get(createdObj.getId());


  }


  /**
   * test output data
   */
  private void testOutput(final A2lRespBoschGroupUser obj, final Long userId) {
    assertTrue("A2lRespId is not equal", obj.getA2lRespId().longValue() == 23560017078L);
    assertTrue("UserId is not equal", obj.getUserId().longValue() == userId.longValue());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
  }

}
