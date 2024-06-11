package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDatasetCheckinDatasetResult Interface
 */
@IID("{296C1FB4-E95E-4841-9B1D-4F1A38CB360A}")
public interface ICDMDatasetCheckinDatasetResult extends Com4jObject {
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
     * property GetCopyLog
     */
    @VTID(11)
    java.lang.String getCopyLog(
        boolean iTakeOwnerShip);

}
