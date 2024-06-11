package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * _IContextEvents Interface
 */
@IID("{4EA12033-C8B7-4C8D-A848-1F2E322AE8A4}")
public interface _IContextEvents extends Com4jObject {
    /**
     * method OnContextChanging
     */
    @VTID(7)
    void onContextChanging(
        java.lang.String contextTemplateName,
        java.lang.String contextInstanceName,
        Holder<Boolean> change);

    /**
     * method OnContextChanged
     */
    @VTID(8)
    void onContextChanged(
        java.lang.String contextTemplateName,
        java.lang.String contextInstanceName);

}
