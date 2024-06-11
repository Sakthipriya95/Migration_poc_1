/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.SortedSet;
import java.util.TreeSet;


/**
 * @author rgo7cob
 */
public class ResponsibiltyRvwDataReport {

  private SortedSet<ResponsibiltyRvwData> dataReportSet = new TreeSet<>();


  /**
   * @return the dataReportSet
   */
  public SortedSet<ResponsibiltyRvwData> getDataReportSet() {
    return this.dataReportSet;
  }

  /**
   * @param dataReportSet the dataReportSet to set
   */
  public void setDataReportSet(final SortedSet<ResponsibiltyRvwData> dataReportSet) {
    this.dataReportSet = dataReportSet == null ? null : new TreeSet<>(dataReportSet);
  }


}
