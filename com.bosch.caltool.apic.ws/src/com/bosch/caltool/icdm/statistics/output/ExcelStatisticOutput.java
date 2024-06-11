/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmLevelStatistics;
import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmMetaData;
import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmStatistics;
import com.bosch.caltool.icdm.statistics.adapter.AbstractIcdmStatistics.PidcLevel;
import com.bosch.caltool.icdm.statistics.text.TextHandler;


/**
 * @author imi2si
 */
public class ExcelStatisticOutput extends AbstractStatisticOutput {

  /**
   * 
   */
  private static final String ZERO_PERCENT = "0%";

  private static final String LEVEL_TAG_COMMENT = "<level>";

  private final TextHandler textHandler = new TextHandler();

  int startingPointOfFirstHeader;

  /**
   * Alignment enum
   */
  public enum Alignment {
                         /**
                          * Left alignment
                          */
                         LEFT,
                         /**
                          * Right alignment
                          */
                         RIGHT,
                         /**
                          * Top alignment
                          */
                         TOP,
                         /**
                          * Bottom alignment
                          */
                         BOTTOM
  }

  StringBuffer output = new StringBuffer();

  // POI objects
  String fileName;
  Workbook wb;
  Sheet statisticSheet;

  /**
   * @param fileName filename
   * @param icdmMetaData metadata
   * @param statistics statistics
   */
  public ExcelStatisticOutput(final String fileName, final AbstractIcdmMetaData icdmMetaData,
      final AbstractIcdmStatistics... statistics) {
    super(icdmMetaData, statistics);
    this.fileName = fileName;

    // 6 fix attributes + number of tree attributes
    this.startingPointOfFirstHeader = 6 + getNumOfTreeAtt();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createOutput() throws IOException {
    int row = 2;
    int col = 0;
    createExcelFile();
    createExcelHeader();

    for (AbstractIcdmStatistics statistic : this.statistics) {
      col = createCell(row, col, statistic.getPidcName());

      for (int loop = 0; loop < getNumOfTreeAtt(); loop++) {
        col = createCell(row, col, statistic.getPidcTreeAttribute(loop + 1));
      }

      col = createCell(row, col, statistic.getPidcOwner());
      col = createCell(row, col, statistic.getPidcNoOfAttributes());
      col = createCell(row, col, statistic.getUsedAttributes());
      col = createCell(row, col, statistic.getNotUsedAttributes());
      col = createCell(row, col, statistic.getNotDefinedAttributes());
      col = createCell(row, col, statistic.getNewAttributes());
      col = createCell(row, col, statistic.getPidcCreateDateString());
      col = createCell(row, col, statistic.getPidcModifyDateString());
      col = createCell(row, col, statistic.getLastConfirmationDateAsString());
      col = createCell(row, col, statistic.getProjectUseCases());

      col = createCell(row, col, statistic.getCoveragePidc());
      col = createCell(row, col, statistic.getCoveragePidcMax());
      col = createCell(row, col, statistic.getCoveragePidcPercentage());
      formatCell(row, col, ZERO_PERCENT);

      col = createCell(row, col, statistic.getCoverageMandAttr());
      col = createCell(row, col, statistic.getCoverageMandAttrMax());
      col = createCell(row, col, statistic.getCoverageMandPercentage());
      formatCell(row, col, ZERO_PERCENT);

      col = createCell(row, col, statistic.getUnratedFocusMatrix());
      col = createCell(row, col, statistic.getFocusMatrixApplicabeAttributes());
      col = createCell(row, col, statistic.getPidcNoOfVariants());
      col = createCell(row, col, statistic.getPidcNoOfSubVariants());
      col = createCell(row, col, statistic.getUsageOfCodexEmAttr());
      col = createCell(row, col, statistic.getUsageOfCodexDiaAttr());
      col = createCell(row, col, statistic.getSAPNumber());
      col = createCell(row, col, statistic.getSpjmCal());
      col = createCell(row, col, statistic.getCalProjectOrga());

      // Iterate through the levels
      for (PidcLevel level : PidcLevel.values()) {
        AbstractIcdmLevelStatistics levelStat = statistic.getLevelStatistics(level);

        col = createCell(row, col, levelStat.getUnknownUsedAttr());
        col = createCell(row, col, levelStat.getNotUsedAttr());
        col = createCell(row, col, levelStat.getUsedAttrWoValues() + levelStat.getUsedAttrWithValues());
        col = createCell(row, col, levelStat.getUsedAttrWithValues());

        if (!level.equals(PidcLevel.SUB_VARIANT)) {
          col = createCell(row, col, levelStat.getNoAttrLowerVariant());
        }

        col = createCell(row, col, levelStat.getNoAttrMandWithVal());
        col = createCell(row, col, levelStat.getNoAttrMandMax());

        if (levelStat.getNoAttrMandMax() != 0) {
          col = createCell(row, col, levelStat.getPercMandAttr());
          formatCell(row, col, ZERO_PERCENT);
        }
        else {
          col = createCell(row, col, "");
        }
      }

      row++;
      col = 0;
    }

    // Create Layout Borders
    createBorders();

    // Autosize is performed after cells are filled and formated. Otherwise the proper length can't be determined.
    autoSizeColumns();

    // Add Default Auto Filters
    setAutoFilter();

    this.statisticSheet.createFreezePane(0, 2);
  }

  /**
   * {@inheritDoc}
   *
   * @throws IOException exception in byte array conversion
   */
  @Override
  public byte[] getOutput() throws IOException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    this.wb.write(byteOut);
    return byteOut.toByteArray();
  }

