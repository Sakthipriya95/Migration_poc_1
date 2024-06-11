package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMFileType Interface
 */
@IID("{445D93B8-61BC-4FC3-9B29-752E6AEE535B}")
public interface ICDMFileType extends Com4jObject {
    /**
     * property Type
     */
    @VTID(7)
    java.lang.String type();

    /**
     * property FileExtension
     */
    @VTID(8)
    java.lang.String fileExtension();

    /**
     * property Description
     */
    @VTID(9)
    java.lang.String description();

    /**
     * property ElementType
     */
    @VTID(10)
    java.lang.String elementType();

    /**
     * method SupportsFeatures
     */
    @VTID(11)
    boolean supportsFeatures(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmFileTypeFeature iFeatures);

}
