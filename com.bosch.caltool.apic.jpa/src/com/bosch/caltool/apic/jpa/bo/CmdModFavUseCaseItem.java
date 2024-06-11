/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;


/**
 * ICDM-1027 This class is to add,delete favourite usecase items
 *
 * @author mkl2cob
 */
public class CmdModFavUseCaseItem extends AbstractCommand {

  /**
   * Unique entity id for setting user details
   */
  private static final String FAV_UC_ENTITY_ID = "FAV_UC_ENTITY_ID";
  /**
   * Apic User to which the private usecase item belongs to
   */
  private ApicUser apicUser;

  /**
   * Project id card to which project usecase item belongs to
   */
  private PIDCard pidCard;

  /**
   * AbstractUseCaseItem - ucItem that is added as favourite node
   */
  private final AbstractUseCaseItem ucItem;

  /**
   * TRUE if this is a project favourite usecase item
   */
  private boolean isProjectUcItem;

  /**
   * FavUseCaseItem bo
   */
  private FavUseCaseItem favUcItem;

  /**
   * Name of the favorite usecase item -for primary object identifier
   */
  private final String name;

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * FavUseCaseItem node if it already exists.otherwise null.
   */
  private FavUseCaseItemNode favNode;
  /**
   * child command stack
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Constructor for Private usecase fav node
   *
   * @param dataProvider AbstractDataProvider
   * @param apicUser ApicUser
   * @param ucItem AbstractUseCaseItem
   */
  public CmdModFavUseCaseItem(final AbstractDataProvider dataProvider, final ApicUser apicUser,
      final AbstractUseCaseItem ucItem) {
    super(dataProvider);
    this.apicUser = apicUser;
    this.ucItem = ucItem;
    this.commandMode = COMMAND_MODE.INSERT;
    this.isProjectUcItem = false;
    this.name = this.ucItem.getName();
  }

  /**
   * Constructor for Project usecase fav node
   *
   * @param dataProvider AbstractDataProvider
   * @param pidCard PIDCard
   * @param ucItem AbstractUseCaseItem
   */
  public CmdModFavUseCaseItem(final AbstractDataProvider dataProvider, final PIDCard pidCard,
      final AbstractUseCaseItem ucItem) {
    super(dataProvider);
    this.pidCard = pidCard;
    this.ucItem = ucItem;
    this.commandMode = COMMAND_MODE.INSERT;
    this.isProjectUcItem = true;
    this.name = this.ucItem.getName();
  }

