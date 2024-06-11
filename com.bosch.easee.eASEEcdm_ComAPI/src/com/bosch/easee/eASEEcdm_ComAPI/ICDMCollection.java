package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCollection Interface
 */
@IID("{7B574393-F8C3-4FCD-AF55-4FDAAFB5DA5B}")
public interface ICDMCollection extends Com4jObject,Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(8)
    @DefaultMethod
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object item(
        int index);

    @VTID(9)
    int count();

    /**
     * method Add
     */
    @VTID(10)
    int add(
        @MarshalAs(NativeType.VARIANT) java.lang.Object iItem);

    /**
     * method AddStringPair
     */
    @VTID(11)
    int addStringPair(
        java.lang.String iFirst,
        java.lang.String iSecond);

    /**
     * method Clear
     */
    @VTID(12)
    void clear();

    /**
     * method AddVariantPair
     */
    @VTID(13)
    int addVariantPair(
        @MarshalAs(NativeType.VARIANT) java.lang.Object iFirst,
        @MarshalAs(NativeType.VARIANT) java.lang.Object iSecond);

}
