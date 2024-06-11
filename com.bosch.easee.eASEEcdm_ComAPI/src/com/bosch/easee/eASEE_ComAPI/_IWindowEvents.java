package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * _IWindowEvents Interface
 */
@IID("{9C3D334D-7F63-49F7-99B5-8AA7E44D5F6E}")
public interface _IWindowEvents extends Com4jObject {
    /**
     * method CanClose
     */
    @VTID(7)
    void canClose(
        Holder<Boolean> close);

    /**
     * method OnClose
     */
    @VTID(8)
    void onClose();

    /**
     * method OnActivate
     */
    @VTID(9)
    void onActivate();

    /**
     * method OnDeactivate
     */
    @VTID(10)
    void onDeactivate(
        Holder<Boolean> deactivate);

    /**
     * method OnRefreshed
     */
    @VTID(11)
    void onRefreshed();

}
