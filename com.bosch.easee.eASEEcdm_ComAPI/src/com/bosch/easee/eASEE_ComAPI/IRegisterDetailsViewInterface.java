package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IRegisterDetailsViewInterface Interface
 */
@IID("{E5C3F0FF-32A5-4D3D-BCBC-43EBCAC9AE42}")
public interface IRegisterDetailsViewInterface extends Com4jObject {
    /**
     * method Register
     */
    @VTID(7)
    void register(
        com.bosch.easee.eASEE_ComAPI.IDetailsViewFactory pIDetailsViewFactory);

    /**
     * method Unregister
     */
    @VTID(8)
    void unregister(
        com.bosch.easee.eASEE_ComAPI.IDetailsViewFactory pIDetailsViewFactory);

}
