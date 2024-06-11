package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCalibrationProject Interface
 */
@IID("{058DF563-247E-4C79-8939-70A3CF79B37A}")
public interface ICDMCalibrationProject extends Com4jObject {
    /**
     * method GetVersionObj
     */
    @VTID(7)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getVersionObj();

    /**
     * method GetCalibrationProjectSettings
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectSettings getCalibrationProjectSettings();

    /**
     * method GetDatasets
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol getDatasets(
        java.lang.String iProgramKey,
        java.lang.String iProductKey);

    /**
     * method CreateDataset
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetResult createDataset(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetSettings iSettings,
        boolean iContinueOnWarnings);

    /**
     * method GetCheckinSettings
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinSettings getCheckinSettings();

    /**
     * method ActivateDeactivate
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationResult activateDeactivate(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationSettings iSettings);

    /**
     * method GetActivationSettings
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationSettings getActivationSettings();

    @VTID(13)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationSettings.class})
    java.lang.Object getActivationSettings(
        int index);

    /**
     * method GetSoftwareChangeSettings
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeSettings getSoftwareChangeSettings();

    /**
     * method GetExportSettings
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportSettings getExportSettings(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmDatasetExportType typeOfExport);

    /**
     * method GetCreateDatasetSettings
     */
    @VTID(16)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetSettings getCreateDatasetSettings();

    /**
     * method GetDeliverySettings
     */
    @VTID(17)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySettings getDeliverySettings(
        boolean externalDelivery);

    /**
     * method DeliverCalibrationData
     */
    @VTID(18)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryResult deliverCalibrationData(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySettings iSettings);

    /**
     * method GetAllDatasets
     */
    @VTID(19)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol getAllDatasets();

    @VTID(19)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class})
    java.lang.Object getAllDatasets(
        int index);

    /**
     * method GetParameterDefinitions
     */
    @VTID(20)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameterDefinitions(
        java.lang.String iProgramKey);

    /**
     * method UpdatePvd
     */
    @VTID(21)
    void updatePvd(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iDatasets,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition pvd);

}
