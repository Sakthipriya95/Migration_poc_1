package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDescriptionFileVersion Interface
 */
@IID("{DBF7AC2F-E117-4124-BADF-F873BFA9D206}")
public interface ICDMDescriptionFileVersion extends Com4jObject {
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
     * method GetContent
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDescriptionFile getContent(
        boolean iTakeOwnerShip);

    /**
     * method FetchFile
     */
    @VTID(10)
    void fetchFile(
        java.lang.String iPathName);

}
