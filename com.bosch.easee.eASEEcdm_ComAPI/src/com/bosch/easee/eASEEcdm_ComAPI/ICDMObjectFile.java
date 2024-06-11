package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMObjectFile Interface
 */
@IID("{4E34819B-BEC3-492F-81AD-B65C9D3039E6}")
public interface ICDMObjectFile extends Com4jObject {
    /**
     * method Open
     */
    @VTID(7)
    void open(
        java.lang.String iObjectFilePath);

    /**
     * method SetValueAtAddress
     */
    @VTID(8)
    void setValueAtAddress(
        java.lang.String iAddress,
        byte[] iValue);

        /**
         * method CalculateChecksum
         */
        @VTID(10)
        void calculateChecksum(
            java.lang.String iConfigFilePath);

        /**
         * method Close
         */
        @VTID(11)
        void close();

        /**
         * method CreateBinary
         */
        @VTID(12)
        void createBinary(
            java.lang.String iStartAddress,
            java.lang.String iEndAddress,
            java.lang.String iTargetFile,
            byte iPadByte);

        /**
         * method MergeAddressRange
         */
        @VTID(13)
        void mergeAddressRange(
            java.lang.String iStartAddress,
            java.lang.String iEndAddress,
            java.lang.String iSourceFile,
            java.lang.String iTargetAddress,
            @MarshalAs(NativeType.VARIANT) java.lang.Object iPadByte);

        /**
         * method UnevenRowPadding
         */
        @VTID(14)
        void unevenRowPadding(
            java.lang.String iConfigFilePath,
            byte iPadByte);

        /**
         * method ValidateAgainstDescription
         */
        @VTID(15)
        com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol validateAgainstDescription(
            java.lang.String iDescriptionPath,
            boolean iPerformEpromCheck,
            boolean iPerformExtractCheck,
            Holder<Boolean> oEpromCheckSucceeded,
            Holder<Boolean> oExtractCheckSucceeded);

    }
