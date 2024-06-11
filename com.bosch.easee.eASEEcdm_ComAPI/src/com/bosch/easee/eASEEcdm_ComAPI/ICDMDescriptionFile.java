package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDescriptionFile Interface
 */
@IID("{00F8BA69-3E8D-4808-846C-0A71659E72D2}")
public interface ICDMDescriptionFile extends Com4jObject {
    /**
     * property FilePath
     */
    @VTID(7)
    java.lang.String filePath();

    /**
     * method GetParameters
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameters();

    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getParameters(
        int index);

    /**
     * method GetFunctions
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getFunctions();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getFunctions(
        int index);

    /**
     * method GetParametersByAddressRange
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMParameterGroup getParametersByAddressRange(
        java.lang.String iRoot,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iAddressRanges);

    /**
     * method GetParameterListByAddressRange
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameterListByAddressRange(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iAddressRanges);

}
