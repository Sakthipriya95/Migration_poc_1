/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrNValueDependencyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeSuperGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;

/**
 * @author dmo5cob
 */
public class AttributesDataHandler extends AbstractClientDataHandler {

  private Map<Long, Attribute> attrsMap;
  private final SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> attrSet =
      Collections.synchronizedSortedSet(new TreeSet<com.bosch.caltool.icdm.model.apic.attr.Attribute>());
  /**
   * The selected Attribute instance
   */
  private List<Attribute> selectedAttributes;
  /**
   * The selected Attribute Value instance
   */
  private List<AttributeValue> selectedValues;
  /**
   * The selected Attribute Dependency instance
   */
  private AttrNValueDependency selectedAttrDep;
  /**
   * The selected Attribute Value Dependency instance
   */
  private AttrNValueDependency selectedAttrValDep;
  private SortedSet<AttributeValue> attrValues;
  private SortedSet<AttrNValueDependency> attrDepnsSet;
  private SortedSet<AttrNValueDependency> valDepnSet;
  private Map<Long, Set<NodeAccess>> allNodeAccessMap;
  /**
   * 491575 - Defect Fix - Failing Test Case "INT:TC31:325288:Validate Group and Super Group Values are not displayed in
   * Properties view of an attribute and attribute editor." Contain all group and super group map
   */
  private AttrGroupModel attrGroupModel;

  /**
   */
  public AttributesDataHandler() {
    super();
    getAllAttrsFromWS();
    getAttrGroupAndSuperGroup();
  }

