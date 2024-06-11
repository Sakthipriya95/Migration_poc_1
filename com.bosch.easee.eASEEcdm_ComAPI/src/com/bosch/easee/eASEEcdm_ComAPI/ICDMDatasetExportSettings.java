package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.*;


/**
 * ICDMDatasetExportSettings Interface
 */
@IID("{0A1C2080-E262-4BA7-BAC2-D83AF14B2926}")
public interface ICDMDatasetExportSettings extends Com4jObject {
    /**
     * property RequiredValidationDataQuality
     */
    @VTID(7)
    void requiredValidationDataQuality(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality newVal);

    /**
     * property RequiredValidationDataQuality
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality requiredValidationDataQuality();

    /**
     * property RequiredIntegrationDataQuality
     */
    @VTID(9)
    void requiredIntegrationDataQuality(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality newVal);

    /**
     * property RequiredIntegrationDataQuality
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality requiredIntegrationDataQuality();

    /**
     * property Path
     */
    @VTID(11)
    void path(java.lang.String path);

    /**
     * property Path
     */
    @VTID(12)
    java.lang.String path();

    /**
     * method GetOption
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMOption getOption(
        com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionCategoryId iOptionCategoryId,
        int iOptionId);

    /**
     * GetDatasetSettings
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetSettings getDatasetSettings(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset pDataset);

    /**
     * GetDatasetSettingsByVersionNumber
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetSettings getDatasetSettingsByVersionNumber(
        int versionNumber);

    /**
     * SetDatasetSettings
     */
    @VTID(16)
    void setDatasetSettings(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset pDataset,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetSettings iSettings);

    /**
     * SetDatasetSettingsByVersionNumber
     */
    @VTID(17)
    void setDatasetSettingsByVersionNumber(int versionNumber,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetSettings iSettings);

    /**
     * HasDatasetSettingsByVersionNumber
     */
    @VTID(18)
    boolean hasDatasetSettingsByVersionNumber(int versionNumber);

    /**
     * property Project
     */
    @VTID(19)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject project();

    /**
     * property ProjectContext
     */
    @VTID(20)
    int projectContext();

    /**
     * property ExportType
     */
    @VTID(21)
    int exportType();

    /**
     * property FileType
     */
    @VTID(22)
    void fileType(java.lang.String fileType);

    /**
     * property FileType
     */
    @VTID(23)
    java.lang.String fileType();

    /**
     * method SetEnableMemoryConversion
     */
    @VTID(24)
    void setEnableMemoryConversion(boolean iEnable, int segements);

    /**
     * property GetEnableMemoryConversion
     */
    @VTID(25)
    boolean getEnableMemoryConversion();

    /**
     * property GetMemorySegements
     */
    @VTID(26)
    int getMemorySegements();

    /**
     * property ExportSettings
     */
    @VTID(27)
    void exportSettings(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmDatasetExportSettings oSettigs);

    /**
     * property ExportSettings
     */
    @VTID(28)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmDatasetExportSettings exportSettings();

    /**
     * property QualityDataProperties
     */
    @VTID(29)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection qualityDataProperties();

    @VTID(29)
    @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough =  {
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class}
    )
    java.lang.Object qualityDataProperties(int index);

    /**
     * property QualityDataHistoryRange
     */
    @VTID(30)
    void qualityDataHistoryRange(
        com.bosch.easee.eASEEcdm_ComAPI.EQualityDataRangeMode oVal);

    /**
     * property QualityDataHistoryRange
     */
    @VTID(31)
    com.bosch.easee.eASEEcdm_ComAPI.EQualityDataRangeMode qualityDataHistoryRange();

    /**
     * property UseHexForMissingValues
     */
    @VTID(32)
    void useHexForMissingValues(boolean oVal);

    /**
     * property UseHexForMissingValues
     */
    @VTID(33)
    boolean useHexForMissingValues();

    /**
     * property ExportActiveParameterSets
     */
    @VTID(34)
    void exportActiveParameterSets(boolean oVal);

    /**
     * property ExportActiveParameterSets
     */
    @VTID(35)
    boolean exportActiveParameterSets();

    /**
     * property ExportBaseHexFile
     */
    @VTID(36)
    java.lang.String exportBaseHexFile();

    /**
     * property ExportBaseHexFile
     */
    @VTID(37)
    void exportBaseHexFile(java.lang.String pVal);

    /**
     * property ParameterFilter
     */
    @VTID(38)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection parameterFilter();

    @VTID(38)
    @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough =  {
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class}
    )
    java.lang.Object parameterFilter(int index);

    /**
     * property ParameterFilter
     */
    @VTID(39)
    void parameterFilter(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection oParameterFilter);

    /**
     * method GetOptionCache
     */
    @VTID(40)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object getOptionCache();

    /**
     * method GetOptionCacheValues
     */
    @VTID(41)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getOptionCacheValues(
        com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionCategoryId optionCategory);
}
