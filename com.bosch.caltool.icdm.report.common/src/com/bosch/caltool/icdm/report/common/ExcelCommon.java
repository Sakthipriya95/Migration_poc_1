/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.common;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.regex.Matcher;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;


/**
 * Utility methods for Excel generation
 *
 * @author bne4cob
 */
public enum ExcelCommon {

                         /**
                          * Unique Instance for Single class implementation
                          */
                         INSTANCE;


  private static final String FILE = "file:";

  private static final short EXCEL_UTIL_VER_STYLE_ROT_DEG = 90;

  private static final short EXCEL_UTIL_FONT_SET_COLOR = 53;

  private static final short EXCEL_UTIL_FONT_HEIGHT = 10;

  private static final int VAR_ROW_COL_NO = 2;

  private static final int SUB_VAR_ROW_COL_NO = 2;

  private static final int RH_USAGE_COL_WIDTH_2000 = 2000;

  private static final int ATTR_RH_COL_WIDTH_3000 = 3000;

  public static final int DEFAULT_COL_WIDTH_7000 = 7000;


  /**
   *
   */
  private static final int CHAR_CONVERSTION = 64;
  /**
   *
   */
  private static final int MAX_LIMIT = 26;
  /**
   * Name of font
   */
  private static final String FONT_NAME = "Arial";
  /**
   * Size of font
   */
  private static final short FONT_SIZE = 10; // NOPMD by rgo7cob on 6/25/14 12:47 PM

  /**
   * Size of font 12
   */
  // ICDM-2330
  private static final short FONT_SIZE_12 = 12;

  /**
   * Review Score cell style for red color
   */
  private CellStyle reviewScoreColRedCellStyle;
  /**
   * Review Score cell style for yellow color
   */
  private CellStyle reviewScoreColYellowCellStyle;
  /**
   * Review Score cell style for blue color
   */
  private CellStyle reviewScoreColBlueCellStyle;
  /**
   * Review Score cell style for green color
   */
  private CellStyle reviewScoreColGreenCellStyle;
  /**
   * Review Score cell style for default white color
   */
  private CellStyle reviewScoreColWhiteCellStyle;

  /**
   * Used column drop down values
   */
  protected static final String[] USED_COL_VALS = new String[] {
      ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType(),
      ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType(),
      ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType() };

  /**
   * @return the unique instance of this class
   */
  public static ExcelCommon getInstance() {
    return INSTANCE;
  }


  /**
   * Create a row
   *
   * @param workSheet the worksheet
   * @param rowIndex the row index
   * @return the row object
   */
  public final Row createExcelRow(final Sheet workSheet, final int rowIndex) {
    return workSheet.createRow(rowIndex);
  }


  /**
   * Creates a new header cell object using the input parameters
   *
   * @param row excel row
   * @param cellValue value of the cell
   * @param cellColumn cell column index
   * @param cellStyle style of cell
   * @param font font of cell
   * @return the header cell object
   */
  public final Cell createHeaderCell(final Row row, final String cellValue, final int cellColumn,
      final CellStyle cellStyle, final Font font) {

    final Cell cell = row.createCell(cellColumn);
    cell.setCellValue(cellValue);
    cellStyle.setFont(font);
    cell.setCellStyle(cellStyle);
    return cell;
  }

  /**
   * Create font style
   *
   * @param workbook workbook
   * @return the font
   */
  public final Font createFont(final Workbook workbook) {
    final Font font = workbook.createFont();
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    font.setFontName(FONT_NAME);
    font.setFontHeightInPoints(FONT_SIZE);
    return font;
  }

  /**
   * @param workbook workbook
   * @return the font
   */
  // ICDM-2330
  // Create font size 12 for headings
  public Font createFontBig(final Workbook workbook) {
    final Font font = workbook.createFont();
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    font.setFontName(FONT_NAME);
    font.setFontHeightInPoints(FONT_SIZE_12);
    return font;
  }

