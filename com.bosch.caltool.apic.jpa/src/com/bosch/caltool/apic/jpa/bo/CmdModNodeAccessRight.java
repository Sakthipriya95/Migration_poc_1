package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicNodeAccess;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Command class to insert, update and delete NODE access rights. Principally, the node can be a PIDC, an Attribute or
 * anything else. But currently, only PIDC is supported. With this class, the Owner flag, the WriteAccess flag and the
 * Grant option can be modified Read access right is available in the database but currently fixed to YES
 *
 * @author hef2fe
 */
public class CmdModNodeAccessRight extends AbstractCommand {

  /**
   * Entity ID for setting user details
   */
  private static final String ENTITY_ID = "NA_ENT_ID";
  /**
   * the NodeAccess right to be modified
   */
  private NodeAccessRight modifyNodeAccess;
  /**
   * the DB NodeAccess right to be modified
   */
  // icdm-229
  private Long modifiedDbNodeAccessId;

  /**
   * the DB NodeAccess right which has been created
   */
  // icdm-229
  private Long newDbNodeAccessId;

  /**
   * the User if a new NodeAccess right is to be created
   */
  private final ApicUser user;

  /**
   * old value for grant option
   */
  private boolean oldHasGrantOption;
  /**
   * new value for grant option
   */
  private boolean newHasGrantOption;


  /**
   * old value for the owner flag
   */
  private boolean oldIsOwner;
  /**
   * new value for the owner flag
   */
  private boolean newIsOwner;

  /**
   * old value for write access
   */
  private boolean oldHasWriteAccess;
  /**
   * new value for write access
   */
  private boolean newHasWriteAccess;

  /**
   * old value for read access
   */
  private boolean oldHasReadAccess;
  /**
   * new value for read access
   */
  private boolean newHasReadAccess;

  /**
   * Node to which the access right details is to be configured
   */
  private final AbstractDataObject node;
  /**
   * Node type
   */
  private final String nodeType;


  /**
   * Constructor to modify or delete an existing NodeAccess
   *
   * @param apicDataProvider data provider
   * @param nodeAccessRight The NodeAccess to be modified or deleted
   * @param node node to be deleted or updated
   * @param delete TRUE if the NodeAccess should be deleted FALSE if the NodeAccess should be modified
   */
  public CmdModNodeAccessRight(final ApicDataProvider apicDataProvider, final NodeAccessRight nodeAccessRight,
      final AbstractDataObject node, final boolean delete) {

    super(apicDataProvider);

    this.modifyNodeAccess = nodeAccessRight;

    this.user = nodeAccessRight.getApicUser();

    this.node = node;

    this.nodeType = nodeAccessRight.getNodeType();


    this.modifiedDbNodeAccessId = this.modifyNodeAccess.getNodeAccessID();

    if (delete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;

      // initialize command with current values
      this.oldHasWriteAccess = nodeAccessRight.hasWriteAccess();
      this.newHasWriteAccess = this.oldHasWriteAccess;

      this.oldHasReadAccess = nodeAccessRight.hasReadAccess();
      this.newHasReadAccess = this.oldHasReadAccess;


      this.oldHasGrantOption = nodeAccessRight.hasGrantOption();
      this.newHasGrantOption = this.oldHasGrantOption;

      this.oldIsOwner = nodeAccessRight.isOwner();
      this.newIsOwner = this.oldIsOwner;

    }

  }

  /**
   * Constructor to create a new NodeAccess
   *
   * @param apicDataProvider data provider
   * @param node The node on which the access should be configured
   * @param user The user who should get access
   */

  public CmdModNodeAccessRight(final ApicDataProvider apicDataProvider, final AbstractDataObject node,
      final ApicUser user) {
    super(apicDataProvider);
    this.node = node;
    this.user = user;
    this.commandMode = COMMAND_MODE.INSERT;

    this.nodeType = this.node.getEntityType().getEntityTypeString();

  }

  // Icdm-327
  /**
   * @return the newHasWriteAccess
   */
  public boolean isNewHasWriteAccess() {
    return this.newHasWriteAccess;
  }

  /**
   * @return the newHasGrantOption
   */
  public boolean isNewHasGrantOption() {
    return this.newHasGrantOption;
  }


