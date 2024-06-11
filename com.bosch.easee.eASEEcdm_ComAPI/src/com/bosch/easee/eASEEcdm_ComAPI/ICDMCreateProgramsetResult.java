package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCreateProgramsetResult Interface
 */
@IID("{1CDEBD67-12DA-4EB1-BE49-9F394071701C}")
public interface ICDMCreateProgramsetResult extends com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo {
    /**
     * property ValidationResult
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult validationResult();

    /**
     * property CreatedProgramSet
     */
    @VTID(12)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject createdProgramSet();

    /**
     * Messages
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol messages();

    @VTID(13)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object messages(
        int index);

    /**
     * The created program set
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramSet createdProgramSetObj();

}
