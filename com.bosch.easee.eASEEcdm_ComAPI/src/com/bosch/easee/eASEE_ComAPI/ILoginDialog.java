package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * ILoginDialog Interface
 */
@IID("{D3909F0A-F4B8-44A4-A287-B1F70C43D545}")
public interface ILoginDialog extends Com4jObject {
    /**
     * property UserName
     */
    @VTID(7)
    java.lang.String userName();

    /**
     * property UserName
     */
    @VTID(8)
    void userName(
        java.lang.String pVal);

    /**
     * property DomainName
     */
    @VTID(9)
    java.lang.String domainName();

    /**
     * property DomainName
     */
    @VTID(10)
    void domainName(
        java.lang.String pVal);

    /**
     * property Server
     */
    @VTID(11)
    java.lang.String server();

    /**
     * property Server
     */
    @VTID(12)
    void server(
        java.lang.String pVal);

    /**
     * property SavePassword
     */
    @VTID(13)
    boolean savePassword();

    /**
     * property SavePassword
     */
    @VTID(14)
    void savePassword(
        boolean pVal);

    /**
     * property LoginOnOk
     */
    @VTID(15)
    boolean loginOnOk();

    /**
     * property LoginOnOk
     */
    @VTID(16)
    void loginOnOk(
        boolean pVal);

}
