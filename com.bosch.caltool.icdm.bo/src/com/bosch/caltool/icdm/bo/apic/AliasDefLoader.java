/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDetail;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.AliasDefinitionModel;
import com.bosch.caltool.icdm.model.apic.AliasDetail;


/**
 * Loader class for AliasDefinition
 *
 * @author dja7cob
 */
public class AliasDefLoader extends AbstractBusinessObject<AliasDef, TAliasDefinition> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public AliasDefLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ALIAS_DEFINITION, TAliasDefinition.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AliasDef createDataObject(final TAliasDefinition entity) throws DataException {
    AliasDef object = new AliasDef();

    setCommonFields(object, entity);

    object.setName(entity.getAdName());

    return object;
  }

  /**
   * Get all AliasDef records in system
   *
   * @return Map. Key - name, Value - AliasDef object
   * @throws DataException error while retrieving data
   */
  public Map<String, AliasDef> getAllByName() throws DataException {
    Map<String, AliasDef> objMap = new ConcurrentHashMap<>();
    TypedQuery<TAliasDefinition> tQuery =
        getEntMgr().createNamedQuery(TAliasDefinition.GET_ALL, TAliasDefinition.class);
    List<TAliasDefinition> dbObj = tQuery.getResultList();
    for (TAliasDefinition entity : dbObj) {
      objMap.put(entity.getAdName(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * Get all Alias Definition records in system
   *
   * @return Map. Key - id, Value - AliasDef object
   * @throws DataException error while retrieving data
   */
  public Map<Long, AliasDef> getAll() throws DataException {
    Map<Long, AliasDef> objMap = new ConcurrentHashMap<>();
    TypedQuery<TAliasDefinition> tQuery =
        getEntMgr().createNamedQuery(TAliasDefinition.GET_ALL, TAliasDefinition.class);
    List<TAliasDefinition> dbObj = tQuery.getResultList();
    for (TAliasDefinition entity : dbObj) {
      objMap.put(entity.getAdId(), createDataObject(entity));
    }
    return objMap;
  }


  /**
   * Get alias defn and details for pidc id
   *
   * @param pidcId project id
   * @return AliasDefinitionModel  
   * @throws DataException throw exception
   */
  public AliasDefinitionModel getAliasDefnDetails(final Long pidcId) throws DataException {
    AliasDefinitionModel model = new AliasDefinitionModel();
    Map<Long, AliasDetail> aliasDetailMap = new HashMap<>();
    TAliasDefinition aliasDefintion = new PidcLoader(getServiceData()).getEntityObject(pidcId).getTaliasDefinition();
    if (aliasDefintion != null) {
      model.setAliasDefnition(new AliasDefLoader(getServiceData()).getDataObjectByID(aliasDefintion.getAdId()));
      List<TAliasDetail> aliasDetailList = aliasDefintion.getTAliasDetails();

      for (TAliasDetail aliasDetail : aliasDetailList) {
        aliasDetailMap.put(aliasDetail.getAliasDetailsId(),
            new AliasDetailLoader(getServiceData()).getDataObjectByID(aliasDetail.getAliasDetailsId()));
      }
      model.setAliasDetailsMap(aliasDetailMap);
    }
    return model;
  }


}
