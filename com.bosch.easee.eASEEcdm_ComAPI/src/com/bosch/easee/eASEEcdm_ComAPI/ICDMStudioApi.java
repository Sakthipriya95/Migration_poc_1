package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMStudioApi Interface
 */
@IID("{DFC206F7-560B-4C00-8046-75BA424BD9D4}")
public interface ICDMStudioApi extends Com4jObject {
    /**
     * method CreateCompareSettings
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareSettings createCompareSettings();

    /**
     * method Compare
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareResult compare(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCompareSettings iSettings);

    /**
     * property CdmStudioVersion
     */
    @VTID(9)
    java.lang.String cdmStudioVersion();

    /**
     * method CdmStudioCfgString
     */
    @VTID(10)
    java.lang.String cdmStudioConfigString();

}
