/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cda;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.JsonUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerFilterModel;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultFileModel;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultModel;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultSummary;
import com.bosch.caltool.icdm.model.cda.CustomerFilter;
import com.bosch.caltool.icdm.model.cda.CustomerFilterModel;
import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class CalDataAnalyserServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link CalDataAnalyzerServiceClient#invokeCalDataAnalyzer(CaldataAnalyzerFilterModel, String) }
   * <br>
   * Test CalDataAnalyser Service, no filter
   *
   * @throws ApicWebServiceException service error
   * @throws IOException json parsing error
   * @throws InvalidInputException result model creation error
   */
  @Test
  public void testInvokeCalDataAnalyzer01NoFilter() throws ApicWebServiceException, InvalidInputException, IOException {
    String outputFilePath = prepareOutputPath();
    new CalDataAnalyzerServiceClient().invokeCalDataAnalyzer(new CaldataAnalyzerFilterModel(), outputFilePath);
    assertTrue("Output file created", new File(outputFilePath).exists());

    CaldataAnalyzerResultModel resultModel = createOutput(outputFilePath);
    verifyResultModelBasics(resultModel);
  }

  /**
   * Test method for {@link CalDataAnalyzerServiceClient#invokeCalDataAnalyzer(CaldataAnalyzerFilterModel, String) }
   * <br>
   * Test CalDataAnalyser Service, with customer, parameter, function filters
   *
   * @throws ApicWebServiceException service error
   * @throws IOException json parsing error
   * @throws InvalidInputException result model creation error
   */
  @Test
  public void testInvokeCalDataAnalyzer02CustFunParFilters()
      throws ApicWebServiceException, InvalidInputException, IOException {

    CaldataAnalyzerFilterModel caldataAnalyzerFilterModel = new CaldataAnalyzerFilterModel();

    List<CustomerFilter> customerFilterList = new ArrayList<>();
    CustomerFilter customerFilter = new CustomerFilter();
    customerFilter.setCustomerName("HONDA");
    customerFilterList.add(customerFilter);
    CustomerFilterModel customerFilterModel = new CustomerFilterModel();
    customerFilterModel.setCustomerFilterList(customerFilterList);
    customerFilterModel.setInverseFlag(true);
    caldataAnalyzerFilterModel.setCustomerFilterModel(customerFilterModel);

    List<ParameterFilterLabel> paramList = new ArrayList<ParameterFilterLabel>();
    ParameterFilterLabel parameterFilterLabel = new ParameterFilterLabel();
    parameterFilterLabel.setLabel("CWBBKR");
    parameterFilterLabel.setMustExist(false);
    paramList.add(parameterFilterLabel);
    caldataAnalyzerFilterModel.setParamFilterList(paramList);

    List<FunctionFilter> functionFilterList = new ArrayList<FunctionFilter>();
    FunctionFilter functionFilter = new FunctionFilter();
    functionFilter.setFunctionName("BBKR");
    functionFilter.setFunctionVersion("*");
    functionFilterList.add(functionFilter);
    caldataAnalyzerFilterModel.setFunctionFilterList(functionFilterList);

    String outputFilePath = prepareOutputPath();

    new CalDataAnalyzerServiceClient().invokeCalDataAnalyzer(caldataAnalyzerFilterModel, outputFilePath);

    assertTrue("Output file created", new File(outputFilePath).exists());

    CaldataAnalyzerResultModel resultModel = createOutput(outputFilePath);
    verifyResultModelBasics(resultModel);

    assertTrue("label analyzed count > 0", resultModel.getSummaryModel().getLabelsAnalyzedCount() > 0);


  }

  private void verifyResultModelBasics(final CaldataAnalyzerResultModel resultModel) {
    assertNotNull("result processed", resultModel);
    assertFalse("result json list not empty", resultModel.getJsonFiles().isEmpty());
    assertNotNull("summary model available", resultModel.getSummaryModel());
    assertFalse("result file list not empty", resultModel.getResultFiles().isEmpty());
  }

  /**
   * Test method for {@link CalDataAnalyzerServiceClient#getDisclaimerFile(String)}
   *
   * @throws ApicWebServiceException service error
   * @throws IOException error while unzipping response
   */
  @Test
  public void testGetDisclaimerFile() throws ApicWebServiceException, IOException {

    byte[] bytes = new CalDataAnalyzerServiceClient().getDisclaimerFile(CommonUtils.getSystemUserTempDirPath());

    Map<String, byte[]> bytesMap = ZipUtils.unzip(bytes);
    byte[] disclaimerFile = bytesMap.get(ApicConstants.CDA_DISCLAIMER_FILE_NAME);
    assertNotNull("disclaimer file downloaded", disclaimerFile);

    String disclaimerText = FileIOUtil.convertHtmlByteToString(disclaimerFile);
    LOG.info("disclaimer text : \n{}", disclaimerText);
    assertTrue("disclaimer text is not null or empty", CommonUtils.isNotEmptyString(disclaimerText));

  }

  /**
   * @return
   */
  private String prepareOutputPath() {
    // create outputdir if it doesnt exist
    new File(ApicConstants.CDA_OUTPUT_DIR).mkdirs();

    String uniqueness = new SimpleDateFormat("dd-MM-yyyy_HH_mm").format(new Date()) + "_" + System.currentTimeMillis();
    String outputFilePath = ApicConstants.CDA_OUTPUT_DIR + File.separator + ApicConstants.CDA_ZIPPED_FILE_NAME +
        uniqueness + ApicConstants.ZIP_FILE_EXT;

    return outputFilePath;
  }

  private CaldataAnalyzerResultModel createOutput(final String outputFilePath)
      throws InvalidInputException, IOException {
    String outputFileDir = outputFilePath.substring(0, outputFilePath.lastIndexOf("."));
    ZipUtils.unzip(outputFilePath, outputFileDir);
    return createResultModel(outputFileDir);
  }

  /**
   * Method to create result model
   *
   * @param outputDir
   * @throws InvalidInputException
   */
  private CaldataAnalyzerResultModel createResultModel(final String outputDir) throws InvalidInputException {
    CaldataAnalyzerResultModel resultModel = new CaldataAnalyzerResultModel();

    List<CaldataAnalyzerResultFileModel> resultFileList = new ArrayList<>();
    List<CaldataAnalyzerResultFileModel> jsonFileList = new ArrayList<>();

    File folder = new File(outputDir);

    if (new File(outputDir).exists() && (folder.listFiles().length > 0)) {
      resultModel.setOutputDirectory(outputDir);
      for (File file : folder.listFiles()) {
        CaldataAnalyzerResultFileModel resultFile =
            new CaldataAnalyzerResultFileModel(file.getName(), file.getAbsolutePath(), (file.length()) / 1000);
        if (resultFile.getFileName().endsWith(".json")) {
          jsonFileList.add(resultFile);
        }
        else {
          resultFileList.add(resultFile);
        }
      }
      resultModel.setResultFiles(resultFileList);
      resultModel.setJsonFiles(jsonFileList);
    }

    // create summary model from json
    for (CaldataAnalyzerResultFileModel caldataAnalyzerResultFileModel : jsonFileList) {
      if (caldataAnalyzerResultFileModel.getFileName().equalsIgnoreCase(ApicConstants.CALDATA_JSON_FILE_NAME)) {

        CaldataAnalyzerResultSummary summary = JsonUtil.toModel(
            new File(caldataAnalyzerResultFileModel.getFilePath()), CaldataAnalyzerResultSummary.class);
        if (summary != null) {
          resultModel.setSummaryModel(summary);
        }

      }
    }

    logResultModel(resultModel);

    return resultModel;

  }

  private void logResultModel(final CaldataAnalyzerResultModel model) {
    StringBuilder str = new StringBuilder();

    str.append("Summary : ").append(model.getSummaryModel());
    str.append('\n').append("Result Files :");
    model.getResultFiles().forEach(f -> str.append("\n  ").append(f));

    str.append('\n').append("JSON Files :");
    model.getJsonFiles().forEach(f -> str.append("\n  ").append(f));

    LOG.info(str.toString());
  }

}
