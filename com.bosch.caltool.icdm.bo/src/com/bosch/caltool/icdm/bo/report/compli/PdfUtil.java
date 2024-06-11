/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.compli;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.jempbox.xmp.pdfa.XMPSchemaPDFAId;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReportConstants;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;

/**
 * @author pdh2cob
 */
public class PdfUtil {


  /**
   * height of page ~842 pixels
   */
  public static final double PAGE_HEIGHT = PDRectangle.A4.getHeight();


  /**
   * Width of page
   */
  public static final double PAGE_WIDTH = PDRectangle.A4.getWidth();

  /**
   * header size - contains bosch logo
   */
  public static final double HEADER_SIZE = 200;

  /**
   * header size - contains bosch logo
   */
  public static final double HEADER_SIZE_A1 = 100;

  /**
   * footer size
   */
  public static final double FOOTER_SIZE = 20;

  /**
   * y axis start
   */
  public static final double PAGE_Y_AXIS_START = FOOTER_SIZE;

  /**
   * y axis end
   */
  public static final double PAGE_Y_AXIS_END = PAGE_HEIGHT - (HEADER_SIZE + FOOTER_SIZE);

  /**
   * left margin
   */
  public static final double LEFT_MARGIN = 30;

  /**
   * bottom margin
   */
  public static final double BOTTOM_MARGIN = 30;

  /**
   * Width of A6 page
   */
  public static final double PAGE_WIDTH_A6 = PDRectangle.A6.getWidth();

  /**
   * Serial Number column width
   */
  public static final int SNUM_COL_WIDTH = 20;


  /**
   * height of page ~842 pixels
   */
  public static final double PAGE_HEIGHT_A1 = PDRectangle.A1.getHeight();

  /**
   * Width of page
   */
  public static final double PAGE_WIDTH_A1 = PDRectangle.A1.getWidth();

  private PdfUtil() {
    throw new IllegalStateException("Utility class");
  }


  /**
   * Method to check and then move the stream to position in pdf page
   *
   * @param stream - pddpagecontent stream
   * @param x - position
   * @param y - position
   * @throws IOException - io exception
   */
  public static void moveToLine(final PDPageContentStream stream, final double x, final double y) throws IOException {
    if (isLinePositionWithinLimits(y)) {
      stream.newLineAtOffset((float) x, (float) y);
    }
  }


  /**
   * Method to check if current line position is within pdf page dimensions
   *
   * @param y - line position
   * @return true or false
   */
  public static boolean isLinePositionWithinLimits(final double y) {
    return y > 30;
  }


  /**
   * Method to create a new page in pdf doc
   *
   * @param doc - pdf document
   * @return created stream
   * @throws IOException - exception
   */
  public static PDPage createNewPage(final PDDocument doc) {
    PDPage page = new PDPage();
    doc.addPage(page);
    return page;
  }


  /**
   * Method to return white spaces
   *
   * @param count number of spaces required
   * @return whitespace string
   */
  public static String getWhiteSpace(final int count) {
    StringBuilder spaceBuilder = new StringBuilder();
    for (int i = 1; i <= count; i++) {
      spaceBuilder.append(" ");
    }
    return spaceBuilder.toString();
  }

