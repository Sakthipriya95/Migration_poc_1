/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.monicareportparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.monicareportparser.data.MonitoringToolOutput;
import com.bosch.caltool.monicareportparser.data.ParameterInfo;
import com.bosch.caltool.monicareportparser.exception.MonicaRptParserException;
import com.bosch.caltool.monicareportparser.util.MonicaRptParCons;


/**
 * @author rgo7cob
 */
public class MonitoringToolParser {

  /**
   * Input Stream for MoniCa Parser
   */
  private InputStream inputStreamForParsing;

  /**
   * logger
   */
  private final ILoggerAdapter logger;

  /**
   * monicaOp output of the excel
   */
  private final MonitoringToolOutput monicaOp = new MonitoringToolOutput();

  /**
   * @param logger logger
   */
  public MonitoringToolParser(final ILoggerAdapter logger) {
    this.logger = logger;
  }


  /**
   * parse the excel file
   *
   * @param sheetName Sheetname from MoniCa excel
   * @throws MonicaRptParserException ParserException
   */
  public void parse(final String sheetName) throws MonicaRptParserException {
    if (this.inputStreamForParsing == null) {
      throw new MonicaRptParserException(MonicaRptParCons.ERR_IN_MONICA_INPUTSTREAM);
    }


    try (Workbook workbook = new XSSFWorkbook(this.inputStreamForParsing)) {


      this.logger.debug("parsing the excel sheet {}", sheetName);

      // get the correct sheet.
      Sheet firstSheet = getWorkBookSheet(workbook, sheetName);
      Iterator<Row> iterator = firstSheet.iterator();

      // fill the putput data
      fillOutputData(iterator, this.monicaOp);
      this.logger.debug("parsing the excel sheet complete {}", sheetName);
    }
    // log Io Exception
    catch (IOException ioExp) {
      throw new MonicaRptParserException(MonicaRptParCons.ERR_READ_MONICA_STREAM, ioExp);
    }


  }

  /**
   * Get the sheet names from the excel file
   *
   * @param excelFilePath file path
   * @return all the sheetNames
   */
  public List<String> getAllSheetNames(final String excelFilePath) {
    List<String> sheetNames = new ArrayList<>();
    try (FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(inputStream)) {

      for (int count = 0; count < workbook.getNumberOfSheets(); count++) {
        sheetNames.add(workbook.getSheetAt(count).getSheetName());
      }
    }
    // log Io Exception
    catch (IOException ioExp) {
      this.logger.error(ioExp.getMessage(), ioExp);
    }
    return sheetNames;
  }

  /**
   * @param workbook
   * @param sheetName sheetName
   * @return the correct sheet
   */
  private Sheet getWorkBookSheet(final Workbook workbook, final String sheetName) throws MonicaRptParserException {
    this.logger.debug("get the sheet using the sheet name {}", sheetName);
    // Iterate through the sheets and get the correct sheet
    for (int count = 0; count < workbook.getNumberOfSheets(); count++) {
      if (workbook.getSheetAt(count).getSheetName().equals(sheetName)) {
        return workbook.getSheetAt(count);
      }
    }
    throw new MonicaRptParserException(MonicaRptParCons.ERR_SHEET_NOT_FOUND);

  }

  /**
   * @param iterator
   * @param outputData
   */
  private void fillOutputData(final Iterator<Row> iterator, final MonitoringToolOutput outputData)
      throws MonicaRptParserException {
    this.logger.debug("filling the output data....");
    int count = 0;
    while (iterator.hasNext()) {
      Row nextRow = iterator.next();
      Iterator<Cell> cellIterator = nextRow.cellIterator();
      while (cellIterator.hasNext()) {
        Cell cell = cellIterator.next();
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
          String cellValue = cell.getStringCellValue().toUpperCase().trim();
          // get param data
          if ((count > 0) && !cellValue.equals(MonicaRptParCons.REVIEWED_BY)) {
            ParameterInfo paramInfo = new ParameterInfo();
            // populate label
            paramInfo.setLabelName(cell.getStringCellValue());
            // populate status
            Cell statusCell = cellIterator.next();
            paramInfo.setStatus(statusCell.getStringCellValue());

            // populate cell comment
            if (null != statusCell.getCellComment()) {
              Comment comment = statusCell.getCellComment();
              paramInfo.setCellCommentForParam(comment.getString().toString());
            }

            // populate comment
            paramInfo.setComment(cellIterator.next().getStringCellValue());
            // populate reviewed by
            if (count == 2) {
              paramInfo.setReviewedBy(cellIterator.next().getStringCellValue());
            }
            outputData.getParamInfoMap().put(paramInfo.getLabelName(), paramInfo);
          }

          switch (cellValue) {
            // get A2l file path
            case MonicaRptParCons.A2L_FILE_PATH:
              cell = cellIterator.next();
              outputData.setA2lFilePath(cell.getStringCellValue());
              break;
            // get Dcm file path
            case MonicaRptParCons.DCM_PATH:
              cell = cellIterator.next();
              outputData.setDcmFilePath(cell.getStringCellValue());
              break;
            // get review status
            case MonicaRptParCons.ICDM_STATUS:
              cell = cellIterator.next();
              outputData.setReviewStatus(cell.getStringCellValue());
              break;
            case MonicaRptParCons.COMMENT:
              count = 1;
              break;
            case MonicaRptParCons.REVIEWED_BY:
              count = 2;
              break;
            default:
              break;
          }
        }
      }
    }
    this.logger.debug("filling the output data complete.");
    validateExcelContents(outputData);
  }

  /**
   * @param outputData outputData
   */
  private void validateExcelContents(final MonitoringToolOutput outputData) throws MonicaRptParserException {
    // No A2l file
    if ((outputData.getA2lFilePath() == null) || (outputData.getA2lFilePath().length() == 0)) {
      throw new MonicaRptParserException(MonicaRptParCons.ERR_NO_A2L);
    }
    // No dcm file
    if ((outputData.getDcmFilePath() == null) || (outputData.getDcmFilePath().length() == 0)) {
      throw new MonicaRptParserException(MonicaRptParCons.ERR_NO_DCM);
    }
    // No params
    if (outputData.getParamInfoMap().isEmpty()) {
      throw new MonicaRptParserException(MonicaRptParCons.ERR_NO_LABEL);
    }
  }

  /**
   * @return the output
   */
  public MonitoringToolOutput getOutput() {
    return this.monicaOp;
  }

  /**
   * @param inputFileStream the inputStream to set
   */
  public void setInputStream(final InputStream inputFileStream) {
    this.inputStreamForParsing = inputFileStream;
  }


}
