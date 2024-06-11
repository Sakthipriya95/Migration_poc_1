/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.math.BigDecimal;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * Sorting logic for parameters
 *
 * @author bne4cob
 */
public final class ParameterSorter<D extends IParameterAttribute, P extends IParameter> {


  /**
   * Constant for exact match-yes
   */
  static final String EXACT_MATCH_YES = "Yes";
  /**
   * Constant for exact match-no
   */
  static final String EXACT_MATCH_NO = "No";

  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";

  /**
   * Parameter properties columns
   */
  public enum SortColumns {
                           /**
                            * Parameter Name
                            */
                           SORT_PARAM_NAME,
                           /**
                            * Parameter Long Name
                            */
                           SORT_PARAM_LONGNAME,
                           /**
                            * Class
                            */
                           SORT_PARAM_CLASS,
                           /**
                            * CodeWord
                            */
                           SORT_PARAM_CODEWORD,
                           /**
                            * LowerLimit
                            */
                           SORT_PARAM_LOWERLIMIT,
                           /**
                            * Reference Value
                            */
                           SORT_PARAM_REFVALUE,
                           /**
                            * UpperLimit
                            */
                           SORT_PARAM_UPPERLIMIT,
                           /**
                            * UpperLimit
                            */
                           SORT_PARAM_BITWISE,
                           /**
                            * Ready for series
                            */
                           SORT_PARAM_READY_SERIES,

                           /**
                            * Unit
                            */
                           SORT_PARAM_UNIT,
                           // ICDM-1173
                           /**
                            * Exact match
                            */
                           SORT_EXACT_MATCH,

                           // ICDM-2152
                           /**
                            * Remarks
                            */
                           SORT_REMARKS;
  }

  private final ParameterDataProvider<D, P> paramDataProvider;

  /**
   * private constructor for utility class
   */
  public ParameterSorter(final ParameterDataProvider<D, P> paramDataProvider) {
    this.paramDataProvider = paramDataProvider;
  }

  /**
   * Compare using parameter name
   *
   * @param param1 IParameter 1
   * @param param2 IParameter 2
   * @return -1 or 0 or 1
   */
  public static int compare(final IParameter param1, final IParameter param2) {
    return ApicUtil.compare(param1.getName(), param2.getName());
  }

  /**
   * Compare using given sort column
   *
   * @param param1 IParameter 1
   * @param param2 IParameter 2
   * @param sortColumn sort Column
   * @return -1 or 0 or 1
   */
  public int compare(final IParameter param1, final IParameter param2, final SortColumns sortColumn) {

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
        compareResult = ApicUtil.compare(param1.getCodeWord(), param2.getCodeWord());
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
   */
  private int defaultSort(final IParameter param1, final IParameter param2, final SortColumns sortColumn) {
    int compareResult;
    // For sorting using the following columns
    // SORT_PARAM_LOWERLIMIT:
    // SORT_PARAM_UPPERLIMIT:
    // SORT_PARAM_REFVALUE:
    // SORT_PARAM_RVW_METHOD:
    // SORT_PARAM_UNIT:
    // SORT_EXACT_MATCH:
    compareResult = sortWithRuleCols(this.paramDataProvider.getReviewRule(param1),
        this.paramDataProvider.getReviewRule(param2), sortColumn);
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
  private int sortWithRuleCols(final ReviewRule param1Rule, final ReviewRule param2Rule, final SortColumns sortColumn) {

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
        compareResult = ApicUtil.compare(getReadyForSeriesUIVal(param1Rule), getReadyForSeriesUIVal(param2Rule));
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
  private static int sortRefVal(final ReviewRule param1Rule, final ReviewRule param2Rule) {
    int compareResult;
    BigDecimal ref1 = null;
    BigDecimal ref2 = null;
    if ((param1Rule != null) && VALUE_TEXT.equalsIgnoreCase(param1Rule.getValueType())) {
      ref1 = param1Rule.getRefValue();
    }
    if ((param2Rule != null) && VALUE_TEXT.equalsIgnoreCase(param2Rule.getValueType())) {
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
  private static int sortExactMatch(final ReviewRule param1Rule, final ReviewRule param2Rule) {
    int compareResult;
    String match1 = "";
    String match2 = "";

    if ((param1Rule != null) && !param1Rule.getRefValueDispString().isEmpty()) {
      match1 = param1Rule.isDcm2ssd() ? EXACT_MATCH_YES : EXACT_MATCH_NO;
    }
    if ((param2Rule != null) && !param2Rule.getRefValueDispString().isEmpty()) {
      match2 = param2Rule.isDcm2ssd() ? EXACT_MATCH_YES : EXACT_MATCH_NO;
    }

    compareResult = ApicUtil.compare(match1, match2);
    return compareResult;
  }

  /**
   * Icdm-500
   *
   * @param rule Cdr Rule
   * @return the Rule Ready for series Ui Value
   */
  private static String getReadyForSeriesUIVal(final ReviewRule rule) {
    if (rule != null) {
      if (ApicConstants.READY_FOR_SERIES.YES.dbType.equals(rule.getReviewMethod())) {
        return ApicConstants.READY_FOR_SERIES.YES.uiType;
      }
      else if (ApicConstants.READY_FOR_SERIES.NO.dbType.equals(rule.getReviewMethod())) {
        return ApicConstants.READY_FOR_SERIES.NO.uiType;
      }
    }
    return null;
  }
}
