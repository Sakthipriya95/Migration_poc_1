/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns.internal;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.datamodel.core.IModel;


/**
 * @author bne4cob
 */
public class ModelParserTest {

  private static final IMapper MYDATA_WRAP_MAPPER = obj -> new HashSet<>(Arrays.asList(((MyDataWrap) obj).getData()));

  private static final IMapper MYDATA_WRAP2_MAPPER = obj -> new HashSet<>(((MyDataWrap2) obj).getDataMap().values());


  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser#getModel(java.lang.Object)}.
   */
  @Test
  public void testGetModelObjectModel() {
    Object respData = new MyData(1L, "OBJ-1", 1L);
    Map<Long, IModel> newDataMap = ModelParser.getModel(respData);

    testResult("testGetModelObjectModel", 1, newDataMap);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser#getModel(java.lang.Object)}.
   */
  @Test
  public void testGetModelObjectArr() {
    Object respData =
        new MyData[] { new MyData(1L, "OBJ-2.1", 1L), new MyData(2L, "OBJ-2.2", 1L), new MyData(3L, "OBJ-2.3", 1L) };
    Map<Long, IModel> newDataMap = ModelParser.getModel(respData);

    testResult("testGetModelObjectArr", 3, newDataMap);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser#getModel(java.lang.Object)}.
   */
  @Test
  public void testGetModelObjectMap() {
    Map<Long, MyData> respData = new HashMap<>();
    respData.put(1L, new MyData(1L, "OBJ-3.1", 1L));
    respData.put(2L, new MyData(2L, "OBJ-3.2", 1L));
    respData.put(3L, new MyData(3L, "OBJ-3.3", 1L));

    Object servResp = respData;

    Map<Long, IModel> newDataMap = ModelParser.getModel(servResp);

    testResult("testGetModelObjectMap", 3, newDataMap);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser#getModel(java.lang.Object)}.
   */
  @Test
  public void testGetModelObjectCol() {
    Set<MyData> respData = new HashSet<>();
    respData.add(new MyData(1L, "OBJ-4.1", 1L));
    respData.add(new MyData(2L, "OBJ-4.2", 1L));
    respData.add(new MyData(3L, "OBJ-4.3", 1L));

    Object servResp = respData;

    Map<Long, IModel> newDataMap = ModelParser.getModel(servResp);

    testResult("testGetModelObjectCol", 3, newDataMap);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser#getModel(java.lang.Object, com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper)}.
   */
  @Test
  public void testGetModelObjectIMapperWrap1() {
    Set<MyDataWrap> respData = new HashSet<>();
    respData.add(new MyDataWrap(new MyData(1L, "OBJ-5.1", 1L)));
    respData.add(new MyDataWrap(new MyData(2L, "OBJ-5.2", 1L)));
    respData.add(new MyDataWrap(new MyData(3L, "OBJ-5.3", 1L)));

    Object servResp = respData;

    Map<Long, IModel> newDataMap = ModelParser.getModel(servResp, MYDATA_WRAP_MAPPER);

    testResult("testGetModelObjectIMapperWrap1", 3, newDataMap);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser#getModel(java.lang.Object, com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper)}.
   */
  @Test
  public void testGetModelObjectIMapperWrap2() {
    Map<Long, MyData> respData = new HashMap<>();
    respData.put(1L, new MyData(1L, "OBJ-6.1", 1L));
    respData.put(2L, new MyData(2L, "OBJ-6.2", 1L));
    respData.put(3L, new MyData(3L, "OBJ-6.3", 1L));


    Object servResp = new MyDataWrap2(1L, respData);

    Map<Long, IModel> newDataMap = ModelParser.getModel(servResp, MYDATA_WRAP2_MAPPER);

    testResult("testGetModelObjectIMapperWrap2", 3, newDataMap);
  }

  private void testResult(final String test, final int expectedSize, final Map<Long, IModel> newDataMap) {
    assertEquals(test + ": data map size is " + expectedSize, expectedSize, newDataMap.size());
    System.out.println("Test : " + test + " ----------------------");
    newDataMap.values().forEach(System.out::println);
  }


}
