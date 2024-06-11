/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpPtTypeRelv;
import com.bosch.caltool.icdm.database.entity.a2l.TPowerTrainType;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;


/**
 * @author bne4cob
 */
public class FC2WPRelvPTTypeLoader extends AbstractBusinessObject<FC2WPRelvPTType, TFc2wpPtTypeRelv> {

  /**
   * @param serviceData service Data
   */
  public FC2WPRelvPTTypeLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.FC2WP_RELV_PT_TYPE, TFc2wpPtTypeRelv.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FC2WPRelvPTType createDataObject(final TFc2wpPtTypeRelv entity) throws DataException {

    FC2WPRelvPTType ret = new FC2WPRelvPTType();

    ret.setId(entity.getFcwpPtTypeRelvId());

    TPowerTrainType dbPtType = entity.getTPowerTrainType();
    ret.setPtTypeDesc(dbPtType.getPtTypeDesc());
    ret.setPtTypeId(dbPtType.getPtTypeId());
    ret.setPtTypeName(dbPtType.getPtType());
    ret.setFcwpDefId(entity.getTFc2wpDefinition().getFcwpDefId());
    ret.setVersion(entity.getVersion());
    ret.setId(entity.getFcwpPtTypeRelvId());

    return ret;
  }

  /**
   * Gets the FC2WP relevant PT-types.
   *
   * @param fc2wpDefID the fc2wp definition ID
   * @return the FC2WP relevant PT-types
   * @throws DataException exception
   */
  public Set<FC2WPRelvPTType> getFC2WPRelevantPTtypes(final Long fc2wpDefID) throws DataException {
    Set<FC2WPRelvPTType> relevantPtTypes = new HashSet<FC2WPRelvPTType>();
    FC2WPDefLoader fc2WpLoader = new FC2WPDefLoader(getServiceData());
    TFc2wpDefinition dbfc2wpDef = fc2WpLoader.getEntityObject(fc2wpDefID);
    if (dbfc2wpDef.getTFc2wpPtTypeRelvs() != null) {
      for (TFc2wpPtTypeRelv dbPtTypeRelObj : dbfc2wpDef.getTFc2wpPtTypeRelvs()) {
        relevantPtTypes.add(createDataObject(dbPtTypeRelObj));
      }
    }
    return relevantPtTypes;
  }


}
