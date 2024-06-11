/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cda;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.FilenameUtils;

import com.bosch.calcomp.caldataanalyzer.CalDataAnalyzer;
import com.bosch.calcomp.caldataanalyzer.exception.CalDataAnalyzerDbConnectException;
import com.bosch.calcomp.caldataanalyzer.exception.CommandLineException;
import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.calcomp.tulservice.internal.model.ToolCategory;
import com.bosch.calcomp.tulservice.internal.model.ToolEvents;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.apic.ws.rest.service.JsonWriter;
import com.bosch.caltool.icdm.bo.IcdmServerTULInvoker;
import com.bosch.caltool.icdm.bo.cda.CDADataCreator;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.bo.tul.TULConstants.TUL_FEATURE;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerFilterModel;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultSummary;
import com.bosch.caltool.icdm.model.cda.CustomerFilter;
import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
import com.bosch.caltool.icdm.model.cda.PlatformFilter;
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDA + "/" + WsCommonConstants.RWS_CAL_DATA_ANALYSIS)
public class CalDataAnalyzerService extends AbstractRestService {


  private static final String ZIP_FILE_EXT = ".zip";


  private static final String CDA_SERVER_PATH = Messages.getString("SERVICE_WORK_DIR") + "//CDA//";
  private static final String CDA_FILE_NAME = "CalDataAnalyzer_";


