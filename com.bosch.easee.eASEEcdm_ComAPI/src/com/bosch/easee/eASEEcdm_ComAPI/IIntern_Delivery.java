package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.Com4jObject;
import com4j.IID;
import com4j.VTID;

@IID("{5923B842-6051-492B-A091-FEBF27B22C3D}")
public interface IIntern_Delivery extends Com4jObject {

  @VTID(7)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySettings getDeliverySettings(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject project, boolean externalDelivery);

  @VTID(8)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliveryResult deliverCalibrationData(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMDeliverySettings settings, boolean withGui);

  @VTID(9)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationSettings getActivationSettings(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProject project);

  @VTID(10)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationResult activate(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMActivationSettings settings);

  @VTID(11)
  void updateDatasetByAddress(com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset iDataSet, java.lang.String iAddress,
      byte[] iValue);


  @VTID(12)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol saveParameterValues(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMDataset iDataSet, com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection values);
}
