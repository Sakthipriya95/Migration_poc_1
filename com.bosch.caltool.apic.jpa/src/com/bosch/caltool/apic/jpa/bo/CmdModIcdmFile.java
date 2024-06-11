/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFileData;


/**
 * Command to maintain ICDM files
 *
 * @author bne4cob
 */
public class CmdModIcdmFile extends AbstractCommand { // NOPMD by bne4cob on 11/27/13 9:32 AM

  /**
   * Entity ID for setting user details
   */
  private static final String IF_ENTITY_ID = "IF_ENTITY_ID";

  /**
   * Zip file name
   */
  private static final String ZIPPED_FILE_NAME = "Files.zip";

  /**
   * ICDM file object
   */
  private IcdmFile icdmFile;


  /**
   * Node ID. This cannot be updated
   */
  private final Long nodeID;

  /**
   * Node type. This cannot be updated
   */
  private final IcdmFile.NodeType nodeType;

  /**
   * Old file name
   */
  private String oldFileName;

  /**
   * New file name
   */
  private String newFileName;

  /**
   * Old file contents
   */
  private byte[] oldFileContents;

  /**
   * New file contents
   */
  private byte[] newFileContents;

  /**
   * Old File count
   */
  private long oldFileCount;

  /**
   * New File count
   */
  private long newFileCount;

  /**
   * Constructor for creating a new ICDM File record.
   *
   * @param dataProvider apic data provider
   * @param nodeID node id
   * @param nodeType node type
   */
  public CmdModIcdmFile(final ApicDataProvider dataProvider, final Long nodeID, final IcdmFile.NodeType nodeType) {
    super(dataProvider);

    // TODO Instead of this constructor, induvidual constructors are to be created for each node objects. The node type
    // should be derived from the input object type.

    this.commandMode = COMMAND_MODE.INSERT;

    this.nodeID = nodeID;
    this.nodeType = nodeType;
  }

  /**
   * Constructor for updating/deleting an ICDM file record.
   *
   * @param dataProvider apic data provider
   * @param icdmFile ICDM file object
   * @param isDelete <code>true</code> to delete, <code>false</code> to update
   */
  public CmdModIcdmFile(final ApicDataProvider dataProvider, final IcdmFile icdmFile, final boolean isDelete) {
    super(dataProvider);

    if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
    }

    this.icdmFile = icdmFile;

    this.nodeID = icdmFile.getNodeID();
    this.nodeType = icdmFile.getNodeType();
    this.oldFileName = icdmFile.getFileName();
    this.oldFileContents = icdmFile.getZippedFileData();
    this.oldFileCount = icdmFile.getFileCount();

    this.newFileName = this.oldFileName;
    this.newFileContents = this.oldFileContents;
    this.newFileCount = this.oldFileCount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    final TabvIcdmFile dbIcdmFile = new TabvIcdmFile();
    dbIcdmFile.setFileName(this.newFileName);
    dbIcdmFile.setFileCount(this.newFileCount);
    dbIcdmFile.setNodeId(this.nodeID);
    dbIcdmFile.setNodeType(this.nodeType.getDbNodeType());
    setUserDetails(COMMAND_MODE.INSERT, dbIcdmFile, IF_ENTITY_ID);

    getEntityProvider().registerNewEntity(dbIcdmFile);

    final TabvIcdmFileData dbFileData = new TabvIcdmFileData();
    dbFileData.setTabvIcdmFile(dbIcdmFile);
    dbIcdmFile.setTabvIcdmFileData(dbFileData);
    dbFileData.setFileData(this.newFileContents);

    getEntityProvider().registerNewEntity(dbFileData);

    this.icdmFile = new IcdmFile(getDataProvider(), dbIcdmFile.getFileId());

    // TODO set add the object to the node object's collection when induvidual constructors are defined.

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final TabvIcdmFile dbIcdmFile = getEntityProvider().getDbIcdmFile(this.icdmFile.getID());
    if (isFileNameChanged()) {
      dbIcdmFile.setFileName(this.newFileName);
    }

    if (isFileCountChanged()) {
      dbIcdmFile.setFileCount(this.newFileCount);
    }

    if (isFileContentsChanged()) {
      boolean newFileDataEntity = false;
      TabvIcdmFileData dbFileData = dbIcdmFile.getTabvIcdmFileData();
      if (dbFileData == null) {
        dbFileData = new TabvIcdmFileData();
        dbFileData.setTabvIcdmFile(dbIcdmFile);
        newFileDataEntity = true;
      }

      dbFileData.setFileData(this.newFileContents);


      if (newFileDataEntity) {
        getEntityProvider().registerNewEntity(dbFileData);
      }

    }

