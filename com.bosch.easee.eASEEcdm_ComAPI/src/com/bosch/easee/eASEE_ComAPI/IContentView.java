package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IContentView Interface
 */
@IID("{3476B24B-0EA9-479B-BBC0-491FBFEC9B08}")
public interface IContentView extends com.bosch.easee.eASEE_ComAPI.IWindow {
    /**
     * property Handle
     */
    @VTID(30)
    int handle();

    /**
     * property ParentFrameWnd
     */
    @VTID(31)
    void parentFrameWnd(
        int rhs);

    /**
     * method CreateControl
     */
    @VTID(32)
    com4j.Com4jObject createControl(
        java.lang.String name);

    /**
     * method AttachControl
     */
    @VTID(33)
    void attachControl(
        com4j.Com4jObject pUnkControl);

    /**
     * method GetControl
     */
    @VTID(34)
    com4j.Com4jObject getControl();

}
