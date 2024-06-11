/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * @author rgo7cob
 */
@Deprecated
public class A2LGroup implements Comparable<A2LGroup> {

  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * @param groupName
   * @param groupLongName
   */
  public A2LGroup(final String groupName, final String groupLongName) {
    super();
    this.groupName = groupName;
    this.groupLongName = groupLongName;
  }


  /**
   * enum to declare the sort columns
   */
  public enum SortColumns {
                           /**
                            * Group Name column
                            */
                           SORT_GROUP_NAME,
                           /**
                            * Work Package Name
                            */
                           SORT_GROUP_LONG,
                           /**
                            * Work Package Number
                            */
                           SORT_NUM_REF

  }

  /**
   * @return the groupName
   */
  public String getGroupName() {
    return this.groupName;
  }


  /**
   * @param groupName the groupName to set
   */
  public void setGroupName(final String groupName) {
    this.groupName = groupName;
  }


  /**
   * @return the groupLongName
   */
  public String getGroupLongName() {
    return this.groupLongName;
  }


  /**
   * @param groupLongName the groupLongName to set
   */
  public void setGroupLongName(final String groupLongName) {
    this.groupLongName = groupLongName;
  }


  /**
   * @return the labelMap
   */
  public Map<String, String> getLabelMap() {
    return this.labelMap;
  }

  private String groupName;
  private String groupLongName;
  private Map<String, String> labelMap = new TreeMap<String, String>();
  private Map<String, List<A2LGroup>> subGrpMap = new HashMap<String, List<A2LGroup>>();
  private String rootName;


  /**
   * @return the rootName
   */
  public String getRootName() {
    return this.rootName;
  }


  /**
   * @param rootName the rootName to set
   */
  public void setRootName(final String rootName) {
    this.rootName = rootName;
  }


  /**
   * @return the subGrpMap
   */
  public Map<String, List<A2LGroup>> getSubGrpMap() {
    return this.subGrpMap;
  }


  /**
   * @param labelMap the labelMap to set
   */
  public void setLabelMap(final Map<String, String> labelMap) {
    this.labelMap = labelMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LGroup arg0) {
    return ApicUtil.compare(getGroupName(), arg0.getGroupName());
  }

  public int compareTo(final A2LGroup a2lgroup, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_GROUP_NAME:
        compareResult = ApicUtil.compare(getGroupName(), a2lgroup.getGroupName());
        break;
      case SORT_GROUP_LONG:
        compareResult = ApicUtil.compare(getGroupLongName(), a2lgroup.getGroupLongName());
        break;

      case SORT_NUM_REF:

        compareResult = ApicUtil.compareLong(getLabelMap().size(), a2lgroup.getLabelMap().size());

        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ApicUtil.compare(getGroupName(), a2lgroup.getGroupName());
    }

    return compareResult;

  }


  /**
   * @param subGrpMap subGrpMap
   */
  public void setSubGrpMap(final Map<String, List<A2LGroup>> subGrpMap) {
    this.subGrpMap = subGrpMap;
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
    A2LGroup a2lGroup = (A2LGroup) obj;
    if (getGroupName() == null) {
      if (a2lGroup.getGroupName() != null) {
        return false;
      }
      return true;
    }
    return getGroupName().equals(a2lGroup.getGroupName());
  }

  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getGroupName() == null) ? 0 : getGroupName().hashCode());
    return result;
  }

}
