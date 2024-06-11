package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMExtractCompleteParmetersetResult Interface
 */
@IID("{84C46B70-BFB9-4730-9536-4D1BFC497E83}")
public interface ICDMExtractCompleteParmetersetResult extends Com4jObject {
    /**
     * property HasResult
     */
    @VTID(7)
    boolean hasResult();

    /**
     * property FileName
     */
    @VTID(8)
    java.lang.String fileName();

    /**
     * property ExtractMessages
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol extractMessages();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object extractMessages(
        int index);

}
