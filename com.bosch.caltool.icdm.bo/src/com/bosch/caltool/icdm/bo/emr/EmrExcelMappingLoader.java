/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrExcelMapping;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.emr.EmrExcelMapping;

/**
 * @author bru2cob
 */
public class EmrExcelMappingLoader extends AbstractBusinessObject<EmrExcelMapping, TEmrExcelMapping> {

  private final Map<String, Long> emissionStdMap = new ConcurrentHashMap<>();
  private final Map<String, Long> columnMap = new ConcurrentHashMap<>();
  private final Map<String, Long> measureUnitMap = new ConcurrentHashMap<>();


  /**
   * @param serviceData Service Data
   */
  public EmrExcelMappingLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_EXCL_MAPPING, TEmrExcelMapping.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrExcelMapping createDataObject(final TEmrExcelMapping entity) throws DataException {
    EmrExcelMapping excelMapping = new EmrExcelMapping();
    excelMapping.setId(entity.getExcelMappingId());
    excelMapping.setValueInExcel(entity.getValueInExcel());
    excelMapping.setVersion(entity.getVersion());
    excelMapping.setColId(entity.getTEmrColumn().getColId());
    excelMapping.setColValId(entity.getTEmrColumnValue().getColValueId());
    excelMapping.setEmissionStdId(entity.getTEmrEmissionStandard().getEmsId());
    excelMapping.setMeasureUnitId(entity.getTEmrMeasureUnit().getMuId());
    return excelMapping;
  }

  /**
   * @return the Map of excel mapping with id as key
   * @throws DataException DataException
   */
  public Map<Long, EmrExcelMapping> getAllExcelMappings() throws DataException {
    Map<Long, EmrExcelMapping> excelMappingMap = new ConcurrentHashMap<>();

    TypedQuery<TEmrExcelMapping> tQuery =
        getEntMgr().createNamedQuery(TEmrExcelMapping.GET_ALL, TEmrExcelMapping.class);

    List<TEmrExcelMapping> dbExcelMapping = tQuery.getResultList();

    for (TEmrExcelMapping dbEmrExcelMapping : dbExcelMapping) {
      excelMappingMap.put(dbEmrExcelMapping.getExcelMappingId(), createDataObject(dbEmrExcelMapping));
    }
    return excelMappingMap;
  }


  /**
   * @return the Map of excel mapping with id as key
   * @throws DataException DataException
   */
  public Map<String, Long> getMappedEms() throws DataException {


    TypedQuery<TEmrExcelMapping> tQuery =
        getEntMgr().createNamedQuery(TEmrExcelMapping.GET_ALL, TEmrExcelMapping.class);

    List<TEmrExcelMapping> dbExcelMapping = tQuery.getResultList();

    for (TEmrExcelMapping dbEmrExcelMapping : dbExcelMapping) {
      if ((dbEmrExcelMapping.getTEmrEmissionStandard() != null) && (dbEmrExcelMapping.getValueInExcel() != null)) {
        this.emissionStdMap.put(dbEmrExcelMapping.getValueInExcel().toUpperCase(),
            dbEmrExcelMapping.getTEmrEmissionStandard().getEmsId());
      }
    }
    return this.emissionStdMap;
  }

  /**
   * @return the Map of excel mapping with id as key
   * @throws DataException DataException
   */
  public Map<String, Long> getMappedMus() throws DataException {


    TypedQuery<TEmrExcelMapping> tQuery =
        getEntMgr().createNamedQuery(TEmrExcelMapping.GET_ALL, TEmrExcelMapping.class);

    List<TEmrExcelMapping> dbExcelMapping = tQuery.getResultList();

    for (TEmrExcelMapping dbEmrExcelMapping : dbExcelMapping) {
      if ((dbEmrExcelMapping.getTEmrMeasureUnit() != null) && (dbEmrExcelMapping.getValueInExcel() != null)) {
        this.measureUnitMap.put(dbEmrExcelMapping.getValueInExcel().toUpperCase(),
            dbEmrExcelMapping.getTEmrMeasureUnit().getMuId());
      }
    }
    return this.measureUnitMap;
  }

  /**
   * @return the Map of excel mapping with id as key
   * @throws DataException DataException
   */
  public Map<String, Long> getMappedCols() throws DataException {


    TypedQuery<TEmrExcelMapping> tQuery =
        getEntMgr().createNamedQuery(TEmrExcelMapping.GET_ALL, TEmrExcelMapping.class);

    List<TEmrExcelMapping> dbExcelMapping = tQuery.getResultList();

    for (TEmrExcelMapping dbEmrExcelMapping : dbExcelMapping) {
      if ((dbEmrExcelMapping.getTEmrColumn() != null) && (dbEmrExcelMapping.getValueInExcel() != null)) {
        this.columnMap.put(dbEmrExcelMapping.getValueInExcel().toUpperCase(),
            dbEmrExcelMapping.getTEmrColumn().getColId());
      }
    }
    return this.columnMap;
  }

  /**
   * @param colName column Name
   * @param excelValue excel Value
   * @return the Map of excel mapping with id as key
   * @throws DataException DataException
   */
  public Long getMappedColVal(final String colName, final String excelValue) throws DataException {
    Long colValID = null;

    TypedQuery<TEmrExcelMapping> tQuery =
        getEntMgr().createNamedQuery(TEmrExcelMapping.GET_ALL, TEmrExcelMapping.class);

    List<TEmrExcelMapping> dbExcelMapping = tQuery.getResultList();

    for (TEmrExcelMapping dbEmrExcelMapping : dbExcelMapping) {
      if (validateTEmrColumn(colName, dbEmrExcelMapping) &&
          (dbEmrExcelMapping.getValueInExcel().equalsIgnoreCase(excelValue)) &&
          "Y".equals(dbEmrExcelMapping.getTEmrColumn().getNomalizedFlag())) {
        colValID = dbEmrExcelMapping.getTEmrColumnValue().getColValueId();
      }
    }
    return colValID;
  }

  /**
   * @param colName
   * @param dbEmrExcelMapping
   * @return
   */
  private boolean validateTEmrColumn(final String colName, final TEmrExcelMapping dbEmrExcelMapping) {
    return (dbEmrExcelMapping.getTEmrColumnValue() != null) && (dbEmrExcelMapping.getTEmrColumn() != null) &&
        dbEmrExcelMapping.getTEmrColumn().getColumnName().equalsIgnoreCase(colName);
  }
}
