package com.bosch.caltool.a2l.jpa.bo;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.common.util.ApicUtil;

/**
 * @author DMO5COB ICDM-204
 */
@Deprecated
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


  private final String bcName;

  private final String version;

  private final String revision;

  private final String state;

  private final String longName;
  // Icdm-949 Changed from Sorted Set to Map
  private final Map<String, A2LBaseComponentFunctions> functionMap;

  /**
   * Constructor with the name,long name , value and value descp.
   *
   * @param bcName -BC Name
   * @param version -Item version
   * @param revision -Item revision
   * @param state -State
   * @param longName - Long Name
   */
  public A2LBaseComponents(final String bcName, final String version, final String revision, final String state,
      final String longName) {

    this.bcName = bcName;
    this.version = version;
    this.revision = revision;
    this.state = state;
    this.longName = longName;
    // Icdm-949 Changed from Sorted Set to Map
    this.functionMap = new HashMap<String, A2LBaseComponentFunctions>();
  }

  /**
   * @return the bcName
   */
  public String getBcName() {
    return this.bcName;
  }


  /**
   * @return the version
   */
  public String getVersion() {
    return this.version;
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
    return new TreeSet<A2LBaseComponentFunctions>(this.functionMap.values());
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
        compareResult = ApicUtil.compare(getBcName(), a2lBC.getBcName());
        break;
      case SORT_VERSION:
        compareResult = ApicUtil.compare(getVersion(), a2lBC.getVersion());
        break;
      case SORT_REVISION:
        compareResult = ApicUtil.compare(getRevision(), a2lBC.getRevision());
        break;
      case SORT_STATE:
        compareResult = ApicUtil.compare(getState(), a2lBC.getState());
        break;
      case SORT_LONG_NAME:
        compareResult = ApicUtil.compare(getLongName(), a2lBC.getLongName());
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ApicUtil.compare(getBcName(), a2lBC.getBcName());
    }

    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LBaseComponents arg0) {
    return ApicUtil.compare(getBcName(), arg0.getBcName());
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
      if (a2lBaseCompFunc.getBcName() != null) {
        return false;
      }
      return true;
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
}