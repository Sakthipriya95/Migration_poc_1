package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IValues Interface
 */
@IID("{40B14744-8968-429F-B333-E3864E27375A}")
public interface IValues extends Com4jObject {
    /**
     * Enable/disable a dialog element.
     */
    @VTID(7)
    void setEnabled(
        java.lang.String sDialogElem,
        boolean bEnableState);

    /**
     * Get the enable state of a dialog element.
     */
    @VTID(8)
    boolean getEnabled(
        java.lang.String sDialogElem);

    /**
     * Check/uncheck a checkbox or a radio button.
     */
    @VTID(9)
    void setValueBool(
        java.lang.String sDialogElem,
        boolean bValue);

    /**
     * Get the check state of a checkbox or radio button.
     */
    @VTID(10)
    boolean getValueBool(
        java.lang.String sDialogElem);

    /**
     * Set a dialog element to a given string.
     */
    @VTID(11)
    void setValueString(
        java.lang.String sDialogElem,
        java.lang.String sValue);

    /**
     * Get the value of a dialog element.
     */
    @VTID(12)
    java.lang.String getValueString(
        java.lang.String sDialogElem);

    /**
     * Set a dialog element to a given object.
     */
    @VTID(13)
    void setValueDispatch(
        java.lang.String sDialogElem,
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pDispObj);

}
