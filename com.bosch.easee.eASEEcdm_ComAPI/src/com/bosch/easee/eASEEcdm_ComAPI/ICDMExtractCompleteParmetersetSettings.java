package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMExtractCompleteParmetersetSettings Interface
 */
@IID("{58AB5F3B-0949-4C6A-8955-5B27CFAE9B60}")
public interface ICDMExtractCompleteParmetersetSettings extends Com4jObject {
    /**
     * property FileType
     */
    @VTID(7)
    void fileType(
        java.lang.String fileType);

    /**
     * property FileType
     */
    @VTID(8)
    java.lang.String fileType();

    /**
     * property ParametersetFileName
     */
    @VTID(9)
    void parametersetFileName(
        java.lang.String fileName);

    /**
     * property ParametersetFileName
     */
    @VTID(10)
    java.lang.String parametersetFileName();

    /**
     * method GetCopyOptions
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMOption getCopyOptions(
        int iOptionId);

    /**
     * property SoftwareTrack
     */
    @VTID(12)
    void softwareTrack(
        java.lang.String swTrack);

    /**
     * property SoftwareTrack
     */
    @VTID(13)
    java.lang.String softwareTrack();

    /**
     * method GetOptionCacheValues
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getOptionCacheValues();

    @VTID(14)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getOptionCacheValues(
        int index);

}
