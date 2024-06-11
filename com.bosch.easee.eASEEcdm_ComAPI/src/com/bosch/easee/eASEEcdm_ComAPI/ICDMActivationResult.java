package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCreateDatasetResult Interface
 */
@IID("{A5D193DB-112E-451C-B1DF-BEAF2415A5C0}")
public interface ICDMActivationResult extends com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo {
    /**
     * Result
     */
    @VTID(11)
    boolean wasActivationSuccessful(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationAssignment iAssignment);

    /**
     * Messages
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol messages();

    @VTID(12)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object messages(
        int index);

}
