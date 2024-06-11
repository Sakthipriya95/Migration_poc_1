package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.Com4jObject;
import com4j.Holder;
import com4j.IID;
import com4j.VTID;


@IID("{73A92B1D-61D2-4098-882E-DCE39B30757A}")
public interface IIntern_ProjectBaseCfg extends Com4jObject {

  @VTID(7)
  int createProjectGui();

  @VTID(8)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProjectSettings getCreateProjectSettings();

  @VTID(9)
  int createProject(com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProjectSettings settings,
      Holder<com.bosch.easee.eASEEcdm_ComAPI.ICDMProcessInfo> oInfo);

  @VTID(10)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetResult createProgramset(
      com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings settings, boolean continueOnValidationFail);

  @VTID(11)
  int createProgramsetGui(com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings settings);

  @VTID(12)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectSettings getProgramKeyConfig(int versionId);

  @VTID(13)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMMessageCol validateAgainstDescription(java.lang.String hexPath,
      java.lang.String descriptionPath, boolean performEpromCheck, boolean performHeuristicCheck,
      Holder<Boolean> epromResult, Holder<Boolean> heuristicResult);

  @VTID(14)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCalibrationProjectSettings getProductKeyConfig(int versionId);

  @VTID(15)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMCreateProgramsetSettings getCreateProgramsetSettings();

  @VTID(16)
  com.bosch.easee.eASEEcdm_ComAPI.ICDMProgramSet getProgramSet(java.lang.String versionId);
}
