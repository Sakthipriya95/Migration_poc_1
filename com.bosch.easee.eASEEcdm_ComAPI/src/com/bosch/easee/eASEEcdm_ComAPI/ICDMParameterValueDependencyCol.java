package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMParameterValueDependencyCol Interface
 */
@IID("{49FEF5D7-9BFD-4C9E-ABDF-DF4AF63BC6CC}")
public interface ICDMParameterValueDependencyCol extends Com4jObject,Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(8)
    @DefaultMethod
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object item(
        int index);

    @VTID(9)
    int count();

    /**
     * method Add
     */
    @VTID(10)
    void add(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValueDependency pInterface);

}
