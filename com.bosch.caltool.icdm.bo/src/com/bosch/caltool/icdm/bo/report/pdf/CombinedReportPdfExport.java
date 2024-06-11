/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.CombinedReportExportHandler;
import com.bosch.caltool.icdm.bo.cdr.DataAssessmentUtil;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuesRespDataProviderServer;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.bo.report.compli.PdfUtil;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.bo.qnaire.QnaireRespVersDataResolver;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RULE_SOURCE;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CombinedReviewResultExcelExportData;
import com.bosch.caltool.icdm.model.cdr.CombinedRvwExportInputModel;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.QnaireResponseCombinedModel;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwResultsSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReportConstants;
import com.bosch.caltool.icdm.model.user.User;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;

/**
 * @author MSP5COB
 */
public class CombinedReportPdfExport extends AbstractSimpleBusinessObject {


  /**
   *
   */
  private static final String DELIMITER_STRING = ", ";

  /**
   * Path of the base folder structure where baseline files need to be exported
   */
  private final String filepath;

  /**
   * variable to hold current line position
   */
  private double linePosition = 0;
  /**
   * holds current page object
   */
  private PDPage currentPage;
  /**
   * PD document
   */
  private PDDocument document;
  /**
   * Current stream
   */
  private PDPageContentStream currentStream;

  /**
   * Bosch Logo
   */
  private byte[] boschLogo;

  /**
   * Column width of Review Results table
   */
  private static final float REVIEW_RESULT_COL_WIDTH = 2.7f;

  /**
   * Column width of Review Results table
   */
  private static final float SEC_RESULT_COL_WIDTH = 5f;

  /**
   * Column width of Review Results table
   */
  private static final float REVIEW_QNAIRE_COL_WIDTH = 10f;

  /**
   * Column width of Review Info table
   */
  private static final float REVIEW_INFO_COL_WIDTH = 30f;

  /**
   * SPACER CONSTANT
   */
  private static final String SPACER = "  ";

  private final Set<Long> exportedFuncIdSet = new HashSet<>();


  /**
   * @param serviceData ServiceData
   * @param filepath filepath
   */
  public CombinedReportPdfExport(final ServiceData serviceData, final String filepath) {
    super(serviceData);
    this.filepath = filepath.trim();
  }

  /**
   * @param rvwResultId rvwResultId
   * @throws IcdmException IcdmException
   * @throws IOException IOException
   */
  public void createCombinedReport(final Long rvwResultId) throws IcdmException, IOException {

    getLogger().debug("Creating Combined Review Result for Review Result : {}", rvwResultId);

    CombinedRvwExportInputModel combinedRvwExportInputModel = getCombinedRvwExportInputModel(rvwResultId);
    CombinedReviewResultExcelExportData combinedExportData =
        new CombinedReportExportHandler(getServiceData(), combinedRvwExportInputModel).getCombinedEditorDataForExport();
    ReviewResultBO rvwResultBO = new ReviewResultBO(combinedExportData.getReviewResultEditorData());

    createCombinedReportForRvwResult(combinedExportData, rvwResultBO, rvwResultId);
  }

  /**
   * @param combinedExportData
   * @param qnaireColumns
   * @param workbook
   * @param pidcVersionWithDetails
   * @throws IcdmException
   * @throws IOException
   */
  private void createCombinedReportForQnaireResponse(final CombinedReviewResultExcelExportData combinedExportData)
      throws IcdmException, IOException {
    getLogger().debug("Creating Review Qnaire page in Combined Report");
    for (RvwWpResp rvwWpResp : combinedExportData.getRvwWpRespSet()) {
      Set<QnaireResponseCombinedModel> qnaireRespSet =
          combinedExportData.getQnaireRespCombinedModelMap().get(rvwWpResp.getId());

      if (CommonUtils.isNotEmpty(qnaireRespSet)) {

        SortedSet<QnaireResponseCombinedModel> qnaireResponseCombinedModels = new TreeSet<>(qnaireRespSet);
        for (QnaireResponseCombinedModel qnaireResponseCombinedModel : qnaireResponseCombinedModels) {

          RvwQnaireResponseModel qnaireRespModel = qnaireResponseCombinedModel.getRvwQnaireResponseModel();

          QuesRespDataProviderServer quesRespDataProviderServer = new QuesRespDataProviderServer(getServiceData(),
              qnaireResponseCombinedModel.getQnaireDefModel().getQuestionnaireVersion().getId(), qnaireRespModel);

          QnaireRespVersDataResolver qnaireRespVersDataResolver =
              new QnaireRespVersDataResolver(quesRespDataProviderServer);

          QnaireResponseBO qnaireResponseBO = new QnaireResponseBO(getServiceData(), qnaireResponseCombinedModel,
              qnaireRespVersDataResolver, qnaireResponseCombinedModel.getQnaireDefModel());

          createQuestResponseInfo(qnaireResponseBO, qnaireResponseCombinedModel);

          constructReviewQnaireTable(qnaireResponseBO);

        }
      }

    }
    getLogger().debug("Created Review Qnaire page in Combined Report");
  }

