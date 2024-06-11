/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdModPidcVariant - Command class to CREATE, UPDATE and DELETE Variants
 *
 * @author adn1cob
 */
public class CmdModPidcVariant extends AbstractCmdModProject {

  /**
   * Entity ID for setting user attributes - variant
   */
  private static final String PVR_ENTITY_ID = "PVR_ENTITY_ID";

  /**
   * Entity ID for setting user attributes - pidc
   */
  private static final String PIDC_ENTITY_ID = "PIDC_ENTITY_ID";

  /**
   * The DB Project Variant entity to modify
   */
  private Long dbProjVarId;

  /**
   * The PIDC Variant model
   */
  private PIDCVariant pidcVar;

  /**
   * The DB PIDC entity of which the VARIANT belongs to
   */
  private final Long dbPidcVrsnId;

  /**
   * Indicates this command is triggered when creating a new revision of pidc variant
   */
  private boolean isNewRevision;

  /**
   * The variantID for which the variant attributes to be created
   */
  private Long variantID;

  // ICDM-406
  /**
   * The Map of attributes & values for the structure attributes
   */
  private Map<Attribute, AttributeValue> attrValues;

  private final TransactionSummary summaryData = new TransactionSummary(this);
  // ICDM-1359
  private String deletedFlag = ApicConstants.CODE_NO;

  private Long varRiskDefID;


  /**
   * Constructor - Used for INSERT new Variant under the PIDC, Set the VARIANT names to this command
   *
   * @param dataProvider the data provider
   * @param pidcVers the ProjectIDCard version in which VARIANT to be created
   * @param isRevision indicates if new variant is created , when creating a new revision for the pidc
   * @param attrValues gives attribute values for the structure attributes , null if there is no virtual levels or
   *          undefined level
   */
  public CmdModPidcVariant(final ApicDataProvider dataProvider, final PIDCVersion pidcVers, final boolean isRevision,
      final Map<Attribute, AttributeValue> attrValues) {
    super(dataProvider);


    this.dbPidcVrsnId = pidcVers.getID();

    // Command mode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.isNewRevision = isRevision;
    // ICDM-406
    this.attrValues = attrValues;
  }

  /**
   * Constructor - Used to UPDATE a VARIANT
   *
   * @param dataProvider the data provider
   * @param pidcVar the Variant to be modified
   * @param isDelete flag indicating if the variant to be deleted, TRUE to delete
   */
  public CmdModPidcVariant(final ApicDataProvider dataProvider, final PIDCVariant pidcVar, final boolean isDelete) {
    super(dataProvider);

    // Icdm-229
    this.dbPidcVrsnId = pidcVar.getPidcVersion().getID();
    this.pidcVar = pidcVar;
    this.dbProjVarId = pidcVar.getVariantID();
    this.attrValName = pidcVar.getVariantName();

    if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    initialiseProperties();
  }


