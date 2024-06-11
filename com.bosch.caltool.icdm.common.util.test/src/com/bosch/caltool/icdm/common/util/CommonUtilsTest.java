/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;


/**
 * Test class for CommonUtils
 *
 * @author bne4cob
 */
public class CommonUtilsTest extends JUnitTest {


  /**
   * Test getDifference() of map
   */
  @Test
  public void test1() {
    Map<String, String> map1 = new HashMap<>();
    map1.put("key1", "val1");
    map1.put("key2", "val2");
    map1.put("key3", "val3");

    Map<String, String> map2 = new HashMap<>();
    map2.put("key1", "val1");

    Map<String, String> map3 = new HashMap<>();
    map3.put("key4", "val4");

    Map<String, String> mapNull = null;

    assertEquals("Test Map1 - Map2", 2, CommonUtils.getDifference(map1, map2).size());
    assertEquals("Test Map2 - Map1", 0, CommonUtils.getDifference(map2, map1).size());

    assertEquals("Test Map1 - Map3", 3, CommonUtils.getDifference(map1, map3).size());
    assertEquals("Test Map3 - Map1", 1, CommonUtils.getDifference(map3, map1).size());

    assertEquals("Test Map1 - MapNull", 3, CommonUtils.getDifference(map1, mapNull).size());
    assertEquals("Test MapNull - Map1", 0, CommonUtils.getDifference(mapNull, map1).size());

  }

  /**
   * Test getDifference() of collection
   */
  @Test
  public void test2() {
    Set<String> set1 = new HashSet<>();
    set1.add("val1");
    set1.add("val2");
    set1.add("val3");

    Set<String> set2 = new HashSet<>();
    set2.add("val1");

    Set<String> set3 = new HashSet<>();
    set3.add("val4");

    Set<String> setNull = null;

    assertEquals("Test Set1 - Set2", 2, CommonUtils.getDifference(set1, set2).size());
    assertEquals("Test Set2 - Set1", 0, CommonUtils.getDifference(set2, set1).size());

    assertEquals("Test Set1 - Set3", 3, CommonUtils.getDifference(set1, set3).size());
    assertEquals("Test Set3 - Set1", 1, CommonUtils.getDifference(set3, set1).size());

    assertEquals("Test Set1 - SetNull", 3, CommonUtils.getDifference(set1, setNull).size());
    assertEquals("Test SetNull - Set1", 0, CommonUtils.getDifference(setNull, set1).size());

  }
}
