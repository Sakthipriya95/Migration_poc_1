/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.FunctionVersionUnique;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * @author rgo7cob
 */
public class FunctionVersionServiceClientTest extends AbstractRestClientTest {

  private static final String FUNCTION_NAME = "BBKR";

  private static final int MAX_LOOP_COUNT = 5;


  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient#getAllFuncVersions()}.
   */
  @Test
  public void testGetAllFuncVersion() {
    FunctionVersionServiceClient service = new FunctionVersionServiceClient();
    try {

      Set<FunctionVersionUnique> functions = service.getAllFuncVersions(FUNCTION_NAME);

      assertFalse("Multiple versions available", (functions == null) || functions.isEmpty());
      LOG.info("Versions count for function {} = {}", FUNCTION_NAME, functions.size());

      int count = 0;
      for (FunctionVersionUnique func : functions) {
        LOG.info("Verifying first {} records ... ", MAX_LOOP_COUNT);
        testOutput("Response item", func);
        count++;

        if (count >= MAX_LOOP_COUNT) {
          break;
        }
      }

    }
    catch (Exception e) {
      LOG.error("Error in WS call", e);
      assertNull("Error in WS call", e);
    }
  }


  /**
   * @param string
   * @param wpRes
   */
  private void testOutput(final String string, final FunctionVersionUnique funcVer) {
    LOG.info("{}: Function Id = {}; {}; {}", string, funcVer.getId(), funcVer.getFuncName(), funcVer.getFuncVersion());
    assertNotNull(string + ": object not null", funcVer);
    assertFalse(string + ": Name not empty", (funcVer.getFuncName() == null) || funcVer.getFuncName().isEmpty());
  }
}
