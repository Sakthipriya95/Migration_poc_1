/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.data;

import com.bosch.calcomp.caldataanalyzer.vo.LabelValueInfoVO;


/**
 * Icdm-674 create the Serial Index Column for the series statistics view part table
 * 
 * @author rgo7cob
 */
public class SeriesStatisticsTableData {


  /**
   * @param labelValueInfo labelValueInfo
   * @param serialIndex serialIndex
   */
  public SeriesStatisticsTableData(final LabelValueInfoVO labelValueInfo, final int serialIndex) {
    this.labelValueInfo = labelValueInfo;
    this.serialIndex = serialIndex;
  }

  private final LabelValueInfoVO labelValueInfo;
  private final int serialIndex;


  /**
   * @return the labelValueInfo
   */
  public LabelValueInfoVO getLabelValueInfo() {
    return this.labelValueInfo;
  }

  /**
   * @return the serialIndex
   */
  public int getSerialIndex() {
    return this.serialIndex;
  }


}
