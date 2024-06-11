/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic.pidc;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;

/**
 * @author dja7cob
 */
public class PidcImpVarSheetParser {

  // Constants to hold Excel column numbers - Variant/ Subvariant sheet
  /**
   * Variant / subvariant attr flag column number - Var sheet
   */
  public static final int VAR_SUBVAR_COL_NUM = 0;
  /**
   * Variant / subvariant attr flag column number - Var sheet
   */
  public static final int VAR_SUBVAR_HEADER_COL_NUM = 1;
  /**
   * Attribute Used Flag column number - Var sheet
   */
  public static final int ATTRIBUTE_USED_FLAG_COL_NUM = 5;
  /**
   * Attribute value column number - Var sheet
   */
  public static final int ATTRIBUTE_VALUE_COL_NUM = 6;

  /**
   * Part number column number - Var sheet
   */
  public static final int ATTR_PARTNUM_COL_NUM = 7;
  /**
   * Spec link column number- Var sheet
   */
  public static final int ATTRIBUTE_SPECLINK_COL_NUM = 8;
  /**
   * Additional info description column number- Var sheet
   */
  public static final int ATTRIBUTE_COMMENTS_COL_NUM = 9;
  /**
   * variant or subvariant id column number- Var sheet
   */
  public static final int VAR_SUBVAR_ID_COL_NUM = 10;
  /**
   * Attribute Id column number- Var sheet
   */
  public static final int ATTRIBUTE_ID_COL_NUM = 11;
  /**
   * Last column number- Var sheet
   */
  public static final short LAST_COLUMN_INDEX = 12;
  /**
   * Pidc Import Parser
   */
  private final PidcImportParser pidcImportParser;
  /**
   * Recent variant / subvariant id in excel
   */
  private Long nameValId;
  /**
   * Recent variant id in excel
   */
  private Long varNameValId;

  /**
   * @param pidcImportParser PidcImportParser
   */
  public PidcImpVarSheetParser(final PidcImportParser pidcImportParser) {
    this.pidcImportParser = pidcImportParser;
  }

  /**
   * @param workBook excel workbook instance
   */
  public void parseVarSubvarSheet(final Workbook workBook) {
    Sheet varSubvarSheet = workBook.getSheetAt(1);
    int rowCount = varSubvarSheet.getLastRowNum();

    for (int rowNum = 1; rowNum <= rowCount; rowNum++) {
      readExcelRow(varSubvarSheet, rowNum);
    }
  }

  /**
   * @param varSubvarSheet variant subvariant details sheet
   * @param rowNum current row
   */
  private void readExcelRow(final Sheet varSubvarSheet, final int rowNum) {
    Row row = varSubvarSheet.getRow(rowNum);
    int colCount = row.getLastCellNum();

    if ((null != row.getCell(VAR_SUBVAR_ID_COL_NUM)) &&
        !row.getCell(VAR_SUBVAR_ID_COL_NUM).getStringCellValue().isEmpty()) {
      this.nameValId = Long.valueOf(row.getCell(VAR_SUBVAR_ID_COL_NUM).getStringCellValue());
      if ((null != row.getCell(VAR_SUBVAR_HEADER_COL_NUM)) &&
          !row.getCell(VAR_SUBVAR_HEADER_COL_NUM).getStringCellValue().isEmpty() &&
          ("Variant").equals(row.getCell(VAR_SUBVAR_HEADER_COL_NUM).getStringCellValue().trim())) {
        this.varNameValId = Long.valueOf(row.getCell(VAR_SUBVAR_ID_COL_NUM).getStringCellValue());
      }
    }
    else if ((null != this.nameValId) && (null != row.getCell(ATTRIBUTE_ID_COL_NUM)) &&
        !row.getCell(ATTRIBUTE_ID_COL_NUM).getStringCellValue().isEmpty()) {
      fillAttrDetails(row, colCount);
    }
  }

  /**
   * @param row current row
   * @param colCount col count in current row
   */
  private void fillAttrDetails(final Row row, final int colCount) {
    PidcVariantAttribute varExcelAttr = null;
    PidcSubVariantAttribute subVarExcelAttr = null;
    Long attrId = Long.valueOf(row.getCell(ATTRIBUTE_ID_COL_NUM).getStringCellValue());
    if ((null != row.getCell(VAR_SUBVAR_COL_NUM)) && !row.getCell(VAR_SUBVAR_COL_NUM).getStringCellValue().isEmpty()) {
      varExcelAttr = createVarAttrObj(attrId);
    }
    else {
      subVarExcelAttr = createSubvarAttrObj(attrId);
    }
    for (int colNum = colCount - 1; colNum > 0; colNum--) {
      if (null != row.getCell(colNum)) {
        Cell cell = row.getCell(colNum);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        setAttrFields(cell, colNum, null != varExcelAttr ? varExcelAttr : subVarExcelAttr);
      }
    }
    if (null != varExcelAttr) {
      addVarAttrToMap(varExcelAttr);
    }
    if (null != subVarExcelAttr) {
      addSubvarAttrToMap(subVarExcelAttr);
    }
  }

