/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDComboBox;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.DataAssessmentUtil;
import com.bosch.caltool.icdm.bo.report.compli.PdfUtil;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReportConstants;
import com.bosch.caltool.icdm.model.user.User;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;

/**
 * @author TRL1COB
 */
public class PidcAttributesPdfExport extends AbstractSimpleBusinessObject {

  /**
   * Column width of variant/sub-variant table and PIDC attributes info table
   */
  private static final int VAR_SUB_VAR_COL_WIDTH = 25;

  /**
   * Column width of PIDC attributes info table
   */
  private static final int PIDC_ATTR_COL_WIDTH = 26;

  /**
   * Column width for serial number columns
   */
  private static final int SNUM_COL_WIDTH = 20;


  /**
   * Constant to hold the folder name for PIDC attributes export
   */
  private static final String PIDC_INFO_ATTRIBUTES = "Project Information and Attributes";

  private final PidcVersionWithDetails pidcVersionWithDetails;

  private final AttrExportModel attrExportModel;

  private final Map<Long, String[]> valueAsStrMap = new HashMap<>();

  private static final int VAR_ROW_COL_NO = 3;

  private static final int SUB_VAR_ROW_COL_NO = 3;


  /**
   * Path of the base folder structure where baseline files need to be exported
   */
  private final String filepath;
  /**
   * variable to hold current line position
   */
  private double linePosition = 0;
  /**
   * holds current page object
   */
  private PDPage currentPage;
  /**
   * PD document
   */
  private PDDocument document;
  /**
   * Current stream
   */
  private PDPageContentStream currentStream;

  /**
   * Bosch Logo
   */
  private byte[] boschLogo;

  private User user;

  /**
   * Constructor
   *
   * @param serviceData Service Data
   * @param attrExp AttrExportModel
   * @param pidcVersionWithDetails PidcVersionWithDetails
   * @param filePath Fodler path to save the exported PDF
   */
  public PidcAttributesPdfExport(final ServiceData serviceData, final AttrExportModel attrExp,
      final PidcVersionWithDetails pidcVersionWithDetails, final String filePath) {
    super(serviceData);
    this.attrExportModel = attrExp;
    this.pidcVersionWithDetails = pidcVersionWithDetails;
    this.filepath = filePath;
  }

