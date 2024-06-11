/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmProjectCharacter;
import com.bosch.caltool.icdm.database.entity.apic.TRmProjectCharacter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.RmCategory.CATEGORY_TYPE;
import com.bosch.caltool.icdm.model.rm.RmCategory.CATEGORY_VALUE;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel.RISK_LEVEL_CONFIG;

/**
 * @author rgo7cob
 */
public class RiskEvaluator {


  private final ServiceData serviceData;
  private final Map<String, RmRiskLevel> riskLvlWithDescMap = new ConcurrentHashMap<>();

  /**
   * @param serviceData serviceData
   * @throws DataException error while fetching risk levels
   */
  public RiskEvaluator(final ServiceData serviceData) throws DataException {
    this.serviceData = serviceData;
    fillRiskLvlWithDescMap();
  }

  /**
   * @param riskLevels risk levels
   * @return the risk with max weight
   */
  public RmRiskLevel getMaxRiskLvl(final RmRiskLevel... riskLevels) {
    RmRiskLevel maxRiskLevel = null;

    for (RmRiskLevel rmRiskLevel : riskLevels) {
      if ((maxRiskLevel == null) || (maxRiskLevel.getRiskWeight() < rmRiskLevel.getRiskWeight())) {
        maxRiskLevel = rmRiskLevel;
      }
    }
    return maxRiskLevel;
  }


  /**
   * @return the not defined risk
   * @throws DataException
   */
  public RmRiskLevel getNotDefinedRisk() throws DataException {
    RmRiskLevelLoader loader = new RmRiskLevelLoader(this.serviceData);
    Map<Long, RmRiskLevel> riskLevels = loader.getRiskLevels();
    for (RmRiskLevel riskLevel : riskLevels.values()) {
      if (RISK_LEVEL_CONFIG.RISK_LVL_NA.getCode().equalsIgnoreCase(riskLevel.getCode())) {
        return riskLevel;
      }
    }

    return null;

  }

  /**
   * @param lvl1 level 1
   * @param lvl2 level 2
   * @return the Consolidated or calculated risk level
   * @throws DataException
   */
  private RmRiskLevel getConsolidatedRisks(final RmRiskLevel lvl1, final RmRiskLevel lvl2) throws DataException {
    long riskProduct = lvl1.getRiskWeight() * lvl2.getRiskWeight();
    if (riskProduct == RISK_LEVEL_CONFIG.RISK_LVL_LOW.getRiskThreshold()) {
      return this.riskLvlWithDescMap.get(RISK_LEVEL_CONFIG.RISK_LVL_LOW.getCode());
    }
    if ((riskProduct > RISK_LEVEL_CONFIG.RISK_LVL_LOW.getRiskThreshold()) &&
        (riskProduct <= RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getRiskThreshold())) {
      return this.riskLvlWithDescMap.get(RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getCode());
    }
    if (riskProduct > RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getRiskThreshold()) {
      return this.riskLvlWithDescMap.get(RISK_LEVEL_CONFIG.RISK_LVL_HIGH.getCode());
    }
    return this.riskLvlWithDescMap.get(RISK_LEVEL_CONFIG.RISK_LVL_NA.getCode());
  }


  /**
   * fill the risk map
   *
   * @throws DataException DataException
   */
  private void fillRiskLvlWithDescMap() throws DataException {

    RmRiskLevelLoader loader = new RmRiskLevelLoader(this.serviceData);
    Map<Long, RmRiskLevel> riskLevels = loader.getRiskLevels();

    for (RmRiskLevel level : riskLevels.values()) {
      this.riskLvlWithDescMap.put(level.getCode(), level);
    }

  }

