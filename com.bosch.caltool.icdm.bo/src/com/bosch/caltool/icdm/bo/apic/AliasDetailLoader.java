/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDetail;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * Loader class for Alias Detail
 *
 * @author bne4cob
 */
public class AliasDetailLoader extends AbstractBusinessObject<AliasDetail, TAliasDetail> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public AliasDetailLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ALIAS_DETAIL, TAliasDetail.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AliasDetail createDataObject(final TAliasDetail entity) throws DataException {
    AliasDetail object = new AliasDetail();

    setCommonFields(object, entity);

    object.setAdId(entity.getTAliasDefinition().getAdId());

    // if this is alias for attribute value, then name and description is same as that of attribute
    TabvAttribute dbAttr = entity.getTabvAttribute();
    if (dbAttr != null) {
      object.setAttrId(dbAttr.getAttrId());
      Attribute attr = new AttributeLoader(getServiceData()).getDataObjectByID(dbAttr.getAttrId());
      object.setName(attr.getName());
      object.setDescription(attr.getDescription());
    }

    // if this is alias for attribute value, then name and description is same as that of value
    TabvAttrValue dbAttrVal = entity.getTabvAttrValue();
    if (dbAttrVal != null) {
      object.setValueId(dbAttrVal.getValueId());
      AttributeValue attrVal = new AttributeValueLoader(getServiceData()).getDataObjectByID(dbAttrVal.getValueId());
      object.setName(attrVal.getNameRaw());
      object.setDescription(attrVal.getDescription());
    }

    object.setAliasName(entity.getAliasName());

    return object;
  }

  /**
   * Get Alias Detail records using Alias Definition ID
   *
   * @param adId Alias Definition ID
   * @return Map. Key - id, Value - AliasDetail object
   * @throws DataException error while retrieving data
   */
  public Map<Long, AliasDetail> getByAdId(final Long adId) throws DataException {
    Map<Long, AliasDetail> objMap = new ConcurrentHashMap<>();
    List<TAliasDetail> dbObj = new AliasDefLoader(getServiceData()).getEntityObject(adId).getTAliasDetails();
    for (TAliasDetail entity : dbObj) {
      objMap.put(entity.getAliasDetailsId(), createDataObject(entity));
    }
    return objMap;
  }

}
