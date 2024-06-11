/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.DataAssessmentUtil;
import com.bosch.caltool.icdm.bo.comphex.CompHexWithCDFxProcess;
import com.bosch.caltool.icdm.bo.report.compli.PdfUtil;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DA_QS_STATUS_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.WP_RESP_STATUS_TYPE;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReportConstants;
import com.bosch.caltool.icdm.model.user.User;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;

/**
 * @author TRL1COB
 */
public class CompareHexPdfExport extends AbstractSimpleBusinessObject {

  /**
   * Constant for Hex compare report directory
   */
  private static final String HEX_COMPARE_REPORT = "Hex Compare Report";

  private final DataAssessmentReport dataAssessmentReport;

  /**
   * Path of the base folder structure where baseline files need to be exported
   */
  private final String filePath;
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
   * User object
   */
  private User user;

  private static final int RESULTS_INFO_COL_WIDTH = 70;

  private static final int COUNT_COL_WIDTH = 40;

  private static final int COMP_RESULTS_COL_WIDTH = 5;

  /**
   * Constructor
   *
   * @param serviceData Service Data
   * @param dataAssessmentReport Data assessment Report model
   * @param filePath Path where the file need to be exported
   */
  public CompareHexPdfExport(final ServiceData serviceData, final DataAssessmentReport dataAssessmentReport,
      final String filePath) {
    super(serviceData);
    this.dataAssessmentReport = dataAssessmentReport;
    this.filePath = filePath;
  }

