/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;


import java.util.SortedSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmo5cob
 */
public class AttrNValueDependencyClientBO implements Comparable<AttrNValueDependencyClientBO> {

  /**
   * Sort columns
   */
  public enum SortColumns {
                           /**
                            * Dependency attribute
                            */
                           SORT_ATTR_DEPEN_NAME,
                           /**
                            * Dependency value
                            */
                           SORT_ATTR_DEPEN_VAL
  }

  /**
   * AttrNValueDependency object
   */
  private final AttrNValueDependency attrNValDependency;


  /**
   * @param depn Attribute obj
   */
  public AttrNValueDependencyClientBO(final AttrNValueDependency depn) {
    this.attrNValDependency = depn;

  }

  /**
   * @return boolean
   */
  public boolean isDeleted() {
    return this.attrNValDependency.isDeleted();
  }

  /**
   * @return String
   */
  public String getName() {
    return this.attrNValDependency.getName();
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
        ModelUtil.isEqual(getAttrNValueDependency(), ((AttrNValueDependencyClientBO) obj).getAttrNValueDependency());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getAttrNValueDependency().getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttrNValueDependencyClientBO obj) {
    return this.attrNValDependency.compareTo(obj.getAttrNValueDependency());
  }

  /**
   * @return the attribute
   */
  public AttrNValueDependency getAttrNValueDependency() {
    return this.attrNValDependency;
  }

  /**
   * Gets the Attribute for which the dependency is created
   *
   * @return Attribute
   */
  public Attribute getAttribute() {
    AttributeServiceClient attrClient = new AttributeServiceClient();
    try {
      return attrClient.get(this.attrNValDependency.getAttributeId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * Gets the AttributeValue for which the dependency is created
   *
   * @return AttributeValue
   */
  public AttributeValue getAttributeValue() {
    AttributeValueServiceClient attrClient = new AttributeValueServiceClient();
    try {
      return attrClient.getById(this.attrNValDependency.getValueId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * Gets the dependant attribute for this dependencyID
   *
   * @return Attribute
   */
  public Attribute getDependencyAttribute() {
    AttributeServiceClient attrClient = new AttributeServiceClient();
    try {
      return attrClient.get(this.attrNValDependency.getDependentAttrId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * Get the attribute dependency value
   *
   * @return attribute dependency value
   */
  public AttributeValue getDependencyValue() {
    AttributeValueServiceClient attrClient = new AttributeValueServiceClient();
    try {
      if (this.attrNValDependency.getDependentValueId() != null) {
        return attrClient.getById(this.attrNValDependency.getDependentValueId());
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * ICDM-341 This method checks whether the attr dependency can be undeleted
   *
   * @return true/false
   */
  public boolean isUnDeleteAllowed() {
    AttributeClientBO attrBO = new AttributeClientBO(getAttribute());
    for (AttrNValueDependency attrDependency : attrBO.getAttrDependencies(false)) {
      // If any other valid dependency exists with another attribute then undelete is not allowed.
      if (!attrDependency.getName().equals(getName())) {
        return false;

      }
      // If any other valid dependency exists based on used flag then undelete is not allowed.
      if (CommonUtils.isEmptyString(attrDependency.getValue())) {
        return false;
      }
    }
    return true;
  }

  /**
   * ICDM-341 This method checks whether the value dependency can be undeleted
   *
   * @return true/false
   */
  public boolean isUnDeleteAllowedForValDependency() {

    AttributeValueClientBO valBO = new AttributeValueClientBO(getAttributeValue());
    SortedSet<AttrNValueDependency> listValDepn = valBO.getValueDependencies(false);
    for (AttrNValueDependency valDependency : listValDepn) {
      // If any other valid dependency exists with another attribute then undelete is not allowed.
      if (!valDependency.getName().equals(getDependencyAttribute().getName())) {
        return false;
      }
      // If any other valid dependency exists based on used flag then undelete is not allowed.
      if (valDependency.getDependentValueId() == null) {
        return false;
      }
    }
    return true;

  }

  /**
   * Compare with sort column
   *
   * @param arg0 other
   * @param sortColumn sortColumn
   * @return int -1/0/1
   */
  public int compareTo(final AttrNValueDependencyClientBO arg0, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_ATTR_DEPEN_VAL:
        compareResult = getAttrNValueDependency().getValue().compareTo(arg0.getAttrNValueDependency().getValue());
        break;
      case SORT_ATTR_DEPEN_NAME: // dependency attribute name needs not to be compared because it is the default sort
      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(arg0);
    }

    return compareResult;
  }

  /**
   * @return is attr enabled
   */
  public boolean isGrantAccessEnabled() {
    CurrentUserBO currentUser = new CurrentUserBO();
    try {
      if (currentUser.hasApicWriteAccess() || currentUser.hasNodeGrantAccess(getAttrNValueDependency().getId())) {
        return true;
      }
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }

    return false;
  }
}
