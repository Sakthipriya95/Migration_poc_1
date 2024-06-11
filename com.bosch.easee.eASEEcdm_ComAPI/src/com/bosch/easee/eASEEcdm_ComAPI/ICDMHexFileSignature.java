package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMHexFileSignature Interface
 */
@IID("{01EF99B6-FE24-494E-8691-22957021C35C}")
public interface ICDMHexFileSignature extends Com4jObject {
    /**
     * property SignatureEnable
     */
    @VTID(7)
    void signatureEnable(
        boolean val);

    /**
     * property SignatureEnable
     */
    @VTID(8)
    boolean signatureEnable();

    /**
     * property SignaturValueLength
     */
    @VTID(9)
    void signaturValueLength(
        int val);

    /**
     * property SignaturValueLength
     */
    @VTID(10)
    int signaturValueLength();

    /**
     * property SignaturValue
     */
    @VTID(11)
    void signaturValue(
        java.lang.String val);

    /**
     * property SignaturValue
     */
    @VTID(12)
    java.lang.String signaturValue();

    /**
     * property SignaturHEXADDRESS
     */
    @VTID(13)
    void signaturHEXADDRESS(
        java.lang.String val);

    /**
     * property SignaturHEXADDRESS
     */
    @VTID(14)
    java.lang.String signaturHEXADDRESS();

    /**
     * property SignatureValueSource
     */
    @VTID(15)
    void signatureValueSource(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmSignatureValueSource val);

    /**
     * property SignatureValueSource
     */
    @VTID(16)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmSignatureValueSource signatureValueSource();

    /**
     * property SignatureMode
     */
    @VTID(17)
    void signatureMode(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmSignatureMode val);

    /**
     * property SignatureMode
     */
    @VTID(18)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmSignatureMode signatureMode();

    /**
     * property SignatureParameter
     */
    @VTID(19)
    void signatureParameter(
        java.lang.String val);

    /**
     * property SignatureParameter
     */
    @VTID(20)
    java.lang.String signatureParameter();

    /**
     * property SignatureAttributeClass
     */
    @VTID(21)
    void signatureAttributeClass(
        java.lang.String val);

    /**
     * property SignatureAttributeClass
     */
    @VTID(22)
    java.lang.String signatureAttributeClass();

    /**
     * property SignatureAttribute
     */
    @VTID(23)
    void signatureAttribute(
        java.lang.String val);

    /**
     * property SignatureAttribute
     */
    @VTID(24)
    java.lang.String signatureAttribute();

    /**
     * property SignatureFillbyte
     */
    @VTID(25)
    void signatureFillbyte(
        short val);

    /**
     * property SignatureFillbyte
     */
    @VTID(26)
    short signatureFillbyte();

    /**
     * property SignatureUseFillByte
     */
    @VTID(27)
    void signatureUseFillByte(
        boolean val);

    /**
     * property SignatureUseFillByte
     */
    @VTID(28)
    boolean signatureUseFillByte();

}
