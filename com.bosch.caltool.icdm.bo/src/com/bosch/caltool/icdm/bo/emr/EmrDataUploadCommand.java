/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.codexmodel.model.CodexResultsConstants.VALTYPE;
import com.bosch.caltool.codexparser.excelparser.CodexParserConstants;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.IcdmFileDataCommand;
import com.bosch.caltool.icdm.bo.general.IcdmFilesCommand;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.emr.CodexResults;
import com.bosch.caltool.icdm.model.emr.EmrCategory;
import com.bosch.caltool.icdm.model.emr.EmrDataUploadResponse;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileData;
import com.bosch.caltool.icdm.model.emr.EmrFileEmsVariantMapping;
import com.bosch.caltool.icdm.model.emr.EmrInputData;
import com.bosch.caltool.icdm.model.emr.EmrMetaData;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;
import com.bosch.caltool.icdm.model.general.IcdmFileData;
import com.bosch.caltool.icdm.model.general.IcdmFiles;

/**
 * @author TRL1COB
 */
public class EmrDataUploadCommand extends AbstractSimpleCommand {

  private EmrInputData inputData = new EmrInputData();
  private final Map<Long, List<EmrUploadError>> uploadErrorsMap = new HashMap<>();
  private final EmrDataUploadResponse uploadResponse = new EmrDataUploadResponse();
  private final Map<Long, EmrFile> emrFileMap = new HashMap<>();
  private boolean fileUpload = false;
  private IcdmFileData icdmFileData = new IcdmFileData();
  private Map<String, List<CodexResults>> parsedeExcelOutput = new HashMap<>();
  private String icdmFileDataStr;
  private EmrFile fileToReload;


