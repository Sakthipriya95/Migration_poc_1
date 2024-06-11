/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.compli;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.compli.CompliReviewData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;
import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.icdm.model.user.User;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;

/**
 * Class to generate PDF report for Hex comparison result
 *
 * @author pdh2cob
 * @deprecated not used
 */
@Deprecated
public class CompHexWithCDFxDataPdfReport {

  private final File file;
  /**
   * byte array to hold bosch logo icon
   */
  private final byte[] reportLogo;

  private final SortedSet<CompHexWithCDFParam> compHexParamSet;

  private PDDocument document;

  private PDPage currentPage;

  private PDPageContentStream currentStream;

  private double linePosition;

  private final ILoggerAdapter logger;

  private final CompliReviewData compliRvwData;

  private final CdrReport cdrReport;

  private final String vcdmDst;

  private final String hexFileName;

  private final CompHexStatistics compHexStat;

  private final Long selPidcVariantId;

  private final String srcHexFilePath;

  private final ServiceData serviceData;


  /**
   * Instantiates a new comp HEX with CD fx data pdf report.
   *
   * @param file - File to be created/overwritten
   * @param response the response
   * @param compliRvwData the compli rvw data
   * @param selPidcVariantId the sel pidc variant id
   * @param reportLogo the report logo
   * @param serviceData the service data
   * @param logger the logger
   */
  public CompHexWithCDFxDataPdfReport(final File file, final CompHexResponse response,
      final CompliReviewData compliRvwData, final Long selPidcVariantId, final byte[] reportLogo,
      final ServiceData serviceData, final ILoggerAdapter logger) {
    this.file = file;
    this.logger = logger;
    this.serviceData = serviceData;
    this.compliRvwData = compliRvwData;
    this.vcdmDst = response.getVcdmDst();
    this.cdrReport = response.getCdrReport();
    this.selPidcVariantId = selPidcVariantId;
    this.hexFileName = response.getHexFileName();
    this.srcHexFilePath = response.getSrcHexFilePath();
    this.compHexParamSet = response.getCompHexResult();
    this.compHexStat = response.getCompHexStatistics();
    this.reportLogo = reportLogo.clone();
  }

  /**
   * Method to create PDF
   *
   * @throws IcdmException
   */
  public void constructPdf() throws IcdmException {

    this.logger.debug("Creating PDF Document");

    // Creating a new empty pdf document
    this.document = new PDDocument();

    try {
      createOverviewPages();

      EnumMap<CompliResValues, TreeSet<String>> output = getCompliResultParam();

      TreeSet<String> cssdAndNoRule =
          output.get(CompliResValues.CSSD) != null ? output.get(CompliResValues.CSSD) : new TreeSet<>();
      cssdAndNoRule
          .addAll(output.get(CompliResValues.NO_RULE) != null ? output.get(CompliResValues.NO_RULE) : new TreeSet<>());
      // Construct CSSD pages
      if (CommonUtils.isNotEmpty(cssdAndNoRule)) {
        createNewPage();
        createOtherPages(CompliResValues.CSSD, cssdAndNoRule);
      }

      // Construct SSD2Rv pages
      if (CommonUtils.isNotEmpty(output.get(CompliResValues.SSD2RV))) {
        createNewPage();
        createOtherPages(CompliResValues.SSD2RV, output.get(CompliResValues.SSD2RV));
      }

      // Construct OK pages
      if (CommonUtils.isNotEmpty(output.get(CompliResValues.OK))) {
        createNewPage();
        createOtherPages(CompliResValues.OK, output.get(CompliResValues.OK));
      }

      // Save pdf document
      this.document.save(this.file);
      this.document.close();
      this.logger.debug("PDF report created in : " + this.file.getPath());
    }
    catch (IOException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      throw new IcdmException(e.getLocalizedMessage(), e);
    }
  }

  /**
   * @throws IOException
   */
  private void createNewPage() throws IOException {
    // create new page
    this.currentPage = PdfUtil.createNewPage(this.document);

    this.currentStream.close();

    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePosition();
  }

