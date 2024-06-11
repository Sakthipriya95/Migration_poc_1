/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.codexmodel.model.CodexResultsModel;
import com.bosch.caltool.codexparser.excelparser.CodexExcelParser;
import com.bosch.caltool.codexparser.exception.CodexParserException;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.ExternalLinkInfoLoader;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.emr.CodexResults;
import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileData;
import com.bosch.caltool.icdm.model.emr.EmrFileInputData;
import com.bosch.caltool.icdm.model.emr.EmrInputData;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;
import com.bosch.caltool.icdm.model.general.IcdmFileData;

/**
 * @author bru2cob
 */
public class EmrFileUploadCommand extends AbstractSimpleCommand {

  /**
   * buffer initial size
   */
  private static final int INVALID_FILES_BUFFER_SIZE = 40;
  private EmrFileInputData inputData;
  private final EMRFileUploadResponse uploadResponse = new EMRFileUploadResponse();
  private Long reloadExcelFileId;
  private boolean isExcelReload;


  /**
   * @param serviceData service data
   * @param inputData em file input data
   * @throws IcdmException Exception in emr data upload
   */
  public EmrFileUploadCommand(final ServiceData serviceData, final EmrFileInputData inputData) throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
  }

  /**
   * Constructor for reloading a2lfile
   *
   * @param serviceData service data
   * @param reloadExcelFileId reload excel file id
   * @throws IcdmException exception in upload
   */
  public EmrFileUploadCommand(final ServiceData serviceData, final Long reloadExcelFileId) throws IcdmException {
    super(serviceData);
    this.reloadExcelFileId = reloadExcelFileId;
    this.isExcelReload = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    try {
      String fromAddr =
          new LdapAuthenticationWrapper().getUserDetails(getServiceData().getUsername()).getEmailAddress();
      CommonParamLoader paramLoader = new CommonParamLoader(getServiceData());
      String toAddr = paramLoader.getValue(CommonParamKey.ICDM_HOTLINE_TO);
      String status = paramLoader.getValue(CommonParamKey.MAIL_NOTIFICATION_ENABLED);
      MailHotline hotline = new MailHotline(fromAddr, toAddr, ApicConstants.CODE_YES.equals(status));
      if (CommonUtils.isNotEmpty(getUploadResponse().getEmrFileMap()) &&
          (CommonUtils.isNotEmpty(getUploadResponse().getEmrFileErrorMap()))) {
        Long pidcVersionId = 0L;
        Map<Long, String> fileDetails = new HashMap<>();
        for (Entry<Long, EmrFile> entry : getUploadResponse().getEmrFileMap().entrySet()) {
          pidcVersionId = entry.getValue().getPidcVersId();
          if (getUploadResponse().getEmrFileErrorMap().containsKey(entry.getKey())) {
            fileDetails.put(entry.getKey(), entry.getValue().getName());
          }
        }
        // Construct the pidc version link
        IModelType type = ModelTypeRegistry.INSTANCE
            .getTypeOfModel(new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVersionId));
        String typeCode = type.getTypeCode();
        ExternalLinkInfo linkInfo =
            new ExternalLinkInfoLoader(getServiceData()).getLinkInfo(pidcVersionId, typeCode, null);

        hotline.sendEmrFileUploadError(linkInfo.getUrl(), fileDetails);
      }
    }
    catch (LdapException e) {
      getLogger().error(e.getMessage(), e);
    }
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
  protected void execute() throws IcdmException {

    // parsed file inputs are stored
    Map<String, List<CodexResults>> parsedOutput = new ConcurrentHashMap<>();

    // parse the files , if it has invalid file construct the error msg
    if (this.isExcelReload) {
      excelReload(parsedOutput);
    }
    else {
      validateFiles(this.inputData.getIcdmFileDataMap(), parsedOutput);

      // save all the parsed files
      for (Map.Entry<String, IcdmFileData> icdmFileDataEntrySet : this.inputData.getIcdmFileDataMap().entrySet()) {

        // get emr file based on the file path
        EmrFile emrFile = this.inputData.getEmrFileMap().get(icdmFileDataEntrySet.getKey());
        EmrInputData emrData = new EmrInputData();
        emrData.setFileName(emrFile.getName());
        emrData.setFileDescription(emrFile.getDescription());
        emrData.setPidcVersId(emrFile.getPidcVersId());
        emrData.setCodexResultsList(parsedOutput.get(icdmFileDataEntrySet.getKey()));

        IcdmFileData icdmFileData = icdmFileDataEntrySet.getValue();

        EmrDataUploadCommand dataUploadCmd = new EmrDataUploadCommand(getServiceData(), emrData, true, icdmFileData);
        executeChildCommand(dataUploadCmd);
        getUploadResponse().getEmrFileMap().putAll(dataUploadCmd.getUploadResponse().getEmrFileMap());
        getUploadResponse().getEmrFileErrorMap().putAll(dataUploadCmd.getUploadResponse().getEmrFileErrorMap());

      }
    }
  }

  /**
   * @param codexResultsModelList
   * @return
   */
  private List<CodexResults> createCodexResults(final List<CodexResultsModel> codexResultsModelList) {
    List<CodexResults> codexResultsList = new ArrayList<>();
    codexResultsModelList.stream().forEach(codex -> {
      CodexResults codexResults = new CodexResults();
      codexResults.setRefProcedure(codex.getRefProcedure());
      codexResults.setCategory(codex.getCategory());
      codexResults.setName(codex.getName());
      codexResults.setMeasure(codex.getMeasure());
      codexResults.setColumn(codex.getColumn());
      codexResults.setDataRowNum(codex.getExcelRowNum());
      codexResults.setValType((codex.getValType() != null) ? codex.getValType().toString() : null);
      if (codex.getFuelType() != null) {
        codexResults.setFuelType(String.valueOf(codex.getFuelType()));
      }
      if (("NUMBER").equalsIgnoreCase(codexResults.getValType())) {
        codexResults.setNumValue(codex.getNumValue());
      }
      else if (("BOOLEAN").equalsIgnoreCase(codexResults.getValType())) {
        codexResults.setBoolValue(codex.isBoolValue());
      }
      else {
        codexResults.setStrValue(codex.getStrValue());
      }
      codexResultsList.add(codexResults);
    });
    return codexResultsList;
  }

  /**
   * @param parsedOutput
   * @param metaDataLoader
   * @param metaData
   * @throws IcdmException
   */
  private void excelReload(final Map<String, List<CodexResults>> parsedOutput) throws IcdmException {

    // delete data from emrfiledata and emruploaderror table
    EmrFileDataLoader dataLoader = new EmrFileDataLoader(getServiceData());
    List<EmrFileData> fileDataList = dataLoader.getFileDataForFile(this.reloadExcelFileId);
    for (EmrFileData fileData : fileDataList) {
      EmrFileDataCommand fileDataCmd = new EmrFileDataCommand(getServiceData(), fileData, false);
      executeChildCommand(fileDataCmd);
    }

    EmrUploadErrorLoader errorLoader = new EmrUploadErrorLoader(getServiceData());
    List<EmrUploadError> errorList = errorLoader.getErrorDataForFile(this.reloadExcelFileId);
    for (EmrUploadError error : errorList) {
      EmrUploadErrorCommand errorCommand = new EmrUploadErrorCommand(getServiceData(), error, false);
      executeChildCommand(errorCommand);
    }

    EmrFileLoader loader = new EmrFileLoader(getServiceData());
    EmrFile fileToReload = loader.getDataObjectByID(this.reloadExcelFileId);
    byte[] reloadExcelFileData = loader.getEmrFile(this.reloadExcelFileId);
    IcdmFileData fileData = new IcdmFileData();
    fileData.setFileData(reloadExcelFileData);
    Map<String, IcdmFileData> fileDataMap = new HashMap<>();
    fileDataMap.put(fileToReload.getName(), fileData);
    validateFiles(fileDataMap, parsedOutput);
    for (Entry<String, IcdmFileData> icdmFileDataEntry : fileDataMap.entrySet()) {
      EmrDataUploadCommand dataUploadCommand =
          new EmrDataUploadCommand(getServiceData(), parsedOutput, icdmFileDataEntry.getKey(), fileToReload);
      executeChildCommand(dataUploadCommand);
      getUploadResponse().getEmrFileMap().putAll(dataUploadCommand.getUploadResponse().getEmrFileMap());
      getUploadResponse().getEmrFileErrorMap().putAll(dataUploadCommand.getUploadResponse().getEmrFileErrorMap());
    }
  }


  /**
   * @param keySet
   * @param parsedOutput
   * @param invalidFile
   * @throws IcdmException
   */
  private void validateFiles(final Map<String, IcdmFileData> fileDataMap,
      final Map<String, List<CodexResults>> parsedOutput)
      throws IcdmException {
    final StringBuilder invalidFile = new StringBuilder(INVALID_FILES_BUFFER_SIZE);
    for (Entry<String, IcdmFileData> currentIcdmFileDataEntry : fileDataMap.entrySet()) {

      try (InputStream inputStream = new ByteArrayInputStream(currentIcdmFileDataEntry.getValue().getFileData())) {
        CodexExcelParser codexParser = new CodexExcelParser(inputStream);
        List<CodexResultsModel> codexResultsList = codexParser.parseExcel();
        parsedOutput.put(currentIcdmFileDataEntry.getKey(), createCodexResults(codexResultsList));

      }
      catch (CodexParserException | IOException excep) {
        getLogger().error(excep.getMessage(), excep);
        invalidFile.append("; Error :").append(excep.getMessage()).append("\n");
      }
    }
    // throw exception in case there are invalid files
    if (!invalidFile.toString().isEmpty()) {
      throw new InvalidInputException(invalidFile.toString());
    }
  }


  /**
   * @return the uploadResponse
   */
  public EMRFileUploadResponse getUploadResponse() {
    return this.uploadResponse;
  }


}
