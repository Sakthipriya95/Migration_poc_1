/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.bosch.caltool.icdm.cns.server.utils.Utils;


/**
 * @author bne4cob
 */
public class UtilsTest {

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.server.utils.Utils#isEmpty(java.lang.String)}.
   */
  @Test
  public void testIsEmpty() {
    assertTrue("Empty String", Utils.isEmpty(""));
    assertTrue("Null", Utils.isEmpty(null));
    assertFalse("Non empty string", Utils.isEmpty(" "));
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.server.utils.Utils#instantToString(java.time.Instant, java.time.ZoneId)}.
   */
  @Test
  public void testInstantToString() {
    Instant item = Instant.parse("2018-01-01T05:30:20.00Z");
    assertEquals("Instant to string", "2018-01-01 05:30:20", Utils.instantToString(item, ZoneId.of("UTC")));
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.cns.server.utils.Utils#compare(java.lang.Comparable, java.lang.Comparable)}.
   */
  @Test
  public void testCompare() {
    assertEquals("compare a & a", 0, Utils.compare("a", "a"));
    assertEquals("compare a & b", -1, Utils.compare("a", "b"));
    assertEquals("compare b & a", 1, Utils.compare("b", "a"));
    assertEquals("compare 1 & 2", -1, Utils.compare(1, 2));
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.cns.server.utils.Utils#isInteger(java.lang.String)}.
   */
  @Test
  public void testIsInteger() {
    assertTrue("Test with 1", Utils.isInteger("1"));
    assertFalse("Test with null", Utils.isInteger(null));
    assertFalse("Test with a", Utils.isInteger("a"));
  }

}
