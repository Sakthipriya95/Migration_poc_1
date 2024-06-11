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
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdModPidcSubVariant.java- Command handles all db operations on INSERT, UPDATE, DELETE on PID Sub Variant
 *
 * @author adn1cob
 */
public class CmdModPidcSubVariant extends AbstractCmdModProject {

  /**
   * Entity ID for setting user details - sub variant
   */
  private static final String PSV_ENTITY_ID = "SV_ENTITY_ID";
  /**
   * Entity ID for setting user details - variant
   */
  private static final String PV_ENTITY_ID = "PV_ENTITY_ID";
  /**
   * Entity ID for setting user details - pidc
   */
  private static final String PIDC_ENTITY_ID = "PIDC_ENTITY_ID";

  /**
   * The PIDC Sub-Variant model
   */
  private PIDCSubVariant pidcSubVar;

  /**
   * The sub-variant DB attribute
   */
  private Long dbProjSubVarId;

  /**
   * The DB Project Variant entity of which the sub variant belongs to
   */
  private final Long dbVariantId;

  /**
   * The DB PIDC entity of which the VARIANT belongs to
   */
  private final Long dbPidcVersionId;

  /**
   * Indicates this command is triggered when creating a new revision of pidc sub variant
   */
  private boolean isNewRevision;

  /**
   * The SubVariantID for which the subVariant attributes to be created when creating revision
   */
  private Long subVariantID;
  /**
   * The VariantID for which the subVariant attributes to be created when creating revision
   */
  private Long variantId;
  // ICDM-404
  /**
   * The Map of attributes & values for the structure attributes
   */
  private Map<Attribute, AttributeValue> attrValues;

  /**
   * Transaction summary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  // ICDM-1359
  /**
   * Deleted flag
   */
  private String deletedFlag = ApicConstants.CODE_NO;

  /**
   * Constructor - Used for INSERT new SUB-Variant under the VARIANT
   *
   * @param dataProvider the data provider
   * @param pidcVar pidcVARIANT where the subvariant to be created
   * @param isRevision indicates if new variant is created , when creating a new revision for the pidc
   * @param attrValues gives attribute values for the structure attributes , null if there is no virtual levels or
   *          undefined level
   */
  public CmdModPidcSubVariant(final ApicDataProvider dataProvider, final PIDCVariant pidcVar, final boolean isRevision,
      final Map<Attribute, AttributeValue> attrValues) {
    super(dataProvider);
    // Icdm-229
    this.dbVariantId = pidcVar.getVariantID();
    this.dbPidcVersionId = pidcVar.getPidcVersion().getID();

    // Command mode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.isNewRevision = isRevision;
    // ICDM-404
    this.attrValues = attrValues;
  }

  /**
   * Constructor - Used to UPDATE, DELETE a Sub-VARIANT
   *
   * @param dataProvider the data provider
   * @param pidcSubVar the SUB-Variant to be modified
   * @param isDelete flag indicating if the variant to be deleted, TRUE to delete
   */
  public CmdModPidcSubVariant(final ApicDataProvider dataProvider, final PIDCSubVariant pidcSubVar,
      final boolean isDelete) {
    super(dataProvider);
    // Icdm-229
    this.dbVariantId = pidcSubVar.getPidcVariant().getVariantID();
    this.dbPidcVersionId = pidcSubVar.getPidcVariant().getPidcVersion().getID();
    this.dbProjSubVarId = pidcSubVar.getSubVariantID();

    this.pidcSubVar = pidcSubVar;
    this.attrValName = pidcSubVar.getSubVariantName();
    if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    initialiseProperties();
  }

