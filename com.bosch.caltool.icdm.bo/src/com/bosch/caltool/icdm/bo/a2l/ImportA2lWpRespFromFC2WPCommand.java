package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPMappingLoader;
import com.bosch.caltool.icdm.common.bo.a2l.A2lObjectIdentifierValidator;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespInput;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;


/**
 * The Class ImportA2lWpRespCommand.
 */
public class ImportA2lWpRespFromFC2WPCommand extends AbstractSimpleCommand {

  /**
   * Count of default wp resp in a2l
   */
  private static final int DEFAULT_A2L_WP_RESP_COUNT = 1;

  /** The wp def vers id. */
  private final Long wpDefVersId;

  /** The variant grp id. */
  private final Long variantGrpId;

  /** The a 2 l file id. */
  private final Long a2lFileId;

  /** The fc 2 wp vers id. */
  private final Long fc2wpVersId;

  /** The can create param mapping. */
  private final boolean canCreateParamMapping;

  /** The func to A 2 l wpresp pal map. */
  private HashMap<String, A2lWpResponsibility> funcToA2lWpRespPalMap;

  /** The fc 2 wp map. */
  private Map<String, WorkPackageDivision> funcToWp;


  private final Long pidcVersId;

  private final Set<A2lWorkPackageCommand> a2lWpCmdSet = new HashSet<>();

  private final Set<A2lWpParamMappingCommand> a2lWpParamMappingSet = new HashSet<>();

  private final Set<A2lWpResponsibilityCommand> wpRespPalSet = new HashSet<>();

  private final Set<A2lResponsibilityCommand> respSet = new HashSet<>();

  private final Set<A2lWpDefinitionVersionCommand> wpDefnVerSet = new HashSet<>();

  private final Set<String> skippedParams = new HashSet<>();

  /** true - if new change is available */
  private boolean isNewChangeAvailable;

  private boolean isPidcQanireConfigBEG = false;

  /**
   * Instantiates a new import A2L wp resp command - FC2WP Input.
   *
   * @param serviceData the service data
   * @param fc2wpInput the input
   * @throws IcdmException the icdm exception
   */
  public ImportA2lWpRespFromFC2WPCommand(final ServiceData serviceData, final ImportA2lWpRespInput fc2wpInput)
      throws IcdmException {
    this(serviceData, fc2wpInput, false);
  }

  /**
   * Instantiates a new import A2L wp resp command - FC2WP Input.
   *
   * @param serviceData the service data
   * @param fc2wpInput the input
   * @param isPidcQanireConfigBEG set to true if the pidc qnaire config attr value is BEG
   * @throws IcdmException the icdm exception
   */
  public ImportA2lWpRespFromFC2WPCommand(final ServiceData serviceData, final ImportA2lWpRespInput fc2wpInput,
      final boolean isPidcQanireConfigBEG) throws IcdmException {
    super(serviceData);
    this.wpDefVersId = fc2wpInput.getWpDefVersId();
    this.variantGrpId = fc2wpInput.getVariantGrpId();
    this.a2lFileId = fc2wpInput.getA2lFileId();
    this.fc2wpVersId = fc2wpInput.getFc2wpVersId();
    this.pidcVersId = fc2wpInput.getPidcVersionId();
    this.canCreateParamMapping = fc2wpInput.isCreateParamMapping();
    this.isPidcQanireConfigBEG = isPidcQanireConfigBEG;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    validateInputs();

    A2lWpDefnVersion a2lWpDefnVersion =
        new A2lWpDefnVersionLoader(getServiceData()).getDataObjectByID(this.wpDefVersId);

    if (!a2lWpDefnVersion.isWorkingSet()) {
      throw new IcdmException("Import is possible only in Working Set WP Definition version.");
    }
    // create a version as a backup of working set
    createBackUpVers(a2lWpDefnVersion);
    // Identify Function-to-Workpackage for import - Applicable for FC2WP
    loadFC2WPActiveVersion();

    // Import Workpackage to A2lWpResponsibility
    createA2lWpRespPalForFC2WP();

    // Create new A2l Wp-Resp Param mapping for parameters under the selected function
    if (this.canCreateParamMapping) {
      Map<String, Set<A2lWpParamMapping>> importParamObjs = getA2lParamForImport(this.funcToWp.keySet());
      createA2lWpParamMappingForFC2WP(importParamObjs);
    }
    if (!this.isNewChangeAvailable) {
      throw new IcdmException(ApicConstants.ERRCODE_FC2WP_IMPORT_A2L_WP_ALREADY_EXISTS);
    }
  }


