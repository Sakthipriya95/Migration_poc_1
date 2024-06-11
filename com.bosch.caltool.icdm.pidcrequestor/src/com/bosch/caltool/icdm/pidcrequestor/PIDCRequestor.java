/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.pidcrequestor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.APICStub.AttributeValue;
import com.bosch.caltool.apic.ws.client.APICStub.SuperGroupType;
import com.bosch.caltool.apic.ws.client.APICStub.ValueList;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * The class reads a masterfile, gets information from a webservice, fill in the information in different sheets and
 * creats a new file for the user.
 *
 * @author ikt8fe
 */
public class PIDCRequestor {


  private static final String ATTR_E_G = "Attr-E-G";

  private static final String GROUP_E_G = "Group-E-G";

  private static final String VALUE_E_G = "Value-E-G";
  /**
   * Constant Value for the sheet 'New Value'. Value = NEW_VALUE
   */
  public final static String NEW_VALUE = "NEW VALUE";
  /**
   * Constant Value for the sheet 'New Attribute'. Value = 2
   */
  public final static String NEW_ATTRIBUTE = "NEW ATTRIBUTE";
  private final ILoggerAdapter logger;

  private int globalI = 0;
  private Attribute[] attributes;
  private int lengthSLinked = 0;
  private Workbook wb;

  /**
   * Webclient, which is used to get the different informations
   */
  private final APICWebServiceClient apicWsClient;

  /**
   * Default Constructor for a PIDC Requestor. After creation of a PIDC Requestor the Excel file can be generated with
   * the {@link PIDCRequestor#createExcelFile(InputStream, String) createExcelFile } method
   *
   * @param logger an ILoggerAdpater for logging information during the file export
   * @see PIDCRequestor#createExcelFile(InputStream, String)
   */
  public PIDCRequestor(final ILoggerAdapter logger) {
    this.logger = logger;
    this.apicWsClient = new APICWebServiceClient();
  }

  /**
   * Constructor for a PIDC Requestor. A logger and a webservice object have to be passed. After creation of a PIDC
   * Requestor the Excel file can be generated with the {@link PIDCRequestor#createExcelFile(InputStream, String)
   * createExcelFile } method
   *
   * @param logger an ILoggerAdpater for logging information during the file export
   * @param apicWsClient a APICWebServiceClient object from that the necessary information are gathered to fill the
   *          Excel export file
   * @see PIDCRequestor#createExcelFile(InputStream, String)
   */
  public PIDCRequestor(final ILoggerAdapter logger, final APICWebServiceClient apicWsClient) {
    this.logger = logger;
    this.apicWsClient = apicWsClient;
  }

  /**
   * Reads the masterfile via input stream and calls different methods to fill the workbook. The given stream must be
   * closed in the calling application after <code>createExcelFile</code> has finished. After the creation of the file,
   * the method <code>getExcelFile()</code> can be called to return the finished file as byte stream or
   * <code>writeExcelFile(String path)</code> can be called to write the file to a given directory
   *
   * @param stream the InputStream of the template file
   * @param activeSheet the sheet that should be active when starting the exported file. Can be one of the values
   *          <code>PIDCRequestor.NEW_VALUE</code> or <code>PIDCRequestor.NEW_ATTRIBUTE</code>
   * @throws IOException when an error happened during reading the template file given at the parameter stream
   * @throws InvalidFormatException when the InputStream doesn't represent the reqwuired template file
   * @see PIDCRequestor#getExcelFile()
   * @see PIDCRequestor#writeExcelFile(String)
   */
  public void createExcelFile(final InputStream stream, final String activeSheet)
      throws IOException, InvalidFormatException {
    this.logger.info("Starting creation of PIDC requestor");
    try {
      setTemplateWorkbook(stream);
      fillExcelFile();
      setActiveSheet(activeSheet);
      this.logger.info("PIDC Requestor ready for export");
    }
    catch (InvalidFormatException e) {

      this.logger.error("InputStream seems to hav to wrong " + "format and is not compatible with POI Excel Format", e);
      throw e;
    }
    catch (IOException e) {

      this.logger.error("Error when trying to convert the input " + "stream to a workbook", e);
      throw e;
    }
  }

  private void setTemplateWorkbook(final InputStream stream) throws InvalidFormatException, IOException {
    this.wb = WorkbookFactory.create(stream);
  }

  private void fillExcelFile() {
    webclientAttributesE();
    dropdownAttributesE();
    WebclientAttributesG();
    dropdownAttributesG();
    this.lengthSLinked = WebclientGroupsE();
    dropdownGroupsE();
    this.lengthSLinked = WebclientGroupsG();
    dropdownGroupsG();
    WebclientValuesE();
    WebclientValuesG();
    webclientAttributesEGIsNormalizedUnit();
    WebclientGroupsEG();
    WebclientValuesEG();
    hide();
  }

