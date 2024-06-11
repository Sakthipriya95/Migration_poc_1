package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.*;


/**
 * ICDMDatasetCheckinSettings Interface
 */
@IID("{33858CF9-B297-4D13-897F-1DC4AB36FE17}")
public interface ICDMDatasetCheckinSettings extends Com4jObject {
    /**
     * property RequiredDataQuality
     */
    @VTID(7)
    void requiredDataQuality(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality newVal);

    /**
     * property RequiredDataQuality
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality requiredDataQuality();

    /**
     * property Comment
     */
    @VTID(9)
    void comment(java.lang.String comment);

    /**
     * property Comment
     */
    @VTID(10)
    java.lang.String comment();

    /**
     * method GetOption
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMOption getOption(
        com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionCategoryId iOptionCategoryId,
        int iOptionId);

    /**
     * GetDatasetSettings
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetSettings getDatasetSettings(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset pDataset);

    /**
     * GetDatasetSettingsByVersionNumber
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetSettings getDatasetSettingsByVersionNumber(
        int versionNumber);

    /**
     * SetDatasetSettings
     */
    @VTID(14)
    void setDatasetSettings(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset pDataset,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetSettings iSettings);

    /**
     * SetDatasetSettingsByVersionNumber
     */
    @VTID(15)
    void setDatasetSettingsByVersionNumber(int versionNumber,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetSettings iSettings);

    /**
     * HasDatasetSettingsByVersionNumber
     */
    @VTID(16)
    boolean hasDatasetSettingsByVersionNumber(int versionNumber);

    /**
     * property Project
     */
    @VTID(17)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject project();

    /**
     * property ProjectContext
     */
    @VTID(18)
    int projectContext();

    /**
     * property CheckinSettings
     */
    @VTID(19)
    void checkinSettings(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmDatasetCheckinSettings oSettigs);

    /**
     * property CheckinSettings
     */
    @VTID(20)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmDatasetCheckinSettings checkinSettings();

    /**
     * method GetOptionCache
     */
    @VTID(21)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object getOptionCache();

    /**
     * method GetOptionCacheValues
     */
    @VTID(22)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getOptionCacheValues(
        com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionCategoryId optionCategory);
}
