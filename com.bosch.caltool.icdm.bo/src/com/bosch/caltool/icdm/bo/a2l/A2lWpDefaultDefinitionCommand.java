/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.Map;

import javax.persistence.Query;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPVersionLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespInput;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * @author gge6cob
 */
public class A2lWpDefaultDefinitionCommand extends AbstractSimpleCommand {

  private final A2lWpDefnVersion inputObj;
  private A2lWpDefnVersion newWorkingSetVersion;
  private A2lWpDefnVersion newActiveVersionDefault;
  private A2lWpDefnVersion newActiveVersionFC2WP;
  private A2lWpResponsibility defaultWpRespPal;
  private final boolean isWpForCopyMappings;
  private final boolean createActiveVersion;
  private final boolean takeOverA2L;

  /**
   * Instantiates a new a2l wp default definition command.
   *
   * @param serviceData the service data
   * @param inputObj the input obj
   * @param isWpForCopyMappings false if mappings need not be copied from working set
   * @param createActiveVersion flag to check whether an active version to be created
   * @throws IcdmException the icdm exception
   */
  public A2lWpDefaultDefinitionCommand(final ServiceData serviceData, final A2lWpDefnVersion inputObj,
      final boolean isWpForCopyMappings, final boolean createActiveVersion) throws IcdmException {

    this(serviceData, inputObj, isWpForCopyMappings, createActiveVersion, false);
  }

  /**
   * Instantiates a new a2l wp default definition command.
   *
   * @param serviceData the service data
   * @param inputObj the input obj
   * @param isWpForCopyMappings false if mappings need not be copied from working set
   * @param createActiveVersion flag to check whether an active version to be created
   * @param takeOverA2L If set to true if the A2L is take over from another A2L
   * @throws IcdmException the icdm exception
   */
  public A2lWpDefaultDefinitionCommand(final ServiceData serviceData, final A2lWpDefnVersion inputObj,
      final boolean isWpForCopyMappings, final boolean createActiveVersion, final boolean takeOverA2L)
      throws IcdmException {

    super(serviceData);

    this.inputObj = inputObj;
    this.isWpForCopyMappings = isWpForCopyMappings;
    this.createActiveVersion = createActiveVersion;
    this.takeOverA2L = takeOverA2L;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    Long pidcVersId =
        new PidcA2lLoader(getServiceData()).getDataObjectByID(this.inputObj.getPidcA2lId()).getPidcVersId();

    if (!this.takeOverA2L && isLoadFC2WPMappings(pidcVersId) &&
        (null == new PidcVersionAttributeLoader(getServiceData()).getPidcQnaireConfigAttrVal(pidcVersId))) {
      throw new IcdmException("FC2WP.DIV_ATTR_NOT_SET", new AttributeLoader(getServiceData())
          .getDataObjectByID(
              Long.valueOf((new CommonParamLoader(getServiceData())).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR)))
          .getName());
    }

    // Create working set
    A2lWpDefinitionVersionCommand workingSetCmd =
        new A2lWpDefinitionVersionCommand(getServiceData(), this.inputObj, false, false, this.isWpForCopyMappings);
    executeChildCommand(workingSetCmd);
    getServiceData().getEntMgr().flush();
    this.newWorkingSetVersion = workingSetCmd.getNewData();
    Long wpDefnVersId = this.newWorkingSetVersion.getId();
    getLogger().info("A2lWpDefinitionVersion WorkingSet created. Id = {}", wpDefnVersId);

    TPidcA2l tPidcA2l = new PidcA2lLoader(getServiceData()).getEntityObject(this.inputObj.getPidcA2lId());
    Long pidcId = tPidcA2l.getTabvProjectidcard().getProjectId();

    // Get Existing or Create new - A2lResponsibility - Bosch Type
    Long a2lRespId = null;
    Map<String, A2lResponsibility> pidcRespMap =
        new A2lResponsibilityLoader(getServiceData()).getDefaultA2lResp(pidcId);

    if (CommonUtils.isNotEmpty(pidcRespMap) && pidcRespMap.containsKey(WpRespType.RB.getCode())) {
      a2lRespId = pidcRespMap.get(WpRespType.RB.getCode()).getId();
    }
    else {
      A2lResponsibility boschResp = new A2lResponsibility();
      boschResp.setProjectId(pidcId);
      boschResp.setRespType(WpRespType.RB.getCode());
      // create default Bosch without userdetails Responsibility
      A2lResponsibilityCommand cmd21 = new A2lResponsibilityCommand(getServiceData(), boschResp, false, false);
      executeChildCommand(cmd21);
      a2lRespId = cmd21.getNewData().getId();
      getLogger().info("A2lResponsibility - Default BOSCH Responsibility created for PIDC Id = {}. Resp Id = {}",
          pidcId, a2lRespId);
    }
    if (!CommonUtils.isNotEmpty(pidcRespMap) || !pidcRespMap.containsKey(WpRespType.CUSTOMER.getCode())) {
      // Customer
      A2lResponsibility custResp = new A2lResponsibility();
      custResp.setProjectId(pidcId);
      custResp.setRespType(WpRespType.CUSTOMER.getCode());
      // create default Customer without user details Responsibility
      A2lResponsibilityCommand cmd22 = new A2lResponsibilityCommand(getServiceData(), custResp, false, false);
      executeChildCommand(cmd22);
      getLogger().info("A2lResponsibility - Default CUSTOMER Responsibility created for PIDC Id = {}. Resp Id = {}",
          pidcId, custResp.getId());
    }

