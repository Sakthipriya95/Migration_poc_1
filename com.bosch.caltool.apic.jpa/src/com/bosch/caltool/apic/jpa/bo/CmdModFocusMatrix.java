/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdModFocusMatrix - Command handles db operations like INSERT, UPDATE on T_FOCUS_MATRIX
 *
 * @author dmo5cob
 */
public class CmdModFocusMatrix extends AbstractCommand {


  /**
   * Old comment
   */
  private String oldComment;
  /**
   * New comment
   */
  private String newComment;
  /**
   * new color
   */
  private String newColor;
  /**
   * old color
   */
  private String oldColor;
  /**
   * Old deleted flag
   */
  private boolean oldDeletedFlag;
  /**
   * New deleted flag
   */
  private boolean newDeletedFlag;

  /**
   * focus matrix to be updated
   */
  private final FocusMatrixDetails existingFocusMatrix;
  /**
   * the entity which is added newly
   */
  private TFocusMatrix newDbFocMatrix;
  /**
   * Entity id
   */
  private static final String FOCUS_MATRIX_ENTITY_ID = "FOCUS_MATRIX_ENTITY_ID";

  /**
   * Store the transactionSummary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * Focus Matrix version
   */
  // ICDM-2569
  private final FocusMatrixVersion fmVersion;
  /**
   * The mapped entity object
   */
  private TabvUcpAttr dbUcpAttr;
  /**
   * Use case id
   */
  private final Long usecaseID;
  /**
   * Use case section id
   */
  private final Long ucsID;
  /**
   * Attr id
   */
  private final Long attributeId;
  /**
   * Deleted flag
   */
  private final boolean canBeSoftDeleted;

  /**
   * old link string
   */
  private String oldLink;

  /**
   * new link string
   */
  private String newLink;

  /**
   * old ucpAttrId
   */
  private Long oldUcpAttrId;

  /**
   * new ucpAttrId
   */
  private Long newUcpAttrId;
  /**
   * old section id
   */
  private Long oldSectionId;

  /**
   * new section id
   */
  private Long newSectionId;


  /**
   * @param dataProvider AbstractDataProvider instance
   * @param fmVersion Focus matrix version
   * @param ucpAttrId use case attribute mapping id
   * @param usecaseID usecase id
   * @param ucsID usecase section id
   * @param attributeId attribute id
   * @param canBeSoftDeleted to update deleted flag
   */
  public CmdModFocusMatrix(final AbstractDataProvider dataProvider, final FocusMatrixVersion fmVersion,
      final Long ucpAttrId, final Long usecaseID, final Long ucsID, final Long attributeId,
      final boolean canBeSoftDeleted) {
    super(dataProvider);

    this.fmVersion = fmVersion;
    this.oldUcpAttrId = ucpAttrId;

    ConcurrentMap<Long, FocusMatrixDetails> attrFmMap = fmVersion.getFocusMatrixItemMap().get(attributeId);
    Long ucItemID = ucsID == null ? usecaseID : ucsID;

    this.existingFocusMatrix = attrFmMap == null ? null : attrFmMap.get(ucItemID);

    if (null == this.existingFocusMatrix) {
      // below case happens if
      // 1. Inserting new focus matrix
      // 2. we Rt click & choose "Set as Relevant for Focus Matrix" in Focus matrix page with the attribute and use case
      // item not having focus matrix definition.
      this.commandMode = COMMAND_MODE.INSERT;

    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
      initialize(this.existingFocusMatrix);
    }

    this.usecaseID = usecaseID;

    this.ucsID = ucsID;

    this.attributeId = attributeId;
    this.canBeSoftDeleted = canBeSoftDeleted;
  }

