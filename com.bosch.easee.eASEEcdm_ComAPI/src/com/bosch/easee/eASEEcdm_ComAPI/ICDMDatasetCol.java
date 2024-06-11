package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;


/**
 * ICDMDatasetCol Interface
 */
@IID("{4C8BE3C8-12F4-4A60-9A40-1FC16C81DD13}")
public interface ICDMDatasetCol extends Com4jObject, Iterable<Com4jObject> {

  @VTID(7)
  java.util.Iterator<Com4jObject> iterator();

  @VTID(8)
  @DefaultMethod
  @ReturnValue(type = NativeType.VARIANT)
  java.lang.Object item(int index);

  @VTID(9)
  int count();

  /**
   * method Add
   */
  @VTID(10)
  void add(com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset pInterface);

  /**
   * method CheckIn
   */
  @VTID(11)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinResult checkIn(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCheckinSettings pInterface);

  /**
   * method SoftwareChange
   */
  @VTID(12)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeResult softwareChange(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMSoftwareChangeSettings iSettings, boolean iContinueOnWarnings);

  /**
   * method Export
   */
  @VTID(13)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportResult export(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetExportSettings pInterface);

  /**
   * method GetCreateRevisionSettings
   */
  @VTID(14)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionSettings getCreateRevisionSettings();

  /**
   * method CreateRevision
   */
  @VTID(15)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionVariantResult createRevision(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionSettings iSettings);

  /**
   * method AddFromNames
   */
  @VTID(16)
  void addFromNames(com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection internalNames);


  /**
   * method AddFromMetaData
   */
  @VTID(17)
  void addFromMetaData(java.lang.String iProjectName, java.lang.String iVariant, int iRevision);

  /**
   * method AddFromXmlFilter
   */
  @VTID(18)
  void addFromXmlFilter(java.lang.String iXmlFilter);

  /**
   * method AddFromVersionIds
   */
  @VTID(19)
  void addFromVersionIds(java.lang.String[] versionIds);

}
