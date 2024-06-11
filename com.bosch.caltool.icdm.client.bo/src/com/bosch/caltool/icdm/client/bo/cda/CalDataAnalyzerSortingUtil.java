/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cda;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultFileModel;
import com.bosch.caltool.icdm.model.cda.CustomerFilter;
import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
import com.bosch.caltool.icdm.model.cda.PlatformFilter;
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;

/**
 * @author say8cob
 */
public class CalDataAnalyzerSortingUtil {


  /**
   * Method to compare 2 ParameterFilterLabel objects based on column index
   *
   * @param label2 - 2nd ParameterFilterLabel
   * @param colIndex - index of column to be compared for sorting
   * @return comparison result
   */
  public int compareTo(final ParameterFilterLabel label1, final ParameterFilterLabel label2, final int colIndex) {
    int compareResult = 0;
    switch (colIndex) {
      case 0:
        compareResult = ApicUtil.compare(label1.getLabel(), label2.getLabel());
        break;
      case 1:
        compareResult = ApicUtil.compareBoolean(label1.isMustExist(), label2.isMustExist());
        break;
    }
    return compareResult;
  }


  /**
   * Method to compare 2 CustomerFilter objects based on column index
   *
   * @param customerFilter - 2nd CustomerFilter object
   * @param colIndex - index of column to be compared for sorting
   * @return comparison result
   */
  public int compareTo(final CustomerFilter customerFilter1, final CustomerFilter customerFilter2, final int colIndex) {
    int compareResult = 0;
    switch (colIndex) {
      case 0:
        compareResult = ApicUtil.compare(customerFilter1.getCustomerName(), customerFilter2.getCustomerName());
        break;

    }
    return compareResult;
  }


  /**
   * Method to compare 2 PlatformFilter objects based on column index
   *
   * @param platformFilter2 - 2nd platformFilter2
   * @param colIndex - index of column to be compared for sorting
   * @return comparison result
   */
  public int compareTo(final PlatformFilter platformFilter1, final PlatformFilter platformFilter2, final int colIndex) {
    int compareResult = 0;
    switch (colIndex) {
      case 0:
        compareResult = ApicUtil.compare(platformFilter1.getEcuPlatformName(), platformFilter2.getEcuPlatformName());
        break;

    }
    return compareResult;
  }


  /**
   * Method to compare 2 FunctionFilter objects based on column index
   *
   * @param functionFilter2 - 2nd FunctionFilter
   * @param colIndex - index of column to be compared for sorting
   * @return comparison result
   */
  public int compareTo(final FunctionFilter functionFilter1, final FunctionFilter functionFilter2, final int colIndex) {
    int compareResult = 0;
    switch (colIndex) {
      case 0:
        compareResult = ApicUtil.compare(functionFilter1.getFunctionName(), functionFilter2.getFunctionName());
        break;

    }
    return compareResult;
  }


  /**
   * Method to compare 2 SystemConstantFilter objects based on column index
   * 
   * @param sysconFilter1 - 1st SystemConstantFilter object
   * @param sysconFilter2 - 2nd SystemConstantFilter object
   * @param colIndex - index of column to be compared for sorting
   * @return comparison result
   */
  public int compareTo(final SystemConstantFilter sysconFilter1, final SystemConstantFilter sysconFilter2,
      final int colIndex) {
    int compareResult = 0;
    switch (colIndex) {
      case 0:
        compareResult = ApicUtil.compare(sysconFilter1.getSystemConstantName(), sysconFilter1.getSystemConstantName());
        break;

    }
    return compareResult;
  }

  /**
   * @param resultFile1
   * @param resultFile2
   * @param colIndex
   * @return comparison result
   */
  public int compareTo(final CaldataAnalyzerResultFileModel resultFile1,
      final CaldataAnalyzerResultFileModel resultFile2, final int colIndex) {
    int compareResult = 0;
    switch (colIndex) {
      case 0:
        if ((resultFile1.getFileName() != null) && (resultFile2.getFileName() != null)) {
          compareResult = ApicUtil.compare(resultFile1.getFileName(), resultFile2.getFileName());
        }
        break;
      case 1:
        compareResult = ApicUtil.compareLong(resultFile1.getFileSize(), resultFile2.getFileSize());
        break;

    }
    return compareResult;
  }


}
