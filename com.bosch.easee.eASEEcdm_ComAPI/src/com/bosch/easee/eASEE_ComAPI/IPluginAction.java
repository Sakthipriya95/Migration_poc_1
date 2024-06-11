package com.bosch.easee.eASEE_ComAPI;

import com4j.*;


/**
 * IPluginAction Interface
 */
@IID("{655ED58C-1445-45E7-9DD7-2EF3FEF15800}")
public interface IPluginAction extends Com4jObject {
    /**
     * method AttachFunction
     */
    @VTID(7)
    void attachFunction(
        @MarshalAs(NativeType.Dispatch)
    com4j.Com4jObject pDispPlugin, java.lang.String function);

    /**
     * method AttachBitmap
     */
    @VTID(9)
    void attachBitmap(int hBitmap);

    /**
     * method GetFunctionInfo
     */
    @VTID(10)
    void getFunctionInfo(Holder<java.lang.String> pName,
        Holder<Integer> pDispID, Holder<Integer> pDispIDLegal,
        java.lang.Object pParameter, Holder<Boolean> pExtendedInterface);

    /**
     * method GetBitmapInfo
     */
    @VTID(11)
    int getBitmapInfo();

    /**
     * property Name
     */
    @VTID(12)
    java.lang.String name();

    /**
     * property Name
     */
    @VTID(13)
    void name(java.lang.String pVal);

    /**
     * property Description
     */
    @VTID(14)
    java.lang.String description();

    /**
     * property Description
     */
    @VTID(15)
    void description(java.lang.String pVal);

    /**
     * property Shortcut
     */
    @VTID(16)
    java.lang.String shortcut();

    /**
     * property Function
     */
    @VTID(17)
    java.lang.String function();

    /**
     * property Parameter
     */
    @VTID(18)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object parameter();

    /**
     * property Parameter
     */
    @VTID(19)
    void parameter(@MarshalAs(NativeType.VARIANT)
    java.lang.Object pVal);

    /**
     * property HelpID
     */
    @VTID(20)
    java.lang.String helpID();

    /**
     * property HelpID
     */
    @VTID(21)
    void helpID(java.lang.String pVal);

    /**
     * property ID
     */
    @VTID(22)
    int _ID();

    /**
     * property ID
     */
    @VTID(23)
    void _ID(int pVal);

    /**
     * property Disabled
     */
    @VTID(24)
    boolean disabled();

    /**
     * property Disabled
     */
    @VTID(25)
    void disabled(boolean pVal);

    /**
     * property NoDisable
     */
    @VTID(26)
    boolean noDisable();

    /**
     * property NoDisable
     */
    @VTID(27)
    void noDisable(boolean pVal);

    /**
     * property Legal
     */
    @VTID(28)
    boolean legal();

    /**
     * property Legal
     */
    @VTID(29)
    void legal(boolean pVal);

    /**
     * property Checkbox
     */
    @VTID(30)
    boolean checkBox();

    /**
     * property Checkbox
     */
    @VTID(31)
    void checkBox(boolean pVal);

    /**
     * property Checked
     */
    @VTID(32)
    boolean checked();

    /**
     * property Checked
     */
    @VTID(33)
    void checked(boolean pVal);

    /**
     * property RadioButton
     */
    @VTID(34)
    boolean radioButton();

    /**
     * property RadioButton
     */
    @VTID(35)
    void radioButton(boolean pVal);

    /**
     * property RadioButton
     */
    @VTID(36)
    boolean firstRadioButton();

    /**
     * property RadioButton
     */
    @VTID(37)
    void firstRadioButton(boolean pVal);

    /**
     * property Class
     */
    @VTID(38)
    java.lang.String _class();

    /**
     * property Class
     */
    @VTID(39)
    void _class(java.lang.String pVal);

    /**
     * method AttachIcon
     */
    @VTID(40)
    void attachIcon(int hIconSmall, int hIconLarge);

    /**
     * method GetIconInfo
     */
    @VTID(41)
    void getIconInfo(Holder<Integer> phIconSmall, Holder<Integer> phIconLarge);

    /**
     * property Parent
     */
    @VTID(42)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject parent();

    /**
     * property Name
     */
    @VTID(43)
    java.lang.String id();

    /**
     * property Name
     */
    @VTID(44)
    void id(java.lang.String pVal);
}
