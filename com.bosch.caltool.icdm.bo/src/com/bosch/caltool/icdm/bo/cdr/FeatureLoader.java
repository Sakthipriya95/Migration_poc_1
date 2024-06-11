/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TSsdFeature;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;

/**
 * @author dja7cob
 */
public class FeatureLoader extends AbstractBusinessObject<Feature, TSsdFeature> {

  /**
   * @param serviceData instance
   */
  public FeatureLoader(final ServiceData serviceData) {
    super(serviceData, "SSD Feature", TSsdFeature.class);
  }

  /**
   * Fetch all features for the feature id list
   *
   * @param featureIdSet Set of feature ids
   * @return Map of attr id, Features
   * @throws DataException Exception
   */
  protected Map<Long, Feature> fetchFeatures(final Set<Long> featureIdSet) throws DataException {
    final Map<Long, Feature> featureMap = new HashMap<>();

    // Empty list would cause error in executing query
    if ((featureIdSet == null) || featureIdSet.isEmpty()) {
      return featureMap;
    }

    Set<Feature> unMappedFeatureSet = new HashSet<>();
    Set<Long> foundFeatureSet = new HashSet<>();

    Map<Long, Feature> featureObjMap = getDataObjectByID(featureIdSet);
    for (Feature featureObj : featureObjMap.values()) {
      if (null == featureObj.getAttrId()) {
        unMappedFeatureSet.add(featureObj);
      }
      else {
        featureMap.put(featureObj.getAttrId(), featureObj);
        foundFeatureSet.add(featureObj.getId());
      }
    }

    if (!unMappedFeatureSet.isEmpty()) {
      StringBuilder message =
          new StringBuilder("Attribute mapping missing for the below features. Contact iCDM Hotline.");
      for (Feature feat : unMappedFeatureSet) {
        message.append('\n').append(feat.getId()).append(" - ").append(feat.getName());
      }
      throw new DataNotFoundException(message.toString());
    }

    if (featureIdSet.size() != foundFeatureSet.size()) {
      throw new DataNotFoundException(
          "Invalid Feature ID(s) - " + CommonUtils.getDifference(featureIdSet, foundFeatureSet));
    }

    return featureMap;
  }

  /**
   * @return the feature map
   * @throws DataException DataException
   */
  public ConcurrentMap<Long, Feature> fetchAllFeatures() throws DataException {
    getLogger().debug("Fetching all Features from database ...");

    final ConcurrentMap<Long, Feature> featureMap = new ConcurrentHashMap<>();

    final TypedQuery<TSsdFeature> query = getEntMgr().createNamedQuery(TSsdFeature.NQ_GET_ALL, TSsdFeature.class);
    final List<TSsdFeature> dbFtreList = query.getResultList();
    for (TSsdFeature dbFeature : dbFtreList) {
      if (dbFeature.getTabvAttribute() != null) {
        featureMap.put(dbFeature.getTabvAttribute().getAttrId(), createDataObject(dbFeature));
      }
    }

    return featureMap;
  }

  /**
   * Fetch feature for the given attribute ID
   *
   * @param attrId attribute ID
   * @return feature model, or null if feature is not mapped
   * @throws DataException data retrieval error
   */
  public Feature getFeature(final Long attrId) throws DataException {
    getLogger().debug("Fetching feature for attribute ID : {}", attrId);

    final TypedQuery<TSsdFeature> query = getEntMgr()
        .createNamedQuery(TSsdFeature.NQ_GET_FEATURE_BY_ATTR_ID, TSsdFeature.class).setParameter("attrId", attrId);

    final List<TSsdFeature> dbFtreList = query.getResultList();
    for (TSsdFeature dbFeature : dbFtreList) {
      if (dbFeature.getTabvAttribute() != null) {
        getLogger().debug("   Feature ID is : {}", dbFeature.getFeatureId());
        return createDataObject(dbFeature);
      }
    }

    getLogger().error("Invalid attribute ID or attribute not mapped to a Feature");

    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Feature createDataObject(final TSsdFeature entity) throws DataException {
    Feature data = new Feature();
    data.setId(entity.getFeatureId());
    data.setName(entity.getFeatureText());

    if ((null != entity.getTabvAttribute()) && (entity.getTabvAttribute().getAttrId() != 1)) {
      Attribute attribute =
          new AttributeLoader(getServiceData()).getDataObjectByID(entity.getTabvAttribute().getAttrId());
      data.setAttrId(attribute.getId());
      data.setAttrName(attribute.getName());
    }


    return data;
  }

}
