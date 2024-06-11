package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMReplaceVersionsItem Interface
 */
@IID("{A92B945C-0790-4D00-B7E6-2C1B4CB11DE9}")
public interface ICDMReplaceVersionsItem extends Com4jObject {
    /**
     * property Group
     */
    @VTID(7)
    int group();

    /**
     * property Version
     */
    @VTID(8)
    int version();

    /**
     * property Replacement
     */
    @VTID(9)
    int replacement();

}
