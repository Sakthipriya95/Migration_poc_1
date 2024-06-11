/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacterExt;
import com.bosch.caltool.icdm.model.rm.RmCategory;
import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;
import com.bosch.caltool.icdm.model.rm.RmMetaData;
import com.bosch.caltool.icdm.model.rm.RmProjectCharacter;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel;

/**
 * @author gge6cob
 */
public class PidcRiskResultHandler {

  private final RmMetaData metaData;

  private final Map<Long, String> allRiskLevelMap = new HashMap<>();

  private final Map<String, String> allRiskLvlCodeMap = new HashMap<>();

  private final Map<Long, String> allRiskMeasureMap = new HashMap<>();

  private final Map<Long, RmProjectCharacter> allProjectCharMap = new HashMap<>();

  private final Map<Long, Integer> allRiskImpactColumn = new HashMap<>();

  private final Map<Integer, String> allRiskImpactHeader = new HashMap<>();

  /** Map to hold category id of the corresponding column */
  private final Map<Integer, Long> colIndexCatIdMap = new HashMap<>();

  private final Map<Long, Long> allProjCharHierarchy = new HashMap<>();

  /**
   * Risk Level value (low, high, meium)
   */
  public enum RISK_CATEGORY_TYPE {
                                  /**
                                   * Impact Column
                                   */
                                  N("N"),
                                  /**
                                   * Impact Column
                                   */
                                  I("I"),
                                  /**
                                   * Probability Column
                                   */
                                  S("S"),
                                  /**
                                   * Probability - Yes/No
                                   */
                                  D("D");

    private final String uiType;