  /**
   * Create PIDC Information and Attributes PDF
   *
   * @param pidcVarId - input PidcVarId
   * @throws IOException - IO Exception
   * @throws IcdmException - ICDM Exception
   */
  public void createPidcAttrPdf(final Long pidcVarId) throws IOException, IcdmException {
    // Creating a new empty pdf document
    this.document = new PDDocument();

    // create new page
    this.currentPage = PdfUtil.createNewPage(this.document);

    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePosition();

    // Adding information to pdf document
    PDDocumentInformation info = this.document.getDocumentInformation();

    // Draw header
    PdfUtil.constructHeader(this.document, this.currentStream, this.boschLogo);

    // set created author and created date details
    info.setAuthor(this.user.getDescription());
    info.setCreationDate(Calendar.getInstance());

    // PIDC Attributes Details
    writeText("PIDC Information and Attributes Report", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    String pidcName = this.pidcVersionWithDetails.getPidcVersionInfo().getPidcVersion().getName();
    writeText(pidcName, PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 20;
    getLogger().debug("Writing PIDC Attributes Info to PDF");
    createPidcAttributesTable();

    if ((pidcVarId != null) && (CommonUtils.isNotEqual(pidcVarId, ApicConstants.NO_VARIANT_ID))) {
      // Create a new page
      createNewPage();

      writeText("Variants/Sub-Variants", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
      this.linePosition -= 20;
      getLogger().debug("Writing Variant/Sub-Variant Info to PDF");
      createVariantSubVariantTable(pidcVarId);
    }

    // Add Bosch logo as header to all pages in the dcoument
    addHeaderForPages();

    File file = DataAssessmentUtil.createFileDirectory(this.filepath, PIDC_INFO_ATTRIBUTES);

    String fullPdfFilePath = file.getAbsolutePath() + File.separator + "PIDCAttrReport.pdf";

    // Delete the PDF report if it already exists
    File outputFile = new File(fullPdfFilePath);
    if (outputFile.exists()) {
      Files.delete(outputFile.toPath());
      getLogger().info("Existing PDF file deleted to create new report : {}", outputFile.getPath());
    }

    getLogger().debug("Updating PDF to PDF/A Standard");
    // Convert the PDF into PDF/A
    PdfUtil.setPdfAStandard(this.document);

    getLogger().debug("Writing data to PDF file");
    // Save pdf document
    this.document.save(fullPdfFilePath);

    // close document
    this.document.close();

    getLogger().info("Project Information and attributes PDF created. Path : {}", fullPdfFilePath);

  }

  /**
   * Create a table with PIDC Attribute details
   *
   * @throws IOException
   */
  private void createPidcAttributesTable() throws IOException {
    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH_A6 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // List of column headers for the PIDC Attributes table
    List<String> pidcAttrHdrs = new ArrayList<>();
    pidcAttrHdrs.addAll(Arrays.asList(PidcPdfExportColumn.HDR_PIDC_ATTR));

    // add headers to table
    PdfUtil.addHeaderRowToTable(baseTable, pidcAttrHdrs, PIDC_ATTR_COL_WIDTH);

    PidcVersionBO pidcVersionBO = new PidcVersionBO(this.pidcVersionWithDetails);

    SortedSet<PidcVersionAttribute> pidcAttrSet =
        new TreeSet<>((p1, p2) -> pidcVersionBO.compare(p1, p2, ApicConstants.SORT_SUPERGROUP));

    // Remove invisible attributes from pidcAttrSet
    this.pidcVersionWithDetails.getPidcVersionAttributeMap().entrySet().forEach(entry -> {
      if (!this.pidcVersionWithDetails.getPidcVersInvisibleAttrSet().contains(entry.getValue().getAttrId())) {
        pidcAttrSet.add(entry.getValue());
      }
    });

    // Create columns with values in PIDC Attributes table
    int serialNum = 0;
    for (IProjectAttribute pidcAttr : pidcAttrSet) {
      Row<PDPage> row = baseTable.createRow(2f);
      serialNum++;
      for (int col = 0; col < (pidcAttrHdrs.size()); col++) {
        createPidcAttrColumns(pidcAttrHdrs, pidcAttr, row, col, serialNum);
      }
    }


    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }

  }

  /**
   * Create columns in PIDC attributes table
   *
   * @param workbook
   * @param tempAttrSheet
   * @param pidcAttrHdrs
   * @param pidcAttrSheet
   * @param pidcAttr
   * @param row
   * @param col
   * @param serialNum
   * @throws IOException
   */
  private void createPidcAttrColumns(final List<String> pidcAttrHdrs, final IProjectAttribute pidcAttr,
      final Row<PDPage> row, final int col, final int serialNum)
      throws IOException {

    String cellValue;
    boolean linkFlag = false;
    boolean applyColor = false;
    Color colorToApply = null;
    Attribute attr = this.pidcVersionWithDetails.getAllAttributeMap().get(pidcAttr.getAttrId());

    if (col == 0) {
      cellValue = String.valueOf(serialNum);
      // Populate values to the columns in table
      PdfUtil.addColumnValsToTable(row, linkFlag, applyColor, colorToApply, cellValue, SNUM_COL_WIDTH,
          this.currentPage);
      return;
    }
    else if (pidcAttrHdrs.get(col).equals(DataAssessmentReportConstants.USED)) {
      cellValue = ApicConstants.PROJ_ATTR_USED_FLAG.getType(pidcAttr.getUsedFlag()).getUiType();
    }
    else if (pidcAttrHdrs.get(col).equals(DataAssessmentReportConstants.VALUE)) {
      createPidcAttrValueCol(pidcAttr, row, col);
      return;
    }
    else if (pidcAttrHdrs.get(col).equals(DataAssessmentReportConstants.SPEC_LINK)) {
      cellValue = pidcAttr.getSpecLink();
      linkFlag = true;
    }
    else if (pidcAttrHdrs.get(col).equals(DataAssessmentReportConstants.ATTR_CREATED_DATE)) {
      cellValue = getFormattedAttrDate(attr);
    }
    else {
      final StringBuilder isAttrValDeleted = new StringBuilder();
      final StringBuilder isAttrValCleared = new StringBuilder();
      cellValue = PidcPdfExportColumn.getInstance().getColTxtFromPrjAttr(this.pidcVersionWithDetails, col, pidcAttr,
          isAttrValDeleted, isAttrValCleared);
      if (Boolean.parseBoolean(isAttrValDeleted.toString())) {
        applyColor = true;
        colorToApply = Color.RED;
      }
      else if ((isAttrValCleared.length() > 0) && !Boolean.parseBoolean(isAttrValCleared.toString())) {
        applyColor = true;
        colorToApply = Color.ORANGE;
      }
    }

    // Populate values to the columns in table
    PdfUtil.addColumnValsToTable(row, linkFlag, applyColor, colorToApply, cellValue, PIDC_ATTR_COL_WIDTH,
        this.currentPage);
  }

  /**
   * Create value column in project id card attribute sheet
   *
   * @param workbook
   * @param pidcAttr
   * @param pidcAttrSheet
   * @param row
   * @param colIndx
   * @param tempAttrSheet
   * @throws IOException
   */
  private void createPidcAttrValueCol(final IProjectAttribute pidcAttr, final Row<PDPage> row, final int colIndx)
      throws IOException {
    boolean linkFlag = false;
    boolean applyColor = false;
    Color colorToApply = null;
    boolean dropDown = false;

    Attribute attr = this.pidcVersionWithDetails.getAllAttributeMap().get(pidcAttr.getAttrId());
    String[] valList = getValuesAsStrArr(attr);

    StringBuilder isAttrValDeleted = new StringBuilder();
    StringBuilder isAttrValCleared = new StringBuilder();

    String cellValue = PidcPdfExportColumn.getInstance().getColTxtFromPrjAttr(this.pidcVersionWithDetails, colIndx,
        pidcAttr, isAttrValDeleted, isAttrValCleared);

    // Apply text color to deleted attr and not cleared attr
    if (Boolean.parseBoolean(isAttrValDeleted.toString())) {
      applyColor = true;
      colorToApply = Color.RED;
    }
    else if ((isAttrValCleared.length() > 0) && !Boolean.parseBoolean(isAttrValCleared.toString())) {
      applyColor = true;
      colorToApply = Color.ORANGE;
    }

    AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());

    if (((attr.getLevel() > 0) || (attr.getLevel().intValue() == ApicConstants.PROJECT_NAME_ATTR)) ||
        (valList.length == 0) ||
        (cellValue.equalsIgnoreCase(DataAssessmentReportConstants.VARIANT) ||
            cellValue.equalsIgnoreCase(DataAssessmentReportConstants.SUB_VARIANT)) ||
        ((valList.length == 1) && (null != valList[0]) && valList[0].equalsIgnoreCase(cellValue))) {
      if ((valueType == AttributeValueType.HYPERLINK) &&
          (!cellValue.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME)) &&
          (!cellValue.equals(ApicConstants.VARIANT_ATTR_DISPLAY_NAME))) {
        linkFlag = true;
      }
    }
    else if ((valList.length > 0)) {
      if (valueType == AttributeValueType.HYPERLINK) {
        linkFlag = true;
      }
      else {
        dropDown = true;
      }
    }

    // Populate values to the columns in table
    Cell<PDPage> cell = PdfUtil.addColumnValsToTable(row, linkFlag, applyColor, colorToApply, cellValue,
        PIDC_ATTR_COL_WIDTH, this.currentPage);

    // Create Dropdown for multiple values with celValue as the default val selected
    if (dropDown) {
      createDropDowns(cell.getWidth(), cell.getBottomPadding(), cell.getLeftPadding(), cell.getRightPadding(), valList,
          cellValue);
    }
  }

