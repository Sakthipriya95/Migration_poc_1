package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Command class to insert and update APIC Users.
 *
 * @author hef2fe
 */
public class CmdModApicUser extends AbstractCommand {

  /**
   * Entity ID for setting user details
   */
  private static final String ENTITY_ID = "APIC_USER_ENT_ID";

  /**
   * the ApicUser to be modified
   */
  private ApicUser modifyApicUser;

  /**
   * the DB ApicUser right to be modified
   */
  // icdm-229
  private Long modifiedDbApicUserId;

  /**
   * the DB ApicUser right which has been created
   */
  // icdm-229
  private Long newDbApicUserId;

  /**
   * the UserName if a new ApicUser is to be created
   */
  private String newUserName;

  /**
   * old value for firstName
   */
  private String oldFirstName = "";
  /**
   * new value for firstName
   */
  private String newFirstName = "";

  /**
   * old value for lastName
   */
  private String oldLastName = "";
  /**
   * new value for lastName
   */
  private String newLastName = "";

  /**
   * old value for department
   */
  private String oldDepartment = "";
  /**
   * new value for department
   */
  private String newDepartment = "";

  /**
   * Disclaimer acceptance date
   */
  private Calendar oldDiclaimerAcceptDate;
  /**
   * Disclaimer acceptance date
   */
  private Calendar newDiclaimerAcceptDate;
  /**
   * Child commands
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * the Command For Top level Entity
   */
  private CmdModTopLevelEntity cmdTopLevel;

  /**
   * Constructor to modify an existing ApicUser
   *
   * @param apicDataProvider data provider
   * @param modifyApicUser The ApicUser to be modified
   */
  public CmdModApicUser(final ApicDataProvider apicDataProvider, final ApicUser modifyApicUser) {

    super(apicDataProvider);
    // when using this constructor, the commandMode is UPDATE
    this.commandMode = COMMAND_MODE.UPDATE;

    // the ApicUser to be modified
    this.modifyApicUser = modifyApicUser;

    // get the DB entity for the ApicUser to be modified
    // icdm-229
    this.modifiedDbApicUserId = modifyApicUser.getUserID();
    // initialize command with current values
    this.oldFirstName = modifyApicUser.getFirstName();
    this.newFirstName = this.oldFirstName;

    this.oldLastName = modifyApicUser.getLastName();
    this.newLastName = this.oldLastName;

    this.oldDepartment = modifyApicUser.getDepartment();
    this.newDepartment = this.oldDepartment;


    this.oldDiclaimerAcceptDate = modifyApicUser.getDisclaimerAcceptedDate();
    this.newDiclaimerAcceptDate = this.oldDiclaimerAcceptDate;
  }

  /**
   * Constructor to create a new ApicUser
   *
   * @param apicDataProvider data provider
   * @param userName The name of the user for which an ApicUser needs to be created
   */
  public CmdModApicUser(final ApicDataProvider apicDataProvider, final String userName) {

    super(apicDataProvider);
    // create a new ApicUser

    // when using this constructor, only INSERT is possible
    this.commandMode = COMMAND_MODE.INSERT;

    // the (windows) name of the user
    this.newUserName = userName;

  }

  /**
   * Set the new firstName
   *
   * @param newFirstName The new firstName
   */
  public void setFirstName(final String newFirstName) {
    this.newFirstName = newFirstName;
  }

  /**
   * Set the new lastName
   *
   * @param newLastName The new lastName
   */
  public void setLastName(final String newLastName) {
    this.newLastName = newLastName;
  }

  /**
   * Set the new department
   *
   * @param newDepartment new department
   */
  public void setDepartment(final String newDepartment) {
    this.newDepartment = newDepartment;
  }

