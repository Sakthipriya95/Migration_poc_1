package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IRelationMgr Interface
 */
@IID("{F3823D06-5F05-40D2-86AD-A508C44ECEED}")
public interface IRelationMgr extends Com4jObject {
    /**
     * method GetClassRelationCollection
     */
    @VTID(7)
    com.bosch.easee.eASEE_ComAPI.IRelationCollection getClassRelationCollection(
        java.lang.String className);

}