  /**
   * creates excel file
   */
  private void createExcelFile() {
    this.wb = new XSSFWorkbook();
    this.statisticSheet = this.wb.createSheet("new sheet");
  }

  /**
   * creates excel header
   */
  private void createExcelHeader() {

    int curCol = 0;

    // First header row
    // 8 fix attributes + number of tree attributes
    int startingPointFirstHeader = 26 + getNumOfTreeAtt();

    createCell(0, startingPointFirstHeader, "PIDC Level");
    mergeCell(0, 0, startingPointFirstHeader, startingPointFirstHeader + 7);
    createCell(0, startingPointFirstHeader + 8, "Variant Level");
    mergeCell(0, 0, startingPointFirstHeader + 8, startingPointFirstHeader + 15);
    createCell(0, startingPointFirstHeader + 16, "Sub-Variant Level");
    mergeCell(0, 0, startingPointFirstHeader + 16, startingPointFirstHeader + 22);

    // Second header row
    createCell(1, 0, "PIDC Name");

    // For PIDC Header Statistics Report
    createCell(0, getNumOfTreeAtt() + 2, "Statistics Info (from PIDC Header)");
    mergeCell(0, 0, getNumOfTreeAtt() + 2, getNumOfTreeAtt() + 17);

    // Number of attributes may vary. Last added column stored in variable curCol
    for (int loop = 0; loop < getNumOfTreeAtt(); loop++) {
      curCol = createCell(1, loop + 1, "Level " + (loop + 1) + " Attr.");
    }

    // Because of dynamic number of PIDC attributes, curCol is used to set col position from now on
    curCol = createCell(1, curCol, "Owner(s) of PIDC");
    curCol = createCell(1, curCol, "Total Attr.");
    curCol = createCell(1, curCol, "Used Attr.");
    curCol = createCell(1, curCol, "Not Used Attr.");
    curCol = createCell(1, curCol, "Undefined Attr.");
    curCol = createCell(1, curCol, "New Attr.");
    curCol = createCell(1, curCol, "Create Date");
    curCol = createCell(1, curCol, "Modify Date");
    curCol = createCell(1, curCol, "Last Confirmation");

    curCol = createCell(1, curCol, "Prj. Use Cases");

    curCol = createCell(1, curCol, "Coverage PIDC (current)");
    curCol = createCell(1, curCol, "Coverage PIDC (target)");
    curCol = createCell(1, curCol, "Coverage %");

    curCol = createCell(1, curCol, "Coverage Mandatory Attr. (current)");
    curCol = createCell(1, curCol, "Coverage Mandatory Attr. (target)");
    curCol = createCell(1, curCol, "Coverage Mandatory Attr. %");

    curCol = createCell(1, curCol, "Unrated FM Attr.");
    curCol = createCell(1, curCol, "Unrated FM Attr (Max)");

    curCol = createCell(1, curCol, "Variants");
    curCol = createCell(1, curCol, "Sub-Variants");

    curCol = createCell(1, curCol, "Codex - EM relevant CAL");
    curCol = createCell(1, curCol, "Codex - DIA relevant CAL");
    curCol = createCell(1, curCol, "SAP number");
    curCol = createCell(1, curCol, "SPjM CAL");
    curCol = createCell(1, curCol, "Cal Project Orga");

    for (PidcLevel level : PidcLevel.values()) {
      curCol = createCell(1, curCol, "???");
      curCol = createCell(1, curCol, "NO");
      curCol = createCell(1, curCol, "Yes (total)");
      curCol = createCell(1, curCol, "Yes (with value)");

      switch (level) {
        case PIDC:
          curCol = createCell(1, curCol, "<Variant>");
          break;
        case VARIANT:
          curCol = createCell(1, curCol, "<Sub-Variant>");
          break;
        default:
          break;
      }

      curCol = createCell(1, curCol, "Mand. Attr.");
      addCellComment(1, curCol, replaceTags(level, this.textHandler.getText("CommentMandatoryLevel.txt")));


      if (level == PidcLevel.PIDC) {
        curCol = createCell(1, curCol,
            "Max. Mand. Attr." + " (of max " + this.icdmMetaData.getPidcNoOfMandAttributes() + ")");
      }
      else {
        curCol = createCell(1, curCol, "Max. Mand. Attr.");
      }
      addCellComment(1, curCol, replaceTags(level, this.textHandler.getText("CommentMandatoryMaxLevel.txt")));

      curCol = createCell(1, curCol, "Mand. Attr. %");
      addCellComment(1, curCol, replaceTags(level, this.textHandler.getText("CommentMandatoryPercentLevel.txt")));
    }
  }

