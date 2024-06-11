package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCreateProgramsetSettings Interface
 */
@IID("{44A85A63-8AC5-4672-B21D-C8A60D8288E2}")
public interface ICDMCreateProgramsetSettings extends Com4jObject {
    /**
     * property DescriptionFilePath
     */
    @VTID(7)
    java.lang.String descriptionFilePath();

    /**
     * property DescriptionFilePath
     */
    @VTID(8)
    void descriptionFilePath(
        java.lang.String pVal);

    /**
     * property ObjectFilePath
     */
    @VTID(9)
    java.lang.String objectFilePath();

    /**
     * property ObjectFilePath
     */
    @VTID(10)
    void objectFilePath(
        java.lang.String pVal);

    /**
     * property Elementname
     */
    @VTID(11)
    java.lang.String elementName();

    /**
     * property Elementname
     */
    @VTID(12)
    void elementName(
        java.lang.String pVal);

    /**
     * property VersionDescription
     */
    @VTID(13)
    java.lang.String versionDescription();

    /**
     * property VersionDescription
     */
    @VTID(14)
    void versionDescription(
        java.lang.String pVal);

    /**
     * method AddAdditionaleASEEObject
     */
    @VTID(15)
    void addAdditionaleASEEObject(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject iAdditionalObject,
        boolean iCopyToDataset);

        /**
         * property EpromCheck
         */
        @VTID(17)
        boolean epromCheck();

        /**
         * property EpromCheck
         */
        @VTID(18)
        void epromCheck(
            boolean pVal);

        /**
         * property HeuristicCheck
         */
        @VTID(19)
        boolean heuristicCheck();

        /**
         * property HeuristicCheck
         */
        @VTID(20)
        void heuristicCheck(
            boolean pVal);

        /**
         * property CheckInPST
         */
        @VTID(21)
        boolean checkInPST();

        /**
         * property CheckInPST
         */
        @VTID(22)
        void checkInPST(
            @DefaultValue("-1")boolean pVal);

        /**
         * method AddAdditionaleFile
         */
        @VTID(23)
        void addAdditionaleFile(
            java.lang.String additionalFile,
            boolean iCopyToDataset);

        }
