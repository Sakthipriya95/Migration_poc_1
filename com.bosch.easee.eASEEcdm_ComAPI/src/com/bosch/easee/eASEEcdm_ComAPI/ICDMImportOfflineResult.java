package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMImportOfflineResult Interface
 */
@IID("{9DCB00EA-5EFD-4D00-A0D8-CD6A2CE4D973}")
public interface ICDMImportOfflineResult extends com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo,Iterable<Com4jObject> {
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
