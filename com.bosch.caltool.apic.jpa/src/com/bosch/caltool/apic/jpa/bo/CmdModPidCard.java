/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Command handles all db operations on INSERT, UPDATE, DELETE on PID card
 *
 * @author bne4cob
 */
public class CmdModPidCard extends AbstractCmdModProject {

  /**
   * The PIDC being modified
   */
  private PIDCard pidCard;

  /**
   * pidc id
   */
  private Long dbPidcId;

  /**
   * Tree node to which the PIDC is created.
   */
  private PIDCNode pidcNode;

  /**
   * Unique entity id for setting user details
   */
  private static final String PIDC_ENTITY_ID = "PIDC_ENTITY_ID";
  /**
   * Store the transactionSummary - ICDM-721
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Flag to indicate updation of VCDM Transfer User and VCDM Transfer Date fields when Transfer to VCDM is invoked
   */
  private boolean updateAPRJModTimestamp;
  /**
   * Old APRJ Id
   */
  private Long oldAPRJID;
  /**
   * New APRJ Id
   */
  private Long newAPRJID;
  /**
   * Old pro rev id
   */
  private Long oldProRevId;
  /**
   * New proRevId
   */
  private Long newProRevId;

  /**
   * CmdModPidcVersion instance
   */
  private CmdModPidcVersion pidcVrsnCmd;

  private AliasDefinition aliasDefinition;

  /**
   * ICDM-2363 project usecases
   */
  private List<AbstractUseCaseItem> projUcItems;


  /**
   * Constructor for creating a new PIDC.
   *
   * @param dataProvider the APIC data provider
   * @param pidcNode tree node to which the PIDC is created.
   */
  public CmdModPidCard(final ApicDataProvider dataProvider, final PIDCNode pidcNode) {
    super(dataProvider);
    this.pidcNode = pidcNode;
    this.commandMode = COMMAND_MODE.INSERT;
    this.pidcVrsnCmd = new CmdModPidcVersion(getDataProvider());
  }

