package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMImportOfflineSettings Interface
 */
@IID("{298BDBFD-A30F-40C0-9324-F9B8AD39DF3B}")
public interface ICDMImportOfflineSettings extends Com4jObject {
    /**
     * property DefaultImportRoot
     */
    @VTID(7)
    java.lang.String defaultImportRoot();

    /**
     * property ImportDirectories
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection importDirectories();

    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object importDirectories(
        int index);

    /**
     * property IgnoreMetadataDifferences
     */
    @VTID(9)
    boolean ignoreMetadataDifferences();

    /**
     * property IgnoreMetadataDifferences
     */
    @VTID(10)
    void ignoreMetadataDifferences(
        boolean oValue);

    /**
     * property ImportFileFormat
     */
    @VTID(11)
    java.lang.String importFileFormat();

    /**
     * property ImportFileFormat
     */
    @VTID(12)
    void importFileFormat(
        java.lang.String oValue);

}
