/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeSuperGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;

/**
 * @author dmo5cob
 */
public class AttrSuperGroupClientBO implements Comparable<AttrSuperGroupClientBO> {

  /**
   * AttrSuperGroup object
   */
  private final AttrSuperGroup superGroup;


  /**
   * @param superGrp AttrSuperGroup obj
   */
  public AttrSuperGroupClientBO(final AttrSuperGroup superGrp) {
    this.superGroup = superGrp;

  }

  /**
   * @return boolean
   */
  public boolean isDeleted() {
    return this.superGroup.isDeleted();
  }

  /**
   * @return String
   */
  public String getName() {
    return this.superGroup.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getSuperGroup(), ((AttrSuperGroupClientBO) obj).getSuperGroup());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getSuperGroup().getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttrSuperGroupClientBO obj) {
    return this.superGroup.compareTo(obj.getSuperGroup());
  }


  /**
   * @return the superGroup
   */
  public AttrSuperGroup getSuperGroup() {
    return this.superGroup;
  }

  /**
   * @return
   */
  public boolean isModifiable() {
    CurrentUserBO userBO = new CurrentUserBO();
    try {
      if (userBO.hasApicWriteAccess()) {
        return true;
      }
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    return false;

  }

  /**
   * @return SortedSet<AttrGroup>
   */
  public SortedSet<AttrGroup> getGroups() {
    AttributeSuperGroupServiceClient client = new AttributeSuperGroupServiceClient();
    SortedSet<AttrGroup> groups = new TreeSet<>();
    try {
      Set<Long> groupIds = client.getAttrGroupModel().getGroupBySuperGroupMap().get(getSuperGroup().getId());
      for (Long id : groupIds) {
        AttrGroupServiceClient grpClient = new AttrGroupServiceClient();
        groups.add(grpClient.getById(id));
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return groups;
  }

  /**
   * @return
   */
  public SortedSet<Link> getLinks() {
    LinkServiceClient linkClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkClient.getNodesWithLink(MODEL_TYPE.SUPER_GROUP);
      boolean hasLinks = nodesWithLink.contains(getSuperGroup().getId());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode =
            linkClient.getAllLinksByNode(getSuperGroup().getId(), MODEL_TYPE.SUPER_GROUP);
        return new TreeSet<>(allLinksByNode.values());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    return new TreeSet<>();
  }

}
