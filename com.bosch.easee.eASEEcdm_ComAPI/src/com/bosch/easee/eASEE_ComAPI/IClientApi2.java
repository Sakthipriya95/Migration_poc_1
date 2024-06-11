package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IClientApi2 Interface
 */
@IID("{5E3DAFFD-AFC8-413E-A7E4-66AFACA58DB0}")
public interface IClientApi2 extends Com4jObject {
    /**
     * method CreateFilter
     */
    @VTID(7)
    com.bosch.easee.eASEE_ComAPI.IFilterObj createFilter(
        java.lang.String xml);

    /**
     * method GetVersionObject
     */
    @VTID(8)
    com.bosch.easee.eASEE_ComAPI.IVersionObj getVersionObject(
        int nVersNumber);

    /**
     * property UserID
     */
    @VTID(9)
    java.lang.String userID();

    /**
     * method GetActiveVersionObject
     */
    @VTID(10)
    com.bosch.easee.eASEE_ComAPI.IVersionObj getActiveVersionObject();

    /**
     * property Domain
     */
    @VTID(11)
    java.lang.String domain();

    /**
     * method AddToFavourites
     */
    @VTID(12)
    void addToFavourites(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion);

    /**
     * method GetSelectedObjectCount
     */
    @VTID(13)
    int getSelectedObjectCount();

    /**
     * method GetSelectedObject
     */
    @VTID(14)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getSelectedObject(
        int nIdx);

    /**
     * method GetSelectedVersionCount
     */
    @VTID(15)
    int getSelectedVersionCount();

    /**
     * method GetSelectedVersionObjects
     */
    @VTID(16)
    com.bosch.easee.eASEE_ComAPI.IVersionObj getSelectedVersionObjects(
        int nIdx);

    /**
     * method Checkin
     */
    @VTID(17)
    com.bosch.easee.eASEE_ComAPI.IVersionObj checkin(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        java.lang.String comment,
        java.lang.String filePath);

    /**
     * method Checkout
     */
    @VTID(18)
    com.bosch.easee.eASEE_ComAPI.IVersionObj checkout(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        java.lang.String variant,
        java.lang.String comment);

    /**
     * method FetchFile
     */
    @VTID(19)
    void fetchFile(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        java.lang.String filePath);

    /**
     * method RefreshAllViews
     */
    @VTID(20)
    void refreshAllViews();

    /**
     * method OpenNFO
     */
    @VTID(21)
    void openNFO(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion);

    /**
     * Validate a string against a predefined character set.
     */
    @VTID(22)
    boolean isValidString(
        com.bosch.easee.eASEE_ComAPI.CharacterSet charset,
        java.lang.String sString);

    /**
     * Validate a string against a predefined character set.
     */
    @VTID(23)
    java.lang.String getInvalidCharacters(
        com.bosch.easee.eASEE_ComAPI.CharacterSet charset,
        java.lang.String sString);

    /**
     * method GetRelationManager
     */
    @VTID(24)
    com.bosch.easee.eASEE_ComAPI.IRelationMgr getRelationManager();

    /**
     * method ReloadFavourites
     */
    @VTID(25)
    void reloadFavourites();

    /**
     * method InitializeUseTreeWithLazyLoading
     */
    @VTID(26)
    void initializeUseTreeWithLazyLoading(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pIRootVersion);

    /**
     * method AddFile
     */
    @VTID(27)
    com.bosch.easee.eASEE_ComAPI.IVersionObj createFileObject(
        java.lang.String _class,
        java.lang.String type,
        java.lang.String elementName,
        java.lang.String variant,
        java.lang.String comment,
        java.lang.String filePath,
        boolean keepCheckedOut);

    /**
     * method CreateContainer
     */
    @VTID(28)
    com.bosch.easee.eASEE_ComAPI.IVersionObj createContainerObject(
        java.lang.String _class,
        java.lang.String type,
        java.lang.String name,
        java.lang.String variant,
        java.lang.String comment);

    /**
     * method DeleteVersionObject
     */
    @VTID(29)
    void deleteVersionObject(
        int vers_nr,
        java.lang.String comment);

    /**
     * method GetActiveFolderObject
     */
    @VTID(30)
    com.bosch.easee.eASEE_ComAPI.IFolderObj getActiveFolderObject();

    /**
     * method AddToScratchpad
     */
    @VTID(31)
    void addToScratchpad(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion);

    /**
     * This method is vCDM-internal and not part of API
     */
    @VTID(32)
    com.bosch.easee.eASEE_ComAPI.IVersionObj getActiveRootVersionObject();

    /**
     * method GetDomainProperty
     */
    @VTID(33)
    java.lang.String getDomainProperty(
        java.lang.String domPropApplicationName,
        java.lang.String domPropName);

    /**
     * method DeleteVersionObject
     */
    @VTID(34)
    void deleteContextVersionObject(
        int versionNumber,
        int containerVersionNumber,
        java.lang.String comment);

    /**
     * method SearchVersionsByXml
     */
    @VTID(35)
    com.bosch.easee.eASEE_ComAPI.IVersionCol searchVersionsByXml(
        java.lang.String xmlfilterXML);

    /**
     * method FetchGroup
     */
    @VTID(36)
    void fetchGroup(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        java.lang.String filePath,
        boolean useUniqueNames);

}