  /**
   * @param dbPidRmProjChar dbPidRmProjChar
   * @param defaultRiskId default Risk id for the Configuration
   * @param categoryType categoryType I,N, D,S
   * @return the consolidated Risk id
   * @throws DataException DataException
   */
  public Long getEvaluatedRiskLevel(final TPidcRmProjectCharacter dbPidRmProjChar, final long defaultRiskId,
      final String categoryType) throws DataException {

    Long riskId = defaultRiskId;
    RmRiskLevel maxRiskLvl = null;
    if ((CATEGORY_TYPE.getType(categoryType) == CATEGORY_TYPE.IMPACTED_WITH_RISK) &&
        ApicConstants.YES.equals(dbPidRmProjChar.getRelevantFlag())) {
      // get rb data name
      String rbData = dbPidRmProjChar.getRbData().getCategoryCode();
      // get rb share name
      String rbShare = dbPidRmProjChar.getRbShare().getCategoryCode();
      // get risk config for the rb data
      String rbDataRiskConfig = CATEGORY_VALUE.getRiskConfig(rbData);
      // get risk config for the rb share
      String rbShareRiskConfig = CATEGORY_VALUE.getRiskConfig(rbShare);

      // get the data risk from the config
      RmRiskLevel dataRisk = this.riskLvlWithDescMap.get(rbDataRiskConfig);
      // get the share risk from the config
      RmRiskLevel shareRisk = this.riskLvlWithDescMap.get(rbShareRiskConfig);
      // get the max risk
      maxRiskLvl = getMaxRiskLvl(dataRisk, shareRisk);

    }
    if (maxRiskLvl != null) {
      RmRiskLevelLoader riskLoader = new RmRiskLevelLoader(this.serviceData);
      // get Default risk
      RmRiskLevel defaultRiskLvl = riskLoader.getDataObjectByID(riskId);
      // get Consolidated Risk
      RmRiskLevel consolidatedRisks = getConsolidatedRisks(maxRiskLvl, defaultRiskLvl);
      riskId = consolidatedRisks.getId();

    }
    return riskId;
  }

  /**
   * @param pidRmId pidRmId
   * @param pidcRmProjCharacter pidcRmProjCharacter
   * @return true if it has no parent
   * @throws DataException error during data retrieval
   */
  public boolean isProjCharEligible(final PidcRmProjCharacter pidcRmProjCharacter, final long pidRmId)
      throws DataException {


    if (!"Y".equals(pidcRmProjCharacter.getRelevant())) {
      return false;
    }

    return checkParentWithRelYes(pidcRmProjCharacter, pidRmId);

  }


  /**
   * @param pidcRmProjCharacter
   * @param pidRmId
   * @throws DataException
   */
  private boolean checkParentWithRelYes(final PidcRmProjCharacter pidcRmProjCharacter, final long pidRmId)
      throws DataException {

    if (pidcRmProjCharacter.getProjCharId() != null) {
      RmProjectCharacterLoader rmProjCharLoader = new RmProjectCharacterLoader(this.serviceData);
      // get the db rm pfoj char
      TRmProjectCharacter rmProjChar = rmProjCharLoader.getEntityObject(pidcRmProjCharacter.getProjCharId());
      // get parent rm character
      TRmProjectCharacter dbParentRmChar = rmProjChar.getTRmProjectCharacter();
      // If parant is null return true
      if (dbParentRmChar != null) {
        Set<TPidcRmProjectCharacter> tPidcRmProjectCharacters = dbParentRmChar.getTPidcRmProjectCharacters();
        for (TPidcRmProjectCharacter dbRmProjChar : tPidcRmProjectCharacters) {
          if ((dbRmProjChar.getTRmProjectCharacter().getPrjCharacterId() == dbParentRmChar.getPrjCharacterId()) &&
              (dbRmProjChar.getTPidcRmDefinition().getPidcRmId() == pidRmId)) {
            if ("Y".equals(dbRmProjChar.getRelevantFlag()) ||
                (CommonUtils.isEmptyString(dbRmProjChar.getRelevantFlag()))) {
              return false;
            }

            PidcRmProjCharacterLoader pidRmLoader = new PidcRmProjCharacterLoader(this.serviceData);
            PidcRmProjCharacter dataObjectByID = pidRmLoader.getDataObjectByID(dbRmProjChar.getPidcPcId());
            return checkParentWithRelYes(dataObjectByID, pidRmId);

          }
        }
      }
    }
    return true;
  }


}
