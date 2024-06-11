package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IClientObjCol Interface
 */
@IID("{6ACE42A1-BE54-4707-9AF7-ED3164A2B9E9}")
public interface IClientObjCol extends Com4jObject,Iterable<Com4jObject> {
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
