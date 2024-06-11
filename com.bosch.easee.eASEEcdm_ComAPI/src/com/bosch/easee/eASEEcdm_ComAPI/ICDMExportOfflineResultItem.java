package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMExportOfflineResultItem Interface
 */
@IID("{B7C1AAFC-0C91-42B9-8D8F-585F06BADAFA}")
public interface ICDMExportOfflineResultItem extends Com4jObject {
    /**
     * property DataSet
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset();

    /**
     * property DescriptionFile
     */
    @VTID(8)
    java.lang.String descriptionFile();

    /**
     * property ObjectFile
     */
    @VTID(9)
    java.lang.String objectFile();

    /**
     * property ParameterSetFile
     */
    @VTID(10)
    java.lang.String parameterSetFile();

    /**
     * property AdditionalFiles
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection additionalFiles();

    @VTID(11)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object additionalFiles(
        int index);

    /**
     * property Result
     */
    @VTID(12)
    boolean result();

    /**
     * property ViewExported
     */
    @VTID(13)
    boolean viewExported();

    /**
     * property RightsExported
     */
    @VTID(14)
    boolean rightsExported();

}
