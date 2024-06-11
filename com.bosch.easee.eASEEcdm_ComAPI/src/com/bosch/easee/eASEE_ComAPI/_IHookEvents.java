package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * _IHookEvents Interface
 */
@IID("{EE4EF3E9-A9CD-4B28-B335-4E49B462242C}")
public interface _IHookEvents extends Com4jObject {
    /**
     * method OnDefaultOpen
     */
    @VTID(7)
    void onDefaultOpen(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        Holder<Boolean> pHandeled);

    /**
     * method OnNewObjectsCreated
     */
    @VTID(8)
    void onNewObjectsCreated(
        com.bosch.easee.eASEE_ComAPI.IVersionCollection pVersionCollection,
        com.bosch.easee.eASEE_ComAPI.ObjectCreationType ocType,
        com.bosch.easee.eASEE_ComAPI.ClientContextType ccType);

    /**
     * method OnFileTransferring
     */
    @VTID(9)
    void onFileTransferring(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        java.lang.String filePath,
        Holder<Boolean> cancelTransfer);

    /**
     * method OnFileTransferred
     */
    @VTID(10)
    void onFileTransferred(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        java.lang.String filePath);

    /**
     * method OnInsertIntoContainer
     */
    @VTID(11)
    java.lang.String onInsertIntoContainer(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        com.bosch.easee.eASEE_ComAPI.IVersionObj pContainer,
        com.bosch.easee.eASEE_ComAPI.IFolderObj pTargetFolder);

    /**
     * method OnCheckingInRecursive
     */
    @VTID(12)
    void onCheckingInRecursive(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        Holder<Boolean> cancelRecursiveCheckin);

    /**
     * method OnCheckedInRecursive
     */
    @VTID(13)
    void onCheckedInRecursive(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        boolean successful);

    /**
     * method OnFileTransferConflict
     */
    @VTID(14)
    void onFileTransferConflict(
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersion,
        java.lang.String filePath,
        Holder<com.bosch.easee.eASEE_ComAPI.TransferConflictSolution> tcSolution);

    /**
     * method OnRefreshingAll
     */
    @VTID(15)
    void onRefreshingAll(
        Holder<Boolean> refresh);

    /**
     * method OnRefreshedAll
     */
    @VTID(16)
    void onRefreshedAll();

    /**
     * method OnFilesDropped
     */
    @VTID(17)
    void onFilesDropped(
        java.lang.String[] filePaths,
        Holder<Boolean> continueDefaultProcessing);

}
