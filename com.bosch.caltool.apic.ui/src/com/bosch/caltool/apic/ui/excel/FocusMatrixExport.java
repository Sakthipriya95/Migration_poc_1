/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.report.excel.ExcelClientConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mkl2cob
 */
public class FocusMatrixExport {

  /**
   * FOCUS MATRIX string constant
   */
  private static final String SHEET_NAME = "Focus Matrix";

  /**
   * Header columns
   */

  private static final String[] useCaseSheetHeader = new String[] {
      ExcelClientConstants.RH_ATTRIBUTES,
      ExcelClientConstants.RH_DESCRIPTION,
      ExcelClientConstants.RH_VALUE,
      ExcelClientConstants.REMARKS };

  private static final int USE_CASE_HEADER_ROW_HEIGHT = 10;

  private static final int USE_CASE_EXPORT_WORK_TIME_30 = 30;
  /**
   * Cellstyle for header row
   */
  private static CellStyle headerCellStyle;
  /**
   * Normal cell style
   */
  private static CellStyle cellStyle;

  /**
   * usecase header cell style
   */
  private static CellStyle useCaseHeaderCellStyle;

  /**
   * usecase header cell style
   */
  private static CellStyle nonFMrelevantAttrCellStyle;
  /**
   * /** Font instance
   */
  private Font font;

  private FocusMatrixDataHandler fmDataHandler;

