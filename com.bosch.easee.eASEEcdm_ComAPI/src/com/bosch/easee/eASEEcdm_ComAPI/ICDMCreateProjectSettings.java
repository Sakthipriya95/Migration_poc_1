package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCreateProjectSettings Interface
 */
@IID("{97EF5E8F-391B-4F3E-A4FB-73CB138295BA}")
public interface ICDMCreateProjectSettings extends Com4jObject {
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
     * property VersionDescription
     */
    @VTID(9)
    java.lang.String versionDescription();

    /**
     * property VersionDescription
     */
    @VTID(10)
    void versionDescription(
        java.lang.String pVal);

    /**
     * property AddToFavorites
     */
    @VTID(11)
    boolean addToFavorites();

    /**
     * property AddToFavorites
     */
    @VTID(12)
    void addToFavorites(
        boolean pVal);

    /**
     * property WithoutHex
     */
    @VTID(13)
    boolean withoutHex();

    /**
     * property WithoutHex
     */
    @VTID(14)
    void withoutHex(
        boolean pVal);

    /**
     * property WithoutHex
     */
    @VTID(15)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmProjectCreationMode projectCreationMode();

    /**
     * property WithoutHex
     */
    @VTID(16)
    void projectCreationMode(
        com.bosch.easee.eASEEcdm_ComAPI.ECdmProjectCreationMode pVal);

}
