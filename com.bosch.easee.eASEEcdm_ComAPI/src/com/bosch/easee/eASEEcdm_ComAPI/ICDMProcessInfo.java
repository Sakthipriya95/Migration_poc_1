package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMProcessInfo Interface
 */
@IID("{C3BDDBF3-A41C-4A03-BB95-D0AC887441AC}")
public interface ICDMProcessInfo extends Com4jObject {
    /**
     * property State
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.EProcessState state();

    /**
     * property ErrorMessage
     */
    @VTID(8)
    java.lang.String errorMessage();

    /**
     * property UserIntervention
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.EUserIntervention userIntervention();

    /**
     * method GetCreatedVersions
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getCreatedVersions();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getCreatedVersions(
        int index);

}
