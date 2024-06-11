package com.bosch.caltool.icdm.model.a2l;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author DMO5COB ICDM-205, // ICDM-2627
 */
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
  private String sysconName;

  /**
   * value description in English
   */
  private String longNameEng;

  /**
   * Value description in German
   */
  private String longNameGer;

  /**
   * Long Name
   */
  private String longName;
  /**
   * Map of A2LSystemConstantValues
   * <p>
   * Key - (syskonName + value) object </br>
   * value - A2lSystemConstantValues
   */
  // ICDM-2627
  private ConcurrentMap<String, A2LSystemConstantValues> sysConValues = new ConcurrentHashMap<>();
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;


  /**
   * @return the sysconName
   */
  public String getSysconName() {
    return this.sysconName;
  }


  /**
   * @return the longName
   */
  public String getLongName() {
    return this.longName;
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
   * @return the sysConValues
   */
  public Map<String, A2LSystemConstantValues> getSysConValues() {
    return this.sysConValues;
  }


  /**
   * @param sysconName the sysconName to set
   */
  public void setSysconName(final String sysconName) {
    this.sysconName = sysconName;
  }


  /**
   * @param longNameEng the longNameEng to set
   */
  public void setLongNameEng(final String longNameEng) {
    this.longNameEng = longNameEng;
  }


  /**
   * @param longNameGer the longNameGer to set
   */
  public void setLongNameGer(final String longNameGer) {
    this.longNameGer = longNameGer;
  }


  /**
   * @param longName the longName to set
   */
  public void setLongName(final String longName) {
    this.longName = longName;
  }


  /**
   * @param sysConValues the sysConValues to set
   */
  public void setSysConValues(final ConcurrentMap<String, A2LSystemConstantValues> sysConValues) {
    this.sysConValues = sysConValues;
  }

  /**
   * Get the value with for the value key
   *
   * @param val value
   * @return the sysConValue object
   */
  // ICDM-2627
  public A2LSystemConstantValues getSysConValues(final String val) {
    return this.sysConValues.get(val);
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
        compareResult = ModelUtil.compare(getSysconName(), systemConst.getSysconName());
        break;
      case SORT_LONG_NAME:
        compareResult = ModelUtil.compare(getLongName(), systemConst.getLongName());
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ModelUtil.compare(getSysconName(), systemConst.getSysconName());
    }

    return compareResult;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LSystemConstant arg0) {
    return ModelUtil.compare(getSysconName(), arg0.getSysconName());
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
    return ModelUtil.isEqualIgnoreCase(getSysconName(), other.getSysconName());
  }
}