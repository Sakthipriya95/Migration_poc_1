package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMProgramSet Interface represents an ECU software container
 */
@IID("{C1C629EC-A7E9-460C-9486-04E990AA36E6}")
public interface ICDMProgramSet extends Com4jObject {
    /**
     * Retrieves the base system version object.
     */
    @VTID(7)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getVersionObj();

    /**
     * method Sets the parameter dependency matrix
     */
    @VTID(8)
    void setParameterDependencies(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iDependencies);

    /**
     * method Gets the parameter dependency matrix
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameterDependencies();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getParameterDependencies(
        int index);

}