  private void createQuestResponseInfo(final QnaireResponseBO qnaireResponseBO,
      final QnaireResponseCombinedModel qnaireResponseCombinedModel)
      throws IOException {

    writeText("Review Questionnaire Response", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 20;

    StringBuilder qnaireNameFul = new StringBuilder();
    StringBuilder responsibilityNameFul = new StringBuilder();
    StringBuilder workPackageNameFul = new StringBuilder();
    StringBuilder qnaireStatusFul = new StringBuilder();

    qnaireNameFul.append(qnaireResponseBO.getQuesResponse().getName());
    qnaireNameFul.append(SPACER);
    qnaireNameFul.append("Response Version : ");
    qnaireNameFul.append(qnaireResponseBO.getQnaireRespModel().getRvwQnrRespVersion().getName());

    writeText(qnaireNameFul.toString(), PDType1Font.HELVETICA, 6, this.linePosition);
    this.linePosition -= 20;

    if (CommonUtils.isNotNull(qnaireResponseCombinedModel)) {

      responsibilityNameFul.append("Responsibility : ");
      responsibilityNameFul.append(qnaireResponseCombinedModel.getA2lResponsibility().getName());

      writeText(responsibilityNameFul.toString(), PDType1Font.HELVETICA, 6, this.linePosition);
      this.linePosition -= 20;

      workPackageNameFul.append("Work Package : ");
      workPackageNameFul.append(qnaireResponseCombinedModel.getA2lWorkPackage().getName());
      writeText(workPackageNameFul.toString(), PDType1Font.HELVETICA, 6, this.linePosition);
      this.linePosition -= 20;
    }
    qnaireStatusFul.append("Status : ");
    qnaireStatusFul.append(CDRConstants.QS_STATUS_TYPE
        .getTypeByDbCode(qnaireResponseBO.getQnaireRespModel().getRvwQnrRespVersion().getQnaireRespVersStatus())
        .getUiType());
    writeText(qnaireStatusFul.toString(), PDType1Font.HELVETICA, 6, this.linePosition);
    this.linePosition -= 20;

  }


  /**
   * Create Combined Report PDF
   *
   * @throws IOException - IO Exception
   * @throws IcdmException - ICDM Exception
   */
  private void createCombinedReportForRvwResult(final CombinedReviewResultExcelExportData combinedExportData,
      final ReviewResultBO rvwResultBO, final Long rvwResultId)
      throws IOException, IcdmException {
    // Creating a new empty pdf document
    this.document = new PDDocument();

    // get bosch logo, requesting user details
    this.boschLogo = DataAssessmentUtil.getBoschLogo(getServiceData());
    User user = new UserLoader(getServiceData()).getDataObjectByID(getServiceData().getUserId());

    // create new page
    this.currentPage = PdfUtil.createNewA1Page(this.document);

    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePositionForA1Size();

    // Adding information to pdf document
    PDDocumentInformation info = this.document.getDocumentInformation();

    // Draw header
    PdfUtil.constructA1Header(this.document, this.currentStream, this.boschLogo);

    // set created author and created date details
    info.setAuthor(user.getDescription());
    info.setCreationDate(Calendar.getInstance());

    writeText("Combined Report of Review Result and Review Questionnaires", PDType1Font.HELVETICA_BOLD, 15,
        this.linePosition);
    this.linePosition -= 20;

    writeText("Review Results", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 20;
    getLogger().debug("Writing Review Results to PDF");
    constructReviewResultTable(rvwResultBO);

    if (CommonUtils.isNotEmpty(rvwResultBO.getSecondaryResultsSorted())) {
      constructSecondaryResultTable(rvwResultBO);
    }

    // Create a new page
    createNewPage();

    writeText("Review Information", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 20;

    constructReviewInfoTable(rvwResultBO);

    // Create a new page
    createNewPage();

    createCombinedReportForQnaireResponse(combinedExportData);

    // Add Bosch logo as header to all pages in the dcoument
    addHeaderForPages();

    File filePathFile = new File(this.filepath);
    if (!filePathFile.exists()) {
      filePathFile.mkdirs();
    }

    String fullPdfFilePath = this.filepath + File.separator + "CombinedReviewReport.pdf";

    // Delete the PDF report if it already exists
    File outputFile = new File(fullPdfFilePath);
    if (outputFile.exists()) {
      Files.delete(outputFile.toPath());
      getLogger().info("Existing PDF file deleted to create new report : {}", outputFile.getPath());
    }

    getLogger().debug("Updating PDF to PDF/A Standard");
    // Convert the PDF into PDF/A
    PdfUtil.setPdfAStandard(this.document);

    getLogger().debug("Writing data to PDF file");
    // Save pdf document
    this.document.save(fullPdfFilePath);

    // close document
    this.document.close();

    getLogger().debug("Created Combined Review Result for Review Result : {}", rvwResultId);

    getLogger().info("Created Combined Review Result PDF created. Path : {}", fullPdfFilePath);

  }

  /**
   * Create Attributes Table details
   *
   * @throws IOException
   */
  private void constructReviewResultTable(final ReviewResultBO rvwResultBO) throws IOException {
    getLogger().debug("Creating Review Result page in Combined Report");

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH_A1 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition,
        (float) (PdfUtil.PAGE_HEIGHT_A1 - PdfUtil.HEADER_SIZE_A1), (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth,
        (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage, true, true);

    // List of column headers for the Review Result table
    List<String> rvwResultHdrs = Arrays.asList(CombinedReportExportColumn.resultSheetHeader);


    // Add Header row to table
    PdfUtil.addHeaderRowToTable(baseTable, rvwResultHdrs, REVIEW_RESULT_COL_WIDTH);

    createReviewResultTable(baseTable, rvwResultHdrs, rvwResultBO);


    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
    getLogger().debug("Created Review Result page in Combined Report");

  }

  /**
   * Create Attributes Table details
   *
   * @throws IOException
   */
  private void constructSecondaryResultTable(final ReviewResultBO rvwResultBO) throws IOException {

    getLogger().debug("Creating Secondary Result page in Combined Report");

    // Create a new page
    createNewPage();

    writeText("Secondary Review Results", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 20;

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH_A1 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition,
        (float) (PdfUtil.PAGE_HEIGHT_A1 - PdfUtil.HEADER_SIZE_A1), (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth,
        (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage, true, true);


    // column names for secondary result
    List<String> secResColumnList = new ArrayList<>();
    secResColumnList.add(DataAssessmentReportConstants.PARAMETER);
    SortedSet<RvwResultsSecondary> secondaryResultSet = rvwResultBO.getSecondaryResultsSorted();

    for (RvwResultsSecondary cDRSecondaryResult : secondaryResultSet) {
      if (RULE_SOURCE.RULE_SET.getDbVal().equals(cDRSecondaryResult.getSource())) {
        secResColumnList.add(cDRSecondaryResult.getRuleSetName());
      }
      else {
        secResColumnList.add(RULE_SOURCE.getSource(cDRSecondaryResult.getSource()).getUiVal());
      }
    }


    // Add Header row to table
    PdfUtil.addHeaderRowToTable(baseTable, secResColumnList, SEC_RESULT_COL_WIDTH);

    createSecondaryResultTable(baseTable, rvwResultBO);


    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }

    getLogger().debug("Created Secondary Result page in Combined Report");

  }

  /**
   * Create Attributes Table details
   *
   * @throws IOException
   */
  private void constructReviewQnaireTable(final QnaireResponseBO qnaireResponseBO) throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH_A1 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition,
        (float) (PdfUtil.PAGE_HEIGHT_A1 - PdfUtil.HEADER_SIZE_A1), (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth,
        (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage, true, true);

    List<String> qnaireHeaderCols = getQnaireColumns();

    // Add Header row to table
    PdfUtil.addHeaderRowToTable(baseTable, qnaireHeaderCols, REVIEW_QNAIRE_COL_WIDTH);

    createReviewQnaireTable(baseTable, qnaireResponseBO, qnaireHeaderCols);


    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }

  }

  /**
   * Create Attributes Table details
   *
   * @throws IOException
   * @throws IcdmException
   */
  private void constructReviewInfoTable(final ReviewResultBO rvwResultBO) throws IOException {

    getLogger().debug("Creating Review Info page in Combined Report");

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH_A1 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition,
        (float) (PdfUtil.PAGE_HEIGHT_A1 - PdfUtil.HEADER_SIZE_A1), (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth,
        (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage, true, true);
    CombinedReportExportColumn.getInstance();

    // List of column headers for the Review Info table
    List<String> rvwInfoHdrs = Arrays.asList(CombinedReportExportColumn.reviewInfoSheetHeader);


    createReviewInfoTable(baseTable, rvwInfoHdrs, rvwResultBO);


    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }

    getLogger().debug("Created Review Info page in Combined Report");

  }

  private void createReviewInfoTable(final BaseTable baseTable, final List<String> reviewInfoHdrs,
      final ReviewResultBO rvwResultBO) {
    for (String reviewInfoHdr : reviewInfoHdrs) {
      Row<PDPage> row = baseTable.createRow(2f);
      createColsForReviewInfoTable(reviewInfoHdr, row, rvwResultBO);
    }

  }

  private void createReviewQnaireTable(final BaseTable baseTable, final QnaireResponseBO qnaireRespEditorDataHandler,
      final List<String> qnaireHeaderCols) {

    SortedSet<RvwQnaireAnswer> rvwQnaireAnsSet = qnaireRespEditorDataHandler.getAllQuestionAnswers();
    // remove the rows that are hidden due to dependency
    rvwQnaireAnsSet.removeIf(rvwQnaire -> !qnaireRespEditorDataHandler.isQuestionVisible(rvwQnaire));

    Map<Long, String> sortedMap = getData(rvwQnaireAnsSet, qnaireRespEditorDataHandler);

    for (Long questionId : sortedMap.keySet()) {
      RvwQnaireAnswer rvwQnaireAnswerObject = qnaireRespEditorDataHandler.getAllQuestionMap().get(questionId);
      Row<PDPage> row = baseTable.createRow(2f);
      for (String reviewQnaireHdr : qnaireHeaderCols) {
        createColsForReviewQnaireTable(reviewQnaireHdr, rvwQnaireAnswerObject, qnaireRespEditorDataHandler, row);
      }
    }

  }

  /**
   * @param workbook
   * @param resultSheetHeader
   * @param parameter
   * @param row
   */
  private void createColsForReviewQnaireTable(final String columnHeader, final RvwQnaireAnswer ansObj,
      final QnaireResponseBO dataHandler, final Row<PDPage> row) {

    boolean applyColor = false;
    String cellValue = DataAssessmentReportConstants.EMPTY_STRING;

    switch (columnHeader) {
      case "No.":
        cellValue = dataHandler.getQuestionNumber(ansObj.getQuestionId());
        applyColor = dataHandler.checkHeading(ansObj);
        break;

      case "Question":
        cellValue = dataHandler.getName(ansObj.getQuestionId());
        applyColor = dataHandler.checkHeading(ansObj);
        break;

      case "Notes Regarding Question":
      case "Hinweis zur Frage":
        cellValue = dataHandler.getDescription(ansObj);
        applyColor = dataHandler.checkHeading(ansObj);
        break;

      case "Ready For Mass Production (Y/N)":
      case "Serienreife (J/N)":
        cellValue = dataHandler.getSeriesUIString(ansObj);
        applyColor = dataHandler.checkHeading(ansObj);
        break;

      case "Measurement Existing (Y/N)":
      case "Messung vorhanden (J/N)":
        cellValue = dataHandler.getMeasurementUIString(ansObj);
        applyColor = dataHandler.checkHeading(ansObj);
        break;

      case "Link":
        cellValue = dataHandler.getLinkUIString(ansObj);
        applyColor = dataHandler.checkHeading(ansObj);
        break;

      case "Open Issues":
      case "Offene Punkte":
        cellValue = dataHandler.getOpenPointsUIString(ansObj);
        applyColor = dataHandler.checkHeading(ansObj);
        break;

      case "Comment":
      case "Kommentar":
        cellValue = dataHandler.getRemarksUIString(ansObj);
        applyColor = dataHandler.checkHeading(ansObj);
        break;

      case "Answer":
      case "Ergebnis in Ordnung":
        cellValue =
            dataHandler.getQuestionResultOptionUIString(ansObj.getQuestionId(), ansObj.getSelQnaireResultOptID());
        applyColor = dataHandler.checkHeading(ansObj);
        break;
      case "Result":
        cellValue = dataHandler.getCalculatedResults(ansObj);
        applyColor = dataHandler.checkHeading(ansObj);
        break;
      default:
        break;
    }

    addColumnValsToQnaireTable(row, applyColor, cellValue, REVIEW_QNAIRE_COL_WIDTH);
  }


  private void createReviewResultTable(final BaseTable baseTable, final List<String> reviewResultHdrs,
      final ReviewResultBO rvwResultBO) {

    SortedSet<CDRResultParameter> parameterSet = getParameterSet(rvwResultBO);
    for (CDRResultParameter parameter : parameterSet) {
      Row<PDPage> row = baseTable.createRow(2f);
      for (String reviewResultHdr : reviewResultHdrs) {
        createColsForReviewResultTable(reviewResultHdr, parameter, row, rvwResultBO);
        this.exportedFuncIdSet.add(parameter.getRvwFunId());
      }

    }

  }

  private void createSecondaryResultTable(final BaseTable baseTable, final ReviewResultBO rvwResultBO) {

    SortedSet<CDRResultParameter> parameterSet = getParameterSet(rvwResultBO);
    for (CDRResultParameter parameter : parameterSet) {
      Row<PDPage> row = baseTable.createRow(2f);
      createColsForSecReviewTable(parameter, row, rvwResultBO);

    }

  }

  private SortedSet<CDRResultParameter> getParameterSet(final ReviewResultBO rvwResultBO) {
    final SortedSet<CDRResultParameter> parameterSet = new TreeSet<>(rvwResultBO::compareTo);
    parameterSet.addAll(rvwResultBO.getResultBo().getParameters());
    return parameterSet;
  }

  private CombinedRvwExportInputModel getCombinedRvwExportInputModel(final Long RvwResultId) {
    CombinedRvwExportInputModel combinedRvwExportInputModel = new CombinedRvwExportInputModel();
    combinedRvwExportInputModel.setRvwResultId(RvwResultId);
    combinedRvwExportInputModel.setOnlyReviewResult(false);
    combinedRvwExportInputModel.setOnlyRvwResAndQnaireWrkSet(false);
    combinedRvwExportInputModel.setOnlyRvwResAndQnaireLstBaseLine(true);
    combinedRvwExportInputModel.setLoadEditorData(true);
    return combinedRvwExportInputModel;
  }

  /**
   * Write content to PDF
   *
   * @param content - String content to be written
   * @param font - font
   * @param fontSize - size
   * @param linePosition1 - position to write
   * @throws IOException - exception
   */
  private void writeText(final String content, final PDType1Font font, final int fontSize, final double linePosition1)
      throws IOException {
    // if line position is beyond bottom margin of page, create new page and add content
    if (PdfUtil.isLinePositionWithinLimits(linePosition1)) {
      PDStreamUtils.write(this.currentStream, content, font, fontSize, (float) PdfUtil.LEFT_MARGIN,
          (float) linePosition1, Color.BLACK);
    }
    else {
      createNewPage();

      PDStreamUtils.write(this.currentStream, content, font, fontSize, (float) PdfUtil.LEFT_MARGIN,
          (float) this.linePosition, Color.BLACK);
    }
  }


  /**
   * Create a new page in PDF
   *
   * @throws IOException
   */
  private void createNewPage() throws IOException {
    // create new page
    this.currentPage = PdfUtil.createNewA1Page(this.document);

    // Close current stream
    this.currentStream.close();

    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePositionForA1Size();
  }

  /**
   * Method to add header for all created pages in document
   *
   * @param doc - document
   * @throws IOException
   */
  private void addHeaderForPages() throws IOException {
    this.currentStream.close();
    // Iterate through all pages in document
    for (PDPage page : this.document.getPages()) {
      this.currentStream = new PDPageContentStream(this.document, page, AppendMode.APPEND, false);
      PdfUtil.constructA1Header(this.document, this.currentStream, this.boschLogo);
      this.currentStream.close();
    }
  }


  /**
   * @param cdrResParam CDRResultParameter
   * @param rvwResultBO ReviewResultBO
   * @return String
   */
  public String getSSDLabelString(final CDRResultParameter cdrResParam, final ReviewResultBO rvwResultBO) {
    StringBuilder ssdLblStrBuilder = new StringBuilder();
    if (rvwResultBO.isComplianceParameter(cdrResParam)) {
      ssdLblStrBuilder.append("Compliance,");
    }
    if (rvwResultBO.isReadOnly(cdrResParam)) {
      ssdLblStrBuilder.append("Read Only,");
    }
    if (rvwResultBO.isBlackList(cdrResParam)) {
      ssdLblStrBuilder.append("Black List,");
    }
    if (rvwResultBO.isQssdParameter(cdrResParam)) {
      ssdLblStrBuilder.append("Q-SSD,");
    }
    if (rvwResultBO.isDependentParam(cdrResParam)) {
      ssdLblStrBuilder.append("Dependent,");
    }
    // delete "," if it is the last character
    return ssdLblStrBuilder.toString().isEmpty() ? ""
        : ssdLblStrBuilder.toString().substring(0, ssdLblStrBuilder.length() - 1);
  }

  private String getBitwise(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    if (null == parameter.getIsbitwise()) {
      return "";
    }
    return rvwResultBO.getParamIsBitWiseDisplay(parameter);
  }

  private String getLowerLimit(final CDRResultParameter parameter) {
    if (parameter.getLowerLimit() == null) {
      return "";
    }
    return parameter.getLowerLimit().toString();

  }

  private String getBitwiseLimit(final CDRResultParameter parameter) {
    if (parameter.getBitwiseLimit() == null) {
      return "";
    }
    return parameter.getBitwiseLimit();

  }


  private String getReadyForSeries(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    if (parameter.getRvwMethod() == null) {
      return "";
    }
    return rvwResultBO.getReadyForSeriesStr(parameter);

  }


  private String getUpperLimit(final CDRResultParameter parameter) {
    if (parameter.getUpperLimit() == null) {
      return "";
    }
    return parameter.getUpperLimit().toString();

  }

  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private String setDataForReferenceCol(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    return CommonUtils.checkNull(rvwResultBO.getRefValueString(parameter));
  }


  private String getExactMatch(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    if (parameter.getMatchRefFlag() == null) {
      return "";
    }
    return rvwResultBO.getExactMatchUiStr(parameter);

  }

  /**
   * @param workbook
   * @param resultSheetHeader
   * @param parameter
   * @param row
   */
  private void createColsForReviewResultTable(final String columnHeader, final CDRResultParameter parameter,
      final Row<PDPage> row, final ReviewResultBO rvwResultBO) {

    boolean applyColor = false;
    Color colorToApply = null;
    String cellValue = "";

    switch (columnHeader) {

      case DataAssessmentReportConstants.IS_BITWISE:
        cellValue = getBitwise(parameter, rvwResultBO);
        applyColor = isBitwiseFlagChanged(parameter, rvwResultBO);
        break;

      case DataAssessmentReportConstants.LOWER_LIMIT:
        cellValue = getLowerLimit(parameter);
        applyColor = isLowerLimitChanged(parameter, rvwResultBO);
        break;

      case DataAssessmentReportConstants.UPPER_LIMIT:
        cellValue = getUpperLimit(parameter);
        applyColor = isUpperLimitChanged(parameter, rvwResultBO);
        break;

      case DataAssessmentReportConstants.BITWISE_LIMIT:
        cellValue = getBitwiseLimit(parameter);
        applyColor = isBitwiseLimitChanged(parameter, rvwResultBO);
        break;

      case DataAssessmentReportConstants.READY_FOR_SERIES:
        cellValue = getReadyForSeries(parameter, rvwResultBO);
        applyColor = isReadyForSeriesFlagChanged(parameter, rvwResultBO);
        break;

      case DataAssessmentReportConstants.REFERENCE_VALUE:
        cellValue = setDataForReferenceCol(parameter, rvwResultBO);
        applyColor = rvwResultBO.isRefValChanged(parameter);
        break;

      case DataAssessmentReportConstants.REF_VAL_MATURITY_LEVEL:
        cellValue = rvwResultBO.getMaturityLevel(parameter);
        break;

      case DataAssessmentReportConstants.EXACT_MATCH:
        cellValue = getExactMatch(parameter, rvwResultBO);
        applyColor = isExactMatchFlagChanged(parameter, rvwResultBO);
        break;

      case DataAssessmentReportConstants.CHECKED_VALUE:
        cellValue = rvwResultBO.getCheckedValueString(parameter);
        applyColor = rvwResultBO.isCheckedValueChanged(parameter);
        break;

      case DataAssessmentReportConstants.RESULT:
        cellValue = rvwResultBO.getResult(parameter);
        applyColor = rvwResultBO.isResultChanged(parameter);
        break;

      case DataAssessmentReportConstants.SECONDARY_RESULT:
        cellValue = rvwResultBO.getCustomSecondaryResult(parameter);
        applyColor = rvwResultBO.isResultChanged(parameter);
        break;

      case DataAssessmentReportConstants.SCORE:
        String valueName = rvwResultBO.getScoreUIType(parameter);
        cellValue = CommonUtils.isEmptyString(valueName) ? "" : valueName;
        DATA_REVIEW_SCORE reviewScore = rvwResultBO.getScore(parameter);
        applyColor = true;
        colorToApply = getReviewScoreStyle(reviewScore);
        break;

      case DataAssessmentReportConstants.SCORE_DESCRIPTION:
        cellValue = getDescription(rvwResultBO.getScore(parameter));
        break;

      case DataAssessmentReportConstants.COMMENT:
        cellValue = CommonUtils.checkNull(parameter.getRvwComment());
        break;

      case DataAssessmentReportConstants.PARAMETER_HINT:
        cellValue = CommonUtils.checkNull(rvwResultBO.getParameterHint(parameter));
        break;

      default:
        cellValue = getColsForReviewResultTable(columnHeader, parameter, rvwResultBO);
        break;
    }

    addColumnValsToTable(row, applyColor, colorToApply, cellValue, REVIEW_RESULT_COL_WIDTH);
  }

  /**
   * @param parameter
   * @param rvwResultBO
   * @return
   */
  private boolean isExactMatchFlagChanged(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    return ((parameter.getMatchRefFlag()) != null) && rvwResultBO.isExactMatchFlagChanged(parameter);
  }

  /**
   * @param parameter
   * @param rvwResultBO
   * @return
   */
  private boolean isReadyForSeriesFlagChanged(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    return ((parameter.getRvwMethod()) != null) && rvwResultBO.isReadyForSeriesFlagChanged(parameter);
  }

  /**
   * @param parameter
   * @param rvwResultBO
   * @return
   */
  private boolean isBitwiseLimitChanged(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    return ((parameter.getBitwiseLimit()) != null) && rvwResultBO.isBitwiseLimitChanged(parameter);
  }

  /**
   * @param parameter
   * @param rvwResultBO
   * @return
   */
  private boolean isUpperLimitChanged(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    return ((parameter.getUpperLimit()) != null) && rvwResultBO.isUpperLimitChanged(parameter);
  }

  /**
   * @param parameter
   * @param rvwResultBO
   * @return
   */
  private boolean isLowerLimitChanged(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    return ((parameter.getLowerLimit()) != null) && rvwResultBO.isLowerLimitChanged(parameter);
  }

  /**
   * @param parameter
   * @param rvwResultBO
   * @return
   */
  private boolean isBitwiseFlagChanged(final CDRResultParameter parameter, final ReviewResultBO rvwResultBO) {
    return (null != parameter.getIsbitwise()) && rvwResultBO.isBitwiseFlagChanged(parameter);
  }


  /**
   * @param workbook
   * @param resultSheetHeader
   * @param parameter
   * @param row
   */
  private String getColsForReviewResultTable(final String columnHeader, final CDRResultParameter parameter,
      final ReviewResultBO rvwResultBO) {

    String cellValue = DataAssessmentReportConstants.EMPTY_STRING;

    switch (columnHeader) {

      case DataAssessmentReportConstants.COMPLIANCE_NAME:
        cellValue = getSSDLabelString(parameter, rvwResultBO);
        break;

      case DataAssessmentReportConstants.FC_NAME:
        cellValue = parameter.getFuncName();
        break;

      case DataAssessmentReportConstants.PARAMETER:
        cellValue = parameter.getName();
        break;

      case DataAssessmentReportConstants.LONG_NAME:
        cellValue = rvwResultBO.getFunctionParameter(parameter).getLongName();
        break;

      case DataAssessmentReportConstants.WP_NAME:
        cellValue = rvwResultBO.getWpName(parameter);
        break;

      case DataAssessmentReportConstants.RESPONSIBILITY_STR:
        cellValue = rvwResultBO.getRespName(parameter);
        break;

      case DataAssessmentReportConstants.RESPONSIBILITY_TYPE:
        cellValue = CommonUtils.checkNull(rvwResultBO.getRespType(parameter));
        break;

      case DataAssessmentReportConstants.TYPE:
        cellValue = CommonUtils.checkNull(rvwResultBO.getFunctionParameter(parameter).getType());
        break;

      case DataAssessmentReportConstants.CLASS:
        cellValue = rvwResultBO.getParameterClassStr(parameter);
        break;

      case DataAssessmentReportConstants.IS_CODE_WORD:
        cellValue = rvwResultBO.getParamIsCodeWordDisplay(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_STATUS:
        cellValue = rvwResultBO.getHistoryState(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_USER:
        cellValue = rvwResultBO.getHistoryUser(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_DATE:
        cellValue = rvwResultBO.getHistoryDate(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_WORK_PACKAGE:
        cellValue = rvwResultBO.getHistoryContext(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_PROJECT:
        cellValue = rvwResultBO.getHistoryProject(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_TARGET_VARIANT:
        cellValue = rvwResultBO.getHistoryTargetVariant(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_TEST_OBJECT:
        cellValue = rvwResultBO.getHistoryTestObject(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_PROGRAM_IDENTIFIER:
        cellValue = rvwResultBO.getHistoryProgramIdentifier(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_DATA_IDENTIFIER:
        cellValue = rvwResultBO.getHistoryDataIdentifier(parameter);
        break;

      case DataAssessmentReportConstants.CDFX_REMARK:
        cellValue = rvwResultBO.getHistoryRemark(parameter);
        break;

      case DataAssessmentReportConstants.IMPORTED_VALUE:
        cellValue = rvwResultBO.getParamCalDataPhy(parameter).toString();
        break;

      case DataAssessmentReportConstants.REFERENCE_VALUE_UNIT:
        cellValue = CommonUtils.checkNull(parameter.getRefUnit());
        break;

      case DataAssessmentReportConstants.CHECKED_VALUE_UNIT:
        cellValue = CommonUtils.checkNull(parameter.getCheckUnit());
        break;

      default:
        break;
    }

    return cellValue;
  }

  /**
   * @param workbook
   * @param resultSheetHeader
   * @param parameter
   * @param row
   */
  private void createColsForSecReviewTable(final CDRResultParameter parameter, final Row<PDPage> row,
      final ReviewResultBO rvwResultBO) {

    addColumnValsToTable(row, false, null, parameter.getName(), SEC_RESULT_COL_WIDTH);

    Map<Long, RvwParametersSecondary> secondaryResParams = rvwResultBO.getSecondaryResParams(parameter);
    if (secondaryResParams != null) {
      Collection<RvwParametersSecondary> values = secondaryResParams.values();
      SortedSet<RvwParametersSecondary> secondaryParamSet = new TreeSet<>();

      secondaryParamSet.addAll(values);

      for (RvwParametersSecondary cDRResParamSecondary : secondaryParamSet) {
        addColumnValsToTable(row, false, null, rvwResultBO.getSecondaryCommonResult(cDRResParamSecondary),
            SEC_RESULT_COL_WIDTH);
      }
    }
  }


  private Color getReviewScoreStyle(final DATA_REVIEW_SCORE reviewScore) {
    Color colorToApply = null;

    switch (reviewScore.getRating()) {
      case DATA_REVIEW_SCORE.RATING_NOT_REVIEWED:
        colorToApply = Color.RED;
        break;
      case DATA_REVIEW_SCORE.RATING_PRELIM_CAL:
        colorToApply = Color.YELLOW;
        break;

      case DATA_REVIEW_SCORE.RATING_CALIBRATED:
        colorToApply = Color.BLUE;
        break;
      case DATA_REVIEW_SCORE.RATING_COMPLETED:
      case DATA_REVIEW_SCORE.RATING_CHECKED:
        colorToApply = Color.GREEN;
        break;
      default:
        break;
    }

    return colorToApply;

  }


  /**
   * Method to populate column values in a table
   *
   * @param row
   * @param applyColor
   * @param colorToApply
   * @param cellValue
   * @param colWidth
   * @return
   * @throws IOException
   */
  private Cell<PDPage> addColumnValsToTable(final Row<PDPage> row, final boolean applyColor, final Color colorToApply,
      final String cellValue, final float colWidth) {
    Cell<PDPage> cell = row.createCell(colWidth, PdfUtil.removeIllegalArgs(cellValue));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(6);

    // Add the value in colored text
    if (applyColor && (null != colorToApply)) {
      cell.setTextColor(colorToApply);
    }
    // Set default color as Yellow if color is not passed
    else if (applyColor) {
      cell.setTextColor(Color.YELLOW);
    }

    return cell;
  }

  /**
   * Method to populate column values in a table
   *
   * @param row
   * @param applyColor
   * @param cellValue
   * @param colWidth
   * @return
   * @throws IOException
   */
  private Cell<PDPage> addColumnValsToQnaireTable(final Row<PDPage> row, final boolean applyColor,
      final String cellValue, final float colWidth) {
    Cell<PDPage> cell = row.createCell(colWidth, PdfUtil.removeIllegalArgs(cellValue));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(6);

    // Add the value in colored text
    if (applyColor) {
      cell.setFillColor(Color.YELLOW);
    }

    return cell;
  }


  /**
   * @param workbook
   * @param resultSheetHeader
   * @param parameter
   * @param row
   */
  private void createColsForReviewInfoTable(final String columnHeader, final Row<PDPage> row,
      final ReviewResultBO rvwResultBO) {

    ReviewResultServerBO resultBO = rvwResultBO.getResultBo();

    boolean applyColor = false;
    Color colorToApply = null;
    String cellValue = DataAssessmentReportConstants.EMPTY_STRING;

    addColumnValsToTable(row, applyColor, colorToApply, columnHeader, REVIEW_INFO_COL_WIDTH);

    switch (columnHeader) {

      case DataAssessmentReportConstants.PROJECT:
        cellValue = resultBO.getPidcVersion().getName();
        break;

      case DataAssessmentReportConstants.REVIEW_VARIANT:
        cellValue = resultBO.getVariant() != null ? resultBO.getVariant().getName() : ApicConstants.EMPTY_STRING;
        break;

      case DataAssessmentReportConstants.A2LFILE:
        cellValue = resultBO.getA2lFileName();
        break;

      case DataAssessmentReportConstants.CALIBRATION_ENGINEER:
        cellValue = resultBO.getCalibrationEngineer() != null ? resultBO.getCalibrationEngineer().getName()
            : ApicConstants.EMPTY_STRING;
        break;

      case DataAssessmentReportConstants.REVIEW_CREATOR:
        cellValue = resultBO.getCreatedUser().getDescription();
        break;

      case DataAssessmentReportConstants.AUDITOR:
        cellValue = resultBO.getAuditor() != null ? resultBO.getAuditor().getName() : ApicConstants.EMPTY_STRING;
        break;

      case DataAssessmentReportConstants.WORKPACKAGE:
        cellValue = resultBO.getWorkPackageName();
        break;

      case DataAssessmentReportConstants.RVW_CREATION_DATE:
        cellValue = resultBO.getCDRResult().getCreatedDate();
        break;

      case DataAssessmentReportConstants.OTH_PARTICIPANTS:
        cellValue = getOtherParticipants(resultBO);
        break;

      case DataAssessmentReportConstants.INTERNAL_FILES:
        cellValue = getInternalFiles(resultBO);
        break;

      case DataAssessmentReportConstants.INPUT_FILES:
        cellValue = getInpFiles(resultBO);
        break;

      case DataAssessmentReportConstants.PARENT_REVIEW:
        cellValue = resultBO.getBaseReviewInfo();
        break;

      case DataAssessmentReportConstants.REVIEW_TYPE:
        cellValue = resultBO.getReviewTypeStr();
        break;

      case DataAssessmentReportConstants.REVIEW_STATUS:
        cellValue = resultBO.getStatusUIType();
        break;

      case DataAssessmentReportConstants.REVIEW_DESCRIPTION:
        cellValue = resultBO.getCDRResult().getDescription();
        break;

      case DataAssessmentReportConstants.FILE_REVIEWED:
        cellValue = getFilesValue(resultBO.getInputFiles());
        break;

      case DataAssessmentReportConstants.FILES_ATTACHED:
        cellValue = getFilesValue(resultBO.getAdditionalFiles());
        break;

      case DataAssessmentReportConstants.FUNCTIONS_REVIEWWED:
        cellValue = getFuncReviewedValue(rvwResultBO);
        break;

      case DataAssessmentReportConstants.ATTRIBUTE_VALUES:
        cellValue = getAttrValue(resultBO);
        break;

      default:
        break;
    }

    addColumnValsToTable(row, applyColor, colorToApply, cellValue, REVIEW_INFO_COL_WIDTH);
  }

  private String getFilesValue(final SortedSet<RvwFile> filesSet) {

    final StringBuilder files = new StringBuilder();
    for (RvwFile file : filesSet) {
      files.append(file.getName());
      files.append(DELIMITER_STRING);
    }

    return "".equals(files.toString()) ? "" : files.substring(0, files.length() - 2);
  }

  private String getFuncReviewedValue(final ReviewResultBO rvwResultBO) {

    final StringBuilder functions = new StringBuilder();
    for (CDRResultFunction function : rvwResultBO.getFunctions()) {

      if (!this.exportedFuncIdSet.contains(function.getId())) {
        continue;
      }

      functions.append(rvwResultBO.getFunctionNameWithVersion(function));
      functions.append(DELIMITER_STRING);

    }
    return "".equals(functions.toString()) ? "" : functions.substring(0, functions.length() - 2);
  }

  private String getAttrValue(final ReviewResultServerBO resultBO) {

    final StringBuilder attrValStr = new StringBuilder();
    for (RvwAttrValue rvwAttrVal : resultBO.getRvwAttrValSet()) {
      attrValStr.append("Attribute : " + rvwAttrVal.getName() + " ,Value :" + rvwAttrVal.getValueDisplay());
      attrValStr.append(DELIMITER_STRING);
    }
    return "".equals(attrValStr.toString()) ? "" : attrValStr.substring(0, attrValStr.length() - 2);
  }


  private String getInpFiles(final ReviewResultServerBO resultBO) {
    final SortedSet<RvwFile> inpFiles = resultBO.getLabFunFiles();
    final StringBuilder inputFiles = new StringBuilder();
    for (RvwFile file : inpFiles) {
      inputFiles.append(file.getName());
      inputFiles.append(DELIMITER_STRING);
    }
    return "".equals(inputFiles.toString()) ? "" : inputFiles.substring(0, inputFiles.length() - 2);
  }

  private String getInternalFiles(final ReviewResultServerBO resultBO) {
    final SortedSet<RvwFile> internFiles = new TreeSet<>();
    if (CommonUtils.isNotEmpty(resultBO.getRuleFile())) {
      for (RvwFile icdmFile : resultBO.getRuleFile()) {
        internFiles.add(icdmFile);
      }
    }
    internFiles.addAll(resultBO.getOutputFiles());
    final StringBuilder files = new StringBuilder();
    for (RvwFile file : internFiles) {
      files.append(file.getName());
      files.append(DELIMITER_STRING);

    }

    if (files.length() == 0) {
      return ApicConstants.EMPTY_STRING;

    }
    return files.substring(0, files.length() - 2);

  }

  private String getOtherParticipants(final ReviewResultServerBO resultBO) {
    if (resultBO.getOtherParticipants().isEmpty()) {
      return ApicConstants.EMPTY_STRING;
    }
    else {
      final StringBuilder builder = new StringBuilder();

      for (RvwParticipant user : resultBO.getOtherParticipants()) {
        builder.append(user.getName());
        builder.append(DELIMITER_STRING);
      }
      return builder.substring(0, builder.length() - 2);
    }
  }

  private String getDescription(final DATA_REVIEW_SCORE dataReviewScore) {
    return getMessageValueForKey(DATA_REVIEW_SCORE.getMessageGroupName(), dataReviewScore.getDescKey());

  }

  private String getMessageValueForKey(final String grpName, final String key) {
    return new MessageLoader(getServiceData()).getMessage(grpName, key);
  }

  private List<String> getQnaireColumns() {
    List<String> qnaireColList = new ArrayList<>();
    qnaireColList.add(ApicConstants.SERIAL_NO);
    qnaireColList.add(ApicConstants.QUESTION_COL_NAME);
    qnaireColList.add(getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_HINT));
    qnaireColList.add(getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_SERIES_MAT_Y_N));
    qnaireColList.add(getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_MEASURABLE_Y_N));
    qnaireColList.add(CDRConstants.LINK_COL_NAME);
    qnaireColList
        .add(getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS));
    qnaireColList.add(getMessageValueForKey(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_REMARK));
    qnaireColList.add(CDRConstants.ANSWER_COL_NAME);
    qnaireColList.add(ApicConstants.RESULT_COL_NAME);
    return qnaireColList;
  }

  /**
   * Sort by value.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @param map the map
   * @return the map
   */
  public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {
    List<Entry<K, V>> newList = new ArrayList<>(map.entrySet());
    newList.sort(Entry.comparingByValue());
    Map<K, V> sortedMap = new LinkedHashMap<>();
    for (Entry<K, V> entry : newList) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }

  /**
   * @param rvwQnaireAnsSet
   * @param dataHandler
   * @return
   */
  private Map<Long, String> getData(final SortedSet<RvwQnaireAnswer> rvwQnaireAnsSet,
      final QnaireResponseBO dataHandler) {
    Map<Long, String> questionIds = new HashMap<>();
    for (RvwQnaireAnswer rvwQnaireAnswerObject : rvwQnaireAnsSet) {
      questionIds.put(rvwQnaireAnswerObject.getQuestionId(),
          dataHandler.getPaddedQuestionNumber(rvwQnaireAnswerObject.getQuestionId()));
    }
    return sortByValue(questionIds);
  }


}
