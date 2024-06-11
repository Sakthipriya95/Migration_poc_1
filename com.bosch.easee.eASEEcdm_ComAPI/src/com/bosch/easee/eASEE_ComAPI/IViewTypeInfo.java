package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IViewTypeInfo Interface
 */
@IID("{00DC335A-4685-4776-B8CA-C97E3011615D}")
public interface IViewTypeInfo extends Com4jObject {
    /**
     * property ViewTypeId
     */
    @VTID(7)
    com.bosch.easee.eASEE_ComAPI.ViewType viewTypeId();

    /**
     * method GetDetails
     */
    @VTID(8)
    com.bosch.easee.eASEE_ComAPI.IViewDetails getDetails();

}
