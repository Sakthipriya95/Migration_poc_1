package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMParameterValueDependency Interface
 */
@IID("{1BAAC536-34D0-4840-9315-B7F0485449FB}")
public interface ICDMParameterValueDependency extends Com4jObject {
    /**
     * method GetParameterDef
     */
    @VTID(7)
    java.lang.String getParameterDef();

    /**
     * method GetValueVariations
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMValueVariationCol getValueVariations();

    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMValueVariationCol.class})
    java.lang.Object getValueVariations(
        int index);

}
