package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDeliveryResult Interface
 */
@IID("{7AF03F66-FE40-4372-9E80-88C23E964046}")
public interface ICDMDeliveryResult extends com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo {
    /**
     * property ValidationResult
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult validationResult();

    /**
     * property DeliveryResult
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmProcessResult deliveryResult();

    /**
     * property ValidationMessages
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol validationMessages();

    @VTID(13)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object validationMessages(
        int index);

    /**
     * property DeliveryMessages
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol deliveryMessages();

    @VTID(14)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object deliveryMessages(
        int index);

    /**
     * method DatasetResult
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryDatasetResult datasetResult(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset);

    /**
     * property DatasetResults
     */
    @VTID(16)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection datasetResults();

    @VTID(16)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object datasetResults(
        int index);

}
