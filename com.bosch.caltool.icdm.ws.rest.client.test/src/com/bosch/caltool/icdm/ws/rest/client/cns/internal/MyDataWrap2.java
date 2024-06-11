/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns.internal;

import java.util.Map;

/**
 * @author bne4cob
 */
public class MyDataWrap2 {

  private Long id;
  private Map<Long, MyData> dataMap;

  public MyDataWrap2() {

  }

  public MyDataWrap2(final Long id, final Map<Long, MyData> dataMap) {
    this.id = id;
    this.dataMap = dataMap;
  }

  /**
   * @return the id
   */
  public Long getId() {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return the data
   */
  public Map<Long, MyData> getDataMap() {
    return this.dataMap;
  }

  /**
   * @param data the data to set
   */
  public void setDataMap(final Map<Long, MyData> data) {
    this.dataMap = data;
  }


}
