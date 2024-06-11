package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMExportIntegratedOFSettings Interface
 */
@IID("{7861B3DB-D1FF-4060-A503-D602D4A2A260}")
public interface ICDMExportIntegratedOFSettings extends Com4jObject {
    /**
     * property ExportFilePath
     */
    @VTID(7)
    java.lang.String exportFilePath();

    /**
     * property ExportFilePath
     */
    @VTID(8)
    void exportFilePath(
        java.lang.String pVal);

    /**
     * method GetContext
     */
    @VTID(9)
    int getContext();

    /**
     * property ExportFilePathReadOnly
     */
    @VTID(10)
    boolean exportFilePathReadOnly();

    /**
     * property ExportFilePathReadOnly
     */
    @VTID(11)
    void exportFilePathReadOnly(
        boolean pVal);

    /**
     * method GetOption
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMOption getOption(
        com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionCategoryId iOptionCategoryId,
        int iOptionId);

    /**
     * method GetOptionCache
     */
    @VTID(13)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object getOptionCache();

    /**
     * method GetOptionCacheValues
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getOptionCacheValues(
        com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionCategoryId optionCategory);

    /**
     * property ExportBaseHexFile
     */
    @VTID(15)
    java.lang.String exportBaseHexFile();

    /**
     * property ExportBaseHexFile
     */
    @VTID(16)
    void exportBaseHexFile(
        java.lang.String pVal);

}
