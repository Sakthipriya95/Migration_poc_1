package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCreateViewDefinitionSettings Interface
 */
@IID("{BB68DB26-4C48-4F14-9C83-C2F28EC2C0D8}")
public interface ICDMCreateViewDefinitionSettings extends Com4jObject {
    /**
     * property Elementname
     */
    @VTID(7)
    java.lang.String elementName();

    /**
     * property Elementname
     */
    @VTID(8)
    void elementName(
        java.lang.String pVal);

    /**
     * property VariantName
     */
    @VTID(9)
    java.lang.String variantName();

    /**
     * property VariantName
     */
    @VTID(10)
    void variantName(
        java.lang.String pVal);

    /**
     * property VersionDescription
     */
    @VTID(11)
    java.lang.String versionDescription();

    /**
     * property VersionDescription
     */
    @VTID(12)
    void versionDescription(
        java.lang.String pVal);

    /**
     * property Mode
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ECreateViewDefinitionMode createMode();

    /**
     * property Mode
     */
    @VTID(14)
    void createMode(
        com.bosch.easee.eASEEcdm_ComAPI.ECreateViewDefinitionMode pVal);

    /**
     * property Root
     */
    @VTID(15)
    java.lang.String root();

    /**
     * property Root
     */
    @VTID(16)
    void root(
        java.lang.String pVal);

    /**
     * property DescriptionFile
     */
    @VTID(17)
    java.lang.String descriptionFilePath();

    /**
     * property DescriptionFile
     */
    @VTID(18)
    void descriptionFilePath(
        java.lang.String pVal);

    /**
     * method AddAddressRange
     */
    @VTID(19)
    void addAddressRange(
        java.lang.String addressFrom,
        java.lang.String addressTo);

    /**
     * method GetAddressRanges
     */
    @VTID(20)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getAddressRanges();

    @VTID(20)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getAddressRanges(
        int index);

}
