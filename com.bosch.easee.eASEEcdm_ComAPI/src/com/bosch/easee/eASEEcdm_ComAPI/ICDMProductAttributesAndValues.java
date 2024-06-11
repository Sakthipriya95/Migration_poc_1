package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMProductAttributesAndValues Interface
 */
@IID("{8DD1B339-FF68-4796-9B32-3991888D7CCB}")
public interface ICDMProductAttributesAndValues extends Com4jObject {
    /**
     * method AddAttributeValue
     */
    @VTID(7)
    void addAttributeValue(
        java.lang.String iProductAttriubte,
        java.lang.String iValue);

    /**
     * method AddAttributeValues
     */
    @VTID(8)
    void addAttributeValues(
        java.lang.String iProductAttriubte,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iValues);

    /**
     * method GetAttributeValues
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getAttributeValues(
        java.lang.String iProductAttriubte);

    /**
     * method GetAttributes
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getAttributes();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getAttributes(
        int index);

}
