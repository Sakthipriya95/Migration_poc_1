package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMParameterDef Interface
 */
@IID("{C7E0E6BB-BEE8-4FC6-AD2E-01AC9442089F}")
public interface ICDMParameterDef extends Com4jObject {
    /**
     * property Name
     */
    @VTID(7)
    java.lang.String name();

    /**
     * property Type
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamType type();

    /**
     * property Criterions
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection criterions();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object criterions(
        int index);

    /**
     * property Variants
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection variants(
        java.lang.String criterion);

    /**
     * property CriterionsAndVariants
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection criterionsAndVariants();

    @VTID(11)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object criterionsAndVariants(
        int index);

    /**
     * property VariantCoded
     */
    @VTID(12)
    boolean variantCoded();

    /**
     * property Function
     */
    @VTID(13)
    java.lang.String function();

    /**
     * property FunctionVersion
     */
    @VTID(14)
    java.lang.String functionVersion();

}
