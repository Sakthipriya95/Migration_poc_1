/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for Alias Definition
 *
 * @author bne4cob
 */
public class AliasDefServiceClientTest extends AbstractRestClientTest {

  /**
   * 779269465 - WebFlow
   */
  private final static Long ALIASDEF_ID = 780309114L;
  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Test method for {@link AliasDefServiceClient#getAll()}
   */
  @Test
  public void testGetAll() {
    AliasDefServiceClient servClient = new AliasDefServiceClient();
    try {
      Map<Long, AliasDef> retMap = servClient.getAll();
      assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
      AliasDef expectedDef = retMap.get(ALIASDEF_ID);
      testOutput(expectedDef);
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * Test method for {@link AliasDefServiceClient#get(Long)}
   */
  @Test
  public void testGet() {
    AliasDefServiceClient servClient = new AliasDefServiceClient();
    try {
      AliasDef ret = servClient.get(ALIASDEF_ID);
      assertFalse("Response should not be null", (ret == null));
      testOutput(ret);
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }


  /**
   * test output data
   */
  private void testOutput(final AliasDef obj) {
    assertNotNull("obj not null", obj);
    LOG.debug("Alias Definition = {}", obj);
    assertEquals("AdName is equal", "webFlow", obj.getName().trim());
    assertEquals("CreatedUser is equal", "DGS_ICDM", obj.getCreatedUser());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
  }

}
