/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.adapter;


/**
 * @author imi2si
 */
public abstract class AbstractIcdmMetaData {

  /**
   * Returns the overall number of mandatory attributes independent of a PIDC
   * 
   * @return the number of mandatory attributes
   */
  public abstract int getPidcNoOfMandAttributes();
}
