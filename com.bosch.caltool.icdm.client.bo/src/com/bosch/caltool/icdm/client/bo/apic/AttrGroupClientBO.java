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
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeSuperGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;

/**
 * @author dmo5cob
 */
public class AttrGroupClientBO implements Comparable<AttrGroupClientBO> {

  /**
   * AttrGroup object
   */
  private final AttrGroup group;


  /**
   * @return the group
   */
  public AttrGroup getGroup() {
    return this.group;
  }

  /**
   * @param superGrp AttrSuperGroup obj
   */
  public AttrGroupClientBO(final AttrGroup superGrp) {
    this.group = superGrp;

  }

  /**
   * @return boolean
   */
  public boolean isDeleted() {
    return this.group.isDeleted();
  }

  /**
   * @return String
   */
  public String getName() {
    return this.group.getName();
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getGroup(), ((AttrGroupClientBO) obj).getGroup());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getGroup().getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttrGroupClientBO obj) {
    return this.group.compareTo(obj.getGroup());
  }


  /**
   * @return the superGroup
   */
  public AttrSuperGroup getSuperGroup() {
    try {
      AttributeSuperGroupServiceClient client = new AttributeSuperGroupServiceClient();

      return client.get(this.group.getSuperGrpId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
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
      CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    return false;

  }

  /**
   * @return
   */
  public SortedSet<Link> getLinks() {
    LinkServiceClient linkClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkClient.getNodesWithLink(MODEL_TYPE.GROUP);
      boolean hasLinks = nodesWithLink.contains(getGroup().getId());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode =
            linkClient.getAllLinksByNode(getGroup().getId(), MODEL_TYPE.GROUP);
        return new TreeSet<>(allLinksByNode.values());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return new TreeSet<>();
  }


}
