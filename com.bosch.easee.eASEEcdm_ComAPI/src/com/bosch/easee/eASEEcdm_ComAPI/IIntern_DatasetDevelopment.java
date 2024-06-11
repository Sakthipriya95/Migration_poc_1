package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.Com4jObject;
import com4j.IID;
import com4j.VTID;

@IID("{4A12DAB8-D4C9-4008-8C4F-141AA95AE578}")
public interface IIntern_DatasetDevelopment extends Com4jObject {

  @VTID(7)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetResult createDataset(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetSettings settings);

  @VTID(8)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetResult createDerivedDataset(int datasetVersion,
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection productKeys, boolean iNoWorkPackageDefinition);

  @VTID(9)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionVariantResult branchVariant(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMChangeDatasetProductSettings settings);

  @VTID(10)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionVariantResult createRevision(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection datasets,
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionSettings settings);

  @VTID(11)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMChangeDatasetProductSettings getGreateProductVariantSettings(int dstVersion);

  @VTID(12)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionSettings getGreateRevisionSettings(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol dsts);

  @VTID(13)
  void rename(int versionId, java.lang.String newName);

  @VTID(14)
  void updatePvd(com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection collectionDatasets,
      com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition pvd);

  @VTID(15)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeSettings getSoftwareChangeSettings(int projectVersionId);

  @VTID(16)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeResult softwareChange(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol dsts,
      com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeSettings softwareChangeSettings,
      boolean iSoftwareChangeContinueOnWarnings);

  @VTID(17)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetSettings getCreateDatasetSettings(int projectVersionId);
}
