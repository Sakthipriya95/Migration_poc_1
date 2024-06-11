package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMStudioCompareSettings Interface
 */
@IID("{511F708C-1439-4485-AD81-40D1E354D96F}")
public interface ICDMStudioCompareSettings extends Com4jObject {
    /**
     * property Target
     */
    @VTID(7)
    void target(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject oTarget);

    /**
     * property Target
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject target();

    /**
     * method AddSource
     */
    @VTID(9)
    void addSource(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject iSource);

    /**
     * method GetSources
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObjectCol getSources();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObjectCol.class})
    java.lang.Object getSources(
        int index);

    /**
     * property Mode
     */
    @VTID(11)
    void mode(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmReportMode oCompareMode);

    /**
     * property Mode
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmReportMode mode();

    /**
     * property Filter
     */
    @VTID(13)
    void filter(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmReportFilter oCompareFilter);

    /**
     * property Filter
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmReportFilter filter();

    /**
     * property ReportFileType
     */
    @VTID(15)
    void reportFileType(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmEReportFileType oFileType);

    /**
     * property ReportFileType
     */
    @VTID(16)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmEReportFileType reportFileType();

    /**
     * property ReportFileName
     */
    @VTID(17)
    void reportFileName(
        java.lang.String oFilename);

    /**
     * property ReportFileName
     */
    @VTID(18)
    java.lang.String reportFileName();

    /**
     * method SetQualityData
     */
    @VTID(19)
    void setQualityData(
        int iData);

    /**
     * method GetQualityData
     */
    @VTID(20)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getQualityData();

    @VTID(20)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getQualityData(
        int index);

    /**
     * property ParameterFilter
     */
    @VTID(21)
    void parameterFilter(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection oParameterNames);

    /**
     * property ParameterFilter
     */
    @VTID(22)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection parameterFilter();

    @VTID(22)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object parameterFilter(
        int index);

    /**
     * property FunctionFilter
     */
    @VTID(23)
    void functionFilter(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection oFunctionNames);

    /**
     * property FunctionFilter
     */
    @VTID(24)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection functionFilter();

    @VTID(24)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object functionFilter(
        int index);

    /**
     * method CreateCDMStudioCallObject
     */
    @VTID(25)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject createCDMStudioCallObject();

    /**
     * property AutoOpen
     */
    @VTID(26)
    void autoOpen(
        boolean oAutoOpen);

    /**
     * property AutoOpen
     */
    @VTID(27)
    boolean autoOpen();

}
