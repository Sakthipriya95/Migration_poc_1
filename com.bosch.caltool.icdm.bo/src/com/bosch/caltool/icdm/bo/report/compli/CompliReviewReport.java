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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewOutputData;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;

/**
 * Class to generate pdf report for compliance review from webflow
 *
 * @author pdh2cob
 * @deprecated not used
 */
@Deprecated
public class CompliReviewReport extends AbstractSimpleBusinessObject {

  /**
   * Current stream
   */
  private PDPageContentStream currentStream;

  /**
   * data model class for compli review report
   */
  private final CompliReviewReportData webFlowCompliReviewData;

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
   * Compli review report pdf file name
   */
  private static final String PDF_FILE_NAME = "ComplianceReviewReport.pdf";

  /**
   * byte array to hold bosch logo icon
   */
  private final byte[] reportLogo;
  private final String outputZipfileName;

  /**
   * @param serviceData Service Data
   * @param webFlowCompliReviewData - data model object
   * @param reportLogo - byte array of report logo
   * @param outputZipfileName
   */
  public CompliReviewReport(final ServiceData serviceData, final CompliReviewReportData webFlowCompliReviewData,
      final byte[] reportLogo, final String outputZipfileName) {
    super(serviceData);
    this.webFlowCompliReviewData = webFlowCompliReviewData;
    this.reportLogo = reportLogo.clone();
    this.outputZipfileName = outputZipfileName;
  }


  /**
   * Method to create PDF
   *
   * @return File path of pdf created
   * @throws IcdmException
   */
  public String createPdf() throws IcdmException {


    String fullPdfFilePath = this.webFlowCompliReviewData.getReportFilePath() + File.separator + PDF_FILE_NAME;


    try {
      // Creating a new empty pdf document
      this.document = new PDDocument();

      // Construct overview pages
      createOverviewPages();

      // Construct CSSD pages
      createNewPage();
      createOtherPages(CDRConstants.COMPLI_RESULT_FLAG.CSSD);

      // Construct SSD2Rv pages
      createNewPage();
      createOtherPages(CDRConstants.COMPLI_RESULT_FLAG.SSD2RV);

      // Construct OK pages
      createNewPage();
      createOtherPages(CDRConstants.COMPLI_RESULT_FLAG.OK);

      // delete report file if it already exists
      File file = new File(fullPdfFilePath);
      if (file.exists()) {
        boolean isDeleted = file.delete();
        if (isDeleted) {
          CDMLogger.getInstance().debug("Existing Compli PDF file deleted to create new report : " + file.getPath());
        }
      }
      // Save pdf document
      this.document.save(fullPdfFilePath);

      // close document
      this.document.close();

      // return created file path if the report is succesfully created
      if (file.exists()) {
        return fullPdfFilePath;
      }
    }
    catch (IOException e) {
      throw new IcdmException("Error occured while creating PDF report. " + e.getMessage(), e);
    }
    return null;
  }


