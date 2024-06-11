/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.compli;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.compli.CompliDataReview;
import com.bosch.caltool.icdm.bo.general.IcdmFileDataCommand;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TCompliRvwA2l;
import com.bosch.caltool.icdm.model.cdr.compli.CompliReviewResult;
import com.bosch.caltool.icdm.model.cdr.compli.CompliReviewResultHex;
import com.bosch.caltool.icdm.model.cdr.compli.CompliRvwHexParam;
import com.bosch.caltool.icdm.model.general.IcdmFileData;
import com.bosch.caltool.icdm.model.general.IcdmFiles;


/**
 * @author dmo5cob
 */
public class CompliReviewResultCommand extends AbstractCommand<CompliReviewResult, CompliReviewResultLoader> {

  private String outputZipfileName;

  private Map<Long, CompliReviewResultHex> compliResultHexMap;

  private Map<Long, Map<String, CompliRvwHexParam>> compliRvwHexParamMap;


  /**
   * Constructor to create an system access to the user
   *
   * @param serviceData Service Data
   * @param inputData system access right to create
   * @throws IcdmException any error
   */
  public CompliReviewResultCommand(final ServiceData serviceData, final CompliReviewResult inputData)
      throws IcdmException {
    super(serviceData, inputData, new CompliReviewResultLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    String outputZipFilePath = CompliDataReview.SERVER_PATH + File.separator + this.outputZipfileName;
    // insert the data into icdm file table
    IcdmFiles icdmFile = new IcdmFiles();
    icdmFile.setFileCount(1L);
    icdmFile.setName(this.outputZipfileName);
    icdmFile.setNodeId(1);
    icdmFile.setNodeType("COMPLI_FILES");
    IcdmFilesCommand fileCmd = new IcdmFilesCommand(getServiceData(), icdmFile, false);
    executeChildCommand(fileCmd);

    // insert data into icdm file data table
    IcdmFileData fileData = new IcdmFileData();
    // Get the bytes[] of the file
    try {
      byte[] fileAsBytes = CommonUtils.getFileAsByteArray(outputZipFilePath);
      fileData.setFileData(fileAsBytes);
    }
    catch (IOException exp) {
      throw new CommandException(exp.getMessage(), exp);
    }
    IcdmFiles icdmFileNewData = fileCmd.getNewData();
    fileData.setIcdmFileId(icdmFileNewData.getId());
    IcdmFileDataCommand fileDataCmd = new IcdmFileDataCommand(getServiceData(), fileData);
    executeChildCommand(fileDataCmd);


    TCompliRvwA2l entity = new TCompliRvwA2l();
    entity.setA2lFileId(null == getInputData().getA2lFileId() ? null : getInputData().getA2lFileId().longValue());
    entity.setA2lFileName(getInputData().getA2lFileName());
    entity.setNumCompli(getInputData().getNumCompli());
    entity.setNumQssd(null == getInputData().getNumQssd() ? 0L : getInputData().getNumQssd());
    entity.setSdomPverVariant(getInputData().getSdomPverVariant());
    entity.setSdomPverName(getInputData().getSdomPverName());
    entity.setSdomPverRevision(getInputData().getSdomPverRevision());
    entity.setTimeUsed(getInputData().getTimeUsed());
    entity.setWebFlowJobId(getInputData().getWebFlowJobId());
    entity.setTimeUsed(getInputData().getTimeUsed());
    entity.setStatus(getInputData().getStatus());
    entity.setTabvIcdmFile(new IcdmFilesLoader(getServiceData()).getEntityObject(icdmFileNewData.getId()));
    PidcVersionLoader vLoader = new PidcVersionLoader(getServiceData());
    entity.settPidcVersion(vLoader.getEntityObject(getInputData().getPidcVersionId()));
    setUserDetails(COMMAND_MODE.CREATE, entity);
    persistEntity(entity);

    for (Entry<Long, CompliReviewResultHex> resHexEntry : this.compliResultHexMap.entrySet()) {
      resHexEntry.getValue().setResultId(entity.getResultId());
      CompliReviewResultHexCommand hexCmd = new CompliReviewResultHexCommand(getServiceData(), resHexEntry.getValue());
      hexCmd.setCompliRvwHexParamMap(this.compliRvwHexParamMap.get(resHexEntry.getKey()));
      executeChildCommand(hexCmd);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return false;
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
    // NA
  }

  /**
   * @param outputZipfileName the outputZipfileName to set
   */
  public void setOutputZipfileName(final String outputZipfileName) {
    this.outputZipfileName = outputZipfileName;
  }

  /**
   * @param compliResultHexMap the compliResultHexMap to set
   */
  public void setCompliResultHexMap(final Map<Long, CompliReviewResultHex> compliResultHexMap) {
    this.compliResultHexMap = compliResultHexMap;
  }


  /**
   * @param compliRvwHexParamMap the compliRvwHexParamMap to set
   */
  public void setCompliRvwHexParamMap(final Map<Long, Map<String, CompliRvwHexParam>> compliRvwHexParamMap) {
    this.compliRvwHexParamMap = compliRvwHexParamMap;
  }

}