  /**
   * @param text - input text
   * @param limit text limit
   * @return list of substrings for given string
   */
  public static List<String> getText(final String text, final int limit) {
    List<String> textList = new ArrayList<>();
    for (int i = 0; i < text.length(); i += limit) {
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
   * Method to create a new page in pdf doc
   *
   * @param doc - pdf document
   * @return created Page
   */
  public static PDPage createNewA1Page(final PDDocument doc) {
    PDPage page = new PDPage(PDRectangle.A1);
    doc.addPage(page);
    return page;
  }

  /**
   * Method to draw header
   *
   * @param doc - pdf document
   * @param stream - content stream
   * @param image - byte array of logo image
   * @throws IOException - io exception
   */
  public static void constructA1Header(final PDDocument doc, final PDPageContentStream stream, final byte[] image)
      throws IOException {

    ByteArrayInputStream bais = new ByteArrayInputStream(image);
    BufferedImage bim = ImageIO.read(bais);
    PDImageXObject pdImage = LosslessFactory.createFromImage(doc, bim);
    stream.drawImage(pdImage, (float) (LEFT_MARGIN + 1450), (float) (PAGE_HEIGHT_A1 - 100));

  }

  /**
   * @return line position at start of page
   */
  public static double resetLinePositionForA1Size() {
    return PAGE_HEIGHT_A1 - HEADER_SIZE_A1;
  }


  /**
   * Method to draw header
   *
   * @param doc - pdf document
   * @param stream - content stream
   * @param image - byte array of logo image
   * @throws IOException - io exception
   */
  public static void constructHeader(final PDDocument doc, final PDPageContentStream stream, final byte[] image)
      throws IOException {

    ByteArrayInputStream bais = new ByteArrayInputStream(image);
    BufferedImage bim = ImageIO.read(bais);
    PDImageXObject pdImage = LosslessFactory.createFromImage(doc, bim);
    stream.drawImage(pdImage, (float) (LEFT_MARGIN + 400), (float) (PAGE_HEIGHT - 150));

  }

  /**
   * @return line position at start of page
   */
  public static double resetLinePosition() {
    return PAGE_HEIGHT - HEADER_SIZE;
  }

  /**
   * Method to create a PDF/A standard PDF file
   *
   * @param document as PDDocument
   * @throws IcdmException as Exception
   */
  public static void setPdfAStandard(final PDDocument document) throws IcdmException {
    try {
      PDDocumentCatalog cat = document.getDocumentCatalog();
      PDMetadata metadata = new PDMetadata(document);
      cat.setMetadata(metadata);
      // creating the metadata for PDF/A
      XMPMetadata xmp = new XMPMetadata();
      XMPSchemaPDFAId pdfaid = new XMPSchemaPDFAId(xmp);
      xmp.addSchema(pdfaid);
      // To Create a PDF/A which follows Conformance level 'B'
      pdfaid.setConformance("B");
      // Part of PDF/A format
      pdfaid.setPart(1);
      pdfaid.setAbout("This file is in PDF/A Standard");

      metadata.importXMPMetadata(xmp.asByteArray());
    }
    catch (Exception exp) {
      throw new IcdmException(exp.getLocalizedMessage(), exp);
    }
  }

  /**
   * Method to populate column values in a table
   *
   * @param row Row
   * @param applyColor if true, color needs to applied for cell
   * @param colorToApply Color
   * @param cellValue cell value
   * @param colWidth column width of cell
   * @return cell
   * @throws IOException IOException
   */
  public static Cell<PDPage> addColumnValsToTable(final Row<PDPage> row, final boolean applyColor,
      final Color colorToApply, final String cellValue, final int colWidth) {
    Cell<PDPage> cell = row.createCell(colWidth, removeIllegalArgs(cellValue));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(6);

    // Add the value in colored text
    if (applyColor && (null != colorToApply)) {
      cell.setTextColor(colorToApply);
    }

    return cell;
  }

  /**
   * Method to populate column values with links in a table
   *
   * @param row Row
   * @param linkFlag true, if cell value is of type link
   * @param cellValue cell value
   * @param colWidth column width of cell
   * @param currentPage PDPage
   * @return Cell
   * @throws IOException IOException
   */
  public static Cell<PDPage> addColumnValsToTable(final Row<PDPage> row, final boolean linkFlag, final String cellValue,
      final int colWidth, final PDPage currentPage)
      throws IOException {
    Cell<PDPage> cell = row.createCell(colWidth, removeIllegalArgs(cellValue));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(6);

    // Add the value as a hyperlink
    if (linkFlag) {
      cell.setTextColor(Color.BLUE);
      createHyperLink(cell.getWidth(), cell.getBottomPadding(), cell.getLeftPadding(), cell.getRightPadding(),
          cellValue, currentPage);
    }

    return cell;
  }

  /**
   * Method to remove all arguments that are not available in font's encoding
   *
   * @param cellValue
   * @return
   */
  public static String removeIllegalArgs(final String cellValue) {
    // these are the special characters which are skipped by Character.isWhitespace
    // It is '\u005Ct', U+0009 HORIZONTAL TABULATION.
    // • It is '\u005Cn', U+000A LINE FEED.
    // • It is '\u005Cu000B', U+000B VERTICAL TABULATION.
    // • It is '\u005Cf', U+000C FORM FEED.
    // • It is '\u005Cr', U+000D CARRIAGE RETURN.
    // • It is '\u005Cu001C', U+001C FILE SEPARATOR.
    // • It is '\u005Cu001D', U+001D GROUP SEPARATOR.
    // • It is '\u005Cu001E', U+001E RECORD SEPARATOR.
    // • It is '\u005Cu001F', U+001F UNIT SEPARATOR.
    List<String> illegalArgsToBeReplaced = Arrays.asList("\n", "\r", "\t", "\\u0084", "\\u00a0", "\\u005Ct", "\\u005Cn",
        "\\u005Cu000B", "\\u005Cf", "\\u005Cr", "\\u005Cu001C", "\\u005Cu001D", "\\u005Cu001E", "\\u005Cu001F");
    if (CommonUtils.isNotEmptyString(cellValue) ||
        checkIfSpecialCharactersAvailable(cellValue, illegalArgsToBeReplaced)) {
      return illegalArgsToBeReplaced.stream().reduce(cellValue, (str, val) -> str.replaceAll(val, ""));
    }
    return cellValue;
  }


  /**
   * @param cellValue
   * @param illegalArgsToBeRepSet
   * @return
   */
  private static boolean checkIfSpecialCharactersAvailable(final String cellValue,
      final List<String> illegalArgsToBeRepSet) {
    boolean isContainSpecialChar = false;
    for (String specialChar : illegalArgsToBeRepSet) {
      if ((cellValue != null) && cellValue.contains(specialChar)) {
        isContainSpecialChar = cellValue.contains(specialChar);
      }
    }
    return isContainSpecialChar;
  }


  /**
   * Create hyperlink in a particular cell of the table
   *
   * @param xStart
   * @param yStart
   * @param xEnd
   * @param yEnd
   * @param linkVal
   * @param currentPage
   * @throws IOException
   */
  public static void createHyperLink(final float xStart, final float yStart, final float xEnd, final float yEnd,
      final String linkVal, final PDPage currentPage)
      throws IOException {

    // create a link annotation
    PDAnnotationLink txtLink = new PDAnnotationLink();

    // add an underline
    PDBorderStyleDictionary underline = new PDBorderStyleDictionary();
    underline.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
    underline.setWidth(0);

    txtLink.setBorderStyle(underline);

    PDColor col = new PDColor(new float[] { 0.0f, 0.0f, 1.0f }, PDDeviceRGB.INSTANCE);
    txtLink.setColor(col);

    PDRectangle position = new PDRectangle();
    position.setLowerLeftX(xStart);
    position.setLowerLeftY(yStart);
    position.setUpperRightX(xEnd);
    position.setUpperRightY(yEnd);

    txtLink.setRectangle(position);

    PDActionURI action = new PDActionURI();
    action.setURI(linkVal);
    txtLink.setAction(action);

    currentPage.getAnnotations().add(txtLink);

  }

  /**
   * Method to add header row to a table
   *
   * @param baseTable
   * @param columnHeaders
   * @param rowWidth
   */
  public static void addHeaderRowToTable(final BaseTable baseTable, final List<String> columnHeaders,
      final int colWidth) {
    Row<PDPage> row = baseTable.createRow(2f);
    for (String hdr : columnHeaders) {
      Cell<PDPage> cell;
      if (hdr.equalsIgnoreCase(DataAssessmentReportConstants.SERIAL_NUM)) {
        cell = row.createCell(SNUM_COL_WIDTH, hdr);
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
   * Method to add header row to a table with column width as float
   *
   * @param baseTable
   * @param columnHeaders
   * @param rowWidth
   */
  public static void addHeaderRowToTable(final BaseTable baseTable, final List<String> columnHeaders,
      final float colWidth) {
    Row<PDPage> row = baseTable.createRow(2f);
    for (String hdr : columnHeaders) {
      hdr = removeIllegalArgs(hdr);
      Cell<PDPage> cell;
      if (hdr.equalsIgnoreCase(DataAssessmentReportConstants.SERIAL_NUM)) {
        cell = row.createCell(SNUM_COL_WIDTH, hdr);
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
   * Method to populate column values in a table
   *
   * @param row
   * @param linkFlag
   * @param applyColor
   * @param colorToApply
   * @param cellValue
   * @param colWidth
   * @param currentPage
   * @param fontSize font size of cell Value
   * @return
   * @throws IOException
   */
  public static Cell<PDPage> addColumnValsToTable(final Row<PDPage> row, final boolean linkFlag,
      final boolean applyColor, final Color colorToApply, final String cellValue, final int colWidth,
      final PDPage currentPage)
      throws IOException {
    Cell<PDPage> cell = row.createCell(colWidth, removeIllegalArgs(cellValue));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(6);

    // Add the value as a hyperlink
    if (linkFlag) {
      cell.setTextColor(Color.BLUE);
      createHyperLink(cell.getWidth(), cell.getBottomPadding(), cell.getLeftPadding(), cell.getRightPadding(),
          cellValue, currentPage);
    }

    // Add the value in colored text
    if (applyColor && (null != colorToApply)) {
      cell.setTextColor(colorToApply);
    }

    return cell;
  }

  /**
   * Method to populate column values in a table
   *
   * @param row Row
   * @param applyColor if true, color needs to applied for cell
   * @param colorToApply Color
   * @param cellValue cell value
   * @param colWidth column width of cell
   * @return cell
   * @param fontSize
   * @throws IOException IOException
   */
  public static Cell<PDPage> addColumnValsToTable(final Row<PDPage> row, final boolean applyColor,
      final Color colorToApply, final String cellValue, final int colWidth, final int fontSize) {
    Cell<PDPage> cell = row.createCell(colWidth, removeIllegalArgs(cellValue));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(fontSize);

    // Add the value in colored text
    if (applyColor && (null != colorToApply)) {
      cell.setTextColor(colorToApply);
    }

    return cell;
  }

}
