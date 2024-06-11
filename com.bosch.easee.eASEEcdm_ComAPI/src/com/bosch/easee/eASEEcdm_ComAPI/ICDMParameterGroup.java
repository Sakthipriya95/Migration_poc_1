package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMParameterGroup Interface
 */
@IID("{7881A191-73B2-4974-ACE1-D1BFB6FB940F}")
public interface ICDMParameterGroup extends Com4jObject {
    /**
     * method GetParameters
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameters();

    @VTID(7)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getParameters(
        int index);

    /**
     * method GetGroups
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getGroups();

    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getGroups(
        int index);

    /**
     * method GetGroupsOfParameter
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getGroupsOfParameter(
        java.lang.String iParameter);

    /**
     * method GetParametersOfGroup
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParametersOfGroup(
        java.lang.String iGroup);

}
