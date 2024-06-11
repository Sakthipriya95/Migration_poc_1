package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IPluginActions Interface
 */
@IID("{69A2CA16-0F90-4528-BFDF-DE41D7022004}")
public interface IPluginActions extends Com4jObject,Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(8)
    @DefaultMethod
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object item(
        int index);

    @VTID(9)
    int count();

    /**
     * method CreateAction
     */
    @VTID(10)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject createAction();

    /**
     * method Add
     */
    @VTID(11)
    void add(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pDispPluginAction);

    /**
     * method RemoveAll
     */
    @VTID(12)
    void removeAll();

    /**
     * method _GetActionOfID
     */
    @VTID(13)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject _GetActionOfID(
        int id);

    /**
     * method CreateActions
     */
    @VTID(14)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject createActions();

    /**
     * property RadioButtons
     */
    @VTID(15)
    boolean radioButtons();

    /**
     * property RadioButtons
     */
    @VTID(16)
    void radioButtons(
        boolean pVal);

    /**
     * property Name
     */
    @VTID(17)
    java.lang.String name();

    /**
     * property Name
     */
    @VTID(18)
    void name(
        java.lang.String pVal);

    /**
     * method ExecuteDefaultAction
     */
    @VTID(19)
    void executeDefaultAction();

}
