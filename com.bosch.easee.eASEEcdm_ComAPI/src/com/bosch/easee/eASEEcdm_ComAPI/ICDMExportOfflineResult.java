package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMExportOfflineResult Interface
 */
@IID("{231ABA5A-7429-4251-8B12-31DEE8E73898}")
public interface ICDMExportOfflineResult extends com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo,Iterable<Com4jObject> {
    @VTID(11)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(12)
    @DefaultMethod
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object item(
        int index);

    @VTID(13)
    int count();

    /**
     * Messages
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol messages();

    @VTID(14)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol.class})
    java.lang.Object messages(
        int index);

}
