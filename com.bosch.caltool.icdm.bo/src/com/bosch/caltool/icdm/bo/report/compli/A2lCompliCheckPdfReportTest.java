/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.compli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.bosch.caltool.icdm.logger.CDMLogger;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;

/**
 * Class to generate PDF report Compliance parameter in an A2l file
 *
 * @author svj7cob
 */
// Task 264144
public class A2lCompliCheckPdfReportTest {

  /**
   * the page height
   */
  private final double pageHeight = PDRectangle.A4.getHeight();
  /**
   * the margin of page
   */
  private final double margin = 30;

  private final String pathName;

  /**
   * @param response the a2l compli param service response
   * @param monitor - progress monitor
   * @param file - File to be created/overwritten
   */
  public A2lCompliCheckPdfReportTest(final String pathName) {
    super();
    this.pathName = pathName;
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

    int tableRowLimit = 25;

    @SuppressWarnings("rawtypes")
    List<List> mainList = new ArrayList<List>();

    Map<String, List<List>> allParamsWithSsdClassMap = new HashMap<String, List<List>>();
    // Fetch data for compli params

    Map<String, String> a2lCompliParamMap = new HashMap<>();
    addElements(a2lCompliParamMap);

    for (Entry<String, String> entryItr : a2lCompliParamMap.entrySet()) {
      List<String> data = new ArrayList<>();
      Map.Entry<String, String> entry = entryItr;
      String parameterName = entry.getValue();
      String ssdClassName = entry.getKey();
      data.add(parameterName);
      data.add(ssdClassName);
      mainList.add(data);
      allParamsWithSsdClassMap.put(ssdClassName, mainList);
    }
    // Create pages according to number of compli params in list, maximum of 25 rows(table row limit) in a page, rest
    // are moved to new page
    for (int i = 0; i < mainList.size(); i += tableRowLimit) {
      // Create a page
      PDPage page = new PDPage();
      doc.addPage(page);

      // Add text to pdf document
      // Create content stream to add content
      PDPageContentStream stream = new PDPageContentStream(doc, page);

      stream.beginText();
      stream.newLineAtOffset((float) this.margin, (float) (this.pageHeight - 200));
      stream.setFont(PDType1Font.HELVETICA_BOLD, 15);
      stream.setLeading(14.5f);
      stream.showText("Compliance parameters in A2L");
      stream.endText();
      stream.close();

      // Compliresults in table
      double tableWidth = page.getMediaBox().getWidth() - (4 * this.margin);

      double bottomMargin = this.pageHeight - 800;


      // old code
      BaseTable baseTable = new BaseTable((float) (this.pageHeight - 230), (float) this.margin, (float) bottomMargin,
          (float) tableWidth, (float) this.margin, doc, page, true, true);
      DataTable dataTable = new DataTable(baseTable, page);


      @SuppressWarnings("rawtypes")
      List<List> dataList = null;

      // Table header
      List<String> headerList = new ArrayList<String>();
      headerList.add("Parameter Name");
      headerList.add("Ssd Class");

      if (mainList.size() < (i + tableRowLimit)) {
        dataList = mainList.subList(i, mainList.size());
      }
      else {
        dataList = mainList.subList(i, i + tableRowLimit);
      }

      @SuppressWarnings("rawtypes")
      List<List> finalList = new ArrayList<>();
      finalList.add(headerList);
      finalList.addAll(dataList);
      dataTable.addListToTable(finalList, true);

      baseTable.draw();
      // old code
    }
  }

  /**
   * @param a2lCompliParamMap
   */
  private void addElements(final Map<String, String> a2lCompliParamMap) {
    for (int i = 0; i < 20; i++) {
      a2lCompliParamMap.put("param" + i, "COMPLI1");
    }
  }

  public void constructPdf() {

    // Creating a new empty pdf document
    PDDocument document = new PDDocument();

    try {

      constructCompliListPages(document);

      // Save pdf document
      document.save(new File(this.pathName));
      document.close();
    }
    catch (IOException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp);
    }
  }

  public static void main(final String[] args) {
    A2lCompliCheckPdfReportTest instanc =
        new A2lCompliCheckPdfReportTest("C:\\localData\\hef2fe\\Tools\\iCDM\\Srinivasan\\pdf-files\\Test01.pdf");
    instanc.constructPdf();
  }

}