  /**
   * Check, if the Owner flag has been changed
   *
   * @return TRUE, if the Owner flag has been changed
   */
  private boolean isModifiedIsOwner() {
    return this.oldIsOwner != this.newIsOwner;
  }

  /**
   * Check, if the Write access right has been changed
   *
   * @return TRUE, if the Write access right has been changed
   */
  private boolean isModifiedHasWriteAccess() {

    return this.oldHasWriteAccess != this.newHasWriteAccess;
  }

  /**
   * Check, if the Grant option has been changed
   *
   * @return TRUE, if the Grant option has been changed
   */
  private boolean isModifiedHasGrantOption() {

    return this.oldHasGrantOption != this.newHasGrantOption;
  }

  /**
   * Check, if the read option has been changed
   *
   * @return TRUE, if the Grant option has been changed
   */
  private boolean isModifiedReadOption() {

    return this.oldHasReadAccess != this.newHasReadAccess;
  }

  /**
   * Set the new Owner flag
   *
   * @param setIsOwner The new Owner flag
   */
  public void setIsOwner(final boolean setIsOwner) {

    this.newIsOwner = setIsOwner;
    if (setIsOwner) {
      this.newHasReadAccess = setIsOwner;
      this.newHasWriteAccess = setIsOwner;
      this.newHasGrantOption = setIsOwner;

    }
  }

  /**
   * Set the new Grant option
   *
   * @param setGrantOption The new Grant option
   */
  public void setGrantOption(final boolean setGrantOption) {

    this.newHasGrantOption = setGrantOption;
    // If grant is enabled then read and write accessess are given by default.
    if (setGrantOption) {
      this.newHasReadAccess = setGrantOption;
      this.newHasWriteAccess = setGrantOption;
    } // If grant is disabled then owner is disabled
    else {
      this.newIsOwner = setGrantOption;
    }
  }

  /**
   * Set the new Write access flag
   *
   * @param setWriteAccess The new Write access flag
   */
  public void setWriteAccess(final boolean setWriteAccess) {

    this.newHasWriteAccess = setWriteAccess;
    // If write is enabled then read access is given by default.
    if (setWriteAccess) {
      this.newHasReadAccess = setWriteAccess;
    } // If write is disabled then write& grand access is disabled by default.
    else {
      this.newHasGrantOption = setWriteAccess;
      this.newIsOwner = setWriteAccess;
    }
  }

  /**
   * Set the new read access flag
   *
   * @param setReadAccess The new Read access flag
   */
  public void setReadAccess(final boolean setReadAccess) {

    this.newHasReadAccess = setReadAccess;
    // If read is disabled then write,grant and owner access is disabled by default.
    if (!setReadAccess) {
      this.newHasWriteAccess = setReadAccess;
      this.newHasGrantOption = setReadAccess;
      this.newIsOwner = setReadAccess;
    }
  }

