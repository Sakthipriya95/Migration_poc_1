/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.database.entity.a2l.TPowerTrainType;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.PTType;

/**
 * The Class PTTypeLoader.
 *
 * @author gge6cob
 */
public class PTTypeLoader extends AbstractBusinessObject<PTType, TPowerTrainType> {

  /**
   * Instantiates a new PT type loader.
   *
   * @param inputData ServiceData
   */
  public PTTypeLoader(final ServiceData inputData) {
    super(inputData, MODEL_TYPE.POWER_TRAIN_TYPE, TPowerTrainType.class);
  }

  /**
   * Gets the all Power Train types.
   *
   * @return Set of PTType
   */
  public Set<PTType> getAllPTtypes() {
    Set<PTType> retSet = new HashSet<>();

    TypedQuery<TPowerTrainType> tQuery =
        getEntMgr().createNamedQuery(TPowerTrainType.NQ_FIND_ALL, TPowerTrainType.class);

    for (TPowerTrainType dbptTypeEntity : tQuery.getResultList()) {
      PTType ptTypeRet = createDataObject(dbptTypeEntity);
      retSet.add(ptTypeRet);
    }

    return retSet;
  }

  /**
   * Creates a PT type object from entity object.
   *
   * @param dbptTypeEntity the dbpt type entity
   * @return the PT type
   */
  @Override
  protected PTType createDataObject(final TPowerTrainType dbptTypeEntity) {
    PTType ptTypeObj = new PTType();
    ptTypeObj.setPtTypeId(dbptTypeEntity.getPtTypeId());
    ptTypeObj.setPtTypeName(dbptTypeEntity.getPtType());
    ptTypeObj.setPtTypeDesc(dbptTypeEntity.getPtTypeDesc());
    return ptTypeObj;
  }

}
