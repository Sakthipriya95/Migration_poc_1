package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDatasetExportDatasetResult Interface
 */
@IID("{26FAB54F-5F6D-4426-98B2-C1AE8777B95D}")
public interface ICDMDatasetExportDatasetResult extends Com4jObject {
    /**
     * property ValidationResult
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult validationResult();

    /**
     * property IntegrationResult
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmProcessResult integrationResult();

    /**
     * property ValidationMessages
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol validationMessages();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object validationMessages(
        int index);

    /**
     * property IntegrationMessages
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol integrationMessages();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object integrationMessages(
        int index);

    /**
     * property Filename
     */
    @VTID(11)
    java.lang.String fileName();

    /**
     * property DescriptionFilename
     */
    @VTID(12)
    java.lang.String descriptionFilename();

    /**
     * property CopyLog
     */
    @VTID(13)
    java.lang.String copyLog();

    /**
     * property Dataset
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset();

}
