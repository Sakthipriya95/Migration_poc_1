/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.IcdmFileDataCommand;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.general.IcdmFileData;
import com.bosch.caltool.icdm.model.general.IcdmFiles;


/**
 * Command class for Review Files
 *
 * @author bru2cob
 */
public class RvwFileCommand extends AbstractCommand<RvwFile, RvwFileLoader> {

  private byte[] fileBytes;

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RvwFileCommand(final ServiceData serviceData, final RvwFile input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new RvwFileLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    // insert the data into icdm file table
    IcdmFiles icdmFile = new IcdmFiles();
    icdmFile.setFileCount(1L);
    icdmFile.setName(getInputData().getName());
    icdmFile.setNodeId(CDRConstants.CDR_FILE_NODE_ID);
    icdmFile.setNodeType(getInputData().getNodeType());
    IcdmFilesCommand fileCmd = new IcdmFilesCommand(getServiceData(), icdmFile, false);
    executeChildCommand(fileCmd);

    getInputData().setFileId(fileCmd.getNewData().getId());

    // insert data into icdm file data table
    IcdmFileData fileData = new IcdmFileData();
    // Get the bytes[] of the file
    try {
      if ((getInputData().getFilePath() == null) && (this.fileBytes != null)) {
        fileData.setFileData(this.fileBytes);
      }
      else {
        byte[] fileAsBytes = CommonUtils.getFileAsByteArray(getInputData().getFilePath());
        fileData.setFileData(fileAsBytes);
      }
    }
    catch (IOException exp) {
      throw new CommandException(exp.getMessage(), exp);
    }
    fileData.setIcdmFileId(fileCmd.getNewData().getId());
    IcdmFileDataCommand fileDataCmd = new IcdmFileDataCommand(getServiceData(), fileData);
    executeChildCommand(fileDataCmd);

    TRvwFile entity = new TRvwFile();

    TRvwResult resultEntity = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    entity.setTRvwResult(resultEntity);
    entity.setTabvIcdmFile(new IcdmFilesLoader(getServiceData()).getEntityObject(getInputData().getFileId()));
    TRvwParameter paramEntity =
        new CDRResultParameterLoader(getServiceData()).getEntityObject(getInputData().getRvwParamId());
    entity.setTRvwParameter(paramEntity);
    entity.setFileType(getInputData().getFileType());

    setUserDetails(COMMAND_MODE.CREATE, entity);
    if (null != resultEntity) {
      Set<TRvwFile> tRvwFiles = resultEntity.getTRvwFiles();
      if (null == tRvwFiles) {
        tRvwFiles = new HashSet<TRvwFile>();
      }
      tRvwFiles.add(entity);
      resultEntity.setTRvwFiles(tRvwFiles);
    }
    if (null != paramEntity) {
      Set<TRvwFile> tParamRvwFiles = paramEntity.getTRvwFiles();
      if (null == tParamRvwFiles) {
        tParamRvwFiles = new HashSet<TRvwFile>();
      }
      tParamRvwFiles.add(entity);
      paramEntity.setTRvwFiles(tParamRvwFiles);
    }
    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RvwFileLoader loader = new RvwFileLoader(getServiceData());
    TRvwFile entity = loader.getEntityObject(getInputData().getId());
    TRvwResult resultEntity = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    if (resultEntity != null) {
      resultEntity.getTRvwFiles().remove(entity);
    }
    TRvwParameter paramEntity =
        new CDRResultParameterLoader(getServiceData()).getEntityObject(getInputData().getRvwParamId());
    if (paramEntity != null) {
      paramEntity.getTRvwFiles().remove(entity);
    }
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub
  }


  /**
   * @return the fileData
   */
  public byte[] getFileData() {
    return this.fileBytes;
  }


  /**
   * @param fileData the fileData to set
   */
  public void setFileData(final byte[] fileData) {
    this.fileBytes = fileData;
  }


}
