package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author DMO5COB ICDM-204
 */
public class A2LBaseComponents implements Comparable<A2LBaseComponents> {


  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * enum to declare the sort columns
   */
  public enum SortColumns {
                           /**
                            * Name column
                            */
                           SORT_NAME,
                           /**
                            * Version column
                            */
                           SORT_VERSION,
                           /**
                            * Revision column
                            */
                           SORT_REVISION,
                           /**
                            * State column
                            */
                           SORT_STATE,
                           /**
                            * Description column
                            */
                           SORT_LONG_NAME

  }

  private String bcName;

  private String bcVersion;

  private String revision;

  private String state;

  private String longName;

  // Icdm-949 Changed from Sorted Set to Map
  private final Map<String, A2LBaseComponentFunctions> functionMap = new HashMap<>();

  /**
   * @return the bcName
   */
  public String getBcName() {
    return this.bcName;
  }

  /**
   * @return the revision
   */
  public String getRevision() {
    return this.revision;
  }


  /**
   * @return the state
   */
  public String getState() {
    return this.state;
  }


  /**
   * @return the longName
   */
  public String getLongName() {
    return this.longName;
  }


  /**
   * @return the functionsList
   */
  public SortedSet<A2LBaseComponentFunctions> getFunctionsList() {
    return new TreeSet<>(this.functionMap.values());
  }

  /**
   * Icdm-949 method for getting Func Map
   *
   * @return the function map create a map with Function name as Key and A2LBaseComponentFunctions as Value new Method
   *         for Outline Filter Optimization
   */
  public Map<String, A2LBaseComponentFunctions> getFunctionMap() {
    return this.functionMap;
  }


  /**
   * @param a2lBC System Constant object
   * @param sortColumn column to be sorted
   * @return compareResult
   */

  public int compareTo(final A2LBaseComponents a2lBC, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_NAME:
        compareResult = ModelUtil.compare(getBcName(), a2lBC.getBcName());
        break;
      case SORT_VERSION:
        compareResult = ModelUtil.compare(getBcVersion(), a2lBC.getBcVersion());
        break;
      case SORT_REVISION:
        compareResult = ModelUtil.compare(getRevision(), a2lBC.getRevision());
        break;
      case SORT_STATE:
        compareResult = ModelUtil.compare(getState(), a2lBC.getState());
        break;
      case SORT_LONG_NAME:
        compareResult = ModelUtil.compare(getLongName(), a2lBC.getLongName());
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ModelUtil.compare(getBcName(), a2lBC.getBcName());
    }

    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LBaseComponents arg0) {
    return ModelUtil.compare(getBcName(), arg0.getBcName());
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
    A2LBaseComponents a2lBaseCompFunc = (A2LBaseComponents) obj;
    if (getBcName() == null) {
      return null == a2lBaseCompFunc.getBcName();
    }
    return getBcName().equals(a2lBaseCompFunc.getBcName());
  }

  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getBcName() == null) ? 0 : getBcName().hashCode());
    return result;
  }

  /**
   * @return the bcVersion
   */
  public String getBcVersion() {
    return this.bcVersion;
  }


  /**
   * @param bcVersion the bcVersion to set
   */
  public void setBcVersion(final String bcVersion) {
    this.bcVersion = bcVersion;
  }

  /**
   * @param bcName the bcName to set
   */
  public void setBcName(final String bcName) {
    this.bcName = bcName;
  }


  /**
   * @param revision the revision to set
   */
  public void setRevision(final String revision) {
    this.revision = revision;
  }

  /**
   * @param state the state to set
   */
  public void setState(final String state) {
    this.state = state;
  }


  /**
   * @param longName the longName to set
   */
  public void setLongName(final String longName) {
    this.longName = longName;
  }


}