  /**
   * This method initialises the old and new names & descriptions
   */
  private void initialiseProperties() {
    if (this.commandMode == COMMAND_MODE.UPDATE) {
      this.oldNameEng = this.pidcSubVar.getSubVariantNameEng();
      this.oldNameGer = this.pidcSubVar.getSubVariantNameGer();
      this.oldDescEng = this.pidcSubVar.getSubVariantDescEng();
      this.oldDescGer = this.pidcSubVar.getSubVariantDescGer();
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

    TabvAttrValue newDbSubVarName;
    // Get the attribute value object for VARIANT name
    if (this.nameValue == null) { // New value to be created.
      newDbSubVarName = createNameValue(getDataProvider().getSubvarNameAttribute());
    }
    else { // Value is selected from the list.
      newDbSubVarName = getEntityProvider().getDbValue(this.nameValue.getValueID());
    }
    // For logging
    this.attrValName = getDataProvider().getAttrValue(newDbSubVarName.getValueId()).getTextValue();
    // Create Sub-VARIANT entity
    this.pidcSubVar = createNewSubVariant(newDbSubVarName);

    // Create default variant Attributes
    createDefaultSubVariantAttrs(this.pidcSubVar);

    // Update PIDC-VARIANT modification details
    // Icdm-229
    final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(this.dbVariantId);
    final TPidcVersion dbPidc = getEntityProvider().getDbPIDCVersion(this.dbPidcVersionId);

    setUserDetails(COMMAND_MODE.UPDATE, dbVariant, PV_ENTITY_ID);

    // Also, Update PIDC modification details
    setUserDetails(COMMAND_MODE.UPDATE, dbPidc, PIDC_ENTITY_ID);

    getChangedData().put(this.pidcSubVar.getID(), new ChangedData(ChangeType.INSERT, this.pidcSubVar.getID(),
        TabvProjectSubVariant.class, DisplayEventSource.COMMAND));

  }

  /**
   * @param pidcSubVariant
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createDefaultSubVariantAttrs(final PIDCSubVariant pidcSubVariant) throws CommandException {

    Map<Long, PIDCSubVariant> pidcSubVars;
    // for a new revision, get the subVars of old variant
    if (this.isNewRevision) {
      pidcSubVars = getDataProvider().getPidcVaraint(this.variantId).getSubVariantsMap();
    }
    else {
      // Icdm-229
      final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(this.dbVariantId);
      pidcSubVars = getDataProvider().getPidcVaraint(dbVariant.getVariantId()).getSubVariantsMap();
    }

    // Check if PID has atleast one Sub-VARIANT
    if (!pidcSubVars.isEmpty()) {
      CmdModProjSubVariantAttr cmdProjSubVarAttr;
      final Map<Long, PIDCAttributeSubVar> pidcSubVarAttrs = getSubVarAttrs(pidcSubVariant, pidcSubVars);
      // Create default attributes for the new sub-variant
      if (pidcSubVarAttrs != null) {
        PIDCAttributeSubVar projSubVarAttr;
        AttributeValue valueToSet = null;
        for (PIDCAttributeSubVar pidcSubVarAttr : pidcSubVarAttrs.values()) {
          projSubVarAttr = new PIDCAttributeSubVar(getDataProvider(), pidcSubVarAttr.getAttribute().getAttributeID(),
              pidcSubVariant.getPidcVariant().getVariantID(), pidcSubVariant.getSubVariantID());
          cmdProjSubVarAttr = new CmdModProjSubVariantAttr(getDataProvider(), projSubVarAttr);
          // For new revision, maintain the used flag and values of original subVarAttr
          if (this.isNewRevision) {
            cmdProjSubVarAttr.setUsed(pidcSubVarAttr.getIsUsed());
            cmdProjSubVarAttr.setValue(pidcSubVarAttr.getAttributeValue());
            // ICDM-372
            cmdProjSubVarAttr.setNewPartNumber(pidcSubVarAttr.getPartNumber());
            cmdProjSubVarAttr.setNewSpecLink(pidcSubVarAttr.getSpecLink());
            cmdProjSubVarAttr.setNewDesc(pidcSubVarAttr.getAdditionalInfoDesc());
            cmdProjSubVarAttr.setNewHiddenFlag(pidcSubVarAttr.isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);
          }
          else {
            if (this.attrValues != null) {
              valueToSet = this.attrValues.get(pidcSubVarAttr.getAttribute());
            }
            // ICDM-404
            if (CommonUtils.isNotNull(valueToSet)) {
              cmdProjSubVarAttr.setValue(this.attrValues.get(pidcSubVarAttr.getAttribute()));
              cmdProjSubVarAttr.setUsed(ApicConstants.USED_YES_DISPLAY);
            }
            else {
              cmdProjSubVarAttr.setValue(null);
              cmdProjSubVarAttr.setUsed(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType());
            }
            // ICDM-372
            cmdProjSubVarAttr.setNewPartNumber("");
            cmdProjSubVarAttr.setNewSpecLink("");
            cmdProjSubVarAttr.setNewDesc("");
          }
          cmdProjSubVarAttr.setProRevId(pidcSubVariant.getRevision());
          this.childCmdStack.addCommand(cmdProjSubVarAttr);
        }
      }
    }
  }

  /**
   * @param pidcSubVariant
   * @param pidcSubVars
   * @return
   */
  private Map<Long, PIDCAttributeSubVar> getSubVarAttrs(final PIDCSubVariant pidcSubVariant,
      final Map<Long, PIDCSubVariant> pidcSubVars) {
    Map<Long, PIDCAttributeSubVar> pidcSubVarAttrs = null;
    // For a new REVISION, get list of variant attrs for which the variant revision is created
    if (this.isNewRevision) {
      for (PIDCSubVariant pidSubVar : pidcSubVars.values()) {
        if (pidSubVar.getSubVariantID() == this.subVariantID.longValue()) {
          // ICDM-1360
          pidcSubVarAttrs = pidSubVar.getAttributesAll();
          break;
        }
      }
    } // Get SUB-VARIANT attributes list from any one of the Sub-VARIANT
    else {
      for (PIDCSubVariant pidSubVar : pidcSubVars.values()) {
        if (pidcSubVariant.getSubVariantID() == pidSubVar.getSubVariantID()) {
          continue;
        }
        // ICDM-1360
        pidcSubVarAttrs = pidSubVar.getAttributesAll();
        // any one variant attributes list is sufficient, hence break
        break;
      }

    }
    return pidcSubVarAttrs;
  }

  /**
   * @param newDbSubVarName
   * @return
   */
  private PIDCSubVariant createNewSubVariant(final TabvAttrValue newDbSubVarName) {

    // Create new SUB-VARIANT entity
    // Icdm-229
    final TabvProjectSubVariant dbProjSubVar = new TabvProjectSubVariant();
    final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(this.dbVariantId);
    final TPidcVersion dbPidcVrsn = getEntityProvider().getDbPIDCVersion(this.dbPidcVersionId);
    dbProjSubVar.setTabvProjectVariant(dbVariant);

    dbProjSubVar.setTPidcVersion(dbPidcVrsn);
    dbProjSubVar.setTabvAttrValue(newDbSubVarName);

    // ICDM-1359
    // set the deleted flag of previous version
    dbProjSubVar.setDeletedFlag(this.deletedFlag);

    setUserDetails(COMMAND_MODE.INSERT, dbProjSubVar, PSV_ENTITY_ID);

    // Register new SUB-VARIANT for this project
    getEntityProvider().registerNewEntity(dbProjSubVar);
    this.dbProjSubVarId = dbProjSubVar.getSubVariantId();

    if (dbPidcVrsn.getTabvProjectSubVariants() == null) {
      dbPidcVrsn.setTabvProjectSubVariants(new ArrayList<TabvProjectSubVariant>());
    }
    dbPidcVrsn.getTabvProjectSubVariants().add(dbProjSubVar);
    if (dbVariant.getTabvProjectSubVariants() == null) {
      dbVariant.setTabvProjectSubVariants(new ArrayList<TabvProjectSubVariant>());
    }
    dbVariant.getTabvProjectSubVariants().add(dbProjSubVar);
    // Create new sub-variant object, this will be added to the sub-variantMap
    // collection after creating default sub-variant attributes
    return new PIDCSubVariant(getDataProvider(), dbProjSubVar.getSubVariantId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {

    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.dbProjSubVarId, TabvProjectSubVariant.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcSubVaraint(this.dbProjSubVarId).getObjectDetails());

    // Check parallel changes
    // Icdm-229
    final TabvProjectSubVariant dbProjSubVar = getEntityProvider().getDbPidcSubVariant(this.dbProjSubVarId);
    final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(this.dbVariantId);
    final TPidcVersion dbPidc = getEntityProvider().getDbPIDCVersion(this.dbPidcVersionId);
    validateStaleData(dbProjSubVar);

    // Check if data changed
    if (isNameUpdated()) {
      validateStaleData(dbProjSubVar.getTabvAttrValue());
      // ICDM-767
      final List<AttributeValue> subVarList = getDataCache().getSubvarNameAttribute().getAttrValues(false);
      boolean subVarUsed = false;
      for (AttributeValue subVar : subVarList) {
        if (CommonUtils.isEqual(this.newNameEng, subVar.getTextValueEng())) {
          // if new sub-variant name already exists, change the mapping of attr value
          final TabvAttrValue newDbSubVarName = getEntityProvider().getDbValue(subVar.getValueID());
          dbProjSubVar.setTabvAttrValue(newDbSubVarName);
          subVarUsed = true;
          break;
        }
      }
      if (subVarUsed || !getDataLoader().isSubVarNameUsed(this.pidcSubVar)) {
        // this is to update description in case of subVarUsed
        // if it is not used in any other pidc
        updateName(dbProjSubVar.getTabvAttrValue().getValueId());
      }
      else {
        // if the new sub-variant name doesnot exist
        if (getDataLoader().isSubVarNameUsed(this.pidcSubVar)) {
          final TabvAttrValue newDbSubVarName = createNameValue(getDataProvider().getSubvarNameAttribute());
          dbProjSubVar.setTabvAttrValue(newDbSubVarName);
        }
      }

    }
    setUserDetails(COMMAND_MODE.UPDATE, dbProjSubVar, PSV_ENTITY_ID);
    // Update PIDC-VARIANT modification details
    setUserDetails(COMMAND_MODE.UPDATE, dbVariant, PV_ENTITY_ID);
    // Also,Update PIDC modification details
    setUserDetails(COMMAND_MODE.UPDATE, dbPidc, PIDC_ENTITY_ID);

    getChangedData().put(this.dbProjSubVarId, chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {

    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.dbProjSubVarId, TabvProjectSubVariant.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcSubVaraint(this.dbProjSubVarId).getObjectDetails());

    // AttributeValue to be marked as deleted, use its command to delete
    // Icdm-229
    final TabvProjectSubVariant dbProjSubVar = getEntityProvider().getDbPidcSubVariant(this.dbProjSubVarId);
    final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(this.dbVariantId);
    final TPidcVersion dbPidc = getEntityProvider().getDbPIDCVersion(this.dbPidcVersionId);

    // DO NOT- mark the AttributeValue as deleted if sub-variant name is deleted in a pidc
    // deletion change removed - iCDM-1155

    validateStaleData(dbProjSubVar);
    // set the deleted flag, do not delete the entity
    if (this.unDelete) {
      dbProjSubVar.setDeletedFlag(ApicConstants.CODE_NO);
    }
    else {
      dbProjSubVar.setDeletedFlag(ApicConstants.YES);
    }
    setUserDetails(COMMAND_MODE.UPDATE, dbProjSubVar, PSV_ENTITY_ID);

    // Update PIDC-VARIANT modification details
    setUserDetails(COMMAND_MODE.UPDATE, dbVariant, PV_ENTITY_ID);

    // Also,Update PIDC modification details
    setUserDetails(COMMAND_MODE.UPDATE, dbPidc, PIDC_ENTITY_ID);

    getChangedData().put(this.dbProjSubVarId, chdata);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // Delete the variant attributes
    this.childCmdStack.undoAll();
    // Delete the Sub-VARIANT
    // Icdm-229
    final TabvProjectSubVariant dbProjSubVar = getEntityProvider().getDbPidcSubVariant(this.dbProjSubVarId);
    final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(this.dbVariantId);

    getEntityProvider().deleteEntity(dbProjSubVar);
    getDataCache().getAllPidcSubVariants().remove(dbProjSubVar.getSubVariantId());
    // remove from collection too, for this pidc-variant
    getDataProvider().getPidcVaraint(dbVariant.getVariantId()).getSubVariantsMap()
        .remove(dbProjSubVar.getSubVariantId());
    // Delete the value for name if created
    if (this.cmdAttrValue != null) {
      this.cmdAttrValue.undo();
    }
    getChangedData().put(this.dbProjSubVarId, new ChangedData(ChangeType.DELETE, this.dbProjSubVarId,
        TabvProjectSubVariant.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {

    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.dbProjSubVarId, TabvProjectSubVariant.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcSubVaraint(this.dbProjSubVarId).getObjectDetails());

    // Check parallel changes
    // Icdm-229
    final TabvProjectSubVariant dbProjSubVar = getEntityProvider().getDbPidcSubVariant(this.dbProjSubVarId);
    validateStaleData(dbProjSubVar);
    // Check if data changed
    if (isNameUpdated()) {
      validateStaleData(dbProjSubVar.getTabvAttrValue());
      if (this.cmdAttrValue != null) {
        this.cmdAttrValue.undo();
      }
    }
    getChangedData().put(this.dbProjSubVarId, chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {

    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.dbProjSubVarId, TabvProjectSubVariant.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcSubVaraint(this.dbProjSubVarId).getObjectDetails());
    this.childCmdStack.undoAll();
    getChangedData().put(this.dbProjSubVarId, chdata);

  }

  /**
   * Sets the SubVariant ID, called when creating new revision
   *
   * @param subVariantId old sub variant for which revision to be created
   */
  protected void setSubVariant(final long subVariantId) {
    this.subVariantID = subVariantId;
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
    // Refresh the cache opbject, for the changes to reflect in UI
    // Icdm-229
    final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(this.dbVariantId);
    if (dbVariant != null) {
      getEntityProvider().refreshCacheObject(dbVariant);
    }
  }

  /**
   * @return new sub variant's ID
   */
  public long getNewSubVariantID() {
    // Icdm-229
    final TabvProjectSubVariant dbProjSubVar = getEntityProvider().getDbPidcSubVariant(this.dbProjSubVarId);
    return dbProjSubVar.getSubVariantId();
  }

  /**
   * @param variantID variant ID
   */
  protected void setVariant(final Long variantID) {
    this.variantId = variantID;
  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {

    this.childCmdStack.rollbackAll(getExecutionMode());

    switch (this.commandMode) {
      case INSERT:
        getDataCache().getAllPidcSubVariants().remove(this.dbProjSubVarId);
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
   * {@inheritDoc} return id of the sub variant that is subjected to modification
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.pidcSubVar == null ? null : this.pidcSubVar.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "PIDC Sub Variant";
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


}
