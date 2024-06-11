/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFile;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFileData;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrPidcVariant;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.emr.EmrFileEmsVariantMapping;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;

/**
 * @author bru2cob
 */
public class EmrPidcVariantLoader extends AbstractBusinessObject<EmrPidcVariant, TEmrPidcVariant> {

  /**
   * @param serviceData Service Data
   */
  public EmrPidcVariantLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_PIDC_VARIANT, TEmrPidcVariant.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrPidcVariant createDataObject(final TEmrPidcVariant entity) throws DataException {
    EmrPidcVariant pidcVar = new EmrPidcVariant();
    pidcVar.setEmissionStdId(entity.getTEmrEmissionStandard().getEmsId());
    pidcVar.setEmrFileId(entity.getTEmrFile().getEmrFileId());
    pidcVar.setId(entity.getEmrPvId());
    pidcVar.setPidcVariantId(entity.getTabvProjectVariant().getVariantId());
    pidcVar.setVersion(entity.getVersion());
    pidcVar.setEmrVariant(entity.getEmrVariant() == null ? 0L : entity.getEmrVariant());
    return pidcVar;
  }


  /**
   * @return the Map of pidc variant with id as key
   * @throws DataException DataException
   */
  public Map<Long, EmrPidcVariant> getAllPidcVariants() throws DataException {
    Map<Long, EmrPidcVariant> pidcVarianttMap = new ConcurrentHashMap<>();

    TypedQuery<TEmrPidcVariant> tQuery = getEntMgr().createNamedQuery(TEmrPidcVariant.GET_ALL, TEmrPidcVariant.class);

    List<TEmrPidcVariant> dbMeasureUnit = tQuery.getResultList();

    for (TEmrPidcVariant dbEmrMeasureUnit : dbMeasureUnit) {
      pidcVarianttMap.put(dbEmrMeasureUnit.getEmrPvId(), createDataObject(dbEmrMeasureUnit));
    }
    return pidcVarianttMap;
  }

  /**
   * Gets list of File-Ems-Variant mapping
   *
   * @param emrFileIds Emr FileIds
   * @return mapped list
   * @throws DataException dataException
   */
  public EmrFileEmsVariantMapping getFileVariantEmsMapping(final Set<Long> emrFileIds) throws DataException {
    EmrFileEmsVariantMapping output = new EmrFileEmsVariantMapping();
    EmrFileLoader fileLdr = new EmrFileLoader(getServiceData());
    EmrPidcVariantLoader emrVariantLdr = new EmrPidcVariantLoader(getServiceData());
    PidcVariantLoader pidcVariantLdr = new PidcVariantLoader(getServiceData());
    Long pidcVersionId = null;
    for (Long emrFileId : emrFileIds) {
      TEmrFile dbEmrFile = fileLdr.getEntityObject(emrFileId);
      output.addToEmrFilesMap(fileLdr.createDataObject(dbEmrFile));
      pidcVersionId = dbEmrFile.getTPidcVersion().getPidcVersId();
      getDetailsFromEmrFileData(output, dbEmrFile);

      Set<TEmrPidcVariant> variants = dbEmrFile.getTEmrPidcVariants();
      if (CommonUtils.isNotEmpty(variants)) {
        for (TEmrPidcVariant variant : variants) {
          // EMR Pidc Variants
          output.addToPidcVariantMap(dbEmrFile.getEmrFileId(), emrVariantLdr.createDataObject(variant));
        }
      }
    }

    if (pidcVersionId != null) {
      output.setPidcVariants(pidcVariantLdr.getVariants(pidcVersionId, false));
    }

    return output;
  }

  /**
   * @param output
   * @param dbEmrFile
   * @throws DataException
   */
  private void getDetailsFromEmrFileData(final EmrFileEmsVariantMapping output, final TEmrFile dbEmrFile)
      throws DataException {
    EmrEmissionStandardLoader emsLdr = new EmrEmissionStandardLoader(getServiceData());
    if (dbEmrFile.getTEmrFileData() != null) {
      for (TEmrFileData fileDataEntity : dbEmrFile.getTEmrFileData()) {
        // All Emission standard IDs in the EMR file
        output.addToEmrEmsMap(dbEmrFile.getEmrFileId(),
            emsLdr.createDataObject(fileDataEntity.getTEmrEmissionStandardProcedure()));
      }

      /*
       * Fetch the set of distinct Variants available in EMR file. FuelTypeNumber is a bitset value of 16 bits, where
       * the first 8 bits is the Emr variant and the next 8 bits, the fuelType. Convert the fuelTypeNumber to 16 bits by
       * appending zeros to the left and then fetch the first 8 bits to get the EMR variant value
       */
      Set<Long> distinctEmrVariants = dbEmrFile.getTEmrFileData().stream()
          .filter(emrFileData -> (emrFileData.getFuelTypeNumber() != null) && (emrFileData.getFuelTypeNumber() > 0))
          .map(data -> Long.parseLong(StringUtils.leftPad(String.valueOf(data.getFuelTypeNumber()), 16, "0")) /
              100000000)
          .distinct().collect(Collectors.toSet());

      // Add the EMR Variant and variant Info string for the emission standard procedure to output map
      if (CommonUtils.isNotEmpty(distinctEmrVariants)) {
        addEmrVariantInfoToOutputMap(output, dbEmrFile, distinctEmrVariants);
      }
    }
  }

