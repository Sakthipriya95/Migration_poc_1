package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;


/**
 * ICDMDataset Interface
 */
@IID("{A01C894F-4B36-4E5B-9832-042A1AF7E53B}")
public interface ICDMDataset extends Com4jObject {

  /**
   * method GetVersionObj
   */
  @VTID(7)
  @ReturnValue(type = NativeType.Dispatch)
  com4j.Com4jObject getVersionObj();

  /**
   * method GetObjectFileVersion
   */
  @VTID(8)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMObjectFileVersion getObjectFileVersion();

  /**
   * method GetDescriptionFileVersion
   */
  @VTID(9)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMDescriptionFileVersion getDescriptionFileVersion();

  /**
   * method GetViewDefinition
   */
  @VTID(10)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition getViewDefinition();

  /**
   * method GetPartitionDatasets
   */
  @VTID(11)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol getPartitionDatasets();

  @VTID(11)
  @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = {
      com.bosch.easee.eASEEcdm_ComAPI.ICDMDatasetCol.class })
  java.lang.Object getPartitionDatasets(int index);

  /**
   * method GetParametersets
   */
  @VTID(12)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParametersets(int iConfigState);

  /**
   * method UpdateDatasetByAddress
   */
  @VTID(13)
  void updateDatasetByAddress(java.lang.String iAddress, byte[] iValue);

  /**
   * method GetType
   */
  @VTID(14)
  com.bosch.easee.eASEEcdm_ComAPI.EDatasetType getType();

  /**
   * method Refresh
   */
  @VTID(15)
  void refresh();

  /**
   * method GetParameterHistory
   */
  @VTID(16)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameterHistory();

  @VTID(16)
  @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = {
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class })
  java.lang.Object getParameterHistory(int index);

  /**
   * method GetParameterHistoryFiltered
   */
  @VTID(17)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameterHistoryFiltered(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameterFilter,
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iFunctionFilter);

  /**
   * method GetChangeDatasetProductSettings
   */
  @VTID(18)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMChangeDatasetProductSettings getChangeDatasetProductSettings();

  /**
   * property ProductKey
   */
  @VTID(19)
  java.lang.String productKey();

  /**
   * property ProgramKey
   */
  @VTID(20)
  java.lang.String programKey();

  /**
   * method CreateProductVariant
   */
  @VTID(21)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetRevisionVariantResult createProductVariant(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMChangeDatasetProductSettings iSettings);

  /**
   * method GetWorkpackageDefinition
   */
  @VTID(22)
  com.bosch.easee.eASEEcdm_ComAPI.EWorkPackageDefinition getWorkpackageDefinition();

  /**
   * method GetProject
   */
  @VTID(23)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject getProject();

  /**
   * method CreateParameter
   */
  @VTID(24)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMParameter createParameter();

  /**
   * method SaveParameterValues
   */
  @VTID(25)
  void saveParameterValues(com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameterValues);

  /**
   * method SaveParameterValuesEx
   */
  @VTID(26)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol saveParameterValuesEx(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameterValues);

  /**
   * method GetParameterDefinitions
   */
  @VTID(27)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameterDefinitions();

  @VTID(27)
  @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = {
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class })
  java.lang.Object getParameterDefinitions(int index);

  /**
   * method GetFunctionDefinitions
   */
  @VTID(28)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getFunctionDefinitions();

  @VTID(28)
  @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = {
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class })
  java.lang.Object getFunctionDefinitions(int index);

  /**
   * method GetPartition
   */
  @VTID(29)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getPartition();

  @VTID(29)
  @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = {
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class })
  java.lang.Object getPartition(int index);

  /**
   * method CreateDerivedVariants
   */
  @VTID(30)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateDatasetResult createDerivedVariants(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iProductKeys, boolean iNoWorkPackageDefinition,
      boolean iContinueOnWarnings);

  /**
   * method GetParameterDefinitionsFiltered
   */
  @VTID(31)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getParameterDefinitionsFiltered(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameterNames);

  /**
   * method UpdateBaseDataset
   */
  @VTID(32)
  void updateBaseDataset();

  /**
   * method GetFolderPath
   */
  @VTID(33)
  java.lang.String getFolderPath();

  /**
   * method Rename
   */
  @VTID(34)
  void rename(java.lang.String newDatasetName);

  /**
   * method GetA2LWithViewsAndRights
   */
  @VTID(35)
  java.lang.String getA2LWithViewsAndRights(java.lang.String a2l);

  /**
   * property Id
   */
  @VTID(36)
  int id();

  /**
   * method GetValues
   */
  @VTID(37)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getValues(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMQueryValuesSetting iQuerySettings);

  /**
   * method GetQueryValuesSettings
   */
  @VTID(38)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMQueryValuesSetting getQueryValuesSettings();

  /**
   * method GetSoftwareContainer
   */
  @VTID(39)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramSet getSoftwareContainer();

  /**
   * method GetBaseDataset
   */
  @VTID(40)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset getBaseDataset();
}
