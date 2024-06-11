package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IVersionCol Interface
 */
@IID("{42B25DA6-A05C-43F1-B613-D96F7D3FCE1B}")
public interface IVersionCol extends com.bosch.easee.eASEE_ComAPI.IClientObjCol {
    /**
     * method InsertVersion
     */
    @VTID(10)
    com.bosch.easee.eASEE_ComAPI.IVersionObj insertVersion(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion);

    /**
     * method InsertVersionToFolder
     */
    @VTID(11)
    com.bosch.easee.eASEE_ComAPI.IVersionObj insertVersionToFolder(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        java.lang.String folderPath);

    /**
     * method RemoveVersionByIndex
     */
    @VTID(12)
    void removeVersionByIndex(
        int lIndex);

    /**
     * method RemoveVersion
     */
    @VTID(13)
    void removeVersion(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion);

    /**
     * method CreateFolder
     */
    @VTID(14)
    com.bosch.easee.eASEE_ComAPI.IClientObj2 createFolder(
        java.lang.String folderPath,
        java.lang.String newFolderName);

    /**
     * method RemoveFolder
     */
    @VTID(15)
    void removeFolder(
        java.lang.String folderPath);

    /**
     * method ExistsFolder
     */
    @VTID(16)
    boolean existsFolder(
        java.lang.String folderPath);

    /**
     * method GetFolder
     */
    @VTID(17)
    com.bosch.easee.eASEE_ComAPI.IFolderObj getFolder(
        java.lang.String folderPath);

    /**
     * method GetFolderCollection
     */
    @VTID(18)
    com.bosch.easee.eASEE_ComAPI.IFolderCollection getFolderCollection();

    /**
     * method ToVersionCollection
     */
    @VTID(19)
    com.bosch.easee.eASEE_ComAPI.IVersionCollection toVersionCollection();

}
