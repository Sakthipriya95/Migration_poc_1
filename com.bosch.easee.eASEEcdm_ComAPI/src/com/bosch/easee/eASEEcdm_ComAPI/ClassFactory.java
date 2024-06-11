package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
    private ClassFactory() {} // instanciation is not allowed


    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings createICDMCreateProgramsetSettings2() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings.class, "{73A92B1D-61D2-4098-882E-DCE39B30757B}" );
    }

    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol createICDMMessageCol2() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class, "{73A92B1D-61D2-4098-882E-DCE39B30757C}" );
    }

    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMMessage createICDMMessage2() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMMessage.class, "{73A92B1D-61D2-4098-882E-DCE39B30757D}" );
    }

    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProjectSettings createICDMCreateProjectSettings2() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProjectSettings.class, "{73A92B1D-61D2-4098-882E-DCE39B30757F}" );
    }

    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectSettings createICDMCalibrationProjectSettings2() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectSettings.class, "{73A92B1D-61D2-4098-882E-DCE39B30758A}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.IApplication createApplication() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.IApplication.class, "{9F9F7497-3AD6-465D-A066-8389F4DE67AF}" );
    }

    /**
     * CDMDomain Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDomain createCDMDomain() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDomain.class, "{24FEBF27-87A1-4400-8E78-979F1C5A86D7}" );
    }

    /**
     * CDMComEvents Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMComEvents createCDMComEvents() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMComEvents.class, "{A8BE6129-CBE2-4310-A465-E9C84521F43E}" );
    }

    /**
     * CDMDataset Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset createCDMDataset() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset.class, "{F7D33A45-1C75-4ABA-BD2F-3540321E942B}" );
    }

    /**
     * CDMCalibrationProjectCol Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol createCDMCalibrationProjectCol() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectCol.class, "{F42B6179-6F0E-42F3-A69A-99C2C7139D19}" );
    }

    /**
     * CDMCalibrationProject Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject createCDMCalibrationProject() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject.class, "{49B33A37-C427-45BE-8B4B-8F9198C29820}" );
    }

    /**
     * CDMDatasetCol Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol createCDMDatasetCol() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class, "{5F7BCBDF-5704-4658-AB3B-A480772381D1}" );
    }

    /**
     * CDMValueVariation Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMValueVariation createCDMValueVariation() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMValueVariation.class, "{0B2B8D14-E83B-4CA0-84F8-267CC434B804}" );
    }

    /**
     * CDMValueVariationCol Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMValueVariationCol createCDMValueVariationCol() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMValueVariationCol.class, "{5DA59389-DD3F-477F-94FE-12C23A5CAF47}" );
    }

    /**
     * CDMParameterValueDependency Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValueDependency createCDMParameterValueDependency() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValueDependency.class, "{E3D4A6A0-8088-43A8-9593-51AAB1E7A6AA}" );
    }

    /**
     * CDMParameterValueDependencyCol Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValueDependencyCol createCDMParameterValueDependencyCol() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValueDependencyCol.class, "{FE744FD8-B7DF-46A2-A2F6-3BCF0922A0F7}" );
    }

    /**
     * CDMObjectFileVersion Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMObjectFileVersion createCDMObjectFileVersion() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMObjectFileVersion.class, "{184E0031-2F49-472F-B9AE-402D492751EA}" );
    }

    /**
     * CDMDescriptionFileVersion Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDescriptionFileVersion createCDMDescriptionFileVersion() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDescriptionFileVersion.class, "{A7DD0FB3-53C4-45CD-ABAA-789690354825}" );
    }

    /**
     * CDMViewDefinitionWorkpackage Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinitionWorkpackage createCDMViewDefinitionWorkpackage() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinitionWorkpackage.class, "{67DF621D-6739-4BCD-8F63-889AF2C50466}" );
    }

    /**
     * CDMCollection Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection createCDMCollection() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class, "{1B194F90-8902-4A52-B79A-6A54E090A923}" );
    }

    /**
     * CDMParameterGroup Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterGroup createCDMParameterGroup() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterGroup.class, "{70AC00F4-8E17-4FEA-97A8-0A2B1D43C43C}" );
    }

    /**
     * CDMDomainSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDomainSettings createCDMDomainSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDomainSettings.class, "{BC0B6FBA-37A1-4C22-8247-0B489E0B88CF}" );
    }

    /**
     * CDMCalibrationProjectSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectSettings createCDMCalibrationProjectSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectSettings.class, "{85B1F00B-10BF-4351-9D40-6F4D860D5896}" );
    }

    /**
     * CDMCreateDatasetSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetSettings createCDMCreateDatasetSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetSettings.class, "{BCACE0B5-D453-48C0-823E-311DEA090E7C}" );
    }

    /**
     * CDMSoftwareChangeSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeSettings createCDMSoftwareChangeSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeSettings.class, "{78950A84-9220-4D4C-88FD-88F5CBBF8D12}" );
    }

    /**
     * CDMSoftwareChangeResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeResult createCDMSoftwareChangeResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeResult.class, "{84476D32-3F32-4836-82D5-C90FC6CDAF40}" );
    }

    /**
     * CDMCreateDatasetResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetResult createCDMCreateDatasetResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetResult.class, "{A1CAE569-FFF0-42B5-AFA1-C8DC19A7EDCA}" );
    }

    /**
     * CDMCreateProjectSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProjectSettings createCDMCreateProjectSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProjectSettings.class, "{B839DF40-442B-480F-A657-74D579973EDF}" );
    }

    /**
     * CDMCreateProgramsetSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings createCDMCreateProgramsetSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings.class, "{3B4562E5-87C6-40EB-947B-5215BC4CEE1F}" );
    }

    /**
     * CDMProductKey Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMProductKey createCDMProductKey() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMProductKey.class, "{339450EE-90F3-4ABF-A9B1-4A9610893B7F}" );
    }

    /**
     * CDMProductKey Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramKey createCDMProgramKey() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramKey.class, "{73D4504E-21A3-4C7F-C9B1-4A9410843BAF}" );
    }

    /**
     * CDMProductKey Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramKey createCDMProgramKeyDummy() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramKey.class, "{0ADDE506-9A1C-4FA2-9944-9ADA0A901AA5}" );
    }

    /**
     * CDMExportIntegratedOFSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMExportIntegratedOFSettings createCDMExportIntegratedOFSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMExportIntegratedOFSettings.class, "{508C0C37-ACF9-40F4-8043-0CE929A05589}" );
    }

    /**
     * CDMGUIAutomation Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMGUIAutomation createCDMGUIAutomation() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMGUIAutomation.class, "{2FF051BE-A4C3-49CE-96D4-CEF8AA377CFB}" );
    }

    /**
     * CDMObjectFile Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMObjectFile createCDMObjectFile() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMObjectFile.class, "{C66F4D23-E217-461B-956C-99D6566D00DF}" );
    }

    /**
     * CDMProcessInfo Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMTestSuite createCDMTestSuite() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMTestSuite.class, "{1AC01613-280C-4F42-8ECB-F21B6FE1CCD2}" );
    }

    /**
     * CDMTestSuite Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo createCDMProcessInfo() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo.class, "{4D86250B-B698-44AB-8FA3-8CF6969348D1}" );
    }

    /**
     * CDMStudioStateInfo Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioStateInfo createCDMStudioStateInfo() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioStateInfo.class, "{C01FAC38-8818-41FD-B5C2-1B33CB8BF36D}" );
    }

    /**
     * CDMExportIntegratedPFSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMExportIntegratedPFSettings createCDMExportIntegratedPFSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMExportIntegratedPFSettings.class, "{156C7613-D6FA-4D93-A300-A04644A315D5}" );
    }

    /**
     * CDMOption Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMOption createCDMOption() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMOption.class, "{35FFBB12-F51D-4B36-B132-4888AE022D68}" );
    }

    /**
     * CDMProductAttributesAndValues Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues createCDMProductAttributesAndValues() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues.class, "{12EF4FD7-79DA-44AD-9605-B91E26D014C5}" );
    }

    /**
     * CdmExtension Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICdmExtension createCdmExtension() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICdmExtension.class, "{DED8184C-2398-477F-BD2E-CA5934687B88}" );
    }

    /**
     * CDMDatasetCheckinSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinSettings createCDMDatasetCheckinSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinSettings.class, "{C7D0F7AB-F3EC-457B-A1E2-33A38B601E37}" );
    }

    /**
     * CDMDatasetCheckinDatasetSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetSettings createCDMDatasetCheckinDatasetSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetSettings.class, "{EAD017AB-23EC-457B-A1E2-33A38B601187}" );
    }

    /**
     * CDMDatasetCheckinResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinResult createCDMDatasetCheckinResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinResult.class, "{987ED9C9-2B00-4480-B0EF-84637AA4D37C}" );
    }

    /**
     * CDMDatasetCheckinDatasetResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetResult createCDMDatasetCheckinDatasetResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinDatasetResult.class, "{9C3C068E-9292-4357-B1D2-1DD4DADE2C88}" );
    }

    /**
     * CDMMessage Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMMessage createCDMMessage() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMMessage.class, "{E14C8F6F-893E-439C-BE03-F23CE5D5FA48}" );
    }

    /**
     * CDMMessageCol Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol createCDMMessageCol() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class, "{4A28393D-F4D2-4010-A5E1-605938939621}" );
    }

    /**
     * CDMParameterHistoryItem Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterHistoryItem createCDMParameterHistoryItem() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterHistoryItem.class, "{374FB23A-7244-406F-B8F6-BA4793F82FA9}" );
    }

    /**
     * CDMParameter Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMParameter createCDMParameter() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMParameter.class, "{E98207BF-4B28-4C20-9728-DE9500E6BBB7}" );
    }

    /**
     * CDMParameterValue Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValue createCDMParameterValue() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValue.class, "{253646BC-6EE9-4460-B62A-976821B9D1ED}" );
    }

    /**
     * CDMParameterDef Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterDef createCDMParameterDef() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterDef.class, "{10AFA7D0-097A-416D-AE4E-38EA722C2DE6}" );
    }

    /**
     * CDMFunctionDef Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMFunctionDef createCDMFunctionDef() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMFunctionDef.class, "{52DEA4A5-015F-40BE-AF02-487683AF3A48}" );
    }

    /**
     * CdmInvokeCommandItem Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICdmInvokeCommandItem createCdmInvokeCommandItem() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICdmInvokeCommandItem.class, "{2342038A-2F4E-4CC0-BB48-E7C2708DAD53}" );
    }

    /**
     * CdmInvokeCommand Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICdmInvokeCommand createCdmInvokeCommand() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICdmInvokeCommand.class, "{7342038A-2F4D-4CC0-BB48-E7C2708DFD53}" );
    }

    /**
     * CDMActivationAssignment Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationAssignment createCDMActivationAssignment() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationAssignment.class, "{F9C155FD-00D6-4326-BA73-F1A4B672FF7E}" );
    }

    /**
     * CDMActivationSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationSettings createCDMActivationSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationSettings.class, "{7C6F7E6B-141B-4E70-B6E1-7230AAE4D4A3}" );
    }

    /**
     * CDMActivationResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationResult createCDMActivationResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationResult.class, "{E960C568-BF82-4446-99D6-AB2F975E4053}" );
    }

    /**
     * CDMFileType Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMFileType createCDMFileType() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMFileType.class, "{10AA8873-2BFC-4F82-ACDB-3FC986DF4E27}" );
    }

    /**
     * CDMDatasetExportSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportSettings createCDMDatasetExportSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportSettings.class, "{B3F5EBD9-BD06-43A2-92F1-E662C9E392E8}" );
    }

    /**
     * CDMDatasetExportDatasetSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetSettings createCDMDatasetExportDatasetSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetSettings.class, "{9C6C7B77-7111-4808-A36F-2E75FCDF3016}" );
    }

    /**
     * CDMDatasetExportResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportResult createCDMDatasetExportResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportResult.class, "{D23473B8-7D54-489F-968C-A7A4FB2F773C}" );
    }

    /**
     * CDMDatasetExportDatasetResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetResult createCDMDatasetExportDatasetResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetResult.class, "{E64806DC-8D56-459E-BE9A-952DC49134A1}" );
    }

    /**
     * CDMCriterionVariants Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCriterionVariants createCDMCriterionVariants() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCriterionVariants.class, "{6DF93B51-88F6-43AC-BA68-8029C80DB347}" );
    }

    /**
     * ICDMHexFileSignature Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMHexFileSignature createCDMHexFileSignature() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMHexFileSignature.class, "{86CB895C-16A4-4B46-B1F8-DF5F4BFA7030}" );
    }

    /**
     * ICDMExtractCompleteParmetersetSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMExtractCompleteParmetersetSettings createCDMExtractCompleteParmetersetSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMExtractCompleteParmetersetSettings.class, "{B2BD421D-1539-4450-9E72-46E2E565022D}" );
    }

    /**
     * ICDMExtractCompleteParmetersetResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMExtractCompleteParmetersetResult createCDMExtractCompleteParmetersetResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMExtractCompleteParmetersetResult.class, "{FB7EC98B-9DDD-4EA2-BC3C-9D1F78B96616}" );
    }

    /**
     * ICDMExportOfflineResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineResult createCDMExportOfflineResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineResult.class, "{26223150-D9CE-40DF-80CF-1DA990DFE8C5}" );
    }

    /**
     * ICDMExportOfflineResultItem Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineResultItem createCDMExportOfflineResultItem() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineResultItem.class, "{AE66A8AC-D377-4C59-98D3-76B6D7AF4BAB}" );
    }

    /**
     * ICDMImportOfflineResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMImportOfflineResult createCDMImportOfflineResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMImportOfflineResult.class, "{85417FD4-AC45-42A8-B471-CE77809986A7}" );
    }

    /**
     * ICDMDeliverySettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySettings createCDMDeliverySettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySettings.class, "{14E250AF-A27E-433F-8601-5617AD73A919}" );
    }

    /**
     * ICDMDeliverySourceFile Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile createCDMDeliverySourceFile() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile.class, "{A43879D6-4BAC-4041-ACDA-F8EC8504264A}" );
    }

    /**
     * ICDMDeliveryAssignment Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryAssignment createCDMDeliveryAssignment() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryAssignment.class, "{84266BC7-0A55-43C6-ABB6-0D34D4028AC3}" );
    }

    /**
     * ICDMDeliveryResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryResult createCDMDeliveryResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryResult.class, "{F95B5005-0ED7-4254-A5C5-9A2857443740}" );
    }

    /**
     * ICDMDeliveryDatasetResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryDatasetResult createCDMDeliveryDatasetResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryDatasetResult.class, "{AFB57EFF-E24E-4D39-9DC4-3D5EC0372239}" );
    }

    /**
     * ICDMCreateDatasetRevisionVariantResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionVariantResult createCDMCreateDatasetRevisionVariantResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionVariantResult.class, "{E0C8D007-BB10-46E3-8B1C-16BB86010495}" );
    }

    /**
     * ICDMDeliveryProperties Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties createCDMDeliveryProperties() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties.class, "{A68CCB5D-CAE2-4AA0-8D9A-18F2045FB121}" );
    }

    /**
     * ICDMDeliverySettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsSettings createCDMReplaceVersionsSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsSettings.class, "{EF8A5331-2A07-4FD2-A64E-735EE45210D3}" );
    }

    /**
     * ICDMDeliverySettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsItem createCDMReplaceVersionsItem() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsItem.class, "{2EF45BC0-0906-418D-A291-8204C55BAD8F}" );
    }

    /**
     * ICDMDeliverySettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsResult createCDMReplaceVersionsResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsResult.class, "{A654C277-CBF2-44C6-AA79-22A25A331906}" );
    }

    /**
     * ICDMDeliverySettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsGroupResult createCDMReplaceVersionsGroupResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsGroupResult.class, "{0368EEFF-73E8-46AC-802A-285AC92CC299}" );
    }

    /**
     * ICDMVersionInfo Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMVersionInfo createCDMVersionInfo() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMVersionInfo.class, "{FF35CBA7-A649-47FC-A2B6-5AB635016B33}" );
    }

    /**
     * ICDMExportOfflineSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineSettings createCDMExportOfflineSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMExportOfflineSettings.class, "{AEC1F69A-7432-4399-9013-9FF043F27D58}" );
    }

    /**
     * ICDMImportOfflineSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMImportOfflineSettings createCDMImportOfflineSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMImportOfflineSettings.class, "{71C1117F-C4D7-4B86-8531-B0F2394C3EF1}" );
    }

    /**
     * ICDMStudioApi Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioApi createCDMStudioApi() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioApi.class, "{03179287-5FED-4440-8C0D-E2C0AC9BF8B8}" );
    }

    /**
     * ICDMStudioCompareSettings Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareSettings createCDMStudioCompareSettings() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareSettings.class, "{0A0FCD64-0FC9-49C7-9BA9-05C13C0418AB}" );
    }

    /**
     * ICDMStudioApi Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject createCDMStudioCallObject() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject.class, "{25183650-88CA-40DF-998B-08D249925C2A}" );
    }

    /**
     * ICDMStudioCallObjectCol Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObjectCol createCDMStudioCallObjectCol() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObjectCol.class, "{F07D22C4-2F1A-4884-A4D9-0965652AD74D}" );
    }

    /**
     * ICDMStudioCompareResult Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResult createCDMStudioCompareResult() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResult.class, "{419E74D4-0BE7-433A-8E22-AB471451EF50}" );
    }

    /**
     * ICDMStudioCompareResultItem Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItem createCDMStudioCompareResultItem() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItem.class, "{DB88FD5A-0955-423A-A531-81BC497DF834}" );
    }

    /**
     * ICDMStudioCompareResultItemCol Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItemCol createCDMStudioCompareResultItemCol() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItemCol.class, "{3CBF6128-E3F5-492D-9CF6-75331B6A7512}" );
    }

    /**
     * ICDMDescriptionFile Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDescriptionFile createCDMDescriptionFile() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDescriptionFile.class, "{F56BEC32-4173-411B-BEB4-6075A9AA227C}" );
    }

    /**
     * ICDMParameterSetFile Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterSetFile createCDMParameterSetFile() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterSetFile.class, "{C93B2FCD-9A43-4CD2-A09D-E1383B13AE7B}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.IDetailsViewImpl createDetailsViewImpl() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.IDetailsViewImpl.class, "{8DF51CB1-DDB6-4B02-9650-3FD88BC7A791}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramSet createCDMProgramSet() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramSet.class, "{3CC3AF3F-DD09-4EE0-BA02-0B0F531F9B55}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin createCDMDlgPagePluginDummy2() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin.class, "{41B7B6A7-3C18-444A-9310-0FB4BC1818A1}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin createCDMDlgPagePluginDummy() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.IDlgPagePlugin.class, "{20128F2E-7662-40F2-BEE0-A2D2AD7A61E1}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionVariantResult createCDMCreateDatasetRevisionVariantResultDummy2() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionVariantResult.class, "{2012EC2E-7662-40F2-BEF0-A262AD74A1E1}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySettings createCDMDeliverySettingsDummy() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySettings.class, "{A457BA13-F8C7-4197-94D0-52054221DE0D}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryResult createCDMDeliveryResultDummy() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryResult.class, "{593E5E45-9E54-441E-9D89-5BEEFF022B83}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties createCDMDeliveryPropertiesDummy() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryProperties.class, "{474247EE-BC14-4FBA-825D-5E5FC0C9EEE9}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile createCDMDeliverySourceFileDummy() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySourceFile.class, "{8CB50102-4ECE-4D55-9BFF-01FFB803975A}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryDatasetResult createCDMDeliveryDatasetResultDummy() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryDatasetResult.class, "{53121734-3CFE-49E5-88D2-B8BEB7A11122}" );
    }

    /**
     * Application Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryAssignment createCDMDeliveryAssignmentDummy() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryAssignment.class, "{0AC4F73D-9F01-43C5-8D08-E30B61AB14B3}" );
    }

    /**
     * CDMQueryValuesSetting Class
     */
    public static com.bosch.easee.eASEEcdm_ComAPI.ICDMQueryValuesSetting createCDMQueryValuesSetting() {
        return COM4J.createInstance( com.bosch.easee.eASEEcdm_ComAPI.ICDMQueryValuesSetting.class, "{C7390F1D-3748-4BC8-8CFA-EEA244EE2985}" );
    }
}
