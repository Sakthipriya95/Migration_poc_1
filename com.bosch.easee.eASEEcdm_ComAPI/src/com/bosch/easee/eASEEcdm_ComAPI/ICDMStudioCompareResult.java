package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMStudioCompareResult Interface
 */
@IID("{FAD3993D-EF17-47B7-9C45-F3604AD1BFED}")
public interface ICDMStudioCompareResult extends Com4jObject {
    /**
     * property Results
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItemCol results();

    @VTID(7)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItemCol.class})
    java.lang.Object results(
        int index);

    /**
     * method ResultFromSourceFilename
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItem resultFromSourceFilename(
        java.lang.String iSourceFileName);

    /**
     * method ResultFromSourceVersion
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItem resultFromSourceVersion(
        int iSourceVersion);

    /**
     * property Target
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject target();

    /**
     * property ValidationResult
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult validationResult();

    /**
     * property ProcessResult
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmProcessResult processResult();

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
     * property ProcessMessages
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol processMessages();

    @VTID(14)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object processMessages(
        int index);

}
