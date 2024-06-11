package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMExportOfflineSettings Interface
 */
@IID("{D6D40314-C752-43FC-A3FB-5C0AAC9C6E50}")
public interface ICDMExportOfflineSettings extends Com4jObject {
    /**
     * property TargetDirectory
     */
    @VTID(7)
    java.lang.String targetDirectory();

    /**
     * property TargetDirectory
     */
    @VTID(8)
    void targetDirectory(
        java.lang.String oValue);

    /**
     * property VisibleCalibrationProjects
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol visibleCalibrationProjects();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol.class})
    java.lang.Object visibleCalibrationProjects(
        int index);

    /**
     * property SelectedDatasets
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol selectedDatasets();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class})
    java.lang.Object selectedDatasets(
        int index);

    /**
     * property SingleSelection
     */
    @VTID(11)
    boolean singleSelection();

    /**
     * property SingleSelection
     */
    @VTID(12)
    void singleSelection(
        boolean oValue);

    /**
     * property ExportProjectFile
     */
    @VTID(13)
    boolean exportProjectFile();

    /**
     * property ExportProjectFile
     */
    @VTID(14)
    void exportProjectFile(
        boolean oValue);

    /**
     * property ExportHex
     */
    @VTID(15)
    boolean exportHex();

    /**
     * property ExportHex
     */
    @VTID(16)
    void exportHex(
        boolean oValue);

    /**
     * property ExportView
     */
    @VTID(17)
    boolean exportView();

    /**
     * property ExportView
     */
    @VTID(18)
    void exportView(
        boolean oValue);

    /**
     * property ExportRights
     */
    @VTID(19)
    boolean exportRights();

    /**
     * property ExportRights
     */
    @VTID(20)
    void exportRights(
        boolean oValue);

    /**
     * property ExportAdditional
     */
    @VTID(21)
    boolean exportAdditional();

    /**
     * property ExportAdditional
     */
    @VTID(22)
    void exportAdditional(
        boolean oValue);

    /**
     * property ForceUpdate
     */
    @VTID(23)
    boolean forceUpdate();

    /**
     * property ForceUpdate
     */
    @VTID(24)
    void forceUpdate(
        boolean oValue);

    /**
     * property OptimizeHex
     */
    @VTID(25)
    boolean optimizeHex();

    /**
     * property OptimizeHex
     */
    @VTID(26)
    void optimizeHex(
        boolean oValue);

    /**
     * property ProgramKeyFilter
     */
    @VTID(27)
    java.lang.String programKeyFilter();

    /**
     * property ProgramKeyFilter
     */
    @VTID(28)
    void programKeyFilter(
        java.lang.String oValue);

    /**
     * property UseOriginalName
     */
    @VTID(29)
    boolean useOriginalName();

    /**
     * property UseOriginalName
     */
    @VTID(30)
    void useOriginalName(
        boolean oValue);

    /**
     * property QualityDataProperties
     */
    @VTID(31)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection qualityDataProperties();

    @VTID(31)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object qualityDataProperties(
        int index);

    /**
     * property QualityDataHistoryRange
     */
    @VTID(32)
    void qualityDataHistoryRange(
        com.bosch.easee.eASEEcdm_ComAPI.EQualityDataRangeMode oVal);

    /**
     * property QualityDataHistoryRange
     */
    @VTID(33)
    com.bosch.easee.eASEEcdm_ComAPI.EQualityDataRangeMode qualityDataHistoryRange();

    /**
     * property NamingMode
     */
    @VTID(34)
    int namingMode();

    /**
     * property NamingMode
     */
    @VTID(35)
    void namingMode(
        int oValue);

}