  public String getExcelFilename(final String path) {
    String fileName = path + "PIDC-Requester_" + name() + "_" + date() + ".xlsm";
    int filePostfix = 1;

    while (new File(fileName).exists()) {
      fileName = path + "PIDC-Requester_" + name() + "_" + date() + "_" + String.valueOf(filePostfix) + ".xlsm";
      filePostfix++;
    }

    this.logger.info("Path with determined filename " + fileName);
    return fileName;
  }

  /**
   * Returns the PIDC-Requestor as byte array. The array represents the Excel workbook with information filled from the
   * iCDM webservice ready for export to a user selected folder for example.
   *
   * @return a byte array that represents an excel workbook
   * @throws IOException when an error happened during reading the template file given at the parameter stream
   */
  public byte[] getExcelFile() throws IOException {
    byte[] excelFileArray = null;

    try (ByteArrayOutputStream excelFile = new ByteArrayOutputStream();) {

      this.wb.write(excelFile);
      excelFileArray = excelFile.toByteArray();

    }
    catch (IOException e) {
      this.logger.error("Excel File couldn't be converted into a Byte Stream", e);
      throw e;
    }

    return excelFileArray;
  }

  /**
   * Writes the PIDC-Requestor to the given path. The file name is set by the method itself with the pattern
   * 'PIDC-Requestor' + 'Username' + 'Current Date' + '.xlsm'
   *
   * @param url the url to write the ecxel file to
   * @return a string containing the URL of the written file
   * @throws IOException when an error happened during writing the file
   */
  public String writeExcelFile(final String url) throws IOException {
    this.logger.info("Starting to write PIDC Requestor to path " + url);

    try (FileOutputStream fileOut = new FileOutputStream(url);) {
      fileOut.write(getExcelFile());
    }
    catch (IOException e) {
      this.logger.error("Excel File couldn't be written to " + url, e);
      throw e;
    }

    this.logger.info("File written to " + url);
    return url;
  }

  private void setActiveSheet(final String sheet) {
    /*
     * If the sheet is neither "New Value" nor "New Attribute" an exception is thrown because because only these
     * attributes should be active after user export.
     */
    if (!(sheet.equals(PIDCRequestor.NEW_VALUE) || sheet.equals(PIDCRequestor.NEW_ATTRIBUTE))) {
      throw new IllegalArgumentException("Only the arguments " +
          "PIDCRequestor.NEW_VALUE or PIDCRequestor.NEW_ATTRIBUTE are" + "valid parameters for setActiveSheet");
    }
    // Get the sheet index in the file. If the sheet index is '-1', the sheet
    // is not found and an error has to be raised
    int sheetIndex = this.wb.getSheetIndex(sheet);

    if (sheetIndex == -1) {
      throw new IllegalArgumentException("The template has no sheet for " +
          "'New Parameter' or 'New Sheet'. They must exist to activate the " + "proper sheet for the user");
    }
    this.wb.setActiveSheet(this.wb.getSheetIndex(sheet));
  }

