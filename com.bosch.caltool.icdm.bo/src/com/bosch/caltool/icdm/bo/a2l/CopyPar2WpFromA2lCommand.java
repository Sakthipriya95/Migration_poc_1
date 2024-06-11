/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Timer;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.GttFuncparam;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.CopyPar2WpFromA2lInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.WP_RESP_STATUS_TYPE;

/**
 * @author bru2cob
 */
public class CopyPar2WpFromA2lCommand extends AbstractSimpleCommand {

  /**
   * Count of default wp resp in a2l
   */
  private static final int DEFAULT_A2L_WP_RESP_COUNT = 1;

  private final CopyPar2WpFromA2lInput inputData;

  private A2lWpDefnVersion destWpDefVer;

  private boolean isActiveAvailable;

  private final Set<AbstractSimpleCommand> commandSet = new HashSet<>();

  /** True, if copy version to working set **/
  private final boolean isCopyToWkgSet;

  private A2lWpDefnVersion sourceWpDefVer;

  private Map<String, Characteristic> sourceFileLabels;

  private Map<String, Characteristic> destFileLabels;

  private A2LFileInfo srcA2lFileInfo;


  /**
   * @param serviceData Service Data
   * @param inputData CopyPar2WpFromA2lInput
   * @throws IcdmException init error
   */
  public CopyPar2WpFromA2lCommand(final ServiceData serviceData, final CopyPar2WpFromA2lInput inputData)
      throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
    this.isCopyToWkgSet = inputData.isCopyToWS();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    try {
      if (isCopyToWkgSet()) {
        copyA2lWpDefVersionToWkgSet();
      }
      else {
        A2lWpDefnVersionLoader wpDefVerLoader = new A2lWpDefnVersionLoader(getServiceData());
        this.sourceWpDefVer = wpDefVerLoader.getDataObjectByID(this.inputData.getSourceWpDefVersId());
        Map<Long, A2lWpDefnVersion> exisitngWpDefMap =
            wpDefVerLoader.getWPDefnVersionsForPidcA2lId(this.inputData.getDescPidcA2lId());
        for (A2lWpDefnVersion wpDefVer : exisitngWpDefMap.values()) {
          if (wpDefVer.getVersionNumber() == 0) {
            this.destWpDefVer = wpDefVer;
            break;
          }
        }
      }

      if (null != this.destWpDefVer) {
        createBackUpVers(this.destWpDefVer);
      }
      else {
        createDefaultWpDefVerCommand(this.sourceWpDefVer, false, this.sourceWpDefVer.getVersionName(), false);
      }
      this.destWpDefVer.setParamLevelChgAllowedFlag(this.sourceWpDefVer.isParamLevelChgAllowedFlag());

      loadSrcAndDestLabels();

      if (this.inputData.isDerivateFromFunc()) {
        // load param-func info into temp table
        loadInfoInTempTable();
      }
      final Query query = getEm().createNativeQuery("call pk_par2wp_copy.p_par2wp_copy (?1, ?2, ?3, ?4, ?5)");
      query.setParameter(1, this.inputData.getSourceWpDefVersId());
      query.setParameter(2, this.destWpDefVer.getId());
      query.setParameter(3, booleanToYorN(this.inputData.isOverOnlyDefaultAssigments()));
      query.setParameter(4, booleanToYorN(this.inputData.isDerivateFromFunc()));
      query.setParameter(5, getServiceData().getUsername());
      query.executeUpdate();

      A2lWpDefinitionVersionCommand wpDefVerCmd =
          new A2lWpDefinitionVersionCommand(getServiceData(), this.destWpDefVer, true, false, true);


      executeChildCommand(wpDefVerCmd);
      this.commandSet.add(wpDefVerCmd);

      getEm().flush();


      if (!isCopyToWkgSet()) {
        createActiveWpDefVersion();
      }
      updatePidcA2l();
    }
    catch (Exception exp) {
      if (exp.getLocalizedMessage().contains("ORA-20005")) {
        throw new IcdmException("COPY_A2L.COPY_MAPPINGS_INVALID", exp);
      }
      throw exp;
    }
  }

  private void loadSrcAndDestLabels() throws IcdmException {
    Timer timer = new Timer();
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    A2LFileInfoLoader a2lFileLoader = new A2LFileInfoLoader(getServiceData());
    A2LFileInfoProvider a2lInfoProvider = new A2LFileInfoProvider(getServiceData());

    // load source A2LFileInfo
    PidcA2l srcPidcA2l = pidcA2lLoader.getDataObjectByID(this.sourceWpDefVer.getPidcA2lId());
    A2LFile srcA2l = a2lFileLoader.getDataObjectByID(srcPidcA2l.getA2lFileId());
    this.srcA2lFileInfo = a2lInfoProvider.fetchA2LFileInfo(srcA2l);

    // load destination A2LFileInfo
    PidcA2l destPidcA2l = pidcA2lLoader.getDataObjectByID(this.destWpDefVer.getPidcA2lId());
    A2LFile destA2l = a2lFileLoader.getDataObjectByID(destPidcA2l.getA2lFileId());
    A2LFileInfo destA2lFileInfo = a2lInfoProvider.fetchA2LFileInfo(destA2l);

    // get the source labels
    this.sourceFileLabels = this.srcA2lFileInfo.getAllModulesLabels();
    // get the destination labels
    this.destFileLabels = destA2lFileInfo.getAllModulesLabels();

    getLogger().debug("Time taken in loadSrcAndDestLabels Method is {} ms", timer.getElapsedTimeMs());
    timer.finish();
  }

  /**
   * load param-func infor into temp table
   *
   * @throws IcdmException
   */
  private void loadInfoInTempTable() {
    Timer timer = new Timer();

    Set<String> srcKeySet = new HashSet<>(this.sourceFileLabels.keySet());
    Set<String> destKeySet = new HashSet<>(this.destFileLabels.keySet());

    // find the difference between source and destination parameter sets
    destKeySet.removeAll(srcKeySet);

    getLogger().debug("New Labels count - {}, Time for loading A2LFileInfo - {} ms", destKeySet.size(),
        timer.getElapsedTimeMs());

    // initialise primary key for temp table
    long recID = 1;
    Set<String> funcNameSet = new HashSet<>();
    for (String paramName : destKeySet) {
      // for all the new parameters in destination file , insert param-func into temp table
      Characteristic characteristic = this.destFileLabels.get(paramName);
      String funcName = characteristic.getDefFunction().getName();
      GttFuncparam gttFuncParam = new GttFuncparam();
      gttFuncParam.setId(recID);
      gttFuncParam.setFunName(funcName);
      gttFuncParam.setParamName(paramName);
      // to identify destination file parameters
      gttFuncParam.setType("D");
      getEm().persist(gttFuncParam);
      recID++;
      funcNameSet.add(funcName);
    }

    Map<String, Function> srcModuleFunctions = this.srcA2lFileInfo.getAllModulesFunctions();
    for (String funcName : funcNameSet) {
      // identify the source fucntion from a2lFileInfo
      Function function = srcModuleFunctions.get(funcName);
      if (function != null) {
        // for all functions in dest having new labels , find params in source
        // insert them into temp table
        List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
        if (null != defCharRefList) {
          for (DefCharacteristic defChar : defCharRefList) {
            GttFuncparam gttFuncParam = new GttFuncparam();
            gttFuncParam.setId(recID);
            gttFuncParam.setFunName(funcName);
            gttFuncParam.setParamName(defChar.getName());
            // to identify destination file parameters
            gttFuncParam.setType("S");
            getEm().persist(gttFuncParam);
            recID++;
          }
        }
      }
    }

    // push the new records into temp table
    getEm().flush();

    getLogger().debug("Number of entries in temp table - {}, time taken to load to temp table - {} ms", recID,
        timer.getElapsedTimeMs());
    timer.finish();
  }

  /**
   * @param copyPar2WpFromA2lInput
   * @throws IcdmException
   */
  private void createActiveWpDefVersion() throws IcdmException {
    Map<Long, A2lWpDefnVersion> exisitngWpDefMap =
        new A2lWpDefnVersionLoader(getServiceData()).getWPDefnVersionsForPidcA2lId(this.inputData.getDescPidcA2lId());
    A2lWpDefnVersion workingSet = null;
    this.isActiveAvailable = false;
    for (A2lWpDefnVersion wpDefVers : exisitngWpDefMap.values()) {
      if (wpDefVers.isWorkingSet()) {
        workingSet = wpDefVers;
      }
      if (wpDefVers.isActive()) {
        this.isActiveAvailable = true;
      }
    }
    if (!this.isActiveAvailable && (workingSet != null)) {
      A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();
      a2lWpDefnVersion.setVersionName("System generated");
      a2lWpDefnVersion.setDescription("Automatically created after copying mappings from selected A2L");
      a2lWpDefnVersion.setActive(true);

      a2lWpDefnVersion.setParamLevelChgAllowedFlag(workingSet.isParamLevelChgAllowedFlag());
      a2lWpDefnVersion.setPidcA2lId(workingSet.getPidcA2lId());

      A2lWpDefinitionVersionCommand activeCmd =
          new A2lWpDefinitionVersionCommand(getServiceData(), a2lWpDefnVersion, false, false, false);
      activeCmd.setNewVersionProc(true);
      activeCmd.setWpDefnVersForFinStatusTakeOver(this.sourceWpDefVer);
      executeChildCommand(activeCmd);
      activeCmd.refreshEntity();
      // Reset WP Resp Status when function version is mismatched for Params
      updateWpStatusIfParamFuncChanged(activeCmd.getNewData().getId());
    }
  }


  /**
   * Update the pidc a2l flag is new mappings are created
   */
  private void updatePidcA2l() throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2l = pidcA2lLoader.getDataObjectByID(this.inputData.getDescPidcA2lId());
    pidcA2l.setWpParamPresentFlag(true);
    pidcA2l.setActiveWpParamPresentFlag(true);
    pidcA2l.setWorkingSetModified(this.isActiveAvailable);
    PidcA2lCommand pidcA2lCmd = new PidcA2lCommand(getServiceData(), pidcA2l, true, false, false);
    executeChildCommand(pidcA2lCmd);
  }

  /**
   * @throws IcdmException
   */
  private void copyA2lWpDefVersionToWkgSet() throws IcdmException {
    A2lWpDefnVersionLoader wpDefVerLoader = new A2lWpDefnVersionLoader(getServiceData());
    this.sourceWpDefVer = wpDefVerLoader.getDataObjectByID(this.inputData.getSourceWpDefVersId());
    this.destWpDefVer = wpDefVerLoader.getDataObjectByID(this.inputData.getDestWpDefVersId());
  }

  /**
   * create a non active version as a backup of working set before import,only when wp resp exists other than default
   * one
   *
   * @throws IcdmException
   **/
  private void createBackUpVers(final A2lWpDefnVersion workingSet) throws IcdmException {
    Map<Long, A2lWpDefnVersion> exisitngWpDefMap =
        new A2lWpDefnVersionLoader(getServiceData()).getWPDefnVersionsForPidcA2lId(this.inputData.getDescPidcA2lId());
    Optional<A2lWpDefnVersion> activerVersOptional =
        exisitngWpDefMap.values().stream().filter(A2lWpDefnVersion::isActive).findFirst();

    Map<Long, A2lWpResponsibility> wpRespMap =
        new A2lWpResponsibilityLoader(getServiceData()).getWpRespForWpDefnVers(workingSet.getId()).getWpRespMap();
    // if only default wp resp is present , back up version is not created
    if (wpRespMap.size() != DEFAULT_A2L_WP_RESP_COUNT) {
      A2lWpDefnVersion backUpVers = new A2lWpDefnVersion();
      backUpVers.setVersionName("Backup of Working Set before take over from A2L");
      backUpVers.setActive(false);
      backUpVers.setParamLevelChgAllowedFlag(workingSet.isParamLevelChgAllowedFlag());
      backUpVers.setPidcA2lId(workingSet.getPidcA2lId());
      backUpVers.setWorkingSet(false);
      A2lWpDefinitionVersionCommand cmd =
          new A2lWpDefinitionVersionCommand(getServiceData(), backUpVers, false, false, false);
      // to create version using procedure
      cmd.setNewVersionProc(true);
      // If active version is available in Destination A2l, For Back up version - Take over the WP Finished Status from
      // active version
      if (activerVersOptional.isPresent()) {
        cmd.setWpDefnVersForFinStatusTakeOver(activerVersOptional.get());
      }
      executeChildCommand(cmd);
      cmd.refreshEntity();
      this.commandSet.add(cmd);
    }
  }

  /**
   * @param wpDefVer
   * @param existingWpDef
   * @param wsName
   * @param createActiveVersion
   * @throws IcdmException
   */
  private void createDefaultWpDefVerCommand(final A2lWpDefnVersion wpDefVer, final boolean existingWpDef,
      final String wsName, final boolean createActiveVersion)
      throws IcdmException {
    A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();

    a2lWpDefnVersion.setVersionName(wsName);
    a2lWpDefnVersion.setDescription(wpDefVer.getDescription());
    a2lWpDefnVersion.setActive(false);
    a2lWpDefnVersion.setWorkingSet(true);
    a2lWpDefnVersion.setParamLevelChgAllowedFlag(wpDefVer.isParamLevelChgAllowedFlag());
    a2lWpDefnVersion.setPidcA2lId(this.inputData.getDescPidcA2lId());
    a2lWpDefnVersion.setVersionNumber(0L);
    A2lWpDefaultDefinitionCommand wpDefVerCmd =
        new A2lWpDefaultDefinitionCommand(getServiceData(), a2lWpDefnVersion, existingWpDef, createActiveVersion, true);

    executeChildCommand(wpDefVerCmd);
    wpDefVerCmd.refreshEntity();
    this.destWpDefVer = wpDefVerCmd.getNewData();
    this.commandSet.add(wpDefVerCmd);
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
  public void refreshEntity() {
    // Refresh the entity after the creation, as the child records are created via stored procedure
    A2lWpDefnVersionLoader ldr = new A2lWpDefnVersionLoader(getServiceData());
    TA2lWpDefnVersion entity = ldr.getEntityObject(this.destWpDefVer.getId());
    getEm().refresh(entity);
    List<TA2lWpResponsibility> a2lWpRespList = entity.getTA2lWpResponsibility();

    for (TA2lWpResponsibility ta2lWpResponsibility : a2lWpRespList) {
      for (TA2lWpParamMapping ta2lWpParamMapping : ta2lWpResponsibility.getTA2lWpParamMappings()) {
        ta2lWpParamMapping.getTA2lResponsibility();
        ta2lWpParamMapping.getTA2lWpResponsibility();
        ta2lWpParamMapping.getTParameter();
        getEm().refresh(ta2lWpParamMapping);
      }
    }
  }


  /**
   * <ol>
   * <li>This method gets all the params whose Wp - Resp status is Finished</li>
   * <li>Then params and it's corresponding function and function version is compared between source and destination A2L
   * files.</li>
   * <li>If Function or Function version is changed between source and destination A2L params, then mark the
   * corresponding Wp - Resp as Not - Finished.</li>
   * </ol>
   *
   * @param wpDefVersion
   * @throws IcdmException
   */
  private void updateWpStatusIfParamFuncChanged(final Long wpDefVersion) throws IcdmException {
    Timer timer = new Timer();
    // Find the Parameters with the Function version mismatch
    Set<String> paramsWithFuncVersMismatch = findFuncVersMimatchForParams();

    if (CommonUtils.isNotEmpty(paramsWithFuncVersMismatch)) {
      A2lWpRespStatusUpdationModel statusUpdationModel = new A2lWpRespStatusUpdationModel();
      Map<Long, A2lWpResponsibilityStatus> wpRespStatusToBeUpdated = new HashMap<>();

      // Get all Parameter Mappings for the Wp Definiton Version
      TypedQuery<TA2lWpParamMapping> queryForParamMappings =
          getEm().createNamedQuery(TA2lWpParamMapping.GET_WP_PARAM_MAPPING_FOR_WP_DEFN_VERS, TA2lWpParamMapping.class);
      queryForParamMappings.setParameter("wpDefnVersId", wpDefVersion);
      // Iterate the Parameter Mappings
      for (TA2lWpParamMapping paramMapping : queryForParamMappings.getResultList()) {
        // If the Paramter's function version is changed, then for the corresponding A2lWpResponsibility's WP RESP
        // STATUS should be reset to 'NOT_FINISHED'
        if (paramsWithFuncVersMismatch.contains(paramMapping.getTParameter().getName())) {

          Set<A2lWpResponsibilityStatus> wpRespStatusSet = new A2lWpResponsibilityStatusLoader(getServiceData())
              .getA2lWpRespStatusBasedOnWPRespId(paramMapping.getTA2lWpResponsibility().getWpRespId());
          for (A2lWpResponsibilityStatus wpRespStatus : wpRespStatusSet) {
            if (CommonUtils.isEqual(wpRespStatus.getWpRespFinStatus(), WP_RESP_STATUS_TYPE.FINISHED.getDbType())) {
              wpRespStatus.setWpRespFinStatus(WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
              wpRespStatusToBeUpdated.put(wpRespStatus.getId(), wpRespStatus);
            }
          }
        }
      }
      resetWpRespStatusForParamFuncChanged(statusUpdationModel, wpRespStatusToBeUpdated);
    }
    getLogger().debug("Time taken in updateWpStatusIfParamFuncChanged Method is {} ms", timer.getElapsedTimeMs());
    timer.finish();

  }

  private void resetWpRespStatusForParamFuncChanged(final A2lWpRespStatusUpdationModel statusUpdationModel,
      final Map<Long, A2lWpResponsibilityStatus> wpRespStatusToBeUpdated)
      throws IcdmException {
    if (CommonUtils.isNotEmpty(wpRespStatusToBeUpdated)) {
      statusUpdationModel.setA2lWpRespStatusToBeUpdatedMap(wpRespStatusToBeUpdated);
      A2lWpRespStatusUpdationCommand updationCommand =
          new A2lWpRespStatusUpdationCommand(getServiceData(), statusUpdationModel);
      executeChildCommand(updationCommand);
    }
  }

  private Set<String> findFuncVersMimatchForParams() {

    Set<String> paramsWithFuncVersMismatch = new HashSet<>();

    for (Entry<String, Characteristic> destCharEntry : this.destFileLabels.entrySet()) {
      String paramName = destCharEntry.getKey();
      if (CommonUtils.isNotNull(this.sourceFileLabels.get(paramName))) {
        Characteristic destCharacteristic = destCharEntry.getValue();
        Characteristic srcCharacteristic = this.sourceFileLabels.get(paramName);
        if (isParamFuncVersMismatched(destCharacteristic, srcCharacteristic)) {
          paramsWithFuncVersMismatch.add(paramName);
        }
      }
    }

    return paramsWithFuncVersMismatch;
  }

  /**
   * @param destCharacteristic
   * @param srcCharacteristic
   * @return
   */
  private boolean isParamFuncVersMismatched(Characteristic destCharacteristic, Characteristic srcCharacteristic) {
    return !(CommonUtils.isNotNull(srcCharacteristic.getDefFunction()) &&
        CommonUtils.isNotNull(destCharacteristic.getDefFunction()) &&
        CommonUtils.isEqual(srcCharacteristic.getDefFunction().getName(),
            destCharacteristic.getDefFunction().getName()) &&
        CommonUtils.isEqual(srcCharacteristic.getDefFunction().getFunctionVersion(),
            destCharacteristic.getDefFunction().getFunctionVersion()));
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
    return new HashSet<>(this.commandSet);
  }

  /**
   * @return the isCopyToWkgSet
   */
  public boolean isCopyToWkgSet() {
    return this.isCopyToWkgSet;
  }

}
