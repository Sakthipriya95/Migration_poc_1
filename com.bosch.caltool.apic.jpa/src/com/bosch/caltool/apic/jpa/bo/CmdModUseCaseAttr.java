/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;


/**
 * CmdModUseCaseAttr - Command handles all db operations on INSERT, UPDATE, DELETE on usecase attributes
 *
 * @author mga1cob
 */
// ICDM-359
public class CmdModUseCaseAttr extends AbstractCommand {

  /**
   * MAP_TYPE_USE CASE
   */
  private static final char MAP_TYPE_UC = 'C';

  /**
   * MAP_TYPE_SECTION
   */
  private static final char MAP_TYPE_SECTION = 'S';

  /**
   * MAP_TYPE_MULTIPLE_ITEMS
   */
  private static final char MAP_TYPE_MULTIPLE_ITEMS = 'M';

  /**
   * MAPPING_ENTITY_ID
   */
  private static final String MAPPING_ENTITY_ID = "MAPPING_ENTITY_ID";

  /**
   * Use case item mapping type. Possible values
   */
  private char mappingType;

  private Long attrID;

  private Long usecaseID;

  private Long ucsID;

  private final Set<AbstractUseCaseItem> ucItemsToUpdate = new HashSet<AbstractUseCaseItem>();

  /**
   * This variable used to check if focus matrix available for given Use case / Use case section items
   */
  private boolean canUpdateFocusMatrix;

  /**
   * Child command stack
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);


  /**
   * Store the transactionSummary - ICDM-722
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor to update existing usecaseItem
   *
   * @param apicDataProvider instance
   * @param attribute modifiable attribute instance
   * @param usecaseItem defines AbstractUseCaseItem
   * @param isDelete defines attribute mapping or not
   */
  public CmdModUseCaseAttr(final ApicDataProvider apicDataProvider, final Attribute attribute,
      final AbstractUseCaseItem usecaseItem, final boolean isDelete) {
    super(apicDataProvider);
    initialize(attribute, usecaseItem, isDelete);
  }

  /**
   * Constructor to update existing usecaseSection
   *
   * @param apicDataProvider instance
   * @param attribute modifiable attribute instance
   * @param mappableUCItems defines set of UseCaseSections
   * @param isDelete defines attribute mapping or not
   */
  public CmdModUseCaseAttr(final ApicDataProvider apicDataProvider, final Attribute attribute,
      final Set<AbstractUseCaseItem> mappableUCItems, final boolean isDelete) {
    super(apicDataProvider);

    initialize(attribute, null /* To update select All/None */, isDelete);
    // To delete mapped attribute with all usecase items
    if (isDelete) {
      for (AbstractUseCaseItem abstractUseCaseItem : mappableUCItems) {
        if (abstractUseCaseItem.isMapped(attribute)) {
          this.ucItemsToUpdate.add(abstractUseCaseItem);
        }
      }
    }
    else {
      for (AbstractUseCaseItem abstractUseCaseItem : mappableUCItems) {
        // Check for unmapped attribute with all usecase items
        if (!abstractUseCaseItem.isMapped(attribute)) {
          this.ucItemsToUpdate.add(abstractUseCaseItem);
        }
      }
    }
  }

  /**
   * this method initialises all variables
   *
   * @param attribute
   * @param isDelete
   */
  private void initialize(final Attribute attribute, final AbstractUseCaseItem usecaseItem, final boolean isDelete) {
    // Check if attribute is already mapped with usecase
    if (isDelete) {
      // Set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      // Set the command mode - INSERT
      this.commandMode = COMMAND_MODE.INSERT;
    }
    // Get the DB entity for the Attribute to be modified
    this.attrID = attribute.getAttributeID();
    if (usecaseItem instanceof UseCase) {
      this.mappingType = MAP_TYPE_UC;
      // Get the DB entity for the Usecase to be modified
      this.usecaseID = usecaseItem.getID();
      this.canUpdateFocusMatrix = getCanUpdateFocusMatrixFlag(attribute, usecaseItem);
    }
    else if (usecaseItem instanceof UseCaseSection) {
      this.mappingType = MAP_TYPE_SECTION;
      // Fet the DB entity for the UsecaseSection to be modified
      this.ucsID = usecaseItem.getID();
      this.canUpdateFocusMatrix = getCanUpdateFocusMatrixFlag(attribute, usecaseItem);
    }
    else {
      this.mappingType = MAP_TYPE_MULTIPLE_ITEMS;
    }
  }

