package com.bosch.caltool.a2l.jpa.bo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.a2l.jpa.A2LDataProvider;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author DMO5COB ICDM-205
 */
@Deprecated
public class A2LSystemConstant implements Comparable<A2LSystemConstant> {

  /**
   * enum to declare the sort columns
   */
  public enum SortColumns {
                           /**
                            * Name column
                            */
                           SORT_NAME,
                           /**
                            * Description column
                            */
                           SORT_LONG_NAME

  }

  /**
   * value Name
   */
  private final String sysconName;

  /**
   * value description in English
   */
  private final String longNameEng;

  /**
   * Value description in German
   */
  private final String longNameGer;

  /**
   * Map of value objects
   * <p>
   * Key - value as string<br>
   * Value - value object
   */
  // ICDM-2627
  private final ConcurrentMap<String, A2LSystemConstantValues> sysConValues = new ConcurrentHashMap<>();

  /**
   * Data provider
   */
  private final A2LDataProvider a2lDataProvider;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * Constructor with the name,long name , value and value descp.
   *
   * @param a2lDataProvider a2lDataProvider instance
   * @param newName the name.
   * @param newLongNameEng long name english
   * @param newLongNameGer long name german
   */
  public A2LSystemConstant(final A2LDataProvider a2lDataProvider, final String newName, final String newLongNameEng,
      final String newLongNameGer) {

    this.sysconName = newName;
    this.longNameEng = newLongNameEng;
    this.longNameGer = newLongNameGer;
    this.a2lDataProvider = a2lDataProvider;

  }

  /**
   * @return the a2lDataProvider
   */
  public A2LDataProvider getA2lDataProvider() {
    return this.a2lDataProvider;
  }


  /**
   * @return the sysconName
   */
  public String getSysconName() {
    return this.sysconName;
  }


  /**
   * @return the sysConValues
   */
  public Map<String, A2LSystemConstantValues> getSysConValues() {
    return this.sysConValues;
  }

  /**
   * Get the value with for the value key
   *
   * @param val value
   * @return the sysConValue object
   */
  // ICDM-2627
  public A2LSystemConstantValues getSysConValue(final String val) {
    return this.sysConValues.get(val);
  }

  /**
   * Add the value
   *
   * @param valObj value object
   * @return the sysConValue object
   */
  // ICDM-2627
  public A2LSystemConstantValues addSysConValue(final A2LSystemConstantValues valObj) {
    return this.sysConValues.put(valObj.getValue(), valObj);
  }


  /**
   * @return the longName
   */
  public String getLongName() {

    return ApicUtil.getLangSpecTxt(this.a2lDataProvider.getLanguage(), getLongNameEng(), getLongNameGer(),
        ApicConstants.EMPTY_STRING).trim();
  }

  /**
   * Returns the Long Name in ENGLISH
   *
   * @return longNameEng
   */
  public String getLongNameEng() {
    return this.longNameEng;
  }

  /**
   * Returns the Long Name in GERMAN
   *
   * @return longNameGer
   */
  public String getLongNameGer() {
    return this.longNameGer;


  }


  /**
   * @param systemConst System Constant object
   * @param sortColumn column to be sorted
   * @return compareResult
   */

  public int compareTo(final A2LSystemConstant systemConst, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_NAME:
        compareResult = ApicUtil.compare(getSysconName(), systemConst.getSysconName());
        break;
      case SORT_LONG_NAME:
        compareResult = ApicUtil.compare(getLongName(), systemConst.getLongName());
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ApicUtil.compare(getSysconName(), systemConst.getSysconName());
    }

    return compareResult;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LSystemConstant arg0) {
    return ApicUtil.compare(getSysconName(), arg0.getSysconName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getSysconName() == null) ? 0 : getSysconName().hashCode());
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
    A2LSystemConstant other = (A2LSystemConstant) obj;
    return CommonUtils.isEqualIgnoreCase(getSysconName(), other.getSysconName());
  }
}