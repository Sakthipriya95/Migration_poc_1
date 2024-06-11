package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IPluginMenuItem Interface
 */
@IID("{F76CCF32-0F7D-46A2-B32B-BB40462F8C4E}")
public interface IPluginMenuItem extends Com4jObject {
    /**
     * method AttachAction
     */
    @VTID(7)
    void attachAction(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pDispPluginAction);

    /**
     * method GetActionDisp
     */
    @VTID(8)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getActionDisp();

    /**
     * property Name
     */
    @VTID(9)
    java.lang.String name();

    /**
     * property Name
     */
    @VTID(10)
    void name(
        java.lang.String pVal);

    /**
     * property Description
     */
    @VTID(11)
    java.lang.String description();

    /**
     * property Description
     */
    @VTID(12)
    void description(
        java.lang.String pVal);

    /**
     * property Separator
     */
    @VTID(13)
    boolean separator();

    /**
     * property Separator
     */
    @VTID(14)
    void separator(
        boolean pVal);

    /**
     * property Action
     */
    @VTID(15)
    java.lang.String action();

    /**
     * property ContextMenu
     */
    @VTID(16)
    boolean contextMenu();

    /**
     * property ContextMenu
     */
    @VTID(17)
    void contextMenu(
        boolean pVal);

    /**
     * property Class
     */
    @VTID(18)
    java.lang.String _class();

    /**
     * property Class
     */
    @VTID(19)
    void _class(
        java.lang.String pVal);

    /**
     * property Disabled
     */
    @VTID(20)
    boolean disabled();

    /**
     * property Disabled
     */
    @VTID(21)
    void disabled(
        boolean pVal);

    /**
     * property Legal
     */
    @VTID(22)
    boolean legal();

    /**
     * property Legal
     */
    @VTID(23)
    void legal(
        boolean pVal);

    /**
     * property ActionId
     */
    @VTID(24)
    java.lang.String actionId();

}