  /**
   * @param cellComment cellComment is the Hint given
   * @param cell cell to add comment
   * @param commentHt comment box height
   * @param commentWidth comment box width
   */
  public void createCellComment(final String cellComment, final Cell cell, final int commentHt,
      final int commentWidth) {

    final Row row = cell.getRow();
    final Sheet sheet = row.getSheet();
    Drawing drawing = sheet.createDrawingPatriarch();

    CreationHelper factory = sheet.getWorkbook().getCreationHelper();

    ClientAnchor anchor = factory.createClientAnchor();

    anchor.setCol1(cell.getColumnIndex());
    anchor.setCol2(cell.getColumnIndex() + commentWidth);
    anchor.setRow1(row.getRowNum());
    anchor.setRow2(row.getRowNum() + commentHt);

    // Create the comment and set the text
    Comment comment = drawing.createCellComment(anchor);
    RichTextString str = factory.createRichTextString(cellComment);
    comment.setString(str);
    cell.setCellComment(comment);
  }

  /**
   * Create the header style
   *
   * @param workbook the workbook
   * @return the header style
   */
  public final CellStyle createHeaderCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();

    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    return cellStyle;
  }

  /**
   * Create Bold Headings
   *
   * @param workbook
   * @return
   */
  public final CellStyle createHeadingCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();

    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    Font createFontBig = createFontBig(workbook);
    cellStyle.setFont(createFontBig);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
    return cellStyle;
  }

  /**
   * Create the header style
   *
   * @param workbook the workbook
   * @param index color index in short
   * @return the header style
   */
  public final CellStyle createCellStyleWithBgColor(final Workbook workbook, final short index) {
    final CellStyle cellStyle = workbook.createCellStyle();

    cellStyle.setFillForegroundColor(index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    return cellStyle;
  }

  /**
   * Create the header style
   *
   * @param workbook the workbook
   * @return the header style
   */
  public final CellStyle createHeaderCellStyleWithoutBorder(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();

    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
    cellStyle.setBorderRight(CellStyle.BORDER_NONE);
    cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
    cellStyle.setBorderTop(CellStyle.BORDER_NONE);
    return cellStyle;
  }

  /**
   * Create the cell style
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    return cellStyle;
  }

  /**
   * Create the cell style
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createCellStyleWithoutBorder(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
    cellStyle.setBorderRight(CellStyle.BORDER_NONE);
    cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
    cellStyle.setBorderTop(CellStyle.BORDER_NONE);
    return cellStyle;
  }


  /**
   * Create the cell style
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createVerticalCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setRotation(EXCEL_UTIL_VER_STYLE_ROT_DEG);
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    cellStyle.setWrapText(true);
    Font font = createFont(workbook);
    cellStyle.setFont(font);
    return cellStyle;
  }

  /**
   * Create the Red cell style
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createRedCellStyleWithoutBorder(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
    cellStyle.setBorderRight(CellStyle.BORDER_NONE);
    cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
    cellStyle.setBorderTop(CellStyle.BORDER_NONE);
    Font font = workbook.createFont();
    font.setColor(Font.COLOR_RED);
    cellStyle.setFont(font);
    return cellStyle;
  }

  /**
   * Create the Red cell style
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createRedCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    Font font = workbook.createFont();
    font.setColor(Font.COLOR_RED);
    cellStyle.setFont(font);
    return cellStyle;
  }


  /**
   * Icdm-8333 Orange Cell Style Create the Orange cell style
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createOrangeCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    Font font = workbook.createFont();
    font.setColor(EXCEL_UTIL_FONT_SET_COLOR);
    cellStyle.setFont(font);
    return cellStyle;
  }

  /**
   * Create the Blue cell style
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createBlueCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    Font font = workbook.createFont();
    font.setColor(HSSFColor.BLUE.index);
    cellStyle.setFont(font);
    return cellStyle;
  }

  /**
   * @param workbook
   * @return
   */
  @SuppressWarnings({ "static-access", "javadoc" })
  public Font fontStyle(final Workbook workbook) {
    final Font font = workbook.createFont();
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    font.setFontName(FONT_NAME);
    font.setFontHeightInPoints(EXCEL_UTIL_FONT_HEIGHT);
    return font;
  }

  /**
   * @param workbook
   * @return
   */
  @SuppressWarnings({ "static-access", "javadoc" })
  public CellStyle hyperLinkStyle(final Workbook workbook, final boolean isRed) {
    CellStyle hLinkStyle = workbook.createCellStyle();
    Font hLinkFont = workbook.createFont();
    hLinkFont.setUnderline(Font.U_SINGLE);
    if (isRed) {
      hLinkFont.setColor(IndexedColors.RED.getIndex());
    }
    else {
      hLinkFont.setColor(IndexedColors.BLUE.getIndex());
    }
    hLinkStyle.setFont(hLinkFont);
    hLinkStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    hLinkStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    hLinkStyle.setBorderLeft(CellStyle.BORDER_THIN);
    hLinkStyle.setBorderRight(CellStyle.BORDER_THIN);
    hLinkStyle.setBorderBottom(CellStyle.BORDER_THIN);
    hLinkStyle.setBorderTop(CellStyle.BORDER_THIN);
    return hLinkStyle;

  }

  /**
   * Create the cell using the given inputs
   *
   * @param row the row
   * @param cellValue the value of cell
   * @param cellColumn column index
   * @param cellStyle cell style
   * @return the cell object
   */
  public final Cell createCell(final Row row, final String cellValue, final int cellColumn, final CellStyle cellStyle) {

    final Cell cell = row.createCell(cellColumn);
    cell.setCellValue(cellValue);
    cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
    cell.setCellStyle(cellStyle);
    return cell;
  }

  /**
   * Create the cell using the given inputs
   *
   * @param row the row
   * @param cellValue the value of cell
   * @param cellColumn column index
   * @param cellStyle cell style
   * @return the cell object
   */
  public final Cell createCellCenterAlign(final Row row, final String cellValue, final int cellColumn,
      final CellStyle cellStyle) {
    final Cell cell = row.createCell(cellColumn);
    cell.setCellValue(cellValue);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cell.setCellStyle(cellStyle);
    return cell;
  }

  /**
   * Create the cell using the given inputs
   *
   * @param workbook the workbook
   * @param row the row
   * @param cellValue the value of cell
   * @param cellColumn column index
   * @param cellStyle cell style
   * @return the cell object
   */
  public final Hyperlink createHyperLinkCell(final Workbook workbook, final Row row, final String cellValue,
      final int cellColumn, final CellStyle cellStyle, final boolean isRed) {

    final Cell cell = createCell(row, cellValue, cellColumn, cellStyle);
    Hyperlink link = null;
    // Added null and empty check to avoid error while opening xlsx fileType
    if ((cellValue != null) && !cellValue.isEmpty()) {
      CreationHelper createHelper = workbook.getCreationHelper();
      if (!cellValue.equals(ApicConstants.HIDDEN_VALUE)) {

        link = createLink(workbook, cellValue, isRed, cell, createHelper);
      }
      else {
        cell.setCellStyle(createCellStyle(workbook));
        cell.setCellValue(cellValue);
      }
    }

    return link;
  }


  /**
   * @param workbook
   * @param cellValue
   * @param isRed
   * @param cell
   * @param createHelper
   * @return
   */
  private Hyperlink createLink(final Workbook workbook, final String cellValue, final boolean isRed, final Cell cell,
      final CreationHelper createHelper) {
    Hyperlink link;
    // ICDM-980
    if (cellValue.trim().toLowerCase(Locale.getDefault())
        .startsWith(ExcelCommonConstants.VCDM_APRJ_PATH.toLowerCase(Locale.getDefault()))) {
      link = createHelper.createHyperlink(org.apache.poi.common.usermodel.Hyperlink.LINK_URL);
      link.setAddress(cellValue.trim());
    }
    // ICDM-323
    else if (cellValue.trim().startsWith(ExcelCommonConstants.SHARED_PATH) ||
        cellValue.trim().startsWith(ExcelCommonConstants.FILE_PATH)) {
      link = createHelper.createHyperlink(org.apache.poi.common.usermodel.Hyperlink.LINK_URL);
      createUriForLink(cellValue, link);
    }
    else {
      link = createHelper.createHyperlink(org.apache.poi.common.usermodel.Hyperlink.LINK_URL);
      // ICDM-2331
      String str = cellValue.trim();
      try {
        URL url = new URL(str);
        URI uri1 = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
            url.getQuery(), url.getRef());
        str = uri1.toString();
      }
      catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
        CDMLogger.getInstance().error(e.getMessage(), e);
      }
      // if its https site user has to specify https else http will be set for the site.
      if (!cellValue.trim().startsWith(ExcelCommonConstants.HTTP) &&
          (!cellValue.trim().startsWith(ExcelCommonConstants.HTTPS))) {
        link.setAddress(ExcelCommonConstants.HTTP + str);
      }
      else {
        link.setAddress(str);
      }
    }

    cell.setHyperlink(link);
    cell.setCellStyle(hyperLinkStyle(workbook, isRed));
    cell.setCellValue(cellValue);
    return link;
  }


  /**
   * @param cellValue
   * @param link
   */
  private void createUriForLink(final String cellValue, final Hyperlink link) {
    String str = cellValue.trim();
    str = str.replaceAll(Matcher.quoteReplacement("\\"), Matcher.quoteReplacement("/"));
    try {
      if (!str.startsWith(FILE)) {
        str = FILE + str.trim();
      }
      // mozilla links
      else if (str.startsWith("file://///")) {
        str = str.replaceAll(Matcher.quoteReplacement("/////"), Matcher.quoteReplacement("//"));
        str = URLDecoder.decode(str, "ISO-8859-1"); // Assuming Mozilla link uses ISO-8859-1 encoding
      }
      URL url = new URL(str);
      URI uri1 = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
          url.getQuery(), url.getRef());
      str = uri1.toString();
    }
    catch (MalformedURLException | URISyntaxException | UnsupportedEncodingException | IllegalArgumentException e) {
      CDMLogger.getInstance().error(e.getMessage(), e);
    }
    link.setAddress(str);
  }

  /**
   * Create the cell using the given inputs
   *
   * @param workbook the workbook
   * @param row the row
   * @param cellValue the value of cell
   * @param linkStr link to which the control needs to be directed
   * @param cellColumn column index
   * @param cellStyle cell style
   * @param isRed
   * @return the cell object
   */
  public final Cell createHyperLinkCell(final Workbook workbook, final Row row, final String cellValue,
      final String linkStr, final int cellColumn, final CellStyle cellStyle, final boolean isRed) {

    final Cell cell = createCell(row, cellValue, cellColumn, cellStyle);
    // Added null and empty check to avoid error while opening xlsx fileType
    if ((linkStr != null) && !linkStr.isEmpty()) {
      CreationHelper createHelper = workbook.getCreationHelper();
      if (!linkStr.equals(ApicConstants.HIDDEN_VALUE)) {

        createLinkForHyperLinkCell(workbook, cellValue, linkStr, isRed, cell, createHelper);
      }
      else {
        cell.setCellStyle(createCellStyle(workbook));
        cell.setCellValue(cellValue);
      }
    }

    return cell;
  }


  /**
   * @param workbook
   * @param cellValue
   * @param linkStr
   * @param isRed
   * @param cell
   * @param createHelper
   */
  private void createLinkForHyperLinkCell(final Workbook workbook, final String cellValue, final String linkStr,
      final boolean isRed, final Cell cell, final CreationHelper createHelper) {
    Hyperlink link;
    // ICDM-980
    if (linkStr.trim().toLowerCase(Locale.getDefault())
        .startsWith(ExcelCommonConstants.VCDM_APRJ_PATH.toLowerCase(Locale.getDefault()))) {
      link = createHelper.createHyperlink(org.apache.poi.common.usermodel.Hyperlink.LINK_URL);
      link.setAddress(linkStr.trim());
    }
    // ICDM-323
    else if (linkStr.trim().startsWith(ExcelCommonConstants.SHARED_PATH) ||
        linkStr.trim().startsWith(ExcelCommonConstants.FILE_PATH)) {
      link = createHelper.createHyperlink(org.apache.poi.common.usermodel.Hyperlink.LINK_URL);
      createUriForLink(linkStr, link);
    }
    else {
      link = createHelper.createHyperlink(org.apache.poi.common.usermodel.Hyperlink.LINK_URL);
      // ICDM-2331
      String str = linkStr.trim();
      try {
        URL url = new URL(str);
        URI uri1 = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
            url.getQuery(), url.getRef());
        str = uri1.toString();
      }
      catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
        CDMLogger.getInstance().error(e.getMessage(), e);
      }
      // if its https site user has to specify https else http will be set for the site.
      if (!linkStr.trim().startsWith(ExcelCommonConstants.HTTP) &&
          (!linkStr.trim().startsWith(ExcelCommonConstants.HTTPS))) {
        link.setAddress(ExcelCommonConstants.HTTP + str);
      }
      else {
        link.setAddress(str);
      }
    }

    cell.setHyperlink(link);
    cell.setCellStyle(hyperLinkStyle(workbook, isRed));
    cell.setCellValue(cellValue);
  }

  /**
   * @param workbook
   * @param sheetName
   * @return
   */
  @SuppressWarnings("javadoc")
  public final Sheet createSheet(final Workbook workbook, final String sheetName) {
    return workbook.createSheet(sheetName);

  }


  /**
   * @param workbook
   * @param varAndSubVarAttrSheetHeader
   * @param pidcAttrSheet
   * @param varRow
   * @param pidcVariant
   * @param varHeaderRow
   */
  @SuppressWarnings("javadoc")
  public void varHeaderRow(final Workbook workbook, final String[] varAndSubVarAttrSheetHeader,
      final Sheet pidcAttrSheet, final int varRowNo, final PidcVariant pidcVariant, final Row varHeaderRow) {
    final CellStyle cellStyle = createCellStyle(workbook);
    final Font font = createFont(workbook);
    int varRow = varRowNo;
    while (varRow <= (varAndSubVarAttrSheetHeader.length)) {
      if (varRow == 1) {
        createHeaderCell(varHeaderRow, "Variant", varRow, cellStyle, font);

      }
      else {
        if (varRow == VAR_ROW_COL_NO) {
          createHeaderCell(varHeaderRow, pidcVariant.getName(), varRow, cellStyle, font);

        }
        else if (varRow == (varAndSubVarAttrSheetHeader.length)) {
          createHeaderCell(varHeaderRow, String.valueOf(pidcVariant.getId()), varRow, cellStyle, font);
        }
        else {
          createHeaderCell(varHeaderRow, ExcelCommonConstants.EMPTY_STRING, varRow, cellStyle, font);
        }
      }
      varRow++;
    }
  }


  /**
   * @param workbook
   * @param varAndSubVarAttrSheetHeader
   * @param pidcAttrSheet
   * @param subVarRow
   * @param projSubVar
   * @param subVarHeaderRow
   */
  @SuppressWarnings("javadoc")
  public void subVarHeaderRow(final Workbook workbook, final String[] varAndSubVarAttrSheetHeader,
      final Sheet pidcAttrSheet, final int subVarRowNo, final PidcSubVariant projSubVar, final Row subVarHeaderRow) {
    final CellStyle cellStyle = createCellStyle(workbook);
    final Font font = fontStyle(workbook);
    int subVarRow = subVarRowNo;
    while (subVarRow <= (varAndSubVarAttrSheetHeader.length)) {
      if (subVarRow == 1) {
        createHeaderCell(subVarHeaderRow, "SubVariant", subVarRow, cellStyle, font);
      }
      else {
        if (subVarRow == SUB_VAR_ROW_COL_NO) {
          createHeaderCell(subVarHeaderRow, projSubVar.getName(), subVarRow, cellStyle, font);
        }
        else if (subVarRow == (varAndSubVarAttrSheetHeader.length)) {
          createCell(subVarHeaderRow, String.valueOf(projSubVar.getId()), subVarRow, cellStyle);
        }
        else {
          createHeaderCell(subVarHeaderRow, ExcelCommonConstants.EMPTY_STRING, subVarRow, cellStyle, font);
        }
      }
      subVarRow++;
    }
  }


  /**
   * Create drop down list for used column in excel
   *
   * @param tempAttrSheet
   * @param row
   * @param col
   * @return
   */
  DataValidation createDropDownForUsed(final Sheet tempAttrSheet, final Row row, final int col) {
    final String[] usedValList = USED_COL_VALS;
    final CellRangeAddressList addressList = new CellRangeAddressList(row.getRowNum(), row.getRowNum(), col, col);
    final DataValidationHelper dvHelper = tempAttrSheet.getDataValidationHelper();

    final DataValidationConstraint dvConstraint = createFormulaListConstraintForUsedColumn(usedValList, dvHelper);
    final DataValidation dataValidation = dvHelper.createValidation(dvConstraint, addressList);

    setsuppressDropDown(dataValidation);
    return dataValidation;
  }


  /**
   * set the drop down baed on validation
   *
   * @param dataValidation
   */
  public void setsuppressDropDown(final DataValidation dataValidation) {
    dataValidation.setSuppressDropDownArrow(dataValidation instanceof XSSFDataValidation);
  }


  /**
   * Create formula list constraint for used column
   *
   * @param usedValList
   * @param dvHelper
   * @return
   */
  private static DataValidationConstraint createFormulaListConstraintForUsedColumn(final String[] usedValList,
      final DataValidationHelper dvHelper) {
    return dvHelper.createExplicitListConstraint(usedValList);
  }

  /**
   * Auto filter for given attribute work sheet
   *
   * @param pidcAttrSheet
   * @param autoFilterRange
   * @param rowCount
   */
  @SuppressWarnings("javadoc")
  public void setAutoFilter(final Sheet pidcAttrSheet, final String autoFilterRange, final int rowCount) {
    StringBuilder filterRange = new StringBuilder();
    filterRange.append(autoFilterRange);
    filterRange.append(rowCount);
    pidcAttrSheet.setAutoFilter(CellRangeAddress.valueOf(filterRange.toString()));
  }

  /**
   * Set the column size to fit the content
   *
   * @param columns
   * @param sheet
   */
  @SuppressWarnings("javadoc")
  public void setColSize(final String[] columns, final Sheet sheet) {
    for (int col = 0; col < columns.length; col++) {
      if (columns[col].equals(ExcelCommonConstants.RH_USED) || columns[col].equals(ExcelCommonConstants.RH_UNIT) ||
          columns[col].equals(ExcelCommonConstants.RH_USAGE)) {
        sheet.setColumnWidth(col, RH_USAGE_COL_WIDTH_2000);
      }
      else if (columns[col].equals(ExcelCommonConstants.ATTRIBUTE_RH_VALUE_TYPE)) {
        sheet.setColumnWidth(col, ATTR_RH_COL_WIDTH_3000);
      }
      else {
        sheet.setColumnWidth(col, DEFAULT_COL_WIDTH_7000);
      }
    }
  }

  // ICDM-1703
  /**
   * Gives the cexcel column name corresponding to col number
   *
   * @param colNumber col number
   * @return excel col name
   */
  public String getExcelColName(final int colNumber) {
    // colnumber starts with 0.
    return CellReference.convertNumToColString(colNumber);

  }

  /**
   * Gets the Date Cell style
   *
   * @param workbook the given workbook
   */
  // ICDM-2230
  public CellStyle createDateCellStyle(final Workbook workbook) {
    CellStyle dateCellStyle = createCellStyleWithoutBorder(workbook);
    CreationHelper createHelper = workbook.getCreationHelper();
    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(DateFormat.DATE_FORMAT_09));
    return dateCellStyle;
  }


  /**
   * Create the Red date cell style
   *
   * @param workbook the workbook
   * @return the cell style
   */
  // ICDM-2230
  public final CellStyle createRedDateCellStyleWithoutBorder(final Workbook workbook) {
    final CellStyle cellStyle = createDateCellStyle(workbook);
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
    cellStyle.setBorderRight(CellStyle.BORDER_NONE);
    cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
    cellStyle.setBorderTop(CellStyle.BORDER_NONE);
    Font font = workbook.createFont();
    font.setColor(Font.COLOR_RED);
    cellStyle.setFont(font);
    return cellStyle;
  }

  /**
   * @param workbook
   */
  public void createReviewScoreCellStyle(final Workbook workbook) {
    this.reviewScoreColRedCellStyle = createRatingNotReviewedCellStyle(workbook);
    this.reviewScoreColYellowCellStyle = createRatingPrelimCalScoreCellStyle(workbook);
    this.reviewScoreColBlueCellStyle = createRatingCalibratedCellStyle(workbook);
    this.reviewScoreColGreenCellStyle = createRatingCmpltdRChkdCellStyle(workbook);
    this.reviewScoreColWhiteCellStyle = createDefaultReviewScoreCellStyle(workbook);
  }


  /**
   * Create the review Score col cell style for rating not reviewed
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createRatingNotReviewedCellStyle(final Workbook workbook) {
    return createReviewScoreCellStyle(workbook, IndexedColors.RED.getIndex());
  }

  /**
   * Create the review Score col cell style for preliminary calibration
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createRatingPrelimCalScoreCellStyle(final Workbook workbook) {
    return createReviewScoreCellStyle(workbook, IndexedColors.YELLOW.getIndex());
  }


  /**
   * Create the review Score col cell style for rating calibrated
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createRatingCalibratedCellStyle(final Workbook workbook) {
    return createReviewScoreCellStyle(workbook, IndexedColors.BLUE.getIndex());
  }

  /**
   * Create the review Score col cell style for rating completed or checked
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createRatingCmpltdRChkdCellStyle(final Workbook workbook) {
    return createReviewScoreCellStyle(workbook, IndexedColors.GREEN.getIndex());
  }

  /**
   * Create the review Score col cell style for default review
   *
   * @param workbook the workbook
   * @return the cell style
   */
  public final CellStyle createDefaultReviewScoreCellStyle(final Workbook workbook) {
    return createReviewScoreCellStyle(workbook, IndexedColors.WHITE.getIndex());
  }


  /**
   * @param workbook
   * @param index
   * @return
   */
  private CellStyle createReviewScoreCellStyle(final Workbook workbook, final short index) {
    final CellStyle reviewScoreColCellStyle = workbook.createCellStyle();
    reviewScoreColCellStyle.setFillForegroundColor(index);
    reviewScoreColCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    return reviewScoreColCellStyle;
  }


  /**
   * @param workbook
   * @param valueName
   * @param row
   * @param col
   * @param reviewScore reviewScore
   */
  public void createCol(final String valueName, final Row row, final int col, final DATA_REVIEW_SCORE reviewScore,
      final CellStyle styleToUse) {

    String valName = CommonUtils.isEmptyString(valueName) ? "" : valueName;
    Cell cell = createCell(row, valName, col, styleToUse);

    if (CommonUtils.isNotNull(reviewScore)) {
      final CellStyle reviewScoreColCellStyle = getReviewScoreColCellStyle(reviewScore);
      cell.setCellStyle(reviewScoreColCellStyle);
    }
  }

  /**
   * @param reviewScore reviewScore
   * @return the review Score Column CellStyle
   */
  private CellStyle getReviewScoreColCellStyle(final DATA_REVIEW_SCORE reviewScore) {
    CellStyle reviewScoreColCellStyle;

    switch (reviewScore.getRating()) {
      case DATA_REVIEW_SCORE.RATING_NOT_REVIEWED:
        reviewScoreColCellStyle = this.reviewScoreColRedCellStyle; // red
        break;

      case DATA_REVIEW_SCORE.RATING_PRELIM_CAL:
        reviewScoreColCellStyle = this.reviewScoreColYellowCellStyle;// yellow
        break;

      case DATA_REVIEW_SCORE.RATING_CALIBRATED:
        reviewScoreColCellStyle = this.reviewScoreColBlueCellStyle;// blue
        break;
      case DATA_REVIEW_SCORE.RATING_COMPLETED:
      case DATA_REVIEW_SCORE.RATING_CHECKED:
        reviewScoreColCellStyle = this.reviewScoreColGreenCellStyle;// green
        break;
      default:
        reviewScoreColCellStyle = this.reviewScoreColWhiteCellStyle;// white
    }

    return reviewScoreColCellStyle;
  }


  /**
   * Open excel file.
   *
   * @param filePath the file path
   * @param pluginID the plugin ID
   */
  public void openExcelFile(final String filePath, final String pluginID) {
    try {
      if (new File(filePath).exists()) {
        Runtime.getRuntime().exec("cmd /c " + "\"" + filePath + "\"");
      }
      else {
        CDMLogger.getInstance().error(filePath + " doesn't exist!", pluginID);
      }
    }
    catch (IOException e) {
      CDMLogger.getInstance().error(
          " Error occurred while opening the Exported file " + filePath + "\nPlease check whether the file exists !", e,
          pluginID);
    }
  }

  /**
   * @param workbook
   * @return
   */
  public CellStyle createOwnerCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setRotation(EXCEL_UTIL_VER_STYLE_ROT_DEG);
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    cellStyle.setWrapText(true);
    return cellStyle;
  }

  /**
   * @param workbook
   * @return
   */
  public CellStyle createExpiryCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    cellStyle.setWrapText(true);
    return cellStyle;
  }

  /**
   * @param workbook
   * @return
   */
  public CellStyle createGrayedCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = createDateCellStyle(workbook);
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
    cellStyle.setBorderRight(CellStyle.BORDER_NONE);
    cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
    cellStyle.setBorderTop(CellStyle.BORDER_NONE);
    Font font = workbook.createFont();
    font.setColor(IndexedColors.GREY_50_PERCENT.index);
    cellStyle.setFont(font);
    return cellStyle;
  }

  /**
   * Auto resize all columns from index 0 to columnToIndex
   *
   * @param sheet
   * @param columnToIndex
   */
  public void autoResizeColumns(final Sheet sheet, final int columnToIndex) {
    // Auto Resize the columns with data
    for (int columnIndex = 0; columnIndex <= columnToIndex; columnIndex++) {
      sheet.autoSizeColumn(columnIndex);
    }
  }

  /**
   * Auto resize all columns from index columnFromIndex to columnToIndex
   *
   * @param sheet
   * @param columnFromIndex
   * @param columnToIndex
   */
  public void autoResizeColumns(final Sheet sheet, final int columnFromIndex, final int columnToIndex) {
    // Auto Resize the columns with data
    for (int columnIndex = columnFromIndex; columnIndex <= columnToIndex; columnIndex++) {
      sheet.autoSizeColumn(columnIndex);
    }
  }

  /**
   * Set default width to all columns from index columnFromIndex to columnToIndex
   *
   * @param sheet
   * @param columnFromIndex
   * @param columnToIndex
   * @param colWidth
   */
  public void setDefaultWidthToColumns(final Sheet sheet, final int columnFromIndex, final int columnToIndex,
      final int colWidth) {
    for (int columnIndex = columnFromIndex; columnIndex <= columnToIndex; columnIndex++) {
      sheet.setDefaultColumnWidth(colWidth);
    }
  }

  /**
   * Auto resize the given input columns
   *
   * @param sheet
   * @param indices
   */
  public void autoResizeColumnIndices(final Sheet sheet, final int... indices) {
    // Auto Resize the specific columns
    for (int columnIndex : indices) {
      sheet.autoSizeColumn(columnIndex);
    }
  }


  /**
   * @param scoreUIType
   * @param row
   * @param col
   * @param score
   * @param cellStyle
   * @param reviewScoreColCellStyle
   */
  public void createCol(final String valueName, final Row row, final int col, final DATA_REVIEW_SCORE reviewScore,
      final CellStyle styleToUse, final CellStyle reviewScoreColCellStyle) {

    String valName = CommonUtils.isEmptyString(valueName) ? "" : valueName;
    Cell cell = createCell(row, valName, col, styleToUse);

    if (CommonUtils.isNotNull(reviewScore)) {
      cell.setCellStyle(reviewScoreColCellStyle);
    }

  }

  /**
   * Method to attach tooltip to a cell in Excel
   *
   * @param sheet
   * @param cell
   * @param tooltipText
   */
  public void attachTooltip(final Sheet sheet, final Cell cell, final String tooltipText) {
    Drawing drawing = sheet.createDrawingPatriarch();
    CreationHelper factory = sheet.getWorkbook().getCreationHelper();

    ClientAnchor anchor = factory.createClientAnchor();
    anchor.setCol1(cell.getColumnIndex());
    anchor.setCol2(cell.getColumnIndex() + 1);

    // number of lines in the tooltip text
    String[] lines = tooltipText.split(System.lineSeparator());
    int numberOfLines = lines.length;

    // Set the vertical position based on the number of lines in the tooltip
    anchor.setRow1(cell.getRowIndex());
    anchor.setRow2(cell.getRowIndex() + numberOfLines);
    Comment comment = drawing.createCellComment(anchor);
    RichTextString str = factory.createRichTextString(tooltipText);
    comment.setString(str);
    cell.setCellComment(comment);
  }


}