  private void mergeCell(final int firstRow, final int lastRow, final int firstColumn, final int lastColumn) {
    this.statisticSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn));
  }

  private <V extends Object> int createCell(final int row, final int column, final V... values) {
    Row header = this.statisticSheet.getRow(row);
    Cell cell;
    int col = column;

    if (header == null) {
      header = this.statisticSheet.createRow(row);
    }

    for (V value : values) {
      cell = header.createCell(col);

      if (value instanceof Integer) {
        if ((Integer) value != 0) {
          cell.setCellValue((Integer) value);
        }
      }
      else if (value instanceof Double) {
        cell.setCellValue((Double) value);
      }
      else if (value instanceof String) {
        cell.setCellValue((String) value);
      }
      col++;
    }

    return col;
  }

  private void addCellComment(final int row, final int column, final String comment) {
    CreationHelper factory = this.wb.getCreationHelper();
    Drawing drawing = this.statisticSheet.createDrawingPatriarch();
    Row curRow = this.statisticSheet.getRow(row);
    Cell curCell = curRow.getCell(column - 1);

    Font font = this.wb.createFont();
    font.setFontHeightInPoints((short) 10);

    ClientAnchor anchor = factory.createClientAnchor();
    anchor.setCol1(curCell.getColumnIndex());
    anchor.setCol2(curCell.getColumnIndex() + 5);
    anchor.setRow1(curRow.getRowNum() + 5);
    anchor.setRow2(curRow.getRowNum() + 27);

    Comment com = drawing.createCellComment(anchor);
    RichTextString str = factory.createRichTextString(comment);
    str.applyFont(font);
    com.setString(str);
    com.setAuthor("iCDM");

    curCell.setCellComment(com);
  }

  private String replaceTags(final PidcLevel level, final String comment) {
    return comment.replace(LEVEL_TAG_COMMENT, level.toString());
  }


  private void autoSizeColumns() {
    Iterator cellIterator = this.statisticSheet.getRow(1).cellIterator();


    for (Cell cell = null;; cell = (Cell) cellIterator.next()) {
      if (cell != null) {
        this.statisticSheet.autoSizeColumn(cell.getColumnIndex());
      }
      if (!cellIterator.hasNext()) {
        break;
      }
    }
  }

  private void createBorders() {
    // Create borders, 0-based index
    addColumnBorder(30, Alignment.LEFT);
    addColumnBorder(38, Alignment.LEFT);
    addColumnBorder(46, Alignment.LEFT);
    addColumnBorder(52, Alignment.RIGHT);
    addColumnBorder(6, Alignment.LEFT);
    addColumnBorder(23, Alignment.LEFT);
    addColumnBorder(0, Alignment.LEFT);

    // Row border
    addRowBorder(1, Alignment.BOTTOM);
    addRowBorder(this.statisticSheet.getLastRowNum(), Alignment.BOTTOM);

  }

  private void addColumnBorder(final int colIndex, final ExcelStatisticOutput.Alignment align) {

    // Iterate through the Rows
    for (int curRow = 0; curRow <= this.statisticSheet.getLastRowNum(); curRow++) {

      CellStyle style = this.wb.createCellStyle();

      if (align == Alignment.LEFT) {
        style.setBorderLeft(CellStyle.BORDER_MEDIUM);
      }
      else if (align == Alignment.RIGHT) {
        // Alignment Right is only used for the last cell which already has a cell style (for the cell format)

        style = rightAlignStyle(colIndex, curRow, style);
      }

      this.statisticSheet.getRow(curRow).getCell(colIndex, Row.CREATE_NULL_AS_BLANK).setCellStyle(style);
    }
  }

  /**
   * @param colIndex
   * @param curRow
   * @param style
   * @return
   */
  private CellStyle rightAlignStyle(final int colIndex, final int curRow, final CellStyle style) {
    CellStyle cellStyle = style;
    if ((this.statisticSheet.getRow(curRow).getCell(colIndex) != null) && (ZERO_PERCENT
        .equalsIgnoreCase(this.statisticSheet.getRow(curRow).getCell(colIndex).getCellStyle().getDataFormatString()))) {
      cellStyle = this.statisticSheet.getRow(curRow).getCell(colIndex).getCellStyle();
    }

    cellStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
    return cellStyle;
  }

  private void addRowBorder(final int rowIndex, final ExcelStatisticOutput.Alignment align) {

    // Row 1 is the header column, which defines the number of cells
    int numOfCells = this.statisticSheet.getRow(1).getLastCellNum();
    Row row = this.statisticSheet.getRow(rowIndex);

    CellStyle style;

    // Iterate through the Cells of the row
    for (int curCell = 0; curCell < numOfCells; curCell++) {
      Cell cell = row.getCell(curCell, Row.CREATE_NULL_AS_BLANK);

      if (isCellStyleValid(cell) || ZERO_PERCENT.equalsIgnoreCase(cell.getCellStyle().getDataFormatString())) {
        style = cell.getCellStyle();
      }
      else {
        style = this.wb.createCellStyle();
      }

      if (align == Alignment.BOTTOM) {
        style.setBorderBottom(CellStyle.BORDER_MEDIUM);
      }
      else if (align == Alignment.TOP) {
        style.setBorderTop(CellStyle.BORDER_MEDIUM);
      }

      row.getCell(curCell, Row.CREATE_NULL_AS_BLANK).setCellStyle(style);
    }
  }

  /**
   * @param cell
   * @return
   */
  private boolean isCellStyleValid(final Cell cell) {
    return isBordersMedium(cell) || (cell.getCellStyle().getAlignment() == CellStyle.ALIGN_RIGHT);
  }

  /**
   * @param cell
   * @return
   */
  private boolean isBordersMedium(final Cell cell) {
    return (cell.getCellStyle() != null) && ((cell.getCellStyle().getBorderLeft() == CellStyle.BORDER_MEDIUM) ||
        (cell.getCellStyle().getBorderRight() == CellStyle.BORDER_MEDIUM));
  }

  private void setAutoFilter() {
    this.statisticSheet
        .setAutoFilter(new CellRangeAddress(1, this.statisticSheet.getLastRowNum(), 0, getNumberOfColumns() - 1));

    // The width of the columns must be increased by 15 pixels, because the auto size function doesn't consider the
    // filter buttons
    for (int col = 0; col < getNumberOfColumns(); col++) {
      this.statisticSheet.setColumnWidth(col, this.statisticSheet.getColumnWidth(col) + (3 * 256));
    }
  }

  private int getNumberOfColumns() {
    return this.statisticSheet.getRow(1).getLastCellNum();
  }

  private void formatCell(final int row, final int col, final String format) {
    CellStyle style = this.wb.createCellStyle();
    style.setDataFormat(this.wb.createDataFormat().getFormat(format));
    this.statisticSheet.getRow(row).getCell(col - 1, Row.CREATE_NULL_AS_BLANK).setCellStyle(style);
  }
}
