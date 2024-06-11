package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCalibrationProjectSettings Interface
 */
@IID("{50809E53-2712-4C22-9A93-41744AD8EE77}")
public interface ICDMCalibrationProjectSettings extends Com4jObject {
    /**
     * method GetProgramKeys
     */
    @VTID(7)
    void getProgramKeys(
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection> oProgramKeysAll,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection> oProgramKeysInUse);

    /**
     * method CreateProgramKey
     */
    @VTID(8)
    void createProgramKey(
        java.lang.String iProgramKey);

    /**
     * method GetProductAttributes
     */
    @VTID(9)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getProductAttributes();

    @VTID(9)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getProductAttributes(
        int index);

    /**
     * method ActivateProductAttribute
     */
    @VTID(10)
    void activateProductAttribute(
        java.lang.String iProductAttribute,
        java.lang.String iProductAttributeValue);

    /**
     * method DeactivateProductAttribute
     */
    @VTID(11)
    void deactivateProductAttribute(
        java.lang.String iProductAttribute);

    /**
     * method DeactivateProductAttributeValue
     */
    @VTID(12)
    void deactivateProductAttributeValue(
        java.lang.String iProductAttribute,
        java.lang.String iProductAttributeValue);

    /**
     * method GetProductKey
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMProductKey getProductKey(
        java.lang.String iProductKeyLabel);

    /**
     * method SaveProductKey
     */
    @VTID(14)
    void saveProductKey(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMProductKey iProductKey);

    /**
     * method GetProductAttributeValues
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getProductAttributeValues(
        java.lang.String iAttribute);

    /**
     * method GetOption
     */
    @VTID(16)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMOption getOption(
        com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionCategoryId iOptionCategoryId,
        int iOptionId);

    /**
     * method SaveOptions
     */
    @VTID(17)
    void saveOptions();

    /**
     * method GetOptionCache
     */
    @VTID(18)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object getOptionCache();

    /**
     * method ReleaseObjects
     */
    @VTID(19)
    void releaseObjects();

    /**
     * method CreateProgramKeys
     */
    @VTID(20)
    void createProgramKeys(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProgramKey);

    /**
     * method ActivateProductAttributes
     */
    @VTID(21)
    void activateProductAttributes(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues iProductAttributesAndValues);

    /**
     * method SaveProductKeys
     */
    @VTID(22)
    void saveProductKeys(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProductKeys);

    /**
     * method DeactivateProductAttributes
     */
    @VTID(23)
    void deactivateProductAttributes(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues iProductAttributesAndValues);

    /**
     * method ActivateProductKeys
     */
    @VTID(24)
    void activateProductKeys(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProductKeyNames);

    /**
     * method DeactivateProductKeys
     */
    @VTID(25)
    void deactivateProductKeys(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProductKeyNames);

    /**
     * method ActivateProgramKeys
     */
    @VTID(26)
    void activateProgramKeys(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProgramKeyNames);

    /**
     * method DeactivateProgramKeys
     */
    @VTID(27)
    void deactivateProgramKeys(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProgramKeyNames);

    /**
     * method DeleteProductKeys
     */
    @VTID(28)
    void deleteProductKeys(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProductKeyNames);

    /**
     * property UniqueProductKey
     */
    @VTID(29)
    boolean uniqueProductKeys();

    /**
     * property UniqueProductKey
     */
    @VTID(30)
    void uniqueProductKeys(
        boolean pVal);

    /**
     * method GetProgramKey
     */
    @VTID(31)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramKey getProgramKey(
        java.lang.String iLabel);

    /**
     * method SaveProgramKeys
     */
    @VTID(32)
    void saveProgramKeys(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iKeys);

    /**
     * method DeleteProgramKeys
     */
    @VTID(33)
    void deleteProgramKeys(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProgramKeyNames);

    /**
     * method GetProductKeys
     */
    @VTID(34)
    void getProductKeys(
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection> oKeysActive,
        Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection> oKeysInactive);

}