  /**
   * The method gets all attributes in english from the webclient
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void webclientAttributesE() {
    int i = 0;
    try {
      this.attributes = getAllAttributesWoDeleted();
      String[] sattributes = new String[this.attributes.length];

      // fetch all attributes and copy it to the array sattributes
      for (int loop = 0; loop < this.attributes.length; loop++) {
        sattributes[loop] = this.attributes[loop].getNameE();
      }
      // sort all attribute names into order, ignor upper case
      Arrays.sort(sattributes, new Comparator<String>() {

        @Override
        public int compare(final String o1, final String o2) {
          return o1.compareToIgnoreCase(o2);
        }

      });

      // all english attributes get transferred into the sheet "Attr-E"
      for (i = 0; i < sattributes.length; i++) {
        Sheet sheet = this.wb.getSheet("Attr-E");
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(0);
        if (cell == null) {
          cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sattributes[i]);

      }
      // all english attributes get transferred into the sheet "Attr-EG"
      for (i = 0; i < sattributes.length; i++) {
        Sheet sheet = this.wb.getSheet("Attr-EG");
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(0);
        if (cell == null) {
          cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sattributes[i]);
        // transfer parameter to write the german attributes after the english ones
        this.globalI++;
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
  }

  /**
   * The method creats two dropdown list
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void dropdownAttributesE() {

    DataValidation dataValidation = null;
    DataValidationConstraint constraint = null;
    DataValidationHelper validationHelper = null;

    // Dropdown list for the sheet "Question & Feedback"
    validationHelper = new XSSFDataValidationHelper((XSSFSheet) this.wb.getSheet("Question & Feedback"));
    CellRangeAddressList addressList = new CellRangeAddressList(7, 26, 1, 1);
    constraint = validationHelper.createFormulaListConstraint("'Attr-E'!$A$2:$A$" + this.attributes.length + 1);
    dataValidation = validationHelper.createValidation(constraint, addressList);
    dataValidation.setSuppressDropDownArrow(false);
    dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
    dataValidation.setShowErrorBox(true);
    dataValidation.createErrorBox("Misentry", "Please choose an attribute from the dropdown menu");
    ((XSSFSheet) this.wb.getSheet("Question & Feedback")).addValidationData(dataValidation);

    // Dropdown list for the sheet "New Value"
    validationHelper = new XSSFDataValidationHelper((XSSFSheet) this.wb.getSheet(NEW_VALUE));
    CellRangeAddressList addressList2 = new CellRangeAddressList(7, 26, 1, 1);
    constraint = validationHelper.createFormulaListConstraint("'Attr-E'!$A$2:$A$" + this.attributes.length + 1);
    dataValidation = validationHelper.createValidation(constraint, addressList2);
    dataValidation.setSuppressDropDownArrow(false);
    dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
    dataValidation.setShowErrorBox(true);
    dataValidation.createErrorBox("Misentry", "Please choose an attribute from the dropdown menu");
    ((XSSFSheet) this.wb.getSheet(NEW_VALUE)).addValidationData(dataValidation);
  }


  /**
   * The method gets all attributes in german from the webclient
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void WebclientAttributesG() {
    int i = 0;
    int localI = 0;
    try {
      this.attributes = getAllAttributesWoDeleted();
      String[] sattributes = new String[this.attributes.length];
      // fetch all attributes and copy it to the array sattributes
      for (int loop = 0; loop < this.attributes.length; loop++) {
        sattributes[loop] = this.attributes[loop].getNameG();
      }
      // sort all attribute names into order, ignor upper case
      Arrays.sort(sattributes, new Comparator<String>() {

        @Override
        public int compare(final String o1, final String o2) {
          return o1.compareToIgnoreCase(o2);
        }

      });

      // all german attributes get transferred into the sheet "Attr-G"
      for (i = 0; i < sattributes.length; i++) {
        Sheet sheet = this.wb.getSheet("Attr-G");
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(0);
        if (cell == null) {
          cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sattributes[i]);

      }
      // all german attributes get transferred into the sheet "Attr-EG"
      // global_i to transfer the german attributes after the english ones
      for (i = this.globalI; i < (sattributes.length + this.globalI); i++) {
        Sheet sheet = this.wb.getSheet("Attr-EG");
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(0);
        if (cell == null) {
          cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sattributes[localI]);
        localI++;
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
  }


  /**
   * The method creats two dropdown list
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void dropdownAttributesG() {

    DataValidation dataValidation = null;
    DataValidationConstraint constraint = null;
    DataValidationHelper validationHelper = null;

    // Dropdown list for the sheet "Question & Feedback"
    validationHelper = new XSSFDataValidationHelper((XSSFSheet) this.wb.getSheet("Question & Feedback"));
    CellRangeAddressList addressList = new CellRangeAddressList(7, 27, 2, 2);
    constraint = validationHelper.createFormulaListConstraint("'Attr-G'!$A$2:$A$" + this.attributes.length + 1);
    dataValidation = validationHelper.createValidation(constraint, addressList);
    dataValidation.setSuppressDropDownArrow(false);
    dataValidation.setShowErrorBox(true);
    dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
    dataValidation.createErrorBox("Ungültiger Wert", "Wählen Sie bitte ein Attribut von der Dropdown-Liste");
    ((XSSFSheet) this.wb.getSheet("Question & Feedback")).addValidationData(dataValidation);

    // Dropdown list for the sheet "New Value"
    validationHelper = new XSSFDataValidationHelper((XSSFSheet) this.wb.getSheet(NEW_VALUE));
    CellRangeAddressList addressList2 = new CellRangeAddressList(7, 26, 2, 2);
    constraint = validationHelper.createFormulaListConstraint("'Attr-G'!$A$2:$A$" + this.attributes.length + 1);
    dataValidation = validationHelper.createValidation(constraint, addressList2);
    dataValidation.setSuppressDropDownArrow(false);
    dataValidation.setShowErrorBox(true);
    dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
    dataValidation.createErrorBox("Ungültiger Wert", "Wählen Sie bitte ein Attribut von der Dropdown-Liste");
    ((XSSFSheet) this.wb.getSheet(NEW_VALUE)).addValidationData(dataValidation);
  }


  /**
   * The method gets all groups in english from the webclient
   *
   * @param wb Workbook
   * @return int length_sLinked is the length of the array. Is needed to creat the dropdown list
   * @author ikt8fe
   */
  private int WebclientGroupsE() {
    int i = 0;
    try {
      SuperGroupType[] superGroups = getAllAttrGroupsWoDeleted();
      String[] ssuperGroups = new String[superGroups.length];

      // fetch all supergroups and copy it to the array ssuperGroups (needed to get the groups)
      for (int loop = 0; loop < superGroups.length; loop++) {
        ssuperGroups[loop] = superGroups[loop].getNameE();
      }
      String[] sLinked = new String[0];

      // fetch all groups in english and copy it to the array sGroups
      for (SuperGroupType superGroup : superGroups) {
        if (superGroup.getGroups() != null) {
          String[] sGroups = new String[superGroup.getGroups().length];

          for (int count = 0; count < superGroup.getGroups().length; count++) {
            sGroups[count] = superGroup.getGroups()[count].getNameE();
          }
          this.lengthSLinked = sLinked.length;
          sLinked = Arrays.copyOf(sLinked, sLinked.length + sGroups.length);
          System.arraycopy(sGroups, 0, sLinked, this.lengthSLinked, sGroups.length);

        }
      }
      this.lengthSLinked = sLinked.length;
      // sort all group names into order, ignor upper case
      Arrays.sort(sLinked, new Comparator<String>() {

        @Override
        public int compare(final String o1, final String o2) {
          return o1.compareToIgnoreCase(o2);
        }

      });
      // all english groups get transferred into the sheet "Group-E"
      for (i = 0; i < sLinked.length; i++) {
        Sheet sheet = this.wb.getSheet("Group-E");
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(0);
        if (cell == null) {
          cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sLinked[i]);
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
    return this.lengthSLinked;
  }


  /**
   * The method creats a dropdown list
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void dropdownGroupsE() {
    DataValidation dataValidation = null;
    DataValidationConstraint constraint = null;
    DataValidationHelper validationHelper = null;

    // Dropdown list for the sheet "New Attribute"
    validationHelper = new XSSFDataValidationHelper((XSSFSheet) this.wb.getSheet(NEW_ATTRIBUTE));
    CellRangeAddressList addressList = new CellRangeAddressList(6, 25, 1, 1);
    constraint = validationHelper.createFormulaListConstraint("'Group-E'!$A$2:$A$" + this.lengthSLinked + 1);
    dataValidation = validationHelper.createValidation(constraint, addressList);
    dataValidation.setSuppressDropDownArrow(false);
    dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
    dataValidation.setShowErrorBox(true);
    dataValidation.createErrorBox("Misentry", "Please choose a group from the dropdown menu");
    ((XSSFSheet) this.wb.getSheet(NEW_ATTRIBUTE)).addValidationData(dataValidation);
  }


  /**
   * The method gets all groups in german from the webclient
   *
   * @param wb Workbook
   * @return int length_sLinked is the length of the array. Is needed to creat the dropdown list
   * @author ikt8fe
   */
  private int WebclientGroupsG() {
    int i = 0;
    try {
      SuperGroupType[] superGroups = getAllAttrGroupsWoDeleted();
      String[] ssuperGroups = new String[superGroups.length];
      // fetch all supergroups and copy it to the array ssuperGroups (needed to get the groups)
      for (int loop = 0; loop < superGroups.length; loop++) {
        ssuperGroups[loop] = superGroups[loop].getNameG();
      }
      String[] sLinked = new String[0];
      // fetch all groups in german and copy it to the array sGroups
      for (SuperGroupType superGroup : superGroups) {
        if (superGroup.getGroups() != null) {
          String[] sGroups = new String[superGroup.getGroups().length];

          for (int count = 0; count < superGroup.getGroups().length; count++) {
            sGroups[count] = superGroup.getGroups()[count].getNameG();
          }
          this.lengthSLinked = sLinked.length;
          sLinked = Arrays.copyOf(sLinked, sLinked.length + sGroups.length);
          System.arraycopy(sGroups, 0, sLinked, this.lengthSLinked, sGroups.length);

        }
      }
      this.lengthSLinked = sLinked.length;
      // sort all group names into order, ignor upper case
      Arrays.sort(sLinked, new Comparator<String>() {

        @Override
        public int compare(final String o1, final String o2) {
          return o1.compareToIgnoreCase(o2);
        }

      });
      // all german groups get transferred into the sheet "Group-G"
      for (i = 0; i < sLinked.length; i++) {
        Sheet sheet = this.wb.getSheet("Group-G");
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(0);
        if (cell == null) {
          cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sLinked[i]);
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
    return this.lengthSLinked;
  }

  /**
   * The method creats a dropdown list
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void dropdownGroupsG() {
    DataValidation dataValidation = null;
    DataValidationConstraint constraint = null;
    DataValidationHelper validationHelper = null;
    // Dropdown list for the sheet "New Attribute"
    validationHelper = new XSSFDataValidationHelper((XSSFSheet) this.wb.getSheet(NEW_ATTRIBUTE));
    CellRangeAddressList addressList = new CellRangeAddressList(6, 25, 2, 2);
    constraint = validationHelper.createFormulaListConstraint("'Group-G'!$A$2:$A$" + this.lengthSLinked + 1);
    dataValidation = validationHelper.createValidation(constraint, addressList);
    dataValidation.setSuppressDropDownArrow(false);
    dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
    dataValidation.setShowErrorBox(true);
    dataValidation.createErrorBox("Ungültiger Wert", "Wählen Sie bitte eine Gruppe von der Dropdown-Liste");
    ((XSSFSheet) this.wb.getSheet(NEW_ATTRIBUTE)).addValidationData(dataValidation);
  }


  /**
   * The method gets all values to an attribute in english from the webclient
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void WebclientValuesE() {
    int i = 0;
    try {
      this.attributes = getAllAttributesWoDeleted();
      long[] Id = new long[this.attributes.length];
      String[] sattributes = new String[this.attributes.length];
      String[] sattributesValues = new String[2];
      int length = 0;
      int c = 1;

      // fetch all attribute id's, this is needed to get the values of an attribute
      for (int loop = 0; loop < this.attributes.length; loop++) {
        Id[loop] = this.attributes[loop].getId();
      }

      ValueList[] values = getAllAttrValuesWoDeleted(Id);
      // fetch all attributes in english
      for (int loop = 0; loop < this.attributes.length; loop++) {
        sattributes[loop] = this.attributes[loop].getNameE();
      }
      // fetch all values and refer these to an attribute
      for (int loop = 0; loop < this.attributes.length; loop++) {
        // this is needed, otherwise you get an exception in case of the attribute has no value
        if (values[loop].getValues() != null) {
          // here you get the values to an attribute
          // order: attribute,value,attribute,value, ...
          for (int count = 0; count < values[loop].getValues().length; count++) {
            sattributesValues[length] = sattributes[loop];
            sattributesValues[length + 1] = values[loop].getValues()[count].getValueE();
            length = sattributesValues.length;
            sattributesValues = Arrays.copyOf(sattributesValues, sattributesValues.length + 2);
          }
        }
        // in case there is no value
        else {
          sattributesValues[length] = sattributes[loop];
          sattributesValues[length + 1] = "No value available";
          length = sattributesValues.length;
          sattributesValues = Arrays.copyOf(sattributesValues, sattributesValues.length + 2);
        }
      }
      // transfer the array to the sheet Value-E
      // in the first column is the attribute and in the second column the value
      for (i = 0; i < (sattributesValues.length - 2); i++) {
        Sheet sheet = this.wb.getSheet("Value-E");
        if ((i % 2) == 0) {
          Row row = sheet.getRow(i + c);
          if (row == null) {
            row = sheet.createRow(i + c);
          }
          Cell cell = row.getCell(0);
          if (cell == null) {
            cell = row.createCell(0);
          }
          cell.setCellType(Cell.CELL_TYPE_STRING);
          cell.setCellValue(sattributesValues[i]);
        }
        else {
          c = c - 1;
          Row row = sheet.getRow(i + c);
          if (row == null) {
            row = sheet.createRow(i + c);
          }
          Cell cell = row.getCell(1);
          if (cell == null) {
            cell = row.createCell(1);
          }
          cell.setCellType(Cell.CELL_TYPE_STRING);
          cell.setCellValue(sattributesValues[i]);
        }
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
  }


  /**
   * The method gets all values to an attribute in german from the webclient
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void WebclientValuesG() {
    int i = 0;
    try {
      this.attributes = getAllAttributesWoDeleted();
      long[] Id = new long[this.attributes.length];
      String[] sattributes = new String[this.attributes.length];
      String[] sattributesValues = new String[2];
      int length = 0;
      int c = 1;
      // fetch all attribute id's, this is needed to get the values of an attribute
      for (int loop = 0; loop < this.attributes.length; loop++) {
        Id[loop] = this.attributes[loop].getId();
      }

      ValueList[] values = getAllAttrValuesWoDeleted(Id);
      // fetch all attributes in german
      for (int loop = 0; loop < this.attributes.length; loop++) {
        sattributes[loop] = this.attributes[loop].getNameG();
      }
      // fetch all values and refer these to an attribute
      for (int loop = 0; loop < this.attributes.length; loop++) {
        // this is needed, otherwise you get an exception in case of the attribute has no value
        if (values[loop].getValues() != null) {
          // here you get the values to an attribute
          // order: attribute,value,attribute,value, ...
          for (int count = 0; count < values[loop].getValues().length; count++) {
            sattributesValues[length] = sattributes[loop];
            sattributesValues[length + 1] = values[loop].getValues()[count].getValueG();
            length = sattributesValues.length;
            sattributesValues = Arrays.copyOf(sattributesValues, sattributesValues.length + 2);
          }
        }
        // in case there is no value
        else {
          sattributesValues[length] = sattributes[loop];
          sattributesValues[length + 1] = "Kein Wert eingetragen";
          length = sattributesValues.length;
          sattributesValues = Arrays.copyOf(sattributesValues, sattributesValues.length + 2);
        }
      }
      // transfer the array to the sheet Value-G
      // in the first column is the attribute and in the second column the value
      for (i = 0; i < (sattributesValues.length - 2); i++) {
        Sheet sheet = this.wb.getSheet("Value-G");
        if ((i % 2) == 0) {
          Row row = sheet.getRow(i + c);
          if (row == null) {
            row = sheet.createRow(i + c);
          }
          Cell cell = row.getCell(0);
          if (cell == null) {
            cell = row.createCell(0);
          }
          cell.setCellType(Cell.CELL_TYPE_STRING);
          cell.setCellValue(sattributesValues[i]);
        }
        else {
          c = c - 1;
          Row row = sheet.getRow(i + c);
          if (row == null) {
            row = sheet.createRow(i + c);
          }
          Cell cell = row.getCell(1);
          if (cell == null) {
            cell = row.createCell(1);
          }
          cell.setCellType(Cell.CELL_TYPE_STRING);
          cell.setCellValue(sattributesValues[i]);
        }
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
  }


  /**
   * The method gets all attributes in english and refer these to all attributes in german. Also it checks if the given
   * attribute is a normalized attribute or not. Also it checks which unit the given attribute has.
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void webclientAttributesEGIsNormalizedUnit() {
    int i = 0;
    int length = 0;
    int count = 0;

    try {
      this.attributes = getAllAttributesWoDeleted();
      String[] sattributes = new String[this.attributes.length];
      Boolean[] normalized = new Boolean[this.attributes.length];
      String[] unit = new String[this.attributes.length];
      // fetch all supergroups and copy it to the array ssuperGroups (needed to get the groups)
      for (int loop = 0; loop < this.attributes.length; loop++) {
        sattributes[loop] = this.attributes[loop].getNameE();
      }

      length = sattributes.length;
      sattributes = Arrays.copyOf(sattributes, sattributes.length + this.attributes.length);
      // fetch all attributes in german into the array after the english ones
      for (int loop = length; loop < sattributes.length; loop++) {
        sattributes[loop] = this.attributes[count].getNameG();
        count = count + 1;
      }
      // get true/false from every attribute
      for (int loop = 0; loop < this.attributes.length; loop++) {
        normalized[loop] = this.attributes[loop].getIsNormalized();

      }
      // get the unit from every attribute
      for (int loop = 0; loop < this.attributes.length; loop++) {
        unit[loop] = this.attributes[loop].getUnit();

      }
      // transfer the attributes in english to the first column of the sheet Attr-E-G
      for (i = 0; i < length; i++) {
        Sheet sheet = this.wb.getSheet(ATTR_E_G);
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(0);
        if (cell == null) {
          cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sattributes[i]);
      }
      count = 0;
      // transfer the attributes in german to the second column of the sheet Attr-E-G
      // length is needed because the attributes in english and in german are in the same array
      for (i = length; i < sattributes.length; i++) {
        Sheet sheet = this.wb.getSheet(ATTR_E_G);
        Row row = sheet.getRow(count + 1);
        if (row == null) {
          row = sheet.createRow(count + 1);
        }
        Cell cell = row.getCell(1);
        if (cell == null) {
          cell = row.createCell(1);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sattributes[i]);
        count++;
      }
      // transfer the status of an attribute to the third column of the sheet Attr-E-G
      for (i = 0; i < length; i++) {
        Sheet sheet = this.wb.getSheet(ATTR_E_G);
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(2);
        if (cell == null) {
          cell = row.createCell(2);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(normalized[i]);
      }
      // transfer the unit of an attribute to the fourth column of the sheet Attr-E-G
      for (i = 0; i < length; i++) {
        Sheet sheet = this.wb.getSheet(ATTR_E_G);
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(3);
        if (cell == null) {
          cell = row.createCell(3);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(unit[i]);
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
  }


  /**
   * The method getss all groups in english and refer these to all groups in german.
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void WebclientGroupsEG() {
    int i = 0;
    int count = 0;
    try {
      SuperGroupType[] superGroups = getAllAttrGroupsWoDeleted();
      String[] ssuperGroups = new String[superGroups.length];
      // fetch all groups in english into the array
      for (int loop = 0; loop < superGroups.length; loop++) {
        ssuperGroups[loop] = superGroups[loop].getNameE();
      }
      String[] sLinked = new String[0];
      // fetch all groups in english and copy it to the array sGroups
      for (SuperGroupType superGroup : superGroups) {
        if (superGroup.getGroups() != null) {
          String[] sGroups = new String[superGroup.getGroups().length];

          for (count = 0; count < superGroup.getGroups().length; count++) {
            sGroups[count] = superGroup.getGroups()[count].getNameE();
          }
          this.lengthSLinked = sLinked.length;
          // to combine the english and the german group names later
          sLinked = Arrays.copyOf(sLinked, sLinked.length + sGroups.length);
          System.arraycopy(sGroups, 0, sLinked, this.lengthSLinked, sGroups.length);

        }
      }
      int first_length = sLinked.length;
      // fetch all groups in german into the array
      for (int loop = 0; loop < superGroups.length; loop++) {
        ssuperGroups[loop] = superGroups[loop].getNameG();
      }
      // fetch all groups in german and copy it to the array sGroups
      for (SuperGroupType superGroup : superGroups) {
        if (superGroup.getGroups() != null) {
          String[] sGroups = new String[superGroup.getGroups().length];

          for (count = 0; count < superGroup.getGroups().length; count++) {
            sGroups[count] = superGroup.getGroups()[count].getNameG();
          }
          this.lengthSLinked = sLinked.length;
          // combine the english and the german group names
          sLinked = Arrays.copyOf(sLinked, sLinked.length + sGroups.length);
          System.arraycopy(sGroups, 0, sLinked, this.lengthSLinked, sGroups.length);

        }
      }
      // transfer the english group names to the first column of the sheet Group-E-G
      for (i = 0; i < first_length; i++) {
        Sheet sheet = this.wb.getSheet(GROUP_E_G);
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(0);
        if (cell == null) {
          cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sLinked[i]);
      }
      count = 0;
      // transfer the german group names to the second column of the sheet Group-E-G
      // first_length is needed because the group names in english and in german are in the same array
      for (i = first_length; i < sLinked.length; i++) {
        Sheet sheet = this.wb.getSheet(GROUP_E_G);
        Row row = sheet.getRow(count + 1);
        if (row == null) {
          row = sheet.createRow(count + 1);
        }
        Cell cell = row.getCell(1);
        if (cell == null) {
          cell = row.createCell(1);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sLinked[i]);
        count++;
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
  }


  /**
   * The method gets all values in english and refer these to all values in german.
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void WebclientValuesEG() {
    int i = 0;
    int length = 0;
    int count = 0;
    try {
      this.attributes = getAllAttributesWoDeleted();
      long[] Id = new long[this.attributes.length];
      String[] sattributesValues = new String[1];
      // fetch all id's of all attributes
      for (int loop = 0; loop < this.attributes.length; loop++) {
        Id[loop] = this.attributes[loop].getId();
      }

      ValueList[] values = getAllAttrValuesWoDeleted(Id);
      // get all values in english
      for (int loop = 0; loop < this.attributes.length; loop++) {
        // check if a value is available
        if (values[loop].getValues() != null) {
          for (count = 0; count < values[loop].getValues().length; count++) {
            sattributesValues[length] = values[loop].getValues()[count].getValueE();
            length = sattributesValues.length;
            sattributesValues = Arrays.copyOf(sattributesValues, sattributesValues.length + 1);
          }
        }
        // in case of no value is available
        else {
          sattributesValues[length] = "No value available";
          length = sattributesValues.length;
          sattributesValues = Arrays.copyOf(sattributesValues, sattributesValues.length + 1);
        }
      }
      int first_length = sattributesValues.length;
      // is needed to start at the end of the english value names
      length = sattributesValues.length;

      // get all values in german
      for (int loop = 0; loop < this.attributes.length; loop++) {
        // check if a value is available
        if (values[loop].getValues() != null) {
          for (count = 0; count < values[loop].getValues().length; count++) {
            sattributesValues[length - 1] = values[loop].getValues()[count].getValueG();
            sattributesValues = Arrays.copyOf(sattributesValues, sattributesValues.length + 1);
            length = sattributesValues.length;
          }
        }
        // in case of no value is available
        else {
          sattributesValues[length - 1] = "Kein Wert eingetragen";
          sattributesValues = Arrays.copyOf(sattributesValues, sattributesValues.length + 1);
          length = sattributesValues.length;
        }
      }
      // transfer all english value names to the first column of the sheet Value-E-G
      for (i = 0; i < (first_length - 1); i++) {
        Sheet sheet = this.wb.getSheet(VALUE_E_G);
        Row row = sheet.getRow(i + 1);
        if (row == null) {
          row = sheet.createRow((short) i + 1);
        }
        Cell cell = row.getCell(0);
        if (cell == null) {
          cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sattributesValues[i]);
      }
      count = 0;
      // transfer all german value names to the second column of the sheet Value-E-G
      for (i = first_length - 1; i < (sattributesValues.length - 1); i++) {
        Sheet sheet = this.wb.getSheet(VALUE_E_G);
        Row row = sheet.getRow(count + 1);
        if (row == null) {
          row = sheet.createRow(count + 1);
        }
        Cell cell = row.getCell(1);
        if (cell == null) {
          cell = row.createCell(1);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(sattributesValues[i]);
        count++;
      }
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
  }


  /**
   * The method hide every sheet which is not needed by the user. The method gives the sheets the "very hidden" status
   *
   * @param wb Workbook
   * @author ikt8fe
   */
  private void hide() {
    int index = 0;
    // search for the index number, this is needed because the setSheetHidden method only works with the indexnumber
    index = this.wb.getSheetIndex("Attr-E");
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex("Attr-G");
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex("Group-E");
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex("Group-G");
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex("Type");
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex("Value-E");
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex("Value-G");
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex(ATTR_E_G);
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex(GROUP_E_G);
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex(VALUE_E_G);
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex("Global Parametres");
    this.wb.setSheetHidden(index, 2);
    index = this.wb.getSheetIndex("Attr-EG");
    this.wb.setSheetHidden(index, 2);
  }


  /**
   * The method finds the current date.
   *
   * @return a string with the current date. Is needed to create the filename with the current date included.
   * @author ikt8fe
   */
  private String date() {
    // format the date into the right format
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return formatter.format(new Date());

  }

  /**
   * The method finds the current NT-User.
   *
   * @return a string with the current NT-User. Is needed to create the filename with the current NT-User included.
   * @author ikt8fe
   */
  private String name() {
    String userName = System.getProperty("user.name");
    userName = userName.toUpperCase(Locale.getDefault());

    return userName;
  }

  /**
   * The method returns all attributes available in iCDM. If no attributes were found an empty array is returned, This
   * arra has the length of 0. It is not null.
   *
   * @return an array of all the attributes
   */
  private Attribute[] getAllAttributesWoDeleted() throws Exception {
    Attribute[] wsAttributes = this.apicWsClient.getAllAttributes();
    ArrayList<Attribute> attributesWoDel = new ArrayList<>();

    // Copy the attributes from web service array to array list when
    // attribute is not deleted
    for (Attribute wsAttribute : wsAttributes) {
      if (!wsAttribute.getIsDeleted()) {
        attributesWoDel.add(wsAttribute);
      }
    }

    // Return an array of attributes, as excepted from the webservice. If the array is empty,
    // an empty String (length of 0) is returned.
    return attributesWoDel.toArray(new Attribute[attributesWoDel.size()]);
  }

  /**
   * The method returns all groups/supergroups available in iCDM. If none were found an empty array is returned, This
   * array has the length of 0. It is not null.
   *
   * @return an array of all the groups/super groups
   */
  private SuperGroupType[] getAllAttrGroupsWoDeleted() throws Exception {
    SuperGroupType[] wsGroups = this.apicWsClient.getAttrGroups();
    ArrayList<SuperGroupType> superGroupsWoDel = new ArrayList<>();

    // Copy the attributes from web service array to array list when
    // attribute is not deleted
    for (SuperGroupType wsGroup : wsGroups) {
      if (!wsGroup.getIsDeleted()) {
        superGroupsWoDel.add(wsGroup);
      }
    }

    // Return an array of attributes, as excepted from the webservice
    return superGroupsWoDel.toArray(new SuperGroupType[superGroupsWoDel.size()]);
  }

  /**
   * The method returns all values available in iCDM not marked as deleted. If none were found an empty array is
   * returned, This array has the length of 0. It is not null.
   *
   * @param ArgumentName a long array containing the attribute ids of the attributes whoose values should be returned
   * @return an array of all the values
   */
  private ValueList[] getAllAttrValuesWoDeleted(final long ids[]) throws Exception {
    ValueList attrValueLists[] = this.apicWsClient.getAttrValues(ids);

    // Loop through the ValueLists. There is one Value list for each id
    // in the array 'ids' passed to the method
    for (ValueList attrValueList : attrValueLists) {
      AttributeValue attrValues[] = attrValueList.getValues();
      ArrayList<AttributeValue> values = new ArrayList<>();

      // Loop through the attributes values for the attribute
      // of the current loop
      for (AttributeValue attrValue : attrValues) {
        if (!attrValue.getIsDeleted()) {
          values.add(attrValue);
        }
      }

      // Replace the value list with just the values that are not deleted
      attrValueList.setValues(values.toArray(new AttributeValue[values.size()]));
    }

    // Return an array of attributes, as excepted from the webservice
    return attrValueLists;
  }
}