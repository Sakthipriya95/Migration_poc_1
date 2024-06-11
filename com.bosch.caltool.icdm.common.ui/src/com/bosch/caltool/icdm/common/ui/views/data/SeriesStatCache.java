/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.data;

import java.util.HashMap;
import java.util.Map;

import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author rgo7cob
 */
public class SeriesStatCache {


  // Singleton class instance
  private static SeriesStatCache singletonObj;

  public static SeriesStatCache getInstance() {
    if (CommonUtils.isNull(singletonObj)) {
      singletonObj = new SeriesStatCache();
    }
    return singletonObj;
  }

  Map<String, LabelInfoVO> mapOfSeriesStatistics = new HashMap<>();


  /**
   * @return the mapOfSeriesStatistics
   */
  public Map<String, LabelInfoVO> getMapOfSeriesStatistics() {
    return this.mapOfSeriesStatistics;
  }


  /**
   * @param mapOfSeriesStatistics the mapOfSeriesStatistics to set
   */
  public void setMapOfSeriesStatistics(final Map<String, LabelInfoVO> mapOfSeriesStatistics) {
    this.mapOfSeriesStatistics = mapOfSeriesStatistics;
  }


}
