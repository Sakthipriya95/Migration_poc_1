package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Review Comment History
 *
 * @author PDH2COB
 */
public class RvwCmntHistoryServiceClientTest extends AbstractRestClientTest {

  /**
  *
  */
  private static final Long USER_ID = 1066590416L;


  private static final Long INVALID_ID = 123L;
  /**
  *
  */
  private static final String JUNIT_TEST_REVIEW_OKAY = "Junit test - Review okay!";


  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.rest.client.cdr.RvwUserCmntHistoryServiceClient#testCreateDeleteRvwCmntHistory()}
   */
  @Test
  public void testCreateDeleteRvwCmntHistory() {
    RvwUserCmntHistoryServiceClient servClient = new RvwUserCmntHistoryServiceClient();
    try {
      RvwUserCmntHistory obj = new RvwUserCmntHistory();
      obj.setRvwComment(JUNIT_TEST_REVIEW_OKAY);
      obj.setRvwCmntUserId(USER_ID);

      // Invoke create method
      RvwUserCmntHistory createdObj = servClient.create(obj);
      assertNotNull("object not null", createdObj);
      validateRvwCommentHistory(createdObj, USER_ID, JUNIT_TEST_REVIEW_OKAY);

      // delete
      servClient.delete(createdObj);

    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetRvwCommentHistoryByUser() throws ApicWebServiceException {
    Map<Long, RvwUserCmntHistory> rvwCmntHistoryMap =
        new RvwUserCmntHistoryServiceClient().getRvwCmntHistoryByUser(USER_ID);
    assertNotNull(rvwCmntHistoryMap);
    assertTrue(rvwCmntHistoryMap.size() > 0);
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    RvwUserCmntHistory rvwCmntHistory = new RvwUserCmntHistoryServiceClient().get(22112841178L);
    assertNotNull(rvwCmntHistory);
    validateRvwCommentHistory(rvwCmntHistory, USER_ID, "ok");
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetByIdNullInput() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Review User Comment History with ID 'null' not found");
    new RvwUserCmntHistoryServiceClient().get(null);
    fail("expected exception not thrown");
  }

  /**
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetByIdInvalidInput() throws ApicWebServiceException {
    RvwUserCmntHistoryServiceClient servClient = new RvwUserCmntHistoryServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Review User Comment History with ID '" + INVALID_ID + "' not found");
    servClient.get(INVALID_ID);
    fail("expected exception not thrown");
  }


  private void validateRvwCommentHistory(final RvwUserCmntHistory createdObj, final Long userId, final String comment) {
    assertEquals("User is not equal", userId, createdObj.getRvwCmntUserId());
    assertEquals("Comment is not equal", comment, createdObj.getRvwComment());

  }

}
