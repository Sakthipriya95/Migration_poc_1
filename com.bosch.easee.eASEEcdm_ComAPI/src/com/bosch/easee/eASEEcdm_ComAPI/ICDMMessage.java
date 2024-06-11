package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMMessage Interface
 */
@IID("{C3AA7A9A-F988-51D1-3BB4-63E7481373C0}")
public interface ICDMMessage extends Com4jObject {
    /**
     * property Type
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmMessageType type();

    /**
     * property Text
     */
    @VTID(8)
    java.lang.String text();

    /**
     * property TimeStamp
     */
    @VTID(9)
    java.lang.String timeStamp();

}
