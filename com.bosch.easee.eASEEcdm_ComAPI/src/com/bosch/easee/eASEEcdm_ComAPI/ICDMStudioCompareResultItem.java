package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMStudioCompareResultItem Interface
 */
@IID("{9451B043-7E7B-48CE-B9E9-0BA0C540CA5A}")
public interface ICDMStudioCompareResultItem extends Com4jObject {
    /**
     * property Source
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMStudioCallObject source();

    /**
     * property UnequalParameters
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection unequalParameters();

    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object unequalParameters(
        int index);

    /**
     * property EqualParameters
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection equalParameters();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object equalParameters(
        int index);

    /**
     * property MissingParameters
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection missingParameters();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object missingParameters(
        int index);

    /**
     * property NewParameters
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection newParameters();

    @VTID(11)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object newParameters(
        int index);

}
