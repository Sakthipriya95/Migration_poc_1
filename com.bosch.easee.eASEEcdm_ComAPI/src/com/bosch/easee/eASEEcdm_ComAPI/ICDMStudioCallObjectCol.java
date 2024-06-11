package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMStudioCallObjectCol Interface
 */
@IID("{B62D3845-D4B0-4B84-8FAC-805E7B3A89D6}")
public interface ICDMStudioCallObjectCol extends Com4jObject,Iterable<Com4jObject> {
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
        com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject pInterface);

}
