package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IGCView Interface
 */
@IID("{42515685-2931-4079-8AB8-613EC84FD037}")
public interface IGCView extends com.bosch.easee.eASEE_ComAPI.IWindow {
    /**
     * method GetGTView
     */
    @VTID(30)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getGTView(
        int number);

    /**
     * method CreateContentView
     */
    @VTID(31)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject createContentView(
        int number);

    /**
     * method GetContentView
     */
    @VTID(32)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject getContentView(
        int number);

}
