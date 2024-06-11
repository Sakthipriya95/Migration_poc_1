/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;


/**
 * @author bne4cob
 */
public class CollectionUtilTest extends JUnitTest {

  private static final String LOG_INPUT = "Input : {}";
  private static final String LOG_METHOD_TIME = "Time taken for method : {}";
  private static final String LOG_OUTPUT = "Output : {}";

  private static final CollectionUtilSampleModel DATA_1 = new CollectionUtilSampleModel(1, "D1");
  private static final CollectionUtilSampleModel DATA_2 = new CollectionUtilSampleModel(2, "D2");

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.common.util.CollectionUtil#toMap(java.util.Collection, java.util.function.Function)}.
   * <br>
   * Input : list with 2 elements
   */
  @Test
  public void testToMap1List() {
    List<CollectionUtilSampleModel> dataList = Arrays.asList(DATA_1, DATA_2);
    TESTER_LOGGER.info(LOG_INPUT, dataList);

    Timer timer = new Timer();
    Map<Integer, CollectionUtilSampleModel> resultMap =
        CollectionUtil.toMap(dataList, CollectionUtilSampleModel::getId);
    TESTER_LOGGER.debug(LOG_METHOD_TIME, timer.finish());

    TESTER_LOGGER.info(LOG_OUTPUT, resultMap);
    assertNotNull("Result not null", resultMap);

    assertEquals("Result size = 2", 2, resultMap.size());
    assertEquals("Data 1 check", DATA_1, resultMap.get(1));
    assertEquals("Data 2 check", DATA_2, resultMap.get(2));
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.common.util.CollectionUtil#toMap(java.util.Collection, java.util.function.Function)}.
   * <br>
   * Input : list with duplicate records
   */
  @Test
  public void testToMap2ListDupData() {
    // data1 added twice
    List<CollectionUtilSampleModel> dataList = Arrays.asList(DATA_1, DATA_1);
    TESTER_LOGGER.info(LOG_INPUT, dataList);

    Timer timer = new Timer();
    Map<Integer, CollectionUtilSampleModel> resultMap =
        CollectionUtil.toMap(dataList, CollectionUtilSampleModel::getId);
    TESTER_LOGGER.debug("Time taken for conversion: {}", timer.finish());

    TESTER_LOGGER.info(LOG_OUTPUT, resultMap);
    assertNotNull("Result not null", resultMap);

    assertEquals("Result size = 1", 1, resultMap.size());
    assertEquals("Data 1 check", DATA_1, resultMap.get(1));
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.common.util.CollectionUtil#toMap(java.util.Collection, java.util.function.Function)}.
   * <br>
   * Input : list with null collection
   */
  @Test
  public void testToMap3NullInput() {
    // null collection
    List<CollectionUtilSampleModel> dataList = null;
    TESTER_LOGGER.info(LOG_INPUT, dataList);

    Timer timer = new Timer();
    Map<Integer, CollectionUtilSampleModel> resultMap = CollectionUtil.toMap(null, CollectionUtilSampleModel::getId);
    TESTER_LOGGER.debug("Time taken for conversion: {}", timer.finish());

    TESTER_LOGGER.info(LOG_OUTPUT, resultMap);
    assertNull("Result is null", resultMap);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.common.util.CollectionUtil#toMap(java.util.Collection, java.util.function.Function)}.
   * <br>
   * Input : list with duplicate records
   */
  @Test
  public void testToMap4NullMapper() {
    List<CollectionUtilSampleModel> dataList = Arrays.asList(DATA_1, DATA_2);
    TESTER_LOGGER.info(LOG_INPUT, dataList);

    this.thrown.expect(IllegalArgumentException.class);
    this.thrown.expectMessage("Key mapper cannot be null");
    CollectionUtil.toMap(dataList, null);
  }

}
