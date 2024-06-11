package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMMessageCol Interface
 */
@IID("{F7E6BB87-CB52-4245-B92C-41243BF2FC95}")
public interface ICDMMessageCol extends Com4jObject,Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(8)
    @DefaultMethod
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object item(
        int index);

    @VTID(9)
    int count();

}
