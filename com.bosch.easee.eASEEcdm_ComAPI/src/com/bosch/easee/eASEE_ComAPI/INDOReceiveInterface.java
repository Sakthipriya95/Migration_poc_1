package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * INDOReceiveInterface Interface
 */
@IID("{BA1F56C3-20B4-49E5-8E36-34587B3B2FE6}")
public interface INDOReceiveInterface extends Com4jObject {
    /**
     * method GetNDOInterface
     */
    @VTID(7)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getNDOInterface(
        int lVersion,
        java.lang.String bstrClassname);

}
