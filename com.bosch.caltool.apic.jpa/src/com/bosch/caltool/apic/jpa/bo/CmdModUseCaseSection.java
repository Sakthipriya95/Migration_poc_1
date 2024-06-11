/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Command handles all db operations on INSERT, UPDATE, DELETE on usecase section
 *
 * @author mkl2cob icdm-299
 */
// ICDM-355
public class CmdModUseCaseSection extends AbstractCmdModUseCaseItem {

  /**
   * UCS Entity ID
   */
  private static final String UCS_ENTITY_ID = "UCS_ENTITY_ID";

  private boolean isParentUseCase;

  private UseCaseSection ucs;

  /**
   * Store the transactionSummary - ICDM-722
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Create a new command for inserting records
   *
   * @param dataProvider apic data provider
   * @param parent parent of this use case section. If this is a top level section, provide usecase . Otherwise usecase
   *          section
   */
  public CmdModUseCaseSection(final ApicDataProvider dataProvider, final AbstractUseCaseItem parent) {
    super(dataProvider, parent);
    setParentType();
  }

  /**
   * Create a new command for updating/deleting records
   *
   * @param dataProvider apic data provider
   * @param ucSection the usecase section to modify/delete
   * @param deleteFlag if delete/un-delete is not to be done, set this as null. else set either ApicConstants.YES or
   *          ApicConstants.NO
   */
  public CmdModUseCaseSection(final ApicDataProvider dataProvider, final UseCaseSection ucSection,
      final String deleteFlag) {
    super(dataProvider, ucSection, deleteFlag);
    this.ucs = ucSection;
    // ICDM-1558
    this.oldFocusMatrxMapping = this.ucs.isFocusMatrixRelevant(false);
  }