    RISK_CATEGORY_TYPE(final String uiType) {
      this.uiType = uiType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the risk flag object for the given db type
     *
     * @param uiType ui literal of flag
     * @return the flag object
     */
    public static RISK_CATEGORY_TYPE getType(final String uiType) {
      for (RISK_CATEGORY_TYPE type : RISK_CATEGORY_TYPE.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return RISK_CATEGORY_TYPE.N;
    }
  }

  /**
   * Constructor
   *
   * @param metaData WS metadata
   */
  public PidcRiskResultHandler(final RmMetaData metaData) {
    this.metaData = metaData;
    getAllRiskLevel();
    getAllRiskMeasure();
    getAllProjectCharacter();
  }

  /**
   * @return All RB Risk Level metadata (Id:riskValue)
   */
  public Map<Long, String> getAllRiskLevel() {
    if (CommonUtils.isNotEmpty(this.allRiskLevelMap)) {
      return this.allRiskLevelMap;
    }
    for (long riskLevelId : this.metaData.getRiskLevelMap().keySet()) {
      RmRiskLevel riskLevel = this.metaData.getRiskLevelMap().get(riskLevelId);
      this.allRiskLevelMap.put(riskLevelId, riskLevel.getName());
      this.allRiskLvlCodeMap.put(riskLevel.getName(), riskLevel.getCode());
    }
    return this.allRiskLevelMap;
  }

  /**
   * @return All RB Risk Measure metadata(Id:Name)
   */
  public Map<Long, String> getAllRiskMeasure() {
    if (CommonUtils.isNotEmpty(this.allRiskMeasureMap)) {
      return this.allRiskMeasureMap;
    }
    for (long id : this.metaData.getRmMeasuresMap().keySet()) {
      RmCategoryMeasures data = this.metaData.getRmMeasuresMap().get(id);
      this.allRiskMeasureMap.put(id, data.getName());
    }
    return this.allRiskMeasureMap;
  }

  /**
   * @return All RB Project Characteristics metadata(Id:Name)
   */
  public Map<Long, RmProjectCharacter> getAllProjectCharacter() {
    if (CommonUtils.isNotEmpty(this.allProjectCharMap)) {
      return this.allProjectCharMap;
    }
    for (long id : this.metaData.getProjCharMap().keySet()) {
      RmProjectCharacter data = this.metaData.getProjCharMap().get(id);
      this.allProjectCharMap.put(id, data);

      // Parent-Child node
      groupProjectCharacter(data);
    }
    return this.allProjectCharMap;
  }

  /**
   * @param data
   */
  private void groupProjectCharacter(final RmProjectCharacter data) {
    this.allProjCharHierarchy.put(data.getId(), data.getParentId());
  }

  /**
   * @param startOfImpactColumn column index
   */
  public void sortColumnCategory(final int startOfImpactColumn) {
    int columnRank = startOfImpactColumn;
    // Add to maps based on categroy type
    List<RmCategory> impactColumnList = new ArrayList<>();
    if (CommonUtils.isNotEmpty(this.metaData.getRbColumnCategoryMap())) {
      for (RmCategory e : this.metaData.getRbColumnCategoryMap().values()) {
        impactColumnList.add(e);
      }
      // Sort impact columns for ordering
      Collections.sort(impactColumnList);
      for (RmCategory e : impactColumnList) {
        this.allRiskImpactColumn.put(e.getId(), columnRank);
        this.colIndexCatIdMap.put(columnRank, e.getId());
        this.allRiskImpactHeader.put(columnRank, e.getName());
        columnRank++;
      }
    }
  }

  /**
   * @param defId Risk definition Id
   * @param webserviceOutput WS output on mapping
   * @param parentSet parent characteristics
   * @return list of mappings for nattable
   */
  public SortedSet<PidcRMCharacterMapping> getPidcProjCharSet(final Long defId,
      final Set<PidcRmProjCharacterExt> webserviceOutput, final Map<Long, PidcRMCharacterMapping> parentSet) {

    HashMap<Long, PidcRMCharacterMapping> allData = new HashMap<>();
    SortedSet<PidcRMCharacterMapping> dataSet = new TreeSet<>();

    if (CommonUtils.isNotEmpty(getAllProjectCharacter())) {
      for (RmProjectCharacter character : this.allProjectCharMap.values()) {
        PidcRMCharacterMapping rowObject = new PidcRMCharacterMapping();

        // Definition Id
        rowObject.setRiskDefId(defId);

        // Character Id
        long charId = character.getId();
        rowObject.setProjCharId(charId);
        rowObject.setProjectCharacter(this.allProjectCharMap.get(charId).getName());

        // Parent Id
        if (this.allProjCharHierarchy.containsKey(charId)) {
          rowObject.setParentProjCharId(this.allProjCharHierarchy.get(charId));
        }

        // is A Parent
        if (rowObject.getParentProjCharId() == 0l) {
          parentSet.put(rowObject.getProjCharId(), rowObject);
        }

        // is A Secondary Parent
        if (this.allProjCharHierarchy.containsKey(charId) && this.allProjCharHierarchy.containsValue(charId)) {
          parentSet.put(rowObject.getProjCharId(), rowObject);
        }

        // Relevant
        rowObject.setRelevantYes(false);
        rowObject.setRelevantNo(false);
        rowObject.setRelevantNA(true);

        rowObject.setAllRBInputDataCatgMap(this.metaData.getRbInputDataCatagoryMap());
        rowObject.setAllRBShareCatgMap(this.metaData.getRbSwShareCategoryMap());

        allData.put(charId, rowObject);
        dataSet.add(rowObject);
      }
    }

    // Override rows to set specific risk fields
    if (CommonUtils.isNotEmpty(webserviceOutput)) {
      setRiskValues(allData, webserviceOutput);
    }
    return dataSet;
  }

  /**
   * @param webserviceOutput
   * @param allData
   * @param metaData2
   */
  private void setRiskValues(final HashMap<Long, PidcRMCharacterMapping> allData,
      final Set<PidcRmProjCharacterExt> webserviceOutput) {

    for (PidcRmProjCharacterExt output : webserviceOutput) {

      PidcRMCharacterMapping rowObject = allData.get(output.getPidRmChar().getProjCharId());

      rowObject.setPidcRmChar(output.getPidRmChar());

      rowObject.setVersion(output.getPidRmChar().getVersion());

      // is Relevant
      rowObject.setRelevantFlags(output.getPidRmChar().getRelevant());

      // RB share
      long shareId = output.getPidRmChar().getRbShareId();
      rowObject.setRbSoftwareShare(this.metaData.getRbSwShareCategoryMap().get(shareId).getValue());

      // RB initial data
      long initDtId = output.getPidRmChar().getRbDataId();
      String inputDataByRB = this.metaData.getRbInputDataCatagoryMap().get(initDtId).getName();
      rowObject.setInputDataByRB(inputDataByRB);

      // get Risk Matrix output
      getRiskMatrix(rowObject, output);
    }
  }

  /**
   * @param rowObject Nattable row object
   * @param output map of risk levels for the row
   */
  public void getRiskMatrix(final PidcRMCharacterMapping rowObject, final PidcRmProjCharacterExt output) {
    // Impact
    Map<Integer, String> riskImpactMap = new HashMap<>();
    Map<Integer, Long> colIndexRiskIdMap = new HashMap<>();

    // catId:levelId
    for (Long catId : output.getCategoryRiskMap().keySet()) {
      if (this.allRiskImpactColumn.containsKey(catId)) {
        int colIndex = this.allRiskImpactColumn.get(catId);
        long riskLvlId = output.getCategoryRiskMap().get(catId);
        riskImpactMap.put(colIndex, this.allRiskLevelMap.get(riskLvlId));
        colIndexRiskIdMap.put(colIndex, riskLvlId);
      }
    }
    rowObject.setRiskImpactMap(riskImpactMap);
    rowObject.setColIndexRiskIdMap(colIndexRiskIdMap);
  }

  /**
   * @param startOfImpactColumn column index of impact columns
   * @return map of risk impact headers
   */
  public Map<Integer, String> getImpactColumnHeaders(final int startOfImpactColumn) {
    sortColumnCategory(startOfImpactColumn);
    return this.allRiskImpactHeader;
  }


  /**
   * @return the colIndexCatIdMap
   */
  public Map<Integer, Long> getColIndexCatIdMap() {
    return this.colIndexCatIdMap;
  }

  /**
   * @return the allProjCharHierarchy
   */
  public Map<Long, Long> getAllProjCharHierarchy() {
    return this.allProjCharHierarchy;
  }

  /**
   * @return the metaData
   */
  public RmMetaData getMetaData() {
    return this.metaData;
  }

  /**
   * @return map of All Risk Level code (riskValue:riskCode)
   */
  public Map<String, String> getAllRiskCodeMap() {
    return this.allRiskLvlCodeMap;
  }

  /**
   * @return map of all RB share values
   */
  public Map<Long, RmCategory> getAllRBSwShareCategoryMap() {
    return this.metaData.getRbSwShareCategoryMap();
  }

  /**
   * @return map of all RB Input Data values
   */
  public Map<Long, RmCategory> getAllRBInputDataMap() {
    return this.metaData.getRbInputDataCatagoryMap();
  }
}