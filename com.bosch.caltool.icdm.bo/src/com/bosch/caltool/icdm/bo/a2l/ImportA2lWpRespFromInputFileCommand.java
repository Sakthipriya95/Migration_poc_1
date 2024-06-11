package com.bosch.caltool.icdm.bo.a2l;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.UserDetailsLoader;
import com.bosch.caltool.icdm.bo.user.UserDetails;
import com.bosch.caltool.icdm.common.bo.a2l.A2lObjectIdentifierValidator;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;
import com.bosch.caltool.icdm.model.a2l.Par2Wp;
import com.bosch.caltool.icdm.model.a2l.VarGrp2Wp;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * The Class ImportA2lWpRespCommand.
 */
public class ImportA2lWpRespFromInputFileCommand extends AbstractSimpleCommand {

  /**
   * Count of default wp resp in a2l
   */
  private static final int DEFAULT_A2L_WP_RESP_COUNT = 1;

  /**
   * By default trucating it to 128 character. Since prefix _WP__ (5 charaters) will be appended truncating it to 123
   * character
   */
  private static final int MAX_WP_NAME_LENGTH = 123;

  /**
   * Max length allowed in DB is 255 (Since prefix _RESP__ (7 charaters) will be appended truncating it to 248 character
   */
  private static final int MAX_RESP_NAME_LENGTH = 248;


  /** The wp def vers id. */
  private final Long wpDefVersId;


  private final ImportA2lWpRespData input;

  private final Set<A2lWpResponsibility> existingWpRespWithDiffResp = new HashSet<>();

  private Map<String, A2lWorkPackage> existingWpMap;

  private Map<Long, A2lResponsibility> a2lRespMap;

  private Map<String, Map<Long, A2lWpResponsibility>> existingWpRespMap;

  private Map<Long, Map<Long, A2lWpParamMapping>> existingParamMapping;

  private Long pidcId;

  private final Set<A2lWorkPackageCommand> a2lWpCommandSet = new HashSet<>();

  private final Set<A2lWpParamMappingCommand> a2lWpParamMappingSet = new HashSet<>();

  private final Set<A2lWpResponsibilityCommand> wpRespPalSet = new HashSet<>();

  private final Set<A2lResponsibilityCommand> respSet = new HashSet<>();

  private final Set<A2lWpDefinitionVersionCommand> wpDefnVerSet = new HashSet<>();

  private final Set<String> skippedParams = new HashSet<>();


  /**
   * Instantiates a new import A 2 l wp resp command.
   *
   * @param serviceData the service data
   * @param input the input
   * @param response
   * @throws IcdmException the icdm exception
   */
  public ImportA2lWpRespFromInputFileCommand(final ServiceData serviceData, final ImportA2lWpRespData input)
      throws IcdmException {
    super(serviceData);
    this.wpDefVersId = input.getWpDefVersId();
    this.input = input;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    A2lWpDefnVersion a2lWpDefnVersion =
        new A2lWpDefnVersionLoader(getServiceData()).getDataObjectByID(this.wpDefVersId);
    if (!a2lWpDefnVersion.isWorkingSet()) {
      throw new IcdmException("Import is possible only in Working Set WP Definition version.");
    }
    createBackUpVers(a2lWpDefnVersion);
    A2lWpParamMappingLoader a2lWpParamMappingLdr = new A2lWpParamMappingLoader(getServiceData());
    this.existingParamMapping = a2lWpParamMappingLdr.getAllMappingByWpDefVersId(this.input.getWpDefVersId());
    A2lWpResponsibilityLoader respPalLdr = new A2lWpResponsibilityLoader(getServiceData());
    this.existingWpRespMap = respPalLdr.getAllWpRespPalForWpDefnVers(this.wpDefVersId, null);
    this.pidcId = new PidcVersionLoader(getServiceData()).getDataObjectByID(this.input.getPidcVersionId()).getPidcId();
    A2lResponsibilityModel a2lRespModel = new A2lResponsibilityLoader(getServiceData()).getByPidc(this.pidcId);

    this.a2lRespMap = a2lRespModel.getA2lResponsibilityMap();
    this.existingWpMap = new A2lWorkPackageLoader(getServiceData()).getByPidcVersionId(this.input.getPidcVersionId());

    // Import Workpackage to A2lWpResponsibility
    if (CommonUtils.isNotEmpty(this.input.getVarGrp2WpRespSet())) {
      createA2lWpResp();
    }

    // Create new A2l Wp-Resp Param mapping for parameters under the selected function
    if (CommonUtils.isNotEmpty(this.input.getParamWpRespMap())) {
      createA2lWpParamMapping();
      // set param level changed flag to true if wp exits with different resp in excel while importing parameter
      // assignment
      if (CommonUtils.isNotEmpty(this.existingWpRespWithDiffResp)) {
        updateParamLevelChgAllowedFlag(a2lWpDefnVersion);
      }
    }
    if (getA2lWpParamMappingSet().isEmpty() && getWpRespPalSet().isEmpty() && getRespSet().isEmpty()) {
      throw new IcdmException("FILE_IMPORT_TO_A2L_WP.WP_ALREADY_EXISTS");
    }
  }