  /**
   * Constructor for update/delete actions
   *
   * @param dataProvider the Apic Data provider
   * @param pidcToModify the PIDC that is being modified
   * @param delete whether the command is to delete the PIDC or not
   */
  public CmdModPidCard(final ApicDataProvider dataProvider, final PIDCard pidcToModify, final boolean delete) {
    super(dataProvider);

    this.pidCard = pidcToModify;
    // Icdm-229
    this.dbPidcId = pidcToModify.getID();
    this.attrValName = pidcToModify.getName();

    if (delete) {
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
      this.oldNameEng = this.pidCard.getPidcNameEng();
      this.oldNameGer = this.pidCard.getPidcNameGer();
      this.oldDescEng = this.pidCard.getPidcDescEng();
      this.oldDescGer = this.pidCard.getPidcDescGer();
      this.newNameEng = this.oldNameEng;
      this.newNameGer = this.oldNameGer;
      this.newDescEng = this.oldDescEng;
      this.newDescGer = this.oldDescGer;
      this.oldAPRJID = this.pidCard.getAPRJID();
      this.newAPRJID = this.oldAPRJID;
      this.oldProRevId = this.pidCard.getProRevID();
      this.newProRevId = this.oldProRevId;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeInsertCommand() throws CommandException {

    this.childCmdStack.clear();

    createPidcRecord();

    createStructAttrs();

    createAccessRights();

    createVersion();

    createProjectUsecases();

  }


  /**
   * ICDM-2363
   *
   * @throws CommandException
   */
  private void createProjectUsecases() throws CommandException {
    getEntityProvider().getEm().flush();
    if (null != this.projUcItems) {
      for (AbstractUseCaseItem ucItem : this.projUcItems) {
        CmdModFavUseCaseItem cmdFavUCItem = new CmdModFavUseCaseItem(getDataProvider(), this.pidCard, ucItem);
        this.childCmdStack.addCommand(cmdFavUCItem);
      }
    }
  }

  /**
   * Create PIDCVersion
   */
  private void createVersion() throws CommandException {
    this.pidcVrsnCmd.setPidCard(this.pidCard);
    this.childCmdStack.addCommand(this.pidcVrsnCmd);
  }

  /**
   * ICdm-1117 create mail for Hotine for new PIDC created Send mail for Hotline
   */
  private void createMailForHotline() {

    Stack<String> nodeStack = getNodeStack();
    int size = nodeStack.size();
    final StringBuilder attrLevelStrBuilder = new StringBuilder();
    for (int i = 0; i < size; i++) {
      attrLevelStrBuilder.append(nodeStack.pop());
      attrLevelStrBuilder.append(", ");
    }
    attrLevelStrBuilder.deleteCharAt(attrLevelStrBuilder.length() - ApicConstants.COLUMN_INDEX_2);
    ApicUser currentUser = getDataProvider().getCurrentUser();
    // Implement a new method and send the mail
    final MailHotline mailHotline = getHotlineNotifier();
    final String subject = getDataProvider().getParameterValue(ApicConstants.MAIL_NEW_PIDC);
    mailHotline.setSubject(subject);
    mailHotline.notifyNewPIDC(attrLevelStrBuilder.toString(), this.pidCard.getActiveVersion().getName(),
        currentUser.getDepartment(), currentUser.getFirstName(), currentUser.getLastName());

  }


  /**
   * Icdm-1117
   *
   * @return the node stack of Curent PIDC with Node Attr Name and Node Name
   */
  private Stack<String> getNodeStack() {
    PIDCNode nodeObj = this.pidcNode;
    Stack<String> nodeStack = new Stack<>();
    while (nodeObj != null) {
      nodeStack.push(CommonUtils.concatenate(nodeObj.getNodeAttr().getName(), " - ", nodeObj.getNodeName()));
      if (nodeObj.getNodeAttr().getAttrLevel() == ApicConstants.CUST_BRAND_ATTR_LEVEL.intValue()) {
        break;
      }
      nodeObj = nodeObj.getParent();
    }
    return nodeStack;
  }

  /**
   * Create the Pidc record
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createPidcRecord() throws CommandException {
    TabvAttrValue newDbNameValue;
    // Get the attribute value object for PIDC name
    if (this.nameValue == null) { // New value to be created.
      newDbNameValue = createNameValue(getDataProvider().getProjNameAttribute());

    }
    else { // Value is selected from the list.
      newDbNameValue = getEntityProvider().getDbValue(this.nameValue.getValueID());
    }

    this.attrValName = getDataProvider().getAttrValue(newDbNameValue.getValueId()).getTextValue();

    // Create PIDC entity icdm-229
    TabvProjectidcard dbPidc = new TabvProjectidcard();
    // set the alias definition for the Project.
    if (this.aliasDefinition != null) {
      dbPidc.setTaliasDefinition(getEntityProvider().getDbAliasDefinition(this.aliasDefinition.getID()));
    }
    dbPidc.setTabvAttrValue(newDbNameValue);

    dbPidc.setProRevId(Long.valueOf(1));

    setUserDetails(this.commandMode, dbPidc, PIDC_ENTITY_ID);

    getEntityProvider().registerNewEntity(dbPidc);
    this.dbPidcId = dbPidc.getProjectId();
    // Active version is not created yet. Will be initialized when the getter is invoked
    this.pidCard = new PIDCard(getDataProvider(), dbPidc.getProjectId(), null);
    this.pidCard.setLeafNode(this.pidcNode);
    this.pidcNode.getPidCardMap().put(this.pidCard.getID(), this.pidCard);


    // Icdm-474 dcn for Pid card
    updateTopLevelEntity();
  }


  /**
   * Create Project Attributes for pidc node structure. Here project attribute creation commands are called. They are
   * added to a stack for undoing
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createStructAttrs() throws CommandException {

    for (Entry<Long, AttributeValue> entry : this.pidcNode.getNodeStructureValues().entrySet()) {
      if (entry.getValue().getAttribute().getAttrLevel() == (ApicConstants.PIDC_ROOT_LEVEL + 1)) {
        createValueDependency(entry.getValue());
      }
    }
  }

  /**
   * Create the value dependency for the PIDC's name value to the value of the highest structure attribute (attribute
   * with level=1)
   *
   * @param dependentValue the value of the highest structure attribute of selected PIDC node
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createValueDependency(final AttributeValue dependentValue) throws CommandException {
    final TabvProjectidcard dbPidc = getEntityProvider().getDbPIDC(this.dbPidcId);
    final AttributeValue value = getDataProvider().getAttrValue(dbPidc.getTabvAttrValue().getValueId());
    final CmdModTabvDependencies command = new CmdModTabvDependencies(getDataProvider(), value);
    command.setNewAttrDepenID(dependentValue.getAttributeID());
    command.setNewAttrDepenValID(dependentValue.getValueID());
    this.childCmdStack.addCommand(command);
  }

  /**
   * Create a node access entity with the current user as the owner and full privileges. Command for node access
   * creation is used
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createAccessRights() throws CommandException {

    final CmdModNodeAccessRight cmdNodeAccess =
        new CmdModNodeAccessRight(getDataProvider(), this.pidCard, getDataProvider().getCurrentUser());
    cmdNodeAccess.setGrantOption(true);
    cmdNodeAccess.setIsOwner(true);
    cmdNodeAccess.setWriteAccess(true);
    this.childCmdStack.addCommand(cmdNodeAccess);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.pidCard.getID(), TabvProjectidcard.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidc(this.pidCard.getID()).getObjectDetails());

    final TabvProjectidcard dbPidc = getEntityProvider().getDbPIDC(this.dbPidcId);
    validateStaleData(dbPidc);

    if (isNameUpdated()) {
      validateStaleData(dbPidc.getTabvAttrValue());
      updateName(dbPidc.getTabvAttrValue().getValueId());
    }


    setUserDetails(COMMAND_MODE.UPDATE, dbPidc, PIDC_ENTITY_ID);
    // ICDM-1344
    dbPidc.setAprjId(this.newAPRJID);

    if (this.updateAPRJModTimestamp) {
      dbPidc.setVcdmTransferDate(getCurrentTime());
      dbPidc.setVcdmTransferUser(getDataCache().getAppUsername());
    }

    if (isProRevIdUpdated()) {
      dbPidc.setProRevId(this.newProRevId);

      // the active version is set to null so that in the PIDCard BO class the active version is again fetched from db
      getDataCache().getPidc(dbPidc.getProjectId()).resetVersions();

    }
    getChangedData().put(this.pidCard.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {
    final TabvProjectidcard dbPidc = getEntityProvider().getDbPIDC(this.dbPidcId);
    final AttributeValue value = getDataProvider().getAttrValue(dbPidc.getTabvAttrValue().getValueId());
    this.cmdAttrValue = new CmdModAttributeValue(getDataProvider(), value, true);
    this.cmdAttrValue.setUnDelete(this.unDelete);
    this.childCmdStack.addCommand(this.cmdAttrValue);

    setUserDetails(COMMAND_MODE.UPDATE, dbPidc, PIDC_ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    // Delete the project attributes
    this.childCmdStack.undoAll();

    // Delete the PIDC
    final TabvProjectidcard dbPidc = getEntityProvider().getDbPIDC(this.dbPidcId);
    getEntityProvider().deleteEntity(dbPidc);

    this.pidcNode.getPidCardMap().remove(this.pidCard.getID());
    getDataCache().getAllPidcMap().remove(this.pidCard.getID());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    final TabvProjectidcard dbPidc = getEntityProvider().getDbPIDC(this.dbPidcId);
    validateStaleData(dbPidc);

    if (isNameUpdated()) {
      validateStaleData(dbPidc.getTabvAttrValue());
      this.childCmdStack.undoAll();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoDeleteCommand() throws CommandException {
    this.childCmdStack.undoAll();
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

    // Refresh the PIDC for changes to reflect in UI
    final TabvProjectidcard dbPidc = getEntityProvider().getDbPIDC(this.dbPidcId);
    getEntityProvider().refreshCacheObject(dbPidc);

    if (this.commandMode == COMMAND_MODE.INSERT) {
      createMailForHotline();
    }
  }

  /**
   * @return the created/modified PID card
   */
  protected final TabvProjectidcard getDbPidc() {
    return getEntityProvider().getDbPIDC(this.dbPidcId);
  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {

    this.childCmdStack.rollbackAll(getExecutionMode());

    switch (this.commandMode) {
      case INSERT:
        if (this.pidCard != null) {
          getDataCache().getAllPidcMap().remove(this.pidCard.getID());
          this.pidcNode.getPidCardMap().remove(this.pidCard.getID());
        }
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
   * {@inheritDoc} return Pidcard id which has been subjected to modification
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.pidCard == null ? null : this.pidCard.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Project ID Card";
  }


  /**
   * ICDM 721 {@inheritDoc}
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return super.dataChanged() || this.updateAPRJModTimestamp || !CommonUtils.isEqual(this.oldAPRJID, this.newAPRJID) ||
        !CommonUtils.isEqual(this.oldProRevId, this.newProRevId);
  }

  /**
   * @param updateAPRJModTimestamp the updateAPRJModTimestamp to set
   */
  public void setUpdateAPRJModTimestamp(final boolean updateAPRJModTimestamp) {
    this.updateAPRJModTimestamp = updateAPRJModTimestamp;
  }


  /**
   * @return the newAPRJID
   */
  public Long getNewAPRJID() {
    return this.newAPRJID;
  }


  /**
   * @param newAPRJID the newAPRJID to set
   */
  public void setNewAPRJID(final Long newAPRJID) {
    this.newAPRJID = newAPRJID;
  }


  /**
   * @return the newProRevId
   */
  public Long getNewProRevId() {
    return this.newProRevId;
  }


  /**
   * @param newProRevId the newProRevId to set
   */
  public void setNewProRevId(final Long newProRevId) {
    this.newProRevId = newProRevId;
  }

  /**
   * @return whether English name is updated
   */
  private boolean isProRevIdUpdated() {
    return isStringChanged(String.valueOf(this.oldProRevId), String.valueOf(this.newProRevId));
  }


  /**
   * @return the pidcVrsnCmd
   */
  public CmdModPidcVersion getPidcVrsnCmd() {
    return this.pidcVrsnCmd;
  }

  /**
   * @param aliasDefinition aliasDefinition
   */
  public void setAliasDefinition(final AliasDefinition aliasDefinition) {
    this.aliasDefinition = aliasDefinition;

  }

  /**
   * @param projUcItems the projUcItems to set
   */
  public void setProjUcItems(final List<AbstractUseCaseItem> projUcItems) {
    this.projUcItems = projUcItems;
  }
}