  /**
   * To Export the HEX Compare results into PDF
   *
   * @throws IOException IO Exception
   * @throws IcdmException ICDM Exception
   * @throws ClassNotFoundException Class not found exception
   */
  public void createCompHexPdf() throws IOException, IcdmException, ClassNotFoundException {

    // Creating a new empty pdf document
    this.document = new PDDocument();

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
    info.setAuthor(this.user.getDescription());
    info.setCreationDate(Calendar.getInstance());

    // PIDC Attributes Details
    writeText("Compare HEX Report", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    writeText("Hex Compare Results Info", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 20;
    getLogger().debug("Writing Hex Compare Statistics Info to PDF");
    createCompHexStatisticsTable();

    getLogger().debug("Creating a new page in compare hex report");
    createNewPage();

    writeText("Hex Compare Results", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 20;
    getLogger().debug("Writing Compare HEX Results to PDF");
    createCompHexResultsTable();

    // Add Bosch logo as header to all pages in the dcoument
    addHeaderForPages();

    getLogger().debug("Creating " + HEX_COMPARE_REPORT + "directory");
    File file = DataAssessmentUtil.createFileDirectory(this.filePath, HEX_COMPARE_REPORT);
    fetchOtherCompareHexRelatedFiles(file);
    String fullPdfFilePath = file.getAbsolutePath() + File.separator + "CompareHEXReport.pdf";

    // Delete the PDF report if it already exists
    File outputFile = new File(fullPdfFilePath);
    if (outputFile.exists()) {
      Files.delete(outputFile.toPath());
      getLogger().info("Existing PDF file deleted to create new report : {}", outputFile.getPath());
    }

    getLogger().debug("Updating PDF to PDF/A Standard");
    // Convert the PDF into PDF/A
    PdfUtil.setPdfAStandard(this.document);

    getLogger().debug("Saving compare hex report PDF file");
    // Save pdf document
    this.document.save(fullPdfFilePath);

    // close document
    this.document.close();

    getLogger().info("Compare HEX Report PDF created. Path : {}", fullPdfFilePath);

  }

  /**
   * Create a table containing compare hex statistics
   *
   * @throws IOException
   */
  private void createCompHexStatisticsTable() throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // Adding table values
    getLogger().debug("Creating rows and columns data for comp hex statistics table started");
    List<String> compareInfo = Arrays.asList(CompHexPdfExportColumn.compareResultsInfo);
    for (String info : compareInfo) {
      Row<PDPage> row = baseTable.createRow(2f);
      for (int col = 0; col < 2; col++) {
        fillStatisticsDataIntoTable(info, row, col);
      }
    }
    getLogger().debug("Creating rows and columns data for comp hex statistics table completed");

    getLogger().debug("Drawing table data for comp hex statistics info started");
    // Creating the table in PDF
    this.linePosition = baseTable.draw() - 20;
    getLogger().debug("Drawing table data for comp hex statistics info completed");


    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }

  }

  /**
   * Fill the staistics data into table
   *
   * @param baseTable
   * @throws IOException
   */
  private void fillStatisticsDataIntoTable(final String info, final Row<PDPage> row, final int colIndx) {

    String cellValue = DataAssessmentReportConstants.EMPTY_STRING;

    if (colIndx == 0) {
      cellValue = info;
      PdfUtil.addColumnValsToTable(row, false, null, cellValue, RESULTS_INFO_COL_WIDTH);
    }
    else if (colIndx == 1) {
      switch (info) {
        case DataAssessmentReportConstants.HEX_FILE:
          cellValue = this.dataAssessmentReport.getHexFileName();
          break;

        case DataAssessmentReportConstants.A2LFILE:
          cellValue = this.dataAssessmentReport.getA2lFileName();
          break;

        case DataAssessmentReportConstants.COMP_VARIANT:
          cellValue = CommonUtils.isEmptyString(this.dataAssessmentReport.getPidcVariantName()) ? "<NO-VARIANT>"
              : this.dataAssessmentReport.getPidcVariantName();
          break;

        default:
          break;
      }

      if (CommonUtils.isNotNull(this.dataAssessmentReport.getDataAssmntCompHexData()) &&
          CommonUtils.isNotNull(this.dataAssessmentReport.getDataAssmntCompHexData().getCompareHexStatics())) {
        CompHexStatistics statistics = this.dataAssessmentReport.getDataAssmntCompHexData().getCompareHexStatics();
        switch (info) {

          case DataAssessmentReportConstants.TOTAL_PARAMS:
            cellValue = String.valueOf(statistics.getStatTotalParamInA2L());
            break;

          case DataAssessmentReportConstants.EQUAL_REVIEWED:
            cellValue = String.valueOf(statistics.getStatParamRvwdEqual());
            break;

          case DataAssessmentReportConstants.COMPLI_PARAMS:
            cellValue = String.valueOf(statistics.getStatCompliParamInA2L());
            break;

          case DataAssessmentReportConstants.COMPLI_PARAMS_PASSED:
            cellValue = String.valueOf(statistics.getStatCompliParamPassed());
            break;

          case DataAssessmentReportConstants.NUM_PARAM_BSH_RESP:
            cellValue = String.valueOf(statistics.getstatNumParamInBoschResp());
            break;

          case DataAssessmentReportConstants.NUM_PARAM_BSH_RESP_RVW:
            cellValue = String.valueOf(statistics.getstatNumParamInBoschRespRvwed());
            break;

          case DataAssessmentReportConstants.PARAM_BSH_RESP_RVW:
            cellValue = String.valueOf(statistics.getStatParamWithBoschRespRvw() + "%");
            break;

          case DataAssessmentReportConstants.PARAM_BSH_RESP_QNAIRE:
            cellValue = String.valueOf(statistics.getStatParamWithBoschRespQnaireRvw() + "%");
            break;

          case DataAssessmentReportConstants.QNAIRE_NEGATIVE_ANSWER:
            cellValue = String.valueOf(statistics.getStatQnaireNagativeAnswer());
            break;

          case DataAssessmentReportConstants.CONSIDERED_PREVIOUS_REVIEWS:
            cellValue = getDisplayTextForConsideredPreVers();
            break;

          default:
            break;
        }
      }
      PdfUtil.addColumnValsToTable(row, false, null, cellValue, COUNT_COL_WIDTH);
    }
  }

  private String getDisplayTextForConsideredPreVers() {
    return this.dataAssessmentReport.getConsiderRvwsOfPrevPidcVers() ? CommonUtilConstants.DISPLAY_YES
        : CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * Create a table contsaining compare hex results
   *
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void createCompHexResultsTable() throws IOException, ClassNotFoundException {


    BaseTable qnaireStatusLegend = createQnaireStatusLegendTable();

    this.linePosition = qnaireStatusLegend.draw() - 20;

    BaseTable createWorkPackageStatusLegendTable = createWorkPackageStatusLegendTable();

    this.linePosition = createWorkPackageStatusLegendTable.draw() - 20;

    this.linePosition -= 20;

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH_A1 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition,
        (float) (PdfUtil.PAGE_HEIGHT_A1 - PdfUtil.HEADER_SIZE), (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth,
        (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage, true, true);

    List<String> resultHdrs = Arrays.asList(CompHexPdfExportColumn.compareHexResultsHeader);

    getLogger().debug("Adding header rows for comp hex results table started");
    // Add Header row to table
    addHeaderRowToTable(baseTable, resultHdrs, COMP_RESULTS_COL_WIDTH);
    getLogger().debug("Adding header rows for comp hex results table completed");

    List<DaCompareHexParam> inputSet = this.dataAssessmentReport.getDataAssmntCompHexData().getDaCompareHexParam();

    getLogger().debug("Creating rows and columns data for comp hex results table started");
    int serialNum = 0;
    for (DaCompareHexParam compObj : inputSet) {
      Row<PDPage> row = baseTable.createRow(2f);
      serialNum++;
      for (String hdr : resultHdrs) {
        generateColumnData(hdr, serialNum, compObj, row);
      }
    }
    getLogger().debug("Creating rows and columns data for comp hex results table completed");

    getLogger().debug("Drawing table data for comp hex results info started");
    // Creating the table in PDF
    this.linePosition = baseTable.draw() - 20;
    getLogger().debug("Drawing table data for comp hex results info completed");

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }

  }

  /**
   * @return
   * @throws IOException
   */
  private BaseTable createQnaireStatusLegendTable() throws IOException {
    // Calculate table width
    double tableWidthLegend = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTableLegend = new BaseTable((float) this.linePosition,
        (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE), (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidthLegend,
        (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage, true, true);

    // add data to table
    List<String> resultHdrsLegend = Arrays.asList(CompHexPdfExportColumn.compareHexQnaireResultLegend);
    addHeaderRowToTable(baseTableLegend, resultHdrsLegend, RESULTS_INFO_COL_WIDTH);
    for (DA_QS_STATUS_TYPE status_TYPE : CDRConstants.DA_QS_STATUS_TYPE.values()) {
      Row<PDPage> row = baseTableLegend.createRow(2f);
      for (String hdr : resultHdrsLegend) {
        if (DataAssessmentReportConstants.QNAIRE_STATUS.equalsIgnoreCase(hdr)) {
          PdfUtil.addColumnValsToTable(row, false, null, status_TYPE.getDbType(), RESULTS_INFO_COL_WIDTH);
        }
        else if (DataAssessmentReportConstants.QNAIRE_STATUS_DETAILS.equalsIgnoreCase(hdr)) {
          PdfUtil.addColumnValsToTable(row, false, null, status_TYPE.getUiType(), RESULTS_INFO_COL_WIDTH);
        }
      }
    }
    return baseTableLegend;
  }

  /**
   * @return
   * @throws IOException
   */
  private BaseTable createWorkPackageStatusLegendTable() throws IOException {
    // Calculate table width
    double tableWidthWrkPkgLegend = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTableWrkPkgLegend = new BaseTable((float) this.linePosition,
        (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE), (float) PdfUtil.BOTTOM_MARGIN,
        (float) tableWidthWrkPkgLegend, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage, true, true);

    // add data to table
    List<String> resultWrkPkgHdrsLegend = Arrays.asList(CompHexPdfExportColumn.compareHexWrkPkgLegend);
    addHeaderRowToTable(baseTableWrkPkgLegend, resultWrkPkgHdrsLegend, RESULTS_INFO_COL_WIDTH);
    for (WP_RESP_STATUS_TYPE status_TYPE : CDRConstants.WP_RESP_STATUS_TYPE.values()) {
      Row<PDPage> row = baseTableWrkPkgLegend.createRow(2f);
      for (String hdr : resultWrkPkgHdrsLegend) {
        if (DataAssessmentReportConstants.WRKPKG_STATUS.equalsIgnoreCase(hdr)) {
          PdfUtil.addColumnValsToTable(row, false, null, status_TYPE.getDbType(), RESULTS_INFO_COL_WIDTH);
        }
        else if (DataAssessmentReportConstants.WRKPKG_STATUS_DETAILS.equalsIgnoreCase(hdr)) {
          PdfUtil.addColumnValsToTable(row, false, null, status_TYPE.getUiType(), RESULTS_INFO_COL_WIDTH);
        }
      }
    }
    return baseTableWrkPkgLegend;
  }

  /**
   * Generate column data for compare hex results table
   *
   * @param columnHeader the column header
   * @param compObj the comp obj
   * @param row the row
   * @param col the col
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void generateColumnData(final String columnHeader, final int serialNum, final DaCompareHexParam compObj,
      final Row<PDPage> row)
      throws ClassNotFoundException, IOException {
    boolean applyColor = false;
    Color colorToApply = null;
    String cellValue = DataAssessmentReportConstants.EMPTY_STRING;

    switch (columnHeader) {

      case DataAssessmentReportConstants.SERIAL_NUM:
        cellValue = String.valueOf(serialNum);
        break;

      case DataAssessmentReportConstants.COMPLIANCE_NAME:
        cellValue = getSSDClassString(compObj);
        break;

      case DataAssessmentReportConstants.TYPE:
        cellValue = compObj.getParamType().getText();
        break;

      case DataAssessmentReportConstants.PARAMETER:
        cellValue = compObj.getParamName();
        break;

      case DataAssessmentReportConstants.FUNCTION:
        cellValue = compObj.getFuncName();
        break;

      case DataAssessmentReportConstants.FC_VERSION:
        cellValue = compObj.getFuncVers();
        break;

      case DataAssessmentReportConstants.WP:
        cellValue = compObj.getWpName();
        break;

      case DataAssessmentReportConstants.RESP_TYPE:
        cellValue = compObj.getRespType();
        break;

      case DataAssessmentReportConstants.RESP:
        cellValue = compObj.getRespName();
        break;

      case DataAssessmentReportConstants.WP_FINISHED:
        cellValue = compObj.getWpFinishedStatus();
        break;

      case DataAssessmentReportConstants.LATEST_A2L_VER:
        cellValue = compObj.getLatestA2lVersion();
        break;

      case DataAssessmentReportConstants.LATEST_FUNC_VER:
        cellValue = compObj.getLatestFunctionVersion();
        break;

      case DataAssessmentReportConstants.QUESTIONNAIRE_STATUS:
        cellValue = compObj.getQnaireStatus();
        break;

      case DataAssessmentReportConstants.IS_REVIEWED:
        cellValue = compObj.isReviewed() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
        break;

      case DataAssessmentReportConstants.EQUAL:
        cellValue = compObj.isEqual() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
        break;

      case DataAssessmentReportConstants.HEX_VALUE:
        cellValue = getCalDataDisplayValue(compObj.getHexValue());
        break;

      case DataAssessmentReportConstants.REVIEWED_VALUE:
        cellValue = getCalDataDisplayValue(compObj.getReviewedValue());
        break;

      case DataAssessmentReportConstants.COMPLI_RESULT:
        cellValue = compObj.getCompliResult().getUiValue();
        break;

      case DataAssessmentReportConstants.REVIEW_SCORE:
        String reviewScr = compObj.getReviewScore();
        if (cellValue != null) {
          DATA_REVIEW_SCORE scoreVal = DATA_REVIEW_SCORE.getType(reviewScr);
          if (scoreVal != null) {
            cellValue = scoreVal.getScoreDisplay();
            applyColor = true;
            colorToApply = getReviewScoreStyle(scoreVal);
          }
        }
        break;

      case DataAssessmentReportConstants.REVIEW_COMMENTS:
        cellValue = compObj.getLatestReviewComments();
        break;

      default:
        cellValue = DataAssessmentReportConstants.EMPTY_STRING;
        break;
    }

    PdfUtil.addColumnValsToTable(row, applyColor, colorToApply, cellValue, COMP_RESULTS_COL_WIDTH);
  }


  /**
   * Get SSD Class string for the parameter
   *
   * @param compObj DaCompareHexParam Obj
   * @return
   */
  private String getSSDClassString(final DaCompareHexParam compObj) {
    StringBuilder ssdLblStrBuilder = new StringBuilder();
    if (compObj.isCompli()) {
      ssdLblStrBuilder.append("Compliance,");
    }
    if (compObj.isReadOnly()) {
      ssdLblStrBuilder.append("Read Only,");
    }
    if (compObj.isDependantCharacteristic()) {
      ssdLblStrBuilder.append("Dependant Characteristic,");
    }
    if (compObj.isBlackList()) {
      ssdLblStrBuilder.append("Black List,");
    }
    if (compObj.isQssdParameter()) {
      ssdLblStrBuilder.append("Q-SSD,");
    }
    // delete "," if it is the last character
    return ssdLblStrBuilder.toString().isEmpty() ? ""
        : ssdLblStrBuilder.toString().substring(0, ssdLblStrBuilder.length() - 1);
  }

  /**
   * Convert the byte[] into Caldata Object and get display value from it
   *
   * @param value
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private String getCalDataDisplayValue(final byte[] value) throws IOException, ClassNotFoundException {
    String cellValue = DataAssessmentReportConstants.EMPTY_STRING;
    CalData calData = CalDataUtil.getCalDataObj(value);
    if (CommonUtils.isNotNull(calData)) {
      cellValue = calData.getCalDataPhy().getSimpleDisplayValue();
    }
    return cellValue;
  }

  /**
   * Get the font color to be used for review score
   *
   * @param reviewScore reviewScore
   * @return the review color for the score
   */
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
   * Copy all other files created during Hex compare process to the data assessment baseline directory
   *
   * @param file Destination directory where files need to be copied
   * @throws IcdmException
   */
  private void fetchOtherCompareHexRelatedFiles(final File destDir) throws IcdmException {
    getLogger().debug("Fetching other files related to compare HEX using compare hex reference ID");
    if (CommonUtils.isNotNull(this.dataAssessmentReport.getDataAssmntCompHexData()) &&
        CommonUtils.isNotEmptyString(this.dataAssessmentReport.getDataAssmntCompHexData().getReferenceId())) {
      String referenceId = this.dataAssessmentReport.getDataAssmntCompHexData().getReferenceId();
      String sourceDir = CompHexWithCDFxProcess.COMP_HEX_WORK_DIR + CompHexWithCDFxProcess.FILE_DELIMITER + referenceId;

      // To exclude file with .pdf extension - Complaince report
      FileFilter pdfFilterer = file -> !file.getName().endsWith(".pdf");
      try {
        FileUtils.copyDirectory(new File(sourceDir), destDir, pdfFilterer);
      }
      catch (IOException ex) {
        throw new IcdmException("An exception occured while fetching compare hex related files using reference ID", ex);
      }
    }
    getLogger().debug("Fetching other files related to compare HEX using compare hex reference ID");
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
    getLogger().debug("Adding headers to all the pages in PDF started");
    this.currentStream.close();
    // Iterate through all pages in document
    for (PDPage page : this.document.getPages()) {
      this.currentStream = new PDPageContentStream(this.document, page, AppendMode.APPEND, false);
      PdfUtil.constructA1Header(this.document, this.currentStream, this.boschLogo);
      this.currentStream.close();
    }
    getLogger().debug("Adding headers to all the pages in PDF completed");
  }

  /**
   * Method to add header row to a table
   *
   * @param baseTable
   * @param columnHeaders
   * @param rowWidth
   */
  private void addHeaderRowToTable(final BaseTable baseTable, final List<String> columnHeaders, final int colWidth) {
    Row<PDPage> row = baseTable.createRow(2f);
    for (String hdr : columnHeaders) {
      Cell<PDPage> cell = row.createCell(colWidth, hdr);
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
    }
    baseTable.addHeaderRow(row);
  }


  /**
   * @param boschLogo the boschLogo to set
   */
  public void setBoschLogo(final byte[] boschLogo) {
    this.boschLogo = boschLogo;
  }

  /**
   * @param user the user to set
   */
  public void setUser(final User user) {
    this.user = user;
  }


}
