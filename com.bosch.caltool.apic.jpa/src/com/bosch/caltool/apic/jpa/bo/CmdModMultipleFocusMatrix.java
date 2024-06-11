/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;


/**
 * @author mkl2cob
 */
public class CmdModMultipleFocusMatrix extends AbstractCommand {

  /**
   * child command stack
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);


  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Map of selected items
   */
  private final Map<FocusMatrixAttribute, List<FocusMatrixUseCaseItem>> fetchSelectedUcItems;

  /**
   * new color code
   */
  private String colorCode;

  /**
   * new comment for focus matrix
   */
  private String comment;

  /**
   * PIDCVersion instance
   */
  private final PIDCVersion pidcVersion;

  /**
   * The mapped attributes id
   */
  private final Long ucpAttrId;

  /**
   * existing Focus Matrix List
   */
  private List<FocusMatrixDetails> existingFocusMatrixList;

  /**
   * use case Id
   */
  private final Long usecaseID;

  /**
   * use case section Id
   */
  private final Long ucsID;

  /**
   * child section id
   */
  private Long childSectionId;


  /**
   * @author svj7cob
   */
  public static enum UPDATE_MODE {

                                  /**
                                   * Normal updation
                                   */
                                  NORMAL,

                                  /**
                                   * Updating ucpa_id as null
                                   */
                                  NULLIFY_UCPA,

                                  /**
                                   * Updating ucpa_id as value
                                   */
                                  NOT_NULLIFY_UCPA,

                                  /**
                                   * Update the section id
                                   */
                                  SECTIONID_UPDATE,
                                  /**
                                   * Update the fm details
                                   */
                                  FM_DETAILS_UPDATE
  }

  /**
   * update mode
   */
  private final UPDATE_MODE updateMode;


  /**
   * storing the child commands for doing post commit changes
   */
  private final List<CmdModFocusMatrix> cmdlist = new ArrayList<>();

  /**
   * link string
   */
  private String link;

