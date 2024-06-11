package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMVersionInfo Interface
 */
@IID("{0B671142-43BD-43C7-B7FC-FD8F488AEF1C}")
public interface ICDMVersionInfo extends Com4jObject {
    /**
     * property ProductMajorVersion
     */
    @VTID(7)
    int productMajorVersion();

    /**
     * property ProductMinorVersion
     */
    @VTID(8)
    int productMinorVersion();

    /**
     * property BuildMajorVersion
     */
    @VTID(9)
    int buildMajorVersion();

    /**
     * property BuildMinorVersion
     */
    @VTID(10)
    int buildMinorVersion();

    /**
     * property PluginFilename
     */
    @VTID(11)
    java.lang.String pluginFilename();

    /**
     * property CdmStudioVersion
     */
    @VTID(12)
    java.lang.String cdmStudioVersion();

    /**
     * property CdmStudioLocation
     */
    @VTID(13)
    java.lang.String cdmStudioLocation();

}
