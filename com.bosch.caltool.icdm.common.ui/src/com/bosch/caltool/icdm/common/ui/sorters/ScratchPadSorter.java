package com.bosch.caltool.icdm.common.ui.sorters;

import java.util.Comparator;

import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author rgo7cob Icdm-707 Scrach pad Soter
 */
public class ScratchPadSorter implements Comparator<ScratchPadDataFetcher> {


  // enum for Value Types.
  enum ValueType {
                  A2L,
                  ATTR_VAL,
                  FUNCTION,
                  PIDC,
                  SUB_VARIANT,
                  VARIANT,
                  CALDATA,
                  CDRFUNC_PARAM
  }

  private static final int RETURN_LOW = -1;
  private static final int RETURN_HIGH = 1;


  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(final ScratchPadDataFetcher data1, final ScratchPadDataFetcher data2) {

    ValueType scratchObjType1 = readInnerObj(data1);
    ValueType scratchObjType2 = readInnerObj(data2);


    if ((scratchObjType1 != null) && (scratchObjType2 != null)) {
      switch (scratchObjType1) {
        case CDRFUNC_PARAM:
          return compareForFuncParam(data1, data2, scratchObjType2);
        // Compare A2l File names
        case A2L:
          return compareForA2l(data1, data2, scratchObjType2);
        // Compare Attr Value
        case ATTR_VAL:
          return compareForAttrVal(data1, data2, scratchObjType2);
        // compare Function
        case FUNCTION:
          return compareForFunctions(data1, data2, scratchObjType2);
        // compare Pidc
        case PIDC:
          return compareForPidcVersion(data1, data2, scratchObjType2);
        // compare Sub Variant
        case SUB_VARIANT:
          return compareForSubVariant(data1, data2, scratchObjType2);
        // compare Variant
        case VARIANT:
          return compareForVariant(data1, data2, scratchObjType2);
        // compare Cal Data
        case CALDATA:
          return compareforCalData(data1, data2, scratchObjType2);
        default:
          return 0;
      }
    }
    return 0;


  }

  /**
   * @param data1
   * @param data2
   * @param scratchObjType2
   * @return
   */
  private int compareForFuncParam(final ScratchPadDataFetcher data1, final ScratchPadDataFetcher data2,
      final ValueType scratchObjType2) {
    if ((scratchObjType2 == ValueType.VARIANT) || (scratchObjType2 == ValueType.CALDATA) ||
        (scratchObjType2 == ValueType.SUB_VARIANT) || (scratchObjType2 == ValueType.PIDC) ||
        (scratchObjType2 == ValueType.FUNCTION) || (scratchObjType2 == ValueType.ATTR_VAL)) {
      return RETURN_LOW;
    }
    return ApicUtil.compare(data1.getParameter(), data2.getParameter());
  }

  /**
   * @param data1 obj1
   * @param data2 obj2
   * @param scratchObjType2 obj2 type
   * @return
   */
  private int compareforCalData(final ScratchPadDataFetcher data1, final ScratchPadDataFetcher data2,
      final ValueType scratchObjType2) {
    // If other objects return 1
    if ((scratchObjType2 == ValueType.A2L) || (scratchObjType2 == ValueType.ATTR_VAL) ||
        (scratchObjType2 == ValueType.FUNCTION) || (scratchObjType2 == ValueType.PIDC) ||
        (scratchObjType2 == ValueType.SUB_VARIANT) || (scratchObjType2 == ValueType.VARIANT) ||
        (scratchObjType2 == ValueType.CDRFUNC_PARAM)) {
      return RETURN_HIGH;
    }
    // else compare the Cal Data Objects
    int compare = ApicUtil.compare(data1.getSeriesStatsInfo().getCalData().getShortName(),
        data2.getSeriesStatsInfo().getCalData().getShortName());
    if (compare == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // Second level with Value Type
      compare = ApicUtil.compare(data1.getSeriesStatsInfo().getCalDataPhyValType().getLabel(),
          data2.getSeriesStatsInfo().getCalDataPhyValType().getLabel());
    }
    return compare;
  }

  /**
   * @param data1 obj1
   * @param data2 obj2
   * @param scratchObjType2 obj2 type
   * @return
   */
  private int compareForVariant(final ScratchPadDataFetcher data1, final ScratchPadDataFetcher data2,
      final ValueType scratchObjType2) {
    if (scratchObjType2 == ValueType.CALDATA) {
      return RETURN_LOW;
    }
    // If other objects return 1
    if ((scratchObjType2 == ValueType.A2L) || (scratchObjType2 == ValueType.ATTR_VAL) ||
        (scratchObjType2 == ValueType.FUNCTION) || (scratchObjType2 == ValueType.PIDC) ||
        (scratchObjType2 == ValueType.SUB_VARIANT) || (scratchObjType2 == ValueType.CDRFUNC_PARAM)) {
      return RETURN_HIGH;
    }
    // else compare the Varaint Objects
    return ApicUtil.compare(data1.getPidcVariant().getName(), data2.getPidcVariant().getName());
  }

