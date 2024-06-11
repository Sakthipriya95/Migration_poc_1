package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IRelationObj Interface
 */
@IID("{6BBAB041-6E56-4CC0-BD48-0D74A236EB59}")
public interface IRelationObj extends Com4jObject {
    /**
     * property Name
     */
    @VTID(7)
    java.lang.String name();

    /**
     * property Direction
     */
    @VTID(8)
    com.bosch.easee.eASEE_ComAPI.RelationDirection direction();

    /**
     * method GetSourceVersion
     */
    @VTID(9)
    com.bosch.easee.eASEE_ComAPI.IVersionObj getSourceVersion();

    /**
     * method GetTargetVersionCollection
     */
    @VTID(10)
    com.bosch.easee.eASEE_ComAPI.IVersionCollection getTargetVersionCollection();

    /**
     * property DomainName
     */
    @VTID(11)
    java.lang.String domainName();

}
