package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMSoftwareChangeSettings Interface
 */
@IID("{DA92F95D-A2FF-4019-86EB-8BCDC09D7AC6}")
public interface ICDMSoftwareChangeSettings extends Com4jObject {
    /**
     * property Source
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ECreateDatasetMode source();

    /**
     * property Source
     */
    @VTID(8)
    void source(
        com.bosch.easee.eASEEcdm_ComAPI.ECreateDatasetMode pVal);

    /**
     * property ProgramKey
     */
    @VTID(9)
    java.lang.String programKey();

    /**
     * property ProgramKey
     */
    @VTID(10)
    void programKey(
        java.lang.String pVal);

    /**
     * property DescriptionFilePath
     */
    @VTID(11)
    java.lang.String descriptionFilePath();

    /**
     * property DescriptionFilePath
     */
    @VTID(12)
    void descriptionFilePath(
        java.lang.String pVal);

    /**
     * property ObjectFilePath
     */
    @VTID(13)
    java.lang.String objectFilePath();

    /**
     * property ObjectFilePath
     */
    @VTID(14)
    void objectFilePath(
        java.lang.String pVal);

    /**
     * method SetSourceVersion
     */
    @VTID(15)
    void setSourceVersion(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject iSourceVersion);

    /**
     * method GetSourceVersion
     */
    @VTID(16)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getSourceVersion();

    /**
     * method GetContext
     */
    @VTID(17)
    int getContext();

    /**
     * method AddWorkPackageDefinitionMapping
     */
    @VTID(18)
    void addWorkPackageDefinitionMapping(
        int versionId,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition iWorkPackageDefinition);

    /**
     * method GetWorkPackageDefinition
     */
    @VTID(19)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition getWorkPackageDefinitionMapping(
        int versionId);

    /**
     * property ExtractData
     */
    @VTID(20)
    boolean extractData();

    /**
     * property ExtractData
     */
    @VTID(21)
    void extractData(
        boolean pVal);

    /**
     * property AdaptParameterSets
     */
    @VTID(22)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmWorkPackageAdaption adaptParameterSets();

    /**
     * property AdaptParameterSets
     */
    @VTID(23)
    void adaptParameterSets(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmWorkPackageAdaption pVal);

    /**
     * method AddRenamedParameterMapping
     */
    @VTID(24)
    void addRenamedParameterMapping(
        java.lang.String iCurrentName,
        java.lang.String iNewName);

    /**
     * method GetRenamedParameterMapping
     */
    @VTID(25)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getRenamedParameterMapping();

    @VTID(25)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getRenamedParameterMapping(
        int index);

    /**
     * property DefaultDataSource
     */
    @VTID(26)
    int defaultDataSource();

    /**
     * property DefaultDataSource
     */
    @VTID(27)
    void defaultDataSource(
        int pVal);

    /**
     * property ShowProgress
     */
    @VTID(28)
    boolean showProgress();

    /**
     * property ShowProgress
     */
    @VTID(29)
    void showProgress(
        boolean pVal);

    /**
     * property RuleConfigurationFromFile
     */
    @VTID(30)
    java.lang.String ruleConfigurationFromFile();

    /**
     * property RuleConfigurationFromFile
     */
    @VTID(31)
    void ruleConfigurationFromFile(
        java.lang.String iRuleFile);

    /**
     * property RuleConfigurationFromVersion
     */
    @VTID(32)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject ruleConfigurationFromVersion();

    /**
     * property RuleConfigurationFromVersion
     */
    @VTID(33)
    void ruleConfigurationFromVersion(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject iRuleVersionObj);

    /**
     * method SetNewDatasetName
     */
    @VTID(34)
    void setNewDatasetName(
        int predecessorDataset,
        java.lang.String newDatasetName);

    /**
     * method GetNewDatasetName
     */
    @VTID(35)
    java.lang.String getNewDatasetName(
        int predecessorDataset);

}
