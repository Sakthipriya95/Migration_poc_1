/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.Map;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.rm.RmCategory;
import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;
import com.bosch.caltool.icdm.model.rm.RmMetaData;
import com.bosch.caltool.icdm.model.rm.RmProjectCharacter;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel;
import com.bosch.caltool.icdm.model.rm.RmCategory.CATEGORY_TYPE;


/**
 * Load the Risk levels
 *
 * @author rgo7cob
 */
public class RmMetaDataLoader {

  private Map<Long, RmRiskLevel> risklvlMap;
  private Map<Long, RmCategory> categoryMap;
  private Map<Long, RmProjectCharacter> projCharMap;
  private Map<Long, RmCategoryMeasures> measuresMap;
  private final ServiceData serviceData;


  /**
   * @param serviceData service Data
   */
  public RmMetaDataLoader(final ServiceData serviceData) {

    this.serviceData = serviceData;
  }


  /**
   * @throws DataException create the meta data information
   */
  public void createMetaData() throws DataException {

    RmRiskLevelLoader loader = new RmRiskLevelLoader(this.serviceData);

    // Fetch all risk
    this.risklvlMap = loader.getRiskLevels();


    RmCategoryLoader categoryLoader = new RmCategoryLoader(this.serviceData);
    // Fetch all categories
    this.categoryMap = categoryLoader.getCategories();


    RmProjectCharacterLoader projCharLoader = new RmProjectCharacterLoader(this.serviceData);
    // Fetch all project characters
    this.projCharMap = projCharLoader.getProjCharacters();

    // Fetch all category measures
    RmCategoryMeasuresLoader meauresLoader = new RmCategoryMeasuresLoader(this.serviceData);
    this.measuresMap = meauresLoader.getMeasures();
  }

  /**
   * @return create and return meta data object
   */
  public RmMetaData getMetaDataObject() {
    RmMetaData metaData = new RmMetaData();
    metaData.setProjCharMap(this.projCharMap);
    metaData.setRiskLevelMap(this.risklvlMap);
    metaData.setRmMeasuresMap(this.measuresMap);
    for (RmCategory data : this.categoryMap.values()) {
      if (data.getCategoryType().equals(CATEGORY_TYPE.DATA)) {
        metaData.getRbInputDataCatagoryMap().put(data.getId(), data);
      }
      else if (data.getCategoryType().equals(CATEGORY_TYPE.SHARE)) {
        metaData.getRbSwShareCategoryMap().put(data.getId(), data);
      }
      else {
        metaData.getRbColumnCategoryMap().put(data.getId(), data);
      }
    }
    return metaData;
  }
}
