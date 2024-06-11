/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.filter;

import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;

/**
 * Interface for a filter applied before executing a statistic report.
 * 
 * @author imi2si
 */
public interface IStatisticFilter {

  /**
   * The filter determines, if the pased pidc should be part of the report or not. If it should be part of the report,
   * true is returned, otherwise false.
   * 
   * @param pidc the ProjectIdCardInfoType that should be checked for apperance in the report
   * @return true, if the PIDC should be part of the report, otherwise false
   */
  boolean filter(ProjectIdCardInfoType pidc);

}
