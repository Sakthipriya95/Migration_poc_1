/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns.internal;


/**
 * @author bne4cob
 */
public class MyDataWrap {

  private Long id;
  private MyData data;

  public MyDataWrap() {

  }

  public MyDataWrap(final MyData data) {
    this.id = data.getId();
    this.data = data;
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
  public MyData getData() {
    return this.data;
  }

  /**
   * @param data the data to set
   */
  public void setData(final MyData data) {
    this.data = data;
  }


}
