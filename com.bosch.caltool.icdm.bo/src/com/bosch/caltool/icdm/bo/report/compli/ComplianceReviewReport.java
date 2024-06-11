/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.compli;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.compli.CompliRvwInputVcdmDst;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QSSD_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewOutputData;
import com.bosch.caltool.icdm.model.cdr.CompliRvwMetaDataWithDstInp;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.User;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.utils.PDStreamUtils;

/**
 * @author pdh2cob
 */
public class ComplianceReviewReport extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String PARAMETER_NAME = "Parameter Name";
  /**
   *
   */
  private static final String PIDC_VARIANT = "PIDC Variant";
  /**
   *
   */
  private static final String FC_NAME = "FC Name";
  /**
   *
   */
  private static final String CHECK_RESULT = "Check Result";
  /**
   *
   */
  private static final String NOT_OK_CONST = "Not Ok";
  /**
   * Input model for compliance report
   */
  private final ComplianceReportInputModel inputModel;
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

  private byte[] boschLogo;

  private User user;

  private boolean isVariantPresent;

  private static final String NOT_APPLICABLE = "n.a";
  /**
   * ket - pidc version/variant id, value - pidc version/variant name
   */
  private final Map<Long, String> pidcElementNameMap = new HashMap<>();

  private String pidcVersionName;

  /**
   * true , when Project Organization is BEG
   */
  private boolean projectOrgBEG;

  /**
   * Key - hex file index, value dst id
   */
  private Map<String, Long> hexFileDstIdMap = new HashMap<>();

  /**
   * Common param table loader
   */
  private final CommonParamLoader commonParamLoader = new CommonParamLoader(getServiceData());

  /**
   * Message loader
   */
  private final MessageLoader messageLoader = new MessageLoader(getServiceData());

  private static final String COMPLI_REPORT_HINT = "COMPLI_REPORT_HINT";

  private static final String COMPLI_REPORT_QSSD_HINT = "COMPLI_REPORT_QSSD_HINT";

  private static final String COMPLI_REVIEW = "COMPLI_REVIEW";

  boolean isQssdParamPresent = false;

  // column width of result tables

  // OK

  private static final float S_NO_COL_WIDTH = 8;

  private static final float OK_FC_NAME_COL_WIDTH = 45;

  private static final float OK_PARAM_NAME_COL_WIDTH = 45;

  private static final float OK_RESULT_COL_WIDTH = 15;

  // c-ssd

  private static final float C_SSD_FC_NAME_COL_WIDTH = 20;

  private static final float C_SSD_PARAM_NAME_COL_WIDTH = 30;

  private static final float C_SSD_RESULT_COL_WIDTH = 10;

  private static final float C_SSD_ACTIVE_COL_WIDTH = 5;

  private static final float C_SSD_NOT_ACTIVE_COL_WIDTH = 5;

  private static final float C_SSD_REASON_COL_WIDTH = 35;

  // SSD2RV

  private static final float SSD2RV_FC_NAME_COL_WIDTH = 20;

  private static final float SSD2RV_PARAM_NAME_COL_WIDTH = 30;

  private static final float SSD2RV_RESULT_COL_WIDTH = 10;

  private static final float SSD2RV_REASON_COL_WIDTH = 45;

  // SSD2RV signature

  private static final float SSD2RV_NO_FC_COL_WIDTH = 17;

  private static final double SSD2RV_SIGN_1_COL_WIDTH = 25;

  private static final double SSD2RV_SIGN_2_COL_WIDTH = 25;

  private static final float SSD2RV_CODEX_CASE_COL_WIDTH = 18;

  private static final double SSD2RV_SIGN_3_COL_WIDTH = 25;

  // Q-SSD

  private static final float QSSD_FC_NAME_COL_WIDTH = 15;

  private static final float QSSD_PARAM_NAME_COL_WIDTH = 20;

  private static final float QSSD_CHECK_RESULT_COL_WIDTH = 15;

  private static final double QSSD_BOSCH_COL_WIDTH = 15;

  private static final double QSSD_CUST_THIRD_COL_WIDTH = 15;

  private static final float QSSD_REASON_COL_WIDTH = 17;

  // Q-SSD signature

  private static final float QSSD_DEV_NO_COL_WIDTH = 17;

  private static final double QSSD_SIGN_1_COL_WIDTH = 25;

  private static final double QSSD_SIGN_2_COL_WIDTH = 25;

  private static final double QSSD_SIGN_3_COL_WIDTH = 18;

  private static final float QSSD_SIGN_COL_WIDTH = 25;


  /**
   * @param serviceData service Data
   * @param repInputModel Input model for compliance report
   */
  public ComplianceReviewReport(final ServiceData serviceData, final ComplianceReportInputModel repInputModel) {
    super(serviceData);
    this.inputModel = repInputModel;
    this.isQssdParamPresent = isQssdParamPresent();
  }


  /**
   * @param isNonSDOMBCRelease
   * @return full PDF File Path
   * @throws IOException error while writing PDF file
   * @throws IcdmException exception
   * @throws DataException error while retriving data from iCDM
   */
  public String createPdf(final boolean isNonSDOMBCRelease) throws IOException, IcdmException {
    getLogger().info("Creating PDF report...");

    CompliReviewInputMetaData inputMetaData = this.inputModel.getCompliReviewInputMetaData();


    if (inputMetaData instanceof CompliRvwMetaDataWithDstInp) {
      // DST meta data
      CompliRvwMetaDataWithDstInp metaDataDST = (CompliRvwMetaDataWithDstInp) inputMetaData;
      this.hexFileDstIdMap.put("1", metaDataDST.getVcdmDstId());
    }
    else {

      // webflow meta data
      if (CommonUtils.isNotEmpty(inputMetaData.getDstMap())) {
        CompliRvwInputVcdmDst dstFiller = new CompliRvwInputVcdmDst(inputMetaData, getServiceData());
        dstFiller.fillDstMap();
        this.hexFileDstIdMap = dstFiller.getHexFileDstIdMap();
      }

    }
    // Creating a new empty pdf document
    this.document = new PDDocument();

    // get bosch logo, requesting user details
    this.boschLogo = getBoschLogo();
    this.user = new UserLoader(getServiceData()).getDataObjectByID(getServiceData().getUserId());

    // create summary page
    getLogger().debug("Adding summary page");
    createSummaryPage(isNonSDOMBCRelease);

    // Construct CSSD pages
    getLogger().debug("Adding CSSD pages");
    createNewPage();
    createOtherPages(CDRConstants.COMPLI_RESULT_FLAG.CSSD);

    // Construct SSD2Rv pages
    getLogger().debug("Adding SSD2Rv pages");
    createNewPage();
    createOtherPages(CDRConstants.COMPLI_RESULT_FLAG.SSD2RV);


    if (this.isQssdParamPresent) {
      // add condition for qssd check
      getLogger().debug("Adding Q-SSD pages");
      createNewPage();
      createOtherQssdPages(CDRConstants.QSSD_RESULT_FLAG.QSSD);
    }

    // Construct OK pages
    getLogger().debug("Adding OK pages");
    createNewPage();
    createOtherPages(CDRConstants.COMPLI_RESULT_FLAG.OK);

    if (this.isQssdParamPresent) {
      // add condition for qssd check
      getLogger().debug("Adding Q-SSD OK pages");
      createNewPage();
      createOtherQssdPages(CDRConstants.QSSD_RESULT_FLAG.OK);
    }

    String fullPdfFilePath = this.inputModel.getDestFilePath();

    // delete report file if it already exists
    File file = new File(fullPdfFilePath);
    if (file.exists()) {
      Files.delete(file.toPath());
      getLogger().info("Existing Compli PDF file deleted to create new report : {}", file.getPath());
    }

    getLogger().debug("Writing data to report file");
    // Save pdf document
    this.document.save(fullPdfFilePath);

    // close document
    this.document.close();

    getLogger().info("Compliance Review Report PDF created. Path : {}", fullPdfFilePath);

    return fullPdfFilePath;
  }


  /**
   * @param isNonSDOMBCRelease
   * @throws IOException
   */
  private void createSummaryPage(final boolean isNonSDOMBCRelease) throws IOException, IcdmException {
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

    // set craeted author and created date details
    info.setAuthor(this.user.getDescription());
    info.setCreationDate(Calendar.getInstance());

    writeText("SSD Compliance Report", PDType1Font.HELVETICA_BOLD, 20, this.linePosition);

    this.linePosition -= 20;

    writeText("(In case of deviation : Superior Release)", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);

    this.linePosition -= 20;

    writeText("HEX file(s) have been reviewed with latest compliance rules.", PDType1Font.HELVETICA, 12,
        this.linePosition);
    this.linePosition -= 20;

    // generated by, date time
    createUserInfoTable(this.user.getDescription());

    // review summary
    writeText("Review Summary", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    // get pver name
    StringBuilder pverNameBuilder = new StringBuilder();
    pverNameBuilder.append(this.inputModel.getCompliReviewInputMetaData().getPverName());
    if (CommonUtils.isNotEmptyString(this.inputModel.getCompliReviewInputMetaData().getPverVariant()) &&
        CommonUtils.isNotEmptyString(this.inputModel.getCompliReviewInputMetaData().getPverRevision())) {
      pverNameBuilder.append(" / ").append(this.inputModel.getCompliReviewInputMetaData().getPverVariant())
          .append(" ; ").append(this.inputModel.getCompliReviewInputMetaData().getPverRevision());
    }


    String pidcElement;
    if (CommonUtils.isNotEmpty(this.inputModel.getPidcElementNameMap())) {
      fillDetails();
      pidcElement = this.pidcVersionName;
    }
    else {
      pidcElement = NOT_APPLICABLE;
    }

    // create table with common information for all HEX files
    createReviewInputInfoTable(pidcElement, pverNameBuilder.toString(), isNonSDOMBCRelease);

    writeText("HEX files reviewed", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    writeText("Compliance usecase", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
    this.linePosition -= 20;

    // create review summary table
    createReviewSummaryTable();

    if ((PdfUtil.PAGE_HEIGHT - this.linePosition) < 100) {
      this.linePosition = PdfUtil.resetLinePosition();
    }

    if (this.isQssdParamPresent) {
      writeText("Q-SSD usecase", PDType1Font.HELVETICA_BOLD, 10, this.linePosition);
      this.linePosition -= 20;
      createQssdReviewSummaryTable();
    }

    if ((PdfUtil.PAGE_HEIGHT - this.linePosition) < 100) {
      this.linePosition = PdfUtil.resetLinePosition();
    }

    if (CommonUtils.isNotEmpty(this.inputModel.getAttrValueModMap())) {
      createAttrDepnSummaryTable();
    }

    // add header for all created pages
    addHeaderForPages();

  }

  /**
   * @throws DataException
   */
  private void fillDetails() throws DataException {
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    Long pidcVersId = null;
    // Project Organisation is set to Others as default
    this.projectOrgBEG = false;

    for (Long elementId : this.inputModel.getCompliReviewInputMetaData().getHexFilePidcElement().values()) {
      String name = "";

      // if variants are given as input, use variant object to get pidc version name
      if (varLoader.isValidId(elementId)) {
        PidcVariant var = varLoader.getDataObjectByID(elementId);
        name = var.getName();
        this.isVariantPresent = true;
        pidcVersId = var.getPidcVersionId();
        // to be used to display in table
        this.pidcVersionName = versLoader.getDataObjectByID(pidcVersId).getName();

      }
      // if no variants are given as input
      else if (versLoader.isValidId(elementId)) {
        name = versLoader.getDataObjectByID(elementId).getName();
        // to be used to display in table
        this.pidcVersionName = name;
        pidcVersId = elementId;
      }
      this.pidcElementNameMap.put(elementId, name);

    }
    // If Compli Review is conducted without PIDC , pidc version id is null
    if (pidcVersId != null) {
      Long attrId = Long.parseLong(this.commonParamLoader.getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
      PidcVersionAttribute versAttr =
          new PidcVersionAttributeLoader(getServiceData()).getPidcVersionAttributeForAttr(pidcVersId, attrId);
      // Pidc version attribute will be null, if no value is set for attribute
      if (versAttr != null) {
        Long valueId = Long.parseLong(this.commonParamLoader.getValue(CommonParamKey.BEG_CAL_PROJ_ATTR_VAL_ID));
        this.projectOrgBEG = CommonUtils.isEqual(versAttr.getValueId(), valueId);
      }
    }
  }


  private boolean isQssdParamPresent() {
    for (CompliReviewOutputData data : this.inputModel.getCompliReviewOutput().values()) {
      if (CommonUtils.isNotEmpty(data.getQssdResult())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param content - String content to be written
   * @param font - font
   * @param fontSize - size
   * @param linePosition1 - position to write
   * @throws IOException - exception
   */
  public void writeText(final String content, final PDType1Font font, final int fontSize, final double linePosition1)
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
   * @throws IOException
   */
  private void createNewPage() throws IOException {
    // create new page
    this.currentPage = PdfUtil.createNewPage(this.document);

    this.currentStream.close();

    // create content stream
    this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, true);

    // set initial line position
    this.linePosition = PdfUtil.resetLinePosition();
  }

  private void createUserInfoTable(final String userName) throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);


    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // add data to table
    Row<PDPage> row = baseTable.createRow(10f);
    Cell<PDPage> cell = row.createCell(30, "Report generated by");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, userName);
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Date/Time");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, new Date().toString());
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    row = baseTable.createRow(10f);
    cell = row.createCell(30, "BM - Number" + " (CoC needs to charge the effort)");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(80, "");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
  }

  private void createReviewInputInfoTable(final String pidcName, final String pverName,
      final boolean isNonSDOMBCRelease)
      throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);


    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // add data to table
    Row<PDPage> row;
    Cell<PDPage> cell;

    // PIDC Name
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "PIDC Name");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(70, pidcName);
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // PVER Name
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "PVER Name");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    if (isNonSDOMBCRelease) {
      cell = row.createCell(70, pverName + " (NON-SDOM)");
    }
    else {
      cell = row.createCell(70, pverName + " (SDOM)");
    }
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // A2l File Name
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "A2L Name");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    CompliReviewInputMetaData inputMetaData = this.inputModel.getCompliReviewInputMetaData();
    cell = row.createCell(70, inputMetaData.getA2lFileName());
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // web Flow ID
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "WebFlow ID");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    // Convert null to empty string, as Webflow ID could be null
    cell = row.createCell(70, CommonUtils.checkNull(inputMetaData.getWebflowID()));
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    // Execution ID
    row = baseTable.createRow(10f);
    cell = row.createCell(30, "Execution ID");
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell = row.createCell(70, this.inputModel.getExecutionID());
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);

    if (this.inputModel.getOutputZipFileName() != null) {
      Long days = Long.valueOf(this.commonParamLoader.getValue(CommonParamKey.COMPLI_FILES_RETENTION_DAYS));

      row = baseTable.createRow(10f);
      cell = row.createCell(100, "Click here to download files  (Only for COC's. Files will be saved for " +
          days.intValue() + " days from the creation date onwards)");

      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);
      cell.setTextColor(Color.BLUE);

      this.linePosition = baseTable.draw() - 20;

      String urlLink = this.commonParamLoader.getValue(CommonParamKey.COMPLI_FILES_APEX_LINK);
      urlLink = urlLink.replace("~uuid~", this.inputModel.getOutputZipFileName());

      addLink(16, 360, 400, 375, urlLink);

    }
    else {
      this.linePosition = baseTable.draw();
    }

    this.linePosition -= 20;

    // Since this is a dynamic data table
    // get current page details after drawing table
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }
  }

  private void addLink(final float xStart, final float yStart, final float xEnd, final float yEnd, final String link)
      throws IOException {

    // create a link annotation
    PDAnnotationLink txtLink = new PDAnnotationLink();

    // add an underline
    PDBorderStyleDictionary underline = new PDBorderStyleDictionary();
    underline.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
    underline.setWidth(0);

    txtLink.setBorderStyle(underline);

    PDColor col = new PDColor(new float[] { 0.0f, 0.0f, 1.0f }, PDDeviceRGB.INSTANCE);
    txtLink.setColor(col);


    PDRectangle position = new PDRectangle();
    position.setLowerLeftX(xStart); // line width
    position.setLowerLeftY(yStart); // space from doc bottom
    position.setUpperRightX(xEnd); // space from left margin
    position.setUpperRightY(yEnd);


    txtLink.setRectangle(position);

    PDActionURI action = new PDActionURI();
    action.setURI(link);
    txtLink.setAction(action);

    this.currentPage.getAnnotations().add(txtLink);


  }

  private void createQssdReviewSummaryTable() throws IOException {
    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);


    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    createQssdSummaryTableHeader(baseTable);

    // create data list
    List<List<String>> dataList = new ArrayList<>();

    for (Entry<Long, String> hexEntry : this.inputModel.getCompliReviewInputMetaData().getHexfileIdxMap().entrySet()) {
      List<String> temp = new ArrayList<>();

      Long serNumber = hexEntry.getKey();

      CompliReviewOutputData reviewOutputData = this.inputModel.getCompliReviewOutput().get(serNumber);

      // S.No
      temp.add(serNumber.toString());

      // PIDC name
      String pidcElement = "";
      Long pidcElementId = this.inputModel.getCompliReviewInputMetaData().getHexFilePidcElement().get(serNumber);

      if ((pidcElementId != null) && this.isVariantPresent) {
        pidcElement = this.pidcElementNameMap.get(pidcElementId);
      }
      else {
        pidcElement = NOT_APPLICABLE;
      }

      temp.add(pidcElement);

      String dstName = this.inputModel.getCompliReviewInputMetaData().getDstMap().get(serNumber);
      if (null == dstName) {
        // hex file name
        temp.add(hexEntry.getValue());
      }
      else {
        temp.add(reviewOutputData.getHexfileName());
      }

      // total no of q-ssd params
      temp.add(String.valueOf(reviewOutputData.getQssdCount()));

      // total no of q-ssd pass
      temp.add(String.valueOf(reviewOutputData.getQssdPassCount()));

      // failed compli - use case type q-ssd
      temp.add(String.valueOf(reviewOutputData.getQssdFailCount()));

      // failed compli - use case type no rule
      temp.add(String.valueOf(reviewOutputData.getQssdNoRuleCount()));

      dataList.add(temp);
    }


    // add data to table
    for (List<String> data : dataList) {
      Row<PDPage> row = baseTable.createRow(10f);
      Cell<PDPage> cell1 = row.createCell(5, data.get(0));
      cell1.setFont(PDType1Font.COURIER);
      cell1.setFontSize(8);
      cell1.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell2 = row.createCell(20, data.get(1));
      cell2.setFontSize(8);
      cell2.setFont(PDType1Font.HELVETICA);
      cell2.setAlign(HorizontalAlignment.CENTER);

      String dstLink = "";
      /**
       * add hex file name with link for dst cond1 to check whether there is a hex file linked to dst cond2 will filter
       * hex files if out of all the hex files selected some are not linked to dst
       */
      if (CommonUtils.isNotEmpty(this.hexFileDstIdMap) && this.hexFileDstIdMap.containsKey(data.get(0))) {
        dstLink = data.get(2) + " (easee:CDM," + this.hexFileDstIdMap.get(data.get(0)).toString() + ")";

      }
      else {
        dstLink = data.get(2);
      }

      Cell<PDPage> cell3 = row.createCell(42, dstLink);
      cell3.setFont(PDType1Font.HELVETICA);
      cell3.setFontSize(8);
      cell3.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell4 = row.createCell(12, data.get(3));
      cell4.setFont(PDType1Font.HELVETICA);
      cell4.setFontSize(8);
      cell4.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell5 = row.createCell(12, data.get(4));
      cell5.setFont(PDType1Font.HELVETICA);
      cell5.setFontSize(8);
      cell5.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell6 = row.createCell(12, data.get(5));
      cell6.setFont(PDType1Font.HELVETICA);
      cell6.setFontSize(8);
      cell6.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell7 = row.createCell(12, data.get(6));
      cell7.setFont(PDType1Font.HELVETICA);
      cell7.setFontSize(8);
      cell7.setAlign(HorizontalAlignment.CENTER);

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
   * Method to create review summary table
   *
   * @param doc
   * @throws IOException
   */
  private void createReviewSummaryTable() throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);


    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // create table headers
    createOtherPagesTableHeader(baseTable);

    // create data list
    List<List<String>> dataList = new ArrayList<>();

    for (Entry<Long, String> hexEntry : this.inputModel.getCompliReviewInputMetaData().getHexfileIdxMap().entrySet()) {
      List<String> temp = new ArrayList<>();

      Long serNumber = hexEntry.getKey();

      CompliReviewOutputData reviewOutputData = this.inputModel.getCompliReviewOutput().get(serNumber);

      // S.No
      temp.add(serNumber.toString());

      // PIDC name
      String pidcElement = "";
      Long pidcElementId = this.inputModel.getCompliReviewInputMetaData().getHexFilePidcElement().get(serNumber);

      if ((pidcElementId != null) && this.isVariantPresent) {
        pidcElement = this.pidcElementNameMap.get(pidcElementId);
      }
      else {
        pidcElement = NOT_APPLICABLE;
      }

      temp.add(pidcElement);

      String dstName = this.inputModel.getCompliReviewInputMetaData().getDstMap().get(serNumber);
      if (null == dstName) {
        // hex file name
        temp.add(hexEntry.getValue());
      }
      else {
        temp.add(reviewOutputData.getHexfileName());
      }

      // total no of compli
      temp.add(String.valueOf(reviewOutputData.getCompliCount()));

      // total no of compli
      temp.add(String.valueOf(reviewOutputData.getCompliPassCount()));

      // failed compli - use case type c-ssd
      temp.add(String.valueOf(reviewOutputData.getCssdFailCount()));

      // failed compli - use case type ssd2rv
      temp.add(String.valueOf(reviewOutputData.getSsd2RvFailCount()));

      // failed compli - use case type no rule
      temp.add(String.valueOf(reviewOutputData.getNoRuleCount()));

      dataList.add(temp);
    }


    // add data to table
    for (List<String> data : dataList) {
      Row<PDPage> row = baseTable.createRow(10f);
      Cell<PDPage> cell1 = row.createCell(5, data.get(0));
      cell1.setFont(PDType1Font.COURIER);
      cell1.setFontSize(8);
      cell1.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell2 = row.createCell(20, data.get(1));
      cell2.setFontSize(8);
      cell2.setFont(PDType1Font.HELVETICA);
      cell2.setAlign(HorizontalAlignment.CENTER);

      String dstLink = "";
      /**
       * add hex file name with link for dst cond1 to check whether there is a hex file linked to dst cond2 will filter
       * hex files if out of all the hex files selected some are not linked to dst
       */
      if (CommonUtils.isNotEmpty(this.hexFileDstIdMap) && this.hexFileDstIdMap.containsKey(data.get(0))) {
        dstLink = data.get(2) + " (easee:CDM," + this.hexFileDstIdMap.get(data.get(0)).toString() + ")";
      }
      else {
        dstLink = data.get(2);
      }

      Cell<PDPage> cell3 = row.createCell(40, dstLink);
      cell3.setFont(PDType1Font.HELVETICA);
      cell3.setFontSize(8);
      cell3.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell4 = row.createCell(10, data.get(3));
      cell4.setFont(PDType1Font.HELVETICA);
      cell4.setFontSize(8);
      cell4.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell5 = row.createCell(10, data.get(4));
      cell5.setFont(PDType1Font.HELVETICA);
      cell5.setFontSize(8);
      cell5.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell6 = row.createCell(10, data.get(5));
      cell6.setFont(PDType1Font.HELVETICA);
      cell6.setFontSize(8);
      cell6.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell7 = row.createCell(10, data.get(6));
      cell7.setFont(PDType1Font.HELVETICA);
      cell7.setFontSize(8);
      cell7.setAlign(HorizontalAlignment.CENTER);

      Cell<PDPage> cell8 = row.createCell(10, data.get(7));
      cell8.setFont(PDType1Font.HELVETICA);
      cell8.setFontSize(8);
      cell8.setAlign(HorizontalAlignment.CENTER);
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
   * Method to create table header row
   *
   * @param baseTable
   */
  private void createQssdSummaryTableHeader(final BaseTable baseTable) {
    // Table header

    List<String> headerList = new ArrayList<>();
    headerList.add("#");
    headerList.add(PIDC_VARIANT);
    headerList.add("Hex file");
    headerList.add(CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType());
    headerList.add("Passed");
    headerList.add("Failed");
    headerList.add("Failed<br>(NO RULE)");

    // Create Header row
    Row<PDPage> headerRow = baseTable.createRow(15f);
    Cell<PDPage> cell = headerRow.createCell(5, headerList.get(0));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(20, headerList.get(1));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(42, headerList.get(2));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(12, headerList.get(3));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(12, headerList.get(4));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(12, headerList.get(5));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(12, headerList.get(6));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    baseTable.addHeaderRow(headerRow);


  }


  /**
   * Method to create table header row
   *
   * @param baseTable
   */
  private void createOtherPagesTableHeader(final BaseTable baseTable) {
    // Table header
    List<String> headerList = new ArrayList<>();
    headerList.add("#");
    headerList.add(PIDC_VARIANT);
    headerList.add("Hex file");
    headerList.add("COMPLI");
    headerList.add("Passed");
    headerList.add("Failed  (C-SSD)");
    headerList.add("Failed  (SSD2Rv)");
    headerList.add("Failed  (NO RULE)");

    // Create Header row
    Row<PDPage> headerRow = baseTable.createRow(15f);
    Cell<PDPage> cell = headerRow.createCell(5, headerList.get(0));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(20, headerList.get(1));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(40, headerList.get(2));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(3));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(4));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(5));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(6));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(10, headerList.get(7));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    baseTable.addHeaderRow(headerRow);
  }


  /**
   * @param attrValModelSet
   * @param curAttrValModel
   * @return attribute values as comma seperated values
   */
  private String getAttrValues(final Set<AttributeValueModel> attrValModelSet,
      final AttributeValueModel curAttrValModel) {

    List<String> valueList =
        attrValModelSet.stream().filter(av -> CommonUtils.isEqual(av.getAttr(), curAttrValModel.getAttr()))
            .map(av -> av.getValue().getName()).collect(Collectors.toList());

    return valueList.isEmpty() ? "" : StringUtils.join(valueList, ",");
  }

  /**
   * @param attr
   * @param attrList
   * @return true if attribute is already loaded in dataList for attr dep table
   */
  private boolean isAttrPresentAlready(final String attr, final List<List<String>> attrList) {
    if (CommonUtils.isNotEmpty(attrList)) {
      return !attrList.stream().filter(dataInRow -> dataInRow.get(2).equals(attr)).collect(Collectors.toList())
          .isEmpty();
    }
    return false;
  }


  private void createAttrDepnSummaryTable() throws IOException {

    // Attr Depn section
    writeText("Dependency Attributes", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
    this.linePosition -= 20;

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    // Create Header row
    Row<PDPage> headerRow = baseTable.createRow(15f);


    Cell<PDPage> cell = headerRow.createCell(5, "#");
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);


    cell = headerRow.createCell(20, PIDC_VARIANT);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);


    cell = headerRow.createCell(30, "Attribute");
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);

    cell = headerRow.createCell(50, "Attribute value");
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);

    baseTable.addHeaderRow(headerRow);
    long index = 0;

    // remove multiple entries for same variant to avoid redundant info in the table
    Set<Long> uniquePidcEleId =
        new HashSet<>(this.inputModel.getCompliReviewInputMetaData().getHexFilePidcElement().values());
    // add data to table
    for (Long pidcElementId : uniquePidcEleId) {

      Set<AttributeValueModel> attrValSet = this.inputModel.getAttrValueModMap().get(pidcElementId);
      // create data list
      List<List<String>> dataList = createAttrModelDataList(++index, pidcElementId, attrValSet);

      int rowNum = 1;
      for (List<String> data : dataList) {
        Row<PDPage> row = baseTable.createRow(10f);

        // S.No
        cell = row.createCell(5, data.get(0));
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);
        setCellBorderStyle(baseTable, cell, rowNum, dataList.size());

        // Variant Name
        cell = row.createCell(20, data.get(1));
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);
        setCellBorderStyle(baseTable, cell, rowNum, dataList.size());

        cell = row.createCell(30, data.get(2));
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);


        cell = row.createCell(50, data.get(3));
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);
        rowNum++;
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
   * @param hexFileIndex
   * @param pidcElementId
   * @param attrValSet
   * @return
   */
  private List<List<String>> createAttrModelDataList(final Long hexFileIndex, final Long pidcElementId,
      final Set<AttributeValueModel> attrValSet) {

    List<List<String>> dataList = new ArrayList<>();

    boolean variantColumnFilled = false;
    for (AttributeValueModel attributeValueModel : attrValSet) {
      List<String> datList = new ArrayList<>();
      if (!variantColumnFilled) {
        datList.add(hexFileIndex.toString());
        String pidcElementName;
        if (this.isVariantPresent) {
          pidcElementName = this.pidcElementNameMap.get(pidcElementId);
        }
        else {
          pidcElementName = NOT_APPLICABLE;
        }

        datList.add(pidcElementName);

        variantColumnFilled = true;
      }
      else {
        datList.add("");
        datList.add("");
      }

      datList.add(attributeValueModel.getAttr().getName());
      datList.add(getAttrValues(attrValSet, attributeValueModel));
      if (!isAttrPresentAlready(attributeValueModel.getAttr().getName(), dataList)) {
        dataList.add(datList);
      }
    }

    return dataList;
  }

  /**
   * Method to add borders for row in attr dep table
   *
   * @param cell
   * @param rowNum
   * @param dataSize
   */
  private void setCellBorderStyle(final BaseTable baseTable, final Cell<PDPage> cell, final int rowNum,
      final int dataSize) {
    LineStyle whiteBottomBorderStyle = new LineStyle(Color.WHITE, 0);
    LineStyle whiteTopBorderStyle = new LineStyle(Color.WHITE, 0);
    LineStyle blackBottomBorderStyle = new LineStyle(Color.BLACK, 1);
    LineStyle blackTopBorderStyle = new LineStyle(Color.BLACK, 1);

    if ((rowNum == 1) && (dataSize != 1)) {
      cell.setTopBorderStyle(blackTopBorderStyle);
      cell.setBottomBorderStyle(whiteBottomBorderStyle);
    }
    else if (rowNum == dataSize) {
      cell.setTopBorderStyle(whiteTopBorderStyle);
      cell.setBottomBorderStyle(blackBottomBorderStyle);
    }
    else {
      cell.setTopBorderStyle(whiteTopBorderStyle);
      cell.setBottomBorderStyle(whiteBottomBorderStyle);
    }

    double position = (this.linePosition - baseTable.getHeaderAndDataHeight()) + 10f;
    if ((position > 50) && (position < 70)) {
      cell.setBottomBorderStyle(blackBottomBorderStyle);
    }

  }


  private void createOtherQssdPages(final QSSD_RESULT_FLAG qssdResult) throws IOException {


    // total number of Q-SSD in all HEX files

    int totalQssd = 0;

    String qssdPageHeading =
        qssdResult.getUiType().equals(QSSD_RESULT_FLAG.OK.getUiType()) ? "Q-SSD OK" : qssdResult.getUiType();

    try {
      // Review details section
      writeText("Review Details for " + qssdPageHeading, PDType1Font.HELVETICA_BOLD, 15, this.linePosition);

      if (qssdResult.getUiType().equals(CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType())) {
        this.linePosition -= 20;

        String hint = "All descriptions shall be done in clear text to enable understanding for signature";

        writeText(hint, PDType1Font.HELVETICA_BOLD, 8, this.linePosition);
        this.linePosition -= 10;

      }


      this.linePosition -= 20;

      // Iterating through review output map
      for (Entry<Long, CompliReviewOutputData> entry : this.inputModel.getCompliReviewOutput().entrySet()) {

        int numQssd = entry.getValue().getQssdFailCount() + entry.getValue().getQssdNoRuleCount();
        totalQssd += numQssd;

        // Create table description section
        // Hex file index number
        String hexName = "Hex file " + entry.getKey() + " - " + entry.getValue().getHexfileName();
        displayTextWithIndent(hexName, 0, 12);
        this.linePosition -= 20;

        // Q-SSD details
        createQssdResult(numQssd, qssdResult, entry);
      }

      if (qssdResult.getUiType().equals(CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType())) {
        // create signatures
        createQssdSignature(totalQssd, CDRConstants.QSSD_RESULT_FLAG.QSSD);

      }

      // add header for all pages
      addHeaderForPages();
    }
    catch (Exception e) {
      getLogger().error(e.getMessage(), e);
    }
    finally {
      this.currentStream.close();
    }

  }

  private void createQssdResult(final int numQssd, final QSSD_RESULT_FLAG qssdResult,
      final Entry<Long, CompliReviewOutputData> entry)
      throws IOException {

    switch (qssdResult) {
      case QSSD:
        if (numQssd > 0) {
          // create Q-SSD page
          createOtherPagesQssdTableSection(entry, qssdResult);
        }
        else {
          // no Q-SSD errors
          writeText("No Q-SSD deviations in HEX file.", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
          this.linePosition -= 30;

        }
        break;

      case OK:
        if (entry.getValue().getQssdPassCount() > 0) {
          // create OK page
          createOtherPagesQssdTableSection(entry, qssdResult);
        }
        break;
      default:
        break;
    }

  }


  private void createOtherPagesQssdTableSection(final Entry<Long, CompliReviewOutputData> entry,
      final QSSD_RESULT_FLAG qssdResult)
      throws IOException {

    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);


    List<String> headerList = new ArrayList<>();

    // create table header
    createQssdTableHeader(headerList, baseTable, qssdResult);

    // create data list
    List<List<String>> dataList = createQssdTableDataList(qssdResult, entry);

    // create table body
    if (qssdResult.getUiType().equals(CDRConstants.QSSD_RESULT_FLAG.OK.getUiType())) {
      createRowForOKRes(baseTable, dataList);
    }
    else if (qssdResult.getUiType().equals(CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType())) {
      Cell<PDPage> cell = null;

      // add data list to table
      for (List<String> data : dataList) {
        Row<PDPage> row = baseTable.createRow(40f);
        // number
        cell = row.createCell(S_NO_COL_WIDTH, data.get(0));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);

        // FC
        cell = row.createCell(QSSD_FC_NAME_COL_WIDTH, data.get(1));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);

        // label
        cell = row.createCell(QSSD_PARAM_NAME_COL_WIDTH, data.get(2));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);

        // result
        cell = row.createCell(QSSD_CHECK_RESULT_COL_WIDTH, data.get(3));
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);

        if (!data.get(3).equals(QSSD_RESULT_FLAG.OK.getUiType())) {
          cell.setTextColor(Color.RED);
        }
        else {
          cell.setTextColor(Color.GREEN);
        }

        // bosch
        cell = row.createCell((float) QSSD_BOSCH_COL_WIDTH, data.get(4));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);


        // customer / third party
        cell = row.createCell((float) QSSD_CUST_THIRD_COL_WIDTH, data.get(5));
        cell.setFont(PDType1Font.COURIER);
        cell.setFontSize(8);


        // reason
        cell = row.createCell(QSSD_REASON_COL_WIDTH, data.get(6));
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(8);

      }
    }


    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table, get the page of the second row
    if (!this.currentPage.equals(baseTable.getCurrentPage())) {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }


  }


  /**
   * Method to construct other pages
   *
   * @param doc
   * @throws IOException
   */
  private void createOtherPages(final CDRConstants.COMPLI_RESULT_FLAG resultFlag) throws IOException {
    // total number of C-SSD in all HEX files
    int totalCSSD = 0;
    // total number of SSD2Rv in all HEX files
    int totalSSD2Rv = 0;

    try {
      // Review details section
      writeText("Review Details for " + resultFlag.getUiType(), PDType1Font.HELVETICA_BOLD, 15, this.linePosition);

      if (resultFlag.getUiType().equals(COMPLI_RESULT_FLAG.CSSD.getUiType())) {
        this.linePosition -= 20;

        String dotChar = "\\.";

        String hint = this.messageLoader.getMessage(COMPLI_REVIEW, COMPLI_REPORT_HINT);

        String codexLink = this.commonParamLoader.getValue(CommonParamKey.COMPLI_REPORT_CODEX_LINK);

        String codexLinkForCssd = this.commonParamLoader.getValue(CommonParamKey.COMPLI_REPORT_CSSD_CODEX_LINK);

        String[] hints = hint.split("\n");

        String[] subHints1 = hints[0].split(dotChar);

        String indentationForSubHints = "        - ";

        String subHint11 = indentationForSubHints + subHints1[1];

        String[] subHints2 = hints[1].split(dotChar);

        String subHint21 = indentationForSubHints + subHints2[1];

        String subHint22 = indentationForSubHints + subHints2[2];

        writeText("Hint", PDType1Font.HELVETICA_BOLD, 8, this.linePosition);
        this.linePosition -= 10;
        writeText("(1) " + subHints1[0], PDType1Font.HELVETICA, 6, this.linePosition);
        this.linePosition -= 10;
        writeText(subHint11, PDType1Font.HELVETICA, 6, this.linePosition);
        this.linePosition -= 10;
        writeText("(2) " + subHints2[0], PDType1Font.HELVETICA, 6, this.linePosition);
        this.linePosition -= 10;
        writeText(subHint21, PDType1Font.HELVETICA, 6, this.linePosition);
        this.linePosition -= 10;
        writeText(subHint22, PDType1Font.HELVETICA, 6, this.linePosition);
        this.linePosition -= 10;
        writeText(hints[2], PDType1Font.HELVETICA, 6, this.linePosition);
        this.linePosition -= 10;

        writeText("Link to PS Codex Reporting", PDType1Font.HELVETICA, 6, this.linePosition, Color.BLUE);
        // add link
        addLink((float) PdfUtil.LEFT_MARGIN, 545, 105f, 553f, codexLink);
        this.linePosition -= 10;

        writeText("Link to PS-EC Codex Reporting Tool for active C-SSD deviation with \"json file\"",
            PDType1Font.HELVETICA, 6, this.linePosition, Color.BLUE);
        // add link
        addLink((float) PdfUtil.LEFT_MARGIN, 535, 238, 543f, codexLinkForCssd);

      }


      this.linePosition -= 20;

      // Iterating through review output map
      for (Entry<Long, CompliReviewOutputData> entry : this.inputModel.getCompliReviewOutput().entrySet()) {

        int numCSSD = entry.getValue().getCssdFailCount() + entry.getValue().getNoRuleCount();
        totalCSSD += numCSSD;

        int numSSD2Rv = entry.getValue().getSsd2RvFailCount();
        totalSSD2Rv += numSSD2Rv;

        // Create table description section
        // Hex file index number
        String hexName = "Hex file " + entry.getKey() + " - " + entry.getValue().getHexfileName();
        displayTextWithIndent(hexName, 0, 12);
        this.linePosition -= 20;

        // Compli details
        createCompliResult(numCSSD, numSSD2Rv, resultFlag, entry);
      }
      // create signatures
      createCompliSignature(totalCSSD, totalSSD2Rv, resultFlag);

      // add header for all pages
      addHeaderForPages();
    }
    catch (Exception e) {
      getLogger().error(e.getMessage(), e);
    }
    finally {
      this.currentStream.close();
    }
  }

  /**
   * @param totalCSSD
   * @param totalSSD2Rv
   * @throws IOException
   */
  private void createCompliSignature(final int totalCSSD, final int totalSSD2Rv,
      final CDRConstants.COMPLI_RESULT_FLAG resultFlag)
      throws IOException {
    switch (resultFlag) {
      case CSSD:
        // add signatures only, if there are C-SSD deviations in at least one HEX
        createNewPageForCssd(totalCSSD);
        break;

      case SSD2RV:
        // add signatures only, if there are SSD2Rv deviations in at least one HEX
        createNewPageForSsd2Rv(totalSSD2Rv);
        break;

      default:
        break;
    }
  }


  /**
   * @param totalSSD2Rv
   * @throws IOException
   */
  private void createNewPageForSsd2Rv(final int totalSSD2Rv) throws IOException {
    if (totalSSD2Rv > 0) {
      // check if signature content can be occupied in current page
      if (this.linePosition < 550) {
        // create signature in new page
        createNewPage();
      }
      createSsd2RvSignature(this.currentStream, false);
    }
  }


  /**
   * @param totalCSSD
   * @throws IOException
   */
  private void createNewPageForCssd(final int totalCSSD) throws IOException {
    if (totalCSSD > 0) {
      // check if signature content can be occupied in current page
      if (this.linePosition < 600) {
        // create signature in new page
        createNewPage();
      }
      createCssdSignature(this.currentStream, false);
    }
  }


  /**
   * @param totalQssd
   * @param qSsdResult
   * @throws IOException
   */
  private void createQssdSignature(final int totalQssd, final QSSD_RESULT_FLAG qSsdResult) throws IOException {
    if (qSsdResult.getUiType().equals(CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType())) {
      if ((totalQssd > 0) && (this.linePosition < 600)) {
        createNewPage();
      }
      if (totalQssd > 0) {
        createQssdSignature(this.currentStream, false);
      }
    }
  }


  /**
   * @param doc
   * @param stream
   * @param isNewPage
   * @param width
   * @throws IOException
   */
  private void createQssdSignature(final PDPageContentStream stream, final boolean isNewPage) throws IOException {

    if (isNewPage) {
      // Create a page
      PDPage page = PdfUtil.createNewPage(this.document);

      // Create content stream to add content
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, page, AppendMode.APPEND, false);
      PdfUtil.constructHeader(this.document, this.currentStream, this.boschLogo);
      this.linePosition = PdfUtil.resetLinePosition();
    }
    else {
      this.currentStream = stream;
    }

    // heading
    writeText("Digital Signatures (Deviations of Q-SSD)", PDType1Font.HELVETICA_BOLD, 10, this.linePosition,
        Color.BLACK);
    this.linePosition -= 10;

    // Underline
    this.currentStream.moveTo((float) PdfUtil.LEFT_MARGIN, (float) this.linePosition);
    this.currentStream.lineTo(220, (float) this.linePosition);
    this.currentStream.stroke();
    this.linePosition -= 10;


    String hint = this.messageLoader.getMessage(COMPLI_REVIEW, COMPLI_REPORT_QSSD_HINT);

    writeText(hint, PDType1Font.HELVETICA_BOLD, 8, this.linePosition, Color.BLACK);

    this.linePosition -= 10;
    writeText(
        "Please cluster the deviations in functional groups depending on the Expert Committee approval of the deviations.",
        PDType1Font.HELVETICA_BOLD, 7, this.linePosition, Color.BLACK);
    this.linePosition -= 10;

    // table
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    List<String> headerList = new ArrayList<>();

    Cell<PDPage> cell;

    String[] qssdheaderCol = CompliReviewSignature.getInstance().getColumnHeadersForQSSDSignature(this.projectOrgBEG);
    // Table header
    headerList.add("Q-SSD dev. No. <br>(see Check Result Table)");
    headerList.add(qssdheaderCol[0]);
    headerList.add(qssdheaderCol[1]);
    headerList.add(qssdheaderCol[2]);
    headerList.add(qssdheaderCol[3]);


    // add header row 1

    Row<PDPage> headerRow1 = baseTable.createRow(15f);

    cell = headerRow1.createCell(QSSD_DEV_NO_COL_WIDTH, "");
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    double cellWidth = QSSD_SIGN_1_COL_WIDTH + QSSD_SIGN_2_COL_WIDTH + QSSD_SIGN_3_COL_WIDTH;
    cell = headerRow1.createCell((float) cellWidth, qssdheaderCol[4]);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell.setColspanCell(true);
    cell.setAlign(HorizontalAlignment.CENTER);
    cell = headerRow1.createCell(QSSD_SIGN_COL_WIDTH, qssdheaderCol[5]);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell.setAlign(HorizontalAlignment.CENTER);
    baseTable.addHeaderRow(headerRow1);


    // Create Header row
    Row<PDPage> headerRow = baseTable.createRow(15f);
    cell = headerRow.createCell(QSSD_DEV_NO_COL_WIDTH, headerList.get(0));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell((float) QSSD_SIGN_1_COL_WIDTH, headerList.get(1));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell((float) QSSD_SIGN_2_COL_WIDTH, headerList.get(2));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell((float) QSSD_SIGN_3_COL_WIDTH, headerList.get(3));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(QSSD_SIGN_COL_WIDTH, headerList.get(4));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    baseTable.addHeaderRow(headerRow);

    for (int i = 1; i <= 5; i++) {
      Row<PDPage> dataRow = baseTable.createRow(50f);
      cell = dataRow.createCell(QSSD_DEV_NO_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell((float) QSSD_SIGN_1_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell((float) QSSD_SIGN_2_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell((float) QSSD_SIGN_3_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell(QSSD_SIGN_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
    }
    this.linePosition = baseTable.draw() - 20;
  }

  /**
   * @param doc
   * @param stream
   * @param isNewPage
   * @param width
   * @throws IOException
   */
  private void createCssdSignature(final PDPageContentStream stream, final boolean isNewPage) throws IOException {


    if (isNewPage) {
      // Create a page
      PDPage page = PdfUtil.createNewPage(this.document);

      // Create content stream to add content
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, page, AppendMode.APPEND, false);
      PdfUtil.constructHeader(this.document, this.currentStream, this.boschLogo);
      this.linePosition = PdfUtil.resetLinePosition();
    }
    else {
      this.currentStream = stream;
    }

    // heading
    writeText("Digital Signatures (Measures for C-SSD)", PDType1Font.HELVETICA_BOLD, 10, this.linePosition,
        Color.BLACK);
    this.linePosition -= 10;

    // Underline
    this.currentStream.moveTo((float) PdfUtil.LEFT_MARGIN, (float) this.linePosition);
    this.currentStream.lineTo(220, (float) this.linePosition);
    this.currentStream.stroke();
    this.linePosition -= 10;

    writeText(
        "(3) Confirmation by project : Codex Case ALM is closed (incl. BST signature) and deviation confirmed via approved ALM workflow",
        PDType1Font.HELVETICA_BOLD, 8, this.linePosition, Color.BLACK);
    this.linePosition -= 10;
    writeText(
        "Please cluster the deviations in functional groups depending on the Expert Committee or Codex Case approval of the deviations.",
        PDType1Font.HELVETICA_BOLD, 7, this.linePosition, Color.BLACK);
    this.linePosition -= 10;

    // table
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    List<String> headerList = new ArrayList<>();


    Cell<PDPage> cell;

    String[] cssdColHeader = CompliReviewSignature.getInstance().getColumnHeadersForCSSDSignature(this.projectOrgBEG);

    // Table header
    headerList.add("C-SSD dev. No. (or) FC Name (see Check Result Table)");
    headerList.add(cssdColHeader[0]);
    headerList.add(cssdColHeader[1]);
    headerList.add(cssdColHeader[2]);


    // add header row 1

    Row<PDPage> headerRow1 = baseTable.createRow(15f);


    cell = headerRow1.createCell(20, "");
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow1.createCell(60, cssdColHeader[3]);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell.setColspanCell(true);
    cell.setAlign(HorizontalAlignment.CENTER);
    cell = headerRow1.createCell(30, cssdColHeader[4]);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell.setAlign(HorizontalAlignment.CENTER);
    baseTable.addHeaderRow(headerRow1);


    // Create Header row
    Row<PDPage> headerRow = baseTable.createRow(15f);
    cell = headerRow.createCell(20, headerList.get(0));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(30, headerList.get(1));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(30, headerList.get(2));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(30, headerList.get(3));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    baseTable.addHeaderRow(headerRow);

    for (int i = 1; i <= 5; i++) {
      Row<PDPage> dataRow = baseTable.createRow(50f);
      cell = dataRow.createCell(20, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell(30, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell(30, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell(30, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
    }

    this.linePosition = baseTable.draw() - 20;
  }

  private void createSsd2RvSignature(final PDPageContentStream stream, final boolean isNewPage) throws IOException {
    if (isNewPage) {
      // Create a page
      PDPage page = PdfUtil.createNewPage(this.document);

      // Create content stream to add content
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, page, AppendMode.APPEND, false);
      PdfUtil.constructHeader(this.document, this.currentStream, this.boschLogo);
      this.linePosition = PdfUtil.resetLinePosition();
    }
    else {
      this.currentStream = stream;
    }
    // heading, 1st line
    writeText("Digital Signatures (Deviations of SSD2rv)", PDType1Font.HELVETICA_BOLD, 10, this.linePosition,
        Color.BLACK);
    this.linePosition -= 10;

    // Underline
    this.currentStream.moveTo((float) PdfUtil.LEFT_MARGIN, (float) this.linePosition);
    this.currentStream.lineTo(220, (float) this.linePosition);
    this.currentStream.stroke();
    this.linePosition -= 10;

    // heading, 2nd line
    writeText(
        "(4) Confirmation by project : Codex Case ALM is closed and deviation confirmed via approved ALM workflow",
        PDType1Font.HELVETICA_BOLD, 8, this.linePosition, Color.BLACK);
    this.linePosition -= 10;

    // heading, 3rd line
    writeText(
        "Please cluster the deviations in functional groups depending on the Expert Committee approving the deviations.",
        PDType1Font.HELVETICA_BOLD, 7, this.linePosition, Color.BLACK);
    this.linePosition -= 10;

    // table
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);

    List<String> headerList = new ArrayList<>();


    Cell<PDPage> cell;
    String[] ssd2rvHeaderCols =
        CompliReviewSignature.getInstance().getColumnHeadersForSSD2RvSignature(this.projectOrgBEG);
    // Table header
    headerList.add("SSD2rv dev. " + "No.<br>" + "OR<br>" + "FC Name<br>" + "(see Check <br> Result Table)");
    headerList.add(ssd2rvHeaderCols[0]);
    headerList.add(ssd2rvHeaderCols[1]);
    headerList.add(ssd2rvHeaderCols[2]);
    headerList.add(ssd2rvHeaderCols[3]);


    // add header row 1

    Row<PDPage> headerRow1 = baseTable.createRow(15f);


    cell = headerRow1.createCell(SSD2RV_NO_FC_COL_WIDTH, "");
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow1.createCell((float) (SSD2RV_SIGN_1_COL_WIDTH + SSD2RV_SIGN_2_COL_WIDTH), ssd2rvHeaderCols[4]);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell.setColspanCell(true);
    cell.setAlign(HorizontalAlignment.CENTER);
    cell = headerRow1.createCell((float) (SSD2RV_CODEX_CASE_COL_WIDTH + SSD2RV_SIGN_3_COL_WIDTH), ssd2rvHeaderCols[5]);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell.setColspanCell(true);
    cell.setAlign(HorizontalAlignment.CENTER);
    baseTable.addHeaderRow(headerRow1);


    // Create Header row
    Row<PDPage> headerRow = baseTable.createRow(15f);
    cell = headerRow.createCell(SSD2RV_NO_FC_COL_WIDTH, headerList.get(0));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell((float) SSD2RV_SIGN_1_COL_WIDTH, headerList.get(1));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell((float) SSD2RV_SIGN_2_COL_WIDTH, headerList.get(2));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell(SSD2RV_CODEX_CASE_COL_WIDTH, headerList.get(3));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    cell = headerRow.createCell((float) SSD2RV_SIGN_3_COL_WIDTH, headerList.get(4));
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(8);
    baseTable.addHeaderRow(headerRow);

    for (int i = 1; i <= 5; i++) {
      Row<PDPage> dataRow = baseTable.createRow(50f);
      cell = dataRow.createCell(SSD2RV_NO_FC_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell((float) SSD2RV_SIGN_1_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell((float) SSD2RV_SIGN_2_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell(SSD2RV_CODEX_CASE_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell = dataRow.createCell((float) SSD2RV_SIGN_3_COL_WIDTH, "");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
    }
    this.linePosition = baseTable.draw() - 20;

  }

  /**
   * @param numCSSD
   * @param numSSD2Rv
   * @param entry
   * @param resultFlag
   * @throws IOException
   */
  private void createCompliResult(final int numCSSD, final int numSSD2Rv, final COMPLI_RESULT_FLAG resultFlag,
      final Entry<Long, CompliReviewOutputData> entry)
      throws IOException {
    switch (resultFlag) {
      case CSSD:
        createSectionForCssd(numCSSD, resultFlag, entry);

        break;

      case SSD2RV:
        createSectionForSSd2Rv(numSSD2Rv, resultFlag, entry);

        break;

      case OK:
        if (entry.getValue().getCompliPassCount() > 0) {
          // create OK page
          createOtherPagesTableSection(entry, resultFlag);
        }
        break;
      default:
        break;
    }
  }


  /**
   * @param numSSD2Rv
   * @param resultFlag
   * @param entry
   * @throws IOException
   */
  private void createSectionForSSd2Rv(final int numSSD2Rv, final COMPLI_RESULT_FLAG resultFlag,
      final Entry<Long, CompliReviewOutputData> entry)
      throws IOException {
    if (numSSD2Rv > 0) {
      // create SSD2Rv page
      createOtherPagesTableSection(entry, resultFlag);
    }
    else {
      // no SSD2Rv errors
      writeText("No SSD2Rv deviations in HEX file.", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
      this.linePosition -= 30;
    }
  }


  /**
   * @param numCSSD
   * @param resultFlag
   * @param entry
   * @throws IOException
   */
  private void createSectionForCssd(final int numCSSD, final COMPLI_RESULT_FLAG resultFlag,
      final Entry<Long, CompliReviewOutputData> entry)
      throws IOException {
    if (numCSSD > 0) {
      // create C-SSD page
      createOtherPagesTableSection(entry, resultFlag);
    }
    else {
      // no C-SSD errors
      writeText("No C-SSD deviations in HEX file.", PDType1Font.HELVETICA_BOLD, 15, this.linePosition);
      this.linePosition -= 30;

    }
  }

  private void createOtherPagesTableSection(final Entry<Long, CompliReviewOutputData> entry,
      final CDRConstants.COMPLI_RESULT_FLAG resultFlag)
      throws IOException {
    // Calculate table width
    double tableWidth = PdfUtil.PAGE_WIDTH - (4 * PdfUtil.LEFT_MARGIN);

    // Initialize base table
    BaseTable baseTable = new BaseTable((float) this.linePosition, (float) (PdfUtil.PAGE_HEIGHT - PdfUtil.HEADER_SIZE),
        (float) PdfUtil.BOTTOM_MARGIN, (float) tableWidth, (float) PdfUtil.LEFT_MARGIN, this.document, this.currentPage,
        true, true);


    List<String> headerList = new ArrayList<>();

    // create table header
    createTableHeader(headerList, baseTable, resultFlag);

    // create data list
    List<List<String>> dataList = createTableDataList(resultFlag, entry);

    // create table body
    if (resultFlag == COMPLI_RESULT_FLAG.OK) {


      createRowForOKRes(baseTable, dataList);
    }
    else if (resultFlag == COMPLI_RESULT_FLAG.CSSD) {


      createRowForCSSD(baseTable, dataList);
    }

    else if (resultFlag == COMPLI_RESULT_FLAG.SSD2RV) {
      createRowForSSD2Rv(baseTable, dataList);
    }

    this.linePosition = baseTable.draw() - 20;

    // Since this is a dynamic data table, get the page of the second row
    if (!this.currentPage.equals(baseTable.getCurrentPage()))

    {
      this.currentPage = baseTable.getCurrentPage();
      this.currentStream.close();
      this.currentStream = new PDPageContentStream(this.document, this.currentPage, AppendMode.APPEND, false);
    }

  }


  /**
   * @param baseTable
   * @param dataList
   */
  private void createRowForSSD2Rv(final BaseTable baseTable, final List<List<String>> dataList) {
    // add data list to table
    for (List<String> data : dataList) {
      Row<PDPage> row = baseTable.createRow(40f);
      // number
      Cell<PDPage> dataCell = row.createCell(S_NO_COL_WIDTH, data.get(0));
      dataCell.setFont(PDType1Font.COURIER);
      dataCell.setFontSize(8);

      // FC
      dataCell = row.createCell(SSD2RV_FC_NAME_COL_WIDTH, data.get(1));
      dataCell.setFont(PDType1Font.COURIER);
      dataCell.setFontSize(8);

      // label
      dataCell = row.createCell(SSD2RV_PARAM_NAME_COL_WIDTH, data.get(2));
      dataCell.setFont(PDType1Font.COURIER);
      dataCell.setFontSize(8);

      // result
      dataCell = row.createCell(SSD2RV_RESULT_COL_WIDTH, data.get(3));
      dataCell.setFont(PDType1Font.HELVETICA);
      dataCell.setFontSize(8);

      if (!data.get(3).equals(CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType())) {
        dataCell.setTextColor(Color.RED);
      }
      else {
        dataCell.setTextColor(Color.GREEN);
      }

      // comment
      dataCell = row.createCell(SSD2RV_REASON_COL_WIDTH, data.get(4));
      dataCell.setFont(PDType1Font.HELVETICA);
      dataCell.setFontSize(8);
    }
  }


  /**
   * @param baseTable
   * @param dataList
   */
  private void createRowForCSSD(final BaseTable baseTable, final List<List<String>> dataList) {
    Cell<PDPage> cell;
    // add data list to table
    for (List<String> data : dataList) {
      Row<PDPage> row = baseTable.createRow(40f);
      // number
      cell = row.createCell(S_NO_COL_WIDTH, data.get(0));
      cell.setFont(PDType1Font.COURIER);
      cell.setFontSize(8);

      // FC
      cell = row.createCell(C_SSD_FC_NAME_COL_WIDTH, data.get(1));
      cell.setFont(PDType1Font.COURIER);
      cell.setFontSize(8);

      // label
      cell = row.createCell(C_SSD_PARAM_NAME_COL_WIDTH, data.get(2));
      cell.setFont(PDType1Font.COURIER);
      cell.setFontSize(8);

      // result
      cell = row.createCell(C_SSD_RESULT_COL_WIDTH, data.get(3));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);

      if (!data.get(3).equals(CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType())) {
        cell.setTextColor(Color.RED);
      }
      else {
        cell.setTextColor(Color.GREEN);
      }

      // not active
      cell = row.createCell(C_SSD_NOT_ACTIVE_COL_WIDTH, data.get(4));
      cell.setFont(PDType1Font.COURIER);
      cell.setFontSize(8);


      // active
      cell = row.createCell(C_SSD_ACTIVE_COL_WIDTH, data.get(5));
      cell.setFont(PDType1Font.COURIER);
      cell.setFontSize(8);


      // comment
      cell = row.createCell(C_SSD_REASON_COL_WIDTH, data.get(6));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);

    }
  }


  /**
   * @param baseTable
   * @param dataList
   */
  private void createRowForOKRes(final BaseTable baseTable, final List<List<String>> dataList) {
    Cell<PDPage> cell;
    // add data list to table
    for (List<String> data : dataList) {
      Row<PDPage> row = baseTable.createRow(8f);
      // number
      cell = row.createCell(S_NO_COL_WIDTH, data.get(0));
      cell.setFont(PDType1Font.COURIER);
      cell.setFontSize(8);

      // FC name
      cell = row.createCell(OK_FC_NAME_COL_WIDTH, data.get(1));
      cell.setFont(PDType1Font.COURIER);
      cell.setFontSize(8);


      // Label name
      cell = row.createCell(OK_PARAM_NAME_COL_WIDTH, data.get(2));
      cell.setFont(PDType1Font.COURIER);
      cell.setFontSize(8);


      // result
      cell = row.createCell(OK_RESULT_COL_WIDTH, data.get(3));
      cell.setFont(PDType1Font.HELVETICA);
      cell.setFontSize(8);

      if (!data.get(3).equals(CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType())) {
        cell.setTextColor(Color.RED);
      }
      else {
        cell.setTextColor(Color.GREEN);
      }
    }
  }

  /**
   * @param entry
   * @param resultFlag
   * @return
   */
  private List<List<String>> createQssdTableDataList(final QSSD_RESULT_FLAG resultFlag,
      final Entry<Long, CompliReviewOutputData> entry) {
    List<List<String>> dataList = new ArrayList<>();

    int count = 1;
    for (Entry<String, String> str : getSortedMap(entry.getValue().getQssdResult(),
        this.inputModel.getParamFunctionMap()).entrySet()) {
      switch (resultFlag) {
        case QSSD:
          if (str.getValue().equals(CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType())) {
            List<String> temp = new ArrayList<>();
            temp.add(entry.getKey() + "." + count);
            temp.add(this.inputModel.getParamFunctionMap().get(str.getKey()));
            temp.add(str.getKey());
            temp.add(NOT_OK_CONST);
            temp.add("");
            temp.add("");
            temp.add("");
            dataList.add(temp);
            count++;
          }
          else if (str.getValue().equals(CDRConstants.QSSD_RESULT_FLAG.NO_RULE.getUiType())) {
            List<String> temp = new ArrayList<>();
            temp.add(entry.getKey() + "." + count);
            temp.add(this.inputModel.getParamFunctionMap().get(str.getKey()));
            temp.add(str.getKey());
            temp.add("No Rule");
            temp.add("");
            temp.add("");
            temp.add("");
            dataList.add(temp);
            count++;
          }

          break;


        case OK:
          if (str.getValue().equals(CDRConstants.QSSD_RESULT_FLAG.OK.getUiType())) {
            List<String> temp = new ArrayList<>();
            temp.add(entry.getKey() + "." + count);
            temp.add(this.inputModel.getParamFunctionMap().get(str.getKey()));
            temp.add(str.getKey());
            temp.add(str.getValue());
            dataList.add(temp);
            count++;
          }

          break;

        default:
          break;
      }
    }
    return dataList;
  }

  /**
   * @param entry
   * @param resultFlag
   * @return
   */
  private List<List<String>> createTableDataList(final COMPLI_RESULT_FLAG resultFlag,
      final Entry<Long, CompliReviewOutputData> entry) {
    List<List<String>> dataList = new ArrayList<>();

    int count = 1;
    for (Entry<String, String> str : getSortedMap(entry.getValue().getCompliResult(),
        this.inputModel.getParamFunctionMap()).entrySet()) {
      switch (resultFlag) {
        case CSSD:
          count = createDataListForCssd(entry, dataList, count, str);

          break;

        case SSD2RV:
          count = createDataListForSsd2Rv(entry, dataList, count, str);

          break;

        case OK:
          count = createDataListForOKRes(entry, dataList, count, str);

          break;

        default:
          break;
      }
    }
    return dataList;
  }


  /**
   * @param entry
   * @param dataList
   * @param count
   * @param str
   * @return
   */
  private int createDataListForOKRes(final Entry<Long, CompliReviewOutputData> entry, final List<List<String>> dataList,
      final int curCount, final Entry<String, String> str) {

    int newCount = curCount;

    if (str.getValue().equals(CDRConstants.COMPLI_RESULT_FLAG.OK.getUiType())) {
      List<String> temp = new ArrayList<>();
      temp.add(entry.getKey() + "." + newCount);
      temp.add(this.inputModel.getParamFunctionMap().get(str.getKey()));
      temp.add(str.getKey());
      temp.add(str.getValue());
      dataList.add(temp);
      newCount++;
    }

    return newCount;
  }


  /**
   * @param entry
   * @param dataList
   * @param count
   * @param str
   * @return
   */
  private int createDataListForSsd2Rv(final Entry<Long, CompliReviewOutputData> entry,
      final List<List<String>> dataList, final int curCount, final Entry<String, String> str) {

    int newCount = curCount;

    if (str.getValue().equals(CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType())) {
      List<String> temp = new ArrayList<>();
      temp.add(entry.getKey() + "." + newCount);
      temp.add(this.inputModel.getParamFunctionMap().get(str.getKey()));
      temp.add(str.getKey());
      temp.add(NOT_OK_CONST);
      temp.add("");
      dataList.add(temp);
      newCount++;
    }

    return newCount;
  }


  /**
   * @param entry
   * @param dataList
   * @param count
   * @param str
   * @return
   */
  private int createDataListForCssd(final Entry<Long, CompliReviewOutputData> entry, final List<List<String>> dataList,
      final int curCount, final Entry<String, String> str) {

    int newCount = curCount;

    if (str.getValue().equals(CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType())) {
      List<String> temp = new ArrayList<>();
      temp.add(entry.getKey() + "." + newCount);
      temp.add(this.inputModel.getParamFunctionMap().get(str.getKey()));
      temp.add(str.getKey());
      temp.add(NOT_OK_CONST);
      temp.add("");
      temp.add("");
      temp.add("");
      dataList.add(temp);
      newCount++;
    }
    else if (str.getValue().equals(CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getUiType())) {
      List<String> temp = new ArrayList<>();
      temp.add(entry.getKey() + "." + newCount);
      temp.add(this.inputModel.getParamFunctionMap().get(str.getKey()));
      temp.add(str.getKey());
      temp.add("No Rule");
      temp.add("");
      temp.add("");
      temp.add("");
      dataList.add(temp);
      newCount++;
    }

    return newCount;
  }

  /**
   * Method to return sorted
   *
   * @param map
   * @return
   */
  private Map<String, String> getSortedMap(final Map<String, String> compliResultMap,
      final Map<String, String> paramFuncMap) {

    Comparator<String> paramComparator = (p1, p2) -> {
      int funcCompare = ApicUtil.compare(paramFuncMap.get(p1), paramFuncMap.get(p2));
      return funcCompare == 0 ? ApicUtil.compare(p1, p2) : funcCompare;
    };

    Map<String, String> sortedCompliResultMap = new LinkedHashMap<>();
    compliResultMap.keySet().stream().sorted(paramComparator).collect(Collectors.toList())
        .forEach(param -> sortedCompliResultMap.put(param, compliResultMap.get(param)));

    return sortedCompliResultMap;

  }


  /**
   * @param resultFlag
   * @param cell
   * @param headerRow
   * @param headerList
   * @param baseTable
   */
  private void createQssdTableHeader(final List<String> headerList, final BaseTable baseTable,
      final QSSD_RESULT_FLAG resultFlag) {

    if (resultFlag.getUiType().equals(CDRConstants.QSSD_RESULT_FLAG.OK.getUiType())) {
      // Table header
      headerList.add("#");
      headerList.add(FC_NAME);
      headerList.add(PARAMETER_NAME);
      headerList.add(CHECK_RESULT);

      // Create Header row
      Row<PDPage> headerRow = baseTable.createRow(15f);

      // number
      Cell<PDPage> cell = headerRow.createCell(S_NO_COL_WIDTH, headerList.get(0));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);

      // FC name
      cell = headerRow.createCell(OK_FC_NAME_COL_WIDTH, headerList.get(1));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);


      // parameter name
      cell = headerRow.createCell(OK_PARAM_NAME_COL_WIDTH, headerList.get(2));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);


      // result
      cell = headerRow.createCell(OK_RESULT_COL_WIDTH, headerList.get(3));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      baseTable.addHeaderRow(headerRow);

    }
    else if (resultFlag.getUiType().equals(CDRConstants.QSSD_RESULT_FLAG.QSSD.getUiType())) {

      // add header row 1
      Row<PDPage> headerRow1 = baseTable.createRow(15f);

      headerRow1.createCell(S_NO_COL_WIDTH, "");
      headerRow1.createCell(QSSD_FC_NAME_COL_WIDTH, "");
      headerRow1.createCell(QSSD_PARAM_NAME_COL_WIDTH, "");
      headerRow1.createCell(QSSD_CHECK_RESULT_COL_WIDTH, "");
      Cell<PDPage> cell =
          headerRow1.createCell((float) (QSSD_BOSCH_COL_WIDTH + QSSD_CUST_THIRD_COL_WIDTH), "Label Responsibility");
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      cell.setColspanCell(true);
      cell.setAlign(HorizontalAlignment.CENTER);
      headerRow1.createCell(QSSD_REASON_COL_WIDTH, "");

      baseTable.addHeaderRow(headerRow1);

      // Table header
      headerList.add("#");
      headerList.add(FC_NAME);
      headerList.add(PARAMETER_NAME);
      headerList.add(CHECK_RESULT);
      headerList.add("Bosch");
      headerList.add("Customer or Third party");
      headerList.add("Reason for Release approval<br>" + "Detailed description");

      // Create Header row

      // number
      Row<PDPage> headerRow = baseTable.createRow(15f);
      Cell<PDPage> cell2 = headerRow.createCell(S_NO_COL_WIDTH, headerList.get(0));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // FC Name
      cell2 = headerRow.createCell(QSSD_FC_NAME_COL_WIDTH, headerList.get(1));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // Param name
      cell2 = headerRow.createCell(QSSD_PARAM_NAME_COL_WIDTH, headerList.get(2));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // Check Result
      cell2 = headerRow.createCell(QSSD_CHECK_RESULT_COL_WIDTH, headerList.get(3));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // Bosch
      cell2 = headerRow.createCell((float) QSSD_BOSCH_COL_WIDTH, headerList.get(4));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // Customer/ Third party
      cell2 = headerRow.createCell((float) QSSD_CUST_THIRD_COL_WIDTH, headerList.get(5));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // Reason for approval
      cell2 = headerRow.createCell(QSSD_REASON_COL_WIDTH, headerList.get(6));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      baseTable.addHeaderRow(headerRow);
    }

  }

  /**
   * @param resultFlag
   * @param cell
   * @param headerRow
   * @param headerList
   * @param baseTable
   */
  private void createTableHeader(final List<String> headerList, final BaseTable baseTable,
      final COMPLI_RESULT_FLAG resultFlag) {

    if (resultFlag == COMPLI_RESULT_FLAG.OK) {
      // Table header
      headerList.add("#");
      headerList.add(FC_NAME);
      headerList.add(PARAMETER_NAME);
      headerList.add(CHECK_RESULT);

      // Create Header row
      Row<PDPage> headerRow = baseTable.createRow(15f);

      // number
      Cell<PDPage> cell = headerRow.createCell(S_NO_COL_WIDTH, headerList.get(0));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);

      // FC name
      cell = headerRow.createCell(OK_FC_NAME_COL_WIDTH, headerList.get(1));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);

      // parameter name
      cell = headerRow.createCell(OK_PARAM_NAME_COL_WIDTH, headerList.get(2));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);

      // result
      cell = headerRow.createCell(OK_RESULT_COL_WIDTH, headerList.get(3));
      cell.setFont(PDType1Font.HELVETICA_BOLD);
      cell.setFontSize(8);
      baseTable.addHeaderRow(headerRow);
    }
    else if (resultFlag == COMPLI_RESULT_FLAG.CSSD) {

      // Table header
      headerList.add("#");
      headerList.add(FC_NAME);
      headerList.add("Parameter Name                          ");
      headerList.add(CHECK_RESULT);
      headerList.add("Not active (1)");
      headerList.add("Active (2)");

      String reasonCol = "Reason for Release approval<br>" + "In case of NOT active (1):<br>" +
          "Detailed description why not active<br>" + "In case of active (2):<br>" +
          "Include ONLY codex case (ALM) ID / opt. Link";

      headerList.add(reasonCol);

      // Create Header row

      // number
      Row<PDPage> headerRow = baseTable.createRow(15f);
      Cell<PDPage> cell2 = headerRow.createCell(S_NO_COL_WIDTH, headerList.get(0));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // FC Name
      cell2 = headerRow.createCell(C_SSD_FC_NAME_COL_WIDTH, headerList.get(1));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // Param name
      cell2 = headerRow.createCell(C_SSD_PARAM_NAME_COL_WIDTH, headerList.get(2));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // Check result
      cell2 = headerRow.createCell(C_SSD_RESULT_COL_WIDTH, headerList.get(3));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      // Not active
      cell2 = headerRow.createCell(C_SSD_NOT_ACTIVE_COL_WIDTH, headerList.get(4));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);
      cell2.setTextRotated(true);

      // Active
      cell2 = headerRow.createCell(C_SSD_ACTIVE_COL_WIDTH, headerList.get(5));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);
      cell2.setTextRotated(true);

      // Reason for approval
      cell2 = headerRow.createCell(C_SSD_REASON_COL_WIDTH, headerList.get(6));
      cell2.setFont(PDType1Font.HELVETICA_BOLD);
      cell2.setFontSize(8);

      baseTable.addHeaderRow(headerRow);
    }
    else if (resultFlag == COMPLI_RESULT_FLAG.SSD2RV) {
      // Table header
      headerList.add("#");
      headerList.add(FC_NAME);
      headerList.add("Parameter Name                          ");
      headerList.add(CHECK_RESULT);
      headerList.add("Reason for Release approval");

      // Create Header row

      // number
      Row<PDPage> headerRow = baseTable.createRow(15f);
      Cell<PDPage> ssd2rvCell = headerRow.createCell(S_NO_COL_WIDTH, headerList.get(0));
      ssd2rvCell.setFont(PDType1Font.HELVETICA_BOLD);
      ssd2rvCell.setFontSize(8);

      // FC Name
      ssd2rvCell = headerRow.createCell(SSD2RV_FC_NAME_COL_WIDTH, headerList.get(1));
      ssd2rvCell.setFont(PDType1Font.HELVETICA_BOLD);
      ssd2rvCell.setFontSize(8);

      // Param name
      ssd2rvCell = headerRow.createCell(SSD2RV_PARAM_NAME_COL_WIDTH, headerList.get(2));
      ssd2rvCell.setFont(PDType1Font.HELVETICA_BOLD);
      ssd2rvCell.setFontSize(8);

      // Check result
      ssd2rvCell = headerRow.createCell(SSD2RV_RESULT_COL_WIDTH, headerList.get(3));
      ssd2rvCell.setFont(PDType1Font.HELVETICA_BOLD);
      ssd2rvCell.setFontSize(8);

      // Reason for approval
      ssd2rvCell = headerRow.createCell(SSD2RV_REASON_COL_WIDTH, headerList.get(4));
      ssd2rvCell.setFont(PDType1Font.HELVETICA_BOLD);
      ssd2rvCell.setFontSize(8);

      baseTable.addHeaderRow(headerRow);
    }

  }


  /**
   * Method to write text with indentation
   *
   * @param text
   * @param indentSize
   * @throws IOException
   */
  private void displayTextWithIndent(final String text, final int indentSize, final int fontSize) throws IOException {
    int limit = 80;
    List<String> temp = PdfUtil.getText(text, limit);
    int line = 1;
    for (String tempStr : temp) {
      if (line > 1) {
        writeText(PdfUtil.getWhiteSpace(indentSize) + tempStr, PDType1Font.HELVETICA, fontSize, this.linePosition);
        line++;
      }
      else {
        writeText(PdfUtil.getWhiteSpace(0) + tempStr, PDType1Font.HELVETICA, fontSize, this.linePosition);
        line++;
      }
      this.linePosition -= 20;
    }
  }


  /**
   * @return logo as byte array
   * @throws DataException
   */
  private byte[] getBoschLogo() throws DataException {
    getLogger().debug("Retrieve report logo from DB");

    // Get logo from db
    IcdmFilesLoader fileLoader = new IcdmFilesLoader(getServiceData());
    Map<String, byte[]> fileMap = fileLoader.getFiles(-6L, CDRConstants.REPORT_LOGO_NODE_TYPE);
    byte[] reportLogo = null;
    if ((fileMap != null) && (fileMap.size() > 0)) {
      reportLogo = fileMap.get(CDRConstants.PDF_REPORT_BOSCH_LOGO_IMAGE);
      getLogger().debug("Report logo retrieved");
    }

    return reportLogo;
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
   * @param content - String content to be written
   * @param font - font
   * @param fontSize - size
   * @param linePosition1 - position to write
   * @param color - text color
   * @throws IOException - exception
   */
  public void writeText(final String content, final PDType1Font font, final int fontSize, final double linePosition1,
      final Color color)
      throws IOException {

    // if line position is beyond bottom margin of page, create new page and add content
    if (PdfUtil.isLinePositionWithinLimits(linePosition1)) {
      PDStreamUtils.write(this.currentStream, content, font, fontSize, (float) PdfUtil.LEFT_MARGIN,
          (float) linePosition1, color);
    }
    else {
      createNewPage();

      PDStreamUtils.write(this.currentStream, content, font, fontSize, (float) PdfUtil.LEFT_MARGIN,
          (float) this.linePosition, color);
    }
  }


}

