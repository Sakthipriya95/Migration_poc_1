package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMExportIntegratedPFSettings Interface
 */
@IID("{9A92D8A5-F4E9-489C-8D98-8248D9D27FF8}")
public interface ICDMExportIntegratedPFSettings extends Com4jObject {
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

}
