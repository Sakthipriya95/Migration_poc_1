package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeSuperGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class represents the root of all attributes/ attribute groups
 *
 * @author dmo5cob
 */
public class AttrRootNode {

  // instance of attr group model
  private AttrGroupModel attrGroupModel;
  
  private String description = "Show all Attributes (No Filters)";
  
  /**
   * @param attrGroupModel attrGroupModel
   *
   */
  public AttrRootNode(AttrGroupModel attrGroupModel) {

    if (attrGroupModel == null) {
      fetchSuperGroupsAndGroups();
    }

    else {
      this.attrGroupModel = attrGroupModel;
    }

  }

  /**
   * Fetch groups and super groups
   */
  private void fetchSuperGroupsAndGroups() {

    AttributeSuperGroupServiceClient client = new AttributeSuperGroupServiceClient();
    try {
      this.attrGroupModel = client.getAttrGroupModel();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @return the attrGroupModel
   */
  public AttrGroupModel getAttrGroupModel() {
    return this.attrGroupModel;
  }

  /**
   * @return the set of super groups
   */
  public SortedSet<AttrSuperGroup> getSuperGroups() {
    return new TreeSet<>(this.attrGroupModel.getAllSuperGroupMap().values());
  }

  /**
   * Get the groups for a specific super group
   * 
   * @param superGroup AttrSuperGroup
   * @return SortedSet<AttrGroup>
   */
  public SortedSet<AttrGroup> getGroups(final AttrSuperGroup superGroup) {

    Set<Long> groupIds = this.attrGroupModel.getGroupBySuperGroupMap().get(superGroup.getId());
    SortedSet<AttrGroup> groups = new TreeSet<>();
    if (CommonUtils.isNotEmpty(groupIds)) {
      for (Long id : groupIds) {
        groups.add(this.attrGroupModel.getAllGroupMap().get(id));
      }
    }
    return groups;
  }

  /**
   * @return the name of the attribute root node
   */
  public String getName() {
    return ApicConstants.ATTR_ROOT_NODE_NAME;
  }

  /**
   * @return tooltip for root node
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return tooltip of this node
   */
  public String getToolTip() {
    return getDescription();
  }

  /**
   * Refresh based on the change
   *
   * @param changeData Change Data
   */
  public void refresh() {
    fetchSuperGroupsAndGroups();
  }


}