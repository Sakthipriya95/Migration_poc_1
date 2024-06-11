/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.framework.impl.A;
import com.bosch.caltool.icdm.client.bo.framework.impl.SAMPLE_MODEL_TYPE;
import com.bosch.caltool.icdm.client.bo.framework.impl.TestUtils;


/**
 * @author bne4cob
 */
public class CnsUtilsTest {

  private static final A A_V1 = TestUtils.createA(1L, "A1", 1L);
  private static final A A_V2 = TestUtils.createA(1L, "A1 - upd", 2L);

  /**
   * create mode
   */
  @Test
  public void test01() {
    ChangeData<A> data = TestUtils.createChangeData(1L, CHANGE_OPERATION.CREATE, SAMPLE_MODEL_TYPE.TYPE_A, null, A_V1);
    assertTrue("has direct changes - create mode", CnsUtils.hasDirectChanges(data));
    assertEquals("direct change - create mode", "A1", CnsUtils.getDirectChanges(data).get("name")[0]);
  }

  /**
   * update mode
   */
  @Test
  public void test02() {
    ChangeData<A> data = TestUtils.createChangeData(1L, CHANGE_OPERATION.UPDATE, SAMPLE_MODEL_TYPE.TYPE_A, A_V1, A_V2);
    assertTrue("has direct changes - update mode", CnsUtils.hasDirectChanges(data));
    assertEquals("direct change - update mode new value", "A1 - upd", CnsUtils.getDirectChanges(data).get("name")[0]);
    assertEquals("direct change - update mode old value", "A1", CnsUtils.getDirectChanges(data).get("name")[1]);
  }

}
