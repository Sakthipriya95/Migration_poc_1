package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IPluginInterface Interface
 */
@IID("{A7AC6106-96FA-4DAD-B7AB-24B5DFDE8FF7}")
public interface IPluginInterface extends Com4jObject {
    /**
     * method GetExtensionInterface
     */
    @VTID(7)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getExtensionInterface();

}
