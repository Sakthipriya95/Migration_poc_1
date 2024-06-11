package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IDetailsViewInfo Interface
 */
@IID("{C1CF1D30-6743-4C33-99FD-65C31F04CCCB}")
public interface IDetailsViewInfo extends Com4jObject {
    /**
     * property DisplayName
     */
    @VTID(7)
    java.lang.String displayName();

    /**
     * property Description
     */
    @VTID(8)
    java.lang.String description();

    /**
     * property RegistryKey
     */
    @VTID(9)
    java.lang.String registryKey();

    /**
     * property IsVisibleDefault
     */
    @VTID(10)
    boolean isVisibleDefault();

}