  /**
   * Create a table with Variant/Sub-Variant details
   *
   * @param pidcVarId as pidcvarId
   * @throws IOException
   * @throws IcdmException
   */
  private void createVariantSubVariantTable(final Long pidcVarId) throws IOException, IcdmException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH_A6 - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // List of column headers for the Variant/Sub-Variant table
    List<String> varSubVarHdrs = Arrays.asList(PidcPdfExportColumn.HDR_VAR_SVAR);

    // add Header data to table
    PdfUtil.addHeaderRowToTable(baseTable, varSubVarHdrs, VAR_SUB_VAR_COL_WIDTH);

    PidcVariant pidcVariant = new PidcVariantLoader(getServiceData()).getDataObjectByID(pidcVarId);


    // Create a Header row for Variants
    createVarHdrRow(baseTable, varSubVarHdrs, pidcVariant);

    PidcVariantBO pidcVariantBo = new PidcVariantBO(getServiceData(), this.pidcVersionWithDetails, pidcVariant);

    SortedSet<PidcVariantAttribute> varattributesWithInvisible =
        pidcVariantBo.getAttributes(ApicConstants.SORT_SUPERGROUP);

    SortedSet<PidcVariantAttribute> varattributes = new TreeSet<>();
    // Remove invisible attributes
    varattributesWithInvisible.forEach(varAttr -> {
      if (!this.pidcVersionWithDetails.getVariantInvisbleAttributeMap().get(pidcVariant.getId())
          .contains(varAttr.getAttrId())) {
        varattributes.add(varAttr);
      }
    });

