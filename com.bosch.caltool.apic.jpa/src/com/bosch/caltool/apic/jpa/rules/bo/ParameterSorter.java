/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.rules.bo;

import java.math.BigDecimal;

import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Sorting logic for parameters
 *
 * @author bne4cob
 */
public final class ParameterSorter {

  /**
   * private constructor for utility class
   */
  private ParameterSorter() {
    // private constructor for utility class
  }

  /**
   * Compare using parameter name
   *
   * @param param1 IParameter 1
   * @param param2 IParameter 2
   * @return -1 or 0 or 1
   */
  public static int compare(final IParameter<?> param1, final IParameter<?> param2) {
    return ApicUtil.compare(param1.getName(), param2.getName());
  }

  /**
   * Compare using given sort column
   *
   * @param param1 IParameter 1
   * @param param2 IParameter 2
   * @param sortColumn sort Column
   * @return -1 or 0 or 1
   * @throws SsdInterfaceException
   */
  public static int compare(final IParameter param1, final IParameter param2, final IParameter.SortColumns sortColumn)
      throws SsdInterfaceException {

    int compareResult;
    switch (sortColumn) {
      case SORT_PARAM_NAME:
        // use compare method for Strings
        compareResult = ApicUtil.compare(param1.getName(), param2.getName());
        break;

      case SORT_PARAM_LONGNAME:
        // use compare method for Strings
        compareResult = ApicUtil.compare(param1.getLongName(), param2.getLongName());
        break;

      case SORT_PARAM_CLASS:
        // use compare method for Strings
        compareResult = ApicUtil.compare(param1.getpClassText(), param2.getpClassText());
        break;

      case SORT_PARAM_CODEWORD:
        // use compare method for Strings
        compareResult = ApicUtil.compareBoolean(param1.isCodeWord(), param2.isCodeWord());
        break;
      // Icdm-500
      default:
        compareResult = defaultSort(param1, param2, sortColumn);
        break;
    }

    // additional compare column is the name of the characteristic
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ApicUtil.compare(param1.getName(), param2.getName());
    }

    return compareResult;
  }

  /**
   * @param param1
   * @param param2
   * @param sortColumn
   * @return
   * @throws SsdInterfaceException
   */
  private static int defaultSort(final IParameter<?> param1, final IParameter<?> param2,
      final IParameter.SortColumns sortColumn)
      throws SsdInterfaceException {
    int compareResult;
    // For sorting using the following columns
    // SORT_PARAM_LOWERLIMIT:
    // SORT_PARAM_UPPERLIMIT:
    // SORT_PARAM_REFVALUE:
    // SORT_PARAM_RVW_METHOD:
    // SORT_PARAM_UNIT:
    // SORT_EXACT_MATCH:
    compareResult = sortWithRuleCols(param1.getReviewRule(), param2.getReviewRule(), sortColumn);
    return compareResult;
  }


  /**
   * Sorting logic for rule columns, if rule is not null
   *
   * @param param1 IParameter 1
   * @param param2 IParameter 2
   * @param sortColumn sort Column
   * @return -1 or 0 or 1
   */
  private static int sortWithRuleCols(final CDRRule param1Rule, final CDRRule param2Rule,
      final IParameter.SortColumns sortColumn) {

    int compareResult = 0;
    switch (sortColumn) {
      case SORT_PARAM_LOWERLIMIT:
        compareResult = ApicUtil.compareBigDecimal(param1Rule == null ? null : param1Rule.getLowerLimit(),
            param2Rule == null ? null : param2Rule.getLowerLimit());
        break;

      case SORT_PARAM_UPPERLIMIT:
        compareResult = ApicUtil.compareBigDecimal(param1Rule == null ? null : param1Rule.getUpperLimit(),
            param2Rule == null ? null : param2Rule.getUpperLimit());
        break;
      case SORT_PARAM_BITWISE:
        compareResult = ApicUtil.compare((param1Rule == null) ? "" : param1Rule.getBitWiseRule(),
            (param2Rule == null) ? "" : param2Rule.getBitWiseRule());
        break;

      case SORT_PARAM_REFVALUE:
        compareResult = sortRefVal(param1Rule, param2Rule);
        break;

      case SORT_PARAM_READY_SERIES:
        // use compare method for Long
        compareResult = ApicUtil.compare(CDRRuleUtil.getReadyForSeriesUIVal(param1Rule),
            CDRRuleUtil.getReadyForSeriesUIVal(param2Rule));
        break;

      case SORT_PARAM_UNIT:
        compareResult = ApicUtil.compare(param1Rule == null ? "" : param1Rule.getUnit(),
            param2Rule == null ? "" : param2Rule.getUnit());
        break;

      // ICDM-1173
      case SORT_EXACT_MATCH:
        compareResult = sortExactMatch(param1Rule, param2Rule);
        break;

      // ICDM-2152
      case SORT_REMARKS:
        // use compare method for Strings
        compareResult = ApicUtil.compare(param1Rule == null ? "" : param1Rule.getHint(),
            param2Rule == null ? "" : param2Rule.getHint());
        break;

      default:
        break;
    }

    return compareResult;

  }

  /**
   * Sorts using reference value
   *
   * @return sort result
   */
  private static int sortRefVal(final CDRRule param1Rule, final CDRRule param2Rule) {
    int compareResult;
    BigDecimal ref1 = null;
    BigDecimal ref2 = null;
    if ((param1Rule != null) && A2LUtil.VALUE_TEXT.equalsIgnoreCase(param1Rule.getValueType())) {
      ref1 = param1Rule.getRefValue();
    }
    if ((param2Rule != null) && A2LUtil.VALUE_TEXT.equalsIgnoreCase(param2Rule.getValueType())) {
      ref2 = param2Rule.getRefValue();
    }
    // complex types ref value are sorted using string compare
    if ((ref1 == null) && (ref2 == null)) {
      compareResult = ApicUtil.compare(param1Rule == null ? "" : param1Rule.getRefValueDispString(),
          param2Rule == null ? "" : param2Rule.getRefValueDispString());
    }
    // value type ref value are sorted using decimal compare
    else {
      compareResult = ApicUtil.compareBigDecimal(ref1, ref2);
    }
    return compareResult;
  }

  /**
   * Sorts using the exact match column
   *
   * @return sort result
   */
  private static int sortExactMatch(final CDRRule param1Rule, final CDRRule param2Rule) {
    int compareResult;
    String match1 = "";
    String match2 = "";

    if ((param1Rule != null) && !param1Rule.getRefValueDispString().isEmpty()) {
      match1 = param1Rule.isDcm2ssd() ? IParameter.EXACT_MATCH_YES : IParameter.EXACT_MATCH_NO;
    }
    if ((param2Rule != null) && !param2Rule.getRefValueDispString().isEmpty()) {
      match2 = param2Rule.isDcm2ssd() ? IParameter.EXACT_MATCH_YES : IParameter.EXACT_MATCH_NO;
    }

    compareResult = ApicUtil.compare(match1, match2);
    return compareResult;
  }
}
