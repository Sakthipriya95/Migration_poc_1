package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDomain Interface
 */
@IID("{76076B57-7A68-472A-BFB2-46105FDC3731}")
public interface ICDMDomain extends Com4jObject {
    /**
     * method SearchCalibrationProjects
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol searchCalibrationProjects(
        java.lang.String iElementName,
        java.lang.String iVariant,
        int iRevision);

    /**
     * method SearchDatasets
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol searchDatasets(
        java.lang.String iElementName,
        java.lang.String iVariant,
        int iRevision);

    /**
     * method GetDomainSettings
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDomainSettings getDomainSettings();

    /**
     * method GetCreateProgramsetSettings
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings getCreateProgramsetSettings();

    /**
     * method GetCreateViewDefinitionSettings
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateViewDefinitionSettings getCreateViewDefinitionSettings();

    /**
     * method GetCalibrationProject
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject getCalibrationProject(
        int iVersionNo);

    /**
     * method GetDataset
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset getDataset(
        int iVersionNo);

    /**
     * method GetViewDefinition
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition getViewDefinition(
        int iVersionNo);

    /**
     * method GetDescriptionFileVersion
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDescriptionFileVersion getDescriptionFileVersion(
        int iVersionNo);

    /**
     * method GetObjectFileVersion
     */
    @VTID(16)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMObjectFileVersion getObjectFileVersion(
        int iVersionNo);

    /**
     * method GetObjectFile
     */
    @VTID(17)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMObjectFile getObjectFile();

    /**
     * method GetCollection
     */
    @VTID(18)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getCollection();

    @VTID(18)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getCollection(
        int index);

    /**
     * method GetGUIAutomation
     */
    @VTID(19)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMGUIAutomation getGUIAutomation();

    /**
     * method CreateViewDefinition
     */
    @VTID(20)
    void createViewDefinition(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateViewDefinitionSettings iSettings,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition> oViewDefinition,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo> oInfo);

    /**
     * method CreateProgramset
     */
    @VTID(21)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetResult createProgramset(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings iSettings,
        boolean iContinueOnValidationFail);

    /**
     * method ReleaseObjects
     */
    @VTID(22)
    void releaseObjects();

    /**
     * method GetTestSuite
     */
    @VTID(23)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMTestSuite getTestSuite();

    /**
     * method GetCreateProjectSettings
     */
    @VTID(24)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProjectSettings getCreateProjectSettings();

    /**
     * method CreateProject
     */
    @VTID(25)
    void createProject(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProjectSettings iSettings,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject> oProject,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo> oInfo);

    /**
     * method DoMaintenance
     */
    @VTID(26)
    void doMaintenance(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iSelection,
        boolean datasets,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection> oResult);

    /**
     * method DoMaintenanceEx
     */
    @VTID(27)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection doMaintenanceEx(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iSelection,
        com.bosch.easee.eASEEcdm_ComAPI.ECDMMaintenanceOption iOptions);

    /**
     * method GetProductAttributesAndValues
     */
    @VTID(28)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues getProductAttributesAndValues();

    /**
     * method GetCdmExtension
     */
    @VTID(29)
    com.bosch.easee.eASEEcdm_ComAPI.ICdmExtension getCdmExtension();

    /**
     * method GetComEvents
     */
    @VTID(30)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMComEvents getComEvents();

    /**
     * method GetDatasetCollection
     */
    @VTID(31)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol getDatasetCollection();

    @VTID(31)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class})
    java.lang.Object getDatasetCollection(
        int index);

    /**
     * method GetCdmInvokeCommand
     */
    @VTID(32)
    com.bosch.easee.eASEEcdm_ComAPI.ICdmInvokeCommand getCdmInvokeCommand();

    /**
     * method GetTypeList
     */
    @VTID(33)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getTypeList(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmFileTypelist typeList);

    /**
     * property VersionInformation
     */
    @VTID(34)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMVersionInfo versionInformation();

    /**
     * method GetProjectCollection
     */
    @VTID(35)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol getProjectCollection();

    @VTID(35)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol.class})
    java.lang.Object getProjectCollection(
        int index);

    /**
     * method GetReplaceVersionsSettings
     */
    @VTID(36)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsSettings getReplaceVersionsSettings();

    /**
     * method ReplaceVersions
     */
    @VTID(37)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsResult replaceVersions(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsSettings iReplaceSettings);

    /**
     * method GetParametersetUsages
     */
    @VTID(38)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParametersetUsages(
        int versionNumber,
        int filter);

    /**
     * method GetExportOfflineSettings
     */
    @VTID(39)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineSettings getExportOfflineSettings();

    /**
     * method GetImportOfflineSettings
     */
    @VTID(40)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMImportOfflineSettings getImportOfflineSettings();

    /**
     * method GetCdmStudio
     */
    @VTID(41)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioApi getCdmStudio();

    /**
     * method GetDescriptionFile
     */
    @VTID(42)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDescriptionFile getDescriptionFile(
        java.lang.String iFileName);

    /**
     * method GetParameterFile
     */
    @VTID(43)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterSetFile getParameterFile(
        java.lang.String iFileName,
        @DefaultValue("")java.lang.String iA2LFile);

    /**
     * method HasAccessibleProjects
     */
    @VTID(44)
    boolean hasAccessibleProjects();

    /**
     * method HasAccessibleProjects
     */
    @VTID(45)
    boolean hasAccessibleDataSets();

    /**
     * method DetermineParameterValueVariations
     */
    @VTID(46)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValueDependencyCol determineParameterValueVariations(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameterNames,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol iDatasets);

    /**
     * method GetProgramSet
     */
    @VTID(47)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramSet getProgramSet(
        int iVersionNo);

}
