package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * _IDomainEvents Interface
 */
@IID("{E2DCAE4F-7C46-40B0-8B5E-844B69DD0A64}")
public interface _IDomainEvents extends Com4jObject {
    /**
     * method OnDomainChanging
     */
    @VTID(7)
    void onDomainChanging(
        java.lang.String sDomainName,
        Holder<Boolean> change);

    /**
     * method OnDomainChanged
     */
    @VTID(8)
    void onDomainChanged(
        java.lang.String sDomainName);

    /**
     * method OnDomainLeave
     */
    @VTID(9)
    void onDomainLeave(
        java.lang.String sDomainName,
        Holder<Boolean> leave);

}
