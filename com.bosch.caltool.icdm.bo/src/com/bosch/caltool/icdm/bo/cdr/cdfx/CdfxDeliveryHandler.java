/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.cdfx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdfwriter.CDFWriter;
import com.bosch.caltool.cdfwriter.exception.CDFWriterException;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.report.cdr.cdfx.CdfxStatisticsExcelReportCreator;
import com.bosch.caltool.icdm.bo.uc.UcpAttrLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.JsonUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.CDFWriterLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDeliveryWrapper;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxProjInfoModel;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportOutput;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author pdh2cob
 */
public class CdfxDeliveryHandler extends AbstractSimpleBusinessObject {

  private final CDFxDeliveryWrapper cdfxDeliveryWrapper;

  /**
   * Key: ParamName & Value: WpRespLabelResponse, CalData
   */
  private Map<String, Map<WpRespLabelResponse, CalData>> wpCalDataObjects = new HashMap<>();


  private static final String NOT_USED = "not used";

  /**
   * @param cdfxDeliveryWrapper wrapper with all data
   * @param serviceData service data
   */
  public CdfxDeliveryHandler(final CDFxDeliveryWrapper cdfxDeliveryWrapper, final ServiceData serviceData) {
    super(serviceData);
    this.cdfxDeliveryWrapper = cdfxDeliveryWrapper;
  }

  /**
   * @param cdfxCreator CdfxCreator
   * @param pidcVariantId pidcVariantId
   * @return created caldata objects
   * @throws IcdmException exception
   */
  public Map<String, CalData> createCaldataObjects(final CdfxCreator cdfxCreator, final Long pidcVariantId)
      throws IcdmException {
    getLogger().info("Fetching relevant Caldata Objects..");

    Map<String, CalData> calDataMap = cdfxCreator.getRelevantCalDataObjects(pidcVariantId, this);

    if (CommonUtils.isNullOrEmpty(calDataMap)) {
      getLogger().error("Caldata objects map is empty!");
      throw new IcdmException("No review information found for CDFx creation");
    }
    return calDataMap;
  }

  /**
   * @param cdfxCreator cdfxCreator
   * @param pidcVariantId pidcVariantId
   * @throws IcdmException IcdmException
   */
  private void createCdrReport(final CdfxCreator cdfxCreator, final Long pidcVariantId) throws IcdmException {
    getLogger().info("Creating Data Review Report..");

    // create cdr report for given input , for all review results
    cdfxCreator.createCdrReport(pidcVariantId);

    if (CommonUtils.isNullOrEmpty(this.cdfxDeliveryWrapper.getCdrReport().getParamRvwDetMap())) {
      throw new IcdmException("No review results found for given input");
    }
  }

