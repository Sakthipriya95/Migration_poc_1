/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpMapPtType;
import com.bosch.caltool.icdm.database.entity.a2l.TPowerTrainType;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapPtType;


/**
 * @author bne4cob
 */
public class FC2WPMapPtTypeLoader extends AbstractBusinessObject<FC2WPMapPtType, TFc2wpMapPtType> {

  /**
   * @param serviceData service Data
   */
  public FC2WPMapPtTypeLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.FC2WP_MAPPED_PT_TYPE, TFc2wpMapPtType.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FC2WPMapPtType createDataObject(final TFc2wpMapPtType entity) throws DataException {
    FC2WPMapPtType ret = new FC2WPMapPtType();

    ret.setId(entity.getFcwpMapPtTypeId());
    ret.setFcwpMapId(entity.getTFc2wpMapping().getFcwpMapId());

    TPowerTrainType dbPtType = entity.getTPowerTrainType();
    ret.setPtTypeId(dbPtType.getPtTypeId());
    ret.setPtTypeDesc(dbPtType.getPtTypeDesc());
    ret.setPtTypeName(dbPtType.getPtType());

    ret.setVersion(entity.getVersion());

    return ret;
  }

}