  /**
   * Constructor for DELETE
   *
   * @param dataProvider AbstractDataProvider
   * @param favUcItem FavUseCaseItem which has to be deleted
   */
  public CmdModFavUseCaseItem(final AbstractDataProvider dataProvider, final FavUseCaseItem favUcItem) {
    super(dataProvider);
    this.favUcItem = favUcItem;
    this.commandMode = COMMAND_MODE.DELETE;
    if (this.favUcItem.getPIDC() != null) {
      this.isProjectUcItem = true;
      this.pidCard = this.favUcItem.getPIDC();
    }
    else {
      this.isProjectUcItem = false;
      this.apicUser = this.favUcItem.getApicUser();
    }
    this.ucItem = this.favUcItem.getUseCaseItem();
    this.name = this.favUcItem.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        // put the BO in the cache map
        caseCmdIns();
        break;

      case DELETE:
        // put the BO in the cache map
        caseCmdDel();
        break;
      case UPDATE:
      default:
        // Do nothing
        break;
    }


  }

  /**
   *
   */
  private void caseCmdDel() {
    if (this.isProjectUcItem) {
      getDataCache().getPidcUCFavMap(this.pidCard.getPidcId()).put(this.favUcItem.getID(), this.favUcItem);
    }
    else {
      getDataCache().getCurrentUserUCFavMap().put(this.favUcItem.getID(), this.favUcItem);
    }
  }

  /**
   *
   */
  private void caseCmdIns() {
    if (this.isProjectUcItem) {
      if (CommonUtils.isNotNull(this.favUcItem)) {
        getDataCache().getPidcUCFavMap(this.pidCard.getPidcId()).remove(this.favUcItem.getID());
      }
    }
    else {
      if (CommonUtils.isNotNull(this.favUcItem)) {
        getDataCache().getCurrentUserUCFavMap().remove(this.favUcItem.getID());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    boolean isValidInsert = isValidInsert();

    if (isValidInsert) {
      final TUsecaseFavorite dbUcFav = new TUsecaseFavorite();
      if (this.isProjectUcItem) {
        dbUcFav.setTabvProjectidcard(getEntityProvider().getDbPIDC(this.pidCard.getPidcId()));
      }
      else {
        dbUcFav.setTabvApicUser(getEntityProvider().getDbApicUser(this.apicUser.getUserID()));
      }
      setUCItem(dbUcFav);
      setUserDetails(this.commandMode, dbUcFav, FAV_UC_ENTITY_ID);
      getEntityProvider().registerNewEntity(dbUcFav);
      this.favUcItem = new FavUseCaseItem(getDataProvider(), dbUcFav.getUcFavId());

      // add the entity to entity mangaer & put the BO in the cache map
      if (this.isProjectUcItem) {
        getEntityProvider().getDbPIDC(this.pidCard.getPidcId()).getTUsecaseFavorites().add(dbUcFav);
        getDataCache().getPidcUCFavMap(this.pidCard.getPidcId()).put(this.favUcItem.getID(), this.favUcItem);
        this.pidCard.resetFavNodes();
      }
      else {
        getEntityProvider().getDbApicUser(getDataCache().getCurrentUser().getID()).getTUsecaseFavorites().add(dbUcFav);
        getDataCache().getCurrentUserUCFavMap().put(this.favUcItem.getID(), this.favUcItem);
        getDataCache().getCurrentUser().resetFavNodes();
      }

      // ICDM-1092 set the favUcItem to Fav node
      if (CommonUtils.isNotNull(this.favNode)) {
        this.favNode.setUcItem(this.favUcItem);
      }
      // put changed data to changed data map
      getChangedData().put(this.favUcItem.getID(), new ChangedData(ChangeType.INSERT, this.favUcItem.getID(),
          TUsecaseFavorite.class, DisplayEventSource.COMMAND));
    }

  }

  /**
   * ICDM-1092 checks if this is a valid insert
   *
   * @return true if this is a valid insert
   */
  private boolean isValidInsert() throws CommandException {

    // Collection of favourite nodes
    Collection<FavUseCaseItemNode> favNodes;
    favNodes = getFavNodes();

    for (FavUseCaseItemNode favUcNode : favNodes) {
      if (CommonUtils.isNull(favUcNode.getFavUcItem())) {
        // if this is a virtual node
        if (CommonUtils.isEqual(favUcNode.getID(), this.ucItem.getID())) {
          // adding the new node & deleting the existing child fav items
          deleteChildFavItems(favUcNode);
          return true;
        }
      }
      else {
        // if this is a fav use case item
        if (CommonUtils.isEqual(favUcNode.getID(), this.ucItem.getID())) {
          if (favUcNode.isVisible()) {
            // Not a valid insert as the value already exists in table
            throw new CommandException("This use case item is already added as the selected favorite type!");
          }
          // store the node to set fav uc item & clear the child node set
          this.favNode = favUcNode;
          return true;
        }
        if (favUcNode.isVisible()) {// do this for undeleted nodes
          for (AbstractUseCaseItem usecaseItem : favUcNode.getChildUCItems()) {
            isChildOfExistingFav(usecaseItem);
          }
        }
      }

    }
    return true;
  }

  /**
   * @return Collection<FavUseCaseItemNode>
   */
  private Collection<FavUseCaseItemNode> getFavNodes() {
    Collection<FavUseCaseItemNode> favNodes;
    if (this.isProjectUcItem) {
      favNodes = getDataCache().getPidcFavUcNodes(this.pidCard.getPidcId()).values();
    }
    else {
      favNodes = getDataCache().getCurrentFavUcNodes().values();
    }
    return favNodes;
  }

  /**
   * ICDM-1092 checks if this child of any level to the existing favourite node
   *
   * @param usecaseItem AbstractUseCaseItem
   * @throws CommandException Invalid insert
   */
  private void isChildOfExistingFav(final AbstractUseCaseItem ucChild) throws CommandException {

    if (CommonUtils.isEqual(ucChild.getID(), this.ucItem.getID())) {
      // throws exception if this is a child of existing fav item
      throw new CommandException("Another use case item in the parent hierarchy is already added as a favorite!");
    }
    for (AbstractUseCaseItem usecaseItem : ucChild.getChildUCItems()) {
      // throws exception if this is a child at any level of existing fav item
      isChildOfExistingFav(usecaseItem);
    }
  }

  /**
   * ICDM-1092 delete the child fav uc items
   *
   * @param favUcNode FavUseCaseItemNode
   * @throws CommandException
   */
  private void deleteChildFavItems(final FavUseCaseItemNode favUcNode) throws CommandException {
    findAndDeleteFavChildItems(favUcNode);
    // store the fav uc node
    this.favNode = favUcNode;
  }

  /**
   * @param favUcNode FavUseCaseItemNode
   * @throws CommandException
   */
  private void findAndDeleteFavChildItems(final FavUseCaseItemNode favUcNode) throws CommandException {
    if (CommonUtils.isNotNull(favUcNode.getChildFavNodes())) {
      for (FavUseCaseItemNode childUcNode : favUcNode.getChildFavNodes()) {
        if (CommonUtils.isNotNull(childUcNode.getFavUcItem())) {
          CmdModFavUseCaseItem cmdDel = new CmdModFavUseCaseItem(getDataProvider(), childUcNode.getFavUcItem());
          this.childCmdStack.addCommand(cmdDel);
        }
        else {
          findAndDeleteFavChildItems(childUcNode);
        }
      }
    }
  }

  /**
   * set the usecase /usecase section/ usecase group
   *
   * @param dbUcFav TUsecaseFavorite
   */
  private void setUCItem(final TUsecaseFavorite dbUcFav) {
    if (this.ucItem.getEntityType() == EntityType.USE_CASE_GROUP) {
      dbUcFav.setTabvUseCaseGroup(getEntityProvider().getDbUseCaseGroup(this.ucItem.getID()));
    }
    else if (this.ucItem.getEntityType() == EntityType.USE_CASE) {
      dbUcFav.setTabvUseCas(getEntityProvider().getDbUseCase(this.ucItem.getID()));
    }
    else if (this.ucItem.getEntityType() == EntityType.USE_CASE_SECT) {
      dbUcFav.setTabvUseCaseSection(getEntityProvider().getDbUseCaseSection(this.ucItem.getID()));
    }
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
    final TUsecaseFavorite dbUcFav = getEntityProvider().getDbFavUcItem(this.favUcItem.getID());
    // create changed data & put the old values
    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.favUcItem.getID(), TUsecaseFavorite.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.favUcItem.getObjectDetails());
    // remove the entity from entity manager
    if (this.isProjectUcItem) {
      getEntityProvider().getDbPIDC(this.pidCard.getPidcId()).getTUsecaseFavorites().remove(dbUcFav);
    }
    else {
      getEntityProvider().getDbApicUser(getDataCache().getCurrentUser().getID()).getTUsecaseFavorites().remove(dbUcFav);
    }

    getEntityProvider().deleteEntity(dbUcFav);

    // remove the BO in the cache map
    if (this.isProjectUcItem) {
      getDataCache().getPidcUCFavMap(this.pidCard.getPidcId()).remove(this.favUcItem.getID());
    }
    else {
      getDataCache().getCurrentUserUCFavMap().remove(this.favUcItem.getID());
    }

    // refresh favorite nodes
    if (this.isProjectUcItem) {
      this.pidCard.refreshFavNodes(this.favUcItem);
    }
    else {
      this.apicUser.refreshFavNodes(this.favUcItem);
    }
    // put changed data to changed data map
    getChangedData().put(this.favUcItem.getID(), chdata);
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
    // Not applicable
    return false;
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
    switch (this.commandMode) {
      case INSERT:
        caseCmdInsert(detailsList);
        break;
      case DELETE:
        addTransactionSummaryDetails(detailsList, this.name, "", getPrimaryObjectType());
        break;
      case UPDATE:
      default:
        // Do nothing
        break;
    }
    // set the details to summary data
    this.summaryData.setObjectType(getPrimaryObjectType());
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);

  }

  /**
   * @param detailsList
   */
  private void caseCmdInsert(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(this.name);
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.favUcItem == null ? null : this.favUcItem.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    if (this.isProjectUcItem) {
      return "Project Usecase";
    }
    return "Private Usecase";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() {
    if (this.isProjectUcItem) {
      if (CommonUtils.isNotNull(this.pidCard.getCurrentUserAccessRights()) &&
          this.pidCard.getCurrentUserAccessRights().isOwner()) {
        return true;
      }
      return false;
    }
    return true;
  }
}
