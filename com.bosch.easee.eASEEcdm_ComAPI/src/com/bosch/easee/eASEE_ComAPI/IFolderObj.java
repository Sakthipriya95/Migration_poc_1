package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IFolderObj Interface
 */
@IID("{72F9AC57-664B-4FF6-8A84-29D6FE588D24}")
public interface IFolderObj extends com.bosch.easee.eASEE_ComAPI.IClientObj {
    /**
     * property FolderName
     */
    @VTID(7)
    java.lang.String folderName();

    /**
     * property FolderId
     */
    @VTID(8)
    java.lang.String folderId();

    /**
     * property IsQuery
     */
    @VTID(9)
    boolean isQuery();

    /**
     * property HasChildren
     */
    @VTID(10)
    boolean hasChildren();

    /**
     * method GetChildren
     */
    @VTID(11)
    com.bosch.easee.eASEE_ComAPI.IFolderCollection getChildren();

    /**
     * method GetContent
     */
    @VTID(12)
    com.bosch.easee.eASEE_ComAPI.IVersionCollection getContent();

    /**
     * method GetParentNCO
     */
    @VTID(13)
    com.bosch.easee.eASEE_ComAPI.IVersionObj getParentNCO();

    /**
     * method GetParentFolder
     */
    @VTID(14)
    com.bosch.easee.eASEE_ComAPI.IFolderObj getParentFolder();

    /**
     * property IsFavorite
     */
    @VTID(15)
    boolean isFavorite();

}