  /**
   * Method to create overview pages
   *
   * @param doc
   * @throws IOException
   * @throws IcdmException
   */
  private void createOverviewPages() throws IOException, IcdmException {

    CompliReviewInputMetaData metaData = this.webFlowCompliReviewData.getReviewInput();

    // create new page
    this.currentPage = PdfUtil.createNewPage(this.document);


    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePosition();

    // Adding information to pdf document
    PDDocumentInformation info = this.document.getDocumentInformation();

    try {

      info.setAuthor(this.webFlowCompliReviewData.getUser().getDescription());
      info.setCreationDate(Calendar.getInstance());
      info.setTitle("Compliance Review Report");

      // title and user description
      writeText("Compliance Review Report", PDType1Font.HELVETICA_BOLD, 20, this.linePosition);
      this.linePosition -= 30;


      writeText("HEX file(s) have been reviewed with latest compliance rules.", PDType1Font.HELVETICA, 12,
          this.linePosition);
      this.linePosition -= 20;

      // create first table with user details and date
      createReportHeaderTable(this.webFlowCompliReviewData.getUser().getDescription());

      // Review summary section
      writeText("Review Summary", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
      this.linePosition -= 20;

      String pverName;

      pverName = metaData.getPverName() + " / " + metaData.getPverVariant() + " ; " + metaData.getPverRevision();

      String pidcElement;
      if (!CommonUtils.isNullOrEmpty(this.webFlowCompliReviewData.getPidcElementNameMap())) {
        String tmpName = this.webFlowCompliReviewData.getPidcElementNameMap().get(1L);
        pidcElement = tmpName.lastIndexOf('/') > 0 ? tmpName.substring(0, tmpName.lastIndexOf('/')) : tmpName;
      }
      else {
        pidcElement = "n.a.";
      }

      writeText("HEX files contained in the review", PDType1Font.HELVETICA, 12, this.linePosition);
      this.linePosition -= 20;

      // create table with common information for all HEX
      createReportCommonTable(pidcElement, metaData.getA2lFileName(), pverName, metaData.getWebflowID());


      createOverviewPageTable();

      if ((PdfUtil.PAGE_HEIGHT - this.linePosition) < 100) {
        this.linePosition = PdfUtil.resetLinePosition();
      }

      // add header for all created pages
      addHeaderForPages();
    }
    catch (Exception e) {
      throw new IcdmException("Error occured while creating PDF report. " + e.getMessage(), e);
    }
    finally {
      // close stream
      this.currentStream.close();
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

  private void createReportCommonTable(final String pidcName, final String a2lName, final String pverName,
      final String webFlowID)
      throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);


    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // add data to table
    Row<PDPage> row;
    Cell<PDPage> cell;

    row = baseTable.createRow(10f);
    cell = row.createCell(30, "PIDC Name");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(70, pidcName);
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    row = baseTable.createRow(10f);
    cell = row.createCell(30, "PVER Name");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(70, pverName);
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);


    row = baseTable.createRow(10f);
    cell = row.createCell(30, "A2L Name");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(70, a2lName);
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    row = baseTable.createRow(10f);

    cell = row.createCell(30, "WebFlow ID:");

    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(70, webFlowID);
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    CommonParamLoader commonParamLoader = new CommonParamLoader(getServiceData());
    Long days = Long.valueOf(commonParamLoader.getValue(CommonParamKey.COMPLI_FILES_RETENTION_DAYS));

    row = baseTable.createRow(10f);
    cell = row.createCell(100, "Click here to download files  (Only for COC's. Files will be saved for " +
        days.intValue() + " days from the creation date onwards)");

    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell.setTextColor(Color.BLUE);


    this.linePosition = baseTable.draw() - 20;


    // create a link annotation
    PDAnnotationLink txtLink = new PDAnnotationLink();

    // add an underline
    PDBorderStyleDictionary underline = new PDBorderStyleDictionary();
    underline.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);

    txtLink.setBorderStyle(underline);
    PDColor col = new PDColor(new float[] { 0.0f, 0.0f, 1.0f }, PDDeviceRGB.INSTANCE);
    txtLink.setColor(col);

    PDRectangle position = new PDRectangle();
    position.setLowerLeftX(420); // line width
    position.setLowerLeftY(562); // space from doc bottom
    position.setUpperRightX(36); // space from left margin
    position.setUpperRightY(400);
    txtLink.setRectangle(position);
    String urlLink = commonParamLoader.getValue(CommonParamKey.COMPLI_FILES_APEX_LINK);
    urlLink = urlLink.replaceAll("~uuid~", this.outputZipfileName);
    PDActionURI action = new PDActionURI();
    action.setURI(urlLink);
    txtLink.setAction(action);

    this.currentPage.getAnnotations().add(txtLink);

