/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.GttFuncparam;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpImportFromFuncInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * @author bru2cob
 */
public class WpImportFromFuncCommand extends AbstractSimpleCommand {

  /**
   * Count of default wp resp in a2l
   */
  private static final int DEFAULT_A2L_WP_RESP_COUNT = 1;

  /**
   * Input - Wp Definition Version
   */
  private A2lWpDefnVersion wpDefVers;

  private final Set<AbstractSimpleCommand> defVerscmdSet = new HashSet<>();

  private long pidcVersId;

  private long pidcId;

  private final long pidcA2lId;

  private final boolean deleteUnusedWPs;

  private final boolean keepExistingResp;


  /**
   * @param serviceData Service Data
   * @param inputData WpImportFromFuncInput
   * @throws IcdmException init error
   */
  public WpImportFromFuncCommand(final ServiceData serviceData, final WpImportFromFuncInput inputData)
      throws IcdmException {
    super(serviceData);
    this.wpDefVers = new A2lWpDefnVersionLoader(getServiceData()).getDataObjectByID(inputData.getWpDefVersId());
    this.pidcA2lId = this.wpDefVers.getPidcA2lId();
    this.deleteUnusedWPs = inputData.isDeleteUnusedWPs();
    this.keepExistingResp = inputData.isKeepExistingResp();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    // Throw error, if the selected Wp Definition version is not a working set version
    if (!this.wpDefVers.isWorkingSet()) {
      throw new IcdmException("WP creation from Functions is possible only in Working Set WP Definition version");
    }
    long startTime = System.currentTimeMillis();
    createBackUpVers(this.wpDefVers);

    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    A2LFileInfoLoader a2lFileLoader = new A2LFileInfoLoader(getServiceData());
    A2LFileInfoProvider a2lInfoProvider = new A2LFileInfoProvider(getServiceData());

    // load A2LFileInfo
    PidcA2l pidcA2l = pidcA2lLoader.getDataObjectByID(getPidcA2lId());
    A2LFile a2lFileObj = a2lFileLoader.getDataObjectByID(pidcA2l.getA2lFileId());
    A2LFileInfo a2lFileInfo = a2lInfoProvider.fetchA2LFileInfo(a2lFileObj);

    Long timeTaken = System.currentTimeMillis() - startTime;
    getLogger().debug("Time for creating Backup Version and loading A2LFileInfo - {}", timeTaken);

    // get labels
    Map<String, Characteristic> labels = a2lFileInfo.getAllModulesLabels();
    // initialise primary key for temp table
    long recId = -1;
    for (Characteristic characteristic : labels.values()) {
      if (characteristic.getDefFunction() != null) {
        GttFuncparam funcParam = new GttFuncparam();
        funcParam.setFunName(characteristic.getDefFunction().getName());
        funcParam.setParamName(characteristic.getName());
        funcParam.setType(characteristic.getType());
        funcParam.setId(recId);
        getEm().persist(funcParam);
        recId--;
      }
    }
    // push the new records into temp table
    getEm().flush();
    timeTaken = System.currentTimeMillis() - timeTaken;
    getLogger().debug("Number of entries in temp table - {}, time taken to load into temp table - {}", recId,
        timeTaken);

    this.pidcVersId = pidcA2l.getPidcVersId();
    this.pidcId = pidcA2l.getProjectId();

    final Query query = getEm().createNativeQuery("call pk_create_wp_from_func.p_create_wp_from_func( ?1, ?2, ?3, ?4)");
    query.setParameter(1, this.wpDefVers.getId());
    query.setParameter(2, CommonUtils.getBooleanCode(this.deleteUnusedWPs));
    query.setParameter(3, CommonUtils.getBooleanCode(this.keepExistingResp));
    query.setParameter(4, getServiceData().getUsername());

    query.executeUpdate();

    getLogger().debug("Time taken to run the procedure - {}", System.currentTimeMillis() - timeTaken);

    getEm().flush();

    // Refresh the A2lWPDefinitionVersion Entity Object
    A2lWpDefnVersionLoader wpDefnVersionLoader = new A2lWpDefnVersionLoader(getServiceData());
    wpDefnVersionLoader.refreshEntity(this.wpDefVers.getId());

    this.wpDefVers = wpDefnVersionLoader.getDataObjectByID(this.wpDefVers.getId());
    // Updated the A2l Wp Definition Version to enable Master Refresh Applicable
    A2lWpDefinitionVersionCommand wpDefVerCmd =
        new A2lWpDefinitionVersionCommand(getServiceData(), this.wpDefVers, true, false, true);

    executeChildCommand(wpDefVerCmd);
    this.defVerscmdSet.add(wpDefVerCmd);

    updatePidcA2l();
  }

