/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TRmProjectCharacter;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.rm.RmProjectCharacter;


/**
 * Load the Risk levels
 *
 * @author rgo7cob
 */
public class RmProjectCharacterLoader extends AbstractBusinessObject<RmProjectCharacter, TRmProjectCharacter> {

  /**
   * @param serviceData serviceData
   */
  public RmProjectCharacterLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RM_PROJECT_CHAR, TRmProjectCharacter.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RmProjectCharacter createDataObject(final TRmProjectCharacter dbRmProjChar) throws DataException {
    RmProjectCharacter projChar = new RmProjectCharacter();
    projChar.setDeleted("Y".equals(dbRmProjChar.getDeletedFlag()));
    projChar.setId(dbRmProjChar.getPrjCharacterId());
    projChar.setNameEng(dbRmProjChar.getProjectCharacterEng());
    projChar.setNameGer(dbRmProjChar.getProjectCharacterGer());
    String name = getLangSpecTxt(dbRmProjChar.getProjectCharacterEng(), dbRmProjChar.getProjectCharacterGer());
    projChar.setName(name);
    if (dbRmProjChar.getTRmProjectCharacter() != null) {
      projChar.setParentId(dbRmProjChar.getTRmProjectCharacter().getPrjCharacterId());
    }
    else {
      projChar.setParentId(0l);
    }
    return projChar;
  }

  /**
   * @return the Map of Project characters with id as key
   * @throws DataException DataException
   */
  public Map<Long, RmProjectCharacter> getProjCharacters() throws DataException {
    Map<Long, RmProjectCharacter> projCharMap = new ConcurrentHashMap<>();


    TypedQuery<TRmProjectCharacter> tQuery =
        getEntMgr().createNamedQuery(TRmProjectCharacter.GET_ALL, TRmProjectCharacter.class);


    List<TRmProjectCharacter> dbProjChars = tQuery.getResultList();

    for (TRmProjectCharacter projChar : dbProjChars) {
      projCharMap.put(projChar.getPrjCharacterId(), createDataObject(projChar));
    }
    return projCharMap;
  }


}
