package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * ICDMViewDefinition Interface
 */
@IID("{8D2AC311-049E-4998-B099-857F267DDCC6}")
public interface ICDMViewDefinition extends Com4jObject {

  /**
   * method GetVersionObj
   */
  @VTID(7)
  @ReturnValue(type = NativeType.Dispatch)
  com4j.Com4jObject getVersionObj();

  /**
   * method AddGroup
   */
  @VTID(8)
  void addGroup(java.lang.String iGroupLabel, com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameters);

  /**
   * method DeleteGroup
   */
  @VTID(9)
  void deleteGroup(java.lang.String iGroupLabel);

  /**
   * method AddViewsByAsap2
   */
  @VTID(10)
  void addViewsByAsap2(java.lang.String iDescriptionFilePath, java.lang.String iRoot,
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iRanges);

  /**
   * method SetAddressRanges
   */
  @VTID(11)
  void setAddressRanges(com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iRanges, boolean iReplace);

  /**
   * method GetWorkpackages
   */
  @VTID(12)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getWorkpackages();

  @VTID(12)
  @ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = {
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class })
  java.lang.Object getWorkpackages(int index);

  /**
   * method GetWorkpackagesResolved
   */
  @VTID(13)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection getWorkpackagesResolved(int iDataSet);

  /**
   * method GetXML
   */
  @VTID(14)
  java.lang.String getXML();

  /**
   * method CreateLabFiles
   */
  @VTID(15)
  void exportLab(java.lang.String iTargetDirectory, java.lang.String iPattern, int iDataSet);

  /**
   * method ImportFromXmlFile
   */
  @VTID(16)
  void importFromXmlFile(java.lang.String iXML);

}
