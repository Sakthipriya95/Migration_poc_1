package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDeliverySource Interface
 */
@IID("{2B4E4D2E-A384-4264-9B90-F547E43601C2}")
public interface ICDMDeliverySourceFile extends Com4jObject {
    /**
     * property FileName
     */
    @VTID(7)
    void fileName(
        java.lang.String fileName);

    /**
     * property FileName
     */
    @VTID(8)
    java.lang.String fileName();

    /**
     * property ClassName
     */
    @VTID(9)
    void className(
        java.lang.String className);

    /**
     * property ClassName
     */
    @VTID(10)
    java.lang.String className();

    /**
     * property Class
     */
    @VTID(11)
    void _class(
        com.bosch.easee.eASEEcdm_ComAPI.EDeliverTargetClass classType);

    /**
     * property Class
     */
    @VTID(12)
    com.bosch.easee.eASEEcdm_ComAPI.EDeliverTargetClass _class();

    /**
     * property CreationMode
     */
    @VTID(13)
    void creationMode(
        com.bosch.easee.eASEEcdm_ComAPI.EDeliverCreationMode mode);

    /**
     * property CreationMode
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.EDeliverCreationMode creationMode();

    /**
     * property ElementName
     */
    @VTID(15)
    void elementName(
        java.lang.String elmentName);

    /**
     * property ElementName
     */
    @VTID(16)
    java.lang.String elementName();

    /**
     * property VariantName
     */
    @VTID(17)
    void variantName(
        java.lang.String variantName);

    /**
     * property VariantName
     */
    @VTID(18)
    java.lang.String variantName();

}
