/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.CmdModIcdmFile;
import com.bosch.caltool.apic.jpa.bo.IcdmFile;
import com.bosch.caltool.apic.jpa.bo.IcdmFile.NodeType;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFile;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_FILE_TYPE;


/**
 * @author adn1cob
 */
public class CmdModCDRFile extends AbstractCDRCommand {

  /**
   * Entity ID
   */
  private static final String ENTITY_ID = "CDR_RVW_FILE";
  /**
   * Child commands
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);
  /**
   * File type (INPUT, OUTPUT..)
   */
  private final REVIEW_FILE_TYPE fileType;
  /**
   * CDRResult against the files are stored
   */
  private final CDRResult cdrResult;
  /**
   * File Name
   */
  private String fileName;
  /**
   * File as bytes
   */
  private byte[] fileAsBytes;
  /**
   * Db entity
   */
  private TRvwFile dbRvwFile;
  /**
   * CDRResultParameter against the files to be stored
   */
  private CDRResultParameter cdrResultParam;
  /**
   * Icdm File to be passed for deletion Icdm-611
   */
  private IcdmFile icdmFile;


  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Icdm-612 Changing the constructor to public
   *
   * @param dataProvider cdrData provider
   * @param cdrResult CDRResult for the files to be added
   * @param fileType - Specify Input, output, additional type of files
   */
  public CmdModCDRFile(final CDRDataProvider dataProvider, final CDRResult cdrResult,
      final CDRConstants.REVIEW_FILE_TYPE fileType) {
    super(dataProvider);
    this.cdrResult = cdrResult;
    this.fileType = fileType;
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * @param dataProvider the CDR data provider
   * @param cdrResParam cdrResultParam for the files to be added
   * @param fileType - Specify Input, output, additional type of files
   */
  public CmdModCDRFile(final CDRDataProvider dataProvider, final CDRResultParameter cdrResParam,
      final CDRConstants.REVIEW_FILE_TYPE fileType) {
    super(dataProvider);
    this.cdrResultParam = cdrResParam;
    this.cdrResult = cdrResParam.getReviewResult();
    this.fileType = fileType;
    this.commandMode = COMMAND_MODE.INSERT;
  }


  /**
   * Icdm-611 for delation of a file
   *
   * @param dataProvider dataProvider
   * @param cdrResultParam cdrResultParameter
   * @param icdmFile icdmFile
   * @param isDelete isDelete
   */
  public CmdModCDRFile(final CDRDataProvider dataProvider, final CDRResultParameter cdrResultParam,
      final IcdmFile icdmFile, final boolean isDelete, final CDRConstants.REVIEW_FILE_TYPE fileType) {
    super(dataProvider);
    this.icdmFile = icdmFile;
    this.cdrResultParam = cdrResultParam;
    this.cdrResult = null;
    this.fileType = fileType;
    this.fileName = icdmFile.getFileName();
    if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
  }


  /**
   * Icdm-612 Constructor for deleting the Files for the result
   *
   * @param dataProvider cdrDataProvider
   * @param cdrResult cdrResult
   * @param icdmFile icdmFile
   * @param isDelete isDelete
   */
  public CmdModCDRFile(final CDRDataProvider dataProvider, final CDRResult cdrResult, final IcdmFile icdmFile,
      final boolean isDelete, final CDRConstants.REVIEW_FILE_TYPE fileType) {
    super(dataProvider);
    this.icdmFile = icdmFile;
    this.cdrResult = cdrResult;
    this.fileType = fileType;
    this.fileName = icdmFile.getFileName();
    if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    this.dbRvwFile = new TRvwFile();
    // Set cdr result id
    if (this.cdrResultParam == null) {
      this.dbRvwFile.setTRvwResult(getEntityProvider().getDbCDRResult(this.cdrResult.getID()));
    }
    // Set res param id, if file added for a result parameter
    else {
      this.dbRvwFile.setTRvwParameter(getEntityProvider().getDbCDRResParameter(this.cdrResultParam.getID()));
    }
    // set file type
    this.dbRvwFile.setFileType(this.fileType.getDbType());
    // Use child command to create icdm file
    TabvIcdmFile tabvIcdmFile;
    try {
      tabvIcdmFile = createICDMFile();
    }
    catch (IOException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      throw new CommandException(e.getMessage());
    }
    // Add files to result
    this.dbRvwFile.setTabvIcdmFile(tabvIcdmFile);

    // Set user details
    setUserDetails(COMMAND_MODE.INSERT, this.dbRvwFile, ENTITY_ID);

    // Register the entity
    getEntityProvider().registerNewEntity(this.dbRvwFile);

    // Add files to Result Icdm-611
    if (this.cdrResultParam == null) {
      Set<TRvwFile> tRvwFiles = getEntityProvider().getDbCDRResult(this.cdrResult.getID()).getTRvwFiles();
      if (tRvwFiles == null) {
        tRvwFiles = new HashSet<TRvwFile>();
      }
      tRvwFiles.add(this.dbRvwFile);
      getEntityProvider().getDbCDRResult(this.cdrResult.getID()).setTRvwFiles(tRvwFiles);
    }

    // Add files to parameter Icdm-611
    else {
      Set<TRvwFile> tRvwParamFiles =
          getEntityProvider().getDbCDRResParameter(this.cdrResultParam.getID()).getTRvwFiles();
      if (tRvwParamFiles == null) {
        tRvwParamFiles = new HashSet<TRvwFile>();
      }
      tRvwParamFiles.add(this.dbRvwFile);
      getEntityProvider().getDbCDRResParameter(this.cdrResultParam.getID()).setTRvwFiles(tRvwParamFiles);
    }


  }

  /**
   * Executes child command CmdModIcdmFile ,to store the file to db
   *
   * @return TabvIcdmFile
   * @throws IOException any exception during zipping of file
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private TabvIcdmFile createICDMFile() throws IOException, CommandException {

    final CmdModIcdmFile cmdIcdmFile = new CmdModIcdmFile(getDataProvider().getApicDataProvider(),
        CDRConstants.CDR_FILE_NODE_ID, NodeType.REVIEW_RESULT);
    final File file = new File(this.fileName);
    cmdIcdmFile.setFile(file.getName(), this.fileAsBytes);
    // Add the command
    this.childCmdStack.addCommand(cmdIcdmFile);
    this.icdmFile = cmdIcdmFile.getIcdmFile();
    return getEntityProvider().getDbIcdmFile(cmdIcdmFile.getPrimaryObjectID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Not applicable

  }

  /**
   * Icdm-611 Delete of a File {@inheritDoc} Icdm-612 delete of result file and param file
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final CmdModIcdmFile cmdFile = new CmdModIcdmFile(getDataProvider().getApicDataProvider(), this.icdmFile, true);
    this.childCmdStack.addCommand(cmdFile);
    Set<TRvwFile> tRvwFiles;

    /**
     * Icdm-612 Delete of a File for Cdr Result
     */
    if (this.cdrResultParam == null) {
      tRvwFiles = getEntityProvider().getDbCDRResult(this.cdrResult.getID()).getTRvwFiles();
      for (TRvwFile tRvwFile : tRvwFiles) {
        if (tRvwFile.getTabvIcdmFile().getFileId() == this.icdmFile.getID()) {
          final TRvwFile dbCDRFile = getEntityProvider().getDbCDRFile(tRvwFile.getRvwFileId());
          getEntityProvider().getDbCDRResult(this.cdrResult.getID()).getTRvwFiles().remove(dbCDRFile);
          getEntityProvider().deleteEntity(dbCDRFile);
          break;
        }
      }
    }

    /**
     * Icdm-611 Delete of a File for Cdr Result Parameter
     */
    else {
      tRvwFiles = getEntityProvider().getDbCDRResParameter(this.cdrResultParam.getID()).getTRvwFiles();

      for (TRvwFile tRvwFile : tRvwFiles) {
        if (tRvwFile.getTabvIcdmFile().getFileId() == this.icdmFile.getID()) {
          final TRvwFile dbCDRFile = getEntityProvider().getDbCDRFile(tRvwFile.getRvwFileId());
          getEntityProvider().getDbCDRResParameter(this.cdrResultParam.getID()).getTRvwFiles().remove(dbCDRFile);
          getEntityProvider().deleteEntity(dbCDRFile);
          break;
        }
      }
    }

  }

