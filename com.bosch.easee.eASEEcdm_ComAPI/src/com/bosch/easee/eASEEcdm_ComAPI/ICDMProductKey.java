package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMProductKey Interface
 */
@IID("{E78BE8A4-D302-469E-B3EA-50A51B29C715}")
public interface ICDMProductKey extends Com4jObject {
    /**
     * method GetLabel
     */
    @VTID(7)
    java.lang.String getLabel();

    /**
     * method SetAttributeValue
     */
    @VTID(8)
    void setAttributeValue(
        java.lang.String iProductAttribute,
        java.lang.String iProductAttributeValue);

    /**
     * method GetAttributeValues
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getAttributeValues(
        java.lang.String iProductAttribute);

    /**
     * method GetAttributes
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getAttributes();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getAttributes(
        int index);

    /**
     * method IsActive
     */
    @VTID(11)
    boolean isActive();

    /**
     * method SetAttributeValues
     */
    @VTID(12)
    void setAttributeValues(
        java.lang.String iProductAttribute,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProductAttributeValues);

}
