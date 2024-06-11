/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr.chkssdxlrptparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author APJ4COB
 */
public class CheckSSDRprtXlData {

  private int ssdLabelCount = 0;

  // Map---Key-->SSD Label name,Value--->UsecaseStatusExcelData,a model that contains excel row info like ssd
  // label,usecase and status
  private final Map<String, List<CheckSSDXlRprtRowData>> ssdLabelToUcMap = new HashMap<>();


  /**
   * @return the ssdLabelCount
   */
  public int getSsdLabelCount() {
    return this.ssdLabelCount;
  }

  /**
   * @param ssdLabelCount the ssdLabelCount to set
   */
  public void setSsdLabelCount(final int ssdLabelCount) {
    this.ssdLabelCount = ssdLabelCount;
  }


  /**
   * @return the ssdLabelToUcMap
   */
  public Map<String, List<CheckSSDXlRprtRowData>> getSsdLabelToUcMap() {
    return this.ssdLabelToUcMap;
  }


}
