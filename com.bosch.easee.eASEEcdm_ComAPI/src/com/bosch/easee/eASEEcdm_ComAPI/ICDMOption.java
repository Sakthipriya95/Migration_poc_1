package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMOption Interface
 */
@IID("{2696602E-0145-4A10-B89C-5682483C195E}")
public interface ICDMOption extends Com4jObject {
    /**
     * property LockState
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionLockState lockState();

    /**
     * property LockState
     */
    @VTID(8)
    void lockState(
        com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionLockState pVal);

    /**
     * property OptionValue
     */
    @VTID(9)
    java.lang.String optionValue();

    /**
     * property OptionValue
     */
    @VTID(10)
    void optionValue(
        java.lang.String pVal);

    /**
     * property Visible
     */
    @VTID(11)
    boolean visible();

    /**
     * property Visible
     */
    @VTID(12)
    void visible(
        boolean pVal);

    /**
     * property OptionSourceLevel
     */
    @VTID(13)
    com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionLevel optionSourceLevel();

    /**
     * property OptionSourceLockState
     */
    @VTID(14)
    com.bosch.easee.eASEEcdm_ComAPI.ECDMOptionLockState optionSourceLockState();

    /**
     * property OptionSourceValue
     */
    @VTID(15)
    java.lang.String optionSourceValue();

    /**
     * property GenerateWarningError
     */
    @VTID(16)
    boolean generateWarningError();

    /**
     * property GenerateWarningError
     */
    @VTID(17)
    void generateWarningError(
        boolean pVal);

}
