/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.compli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileSizeUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lCompliParameterServiceResponse;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.user.User;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;

/**
 * Class to generate PDF report Compliance parameter in an A2l file
 *
 * @author svj7cob
 */
// Task 264144
public class A2lCompliCheckPdfReport extends AbstractSimpleBusinessObject {

  /**
   * Compliance Key
   */
  private static final String COMPLIANCE_KEY = "COMPLIANCE";
  /**
   * QSSD Key
   */
  private static final String QSSD_KEY = "QSSD";
  /**
   *
   */
  private static final String INFO_MSG_NO_COMPLIANCE = "No Compliance parameter available in the A2L file";
  /**
   *
   */
  private static final String INFO_MSG_NO_QSSD = "No Q-SSD parameter available in the A2L file";
  /**
   *
   */
  private static final String DISPLAY_MSG_QSSD_PARAMETERS = "Q-SSD parameters in A2L";
  /**
   *
   */
  private static final String DISPLAY_MSG_COMPLIANCE_PARAMETERS = "Compliance parameters in A2L";
  /**
   * the file stream
   */
  private final File file;
  /**
   * the page height
   */
  private final double pageHeight = PDRectangle.A4.getHeight();
  /**
   * the a2l compli param service response
   */
  private final A2lCompliParameterServiceResponse response;
  /**
   * the margin of page
   */
  private static final double MARGIN = 30;

  /**
   * a2l file name
   */
  private final String a2lFileName;
  /**
   * a2l file size
   */
  private final Long a2lFileSize;
  /**
   * Table row limit
   */
  private static final int TABLE_ROW_LIMIT = 25;
  /**
   * a2l last modified date
   */
  private final String a2lLastModifiedDate;
  private byte[] reportLogo;
  private final String userName;

  /**
   * @param serviceData
   * @param response the a2l compli param service response
   * @param a2lFileName the given a2l file path name
   * @param a2lFileSize the size of a2l file
   * @param monitor - progress monitor
   * @param file - File to be created/overwritten
   * @param a2lLastModifiedDate modified date of a2l file
   * @param reportLogo
   * @param userName
   * @throws DataException error while retrieving data
   */
  public A2lCompliCheckPdfReport(final ServiceData serviceData, final A2lCompliParameterServiceResponse response,
      final File file) throws DataException {

    super(serviceData);

    this.response = response;
    this.a2lFileName = response.getFileName();
    this.a2lFileSize = response.getFileSize();
    this.file = file;
    this.a2lLastModifiedDate = response.getLastModifiedDate();

    // Get logo from db
    IcdmFilesLoader fileLoader = new IcdmFilesLoader(getServiceData());
    Map<String, byte[]> fileMap = fileLoader.getFiles(-6L, CDRConstants.REPORT_LOGO_NODE_TYPE);
    byte[] rptLogo = null;
    if ((fileMap != null) && (fileMap.size() > 0)) {
      rptLogo = fileMap.get(CDRConstants.PDF_REPORT_BOSCH_LOGO_IMAGE);
    }

    if (null != rptLogo) {
      this.reportLogo = rptLogo.clone();
    }

    this.userName = new UserLoader(getServiceData()).getDataObjectCurrentUser().getDescription();
  }

