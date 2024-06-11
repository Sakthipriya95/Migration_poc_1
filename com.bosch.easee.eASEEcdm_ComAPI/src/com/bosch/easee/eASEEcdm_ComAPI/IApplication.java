package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * IApplication Interface
 */
@IID("{8990F57E-C146-40A8-94F1-72908769AB5C}")
public interface IApplication extends Com4jObject {
    /**
     * method ConnectNestor
     */
    @VTID(7)
    void connectNestor(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject __MIDL__IApplication0000);

    /**
     * method DisconnectNestor
     */
    @VTID(8)
    void disconnectNestor();

    /**
     * property PluginMenu
     */
    @VTID(9)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject pluginMenu();

    /**
     * method InitPlugin
     */
    @VTID(10)
    boolean initPlugin();

    /**
     * method ExitPlugin
     */
    @VTID(11)
    boolean exitPlugin();

    /**
     * property DispIDFilterMsg
     */
    @VTID(12)
    int dispIDFilterMsg();

    /**
     * property DispIDInputIdle
     */
    @VTID(13)
    int dispIDInputIdle();

    /**
     * method ProcessInputIdle
     */
    @VTID(14)
    void processInputIdle();

    /**
     * method FilterMsg
     */
    @VTID(15)
    boolean filterMsg(
        int lHwnd,
        int lMsg,
        int wParam,
        int lParam,
        int lTime,
        int x,
        int y);

    /**
     * method dispatchADHCmd
     */
    @VTID(16)
    int dispatchPluginCmd(
        @MarshalAs(NativeType.VARIANT) java.lang.Object itemArray,
        @MarshalAs(NativeType.VARIANT) java.lang.Object vParam);

    /**
     * method IsLegalADHCmd
     */
    @VTID(17)
    int isLegaldispatchPluginCmd(
        @MarshalAs(NativeType.VARIANT) java.lang.Object item,
        @MarshalAs(NativeType.VARIANT) java.lang.Object vParam);

    /**
     * method InitPluginPost
     */
    @VTID(18)
    boolean initPluginPost();

}