  /**
   * @param serviceData
   * @param inputData
   * @param fileUpload
   * @param icdmFileData
   * @throws IcdmException
   */
  public EmrDataUploadCommand(final ServiceData serviceData, final EmrInputData inputData, final boolean fileUpload,
      final IcdmFileData icdmFileData) throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
    this.fileUpload = fileUpload;
    this.icdmFileData = icdmFileData;
  }

  /**
   * @param serviceData
   * @param emrData
   * @throws IcdmException
   */
  public EmrDataUploadCommand(final ServiceData serviceData, final EmrInputData inputData) throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
  }


  /**
   * @param serviceData
   * @param parsedOutput
   * @param icdmFileData
   * @param fileToReload
   * @throws IcdmException
   */
  public EmrDataUploadCommand(final ServiceData serviceData, final Map<String, List<CodexResults>> parsedOutput,
      final String icdmFileData, final EmrFile fileToReload) throws IcdmException {
    super(serviceData);
    this.parsedeExcelOutput = parsedOutput;
    this.icdmFileDataStr = icdmFileData;
    this.fileToReload = fileToReload;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    // To load all metadata from metadata tables
    EmrMetaDataLoader metaDataLoader = new EmrMetaDataLoader(getServiceData());
    metaDataLoader.createMetaData();
    EmrMetaData metaData = metaDataLoader.getMetaDataObject();

    // Excel file Reload
    if (this.fileToReload != null) {
      insertExcelData(this.parsedeExcelOutput, metaDataLoader, metaData, this.icdmFileDataStr, this.fileToReload);
      // update the loaded_without_errors_flag always because the user details has to be updated for reload anyways
      boolean loadedWithoutError = false;
      if (this.uploadErrorsMap.get(this.fileToReload.getId()) == null) {
        loadedWithoutError = true;
      }
      updateLoadedWithoutErrorsFlag(this.fileToReload, loadedWithoutError);
      updateUploadResponse(this.fileToReload);

    }
    // Loading of data for first time
    else {
      // Get Emr file details from the input request
      EmrFile emrFile = new EmrFile();
      emrFile.setDeletedFlag(false);
      emrFile.setPidcVersId(this.inputData.getPidcVersId());
      emrFile.setIsVariant(CommonUtils.isNotEmpty(this.inputData.getPidcVariantIdList()));

      if (CommonUtils.isNotEmptyString(this.inputData.getFileDescription())) {
        emrFile.setDescription(this.inputData.getFileDescription());
      }

      if (CommonUtils.isNotEmptyString(this.inputData.getFileName())) {
        emrFile.setName(this.inputData.getFileName());
      }

      IcdmFiles icdmFile = new IcdmFiles();
      icdmFile.setFileCount(1L);
      icdmFile.setName(emrFile.getName());
      icdmFile.setNodeId(ApicConstants.EMR_NODE_ID);
      icdmFile.setNodeType(MODEL_TYPE.EMR_FILE.getTypeCode());

      // insert data into icdm file table
      IcdmFilesCommand fileCmd = new IcdmFilesCommand(getServiceData(), icdmFile, false);
      executeChildCommand(fileCmd);

      if (this.fileUpload) {
        // get IcdmFileData object
        this.icdmFileData.setIcdmFileId(fileCmd.getNewData().getId());
        // insert data into icdm file data table
        IcdmFileDataCommand fileDataCmd = new IcdmFileDataCommand(getServiceData(), this.icdmFileData);
        executeChildCommand(fileDataCmd);
      }

      IcdmFiles newIcdmFile = fileCmd.getNewData();
      // Update IcdmFileId to emr tabble
      emrFile.setIcdmFileId(newIcdmFile.getId());
      setDescBasedOnCharLength(emrFile);
      // insert data into emr_file table
      EmrFileCommand emrFileCommand = new EmrFileCommand(getServiceData(), emrFile, false);
      executeChildCommand(emrFileCommand);

      EmrFile newEmrFile = emrFileCommand.getNewData();
      // update node id to icdm file table
      newIcdmFile.setNodeId(newEmrFile.getId());
      IcdmFilesCommand icdmFileCmd = new IcdmFilesCommand(getServiceData(), newIcdmFile, true);
      executeChildCommand(icdmFileCmd);

      Map<String, List<CodexResults>> codexResults = new ConcurrentHashMap<>();
      codexResults.put(this.inputData.getFileName(), this.inputData.getCodexResultsList());

      // Insert into T_EMR_FILE_DATA table if the input value is found in metadata else into T_EMR_UPLOAD_ERROR table

      insertExcelData(codexResults, metaDataLoader, metaData, this.inputData.getFileName(), newEmrFile);


      // update the loaded_without_errors_flag when its true , by default it wil be false
      if (this.uploadErrorsMap.get(newEmrFile.getId()) == null) {
        // get the emr file object with latest versions
        newEmrFile = emrFileCommand.getNewData();
        updateLoadedWithoutErrorsFlag(newEmrFile, true);
      }

      // Update the response details in response
      updateUploadResponse(newEmrFile);

      emrToPidcVariantAssignment();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // no implementation needed
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * @param parsedOutput
   * @param metaDataLoader
   * @param metaData
   * @param icdmFileData
   * @param newEmrFile
   * @throws IcdmException
   */
  private void insertExcelData(final Map<String, List<CodexResults>> parsedOutput,
      final EmrMetaDataLoader metaDataLoader, final EmrMetaData metaData, final String icdmFileData,
      final EmrFile newEmrFile)
      throws IcdmException {
    List<CodexResults> dataList = parsedOutput.get(icdmFileData);
    for (CodexResults result : dataList) {
      Long emrFileId = newEmrFile.getId();
      Long emsId = null;
      Long emsTesCaseId = null;
      Long muId = null;

      if (CommonUtils.isNull(result.getColumn())) {
        throw new InvalidInputException("'Column' values are not provided in the uploaded sheet!!!");
      }

      if (CommonUtils.isNotEmptyString(result.getName())) {
        emsTesCaseId = metaData.getEmissionStdMap().get(result.getName().toUpperCase());
      }

      if (null != result.getRefProcedure()) {
        emsId = metaData.getEmissionStdMap().get(result.getRefProcedure().toUpperCase());
      }

      Long catId = getCategoryId(metaData, result);


      Long colId = metaData.getColumnMap().get(result.getColumn().toUpperCase());

      if (CommonUtils.isNotEmptyString(result.getMeasure())) {
        muId = metaData.getMeasureUnitMap().get(result.getMeasure().toUpperCase());
      }

      if (isEmsResultAvail(result, emsId, emsTesCaseId) && isCatColMeasAvail(result, catId, colId, muId)) {
        // if all values are not null , then add into emr_file_data table
        EmrFileData fileData = new EmrFileData();
        fileData.setCategoryId(catId);
        fileData.setColId(colId);
        getEmrColumnValue(metaDataLoader, result, colId, fileData);
        fileData.setEmissionStdTestcaseId(emsTesCaseId);
        fileData.setEmissionStdProcedureId(emsId);
        fileData.setFuelTypeNumber(
            CommonUtils.isNotEmptyString(result.getFuelType()) ? Long.valueOf(result.getFuelType()) : 0L);
        fileData.setMeasureUnitId(muId);
        fileData.setFileId(emrFileId);
        EmrFileDataCommand emrFileDataCmd = new EmrFileDataCommand(getServiceData(), fileData, true);
        executeChildCommand(emrFileDataCmd);
      }
      else {
        checkForErrorsInName(newEmrFile, result, emrFileId, emsTesCaseId);
        checkForErrorsInCategory(newEmrFile, result, emrFileId, catId);
        checkForErrorsInColumn(newEmrFile, result, emrFileId, colId);
        checkForErrorsInRefProc(newEmrFile, result, emrFileId, emsId);
        checkForErrorsInMeasure(newEmrFile, result, emrFileId, muId);
      }
    }
  }

  /**
   * @param metaData
   * @param result
   * @param catId
   * @return
   */
  private Long getCategoryId(final EmrMetaData metaData, final CodexResults result) {
    Long catId = null;
    if (null != result.getCategory()) {
      EmrCategory emrCat = metaData.getCategoryMap().get(result.getCategory().toUpperCase());
      if (null != emrCat) {
        catId = emrCat.getId();
      }
    }
    return catId;
  }

  /**
   * @param rowValue row value
   * @param errorCategory error category
   * @param emrFileId emr file id
   * @param rowNumber row number
   * @param newEmrFile new emr file object
   * @throws IcdmException eception in inserting into error table
   */
  private void insertIntoErrorTable(final String rowValue, final String errorCategory, final Long emrFileId,
      final Long rowNumber, final EmrFile newEmrFile)
      throws IcdmException {

    EmrUploadError error = new EmrUploadError();
    error.setErrorCategory(errorCategory);
    error.setErrorMessage("Invalid Data");
    error.setFileId(emrFileId);
    error.setErrorData(rowValue);
    error.setRowNumber(rowNumber);
    EmrUploadErrorCommand errorCmd = new EmrUploadErrorCommand(getServiceData(), error, true);
    executeChildCommand(errorCmd);
    List<EmrUploadError> uploadErrorList = this.uploadErrorsMap.get(newEmrFile.getId());
    if (uploadErrorList == null) {
      uploadErrorList = new ArrayList<>();
    }
    uploadErrorList.add(error);
    this.uploadErrorsMap.put(newEmrFile.getId(), uploadErrorList);
  }

  /**
   * @param emrFile
   */
  private void setDescBasedOnCharLength(final EmrFile emrFile) {
    // Review 286180
    if (CommonUtils.isNotEmptyString(emrFile.getDescription()) && (emrFile.getDescription().length() > 4000)) {
      // if the description text is more than 4000 characters then take the first 4000 characters alone
      emrFile.setDescription(emrFile.getDescription().substring(0, 3999));
    }
  }

  /**
   * @param metaDataLoader
   * @param result
   * @param colId
   * @param fileData
   * @throws DataException
   */
  private void getEmrColumnValue(final EmrMetaDataLoader metaDataLoader, final CodexResults result, final Long colId,
      final EmrFileData fileData)
      throws DataException {
    Long colValId = null;
    EmrColumnLoader colLoader = new EmrColumnLoader(getServiceData());
    if ("Y".equals(colLoader.getEntityObject(colId).getNomalizedFlag())) {
      colValId = metaDataLoader.getMappedColVal(result.getColumn(), result.getStrValue());
      fileData.setColValId(colValId);
    }
    else if (CommonUtils.isNotEmptyString(result.getValType())) {
      if (VALTYPE.NUMBER.toString().equalsIgnoreCase(result.getValType())) {
        fileData.setValueNum(result.getNumValue());
      }
      else if (VALTYPE.BOOLEAN.toString().equalsIgnoreCase(result.getValType())) {
        fileData.setValueText(
            result.isBoolValue() ? ApicConstants.BOOLEAN_TRUE_STRING : ApicConstants.BOOLEAN_FALSE_STRING);
      }
      else {
        setTextValue(result, fileData);
      }
    }
  }

  /**
   * @param result
   * @param emsId
   * @param emsTesCaseId
   * @return
   */
  private boolean isEmsResultAvail(final CodexResults result, final Long emsId, final Long emsTesCaseId) {
    return (emsId != null) && (CommonUtils.isEmptyString(result.getName()) || (emsTesCaseId != null));
  }

  /**
   * @param result
   * @param catId
   * @param colId
   * @param muId
   * @return
   */
  private boolean isCatColMeasAvail(final CodexResults result, final Long catId, final Long colId, final Long muId) {
    return (catId != null) && (colId != null) && isMeasurementAvail(result, muId);
  }

  /**
   * @param result
   * @param muId
   * @return
   */
  private boolean isMeasurementAvail(final CodexResults result, final Long muId) {
    return (CommonUtils.isEmptyString(result.getMeasure())) || (muId != null);
  }

  /**
   * @param result
   * @param fileData
   */
  private void setTextValue(final CodexResults result, final EmrFileData fileData) {
    if (CommonUtils.isNotEmptyString(result.getStrValue())) {
      if (result.getStrValue().length() > 4000) {
        // if the value text is more than 4000 characters then take the first 4000 characters alone
        fileData.setValueText(result.getStrValue().substring(0, 3999));
      }
      else {
        fileData.setValueText(result.getStrValue());
      }
    }
  }

  /**
   * For each of the Emission standard procedure from EMR file, multiple rows would be displayed differentiated by their
   * Variants
   *
   * @param emsId Emission standard procedure ID
   * @param fileName Name of the input EMR File
   * @param variantMapping
   * @return Map of Sheetnames that would be displayed in UI along with their EMR variants For each of the Emission
   *         standard procedure from EMR file
   */
  private Map<String, Long> getSheetNamesWithVariantInfo(final Long emsId, final String fileName,
      final EmrFileEmsVariantMapping variantMapping) {
    Map<String, Long> sheetsWithVariantMap = new HashMap<>();
    if (CommonUtils.isNotEmpty(variantMapping.getEmrVariantInfoMap()) &&
        variantMapping.getEmrVariantInfoMap().containsKey(emsId)) {
      variantMapping.getEmrVariantInfoMap().get(emsId).forEach((emrVar, emrVarInfo) -> {
        if (CommonUtils.isNotEmptyString(emrVarInfo)) {
          StringBuilder sheetNameBuilder = new StringBuilder(fileName);
          sheetNameBuilder.append(" / ")
              .append(variantMapping.getEmissionStandard().get(emsId).getEmissionStandardName());
          sheetNameBuilder.append(" ").append(emrVarInfo);
          sheetsWithVariantMap.put(sheetNameBuilder.toString(), emrVar);
        }
      });
    }
    else {
      StringBuilder sheetNameBuilder = new StringBuilder(fileName);
      sheetNameBuilder.append(" / ").append(variantMapping.getEmissionStandard().get(emsId).getEmissionStandardName());
      sheetsWithVariantMap.put(sheetNameBuilder.toString(), 0L);
    }
    return sheetsWithVariantMap;
  }

  /**
   * @param newEmrFile
   * @param result
   * @param emrFileId
   * @param emsTesCaseId
   * @throws IcdmException
   */
  private void checkForErrorsInName(final EmrFile newEmrFile, final CodexResults result, final Long emrFileId,
      final Long emsTesCaseId)
      throws IcdmException {
    if (CommonUtils.isNotEmptyString(result.getName()) && (null == emsTesCaseId)) {
      insertIntoErrorTable(result.getName(), CodexParserConstants.NAME_COL_TITLE, emrFileId,
          Long.valueOf(result.getDataRowNum()), newEmrFile);
    }
  }

  /**
   * @param newEmrFile
   * @param result
   * @param emrFileId
   * @param catId
   * @throws IcdmException
   */
  private void checkForErrorsInCategory(final EmrFile newEmrFile, final CodexResults result, final Long emrFileId,
      final Long catId)
      throws IcdmException {
    if (null == catId) {
      insertIntoErrorTable(result.getCategory(), CodexParserConstants.CAT_COL_TITLE, emrFileId,
          Long.valueOf(result.getDataRowNum()), newEmrFile);
    }
  }

  /**
   * @param newEmrFile
   * @param result
   * @param emrFileId
   * @param colId
   * @throws IcdmException
   */
  private void checkForErrorsInColumn(final EmrFile newEmrFile, final CodexResults result, final Long emrFileId,
      final Long colId)
      throws IcdmException {
    if (null == colId) {
      insertIntoErrorTable(result.getColumn(), CodexParserConstants.COLUMN_COL_TITLE, emrFileId,
          Long.valueOf(result.getDataRowNum()), newEmrFile);
    }
  }

  /**
   * @param newEmrFile
   * @param result
   * @param emrFileId
   * @param emsId
   * @throws IcdmException
   */
  private void checkForErrorsInRefProc(final EmrFile newEmrFile, final CodexResults result, final Long emrFileId,
      final Long emsId)
      throws IcdmException {
    if (null == emsId) {
      insertIntoErrorTable(result.getRefProcedure(), CodexParserConstants.REF_COL_TITLE, emrFileId,
          Long.valueOf(result.getDataRowNum()), newEmrFile);
    }
  }

  /**
   * @param newEmrFile
   * @param result
   * @param emrFileId
   * @param muId
   * @throws IcdmException
   */
  private void checkForErrorsInMeasure(final EmrFile newEmrFile, final CodexResults result, final Long emrFileId,
      final Long muId)
      throws IcdmException {
    if (CommonUtils.isNotEmptyString(result.getMeasure()) && (null == muId)) {
      insertIntoErrorTable(result.getMeasure(), CodexParserConstants.MEAS_COL_TITLE, emrFileId,
          Long.valueOf(result.getDataRowNum()), newEmrFile);
    }
  }

  /**
   * @param emrFile
   * @throws IcdmException
   */
  private void updateLoadedWithoutErrorsFlag(final EmrFile emrFile, final boolean loadedWithoutError)
      throws IcdmException {
    emrFile.setLoadedWithoutErrorsFlag(loadedWithoutError);
    EmrFileCommand updateEmrFileCommand = new EmrFileCommand(getServiceData(), emrFile, true);
    executeChildCommand(updateEmrFileCommand);
  }

  /**
   * @param emrFile
   */
  private void updateUploadResponse(final EmrFile emrFile) {
    this.emrFileMap.put(emrFile.getId(), emrFile);

    // set the response to the pojo
    this.uploadResponse.setEmrFileErrorMap(this.uploadErrorsMap);
    this.uploadResponse.setEmrFileMap(this.emrFileMap);
  }

  /**
   * @throws IcdmException
   */
  private void emrToPidcVariantAssignment() throws IcdmException {
    if (!this.fileUpload && CommonUtils.isNotEmpty(this.inputData.getPidcVariantIdList())) {
      saveVariantAssignment(this.inputData);
    }
  }

  /**
   * @param emrData
   * @param uploadResponse
   * @throws UnAuthorizedAccessException
   * @throws DataException
   * @throws IcdmException
   */
  private void saveVariantAssignment(final EmrInputData emrData) throws IcdmException {

    EmrPidcVariantLoader pidcVariantLoader = new EmrPidcVariantLoader(getServiceData());
    Set<Long> emrFileIds = this.uploadResponse.getEmrFileMap().keySet();
    EmrFileEmsVariantMapping variantMapping = pidcVariantLoader.getFileVariantEmsMapping(emrFileIds);
    List<EmrPidcVariant> assignVariantData = new ArrayList<>();

    // Fetch the set of EmrPidcVariant for variant assignment
    for (Long emrFileId : emrFileIds) {
      if (variantMapping.getEmrFileEmsMap().containsKey(emrFileId)) {
        String fileName = variantMapping.getEmrFilesMap().get(emrFileId).getName();
        for (Long emsId : variantMapping.getEmrFileEmsMap().get(emrFileId)) {

          // To get the list of sheetnames sheet names/Emission standard based on the EMR variants
          Map<String, Long> sheetsWithVariantMap = getSheetNamesWithVariantInfo(emsId, fileName, variantMapping);

          // Mapping to be done for each of the sheet
          sheetsWithVariantMap.forEach((sheetName, emrVariant) ->
          // Assign the input Pidc variant list to each of the sheet
          emrData.getPidcVariantIdList().forEach(pidcVarId -> {
            // Assign the sheet to each of pidc variant
            EmrPidcVariant newMapping = new EmrPidcVariant();
            newMapping.setEmrFileId(emrFileId);
            newMapping.setEmissionStdId(emsId);
            newMapping.setEmrVariant(emrVariant);
            newMapping.setPidcVariantId(pidcVarId);
            assignVariantData.add(newMapping);
          }));
        }
      }
    }

    // To save the variant assignment to T_EMR_PIDC_VARIANT table
    Map<Long, EmrPidcVariant> createdEmrPidcVarMap = new HashMap<>();
    for (EmrPidcVariant data : assignVariantData) {
      EmrPidcVariantCommand cmd = new EmrPidcVariantCommand(getServiceData(), data);
      executeChildCommand(cmd);
      EmrPidcVariant newData = cmd.getNewData();
      createdEmrPidcVarMap.put(newData.getId(), newData);
    }

    this.uploadResponse.setEmrPidcVariantMap(createdEmrPidcVarMap);
  }

  /**
   * @return the uploadResponse
   */
  public EmrDataUploadResponse getUploadResponse() {
    return this.uploadResponse;
  }

}
