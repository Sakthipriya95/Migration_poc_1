package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.Com4jObject;
import com4j.IID;
import com4j.VTID;


/**
 * ICdmExtension Interface
 */
@IID("{E20B0378-3D5D-4AE8-B4A1-A2DF457EE633}")
public interface ICdmExtension extends Com4jObject {

  /**
   * method GetCdmStudio
   */
  @VTID(7)
  com4j.Com4jObject getCdmStudio();

  /**
   * method GeteASEEApplication
   */
  @VTID(8)
  com4j.Com4jObject geteASEEApplication();

  /**
   * method GetValueDisplayPrecision
   */
  @VTID(9)
  boolean getValueDisplayPrecision();

  /**
   * method SetValueDisplayPrecision
   */
  @VTID(10)
  void setValueDisplayPrecision(boolean iFullPrecision);

  /**
   * method GetParameterHistory
   */
  @VTID(11)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection internGetParameterHistory(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset);

  /**
   * method GetParameterHistoryFiltered
   */
  @VTID(12)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection internGetParameterHistoryFiltered(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset dataset,
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameterFilter,
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iFunctionFilter);
}