  /**
   * Method to create PDF
   *
   * @throws DataException
   */
  public void constructPdf() throws DataException {
    // Creating a new empty pdf document
    PDDocument document = new PDDocument();

    try {
      constructSummaryPage(document);
      constructCompliListPages(document);
      constructQSSDListPages(document);


      // Save pdf document
      document.save(this.file);
      document.close();

    }
    catch (IOException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp);
    }

  }

  /**
   * Method to create PDFA Report for Data Assessment
   *
   * @throws IcdmException IcdmException
   */
  public void constructPdfA() throws IcdmException {
    // Creating a new empty pdf document
    PDDocument document = new PDDocument();

    try {
      constructSummaryPage(document);
      constructCompliListPages(document);
      constructQSSDListPages(document);

      getLogger().debug("Updating PDF to PDF/A Standard");
      // Convert the PDF into PDF/A
      PdfUtil.setPdfAStandard(document);

      // Save pdf document
      document.save(this.file);
      document.close();

    }
    catch (IOException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp);
    }

  }


  /**
   * Method to create page 1 of PDF
   *
   * @param doc the pdf document
   * @throws IOException
   * @throws DataException
   */
  private void constructSummaryPage(final PDDocument doc) throws IOException, DataException {

    // Create a page
    PDPage page = new PDPage();

    doc.addPage(page);

    // Adding information to pdf document
    PDDocumentInformation info = doc.getDocumentInformation();
    User curUser = new UserLoader(getServiceData()).getDataObjectByUserName(getServiceData().getUsername());
    info.setAuthor(curUser.getDescription());
    info.setCreationDate(Calendar.getInstance());
    info.setTitle("iCDM-A2L COMPLIANCE parameter check report");

    // Add text to pdf document
    // Create content stream to add content
    PDPageContentStream stream = new PDPageContentStream(doc, page);
    // Draw header
    PdfUtil.constructHeader(doc, stream, this.reportLogo);

    // Summary
    stream.beginText();
    stream.newLineAtOffset((float) A2lCompliCheckPdfReport.MARGIN, (float) (this.pageHeight - 200));
    stream.setFont(PDType1Font.HELVETICA_BOLD, 20);
    stream.setLeading(14.5f);
    stream.showText("iCDM-A2L COMPLIANCE parameter check report");
    stream.newLine();
    stream.newLine();
    stream.endText();

    stream.beginText();
    stream.newLineAtOffset((float) A2lCompliCheckPdfReport.MARGIN, (float) (this.pageHeight - 230));
    stream.setFont(PDType1Font.HELVETICA, 12);
    stream.setLeading(14.5f);
    stream.showText("A2L file has been checked if it contains COMPLIANCE parameter");
    stream.newLine();
    stream.newLine();
    stream.showText("Report Generated By           : " + this.userName);
    stream.newLine();
    stream.newLine();
    stream.showText("Date/Time                            : " + new Date());
    stream.newLine();
    stream.endText();

    stream.beginText();
    stream.newLineAtOffset((float) A2lCompliCheckPdfReport.MARGIN, (float) (this.pageHeight - 350));
    stream.setFont(PDType1Font.HELVETICA_BOLD, 15);
    stream.setLeading(14.5f);
    stream.showText("A2L file details");
    stream.newLine();
    stream.endText();

    stream.beginText();
    stream.newLineAtOffset((float) A2lCompliCheckPdfReport.MARGIN, (float) (this.pageHeight - 380));
    stream.setFont(PDType1Font.HELVETICA, 12);
    stream.setLeading(14.5f);
    displayTextWithIndent(stream, "A2L file                                 : " + this.a2lFileName);
    stream.newLine();
    displayTextWithIndent(stream, "A2L file  modification date   : " + CommonUtils.checkNull(this.a2lLastModifiedDate));
    stream.newLine();
    displayTextWithIndent(stream,
        "A2L file size                         : " + FileSizeUtil.getFormattedSize(this.a2lFileSize));
    stream.newLine();
    stream.newLine();
    stream.endText();

    // Check Results
    stream.beginText();
    stream.newLineAtOffset((float) A2lCompliCheckPdfReport.MARGIN, (float) (this.pageHeight - 510));
    stream.setFont(PDType1Font.HELVETICA_BOLD, 15);
    stream.setLeading(14.5f);
    stream.showText("Check Results");
    stream.endText();


    // Input list for comparison
    stream.beginText();
    stream.newLineAtOffset((float) A2lCompliCheckPdfReport.MARGIN, (float) (this.pageHeight - 540));
    stream.setFont(PDType1Font.HELVETICA, 12);
    stream.setLeading(14.5f);
    stream.showText("Total Parameters in A2L     : " + this.response.getTotalParamSize());
    stream.newLine();
    stream.newLine();
    stream.showText("Compliance parameters     : " + this.response.getCompliParamSize());
    stream.newLine();
    stream.newLine();
    stream.showText("Q-SSD parameters     : " + this.response.getQssdParamSize());
    stream.newLine();
    stream.newLine();
    stream.endText();

    stream.close();
  }


  /**
   * Display the text with indent space
   *
   * @param stream the pd page stream
   * @param text the passing text
   * @throws IOException exception throws if No file found
   */
  private void displayTextWithIndent(final PDPageContentStream stream, final String text) throws IOException {
    List<String> temp = getText(text);
    boolean firstLine = true;
    for (String tempStr : temp) {
      showText(stream, tempStr, firstLine);
      firstLine = false;
    }
  }


  /**
   * @param text text
   * @return list of text
   */
  private List<String> getText(final String text) {
    int limit = 85;
    List<String> textList = new ArrayList<>();
    for (int i = 0; i < text.length(); i += limit) {
      if (i > 0) {
        limit = 50;
      }
      if (text.length() < (i + limit)) {
        textList.add(text.substring(i, text.length()));
      }
      else {
        textList.add(text.substring(i, i + limit));
      }
    }
    return textList;
  }


  /**
   * Method to automatically show text in next line based on length of text
   *
   * @param stream the pd page stream
   * @param text the passing text
   * @throws IOException exception throws if No file found
   */
  private void showText(final PDPageContentStream stream, final String text, final boolean isFirstLine)
      throws IOException {
    if (isFirstLine) {
      stream.showText(text);
    }
    else {
      stream.showText("                                           " + text);
    }
    stream.newLine();
  }


  /**
   * Construct pages with compli param list dynamically according to number of compli params
   *
   * @param doc
   * @throws IOException
   */
  private void constructCompliListPages(final PDDocument doc) throws IOException {

    @SuppressWarnings("rawtypes")
    List<List> mainList = new ArrayList<>();

    Map<String, List<List>> reqdSsdClassMap = new HashMap<>();
    // Fetch data for compli params

    Map<String, String> a2lCompliParamMap = this.response.getA2lCompliParamMap();
    if (a2lCompliParamMap.size() == 0) {
      createPDFPage(doc, INFO_MSG_NO_COMPLIANCE);
    }
    else {
      createPDFTables(doc, mainList, reqdSsdClassMap, a2lCompliParamMap, DISPLAY_MSG_COMPLIANCE_PARAMETERS, false);
    }
  }


  /**
   * Construct pages with compli param list dynamically according to number of compli params
   *
   * @param doc
   * @throws IOException
   */
  private void constructQSSDListPages(final PDDocument doc) throws IOException {

    @SuppressWarnings("rawtypes")
    List<List> mainList = new ArrayList<>();

    Map<String, List<List>> reqdSsdClassMap = new HashMap<>();
    // Fetch data for compli params

    Map<String, String> a2lQSSDParamMap = this.response.getA2lQSSDParamMap();
    if (a2lQSSDParamMap.size() == 0) {
      // Create a page
      createPDFPage(doc, INFO_MSG_NO_QSSD);
    }
    else {
      createPDFTables(doc, mainList, reqdSsdClassMap, a2lQSSDParamMap, DISPLAY_MSG_QSSD_PARAMETERS, true);
    }
  }

  /**
   * @param doc
   * @throws IOException
   */
  private void createPDFPage(final PDDocument doc, final String displayText) throws IOException {
    PDPage page = new PDPage();
    doc.addPage(page);
    // Add text to pdf document
    // Create content stream to add content
    PDPageContentStream stream = new PDPageContentStream(doc, page);

    PdfUtil.constructHeader(doc, stream, this.reportLogo);

    stream.beginText();
    stream.newLineAtOffset((float) A2lCompliCheckPdfReport.MARGIN, (float) (this.pageHeight - 200));
    stream.setFont(PDType1Font.HELVETICA_BOLD, 15);
    stream.setLeading(14.5f);
    stream.showText(displayText);
    stream.endText();
    stream.close();
  }

  /**
   * @param doc
   * @param mainList
   * @param reqdSsdClassMap
   * @param a2lParamMap
   * @param isQSSDTable
   * @throws IOException
   */
  private void createPDFTables(final PDDocument doc, final List<List> mainList,
      final Map<String, List<List>> reqdSsdClassMap, final Map<String, String> a2lParamMap, final String displayText,
      final boolean isQSSDTable)
      throws IOException {
    Set<String> ssdClassSet = new HashSet<>();
    int paramCount = 0;
    for (Entry<String, String> entryItr : a2lParamMap.entrySet()) {
      List<String> data = new ArrayList<>();
      Map.Entry<String, String> entry = entryItr;
      String parameterName = entry.getKey();
      String ssdClassName = entry.getValue();
      data.add(String.valueOf(++paramCount));
      data.add(parameterName);
      data.add(ssdClassName);
      mainList.add(data);
      if (isQSSDTable) {
        reqdSsdClassMap.put(QSSD_KEY, mainList);
      }
      else {
        reqdSsdClassMap.put(COMPLIANCE_KEY, mainList);
      }
      ssdClassSet.add(ssdClassName);
    }
    int mainListSize = mainList.size();

    // Create pages according to number of compli params in list, maximum of 25 rows(table row limit) in a page, rest
    // are moved to new page
    for (int i = 0; i < mainListSize; i += TABLE_ROW_LIMIT) {
      // Create a page
      PDPage page = new PDPage();
      doc.addPage(page);
      // Add text to pdf document
      // Create content stream to add content
      PDPageContentStream stream = new PDPageContentStream(doc, page);

      PdfUtil.constructHeader(doc, stream, this.reportLogo);

      stream.beginText();
      stream.newLineAtOffset((float) A2lCompliCheckPdfReport.MARGIN, (float) (this.pageHeight - 200));
      stream.setFont(PDType1Font.HELVETICA_BOLD, 15);
      stream.setLeading(14.5f);
      stream.showText(displayText);
      stream.endText();

      // Compliresults in table
      double tableWidth = page.getMediaBox().getWidth() - (4 * A2lCompliCheckPdfReport.MARGIN);

      double bottomMargin = this.pageHeight - 800;

      // Table header
      List<String> headerList = new ArrayList<>();
      headerList.add("#");
      headerList.add("Parameter Name");
      headerList.add("SSD Class");

      // creating new table for each ssd class
      Set<Entry<String, List<List>>> entrySet = reqdSsdClassMap.entrySet();
      @SuppressWarnings("rawtypes")
      List<List> masterDataList = new ArrayList<>();
      Map<String, List<List>> filledMap = new HashMap<>();
      BaseTable baseTable = new BaseTable((float) (this.pageHeight - 230), (float) A2lCompliCheckPdfReport.MARGIN,
          (float) bottomMargin, (float) tableWidth, (float) A2lCompliCheckPdfReport.MARGIN, doc, page, true, true);
      for (Entry<String, List<List>> entry : entrySet) {
        List<List> localDataList;
        DataTable dataTable = new DataTable(baseTable, page);
        String ssdClassName = entry.getKey();
        List<List> list = entry.getValue();// 20
        if ((masterDataList.size() + list.size()) < TABLE_ROW_LIMIT) {
          localDataList = list.subList(0, list.size());
          masterDataList.addAll(localDataList);
          filledMap.put(ssdClassName, localDataList);
        }
        else {
          localDataList = list.subList(0, TABLE_ROW_LIMIT - masterDataList.size());
          masterDataList.addAll(localDataList);
          filledMap.put(ssdClassName, localDataList);
        }
        @SuppressWarnings("rawtypes")
        List<List> finalList = new ArrayList<>();
        finalList.add(headerList);
        finalList.addAll(localDataList);
        dataTable.addListToTable(finalList, true);

        if ((masterDataList.size()) < TABLE_ROW_LIMIT) {
          stream.beginText();
          stream.newLine();
          stream.endText();
          stream.beginText();
          stream.newLine();
          stream.endText();
        }
        else {
          break;
        }
      }
      baseTable.draw();

      // Business logic to remove the filled parameters and storing the unfilled parameters into map for next page
      // display

      // Mutable members should not be stored directly, Otherwise concurrent modification would be thrown
      updateReqdSsdClassMap(reqdSsdClassMap, filledMap);

      stream.close();
    }
  }

  /**
   * @param reqdSsdClassMap
   * @param filledMap
   */
  private void updateReqdSsdClassMap(final Map<String, List<List>> reqdSsdClassMap,
      final Map<String, List<List>> filledMap) {
    Map<String, List<List>> wantedMap = new HashMap<>(reqdSsdClassMap);
    for (Entry<String, List<List>> filledEntry : filledMap.entrySet()) {
      String unwantedParam = filledEntry.getKey();
      List<List> unwantedList = filledEntry.getValue();
      Set<Entry<String, List<List>>> wantedSet = wantedMap.entrySet();
      for (Entry<String, List<List>> wantedEntry : wantedSet) {
        String wantedParam = wantedEntry.getKey();
        if (wantedParam.equals(unwantedParam)) {
          List<List> wantedList = wantedEntry.getValue();
          wantedList.removeAll(unwantedList);
          if (wantedList.isEmpty()) {
            reqdSsdClassMap.remove(wantedParam);
          }
          else {
            reqdSsdClassMap.put(wantedParam, wantedList);
          }
          break;
        }
      }
    }
  }

}

