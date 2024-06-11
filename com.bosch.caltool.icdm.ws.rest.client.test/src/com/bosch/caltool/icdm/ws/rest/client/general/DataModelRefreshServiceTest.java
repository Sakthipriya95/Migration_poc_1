/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * @author bne4cob
 */
@Deprecated
public class DataModelRefreshServiceTest extends AbstractRestClientTest {

  /**
   * Test CQN State
   */
  @Test
  public void test01() {
    try {
      DataModelRefreshServiceClient servClient = new DataModelRefreshServiceClient();
      String state = servClient.getCqnRefreshState();

      LOG.info("CQN State = " + state);

      assertNotNull("CQN State return not null", state);
    }
    catch (Exception e) {
      LOG.error("Error when fetching CQN State", e);
      assertNull("Error when fetching CQN State", e);
    }
  }


}
