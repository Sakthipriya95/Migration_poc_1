package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;


/**
 * Loader class for A2lVarGrpVarMapping
 *
 * @author pdh2cob
 */
public class A2lVarGrpVariantMappingLoader
    extends AbstractBusinessObject<A2lVarGrpVariantMapping, TA2lVarGrpVariantMapping> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lVarGrpVariantMappingLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_VAR_GRP_VAR_MAPPING, TA2lVarGrpVariantMapping.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lVarGrpVariantMapping createDataObject(final TA2lVarGrpVariantMapping entity) throws DataException {
    A2lVarGrpVariantMapping object = new A2lVarGrpVariantMapping();

    setCommonFields(object, entity);
    // set variant group id
    object
        .setA2lVarGroupId(entity.getTA2lVariantGroup() == null ? null : entity.getTA2lVariantGroup().getA2lVarGrpId());
    // set variant id
    object.setVariantId(entity.getTabvProjectVariant() == null ? null : entity.getTabvProjectVariant().getVariantId());

    return object;
  }


  /**
   * Get all A2lVarGrpVarMapping records in system corresponding to a2lgrp
   *
   * @param a2lGrpId A2L Group ID
   * @return Map. Key - id, Value - A2lVariantGroup object
   * @throws DataException error while retrieving data
   */
  public Map<Long, A2lVarGrpVariantMapping> getA2LVarGrps(final Long a2lGrpId) throws DataException {

    A2lVariantGroupLoader varGrpLdr = new A2lVariantGroupLoader(getServiceData());
    varGrpLdr.validateId(a2lGrpId);

    List<TA2lVarGrpVariantMapping> dbObj = varGrpLdr.getEntityObject(a2lGrpId).getTA2lVarGrpVariantMappings();

    Map<Long, A2lVarGrpVariantMapping> retMap = new HashMap<>();

    if (dbObj != null) {
      for (TA2lVarGrpVariantMapping entity : dbObj) {
        retMap.put(entity.getA2lVarGrpVarId(), createDataObject(entity));
      }
    }
    return retMap;
  }
}
