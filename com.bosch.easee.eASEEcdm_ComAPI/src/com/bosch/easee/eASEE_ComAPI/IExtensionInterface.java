package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IExtensionInterface Interface
 */
@IID("{54B3C3AE-4F29-4CA8-9F2A-DFED982CFFCC}")
public interface IExtensionInterface extends Com4jObject {
    /**
     * method GetExtension
     */
    @VTID(7)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getExtension(
        java.lang.String progId);

}