  /**
   * @param existingFocusMatrix
   */
  private void initialize(@SuppressWarnings("hiding") final FocusMatrixDetails existingFocusMatrix) {
    // comment
    this.oldComment = existingFocusMatrix.getComments();
    this.newComment = this.oldComment;
    // color
    this.oldColor = existingFocusMatrix.getColorCode().getColor();
    this.newColor = this.oldColor;

    this.oldDeletedFlag = existingFocusMatrix.isDeleted();
    this.newDeletedFlag = this.oldDeletedFlag;

    // link
    this.oldLink = existingFocusMatrix.getLink();
    this.newLink = this.oldLink;

    // ucpAttrId
    this.oldUcpAttrId = existingFocusMatrix.getUcpaId();
    this.newUcpAttrId = this.oldUcpAttrId;

    // Section id
    this.oldSectionId = existingFocusMatrix.getUseCaseSectionId();
    this.newSectionId = this.oldSectionId;
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
    this.newDbFocMatrix = new TFocusMatrix();
    setNewFocusMatrixFields(this.newDbFocMatrix);
    // set created date and user
    setUserDetails(COMMAND_MODE.INSERT, this.newDbFocMatrix, FOCUS_MATRIX_ENTITY_ID);
    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(this.newDbFocMatrix);

    // ICDM-2569
    getEntityProvider().getDbFocuMatrixVersion(getFmVersion().getID()).addTFocusMatrix(this.newDbFocMatrix);

    getFmVersion().resetFocusMatrixDefinitionLoaded();

    getChangedData().put(this.newDbFocMatrix.getFmId(), new ChangedData(ChangeType.INSERT,
        this.newDbFocMatrix.getFmId(), TFocusMatrix.class, DisplayEventSource.COMMAND));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    validateStaleData(this.existingFocusMatrix);
    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.existingFocusMatrix.getID(), TFocusMatrix.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getFocusMatrix(this.existingFocusMatrix.getID()).getObjectDetails());
    final TFocusMatrix existingFocusMatrixDb = getEntityProvider().getDbFocuMatrix(this.existingFocusMatrix.getID());
    // below case happens if we "Set as Not Relevant" in Focus matrix page
    if (this.canBeSoftDeleted) {
      existingFocusMatrixDb.setDeletedFlag(ApicConstants.YES);
    }
    else {
      // below case happens if we add child section in use case editor
      if ((this.newSectionId != null) && !CommonUtils.isEqual(this.oldSectionId, this.newSectionId)) {
        existingFocusMatrixDb.setTabvUseCaseSection(getEntityProvider().getDbUseCaseSection(this.newSectionId));
      }
      // below case happens if we unmap an attribute in use case, then newUcpAttrId=null, oldUcpAttrId=<old-existing-id>
      else if ((this.newUcpAttrId == null) && !CommonUtils.isEqual(this.oldUcpAttrId, this.newUcpAttrId)) {
        existingFocusMatrixDb.setTabvUcpAttr(null);
        existingFocusMatrixDb.setDeletedFlag(ApicConstants.YES);
      }
      // below case happens if
      // 1. we map an attribute in use case, then newUcpAttrId= <new-inserted-id>, oldUcpAttrId =null
      // 2. we Rt click & choose "Set as Relevant for Focus Matrix" in Focus matrix page with the attribute and use case
      // item already having focus matrix definition.
      else if (CommonUtils.isEqual(this.oldSectionId, this.newSectionId)) {
        existingFocusMatrixDb.setComments(this.newComment);
        existingFocusMatrixDb.setColorCode(this.newColor);
        existingFocusMatrixDb.setDeletedFlag(ApicConstants.CODE_NO);
        existingFocusMatrixDb.setLink(this.newLink);
        TabvUcpAttr tabVUcpAttr =
            this.newUcpAttrId == null ? null : getEntityProvider().getDbUcpAttr(this.newUcpAttrId);
        existingFocusMatrixDb.setTabvUcpAttr(tabVUcpAttr);
      }
    }
    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, existingFocusMatrixDb, FOCUS_MATRIX_ENTITY_ID);
    getChangedData().put(existingFocusMatrixDb.getFmId(), chdata);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Not Applicable
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
    return isStringChanged(this.oldComment, this.newComment) || isStringChanged(this.oldColor, this.newColor) ||
        (this.oldDeletedFlag != this.newDeletedFlag) || isStringChanged(this.oldLink, this.newLink) ||
        !CommonUtils.isEqual(this.oldUcpAttrId, this.newUcpAttrId) ||
        !CommonUtils.isEqual(this.oldSectionId, this.newSectionId);

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
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        addTransactionSummaryDetails(detailsList, this.oldColor, this.newColor, "Color");
        addTransactionSummaryDetails(detailsList, this.oldComment, this.newComment, "Comment");
        break;
      default:
        // Do nothing
        break;
    }

    this.summaryData.setObjectName(getPrimaryObjectIdentifier());
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);

    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(null == this.dbUcpAttr ? ""
        : getDataProvider().getAttribute(this.dbUcpAttr.getTabvAttribute().getAttrId()).getAttributeName());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    switch (this.commandMode) {

      case INSERT:
        doAfterInsert();
        break;
      default:
        break;
    }

  }


  /**
   * Update the cache after insert operation
   */
  protected void doAfterInsert() {

    FocusMatrixDetails fmDetails = getDataCache().getFocusMatrix(this.newDbFocMatrix.getFmId());

    Map<Long, ConcurrentMap<Long, FocusMatrixDetails>> fmMap = getFmVersion().getFocusMatrixItemMap();
    ConcurrentMap<Long, FocusMatrixDetails> childMap = fmMap.get(fmDetails.getAttributeId());
    if (CommonUtils.isNullOrEmpty(childMap)) {
      childMap = new ConcurrentHashMap<Long, FocusMatrixDetails>();
    }
    childMap.put(fmDetails.getUseCaseItem().getID(), fmDetails);

    // ICDM-2569
    getFmVersion().getFocusMatrixItemMap().put(fmDetails.getAttributeId(), childMap);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    if (CommonUtils.isNotNull(this.existingFocusMatrix)) {
      return this.existingFocusMatrix.getID();
    }
    else if (CommonUtils.isNotNull(this.newDbFocMatrix)) {
      return this.newDbFocMatrix.getFmId();
    }
    return null;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Focus Matrix";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    if (null != this.usecaseID) {
      return getDataProvider().getUseCase(this.usecaseID).getName();
    }
    else if (null != this.ucsID) {
      return getDataProvider().getUseCaseSection(this.ucsID).getName();
    }

    return "";
  }

  /**
   * Method sets the required fields of the TFocusMatrix entity
   *
   * @throws CommandException In case of parallel changes detected
   */
  private void setNewFocusMatrixFields(final TFocusMatrix dbFMatrix) throws CommandException {

    dbFMatrix.setTFocusMatrixVersion(getEntityProvider().getDbFocuMatrixVersion(getFmVersion().getID()));
    if (null != this.oldUcpAttrId) {
      this.dbUcpAttr = getEntityProvider().getDbUcpAttr(this.oldUcpAttrId);
      dbFMatrix.setTabvUcpAttr(this.dbUcpAttr);
    }
    if (null != this.ucsID) {
      dbFMatrix.setTabvUseCaseSection(getEntityProvider().getDbUseCaseSection(this.ucsID));
      dbFMatrix.setTabvUseCas(getEntityProvider().getDbUseCaseSection(this.ucsID).getTabvUseCas());
    }
    else {
      // Set UseCase to usecase attribute mapping
      dbFMatrix.setTabvUseCas(getEntityProvider().getDbUseCase(this.usecaseID));
    }
    // Set attribute to usecase attribute mapping
    dbFMatrix.setTabvAttribute(getEntityProvider().getDbAttribute(this.attributeId));


    dbFMatrix.setComments(this.newComment);
    dbFMatrix.setColorCode(this.newColor);
    dbFMatrix.setLink(this.newLink);

  }

  /**
   * @param newComment the newComment to set
   */
  public void setComment(final String newComment) {
    this.newComment = newComment;
  }


  /**
   * @param newColor the newColor to set
   */
  public void setColor(final String newColor) {
    this.newColor = newColor;
  }

  /**
   * @param newDeletedFlag the deleted flag to set
   */
  public void setDeletedFlag(final boolean newDeletedFlag) {
    this.newDeletedFlag = newDeletedFlag;
  }

  /**
   * @return the newDbFocMatrix
   */
  protected TFocusMatrix getNewDbFocMatrix() {
    return this.newDbFocMatrix;
  }

  /**
   * @param newlinkStr String
   */
  public void setLink(final String newlinkStr) {
    this.newLink = newlinkStr;

  }


  /**
   * @param newUcpAttrId Long
   */
  // iCDM-1614
  public void setNewUcpAttrId(final Long newUcpAttrId) {
    this.newUcpAttrId = newUcpAttrId;
  }

  /**
   * @param newSectionId Long
   */
  public void setSectionId(final Long newSectionId) {
    this.newSectionId = newSectionId;

  }

  /**
   * @return the fmVersion
   */
  // ICDM-2569
  FocusMatrixVersion getFmVersion() {
    return this.fmVersion;
  }
}