  /**
   * @return SortedSet<Attribute>
   */
  public SortedSet<Attribute> getAllAttrsFromWS() {

    AttributeServiceClient wsClient = new AttributeServiceClient();
    try {
      this.attrsMap = wsClient.getAll();
      this.attrSet.addAll(this.attrsMap.values());

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    return this.attrSet;
  }

  /**
   * 491575 - Defect Fix - Failing Test Case "INT:TC31:325288:Validate Group and Super Group Values are not displayed in
   * Properties view of an attribute and attribute editor." Contain all group and super group map
   *
   * @return {@link AttrGroupModel} object
   */
  public AttrGroupModel getAttrGroupAndSuperGroup() {
    AttributeSuperGroupServiceClient superGrpClient = new AttributeSuperGroupServiceClient();
    try {
      this.attrGroupModel = superGrpClient.getAttrGroupModel();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    return this.attrGroupModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {

    registerCns(this::loadAttributes, MODEL_TYPE.ATTRIBUTE);
    registerCns(this::loadValues, MODEL_TYPE.ATTRIB_VALUE);
    registerCns(this::loadAttrDependencies, MODEL_TYPE.ATTR_N_VAL_DEP);
    registerCns(data -> ((NodeAccess) CnsUtils.getModel(data)).getNodeType().equals(MODEL_TYPE.ATTRIBUTE.getTypeCode()),
        this::fetchAllAttrNodeAccess, MODEL_TYPE.NODE_ACCESS);
    registerCns(
        data -> ((ActiveDirectoryGroupNodeAccess) CnsUtils.getModel(data)).getNodeType()
            .equals(MODEL_TYPE.ATTRIBUTE.getTypeCode()),
        this::fetchAllAttrNodeAccess, MODEL_TYPE.ACTIVE_DIRECTORY_GROUP_NODE_ACCES);
    registerCns(this::loadAttrGrpModel, MODEL_TYPE.SUPER_GROUP, MODEL_TYPE.GROUP);
  }


  /**
   * @return all the attribute Node access set.
   */
  public void fetchAllAttrNodeAccess(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    NodeAccessServiceClient client = new NodeAccessServiceClient();

    Map<Long, Set<NodeAccess>> nodeAccessMap = new HashMap<>();
    try {
      Set<Long> attrIdsList = new HashSet<>();
      SortedSet<Attribute> attrSet = getAttrSet();
      for (Attribute attribute : attrSet) {
        attrIdsList.add(attribute.getId());
      }

      NodeAccessDetails ret = client.getNodeAccessDetailsByNode(MODEL_TYPE.ATTRIBUTE, null);
      if (CommonUtils.isNotEmpty(ret.getNodeAccessMap())) {
        for (Entry<Long, Set<NodeAccess>> entry : ret.getNodeAccessMap().entrySet()) {
          nodeAccessMap.put(entry.getKey(), entry.getValue());
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error("Error loading  Node access Data for  all Attributes :", e);
    }


    this.allNodeAccessMap = nodeAccessMap;

  }


  /**
  *
  */
  private void loadAttrDependencies(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo chInfo : chDataInfoMap.values()) {
      try {
        AttrNValueDependency attrNval = new AttrNValueDependencyServiceClient().getById(chInfo.getObjId());
        if (null != attrNval.getAttributeId()) {
          Attribute attr = getSelectedAttributes().get(0);
          AttributeClientBO attrBO = new AttributeClientBO(attr);
          this.attrDepnsSet = attrBO.getAttrDependencies(true);
          Attribute newAttr = new AttributeServiceClient().get(attr.getId());
          this.attrsMap.put(newAttr.getId(), newAttr);
          this.attrSet.remove(newAttr);
          this.attrSet.add(newAttr);
        }
        else if ((null != attrNval.getValueId()) && (getSelectedValues() != null)) {
          AttributeValue val = getSelectedValues().get(0);
          AttributeValueClientBO valBO = new AttributeValueClientBO(val);
          this.valDepnSet = valBO.getValueDependencies(true);
          AttributeValue newAttrVal = new AttributeValueServiceClient().getById(val.getId());
          this.attrValues.remove(newAttrVal);
          this.attrValues.add(newAttrVal);
        }

      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      }
    }

  }

  /**
  *
  */
  private void loadValues(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    Attribute attr = getSelectedAttributes().get(0);
    this.attrValues = new AttributeClientBO(attr).getAttrValues();
    try {
      Attribute newAttr = new AttributeServiceClient().get(attr.getId());
      this.attrsMap.put(newAttr.getId(), newAttr);
      this.attrSet.remove(newAttr);
      this.attrSet.add(newAttr);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }

  }

  /**
  *
  */
  public void loadAttributes(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.attrSet.clear();
    this.attrSet.addAll(getAllAttrsFromWS());
  }

  /**
  *
  */
  public void loadAttrGrpModel(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    getAttrGroupAndSuperGroup();
  }

  /**
   * @return the attrsMap
   */
  public Map<Long, Attribute> getAttrsMap() {
    return this.attrsMap;
  }


  /**
   * @return the attrSet
   */
  public SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> getAttrSet() {
    return this.attrSet;
  }


  /**
   * @return the selectedAttributes
   */
  public List<Attribute> getSelectedAttributes() {
    return this.selectedAttributes;
  }


  /**
   * @return the selectedValues
   */
  public List<AttributeValue> getSelectedValues() {
    return this.selectedValues;
  }


  /**
   * @return the selectedAttrDep
   */
  public AttrNValueDependency getSelectedAttrDep() {
    return this.selectedAttrDep;
  }


  /**
   * @return the selectedAttrValDep
   */
  public AttrNValueDependency getSelectedAttrValDep() {
    return this.selectedAttrValDep;
  }


  /**
   * @param selectedAttributes the selectedAttributes to set
   */
  public void setSelectedAttributes(final List<Attribute> selectedAttributes) {
    this.selectedAttributes = selectedAttributes;
  }


  /**
   * @param selectedValues the selectedValues to set
   */
  public void setSelectedValues(final List<AttributeValue> selectedValues) {
    this.selectedValues = selectedValues;
  }


  /**
   * @param selectedAttrDep the selectedAttrDep to set
   */
  public void setSelectedAttrDep(final AttrNValueDependency selectedAttrDep) {
    this.selectedAttrDep = selectedAttrDep;
  }


  /**
   * @param selectedAttrValDep the selectedAttrValDep to set
   */
  public void setSelectedAttrValDep(final AttrNValueDependency selectedAttrValDep) {
    this.selectedAttrValDep = selectedAttrValDep;
  }


  /**
   * @return the attrValues
   */
  public SortedSet<AttributeValue> getAttrValues() {
    return this.attrValues;
  }


  /**
   * @param attrValues the attrValues to set
   */
  public void setAttrValues(final SortedSet<AttributeValue> attrValues) {
    this.attrValues = attrValues;
  }


  /**
   * @return the attrDepnsSet
   */
  public SortedSet<AttrNValueDependency> getAttrDepnsSet() {
    return this.attrDepnsSet;
  }


  /**
   * @param attrDepnsSet the attrDepnsSet to set
   */
  public void setAttrDepnsSet(final SortedSet<AttrNValueDependency> attrDepnsSet) {
    this.attrDepnsSet = attrDepnsSet;
  }


  /**
   * @return the valDepnSet
   */
  public SortedSet<AttrNValueDependency> getValDepnSet() {
    return this.valDepnSet;
  }


  /**
   * @param valDepnSet the valDepnSet to set
   */
  public void setValDepnSet(final SortedSet<AttrNValueDependency> valDepnSet) {
    this.valDepnSet = valDepnSet;
  }


  /**
   * @return the allNodeAccessMap
   */
  public Map<Long, Set<NodeAccess>> getAllNodeAccessMap() {
    return this.allNodeAccessMap;
  }


  /**
   * @return the attrGroupModel
   */
  public AttrGroupModel getAttrGroupModel() {
    return this.attrGroupModel;
  }

  /**
   * @param input - list of values present in the values tableviewer
   * @param selectedValue - value selected in tableviewer
   * @return - set of values against which the added/edited value would be validated with
   */
  public Set<String> getValueStringsForValidation(final Object input, final Object selectedValue) {
    Set<String> valueStringSet = new HashSet<>();
    if (input != null) {
      Set<AttributeValue> attrValueSet = (Set<AttributeValue>) input;
      // remove the selected value incase of edit operation to avoid validating against the same value
      if (null != selectedValue) {
        attrValueSet.remove(selectedValue);
      }
      valueStringSet = attrValueSet.stream().map(AttributeValue::getNameRaw).collect(Collectors.toSet());
    }
    return valueStringSet;
  }

  /**
   * @param attrValue selected attribute value
   * @param existingValues existing attribute values
   * @return boolean
   */
  public boolean checkForDuplicates(final String attrValue, final Set<String> existingValues) {
    for (String stringValue : existingValues) {
      if (attrValue.equals(stringValue)) {
        return true;
      }
    }
    return false;
  }


}
