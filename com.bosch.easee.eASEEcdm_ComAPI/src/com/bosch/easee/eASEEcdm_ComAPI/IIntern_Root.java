package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

@IID("{562959AE-CB5C-4E96-B193-4115D98BF0A8}")
public interface IIntern_Root extends Com4jObject {
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset getDataset(
        java.lang.String versionId);

    @VTID(8)
    void getDatasetByProject(
        java.lang.String versionId,
        java.lang.String softwareFilter,
        java.lang.String productKeyFilter,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol iTargetCollection);

    @VTID(9)
    void getDatasets(
        java.lang.String[] versionIds,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol iTargetCollection);

    @VTID(10)
    void getDatasetsByFilter(
        java.lang.String filterXml,
        com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol iTargetCollection);

    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues getCdmProductAttributesCollection();

}