  /**
   * Update the pidc a2l flag is new mappings are created
   */
  private void updatePidcA2l() throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2l = pidcA2lLoader.getDataObjectByID(getPidcA2lId());
    pidcA2l.setWorkingSetModified(true);
    PidcA2lCommand pidcA2lCmd = new PidcA2lCommand(getServiceData(), pidcA2l, true, false, false);
    executeChildCommand(pidcA2lCmd);
  }

  /**
   * create a non active version as a backup of working set before import,only when wp resp exists other than default
   * one
   *
   * @throws IcdmException
   **/
  private void createBackUpVers(final A2lWpDefnVersion workingSet) throws IcdmException {
    Map<Long, A2lWpResponsibility> wpRespMap =
        new A2lWpResponsibilityLoader(getServiceData()).getWpRespForWpDefnVers(workingSet.getId()).getWpRespMap();
    // if only default wp resp is present , back up version is not created
    if (wpRespMap.size() != DEFAULT_A2L_WP_RESP_COUNT) {
      getLogger().debug("Creating Backup Version of Working Set");
      A2lWpDefnVersion backUpVers = new A2lWpDefnVersion();
      backUpVers.setVersionName("Backup of Working Set before WP creation from Func");
      backUpVers.setActive(false);
      backUpVers.setParamLevelChgAllowedFlag(workingSet.isParamLevelChgAllowedFlag());
      backUpVers.setPidcA2lId(workingSet.getPidcA2lId());
      backUpVers.setWorkingSet(false);
      A2lWpDefinitionVersionCommand cmd =
          new A2lWpDefinitionVersionCommand(getServiceData(), backUpVers, false, false, false);
      // to create version using procedure
      cmd.setNewVersionProc(true);
      executeChildCommand(cmd);
      cmd.refreshEntity();
      this.defVerscmdSet.add(cmd);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    refreshEntity();
  }

  /**
   *
   */
  private void refreshEntity() {
    getLogger().debug("Refreshing Entities post commit");

    // Refresh parent of a2l work packages and a2l responsibilities
    new PidcLoader(getServiceData()).refreshEntity(this.pidcId);
    new PidcVersionLoader(getServiceData()).refreshEntity(this.pidcVersId);
    // Refresh the parent of A2l wp Definition Version
    new PidcA2lLoader(getServiceData()).refreshEntity(this.pidcA2lId);

    // Refresh the entity after the creation, as the child records are created via stored procedure
    TA2lWpDefnVersion entity = new A2lWpDefnVersionLoader(getServiceData()).refreshEntity(this.wpDefVers.getId());

    List<TA2lWpResponsibility> a2lWpRespList = entity.getTA2lWpResponsibility();

    for (TA2lWpResponsibility ta2lWpResponsibility : a2lWpRespList) {
      // Refresh the parent of A2l Wp Param mapping
      getEm().refresh(ta2lWpResponsibility);
      for (TA2lWpParamMapping ta2lWpParamMapping : ta2lWpResponsibility.getTA2lWpParamMappings()) {
        ta2lWpParamMapping.getTA2lResponsibility();
        ta2lWpParamMapping.getTA2lWpResponsibility();
        ta2lWpParamMapping.getTParameter();
        getEm().refresh(ta2lWpParamMapping);
      }
    }

    getLogger().debug("Refreshing Entities post commit completed");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }


  /**
   * @return the a2lWpDefCommandSet
   */
  public Set<AbstractSimpleCommand> getA2lWpDefCommandSet() {
    return new HashSet<>(this.defVerscmdSet);
  }

  /**
   * @return the pidcA2lId
   */
  public long getPidcA2lId() {
    return this.pidcA2lId;
  }

}