  /**
   * @param subVarExcelAttr excel sub variant attr
   */
  private void addSubvarAttrToMap(final PidcSubVariantAttribute subVarExcelAttr) {
    if (this.pidcImportParser.getSubvarImportAttrMap().containsKey(this.varNameValId) &&
        (null != this.pidcImportParser.getSubvarImportAttrMap().get(this.varNameValId))) {
      if (this.pidcImportParser.getSubvarImportAttrMap().get(this.varNameValId)
          .containsKey(subVarExcelAttr.getSubVarNameValId()) &&
          (null != this.pidcImportParser.getSubvarImportAttrMap().get(this.varNameValId)
              .get(subVarExcelAttr.getSubVarNameValId()))) {
        this.pidcImportParser.getSubvarImportAttrMap().get(this.varNameValId).get(subVarExcelAttr.getSubVarNameValId())
            .put(subVarExcelAttr.getAttrId(), subVarExcelAttr);
      }
      else {
        Map<Long, PidcSubVariantAttribute> subvarAttrMap = new HashMap<>();
        subvarAttrMap.put(subVarExcelAttr.getAttrId(), subVarExcelAttr);
        this.pidcImportParser.getSubvarImportAttrMap().get(this.varNameValId).put(subVarExcelAttr.getSubVarNameValId(),
            subvarAttrMap);
      }
    }
    else {
      Map<Long, PidcSubVariantAttribute> subvarAttrMap = new HashMap<>();
      subvarAttrMap.put(subVarExcelAttr.getAttrId(), subVarExcelAttr);
      Map<Long, Map<Long, PidcSubVariantAttribute>> subvarIdSubvarAttrMap = new HashMap<>();
      subvarIdSubvarAttrMap.put(subVarExcelAttr.getSubVarNameValId(), subvarAttrMap);
      this.pidcImportParser.getSubvarImportAttrMap().put(this.varNameValId, subvarIdSubvarAttrMap);
    }
  }

  /**
   * @param varExcelAttr excel var attr
   */
  private void addVarAttrToMap(final PidcVariantAttribute varExcelAttr) {
    if (null != this.pidcImportParser.getVarImportAttrMap().get(varExcelAttr.getVarNameValId())) {
      this.pidcImportParser.getVarImportAttrMap().get(varExcelAttr.getVarNameValId()).put(varExcelAttr.getAttrId(),
          varExcelAttr);
    }
    else {
      Map<Long, PidcVariantAttribute> varAttrMap = new HashMap<>();
      varAttrMap.put(varExcelAttr.getAttrId(), varExcelAttr);
      this.pidcImportParser.getVarImportAttrMap().put(varExcelAttr.getVarNameValId(), varAttrMap);
    }
  }

  /**
   * @param attrId excel sub var attr id
   * @return PidcSubVariantAttribute
   */
  private PidcSubVariantAttribute createSubvarAttrObj(final Long attrId) {
    PidcSubVariantAttribute subVarExcelAttr;
    subVarExcelAttr = new PidcSubVariantAttribute();
    subVarExcelAttr.setSubVarNameValId(this.nameValId);
    subVarExcelAttr.setPidcVersionId(this.pidcImportParser.getPidcVer().getId());
    subVarExcelAttr.setAttrId(attrId);
    return subVarExcelAttr;
  }

  /**
   * @param attrId excel var attr id
   * @return PidcVariantAttribute
   */
  private PidcVariantAttribute createVarAttrObj(final Long attrId) {
    PidcVariantAttribute varExcelAttr;
    varExcelAttr = new PidcVariantAttribute();
    varExcelAttr.setVarNameValId(this.nameValId);
    varExcelAttr.setPidcVersionId(this.pidcImportParser.getPidcVer().getId());
    varExcelAttr.setAttrId(attrId);
    return varExcelAttr;
  }

  /**
   * @param cell current cell
   * @param IProjectAttribute var / subvar attr
   */
  private void setAttrFields(final Cell cell, final int colNum, final IProjectAttribute excelAttr) {
    if (colNum == ATTRIBUTE_USED_FLAG_COL_NUM) {
      excelAttr.setUsedFlag(
          "".equals(cell.getStringCellValue()) ? null : PROJ_ATTR_USED_FLAG.getDbType(cell.getStringCellValue()));
    }
    if (colNum == ATTRIBUTE_VALUE_COL_NUM) {
      excelAttr.setValue("".equals(cell.getStringCellValue()) ? null : cell.getStringCellValue());
    }
    if (colNum == ATTR_PARTNUM_COL_NUM) {
      excelAttr.setPartNumber(cell.getStringCellValue());
    }
    else if (colNum == ATTRIBUTE_SPECLINK_COL_NUM) {
      excelAttr.setSpecLink(cell.getStringCellValue());
    }
    else if (colNum == ATTRIBUTE_COMMENTS_COL_NUM) {
      excelAttr.setAdditionalInfoDesc(cell.getStringCellValue());
    }
  }


  /**
   * @return the pidcImportParser
   */
  public PidcImportParser getPidcImportParser() {
    return this.pidcImportParser;
  }

}
