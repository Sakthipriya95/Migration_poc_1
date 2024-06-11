package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMReplaceVersionsResult Interface
 */
@IID("{C72A576A-1C98-4290-B8E2-7AC8D9BAA525}")
public interface ICDMReplaceVersionsResult extends com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo {
    /**
     * property ValidationResult
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult validationResult();

    /**
     * property ValidationMessages
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol validationMessages();

    @VTID(12)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object validationMessages(
        int index);

    /**
     * method GroupResult
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsGroupResult groupResult(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject group);

    /**
     * method GroupResultByVersionNumber
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMReplaceVersionsGroupResult groupResultByVersionNumber(
        int versionNumber);

    /**
     * property GroupResults
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection groupResults();

    @VTID(15)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object groupResults(
        int index);

}