  /**
   * @param input ImportA2lWpRespInput
   * @throws InvalidInputException
   */
  private void validateInputs() throws InvalidInputException {
    if (CommonUtils.isNull(this.a2lFileId) || CommonUtils.isNull(this.fc2wpVersId) ||
        CommonUtils.isNull(this.wpDefVersId) || CommonUtils.isNull(this.pidcVersId)) {
      throw new InvalidInputException("Missing parameter(s) in input");
    }
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
      A2lWpDefnVersion backUpVers = new A2lWpDefnVersion();
      backUpVers.setVersionName("Backup of Working Set before FC2WP Import");
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
      getWpDefnVerSet().add(cmd);
    }
  }

  /**
   * Gets the a 2 l param for import.
   *
   * @param functionSet the function set
   * @return the a 2 l param for import
   * @throws IcdmException
   */
  private Map<String, Set<A2lWpParamMapping>> getA2lParamForImport(final Set<String> functionSet) throws IcdmException {
    // Step 2 : Get list of parameters to update based on fc2wp function list
    A2lWpParamMappingLoader a2lMapLdr = new A2lWpParamMappingLoader(getServiceData());
    Map<String, Set<A2lWpParamMapping>> importObjs = a2lMapLdr.getParamByFunction(this.a2lFileId, functionSet);
    getLogger().info("Import WP-Resp : Get No. of parameters to be updated with wp-resp : " + importObjs.size());
    return importObjs;
  }

  /**
   * Load FC 2 WP active version.
   *
   * @throws DataException the data exception
   */
  private void loadFC2WPActiveVersion() throws DataException {
    // Step 1 : Get FC2WP mapping based on active version
    FC2WPMappingLoader fc2wpLdr = new FC2WPMappingLoader(getServiceData());
    this.funcToWp = fc2wpLdr.getFc2WpByA2lNVersion(this.a2lFileId, this.fc2wpVersId);

    getLogger().info("Import WP-Resp : Get FC2WP mapping based on active version : " + this.fc2wpVersId);
    Set<String> functionSet = this.funcToWp.keySet();
    getLogger().info("Import WP-Resp : No. of functions to be imported : " + functionSet.size());
  }

  /**
   * Creates the A 2 l wp param mapping.
   *
   * @param importParamObjs the import param objs
   * @throws IcdmException the icdm exception
   */
  private void createA2lWpParamMappingForFC2WP(final Map<String, Set<A2lWpParamMapping>> importParamObjs)
      throws IcdmException {
    // Step 4 : Create entry in a2l param mapping table for all labels identified
    A2lWpParamMappingLoader a2lWpParamMappingLdr = new A2lWpParamMappingLoader(getServiceData());
    Map<Long, Map<Long, A2lWpParamMapping>> existingMapping =
        a2lWpParamMappingLdr.getAllMappingByWpDefVersId(this.wpDefVersId);
    A2lWpResponsibility defaultWpRespPal =
        new A2lWpResponsibilityLoader(getServiceData()).getDefaultWpRespPal(this.wpDefVersId);
    ParamMappingRespBo destMappingBo = a2lWpParamMappingLdr.getParamMapRespBo(this.wpDefVersId);
    for (Map.Entry<String, Set<A2lWpParamMapping>> entry : importParamObjs.entrySet()) {
      String funcName = entry.getKey();
      A2lWpResponsibility a2lWpResponsibility =
          this.funcToA2lWpRespPalMap.get(funcName.toUpperCase(Locale.getDefault()));
      if (a2lWpResponsibility == null) {
        getLogger().error(
            "A2l Resp PAL definition could not be retrieved/created for the given function-workpackage, hence will not be imported : " +
                funcName);
        addParamsToSkip(entry);

        continue;
      }
      getLogger().info("Import WP-Resp :  A2l Parameters to be mapped with WP-Resp for the function : " + funcName);
      for (A2lWpParamMapping sourceMapping : entry.getValue()) {
        if (existingMapping.containsKey(sourceMapping.getParamId())) {
          Map<Long, A2lWpParamMapping> paramMappingMap = existingMapping.get(sourceMapping.getParamId());
          if (this.variantGrpId == null) {
            checkNUpdateDefaultLevelMapping(defaultWpRespPal, destMappingBo, a2lWpResponsibility, paramMappingMap);
          }
          // if mappings are not available for the param at var grp level , then create a mapping
          else {
            createMappingAtVarGrpLevel(destMappingBo, a2lWpResponsibility, sourceMapping, paramMappingMap);
          }
        }
      }

      getLogger().debug(funcName + "Import WP-Resp : No. of A2l WP-Resp Parameters imported for the function : " +
          funcName + " is : " + entry.getValue().size());
    }
    getLogger()
        .info("Import WP-Resp : Total No. of A2lWpParamMapping objects imported : " + this.a2lWpParamMappingSet.size());
  }

  /**
   * @param destMappingBo
   * @param a2lWpResponsibility
   * @param sourceMapping
   * @param paramMappingMap
   * @throws IcdmException
   */
  private void createMappingAtVarGrpLevel(final ParamMappingRespBo destMappingBo,
      final A2lWpResponsibility a2lWpResponsibility, final A2lWpParamMapping sourceMapping,
      final Map<Long, A2lWpParamMapping> paramMappingMap)
      throws IcdmException {
    boolean canInsert = true;
    for (A2lWpParamMapping destMapping : paramMappingMap.values()) {
      A2lWpResponsibility destWpResp = destMappingBo.getWpRespMap().get(destMapping.getWpRespId());
      // if var grp id is null , then check default level mappings and update if needed

      if ((this.variantGrpId.equals(destWpResp.getVariantGrpId()))) {
        canInsert = false;
        break;
      }
    }
    if (canInsert) {
      insertWpMapping(sourceMapping, a2lWpResponsibility);
    }
  }

  /**
   * @param defaultWpRespPal
   * @param destMappingBo
   * @param a2lWpResponsibility
   * @param paramMappingMap
   * @throws IcdmException
   */
  private void checkNUpdateDefaultLevelMapping(final A2lWpResponsibility defaultWpRespPal,
      final ParamMappingRespBo destMappingBo, final A2lWpResponsibility a2lWpResponsibility,
      final Map<Long, A2lWpParamMapping> paramMappingMap)
      throws IcdmException {
    for (A2lWpParamMapping destMapping : paramMappingMap.values()) {
      A2lWpResponsibility destWpResp = destMappingBo.getWpRespMap().get(destMapping.getWpRespId());
      // if var grp id is null , then check default level mappings and update if needed

      if ((destWpResp.getVariantGrpId() == null) &&
          (destMapping.getWpRespId().longValue() == defaultWpRespPal.getId())) {
        destMapping.setWpRespId(a2lWpResponsibility.getId());
        A2lWpParamMappingCommand cmd = new A2lWpParamMappingCommand(getServiceData(), destMapping, true, false);
        executeChildCommand(cmd);
        this.a2lWpParamMappingSet.add(cmd);
        this.isNewChangeAvailable = true;
      }
    }
  }

  /**
   * @param entry
   */
  private void addParamsToSkip(final Map.Entry<String, Set<A2lWpParamMapping>> entry) {
    for (A2lWpParamMapping importObj : entry.getValue()) {
      this.skippedParams.add(String.valueOf(importObj.getParamId()));
    }
  }

  /**
   * @param sourceWpParMapping
   * @param a2lWpResponsibility
   */
  private void insertWpMapping(final A2lWpParamMapping sourceWpParMapping,
      final A2lWpResponsibility a2lWpResponsibility)
      throws IcdmException {
    A2lWpParamMapping destParamMapping = new A2lWpParamMapping();
    destParamMapping.setDescription(sourceWpParMapping.getDescription());

    destParamMapping.setParamId(sourceWpParMapping.getParamId());

    destParamMapping.setWpRespId(a2lWpResponsibility.getId());
    destParamMapping.setWpRespInherited(true);


    A2lWpParamMappingCommand wpParMapCmd =
        new A2lWpParamMappingCommand(getServiceData(), destParamMapping, false, false);
    executeChildCommand(wpParMapCmd);
    this.isNewChangeAvailable = true;
    this.a2lWpParamMappingSet.add(wpParMapCmd);

  }

  /**
   * Creates the A 2 l wp resp pal.
   *
   * @throws IcdmException the icdm exception
   */
  private void createA2lWpRespPalForFC2WP() throws IcdmException {
    // Step 2.1 Check if A2lWpResponsibility already exists, if so, do not create new one

    A2lWorkPackageLoader wpPalLoader = new A2lWorkPackageLoader(getServiceData());
    Map<String, A2lWorkPackage> existingWpPals = wpPalLoader.getByPidcVersionId(this.pidcVersId);

    A2lWpResponsibilityLoader respPalLdr = new A2lWpResponsibilityLoader(getServiceData());
    Map<String, Map<Long, A2lWpResponsibility>> existingWps =
        respPalLdr.getAllWpRespPalForWpDefnVers(this.wpDefVersId, this.variantGrpId);

    Map<String, Map<Long, A2lWpResponsibility>> defaultLevelWps = getDefaultWps(respPalLdr);

    Map<String, A2lWpResponsibility> insertedWps = new HashMap<>();
    this.funcToA2lWpRespPalMap = new HashMap<>();

    Map<String, A2lResponsibility> rbAndBEGRespMap = new A2lResponsibilityLoader(getServiceData())
        .getRBAndBEGResp(new PidcVersionLoader(getServiceData()).getDataObjectByID(this.pidcVersId).getPidcId());

    A2lResponsibility defaultBoschRespType = rbAndBEGRespMap.get(ApicConstants.ALIAS_NAME_RB);

    A2lResponsibility begRespType = getBegRespType(rbAndBEGRespMap);

    for (Entry<String, WorkPackageDivision> entry : this.funcToWp.entrySet()) {

      String function = entry.getKey();
      A2lWpResponsibility respPal = new A2lWpResponsibility();
      A2lWorkPackage wpPalObj = new A2lWorkPackage();
      respPal.setWpDefnVersId(this.wpDefVersId);

      // If FC2WP-WP name exceeds 150 characters - truncate it before saving
      String wpName = entry.getValue().getWpName();
      wpName = getValidatedWpName(wpName);

      A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
      String modifiedWpName = validator.replaceInvalidChars(wpName);

      // if first letter is a digit, we add an underscore at the beginning
      modifiedWpName = Character.isDigit(modifiedWpName.charAt(0)) ? "_" + modifiedWpName : modifiedWpName;

      String wpNameUpper = modifiedWpName.toUpperCase(Locale.getDefault());

      boolean wpPalExists = existingWpPals.containsKey(wpNameUpper);

      wpPalObj = getOrCreateA2lWp(existingWpPals, wpPalObj, modifiedWpName, wpNameUpper, wpPalExists);

      // Check if already exists
      boolean workpackageExists = existingWps.containsKey(wpPalObj.getName().toUpperCase()) ||
          insertedWps.containsKey(wpPalObj.getName().toUpperCase());

      if (workpackageExists) {
        this.funcToA2lWpRespPalMap.put(function.toUpperCase(Locale.getDefault()), existingWps.containsKey(wpNameUpper)
            ? getExistingPalObj(existingWps.get(wpNameUpper)) : insertedWps.get(wpNameUpper));
      }
      else {
        if ((defaultLevelWps != null) && defaultLevelWps.containsKey(wpPalObj.getName().toUpperCase())) {
          return;
        }
        respPal.setA2lWpId(existingWpPals.get(wpPalObj.getName().toUpperCase()).getId());

        setResponsibility(defaultBoschRespType, begRespType, respPal);
        respPal.setVariantGrpId(this.variantGrpId);
        A2lWpResponsibilityCommand cmd = new A2lWpResponsibilityCommand(getServiceData(), respPal, false, false);
        executeChildCommand(cmd);
        this.wpRespPalSet.add(cmd);
        this.isNewChangeAvailable = true;
        this.funcToA2lWpRespPalMap.put(function.toUpperCase(Locale.getDefault()), cmd.getNewData());
        insertedWps.put(wpNameUpper, cmd.getNewData());
      }
    }

    getLogger().info("Import WP-Resp : Total No. of newly created A2lWpResponsibility objects imported : {}",
        insertedWps.size());
    getLogger().info("Import WP-Resp : Total No. of A2lWpResponsibility objects imported : {}",
        this.funcToA2lWpRespPalMap.size());

  }

  /**
   * @param wpName
   * @return
   */
  private String getValidatedWpName(final String wpName) {
    return wpName.length() > 128 ? wpName.substring(0, 127) : wpName;
  }

  /**
   * @param rbAndBEGRespMap
   * @return
   */
  private A2lResponsibility getBegRespType(final Map<String, A2lResponsibility> rbAndBEGRespMap) {
    return this.isPidcQanireConfigBEG ? rbAndBEGRespMap.get(ApicConstants.ALIAS_NAME_RB_BEG) : null;
  }

  /**
   * @param existingWpPals
   * @param wpPalObj
   * @param modifiedWpName
   * @param wpNameUpper
   * @param wpPalExists
   * @return
   * @throws IcdmException
   */
  private A2lWorkPackage getOrCreateA2lWp(final Map<String, A2lWorkPackage> existingWpPals,
      final A2lWorkPackage wpPalObj, final String modifiedWpName, final String wpNameUpper, final boolean wpPalExists)
      throws IcdmException {
    if (wpPalExists) {
      return existingWpPals.get(wpNameUpper);
    }

    wpPalObj.setName(modifiedWpName);
    wpPalObj.setPidcVersId(this.pidcVersId);
    A2lWorkPackageCommand wpPalCmd = new A2lWorkPackageCommand(getServiceData(), wpPalObj, false, false);
    executeChildCommand(wpPalCmd);
    this.a2lWpCmdSet.add(wpPalCmd);
    this.isNewChangeAvailable = true;
    existingWpPals.put(wpNameUpper, wpPalCmd.getNewData());

    return wpPalObj;
  }

  /**
   * @param defaultBoschRespType
   * @param begRespType
   * @param respPal
   */
  private void setResponsibility(final A2lResponsibility defaultBoschRespType, final A2lResponsibility begRespType,
      final A2lWpResponsibility respPal) {
    // Set default responsibility to BOSCH
    if (this.isPidcQanireConfigBEG && (null != begRespType)) {
      respPal.setA2lRespId(begRespType.getId());
    }
    else {
      respPal.setA2lRespId(defaultBoschRespType.getId());
    }
  }

  /**
   * @param respPalLdr
   * @param defaultLevelWps
   * @return
   * @throws DataException
   */
  private Map<String, Map<Long, A2lWpResponsibility>> getDefaultWps(final A2lWpResponsibilityLoader respPalLdr)
      throws DataException {
    Map<String, Map<Long, A2lWpResponsibility>> defaultLevelWps = null;
    if (this.canCreateParamMapping && (this.variantGrpId != null)) {
      return respPalLdr.getAllWpRespPalForWpDefnVers(this.wpDefVersId, null);
    }
    return defaultLevelWps;
  }

  /**
   * Gets the existing resp obj.
   *
   * @param map the map
   * @param defaultBoschRespType the default bosch resp type
   * @param variantGrpId the variant grp id
   * @return the existing resp obj
   * @throws DataException the data exception
   */
  private A2lWpResponsibility getExistingPalObj(final Map<Long, A2lWpResponsibility> map) {
    A2lWpResponsibility a2lWpResp = null;
    for (A2lWpResponsibility obj : map.values()) {
      // Ignore the default type (BOSCH without user details) - return only 'BOSCH with details' to be overwritten
      if (checkWpRespHasVariantGrp(obj)) {
        a2lWpResp = obj;
        break;
      }
    }
    return a2lWpResp;
  }

  /**
   * Check wp resp has variant grp.
   *
   * @param obj the obj
   * @param variantGrpId the variant grp id
   * @return true, if successful
   */
  private boolean checkWpRespHasVariantGrp(final A2lWpResponsibility obj) {
    if ((this.variantGrpId == null) && (obj.getVariantGrpId() == null)) {
      return true;
    }
    return obj.getVariantGrpId() == null ? false
        : (this.variantGrpId != null) && (obj.getVariantGrpId().longValue() == this.variantGrpId.longValue());
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


  /**
   * @return the a2lWpParamMappingSet
   */
  public Set<A2lWpParamMappingCommand> getA2lWpParamMappingSet() {
    return new HashSet<>(this.a2lWpParamMappingSet);
  }


  /**
   * @return the wpRespPalSet
   */
  public Set<A2lWpResponsibilityCommand> getWpRespPalSet() {
    return new HashSet<>(this.wpRespPalSet);
  }


  /**
   * @return the respSet
   */
  public Set<A2lResponsibilityCommand> getRespSet() {
    return new HashSet<>(this.respSet);
  }

  /**
   * @return the skippedParams
   */
  public Set<String> getSkippedParams() {
    return new HashSet<>(this.skippedParams);
  }


  /**
   * @return the a2lWpCmdSet
   */
  public Set<A2lWorkPackageCommand> getA2lWpCmdSet() {
    return new HashSet<>(this.a2lWpCmdSet);
  }

  /**
   * @return the wpDefnVerSet
   */
  public Set<A2lWpDefinitionVersionCommand> getWpDefnVerSet() {
    return this.wpDefnVerSet;
  }


}
