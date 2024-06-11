package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMParameter Interface
 */
@IID("{09DB959E-1B8E-4D62-9B34-F1B6DB53E345}")
public interface ICDMParameter extends Com4jObject {
    /**
     * property Name
     */
    @VTID(7)
    java.lang.String name();

    /**
     * property Criterion
     */
    @VTID(8)
    java.lang.String criterion();

    /**
     * property Criterion
     */
    @VTID(9)
    void criterion(
        java.lang.String pVal);

    /**
     * property Variant
     */
    @VTID(10)
    java.lang.String variant();

    /**
     * property Variant
     */
    @VTID(11)
    void variant(
        java.lang.String pVal);

    /**
     * property IntegrationUser
     */
    @VTID(12)
    java.lang.String integrationUser();

    /**
     * property IntegrationDate
     */
    @VTID(13)
    java.lang.String integrationDate();

    /**
     * property CalibrationRemarks
     */
    @VTID(14)
    java.lang.String calibrationRemarks();

    /**
     * property CalibrationRemarks
     */
    @VTID(15)
    void calibrationRemarks(
        java.lang.String pVal);

    /**
     * property CalibrationState
     */
    @VTID(16)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmCalibrationState calibrationState();

    /**
     * property CalibrationState
     */
    @VTID(17)
    void calibrationState(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmCalibrationState pVal);

    /**
     * method GetCalibrationStateText
     */
    @VTID(18)
    java.lang.String getCalibrationStateText();

    /**
     * property SourceVersion
     */
    @VTID(19)
    int sourceVersion();

    /**
     * property IntegrationLevel
     */
    @VTID(20)
    int integrationLevel();

    /**
     * property IntegrationLevel
     */
    @VTID(21)
    void integrationLevel(
        int oVal);

    /**
     * property IntegrationOperation
     */
    @VTID(22)
    java.lang.String integrationOperation();

    /**
     * property Value
     */
    @VTID(23)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValue value();

    /**
     * method CreateValueParameter
     */
    @VTID(24)
    void createValueParameter(
        java.lang.String iParameterName,
        com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamValueType iValueType,
        java.lang.String iUnit);

    /**
     * method CreateAcsciiParameter
     */
    @VTID(25)
    void createAcsciiParameter(
        java.lang.String iParameterName);

    /**
     * method CreateCurveParameter
     */
    @VTID(26)
    void createCurveParameter(
        java.lang.String iParameterName,
        com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamValueType iValueType,
        java.lang.String iUnit,
        int iCurveSize,
        com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamValueType iAxisValuetype,
        java.lang.String iAxisUnit);

    /**
     * method CreateMapParameter
     */
    @VTID(27)
    void createMapParameter(
        java.lang.String iParameterName,
        com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamValueType iValueType,
        java.lang.String iUnit,
        int iMapSizeX,
        com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamValueType iAxisValuetypeX,
        java.lang.String iAxisUnitX,
        int iMapSizeY,
        com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamValueType iAxisValuetypeY,
        java.lang.String iAxisUnitY);

    /**
     * method CreateAxisParameter
     */
    @VTID(28)
    void createAxisParameter(
        java.lang.String iParameterName,
        com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamValueType iValueType,
        java.lang.String iUnit,
        int iAxisSize);

    /**
     * method CreateValueBlockParameter
     */
    @VTID(29)
    void createValueBlockParameter(
        java.lang.String iParameterName,
        com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamValueType iValueType,
        java.lang.String iUnit,
        int iMapSizeX,
        int iMapSizeY);

    /**
     * property QualityInformation
     */
    @VTID(30)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection qualityInformation();

    @VTID(30)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object qualityInformation(
        int index);

    /**
     * property ValueOrigin
     */
    @VTID(31)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValueOrigin valueOrigin();

    /**
     * property Internal
     */
    @VTID(32)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object internal();

    /**
     * method InternalGetValue
     */
    @VTID(33)
    byte[] internalGetBinary();

}