  private void createExcelReport(final String excelFilePath, final Map<String, Integer> missingParamCountMap,
      final Long variantId, final String variantName)
      throws IcdmException {

    Long pidcA2lId = this.cdfxDeliveryWrapper.getInput().getPidcA2lId();
    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId);
    CDFxProjInfoModel cdFxProjInfoModel = createCdfxProjInfoModel(pidcA2l, variantId);
    CdfxStatisticsExcelReportCreator cdfxStatisticsExcelReportCreator =
        new CdfxStatisticsExcelReportCreator(pidcA2l, this.cdfxDeliveryWrapper, cdFxProjInfoModel, getLogger());
    cdfxStatisticsExcelReportCreator.exportCdfxStatistics(excelFilePath, missingParamCountMap, variantName);

  }

  /**
   * @param pidcA2l
   * @param variantId
   * @return
   * @throws DataException
   */
  private CDFxProjInfoModel createCdfxProjInfoModel(final PidcA2l pidcA2l, final Long inputVarId) throws IcdmException {

    String ucIdsStr = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.CDFX_100_DELIVERY_UC_IDS);

    Set<Long> ucIdSet =
        new HashSet<>(Arrays.asList(ucIdsStr.split(","))).stream().map(Long::valueOf).collect(Collectors.toSet());

    Set<Long> attrIdSet = new HashSet<>();
    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());
    for (Long ucId : ucIdSet) {
      // throw exception if any of the provided use case Ids are invalid/deleted
      if (!ucLoader.isValidId(ucId) || ucLoader.isDeleted(ucId)) {
        throw new IcdmException("100% CDFx export aborted since one or more of the Use Case Ids is invalid/deleted.");
      }
      attrIdSet.addAll(new UcpAttrLoader(getServiceData()).getMappedAttributesUseCase(ucId));
    }
    String projectName = new PidcLoader(getServiceData()).getDataObjectByID(pidcA2l.getProjectId()).getName();
    String variantName = "";
    if ((null != inputVarId) && (inputVarId > 0)) {
      variantName = new PidcVariantLoader(getServiceData()).getDataObjectByID(inputVarId).getName();
    }

    CDFxProjInfoModel cdFxProjInfoModel = new CDFxProjInfoModel();
    cdFxProjInfoModel.setProjectName(projectName);
    cdFxProjInfoModel.setVariantName(variantName);
    cdFxProjInfoModel.setSoftwareName(pidcA2l.getSdomPverName() + " - " + pidcA2l.getSdomPverVarName());

    if ((null == inputVarId) || (inputVarId < 0)) {
      fillAttrValMapForNoVaiant(pidcA2l, cdFxProjInfoModel, attrIdSet);
    }
    else {
      fillAttrValMapForVariant(pidcA2l, cdFxProjInfoModel, attrIdSet, inputVarId);
    }
    return cdFxProjInfoModel;
  }

  /**
   * @param pidcA2l
   * @param cdFxProjInfoModel
   * @param attrIdSet
   * @throws IcdmException
   */
  private void fillAttrValMapForNoVaiant(final PidcA2l pidcA2l, final CDFxProjInfoModel cdFxProjInfoModel,
      final Set<Long> attrIdSet)
      throws IcdmException {
    PidcVersionAttributeModel projectAttrModel;
    // if input does not contain variant id, load project level attributes
    projectAttrModel = new ProjectAttributeLoader(getServiceData()).createModel(pidcA2l.getPidcVersId(),
        LOAD_LEVEL.L1_PROJ_ATTRS, false);

    for (Long attributeId : attrIdSet) {
      getAttrValFromProjLevel(cdFxProjInfoModel, projectAttrModel, attributeId);
    }
  }

  /**
   * @param cdFxProjInfoModel
   * @param projectAttrModel
   * @param attributeId
   */
  private void getAttrValFromProjLevel(final CDFxProjInfoModel cdFxProjInfoModel,
      final PidcVersionAttributeModel projectAttrModel, final Long attributeId) {
    PidcVersionAttribute pidcVersionAttribute = projectAttrModel.getPidcVersAttrMap().get(attributeId);
    // check if attribute is invisible
    if (null != pidcVersionAttribute) {
      // check if the used flag = NO
      if (CommonUtils.isEqual(pidcVersionAttribute.getUsedFlag(), PROJ_ATTR_USED_FLAG.NO.getDbType())) {
        cdFxProjInfoModel.getAttrValueMap().put(pidcVersionAttribute.getName(), NOT_USED);
      }
      else {
        cdFxProjInfoModel.getAttrValueMap().put(pidcVersionAttribute.getName(),
            CommonUtils.checkNull(pidcVersionAttribute.getValue()));
      }
    }
  }

  /**
   * @param pidcA2l
   * @param cdFxProjInfoModel
   * @param attrIdSet
   * @param inputVarId
   * @throws IcdmException
   */
  private void fillAttrValMapForVariant(final PidcA2l pidcA2l, final CDFxProjInfoModel cdFxProjInfoModel,
      final Set<Long> attrIdSet, final Long inputVarId)
      throws IcdmException {
    PidcVersionAttributeModel projectAttrModel;
    projectAttrModel = new ProjectAttributeLoader(getServiceData()).createModel(pidcA2l.getPidcVersId(),
        LOAD_LEVEL.L5_SUBVAR_ATTRS, false);
    Map<Long, PidcVariantAttribute> variantAttrMap = projectAttrModel.getVariantAttributeMap(inputVarId);

    for (Long attributeId : attrIdSet) {
      PidcVariantAttribute pidcVariantAttribute = variantAttrMap.get(attributeId);
      // check whether the attribute is invisible
      if (!projectAttrModel.getVariantInvisbleAttributeSet(inputVarId).contains(attributeId)) {
        // check whether attribute is moved to the variant level
        if (null == pidcVariantAttribute) {
          getAttrValFromProjLevel(cdFxProjInfoModel, projectAttrModel, attributeId);
          continue;
        }
        // check if the used flag = NO
        if (CommonUtils.isEqual(pidcVariantAttribute.getUsedFlag(), PROJ_ATTR_USED_FLAG.NO.getDbType())) {
          cdFxProjInfoModel.getAttrValueMap().put(pidcVariantAttribute.getName(), NOT_USED);
        }
        else {
          String attrValue = pidcVariantAttribute.getValue();
          if (pidcVariantAttribute.isAtChildLevel()) {
            attrValue = getSubVariantAttrVal(inputVarId, projectAttrModel, attributeId);
          }
          cdFxProjInfoModel.getAttrValueMap().put(pidcVariantAttribute.getName(), attrValue);
        }
      }
    }
  }

  /**
   * @param inputVarId
   * @param projectAttrModel
   * @param attributeId
   * @return
   */
  private String getSubVariantAttrVal(final Long inputVarId, final PidcVersionAttributeModel projectAttrModel,
      final Long attributeId) {
    StringBuilder attrValStr = new StringBuilder();

    // get the sub variants for the input variant
    Set<Long> subVarIdSet = projectAttrModel.getSubVariantIdSet(inputVarId);

    for (Long subvarId : subVarIdSet) {
      Map<Long, PidcSubVariantAttribute> subVarAttrMap = projectAttrModel.getSubVariantAttributeMap(subvarId);

      if (subVarAttrMap.containsKey(attributeId)) {
        PidcSubVariantAttribute pidcSubVariantAttribute = subVarAttrMap.get(attributeId);
        if (!attrValStr.toString().isEmpty()) {
          attrValStr.append(", ");
        }
        // check if the used flag = NO
        if (CommonUtils.isEqual(pidcSubVariantAttribute.getUsedFlag(), PROJ_ATTR_USED_FLAG.NO.getDbType())) {
          attrValStr.append(NOT_USED);
        }
        else {
          attrValStr.append(CommonUtils.checkNull(pidcSubVariantAttribute.getValue()));
        }
      }
    }
    return attrValStr.toString();
  }


  private void createOutputZipFile(final String outputFilePath, final String zipFilePath) throws IcdmException {
    try (Stream<java.nio.file.Path> path = Files.list(new File(outputFilePath).toPath())) {
      if (path.count() > 0) {
        // zip all files inside dir
        ZipUtils.zip(Paths.get(new File(outputFilePath).toURI()), Paths.get(zipFilePath));
        // delete dir
        FileUtils.deleteDirectory(new File(outputFilePath));
      }
    }
    catch (IOException e) {
      getLogger().error(e.getMessage(), e);
      throw new IcdmException("Error during zipping output files");
    }
  }

  /**
   * @return created output file path
   * @throws IcdmException exception
   */
  public String createOutputFiles() throws IcdmException {

    getLogger().info("Creating output files..");

    String fileWritingTime = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_17);
    this.cdfxDeliveryWrapper.setFileCreationDate(fileWritingTime);

    if (!new File(this.cdfxDeliveryWrapper.getServerTempDir()).exists()) {
      new File(this.cdfxDeliveryWrapper.getServerTempDir()).mkdir();
    }

    String cdfxFolderName = CDRConstants.CDFX_FILE_NAME + "_" + fileWritingTime;
    String cdfxFolderPath = this.cdfxDeliveryWrapper.getServerTempDir() + cdfxFolderName;
    if (!new File(cdfxFolderPath).exists()) {
      new File(cdfxFolderPath).mkdir();
    }

    String fileNameFromInput = this.cdfxDeliveryWrapper.getInput().getExportFileName();
    String zipFileName = fileNameFromInput + ".zip";
    String zipFilePath = cdfxFolderPath + File.separator + zipFileName;
    this.cdfxDeliveryWrapper.setServiceDirZipFilePath(zipFilePath);

    File outputFolderPath = new File(cdfxFolderPath + File.separator + fileNameFromInput);
    if (!outputFolderPath.exists()) {
      outputFolderPath.mkdir();
    }
    Set<PidcVariant> selPidcVariants = this.cdfxDeliveryWrapper.getInput().getVariantsList();
    if (CommonUtils.isNotEmpty(selPidcVariants)) {
      for (PidcVariant variant : selPidcVariants) {
        if (!variant.getName().equals(CDRConstants.NO_VARIANT_NAME)) {
          // Create output files for each variant.
          exportOutputFiles(variant.getName(), variant.getId(), outputFolderPath);
        }
        else {
          // Handling NO-VARIANT case when cdfx export is performed from RESP/WP Node
          exportOutputFiles(CDRConstants.NO_VARIANT, variant.getId(), outputFolderPath);
        }
        // Clearing the maps for next variant to aviod data issues.
        this.cdfxDeliveryWrapper.getParamReviewStatusMap().clear();
        this.cdfxDeliveryWrapper.getRelevantWpRespLabelMap().clear();
      }
    }
    // Handling NO-VARIANT case when 100% cdfx export is performed from A2L file
    else {
      exportOutputFiles(CDRConstants.NO_VARIANT, null, outputFolderPath);
    }
    // create zip file
    createOutputZipFile(outputFolderPath.toString(), zipFilePath);
    this.cdfxDeliveryWrapper.setOutputZipFileName(zipFileName);
    return zipFilePath;
  }

  /**
   * @param variantName variantName
   * @param variantId variantId
   * @param outputFolderPath outputFolderPath
   * @throws IcdmException
   */
  private void exportOutputFiles(final String variantName, final Long variantId, final File outputFolderPath)
      throws IcdmException {
    File variantCdfxFolderPath = new File(outputFolderPath + File.separator + variantName);
    if (!variantCdfxFolderPath.exists()) {
      variantCdfxFolderPath.mkdir();
    }
    CdfxCreator cdfxCreator = new CdfxCreator(this.cdfxDeliveryWrapper, getServiceData());
    createCdrReport(cdfxCreator, variantId);
    if (CommonUtils.isNotNull(variantId)) {
      this.cdfxDeliveryWrapper.getCdrReportMap().put(variantId, this.cdfxDeliveryWrapper.getCdrReport());
    }
    Map<String, CalData> calDataMap = createCaldataObjects(cdfxCreator, variantId);

    // Create complete cdfx file for each variant
    String cdfxFileName = createCompleteCdfxFile(variantName, variantCdfxFolderPath, calDataMap);

    // Create cdfx file based one file per workpackage checkbox selection
    if (this.cdfxDeliveryWrapper.getInput().isOneFilePerWpFlag()) {
      createCdfxFilePerWp(variantCdfxFolderPath);
    }
    // create excel report
    String excelFileName = variantName + ".xlsx";
    String excelFilePath = variantCdfxFolderPath + File.separator + excelFileName;
    createExcelReport(excelFilePath, cdfxCreator.getMissingParamCountMap(), variantId, variantName);

    // Create json file
    createJsonFile(variantName, variantCdfxFolderPath, calDataMap, cdfxFileName, excelFileName);
  }

  /**
   * @param variantName variantName
   * @param variantCdfxFolderPath variantCdfxFolderPath
   * @param calDataMap calDataMap
   * @return cdfxFileName cdfxFileName
   * @throws IcdmException IcdmException
   */
  private String createCompleteCdfxFile(final String variantName, final File variantCdfxFolderPath,
      final Map<String, CalData> calDataMap)
      throws IcdmException {
    String cdfxFileName = variantName + ".cdfx";
    String cdfxFilePath = variantCdfxFolderPath + File.separator + cdfxFileName;
    // create cdfx report
    createCdfxFile(calDataMap, cdfxFilePath);
    if (!CommonUtils.isFileAvailable(cdfxFilePath)) {
      throw new IcdmException("Error during CDFX file writing!");
    }
    return cdfxFileName;
  }

  /**
   * @param variantName variantName
   * @param variantCdfxFolderPath variantCdfxFolderPath
   * @param calDataMap calDataMap
   * @param cdfxFileName cdfxFileName
   * @param excelFileName excelFileName
   * @throws IcdmException IcdmException
   */
  private void createJsonFile(final String variantName, final File variantCdfxFolderPath,
      final Map<String, CalData> calDataMap, final String cdfxFileName, final String excelFileName)
      throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(
        pidcA2lLoader.getDataObjectByID(this.cdfxDeliveryWrapper.getInput().getPidcA2lId()).getA2lFileId());
    CdfxExportOutput outputModel = new CdfxExportOutput();
    outputModel.setCdfxFileName(cdfxFileName);
    outputModel.setCalDataObjCount(calDataMap.size());
    outputModel.setA2lParamCount(null == a2lFile.getNumParam() ? 0 : a2lFile.getNumParam().intValue());
    outputModel.setExcelFileName(excelFileName);
    outputModel.setCompletedParamCount(fetchParamStatusCount(DATA_REVIEW_SCORE.RATING_COMPLETED));
    outputModel.setNotRevParamCount(fetchParamStatusCount(DATA_REVIEW_SCORE.RATING_NOT_REVIEWED));
    outputModel.setPreCalParamCount(fetchParamStatusCount(DATA_REVIEW_SCORE.RATING_PRELIM_CAL));
    outputModel.setCalibratedParamCount(fetchParamStatusCount(DATA_REVIEW_SCORE.RATING_CALIBRATED));
    outputModel.setCheckedParamCount(fetchParamStatusCount(DATA_REVIEW_SCORE.RATING_CHECKED));
    outputModel.setWpRespParamCount(this.cdfxDeliveryWrapper.getWpRespParamSet().size());
    // create json file
    String jsonFileName = variantName + ".json";
    createJsonFile(variantCdfxFolderPath.getAbsolutePath(), jsonFileName, outputModel);
  }

  /**
   * @param variantCdfxFolderPath variantCdfxFolderPath
   * @param wpCalDataMap wpCalDataMap
   * @throws IcdmException IcdmException
   */
  private void createCdfxFilePerWp(final File wpCdfxFolderPath) throws IcdmException {
    Map<String, CalData> finalCalDataMap = new HashMap<>();
    Map<String, Map<WpRespLabelResponse, CalData>> wpNameCalDataMap = getWpCalDataObjects();
    for (Entry<String, Map<WpRespLabelResponse, CalData>> wpNameCalDataMapEntry : wpNameCalDataMap.entrySet()) {
      for (Entry<WpRespLabelResponse, CalData> wpRespCalDataEntrySet : wpNameCalDataMapEntry.getValue().entrySet()) {
        List<String> selectedWpList = this.cdfxDeliveryWrapper.getInput().getWpRespModelList().stream()
            .map(WpRespModel::getWpName).collect(Collectors.toList());
        if (CommonUtils.isNotEmpty(selectedWpList)) {
          if (selectedWpList.contains(wpNameCalDataMapEntry.getKey())) {
            // If selected scope is work package then filter the caldata map based on the selected wp name
            finalCalDataMap.put(wpRespCalDataEntrySet.getKey().getParamName(), wpRespCalDataEntrySet.getValue());
          }
        }
        else if (CommonUtils.isEqualIgnoreCase(this.cdfxDeliveryWrapper.getInput().getScope(),
            wpRespCalDataEntrySet.getKey().getWpRespModel().getA2lResponsibility().getRespType())) {
          // If the selected scope is Bosch, Customers or Others then filter the caldata map based on selected scope
          finalCalDataMap.put(wpRespCalDataEntrySet.getKey().getParamName(), wpRespCalDataEntrySet.getValue());
        }
      }
      if (CommonUtils.isNotEmpty(finalCalDataMap)) {
        createCdfxFile(finalCalDataMap, wpCdfxFolderPath + File.separator + wpNameCalDataMapEntry.getKey() + ".cdfx");
        finalCalDataMap.clear();
      }
    }
    getWpCalDataObjects().clear();
  }

  /**
   * @param ratingCompleted
   * @return
   */
  private int fetchParamStatusCount(final int ratingCompleted) {
    Set<String> params = this.cdfxDeliveryWrapper.getParamReviewStatusMap().get(ratingCompleted);
    return null == params ? 0 : params.size();
  }


  private void createJsonFile(final String jsonFilePath, final String jsonFileName, final CdfxExportOutput outputModel)
      throws IcdmException {
    JsonUtil.toJsonFile(outputModel, jsonFilePath, jsonFileName);
  }

  private void createCdfxFile(final Map<String, CalData> cdfCalDataObjects, final String cdfxFilePath)
      throws IcdmException {
    if (CommonUtils.isNotEmpty(cdfCalDataObjects)) {
      try {
        new CDFWriter(cdfCalDataObjects, cdfxFilePath, CDFWriterLogger.getInstance()).writeCalDataToXML();
      }
      catch (CDFWriterException e) {
        getLogger().error(e.getMessage(), e);
        throw new IcdmException("Error during writing CDFx file");
      }
    }
  }

  /**
   * @return the wpCalDataObjects
   */
  public Map<String, Map<WpRespLabelResponse, CalData>> getWpCalDataObjects() {
    return this.wpCalDataObjects;
  }

  /**
   * @param wpCalDataObjects the wpCalDataObjects to set
   */
  public void setWpCalDataObjects(final Map<String, Map<WpRespLabelResponse, CalData>> wpCalDataObjects) {
    this.wpCalDataObjects = wpCalDataObjects;
  }

}
