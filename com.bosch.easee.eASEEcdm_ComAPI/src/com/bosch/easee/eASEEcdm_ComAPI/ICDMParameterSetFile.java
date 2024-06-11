package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMParameterSetFile Interface
 */
@IID("{68B68631-577E-4149-B7C8-7A98A7BCC413}")
public interface ICDMParameterSetFile extends Com4jObject {
    /**
     * method ExportCompleteParameterSet
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMExtractCompleteParmetersetResult exportCompleteParameterSet(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMExtractCompleteParmetersetSettings iSettings);

    /**
     * method GetExportCompleteParameterSetSettings
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMExtractCompleteParmetersetSettings getExportCompleteParameterSetSettings();

    /**
     * method SplitByParameters
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection splitByParameters(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iSplitPackages,
        java.lang.String iFileType,
        @DefaultValue("")java.lang.String iPath);

    /**
     * method GetParameters
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameters();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getParameters(
        int index);

    /**
     * method GetFunctions
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getFunctions();

    @VTID(11)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getFunctions(
        int index);

    /**
     * method GetHexValues
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getHexValues(
        boolean iRawFormat,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameterList);

    /**
     * method GetParameterValues
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameterValues(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameters);

}