  /**
   * @param asyncResponse
   * @param caldataAnalyzerFilterModel
   * @return
   * @throws IcdmException
   * @throws IOException
   * @throws CommandLineException
   * @throws CalDataAnalyzerDbConnectException
   * @throws InterruptedException
   */
  @POST
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response invokeCalDataAnalyzer(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel)
      throws IcdmException, CommandLineException, IOException, CalDataAnalyzerDbConnectException, InterruptedException {

    // Initialize TUL Invoker
    IcdmServerTULInvoker serverTul =
        new IcdmServerTULInvoker(getServiceData(), ToolCategory.PROCESSING, TUL_FEATURE.CALIBRATION_DATA_ANALYSIS);
    try {
      // Send Service started message to TUL
      serverTul.postToolData(ToolEvents.STARTED, null, null);

      String currentDate = DateUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);

      CDADataCreator cdaDataCreator = new CDADataCreator();
      CalDataAnalyzer calDataAnalyzer = cdaDataCreator.initializeCaldataAnalyzer(caldataAnalyzerFilterModel);
      String tempFolderLocation = createOutputFolder(currentDate);

      calDataAnalyzer.setOutputPath(tempFolderLocation);

      calDataAnalyzer.setOutputReportFileName(CDA_FILE_NAME + "_" + currentDate);

      Map<String, LabelInfoVO> fetchDataSets = calDataAnalyzer.fetchDataSets(null);
      // To generate CDA output files
      calDataAnalyzer.genExcelDatasets();
      if (!caldataAnalyzerFilterModel.getParamFilterList().isEmpty()) {
        calDataAnalyzer.genUsedDcmValues();
        calDataAnalyzer.genUsedExcelValues();
        calDataAnalyzer.genExcelValueIdCombination();
        calDataAnalyzer.genExcelCalDataStatistics();
      }

      CaldataAnalyzerResultSummary summary = createSummaryModel(fetchDataSets, caldataAnalyzerFilterModel);
      JsonWriter.createJsonFile(summary, tempFolderLocation, ApicConstants.CALDATA_JSON_FILE_NAME);
      JsonWriter.createJsonFile(caldataAnalyzerFilterModel, tempFolderLocation, ApicConstants.CALDATA_FILTER_JSON_NAME);


      String zipfileName = FilenameUtils.getBaseName(tempFolderLocation) + ZIP_FILE_EXT;
      String zipFilePath = CDA_SERVER_PATH + zipfileName;

      // Compress output files to a single zip file
    ZipUtils.zip(Paths.get(tempFolderLocation), Paths.get(CDA_SERVER_PATH + zipfileName));

      // Delete zip file's original folder
      File file = new File(tempFolderLocation);
      delete(file);

      File zipFile = new File(zipFilePath);

      // Frame the artifactInfo and misc strings that needs to be sent to TUL for logging
      List<String> artifactInfo = new ArrayList<>();
      try (ZipFile zippedFile = new ZipFile(zipFile)) {
        artifactInfo = zippedFile.stream().map(ZipEntry::getName).collect(Collectors.toList());
      }
      String tulMisc = getTulMiscInfo(caldataAnalyzerFilterModel, summary);

      // Send Service completed message to TUL
      serverTul.postToolData(ToolEvents.CLOSED, artifactInfo, tulMisc);

      // Create service response
      ResponseBuilder response = Response.ok(zipFile);

      // set the Response file name
      response.header("Content-Disposition", "attachment; filename=" + zipfileName);
      return response.build();
    }
    catch (Exception e) {
      serverTul.postToolData(ToolEvents.USER_ABORTED, null, " " + e.toString());
      throw e;
    }

  }


  /**
   * Method to construct summary model
   *
   * @param fetchDataSets - data set from caldata analyzer
   * @param caldataAnalyzerFilterModel - input model
   * @return summary model
   */
  private CaldataAnalyzerResultSummary createSummaryModel(final Map<String, LabelInfoVO> fetchDataSets,
      final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel) {
    CaldataAnalyzerResultSummary summary = new CaldataAnalyzerResultSummary();

    if (fetchDataSets != null) {
      summary.setLabelsAnalyzedCount(fetchDataSets.size());
    }
    summary.setLabelsAnalyzedCount(caldataAnalyzerFilterModel.getParamFilterList().size());
    summary.setCustomerFilterItemsCount(
        caldataAnalyzerFilterModel.getCustomerFilterModel().getCustomerFilterList().size());
    summary.setFunctionFilterItemsCount(caldataAnalyzerFilterModel.getFunctionFilterList().size());
    summary.setPlatformFilterItemsCount(
        caldataAnalyzerFilterModel.getPlatformFilterModel().getPlatformFilterList().size());
    summary.setSysconFilterItemsCount(caldataAnalyzerFilterModel.getSysconFilterList().size());
    summary.setParamFilterItemsCount(caldataAnalyzerFilterModel.getParamFilterList().size());
    return summary;
  }

  /**
   * create Ouput folder
   */
  private String createOutputFolder(final String currentDate) {
    File file = new File(CDA_SERVER_PATH);
    if (!file.exists()) {
      file.mkdir();
    }
    file = new File(file.getAbsoluteFile() + File.separator + CDA_FILE_NAME + currentDate);
    file.mkdir();
    return file.getAbsolutePath();
  }

  /**
   * @param folderPath
   */
  private void delete(final File file) {
    if (file.isDirectory()) {

      // directory is empty, then delete it
      if (file.list().length == 0) {
        file.delete();

      }
      // Directory is not empty
      else {

        // list all the directory contents
        String[] files = file.list();
        for (String temp : files) {
          // construct the file structure
          File fileDelete = new File(file, temp);

          // recursive delete
          delete(fileDelete);
        }

        // check the directory again, if empty then delete it
        if (file.list().length == 0) {
          file.delete();

        }
      }

    }
    else {
      // if file, then delete it
      file.delete();

    }
  }

  /**
   * @param caldataAnalyzerFilterModel CalData Analyzer Filter model
   * @param summary Summary of Cal data analysis
   * @return Misc information String
   */
  private String getTulMiscInfo(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel,
      final CaldataAnalyzerResultSummary summary) {
    StringBuilder misc = new StringBuilder();
    // Appending the count of parameters and filters used for cal data analysis to Misc info
    misc.append("Count of Parameters analyzed: ").append(summary.getLabelsAnalyzedCount()).append("; ");
    misc.append("Count of Parameter filters: ").append(summary.getParamFilterItemsCount()).append("; ");
    misc.append("Count of Function filters: ").append(summary.getFunctionFilterItemsCount()).append("; ");
    misc.append("Count of System constant filters: ").append(summary.getSysconFilterItemsCount()).append("; ");
    misc.append("Count of Platform filters: ").append(summary.getPlatformFilterItemsCount()).append("; ");
    misc.append("Count of customer filters: ").append(summary.getCustomerFilterItemsCount()).append("; ").append("\n");

    // Append the list of parameters used for cal data analysis to Misc info
    if (CommonUtils.isNotEmpty(caldataAnalyzerFilterModel.getParamFilterList())) {
      misc.append("List of Parameter filters: ");
      misc.append(caldataAnalyzerFilterModel.getParamFilterList().stream().map(ParameterFilterLabel::getLabel)
          .collect(Collectors.joining(", "))).append("; \n");
    }

    // Append the list of function filters used for cal data analysis to Misc info
    if (CommonUtils.isNotEmpty(caldataAnalyzerFilterModel.getFunctionFilterList())) {
      misc.append("List of Function filters: ");
      misc.append(caldataAnalyzerFilterModel.getFunctionFilterList().stream().map(FunctionFilter::getFunctionName)
          .collect(Collectors.joining(", "))).append("; \n");
    }

    // Append the list of system constant filters used for cal data analysis to Misc info
    if (CommonUtils.isNotEmpty(caldataAnalyzerFilterModel.getSysconFilterList())) {
      misc.append("List of System constant filters: ");
      misc.append(caldataAnalyzerFilterModel.getSysconFilterList().stream()
          .map(SystemConstantFilter::getSystemConstantName).collect(Collectors.joining(", "))).append("; \n");
    }

    // Append the list of customer filters used for cal data analysis to Misc info
    if (CommonUtils.isNotNull(caldataAnalyzerFilterModel.getCustomerFilterModel()) &&
        CommonUtils.isNotEmpty(caldataAnalyzerFilterModel.getCustomerFilterModel().getCustomerFilterList())) {
      misc.append("List of Customer filters: ");
      misc.append(caldataAnalyzerFilterModel.getCustomerFilterModel().getCustomerFilterList().stream()
          .map(CustomerFilter::getCustomerName).collect(Collectors.joining(", "))).append("; \n");
    }

    // Append the list of platform filters used for cal data analysis to Misc info
    if (CommonUtils.isNotNull(caldataAnalyzerFilterModel.getPlatformFilterModel()) &&
        CommonUtils.isNotEmpty(caldataAnalyzerFilterModel.getPlatformFilterModel().getPlatformFilterList())) {
      misc.append("List of Platform filters: ");
      misc.append(caldataAnalyzerFilterModel.getPlatformFilterModel().getPlatformFilterList().stream()
          .map(PlatformFilter::getEcuPlatformName).collect(Collectors.joining(", "))).append("; \n");
    }
    // Return the misc info as string
    return misc.toString();
  }


  /**
   * retrieve the dislaimer file
   *
   * @return rest response
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Path(WsCommonConstants.RWS_CDA_DISCLAIMER_FILE)
  @CompressData
  public Response getCaldataAnalyzerDisclaimerFile() throws IcdmException {
    byte[] ret = new IcdmFilesLoader(getServiceData())
        .getEntityObject(
            Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.CDA_DISCLAIMER_FILE_ID)))
        .getTabvIcdmFileData().getFileData();

    return Response.ok(ret).build();
  }

}
