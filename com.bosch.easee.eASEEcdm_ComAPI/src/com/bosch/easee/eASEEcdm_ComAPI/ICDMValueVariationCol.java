package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMValueVariationCol Interface
 */
@IID("{5DCFB0D7-1A04-4493-AAA2-0A8385AB0AB6}")
public interface ICDMValueVariationCol extends Com4jObject,Iterable<Com4jObject> {
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
        com.bosch.easee.eASEEcdm_ComAPI.ICDMValueVariation pInterface);

}
