/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic.pidc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.VersionValidator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportExcelData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PidcImportParser {

  // Constants to hold Excel column numbers - Attribute sheet
  /**
   * Attribute Used Flag column number - Attr sheet
   */
  public static final int ATTRIBUTE_USED_FLAG_COL_NUM = 4;
  /**
   * Attribute value column number - Attr sheet
   */
  public static final int ATTRIBUTE_VALUE_COL_NUM = 5;

  /**
   * Attribute value column number - Attr sheet
   */
  public static final int ATTR_VALUE_UNIT_COL_NUM = 6;

  /**
   * Part number column number - Attr sheet
   */
  public static final int ATTR_PARTNUM_COL_NUM = 7;
  /**
   * Spec link column number- Attr sheet
   */
  public static final int ATTRIBUTE_SPECLINK_COL_NUM = 8;
  /**
   * Additional info description column number- Attr sheet
   */
  public static final int ATTRIBUTE_COMMENTS_COL_NUM = 9;
  /**
   * Attribute Id column number- Attr sheet
   */
  public static final int ATTRIBUTE_ID_COL_NUM = 11;
  /**
   * Last column number- Attr sheet
   */
  public static final short LAST_COLUMN_INDEX = 12;

  // Constants to hold Parser Error Messages
  /**
   * Error message when excel pidc id and selected pidc id doesn't match
   */
  public static final String PIDC_ID_MISMATCH = "Invalid PIDCard Import: Wrong PIDC";
  /**
   * Error message when the excel language and tool's language selection doesn't match
   */
  public static final String LANGUAGE_MISMATCH_ERROR_MSG = "Invalid PIDCard Import: Language Mismatch";
  /**
   * Error message when the excel file is not in a valid format
   */
  public static final String WRONG_IMPORT_FILE_ERROR_MSG =
      "Invalid PIDCard Import: Wrong input format. Kindly Contact iCDM Hotline for further details.";

  /**
   * boolean to check whether the excel import is valid
   */
  private boolean invalidImport;

  /**
   * Field to set comment / error message for the excel import
   */
  private String importMessage;

  private PidcVersion pidcVer;

  private final String excelFilePath;

  private boolean filterAttrFlag;

  /**
   * Map to hold key - attr id , value - excel pidc version attribute
   */
  private Map<Long, PidcVersionAttribute> pidcImportAttrMap = new HashMap<>();

  /**
   * Map to hold key - var name value id, key - map of <key - attr id , value - excel pidc var attribute>
   */
  private Map<Long, Map<Long, PidcVariantAttribute>> varImportAttrMap = new HashMap<>();

  /**
   * key - var name value id, value -Map to hold key - sub var name value id, key - map of <key - attr id , value -
   * excel pidc sub var attribute>
   */
  private Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> subvarImportAttrMap = new HashMap<>();

  /**
   * @param selPidcVersion selected pidc version
   * @param excelPath file path of the excel file
   */
  public PidcImportParser(final PidcVersion selPidcVersion, final String excelPath) {
    this.pidcVer = selPidcVersion;
    this.excelFilePath = excelPath;
  }

  /**
   * @return PidcImportExcelData
   * @throws InvalidInputException Exception in case of invalid data in excel file
   */
  public PidcImportExcelData parseExcelAndFetchAttr() throws InvalidInputException {


    Workbook workBook = null;
    try (InputStream inp = new FileInputStream(this.excelFilePath)) {
      workBook = WorkbookFactory.create(inp);
      if (workBook.getNumberOfSheets() == 4) {
        parseAttributesSheet(workBook);
        if (this.invalidImport) {
          closeInpFile(inp, workBook);
          throw new InvalidInputException("Invalid Input Excel. Please choose a different file!");
        }
        parseVarSubvarSheet(workBook);
      }
    }
    catch (Exception exp) {
      throw new InvalidInputException(exp.getMessage(), exp);
    }
    finally {
      if (null != workBook) {
        try {
          workBook.close();
        }
        catch (IOException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    }
    PidcImportExcelData pidcImportExcelData = new PidcImportExcelData();
    setServiceDataFields(pidcImportExcelData);
    return pidcImportExcelData;
  }

  /**
   * @param workBook
   */
  private void parseVarSubvarSheet(final Workbook workBook) {
    PidcImpVarSheetParser varSubvarSheetParser = new PidcImpVarSheetParser(this);
    varSubvarSheetParser.parseVarSubvarSheet(workBook);
  }

  /**
   * @param pidcImportServiceData
   */
  private void setServiceDataFields(final PidcImportExcelData pidcImportExcelData) {
    pidcImportExcelData.setPidcVersion(this.pidcVer);
    pidcImportExcelData.setPidcImportAttrMap(this.pidcImportAttrMap);
    pidcImportExcelData.setVarImportAttrMap(this.varImportAttrMap);
    pidcImportExcelData.setSubvarImportAttrMap(this.subvarImportAttrMap);
  }

  /**
   * @param inp
   * @param workBook
   */
  private void closeInpFile(final InputStream inp, final Workbook workBook) {
    try {
      if (null != workBook) {
        workBook.close();
      }
      if (null != inp) {
        inp.close();
      }
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getLocalizedMessage(), ioe, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param workBook
   * @throws ApicWebServiceException
   */
  private void parseAttributesSheet(final Workbook workBook) throws ApicWebServiceException {
    Sheet attrSheet = workBook.getSheetAt(0);
    int rowCount = attrSheet.getLastRowNum();
    for (int i = 0; i <= rowCount; i++) {
      Row row = attrSheet.getRow(i);
      if (row.getRowNum() == 0) {
        validateImpVersionInfo(row);
        continue;
      }
      if (!this.invalidImport) {
        getRowAttr(row);
      }
      else {
        return;
      }
    }
  }

  /**
   * @param row
   * @return
   */
  private void getRowAttr(final Row row) {
    PidcVersionAttribute excelAttr = new PidcVersionAttribute();
    int colCount = row.getLastCellNum();
    for (int colNum = colCount - 1; colNum > 0; colNum--) {
      Cell cell = row.getCell(colNum);
      cell.setCellType(Cell.CELL_TYPE_STRING);
      if (colNum == ATTRIBUTE_ID_COL_NUM) {
        // The below case is possible when an empty cell is encountered(possibly due to invalid structural changes)
        if ((cell.getStringCellValue() == null) || cell.getStringCellValue().isEmpty()) {
          break;
        }
        Long attributeID = Long.valueOf(cell.getStringCellValue());
        excelAttr.setAttrId(attributeID);
      }

      setExcelAttrFields(cell, colNum, excelAttr);
    }
    this.pidcImportAttrMap.put(excelAttr.getAttrId(), excelAttr);
  }

  /**
   * @param cell
   * @param excelAttr
   * @param colNum
   */
  private void setExcelAttrFields(final Cell cell, final int colNum, final PidcVersionAttribute excelAttr) {
    if (colNum == ATTRIBUTE_USED_FLAG_COL_NUM) {
      excelAttr.setUsedFlag(
          cell.getStringCellValue().equals("") ? null : PROJ_ATTR_USED_FLAG.getDbType(cell.getStringCellValue()));
    }
    if (colNum == ATTRIBUTE_VALUE_COL_NUM) {
      excelAttr.setValue(cell.getStringCellValue().equals("") ? null : cell.getStringCellValue());
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
   * @param firstRow
   * @throws ApicWebServiceException
   */
  private void validateImpVersionInfo(final Row firstRow) throws ApicWebServiceException {

    String[] importPidcInfo = getPidcVerInfoCellVal(firstRow);
    if (importPidcInfo.length != 7) {
      this.importMessage =
          "The imported excel is an older template.Please export with latest version.Contact iCDM Hotline for further information.";
      this.invalidImport = true;
      return;
    }
    CommonDataBO commonDataBO = new CommonDataBO();
    if (!validateNumOfCol(firstRow) || !validatePidcVer(importPidcInfo, commonDataBO) ||
        !validateLanguage(importPidcInfo, commonDataBO) || !validatePidcId(Long.valueOf(importPidcInfo[2]))) {
      this.invalidImport = true;
      return;
    }
    this.invalidImport = false;
    this.importMessage = "";
    if (CommonUtils.isEqual(importPidcInfo[3], ApicConstants.FILTERED_ATTR_EXCEL)) {// ICDM-788
      setFilterAttrFlag(true);
    }
  }

  /**
   * @param pidcID
   * @return
   */
  private boolean validatePidcId(final Long pidcID) {
    if (!pidcID.equals(this.pidcVer.getPidcId())) {
      this.importMessage = PIDC_ID_MISMATCH;
      return false;
    }
    return true;
  }

  /**
   * @param importPidcInfo
   * @param commonDataBO
   * @return
   */
  private boolean validateLanguage(final String[] importPidcInfo, final CommonDataBO commonDataBO) {
    if (!commonDataBO.getLanguage().getText().equals(importPidcInfo[1])) {
      this.importMessage = LANGUAGE_MISMATCH_ERROR_MSG;
      return false;
    }
    return true;
  }

  /**
   * @param importPidcInfo
   * @param commonDataBO
   * @return
   * @throws ApicWebServiceException
   */
  private boolean validatePidcVer(final String[] importPidcInfo, final CommonDataBO commonDataBO)
      throws ApicWebServiceException {
    VersionValidator versionValidator =
        new VersionValidator(commonDataBO.getParameterValue(CommonParamKey.PIDC_IMPORT_VERSION), true);
    if (!versionValidator.validateVersionFactoredNos(Long.parseLong(importPidcInfo[6]))) {
      // ICDM-2229
      this.importMessage =
          "The imported excel is not in expected template.Contact iCDM Hotline for further information.";
      return false;
    }
    return true;
  }

  /**
   * @param firstRow
   * @return
   */
  private String[] getPidcVerInfoCellVal(final Row firstRow) {
    String pidcIDLangValue = firstRow.getCell(firstRow.getLastCellNum() - 1).getStringCellValue();
    return pidcIDLangValue.split("##");
  }

  /**
   * @param firstRow
   * @return
   */
  private boolean validateNumOfCol(final Row firstRow) {
    if (firstRow.getLastCellNum() != LAST_COLUMN_INDEX) {
      this.importMessage = WRONG_IMPORT_FILE_ERROR_MSG;
      return false;
    }
    return true;
  }

  /**
   * @return the pidcVer
   */
  public PidcVersion getPidcVer() {
    return this.pidcVer;
  }

  /**
   * @param pidcVer the pidcVer to set
   */
  public void setPidcVer(final PidcVersion pidcVer) {
    this.pidcVer = pidcVer;
  }

  /**
   * @return the isInvalidImport
   */
  public boolean isInvalidImport() {
    return this.invalidImport;
  }

  /**
   * @param isInvalidImport the isInvalidImport to set
   */
  public void setInvalidImport(final boolean isInvalidImport) {
    this.invalidImport = isInvalidImport;
  }

  /**
   * @return the importMessage
   */
  public String getImportMessage() {
    return this.importMessage;
  }

  /**
   * @param importMessage the importMessage to set
   */
  public void setImportMessage(final String importMessage) {
    this.importMessage = importMessage;
  }

  /**
   * @return the filterAttrFlag
   */
  public boolean isFilterAttrFlag() {
    return this.filterAttrFlag;
  }

  /**
   * @param filterAttrFlag the filterAttrFlag to set
   */
  public void setFilterAttrFlag(final boolean filterAttrFlag) {
    this.filterAttrFlag = filterAttrFlag;
  }

  /**
   * @return the pidcImportAttrMap
   */
  public Map<Long, PidcVersionAttribute> getPidcImportAttrMap() {
    return this.pidcImportAttrMap;
  }

  /**
   * @param pidcImportAttrMap the pidcImportAttrMap to set
   */
  public void setPidcImportAttrMap(final Map<Long, PidcVersionAttribute> pidcImportAttrMap) {
    this.pidcImportAttrMap = pidcImportAttrMap;
  }


  /**
   * @return the varImportAttrMap
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getVarImportAttrMap() {
    return this.varImportAttrMap;
  }


  /**
   * @param varImportAttrMap the varImportAttrMap to set
   */
  public void setVarImportAttrMap(final Map<Long, Map<Long, PidcVariantAttribute>> varImportAttrMap) {
    this.varImportAttrMap = varImportAttrMap;
  }


  /**
   * @return the subvarImportAttrMap
   */
  public Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> getSubvarImportAttrMap() {
    return this.subvarImportAttrMap;
  }


  /**
   * @param subvarImportAttrMap the subvarImportAttrMap to set
   */
  public void setSubvarImportAttrMap(
      final Map<Long, Map<Long, Map<Long, PidcSubVariantAttribute>>> subvarImportAttrMap) {
    this.subvarImportAttrMap = subvarImportAttrMap;
  }
}
