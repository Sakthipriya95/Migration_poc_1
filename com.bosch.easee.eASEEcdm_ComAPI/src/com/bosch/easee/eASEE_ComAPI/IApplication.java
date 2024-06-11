package com.bosch.easee.eASEE_ComAPI;

import com4j.*;


/**
 * IApplication Interface
 */
@IID("{875D0D02-FB5C-40EC-92C7-005C713FD5DC}")
public interface IApplication extends Com4jObject {
    /**
     * method Connect
     */
    @VTID(7)
    void connect(java.lang.String sUsername, java.lang.String sPassword,
        java.lang.String sDomain, java.lang.String sServer);

    /**
     * method Disconnect
     */
    @VTID(8)
    void disconnect();

    /**
     * property IsConnected
     */
    @VTID(9)
    boolean isConnected();

    /**
     * property TempPath
     */
    @VTID(10)
    java.lang.String tempPath();

    /**
     * property MainWnd
     */
    @VTID(11)
    int mainWnd();

    /**
     * method RegisterUpdateWindow
     */
    @VTID(12)
    void registerUpdateWindow(int hwndUpdate);

    /**
     * method OpenMDIWnd
     */
    @VTID(13)
    int openMDIWnd(java.lang.String sTitle);

    /**
     * method CloseMDIWnd
     */
    @VTID(14)
    void closeMDIWnd(int hWnd);

    /**
     * method UpdateMDIWnd
     */
    @VTID(15)
    void updateMDIWnd(int hWnd);

    /**
     * method ExecuteHelp
     */
    @VTID(16)
    void executeHelp(java.lang.String sContext);

    /**
     * method SetMDIWndTitle
     */
    @VTID(17)
    void setMDIWndTitle(int hWnd, java.lang.String sTitle);

    /**
     * method RefreshView
     */
    @VTID(18)
    void refreshView(int hWnd);

    /**
     * method GetActiveView
     */
    @VTID(19)
    int getActiveView();

    /**
     * method AddActionText
     */
    @VTID(20)
    boolean addActionText(java.lang.String sText, boolean bWithTimeStamp);

    /**
     * method SetStatusInfo
     */
    @VTID(21)
    void setStatusInfo(java.lang.String statusInfoText);

    /**
     * property ClientApi2
     */
    @VTID(22)
    com.bosch.easee.eASEE_ComAPI.IClientApi2 clientApi2();

    /**
     * method ForceActiveViewRefresh
     */
    @VTID(23)
    void forceActiveViewRefresh();

    /**
     * method RefreshPrefListView
     */
    @VTID(24)
    void refreshPrefListView();

    /**
     * Creates and returns Generic View
     */
    @VTID(25)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject createGCView(java.lang.String gcvCaption);

    /**
     * Returns login data of the logged in user
     */
    @VTID(26)
    void getLoginData(
        @MarshalAs(NativeType.Dispatch)
    com4j.Com4jObject pDispPlugin, Holder<java.lang.String> pUserName,
        Holder<java.lang.String> pPassword, Holder<java.lang.String> pDomain,
        Holder<java.lang.String> pDatabase);

    /**
     * method SetDomain
     */
    @VTID(27)
    void setDomain(java.lang.String domain);

    /**
     * method SetActiveContext
     */
    @VTID(28)
    void setActiveContext(java.lang.String contextTemplateName,
        java.lang.String contextInstanceName);

    /**
     * property SilentMode
     */
    @VTID(29)
    boolean silentMode();

    /**
     * method CreateContentView
     */
    @VTID(30)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject createContentView(java.lang.String caption, int hIcon,
        boolean bRestoreable, java.lang.String classID,
        java.lang.String restoreWindowData);

    /**
     * method CreateGroupEditView
     */
    @VTID(31)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject createGroupEditView(int version);

    /**
     * method CreateSearchDialog
     */
    @VTID(32)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject createSearchDialog();

    /**
     * method GetPlugin
     */
    @VTID(33)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject getPlugin(java.lang.String progId);

    /**
     * method GetActiveViewType
     */
    @VTID(34)
    com.bosch.easee.eASEE_ComAPI.IViewTypeInfo getActiveViewType();

    /**
     * method Exit
     */
    @VTID(35)
    void exit();

    /**
     * method CreateDeleteObjectDialog
     */
    @VTID(36)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject createDeleteObjectDialog();

    /**
     * method CreateGenericTreeView
     */
    @VTID(37)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject createGenericTreeView(java.lang.String templateName,
        java.lang.String instanceName, boolean createIfNotExists);

    /**
     * method RefreshFavoritesView
     */
    @VTID(38)
    void refreshFavoritesView(boolean bReload);

    /**
     * method RefreshGenericViewBar
     */
    @VTID(39)
    void refreshGenericViewBar(boolean bReload);

    /**
     * method RefreshDockingViews
     */
    @VTID(40)
    void refreshDockingViews(boolean bReload);

    /**
     * method ExecuteDefaultAction
     */
    @VTID(41)
    void executeDefaultAction(int versionId);

    /**
     * method GetActualApplicationVersion
     */
    @VTID(42)
    java.lang.String getActualApplicationVersion(java.lang.String applName,
        Holder<Integer> pMajorVersion, Holder<Integer> pMinorVersion,
        Holder<Integer> pReleaseVersion, Holder<Integer> pBuildVersion);

    /**
     * Creates a performance timer
     */
    @VTID(43)
    void createPerformanceTimer(java.lang.String name,
        java.lang.String objectDescription, int typeCode);

    /**
     * Restarts given performance timer
     */
    @VTID(44)
    void restartPerformanceTimer(java.lang.String name);

    /**
     * Stop performance timer and write result to log
     */
    @VTID(45)
    void stopPerformanceTimer(java.lang.String name, boolean destroyTimer);

    /**
     * Sets a client option
     */
    @VTID(46)
    void setOption(java.lang.String optionName, java.lang.String optionValue);

    /**
     * Gets a client option
     */
    @VTID(47)
    java.lang.String getOption(java.lang.String optionName);

    /**
     * This method is vCDM-internal and not part of API
     */
    @VTID(48)
    void refreshViewWithVersion(int hWnd, int versionNo);

    /**
     * Set project explorer project version
     */
    @VTID(49)
    void setContentVersion(int hWnd, java.lang.String restoreData);

    /**
     * Notify about a changed selection
     */
    @VTID(50)
    void notifySelectionChanged();

    /**
     * method ConnectUI
     */
    @VTID(51)
    void connectUI();

    /**
     * method OnVersionsDeleted
     */
    @VTID(52)
    void onVersionsDeleted(
        @MarshalAs(NativeType.VARIANT)
    java.lang.Object versionNumbers);

    /**
     * This method is vCDM-internal and not part of API
     */
    @VTID(53)
    void refreshActiveConfigTreeView();
}