  /**
   * Intialise properties
   */
  private void initialiseProperties() {
    if (this.commandMode == COMMAND_MODE.UPDATE) {
      this.oldNameEng = this.pidcVar.getVariantNameEng();
      this.oldNameGer = this.pidcVar.getVariantNameGer();
      this.oldDescEng = this.pidcVar.getVariantDescEng();
      this.oldDescGer = this.pidcVar.getVariantDescGer();
      this.newNameEng = this.oldNameEng;
      this.newNameGer = this.oldNameGer;
      this.newDescEng = this.oldDescEng;
      this.newDescGer = this.oldDescGer;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    // Clear the stack
    this.childCmdStack.clear();

    TabvAttrValue newDbVarName;
    // Get the attribute value object for VARIANT name
    if (this.nameValue == null) { // New value to be created.
      newDbVarName = createNameValue(getDataProvider().getVarNameAttribute());
    }
    else { // Value is selected from the list.
      newDbVarName = getEntityProvider().getDbValue(this.nameValue.getValueID());
    }
    // For logging
    this.attrValName = getDataProvider().getAttrValue(newDbVarName.getValueId()).getTextValue();
    // Create VARIANT entity
    this.pidcVar = createNewVariant(newDbVarName);

    // Create default variant Attributes
    createDefaultVariantAttrs(this.pidcVar);

    // Update PIDC modification details
    // Icdm-229
    final TPidcVersion dbPidc = getEntityProvider().getDbPIDCVersion(this.dbPidcVrsnId);
    setUserDetails(COMMAND_MODE.UPDATE, dbPidc, PIDC_ENTITY_ID);

    getChangedData().put(this.pidcVar.getID(),
        new ChangedData(ChangeType.INSERT, this.pidcVar.getID(), TabvProjectVariant.class, DisplayEventSource.COMMAND));
  }


  /**
   * @param pidcVariant
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createDefaultVariantAttrs(final PIDCVariant pidcVariant) throws CommandException {
    // Icdm-229
    final TPidcVersion dbPidc = getEntityProvider().getDbPIDCVersion(this.dbPidcVrsnId);

    Map<Long, PIDCVariant> pidcVars = getDataCache().getPidcVersion(dbPidc.getPidcVersId()).getVariantsMap();

    // Create sub-variants, only in case of creating new revision
    if (this.isNewRevision) {
      pidcVars = getDataCache().getPidcVersion(dbPidc.getPidcVersId()).getParentVersion().getVariantsMap();
      createSubVariants(pidcVars, pidcVariant);
    }
    // Check if PID has atleast one VARIANT
    if (!pidcVars.isEmpty()) {
      CmdModProjVariantAttr cmdProjVarAttr;
      final Map<Long, PIDCAttributeVar> pidcVarAttrs = getVariantAttrs(pidcVariant, pidcVars);
      // Create default attributes for the new variant
      if (pidcVarAttrs != null) {
        PIDCAttributeVar projVarAttr;
        AttributeValue valueToSet = null;
        for (PIDCAttributeVar pidcVarAttr : pidcVarAttrs.values()) {
          projVarAttr = new PIDCAttributeVar(getDataProvider(), pidcVarAttr.getAttribute().getAttributeID(),
              pidcVariant.getVariantID());
          cmdProjVarAttr = new CmdModProjVariantAttr(getDataProvider(), projVarAttr);
          // For new revision, maintain the used flag,valu,isVariant info of original varAttr
          if (this.isNewRevision) {
            cmdProjVarAttr.setIsVariant(pidcVarAttr.isVariant());
            cmdProjVarAttr.setUsed(pidcVarAttr.getIsUsed());
            cmdProjVarAttr.setValue(pidcVarAttr.getAttributeValue());
            // ICDM-372
            cmdProjVarAttr.setNewPartNumber(pidcVarAttr.getPartNumber());
            cmdProjVarAttr.setNewSpecLink(pidcVarAttr.getSpecLink());
            cmdProjVarAttr.setNewDesc(pidcVarAttr.getAdditionalInfoDesc());
            cmdProjVarAttr.setNewHiddenFlag(pidcVarAttr.isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);
          }
          else {
            if (this.attrValues != null) {
              valueToSet = this.attrValues.get(pidcVarAttr.getAttribute());
            }
            // ICDM-406
            if (CommonUtils.isNotNull(valueToSet)) {
              cmdProjVarAttr.setValue(this.attrValues.get(pidcVarAttr.getAttribute()));
              cmdProjVarAttr.setUsed(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType());
            }
            else {
              cmdProjVarAttr.setValue(null);
              cmdProjVarAttr.setUsed(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType());
            }
            // ICDM-372
            cmdProjVarAttr.setNewPartNumber("");
            cmdProjVarAttr.setNewSpecLink("");
            cmdProjVarAttr.setNewDesc("");
          }
          cmdProjVarAttr.setProRevId(pidcVariant.getRevision());
          this.childCmdStack.addCommand(cmdProjVarAttr);
        }
      }
    }
  }

  /**
   * @param pidcVars
   * @param pidcVariant
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createSubVariants(final Map<Long, PIDCVariant> pidcVars, final PIDCVariant pidcVariant)
      throws CommandException {
    // Iterate thru all variants
    for (PIDCVariant pidVar : pidcVars.values()) {
      // chek for the match of the variant for which subvariants to be got
      if (pidVar.getVariantID() == this.variantID.longValue()) {
        final Map<Long, PIDCSubVariant> pidcSubVars = pidVar.getSubVariantsMap();
        if (!pidcSubVars.isEmpty()) {
          CmdModPidcSubVariant cmdProjSubVar;
          for (PIDCSubVariant pidSubVar : pidcSubVars.values()) {
            final TabvProjectSubVariant dbSubVar = getEntityProvider().getDbPidcSubVariant(pidSubVar.getSubVariantID());
            // Create Command for PIDC SubVariant
            cmdProjSubVar =
                new CmdModPidcSubVariant(getDataProvider(), pidcVariant, true/* isRevision */, this.attrValues);
            cmdProjSubVar.setNameValue(getDataProvider().getAttrValue(dbSubVar.getTabvAttrValue().getValueId()));
            // Set the subVariantID to copy this subVariant attrs
            cmdProjSubVar.setSubVariant(dbSubVar.getSubVariantId());
            // ICDM-1359
            // set the deleted flag of previous version
            cmdProjSubVar.setDeletedFlag(dbSubVar.getDeletedFlag());
            // Set the variant id, used when creating sub-variant Attrs inside CmdModPidcSubVariant
            cmdProjSubVar.setVariant(pidVar.getVariantID());
            this.childCmdStack.addCommand(cmdProjSubVar);
          }
        }
        break;
      }
    }


  }

  /**
   * @param pidcVariant
   * @param pidcVars
   * @param pidcVarAttrs
   * @return
   */
  private Map<Long, PIDCAttributeVar> getVariantAttrs(final PIDCVariant pidcVariant,
      final Map<Long, PIDCVariant> pidcVars) {
    Map<Long, PIDCAttributeVar> pidcVarAttrs = null;
    // For a new REVISION, get list of variant attrs for which the variant revision is created
    if (this.isNewRevision) {
      for (PIDCVariant pidVar : pidcVars.values()) {
        if (pidVar.getVariantID() == this.variantID.longValue()) {
          // ICDM-1360
          pidcVarAttrs = pidVar.getAttributesAll();
          break;
        }
      }
    } // Get VARIANT attributes list from any one of the VARIANT
    else {
      for (PIDCVariant pidVar : pidcVars.values()) {
        if (pidcVariant.getVariantID() == pidVar.getVariantID()) {
          continue;
        }
        // ICDM-1360
        pidcVarAttrs = pidVar.getAttributesAll();
        // any one variant attributes list is sufficient, hence break
        break;
      }

    }
    return pidcVarAttrs;
  }

  /**
   * @param newDbVarName
   * @return
   */
  private PIDCVariant createNewVariant(final TabvAttrValue newDbVarName) {
    // Create new VARIANT entity
    // Icdm-229
    TabvProjectVariant dbProjVar;

    final TPidcVersion dbPidc = getEntityProvider().getDbPIDCVersion(this.dbPidcVrsnId);
    dbProjVar = new TabvProjectVariant();
    dbProjVar.setTPidcVersion(dbPidc);
    dbProjVar.setTabvAttrValue(newDbVarName);
    if (null != this.varRiskDefID) {
      dbProjVar.setTPidcRmDefinition(getEntityProvider().getDbPidcRmDef(this.varRiskDefID));
    }

    // Update PIDC-VARIANT modification details
    setUserDetails(COMMAND_MODE.INSERT, dbProjVar, PVR_ENTITY_ID);

    // ICDM-1359
    // set the deleted flag of previous version
    dbProjVar.setDeletedFlag(this.deletedFlag);
    // Register new VARIANT for this project
    getEntityProvider().registerNewEntity(dbProjVar);
    this.dbProjVarId = dbProjVar.getVariantId();
    if (dbPidc.getTabvProjectVariants() == null) {
      dbPidc.setTabvProjectVariants(new ArrayList<TabvProjectVariant>());
    }
    dbPidc.getTabvProjectVariants().add(dbProjVar);
    // Create new variant object, this will be added to the variantMap
    // collection after creating default variant attributes
    return new PIDCVariant(getDataProvider(), dbProjVar.getVariantId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Check parallel changes
    // Icdm-229
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.dbProjVarId, TabvProjectVariant.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcVaraint(this.dbProjVarId).getObjectDetails());
    final TabvProjectVariant dbProjVar = getEntityProvider().getDbPidcVariant(this.dbProjVarId);
    final TPidcVersion dbPidcVersion = getEntityProvider().getDbPIDCVersion(this.dbPidcVrsnId);
    validateStaleData(dbProjVar);
    // Check if data changed
    if (isNameUpdated()) {
      validateStaleData(dbProjVar.getTabvAttrValue());
      // ICDM-767
      final List<AttributeValue> varList = getDataCache().getVarNameAttribute().getAttrValues(false);
      boolean varNameAlreadyExists = false;
      for (AttributeValue var : varList) {
        if (CommonUtils.isEqual(this.newNameEng, var.getTextValueEng())) {
          // if new variant name already exists, change the mapping of attr value
          final TabvAttrValue newDbVarName = getEntityProvider().getDbValue(var.getValueID());
          dbProjVar.setTabvAttrValue(newDbVarName);
          varNameAlreadyExists = true;
          break;
        }
      }
      if (varNameAlreadyExists) {
        // this is to update description
        // if it is not used in any other pidc
        updateName(dbProjVar.getTabvAttrValue().getValueId());
      }
      else {
        // if the new variant name doesnot exist
        if (getDataLoader().isVarNameUsed(this.pidcVar)) {
          // create new attr value
          final TabvAttrValue newDbVarName = createNameValue(getDataProvider().getVarNameAttribute());
          dbProjVar.setTabvAttrValue(newDbVarName);

        }
        else {
          // if it is not used in any other pidc
          updateName(dbProjVar.getTabvAttrValue().getValueId());
        }
      }


    }

    setUserDetails(COMMAND_MODE.UPDATE, dbProjVar, PVR_ENTITY_ID);

    // Update PIDC modification details
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcVersion, PIDC_ENTITY_ID);

    getChangedData().put(this.dbProjVarId, chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // AttributeValue to be marked as deleted, use its command to delete
    // Icdm-229
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.dbProjVarId, TabvProjectVariant.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcVaraint(this.dbProjVarId).getObjectDetails());
    final TabvProjectVariant dbProjVar = getEntityProvider().getDbPidcVariant(this.dbProjVarId);
    final TPidcVersion dbPidcVersion = getEntityProvider().getDbPIDCVersion(this.dbPidcVrsnId);

    // DO NOT- mark the AttributeValue as deleted if variant name is deleted in a pidc
    // deletion change removed - iCDM-1155

    validateStaleData(dbProjVar);
    // set the deleted flag, do not delete the entity
    if (this.unDelete) {
      dbProjVar.setDeletedFlag(ApicConstants.CODE_NO);
    }
    else {
      dbProjVar.setDeletedFlag(ApicConstants.YES);
    }

    setUserDetails(COMMAND_MODE.UPDATE, dbProjVar, PVR_ENTITY_ID);

    // Update PIDC modification details
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcVersion, PIDC_ENTITY_ID);

    getChangedData().put(this.dbProjVarId, chdata);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // Delete the variant attributes
    this.childCmdStack.undoAll();
    // Delete the VARIANT
    // Icdm-229
    final TabvProjectVariant dbProjVar = getEntityProvider().getDbPidcVariant(this.dbProjVarId);
    final TPidcVersion dbPidc = getEntityProvider().getDbPIDCVersion(this.dbPidcVrsnId);
    getEntityProvider().deleteEntity(dbProjVar);
    getDataCache().getAllPidcVariants().remove(dbProjVar.getVariantId());
    // remove from collection too, for this pidc
    getDataCache().getPidcVersion(dbPidc.getPidcVersId()).getVariantsMap().remove(dbProjVar.getVariantId());
    // Delete the value for name if created
    if (this.cmdAttrValue != null) {
      this.cmdAttrValue.undo();
    }
    getChangedData().put(this.dbProjVarId,
        new ChangedData(ChangeType.DELETE, this.dbProjVarId, TabvProjectVariant.class, DisplayEventSource.COMMAND));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // Icdm-229

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.dbProjVarId, TabvProjectVariant.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcVaraint(this.dbProjVarId).getObjectDetails());
    final TabvProjectVariant dbProjVar = getEntityProvider().getDbPidcVariant(this.dbProjVarId);
    // Check parallel changes
    validateStaleData(dbProjVar);
    // Check if data changed
    if (isNameUpdated()) {
      validateStaleData(dbProjVar.getTabvAttrValue());
      this.childCmdStack.undoAll();
    }
    getChangedData().put(this.dbProjVarId, chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.dbProjVarId, TabvProjectVariant.class, DisplayEventSource.COMMAND);
    this.childCmdStack.undoAll();
    getChangedData().put(this.dbProjVarId, chdata);
  }

  /**
   * Sets the variant ID, called when creating new revision
   *
   * @param variantId old variant for which revision to be created
   */
  protected void setVariant(final long variantId) {
    this.variantID = variantId;
  }

  /**
   * Return the variant ID
   *
   * @return the variant ID
   */
  public long getNewVariantID() {
    // Icdm-229
    final TabvProjectVariant dbProjVar = getEntityProvider().getDbPidcVariant(this.dbProjVarId);
    return dbProjVar.getVariantId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // iCDM-834
    // Notify icdm hotline ,if value clearance required
    CmdModAttributeValue cmdAttrVal = getCmdAttrVal();
    if ((cmdAttrVal != null) && cmdAttrVal.isClearanceRequired()) {
      cmdAttrVal.sendMailNotification();
    }

    // Refresh the Variant in cache to reflect changes in UI
    // Icdm-229
    final TabvProjectVariant dbProjVar = getEntityProvider().getDbPidcVariant(this.dbProjVarId);

    final TPidcVersion dbPidcVer = getEntityProvider().getDbPIDCVersion(this.dbPidcVrsnId);
    final TabvProjectidcard dbPidc = getEntityProvider().getDbPIDC(dbPidcVer.getTabvProjectidcard().getProjectId());
    // Null Check added For the Undo Redo
    if (dbProjVar != null) {
      getEntityProvider().refreshCacheObject(dbProjVar);
    }
    getEntityProvider().refreshCacheObject(dbPidcVer);


    if (dbPidc != null) {
      getEntityProvider().refreshCacheObject(dbPidc);
    }

  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {
    this.childCmdStack.rollbackAll(getExecutionMode());
    switch (this.commandMode) {
      case INSERT:
        getDataCache().getAllPidcVariants().remove(this.dbProjVarId);
        break;
      default:
        // Do nothing
        break;
    }
    if (this.cmdAttrValue != null) {
      this.cmdAttrValue.rollBackDataModel();
    }
  }

  /**
   * {@inheritDoc} returns the variant id that has been subjected to modifications
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.pidcVar == null ? null : this.pidcVar.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "PIDC Variant";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {


    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        addTransactionSummaryDetails(detailsList, this.oldDescEng, this.newDescEng, "Description (English)");
        addTransactionSummaryDetails(detailsList, this.oldDescGer, this.newDescGer, "Description (German)");
        addTransactionSummaryDetails(detailsList, this.oldNameEng, this.newNameEng, "Name (English)");
        addTransactionSummaryDetails(detailsList, this.oldNameGer, this.newNameGer, "Name  (German)");
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
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  // ICDM-1359
  /**
   * @param deletedFlag
   */
  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

  /**
   * @return the varRiskDefID
   */
  public Long getVarRiskDefID() {
    return this.varRiskDefID;
  }

  /**
   * @param varRiskDefID the varRiskDefID to set
   */
  public void setVarRiskDefID(final Long varRiskDefID) {
    this.varRiskDefID = varRiskDefID;
  }

}
