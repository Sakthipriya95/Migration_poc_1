/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

  private final TextHandler textHandler = new TextHandler();

  int startingPointFirstHeader;

  public enum Alignment {
                         LEFT,
                         RIGHT,
                         TOP,
                         BOTTOM
  }

  StringBuffer output = new StringBuffer();

  // POI objects
  String fileName;
  Workbook wrkBook;
  Sheet statisticSheet;

  public ExcelStatisticOutput(final String fileName, final AbstractIcdmMetaData icdmMetaData,
      final AbstractIcdmStatistics... statistics) {
    super(icdmMetaData, statistics);
    this.fileName = fileName;

    // 6 fix attributes + number of tree attributes
    this.startingPointFirstHeader = 6 + getNumOfTreeAtt();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createOutput() throws IOException, FileNotFoundException {
    int row = 2;
    int col = 0;
    createExcelFile();
    createExcelHeader();

    for (AbstractIcdmStatistics statistic : this.statistics) {
      col = createCell(row, col, statistic.getPidcName());

      for (int loop = 0; loop < getNumOfTreeAtt(); loop++) {
        col = createCell(row, col, statistic.getPidcTreeAttribute(loop + 1));
      }

      col = createCell(row, col, statistic.getPidcCreateDateString());
      col = createCell(row, col, statistic.getPidcModifyDateString());
      col = createCell(row, col, statistic.getPidcNoOfAttributes());
      col = createCell(row, col, statistic.getPidcNoOfMandAttr() + " (" + statistic.getPidcNoOfMandMax() + ")");
      alignCell(row, col, CellStyle.ALIGN_RIGHT);

      col = createCell(row, col, statistic.getPidcPercMandAttr());
      formatCell(row, col, "0%");

      col = createCell(row, col, statistic.getPidcNoOfVariants());
      col = createCell(row, col, statistic.getPidcNoOfSubVariants());

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

    writeExcel();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOutput() {
    // TODO Auto-generated method stub
    return this.output.toString();
  }

  private void createExcelFile() {
    this.wrkBook = new XSSFWorkbook();
    this.statisticSheet = this.wrkBook.createSheet("new sheet");
  }

  private void writeExcel() throws IOException, FileNotFoundException {
    FileOutputStream fileOut = new FileOutputStream(this.fileName);
    this.wrkBook.write(fileOut);
    fileOut.close();
  }

  private void createExcelHeader() {

    int curCol = 0;

    // First header row
    // 8 fix attributes + number of tree attributes
    int strtPntFirstHeader = 8 + getNumOfTreeAtt();

    createCell(0, strtPntFirstHeader, "PIDC Level");
    mergeCell(0, 0, strtPntFirstHeader, strtPntFirstHeader + 4);
    createCell(0, strtPntFirstHeader + 5, "Variant Level");
    mergeCell(0, 0, strtPntFirstHeader + 5, strtPntFirstHeader + 9);
    createCell(0, strtPntFirstHeader + 10, "Sub-Variant Level");
    mergeCell(0, 0, strtPntFirstHeader + 10, strtPntFirstHeader + 14);

    // Second header row
    createCell(1, 0, "PIDC Name");

    // Number of attributes may vary. Last added column stored in variable curCol
    for (int loop = 0; loop < getNumOfTreeAtt(); loop++) {
      curCol = createCell(1, loop + 1, "Level " + (loop + 1) + " Attr.");
    }

    // Because of dynamic number of PIDC attributes, curCol is used to set col position from now on
    curCol = createCell(1, curCol, "Create Date");
    curCol = createCell(1, curCol, "Modify Date");
    curCol = createCell(1, curCol, "Attributes");

    curCol = createCell(1, curCol, "Mand. Attr. (of max " + this.icdmMetaData.getPidcNoOfMandAttributes() + ")");
    addCellComment(1, curCol, this.textHandler.getText("CommentMandatory.txt"));

    curCol = createCell(1, curCol, "Mand. Attr. %");
    addCellComment(1, curCol, this.textHandler.getText("CommentMandatoryPercent.txt"));

    curCol = createCell(1, curCol, "Variants");
    curCol = createCell(1, curCol, "Sub-Variants");

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
      }
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
    CreationHelper factory = this.wrkBook.getCreationHelper();
    Drawing drawing = this.statisticSheet.createDrawingPatriarch();
    Row curRow = this.statisticSheet.getRow(row);
    Cell curCell = curRow.getCell(column - 1);

    Font font = this.wrkBook.createFont();
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
    addColumnBorder(12, Alignment.LEFT);
    addColumnBorder(17, Alignment.LEFT);
    addColumnBorder(22, Alignment.LEFT);
    addColumnBorder(25, Alignment.RIGHT);
    addColumnBorder(0, Alignment.LEFT);

    // Row border
    addRowBorder(1, Alignment.BOTTOM);
    addRowBorder(this.statisticSheet.getLastRowNum(), Alignment.BOTTOM);

  }

  private void addColumnBorder(final int colIndex, final ExcelStatisticOutput.Alignment align) {

    // Iterate through the Rows
    for (int curRow = 0; curRow <= this.statisticSheet.getLastRowNum(); curRow++) {

      CellStyle style = this.wrkBook.createCellStyle();

      if (align == Alignment.LEFT) {
        style.setBorderLeft(CellStyle.BORDER_MEDIUM);
      }
      else if (align == Alignment.RIGHT) {
        style.setBorderRight(CellStyle.BORDER_MEDIUM);
      }

      this.statisticSheet.getRow(curRow).getCell(colIndex, Row.CREATE_NULL_AS_BLANK).setCellStyle(style);
    }
  }

  private void addRowBorder(final int rowIndex, final ExcelStatisticOutput.Alignment align) {

    // Row 1 is the header column, which defines the number of cells
    int numOfCells = this.statisticSheet.getRow(1).getLastCellNum();
    Row row = this.statisticSheet.getRow(rowIndex);

    CellStyle style;

    // Iterate through the Cells of the row
    for (int curCell = 0; curCell < numOfCells; curCell++) {
      Cell cell = row.getCell(curCell, Row.CREATE_NULL_AS_BLANK);

      if (((cell.getCellStyle() != null) && ((cell.getCellStyle().getBorderLeft() == CellStyle.BORDER_MEDIUM) ||
          (cell.getCellStyle().getBorderRight() == CellStyle.BORDER_MEDIUM))) ||
          (cell.getCellStyle().getAlignment() == CellStyle.ALIGN_RIGHT) ||
          cell.getCellStyle().getDataFormatString().equalsIgnoreCase("0%")) {
        style = cell.getCellStyle();
      }
      else {
        style = this.wrkBook.createCellStyle();
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
    CellStyle style = this.wrkBook.createCellStyle();
    style.setDataFormat(this.wrkBook.createDataFormat().getFormat(format));
    this.statisticSheet.getRow(row).getCell(col - 1, Row.CREATE_NULL_AS_BLANK).setCellStyle(style);
  }

  private void alignCell(final int row, final int col, final short alignmnet) {
    CellStyle style = this.wrkBook.createCellStyle();
    style.setAlignment(alignmnet);
    this.statisticSheet.getRow(row).getCell(col - 1, Row.CREATE_NULL_AS_BLANK).setCellStyle(style);
  }
}