  /**
   * @param data1 obj1
   * @param data2 obj2
   * @param scratchObjType2 obj2 type
   * @return
   */
  private int compareForSubVariant(final ScratchPadDataFetcher data1, final ScratchPadDataFetcher data2,
      final ValueType scratchObjType2) {
    if ((scratchObjType2 == ValueType.VARIANT) || (scratchObjType2 == ValueType.CALDATA)) {
      return RETURN_LOW;
    }
    // If other objects return 1
    if ((scratchObjType2 == ValueType.A2L) || (scratchObjType2 == ValueType.ATTR_VAL) ||
        (scratchObjType2 == ValueType.FUNCTION) || (scratchObjType2 == ValueType.PIDC) ||
        (scratchObjType2 == ValueType.CDRFUNC_PARAM)) {
      return RETURN_HIGH;
    }
    // else compare the Sub Varaint Objects
    return ApicUtil.compare(data1.getPidcSubVariant().getName(), data2.getPidcSubVariant().getName());
  }

  /**
   * @param data1 obj1
   * @param data2 obj2
   * @param scratchObjType2 obj2 type
   * @return
   */
  private int compareForPidcVersion(final ScratchPadDataFetcher data1, final ScratchPadDataFetcher data2,
      final ValueType scratchObjType2) {
    // If other objects return 1
    if ((scratchObjType2 == ValueType.VARIANT) || (scratchObjType2 == ValueType.CALDATA) ||
        (scratchObjType2 == ValueType.SUB_VARIANT)) {
      return RETURN_LOW;
    }
    if ((scratchObjType2 == ValueType.A2L) || (scratchObjType2 == ValueType.ATTR_VAL) ||
        (scratchObjType2 == ValueType.FUNCTION) || (scratchObjType2 == ValueType.CDRFUNC_PARAM)) {
      return RETURN_HIGH;
    }
    // else compare the Pidc Objects
    return ApicUtil.compare(data1.getPidcVersion().getName(), data2.getPidcVersion().getName());
  }

  /**
   * @param data1 obj1
   * @param data2 obj2
   * @param scratchObjType2 obj2 type
   * @return
   */
  private int compareForFunctions(final ScratchPadDataFetcher data1, final ScratchPadDataFetcher data2,
      final ValueType scratchObjType2) {
    if ((scratchObjType2 == ValueType.VARIANT) || (scratchObjType2 == ValueType.CALDATA) ||
        (scratchObjType2 == ValueType.SUB_VARIANT) || (scratchObjType2 == ValueType.PIDC)) {
      return RETURN_LOW;
    }
    // If other objects return 1
    if ((scratchObjType2 == ValueType.A2L) || (scratchObjType2 == ValueType.ATTR_VAL) ||
        (scratchObjType2 == ValueType.CDRFUNC_PARAM)) {
      return RETURN_HIGH;
    }
    // Compare Function objects
    return ApicUtil.compare(data1.getFunction().getName(), data2.getFunction().getName());
  }

  /**
   * @param data1 obj1
   * @param data2 obj2
   * @param scratchObjType2 obj2 type
   * @return
   */
  private int compareForAttrVal(final ScratchPadDataFetcher data1, final ScratchPadDataFetcher data2,
      final ValueType scratchObjType2) {
    // If other objects return 1
    if ((scratchObjType2 == ValueType.VARIANT) || (scratchObjType2 == ValueType.CALDATA) ||
        (scratchObjType2 == ValueType.SUB_VARIANT) || (scratchObjType2 == ValueType.PIDC) ||
        (scratchObjType2 == ValueType.FUNCTION)) {
      return RETURN_LOW;
    }
    if ((scratchObjType2 == ValueType.A2L) || (scratchObjType2 == ValueType.CDRFUNC_PARAM)) {
      return RETURN_HIGH;
    }
    // Compare Attr Val
    return ApicUtil.compare(data1.getAttrVal(), data2.getAttrVal());
  }

  /**
   * @param data1 obj1
   * @param data2 obj2
   * @param scratchObjType2 obj2 type
   * @return
   */
  private int compareForA2l(final ScratchPadDataFetcher data1, final ScratchPadDataFetcher data2,
      final ValueType scratchObjType2) {
    if ((scratchObjType2 == ValueType.VARIANT) || (scratchObjType2 == ValueType.CALDATA) ||
        (scratchObjType2 == ValueType.SUB_VARIANT) || (scratchObjType2 == ValueType.PIDC) ||
        (scratchObjType2 == ValueType.FUNCTION) || (scratchObjType2 == ValueType.ATTR_VAL)) {
      return RETURN_LOW;
    }
    if (scratchObjType2 == ValueType.CDRFUNC_PARAM) {
      return RETURN_HIGH;
    }
    return ApicUtil.compare(data1.getPidcA2l().getName(), data2.getPidcA2l().getName());
  }

  /**
   * Check the Internal Object Stored to find its type.
   *
   * @param scratchPadObj1
   */
  private ValueType readInnerObj(final ScratchPadDataFetcher scratchPadObj1) {
    ValueType objectType = null;

    if (scratchPadObj1.getParameter() != null) {
      objectType = ValueType.CDRFUNC_PARAM;
    }
    if (scratchPadObj1.getPidcA2l() != null) {
      objectType = ValueType.A2L;

    }
    if (scratchPadObj1.getAttrVal() != null) {
      objectType = ValueType.ATTR_VAL;

    }
    if (scratchPadObj1.getFunction() != null) {
      objectType = ValueType.FUNCTION;

    }
    if (scratchPadObj1.getPidcVersion() != null) {
      objectType = ValueType.PIDC;

    }
    if (scratchPadObj1.getPidcSubVariant() != null) {
      objectType = ValueType.SUB_VARIANT;

    }
    if (scratchPadObj1.getPidcVariant() != null) {
      objectType = ValueType.VARIANT;

    }
    if (scratchPadObj1.getSeriesStatsInfo() != null) {
      objectType = ValueType.CALDATA;

    }
    return objectType;
  }

}