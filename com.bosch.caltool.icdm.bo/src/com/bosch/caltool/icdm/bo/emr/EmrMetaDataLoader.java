/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.Map;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.emr.EmrCategory;
import com.bosch.caltool.icdm.model.emr.EmrMetaData;

/**
 * @author bru2cob
 */
public class EmrMetaDataLoader {

  private Map<String, EmrCategory> categoryMap;
  private Map<String, Long> columnMap;
  private Map<String, Long> measureUnitMap;
  private Map<String, Long> emissionStdIdMap;
  private final ServiceData serviceData;
  private EmrExcelMappingLoader excelMappingLoader;

  /**
   * @param serviceData service Data
   */
  public EmrMetaDataLoader(final ServiceData serviceData) {

    this.serviceData = serviceData;
  }

  /**
   * @throws DataException create the meta data information
   */
  public void createMetaData() throws DataException {

    EmrCategoryLoader categoryLoader = new EmrCategoryLoader(this.serviceData);
    // Fetch all categories
    this.categoryMap = categoryLoader.getCategories();

    this.excelMappingLoader = new EmrExcelMappingLoader(this.serviceData);
    this.columnMap = this.excelMappingLoader.getMappedCols();
    this.measureUnitMap = this.excelMappingLoader.getMappedMus();
    this.emissionStdIdMap = this.excelMappingLoader.getMappedEms();
  }

  /**
   * @return create and return meta data object
   */
  public EmrMetaData getMetaDataObject() {
    EmrMetaData metaData = new EmrMetaData();
    metaData.setCategoryMap(this.categoryMap);
    metaData.setColumnMap(this.columnMap);
    metaData.setEmissionStdMap(this.emissionStdIdMap);
    metaData.setMeasureUnitMap(this.measureUnitMap);
    return metaData;
  }

  public Long getMappedColVal(final String colName, final String excelValue) throws DataException {
    return this.excelMappingLoader.getMappedColVal(colName, excelValue);
  }
}