    setUserDetails(COMMAND_MODE.UPDATE, dbIcdmFile, IF_ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final TabvIcdmFile dbIcdmFile = getEntityProvider().getDbIcdmFile(this.icdmFile.getID());
    final TabvIcdmFileData dbFileData = dbIcdmFile.getTabvIcdmFileData();

    getEntityProvider().deleteEntity(dbFileData);
    getEntityProvider().deleteEntity(dbIcdmFile);

    getDataCache().getIcdmFilesOfNode(this.nodeID).remove(this.icdmFile.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    final TabvIcdmFile dbIcdmFile = getEntityProvider().getDbIcdmFile(this.icdmFile.getID());
    final TabvIcdmFileData dbFileData = dbIcdmFile.getTabvIcdmFileData();

    getEntityProvider().deleteEntity(dbFileData);
    getEntityProvider().deleteEntity(dbIcdmFile);

    getDataCache().getIcdmFilesOfNode(this.nodeID).remove(this.icdmFile.getID());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    final TabvIcdmFile dbIcdmFile = getEntityProvider().getDbIcdmFile(this.icdmFile.getID());
    if (isFileNameChanged()) {
      dbIcdmFile.setFileName(this.oldFileName);
    }
    if (isFileCountChanged()) {
      dbIcdmFile.setFileCount(this.oldFileCount);
    }
    if (isFileContentsChanged()) {
      boolean newFileDataEntity = false;
      TabvIcdmFileData dbFileData = dbIcdmFile.getTabvIcdmFileData();
      if (dbFileData == null) {
        dbFileData = new TabvIcdmFileData();
        dbFileData.setTabvIcdmFile(dbIcdmFile);
        newFileDataEntity = true;
      }

      dbFileData.setFileData(this.oldFileContents);


      if (newFileDataEntity) {
        getEntityProvider().registerNewEntity(dbFileData);
      }

    }

    setUserDetails(COMMAND_MODE.UPDATE, dbIcdmFile, IF_ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    final TabvIcdmFile dbIcdmFile = new TabvIcdmFile();
    dbIcdmFile.setFileName(this.oldFileName);
    dbIcdmFile.setFileCount(this.oldFileCount);
    dbIcdmFile.setNodeId(this.nodeID);
    dbIcdmFile.setNodeType(this.nodeType.getDbNodeType());
    setUserDetails(COMMAND_MODE.INSERT, dbIcdmFile, IF_ENTITY_ID);
    getEntityProvider().registerNewEntity(dbIcdmFile);
    getEntityProvider().getEm().flush();

    final TabvIcdmFileData dbFileData = new TabvIcdmFileData();
    dbFileData.setTabvIcdmFile(dbIcdmFile);
    dbFileData.setFileData(this.oldFileContents);

    dbIcdmFile.setTabvIcdmFileData(dbFileData);

    getEntityProvider().registerNewEntity(dbFileData);

    this.icdmFile = new IcdmFile(getDataProvider(), dbIcdmFile.getFileId());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isFileNameChanged() || isFileContentsChanged() || isFileCountChanged();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", this.newFileName);
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
  protected void rollBackDataModel() {
    // TODO incomplete
    if (this.commandMode == COMMAND_MODE.INSERT) {
      if (this.icdmFile != null) {
        getDataCache().getIcdmFilesOfNode(this.nodeID).remove(this.icdmFile.getID());
      }
    }
    else if ((this.commandMode == COMMAND_MODE.DELETE) && (this.icdmFile != null)) {
      // Assuming the ICDM file is not deleted
      getDataCache().getIcdmFilesOfNode(this.nodeID).put(this.icdmFile.getID(), this.icdmFile);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.icdmFile == null ? null : this.icdmFile.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "File";
  }

  /**
   * Set the files to be stored. The files should be given as a map, with key being the file name and value being the
   * file as byte array. The file name can be a simple name or the relative name, if the files are to be arranged in a
   * directory structure.
   * <p>
   * Note : File name should not be an absolute path.
   *
   * @param files map of the files to be stored.
   * @throws IOException for any exceptions during zipping
   */
  public void setFiles(final Map<String, byte[]> files) throws IOException {
    this.newFileCount = files.size();
    if (this.newFileCount > 1) {
      this.newFileName = ZIPPED_FILE_NAME;
    }
    else {
      // The one and only key is the name of the file
      this.newFileName = files.keySet().toArray()[0].toString();
    }
    this.newFileContents = ZipUtils.createZip(files);
  }

  /**
   * Set the file to this command
   *
   * @param name name of the file
   * @param file file as byte array.
   * @throws IOException any exception during zipping of file
   */
  public void setFile(final String name, final byte[] file) throws IOException {
    this.newFileName = name;
    final Map<String, byte[]> fileMap = new HashMap<String, byte[]>();
    fileMap.put(name, file);
    this.newFileContents = ZipUtils.createZip(fileMap);
    this.newFileCount = 1;
  }

  /**
   * @return whether file name is changed from the old one
   */
  private boolean isFileNameChanged() {
    return !CommonUtils.isEqual(this.oldFileName, this.newFileName);
  }

  /**
   * @return whether file count is changed from the old one
   */
  private boolean isFileCountChanged() {
    return this.oldFileCount != this.newFileCount;
  }

  /**
   * @return whether file content is changed from the old one
   */
  private boolean isFileContentsChanged() {
    return this.oldFileContents != this.newFileContents;
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

  /**
   * @return the icdmFile
   */
  public IcdmFile getIcdmFile() {
    return this.icdmFile;
  }
}
