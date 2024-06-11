/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmProjectCharacter;
import com.bosch.caltool.icdm.database.entity.apic.TRmCharacterCategoryMatrix;
import com.bosch.caltool.icdm.model.rm.ConsolidatedRisks;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.RmCategory;
import com.bosch.caltool.icdm.model.rm.RmCategory.CATEGORY_TYPE;
import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel;


/**
 * Load the Risk levels
 *
 * @author rgo7cob
 */
public class ConsolidatedRiskLoader {

  private final ServiceData serviceData;

  /**
   * @param serviceData serviceData
   */
  public ConsolidatedRiskLoader(final ServiceData serviceData) {
    this.serviceData = serviceData;
  }

  /**
   * @param pidRmId pidRmId
   * @return set of FunctionDetails
   * @throws DataException DataException
   */
  public ConsolidatedRisks getConsolidatedRisk(final long pidRmId) throws DataException {


    PidcRmProjCharacterLoader rmpidcLoader = new PidcRmProjCharacterLoader(this.serviceData);
    Map<Long, PidcRmProjCharacter> pidRmProjChars = rmpidcLoader.getPidRmProjChar(pidRmId);
    Set<TPidcRmProjectCharacter> dbPidRmProjChars =
        new PidcRmDefinitionLoader(this.serviceData).getEntityObject(pidRmId).getTPidcRmProjectCharacters();

    RmCategoryLoader categoryLoader = new RmCategoryLoader(this.serviceData);
    Map<Long, RmCategory> categories = categoryLoader.getCategories();
    Map<Long, Long> riskMap = createConsolidatedRiskMap(pidRmProjChars, dbPidRmProjChars, pidRmId);
    ConsolidatedRisks consolidatedRisks = new ConsolidatedRisks();


    consolidatedRisks.setCategoryMap(categories);

    RmCategoryMeasuresLoader loader = new RmCategoryMeasuresLoader(this.serviceData);
    Map<Long, RmCategoryMeasures> measuresMap = loader.getMeasures();
    Map<Long, Long> riskCatMap = new ConcurrentHashMap<>();
    prefillRiskCatMap(riskCatMap, categories);
    setMeasures(consolidatedRisks, measuresMap, riskMap, riskCatMap);
    consolidatedRisks.setCatRiskMap(riskCatMap);

    return consolidatedRisks;
  }


  /**
   * @param riskCatMap
   * @param categories
   */
  private void prefillRiskCatMap(final Map<Long, Long> riskCatMap, final Map<Long, RmCategory> categories)
      throws DataException {
    RiskEvaluator evalutor = new RiskEvaluator(this.serviceData);
    RmRiskLevel notDefinedRisk = evalutor.getNotDefinedRisk();
    // prefill for the risk values.not defined values
    for (RmCategory rmCategory : categories.values()) {
      if ((CATEGORY_TYPE.IMPACTED_WITH_RISK == rmCategory.getCategoryType()) ||
          (CATEGORY_TYPE.IMPACT_WITHOUT_RISK == rmCategory.getCategoryType())) {
        riskCatMap.put(rmCategory.getId(), notDefinedRisk.getId());
      }
    }

  }

  /**
   * @param consolidatedRisks
   * @param measuresMap
   * @param riskMap
   * @param riskCatMap
   * @throws DataException
   */
  private void setMeasures(final ConsolidatedRisks consolidatedRisks, final Map<Long, RmCategoryMeasures> measuresMap,
      final Map<Long, Long> riskMap, final Map<Long, Long> riskCatMap) throws DataException {
    Map<Long, RmCategoryMeasures> measureMap = new ConcurrentHashMap<>();
    for (Entry<Long, Long> riskCatEntry : riskMap.entrySet()) {
      Long riskId = riskCatEntry.getValue();
      Long catId = riskCatEntry.getKey();

      for (RmCategoryMeasures catgoryMeasure : measuresMap.values()) {
        if ((catgoryMeasure.getCategoryId().longValue() == catId.longValue()) &&
            (catgoryMeasure.getRiskLevel().longValue() == riskId.longValue())) {
          measureMap.put(catId, catgoryMeasure);
          // Override with the new values.
          riskCatMap.put(catId, riskId);
        }

      }

    }
    consolidatedRisks.setCategoryMeasureMap(measureMap);
    RmRiskLevelLoader riskLoader = new RmRiskLevelLoader(this.serviceData);
    consolidatedRisks.setRiskLevelMap(riskLoader.getRiskLevels());
  }

