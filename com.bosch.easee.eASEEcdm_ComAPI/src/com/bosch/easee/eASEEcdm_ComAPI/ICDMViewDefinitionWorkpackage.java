package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMViewDefinitionWorkpackage Interface
 */
@IID("{23F4843D-7E90-4E69-92A0-8C01282B7398}")
public interface ICDMViewDefinitionWorkpackage extends Com4jObject {
    /**
     * property WorkpackageName
     */
    @VTID(7)
    java.lang.String workpackageName();

    /**
     * method GetFunctions
     */
    @VTID(8)
    java.lang.String[] getFunctions();

    /**
     * method GetGroups
     */
    @VTID(9)
    java.lang.String[] getGroups();

    /**
     * method GetParameters
     */
    @VTID(10)
    java.lang.String[] getParameters();

}
