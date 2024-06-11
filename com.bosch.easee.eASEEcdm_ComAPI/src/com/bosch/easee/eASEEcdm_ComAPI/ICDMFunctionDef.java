package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMFunctionDef Interface
 */
@IID("{A18DC392-5BF1-4EA3-83DE-64635797E425}")
public interface ICDMFunctionDef extends Com4jObject {
    /**
     * property Name
     */
    @VTID(7)
    java.lang.String name();

    /**
     * property Version
     */
    @VTID(8)
    java.lang.String version();

    /**
     * property Parameters
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection parameters();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object parameters(
        int index);

}
