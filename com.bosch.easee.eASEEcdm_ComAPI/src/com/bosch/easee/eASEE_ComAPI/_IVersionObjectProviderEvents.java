package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * _IVersionObjectProviderEvents Interface
 */
@IID("{6D9203E7-4B43-4A09-A02C-04DE08BBFDEA}")
public interface _IVersionObjectProviderEvents extends Com4jObject {
    /**
     * method OnGetActiveObject
     */
    @VTID(7)
    void onGetActiveObject(
        Holder<com.bosch.easee.eASEE_ComAPI.IVersionObj> ppVersion);

    /**
     * method OnGetSelectedObjects
     */
    @VTID(8)
    void onGetSelectedObjects(
        Holder<com.bosch.easee.eASEE_ComAPI.IVersionCollection> ppVersionCollection);

    /**
     * method OnGetActiveObjectId
     */
    @VTID(9)
    int onGetActiveObjectId();

    /**
     * method OnGetSelectedObjectIds
     */
    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object onGetSelectedObjectIds();

}
