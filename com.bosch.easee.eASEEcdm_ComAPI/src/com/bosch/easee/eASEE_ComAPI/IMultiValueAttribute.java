package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IMultiValueAttribute Interface
 */
@IID("{14073643-7382-465E-9928-BDA8E640BA97}")
public interface IMultiValueAttribute extends Com4jObject {
    /**
     * property Comments
     */
    @VTID(7)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object comments();

    /**
     * property Values
     */
    @VTID(8)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object values();

    /**
     * property Name
     */
    @VTID(9)
    java.lang.String name();

    /**
     * method AddValue
     */
    @VTID(10)
    void addValue(
        java.lang.String value,
        java.lang.String comment);

    /**
     * method RemoveValue
     */
    @VTID(11)
    void removeValue(
        java.lang.String value);

    /**
     * method ChangeValue
     */
    @VTID(12)
    void changeValue(
        java.lang.String oldValue,
        java.lang.String newValue,
        java.lang.String comment);

}
