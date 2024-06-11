/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * @author NIP4COB
 */
public class PidcA2lCommand extends AbstractCommand<PidcA2l, PidcA2lLoader> {

  private boolean updateOnlyWSFlag;

  private boolean takeOverA2L;
  private boolean isMasterRefreshApplicable;

  /**
   * @param serviceData ServiceData
   * @param inputData PidcA2l
   * @throws IcdmException error while retrieving data
   */
  public PidcA2lCommand(final ServiceData serviceData, final PidcA2l inputData) throws IcdmException {
    this(serviceData, inputData, false);
  }

  /**
   * @param serviceData ServiceData
   * @param inputData PidcA2l
   * @param isUpdate true, for update
   * @param updateOnlyWSFlag updateOnlyWSFlag
   * @param isMasterRefreshApplicable TODO
   * @throws IcdmException error while retrieving data
   */
  public PidcA2lCommand(final ServiceData serviceData, final PidcA2l inputData, final boolean isUpdate,
      final boolean updateOnlyWSFlag, final boolean isMasterRefreshApplicable) throws IcdmException {
    super(serviceData, inputData, new PidcA2lLoader(serviceData), isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
    this.updateOnlyWSFlag = updateOnlyWSFlag;
    this.isMasterRefreshApplicable = isMasterRefreshApplicable;
  }

  /**
   * @param serviceData service data
   * @param inputData pidc a2l
   * @param isTakeOverA2L is take over from a2l
   * @throws IcdmException Exception
   */
  public PidcA2lCommand(final ServiceData serviceData, final PidcA2l inputData, final boolean isTakeOverA2L)
      throws IcdmException {
    super(serviceData, inputData, new PidcA2lLoader(serviceData), COMMAND_MODE.CREATE);
    this.takeOverA2L = isTakeOverA2L;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TPidcA2l dbPidcA2l = new TPidcA2l();
    dbPidcA2l.setSdomPverName(getInputData().getSdomPverName());
    dbPidcA2l.setMvTa2lFileinfo(new A2LFileInfoLoader(getServiceData()).getEntityObject(getInputData().getA2lFileId()));
    dbPidcA2l.setTabvProjectidcard(new PidcLoader(getServiceData()).getEntityObject(getInputData().getProjectId()));
    if (getInputData().getPidcVersId() != null) {
      dbPidcA2l
          .setTPidcVersion(new PidcVersionLoader(getServiceData()).getEntityObject(getInputData().getPidcVersId()));
      dbPidcA2l.setAssignedDate(getCurrentTime());
      dbPidcA2l.setAssignedUser(getServiceData().getUsername());
    }
    else {
      dbPidcA2l.setTPidcVersion(null);
      dbPidcA2l.setAssignedDate(null);
      dbPidcA2l.setAssignedUser(null);
    }
    if (getInputData().getVcdmA2lName() != null) {
      dbPidcA2l.setVcdmA2lName(getInputData().getVcdmA2lName());
    }
    dbPidcA2l.setIsActive(booleanToYorN(getInputData().isActive()));
    dbPidcA2l.setWpParamPresentFlag(booleanToYorN(getInputData().isWpParamPresentFlag()));
    dbPidcA2l.setActiveWpParamPresentFlag(booleanToYorN(getInputData().isActiveWpParamPresentFlag()));
    dbPidcA2l.setIsWorkingSetModifiedFlag(booleanToYorN(getInputData().isWorkingSetModified()));
    setUserDetails(COMMAND_MODE.CREATE, dbPidcA2l);

    persistEntity(dbPidcA2l);
    setOtherTablesForMapping(dbPidcA2l);
  }

  /**
   * @throws IcdmException
   * @throws DataException
   */
  private void setOtherTablesForMapping(final TPidcA2l dbPidcA2l) throws IcdmException {
    // Update related entities
    Set<TPidcA2l> tabvPidcA2l = new PidcLoader(getServiceData())
        .getEntityObject(dbPidcA2l.getTPidcVersion().getTabvProjectidcard().getProjectId()).getTabvPidcA2ls();
    if (tabvPidcA2l == null) {
      tabvPidcA2l = new HashSet<>();
    }
    tabvPidcA2l.add(dbPidcA2l);
    // create default A2lResponsibility objects if they are not present for the PIDC
    createDefaultA2lResp(dbPidcA2l.getTPidcVersion());
    TPidcVersion newDbPidcVers =
        new PidcVersionLoader(getServiceData()).getEntityObject(getInputData().getPidcVersId());
    if (newDbPidcVers.getTabvPidcA2ls() == null) {
      newDbPidcVers.setTabvPidcA2ls(new ArrayList<TPidcA2l>());
    }
    newDbPidcVers.getTabvPidcA2ls().add(dbPidcA2l);
    // Update pidcversion timestamp
    updateTimeStamp(newDbPidcVers);
  }


  /**
   * @param tPidcVersion
   * @throws IcdmException
   */
  private void createDefaultA2lResp(final TPidcVersion tPidcVersion) throws IcdmException {

    long pidcId = tPidcVersion.getTabvProjectidcard().getProjectId();

    A2lResponsibilityModel a2lRespForPidc = new A2lResponsibilityLoader(getServiceData()).getByPidc(pidcId);
    Map<String, A2lResponsibility> defaultA2lRespMap = a2lRespForPidc.getDefaultA2lRespMap();

    // Create Default responsibilities for BOSCH ,OTHER CUSTOMER types
    if (!defaultA2lRespMap.containsKey(WpRespType.RB.getCode())) {
      createDefaultA2lRespObj(pidcId, WpRespType.RB);
    }
    if (!defaultA2lRespMap.containsKey(WpRespType.CUSTOMER.getCode())) {
      createDefaultA2lRespObj(pidcId, WpRespType.CUSTOMER);
    }
    if (!defaultA2lRespMap.containsKey(WpRespType.OTHERS.getCode())) {
      createDefaultA2lRespObj(pidcId, WpRespType.OTHERS);
    }
    addBegRespIfNeeded(tPidcVersion.getPidcVersId(), pidcId, a2lRespForPidc);
  }


  /**
   * @param pidcVersId
   * @param pidcId
   * @param a2lRespForPidc
   * @throws IcdmException
   */
  private void addBegRespIfNeeded(final long pidcVersId, final long pidcId, final A2lResponsibilityModel a2lRespForPidc)
      throws IcdmException {
    A2lResponsibility a2lRespBEGObj = new A2lResponsibilityLoader(getServiceData())
        .createBegRespIfApplicable(this.takeOverA2L, pidcVersId, pidcId, a2lRespForPidc);
    if (null != a2lRespBEGObj) {
      invokeA2LRespCmd(a2lRespBEGObj);
    }
  }


  /**
   * @param pidcId
   * @param respType
   * @throws IcdmException
   */
  private void createDefaultA2lRespObj(final long pidcId, final WpRespType respType) throws IcdmException {
    A2lResponsibility a2lResp = new A2lResponsibilityLoader(getServiceData()).createA2LRespObj(pidcId, respType);
    invokeA2LRespCmd(a2lResp);
  }

  /**
   * @param a2lResp
   * @throws IcdmException
   * @throws DataException
   */
  private void invokeA2LRespCmd(final A2lResponsibility a2lResp) throws IcdmException {
    A2lResponsibilityCommand cmd = new A2lResponsibilityCommand(getServiceData(), a2lResp, false, false);
    executeChildCommand(cmd);
  }

  /**
   * @param tPidcVersion
   * @throws IcdmException
   */
  private void updateTimeStamp(final TPidcVersion tPidcVersion) throws IcdmException {
    PidcVersionCommand pidcVerCmd = new PidcVersionCommand(getServiceData(),
        new PidcVersionLoader(getServiceData()).getDataObjectByID(tPidcVersion.getPidcVersId()), true, false);
    pidcVerCmd.setUpdateTimestamp(true);
    executeChildCommand(pidcVerCmd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TPidcA2l dbPidcA2lToUpdate = new PidcA2lLoader(getServiceData()).getEntityObject(getInputData().getId());
    setMasterRefreshApplicable(this.isMasterRefreshApplicable);
    if (this.updateOnlyWSFlag) {
      dbPidcA2lToUpdate.setIsWorkingSetModifiedFlag(booleanToYorN(getInputData().isWorkingSetModified()));
    }
    else {
      setNewValues(dbPidcA2lToUpdate);
      if (getInputData().getPidcVersId() != null) {
        setOtherTablesForUnMapping(dbPidcA2lToUpdate);
        setOtherTablesForMapping(dbPidcA2lToUpdate);
      }
      else {
        setOtherTablesForUnMapping(dbPidcA2lToUpdate);
      }
    }
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcA2lToUpdate);
  }

  /**
   * @param modifiedPidcA2l
   * @throws IcdmException
   */
  private void setOtherTablesForUnMapping(final TPidcA2l modifiedPidcA2l) throws IcdmException {
    if (getOldData().getPidcVersId() != null) {
      TPidcVersion existingDbPidcVers =
          new PidcVersionLoader(getServiceData()).getEntityObject(getOldData().getPidcVersId());
      if (existingDbPidcVers.getTabvPidcA2ls() != null) {
        existingDbPidcVers.getTabvPidcA2ls().remove(modifiedPidcA2l);
      }
      // Update pidcversion timestamp
      updateTimeStamp(existingDbPidcVers);
    }

  }

  /**
   * @param modifiedPidcA2l
   * @throws IcdmException
   * @throws ParseException
   */
  private void setNewValues(final TPidcA2l modifiedPidcA2l) throws IcdmException {

    modifiedPidcA2l.setIsActive(booleanToYorN(getInputData().isActive()));
    modifiedPidcA2l.setWpParamPresentFlag(booleanToYorN(getInputData().isWpParamPresentFlag()));
    modifiedPidcA2l.setActiveWpParamPresentFlag(booleanToYorN(getInputData().isActiveWpParamPresentFlag()));
    modifiedPidcA2l.setIsWorkingSetModifiedFlag(booleanToYorN(getInputData().isWorkingSetModified()));

    if (isObjectChanged(getOldData().getPidcVersId(), getInputData().getPidcVersId())) {
      if (getInputData().getPidcVersId() == null) {
        modifiedPidcA2l.setTPidcVersion(null);
        modifiedPidcA2l.setAssignedUser(null);
        modifiedPidcA2l.setAssignedDate(null);
      }
      else {
        modifiedPidcA2l
            .setTPidcVersion(new PidcVersionLoader(getServiceData()).getEntityObject(getInputData().getPidcVersId()));
        modifiedPidcA2l.setAssignedDate(getCurrentTime());
        modifiedPidcA2l.setAssignedUser(getServiceData().getUsername());
      }

    }
    if (isObjectChanged(getOldData().getVcdmA2lDate(), getInputData().getVcdmA2lDate())) {
      try {
        modifiedPidcA2l.setVcdmA2lDate(string2timestamp(getInputData().getVcdmA2lDate()));
      }
      catch (ParseException e) {
        throw new IcdmException("Error in parsing VcdmA2lDate format : " + e.getMessage());
      }
    }
    if (isObjectChanged(getOldData().getVcdmA2lName(), getInputData().getVcdmA2lName())) {
      modifiedPidcA2l.setVcdmA2lName(getInputData().getVcdmA2lName());
    }
    if (isObjectChanged(getOldData().getSsdSoftwareProjId(), getInputData().getSsdSoftwareProjId())) {
      modifiedPidcA2l.setSsdSoftwareProjID(getInputData().getSsdSoftwareProjId());
    }
    if (isObjectChanged(getOldData().getSsdSoftwareVersion(), getInputData().getSsdSoftwareVersion())) {
      modifiedPidcA2l.setSsdSoftwareVersion(getInputData().getSsdSoftwareVersion());
    }
    if (isObjectChanged(getOldData().getSsdSoftwareVersionId(), getInputData().getSsdSoftwareVersionId())) {
      modifiedPidcA2l.setSsdSoftwareVersionID(getInputData().getSsdSoftwareVersionId());
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TPidcA2l entity = new PidcA2lLoader(getServiceData()).getEntityObject(getInputData().getId());
    // Unmap from pidcVersion
    setOtherTablesForUnMapping(entity);
    // Unmap from pidc
    Set<TPidcA2l> tabvPidcA2l =
        new PidcLoader(getServiceData()).getEntityObject(getInputData().getProjectId()).getTabvPidcA2ls();
    tabvPidcA2l.remove(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return validatePidcVerDetails() ||
        isObjectChanged(getOldData().getVcdmA2lName(), getInputData().getVcdmA2lName()) || validateSsdDetails() ||
        validateWpDetails();
  }

  /**
   * @return
   */
  private boolean validateWpDetails() {
    return isObjectChanged(getOldData().isActiveWpParamPresentFlag(), getInputData().isActiveWpParamPresentFlag()) ||
        isObjectChanged(getOldData().isWpParamPresentFlag(), getInputData().isWpParamPresentFlag()) ||
        isObjectChanged(getOldData().isWorkingSetModified(), getInputData().isWorkingSetModified());
  }

  /**
   * @return
   */
  private boolean validatePidcVerDetails() {
    return isObjectChanged(getOldData().getPidcVersId(), getInputData().getPidcVersId()) ||
        isObjectChanged(getOldData().getVcdmA2lDate(), getInputData().getVcdmA2lDate()) ||
        isObjectChanged(getOldData().isActive(), getInputData().isActive());
  }

  /**
   * @return
   */
  private boolean validateSsdDetails() {
    return isObjectChanged(getOldData().getSsdSoftwareProjId(), getInputData().getSsdSoftwareProjId()) ||
        isObjectChanged(getOldData().getSsdSoftwareVersion(), getInputData().getSsdSoftwareVersion()) ||
        isObjectChanged(getOldData().getSsdSoftwareVersionId(), getInputData().getSsdSoftwareVersionId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }
}
