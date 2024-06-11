package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IGTView Interface
 */
@IID("{4E2EFA96-889B-4B23-91D2-35CF705F7B0A}")
public interface IGTView extends com.bosch.easee.eASEE_ComAPI.IDialog {
    /**
     * method Show
     */
    @VTID(9)
    void show();

    /**
     * method Hide
     */
    @VTID(10)
    void hide();

    /**
     * method Reload
     */
    @VTID(11)
    void reload();

    /**
     * method Destroy
     */
    @VTID(12)
    void destroy();

    /**
     * method AddFolder
     */
    @VTID(13)
    void addFolder(
        java.lang.String name);

    /**
     * method AddQuery
     */
    @VTID(14)
    void addQuery(
        java.lang.String name,
        java.lang.String query);

    /**
     * property Visible
     */
    @VTID(15)
    boolean visible();

    /**
     * property Visible
     */
    @VTID(16)
    void visible(
        boolean pVal);

    /**
     * property ViewID
     */
    @VTID(17)
    java.lang.String viewID();

    /**
     * property ViewID
     */
    @VTID(18)
    void viewID(
        java.lang.String pVal);

    /**
     * property ParentFrameWnd
     */
    @VTID(19)
    void parentFrameWnd(
        int rhs);

    /**
     * method SelectVersion
     */
    @VTID(20)
    void selectVersion(
        java.lang.String parentFolderID,
        int versionNumber);

    /**
     * method GetFolderIDByName
     */
    @VTID(21)
    java.lang.String getFolderIDByName(
        java.lang.String folderName);

    /**
     * method GetFixedFolderIDByName
     */
    @VTID(22)
    java.lang.String getFixedFolderIDByName(
        java.lang.String folderName);

    /**
     * method GetActiveObject
     */
    @VTID(23)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getActiveObject();

    /**
     * method GetSelectedObjectCount
     */
    @VTID(24)
    int getSelectedObjectCount();

    /**
     * method GetSelectedObject
     */
    @VTID(25)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getSelectedObject(
        int nIdx);

    /**
     * method GetFolderContent
     */
    @VTID(26)
    com.bosch.easee.eASEE_ComAPI.IVersionCollection getFolderContent(
        java.lang.String folderId);

    /**
     * method AddToFolder
     */
    @VTID(27)
    void addToFolder(
        java.lang.String folderId,
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersionObj);

    /**
     * method RemoveFromFolder
     */
    @VTID(28)
    void removeFromFolder(
        java.lang.String folderId,
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersionObj);

    /**
     * method GetSelectedVersions
     */
    @VTID(29)
    com.bosch.easee.eASEE_ComAPI.IVersionCollection getSelectedVersions();

}
