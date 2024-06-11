package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for ActiveDirectoryGroupUser
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupUserServiceClientTest extends AbstractRestClientTest {

  private static final Long ACTIVEDIRECTORYGROUPUSER_ID = 36308996028l;


  /**
   *
   */
  @Test
  public void testGet() {
    ActiveDirectoryGroupUserServiceClient servClient = new ActiveDirectoryGroupUserServiceClient();
    try {
      List<ActiveDirectoryGroupUser> userList = servClient.getByGroupId(ACTIVEDIRECTORYGROUPUSER_ID);
      assertNotNull(userList);
      assertTrue(!userList.isEmpty());
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   *
   */
  @Test
  public void testGetInvalidGroupID() {
    ActiveDirectoryGroupUserServiceClient servClient = new ActiveDirectoryGroupUserServiceClient();
    try {
      List<ActiveDirectoryGroupUser> userList = servClient.getByGroupId(1231);
      assertNotNull(userList);
      assertTrue(userList.isEmpty());
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }
}
