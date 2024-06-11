package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICdmInvokeCommand Interface
 */
@IID("{367BAF63-3ED7-40AC-BF0A-1BCC5621E3C4}")
public interface ICdmInvokeCommandItem extends Com4jObject {
    /**
     * method Id
     */
    @VTID(7)
    int id();

    /**
     * method Name
     */
    @VTID(8)
    java.lang.String name();

    /**
     * method Description
     */
    @VTID(9)
    java.lang.String description();

}
