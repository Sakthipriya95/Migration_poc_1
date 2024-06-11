package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDatasetExportinResult Interface
 */
@IID("{EAFD44BC-3C15-4015-B414-E3657E549285}")
public interface ICDMDatasetExportResult extends Com4jObject {
    /**
     * HasDatasetResult
     */
    @VTID(7)
    boolean hasDatasetResult(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset pDataset);

    /**
     * HasDatasetResultByVersionNumber
     */
    @VTID(8)
    boolean hasDatasetResultByVersionNumber(
        int versionNumber);

    /**
     * GetDatasetResult
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetResult getDatasetResult(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset pDataset);

    /**
     * GetDatasetResultByVersionNumber
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportDatasetResult getDatasetResultByVersionNumber(
        int versionNumber);

    /**
     * property PreValidationResult
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult preValidationResult();

    /**
     * property PreValidationMessages
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol preValidationMessages();

    @VTID(12)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object preValidationMessages(
        int index);

    /**
     * property DatasetResults
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection datasetResults();

    @VTID(13)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object datasetResults(
        int index);

}
