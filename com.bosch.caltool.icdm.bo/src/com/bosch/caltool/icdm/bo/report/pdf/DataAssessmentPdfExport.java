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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.DataAssessmentUtil;
import com.bosch.caltool.icdm.bo.report.compli.PdfUtil;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;
import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReportConstants;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.util.ModelUtil;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;

/**
 * @author hnu1cob
 */
public class DataAssessmentPdfExport extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String NO_VARIANT = "<NO-VARIANT>";

  private static final String DATA_ASSESSMENT_REPORT = "Data Assessment Report";

  /**
   * Constant to hold the server path for storing exported pdf
   */
  static final String DATA_ASSESSMENT_PATH = "Data Assessment Reports";

  /**
   * column width
   */
  public static final int SMALL_COL_WIDTH = 25;

  private static final int WP_TABLE_COL_WIDTH = 40;

  private static final int QNAIRE_TABLE_COL_WIDTH = 25;

  private static final int BASELINE_TABLE_COL_WIDTH = 50;

  private static final int COMPHEX_STATISTICS_COL_WIDTH = 70;
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

  private User user;

  private final DataAssessmentReport dataAssessmentReport;


  /**
   * Path of the base folder structure where baseline files need to be exported
   */
  private final String filePath;

  /**
   * @param serviceData Service Data
   * @param dataAssessmentReport DataAssessmentReport
   * @param filePath Fodler path to save the exported PDF
   */
  public DataAssessmentPdfExport(final ServiceData serviceData, final DataAssessmentReport dataAssessmentReport,
      final String filePath) {
    super(serviceData);
    this.dataAssessmentReport = dataAssessmentReport;
    this.filePath = filePath;
  }

  /**
   * Create PIDC Information and Attributes PDF
   *
   * @throws IOException - IO Exception
   * @throws IcdmException - ICDM Exception
   */
  public void createDataAssessmntReportPdf() throws IOException, IcdmException {
    // Creating a new empty pdf document
    this.document = new PDDocument();

    // create new page
    this.currentPage = PdfUtil.createNewPage(this.document);

    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePosition();

    // Adding information to pdf document
    PDDocumentInformation info = this.document.getDocumentInformation();

    // Draw header
    PdfUtil.constructHeader(this.document, this.currentStream, this.boschLogo);

    // set created author and created date details
    info.setAuthor(this.user.getDescription());
    info.setCreationDate(Calendar.getInstance());

    // Data Assessment Overview Details
    writeText(DATA_ASSESSMENT_REPORT, PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    getLogger().debug("Writing Data Assessment Report Overview to PDF");

    createOverviewPage();

    // Create a new page
    createNewPage();

    // Data Assessment Work Package Details
    writeText("Work Packages", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    getLogger().debug("Writing Work Package details to PDF");
    createWorkpackagePage();

    // Create a new page
    createNewPage();

    writeText("Hex Compare Results Information", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 20;
    getLogger().debug("Writing Hex Compare Statistics Info to PDF");
    createCompHexStatisticsTable();

    // Create a new page
    createNewPage();

    // Data Assessment Questionnaire Details
    writeText("Questionnaires", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    getLogger().debug("Writing Questionnaire details to PDF");
    createQnaireResultsPage();

    // Create a new page
    createNewPage();

    // Data Assessment Baseline Details
    writeText("Baselines", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    getLogger().debug("Writing Baseline details to PDF");
    createBaselinesPage();

    // Add Bosch logo as header to all pages in the dcoument
    addHeaderForPages();

    File file = DataAssessmentUtil.createFileDirectory(this.filePath, DATA_ASSESSMENT_PATH);
    String fullPdfFilePath = file.getAbsolutePath() + File.separator + "DataAssessmentReport.pdf";

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

    getLogger().info("Data Assessment Report PDF created. Path : {}", fullPdfFilePath);
  }

  /**
   * @throws IOException
   */
  private void createBaselinesPage() throws IOException {

    // Calculate table width
    double baselineTableWidth = PdfUtil.PAGE_WIDTH_A6 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baselineBaseTable = new BaseTable((float) this.linePosition,
        (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE), (float) PdfUtil.BOTTOM_MARGIN, (float) baselineTableWidth,
        (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage, true, true);

    // List of column headers for the PIDC Attributes table
    List<String> baselinesTableHeaders = new ArrayList<>();
    baselinesTableHeaders.addAll(Arrays.asList(DaPdfExportColumn.HDR_DA_BASELINES_TABLE));

    // add headers to table
    addHeaderRowToBaselineTable(baselineBaseTable, baselinesTableHeaders, BASELINE_TABLE_COL_WIDTH);

    Set<DaDataAssessment> dataAssmntBaselines = this.dataAssessmentReport.getDataAssmntBaselines();
    List<DaDataAssessment> sortedDataAssmntBaseline = new ArrayList<>(dataAssmntBaselines);
    Collections.sort(sortedDataAssmntBaseline, getBaselineComparator());


    // Create columns with values in Questionnaire table
    int serialNum = 0;
    for (DaDataAssessment daBaseline : sortedDataAssmntBaseline) {
      Row<PDPage> row = baselineBaseTable.createRow(2f);
      serialNum++;
      for (int col = 0; col < (DaPdfExportColumn.HDR_DA_BASELINES_TABLE.length); col++) {
        createBaselineColumn(daBaseline, row, col, serialNum);
      }
    }

    this.linePosition = baselineBaseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baselineBaseTable.getCurrentPage())) {
      this.currentPage = baselineBaseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }


  }

  /**
   * Method to add header row to a table
   *
   * @param baseTable
   * @param columnHeaders
   * @param rowWidth
   */
  public static void addHeaderRowToBaselineTable(final BaseTable baseTable, final List<String> columnHeaders,
      final int colWidth) {
    Row<PDPage> row = baseTable.createRow(2f);
    for (String hdr : columnHeaders) {
      Cell<PDPage> cell;
      if (hdr.equalsIgnoreCase(DataAssessmentReportConstants.SERIAL_NUM) ||
          hdr.equalsIgnoreCase(DataAssessmentReportConstants.VARIANT_NAME) ||
          hdr.equalsIgnoreCase(DataAssessmentReportConstants.BASELINE_FILE_STATUS)) {
        cell = row.createCell(SMALL_COL_WIDTH, hdr);
      }
      else {
        cell = row.createCell(colWidth, hdr);
      }
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
    }
    baseTable.addHeaderRow(row);
  }

  /**
   * @param daBaseline
   * @param row
   * @param col
   * @param serialNum
   * @throws IOException
   */
  private void createBaselineColumn(final DaDataAssessment daBaseline, final Row<PDPage> row, final int col,
      final int serialNum)
      throws IOException {


    String cellValue = "";
    switch (col) {
      case 0:
        cellValue = String.valueOf(serialNum);
        // Populate values to the columns in table
        PdfUtil.addColumnValsToTable(row, false, false, null, cellValue, SMALL_COL_WIDTH, this.currentPage);
        break;
      case 1:
        cellValue = daBaseline.getBaselineName();
        break;
      case 2:
        cellValue = CommonUtils.isNotEmptyString(daBaseline.getDescription()) ? daBaseline.getDescription() : "";
        break;
      case 3:
        cellValue = daBaseline.getCreatedDate();
        break;
      case 4:
        cellValue = CDRConstants.TYPE_OF_ASSESSMENT.getType(daBaseline.getTypeOfAssignment()).getUiType();
        break;
      case 5:
        cellValue = daBaseline.getVariantName();
        // Populate values to the columns in table
        PdfUtil.addColumnValsToTable(row, false, false, null, cellValue, SMALL_COL_WIDTH, this.currentPage);
        break;
      case 6:
        cellValue = CDRConstants.FILE_ARCHIVAL_STATUS.getTypeByDbCode(daBaseline.getFileArchivalStatus()).getUiType();
        // Populate values to the columns in table
        PdfUtil.addColumnValsToTable(row, false, false, null, cellValue, SMALL_COL_WIDTH, this.currentPage);
        break;

      default:
        break;
    }
    if ((col != 0) && (col < 5)) {
      PdfUtil.addColumnValsToTable(row, false, false, null, cellValue, BASELINE_TABLE_COL_WIDTH, this.currentPage);
    }

  }

  /**
   * @throws IOException
   */
  private void createQnaireResultsPage() throws IOException {
    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH_A6 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // List of column headers for the PIDC Attributes table
    List<String> qnaireTableHeaders = new ArrayList<>();
    qnaireTableHeaders.addAll(Arrays.asList(DaPdfExportColumn.HDR_DA_QNAIRE_TABLE));

    // add headers to table
    PdfUtil.addHeaderRowToTable(baseTable, qnaireTableHeaders, QNAIRE_TABLE_COL_WIDTH);

    Set<DataAssessmentQuestionnaires> dataAssmntQnaires = this.dataAssessmentReport.getDataAssmntQnaires();

    // Comparison : Wp Name, then Responsible Name, then Questionnaire Response Name
    Comparator<DataAssessmentQuestionnaires> qnaireComparator =
        Comparator.comparing(DataAssessmentQuestionnaires::getA2lWpName)
            .thenComparing(DataAssessmentQuestionnaires::getA2lRespName)
            .thenComparing(DataAssessmentQuestionnaires::getQnaireRespVersName);
    List<DataAssessmentQuestionnaires> sortedDataAssmntQnaires = new ArrayList<>(dataAssmntQnaires);
    Collections.sort(sortedDataAssmntQnaires, qnaireComparator);


    // Create columns with values in Questionnaire table
    int serialNum = 0;
    for (DataAssessmentQuestionnaires daQnaire : sortedDataAssmntQnaires) {
      Row<PDPage> row = baseTable.createRow(2f);
      serialNum++;
      for (int col = 0; col < (DaPdfExportColumn.HDR_DA_QNAIRE_TABLE.length); col++) {
        createQnaireColumns(daQnaire, row, col, serialNum);
      }
    }

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
   * @param daQnaire
   * @param row
   * @param col
   * @param serialNum
   * @throws IOException
   */
  private void createQnaireColumns(final DataAssessmentQuestionnaires daQnaire, final Row<PDPage> row, final int col,
      final int serialNum)
      throws IOException {

    String cellValue = "";
    boolean applyColor = false;
    Color colorToApply = null;
    boolean isLinkType = false;
    switch (col) {
      case 0:
        cellValue = String.valueOf(serialNum);
        // Populate values to the columns in table
        PdfUtil.addColumnValsToTable(row, false, false, null, cellValue, PdfUtil.SNUM_COL_WIDTH, this.currentPage);
        break;
      case 1:
        cellValue = daQnaire.getA2lWpName();
        break;
      case 2:
        cellValue = daQnaire.getA2lRespName();
        break;
      case 3:
        cellValue = daQnaire.getQnaireRespName();
        break;
      case 4:
        cellValue = getBooleanText(daQnaire.isQnaireReadyForProd());
        applyColor = true;
        break;
      case 5:
        cellValue = getBooleanText(daQnaire.isQnaireBaselineExisting());
        applyColor = true;
        break;
      case 6:
        cellValue = String.valueOf(daQnaire.getQnairePositiveAnsCount());
        break;
      case 7:
        cellValue = String.valueOf(daQnaire.getQnaireNegativeAnsCount());
        break;
      case 8:
        cellValue = String.valueOf(daQnaire.getQnaireNeutralAnsCount());
        break;
      case 9:
        cellValue = daQnaire.getQnaireRespVersName();
        break;
      case 10:
        cellValue = CommonUtils.isNotEmptyString(daQnaire.getQnaireRvdUserDisplayName())
            ? daQnaire.getQnaireRvdUserDisplayName() : "";
        break;
      case 11:
        cellValue = CommonUtils.isNotEmptyString(daQnaire.getQnaireReviewedDate())
            ? CommonUtils.setDateFormat(daQnaire.getQnaireReviewedDate()) : "";
        break;
      case 12:
        cellValue = CommonUtils.isNotNull(daQnaire.getQnaireBaselineLink()) &&
            CommonUtils.isNotEmptyString(daQnaire.getQnaireBaselineLink().getUrl())
                ? daQnaire.getQnaireBaselineLink().getUrl() : "";
        isLinkType = true;
        break;
      default:
        break;
    }
    if (col != 0) {
      if (applyColor) {
        colorToApply = findColorBasedOnCellValue(cellValue);
      }
      PdfUtil.addColumnValsToTable(row, isLinkType, applyColor, colorToApply, cellValue, QNAIRE_TABLE_COL_WIDTH,
          this.currentPage);
    }


  }

  /**
   * @throws IOException
   */
  private void createWorkpackagePage() throws IOException {
    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH_A6 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // List of column headers for the PIDC Attributes table
    List<String> wpTableHeaders = new ArrayList<>();
    wpTableHeaders.addAll(Arrays.asList(DaPdfExportColumn.HDR_DA_WP_TABLE));

    // add headers to table
    PdfUtil.addHeaderRowToTable(baseTable, wpTableHeaders, WP_TABLE_COL_WIDTH);

    Set<DaWpResp> dataAssmntWps = this.dataAssessmentReport.getDataAssmntWps();
    List<DaWpResp> sortedDataAssmntWP = new ArrayList<>(dataAssmntWps);
    Collections.sort(sortedDataAssmntWP, getWpNameComparator());


    // Create columns with values in WP Resp table
    int serialNum = 0;
    for (DaWpResp wpResp : sortedDataAssmntWP) {
      Row<PDPage> row = baseTable.createRow(2f);
      serialNum++;
      for (int col = 0; col < (DaPdfExportColumn.HDR_DA_WP_TABLE.length); col++) {
        createWpRespColumns(wpResp, row, col, serialNum);
      }
    }

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
   * @param dataAssmntWps
   * @param wpResp
   * @param row
   * @param col
   * @param serialNum
   * @throws IOException
   */
  private void createWpRespColumns(final DaWpResp wpResp, final Row<PDPage> row, final int col, final int serialNum)
      throws IOException {
    String cellValue = "";
    boolean applyColor = false;
    Color colorToApply = null;
    switch (col) {
      case 0:
        cellValue = String.valueOf(serialNum);
        // Populate values to the columns in table
        PdfUtil.addColumnValsToTable(row, false, false, null, cellValue, PdfUtil.SNUM_COL_WIDTH, this.currentPage);
        break;
      case 1:
        cellValue = wpResp.getA2lWpName();
        break;
      case 2:
        cellValue = wpResp.getA2lRespName();
        break;
      case 3:
        cellValue = getBooleanText(wpResp.getWpReadyForProductionFlag());
        applyColor = true;
        break;
      case 4:
        cellValue = getBooleanText(wpResp.getWpFinishedFlag());
        applyColor = true;
        break;
      case 5:
        cellValue =
            CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.getTypeByDbCode(wpResp.getQnairesAnsweredFlag()).getUiType();
        applyColor = true;
        break;
      case 6:
        cellValue = getBooleanText(wpResp.getParameterReviewedFlag());
        applyColor = true;
        break;
      case 7:
        cellValue = getBooleanText(wpResp.getHexRvwEqualFlag());
        applyColor = true;
        break;
      default:
        break;
    }
    if (col != 0) {
      if (applyColor) {
        colorToApply = findColorBasedOnCellValue(cellValue);
      }
      PdfUtil.addColumnValsToTable(row, false, applyColor, colorToApply, cellValue, WP_TABLE_COL_WIDTH,
          this.currentPage);
    }

  }

  /**
   * @param wpFinishedFlag
   * @param applyColor
   * @param colorToApply
   */
  private Color findColorBasedOnCellValue(final String cellValue) {
    if (CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.N_A.getUiType().equals(cellValue)) {
      return Color.GREEN;
    }
    return CommonUtils.isEqual(CommonUtilConstants.DISPLAY_YES, cellValue) ? Color.GREEN : Color.RED;
  }

  /**
   * Gets the name comparator.
   *
   * @return the name comparator
   */
  public static Comparator<DaDataAssessment> getBaselineComparator() {
    return (final DaDataAssessment val1, final DaDataAssessment val2) -> ModelUtil.compare(val1.getCreatedDate(),
        val2.getCreatedDate());
  }


  /**
   * Gets the name comparator.
   *
   * @return the name comparator
   */
  public static Comparator<DaWpResp> getWpNameComparator() {
    return (final DaWpResp val1, final DaWpResp val2) -> ModelUtil.compare(val1.getA2lWpName(), val2.getA2lWpName());
  }

  /**
   * @throws IOException
   */
  private void createOverviewPage() throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);


    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // add data to table


    createRow(baseTable, "PIDC Name", this.dataAssessmentReport.getPidcName(), 30, 80, false);
    createRow(baseTable, "A2l File Name", this.dataAssessmentReport.getA2lFileName(), 30, 80, false);
    createRow(baseTable, "PIDC Variant", CommonUtils.isNotEmptyString(this.dataAssessmentReport.getPidcVariantName())
        ? this.dataAssessmentReport.getPidcVariantName() : NO_VARIANT, 30, 80, false);

    createRow(baseTable, "Considered Reviews of previous PIDC Versions",
        getBooleanText(this.dataAssessmentReport.getConsiderRvwsOfPrevPidcVers()), 30, 80, false);

    this.linePosition = baseTable.draw() - 20;

    writeText("Baseline Details", PDType1Font.HELVETICA_BOLD, 12, this.linePosition);
    this.linePosition -= 20;

    // Initialize base table
    BaseTable baselineBaseTable = new BaseTable((float) this.linePosition,
        (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE), (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth,
        (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage, true, true);

    createRow(baselineBaseTable, "Baseline Name", this.dataAssessmentReport.getBaselineName(), 80, 30, false);
    createRow(baselineBaseTable, "Remarks", this.dataAssessmentReport.getDescription(), 80, 30, false);
    createRow(baselineBaseTable, "Assessment type", this.dataAssessmentReport.getTypeOfAssignment(), 80, 30, false);
    createRow(baselineBaseTable, "Baseline Creation Date", this.dataAssessmentReport.getBaselineCreatedDate(), 80, 30,
        false);

    createRow(baselineBaseTable, "HEX file is equal to Reviewed data in RB responsibility",
        getBooleanText(this.dataAssessmentReport.isHexFileDataEqualWithDataReviews()), 80, 30, true);
    createRow(baselineBaseTable, "All parameters in RB responsibility are reviewed",
        getBooleanText(this.dataAssessmentReport.isAllParametersReviewed()), 80, 30, true);
    createRow(baselineBaseTable, "All questionnaires in RB responsibility are answered",
        getBooleanText(this.dataAssessmentReport.isAllQnairesAnswered()), 80, 30, true);
    createRow(baselineBaseTable, "Ready for series. The baseline for series release can be created",
        getBooleanText(this.dataAssessmentReport.isReadyForSeries()), 80, 30, true);

    this.linePosition = baselineBaseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baselineBaseTable.getCurrentPage())) {
      this.currentPage = baselineBaseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
  }

  private void createRow(final BaseTable baseTable, final String rowHeading, final String value,
      final int rowHeadingWidth, final int rowValueWidth, final boolean applyColor) {
    Color colorToApply = null;
    if (applyColor) {
      colorToApply = findColorBasedOnCellValue(value);
    }
    // create cell for row heading
    Row<PDPage> row = baseTable.createRow(10f);

    // create cell for row value
    PdfUtil.addColumnValsToTable(row, false, colorToApply, rowHeading, rowHeadingWidth, 8);

    // create cell for row value
    PdfUtil.addColumnValsToTable(row, applyColor, colorToApply, value, rowValueWidth, 8);
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
    this.currentPage = PdfUtil.createNewPage(this.document);

    // Close current stream
    this.currentStream.close();

    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePosition();
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
      PdfUtil.constructHeader(this.document, this.currentStream, this.boschLogo);
      this.currentStream.close();
    }
  }

  private String getBooleanText(final boolean booleanValue) {
    return booleanValue ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
  }


  private String getBooleanText(final String booleanValue) {
    return CommonUtils.isEqual(CommonUtilConstants.CODE_YES, booleanValue) ? CommonUtilConstants.DISPLAY_YES
        : CommonUtilConstants.DISPLAY_NO;
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
    List<String> compareInfo = Arrays.asList(CompHexPdfExportColumn.compareResultsInfo);
    for (String info : compareInfo) {
      Row<PDPage> row = baseTable.createRow(2f);
      for (int col = 0; col < 2; col++) {
        fillStatisticsDataIntoTable(info, row, col);
      }
    }

    // Creating the table in PDF
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
   * Fill the staistics data into table
   *
   * @param baseTable
   * @throws IOException
   */
  private void fillStatisticsDataIntoTable(final String info, final Row<PDPage> row, final int colIndx) {

    String cellValue = DataAssessmentReportConstants.EMPTY_STRING;

    if (colIndx == 0) {
      cellValue = info;
      PdfUtil.addColumnValsToTable(row, false, null, cellValue, COMPHEX_STATISTICS_COL_WIDTH);
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
          cellValue = CommonUtils.isEmptyString(this.dataAssessmentReport.getPidcVariantName()) ? NO_VARIANT
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
            cellValue = getBooleanText(this.dataAssessmentReport.getConsiderRvwsOfPrevPidcVers());
            break;
          default:
            break;
        }
      }
      PdfUtil.addColumnValsToTable(row, false, null, cellValue, WP_TABLE_COL_WIDTH);
    }
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