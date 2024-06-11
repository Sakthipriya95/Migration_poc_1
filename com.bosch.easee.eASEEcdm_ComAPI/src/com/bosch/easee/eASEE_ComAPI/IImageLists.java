package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IImageLists Interface
 */
@IID("{4DE45490-E14D-4AB1-8073-D0AFB04A3EF6}")
public interface IImageLists extends Com4jObject {
    /**
     * method GetImageList
     */
    @VTID(7)
    int getImageList();

    /**
     * method GetIconIndicesByVersionObj2
     */
    @VTID(8)
    void getIconIndicesByVersionObj2(
        com.bosch.easee.eASEE_ComAPI.eASEEViews view,
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersionObject,
        Holder<Integer> pTypeIndex,
        Holder<Integer> pStateIndex,
        Holder<Integer> pTypeOverlayIndex,
        Holder<Integer> pStateOverlayIndex,
        Holder<Integer> pForeignDomainIndex,
        Holder<Boolean> pHasAdditionalOverlays);

    /**
     * method GetIconIndicesByVersionNumber2
     */
    @VTID(9)
    void getIconIndicesByVersionNumber2(
        com.bosch.easee.eASEE_ComAPI.eASEEViews view,
        int lVersionNumber,
        Holder<Integer> pTypeIndex,
        Holder<Integer> pStateIndex,
        Holder<Integer> pTypeOverlayIndex,
        Holder<Integer> pStateOverlayIndex,
        Holder<Integer> pForeignDomainIndex,
        Holder<Boolean> pHasAdditionalOverlays);

    /**
     * method GetTypeIconIndexByName
     */
    @VTID(10)
    int getTypeIconIndexByName(
        java.lang.String sTypeName);

    /**
     * method GetTypeIconIndexByGUID
     */
    @VTID(11)
    int getTypeIconIndexByGUID(
        java.lang.String sTypeID);

    /**
     * method GetTypeIconHandleByName
     */
    @VTID(12)
    int getTypeIconHandleByName(
        java.lang.String sTypeName);

    /**
     * method GetTypeIconHandleByGUID
     */
    @VTID(13)
    int getTypeIconHandleByGUID(
        java.lang.String sTypeID);

    /**
     * method GetTransparency
     */
    @VTID(14)
    int getTransparency();

    /**
     * method GetAssembledImageList
     */
    @VTID(15)
    int getAssembledImageList();

    /**
     * method GetAssembledIconIndexByVersionObj
     */
    @VTID(16)
    int getAssembledIconIndexByVersionObj(
        com.bosch.easee.eASEE_ComAPI.eASEEViews view,
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersionObject);

    /**
     * method GetAssembledIconIndexByVersionNumber
     */
    @VTID(17)
    int getAssembledIconIndexByVersionNumber(
        com.bosch.easee.eASEE_ComAPI.eASEEViews view,
        int lVersionNumber);

    /**
     * method GetAssembledIconHandleByVersionObj
     */
    @VTID(18)
    int getAssembledIconHandleByVersionObj(
        com.bosch.easee.eASEE_ComAPI.eASEEViews view,
        com.bosch.easee.eASEE_ComAPI.IVersionObj pVersionObject);

    /**
     * method GetAssembledIconHandleByVersionNumber
     */
    @VTID(19)
    int getAssembledIconHandleByVersionNumber(
        com.bosch.easee.eASEE_ComAPI.eASEEViews view,
        int lVersionNumber);

    /**
     * method DestroyIcon
     */
    @VTID(20)
    void destroyIcon(
        int lHandleBitmap);

}