  /**
   * {@inheritDoc}
   */

  @Override
  public TransactionSummary getTransactionSummary() {
    // ICDM-723
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmIns(detailsList);
        break;
      case UPDATE:
        // Not applicable
      case DELETE:
        // no details section necessary in case of delete (parent row is sufficient in transansions view)
        addTransactionSummaryDetails(detailsList, this.fileName, "", getPrimaryObjectType());
        break;
      default:
        // Do nothing
        break;
    }
    this.summaryData.setObjectType("CDR File");
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
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
  protected void undoInsertCommand() throws CommandException {
    // Not applicable

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
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // Not applicable

  }

  /**
   * Set the file, which needs to be stored in db
   *
   * @param filePath file path
   * @throws CommandException exception when converting as byte array
   */
  public void setFile(final String filePath) throws CommandException {
    // store file name with full path
    this.fileName = filePath;
    // Get the bytes[] of the file
    try {
      this.fileAsBytes = CommonUtils.getFileAsByteArray(filePath);
    }
    catch (IOException exp) {
      throw new CommandException(exp.getMessage(), exp);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // TODO Auto-generated method stub
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
  protected void doPostCommit() {
    // Nothing to do

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.dbRvwFile == null ? null : this.dbRvwFile.getRvwFileId();
  }

  /**
   * Icdm-611 Return the Objcet type as CDR File {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "CDR File type:" + this.fileType.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // applicable ??
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.fileName;
  }

  /**
   * @return the icdmFile
   */
  public IcdmFile getIcdmFile() {
    return this.icdmFile;
  }
}
