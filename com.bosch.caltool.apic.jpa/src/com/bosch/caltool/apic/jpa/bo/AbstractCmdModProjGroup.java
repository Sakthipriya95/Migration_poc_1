package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.common.util.Language;

/**
 * Abstract class as base for Group and Super Group Commands
 */
public abstract class AbstractCmdModProjGroup extends AbstractCommand {

  /**
   * Old English name
   */
  protected String oldNameEng;
  /**
   * New English name
   */
  protected String newNameEng;

  /**
   * Old English description
   */
  protected String oldDescEng;
  /**
   * New English description
   */
  protected String newDescEng;


  /**
   * Old German name
   */
  protected String oldNameGer;
  /**
   * New German name
   */
  protected String newNameGer;


  /**
   * Old German description
   */
  protected String oldDescGer;
  /**
   * New German description
   */
  protected String newDescGer;

  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = "with name: ";

  /**
   * command for editing multiple links
   */
  protected CmdModMultipleLinks cmdMultipleLinks;

  /**
   * Stack for storing child commands executed after creating the Use Case Entry
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);


  /**
   * Store the transactionSummary - ICDM-722
   */
  protected final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * @param dataProvider the Apic Data Provider
   */
  protected AbstractCmdModProjGroup(final ApicDataProvider dataProvider) {
    super(dataProvider);
  }

  /**
   * Sets the English Name
   *
   * @param newNameEng the new English name
   */
  public void setNameEng(final String newNameEng) {
    this.newNameEng = newNameEng;
  }

  /**
   * Sets the German Name
   *
   * @param newNameGer the new German name
   */
  public void setNameGer(final String newNameGer) {
    this.newNameGer = newNameGer;
  }

  /**
   * Sets the English Description
   *
   * @param newDescEng the new English Description
   */
  public void setDescEng(final String newDescEng) {
    this.newDescEng = newDescEng;
  }

  /**
   * Sets the German Description
   *
   * @param newDescGer the new German Description
   */
  public void setDescGer(final String newDescGer) {
    this.newDescGer = newDescGer;
  }

  /**
   * {@inheritDoc} return true if English and German names or descriptions are changed
   */
  @Override
  protected boolean dataChanged() {
    return isStringChanged(this.oldNameEng, this.newNameEng) || isStringChanged(this.oldDescEng, this.newDescEng) ||
        isStringChanged(this.oldNameGer, this.newNameGer) || isStringChanged(this.oldDescGer, this.newDescGer) ||
        (this.cmdMultipleLinks != null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    String objectIdentifier;

    switch (this.commandMode) {
      case INSERT:
      case UPDATE:
      case DELETE:
        objectIdentifier = getObjForCmdDel();
        break;
      default:
        objectIdentifier = " INVALID!";
        break;
    }

    return super.getString("", objectIdentifier);
  }

  /**
   * @return
   */
  private String getObjForCmdDel() {
    String objectIdentifier;
    if (getDataProvider().getLanguage().equals(Language.ENGLISH)) {
      objectIdentifier = STR_WITH_NAME + this.newNameEng;
    }
    else {
      objectIdentifier = STR_WITH_NAME + this.newNameGer;
    }
    return objectIdentifier;
  }

}
