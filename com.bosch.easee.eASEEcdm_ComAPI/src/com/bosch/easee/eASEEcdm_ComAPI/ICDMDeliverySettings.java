package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDeliverySettings Interface
 */
@IID("{428220D9-1B57-4B65-8C59-39BDF9F71CF8}")
public interface ICDMDeliverySettings extends Com4jObject {
    /**
     * property DeliveryFile
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile deliveryFile();

    /**
     * method AssignFile
     */
    @VTID(8)
    void assignFile(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile fileSource,
        boolean activateSource);

    /**
     * method AssignObject
     */
    @VTID(9)
    void assignObject(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset,
        int sourceVersion,
        boolean activateSource);

    /**
     * method AssignFileWithProperties
     */
    @VTID(10)
    void assignFileWithProperties(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile fileSource,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties deliveryProperties);

    /**
     * method AssignObjectWithProperties
     */
    @VTID(11)
    void assignObjectWithProperties(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset,
        int sourceVersion,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties deliveryProperties);

    /**
     * method AssignFileByDatasetVersionNumber
     */
    @VTID(12)
    void assignFileByDatasetVersionNumber(
        int dataset,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile fileSource,
        boolean activateSource);

    /**
     * method AssignObjectByDatasetVersionNumber
     */
    @VTID(13)
    void assignObjectByDatasetVersionNumber(
        int dataset,
        int sourceVersion,
        boolean activateSource);

    /**
     * method AssignFileWithPropertiesByDatasetVersionNumber
     */
    @VTID(14)
    void assignFileWithPropertiesByDatasetVersionNumber(
        int datasetVersion,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile fileSource,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties deliveryProperties);

    /**
     * method AssignObjectWithPropertiesByDatasetVersionNumber
     */
    @VTID(15)
    void assignObjectWithPropertiesByDatasetVersionNumber(
        int datasetVersion,
        int sourceVersion,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties deliveryProperties);

    /**
     * property RequiredDataQuality
     */
    @VTID(16)
    void requiredDataQuality(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality newVal);

    /**
     * property RequiredDataQuality
     */
    @VTID(17)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmRequiredDataQuality requiredDataQuality();

    /**
     * method ValidateSettings
     */
    @VTID(18)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryResult validateSettings();

    /**
     * property Assignments
     */
    @VTID(19)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection assignments();

    @VTID(19)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object assignments(
        int index);

    /**
     * property Project
     */
    @VTID(20)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject project();

    /**
     * property ProjectContext
     */
    @VTID(21)
    int projectContext();

    /**
     * property ExternalDelivery
     */
    @VTID(22)
    boolean externalDelivery();

    /**
     * property DeliveryUser
     */
    @VTID(23)
    void deliveryUser(
        java.lang.String userId);

    /**
     * property DeliveryUser
     */
    @VTID(24)
    java.lang.String deliveryUser();

    /**
     * property EpromCheck
     */
    @VTID(25)
    void epromCheck(
        boolean value);

    /**
     * property EpromCheck
     */
    @VTID(26)
    boolean epromCheck();

    /**
     * method GetPropertiesObject
     */
    @VTID(27)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties getPropertiesObject();

    /**
     * method SetAutoAssignmentAttributes
     */
    @VTID(28)
    void setAutoAssignmentAttributes(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iSettings,
        java.lang.String iProgramKey);

    /**
     * method GetAutoAssignmentAttributes
     */
    @VTID(29)
    void getAutoAssignmentAttributes(
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection> oSettings,
        Holder<java.lang.String> oProgramKey);

    /**
     * property ProgramKey
     */
    @VTID(30)
    void programKey(
        java.lang.String value);

    /**
     * property ProgramKey
     */
    @VTID(31)
    java.lang.String programKey();

    /**
     * property DefaultActivationSetting
     */
    @VTID(32)
    boolean defaultActivationSetting();

}
