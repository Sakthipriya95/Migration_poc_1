package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IVersionCollection Interface
 */
@IID("{9A5C5126-E52F-44BD-8DC8-10C13F93EA58}")
public interface IVersionCollection extends com.bosch.easee.eASEE_ComAPI.IClientObjCol {
    /**
     * method Add
     */
    @VTID(10)
    void add(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion);

    /**
     * method Remove
     */
    @VTID(11)
    void remove(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion);

    /**
     * method Get
     */
    @VTID(12)
    com.bosch.easee.eASEE_ComAPI.IVersionObj get(
        int versionNumber);

    /**
     * method Clear
     */
    @VTID(13)
    void clear();

}
