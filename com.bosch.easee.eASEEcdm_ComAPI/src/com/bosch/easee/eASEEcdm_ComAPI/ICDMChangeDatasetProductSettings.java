package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMChangeDatasetProductSettings Interface
 */
@IID("{DBFB3B1B-E313-4BFD-88A3-D9F7E18A9B20}")
public interface ICDMChangeDatasetProductSettings extends Com4jObject {
    /**
     * method GetContext
     */
    @VTID(7)
    int getContext();

    /**
     * method GetProjectContext
     */
    @VTID(8)
    int getProjectContext();

    /**
     * method AddProductKey
     */
    @VTID(9)
    void addProductKey(
        java.lang.String iProductKey);

    /**
     * method GetProductKeys
     */
    @VTID(10)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getProductKeys();

    @VTID(10)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object getProductKeys(
        int index);

    /**
     * property ProductKeysReadOnly
     */
    @VTID(11)
    boolean productKeysReadOnly();

    /**
     * property ProductKeysReadOnly
     */
    @VTID(12)
    void productKeysReadOnly(
        boolean pVal);

    /**
     * property InheritPermissions
     */
    @VTID(13)
    boolean inheritPermissions();

    /**
     * property InheritPermissions
     */
    @VTID(14)
    void inheritPermissions(
        boolean pVal);

    /**
     * property RemoveExternalWorksplitParsets
     */
    @VTID(15)
    boolean removeExternalWorksplitParsets();

    /**
     * property RemoveExternalWorksplitParsets
     */
    @VTID(16)
    void removeExternalWorksplitParsets(
        boolean pVal);

    /**
     * property RemoveCalibrationDataParsets
     */
    @VTID(17)
    boolean removeCalibrationDataParsets();

    /**
     * property RemoveCalibrationDataParsets
     */
    @VTID(18)
    void removeCalibrationDataParsets(
        boolean pVal);

    /**
     * property RemoveEndOfLineParsets
     */
    @VTID(19)
    boolean removeEndOfLineParsets();

    /**
     * property RemoveEndOfLineParsets
     */
    @VTID(20)
    void removeEndOfLineParsets(
        boolean pVal);

    /**
     * property CouldInheritPermissions
     */
    @VTID(21)
    boolean couldInheritPermissions();

    /**
     * property CouldRemoveCalibrationDataParsets
     */
    @VTID(22)
    boolean couldRemoveCalibrationDataParsets();

    /**
     * property CouldRemoveExternalWorksplit
     */
    @VTID(23)
    boolean couldRemoveExternalWorksplit();

    /**
     * property CouldRemoveEndOfLineData
     */
    @VTID(24)
    boolean couldRemoveEndOfLineData();

    /**
     * property NewDatasetName
     */
    @VTID(25)
    java.lang.String newDatasetName();

    /**
     * property NewDatasetName
     */
    @VTID(26)
    void newDatasetName(
        java.lang.String pVal);

    /**
     * property Comment
     */
    @VTID(27)
    java.lang.String comment();

    /**
     * property Comment
     */
    @VTID(28)
    void comment(
        java.lang.String pVal);

}
