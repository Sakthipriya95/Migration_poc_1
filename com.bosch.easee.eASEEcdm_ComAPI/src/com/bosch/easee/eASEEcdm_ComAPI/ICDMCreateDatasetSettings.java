package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.Com4jObject;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * ICDMCreateDatasetSettings Interface
 */
@IID("{54A395C5-9B6C-456E-A0A4-AF3457790051}")
public interface ICDMCreateDatasetSettings extends Com4jObject {

  /**
   * property Source
   */
  @VTID(7)
  com.bosch.easee.eASEEcdm_ComAPI.ECreateDatasetMode source();

  /**
   * property Source
   */
  @VTID(8)
  void source(com.bosch.easee.eASEEcdm_ComAPI.ECreateDatasetMode pVal);

  /**
   * property ProgramKey
   */
  @VTID(9)
  java.lang.String programKey();

  /**
   * property ProgramKey
   */
  @VTID(10)
  void programKey(java.lang.String pVal);

  /**
   * method AddProductKey
   */
  @VTID(11)
  void addProductKey(java.lang.String iProductKey);

  /**
   * property DatasetScaleValue
   */
  @VTID(12)
  com.bosch.easee.eASEEcdm_ComAPI.EDatasetScaleValue datasetScaleValue();

  /**
   * property DatasetScaleValue
   */
  @VTID(13)
  void datasetScaleValue(com.bosch.easee.eASEEcdm_ComAPI.EDatasetScaleValue pVal);


  /**
   * property WorkPackageDefinition
   */
  @VTID(14)
  com.bosch.easee.eASEEcdm_ComAPI.EWorkPackageDefinition workPackageDefinition();

  /**
   * property WorkPackageDefinition
   */
  @VTID(15)
  void workPackageDefinition(com.bosch.easee.eASEEcdm_ComAPI.EWorkPackageDefinition pVal);

  /**
   * property DescriptionFilePath
   */
  @VTID(16)
  java.lang.String descriptionFilePath();

  /**
   * property DescriptionFilePath
   */
  @VTID(17)
  void descriptionFilePath(java.lang.String pVal);

  /**
   * property ObjectFilePath
   */
  @VTID(18)
  java.lang.String objectFilePath();

  /**
   * property ObjectFilePath
   */
  @VTID(19)
  void objectFilePath(java.lang.String pVal);

  /**
   * method SetSourceVersion
   */
  @VTID(20)
  void setSourceVersion(@MarshalAs(NativeType.Dispatch) com4j.Com4jObject iSourceVersion);

  /**
   * method SetViewDefinition
   */
  @VTID(21)
  void setViewDefinition(com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition iViewDefinition);

  /**
   * method GetSourceVersion
   */
  @VTID(22)
  @ReturnValue(type = NativeType.Dispatch)
  com4j.Com4jObject getSourceVersion();

  /**
   * method GetViewDefinition
   */
  @VTID(23)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMViewDefinition getViewDefinition();

  /**
   * method GetContext
   */
  @VTID(24)
  int getContext();

  /**
   * method GetProductKeys
   */
  @VTID(25)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getProductKeys();

  @VTID(25)
  @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = {
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class })
  java.lang.Object getProductKeys(int index);

  /**
   * property FunctionsWithoutParameters
   */
  @VTID(26)
  boolean functionsWithoutParameters();

  /**
   * property FunctionsWithoutParameters
   */
  @VTID(27)
  void functionsWithoutParameters(boolean pVal);


  /**
   * property ExtractData
   */
  @VTID(28)
  boolean extractData();

  /**
   * property ExtractData
   */
  @VTID(29)
  void extractData(boolean pVal);

  /**
   * property CreateParameterSets
   */
  @VTID(30)
  boolean createParameterSets();

  /**
   * property CreateParameterSets
   */
  @VTID(31)
  void createParameterSets(boolean pVal);

  /**
   * property DefaultDataSource
   */
  @VTID(32)
  int defaultDataSource();

  /**
   * property DefaultDataSource
   */
  @VTID(33)
  void defaultDataSource(int pVal);


  /**
   * method SetParameterRightsCheck
   */
  @VTID(34)
  void setParameterRightsCheck(@MarshalAs(NativeType.Dispatch) com4j.Com4jObject iDefinitionVersion,
      com.bosch.easee.eASEEcdm_ComAPI.EParamRightsPolicy iCheckPolicy);

  /**
   * method GetParameterRightsDef
   */
  @VTID(35)
  @ReturnValue(type = NativeType.Dispatch)
  com4j.Com4jObject getParameterRightsDef();

  /**
   * method GetParameterRightsPolicy
   */
  @VTID(36)
  com.bosch.easee.eASEEcdm_ComAPI.EParamRightsPolicy getParameterRightsPolicy();


  /**
   * property FunctionsResolveRecursive
   */
  @VTID(37)
  boolean functionsResolveRecursive();

  /**
   * property FunctionsResolveRecursive
   */
  @VTID(38)
  void functionsResolveRecursive(boolean pVal);

  /**
   * property FunctionsIncludeReferencedParameters
   */
  @VTID(39)
  boolean functionsIncludeReferencedParameters();

  /**
   * property FunctionsIncludeReferencedParameters
   */
  @VTID(40)
  void functionsIncludeReferencedParameters(boolean pVal);

  /**
   * property ManualDatasetName
   */
  @VTID(41)
  java.lang.String manualDatasetName();

  /**
   * property ManualDatasetName
   */
  @VTID(42)
  void manualDatasetName(java.lang.String pVal);

  /**
   * property FolderPath
   */
  @VTID(43)
  java.lang.String folderPath();

  /**
   * property FolderPath
   */
  @VTID(44)
  void folderPath(java.lang.String pVal);

  /**
   * property Attributes
   */
  @VTID(45)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues attributes();

  /**
   * property Attributes
   */
  @VTID(46)
  void attributes(com.bosch.easee.eASEEcdm_ComAPI.ICDMProductAttributesAndValues attributeValues);

}
