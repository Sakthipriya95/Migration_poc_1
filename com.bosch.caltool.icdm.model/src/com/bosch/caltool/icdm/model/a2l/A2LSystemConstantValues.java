package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author DMO5COB ICDM-205
 */
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

    private final String text;


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


  private String value;

  private String valueDescription;

  private ValueType valType;

  private String sysconName;

  private String sysconLongName;

  /**
   * @param text
   * @return
   */
  private boolean isNumber(final String text) {
    return text.matches("^[-+]?[0-9]*\\.?,?[0-9]+([eE][-+]?[0-9]+)?$");
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
   * @param value the value to set
   */
  public void setValue(final String value) {
    if (isNumber(value)) {
      this.valType = ValueType.NUMBER;
    }
    else {
      this.valType = ValueType.TEXT;
    }
    this.value = value;
  }


  /**
   * @param valueDescription the valueDescription to set
   */
  public void setValueDescription(final String valueDescription) {
    this.valueDescription = valueDescription;
  }


  /**
   * @param valType the valType to set
   */
  public void setValType(final ValueType valType) {
    this.valType = valType;
  }


  /**
   * @return the sysconName
   */
  public String getSysconName() {
    return this.sysconName;
  }


  /**
   * @param sysconName the sysconName to set
   */
  public void setSysconName(final String sysconName) {
    this.sysconName = sysconName;
  }


  /**
   * @return the sysconLongName
   */
  public String getSysconLongName() {
    return this.sysconLongName;
  }


  /**
   * @param sysconLongName the sysconLongName to set
   */
  public void setSysconLongName(final String sysconLongName) {
    this.sysconLongName = sysconLongName;
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
        compareResult = ModelUtil.compare(getValueDescription(), systemConst.getValueDescription());
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
    return ModelUtil.compare(getValue() + ":" + getSysconName(), arg0.getValue() + ":" + arg0.getSysconName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) +
        (((getValue() == null) || (getSysconName() == null)) ? 0 : (getValue() + ":" + getSysconName()).hashCode());
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
    return ModelUtil.isEqualIgnoreCase(getValue() + ":" + getSysconName(),
        other.getValue() + ":" + other.getSysconName());
  }

  /**
   * @param sysKonName
   * @param syskonValue
   * @return
   */
  public String createUniqueKey(final String sysKonName, final String syskonValue) {
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
      return ModelUtil.compare(systemConst1.getValue(), systemConst2.getValue());
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