    Map<String, A2lWorkPackage> a2lWpMap =
        new A2lWorkPackageLoader(getServiceData()).getByPidcVersionId(tPidcA2l.getTPidcVersion().getPidcVersId());
    Long a2lWpId;

    // Condition to check whether the same wp name is already inserted for the pidc version
    if (a2lWpMap.containsKey(ApicConstants.DEFAULT_A2L_WP_NAME)) {
      a2lWpId = a2lWpMap.get(ApicConstants.DEFAULT_A2L_WP_NAME).getId();
    }
    else {
      A2lWorkPackage a2lWp = new A2lWorkPackage();
      a2lWp.setPidcVersId(tPidcA2l.getTPidcVersion().getPidcVersId());
      a2lWp.setName(ApicConstants.DEFAULT_A2L_WP_NAME);
      A2lWorkPackageCommand a2lWpCommand = new A2lWorkPackageCommand(getServiceData(), a2lWp, false, false);
      executeChildCommand(a2lWpCommand);
      a2lWpId = a2lWpCommand.getObjId();
    }

    A2lWpResponsibility input = new A2lWpResponsibility();
    input.setWpDefnVersId(wpDefnVersId);
    input.setA2lRespId(a2lRespId);
    input.setA2lWpId(a2lWpId);

    // Create default PAL definition
    A2lWpResponsibilityCommand cmd3 = new A2lWpResponsibilityCommand(getServiceData(), input, false, false);
    executeChildCommand(cmd3);
    this.defaultWpRespPal = cmd3.getNewData();
    getLogger().info("A2lWpResponsibility - Default Work Package Defintion created. Id = {}",
        cmd3.getNewData().getId());

    Query insertDefaultParamMapping =
        getServiceData().getEntMgr().createNamedQuery(TA2lWpParamMapping.NNS_INS_TABLE_A2L_WP_DEFAULT_MAPPING);
    insertDefaultParamMapping.setParameter(1, this.defaultWpRespPal.getId()); // WP_RESP_ID
    insertDefaultParamMapping.setParameter(2, getServiceData().getUsername()); // CREATED_USER
    insertDefaultParamMapping.setParameter(3, tPidcA2l.getMvTa2lFileinfo().getId()); // a2lFileID
    insertDefaultParamMapping.executeUpdate();
    getLogger().info(
        "A2lWpParamMapping - Default Work Package Mapping created for all parameters by BatchInsert. A2lFileId = {}",
        tPidcA2l.getMvTa2lFileinfo().getId());

    if (this.createActiveVersion) {
      this.newActiveVersionDefault = createActiveVersion("Version created by System",
          "Automatically created when a newly mapped A2L is loaded", true);
    }