  /**
   * @param dataProvider AbstractDataProvider
   * @param fetchSelectedUcItems Map<FocusMatrixAttribute, List<FocusMatrixUseCaseItem>>
   * @param pidcVers PIDCVersion
   */
  public CmdModMultipleFocusMatrix(final AbstractDataProvider dataProvider,
      final Map<FocusMatrixAttribute, List<FocusMatrixUseCaseItem>> fetchSelectedUcItems, final PIDCVersion pidcVers) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.UPDATE;
    this.fetchSelectedUcItems = fetchSelectedUcItems;
    this.pidcVersion = pidcVers;
    this.ucpAttrId = null;
    this.usecaseID = null;
    this.ucsID = null;
    this.updateMode = UPDATE_MODE.NORMAL;
  }

  /**
   * use this constructor to delete / update the list of Focus Matrix
   *
   * @param dataProvider AbstractDataProvider instance
   * @param ucpAttrId use case attribute mapping id
   * @param isDelete delete flag
   * @param mode
   * @param attrId
   * @param usecaseID
   * @param ucsId
   */
  CmdModMultipleFocusMatrix(final AbstractDataProvider dataProvider, final Long ucpAttrId, final boolean isDelete,
      final UPDATE_MODE mode, final Long attrId, final Long usecaseID, final Long ucsId) {
    super(dataProvider);
    if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    this.ucpAttrId = ucpAttrId;
    this.pidcVersion = null;
    this.fetchSelectedUcItems = null;
    this.updateMode = mode;
    this.usecaseID = usecaseID;
    this.ucsID = ucsId;
    this.existingFocusMatrixList =
        ((ApicDataProvider) dataProvider).getFocusMatrix(this.usecaseID, this.ucsID, attrId, null);
  }

  /**
   * use this constructor to reset the list of Focus Matrix
   *
   * @param dataProvider AbstractDataProvider instance
   * @param ucpAttrId use case attribute mapping id
   * @param isDelete delete flag
   * @param mode
   * @param attrId
   * @param usecaseID
   * @param ucsId
   */
  CmdModMultipleFocusMatrix(final AbstractDataProvider dataProvider, final UPDATE_MODE mode,
      final List<FocusMatrixDetails> existingFocusMatrixList) {
    super(dataProvider);

    this.commandMode = COMMAND_MODE.UPDATE;

    this.ucpAttrId = null;
    this.pidcVersion = null;
    this.fetchSelectedUcItems = null;
    this.updateMode = mode;
    this.usecaseID = null;
    this.ucsID = null;
    // Task 290992 : Mutable members should not be stored or returned directly
    this.existingFocusMatrixList = new ArrayList<>(existingFocusMatrixList);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    if (this.updateMode == UPDATE_MODE.NORMAL) {
      for (FocusMatrixAttribute fmAttribute : this.fetchSelectedUcItems.keySet()) {
//        for (FocusMatrixUseCaseItem fmUcItem : this.fetchSelectedUcItems.get(fmAttribute)) {
//
//          Map<String, Long> useCaseItemIds = ApicBOUtil.getUseCaseItemIds(fmUcItem.getUseCaseItem());
//          CmdModFocusMatrix command = new CmdModFocusMatrix(getDataProvider(), fmUcItem.getFocusMatrixVersion(),
//              fmUcItem.getAttributeMapping().get(fmAttribute.getAttribute()),
//              useCaseItemIds.get(ApicConstants.UC_NODE_TYPE), useCaseItemIds.get(ApicConstants.UCS_NODE_TYPE),
//              fmAttribute.getAttribute().getAttributeID(), false);
//
//          setNewValues(command);
//
//          this.childCmdStack.addCommand(command);
//          this.cmdlist.add(command);
//        }
      }
    }
    else {
      // ICDM-1614
      for (FocusMatrixDetails focusMatrixDetails : this.existingFocusMatrixList) {
        Long ucaseID = focusMatrixDetails.getUseCaseId();
        Long ucsectionID = focusMatrixDetails.getUseCaseSectionId();
        Long attributeId = focusMatrixDetails.getAttributeId();
        CmdModFocusMatrix cmdModFocusMatrix = new CmdModFocusMatrix(getDataProvider(),
            focusMatrixDetails.getFocusMatrixVersion(), this.ucpAttrId, ucaseID, ucsectionID, attributeId, false);
        if (this.updateMode == UPDATE_MODE.NULLIFY_UCPA) {
          // updating ucpa_id as null in focus matrix, when deleting the ucp Attributes
          cmdModFocusMatrix.setNewUcpAttrId(null);
        }
        else if (this.updateMode == UPDATE_MODE.NOT_NULLIFY_UCPA) {
          // updating ucpa_id in focus matrix, when inserting the ucp Attributes
          cmdModFocusMatrix.setNewUcpAttrId(this.ucpAttrId);
        }
        else if ((this.updateMode == UPDATE_MODE.SECTIONID_UPDATE) && (null != this.childSectionId)) {

          cmdModFocusMatrix.setSectionId(this.childSectionId);

        }
        else if (this.updateMode == UPDATE_MODE.FM_DETAILS_UPDATE) {

          setNewValues(cmdModFocusMatrix);

        }
        this.childCmdStack.addCommand(cmdModFocusMatrix);
        this.cmdlist.add(cmdModFocusMatrix);

      }
    }
  }

  /**
   * @param command CmdModFocusMatrix
   */
  void setNewValues(final CmdModFocusMatrix command) {
    if (null != this.colorCode) {
      command.setColor(this.colorCode);
    }

    if (null != this.comment) {
      command.setComment(this.comment);
    }

    if (null != this.link) {
      command.setLink(this.link);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub

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
    this.childCmdStack.undoAll();
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
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", getPrimaryObjectIdentifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();

    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   *
   */
  @Override
  protected void doPostCommit() {

    /**
     * For INSERT this loop is to add focus matrix to data cache, For UPDATE cache updated automatically
     */
    for (CmdModFocusMatrix command : this.cmdlist) {
      createNewDMForFocMatrx(command);
    }

  }

  /**
   * @param command CmdModFocusMatrix
   */
  private void createNewDMForFocMatrx(final CmdModFocusMatrix command) {
    TFocusMatrix newDbFocMatrix = command.getNewDbFocMatrix();

    if (null != newDbFocMatrix) {
      FocusMatrixDetails fmDetails = getDataCache().getFocusMatrix(newDbFocMatrix.getFmId());
      FocusMatrixVersion fmVers = command.getFmVersion();

      Map<Long, ConcurrentMap<Long, FocusMatrixDetails>> fmMap = fmVers.getFocusMatrixItemMap();
      ConcurrentMap<Long, FocusMatrixDetails> childMap = fmMap.get(fmDetails.getAttributeId());
      if (childMap.isEmpty()) {
        childMap = new ConcurrentHashMap<Long, FocusMatrixDetails>();
      }
      childMap.put(fmDetails.getUseCaseItem().getID(), fmDetails);

      fmVers.getFocusMatrixItemMap().put(fmDetails.getAttributeId(), childMap);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return (this.pidcVersion == null) ? this.ucpAttrId : this.pidcVersion.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Multiple Focus Matrix Items";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return (this.pidcVersion == null) ? "Multiple Focus Matrix Items" : this.pidcVersion.getName();
  }

  /**
   * @param setColorInCommand String
   */
  public void setColorCode(final String setColorInCommand) {
    this.colorCode = setColorInCommand;
  }

  /**
   * @param remarks String
   */
  public void setComment(final String remarks) {
    this.comment = remarks;
  }

  /**
   * @param newlinkStr String
   */
  public void setLink(final String newlinkStr) {
    this.link = newlinkStr;
  }


  /**
   * @param childSectionId the childSectionId to set
   */
  public void setChildSectionId(final Long childSectionId) {
    this.childSectionId = childSectionId;
  }

}