  /**
   * Set the date
   *
   * @param date DiclaimerAcceptDate
   */
  public void setDisclaimerAcceptedDate(final Calendar date) {
    this.newDiclaimerAcceptDate = date;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {

    return isStringChanged(this.oldFirstName, this.newFirstName) ||
        isStringChanged(this.oldLastName, this.newLastName) ||
        isStringChanged(this.oldDepartment, this.newDepartment) ||
        isObjectChanged(this.oldDiclaimerAcceptDate, this.newDiclaimerAcceptDate);

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // create a new ApicUser

    // create a new database entity
    // Icdm-229
    final TabvApicUser newDbApicUser = new TabvApicUser();

    // set the userName
    newDbApicUser.setUsername(this.newUserName);

    // set the version
    newDbApicUser.setVersion(Long.valueOf(1));

    // set the CreatedUser / date information
    setUserDetails(COMMAND_MODE.INSERT, newDbApicUser, ENTITY_ID);

    // set the other attributes
    newDbApicUser.setFirstname(this.newFirstName);
    newDbApicUser.setLastname(this.newLastName);
    newDbApicUser.setDepartment(this.newDepartment);


    // register the new Entity in the EntityManager to get the ID
    getEntityProvider().registerNewEntity(newDbApicUser);
    this.newDbApicUserId = newDbApicUser.getUserId();

    // add the new ApicUser to the list of all ApicUsers
    // (the constructor of ApicUser will add the new object)
    this.modifyApicUser = new ApicUser(getDataProvider(), newDbApicUser.getUserId());

    // add apic access right command as child command
    CmdModApicAccssRights cmdApicAccessRights = new CmdModApicAccssRights(getDataProvider(), this.modifyApicUser);
    this.childCmdStack.addCommand(cmdApicAccessRights);

    // icdm-474 Dcn for Top level entity
    this.cmdTopLevel = new CmdModTopLevelEntity(getDataProvider(), ApicConstants.TOP_LVL_ENT_ID_USER);
    this.childCmdStack.addCommand(this.cmdTopLevel);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() {
    // data modified

    // check is parallel change happened
    // get the version of the cached object
    // Icdm-229
    final TabvApicUser modifyDbApicUser = getEntityProvider().getDbApicUser(this.modifiedDbApicUserId);
    Long oldVersion = modifyDbApicUser.getVersion();
    // refresh the cache from the database
    TabvApicUser refreshedDbUser = (TabvApicUser) getEntityProvider().refreshCacheObject(modifyDbApicUser);
    // get the actual version
    Long newVersion = refreshedDbUser.getVersion();

    // log a warning in case of parallel changes
    if (!oldVersion.equals(newVersion)) {
      getEntityProvider().logger.warn("parallel change in UserID: " + this.modifyApicUser.getUserID());
    }

    // update modified data
    if (isStringChanged(this.oldFirstName, this.newFirstName)) {
      refreshedDbUser.setFirstname(this.newFirstName);
    }
    if (isStringChanged(this.oldLastName, this.newLastName)) {
      refreshedDbUser.setLastname(this.newLastName);
    }
    if (isStringChanged(this.oldDepartment, this.newDepartment)) {
      refreshedDbUser.setDepartment(this.newDepartment);
    }
    if (isObjectChanged(this.oldDiclaimerAcceptDate, this.newDiclaimerAcceptDate)) {
      refreshedDbUser.setDisclaimerAcceptnceDate(
          null == this.newDiclaimerAcceptDate ? null : ApicUtil.calendarToTimestamp(this.newDiclaimerAcceptDate));
    }

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifyDbApicUser, ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() {
    // drop new ApicUser

    // unregister the ApicUser
    // Icdm-229
    final TabvApicUser newDbApicUser = getEntityProvider().getDbApicUser(this.newDbApicUserId);
    getEntityProvider().deleteEntity(newDbApicUser);

    // remove the ApicUser from the list of all ApicUsers
    getDataCache().getAllApicUsersMap().remove(newDbApicUser.getUserId());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() {
    // data modified
    // Icdm-229
    final TabvApicUser modifyDbApicUser = getEntityProvider().getDbApicUser(this.modifiedDbApicUserId);
    // restore the old attributes
    modifyDbApicUser.setFirstname(this.oldFirstName);
    modifyDbApicUser.setLastname(this.oldLastName);
    modifyDbApicUser.setDepartment(this.oldDepartment);

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifyDbApicUser, ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() {
    // Not applicable

  }


  @Override
  public String getString() {

    String objectIdentifier = " INVALID!";

    switch (this.commandMode) {
      case INSERT:
        objectIdentifier = this.newUserName;
        break;

      case UPDATE:
        objectIdentifier = objForCmdUpd(objectIdentifier);
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
  private String objForCmdUpd(String objectIdentifier) {
    if (this.modifyApicUser.getUserID() != null) {
      // ApicUser is valid
      // a deleted ApicUser is invalid, because it has
      // been removed from the cache

      objectIdentifier = this.modifyApicUser.getUserName();
    }
    return objectIdentifier;
  }


  @Override
  public String getErrorMessage() {

    String errorMessage = "";

    switch (this.errorCause) {
      case NONE:
        break;

      case CONSTRAINT_VIOLATION:
        errorMessage = "user still existing: " + this.newUserName;
        break;

      case OPTIMISTIC_LOCK:
        errorMessage = "ApicUser has been changed by onother user in parallel: " + this.newUserName;
        break;

      default:
        errorMessage = "not handled errorCause " + this.errorCause;
        break;
    }

    return errorMessage;
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
        this.childCmdStack.rollbackAll(getExecutionMode());
        getDataCache().getAllApicUsersMap().remove(this.modifyApicUser.getUserID());
        break;
      case UPDATE:
      case DELETE:
      default:
        // Do nothing
        break;
    }
  }

  /**
   * {@inheritDoc} return the Apic User to be modified
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.modifyApicUser == null ? null : this.modifyApicUser.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Apic User";
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