    int serialNum = 1;
    for (IProjectAttribute varAttr : varattributes) {
      Row<PDPage> row = baseTable.createRow(2f);
      serialNum++;
      for (int col = 0; col < (varSubVarHdrs.size()); col++) {
        createVariantColumns(pidcVariant, varSubVarHdrs, varAttr, col, row, serialNum);
      }
    }
    createSubVariantAttributes(baseTable, pidcVariant, varSubVarHdrs, serialNum);

    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
  }

  /**
   * Create a header row for Variants
   *
   * @param baseTable
   * @param varAndSubVarAttrHdrs
   * @param pidcVariant
   */
  private void createVarHdrRow(final BaseTable baseTable, final List<String> varAndSubVarAttrHdrs,
      final PidcVariant pidcVariant) {

    // To populate first column with serial number
    String cellValue = "1";
    Row<PDPage> row = baseTable.createRow(2f);
    Cell<PDPage> cell = row.createCell(SNUM_COL_WIDTH, cellValue);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);

    int varCol = 1;
    while (varCol < (varAndSubVarAttrHdrs.size())) {

      if (varCol == 2) {
        cellValue = "Variant";
      }
      else if (varCol == VAR_ROW_COL_NO) {
        cellValue = pidcVariant.getName();
      }
      else if (varCol == (varAndSubVarAttrHdrs.size() - 2)) {
        cellValue = String.valueOf(pidcVariant.getId());
      }
      else {
        cellValue = DataAssessmentReportConstants.EMPTY_STRING;
      }

      // Create headers cells
      cell = row.createCell(VAR_SUB_VAR_COL_WIDTH, cellValue);
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);

      varCol++;
    }

  }

  /**
   * Create columns in Variant/Sub-Variant table
   *
   * @param pidcVariant
   * @param hdrList
   * @param varAttr
   * @param colIndx
   * @param row
   * @param serialNum
   * @throws IOException
   */
  private void createVariantColumns(final PidcVariant pidcVariant, final List<String> hdrList,
      final IProjectAttribute varAttr, final int colIndx, final Row<PDPage> row, final int serialNum)
      throws IOException {

    boolean linkFlag = false;
    boolean applyColor = false;
    Color colorToApply = null;

    StringBuilder isAttrValDeleted = new StringBuilder();
    StringBuilder isAttrValCleared = new StringBuilder();

    String cellValue = PidcPdfExportColumn.getInstance().getColTxtFromVarAttr(this.pidcVersionWithDetails, colIndx,
        varAttr, isAttrValDeleted, isAttrValCleared);

    if (Boolean.parseBoolean(isAttrValDeleted.toString())) {
      applyColor = true;
      colorToApply = Color.RED;
    }
    else if ((isAttrValCleared.length() > 0) && !(Boolean.parseBoolean(isAttrValCleared.toString()))) {
      applyColor = true;
      colorToApply = Color.ORANGE;
    }

    // Populate serial number
    if (colIndx == 0) {
      cellValue = String.valueOf(serialNum);
      // Populate values to the columns in table
      PdfUtil.addColumnValsToTable(row, linkFlag, applyColor, colorToApply, cellValue, SNUM_COL_WIDTH,
          this.currentPage);
      return;
    }

    // Populate column values
    if (colIndx == 1) {
      cellValue = pidcVariant.getName();
    }
    else if (hdrList.get(colIndx).equals(DataAssessmentReportConstants.VALUE)) {
      createVariantValueCol(cellValue, varAttr, row);
      return;
    }
    else if (hdrList.get(colIndx).equals(DataAssessmentReportConstants.SPEC_LINK)) {
      linkFlag = true;
    }

    // Populate values to the columns in table
    PdfUtil.addColumnValsToTable(row, linkFlag, applyColor, colorToApply, cellValue, VAR_SUB_VAR_COL_WIDTH,
        this.currentPage);

  }


  /**
   * Populate values to Value columns of variant
   *
   * @param cellValue
   * @param varAttr
   * @param row
   * @throws IOException
   */
  private void createVariantValueCol(final String cellValue, final IProjectAttribute varAttr, final Row<PDPage> row)
      throws IOException {
    boolean linkFlag = false;
    boolean dropDown = false;

    Attribute attr = this.pidcVersionWithDetails.getAllAttributeMap().get(varAttr.getAttrId());
    String[] valList = getValuesAsStrArr(attr);
    AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());
    if ((valList.length > 0) && (valueType != AttributeValueType.HYPERLINK)) {
      if (attr.getLevel() > 0) {
        if ((attr.getLevel().intValue() != ApicConstants.VARIANT_CODE_ATTR)) {
          dropDown = true;
        }
      }
      else {
        if (!cellValue.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME)) {
          dropDown = true;
        }
      }
    }

    if ((valueType == AttributeValueType.HYPERLINK) && !cellValue.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME)) {
      linkFlag = true;
    }

    Cell<PDPage> cell =
        PdfUtil.addColumnValsToTable(row, linkFlag, false, null, cellValue, VAR_SUB_VAR_COL_WIDTH, this.currentPage);

    // Create Dropdown for multiple values with celValue as the default val selected
    if (dropDown) {
      createDropDowns(cell.getWidth(), cell.getBottomPadding(), cell.getLeftPadding(), cell.getRightPadding(), valList,
          cellValue);
    }

  }

  /**
   * Add Sub variant related info to table
   *
   * @param baseTable
   * @param pidcVariant
   * @param varNSVarAtrHdr
   * @param serialNum
   * @throws IcdmException
   * @throws IOException
   */
  private void createSubVariantAttributes(final BaseTable baseTable, final PidcVariant pidcVariant,
      final List<String> varNSVarAtrHdr, final int serialNum)
      throws IcdmException, IOException {

    int serNumb = serialNum;
    PidcVariantBO pidcVariantBo = new PidcVariantBO(getServiceData(), this.pidcVersionWithDetails, pidcVariant);
    final SortedSet<PidcSubVariant> subVariantsSet = pidcVariantBo.getSubVariantsSet();

    for (PidcSubVariant projSubVar : subVariantsSet) {

      serNumb++;
      // Create a Header row for Sub Variants
      createSubVarHdrRow(baseTable, varNSVarAtrHdr, projSubVar, serNumb);


      // Sorting subvariant attributes
      PidcSubVariantBO subVarHandler = new PidcSubVariantBO(getServiceData(), this.pidcVersionWithDetails, projSubVar);

      SortedSet<PidcSubVariantAttribute> subVarAttrSetWithInvisibleAttr =
          subVarHandler.getAttributes(ApicConstants.SORT_SUPERGROUP);

      SortedSet<PidcSubVariantAttribute> subVarAttrSet = new TreeSet<>();
      // Remove invisible attributes
      subVarAttrSetWithInvisibleAttr.forEach(subvarAttr -> {
        if (!this.pidcVersionWithDetails.getSubVariantInvisbleAttributeMap().get(projSubVar.getId())
            .contains(subvarAttr.getAttrId())) {
          subVarAttrSet.add(subvarAttr);
        }
      });

      // Populate sub variant attributes info to table
      for (IProjectAttribute subVarAttr : subVarAttrSet) {
        Row<PDPage> row = baseTable.createRow(2f);
        serNumb++;
        for (int col = 0; col < (varNSVarAtrHdr.size()); col++) {
          createSubVarColumns(varNSVarAtrHdr, subVarAttr, row, col, serNumb);
        }
      }
    }
  }

  /**
   * Create a header row for Sub variant
   *
   * @param baseTable
   * @param varAndSubVarAttrHdrs
   * @param projSubVar
   * @param serNumb
   */
  private void createSubVarHdrRow(final BaseTable baseTable, final List<String> varAndSubVarAttrHdrs,
      final PidcSubVariant projSubVar, final int serNumb) {

    Row<PDPage> row = baseTable.createRow(2f);
    Cell<PDPage> cell = row.createCell(SNUM_COL_WIDTH, String.valueOf(serNumb));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);

    String cellValue;
    int subVarCol = 1;
    while (subVarCol < (varAndSubVarAttrHdrs.size())) {
      if (subVarCol == 2) {
        cellValue = "SubVariant";
      }
      else if (subVarCol == SUB_VAR_ROW_COL_NO) {
        cellValue = projSubVar.getName();
      }
      else if (subVarCol == (varAndSubVarAttrHdrs.size() - 2)) {
        cellValue = String.valueOf(projSubVar.getId());
      }
      else {
        cellValue = DataAssessmentReportConstants.EMPTY_STRING;
      }

      // Create headers cells
      cell = row.createCell(VAR_SUB_VAR_COL_WIDTH, cellValue);
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);

      subVarCol++;
    }

  }


  /**
   * Create Sub variant columns in table
   *
   * @param varNSVarHdrs
   * @param projSubVar
   * @param subVarAttr
   * @param row
   * @param col
   * @param serialNum
   * @throws IOException
   */
  private void createSubVarColumns(final List<String> varNSVarHdrs, final IProjectAttribute subVarAttr,
      final Row<PDPage> row, final int col, final int serialNum)
      throws IOException {

    boolean linkFlag = false;
    boolean applyColor = false;
    Color colorToApply = null;

    if (col == 0) {
      // Populate values to serial number column in table
      Cell<PDPage> cell = row.createCell(SNUM_COL_WIDTH, String.valueOf(serialNum));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(6);
    }
    else {
      // Populate values to other columns in table
      StringBuilder isAttrValDeleted = new StringBuilder();
      StringBuilder isAttrValCleared = new StringBuilder();

      String cellValue = PidcPdfExportColumn.getInstance().getColTxtFromSubVarAttr(this.pidcVersionWithDetails, col,
          subVarAttr, isAttrValDeleted, isAttrValCleared);

      if (Boolean.parseBoolean(isAttrValDeleted.toString())) {
        applyColor = true;
        colorToApply = Color.RED;
      }
      else if ((isAttrValCleared.length() > 0) && !Boolean.parseBoolean(isAttrValCleared.toString())) {
        applyColor = true;
        colorToApply = Color.ORANGE;
      }

      if (col == (varNSVarHdrs.size() - 1)) {
        cellValue = String.valueOf(subVarAttr.getAttrId());
      }
      else if (varNSVarHdrs.get(col).equals(DataAssessmentReportConstants.VALUE)) {
        createSubVarValueCol(subVarAttr, row, cellValue);
        return;
      }
      else if (varNSVarHdrs.get(col).equals(DataAssessmentReportConstants.SPEC_LINK)) {
        linkFlag = true;
      }

      // Populate values to the columns in table
      PdfUtil.addColumnValsToTable(row, linkFlag, applyColor, colorToApply, cellValue, VAR_SUB_VAR_COL_WIDTH,
          this.currentPage);
    }

  }

  /**
   * Populate values to value column in Sub variant
   *
   * @param subVarAttr
   * @param row
   * @param col
   * @param cellValue
   * @throws IOException
   */
  private void createSubVarValueCol(final IProjectAttribute subVarAttr, final Row<PDPage> row, final String cellValue)
      throws IOException {

    boolean dropDown = false;
    boolean linkFlag = false;

    Attribute attr = this.pidcVersionWithDetails.getAllAttributeMap().get(subVarAttr.getAttrId());
    String[] valList = getValuesAsStrArr(attr);
    AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());

    if ((valueType == AttributeValueType.HYPERLINK) && !cellValue.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME)) {
      linkFlag = true;
    }

    if ((valList.length > 0) && (valueType != AttributeValueType.HYPERLINK) &&
        (attr.getLevel().intValue() != ApicConstants.SUB_VARIANT_CODE_ATTR)) {
      dropDown = true;
    }


    // populate valus to columns in table
    Cell<PDPage> cell =
        PdfUtil.addColumnValsToTable(row, linkFlag, false, null, cellValue, VAR_SUB_VAR_COL_WIDTH, this.currentPage);

    // Create Dropdown for multiple values with celValue as the default val selected
    if (dropDown) {
      createDropDowns(cell.getWidth(), cell.getBottomPadding(), cell.getLeftPadding(), cell.getRightPadding(), valList,
          cellValue);
    }
  }


  /**
   * Method to format the attribute creation date
   *
   * @param attr
   * @return
   */
  private String getFormattedAttrDate(final Attribute attr) {
    String cellValue;
    Date date = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");
    if (attr.getCreatedDate() != null) {
      try {
        date = dateFormat.parse(attr.getCreatedDate());
      }
      catch (ParseException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    cellValue = sdf.format(date);
    return cellValue;
  }

  /**
   * Method to get the Attribute values a string array
   *
   * @param attr
   * @return
   */
  private String[] getValuesAsStrArr(final Attribute attr) {
    if (!this.valueAsStrMap.containsKey(attr.getId())) {
      final SortedSet<AttributeValue> attrValSet = getAttrValues(attr.getId());
      String[] valArr = new String[attrValSet.size()];
      int indx = 0;
      for (AttributeValue val : attrValSet) {
        valArr[indx] = val.getNameRaw();
        indx++;
      }
      this.valueAsStrMap.put(attr.getId(), valArr);
    }
    return this.valueAsStrMap.get(attr.getId());
  }

  /**
   * Method to get attribute values from AttrExportModel
   *
   * @param attrId
   * @return
   */
  private SortedSet<AttributeValue> getAttrValues(final Long attrId) {
    return this.attrExportModel.getAllAttrValuesMap().get(attrId).values().stream().filter(a -> !a.isDeleted())
        .collect(Collectors.toCollection(TreeSet::new));
  }

  /**
   * Write content to PDF
   *
   * @param content - String content to be written
   * @param font - font
   * @param fontSize - size
   * @param linePosition1 - position to write
   * @throws IOException - exception
   */
  private void writeText(final String content, final PDType1Font font, final int fontSize, final double linePosition1)
      throws IOException {
    // if line position is beyond bottom margin of page, create new page and add content
    if (PdfUtil.isLinePositionWithinLimits(linePosition1)) {
      PDStreamUtils.write(this.currentStream, content, font, fontSize, (float) PdfUtil.LEFT_MARGIN,
          (float) linePosition1, Color.BLACK);
    }
    else {
      createNewPage();

      PDStreamUtils.write(this.currentStream, content, font, fontSize, (float) PdfUtil.LEFT_MARGIN,
          (float) this.linePosition, Color.BLACK);
    }
  }


  /**
   * Create a new page in PDF
   *
   * @throws IOException
   */
  private void createNewPage() throws IOException {
    // create new page
    this.currentPage = PdfUtil.createNewPage(this.document);

    // Close current stream
    this.currentStream.close();

    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePosition();
  }

  /**
   * Method to add header for all created pages in document
   *
   * @param doc - document
   * @throws IOException
   */
  private void addHeaderForPages() throws IOException {
    this.currentStream.close();
    // Iterate through all pages in document
    for (PDPage page : this.document.getPages()) {
      this.currentStream = new PDPageContentStream(this.document, page, AppendMode.APPEND, false);
      PdfUtil.constructHeader(this.document, this.currentStream, this.boschLogo);
      this.currentStream.close();
    }
  }

  /**
   * Create a particular cell in table as dropdown
   *
   * @param xStart
   * @param yStart
   * @param xEnd
   * @param yEnd
   * @param values
   * @param defValue
   * @throws IOException
   */
  private void createDropDowns(final float xStart, final float yStart, final float xEnd, final float yEnd,
      final String[] values, final String defValue)
      throws IOException {

    PDAcroForm acroForm = new PDAcroForm(this.document);
    this.document.getDocumentCatalog().setAcroForm(acroForm);

    PDComboBox dropdown = new PDComboBox(acroForm);
    dropdown.setOptions(Arrays.asList(values));
    dropdown.setDefaultValue(defValue);
    acroForm.getFields().add(dropdown);

    PDAnnotationWidget widget = dropdown.getWidgets().get(0);
    PDRectangle rect = new PDRectangle(xStart, yStart, xEnd, yEnd);
    widget.setRectangle(rect);

    this.currentPage.getAnnotations().add(widget);

  }

  /**
   * @param boschLogo the boschLogo to set
   */
  public void setBoschLogo(final byte[] boschLogo) {
    this.boschLogo = boschLogo;
  }

  /**
   * @param user the user to set
   */
  public void setUser(final User user) {
    this.user = user;
  }

}
