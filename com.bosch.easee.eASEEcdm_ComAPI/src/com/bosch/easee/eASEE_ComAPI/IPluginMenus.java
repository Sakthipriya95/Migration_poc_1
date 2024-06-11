package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IPluginMenus Interface
 */
@IID("{F16958C2-C6E6-4C3D-98E6-7D4319B0815A}")
public interface IPluginMenus extends Com4jObject,Iterable<Com4jObject> {
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
     * method CreateMenu
     */
    @VTID(10)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject createMenu();

    /**
     * method Add
     */
    @VTID(11)
    void add(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pDispPluginMenu);

    /**
     * method RemoveAll
     */
    @VTID(12)
    void removeAll();

    /**
     * method RemoveAt
     */
    @VTID(13)
    void removeAt(
        int index);

    /**
     * method RemoveByName
     */
    @VTID(14)
    void removeByName(
        java.lang.String name);

}
