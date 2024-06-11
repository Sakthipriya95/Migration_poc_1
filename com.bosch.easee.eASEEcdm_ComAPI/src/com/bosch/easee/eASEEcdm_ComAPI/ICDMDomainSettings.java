package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMDomainSettings Interface
 */
@IID("{D83765FB-2B70-4F65-92F7-21A4219221FA}")
public interface ICDMDomainSettings extends Com4jObject {
    /**
     * method GetProductAttributes
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getProductAttributes();

    @VTID(7)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getProductAttributes(
        int index);

    /**
     * method GetProductAttributeNormValues
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getProductAttributeNormValues(
        java.lang.String iProductAttribute);

    /**
     * method AddProductAttributeNormValue
     */
    @VTID(9)
    void addProductAttributeNormValue(
        java.lang.String iProductAttribute,
        java.lang.String iProductAttributeNormValue);

    /**
     * method GetOption
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMOption getOption(
        com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionCategoryId iOptionCategoryId,
        int iOptionId);

    /**
     * method SaveOptions
     */
    @VTID(11)
    void saveOptions();

    /**
     * method GetOptionCache
     */
    @VTID(12)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object getOptionCache();

    /**
     * method AddProductAttributesAndNormValues
     */
    @VTID(13)
    void addProductAttributesAndNormValues(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues iProductAttributesAndNormValues);

    /**
     * method GetProductAttributesAndNormValues
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues getProductAttributesAndNormValues();

    /**
     * method DeleteProductAttributesAndNormValues
     */
    @VTID(15)
    void deleteProductAttributesAndNormValues(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues iProductAttributesAndNormValues);

}
