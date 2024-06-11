/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrNValueDependencyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeSuperGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.CharacteristicServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;

/**
 * @author dmo5cob
 */
public class AttributeClientBO implements Comparable<AttributeClientBO> {

  private NodeAccessDetails nodeAccessWithUserInfo;
  /**
   * Attribute object
   */
  private final Attribute attribute;


  /**
   * @param attribute Attribute obj
   */
  public AttributeClientBO(final Attribute attribute) {
    this.attribute = attribute;

  }

  /**
   * @return boolean
   */
  public boolean isDeleted() {
    return this.attribute.isDeleted();
  }

  /**
   * @return String
   */
  public String getName() {
    return this.attribute.getName();
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
        ModelUtil.isEqual(getAttribute(), ((AttributeClientBO) obj).getAttribute());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getAttribute().getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttributeClientBO obj) {
    return this.attribute.compareTo(obj.getAttribute());
  }

  /**
   * @return the attribute
   */
  public Attribute getAttribute() {
    return this.attribute;
  }

  /**
   * Check, if the current user is allowed to modify values of the Attribute. Modify includes Create, Update and mark as
   * deleted
   *
   * @return TRUE if user is allowed to modify
   */
  public boolean canModifyValues() {

    CurrentUserBO currentUser = new CurrentUserBO();
    try {
      if (currentUser.hasApicWriteAccess() || currentUser.hasNodeWriteAccess(getAttribute().getId())) {
        return true;
      }
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    return false;

  }

  /**
   * ICDM-120
   *
   * @param refPidcAttributes Reference PIDC attributes
   * @param pidcDataHandler pidcDataHandler
   * @return valid values
   */
  public AttributeValueWithDetails getValidAttrValuesWithDetails(final Map<Long, IProjectAttribute> refPidcAttributes,
      final PidcDataHandler pidcDataHandler) {

    final Set<AttributeValue> valueList = getAttrValues();
    final Set<AttributeValue> resultList = new TreeSet<>();
    AttributeValueWithDetails attrValWithDetails = new AttributeValueWithDetails();
    Map<Long, Set<AttrNValueDependency>> valAndValDepMap = getValDependencyMap(getAttribute().getId());
    for (AttributeValue attributeValue : valueList) {
      AttributeValueClientBO attributeValueClientBO = new AttributeValueClientBO(attributeValue);
      boolean validValue = attributeValueClientBO.isValidValue(refPidcAttributes, pidcDataHandler,
          valAndValDepMap.get(attributeValue.getId()));
      attrValWithDetails.getValidValMap().put(attributeValue.getId(), validValue);

      resultList.add(attributeValue);
    }
    attrValWithDetails.setAttrValset(resultList);

    return attrValWithDetails;

  }

  /**
   * @param attrId attribute id
   * @return key - attrValId , value - set of AttrNValueDependency
   */
  public Map<Long, Set<AttrNValueDependency>> getValDependencyMap(final Long attrId) {
    AttributeValueServiceClient attrValClient = new AttributeValueServiceClient();
    try {
      return attrValClient.getValueDependecyMap(attrId);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * @return SortedSet<AttributeValue>
   */
  public SortedSet<AttributeValue> getAttrValues() {
    SortedSet<AttributeValue> valSet =
        Collections.synchronizedSortedSet(new TreeSet<com.bosch.caltool.icdm.model.apic.attr.AttributeValue>());
    Map<Long, AttributeValue> mapOfVals = getAttrValuesMap();
    if (CommonUtils.isNotEmpty(mapOfVals)) {
      valSet.addAll(mapOfVals.values());
    }
    return valSet;
  }

  /**
   * iCDM-1345 Check if the attribute is dependending on othe attributes
   *
   * @param projObjBO
   * @param attributeId
   * @return true if has dependencies
   */
  public boolean hasReferentialDependencies(final AbstractProjectObjectBO projObjBO, final Long attributeId) {
    Set<AttrNValueDependency> depenAttr = projObjBO.getPidcDataHandler().getAttrRefDependenciesMap().get(attributeId);

    return CommonUtils.isNotEmpty(depenAttr);

  }


  /**
   * @return Values map
   */
  public Map<Long, AttributeValue> getAttrValuesMap() {
    try {

      AttributeValueServiceClient attributeValueServiceClient = new AttributeValueServiceClient();
      Map<Long, Map<Long, AttributeValue>> resultMap =
          attributeValueServiceClient.getValuesByAttribute(getAttribute().getId());
      return resultMap.get(getAttribute().getId());

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);

    }
    return null;
  }

  /**
   * @return AttrSuperGroup
   */
  public AttrSuperGroup getSuperGroup() {
    try {
      AttributeSuperGroupServiceClient client = new AttributeSuperGroupServiceClient();

      return client.get(getAttributeGroup().getSuperGrpId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * Get all DEPENDENCIES defined for this attribute, pass parameter to include deleted items
   *
   * @param includeDeleted - if TRUE includes items which are even marked as deleted, FALSE otherwise
   * @return SortedSet<AttrNValueDependency>
   */
  public SortedSet<AttrNValueDependency> getAttrDependencies(final boolean includeDeleted) {

    AttrNValueDependencyServiceClient depnServiceClient = new AttrNValueDependencyServiceClient();
    SortedSet<AttrNValueDependency> depSet = Collections.synchronizedSortedSet(new TreeSet<AttrNValueDependency>());
    try {

      Set<AttrNValueDependency> dependenciesByAttribute =
          depnServiceClient.getDependenciesByAttribute(getAttribute().getId());
      if (includeDeleted) {
        // add all dependencies which are also marked as deleted
        depSet.addAll(dependenciesByAttribute);
      }
      else {
        for (AttrNValueDependency attrNValueDependency : dependenciesByAttribute) {
          // check if the dependency is deleted
          if (!attrNValueDependency.isDeleted()) {
            // add only not deleted dependencies to the results list
            depSet.add(attrNValueDependency);
          }
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return depSet;
  }


  /**
   * @return dependencies as string
   */
  public String getDependencies(final PidcDataHandler dataHandler) {

    StringBuilder depStr = new StringBuilder();

    int countr;

    Set<AttrNValueDependency> attrDepList = dataHandler.getAttrDependenciesMap().get(this.attribute.getId());
    Map<Long, AttributeValue> attrValDepMap = dataHandler.getAttributeDepValueMap();

    if (CommonUtils.isNotEmpty(attrDepList)) {

      depStr.append("[ ");

      countr = 0;
      for (AttrNValueDependency dependency : attrDepList) {
        countr++;

        if (countr > 1) {
          depStr.append(" [OR] ");
        }

        depStr.append(dataHandler.getAttributeMap().get(dependency.getDependentAttrId()).getName()).append(" = ");
        if ((dependency.getDependentValueId() != null) &&
            (attrValDepMap.get(dependency.getDependentValueId()) != null)) {
          depStr.append(attrValDepMap.get(dependency.getDependentValueId()).getDescription());
        }
        else {
          depStr.append(dependency.getValue());
        }

      }
      depStr.append(" ]");
    }

    return depStr.toString();
  }

  /**
   * Check, if the Attribute is grouped attribute
   *
   * @return TRUE, if the Attribute is a grouped attr
   */
  public boolean isGrouped() {
    return getAttribute().isGroupedAttr();
  }

  /**
   * @return AttrGroup
   */
  public AttrGroup getAttributeGroup() {
    try {
      AttrGroupServiceClient grpClient = new AttrGroupServiceClient();

      return grpClient.getById(getAttribute().getAttrGrpId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;

  }

  /**
   * @return is attr enabled
   */
  public boolean isGrantAccessEnabled() {
    CurrentUserBO currentUser = new CurrentUserBO();
    try {
      if (currentUser.hasApicWriteAccess() || currentUser.hasNodeGrantAccess(getAttribute().getId())) {
        return true;
      }
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }

    return false;
  }

  /**
   * Get a sorted set of the Attribute
   *
   * @return NodeAccessRight
   */
  public SortedSet<NodeAccess> getAccessRights() {
    NodeAccessServiceClient client = new NodeAccessServiceClient();

    SortedSet<NodeAccess> set = new TreeSet<>();
    try {
      NodeAccessDetails ret = client.getNodeAccessDetailsByNode(MODEL_TYPE.ATTRIBUTE, getAttribute().getId());
      setNodeAccessWithUserInfo(ret);
      if (CommonUtils.isNotEmpty(ret.getNodeAccessMap())) {
        for (Entry<Long, Set<NodeAccess>> entry : ret.getNodeAccessMap().entrySet()) {
          set.addAll(entry.getValue());
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error("Error loading  Node access Data for  :" + getAttribute().getName(), e);
    }
    return set;
  }

  /**
   * Check, if the current user is allowed to modify dependencies of the Attribute. Modify includes Create, Update and
   * mark as deleted
   *
   * @return TRUE if user is allowed to modify
   */
  public boolean canModifyDependencies() {
    CurrentUserBO currentUser = new CurrentUserBO();
    try {
      if (currentUser.hasApicWriteAccess() ||
          (currentUser.hasNodeWriteAccess(getAttribute().getId()) && this.attribute.isNormalized())) {
        return true;
      }
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }

    return false;

  }

  /**
   * @param arg0 other
   * @param sortColumn sort column
   * @return compare result based on sort column
   */
  public int compareTo(final AttributeClientBO arg0, final int sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
        // attribute name needs not to be compared because it is the default sort
        compareResult = 0;
        break;

      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(getAttribute().getDescription(), arg0.getAttribute().getDescription());
        break;

      case ApicConstants.SORT_LEVEL:
        compareResult = ApicUtil.compareLong(getAttribute().getLevel(), arg0.getAttribute().getLevel());
        break;

      case ApicConstants.SORT_UNIT:
        compareResult = ApicUtil.compare(getAttribute().getUnit(), arg0.getAttribute().getUnit());
        break;

      case ApicConstants.SORT_VALUETYPE:
        compareResult = ApicUtil.compare(getAttribute().getValueType(), arg0.getAttribute().getValueType());
        break;
      // ICDM-179
      case ApicConstants.SORT_MANDATORY:
        // compare only boolean value information
        compareResult = ApicUtil.compareBoolean(getAttribute().isMandatory(), arg0.getAttribute().isMandatory());
        break;
      // ICDM-860
      case ApicConstants.SORT_NORMALIZED_FLAG:
        compareResult = ApicUtil.compareBoolean(getAttribute().isNormalized(), arg0.getAttribute().isNormalized());
        break;
      case ApicConstants.SORT_FORMAT:
        compareResult = ApicUtil.compare(getAttribute().getFormat(), arg0.getAttribute().getFormat());
        break;
      case ApicConstants.SORT_PART_NUMBER:
        compareResult =
            ApicUtil.compareBoolean(getAttribute().isWithPartNumber(), arg0.getAttribute().isWithPartNumber());
        break;
      case ApicConstants.SORT_SPEC_LINK:
        compareResult = ApicUtil.compareBoolean(getAttribute().isWithSpecLink(), arg0.getAttribute().isWithSpecLink());
        break;
      // ICdm-480
      case ApicConstants.SORT_ATTR_SEC:
        compareResult = ApicUtil.compareBoolean(getAttribute().isExternal(), arg0.getAttribute().isExternal());
        break;
      case ApicConstants.SORT_ATTR_VAL_SEC:
        compareResult =
            ApicUtil.compareBoolean(getAttribute().isExternalValue(), arg0.getAttribute().isExternalValue());
        break;
      case ApicConstants.SORT_CHAR:
        compareResult = ApicUtil.compare(getAttribute().getCharStr(), arg0.getAttribute().getCharStr());
        break;
      // ICDM-1560
      case ApicConstants.SORT_ATTR_EADM_NAME:
        compareResult = ApicUtil.compare(getAttribute().getEadmName(), arg0.getAttribute().getEadmName());
        break;
      case ApicConstants.SORT_ATTR_CREATED_DATE_PIDC:
        compareResult = ApicUtil.compare(getAttribute().getCreatedDate(), arg0.getAttribute().getCreatedDate());
        break;
      default:
        compareResult = ApicConstants.OBJ_EQUAL_CHK_VAL;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(arg0);
    }

    return compareResult;
  }


  /**
   * @return the nodeAccessWithUserInfo
   */
  public NodeAccessDetails getNodeAccessWithUserInfo() {
    return this.nodeAccessWithUserInfo;
  }


  /**
   * @param nodeAccessWithUserInfo the nodeAccessWithUserInfo to set
   */
  public void setNodeAccessWithUserInfo(final NodeAccessDetails nodeAccessWithUserInfo) {
    this.nodeAccessWithUserInfo = nodeAccessWithUserInfo;
  }

  /**
   * Check, if the Attribute has been defined as normalized A normalized attribute can have only pre-defined values
   *
   * @return TRUE, if the Attribute has been marked as normalized
   */
  public boolean isNormalized() {
    return this.attribute.isNormalized();
  }

  /**
   * @return true if the Attribute has characteristic
   */
  public boolean hasCharacteristic() {
    return getCharacteristic() != null;
  }


  /**
   * Icdm-830 Data Model changes for New Column Clearing status
   *
   * @return the cleared Value List
   */
  public List<AttributeValue> getUnclearedValues() {

    final List<AttributeValue> unclearedValList = new ArrayList<AttributeValue>();
    for (AttributeValue attributeValue : getAttrValues()) {
      AttributeValueClientBO attributeValueClientBO = new AttributeValueClientBO(attributeValue);
      if (!attributeValueClientBO.isCleared()) {
        unclearedValList.add(attributeValue);
      }
    }
    return unclearedValList;
  }

  /**
   * Icdm-954 Get the Unit defined for the Attribute
   *
   * @return the Unit defined for the Attribute
   */
  public Characteristic getCharacteristic() {
    if (null != getAttribute().getCharacteristicId()) {
      // call the service only when there is a characteristic id
      try {
        CharacteristicServiceClient servClient = new CharacteristicServiceClient();
        return servClient.getById(getAttribute().getCharacteristicId());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return null;
  }

  /**
   * @return SortedSet<Link>
   */
  public SortedSet<Link> getLinks() {
    LinkServiceClient linkClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkClient.getNodesWithLink(MODEL_TYPE.ATTRIBUTE);
      boolean hasLinks = nodesWithLink.contains(getAttribute().getId());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode =
            linkClient.getAllLinksByNode(getAttribute().getId(), MODEL_TYPE.ATTRIBUTE);
        return new TreeSet<>(allLinksByNode.values());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return new TreeSet<>();
  }

  /**
   * Check if the attribute is valid (visible) based on the attribute dependencies. This method assumes: - that an
   * attribute depends on only one attribute - that if an attribute depends on the used flag, only one dependency is
   * defined
   *
   * @param refPidcAttributes the list of attributes defined in the PIDC
   * @return true if the attr is valid
   */
  public <P extends IProjectAttribute> boolean isValid(final Map<Long, P> refPidcAttributes, final Long attrId,
      final PidcDataHandler pidcDataHandler) {

    // the list of all dependencies of this attribute
    AttributeClientBO attrClientBo = new AttributeClientBO(pidcDataHandler.getAttributeMap().get(attrId));

    SortedSet<AttrNValueDependency> dependencies = attrClientBo.getAttrDependencies(false);

    if (dependencies.isEmpty()) {
      // no dependencies
      return true;
    }
    // get the attribute on which this attribute depends on
    final Long dependencyAttrID = dependencies.first().getDependentAttrId();

    // check, if the dependency attribute is deleted
    if (pidcDataHandler.getAttributeMap().get(dependencyAttrID).isDeleted()) {
      // the dependency attribute is deleted
      // thus the attribute is visible
      return true;
    }

    PidcVersionAttribute tempAttr =
        pidcDataHandler.getPidcVersionInfo().getLevelAttrMap().get(ApicConstants.PROJECT_NAME_ATTR);
    boolean equalsProjName = false;
    if (tempAttr != null) {

      equalsProjName = pidcDataHandler.getAttributeMap().get(dependencyAttrID).getId().equals(tempAttr.getAttrId());
    }

    // ICDM-196 null check placed if the depency is at higher level
    // ICDM-963
    if (((refPidcAttributes.get(dependencyAttrID) == null) ||
        refPidcAttributes.get(dependencyAttrID).isAtChildLevel()) && !equalsProjName) {
      return true;
    }


    // check, if attribute depends on the used flag
    // ICDM-962


    PidcVersionBO pidcVersionBO =
        new PidcVersionBO(pidcDataHandler.getPidcVersionInfo().getPidcVersion(), pidcDataHandler);

    PidcVersionAttributeBO pidcVersionAttrHandler =
        new PidcVersionAttributeBO((PidcVersionAttribute) refPidcAttributes.get(dependencyAttrID), pidcVersionBO);

    if ((dependencies.first().getDependentValueId() == null) && pidcVersionAttrHandler.isVisible()) {
      // attribute depends on the used flag
      // check the used flag of the dependency attribute in the PIDC
      // ICDM-133
      return refPidcAttributes.get(dependencyAttrID).getUsedFlag().equals(ApicConstants.USED_YES_DISPLAY);
    }
    AttributeValue referenceValue;
    // If the dependent attr is Project Name - ICDM-2118
    if (equalsProjName && !refPidcAttributes.isEmpty()) {
      referenceValue =
          pidcDataHandler.getAttributeValueMap().get(pidcDataHandler.getPidcVersionInfo().getPidc().getNameValueId());
    }
    else {
      // get the value defined in the PIDC for the dependency attribute
      referenceValue = pidcDataHandler.getAttributeValueMap().get(refPidcAttributes.get(dependencyAttrID));
    }
    // check if a value is defined for the attribute and not a Variant or Sub variant
    if (referenceValue == null) {
      return refPidcAttributes.get(dependencyAttrID).isAtChildLevel();
    }

    // iterate over all dependencies
    for (AttrNValueDependency attrDependency : dependencies) {
      // If the dependent attr is Project Name - ICDM-2118
      if (equalsProjName) {
        if ((pidcDataHandler.getAttributeValueMap().get(attrDependency.getDependentValueId()) != null) &&
            pidcDataHandler.getAttributeValueMap().get(attrDependency.getDependentValueId()).equals(referenceValue)) {
          return true;
        }
      }
      // check the value
      // ICDM-962
      if ((pidcDataHandler.getAttributeValueMap().get(attrDependency.getDependentValueId()) != null) &&
          attrDependency.getValueId().equals(referenceValue.getId()) && pidcVersionAttrHandler.isVisible()) {
        return true;
      }

    }
    return false;

  }

  /**
   * @return true if any value has characteristics value
   */
  public boolean isWithCharValue() {
    if (CommonUtils.isNotEmpty(getAttrValuesMap())) {
      for (AttributeValue attrValue : getAttrValuesMap().values()) {
        if (null != attrValue.getCharacteristicValueId()) {
          return true;
        }
      }
    }
    return false;
  }
}
