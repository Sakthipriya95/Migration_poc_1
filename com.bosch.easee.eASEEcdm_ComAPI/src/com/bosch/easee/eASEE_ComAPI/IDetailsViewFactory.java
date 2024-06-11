package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IDetailsViewFactory Interface
 */
@IID("{FBA32340-B3F0-40F6-904D-DDEF2FF4BA6E}")
public interface IDetailsViewFactory extends Com4jObject {
    /**
     * method GetDetailsViewInterface
     */
    @VTID(7)
    com.bosch.easee.eASEE_ComAPI.IDetailsView getDetailsViewInterface(
        com.bosch.easee.eASEE_ComAPI.IViewTypeInfo pViewTypeInfo,
        com.bosch.easee.eASEE_ComAPI.IContentView pIContentView);

    /**
     * method GetDetailsViewInfo
     */
    @VTID(8)
    com.bosch.easee.eASEE_ComAPI.IDetailsViewInfo getDetailsViewInfo();

}
