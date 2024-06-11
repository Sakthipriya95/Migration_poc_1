package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroup;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for ActiveDirectoryGroup
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link ActiveDirectoryGroupServiceClient}
   */
  @Test
  public void testGet() {
    ActiveDirectoryGroupServiceClient servClient = new ActiveDirectoryGroupServiceClient();
    try {
      ActiveDirectoryGroup ret = servClient.get("MS/ETB5-PS-Delivery Leads");
      assertFalse("Response should not be null", (ret == null));
      testOutput(ret);
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * Test method for {@link ActiveDirectoryGroupServiceClient}
   */
  @Test
  public void testSyncActiveDirectoryGroupUsers() {
    ActiveDirectoryGroupServiceClient servClient = new ActiveDirectoryGroupServiceClient();
    try {
      servClient.syncActiveDirectoryGroupUsers();
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }


  /**
   * test output data
   */
  private void testOutput(final ActiveDirectoryGroup obj) {
    assertEquals("GroupName is equal", obj.getGroupName(), "MS/ETB5-PS-Delivery Leads");
    assertEquals("GroupSid is equal", obj.getGroupSid(), "COB_RBEI_MS_ETB5_PS-DELIVERYLEADS");
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
  }

}
