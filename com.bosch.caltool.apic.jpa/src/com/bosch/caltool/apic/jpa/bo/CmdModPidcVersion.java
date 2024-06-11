/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.bo.rm.PidcRmDefinitionLoader;
import com.bosch.caltool.icdm.bo.rm.PidcRmProjCharacterLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;


/**
 * CmdModPidcVersion.java - Command handles all db operations on INSERT, UPDATE on PIDVersion
 *
 * @author dmo5cob
 */
public class CmdModPidcVersion extends AbstractCmdModProject {

  /**
   *
   */
  private static final String INVALID = " INVALID!";
  /**
   * PIDCVersion object
   */
  private PIDCVersion pidcVersion;
  /**
   * The version from which the new version is created
   */
  private PIDCVersion parentVersion;
  /**
   * PIDC
   */
  private PIDCard pidc;
  /**
   * new version state
   */
  private String newVrsnState;
  /**
   * old version state
   */
  private String oldVrsnState;
  /**
   * List of existing PIDC RM definitions
   */
  private Set<PidcRmDefinition> pidcRiskDefList = new HashSet<PidcRmDefinition>();
  /**
   * Unique entity id for setting user details
   */
  private static final String PIDC_VRSN_ENTITY_ID = "PIDC_VRSN_ENTITY_ID";
  /**
   * Store the transactionSummary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * Timestamp
   */
  private Timestamp lastConfirmationDate;
  private Timestamp oldLastConfirmationDate;