  /**
   * @param categoryRiskMap
   * @param pidRmProjChars
   * @param dbPidRmProjChars
   * @param pidRmId
   * @param categories
   */
  private Map<Long, Long> createConsolidatedRiskMap(final Map<Long, PidcRmProjCharacter> pidRmProjChars,
      final Set<TPidcRmProjectCharacter> dbPidRmProjChars, final long pidRmId) throws DataException {

    RmRiskLevelLoader riskLevelLoader = new RmRiskLevelLoader(this.serviceData);
    RiskEvaluator evaluator = new RiskEvaluator(this.serviceData);
    Map<Long, Long> consolidatedRiskMap = new ConcurrentHashMap<>();


    if (dbPidRmProjChars != null) {

      Map<Long, List<RmRiskLevel>> fillCategoryMap =
          fillcatRiskMap(pidRmProjChars, dbPidRmProjChars, pidRmId, riskLevelLoader, evaluator);

      for (Entry<Long, List<RmRiskLevel>> riskMapEntry : fillCategoryMap.entrySet()) {
        Long categoryId = riskMapEntry.getKey();
        List<RmRiskLevel> riskList = riskMapEntry.getValue();
        RmRiskLevel maxRiskLvl = evaluator.getMaxRiskLvl(riskList.toArray(new RmRiskLevel[riskList.size()]));
        consolidatedRiskMap.put(categoryId, maxRiskLvl.getId());
      }


    }

    return consolidatedRiskMap;
  }


  /**
   * @param pidRmProjChars
   * @param dbPidRmProjChars
   * @param pidRmId
   * @param categoryRiskMap
   * @param riskLevelLoader
   * @param evaluator
   * @throws DataException
   */
  private Map<Long, List<RmRiskLevel>> fillcatRiskMap(final Map<Long, PidcRmProjCharacter> pidRmProjChars,
      final Set<TPidcRmProjectCharacter> dbPidRmProjChars, final long pidRmId, final RmRiskLevelLoader riskLevelLoader,
      final RiskEvaluator evaluator) throws DataException {

    Map<Long, List<RmRiskLevel>> categoryRiskMap = new ConcurrentHashMap<>();
    List<RmRiskLevel> riskLevelList;
    for (TPidcRmProjectCharacter dbPidRmProjChar : dbPidRmProjChars) {


      PidcRmProjCharacter pidcRmProjCharacter = pidRmProjChars.get(dbPidRmProjChar.getPidcPcId());
      if (evaluator.isProjCharEligible(pidcRmProjCharacter, pidRmId)) {
        Set<TRmCharacterCategoryMatrix> tRmCharacterCategoryMatrixs =
            dbPidRmProjChar.getTRmProjectCharacter().getTRmCharacterCategoryMatrixs();

        for (TRmCharacterCategoryMatrix tRmCharacterCategoryMatrix : tRmCharacterCategoryMatrixs) {
          Long categoryId = tRmCharacterCategoryMatrix.getTRmCategory().getCategoryId();

          // get the risk based on rb data and share risk.
          Long riskId = evaluator.getEvaluatedRiskLevel(dbPidRmProjChar,
              tRmCharacterCategoryMatrix.getTRmRiskLevel().getRiskLevelId(),
              tRmCharacterCategoryMatrix.getTRmCategory().getCatType());

          // If the Proj char is parent and also not defined then risk is high


          RmRiskLevel riskLevel = riskLevelLoader.getDataObjectByID(riskId);

          if (categoryRiskMap.get(categoryId) == null) {
            riskLevelList = new ArrayList<>();

          }
          else {
            riskLevelList = categoryRiskMap.get(categoryId);

          }
          riskLevelList.add(riskLevel);
          RmCategoryLoader categoryLoader = new RmCategoryLoader(this.serviceData);
          RmCategory rmCategory = categoryLoader.getDataObjectByID(categoryId);
          if ((CATEGORY_TYPE.IMPACTED_WITH_RISK == rmCategory.getCategoryType()) ||
              (CATEGORY_TYPE.IMPACT_WITHOUT_RISK == rmCategory.getCategoryType())) {
            categoryRiskMap.put(categoryId, riskLevelList);
          }
        }
      }

    }
    return categoryRiskMap;
  }

}

