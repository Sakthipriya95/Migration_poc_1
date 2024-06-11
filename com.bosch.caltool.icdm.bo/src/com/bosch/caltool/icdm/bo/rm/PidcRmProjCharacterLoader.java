/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmProjectCharacter;
import com.bosch.caltool.icdm.database.entity.apic.TRmCharacterCategoryMatrix;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacterExt;


/**
 * Load the Risk levels
 *
 * @author rgo7cob
 */
public class PidcRmProjCharacterLoader extends AbstractBusinessObject<PidcRmProjCharacter, TPidcRmProjectCharacter> {

  /**
   * @param serviceData service Data
   */
  public PidcRmProjCharacterLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC_RM_PROJ_CHAR, TPidcRmProjectCharacter.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcRmProjCharacter createDataObject(final TPidcRmProjectCharacter dbPidProjChar) throws DataException {

    PidcRmProjCharacter pidProjChar = new PidcRmProjCharacter();
    pidProjChar.setId(dbPidProjChar.getPidcPcId());
    pidProjChar.setProjCharId(dbPidProjChar.getTRmProjectCharacter().getPrjCharacterId());
    if (dbPidProjChar.getRbData() != null) {
      pidProjChar.setRbDataId(dbPidProjChar.getRbData().getCategoryId());
    }
    if (dbPidProjChar.getRbShare() != null) {
      pidProjChar.setRbShareId(dbPidProjChar.getRbShare().getCategoryId());
    }
    pidProjChar.setRmDefId(dbPidProjChar.getTPidcRmDefinition().getPidcRmId());
    pidProjChar.setRelevant(dbPidProjChar.getRelevantFlag());
    pidProjChar.setVersion(dbPidProjChar.getVersion());
    return pidProjChar;
  }

  /**
   * @param pidRmId pidRmId
   * @return set of FunctionDetails
   * @throws DataException DataException
   */
  public Map<Long, PidcRmProjCharacter> getPidRmProjChar(final long pidRmId) throws DataException {

    Map<Long, PidcRmProjCharacter> pidRmProjChar = new ConcurrentHashMap<>();
    Set<TPidcRmProjectCharacter> dbPidProjChars =
        new PidcRmDefinitionLoader(getServiceData()).getEntityObject(pidRmId).getTPidcRmProjectCharacters();

    if (dbPidProjChars != null) {
      for (TPidcRmProjectCharacter dbPidProjChar : dbPidProjChars) {

        pidRmProjChar.put(dbPidProjChar.getPidcPcId(), createDataObject(dbPidProjChar));
      }

    }
    return pidRmProjChar;

  }

  /**
   * @param pidRmId pidRmId
   * @return set of FunctionDetails
   * @throws DataException DataException
   */
  public Set<PidcRmProjCharacterExt> getPidRmProjCharExt(final long pidRmId) throws DataException {


    PidcRmProjCharacterLoader rmpidcLoader = new PidcRmProjCharacterLoader(getServiceData());
    Map<Long, PidcRmProjCharacter> pidRmProjChars = rmpidcLoader.getPidRmProjChar(pidRmId);
    Set<TPidcRmProjectCharacter> dbPidRmProjChars =
        new PidcRmDefinitionLoader(getServiceData()).getEntityObject(pidRmId).getTPidcRmProjectCharacters();

    return createPidcRmProjCharExts(pidRmProjChars, dbPidRmProjChars);

  }


  /**
   * @param categoryRiskMap
   * @param pidRmProjChars
   * @param dbPidRmProjChars
   */
  private Set<PidcRmProjCharacterExt> createPidcRmProjCharExts(final Map<Long, PidcRmProjCharacter> pidRmProjChars,
      final Set<TPidcRmProjectCharacter> dbPidRmProjChars) throws DataException {
    Set<PidcRmProjCharacterExt> pidcRmProjCharExtSet = new TreeSet<>();
    RiskEvaluator evaluator = new RiskEvaluator(getServiceData());
    if (dbPidRmProjChars != null) {

      for (TPidcRmProjectCharacter dbPidRmProjChar : dbPidRmProjChars) {

        PidcRmProjCharacterExt output = new PidcRmProjCharacterExt();
        Map<Long, Long> categoryRiskMap = new ConcurrentHashMap<>();
        PidcRmProjCharacter pidcRmProjCharacter = pidRmProjChars.get(dbPidRmProjChar.getPidcPcId());
        Set<TRmCharacterCategoryMatrix> tRmCharacterCategoryMatrixs =
            dbPidRmProjChar.getTRmProjectCharacter().getTRmCharacterCategoryMatrixs();

        for (TRmCharacterCategoryMatrix tRmCharacterCategoryMatrix : tRmCharacterCategoryMatrixs) {
          Long categoryId = tRmCharacterCategoryMatrix.getTRmCategory().getCategoryId();

          Long riskId = evaluator.getEvaluatedRiskLevel(dbPidRmProjChar,
              tRmCharacterCategoryMatrix.getTRmRiskLevel().getRiskLevelId(),
              tRmCharacterCategoryMatrix.getTRmCategory().getCatType());


          categoryRiskMap.put(categoryId, riskId);
        }
        output.setPidRmChar(pidcRmProjCharacter);
        output.setCategoryRiskMap(categoryRiskMap);

        pidcRmProjCharExtSet.add(output);

      }
    }

    return pidcRmProjCharExtSet;
  }


}
