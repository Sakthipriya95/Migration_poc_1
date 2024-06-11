package com.bosch.easee.eASEE_ComAPI;

import com4j.*;


/**
 * IPluginMenu Interface
 */
@IID("{72CFC96A-1D41-41DC-81BA-938541B989EF}")
public interface IPluginMenu extends Com4jObject, Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(8)
    @DefaultMethod
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object item(int index);

    @VTID(9)
    int count();

    /**
     * method CreateMenuItem
     */
    @VTID(10)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject createMenuItem();

    /**
     * method CreateSubMenu
     */
    @VTID(11)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject createSubMenu();

    /**
     * method Add
     */
    @VTID(12)
    void add(@MarshalAs(NativeType.Dispatch)
    com4j.Com4jObject pDispatch);

    /**
     * method RemoveAll
     */
    @VTID(13)
    void removeAll();

    /**
     * property Name
     */
    @VTID(14)
    java.lang.String name();

    /**
     * property Name
     */
    @VTID(15)
    void name(java.lang.String pVal);

    /**
     * property Disabled
     */
    @VTID(16)
    boolean disabled();

    /**
     * property Disabled
     */
    @VTID(17)
    void disabled(boolean pVal);

    /**
     * property Separator
     */
    @VTID(18)
    boolean separator();

    /**
     * property Separator
     */
    @VTID(19)
    void separator(boolean pVal);

    /**
     * method InsertBefore
     */
    @VTID(20)
    void insertBefore(
        @MarshalAs(NativeType.Dispatch)
    com4j.Com4jObject pDispatch, int nextIndex);

    /**
     * method AttachIcon
     */
    @VTID(21)
    void attachIcon(int hIcon);

    /**
     * method GetIconInfo
     */
    @VTID(22)
    int getIconInfo();

    /**
     * method RemoveAt
     */
    @VTID(23)
    void removeAt(int index);

    /**
     * method RemoveByName
     */
    @VTID(24)
    void removeByName(java.lang.String name);
}
