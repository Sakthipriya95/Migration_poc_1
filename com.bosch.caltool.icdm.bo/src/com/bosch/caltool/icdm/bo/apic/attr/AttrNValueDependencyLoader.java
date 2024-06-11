/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrDependency;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * @author bne4cob
 */
public class AttrNValueDependencyLoader extends AbstractBusinessObject<AttrNValueDependency, TabvAttrDependency> {

  /**
   * @param serviceData Service Data
   */
  public AttrNValueDependencyLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ATTR_N_VAL_DEP, TabvAttrDependency.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AttrNValueDependency createDataObject(final TabvAttrDependency entity) throws DataException {
    AttrNValueDependency data = new AttrNValueDependency();

    data.setId(entity.getDepenId());
    data.setVersion(entity.getVersion());
    data.setCreatedUser(entity.getCreatedUser());
    data.setCreatedDate(timestamp2String(entity.getCreatedDate()));
    data.setModifiedUser(entity.getModifiedUser());
    data.setModifiedDate(timestamp2String(entity.getModifiedDate()));


    if (CommonUtils.isNotNull(entity.getTabvAttribute())) {
      // Dependency against attributes
      data.setAttributeId(entity.getTabvAttribute().getAttrId());
    }
    else if (CommonUtils.isNotNull(entity.getTabvAttrValue())) {
      // Dependency against values
      data.setValueId(entity.getTabvAttrValue().getValueId());
    }

    // Dependency attribute
    Long attrId = entity.getTabvAttributeD().getAttrId();
    data.setDependentAttrId(attrId);

    Attribute attr = (new AttributeLoader(getServiceData())).getDataObjectByID(attrId);
    data.setName(attr.getName());
    data.setDescription(attr.getDescription());

    // Dependency value. if value is null, then dependency is towards used flag=YES
    TabvAttrValue dbDepValue = entity.getTabvAttrValueD();
    if (CommonUtils.isNull(dbDepValue)) {
      data.setValue("<USED FLAG = YES>");
    }
    else {
      data.setDependentValueId(dbDepValue.getValueId());
      data.setValue((new AttributeValueLoader(getServiceData())).getDataObjectByID(dbDepValue.getValueId()).getName());
    }

    data.setChangeComment(entity.getChangeComment());
    data.setDeleted(CommonUtilConstants.CODE_YES.equals(entity.getDeletedFlag()));

    return data;

  }

  /**
   * Fetch the dependencies for this attribute ID
   *
   * @param attrID attribute ID
   * @param includeDeleted if true, deleted dependencies are also returned
   * @return set of dependencies
   * @throws DataException error during object creation
   */
  public Set<AttrNValueDependency> getAttributeDependencies(final Long attrID, final boolean includeDeleted)
      throws DataException {

    return doGetDependencies((new AttributeLoader(getServiceData()).getEntityObject(attrID)).getTabvAttrDependencies(),
        includeDeleted);
  }


  /**
   * @param attrId
   * @param includeDeleted
   * @return
   * @throws DataException
   */
  public Set<AttrNValueDependency> getReferentialAttrDependencies(final Long attrId, final boolean includeDeleted)
      throws DataException {
    Set<AttrNValueDependency> attrDepSet = new HashSet<>();
    for (TabvAttrDependency dependency : new AttributeLoader(getServiceData()).getEntityObject(attrId)
        .getTabvAttrDependenciesD()) {
      if (!includeDeleted) {
        AttrNValueDependency dep =
            new AttrNValueDependencyLoader(getServiceData()).getDataObjectByID(dependency.getDepenId());
        if (!dep.isDeleted() && ((null != dependency.getTabvAttribute()) &&
            dependency.getTabvAttribute().getDeletedFlag().equals(ApicConstants.CODE_NO))) {
          attrDepSet.add(dep);
        }
      }
      else {
        AttrNValueDependency dep =
            new AttrNValueDependencyLoader(getServiceData()).getDataObjectByID(dependency.getDepenId());
        attrDepSet.add(dep);
      }
    }
    return attrDepSet;
  }


  /**
   * Fetch the dependencies for this attribute value ID
   *
   * @param valueID value ID
   * @param includeDeleted if true, deleted dependencies are also returned
   * @return set of dependencies
   * @throws DataException error during object creation
   */
  public Set<AttrNValueDependency> getValueDependencies(final Long valueID, final boolean includeDeleted)
      throws DataException {

    return doGetDependencies(
        (new AttributeValueLoader(getServiceData()).getEntityObject(valueID)).getTabvAttrDependencies(),
        includeDeleted);
  }

  private Set<AttrNValueDependency> doGetDependencies(final Collection<TabvAttrDependency> entityCol,
      final boolean includeDeleted)
      throws DataException {

    Set<AttrNValueDependency> retSet = new HashSet<>();
    AttributeValueLoader valLdr = new AttributeValueLoader(getServiceData());
    AttributeLoader attrLdr = new AttributeLoader(getServiceData());

    for (TabvAttrDependency entity : entityCol) {
      AttrNValueDependency data = createDataObject(entity);
      // check if the dependency is deleted
      if (data.isDeleted() && !includeDeleted) {
        continue;
      }

      if (!isAttributeDeleted(attrLdr, data) || !isValueDeleted(valLdr, data) || includeDeleted) {
        // skip the data, if attribute or value is deleted and include deleted flag is false
        retSet.add(data);
      }

    }

    return retSet;
  }

  private boolean isAttributeDeleted(final AttributeLoader attrLdr, final AttrNValueDependency data) {
    return CommonUtilConstants.CODE_YES.equals(attrLdr.getEntityObject(data.getDependentAttrId()).getDeletedFlag());
  }

