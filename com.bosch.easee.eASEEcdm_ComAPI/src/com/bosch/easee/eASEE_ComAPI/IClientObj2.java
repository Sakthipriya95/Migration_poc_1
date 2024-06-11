package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IClientObj Interface
 */
@IID("{1A3DBE09-5D25-4EE1-A3E6-0B0E6722BFCC}")
public interface IClientObj2 extends Com4jObject {
    /**
     * property DBGuid
     */
    @VTID(7)
    java.lang.String dbGuid();

    /**
     * property ObjectType
     */
    @VTID(8)
    int objectType();

}
