package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMParameterHistoryItem Interface
 */
@IID("{34212758-8FD3-4712-925C-D5EE329E3B3E}")
public interface ICDMParameterHistoryItem extends Com4jObject {
    /**
     * property Name
     */
    @VTID(7)
    java.lang.String name();

    /**
     * property Type
     */
    @VTID(8)
    java.lang.String type();

    /**
     * property FunctionName
     */
    @VTID(9)
    java.lang.String functionName();

    /**
     * property FunctionVersion
     */
    @VTID(10)
    java.lang.String functionVersion();

    /**
     * property Criterion
     */
    @VTID(11)
    java.lang.String criterion();

    /**
     * property Variant
     */
    @VTID(12)
    java.lang.String variant();

    /**
     * property IntegrationUser
     */
    @VTID(13)
    java.lang.String integrationUser();

    /**
     * property IntegrationDate
     */
    @VTID(14)
    java.lang.String integrationDate();

    /**
     * property CalibrationRemarks
     */
    @VTID(15)
    java.lang.String calibrationRemarks();

    /**
     * property SourceVersion
     */
    @VTID(16)
    int sourceVersion();

    /**
     * property LastSourceVersion
     */
    @VTID(17)
    int lastSourceVersion();

    /**
     * property IntegrationLevel
     */
    @VTID(18)
    int integrationLevel();

    /**
     * property IntegrationOperation
     */
    @VTID(19)
    java.lang.String integrationOperation();

    /**
     * property SourceVersionName
     */
    @VTID(20)
    java.lang.String sourceVersionName();

    /**
     * property SourceVersionWorkPackage
     */
    @VTID(21)
    java.lang.String sourceVersionWorkPackage();

    /**
     * property SourceVersionFileName
     */
    @VTID(22)
    java.lang.String sourceVersionFileName();

    /**
     * property SourceVersionFileDate
     */
    @VTID(23)
    java.lang.String sourceVersionFileDate();

    /**
     * property SourceVersionCreationUser
     */
    @VTID(24)
    java.lang.String sourceVersionCreationUser();

    /**
     * property SourceVersionCreationDate
     */
    @VTID(25)
    java.lang.String sourceVersionCreationDate();

    /**
     * property LastSourceVersionName
     */
    @VTID(26)
    java.lang.String lastSourceVersionName();

    /**
     * property LastSourceVersionWorkPackage
     */
    @VTID(27)
    java.lang.String lastSourceVersionWorkPackage();

    /**
     * property LastSourceVersionFileName
     */
    @VTID(28)
    java.lang.String lastSourceVersionFileName();

    /**
     * property LastSourceVersionFileDate
     */
    @VTID(29)
    java.lang.String lastSourceVersionFileDate();

    /**
     * property LastSourceVersionCreationUser
     */
    @VTID(30)
    java.lang.String lastSourceVersionCreationUser();

    /**
     * property LastSourceVersionCreationDate
     */
    @VTID(31)
    java.lang.String lastSourceVersionCreationDate();

    /**
     * property ParameterValue
     */
    @VTID(32)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValue parameterValue();

    /**
     * property ParameterValueExists
     */
    @VTID(33)
    boolean parameterValueExists();

    /**
     * property CalibrationState
     */
    @VTID(34)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmCalibrationState calibrationState();

    /**
     * property CalibrationStateString
     */
    @VTID(35)
    java.lang.String calibrationStateString();

    /**
     * property ParameterValuePrevious
     */
    @VTID(36)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValue parameterValuePrevious();

    /**
     * property ParameterValuePreviousExists
     */
    @VTID(37)
    boolean parameterValuePreviousExists();

    /**
     * property ICDMParameter
     */
    @VTID(38)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMParameter icdmParameter();

    /**
     * property QualityInformation
     */
    @VTID(39)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection qualityInformation();

    @VTID(39)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object qualityInformation(
        int index);

}