  /**
   * set true if the parent of the usecase section is an usecase
   */
  private void setParentType() {
    this.isParentUseCase = false;
    if (getParent() instanceof UseCase) {
      this.isParentUseCase = true;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    final TabvUseCaseSection dbUcs = new TabvUseCaseSection();

    dbUcs.setNameEng(getNewNameEng());
    dbUcs.setNameGer(getNewNameGer());
    dbUcs.setDescEng(getNewDescEng());
    dbUcs.setDescGer(getNewDescGer());
    if (this.newFocusMatrxMapping) {
      dbUcs.setFocusMatrixRelevant(ApicConstants.YES);
    }
    else {
      dbUcs.setFocusMatrixRelevant(ApicConstants.CODE_NO);
    }


    setUserDetails(COMMAND_MODE.INSERT, dbUcs, UCS_ENTITY_ID);

    // Setting the parent for the new usecase section

    if (this.isParentUseCase) {
      dbUcs.setTabvUseCas(getEntityProvider().getDbUseCase(getParent().getID()));
    }
    else {
      dbUcs.setTabvUseCaseSection(getEntityProvider().getDbUseCaseSection(getParent().getID()));
      dbUcs.setTabvUseCas(getEntityProvider().getDbUseCaseSection(getParent().getID()).getTabvUseCas());
    }

    getEntityProvider().registerNewEntity(dbUcs);

    // creating business object of usecase section
    this.ucs = new UseCaseSection(getDataProvider(), dbUcs.getSectionId());


    // Attributes From parent is mapped to the first Child
    mapAttributesInDB(dbUcs);
    // Attributes From parent is mapped to the first Child
    mapAttrInModel();

    mapFocusMatrixDetails(dbUcs);
    // Adding the created section to the usecase section map
    if (this.isParentUseCase) {
      getEntityProvider().getDbUseCase(getParent().getID()).getTabvUseCaseSections().add(dbUcs);
    }
    else {
      getEntityProvider().getDbUseCaseSection(getParent().getID()).getTabvUseCaseSections().add(dbUcs);
    }

    setUcItem(this.ucs);
    getDataCache().getAllUseCaseSectionMap().put(dbUcs.getSectionId(), this.ucs);


    // Add the section to the Parent
    if (this.isParentUseCase) {
      ((UseCase) getParent()).getUseCaseSections().add(this.ucs);

    }
    else {
      ((UseCaseSection) getParent()).getChildSections().add(this.ucs);
    }


    getChangedData().put(this.ucs.getID(),
        new ChangedData(ChangeType.INSERT, this.ucs.getID(), TabvUseCaseSection.class, DisplayEventSource.COMMAND));
  }

  /**
   * Map the Attributes from the Parent to the First Child in data model
   */
  private void mapAttrInModel() {
    // Attributes From parent is mapped to the first Child
    if (getParent().canMapAttributes()) {
      // if first child
      if (this.isParentUseCase) {
        // when parent is usecase
        // creating bo for the parent
        UseCase parentUc = (UseCase) getParent();
        // move attributes to child from parent in bo

        this.ucs.getAttributes().addAll(parentUc.getAttributes());
        // clear the parent attributes in bo
        parentUc.getAttributes().clear();

      }
      else {
        // when parent is usecase section
        // creating bo for the parent
        UseCaseSection parentUcs = (UseCaseSection) getParent();
        // move attributes to child from parent in bo
        this.ucs.getAttributes().addAll(parentUcs.getAttributes());
        // clear the parent attributes in bo
        parentUcs.getAttributes().clear();

      }
    }
  }

  /**
   * Map the Attributes from the Parent to the First Child in database
   *
   * @param dbUcs
   */
  private void mapAttributesInDB(final TabvUseCaseSection dbUcs) {

    if (getParent().canMapAttributes()) {
      // if first child
      if (this.isParentUseCase) {

        List<TabvUcpAttr> tabvUcpAttrs = (dbUcs.getTabvUseCas()).getTabvUcpAttrs();
        dbUcs.setTabvUcpAttrs(tabvUcpAttrs);
        // setting section for the TabvUcpAttr
        for (TabvUcpAttr dbUcpAttr : dbUcs.getTabvUcpAttrs()) {
          dbUcpAttr.setTabvUseCaseSection(dbUcs);
        }
      }
      else {
        List<TabvUcpAttr> ucpAttrs = new ArrayList<>(dbUcs.getTabvUseCaseSection().getTabvUcpAttrs());
        // setting section for the TabvUcpAttr
        for (TabvUcpAttr dbUcpAttr : dbUcs.getTabvUseCaseSection().getTabvUcpAttrs()) {
          dbUcpAttr.setTabvUseCaseSection(dbUcs);
          dbUcpAttr.setModifiedDate(getCurrentTime());
        }
        if (!ucpAttrs.isEmpty()) {
          dbUcs.setTabvUcpAttrs(ucpAttrs);
        }

      }
    }

  }

  /**
   * Map the focus matrix defenitions from the Parent to the First Child in database
   *
   * @param dbUcs
   * @throws CommandException
   */
  private void mapFocusMatrixDetails(final TabvUseCaseSection dbUcs) throws CommandException {

    if (getParent().canMapAttributes()) {
      CmdModMultipleFocusMatrix cmdModMultipleFocusMatrix;
      // if first child
      if (this.isParentUseCase) {
        cmdModMultipleFocusMatrix = new CmdModMultipleFocusMatrix(getDataProvider(), null, false,
            CmdModMultipleFocusMatrix.UPDATE_MODE.SECTIONID_UPDATE, null, dbUcs.getTabvUseCas().getUseCaseId(), null);


      }
      else {
        cmdModMultipleFocusMatrix = new CmdModMultipleFocusMatrix(getDataProvider(), null, false,
            CmdModMultipleFocusMatrix.UPDATE_MODE.SECTIONID_UPDATE, null,
            dbUcs.getTabvUseCaseSection().getTabvUseCas().getUseCaseId(), dbUcs.getTabvUseCaseSection().getSectionId());
      }
      cmdModMultipleFocusMatrix.setChildSectionId(dbUcs.getSectionId());
      this.childCmdStack.addCommand(cmdModMultipleFocusMatrix);


    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, getUcItem().getID(), TabvUseCaseSection.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getUseCaseSection(getUcItem().getID()).getObjectDetails());


    final TabvUseCaseSection dbUcs = getEntityProvider().getDbUseCaseSection(getUcItem().getID());
    validateStaleData(dbUcs);

    if (isNameChangedEng()) {
      dbUcs.setNameEng(getNewNameEng());
    }
    if (isNameChangedGer()) {
      dbUcs.setNameGer(getNewNameGer());
    }
    if (isDescChangedEng()) {
      dbUcs.setDescEng(getNewDescEng());
    }
    if (isDescChangedGer()) {
      dbUcs.setDescGer(getNewDescGer());
    }
    if (isDeletedFlagChanged()) {
      dbUcs.setDeletedFlag(getNewDeleteFlag());
    }
    if (isFocusMatrixMappingChanged()) { // ICDM-1558
      if (this.newFocusMatrxMapping) {
        dbUcs.setFocusMatrixRelevant(ApicConstants.YES);
      }
      else {
        dbUcs.setFocusMatrixRelevant(ApicConstants.CODE_NO);
      }
    }

    if (null != this.cmdMultipleLinks) {
      // update the links
      this.childCmdStack.addCommand(this.cmdMultipleLinks);
    }

    setUserDetails(COMMAND_MODE.UPDATE, dbUcs, UCS_ENTITY_ID);
    getChangedData().put(getUcItem().getID(), chdata);
  }

  /**
   * {@inheritDoc} Added Command For use Case Section icdm-298
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, getUcItem().getID(), TabvUseCaseSection.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getUseCaseSection(getUcItem().getID()).getObjectDetails());

    final TabvUseCaseSection dbUcs = getEntityProvider().getDbUseCaseSection(getUcItem().getID());
    validateStaleData(dbUcs);
    if (isDeletedFlagChanged()) {
      // Set Deleted Flag Yes to the Use Case Section
      dbUcs.setDeletedFlag(ApicConstants.YES);
    }

    setUserDetails(COMMAND_MODE.DELETE, dbUcs, UCS_ENTITY_ID);
    getChangedData().put(getUcItem().getID(), chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() {
    final TabvUseCaseSection dbUcs = getEntityProvider().getDbUseCaseSection(getUcItem().getID());
    // mapping back the attributes in Database
    mapBackAttrInDb(dbUcs);
    // mapping back the attributes in model/BO
    mapBackAttrInModel();
    getEntityProvider().deleteEntity(dbUcs);

    if (getParent() != null) {
      if (this.isParentUseCase) {
        getEntityProvider().getDbUseCase(getParent().getID()).getTabvUseCaseSections().remove(dbUcs);
      }
      else {
        getEntityProvider().getDbUseCaseSection(getParent().getID()).getTabvUseCaseSections().remove(dbUcs);
      }
    }

    getDataCache().getAllUseCaseSectionMap().remove(getUcItem().getID());

  }

  /**
   * Map back the Attributes from the First Child to the Parent in data model
   *
   * @param dbUcs
   */
  private void mapBackAttrInModel() {
    if (this.isParentUseCase && (getParent().getMappableItems().size() == 1)) {
      // when parent is usecase & first child
      // creating bo for the parent
      UseCase parentUc = (UseCase) getParent();
      // move attributes to parent from child
      parentUc.getAttributes().addAll(this.ucs.getAttributes());
    }
    else if (!this.isParentUseCase && (getParent().getMappableItems().size() == 1)) {
      // when parent is usecase section & first child
      // creating bo for the parent
      UseCaseSection parentUcs = (UseCaseSection) getParent();
      // move attributes to parent from child
      parentUcs.getAttributes().addAll(this.ucs.getAttributes());
    }
  }

  /**
   * Map back the Attributes from the First Child to the Parent in database
   *
   * @param dbUcs
   */
  private void mapBackAttrInDb(final TabvUseCaseSection dbUcs) {
    if (this.isParentUseCase && (getParent().getMappableItems().size() == 1)) {
      final TabvUseCase parentUc = getEntityProvider().getDbUseCase(getParent().getID());
      // adding UCP attributes to the patent from child if first child
      parentUc.setTabvUcpAttrs(dbUcs.getTabvUcpAttrs());
      // setting the section of UCP attr to null
      for (TabvUcpAttr dbUcpAttr : parentUc.getTabvUcpAttrs()) {
        dbUcpAttr.setTabvUseCas(dbUcs.getTabvUseCas());
        dbUcpAttr.setTabvUseCaseSection(null);
      }
    }
    else if (!this.isParentUseCase && (getParent().getMappableItems().size() == 1)) {
      final TabvUseCaseSection parentUcs = getEntityProvider().getDbUseCaseSection(getParent().getID());
      // adding UCP attributes to the patent from child if first child
      parentUcs.setTabvUcpAttrs(dbUcs.getTabvUcpAttrs());
      // setting the section of UCP attr to null
      for (TabvUcpAttr dbUcpAttr : parentUcs.getTabvUcpAttrs()) {
        dbUcpAttr.setTabvUseCas(dbUcs.getTabvUseCas());
        dbUcpAttr.setTabvUseCaseSection(null);
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    final TabvUseCaseSection dbUcs = getEntityProvider().getDbUseCaseSection(getUcItem().getID());
    validateStaleData(dbUcs);

    if (isNameChangedEng()) {
      dbUcs.setNameEng(getOldNameEng());
    }
    if (isNameChangedGer()) {
      dbUcs.setNameGer(getOldNameGer());
    }
    if (isDescChangedEng()) {
      dbUcs.setDescEng(getOldDescEng());
    }
    if (isDescChangedGer()) {
      dbUcs.setDescGer(getOldDescGer());
    }
    if (isDeletedFlagChanged()) {
      dbUcs.setDeletedFlag(getOldDeleteFlag());
    }
    if (isFocusMatrixMappingChanged()) { // ICDM-1558
      if (this.oldFocusMatrxMapping) {
        dbUcs.setFocusMatrixRelevant(ApicConstants.YES);
      }
      else {
        dbUcs.setFocusMatrixRelevant(ApicConstants.CODE_NO);
      }
    }

    setUserDetails(COMMAND_MODE.UPDATE, dbUcs, UCS_ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException on stale data
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    final TabvUseCaseSection dbUcs = getEntityProvider().getDbUseCaseSection(getUcItem().getID());
    validateStaleData(dbUcs);
    if (isDeletedFlagChanged()) {
      // Set Deleted Flag No to the Use Case Section
      dbUcs.setDeletedFlag(ApicConstants.CODE_NO);
    }

    setUserDetails(COMMAND_MODE.DELETE, dbUcs, UCS_ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // parent available only for INSERT
    if (COMMAND_MODE.INSERT == this.commandMode) {
      if (this.isParentUseCase) {
        getEntityProvider().refreshCacheObject(getEntityProvider().getDbUseCase(getParent().getID()));
      }
      else {
        getEntityProvider().refreshCacheObject(getEntityProvider().getDbUseCaseSection(this.ucs.getID()));
        getEntityProvider().refreshCacheObject(getEntityProvider().getDbUseCaseSection(getParent().getID()));
      }
    }
  }

  /**
   * @return get Db Use case Section
   */
  public TabvUseCaseSection getDbUseCaseSection() {
    return getEntityProvider().getDbUseCaseSection(this.ucs.getID());
  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        caseCmdIns();
        break;
      default:
        // Do nothing
        break;
    }
  }

  /**
   *
   */
  private void caseCmdIns() {
    getDataCache().getAllUseCaseSectionMap().remove(this.ucs.getID());
    // Add the section to the Parent
    if (this.isParentUseCase) {
      ((UseCase) getParent()).getUseCaseSections().remove(this.ucs);
    }
    else {
      ((UseCaseSection) getParent()).getChildSections().remove(this.ucs);
    }
  }

  /**
   * {@inheritDoc} return the id of the usecase section that is subjected to modification
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.ucs == null ? null : this.ucs.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Use Case Section";
  }

  /**
   * ICDM-722 {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdInsert(detailsList);
        break;
      case UPDATE:
        caseCmdUpdate(detailsList);
        break;
      case DELETE:
        // no details section necessary in case of delete (parent row is sufficient in transansions view)
        if (isDeletedFlagChanged()) {
          this.summaryData.setOperation("DELETE");
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
  private void caseCmdUpdate(final SortedSet<TransactionSummaryDetails> detailsList) {
    addTransactionSummaryDetails(detailsList, getOldDescEng(), getNewDescEng(), "Description (English)");
    addTransactionSummaryDetails(detailsList, getOldDescGer(), getNewDescGer(), "Description (German)");
    addTransactionSummaryDetails(detailsList, getOldNameEng(), getNewNameEng(), "Name (English)");
    addTransactionSummaryDetails(detailsList, getOldNameGer(), getNewNameGer(), "Name  (German)");
    addTransactionSummaryDetails(detailsList, Boolean.toString(this.oldFocusMatrxMapping),
        Boolean.toString(this.newFocusMatrxMapping), "Relevant to Focus Matrix");

    this.summaryData.setObjectName(getNewNameEng());
  }

  /**
   * @param detailsList
   */
  private void caseCmdInsert(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * @param cmdMultipleLinks CmdModMultipleLinks
   */
  public void setCmdMultipleLinks(final CmdModMultipleLinks cmdMultipleLinks) {
    this.cmdMultipleLinks = cmdMultipleLinks;

  }


}
