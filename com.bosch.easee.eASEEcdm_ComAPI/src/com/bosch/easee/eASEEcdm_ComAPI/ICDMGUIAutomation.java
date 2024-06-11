package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.*;


/**
 * ICDMGUIAutomation Interface
 */
@IID("{AECC6E3F-CF35-4FBC-A4D0-62C2B1C8F974}")
public interface ICDMGUIAutomation extends Com4jObject {
    /**
     * method ExportIntegratedObjectFile
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo exportIntegratedObjectFile(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMExportIntegratedOFSettings iSettings);

    /**
     * method ExportIntegratedParameterSetFile
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo exportIntegratedParameterSetFile(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMExportIntegratedPFSettings iSettings);

    /**
     * method GetExportIntegratedOFSettings
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMExportIntegratedOFSettings getExportIntegratedOFSettings(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset iDataSet);

    /**
     * method GetExportIntegratedPFSettings
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMExportIntegratedPFSettings getExportIntegratedPFSettings(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset iDataSet);

    /**
     * method ExportOffline
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineResult exportOffline(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineSettings iSettings);

    /**
     * method ImportOffline
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMImportOfflineResult importOffline(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMImportOfflineSettings iSettings);

    /**
     * method DeliverCalibrationData
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryResult deliverCalibrationData(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySettings iSettings);

    /**
     * method SelectProject
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject selectProject();

    /**
     * method SelectProjects
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol selectProjects();

    @VTID(15)
    @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough =  {
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol.class}
    )
    java.lang.Object selectProjects(int index);

    /**
     * property SetParentWindow
     */
    @VTID(16)
    void parentWindow(int rhs);

    /**
     * property AutoOk
     */
    @VTID(17)
    void autoOk(boolean rhs);

    /**
     * method CreateProject
     */
    @VTID(18)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject createProject();

    /**
     * method ExportWizard
     */
    @VTID(19)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineResult exportWizard(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineSettings iSettings);

    /**
     * method CreateProgramset
     */
    @VTID(20)
    int createProgramset(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings iSettings);

    /**
     * method OpenEditor
     */
    @VTID(21)
    void openEditor(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject iProject,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationSettings iSettings);

    /**
     * method OpenEditorPset
     */
    @VTID(22)
    void openEditorPset(int versionId);
}