  /**
   * Constructor to create first version of PIDC. Set the parent PIDC using the setter method.
   *
   * @param dataProvider ApicDataProvider
   */
  CmdModPidcVersion(final ApicDataProvider dataProvider) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Constructor to create a new version in an existing pidc
   *
   * @param dataProvider ApicDataProvider
   * @param parentVrsn Version from which new version of pidc is created
   */
  public CmdModPidcVersion(final ApicDataProvider dataProvider, final PIDCVersion parentVrsn) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.parentVersion = parentVrsn;
    this.pidc = this.parentVersion.getPidc();
  }

  /**
   * Constructor for update/delete actions
   *
   * @param dataProvider the Apic Data provider
   * @param pidcToModify the PIDC that is being modified
   * @param delete whether the command is to delete the PIDC or not
   */
  public CmdModPidcVersion(final ApicDataProvider dataProvider, final PIDCVersion pidcToModify, final boolean delete) {
    super(dataProvider);

    this.pidcVersion = pidcToModify;
    this.attrValName = pidcToModify.getPidc().getName();

    this.pidc = this.pidcVersion.getPidc();

    if (delete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    // set all the fields
    setVersionFieldsToCommand(pidcToModify);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    this.childCmdStack.clear();

    createPidcVersion();

    // only for first version
    if (this.parentVersion == null) {
      createProjStructAttrs();

    }
    else {
      // Get and set Project Attr
      createPidcAttrRevisions();
      // create varaints
      createPidcVariantRevisions();
      // create the virtual structure attrs
      createVirtualStructureAttrs();

    }
    createPidcRmDef();
    createFocusMatrixVersion();
  }

  /**
   * @throws CommandException
   */
  private void createPidcRmDef() throws CommandException {
    if (this.parentVersion == null) {
      createDefaultRiskDef();
    }
    else {
      // Get existing PIDC RM Definitions of the parent PIDC version
      this.pidcRiskDefList = getExistingPidcRmDef(this.parentVersion.getID());
    }
    if ((null != this.pidcRiskDefList) && !this.pidcRiskDefList.isEmpty()) {
      for (PidcRmDefinition oldPidcRmDef : this.pidcRiskDefList) {
        if ((null != oldPidcRmDef.getIsVariant()) && oldPidcRmDef.getIsVariant().equalsIgnoreCase("N")) {
          try (ServiceData serviceData = new ServiceData()) {
            PidcRmDefinitionLoader riskDefLoader = new PidcRmDefinitionLoader(serviceData);
            PidcRmProjCharacterLoader projCharLoader = new PidcRmProjCharacterLoader(serviceData);
            PidcRmDefinition newPidcRmDef = createPidcRmDefCommands(oldPidcRmDef, riskDefLoader);
            if (this.parentVersion != null) {
              createPidcProjChar(oldPidcRmDef, newPidcRmDef, projCharLoader);
            }
          }
          catch (Exception exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }
      }
    }
  }

  /**
   * Fetch the list of risk definitions for a pidc version
   *
   * @param pidVersId pidc version id
   * @return Set of PidcRmDefinition
   */
  public Set<PidcRmDefinition> getExistingPidcRmDef(final Long pidVersId) {
    Set<PidcRmDefinition> pidcRmDefList = null;

    try (ServiceData serviceData = new ServiceData()) {
      PidcRmDefinitionLoader riskDefLoader = new PidcRmDefinitionLoader(serviceData);
      // Fetch all pidc rm defintions
      pidcRmDefList = riskDefLoader.getPidRmDefintions(pidVersId);
    }
    catch (DataException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), Activator.PLUGIN_ID);
    }
    return pidcRmDefList;
  }

  /**
   * @param oldPidcRmDef
   * @param newPidcRmDef
   * @param projCharLoader
   * @throws CommandException
   */
  private void createPidcProjChar(final PidcRmDefinition oldPidcRmDef, final PidcRmDefinition newPidcRmDef,
      final PidcRmProjCharacterLoader projCharLoader) throws CommandException {
    Set<PidcRmProjCharacter> existingPidcProjCharList = new HashSet<PidcRmProjCharacter>();
    Map<Long, PidcRmProjCharacter> pidRmProjCharMap = new ConcurrentHashMap<>();

    try {
      pidRmProjCharMap = projCharLoader.getPidRmProjChar(oldPidcRmDef.getId());
    }
    catch (DataException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), Activator.PLUGIN_ID);
    }
    existingPidcProjCharList.addAll(pidRmProjCharMap.values());
    if (!existingPidcProjCharList.isEmpty()) {
      for (PidcRmProjCharacter projChar : existingPidcProjCharList) {
        createPidcProjCharCommand(newPidcRmDef, projChar);
      }
    }
  }

  /**
   * @param projChar
   * @param newPidcRmDef
   * @throws CommandException
   */
  private void createPidcProjCharCommand(final PidcRmDefinition newPidcRmDef, final PidcRmProjCharacter oldProjchar)
      throws CommandException {
    CmdModPidcRmprojChar cmdRmProjChar = new CmdModPidcRmprojChar(getDataProvider(), newPidcRmDef, oldProjchar);
    this.childCmdStack.addCommand(cmdRmProjChar);
  }

  /**
   *
   */
  private void createDefaultRiskDef() {
    this.pidcRiskDefList = new TreeSet<>();
    // Create a default PIDC Rm Definition
    PidcRmDefinition rmDef = new PidcRmDefinition();
    rmDef.setPidcVersId(this.pidcVersion.getID());
    rmDef.setRmNameEng("PIDC");
    rmDef.setIsVariant("N");
    rmDef.setVersion(1L);
    this.pidcRiskDefList.add(rmDef);
  }

  /**
   * @param oldPidcRmDef
   * @param riskDefLoader
   * @param newPidcRmDef
   * @param dbPidcVersion
   * @return
   * @throws CommandException
   */
  private PidcRmDefinition createPidcRmDefCommands(final PidcRmDefinition oldPidcRmDef,
      final PidcRmDefinitionLoader riskDefLoader) throws CommandException {
    CmdModPidcRmDefinition cmdRmDef =
        new CmdModPidcRmDefinition(getDataProvider(), oldPidcRmDef, this.pidcVersion, riskDefLoader);
    this.childCmdStack.addCommand(cmdRmDef);
    return cmdRmDef.getNewPidcRmDef();
  }

  /**
   * Create working set version of the foucs matrix
   *
   * @throws CommandException any exception from focus matrix version created
   */
  // ICDM-2569
  private void createFocusMatrixVersion() throws CommandException {
    FocusMatrixVersion refFmVers =
        this.parentVersion == null ? null : this.parentVersion.getFocusMatrixWorkingSetVersion();

    CmdModFocusMatrixVersion cmdFmVers = new CmdModFocusMatrixVersion(this.pidcVersion, refFmVers);
    cmdFmVers.setVersStatus(FocusMatrixVersion.FM_VERS_STATUS.WORKING_SET);
    this.childCmdStack.addCommand(cmdFmVers);
  }

  /**
   * @throws CommandException
   */
  private void createVirtualStructureAttrs() throws CommandException {
    for (PIDCDetStructure detStrObj : this.parentVersion.getVirtualLevelAttrs().values()) {
      CmdModPidcDetStructure command = new CmdModPidcDetStructure(getDataProvider(), this.pidcVersion,
          detStrObj.getAttribute(), detStrObj.getPidAttrLevel(), false, true);
      this.childCmdStack.addCommand(command);
    }
  }


  /**
   * Create Project Attributes for pidc node structure. Here project attribute creation commands are called. They are
   * added to a stack for undoing
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createProjStructAttrs() throws CommandException {
    PIDCNode pidcNode = this.pidc.getLeafNode();
    for (Entry<Long, AttributeValue> entry : pidcNode.getNodeStructureValues().entrySet()) {
      final CmdModProjectAttr projAttrCmd = createProjAttrCommand(entry.getKey(), entry.getValue());
      this.childCmdStack.addCommand(projAttrCmd);
    }
  }

  /**
   * @param pidcAttrID the pidc attribute ID
   * @param value the attribute value
   * @return the new command
   */
  private CmdModProjectAttr createProjAttrCommand(final Long pidcAttrID, final AttributeValue value) {

    final TPidcVersion dbPidcVrsn = getEntityProvider().getDbPIDCVersion(this.pidcVersion.getID());
    final PIDCAttribute pidcAttr = new PIDCAttribute(getDataProvider(), null, pidcAttrID, dbPidcVrsn.getPidcVersId());
    final CmdModProjectAttr projAttrCmd = new CmdModProjectAttr(getDataProvider(), pidcAttr);
    projAttrCmd.setValue(value);
    projAttrCmd.setUsed(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType());

    return projAttrCmd;
  }

  /**
   * Create revision for PIDC's attributes
   *
   * @param projID
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createPidcAttrRevisions() throws CommandException {
    TPidcVersion parentDbPidcVrsn = getEntityProvider().getDbPIDCVersion(this.parentVersion.getID());
    final Map<Long, IPIDCAttribute> projAttr = getDefinedProjectAttr(parentDbPidcVrsn);

    PIDCAttribute pidcAttr;
    CmdModProjectAttr cmdProjAttr;
    for (IPIDCAttribute pidcAttribute : projAttr.values()) {
      final long attrID = pidcAttribute.getAttribute().getAttributeID();
      pidcAttr = new PIDCAttribute(getDataProvider(), null, attrID, this.pidcVersion.getID());
      // Create Command for PIDC Attr
      cmdProjAttr = new CmdModProjectAttr(getDataProvider(), pidcAttr);
      // Set the following information from original pidc attr
      cmdProjAttr.setIsVariant(pidcAttribute.isVariant());
      cmdProjAttr.setUsed(pidcAttribute.getIsUsed());
      cmdProjAttr.setValue(pidcAttribute.getAttributeValue());
      // ICDM-372
      cmdProjAttr.setNewPartNumber(pidcAttribute.getPartNumber());
      cmdProjAttr.setNewSpecLink(pidcAttribute.getSpecLink());
      cmdProjAttr.setNewDesc(pidcAttribute.getAdditionalInfoDesc());
      cmdProjAttr.setNewHiddenFlag(pidcAttribute.isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);
      cmdProjAttr.setNewTrnfrVcdmFlag(pidcAttribute.canTransferToVcdm() ? ApicConstants.YES : ApicConstants.CODE_NO);
      cmdProjAttr
          .setNewFMRelevantFlag(pidcAttribute.isFocusMatrixApplicable() ? ApicConstants.YES : ApicConstants.CODE_NO);


      this.childCmdStack.addCommand(cmdProjAttr);
    }
  }

  /**
   * Gets the attributes of the project which are defined
   *
   * @param dbpidcVrsn
   * @return
   */
  private Map<Long, IPIDCAttribute> getDefinedProjectAttr(final TPidcVersion dbpidcVrsn) {

    final Map<Long, IPIDCAttribute> projAttrMap = new HashMap<Long, IPIDCAttribute>();
    // iterate through all the project attributes
    for (TabvProjectAttr dbPidcAttr : dbpidcVrsn.getTabvProjectAttrs()) {
      PIDCAttribute pidcAttr = getDataProvider().getPidcAttribute(dbPidcAttr.getPrjAttrId());

      if (pidcAttr == null) {
        pidcAttr = new PIDCAttribute(getDataProvider(), dbPidcAttr.getPrjAttrId());
      }

      // add the attribute
      projAttrMap.put(dbPidcAttr.getTabvAttribute().getAttrId(), pidcAttr);
    }
    return projAttrMap;
  }

  /**
   * Create revision for PIDC's all Variants and Variant Attrs
   *
   * @param pidcVrsn
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createPidcVariantRevisions() throws CommandException {
    final Map<Long, PIDCVariant> pidcVars = new HashMap<Long, PIDCVariant>();
    if (null != this.parentVersion) {
      pidcVars.putAll(this.parentVersion.getVariantsMap());

      CmdModPidcVariant cmdProjVar;
      for (PIDCVariant pidcVar : pidcVars.values()) {
        final TabvProjectVariant dbVar = getEntityProvider().getDbPidcVariant(pidcVar.getVariantID());
        // // copy Risk Definitions of the parent
        TPidcRmDefinition dbRiskDefVar = dbVar.getTPidcRmDefinition();
        // // Create Command for PIDC Variant
        PidcRmDefinition newRmDef = null;
        if (null != dbRiskDefVar) {

          try (ServiceData serviceData = new ServiceData()) {
            PidcRmDefinitionLoader riskDefLoader = new PidcRmDefinitionLoader(serviceData);
            PidcRmDefinition oldpidcRmDef = riskDefLoader.createDataObject(dbRiskDefVar);
            newRmDef = createPidcRmDefCommands(oldpidcRmDef, riskDefLoader);
          }
          catch (DataException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }
        cmdProjVar = new CmdModPidcVariant(getDataProvider(), this.pidcVersion, true/* isRevision */, null);
        cmdProjVar.setNameValue(getDataProvider().getAttrValue(dbVar.getTabvAttrValue().getValueId()));
        if (null != newRmDef) {
          cmdProjVar.setVarRiskDefID(newRmDef.getId());
        }
        // Set the variantID to copy this variant attrs
        cmdProjVar.setVariant(dbVar.getVariantId());
        // ICDM-1359
        // set the deleted flag of previous version
        cmdProjVar.setDeletedFlag(dbVar.getDeletedFlag());
        this.childCmdStack.addCommand(cmdProjVar);
      }
    }
  }

  /**
   * Set required fileds to the Command from UI, also store old fields to support undo
   *
   * @param apicDataProvider
   * @param modifyAttribute
   */
  private void setVersionFieldsToCommand(final PIDCVersion modifyVersion) {
    // version state
    this.oldVrsnState = modifyVersion.getStatus().getDbStatus();
    this.newVrsnState = this.oldVrsnState;

    this.oldNameEng = modifyVersion.getPidcVersionName();
    this.newNameEng = this.oldNameEng;

    this.oldDescEng = modifyVersion.getPidcVersionDescEng();
    this.newDescEng = this.oldDescEng;

    this.oldDescGer = modifyVersion.getPidcVersionDescGer();
    this.newDescGer = this.oldDescGer;

    this.oldLastConfirmationDate = ApicUtil.calendarToTimestamp(modifyVersion.getLastConfirmationDate());
    this.lastConfirmationDate = this.oldLastConfirmationDate;
  }

  /**
   * Create the PidcVersion record
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createPidcVersion() throws CommandException {

    Long proRevID;
    TabvProjectidcard dbProjIdCard;

    if (this.parentVersion == null) {
      proRevID = 1L;
      dbProjIdCard = getEntityProvider().getDbPIDC(this.pidc.getID());
    }
    else {
      SortedSet<PIDCVersion> allVrsnSet = this.pidc.getAllVersions();
      Long maxRevId = 0L;
      for (PIDCVersion vrsn : allVrsnSet) {
        if ((getEntityProvider().getDbPIDCVersion(vrsn.getID()) != null) && (vrsn.getProRevId() > maxRevId)) {
          maxRevId = vrsn.getProRevId();
        }

      }
      proRevID = maxRevId + 1;
      dbProjIdCard = getEntityProvider().getDbPIDC(this.parentVersion.getPidc().getID());
    }
    TPidcVersion dbPidcVersion = new TPidcVersion();

    dbPidcVersion.setProRevId(proRevID);
    if (null != this.parentVersion) {
      dbPidcVersion.setTPidcVers(getEntityProvider().getDbPIDCVersion(this.parentVersion.getID()));
    }
    setUserDetails(this.commandMode, dbPidcVersion, PIDC_VRSN_ENTITY_ID);
    dbPidcVersion.setTabvProjectidcard(dbProjIdCard);
    dbPidcVersion.setVersName(this.newNameEng);
    dbPidcVersion.setVersDescEng(this.newDescEng);
    dbPidcVersion.setVersDescGer(this.newDescGer);
    dbPidcVersion.setPidStatus(ApicConstants.PIDC_STATUS_ID_INWORK_STR);
    getEntityProvider().registerNewEntity(dbPidcVersion);
    this.pidcVersion = new PIDCVersion(getDataProvider(), dbPidcVersion.getPidcVersId());

    if (dbProjIdCard.getTPidcVersions() == null) {
      dbProjIdCard.setTPidcVersions(new HashSet<TPidcVersion>());
    }
    dbProjIdCard.getTPidcVersions().add(dbPidcVersion);
    this.pidc.resetVersions();


    getChangedData().put(this.pidcVersion.getID(),
        new ChangedData(ChangeType.INSERT, this.pidcVersion.getID(), TPidcVersion.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.pidcVersion.getID(), TPidcVersion.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcVersion(this.pidcVersion.getID()).getObjectDetails());

    final TPidcVersion dbPidcVrsn = getEntityProvider().getDbPIDCVersion(this.pidcVersion.getID());
    validateStaleData(dbPidcVrsn);

    if (!CommonUtils.isEmptyString(this.newVrsnState)) {
      dbPidcVrsn.setPidStatus(this.newVrsnState);
    }
    dbPidcVrsn.setVersName(this.newNameEng);
    dbPidcVrsn.setVersDescEng(this.newDescEng);
    dbPidcVrsn.setVersDescGer(this.newDescGer);


    // Task 242053
    dbPidcVrsn.setLastConfirmationDate(this.lastConfirmationDate);


    setUserDetails(COMMAND_MODE.UPDATE, dbPidcVrsn, PIDC_VRSN_ENTITY_ID);


    getChangedData().put(this.pidcVersion.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.pidcVersion.getID(), TPidcVersion.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcVersion(this.pidcVersion.getID()).getObjectDetails());


    final TPidcVersion deletedPidVrsn = getEntityProvider().getDbPIDCVersion(this.pidcVersion.getID());

    validateStaleData(deletedPidVrsn);
    // set the deleted flag, do not delete the entity
    if (this.unDelete) {
      deletedPidVrsn.setDeletedFlag(ApicConstants.CODE_NO);
    }
    else {
      deletedPidVrsn.setDeletedFlag(ApicConstants.YES);
    }

    setUserDetails(COMMAND_MODE.UPDATE, deletedPidVrsn, PIDC_VRSN_ENTITY_ID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isNameChaged() || isDescChanged() || isLastConfirmDateChanged();
  }

  /**
   * @return true if the name fields are changed
   */
  private boolean isNameChaged() {
    return isStringChanged(this.oldVrsnState, this.newVrsnState) || isStringChanged(this.newNameEng, this.oldNameEng);
  }

  /**
   * @return true if the desc fields are changed
   */
  private boolean isDescChanged() {
    return isStringChanged(this.newDescEng, this.oldDescEng) || isStringChanged(this.newDescGer, this.oldDescGer);
  }

  /**
   * @return true if the name fields are changed
   */
  private boolean isLastConfirmDateChanged() {
    return isObjectChanged(this.oldLastConfirmationDate, this.lastConfirmationDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {

    String commandModeText = getPrimaryObjectType();

    return super.getString(commandModeText, getPrimaryObjectIdentifier());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {


    SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      // If the command mode is insert
      case INSERT:
        caseCmdIns(detailsList);
        break;
      // If the command mode is update
      case UPDATE:
        caseCmdUpd(detailsList);

        break;
      case DELETE:
        // no details section necessary in case of delete (parent row is sufficient in transansions view)
        if (this.unDelete) {
          this.summaryData.setOperation("UNDELETE");
        }
        break;
      default:
        // Do nothing
        break;
    }
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmdUpd(final SortedSet<TransactionSummaryDetails> detailsList) {
    addTransactionSummaryDetails(detailsList, this.oldDescEng, this.newDescEng, "Description (English)");
    addTransactionSummaryDetails(detailsList, this.oldDescGer, this.newDescGer, "Description (German)");
    addTransactionSummaryDetails(detailsList, this.oldNameEng, this.newNameEng, "Name (English)");
    addTransactionSummaryDetails(detailsList, this.oldNameGer, this.newNameGer, "Name  (German)");
    addTransactionSummaryDetails(detailsList, PidcVersionStatus.getStatus(this.oldVrsnState).getUiStatus(),
        PidcVersionStatus.getStatus(this.newVrsnState).getUiStatus(), "Status");
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    switch (this.commandMode) {
      case INSERT:
        if (null != this.parentVersion) {
          this.pidcVersion.getPidc().resetVersions();
        }
        break;
      default:
        break;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.pidcVersion == null ? null : this.pidcVersion.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "PID Card Version";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    Long dbpidcVrsID;
    switch (this.commandMode) {
      case INSERT:
        dbpidcVrsID = this.pidcVersion.getID();
        final TPidcVersion dbVrsn = getEntityProvider().getDbPIDCVersion(dbpidcVrsID);
        if (dbVrsn == null) {
          return INVALID;
        }
        return dbVrsn.getVersName();
      case UPDATE:
      case DELETE:
        dbpidcVrsID = this.pidcVersion.getID();
        final TPidcVersion dbUpdateVrsn = getEntityProvider().getDbPIDCVersion(dbpidcVrsID);
        if (dbUpdateVrsn == null) {
          return INVALID;
        }
        return dbUpdateVrsn.getVersName();
      default:
        // Do nothing
        break;
    }
    return INVALID;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    this.childCmdStack.rollbackAll(getExecutionMode());

    switch (this.commandMode) {
      case INSERT:
        // clear pidc version from cache
        caseCmdModIns();

        break;
      default:
        // Do nothing
        break;
    }


  }

  /**
   *
   */
  private void caseCmdModIns() {
    getDataCache().getPidc(this.pidc.getID()).resetVersions();

    getDataCache().getAllPidcVersionMap().remove(this.pidcVersion.getID());
  }

  /**
   * Set the new pidc version state
   *
   * @param newState version state
   */
  public void setPIDCVersionState(final String newState) {
    this.newVrsnState = PidcVersionStatus.valueOf(newState).getDbStatus();
  }

  /**
   * @param pidCard
   */
  void setPidCard(final PIDCard pidCard) {
    this.pidc = pidCard;
  }

  /**
   * @param lastConfirmationDate Timestamp
   */
  public void setLastConfirmationDate(final Timestamp lastConfirmationDate) {
    this.lastConfirmationDate = lastConfirmationDate == null ? null : (Timestamp) lastConfirmationDate.clone();
  }

  /**
   * @return the existngPidcRmDefList
   */
  public Set<PidcRmDefinition> getExistngPidcRmDefList() {
    return this.pidcRiskDefList;
  }

  /**
   * @param existngPidcRmDefList the existngPidcRmDefList to set
   */
  public void setExistngPidcRmDefList(final SortedSet<PidcRmDefinition> existngPidcRmDefList) {
    this.pidcRiskDefList = existngPidcRmDefList;
  }
}
