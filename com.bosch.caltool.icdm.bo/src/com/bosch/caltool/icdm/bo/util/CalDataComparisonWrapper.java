/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.util;

import com.bosch.calcomp.caldatautils.CalDataComparism;
import com.bosch.calcomp.caldatautils.CompareQuantized;
import com.bosch.calcomp.caldatautils.ItemsToCompare.AvailableItemsForComparison;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;

/**
 * @author bne4cob
 */
public class CalDataComparisonWrapper {

  private final Characteristic a2lChar;

  /**
   * @param a2lChar A2L Characteristic
   */
  public CalDataComparisonWrapper(final Characteristic a2lChar) {
    this.a2lChar = a2lChar;
  }


  /**
   * Compare two <code>CalDataPhy</code> objects. <br>
   * NOTE : method returns <code>NOT_EQUAL</code>, if both inputs are <code>null</code>
   *
   * @param calDataPhy1 <code>CalDataPhy</code> 1
   * @param calDataPhy2 <code>CalDataPhy</code> 2
   * @return CalDataComparism
   */
  public CalDataComparism compare(final CalDataPhy calDataPhy1, final CalDataPhy calDataPhy2) {
    if ((calDataPhy1 == null) || (calDataPhy2 == null)) {
      return CalDataComparism.compareResultNotEqual();
    }

    return doCompare(calDataPhy1, calDataPhy2);
  }

  /**
   * Check if the two <code>CalData</code> objects are same by comparing their <code>CalDataPhy</code> objects
   *
   * @param calData1 <code>CalData</code> 1
   * @param calData2 <code>CalData</code> 2
   * @return true if two objects are equal
   */
  public boolean isEqual(final CalData calData1, final CalData calData2) {
    if ((calData1 == null) && (calData2 == null)) {
      // when both objects are null, return true
      return true;
    }
    if ((calData1 == null) || (calData2 == null)) {
      // if only one object is null, return false
      return false;
    }

    CalDataComparism comparison = doCompare(calData1.getCalDataPhy(), calData2.getCalDataPhy());

    return comparison.getCompareResult() == CalDataComparism.CompareResult.EQUAL;
  }

  /**
   * Invoke CalDataComparism to compare CalDataPhy objects
   *
   * @param calDataPhy1 CalDataPhy 1
   * @param calDataPhy2 CalDataPhy 2
   * @return CalDataComparism
   */
  private CalDataComparism doCompare(final CalDataPhy calDataPhy1, final CalDataPhy calDataPhy2) {

    return CompareQuantized.isEqualForAllItemsExceptExcludedWithCause(this.a2lChar, calDataPhy1, calDataPhy2,
        AvailableItemsForComparison.A2L_UNIT, AvailableItemsForComparison.CAL_DATA_UNITS,
        AvailableItemsForComparison.NO_OF_CHARACTERS, AvailableItemsForComparison.TEXT_BIT);

  }

}
