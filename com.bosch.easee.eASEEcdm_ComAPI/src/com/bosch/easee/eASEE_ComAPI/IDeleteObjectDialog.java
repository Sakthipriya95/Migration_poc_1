package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IDeleteObjectDialog Interface
 */
@IID("{26301ADD-9DAD-4473-AB05-28D119F4E5C0}")
public interface IDeleteObjectDialog extends Com4jObject {
    /**
     * Add a version to dialog
     */
    @VTID(7)
    void addVersion(
        int versionNumber);

    /**
     * Add a version to dialog and the usage-context
     */
    @VTID(8)
    void addVersionAndUsageContext(
        int versionNumber,
        java.lang.String contextIds);

    /**
     * property DeletedVersionIds
     */
    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object deletedVersionIds();

}
