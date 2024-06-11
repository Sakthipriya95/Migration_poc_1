package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMStudioCompareResultItemCol Interface
 */
@IID("{ABE592CE-A717-49B3-B28B-F0198B4C2392}")
public interface ICDMStudioCompareResultItemCol extends Com4jObject,Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(8)
    @DefaultMethod
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object item(
        int index);

    @VTID(9)
    int count();

    /**
     * method Add
     */
    @VTID(10)
    void add(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResultItem pInterface);

}
