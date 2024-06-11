package com.bosch.caltool.apic.jpa.bo;

import java.util.Collection;

import com.bosch.caltool.dmframework.transactions.TransactionSummary;


/**
 * CmdModUsersPidcFavorites-Command handles all db operations on INSERT, UPDATE, DELETE on PIDC favourites
 */
public class CmdModUsersPidcFavorites extends AbstractCommand {

  private final ApicUser apicUser;

  private final PIDCVersion pidVersion;

  private boolean isDataChanged;

  /**
   * Constructor, for adding/removing favorite
   *
   * @param apicDataProvider the data provider
   * @param pidVersion PID Card
   * @param removePidc remove PIDC or not
   */
  public CmdModUsersPidcFavorites(final ApicDataProvider apicDataProvider, final PIDCVersion pidVersion,
      final boolean removePidc) {
    super(apicDataProvider);
    this.apicUser = apicDataProvider.getCurrentUser();

    this.pidVersion = pidVersion;

    this.commandMode = removePidc ? COMMAND_MODE.DELETE : COMMAND_MODE.INSERT;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() {
    this.isDataChanged = this.apicUser.removePidcFavorite(this.pidVersion);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() {
    this.isDataChanged = this.apicUser.addPidcFavorite(this.pidVersion);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() {
    // Not applicable for this command

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() {
    this.apicUser.addPidcFavorite(this.pidVersion);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() {
    this.apicUser.removePidcFavorite(this.pidVersion);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() {
    // Not applicable for this command
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return this.isDataChanged;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {

    String objectIdentifier = " INVALID!";

    String verNameFull = this.pidVersion.getName();
    if ((this.apicUser.getUserID() != null) && (verNameFull != null)) {
      objectIdentifier = " for " + this.apicUser.getUserName() + " on " + verNameFull;
    }

    return super.getString("", objectIdentifier);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    Collection<PIDCard> usersPidcList = getDataLoader().getUserPIDCFavSet(this.apicUser);
    switch (this.commandMode) {
      case INSERT:
        usersPidcList.remove(this.pidVersion.getPidc());
        break;
      case DELETE:
        usersPidcList.add(this.pidVersion.getPidc());
        break;
      default:
        // Do nothing
        break;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    // TODO move the favorites maintenance code from Dataloader class to this class. Update this method appropriately
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "PIDC Favorite";
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
