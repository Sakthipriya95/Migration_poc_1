package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMProgramKey Interface
 */
@IID("{E18BE4A4-DB02-469E-A3EA-50A51B29C713}")
public interface ICDMProgramKey extends Com4jObject {
    /**
     * method GetLabel
     */
    @VTID(7)
    java.lang.String getLabel();

    /**
     * method SetLabel
     */
    @VTID(8)
    void setLabel(
        java.lang.String oLabel);

    /**
     * method GetA2lVersion
     */
    @VTID(9)
    int getA2lVersion();

    /**
     * method SetA2lVersion
     */
    @VTID(10)
    void setA2lVersion(
        int versionNo);

    /**
     * method GetHexVersion
     */
    @VTID(11)
    int getHexVersion();

    /**
     * method SetHexVersion
     */
    @VTID(12)
    void setHexVersion(
        int versionNo);

    /**
     * method GetPstVersion
     */
    @VTID(13)
    int getPstVersion();

    /**
     * method SetPstVersion
     */
    @VTID(14)
    void setPstVersion(
        int versionNo);

    /**
     * method GetActive
     */
    @VTID(15)
    boolean getActive();

    /**
     * method SetActive
     */
    @VTID(16)
    void setActive(
        boolean iFlag);

    /**
     * method GetInuse
     */
    @VTID(17)
    boolean getInuse();

    /**
     * method SetA2lVersionV
     */
    @VTID(18)
    void setA2lVersionV(
        @MarshalAs(NativeType.VARIANT) java.lang.Object versionNo);

    /**
     * method SetHexVersionV
     */
    @VTID(19)
    void setHexVersionV(
        @MarshalAs(NativeType.VARIANT) java.lang.Object versionNo);

    /**
     * method SetPstVersionV
     */
    @VTID(20)
    void setPstVersionV(
        @MarshalAs(NativeType.VARIANT) java.lang.Object versionNo);

}