  /**
   * Method to construct other pages
   *
   * @param treeSet
   * @param doc
   * @throws IOException
   * @throws IcdmException
   */
  private void createOtherPages(final CompliResValues resultFlag, final TreeSet<String> paramSet)
      throws IOException, IcdmException {

    try {
      // Review details section
      writeText("Compliance Parameter Details for " + resultFlag.getUiValue(), PDType1Font.HELVETICA_BOLD, 15,
          this.linePosition);
      this.linePosition -= 20;

      // Compli details
      switch (resultFlag) {
        case CSSD:
          // create C-SSD page
          createSectionForCssd(resultFlag, paramSet);
          break;

        case SSD2RV:
          // create SSD2Rv page
          createSectionForSsd2Rv(resultFlag, paramSet);
          break;

        case OK:
          // create OK page
          createOtherPagesTableSection(paramSet, resultFlag);
          break;

        default:
          break;
      }
      // add header for all pages
      addHeaderForPages();
    }
    catch (Exception e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      throw new IcdmException(e.getLocalizedMessage(), e);
    }
    finally {
      this.currentStream.close();
    }
  }

  /**
   * @param resultFlag
   * @param paramSet
   * @throws IOException
   */
  private void createSectionForSsd2Rv(final CompliResValues resultFlag, final TreeSet<String> paramSet)
      throws IOException {
    createOtherPagesTableSection(paramSet, resultFlag);
    // check if signature content can be occupied in current page
    if (this.linePosition < 550) {
      // create signature in new page
      createNewPage();
    }
    createSsd2RvSignature(this.currentStream, false);
  }

