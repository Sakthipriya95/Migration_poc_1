package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IRegisterNDOInterface Interface
 */
@IID("{D3ABBEF9-5FD2-4FAB-9D87-79DA0B5BD219}")
public interface IRegisterNDOInterface extends Com4jObject {
    /**
     * method Register
     */
    @VTID(7)
    void register(
        java.lang.String className,
        com.bosch.easee.eASEE_ComAPI.INDOReceiveInterface pINDOReceiveInterface);

    /**
     * method Unregister
     */
    @VTID(8)
    void unregister(
        java.lang.String className,
        com.bosch.easee.eASEE_ComAPI.INDOReceiveInterface pINDOReceiveInterface);

}
