package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMActivationAssignment Interface
 */
@IID("{9B1D006F-C0E0-40EC-8B91-8560E166EB72}")
public interface ICDMActivationAssignment extends Com4jObject {
    /**
     * property DataSet
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset();

    /**
     * property ActivationItem
     */
    @VTID(8)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject activationItem();

    /**
     * property Activate
     */
    @VTID(9)
    boolean activate();

    /**
     * property ParametersToIgnore
     */
    @VTID(10)
    void parametersToIgnore(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection oParametersToIgnore);

    /**
     * property ParametersToIgnore
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection parametersToIgnore();

    @VTID(11)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object parametersToIgnore(
        int index);

}