  /**
   * @param a2lWpDefnVersion working set version
   * @throws IcdmException
   */
  private void updateParamLevelChgAllowedFlag(final A2lWpDefnVersion a2lWpDefnVersion) throws IcdmException {
    a2lWpDefnVersion.setParamLevelChgAllowedFlag(true);
    A2lWpDefinitionVersionCommand cmd =
        new A2lWpDefinitionVersionCommand(getServiceData(), a2lWpDefnVersion, true, false, false);
    executeChildCommand(cmd);
    cmd.getNewData();
    this.wpDefnVerSet.add(cmd);
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
      backUpVers.setVersionName("Backup of Working Set before Excel Import");
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
      this.wpDefnVerSet.add(cmd);
    }
  }

  /**
   * creat a2l param mapping
   */
  private void createA2lWpParamMapping() throws IcdmException {

    Map<String, A2lResponsibility> newlyCreatedRespNames = new HashMap<>();

    Collection<Par2Wp> excelParWp = this.input.getParamWpRespMap().values();
    for (Par2Wp parWp : excelParWp) {

      String modifiedWpName = parWp.getWpName();
      modifiedWpName = modifiedWpName.length() > MAX_WP_NAME_LENGTH ? modifiedWpName.substring(0, MAX_WP_NAME_LENGTH)
          : modifiedWpName;

      // Modify new workpackage name based on A2l Specifications
      if (null == this.existingWpRespMap.get(modifiedWpName.toUpperCase(Locale.getDefault()))) {
        modifiedWpName = modifyWpName(modifiedWpName);
      }

      String excelRespName = parWp.getRespName();
      excelRespName = excelRespName.length() > MAX_RESP_NAME_LENGTH ? excelRespName.substring(0, MAX_RESP_NAME_LENGTH)
          : excelRespName;
      String respName = excelRespName;

      // Modify new responsibility alias name based on A2l Specifications
      A2lResponsibility checkIfSameA2lRespExists = checkIfSameA2lRespExists(respName);
      if (null == checkIfSameA2lRespExists) {
        respName = modifyRespName(respName);
      }

      /**
       * check if wp mapped to param already exists in the variant group 1. if same wp exists check if resp are same ,
       * if resp are different overwrite the resp only at the param level 2. if wp doesnt exist , create a new wp-resp
       */

      A2lWpResponsibility wpRespPal = checkIfWpExists(modifiedWpName, respName, parWp.getVarGrpId());
      // if wp-def is not available at the var group level , check in defaul level.
      if ((wpRespPal == null) && checkIfWpExistsInDefaultLevel(modifiedWpName)) {
        /**
         * if same name wp exists in the default level, skip the param mapping. Coz same wp if created at variant level
         * will affect all other params mapped to default level wp as well
         */
        this.skippedParams.add(parWp.getParamName());
        continue;
      }
      A2lResponsibility importedRespObj = null;

      // if wp doesnt exist for same variant group
      boolean excelWpRespTypeColAvail = parWp.isExcelWPRespTypeColAvail();
      if ((wpRespPal == null)) {
        importedRespObj = getOrCreateResp(newlyCreatedRespNames, respName, excelRespName, parWp.getRespTypeCode(),
            excelWpRespTypeColAvail);
        if (!modifiedWpName.equalsIgnoreCase(ApicConstants.DEFAULT_A2L_WP_NAME)) {
          wpRespPal = createWpRespCmd(parWp.getVarGrpId(), modifiedWpName, importedRespObj, parWp.getWpName());
        }
      }
      else {
        if (this.existingWpRespWithDiffResp.contains(wpRespPal)) {
          importedRespObj = getOrCreateResp(newlyCreatedRespNames, respName, excelRespName, parWp.getRespTypeCode(),
              excelWpRespTypeColAvail);
        }
        else {
          // if there are no changes in WP mappings and there are changes only in responsibility types
          checkIfSameA2lRespExists = checkIfSameA2lRespExists(respName);
          if ((null != checkIfSameA2lRespExists) && excelWpRespTypeColAvail &&
              CommonUtils.isNotEqual(parWp.getRespTypeCode(), checkIfSameA2lRespExists.getRespType())) {
            // update the pidc resp object if they do not have same resp type
            updatePidcRespObj(checkIfSameA2lRespExists, parWp.getRespTypeCode());
          }
          importedRespObj = new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(wpRespPal.getA2lRespId());
        }
      }

      // No need for Updation if the resp pal object is not available - In this case default
      if (wpRespPal != null) {
        Map<Long, A2lWpParamMapping> paramMapping = this.existingParamMapping.get(parWp.getA2lParamId());

        Map<Long, A2lWpResponsibility> respMap = new HashMap<>();
        for (String wrkPkg : this.existingWpRespMap.keySet()) {
          respMap.putAll(this.existingWpRespMap.get(wrkPkg));
        }

        boolean createParamMapping = false;

        // A2l resp corresponding to the new wp defined in excel
        A2lWpResponsibility wpRespInExcel = respMap.get(wpRespPal.getId());


        if ((wpRespInExcel.getVariantGrpId() == null)) {
          for (A2lWpParamMapping a2lParamMapping : paramMapping.values()) {
            A2lWpParamMapping paramMappingInDB = a2lParamMapping.clone();
            A2lWpResponsibility respInDb = respMap.get(a2lParamMapping.getWpRespId());
            if (respInDb.getVariantGrpId() == null) {
              a2lParamMapping.setWpRespId(wpRespPal.getId());
              if (this.existingWpRespWithDiffResp.contains(wpRespPal) &&
                  !importedRespObj.getId().equals(wpRespPal.getA2lRespId())) {
                a2lParamMapping.setWpNameCustInherited(false);
                a2lParamMapping.setWpRespInherited(false);
              }
              else {
                a2lParamMapping.setWpNameCustInherited(true);
                a2lParamMapping.setWpRespInherited(true);
              }
              if (!a2lParamMapping.isWpRespInherited()) {
                a2lParamMapping.setParA2lRespId(importedRespObj.getId());
              }
              if (isA2lWpParamMapObjChanged(paramMappingInDB, a2lParamMapping)) {
                A2lWpParamMappingCommand wpParMapCmd =
                    new A2lWpParamMappingCommand(getServiceData(), a2lParamMapping, true, false);
                executeChildCommand(wpParMapCmd);
                this.a2lWpParamMappingSet.add(wpParMapCmd);
              }
            }
          }
        }

        else {
          createParamMapping = true;
          for (A2lWpParamMapping a2lParamMapping : paramMapping.values()) {
            A2lWpParamMapping pramMappingInDB = a2lParamMapping.clone();
            A2lWpResponsibility respInDb = respMap.get(a2lParamMapping.getWpRespId());
            if ((respInDb != null) && (respInDb.getVariantGrpId() != null) &&
                respInDb.getVariantGrpId().equals(wpRespInExcel.getVariantGrpId())) {

              createParamMapping = false;
              a2lParamMapping.setWpRespId(wpRespPal.getId());
              if (this.existingWpRespWithDiffResp.contains(wpRespPal)) {
                a2lParamMapping.setWpNameCustInherited(false);
                a2lParamMapping.setWpRespInherited(false);
              }
              else {
                a2lParamMapping.setWpNameCustInherited(true);
                a2lParamMapping.setWpRespInherited(true);
              }
              if (!a2lParamMapping.isWpRespInherited()) {
                a2lParamMapping.setParA2lRespId(importedRespObj.getId());
              }
              if (isA2lWpParamMapObjChanged(pramMappingInDB, a2lParamMapping)) {
                A2lWpParamMappingCommand wpParMapCmd =
                    new A2lWpParamMappingCommand(getServiceData(), a2lParamMapping, true, false);
                executeChildCommand(wpParMapCmd);
                this.a2lWpParamMappingSet.add(wpParMapCmd);

              }
            }
          }
          if (createParamMapping) {
            A2lWpParamMapping a2lParamMapping = new A2lWpParamMapping();
            a2lParamMapping.setWpRespId(wpRespPal.getId());
            a2lParamMapping.setParA2lRespId(importedRespObj.getId());
            if (this.existingWpRespWithDiffResp.contains(wpRespPal)) {
              a2lParamMapping.setWpNameCustInherited(false);
              a2lParamMapping.setWpRespInherited(false);
            }
            else {
              a2lParamMapping.setWpNameCustInherited(true);
              a2lParamMapping.setWpRespInherited(true);
            }
            a2lParamMapping.setParamId(parWp.getA2lParamId());
            A2lWpParamMappingCommand wpParMapCmd =
                new A2lWpParamMappingCommand(getServiceData(), a2lParamMapping, false, false);
            executeChildCommand(wpParMapCmd);
            this.a2lWpParamMappingSet.add(wpParMapCmd);

          }
        }
      }

    }
  }

  /**
   * @param paramMappingInDB
   * @param a2lParamMapping
   * @return
   */
  private boolean isA2lWpParamMapObjChanged(final A2lWpParamMapping paramMappingInDB,
      final A2lWpParamMapping a2lParamMapping) {
    return validateRespDetails(paramMappingInDB, a2lParamMapping) ||
        validateWpNameCustDetails(paramMappingInDB, a2lParamMapping) ||
        isObjectChanged(a2lParamMapping.isWpRespInherited(), paramMappingInDB.isWpRespInherited());
  }

  /**
   * @param paramMappingInDB
   * @param a2lParamMapping
   * @return
   */
  private boolean validateWpNameCustDetails(final A2lWpParamMapping paramMappingInDB,
      final A2lWpParamMapping a2lParamMapping) {
    return isObjectChanged(a2lParamMapping.getWpNameCust(), paramMappingInDB.getWpNameCust()) ||
        isObjectChanged(a2lParamMapping.isWpNameCustInherited(), paramMappingInDB.isWpNameCustInherited());
  }

  /**
   * @param paramMappingInDB
   * @param a2lParamMapping
   * @return
   */
  private boolean validateRespDetails(final A2lWpParamMapping paramMappingInDB,
      final A2lWpParamMapping a2lParamMapping) {
    return isObjectChanged(a2lParamMapping.getWpRespId(), paramMappingInDB.getWpRespId()) ||
        isObjectChanged(a2lParamMapping.getParA2lRespId(), paramMappingInDB.getParA2lRespId());
  }

  /**
   * Validate and modify the excel wp name
   *
   * @param wpName
   * @return
   */
  private String modifyWpName(final String wpName) {
    String modifiedWpName = wpName;
    // replace special character with underscore
    if (!wpName.equals(ApicConstants.DEFAULT_A2L_WP_NAME)) {
      A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
      modifiedWpName = validator.replaceInvalidChars(wpName);
      String profileDataWpPrefix = this.input.getA2lWpImportProfileData().getPrefixForWp();
      if ((null != profileDataWpPrefix) && !modifiedWpName.startsWith(profileDataWpPrefix)) {
        modifiedWpName = profileDataWpPrefix + modifiedWpName;
      }
    }
    return modifiedWpName;
  }

  /**
   * Validate and modify the excel resp name
   *
   * @param excelRespName
   * @return
   * @throws InvalidInputException
   */
  private String modifyRespName(final String excelRespName) throws InvalidInputException {
    if (!excelRespName.endsWith("bosch.com") && !ApicConstants.ALIAS_NAME_RB.equals(excelRespName) &&
        !ApicConstants.ALIAS_NAME_CUSTOMER.equals(excelRespName) &&
        !ApicConstants.ALIAS_NAME_OTHERS.equals(excelRespName)) {
      A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
      String modifiedRespName = validator.replaceInvalidChars(excelRespName, '@');
      String profileDataRespPrefix = this.input.getA2lWpImportProfileData().getPrefixForResp();
      if ((null != profileDataRespPrefix) && !modifiedRespName.startsWith(profileDataRespPrefix)) {
        modifiedRespName = profileDataRespPrefix + modifiedRespName;
      }
      BitSet validationResult = validator.isValidName(modifiedRespName, '@');
      if (validationResult.cardinality() != 0) {
        throw new InvalidInputException("The generated alias name for the given responsibility inputs '" +
            excelRespName + "' does not comply with A2L specification\n" + validator.createErrorMsg(validationResult));
      }
      return modifiedRespName;
    }
    return excelRespName;
  }


  /**
   * Returns wp pal with same wp name in var grp specified irrespective of resp name
   *
   * @param wpName
   * @param firstName
   * @param lastName
   * @param respName
   * @param varGrpId
   * @return A2lWpResponsibility
   */
  private A2lWpResponsibility checkIfWpExists(final String wpName, final String respName, final Long varGrpId) {
    Map<Long, A2lWpResponsibility> wpRespMap = this.existingWpRespMap.get(wpName.toUpperCase(Locale.getDefault()));
    if (wpRespMap != null) {
      for (A2lWpResponsibility wpRespPal : wpRespMap.values()) {
        if (checkWpRespHasVariantGrp(wpRespPal, varGrpId)) {
          // if same wp exists but with different responsibilty , store in set
          A2lResponsibility resp = this.a2lRespMap.get(wpRespPal.getA2lRespId());
          if ((resp != null) && !(CommonUtils.isEqual(resp.getAliasName(), respName))) {
            this.existingWpRespWithDiffResp.add(wpRespPal);
          }
          return wpRespPal;
        }

      }
    }
    return null;
  }


  /**
   * Returns wp pal with same wp name in var grp specified irrespective of resp name
   *
   * @param wpName
   * @param firstName
   * @param lastName
   * @param respName
   * @param varGrpId
   * @return A2lWpResponsibility
   */
  private boolean checkIfWpExistsInDefaultLevel(final String wpName) {
    Map<Long, A2lWpResponsibility> wpRespMap = this.existingWpRespMap.get(wpName.toUpperCase(Locale.getDefault()));
    if (wpRespMap != null) {
      for (A2lWpResponsibility wpRespPal : wpRespMap.values()) {
        if (wpRespPal.getVariantGrpId() == null) {
          return true;
        }

      }
    }
    return false;
  }

  /**
   * Creates the A 2 l wp resp pal.
   *
   * @throws IcdmException the icdm exception
   */
  private void createA2lWpResp() throws IcdmException {
    // Step 2.1 : Check if A2lWpResponsibility already exists, if so, do not create new one

    Map<String, A2lResponsibility> newlyCreatedRespNames = new HashMap<>();
    for (VarGrp2Wp entry : this.input.getVarGrp2WpRespSet()) {
      A2lWpResponsibility respPal = new A2lWpResponsibility();
      respPal.setWpDefnVersId(this.wpDefVersId);

      // If FC2WP-WP name exceeds 150 characters - truncate it before saving
      String wpName = entry.getWpName();
      wpName = wpName.length() > MAX_WP_NAME_LENGTH ? wpName.substring(0, MAX_WP_NAME_LENGTH) : wpName;

      String respTypeCode = entry.getRespTypeCode();
      // Modify new workpackage name based on A2l Specifications
      if (null == this.existingWpRespMap.get(wpName.toUpperCase(Locale.getDefault()))) {
        wpName = modifyWpName(wpName);
      }

      String excelRespName = entry.getRespName();
      excelRespName = excelRespName.length() > MAX_RESP_NAME_LENGTH ? excelRespName.substring(0, MAX_RESP_NAME_LENGTH)
          : excelRespName;
      String respName = excelRespName;

      // Modify new responsibility alias name based on A2l Specifications
      A2lResponsibility checkIfSameA2lRespExists = checkIfSameA2lRespExists(respName);
      if (null == checkIfSameA2lRespExists) {
        respName = modifyRespName(respName);
      }

      A2lWpResponsibility wpRespPal = checkIfWpExists(wpName, respName, entry.getVarGrpId());
      A2lResponsibility importedRespObj = null;
      // if wp doesnt exist for same variant group
      boolean excelWpRespTypeColAvail = entry.isExcelWPRespTypeColAvail();
      if ((wpRespPal == null)) {
        importedRespObj =
            getOrCreateResp(newlyCreatedRespNames, respName, excelRespName, respTypeCode, excelWpRespTypeColAvail);
        if (!wpName.equals(ApicConstants.DEFAULT_A2L_WP_NAME)) {
          createWpRespCmd(entry.getVarGrpId(), wpName, importedRespObj, entry.getWpName());
        }
      }
      else if (this.existingWpRespWithDiffResp.contains(wpRespPal)) {
        // if wp exists in var group but with different resp , overwrite the resp with the name from excel
        importedRespObj =
            getOrCreateResp(newlyCreatedRespNames, respName, excelRespName, respTypeCode, excelWpRespTypeColAvail);
        wpRespPal.setA2lRespId(importedRespObj.getId());
        A2lWpResponsibilityCommand cmd = new A2lWpResponsibilityCommand(getServiceData(), wpRespPal, true, false);
        executeChildCommand(cmd);
        this.wpRespPalSet.add(cmd);
      }
      else {
        // if there are no changes in WP mappings and there are changes only in responsibility types
        checkIfSameA2lRespExists = checkIfSameA2lRespExists(respName);
        if ((null != checkIfSameA2lRespExists) && excelWpRespTypeColAvail &&
            CommonUtils.isNotEqual(respTypeCode, checkIfSameA2lRespExists.getRespType())) {
          // update the pidc resp object if they do not have same resp type
          updatePidcRespObj(checkIfSameA2lRespExists, respTypeCode);
        }
      }
    }

  }

  /**
   * Checks whether the resp is existing in pidc or newly created during import , if not new record is inserted
   *
   * @param newlyCreatedRespNames
   * @param firstName
   * @param lastName
   * @param respName
   * @param respTypeCode
   * @return A2lResponsibility
   * @throws IcdmException
   */
  private A2lResponsibility getOrCreateResp(final Map<String, A2lResponsibility> newlyCreatedRespNames,
      final String respName, final String excelRespName, final String respTypeCode,
      final boolean isRespTypeCodeAvailable)
      throws IcdmException {
    A2lResponsibility importedRespObj;
    // check if A2lResponsibility exists with same resp name , type for the pidc
    importedRespObj = checkIfSameA2lRespExists(respName);

    // if resp is not available for pidc , check whether it is newly created during import
    if (importedRespObj == null) {
      importedRespObj = newlyCreatedRespNames.get(respName);
    } // isRespTypeCodeAvailable is used to update the respTypeCode if only respTypeCode is available in the input excel
      // file
    else if (isRespTypeCodeAvailable && CommonUtils.isNotEqual(respTypeCode, importedRespObj.getRespType())) {
      // update the pidc resp object if they do not have same resp type
      updatePidcRespObj(importedRespObj, respTypeCode);
    }

    // if resp is not available , create a new one
    if (importedRespObj == null) {
      // Overwrite if existing else create new
      importedRespObj = createPidcRespObj(respName, excelRespName, respTypeCode);
      newlyCreatedRespNames.put(respName, importedRespObj);
    }

    return importedRespObj;
  }

  /**
   * @param importedRespObj
   * @param respTypeCode
   * @return
   * @throws IcdmException
   */
  private A2lResponsibility updatePidcRespObj(final A2lResponsibility importedRespObj, final String respTypeCode)
      throws IcdmException {
    importedRespObj.setRespType(respTypeCode);
    A2lResponsibilityCommand a2lRespCommand =
        new A2lResponsibilityCommand(getServiceData(), importedRespObj, true, false);
    executeChildCommand(a2lRespCommand);
    this.respSet.add(a2lRespCommand);
    return a2lRespCommand.getNewData();
  }

  /**
   * Create the wp resp
   *
   * @param entry
   * @param modifiedWpName
   * @param importedRespObj
   * @param originalWpName
   * @throws IcdmException
   */
  private A2lWpResponsibility createWpRespCmd(final Long varGrpId, final String modifiedWpName,
      final A2lResponsibility importedRespObj, final String originalWpName)
      throws IcdmException {
    // if wp name is already existing for the selected version use the same else create new one
    A2lWorkPackage a2lWp = this.existingWpMap.get(modifiedWpName.toUpperCase(Locale.getDefault()));
    if (a2lWp == null) {
      A2lWorkPackage newWpPalObj = new A2lWorkPackage();
      newWpPalObj.setName(modifiedWpName);
      newWpPalObj.setPidcVersId(this.input.getPidcVersionId());
      // set the actual WP name from Excel file to the description of the A2LWorkpackage that is created
      newWpPalObj.setDescription(originalWpName);
      A2lWorkPackageCommand wpPalCmd = new A2lWorkPackageCommand(getServiceData(), newWpPalObj, false, false);
      executeChildCommand(wpPalCmd);
      a2lWp = wpPalCmd.getNewData();
      this.a2lWpCommandSet.add(wpPalCmd);
      this.existingWpMap.put(modifiedWpName.toUpperCase(Locale.getDefault()), a2lWp);
    }

    A2lWpResponsibility palObj = new A2lWpResponsibility();
    palObj.setWpDefnVersId(this.wpDefVersId);
    palObj.setA2lRespId(importedRespObj.getId());
    palObj.setVariantGrpId(varGrpId);
    palObj.setA2lWpId(a2lWp.getId());
    A2lWpResponsibilityCommand cmd = new A2lWpResponsibilityCommand(getServiceData(), palObj, false, false);
    executeChildCommand(cmd);


    A2lWpResponsibility newWpRespPal = cmd.getNewData();
    this.wpRespPalSet.add(cmd);
    Map<Long, A2lWpResponsibility> wpRespMap =
        this.existingWpRespMap.get(modifiedWpName.toUpperCase(Locale.getDefault()));
    if (wpRespMap == null) {
      wpRespMap = new HashMap<>();
    }
    wpRespMap.put(newWpRespPal.getId(), newWpRespPal);
    this.existingWpRespMap.put(modifiedWpName.toUpperCase(Locale.getDefault()), wpRespMap);

    return newWpRespPal;
  }


  /**
   * @param firstName
   * @param lastName
   * @param excelRespName
   * @return A2lResponsibility
   */
  private A2lResponsibility checkIfSameA2lRespExists(final String excelRespName) {
    for (A2lResponsibility wpResp : this.a2lRespMap.values()) {
      if (CommonUtils.isEqual(wpResp.getAliasName(), excelRespName)) {
        return wpResp;
      }
    }
    return null;
  }

  private A2lResponsibility createPidcRespObj(final String respName, final String excelRespName,
      final String respTypeCode)
      throws IcdmException {
    // create new pidc resp obj
    A2lResponsibility respObj = new A2lResponsibility();
    respObj.setProjectId(this.pidcId);
    respObj.setRespType(respTypeCode);
    respObj.setLDepartment(excelRespName);
    respObj.setAliasName(respName);
    if (respName.endsWith("bosch.com")) {
      UserDetailsLoader detailsLoader = new UserDetailsLoader(getServiceData());
      UserDetails userInfo = detailsLoader.getByEmail(respName);
      if (null != userInfo) {
        respObj.setLFirstName(userInfo.getFirstName());
        respObj.setLLastName(userInfo.getLastName());
        respObj.setUserId(userInfo.getId());
        respObj.setLDepartment(userInfo.getDepartment());
        respObj.setAliasName(respName);
      }
    }
    A2lResponsibilityCommand cmd = new A2lResponsibilityCommand(getServiceData(), respObj, false, false);
    executeChildCommand(cmd);
    A2lResponsibility newRespObj = cmd.getNewData();
    this.respSet.add(cmd);
    return newRespObj;
  }


  /**
   * @param obj
   * @param variantGrpId
   * @return
   */
  private boolean checkWpRespHasVariantGrp(final A2lWpResponsibility obj, final Long variantGrpId) {
    if ((variantGrpId == null) && (obj.getVariantGrpId() == null)) {
      return true;
    }
    return obj.getVariantGrpId() == null ? false
        : (variantGrpId != null) && (obj.getVariantGrpId().longValue() == variantGrpId.longValue());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No Implementation
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
   * @return the a2lWpCommandSet
   */
  public Set<A2lWorkPackageCommand> getA2lWpCommandSet() {
    return new HashSet<>(this.a2lWpCommandSet);
  }


  /**
   * @return the a2lRespMap
   */
  public Map<Long, A2lResponsibility> getA2lRespMap() {
    return new HashMap<>(this.a2lRespMap);
  }

  /**
   * @return the wpDefnVerSet
   */
  public Set<A2lWpDefinitionVersionCommand> getWpDefnVerSet() {
    return new HashSet<>(this.wpDefnVerSet);
  }


}
