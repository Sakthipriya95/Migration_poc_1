package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for ActiveDirectoryGroupNodeAccess
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupNodeAccessServiceClientTest extends AbstractRestClientTest {

  private static final Long ACTIVEDIRECTORYGROUPNODEACCESS_ID = 30073876729l;


  /**
   * Test method for {@link ActiveDirectoryGroupNodeAccessServiceClient#getNodeAccessByNodeId(long)}
   */
  @Test
  public void testGet() {
    ActiveDirectoryGroupNodeAccessServiceClient servClient = new ActiveDirectoryGroupNodeAccessServiceClient();
    try {
      Map<Long, List<ActiveDirectoryGroupNodeAccess>> ret =
          servClient.getNodeAccessByNodeId(ACTIVEDIRECTORYGROUPNODEACCESS_ID);
      assertFalse("Response should not be null", (ret == null));
      testOutput(ret.get(ACTIVEDIRECTORYGROUPNODEACCESS_ID).get(0));
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * Test method for
   * {@link ActiveDirectoryGroupNodeAccessServiceClient#createADGroupAccess(String, String, long, String)}
   */
  @Test
  public void testCRUD() {
    ActiveDirectoryGroupNodeAccessServiceClient servClient = new ActiveDirectoryGroupNodeAccessServiceClient();
    try {
      String groupName = "MS/ETB53-PS-MDM-Team";
      String groupSID = "Cob2_MS_ETB53-PS-MDM-Team";
      long nodeId = 2176812329l;
      // Create access to new AD Group
      ActiveDirectoryGroupNodeAccess access = servClient.createADGroupAccess(groupName, groupSID, nodeId, "PIDC");
      assertNotNull(access);
      assertNotNull(access.getAdGroup());
      // Update the access for the created obj
      access.setWrite(true);
      access.setOwner(true);
      access.setGrant(true);
      ActiveDirectoryGroupNodeAccess updObj = servClient.update(access);
      assertNotNull(updObj);
      assertTrue(updObj.isGrant());
      assertTrue(updObj.isOwner());
      assertTrue(updObj.isWrite());
      // Delete the access for created Obj
      servClient.delete(updObj);
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }


  /**
   * test output data
   */
  private void testOutput(final ActiveDirectoryGroupNodeAccess obj) {
    assertEquals("NodeId is equal", obj.getNodeId(), ACTIVEDIRECTORYGROUPNODEACCESS_ID);
    assertEquals("NodeType is equal", obj.getNodeType(), "PIDC");
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
  }

}
