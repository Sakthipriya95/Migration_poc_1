package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{D7AF1893-4FA1-4F27-92A1-C54A0A6BB1C0}")
public interface IDlgPagePlugin extends Com4jObject {
    @VTID(7)
    java.lang.String name();

    @VTID(8)
    java.lang.String description();

    @VTID(9)
    java.lang.String getContextHelpID();

    @VTID(10)
    int icon();

    @VTID(11)
    int createPageWnd(
        int iParent);

    @VTID(12)
    boolean setActive(
        boolean iActivate);

    @VTID(13)
    boolean validate();

    @VTID(14)
    boolean apply();

    @VTID(15)
    void closeDialog();

    @VTID(16)
    boolean isModified();

}