  /**
   * Check, if any data have been changed
   *
   * @return true/false
   */
  @Override
  protected boolean dataChanged() {
    return isModifiedHasGrantOption() || isModifiedHasWriteAccess() || isModifiedIsOwner() || isModifiedReadOption();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() {
    // create a new database entity icdm-229
    final TabvApicNodeAccess newDbNodeAccess = new TabvApicNodeAccess();
    Long nodeID = null;


    if (this.node != null) {

      // set the NodeType
      newDbNodeAccess.setNodeType(this.nodeType);
      nodeID = this.node.getID();
      // set the NodeID
      newDbNodeAccess.setNodeId(nodeID);

      // set the Owner flag
      if (this.nodeType.equals(ApicConstants.ATTR_NODE_TYPE) || this.nodeType.equals(ApicConstants.FUNC_NODE_TYPE)) {
        newDbNodeAccess.setOwner(ApicConstants.CODE_NO);
      }
      else {
        newDbNodeAccess.setOwner(this.newIsOwner ? ApicConstants.YES : ApicConstants.CODE_NO);
      }
    }

    // by default the READ right is always YES
    newDbNodeAccess.setReadright(ApicConstants.YES);

    // set the username
    newDbNodeAccess.setTabvApicUser(getEntityProvider().getDbApicUser(this.user.getUserID()));

    setUserDetails(COMMAND_MODE.INSERT, newDbNodeAccess, ENTITY_ID);

    // set the Grant option
    newDbNodeAccess.setGrantright(this.newHasGrantOption ? ApicConstants.YES : ApicConstants.CODE_NO);

    // set the Write access right
    newDbNodeAccess.setWriteright(this.newHasWriteAccess ? ApicConstants.YES : ApicConstants.CODE_NO);


    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbNodeAccess);
    this.newDbNodeAccessId = newDbNodeAccess.getNodeaccessId();

    // (the constructor of NodeAccessRight will add the new object)
    this.modifyNodeAccess = new NodeAccessRight(getDataProvider(), newDbNodeAccess.getNodeaccessId());

    // Icdm-346 Change add the new Access right to the User access Rights map
    if (this.user.getUserID().longValue() == getDataProvider().getCurrentUser().getUserID().longValue()) {
      getDataCache().getUserNodeAccRights().put(nodeID, this.modifyNodeAccess);
    }

    getChangedData().put(this.modifyNodeAccess.getID(), new ChangedData(ChangeType.INSERT,
        this.modifyNodeAccess.getID(), TabvApicNodeAccess.class, DisplayEventSource.COMMAND));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() {


    // remember the old NodeAccess database entity for UNDO
    // icdm-229
    final TabvApicNodeAccess deletedDbNodeAccess = getEntityProvider().getDbNodeAccess(this.modifiedDbNodeAccessId);

    final ChangedData chdata = new ChangedData(ChangeType.DELETE, this.modifyNodeAccess.getID(),
        TabvApicNodeAccess.class, DisplayEventSource.COMMAND);
    chdata
        .setOldDataDetails(getDataCache().getAllNodeAccRights().get(this.modifyNodeAccess.getID()).getObjectDetails());

    // unregister the NodeAccessRight database entity
    getEntityProvider().deleteEntity(deletedDbNodeAccess);

    // remove the NodeAccessRight from the list of all NodeAccessRights
    getDataCache().getAllNodeAccRights().remove(this.modifyNodeAccess.getID());
    // Remove the User node access
    if (this.user.getUserID().longValue() == getDataProvider().getCurrentUser().getUserID().longValue()) {
      getDataCache().getUserNodeAccRights().remove(this.node.getID());
    }

    getChangedData().put(this.modifyNodeAccess.getID(), chdata);

  }


  /**
   * {@inheritDoc}
   *
   * @throws CommandException stale data valiation
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // data modified

    // check is parallel change happened
    // Icdm-229
    final TabvApicNodeAccess modifiedDbNodeAccess = getEntityProvider().getDbNodeAccess(this.modifiedDbNodeAccessId);

    validateStaleData(modifiedDbNodeAccess);

    // update modified data
    if (isModifiedIsOwner()) {
      modifiedDbNodeAccess.setOwner(this.newIsOwner ? ApicConstants.YES : ApicConstants.CODE_NO);
    }

    if (isModifiedHasGrantOption()) {
      modifiedDbNodeAccess.setGrantright(this.newHasGrantOption ? ApicConstants.YES : ApicConstants.CODE_NO);
    }

    if (isModifiedHasWriteAccess()) {
      modifiedDbNodeAccess.setWriteright(this.newHasWriteAccess ? ApicConstants.YES : ApicConstants.CODE_NO);
    }
    if (isModifiedReadOption()) {
      modifiedDbNodeAccess.setReadright(this.newHasReadAccess ? ApicConstants.YES : ApicConstants.CODE_NO);
    }

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedDbNodeAccess, ENTITY_ID);

    getChangedData().put(this.modifyNodeAccess.getID(), new ChangedData(ChangeType.UPDATE,
        this.modifyNodeAccess.getID(), TabvApicNodeAccess.class, DisplayEventSource.COMMAND));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() {
    // drop new NodeAccess

    // unregister the NodeAccessRight

    // Icdm-229
    final TabvApicNodeAccess newDbNodeAccess = getEntityProvider().getDbNodeAccess(this.newDbNodeAccessId);
    getEntityProvider().deleteEntity(newDbNodeAccess);

    // remove the NodeAccessRight from the list of all NodeAccessRights
    getDataCache().getAllNodeAccRights().remove(newDbNodeAccess.getNodeaccessId());
    // Code Added To remove the Element from the User Map if Insert Fails
    if (this.user.getUserID().longValue() == getDataProvider().getCurrentUser().getUserID().longValue()) {
      getDataCache().getUserNodeAccRights().remove(this.modifyNodeAccess);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() {
    // re-create the old NodeAccess
    // icdm-229
    final TabvApicNodeAccess deletedDbNodeAccess = getEntityProvider().getDbNodeAccess(this.modifiedDbNodeAccessId);
    // register the old Entity
    getEntityProvider().registerNewEntity(deletedDbNodeAccess);

    // add the old NodeAccess to the list of all NodeAccess
    // (the constructor of NodeAccessRight will add the new object)
    // Code Added To add the Element to the User Map if Delete Fails
    if (this.user.getUserID().longValue() == getDataProvider().getCurrentUser().getUserID().longValue()) {
      getDataCache().getUserNodeAccRights().put(deletedDbNodeAccess.getNodeaccessId(), this.modifyNodeAccess);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() {

    // Icdm-229
    final TabvApicNodeAccess modifiedDbNodeAccess = getEntityProvider().getDbNodeAccess(this.modifiedDbNodeAccessId);
    // data modified
    if (isModifiedIsOwner()) {
      modifiedDbNodeAccess.setOwner(this.oldIsOwner ? ApicConstants.YES : ApicConstants.CODE_NO);
    }

    if (isModifiedHasGrantOption()) {
      modifiedDbNodeAccess.setGrantright(this.oldHasGrantOption ? ApicConstants.YES : ApicConstants.CODE_NO);
    }

    if (isModifiedHasWriteAccess()) {
      modifiedDbNodeAccess.setWriteright(this.oldHasWriteAccess ? ApicConstants.YES : ApicConstants.CODE_NO);
    }

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedDbNodeAccess, ENTITY_ID);

  }


  @Override
  public String getString() {

    String objectIdentifier = " INVALID!";

    switch (this.commandMode) {
      case INSERT:
      case UPDATE:
      case DELETE:
        objectIdentifier = caseCmdDel(objectIdentifier);

        break;

      default:
        break;
    }

    return super.getString("", objectIdentifier);

  }

  /**
   * @param objectIdentifier
   * @return
   */
  private String caseCmdDel(String objectIdentifier) {
    if (this.node != null) {
      objectIdentifier = this.user.getUserName() + " on " + this.node.getName();
    }
    return objectIdentifier;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Not applicable

  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        getDataCache().getAllNodeAccRights().remove(this.modifyNodeAccess.getNodeAccessID());
        if (this.user.getUserID().longValue() == getDataProvider().getCurrentUser().getUserID().longValue()) {
          getDataCache().getUserNodeAccRights().remove(this.node.getID());
        }
        break;
      case DELETE:
        getDataCache().getAllNodeAccRights().put(this.modifyNodeAccess.getNodeAccessID(), this.modifyNodeAccess);
        if (this.user.getUserID().longValue() == getDataProvider().getCurrentUser().getUserID().longValue()) {
          getDataCache().getUserNodeAccRights().put(this.node.getID(), this.modifyNodeAccess);
        }
        break;
      default:
        // Do nothing
        break;
    }
  }

  /**
   * Get newly added Super group to enable selection after adding
   *
   * @return TabvAttribute newDbAttr
   */
  public TabvApicNodeAccess getNewDbNodeAccess() {
    // ICDM-229
    return getEntityProvider().getDbNodeAccess(this.newDbNodeAccessId);
  }

  /**
   * {@inheritDoc} return the Node access id that has been subjected to insert or update
   */
  @Override
  public Long getPrimaryObjectID() {

    if (this.commandMode == COMMAND_MODE.INSERT) {
      return this.newDbNodeAccessId;
    }

    // field modifiedDbNodeAccessId is used for both update and delete
    return this.modifiedDbNodeAccessId;


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Node Access Right";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }

}