  /**
   * Returns true, if focus matrix availabe for given attribute, usecaseItem
   *
   * @param attribute attribute
   * @param usecaseItem usecaseItem
   * @return
   */
  private boolean getCanUpdateFocusMatrixFlag(final Attribute attribute, final AbstractUseCaseItem usecaseItem) {
    boolean canUpdateFocusMatrixFlag = false;
    if (this.commandMode == COMMAND_MODE.DELETE) {
      // checking if focusmatrix available for given attribute
      canUpdateFocusMatrixFlag = usecaseItem.isFocusMatrixAvailableWhileUnMapping(attribute);
    }
    else if (this.commandMode == COMMAND_MODE.INSERT) {
      // checking if focusmatrix available for given attribute
      canUpdateFocusMatrixFlag = usecaseItem.isFocusMatrixAvailableWhileMapping(attribute);
    }
    return canUpdateFocusMatrixFlag;
  }

  /**
   * This method adds mapping between UseCase and Attribute
   *
   * @param dbUseCase instance
   * @param dbAttr instance
   * @param numItem
   * @throws CommandException
   */
  private void addUsecaseAttrMapping(final TabvUseCase dbUseCase, final TabvAttribute dbAttr, final int numItem)
      throws CommandException {
    // Create new usecase attribute mapping instance
    final TabvUcpAttr dbUCAttrMapping = new TabvUcpAttr();
    // Set usecase to usecase attribute mapping
    dbUCAttrMapping.setTabvUseCas(dbUseCase);
    // Set attribute to usecase attribute mapping
    dbUCAttrMapping.setTabvAttribute(dbAttr);
    // Set user details
    setUserDetails(COMMAND_MODE.INSERT, dbUCAttrMapping, MAPPING_ENTITY_ID + numItem);
    // Regisetr TabvUcpAttr
    getDataProvider().getEntityProvider().registerNewEntity(dbUCAttrMapping);
    dbUseCase.getTabvUcpAttrs().add(dbUCAttrMapping);

    getDataCache().getUseCase(dbUseCase.getUseCaseId()).getAttributes()
        .add(getDataCache().getAttribute(dbAttr.getAttrId()));


    getChangedData().put(dbUCAttrMapping.getUcpaId(),
        new ChangedData(ChangeType.INSERT, dbUCAttrMapping.getUcpaId(), TabvUcpAttr.class, DisplayEventSource.COMMAND));

    // ICDM-1614, updating the UCPA_ID in TFocusMatrix, when inserting use case attr at Use Case Level
    if (this.canUpdateFocusMatrix) {
      CmdModMultipleFocusMatrix cmdModMultipleFocusMatrix =
          new CmdModMultipleFocusMatrix(getDataProvider(), dbUCAttrMapping.getUcpaId(), false,
              CmdModMultipleFocusMatrix.UPDATE_MODE.NOT_NULLIFY_UCPA, this.attrID, this.usecaseID, this.ucsID);
      this.childCmdStack.addCommand(cmdModMultipleFocusMatrix);
    }
  }

