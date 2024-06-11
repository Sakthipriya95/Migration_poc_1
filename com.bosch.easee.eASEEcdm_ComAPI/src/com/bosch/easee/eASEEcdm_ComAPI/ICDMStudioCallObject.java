package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMStudioCallObject Interface
 */
@IID("{A94CFAF0-3B29-45E9-9CA5-0F9FBD644BA4}")
public interface ICDMStudioCallObject extends Com4jObject {
    /**
     * property VersionNumber
     */
    @VTID(7)
    void versionNumber(
        int oVersionNo);

    /**
     * property FileName
     */
    @VTID(8)
    void fileName(
        java.lang.String oFilename);

    /**
     * property A2lVersionNumber
     */
    @VTID(9)
    void a2lVersionNumber(
        int oA2lVersionNo);

    /**
     * property A2lFileName
     */
    @VTID(10)
    void a2lFileName(
        java.lang.String oA2lVersionNo);

    /**
     * property VersionNumber
     */
    @VTID(11)
    int versionNumber();

    /**
     * property FileName
     */
    @VTID(12)
    java.lang.String fileName();

    /**
     * property A2lVersionNumber
     */
    @VTID(13)
    int a2lVersionNumber();

    /**
     * property A2lFileName
     */
    @VTID(14)
    java.lang.String a2lFileName();

}
