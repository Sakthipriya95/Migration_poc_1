package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDatasetExportDatasetSettings Interface
 */
@IID("{0F88E316-4D1B-4AFC-AFF8-FD6D4A966505}")
public interface ICDMDatasetExportDatasetSettings extends Com4jObject {
    /**
     * property file type
     */
    @VTID(7)
    void fileType(
        java.lang.String oFileType);

    /**
     * property file type
     */
    @VTID(8)
    java.lang.String fileType();

    /**
     * property file name
     */
    @VTID(9)
    void fileName(
        java.lang.String oFilename);

    /**
     * property file name
     */
    @VTID(10)
    java.lang.String fileName();

    /**
     * property folder name
     */
    @VTID(11)
    void folderName(
        java.lang.String oFileFolder);

    /**
     * property folder name
     */
    @VTID(12)
    java.lang.String folderName();

    /**
     * property VariantsForParameterCheck
     */
    @VTID(13)
    void variantsForParameterCheck(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCriterionVariants iInterface);

    /**
     * property VariantsForParameterCheck
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCriterionVariants variantsForParameterCheck();

    /**
     * property CreateHexFileSignature
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMHexFileSignature createHexFileSignature();

    /**
     * property AddHexFileSignature
     */
    @VTID(16)
    void addHexFileSignature(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMHexFileSignature iInterface);

    /**
     * property GetHexFileSignatures
     */
    @VTID(17)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getHexFileSignatures();

    @VTID(17)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getHexFileSignatures(
        int index);

}
