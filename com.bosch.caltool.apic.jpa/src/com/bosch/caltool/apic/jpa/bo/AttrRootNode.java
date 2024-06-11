package com.bosch.caltool.apic.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.IBasicObject;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents the root of all attributes/ attribute groups
 * 
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class AttrRootNode implements IBasicObject {

  /**
   * Data Provider
   */
  private final ApicDataProvider apicDataProvider;

  /**
   * Constructor
   * 
   * @param apicDataProvider ApicDataProvider
   */
  public AttrRootNode(final ApicDataProvider apicDataProvider) {
    this.apicDataProvider = apicDataProvider;
  }

  /**
   * @return the set of super groups
   */
  public SortedSet<AttrSuperGroup> getSuperGroups() {
    return new TreeSet<AttrSuperGroup>(this.apicDataProvider.getAllSuperGroups().values());
  }

  /**
   * @return the set of all attributes
   */
  public SortedSet<Attribute> getAllAttributes() {
    return new TreeSet<Attribute>(this.apicDataProvider.getAllAttributes().values());
  }

  /**
   * @return the name of the attribute root node
   */
  @Override
  public String getName() {
    return ApicConstants.ATTR_ROOT_NODE_NAME;
  }

  /**
   * iCDM-530
   * 
   * @return tooltip for root node
   */
  @Override
  public String getDescription() {
    return "Show all Attributes (No Filters)";
  }

  /**
   * @return tooltip of this node
   */
  @Override
  public String getToolTip() {
    return getDescription();
  }

}