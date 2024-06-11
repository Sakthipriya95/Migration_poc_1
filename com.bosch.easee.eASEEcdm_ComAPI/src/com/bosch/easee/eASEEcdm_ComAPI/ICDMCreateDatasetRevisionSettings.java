package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMCreateDatasetRevisionSettings Interface
 */
@IID("{7E1A38EB-9B6A-4877-99BC-95348231F906}")
public interface ICDMCreateDatasetRevisionSettings extends Com4jObject {
    /**
     * property getDatasetContext
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol getDatasetContext();

    @VTID(7)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class})
    java.lang.Object getDatasetContext(
        int index);

    /**
     * property InheritPermissions
     */
    @VTID(8)
    boolean inheritPermissions();

    /**
     * property InheritPermissions
     */
    @VTID(9)
    void inheritPermissions(
        boolean pVal);

    /**
     * property RemoveExternalWorksplitParsets
     */
    @VTID(10)
    boolean removeExternalWorksplitParsets();

    /**
     * property RemoveExternalWorksplitParsets
     */
    @VTID(11)
    void removeExternalWorksplitParsets(
        boolean pVal);

    /**
     * property RemoveCalibrationDataParsets
     */
    @VTID(12)
    boolean removeCalibrationDataParsets();

    /**
     * property RemoveCalibrationDataParsets
     */
    @VTID(13)
    void removeCalibrationDataParsets(
        boolean pVal);

    /**
     * property RemoveEndOfLineParsets
     */
    @VTID(14)
    boolean removeEndOfLineParsets();

    /**
     * property RemoveEndOfLineParsets
     */
    @VTID(15)
    void removeEndOfLineParsets(
        boolean pVal);

    /**
     * property CouldInheritPermissions
     */
    @VTID(16)
    boolean couldInheritPermissions();

    /**
     * property CouldRemoveCalibrationDataParsets
     */
    @VTID(17)
    boolean couldRemoveCalibrationDataParsets();

    /**
     * property CouldRemoveExternalWorksplit
     */
    @VTID(18)
    boolean couldRemoveExternalWorksplit();

    /**
     * property CouldRemoveEndOfLineData
     */
    @VTID(19)
    boolean couldRemoveEndOfLineData();

    /**
     * property UpdateBaseVariants
     */
    @VTID(20)
    boolean updateBaseVariants();

    /**
     * property UpdateBaseVariants
     */
    @VTID(21)
    void updateBaseVariants(
        boolean pVal);

    /**
     * property GetNewDatasetName
     */
    @VTID(22)
    java.lang.String getNewDatasetName(
        int dataset);

    /**
     * property SetNewDatasetName
     */
    @VTID(23)
    void setNewDatasetName(
        int dataset,
        java.lang.String datasetName);

}