  /**
   * @param output EmrFileEmsVariantMapping information
   * @param dbEmrFile Data from TemrFile table for the EMR file
   * @param distinctEmrVariants List of all distinct values of EMRVariant from the Emr file
   */
  private void addEmrVariantInfoToOutputMap(final EmrFileEmsVariantMapping output, final TEmrFile dbEmrFile,
      final Set<Long> distinctEmrVariants) {
    for (long emsId : output.getEmissionStandard().keySet()) {
      // For each of the standard procedure, fetch only the rows for 'Transmission', 'Additional Information' and
      // 'Regulation limit region'
      List<TEmrFileData> emrFileDataList = dbEmrFile.getTEmrFileData().stream()
          .filter(fileData -> ((fileData.getTEmrEmissionStandardProcedure().getEmsId() == emsId) &&
              (fileData.getTEmrColumn().getColumnName().equalsIgnoreCase("Transmission") ||
                  fileData.getTEmrColumn().getColumnName().equalsIgnoreCase("Additional Information") ||
                  fileData.getTEmrColumn().getColumnName().equalsIgnoreCase("Regulation Limit Region"))))
          .collect(Collectors.toList());

      if (CommonUtils.isNotEmpty(emrFileDataList)) {
        /*
         * For each of the distinct variant, fetch distinct column values for the 3 columns ('Transmission', 'Additional
         * Information' and 'Regulation limit region')and frame a string which would be appended to the sheet name to
         * differentaite each sheet
         */
        for (Long emrVariant : distinctEmrVariants) {
          String variantInfo = getVariantInfoForFuelType(emrFileDataList, emrVariant);
          // Add the EMR Variant and variant Info string for the emission standard procedure to output map
          if (CommonUtils.isNotEmptyString(variantInfo)) {
            output.addToEmrVariantInfoMap(emsId, emrVariant, variantInfo);
          }
        }
      }
    }
  }

  /**
   * Frame the variant info string that needs to be appended to emission standard procedure name so that it can be
   * differentiated based on variant
   *
   * @param emrFileDataList List of EMRFileData containing data of 3 columns ('Transmission','Additional Information'
   *          and 'Regulation limit region')
   * @param emrVariant EMRVariant value
   * @return
   */
  private String getVariantInfoForFuelType(final List<TEmrFileData> emrFileDataList, final Long emrVariant) {
    String variantInfo = null;
    Set<String> variantVals = new LinkedHashSet<>();

    // Fetch emrFiledata for the column 'Transmission'
    Optional<TEmrFileData> transmisisonFileData = emrFileDataList.stream()
        .filter(
            fileData -> ((Long.parseLong(StringUtils.leftPad(String.valueOf(fileData.getFuelTypeNumber()), 16, "0")) /
                100000000) == emrVariant) && fileData.getTEmrColumn().getColumnName().equalsIgnoreCase("Transmission"))
        .findFirst();
    // Append the 'Transmission' column value to variantInfo string if values are present
    if (transmisisonFileData.isPresent()) {
      TEmrFileData emrFileData = transmisisonFileData.get();
      getColumnValueStr(emrFileData, variantVals);
    }

    // Fetch emrFiledata for the column 'Additional Information'
    Optional<TEmrFileData> addtnlInfoFileData = emrFileDataList.stream()
        .filter(
            fileData -> ((Long.parseLong(StringUtils.leftPad(String.valueOf(fileData.getFuelTypeNumber()), 16, "0")) /
                100000000) == emrVariant) &&
                fileData.getTEmrColumn().getColumnName().equalsIgnoreCase("Additional Information"))
        .findFirst();
    // Append the 'Additional Information' column value to variantInfo string if values are present
    if (addtnlInfoFileData.isPresent()) {
      TEmrFileData emrFileData = addtnlInfoFileData.get();
      getColumnValueStr(emrFileData, variantVals);
    }

    // Fetch emrFiledata for the column 'Regulation limit region'
    Optional<TEmrFileData> regLimitFileData = emrFileDataList.stream()
        .filter(
            fileData -> ((Long.parseLong(StringUtils.leftPad(String.valueOf(fileData.getFuelTypeNumber()), 16, "0")) /
                100000000) == emrVariant) &&
                fileData.getTEmrColumn().getColumnName().equalsIgnoreCase("Regulation Limit Region"))
        .findFirst();
    // Append the 'Regulation Limit region' column value to variantInfo string if values are present
    if (regLimitFileData.isPresent()) {
      TEmrFileData emrFileData = regLimitFileData.get();
      getColumnValueStr(emrFileData, variantVals);
    }

    StringBuilder variantInfoBuilder = new StringBuilder();
    variantInfoBuilder.append("(");
    variantVals.forEach(val -> variantInfoBuilder.append(val).append("/"));

    // Remove '/' at the end and append close braces
    if (variantInfoBuilder.length() > 1) {
      variantInfo = variantInfoBuilder.substring(0, variantInfoBuilder.length() - 1) + ")";
    }
    return variantInfo;
  }

  /**
   * @param emrFileData EmrFileData
   * @param variantVals Set of EMR variant values
   */
  private void getColumnValueStr(final TEmrFileData emrFileData, final Set<String> variantVals) {
    String colValue = null;
    if (null != emrFileData.getTEmrColumnValue()) {
      colValue = emrFileData.getTEmrColumnValue().getColValue();
    }
    else if (null != emrFileData.getValueText()) {
      colValue = emrFileData.getValueText();
    }
    else if (null != emrFileData.getValueNum()) {
      colValue = emrFileData.getValueNum().toString();
    }
    if (CommonUtils.isNotEmptyString(colValue)) {
      variantVals.add(colValue);
    }
  }
}
