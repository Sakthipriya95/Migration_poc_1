/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.report.common.ExcelCommon;

/**
 * @author dmo5cob
 */
public class AttributesExport {

  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;

  private static final int ATTR_EXPORT_MONTR_TIME_100 = 100;
  private static final int ATTR_EXPORT_MONTR_TIME_10 = 10;
  private static final int ATTR_EXPORT_MONTR_TIME_30 = 30;
  private static final int ATTR_EXPORT_MONTR_TIME_50 = 50;

  /**
   * Constructor
   *
   * @param monitor IProgressMonitor
   */
  public AttributesExport(final IProgressMonitor monitor) {
    this.monitor = monitor;
  }

  /**
   * Cellstyle for header row
   */
  private CellStyle headerCellStyle;
  /**
   * Normal cell style
   */
  private CellStyle cellStyle;
  /**
   * Red cell style
   */
  private CellStyle redCellStyle;
  /**
   * Font instance
   */
  private Font font;

  /**
   * Export Attributes
   *
   * @param filePath destination
   * @param fileExtn file extension
   * @param attrMap filtered sest of attributes
   * @param attrExpMdl AtrrExportModel object
   * @throws FileNotFoundException exception
   * @throws IOException exception
   */
  public void exportAttributes(final String filePath, final String fileExtn, final Map<Long, Attribute> attrMap,
      final AttrExportModel attrExpMdl)
      throws IOException {

    this.monitor.subTask("Creating excel report");
    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    // Main sheets are created before Temp sheet
    createSheets(workbook);

    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
    this.redCellStyle = ExcelCommon.getInstance().createRedCellStyle(workbook);
    this.font = ExcelCommon.getInstance().createFont(workbook);

    String fileFullPath;

    if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
      fileFullPath = filePath;
    }
    else {
      fileFullPath = filePath + "." + fileExtn;
    }
    try (OutputStream fileOut = new FileOutputStream(fileFullPath)) {
      this.monitor.worked(ATTR_EXPORT_MONTR_TIME_10);
      this.monitor.subTask("Exporting attributes . . .");
      createAttrsWorkSheet(workbook, attrMap, attrExpMdl);
      this.monitor.worked(ATTR_EXPORT_MONTR_TIME_30);
      this.monitor.subTask("Exporting values . . .");
      createAttValuessWorkSheet(workbook, attrMap, attrExpMdl);

      this.monitor.worked(ATTR_EXPORT_MONTR_TIME_50);
      this.monitor.subTask("Finishing export . . .");
      workbook.setSelectedTab(0);
      workbook.write(fileOut);
      fileOut.flush();
    }
    this.monitor.worked(ATTR_EXPORT_MONTR_TIME_100);

  }

  /**
   *
   */
  private void createSheets(final Workbook workbook) {
    // create attrs sheet
    workbook.createSheet("Attributes");
    // create values sheet
    workbook.createSheet("Values");
  }

  /**
   * @param workbook
   * @param tempAttrSheet
   * @param attrMap
   * @param attrExpMdl
   * @throws IOException
   */
  private void createAttValuessWorkSheet(final Workbook workbook, final Map<Long, Attribute> attrMap,
      final AttrExportModel attrExpMdl) {

    final String[] attrShtHdr = AttributesExcelColumn.SHT_HDR_ATTR_VAL;

    final Sheet attrSheet = workbook.getSheet("Values");

    final Row headerRow = ExcelCommon.getInstance().createExcelRow(attrSheet, 0);
    for (int headerCol = 0; headerCol < (attrShtHdr.length); headerCol++) {
      String cellValue = attrShtHdr[headerCol];
      ExcelCommon.getInstance().createHeaderCell(headerRow, cellValue, headerCol, this.headerCellStyle, this.font);
    }
    int rowCount = 0;

    SortedSet<Attribute> attrToExport = new TreeSet<>();
    attrToExport.addAll(attrMap.values());
    for (Attribute attr : attrToExport) {
      // Collection of AttributeValue object for that AttributeId
      for (AttributeValue val : attrExpMdl.getAllAttrValuesMap().get(attr.getId()).values()) {
        rowCount++;
        final Row row = ExcelCommon.getInstance().createExcelRow(attrSheet, rowCount);
        for (int col = 0; col < attrShtHdr.length; col++) {
          createAttrValCol(val, row, col, attrMap);
        }
      }
    }
    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(attrSheet, AttributesExcelColumn.VAL_AUTOFLTRRNG, rowCount);
    }
    attrSheet.createFreezePane(0, 1, 0, 1);
    ExcelCommon.getInstance().setColSize(attrShtHdr, attrSheet);
  }

  /**
   * @param workbook
   * @param tempAttrSheet
   * @param attrMap
   * @param attrExpMdl
   * @throws IOException
   */
  private void createAttrsWorkSheet(final Workbook workbook, final Map<Long, Attribute> attrMap,
      final AttrExportModel attrExpMdl) {

    final String[] attrShtHdr = AttributesExcelColumn.SHT_HDR_ATTR;

    final Sheet attrSheet = workbook.getSheet("Attributes");

    final Row headerRow = ExcelCommon.getInstance().createExcelRow(attrSheet, 0);
    for (int headerCol = 0; headerCol < (attrShtHdr.length); headerCol++) {
      String cellValue = attrShtHdr[headerCol];
      ExcelCommon.getInstance().createHeaderCell(headerRow, cellValue, headerCol, this.headerCellStyle, this.font);
    }

    List<Attribute> attrToExport = new ArrayList<>();
    attrToExport.addAll(attrMap.values());


    Collections.sort(attrToExport, new Comparator<Attribute>() {

      @Override
      public int compare(final Attribute arg0, final Attribute arg1) {
        final Attribute attr1 = arg0;
        final Attribute attr2 = arg1;

        return (attrExpMdl.getAttrGroup().getAllSuperGroupMap()
            .get(attrExpMdl.getAttrGroup().getAllGroupMap().get(attr1.getAttrGrpId()).getSuperGrpId()).getName()
            .toLowerCase() +
            attrExpMdl.getAttrGroup().getAllGroupMap().get(attr1.getAttrGrpId()).getName().toLowerCase() +
            attr1.getName().toLowerCase())
                .compareToIgnoreCase(attrExpMdl.getAttrGroup().getAllSuperGroupMap()
                    .get(attrExpMdl.getAttrGroup().getAllGroupMap().get(attr2.getAttrGrpId()).getSuperGrpId())
                    .getName() + attrExpMdl.getAttrGroup().getAllGroupMap().get(attr2.getAttrGrpId()).getName() +
                    attr2.getName().toLowerCase());

      }
    });

    int rowCount = 0;
    for (Attribute attr : attrToExport) {
      rowCount++;
      final Row row = ExcelCommon.getInstance().createExcelRow(attrSheet, rowCount);
      for (int col = 0; col < attrShtHdr.length; col++) {
        createAttrCol(attr, row, col, attrExpMdl);
      }
    }
    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(attrSheet, AttributesExcelColumn.ATTR_AUTOFLTRRNG, rowCount);
    }
    attrSheet.createFreezePane(0, 1, 0, 1);
    ExcelCommon.getInstance().setColSize(attrShtHdr, attrSheet);
  }


  /**
   * Create columns in attr sheet
   *
   * @param attr
   * @param row
   * @param col
   * @param tempAttrSheet
   * @param attrShtHdr
   * @param attrSheet
   * @param expModel
   */
  private void createAttrCol(final Attribute attr, final Row row, final int col, final AttrExportModel expModel) {
    CellStyle styleToUse = this.cellStyle;
    if (attr.isDeleted()) {
      styleToUse = this.redCellStyle;
    }
    final String result = AttributesExcelColumn.getInstance().getColTxtFromAttribute(col, attr, expModel);
    ExcelCommon.getInstance().createCell(row, result, col, styleToUse);
  }


  /**
   * Create columns in values sheet
   *
   * @param val
   * @param row
   * @param col
   * @param tempAttrSheet
   * @param attrShtHdr
   * @param attrSheet
   * @param attrMap
   */
  private void createAttrValCol(final AttributeValue val, final Row row, final int col,
      final Map<Long, Attribute> attrMap) {

    CellStyle styleToUse = this.cellStyle;
    if (val.isDeleted()) {
      styleToUse = this.redCellStyle;
    }
    final String result = AttributesExcelColumn.getInstance().getColTxtFromAttributeValue(col, val, attrMap);

    ExcelCommon.getInstance().createCell(row, result, col, styleToUse);
  }
}