    this.linePosition -= 20;
    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
  }

  /**
   * Method to create overview page table
   *
   * @param doc
   * @throws IOException
   */
  private void createOverviewPageTable() throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);


    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // create table headers
    createOtherPagesTableHeader(baseTable);

    // create data list
    List<List<String>> dataList = new ArrayList<>();

    for (Entry<Long, String> hexEntry : this.webFlowCompliReviewData.getReviewInput().getHexfileIdxMap().entrySet()) {
      List<String> temp = new ArrayList<>();

      Long serNumber = hexEntry.getKey();

      CompliReviewOutputData reviewOutputData = this.webFlowCompliReviewData.getReviewOutput().get(serNumber);

      // S.No
      temp.add(serNumber.toString());

      // PIDC name
      String pidcElement;
      if (!CommonUtils.isNullOrEmpty(this.webFlowCompliReviewData.getPidcElementNameMap())) {
        String tempPidc = this.webFlowCompliReviewData.getPidcElementNameMap().get(serNumber);
        pidcElement = tempPidc.substring(tempPidc.lastIndexOf('/') + 1);
      }
      else {
        pidcElement = "n.a.";
      }
      temp.add(pidcElement);

      String dstName = this.webFlowCompliReviewData.getReviewInput().getDstMap().get(serNumber);
      if (null == dstName) {
        // hex file name
        temp.add(hexEntry.getValue());
      }
      else {
        // vCDM dst source (aprj - hex)
        StringBuilder dstNameString = new StringBuilder();
        int cellSize = 40;
        List<String> tempList = PdfUtil.getText(dstName, cellSize);
        for (String tempStr : tempList) {
          if (tempStr.length() < cellSize) {
            tempStr = tempStr + PdfUtil.getWhiteSpace(cellSize - tempStr.length());
          }
          dstNameString.append(tempStr);
        }
        temp.add(dstNameString.toString() + reviewOutputData.getHexfileName());
      }

      // total no of compli
      temp.add(String.valueOf(reviewOutputData.getCompliCount()));

      // total no of compli
      temp.add(String.valueOf(reviewOutputData.getCompliPassCount()));

      // failed compli - use case type c-ssd
      temp.add(String.valueOf(reviewOutputData.getCssdFailCount()));

      // failed compli - use case type ssd2rv
      temp.add(String.valueOf(reviewOutputData.getSsd2RvFailCount()));

      // failed compli - use case type no rule
      temp.add(String.valueOf(reviewOutputData.getNoRuleCount()));

      dataList.add(temp);
    }


    // add data to table
    for (List<String> data : dataList) {
      Row<PDPage> row = baseTable.createRow(10f);
      Cell<PDPage> cell = row.createCell(5, data.get(0));
      cell.setFont(PDType1Font.COURIER);
      cell.setFontSize(8);
      cell.setAlign(HorizontalAlignment.CENTER);
      cell = row.createCell(20, data.get(1));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell = row.createCell(40, data.get(2));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell = row.createCell(10, data.get(3));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell.setAlign(HorizontalAlignment.CENTER);
      cell = row.createCell(10, data.get(4));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell.setAlign(HorizontalAlignment.CENTER);
      cell = row.createCell(10, data.get(5));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell.setAlign(HorizontalAlignment.CENTER);
      cell = row.createCell(10, data.get(6));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell.setAlign(HorizontalAlignment.CENTER);
      cell = row.createCell(10, data.get(7));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell.setAlign(HorizontalAlignment.CENTER);
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
   * Method to create table header row
   *
   * @param baseTable
   */
  private void createOtherPagesTableHeader(final BaseTable baseTable) {
    // Table header
    List<String> headerList = new ArrayList<>();
    headerList.add("#");
    headerList.add("PIDC Variant");
    headerList.add("Hex file");
    headerList.add("COMPLI");
    headerList.add("Passed");
    headerList.add("Failed  (C-SSD)");
    headerList.add("Failed  (SSD2Rv)");
    headerList.add("Failed  (NO RULE)");

    // Create Header row
    Row<PDPage> headerRow = baseTable.createRow(15f);
    Cell<PDPage> cell = headerRow.createCell(5, headerList.get(0));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(20, headerList.get(1));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(40, headerList.get(2));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(3));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(4));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(5));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(6));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(7));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    baseTable.addHeaderRow(headerRow);
  }


  /**
   * Method to construct other pages
   *
   * @param doc
   * @throws IOException
   */
  private void createOtherPages(final CDRConstants.COMPLI_RESULT_FLAG resultFlag) throws IOException {

    // total number of C-SSD in all HEX files
    int totalCSSD = 0;
    // total number of SSD2Rv in all HEX files
    int totalSSD2Rv = 0;

    try {
      // Review details section
      writeText("Review Details for " + resultFlag.getUiType(), PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
      this.linePosition -= 20;

      // Iterating through review output map
      for (Entry<Long, CompliReviewOutputData> entry : this.webFlowCompliReviewData.getReviewOutput().entrySet()) {

        int numCSSD = entry.getValue().getCssdFailCount() + entry.getValue().getNoRuleCount();
        totalCSSD += numCSSD;

        int numSSD2Rv = entry.getValue().getSsd2RvFailCount();
        totalSSD2Rv += numSSD2Rv;

        // Create table description section
        // Hex file index number
        String hexName = "Hex file " + entry.getKey() + " - " + entry.getValue().getHexfileName();
        displayTextWithIndent(hexName, 0, 12);
        this.linePosition -= 20;

        // Compli details
        createCompliResult(numCSSD, numSSD2Rv, resultFlag, entry);
      }
      // create signatures
      createCompliSignature(totalCSSD, totalSSD2Rv, resultFlag);

      // add header for all pages
      addHeaderForPages();
    }
    catch (Exception e) {
      getLogger().error(e.getMessage(), e);
    }
    finally {
      this.currentStream.close();
    }
  }

  /**
   * @param totalCSSD
   * @param totalSSD2Rv
   * @throws IOException
   * @deprecated
   */
  @Deprecated
  private void createCompliSignature(final int totalCSSD, final int totalSSD2Rv,
      final CDRConstants.COMPLI_RESULT_FLAG resultFlag)
      throws IOException {
    switch (resultFlag) {
      case CSSD:
        // add signatures only, if there are C-SSD deviations in at least one HEX
        if (totalCSSD > 0) {
          // check if signature content can be occupied in current page
          if (this.linePosition < 310) {
            // create signature in new page
            createNewPage();
          }
          createCssdSignature(this.currentStream, false, (float) (PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN)));
        }
        break;

      case SSD2RV:
        // add signatures only, if there are SSD2Rv deviations in at least one HEX
        if (totalSSD2Rv > 0) {
          // check if signature content can be occupied in current page
          if (this.linePosition < 550) {
            // create signature in new page
            createNewPage();
          }
          createSsd2RvSignature(this.currentStream, false);
        }
        break;

      default:
        break;
    }
  }


  /**
   * @param numCSSD
   * @param numSSD2Rv
   * @param entry
   * @param resultFlag
   * @throws IOException
   * @deprecated
   */
  @Deprecated
  private void createCompliResult(final int numCSSD, final int numSSD2Rv, final COMPLI_RESULT_FLAG resultFlag,
      final Entry<Long, CompliReviewOutputData> entry)
      throws IOException {
    switch (resultFlag) {
      case CSSD:
        if (numCSSD > 0) {
          // create C-SSD page
          createOtherPagesTableSection(entry, resultFlag);
        }
        else {
          // no C-SSD errors
          writeText("No C-SSD deviations in HEX file.", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
          this.linePosition -= 30;

        }

        break;

      case SSD2RV:
        if (numSSD2Rv > 0) {
          // create SSD2Rv page
          createOtherPagesTableSection(entry, resultFlag);
        }
        else {
          // no SSD2Rv errors
          writeText("No SSD2Rv deviations in HEX file.", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
          this.linePosition -= 30;
        }

        break;

      case OK:
        if (entry.getValue().getCompliPassCount() > 0) {
          // create OK page
          createOtherPagesTableSection(entry, resultFlag);
        }
        break;
      default:
        break;
    }
  }

  private void createOtherPagesTableSection(final Entry<Long, CompliReviewOutputData> entry,
      final CDRConstants.COMPLI_RESULT_FLAG resultFlag)
      throws IOException {
    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);


    List<String> headerList = new ArrayList<>();

    Row<PDPage> headerRow = null;
    Cell<PDPage> cell = null;

    // create table header
    createTableHeader(headerList, headerRow, cell, baseTable, resultFlag);

    // create data list
    List<List<String>> dataList = createTableDataList(resultFlag, entry);

    // create table body
    if (resultFlag == COMPLI_RESULT_FLAG.OK) {
      // add data list to table
      for (List<String> data : dataList) {
        Row<PDPage> row = baseTable.createRow(8f);
        // number
        cell = row.createCell(10, data.get(0));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);
        // Label name
        cell = row.createCell(50, data.get(1));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);
        // result
        cell = row.createCell(15, data.get(3));
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);
        if (!data.get(3).equals(CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType())) {
          cell.setTextColor(Color.RED);
        }
        else {
          cell.setTextColor(Color.GREEN);
        }
      }
    }
    else {
      // add data list to table
      for (List<String> data : dataList) {
        Row<PDPage> row = baseTable.createRow(40f);
        // number
        cell = row.createCell(10, data.get(0));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);
        // Label name
        cell = row.createCell(30, data.get(1));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);
        // result
        cell = row.createCell(15, data.get(2));
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);
        if (!data.get(2).equals(CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType())) {
          cell.setTextColor(Color.RED);
        }
        else {
          cell.setTextColor(Color.GREEN);
        }
        // comment
        cell = row.createCell(55, data.get(3));
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);
      }
    }

    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table, get the page of the second row
    if (!this.currentPage.equals(baseTable.getCurrentPage()))

    {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }

  }

  /**
   * @param entry
   * @param resultFlag
   * @return
   * @deprecated
   */
  @Deprecated
  private List<List<String>> createTableDataList(final COMPLI_RESULT_FLAG resultFlag,
      final Entry<Long, CompliReviewOutputData> entry) {
    List<List<String>> dataList = new ArrayList<>();

    int count = 1;
    for (Entry<String, String> str : getTreeMap(entry.getValue().getCompliResult()).entrySet()) {
      switch (resultFlag) {
        case CSSD:
          if (str.getValue().equals(CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType()) ||
              str.getValue().equals(CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getUiType())) {
            List<String> temp = new ArrayList<>();
            temp.add(entry.getKey() + "." + count);
            temp.add(str.getKey());
            temp.add("Not Ok");
            temp.add("");
            dataList.add(temp);
            count++;
          }

          break;

        case SSD2RV:
          if (str.getValue().equals(CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType())) {
            List<String> temp = new ArrayList<>();
            temp.add(entry.getKey() + "." + count);
            temp.add(str.getKey());
            temp.add("Not Ok");
            temp.add("");
            dataList.add(temp);
            count++;
          }

          break;

        case OK:
          if (str.getValue().equals(CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType())) {
            List<String> temp = new ArrayList<>();
            temp.add(entry.getKey() + "." + count);
            temp.add(str.getKey());
            temp.add("???");
            temp.add(str.getValue());
            dataList.add(temp);
            count++;
          }

          break;

        default:
          break;
      }
    }
    return dataList;
  }


  /**
   * @param resultFlag
   * @param cell
   * @param headerRow
   * @param headerList
   * @param baseTable
   */
  private void createTableHeader(final List<String> headerList, Row<PDPage> headerRow, Cell<PDPage> cell,
      final BaseTable baseTable, final COMPLI_RESULT_FLAG resultFlag) {
    if (resultFlag == COMPLI_RESULT_FLAG.OK) {
      // Table header
      headerList.add("#");
      headerList.add("Parameter Name                          ");
      headerList.add("Case");
      headerList.add("Check Result");
      // Create Header row
      headerRow = baseTable.createRow(15f);
      // number
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
    // Text below signature box
    writeText(
        "           1. Resp.Engineer                                   2. DH Expert Organisation                       3. Business Steering Team(BST)",
        PDType1Font.HELVETICA_BOLD, 10, this.linePosition - 210);

    writeText(
        "       [electronic signature]                                    [electronic signature]                                  [electronic signature]",
        PDType1Font.HELVETICA_BOLD, 10, this.linePosition - 220);

    writeText(
        "Note: This signature is required                      Note: This signature is required                      Hint: Ensure that 2 signatures left",
        PDType1Font.HELVETICA, 10, this.linePosition - 230);

    writeText(
        "before DH Expert Orga signature                   before business steering team signature               are there first",
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
   * Method to write text with indentation
   *
   * @param text
   * @param indentSize
   * @throws IOException
   */
  private void displayTextWithIndent(final String text, final int indentSize, final int fontSize) throws IOException {
    int limit = 80;
    List<String> temp = PdfUtil.getText(text, limit);
    int line = 1;
    for (String tempStr : temp) {
      if (line > 1) {
        writeText(PdfUtil.getWhiteSpace(indentSize) + tempStr, PDType1Font.HELVETICA, fontSize, this.linePosition);
        line++;
      }
      else {
        writeText(PdfUtil.getWhiteSpace(0) + tempStr, PDType1Font.HELVETICA, fontSize, this.linePosition);
        line++;
      }
      this.linePosition -= 20;
    }
  }


  /**
   * Method to return treemap for sorting purpose
   *
   * @param map
   * @return
   */
  private TreeMap<String, String> getTreeMap(final Map<String, String> map) {
    return new TreeMap<>(map);

  }
}