    // Load FC2WP mappings
    loadFc2WpMappingsIfApplicable(pidcVersId, pidcId);
  }

  /**
   * @param description
   * @param versionName
   * @param isInitialVersion
   * @return
   * @throws IcdmException
   * @throws DataException
   */
  private A2lWpDefnVersion createActiveVersion(final String versionName, final String description,
      final boolean isInitialVersion)
      throws IcdmException {

    getServiceData().getEntMgr().flush();
    refreshEntity();

    A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();
    a2lWpDefnVersion.setVersionName(versionName);
    a2lWpDefnVersion.setDescription(description);
    a2lWpDefnVersion.setActive(true);
    a2lWpDefnVersion.setParamLevelChgAllowedFlag(this.newWorkingSetVersion.isParamLevelChgAllowedFlag());
    a2lWpDefnVersion.setPidcA2lId(this.newWorkingSetVersion.getPidcA2lId());

    A2lWpDefnVersion newA2lWpDefnVersion;
    if (isInitialVersion) {
      A2lWpDefinitionVersionCommand activeCmd =
          new A2lWpDefinitionVersionCommand(getServiceData(), a2lWpDefnVersion, false, false, false);
      activeCmd.setNewVersionProc(true);
      executeChildCommand(activeCmd);
      newA2lWpDefnVersion = activeCmd.getNewData();
    }
    else {
      A2lWpDefinitionActiveVersionCommand activeVersCmd =
          new A2lWpDefinitionActiveVersionCommand(getServiceData(), a2lWpDefnVersion);
      executeChildCommand(activeVersCmd);
      newA2lWpDefnVersion = activeVersCmd.getNewData();
    }

    getServiceData().getEntMgr().flush();
    refreshEntity();
    updatePidcA2l(this.newWorkingSetVersion.getPidcA2lId());
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    // fetch the details
    loader.resolveA2lRespWpForAllGrpTypes(this.newWorkingSetVersion.getPidcA2lId());

    return newA2lWpDefnVersion;
  }

  /**
   * @param pidcVersId
   * @param pidcId
   * @param wpDefnVersId
   * @throws DataException
   * @throws IcdmException
   */
  private void loadFc2WpMappingsIfApplicable(final Long pidcVersId, final Long pidcId) throws IcdmException {
    if (this.takeOverA2L) {
      return;
    }

    if (isLoadFC2WPMappings(pidcVersId)) {

      ImportA2lWpRespInput fc2wpImportInput = new ImportA2lWpRespInput();
      fc2wpImportInput.setA2lFileId(new PidcA2lLoader(getServiceData())
          .getDataObjectByID(this.newWorkingSetVersion.getPidcA2lId()).getA2lFileId());
      fc2wpImportInput.setVariantGrpId(null);
      fc2wpImportInput
          .setFc2wpVersId(new FC2WPVersionLoader(getServiceData()).findActiveVersionByPidcVersion(pidcVersId).getId());
      fc2wpImportInput.setWpDefVersId(this.newWorkingSetVersion.getId());
      fc2wpImportInput.setPidcVersionId(pidcVersId);
      fc2wpImportInput.setCreateParamMapping(true);

      // create BEG responsibility in Pidc if not available (if iCDM Qnaire Config is BEG)
      A2lResponsibilityModel a2lRespModelForPidc = new A2lResponsibilityLoader(getServiceData()).getByPidc(pidcId);
      addBegRespIfNeeded(pidcVersId, pidcId, a2lRespModelForPidc);

      // Import FC2WP mappings in A2L
      ImportA2lWpRespFromFC2WPCommand importCmd =
          new ImportA2lWpRespFromFC2WPCommand(getServiceData(), fc2wpImportInput, true);
      executeChildCommand(importCmd);

      // Create active version with FC2WP mapping loaded
      this.newActiveVersionFC2WP = createActiveVersion("FC2WP imported - created by System",
          "Automatically created when a newly mapped A2L is loaded with FC2WP mappings", false);
    }
  }

  /**
   * @param pidcVersId
   * @param pidcId
   * @param a2lRespModelForPidc
   * @throws IcdmException
   */
  private void addBegRespIfNeeded(final Long pidcVersId, final Long pidcId,
      final A2lResponsibilityModel a2lRespModelForPidc)
      throws IcdmException {
    A2lResponsibility a2lRespBEGObj = new A2lResponsibilityLoader(getServiceData())
        .createBegRespIfApplicable(this.takeOverA2L, pidcVersId, pidcId, a2lRespModelForPidc);
    if (null != a2lRespBEGObj) {
      A2lResponsibilityCommand cmd = new A2lResponsibilityCommand(getServiceData(), a2lRespBEGObj, false, false);
      executeChildCommand(cmd);
    }
  }

  /**
   * @param pidcVersId
   * @return
   * @throws DataException
   */
  private boolean isLoadFC2WPMappings(final Long pidcVersId) throws DataException {

    PidcVersionAttribute loadFC2WPAttr =
        new PidcVersionAttributeLoader(getServiceData()).getPidcVersionAttribute(pidcVersId)
            .get(Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.LOAD_FC2WP_IN_A2L_ATTR)));
    return (null != loadFC2WPAttr) && ApicConstants.CODE_YES.equals(loadFC2WPAttr.getUsedFlag());
  }

  /**
   * @param pidcA2lId
   * @throws IcdmException
   */
  private void updatePidcA2l(final Long pidcA2lId) throws IcdmException {
    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId);
    pidcA2l.setWpParamPresentFlag(true);
    pidcA2l.setActiveWpParamPresentFlag(true);
    PidcA2lCommand cmd = new PidcA2lCommand(getServiceData(), pidcA2l, true, false, false);
    executeChildCommand(cmd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    refreshEntity();
  }

  /**
   * Used for copy a2l file command alone
   */
  public void refreshEntity() {
    if (null != this.newWorkingSetVersion) {
      getServiceData().getEntMgr().refresh(
          new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(this.newWorkingSetVersion.getId()));
    }
    if (null != this.newActiveVersionDefault) {
      getServiceData().getEntMgr().refresh(
          new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(this.newActiveVersionDefault.getId()));
    }
    if (null != this.defaultWpRespPal) {
      getServiceData().getEntMgr()
          .refresh(new A2lWpResponsibilityLoader(getServiceData()).getEntityObject(this.defaultWpRespPal.getId()));
    }
    if (null != this.newActiveVersionFC2WP) {
      getServiceData().getEntMgr().refresh(
          new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(this.newActiveVersionFC2WP.getId()));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * Gets the new data.
   *
   * @return the new data
   */
  public A2lWpDefnVersion getNewData() {
    return this.newWorkingSetVersion;
  }

}
