package com.bosch.caltool.a2l.jpa.bo;

import com.bosch.caltool.a2l.jpa.A2LDataProvider;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author DMO5COB ICDM-205
 */
@Deprecated
public class A2LSystemConstantValues implements Comparable<A2LSystemConstantValues> {

  /**
   * enum to declare the sort columns
   */
  public enum SortColumns {
                           /**
                            * Value column
                            */
                           SORT_VALUE,
                           /**
                            * Value Description column
                            */
                           SORT_VALUE_DESC
  }

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * enum to value types
   */
  public enum ValueType {
                         /**
                          * Number
                          */
                         NUMBER("Number"),
                         /**
                          * Text
                          */
                         TEXT("Text");

    private String text;


    ValueType(final String text) {
      this.text = text;
    }

    /**
     * @return text
     */
    public String getText() {
      return this.text;
    }

    /**
     * @param text : given text
     * @return v
     */
    public static ValueType getValueType(final String text) {
      for (ValueType v : ValueType.values()) {
        if (v.text.equals(text)) {
          return v;
        }
      }
      return TEXT;
    }
  }


  private final String value;

  private final String valueDescription;

  private ValueType valType;

  private final A2LSystemConstant sysConst;

  private final A2LDataProvider a2lDataProvider;


  /**
   * @return the sysConst
   */
  public A2LSystemConstant getSysConst() {
    return this.sysConst;
  }


  /**
   * Constructor with the name,long name , value and value descp.
   *
   * @param a2lDataProvider a2ldataprovider instance
   * @param newValue the value.
   * @param newValueDescrip the value description
   * @param sysConst A2lSystemConstant object
   */
  public A2LSystemConstantValues(final A2LDataProvider a2lDataProvider, final String newValue,
      final String newValueDescrip, final A2LSystemConstant sysConst) {

    this.sysConst = sysConst;
    this.value = newValue;
    this.valueDescription = newValueDescrip;
    if (isNumber(newValue)) {
      this.valType = ValueType.NUMBER;
    }
    else {
      this.valType = ValueType.TEXT;
    }
    this.a2lDataProvider = a2lDataProvider;
    this.a2lDataProvider.getAllSysConstValues().put(createUniqueKey(this.sysConst.getSysconName(), this.value), this);
  }


  /**
   * @param text
   * @return
   */
  private boolean isNumber(final String text) {
    if (text.matches("^[-+]?[0-9]*\\.?,?[0-9]+([eE][-+]?[0-9]+)?$")) {
      return true;
    }
    return false;

  }


  /**
   * @return the valueDescrip
   */
  public String getValueDescription() {
    return this.valueDescription;
  }


  /**
   * @return the valType
   */
  public ValueType getValType() {
    return this.valType;
  }


  /**
   * @return the value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * @param systemConst System Constant object
   * @param sortColumn column to be sorted
   * @return compareResult
   */

  public int compareTo(final A2LSystemConstantValues systemConst, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_VALUE:
        compareResult = compareStringAndNum(this, systemConst);
        break;
      case SORT_VALUE_DESC:
        compareResult = ApicUtil.compare(getValueDescription(), systemConst.getValueDescription());
        break;
      default:
        compareResult = 0;
        break;
    }


    return compareResult;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LSystemConstantValues arg0) {
    return ApicUtil.compare(getValue() + ":" + getSysConst().getSysconName(),
        arg0.getValue() + ":" + arg0.getSysConst().getSysconName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + (((getValue() == null) || (getSysConst() == null)) ? 0
        : (getValue() + ":" + getSysConst().getSysconName()).hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    A2LSystemConstantValues other = (A2LSystemConstantValues) obj;
    return CommonUtils.isEqualIgnoreCase(getValue() + ":" + getSysConst().getSysconName(),
        other.getValue() + ":" + other.getSysConst().getSysconName());
  }

  /**
   * @param sysKonName
   * @param syskonValue
   * @return
   */
  private String createUniqueKey(final String sysKonName, final String syskonValue) {
    return sysKonName + ":" + syskonValue;
  }

  /**
   * ICDM-66
   *
   * @param systemConstant
   * @param systemConst2
   * @return comparison result of string and double system constant values
   */

  private int compareStringAndNum(final A2LSystemConstantValues systemConst1,
      final A2LSystemConstantValues systemConst2) {

    if ((systemConst1.valType == ValueType.TEXT) && (systemConst2.valType == ValueType.TEXT)) {
      return ApicUtil.compare(systemConst1.getValue(), systemConst2.getValue());
    }
    else if ((systemConst1.valType == ValueType.NUMBER) && (systemConst2.valType == ValueType.NUMBER)) {
      return Double.valueOf(systemConst1.getValue()).compareTo(Double.valueOf(systemConst2.getValue()));
    }
    else if ((systemConst1.valType == ValueType.TEXT) && (systemConst2.valType == ValueType.NUMBER)) {
      return 1;
    }
    else if ((systemConst1.valType == ValueType.NUMBER) && (systemConst2.valType == ValueType.TEXT)) {
      return -1;
    }
    return 0;
  }


}