  /**
   * @param resultFlag
   * @param paramSet
   * @throws IOException
   */
  private void createSectionForCssd(final CompliResValues resultFlag, final TreeSet<String> paramSet)
      throws IOException {
    createOtherPagesTableSection(paramSet, resultFlag);
    // check if signature content can be occupied in current page
    if (this.linePosition < 310) {
      // create signature in new page
      createNewPage();
    }
    createCssdSignature(this.currentStream, false, (float) (PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN)));
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
      PdfUtil.constructHeader(this.document, this.currentStream, this.reportLogo);
      this.currentStream.close();
    }
  }

  /**
   * @param content - String content to be written
   * @param font - font
   * @param fontSize - size
   * @param linePosition1 - position to write
   * @throws IOException - exception
   */
  public void writeText(final String content, final PDType1Font font, final int fontSize, final double linePosition1)
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

  private void createOtherPagesTableSection(final TreeSet<String> paramSet, final CompliResValues resultFlag)
      throws IOException {
    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    List<String> headerList = new ArrayList<>();

    Row<PDPage> headerRow;
    Cell<PDPage> cell;

    // Create Table headers
    if (resultFlag.equals(CompliResValues.OK)) {
      // Table header
      headerList.add("#");
      headerList.add("Parameter Name                          ");
      headerList.add("Case");
      headerList.add("Check Result");
      // Create Header row
      headerRow = baseTable.createRow(15f);
      cell = headerRow.createCell(10, headerList.get(0));

      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      // parameter name
      cell = headerRow.createCell(50, headerList.get(1));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);

      // result
      cell = headerRow.createCell(15, headerList.get(3));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      baseTable.addHeaderRow(headerRow);
    }
    else {
      // Table header
      headerList.add("#");
      headerList.add("Parameter Name                          ");
      headerList.add("Check Result");
      headerList.add(
          "Reason for Release approval / detailed description Hint: All descriptions shall be done in clear text to enable to understand for signature. A link to codex case shall be added as reference when already available.");
      // Create Header row
      headerRow = baseTable.createRow(15f);
      cell = headerRow.createCell(10, headerList.get(0));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = headerRow.createCell(30, headerList.get(1));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = headerRow.createCell(15, headerList.get(2));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = headerRow.createCell(55, headerList.get(3));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      baseTable.addHeaderRow(headerRow);
    }

    // create data list
    int count = 1;
    if (resultFlag.equals(CompliResValues.OK)) {
      // add data list to table
      for (String param : paramSet) {
        Row<PDPage> row = baseTable.createRow(8f);
        // number
        cell = row.createCell(10, String.valueOf(count));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);
        // Label name
        cell = row.createCell(50, param);
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);
        // result
        cell = row.createCell(15, CompliResValues.OK.getUiValue());
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);
        cell.setTextColor(Color.GREEN);
        count++;
      }
    }
    else {
      // add data list to table
      for (String param : paramSet) {
        Row<PDPage> row = baseTable.createRow(40f);
        // number
        cell = row.createCell(10, String.valueOf(count));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);
        // Label name
        cell = row.createCell(30, param);
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);
        // result
        cell = row.createCell(15, "Not Ok");
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);
        cell.setTextColor(Color.RED);

        // comment
        cell = row.createCell(55, "");
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);
        count++;
      }
    }

    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table, get the page of the second row
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
  }

  /**
   * Gets the compli result param.
   *
   * @param compHEXWithCDFxData the comp HEX with CD fx data
   * @return the compli result param
   */
  private EnumMap<CompliResValues, TreeSet<String>> getCompliResultParam() {
    EnumMap<CompliResValues, TreeSet<String>> output = new EnumMap<>(CompliResValues.class);

    for (CompHexWithCDFParam result : this.compHexParamSet) {
      if (result.isCompli()) {
        CompliResValues key = result.getCompliResult();
        TreeSet<String> paramSet = output.get(key) == null ? new TreeSet<>() : output.get(key);
        paramSet.add(result.getParamName());
        output.put(key, paramSet);
      }
    }
    return output;
  }

  /**
   * @param doc
   * @param stream
   * @param isNewPage
   * @param width
   * @throws IOException
   */
  private void createCssdSignature(final PDPageContentStream stream, final boolean isNewPage, final double width)
      throws IOException {

    if (isNewPage) {
      // Create a page
      PDPage page = PdfUtil.createNewPage(this.document);

      // Create content stream to add content
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, page, AppendMode.APPEND, false);
      PdfUtil.constructHeader(this.document, this.currentStream, this.reportLogo);
      this.linePosition = PdfUtil.resetLinePosition();
    }
    else {
      this.currentStream = stream;
    }

    // heading
    writeText("Digital Signatures (Measures for C-SSD)", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 10;

    // Underline
    this.currentStream.moveTo((float) PdfUtil.LEFT_MARGIN, (float) this.linePosition);
    this.currentStream.lineTo(305, (float) this.linePosition);
    this.currentStream.stroke();
    this.linePosition -= 10;

    // heading
    writeText("Required and agreed measures out of the special release:", PDType1Font.HELVETICA_BOLD, 10,
        this.linePosition);
    this.linePosition -= 10;
    writeText(
        "Hint: This field is mandatorily to be filled. If no measure is required or decided please document this too.",
        PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 10;

    // add text box area
    this.currentStream.addRect((float) PdfUtil.LEFT_MARGIN, (float) (this.linePosition - 100), (float) (width + 60),
        80);
    this.currentStream.stroke();

    this.currentStream.addRect((float) PdfUtil.LEFT_MARGIN, (float) (this.linePosition - 190), 150, 70);
    this.currentStream.stroke();

    this.currentStream.addRect((float) (PdfUtil.LEFT_MARGIN + 195), (float) (this.linePosition - 190), 150, 70);
    this.currentStream.stroke();

    this.currentStream.addRect((float) (PdfUtil.LEFT_MARGIN + 390), (float) (this.linePosition - 190), 150, 70);
    this.currentStream.stroke();

    // Text below signature box
    writeText(
        "           1. Resp.Engineer                                   2. DH Expert Organisation                         3. Business Steering Team(BST)",
        PDType1Font.HELVETICA_BOLD, 10, this.linePosition - 210);

    writeText(
        "       [electronic signature]                                    [electronic signature]                                  [electronic signature]",
        PDType1Font.HELVETICA_BOLD, 10, this.linePosition - 220);

    writeText(
        "Note: This signature is required                      Note: This signature is required                      Hint: Ensure that 2 signatures left",
        PDType1Font.HELVETICA, 10, this.linePosition - 230);

    writeText(
        "before DH Expert Orga signature                   before business steering team signature                   are there first",
        PDType1Font.HELVETICA, 10, this.linePosition - 240);

    this.currentStream.close();
  }

  private void createSsd2RvSignature(final PDPageContentStream stream, final boolean isNewPage) throws IOException {

    if (isNewPage) {
      // Create a page
      PDPage page = PdfUtil.createNewPage(this.document);

      // Create content stream to add content
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, page, AppendMode.APPEND, false);
      PdfUtil.constructHeader(this.document, this.currentStream, this.reportLogo);
      this.linePosition = PdfUtil.resetLinePosition();
    }
    else {
      this.currentStream = stream;
    }

    // heading, 1st line
    writeText("Digital Signatures (Deviations of SSD2rv)", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 10;

    // Underline
    this.currentStream.moveTo((float) PdfUtil.LEFT_MARGIN, (float) this.linePosition);
    this.currentStream.lineTo(305, (float) this.linePosition);
    this.currentStream.stroke();
    this.linePosition -= 10;

    // heading, 2nd line
    writeText(
        "Please cluster the deviations in functional groups depending on the Expert Committee approving the deviations.",
        PDType1Font.HELVETICA_BOLD, 8, this.linePosition);
    this.linePosition -= 10;

    // table
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    List<String> headerList = new ArrayList<>();

    Row<PDPage> headerRow;
    Cell<PDPage> cell;

    // Table header
    headerList.add("SSD2rv dev. No. (see Check Result Table #)");
    headerList.add("(digitally signed) Signature CAL Expert 1");
    headerList.add("(digitally signed) Signature CAL Expert 2");
    headerList.add("(digitally signed) DHs of Codex Case if required");
    headerList.add("Link (optional)");

    // Create Header row
    headerRow = baseTable.createRow(15f);
    cell = headerRow.createCell(25, headerList.get(0));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(25, headerList.get(1));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(25, headerList.get(2));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(25, headerList.get(3));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(15, headerList.get(4));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    baseTable.addHeaderRow(headerRow);

    for (int i = 0; i < 4; i++) {
      headerRow = baseTable.createRow(75f);
      cell = headerRow.createCell(25, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = headerRow.createCell(25, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = headerRow.createCell(25, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = headerRow.createCell(25, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = headerRow.createCell(15, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      baseTable.addHeaderRow(headerRow);
    }

    this.linePosition = baseTable.draw() - 20;

  }

  /**
   * Method to create overview pages
   *
   * @param doc
   * @throws IOException
   * @throws IcdmException
   */
  private void createOverviewPages() throws IOException, IcdmException {
    // create new page
    this.currentPage = PdfUtil.createNewPage(this.document);

    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePosition();

    // Adding information to pdf document
    PDDocumentInformation info = this.document.getDocumentInformation();

    // Draw header
    PdfUtil.constructHeader(this.document, this.currentStream, this.reportLogo);

    try {
      User user = new UserLoader(this.serviceData).getDataObjectByID(this.serviceData.getUserId());
      info.setAuthor(user.getDescription());
      info.setCreationDate(Calendar.getInstance());

      // title and user description
      writeText("iCDM-Hex Compare Report", PDType1Font.HELVETICA_BOLD, 20, this.linePosition);
      this.linePosition -= 30;

      writeText("HEX File has been compared with latest reviewed data and COMPLIANCE parameter have ",
          PDType1Font.HELVETICA, 12, this.linePosition);
      this.linePosition -= 20;
      writeText("been checked with current rules.", PDType1Font.HELVETICA, 12, this.linePosition);
      this.linePosition -= 20;

      // create first table with user details and date
      createReportHeaderTable(user.getDescription());

      // create table with common information for all HEX
      createInputSummaryTable();

      // create check results table
      createCheckResultSummaryTable();

      // create attr dependency table
      if ((this.compliRvwData != null) && (CommonUtils.isNotEmpty(this.compliRvwData.getAttrValueModSet()))) {
        createAttrDepnSummaryTable();
      }

      if ((PdfUtil.PAGE_HEIGHT - this.linePosition) < 100) {
        this.linePosition = PdfUtil.resetLinePosition();
      }

      // add header for all created pages
      addHeaderForPages();
    }
    catch (Exception e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      throw new IcdmException(e.getLocalizedMessage(), e);
    }
    finally {
      // close stream
      this.currentStream.close();
    }
  }

  private void createInputSummaryTable() throws IOException {

    // Review summary section
    writeText("Inputs for Comparison", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // add data to table
    Row<PDPage> row;
    Cell<PDPage> cell;

    // PIDC
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "PIDC Name");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, this.cdrReport.getPidcVersion().getName());
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Variant
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Variant");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    if (this.selPidcVariantId == null) {
      cell = row.createCell(80, " - ");
    }
    else {
      String pidcVariantName = null;
      try {
        pidcVariantName = new PidcVariantLoader(this.serviceData).getDataObjectByID(this.selPidcVariantId).getName();
      }
      catch (DataException e) {
        this.logger.error(e.getLocalizedMessage(), e);
      }
      cell = row.createCell(80, pidcVariantName);
    }

    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Pver
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "PVER Name/Variant ");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80,
        this.cdrReport.getA2lFile().getSdomPverName() + "/ " + this.cdrReport.getA2lFile().getSdomPverVariant());
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Hex
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Hex File");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, this.hexFileName);
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Hex Filepath
    row = baseTable.createRow(10f);
    if (CommonUtils.isNotEmptyString(this.vcdmDst)) {
      cell = row.createCell(30, "vCDM DST Source");
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell = row.createCell(80, this.vcdmDst);
    }
    else {
      cell = row.createCell(30, "Hex Filepath");
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell = row.createCell(80, this.srcHexFilePath);
    }
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // A2l
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "A2L File");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, this.cdrReport.getA2lFile().getFilename());
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
  }

  private void createCheckResultSummaryTable() throws IOException {

    // Check Results
    writeText("Check Results ", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // add data to table
    Row<PDPage> row;
    Cell<PDPage> cell;

    // Total param in a2l
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Total Parameters in A2L");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, Integer.toString(this.compHexStat.getStatTotalParamInA2L()));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Total compliance parameters
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Total Compliance parameters");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, Integer.toString(this.compHexStat.getStatCompliParamInA2L()));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Compliance parameters passed
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Compliance parameters passed");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, Integer.toString(this.compHexStat.getStatCompliParamPassed()));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Compliance parameters (C-SSD)
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Compliance parameters (C-SSD)");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, Integer.toString(this.compHexStat.getStatCompliCssdFailed()));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Compliance parameters (SSD2Rv)
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Compliance parameters (SSD2Rv)");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, Integer.toString(this.compHexStat.getStatCompliSSDRvFailed()));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Compliance parameters (NO RULE)
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Compliance parameters (NO RULE)");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, Integer.toString(this.compHexStat.getStatCompliNoRuleFailed()));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
  }

  private void createAttrDepnSummaryTable() throws IOException {

    // Attr Depn section
    writeText("Dependency Attributes for Compliance check", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // Create Header row
    Row<PDPage> headerRow = baseTable.createRow(15f);
    Cell<PDPage> cell = headerRow.createCell(40, "Attribute name");
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);

    cell = headerRow.createCell(70, "Attribute value");
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);

    baseTable.addHeaderRow(headerRow);

    // create data list
    List<List<String>> dataList = new ArrayList<>();

    for (AttributeValueModel model : this.compliRvwData.getAttrValueModSet()) {
      List<String> temp = new ArrayList<>();
      temp.add(model.getAttr().getName());
      temp.add(model.getValue().getTextValueEng());
      dataList.add(temp);
    }
    // add data to table
    for (List<String> data : dataList) {
      Row<PDPage> row = baseTable.createRow(10f);

      cell = row.createCell(40, data.get(0));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);

      cell = row.createCell(70, data.get(1));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
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

  private void createReportHeaderTable(final String userName) throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);


    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // add data to table
    Row<PDPage> row = baseTable.createRow(10f);
    Cell<PDPage> cell = row.createCell(30, "Report generated by");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, userName);
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Date/Time");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, new Date().toString());
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
  }
}

