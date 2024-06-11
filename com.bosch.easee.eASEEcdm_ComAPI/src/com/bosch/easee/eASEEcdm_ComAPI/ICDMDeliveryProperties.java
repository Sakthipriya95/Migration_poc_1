package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDeliveryProperties Interface
 */
@IID("{11885EE4-628C-4F83-BFA2-11119A99E7FD}")
public interface ICDMDeliveryProperties extends Com4jObject {
    /**
     * property Activate
     */
    @VTID(7)
    void activate(
        boolean activate);

    /**
     * property Activate
     */
    @VTID(8)
    boolean activate();

    /**
     * method AddTargetVariant
     */
    @VTID(9)
    void addTargetVariant(
        java.lang.String variant);

    /**
     * method GetTargetVariants
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getTargetVariants();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getTargetVariants(
        int index);

}
