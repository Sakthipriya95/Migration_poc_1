package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IFilterObj Interface
 */
@IID("{861FD293-1147-46D0-B998-5095EE1BED70}")
public interface IFilterObj extends com.bosch.easee.eASEE_ComAPI.IClientObj {
    /**
     * method Load
     */
    @VTID(7)
    void load(
        java.lang.String filterId);

    /**
     * method SearchVersions
     */
    @VTID(8)
    com.bosch.easee.eASEE_ComAPI.IVersionCol searchVersions();

    /**
     * property FilterId
     */
    @VTID(9)
    java.lang.String filterId();

    /**
     * property XML
     */
    @VTID(10)
    java.lang.String xml();

    /**
     * property XML
     */
    @VTID(11)
    void xml(
        java.lang.String pVal);

}