  /**
   * @param fmDataHandler
   * @param monitor
   */
  public void exportFM(final String filePath, final String fileExtn, final FocusMatrixDataHandler fmDataHandler,
      final IProgressMonitor monitor) {

    this.fmDataHandler = fmDataHandler;
    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    createSheet(workbook);

    headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyleWithoutBorder(workbook);
    cellStyle = ExcelCommon.getInstance().createCellStyleWithoutBorder(workbook);

    useCaseHeaderCellStyle = ExcelCommon.getInstance().createVerticalCellStyle(workbook);
    nonFMrelevantAttrCellStyle = ExcelCommon.getInstance().createGrayedCellStyle(workbook);


    this.font = ExcelCommon.getInstance().createFont(workbook);

    try {

      final String fileFullPath;

      if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
        fileFullPath = filePath;
      }
      else {
        fileFullPath = filePath + "." + fileExtn;
      }

      final FileOutputStream fileOut = new FileOutputStream(fileFullPath);

      monitor.worked(USE_CASE_EXPORT_WORK_TIME_30);
      monitor.subTask("Exporting Focus matrix entries . . .");
      createFMWorkSheet(workbook);

      workbook.setSelectedTab(0);

      workbook.write(fileOut);
      fileOut.flush();
      fileOut.close();

    }
    catch (FileNotFoundException fnfe) {
      CDMLogger.getInstance().error(fnfe.getLocalizedMessage(), fnfe, Activator.PLUGIN_ID);
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getLocalizedMessage(), ioe, Activator.PLUGIN_ID);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param workbook
   * @throws IOException
   * @throws ApicWebServiceException
   */
  private void createFMWorkSheet(final Workbook workbook) throws IOException, ApicWebServiceException {
    final String[] sheetHeader = FocusMatrixExport.useCaseSheetHeader;

    final Sheet fmSheet = workbook.getSheet(SHEET_NAME);
    fmSheet.setRowSumsRight(false);

    // Create header for attributes ,value, remarks columns
    Row headerRow = writeHeader(sheetHeader, fmSheet);

    writeUseCaseItemsAsHeader(fmSheet, headerRow);

    ExcelCommon.getInstance().createExcelRow(fmSheet, 3);
    // Create cells for attributes and usage columns
    writeAttributesAndRemarks(workbook, fmSheet);


    setColSize(sheetHeader, fmSheet);
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
      if (columns[col].equals(ExcelClientConstants.RH_ATTRIBUTES) || columns[col].equals(ExcelClientConstants.RH_DESCRIPTION)) {
        sheet.setColumnWidth(col, 9000);
      }
      else if (columns[col].equals(ExcelClientConstants.RH_VALUE) || columns[col].equals(ExcelClientConstants.REMARKS)) {
        sheet.setColumnWidth(col, 4000);
      }
      else {
        sheet.setColumnWidth(col, ExcelCommon.DEFAULT_COL_WIDTH_7000);
      }
    }
  }

  /**
   * @param workbook
   * @param fmSheet
   * @return
   * @throws ApicWebServiceException
   * @throws IOException
   */
  private Map<Integer, Integer> writeAttributesAndRemarks(final Workbook workbook, final Sheet fmSheet)
      throws ApicWebServiceException, IOException {
    final SortedSet<FocusMatrixAttributeClientBO> fmAttributes =
        this.fmDataHandler.getSelFmVersion().getFocusMatrixAttrsSet();
    int rowCount = 1;
    List<IUseCaseItemClientBO> selectedUCItemsList = this.fmDataHandler.getSelFmVersion().getSelectedUCItemsList();
    for (FocusMatrixAttributeClientBO fmAttribute : fmAttributes) {
      if (this.fmDataHandler.isAllAttrNeededForExport() || fmAttribute.isFocusMatrixApplicable()) {
        // show attributes based on filtering in editor
        final Row row = ExcelCommon.getInstance().createExcelRow(fmSheet, rowCount);
        // Attribute name
        CellStyle rowCellStyle =
            (this.fmDataHandler.isAllAttrNeededForExport() && !fmAttribute.isFocusMatrixApplicable())
                ? nonFMrelevantAttrCellStyle : cellStyle;
        ExcelCommon.getInstance().createCell(row, fmAttribute.getAttribute().getName(), 0, rowCellStyle);
        // description
        ExcelCommon.getInstance().createCell(row, fmAttribute.getAttribute().getDescription(), 1, rowCellStyle);
        // attr value
        ExcelCommon.getInstance().createCell(row, fmAttribute.getValueDisplay(), 2, rowCellStyle);
        // remark
        ExcelCommon.getInstance().createCell(row, fmAttribute.getRemarksDisplay(), 3, rowCellStyle);

        // create focus matrix entries as cells for usecase items
        int colCount = 4;
        for (int i = 0; i < selectedUCItemsList.size(); i++) {
          // get cell style with bg color
          CellStyle fmCellStyle = getCellStyle(workbook, fmAttribute, colCount);
          String cellValue = getCellValue(fmAttribute, colCount);
          Cell fmCell;
          String fmLink = getFMLink(fmAttribute, colCount);
          if (CommonUtils.isEmptyString(fmLink)) {
            fmCell = ExcelCommon.getInstance().createCell(row, cellValue, colCount, fmCellStyle);
          }
          else {
            fmCell = ExcelCommon.getInstance().createHyperLinkCell(workbook, row, cellValue, fmLink, colCount,
                fmCellStyle, false);
            fmCell.getCellStyle().setFillForegroundColor(fmCellStyle.getFillForegroundColor());
          }
          fmCell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
          setCellCommentsAndImage(workbook, fmCell, fmAttribute, colCount, row);
          colCount++;
        }
        rowCount++;
      }
    }
    return null;
  }

  /**
   * @param fmAttribute
   * @param colCount
   * @return
   */
  private String getFMLink(final FocusMatrixAttributeClientBO fmAttribute, final int colCount) {
    com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem fmUseCaseItem =
        fmAttribute.getFocusmatrixUseCaseItem(colCount - 4);
    Attribute attribute = fmAttribute.getAttribute();
    return fmUseCaseItem.getLink(attribute);
  }

  /**
   * this method displays the cell comments as remarks and
   *
   * @param workbook
   * @param fmCell
   * @param colCount
   * @param fmAttribute
   * @param row
   * @throws IOException
   */
  private void setCellCommentsAndImage(final Workbook workbook, final Cell fmCell,
      final FocusMatrixAttributeClientBO fmAttribute, final int colCount, final Row row)
      throws IOException {

    com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem fmUseCaseItem =
        fmAttribute.getFocusmatrixUseCaseItem(colCount - 4);
    Attribute attribute = fmAttribute.getAttribute();
    String comments = fmUseCaseItem.getComments(attribute);
    Drawing drawing = fmCell.getSheet().createDrawingPatriarch();
    CreationHelper factory = workbook.getCreationHelper();

    if (CommonUtils.isNotEmptyString(comments)) {
      // When the comment box is visible, have it show in a 1x3 space
      ClientAnchor anchor = factory.createClientAnchor();
      anchor.setCol1(fmCell.getColumnIndex());
      anchor.setCol2(fmCell.getColumnIndex() + 1);
      anchor.setRow1(row.getRowNum());
      anchor.setRow2(row.getRowNum() + 3);

      // Create the comment and set the text+author
      Comment comment = drawing.createCellComment(anchor);

      RichTextString str = factory.createRichTextString(comments);
      comment.setString(str);

    }

    if (fmUseCaseItem.isUcpaMapped(attribute)) {
      // usecase proposal

      // for inserting symbols
      /* Read the input image into InputStream */
      Bundle bundle = Platform.getBundle(com.bosch.caltool.icdm.common.ui.Activator.PLUGIN_ID);
      Path path = new Path(ImageKeys.UC_14X14.getPath());
      URL fileURL = FileLocator.find(bundle, path, null);
      InputStream bagImage = fileURL.openStream();

      /* Convert Image to byte array */
      byte[] bytes = IOUtils.toByteArray(bagImage);
      /* Add Picture to workbook and get a index for the picture */
      int picID = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
      /* Close Input Stream */
      bagImage.close();
      ClientAnchor anchor1 = factory.createClientAnchor();
      anchor1.setCol1(fmCell.getColumnIndex());
      anchor1.setRow1(row.getRowNum());

      /* Invoke createPicture and pass the anchor point and ID */
      Picture ucProposalPic = drawing.createPicture(anchor1, picID);
      /* Call resize method, which resizes the image */
      ucProposalPic.resize();
    }

  }

  /**
   * @param workbook
   * @param fmAttribute
   * @param colCount
   * @return
   */
  private CellStyle getCellStyle(final Workbook workbook, final FocusMatrixAttributeClientBO fmAttribute,
      final int colCount) {
    com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem fmUseCaseItem =
        fmAttribute.getFocusmatrixUseCaseItem(colCount - 4);
    short colorIndex;
    switch (fmUseCaseItem.getColorCode(fmAttribute.getAttribute())) {
      case RED:
        colorIndex = IndexedColors.RED.index;
        break;
      case ORANGE:
        colorIndex = IndexedColors.ORANGE.index;
        break;
      case YELLOW:
        colorIndex = IndexedColors.YELLOW.index;
        break;
      case GREEN:
        colorIndex = IndexedColors.GREEN.index;
        break;
      default:
        colorIndex = IndexedColors.WHITE.index;
        break;
    }
    CellStyle cellStyleWithBgColor = ExcelCommon.INSTANCE.createCellStyleWithBgColor(workbook, colorIndex);

    return cellStyleWithBgColor;
  }

  /**
   * @param fmAttribute
   * @param colCount
   * @return
   */
  private String getCellValue(final FocusMatrixAttributeClientBO fmAttribute, final int colCount) {
    com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem fmUseCaseItem =
        fmAttribute.getFocusmatrixUseCaseItem(colCount - 4);
    // check if the cell is focus matrix relevant
    return fmUseCaseItem.isMapped(fmAttribute.getAttribute()) ? "\u2611" : "";
  }

  /**
   * @param sheetHeader
   * @param ucSheet
   * @return
   */
  private Row writeHeader(final String[] sheetHeader, final Sheet ucSheet) {
    Row headerRow = ExcelCommon.getInstance().createExcelRow(ucSheet, 0);
    for (int headerCol = 0; headerCol < (sheetHeader.length); headerCol++) {
      String cellValue = sheetHeader[headerCol];
      ExcelCommon.getInstance().createHeaderCell(headerRow, cellValue, headerCol, FocusMatrixExport.headerCellStyle,
          this.font);
    }
    return headerRow;
  }

  /**
   * @param fmSheet Sheet
   * @param headerRow
   */
  private void writeUseCaseItemsAsHeader(final Sheet fmSheet, final Row headerRow) {
    List<IUseCaseItemClientBO> selectedUCItemsList = this.fmDataHandler.getSelFmVersion().getSelectedUCItemsList();
    int colCount = 4;
    for (IUseCaseItemClientBO ucItemBO : selectedUCItemsList) {
      ExcelCommon.getInstance().createCell(headerRow, ucItemBO.getName(), colCount, useCaseHeaderCellStyle);
      colCount++;
    }

    headerRow.setHeightInPoints(USE_CASE_HEADER_ROW_HEIGHT * fmSheet.getDefaultRowHeightInPoints());
  }

  /**
   * @param workbook
   */
  private void createSheet(final Workbook workbook) {
    // create first sheet
    workbook.createSheet(SHEET_NAME);

  }

}
