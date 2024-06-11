package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMValueVariation Interface
 */
@IID("{E2904AF0-D8EB-4F27-A2B3-4CB6C0CF146F}")
public interface ICDMValueVariation extends Com4jObject {
    /**
     * method GetParameterValue
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterValue getParameterValue();

    /**
     * method GetDataSets
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol getDatasets();

    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class})
    java.lang.Object getDatasets(
        int index);

}