  /**
   * This method adds mapping between UseCaseSction and Attribute
   *
   * @param dbUCS instance
   * @param dbAttr instance
   * @param numItem
   * @throws CommandException
   */
  private void addUCSAttrMapping(final TabvUseCaseSection dbUCS, final TabvAttribute dbAttr, final int numItem)
      throws CommandException {
    // Create new usecase attribute mapping instance
    final TabvUcpAttr dbUCAttrMapping = new TabvUcpAttr();
    // Set UseCaseSection to usecase attribute mapping
    dbUCAttrMapping.setTabvUseCaseSection(dbUCS);
    // Set attribute to usecase attribute mapping
    dbUCAttrMapping.setTabvAttribute(dbAttr);
    // Set UseCase to usecase attribute mapping
    dbUCAttrMapping.setTabvUseCas(dbUCS.getTabvUseCas());
    // Set user details
    setUserDetails(COMMAND_MODE.INSERT, dbUCAttrMapping, MAPPING_ENTITY_ID + numItem);
    // Regisetr TabvUcpAttr
    getDataProvider().getEntityProvider().registerNewEntity(dbUCAttrMapping);
    dbUCS.getTabvUcpAttrs().add(dbUCAttrMapping);
    getDataCache().getUseCaseSection(dbUCS.getSectionId()).getAttributes()
        .add(getDataCache().getAttribute(dbAttr.getAttrId()));

    getChangedData().put(dbUCAttrMapping.getUcpaId(),
        new ChangedData(ChangeType.INSERT, dbUCAttrMapping.getUcpaId(), TabvUcpAttr.class, DisplayEventSource.COMMAND));
    // ICDM-1614, updating the UCPA_ID in TFocusMatrix, when inserting use case attr at Use Case Level
    if (this.canUpdateFocusMatrix) {
      CmdModMultipleFocusMatrix cmdModMultipleFocusMatrix =
          new CmdModMultipleFocusMatrix(getDataProvider(), dbUCAttrMapping.getUcpaId(), false,
              CmdModMultipleFocusMatrix.UPDATE_MODE.NOT_NULLIFY_UCPA, this.attrID, this.usecaseID, this.ucsID);
      this.childCmdStack.addCommand(cmdModMultipleFocusMatrix);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // Get modifiable attribute instance
    final TabvAttribute dbAttr = getEntityProvider().getDbAttribute(this.attrID);
    Attribute attribute = getDataProvider().getAttribute(this.attrID);
    // Check for any parallel changes for modifiable attribute
    validateStaleData(dbAttr);
    if (this.mappingType == MAP_TYPE_UC) {
      insertUsecaseInfo(dbAttr, this.usecaseID, 1 /* Use case count */);
    }
    else if (this.mappingType == MAP_TYPE_SECTION) {
      insertUCSInfo(dbAttr, this.ucsID, 1 /* Use case section count */);
    }
    else {
      int ucItemCount = 1;
      for (AbstractUseCaseItem ucItem : this.ucItemsToUpdate) {
        this.canUpdateFocusMatrix = ucItem.isFocusMatrixAvailableWhileMapping(attribute);
        if (ucItem instanceof UseCaseSection) {
          this.ucsID = ucItem.getID();
          insertUCSInfo(dbAttr, ucItem.getID(), ucItemCount);
        }
        else if (ucItem instanceof UseCase) {
          this.usecaseID = ucItem.getID();
          insertUsecaseInfo(dbAttr, ucItem.getID(), ucItemCount);
        }
        ucItemCount++;
      }
    }
  }

  /**
   * @param dbAttr
   * @param usecaseId
   * @param numItem
   * @throws CommandException
   */
  private void insertUsecaseInfo(final TabvAttribute dbAttr, final Long usecaseId, final int numItem)
      throws CommandException {
    // Get modifiable usecase instance
    final TabvUseCase dbUseCase = getEntityProvider().getDbUseCase(usecaseId);
    // Check for any parallel changes for usecase
    validateStaleData(dbUseCase);
    // Add mapping between use case and attribute
    addUsecaseAttrMapping(dbUseCase, dbAttr, numItem);
  }

  /**
   * @param dbAttr
   * @param ucsId
   * @param numItem
   * @throws CommandException
   */
  private void insertUCSInfo(final TabvAttribute dbAttr, final Long ucsId, final int numItem) throws CommandException {
    // Get modifiable UseCaseSection instance
    final TabvUseCaseSection dbUCS = getEntityProvider().getDbUseCaseSection(ucsId);
    // Check for any parallel changes for usecasesection
    validateStaleData(dbUCS);
    // Add mapping between use case section and attribute
    addUCSAttrMapping(dbUCS, dbAttr, numItem);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Not Applicable
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final TabvAttribute dbAttr = getEntityProvider().getDbAttribute(this.attrID);
    // Check for any parallel changes attribute
    validateStaleData(dbAttr);
    final Attribute attribute = getDataCache().getAttribute(this.attrID);
    if (this.mappingType == MAP_TYPE_UC) {
      deleteUsecaseUCPAttr(this.usecaseID, attribute, 1 /* Use case count */);
    }
    else if (this.mappingType == MAP_TYPE_SECTION) {
      deleteUCSUCPAttr(this.ucsID, attribute, 1 /* Use case section count */);
    }
    else {
      // Defines usecase item count
      int ucItemCount = 1;
      for (AbstractUseCaseItem ucItem : this.ucItemsToUpdate) {
        this.canUpdateFocusMatrix = ucItem.isFocusMatrixAvailableWhileUnMapping(attribute);
        if (ucItem instanceof UseCaseSection) {
          deleteUCSUCPAttr(ucItem.getID(), attribute, ucItemCount);
        }
        else if (ucItem instanceof UseCase) {
          deleteUsecaseUCPAttr(ucItem.getID(), attribute, ucItemCount);
        }
        ucItemCount++;
      }
    }
  }

  /**
   * This method removes the mapping between attribute and usecase
   *
   * @param usecaseId
   * @param attribute
   * @param numItem
   * @throws CommandException
   */
  private void deleteUsecaseUCPAttr(final Long usecaseId, final Attribute attribute, final int numItem)
      throws CommandException {
    final TabvUseCase dbUseCase = getEntityProvider().getDbUseCase(usecaseId);

    // Check for any parallel changes usecase
    validateStaleData(dbUseCase);
    final TabvUcpAttr delUcpAttr = getDataCache().getUseCase(usecaseId).getMappingEntity(attribute);

    // ICDM-1614, updating the UCPA_ID as NULL in TFocusMatrix, when deleting use case attr at Use Case Level
    if (this.canUpdateFocusMatrix) {
      CmdModMultipleFocusMatrix cmdModMultipleFocusMatrix =
          new CmdModMultipleFocusMatrix(getDataProvider(), delUcpAttr.getUcpaId(), false,
              CmdModMultipleFocusMatrix.UPDATE_MODE.NULLIFY_UCPA, attribute.getID(), usecaseId, null);
      this.childCmdStack.addCommand(cmdModMultipleFocusMatrix);
    }
    setUserDetails(COMMAND_MODE.DELETE, delUcpAttr, MAPPING_ENTITY_ID + numItem);
    dbUseCase.getTabvUcpAttrs().remove(delUcpAttr);
    getEntityProvider().deleteEntity(delUcpAttr);
    getChangedData().put(delUcpAttr.getUcpaId(),
        new ChangedData(ChangeType.DELETE, delUcpAttr.getUcpaId(), TabvUcpAttr.class, DisplayEventSource.COMMAND));
    getDataCache().getUseCase(usecaseId).getAttributes().remove(attribute);
  }

  /**
   * This method removes the mapping between attribute and usecase section
   *
   * @param ucsId
   * @param attribute
   * @param numItem
   * @throws CommandException
   */
  private void deleteUCSUCPAttr(final Long ucsId, final Attribute attribute, final int numItem)
      throws CommandException {
    final TabvUseCaseSection dbUseCaseSection = getEntityProvider().getDbUseCaseSection(ucsId);
    // Check for any parallel changes usecase section and Use case
    // Code Refactoring
    validateStaleData(dbUseCaseSection.getTabvUseCas());
    validateStaleData(dbUseCaseSection);

    final TabvUcpAttr delUcpAttr = getDataCache().getUseCaseSection(ucsId).getMappingEntity(attribute);

    // ICDM-1614, updating the UCPA_ID as NULL in TFocusMatrix, when deleting use case attr at Use Case Section Level
    if (this.canUpdateFocusMatrix) {
      CmdModMultipleFocusMatrix cmdModMultipleFocusMatrix = new CmdModMultipleFocusMatrix(getDataProvider(),
          delUcpAttr.getUcpaId(), false, CmdModMultipleFocusMatrix.UPDATE_MODE.NULLIFY_UCPA, attribute.getID(),
          dbUseCaseSection.getTabvUseCas().getUseCaseId(), ucsId);
      this.childCmdStack.addCommand(cmdModMultipleFocusMatrix);
    }
    setUserDetails(COMMAND_MODE.DELETE, delUcpAttr, MAPPING_ENTITY_ID + numItem);
    dbUseCaseSection.getTabvUcpAttrs().remove(delUcpAttr);
    getEntityProvider().deleteEntity(delUcpAttr);
    getChangedData().put(delUcpAttr.getUcpaId(),
        new ChangedData(ChangeType.DELETE, delUcpAttr.getUcpaId(), TabvUcpAttr.class, DisplayEventSource.COMMAND));
    getDataCache().getUseCaseSection(ucsId).getAttributes().remove(attribute);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() {
    final Attribute attribute = getDataCache().getAttribute(this.attrID);
    TabvUcpAttr delUcpAttr;
    if (this.mappingType == MAP_TYPE_UC) {
      delUcpAttr = getDataCache().getUseCase(this.usecaseID).getMappingEntity(attribute);
      getEntityProvider().deleteEntity(delUcpAttr);
    }
    else if (this.mappingType == MAP_TYPE_SECTION) {
      delUcpAttr = getDataCache().getUseCaseSection(this.ucsID).getMappingEntity(attribute);
      getEntityProvider().deleteEntity(delUcpAttr);
    }
    else {
      for (AbstractUseCaseItem ucs : this.ucItemsToUpdate) {
        TabvUcpAttr ucpAttr = null;
        if (ucs instanceof UseCaseSection) {
          ucpAttr = getDataCache().getUseCaseSection(ucs.getID()).getMappingEntity(attribute);
        }
        else if (ucs instanceof UseCase) {
          ucpAttr = getDataCache().getUseCase(ucs.getID()).getMappingEntity(attribute);
        }
        getEntityProvider().deleteEntity(ucpAttr);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // Not applicable
  }


  /**
   * {@inheritDoc}
   *
   * @throws CommandException this method throws CommandException
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    executeInsertCommand();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    String objectIdentifier;
    final Attribute attribute = getDataCache().getAttribute(this.attrID);
    if (this.commandMode == COMMAND_MODE.INSERT) {
      if (this.mappingType == MAP_TYPE_UC) {
        objectIdentifier = "Mapped Attribute " + attribute.getAttributeName() + " with Use case " +
            getDataCache().getUseCase(this.usecaseID).getName();
      }
      else if (this.mappingType == MAP_TYPE_SECTION) {
        objectIdentifier = "Mapped Attribute " + attribute.getAttributeName() + " with Use case Section " +
            getDataCache().getUseCaseSection(this.ucsID).getName();
      }
      else {
        objectIdentifier = "Mapped Attribute with multiple Use case Sections";
      }
    }
    else {
      if (this.mappingType == MAP_TYPE_UC) {
        objectIdentifier = "Removed mapping between Attribute " + attribute.getAttributeName() + " and Use case " +
            getDataCache().getUseCase(this.usecaseID).getName();
      }
      else if (this.mappingType == MAP_TYPE_SECTION) {
        objectIdentifier = "Removed mapping between Attribute " + attribute.getAttributeName() +
            " and Use case Section" + getDataCache().getUseCaseSection(this.ucsID).getName();
      }
      else {
        objectIdentifier = "Removed mapping between Attribute with multiple Use case Sections";
      }
    }
    return super.getString("", objectIdentifier);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Attribute & usecaseitem are refreshed after insert and delete operations
    if (this.mappingType == MAP_TYPE_UC) {
      final TabvUseCase dbUsecase = getEntityProvider().getDbUseCase(this.usecaseID);
      // Refresh the dbUsecase entity
      getEntityProvider().refreshCacheObject(dbUsecase);
    }
    else if (this.mappingType == MAP_TYPE_SECTION) {
      final TabvUseCaseSection dbUCS = getEntityProvider().getDbUseCaseSection(this.ucsID);
      // Refresh the dbUsecaseSection entity
      getEntityProvider().refreshCacheObject(dbUCS);
    }
    else {
      for (AbstractUseCaseItem ucItem : this.ucItemsToUpdate) {
        if (ucItem instanceof UseCaseSection) {
          final TabvUseCaseSection dbUCS = getEntityProvider().getDbUseCaseSection(ucItem.getID());
          // Refresh the dbUsecaseSection entity
          getEntityProvider().refreshCacheObject(dbUCS);
        }
        else if (ucItem instanceof UseCase) {
          final TabvUseCase dbUseCase = getEntityProvider().getDbUseCase(ucItem.getID());
          // Refresh the dbUsecaseSection entity
          getEntityProvider().refreshCacheObject(dbUseCase);
        }
      }
    }
  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {

    final Attribute attribute = getDataCache().getAttribute(this.attrID);
    switch (this.commandMode) {
      case INSERT:
        doInsertRollBack(attribute);
        break;
      case DELETE:
        doDeleteRollBack(attribute);
        break;
      default:
        // Do nothing
        break;
    }

  }

  /**
   * @param attribute Do Roll back after delete Failure
   */
  private void doDeleteRollBack(final Attribute attribute) {

    if (this.mappingType == MAP_TYPE_SECTION) {
      getDataCache().getUseCaseSection(this.ucsID).getAttributes()
          .add(getDataCache().getAttribute(attribute.getAttributeID()));
    }
    else if (this.mappingType == MAP_TYPE_UC) {
      getDataCache().getUseCase(this.usecaseID).getAttributes()
          .add(getDataCache().getAttribute(attribute.getAttributeID()));
    }
    else {
      for (AbstractUseCaseItem ucItem : this.ucItemsToUpdate) {
        if ((ucItem instanceof UseCaseSection) || (ucItem instanceof UseCase)) {
          getDataCache().getUseCase(ucItem.getID()).getAttributes().add(attribute);
        }
      }
    }
  }

  /**
   * @param attribute Do Roll back after Insertion Failure
   */
  private void doInsertRollBack(final Attribute attribute) {

    if (this.mappingType == MAP_TYPE_SECTION) {
      getDataCache().getUseCaseSection(this.ucsID).getAttributes().remove(attribute);
    }
    else if (this.mappingType == MAP_TYPE_UC) {
      getDataCache().getUseCase(this.usecaseID).getAttributes().remove(attribute);
    }
    else {
      if (this.ucItemsToUpdate != null) {
        for (AbstractUseCaseItem ucItem : this.ucItemsToUpdate) {
          if ((ucItem instanceof UseCaseSection) || (ucItem instanceof UseCase)) {
            getDataCache().getUseCaseSection(ucItem.getID()).getAttributes().remove(attribute);
          }
        }
      }
    }
  }

  /**
   * {@inheritDoc} return usecase section id to which the attribute is mapped
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.ucsID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Attribute Mapping";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdins(detailsList);
        break;
      case DELETE:
        caseCmdDel(detailsList);
        break;
      default:
        // Do nothing
        break;
    }
    String objectIdentifier = null;
    if (this.mappingType == MAP_TYPE_UC) {
      objectIdentifier = "Use Case";
    }
    else if (this.mappingType == MAP_TYPE_SECTION) {
      objectIdentifier = "Use Case Section";
    }
    this.summaryData.setObjectName(objectIdentifier);
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);

    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmdDel(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue(getPrimaryObjectIdentifier());
    details.setNewValue("");
    details.setModifiedItem(getDataCache().getAttribute(this.attrID).getAttributeName());
    detailsList.add(details);
  }

  /**
   * @param detailsList
   */
  private void caseCmdins(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getDataCache().getAttribute(this.attrID).getAttributeName());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier;
    if (this.commandMode == COMMAND_MODE.INSERT) {
      if (this.mappingType == MAP_TYPE_UC) {
        objectIdentifier = getDataCache().getUseCase(this.usecaseID).getName();
      }
      else if (this.mappingType == MAP_TYPE_SECTION) {
        objectIdentifier =

            getDataCache().getUseCaseSection(this.ucsID).getName();
      }
      else {
        objectIdentifier = "Mapped Attribute with multiple Use case Sections";
      }
    }
    else {
      if (this.mappingType == MAP_TYPE_UC) {
        objectIdentifier = getDataCache().getUseCase(this.usecaseID).getName();
      }
      else if (this.mappingType == MAP_TYPE_SECTION) {
        objectIdentifier = getDataCache().getUseCaseSection(this.ucsID).getName();
      }
      else {
        objectIdentifier = "Removed mapping between Attribute with multiple Use case Sections";
      }
    }
    return objectIdentifier;
  }

}
