package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMObjectFileVersion Interface
 */
@IID("{5081456B-9D67-42BF-9162-98C47651567C}")
public interface ICDMObjectFileVersion extends Com4jObject {
    /**
     * method GetVersionObj
     */
    @VTID(7)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getVersionObj();

    /**
     * method GetFilename
     */
    @VTID(8)
    java.lang.String getFilename(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmFilenameRule iFilenameRule);

    /**
     * method FetchFile
     */
    @VTID(9)
    void fetchFile(
        java.lang.String iPathName);

}
