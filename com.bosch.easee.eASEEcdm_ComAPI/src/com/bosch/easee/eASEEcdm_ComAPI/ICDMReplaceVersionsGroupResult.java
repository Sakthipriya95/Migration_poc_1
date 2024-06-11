package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMReplaceVersionsGroupResult Interface
 */
@IID("{B7FCC880-48AC-44E9-A783-EF01C488F55E}")
public interface ICDMReplaceVersionsGroupResult extends Com4jObject {
    /**
     * property ValidationResult
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult validationResult();

    /**
     * property ProcessResult
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmProcessResult processResult();

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
     * property ProcessMessages
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol processMessages();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object processMessages(
        int index);

    /**
     * property Group
     */
    @VTID(11)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject group();

}