  private boolean isValueDeleted(final AttributeValueLoader valLdr, final AttrNValueDependency data) {
    return (data.getDependentValueId() != null) &&
        CommonUtilConstants.CODE_YES.equals(valLdr.getEntityObject(data.getDependentValueId()).getDeletedFlag());
  }

  /**
   * Attribute value dependencies of the given input attributes. The elements are sorted based on their relative
   * dependencies.
   * <p>
   * NOTE : Attributes without dependencies will not be available in the output
   *
   * @param attrIdSet set of attribute IDs
   * @return Map. Key - attribute ID. Value - Map of dependencies, with key as value ID(for used flag, key is null) and
   *         value as attribute dependency object
   * @throws DataException error while creating dependency objects
   */
  // * @return Map. Key - attribute ID. Value - Map of dependencies, with key as value ID(for used flag, key is null)
  // and value as attribute dependency object
  public Map<Long, Map<Long, AttrNValueDependency>> getAttributeDependencies(final Set<Long> attrIdSet)
      throws DataException {
    Map<Long, Map<Long, AttrNValueDependency>> sortedMap = new LinkedHashMap<>();
    doSort(sortedMap, new HashSet<>(attrIdSet), attrIdSet);
    return sortedMap;
  }

  private void doSort(final Map<Long, Map<Long, AttrNValueDependency>> sortedMap, final Set<Long> remAttrSet,
      final Set<Long> inputAttrSet)
      throws DataException {

    Long attrId;
    Set<AttrNValueDependency> attrDepSet;
    Long depAttrId;

    Iterator<Long> itr = remAttrSet.iterator();
    while (itr.hasNext()) {
      attrId = itr.next();
      attrDepSet = getAttributeDependencies(attrId, false);

      if (attrDepSet.isEmpty()) {
        // Skip the attributes without dependencies
        itr.remove();
      }
      else {
        depAttrId = attrDepSet.iterator().next().getDependentAttrId();
        if (!inputAttrSet.contains(depAttrId) || !remAttrSet.contains(depAttrId)) {
          Map<Long, AttrNValueDependency> depMap = new HashMap<>();
          attrDepSet.forEach(attrDep -> depMap.put(attrDep.getDependentValueId(), attrDep));
          sortedMap.put(attrId, depMap);
          itr.remove();
        }
      }
    }

    if (remAttrSet.isEmpty()) {
      return;
    }

    doSort(sortedMap, remAttrSet, inputAttrSet);

  }

  /**
   * @return AttrNValueDependencyDetails
   * @throws DataException error while filling AttrNValueDependencyDetails
   */
  public AttrNValueDependencyDetails fillAttrDependencyDetails() throws DataException {
    AttrNValueDependencyDetails attrDependencyDetails = new AttrNValueDependencyDetails();

    // fill dependencies for all attributes
    Map<Long, AttributeValue> depAttrVals = new HashMap<>();

    AttributeValueLoader attrValLdr = new AttributeValueLoader(getServiceData());

    Map<Long, Set<AttrNValueDependency>> attrRefDependenciesMap = new HashMap<>();

    // fetch dependencies for each attribute
    Map<Long, Set<AttrNValueDependency>> attrDependenciesMap = new HashMap<>();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Map<Long, Map<Long, Set<AttrNValueDependency>>> depMap = new HashMap<>();
    for (Attribute attr : attrLoader.getAllAttributes().values()) {

      // fill referential dependencies.. dependent attributes and its dependencies
      Set<AttrNValueDependency> depSet =
          new AttrNValueDependencyLoader(getServiceData()).getReferentialAttrDependencies(attr.getId(), false);
      if (CommonUtils.isNotEmpty(depSet)) {
        attrRefDependenciesMap.put(attr.getId(), depSet);
        for (AttrNValueDependency depVal : depSet) {
          if (depVal.getDependentValueId() != null) {
            depAttrVals.put(depVal.getDependentValueId(), attrValLdr.getDataObjectByID(depVal.getDependentValueId()));
          }
        }
      }

      // for each attribute, fetch all values, for each value, fetch dependencies
      TabvAttribute dbAttr = attrLoader.getEntityObject(attr.getId());

      Map<Long, Set<AttrNValueDependency>> attrNValDependencyMap = new HashMap<>();
      for (TabvAttrValue entity : dbAttr.getTabvAttrValues()) {

        Set<AttrNValueDependency> valueDependencies = getValueDependencies(entity.getValueId(), false);
        if (!valueDependencies.isEmpty()) {

          attrNValDependencyMap.put(entity.getValueId(), valueDependencies);
        }
      }
      depMap.put(attr.getId(), attrNValDependencyMap);


      Set<AttrNValueDependency> attrDepSet =
          new AttrNValueDependencyLoader(getServiceData()).getAttributeDependencies(attr.getId(), false);
      if (CommonUtils.isNotEmpty(attrDepSet)) {
        attrDependenciesMap.put(attr.getId(), attrDepSet);
        for (AttrNValueDependency valDep : attrDepSet) {
          if (valDep.getDependentValueId() != null) {
            depAttrVals.put(valDep.getDependentValueId(), attrValLdr.getDataObjectByID(valDep.getDependentValueId()));
          }
        }
      }

    }

    attrDependencyDetails.getAttrDependenciesMap().putAll(attrDependenciesMap);

    attrDependencyDetails.getDepAttrVals().putAll(depAttrVals);

    attrDependencyDetails.getAttrRefDependenciesMap().putAll(attrRefDependenciesMap);

    attrDependencyDetails.getAttrDependenciesMapForAllAttr().putAll(depMap);

    return attrDependencyDetails;
  }


}
