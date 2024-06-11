/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.filter;

import java.lang.reflect.InvocationTargetException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.ProjectIdCardInfoType;


/**
 * Filter for any attribute of a PIDC. Member name and it's value are passed. The filter works with reflection to do the
 * filtering.
 *
 * @author imi2si
 */
public class StatisticFilter implements IStatisticFilter {

  /**
   * filter
   */
  private final String filterStr;
  /**
   * member name
   */
  private final String memberName;
  /**
   * inclusive
   */
  private final boolean inclusive;
  /**
   * logger
   */
  private final ILoggerAdapter logger;

  /**
   * Constructor for a Default Customer Filter
   *
   * @param memberName the attributename of the PIDC, that should be filtered
   * @param filter the value that should be filtered
   * @param inclusive true, if the filter should be inclusive, otherwise false
   * @param logger logger
   */
  public StatisticFilter(final String memberName, final String filter, final boolean inclusive,
      final ILoggerAdapter logger) {
    this.filterStr = ".*" + filter + ".*";
    this.memberName = memberName;
    this.inclusive = inclusive;
    this.logger = logger;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean filter(final ProjectIdCardInfoType pidc) {

    String curValue;
    try {
      curValue = (String) pidc.getClass().getMethod("get" + this.memberName).invoke(pidc);
      // if the current value is matching then return true || // if the current value is not matching and not inclusive
      // then return true
      if ((curValue.matches(this.filterStr) && this.inclusive) ||
          (!curValue.matches(this.filterStr) && !this.inclusive)) {
        return true;
      }
    }
    catch (IllegalArgumentException | IllegalAccessException | SecurityException | InvocationTargetException
        | NoSuchMethodException exp) {
      this.logger.error(exp.getMessage(), exp);
    }

    return false;
  }

}
