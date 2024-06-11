/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.apic.ws.rest.service.JsonParser;
import com.bosch.caltool.apic.ws.rest.service.JsonWriter;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.bo.a2l.CalDataFileLoader;
import com.bosch.caltool.icdm.bo.apic.SdomPverLoader;
import com.bosch.caltool.icdm.bo.apic.VcdmDataSetAllLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.HexFileHandler;
import com.bosch.caltool.icdm.bo.cdr.VCDMInterface;
import com.bosch.caltool.icdm.bo.cdr.compli.CompliReviewResultCommand;
import com.bosch.caltool.icdm.bo.compli.CompliDataReview;
import com.bosch.caltool.icdm.bo.compli.CompliReviewHexParamResult;
import com.bosch.caltool.icdm.bo.compli.CompliReviewInputDataProvider;
import com.bosch.caltool.icdm.bo.compli.CompliRvwBoInput;
import com.bosch.caltool.icdm.bo.compli.CompliRvwInputVcdmDst;
import com.bosch.caltool.icdm.bo.report.compli.ComplianceReportInputModel;
import com.bosch.caltool.icdm.bo.report.compli.ComplianceReviewReport;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.EASEELogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CompliDstInput;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewOutputData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewResponse;
import com.bosch.caltool.icdm.model.cdr.CompliRvwMetaDataWithDstInp;
import com.bosch.caltool.icdm.model.cdr.VcdmHexFileData;
import com.bosch.caltool.icdm.model.cdr.compli.CompliReviewResult;
import com.bosch.caltool.icdm.model.cdr.compli.CompliReviewResultHex;


/**
 * Get the compliance parameter of an A2L file with configured SSD Class
 *
 * @author svj7cob
 */
// Task 263282
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_COMPLI_REVIEW)
public class CompliReviewService extends AbstractRestService {


  /**
   *
   */
  private static final String ERR_CODE_A2L_MISSING = "COMPLI_REVIEW.A2L_MISSING";
  /**
   *
   */
  private static final String COMPLI_REVIEW_FOLDER_NAME = "CompliReview_";
  /**
   *
   */
  private static final String COMPLI_RESULT_JSON_FILE_NAME = "CompliReviewResult.json";
  /**
  *
  */
  private boolean isNONSDOMBCRelease;

  /**
   * Run compli review (invoked from iCDM/externally, using all inputs as file)
   *
   * @param multiPart multipart data
   * @return service response as a zip file
   * @throws IcdmException error during service
   */
  @POST
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @CompressData
  public Response compliReviewResponse(final FormDataMultiPart multiPart) throws IcdmException {

    long startTime = System.currentTimeMillis();

    // get the a2l file
    List<FormDataBodyPart> a2lFileInput = multiPart.getFields(WsCommonConstants.A2L_FILE_MULTIPART);
    if (CommonUtils.isNullOrEmpty(a2lFileInput)) {
      throw new InvalidInputException(ERR_CODE_A2L_MISSING);
    }
    String a2lFileName = null;
    InputStream a2lInputStream = null;
    // get the A2l Stream
    for (FormDataBodyPart field : a2lFileInput) {
      a2lInputStream = field.getValueAs(InputStream.class);
      a2lFileName = getUTFFilePath(field.getContentDisposition().getFileName());
    }
    return performCompliReview(multiPart, startTime, a2lFileName, a2lInputStream, null);
  }


  /**
   * Run compli review using PIDC A2L ID (invoked from iCDM's PIDC tree view)
   *
   * @param multiPart multipart data
   * @param pidcA2lId PIDC A2L Mapping ID
   * @return service response as a zip file
   * @throws IcdmException error during service
   */
  @POST
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_COMPLI_REVIEW_PIDC_A2LID)
  @CompressData
  public Response compliReview(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final String pidcA2lId)
      throws IcdmException {
    long startTime = System.currentTimeMillis();
    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(Long.valueOf(pidcA2lId));
    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(pidcA2l.getA2lFileId());
    // download the a2l file if not available already and get the file path
    String filePath = new A2LFileInfoProvider(getServiceData()).getA2lFilePath(a2lFile);

    try (InputStream a2lInputStream = new FileInputStream(new File(filePath))) {
      return performCompliReview(multiPart, startTime, pidcA2l.getName(), a2lInputStream, pidcA2l.getA2lFileId());
    }
    catch (IOException exp) {
      throw new InvalidInputException(ERR_CODE_A2L_MISSING, exp);
    }
  }

  /**
   * Run compli review using vcdm dst id (invoked from Data checker tool)
   *
   * @param compliDstInp input model to store input from data checker
   * @return servcie response as zip file
   * @throws IcdmException error during service
   */
  @POST
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Path(WsCommonConstants.RWS_VCDM_DST_ID)
  @CompressData
  public Response compliReviewDstInput(final CompliDstInput compliDstInp) throws IcdmException {

    long startTime = System.currentTimeMillis();
    Long vcdmDstId = compliDstInp.getVcdmDstId();

    getLogger().debug("Validating inputs to the POST:compliReviewDstInput service  : start");
    validateDstInput(compliDstInp, vcdmDstId);

    A2LFile a2lFile;
    Long pidcA2lId = compliDstInp.getPidcA2lId();
    String a2lFileName = "";
    // Get the A2l file in Vcdm dst
    if (null != pidcA2lId) {
      getLogger().debug("Pidc a2l id is available as input.Downloading A2l file having pidc a2l id {}  : start",
          pidcA2lId);
      PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId);
      a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(pidcA2l.getA2lFileId());
      a2lFileName = a2lFile.getFilename();
    }
    else {
      // get a2l file id of a2l in dst when pidc a2l is not available as input
      getLogger().debug(
          "Pidc a2l id is not available as input.Downloading A2l file from vcdm dst with dst id {} : start", vcdmDstId);
      List<Long> a2lFileInfoByDstId = new CalDataFileLoader(getServiceData()).getA2lFileInfoByDstId(vcdmDstId);
      if (a2lFileInfoByDstId.isEmpty()) {
        throw new InvalidInputException(ERR_CODE_A2L_MISSING);
      }
      a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(a2lFileInfoByDstId.get(0));
      a2lFileName = a2lFile.getFilename();
    }


    // download the a2l file if not available already and get the file path
    String filePath = new A2LFileInfoProvider(getServiceData()).getA2lFilePath(a2lFile);

    getLogger().debug("A2l File downloaded successfully : end");
    getLogger().debug("Compliance Review Service with DST input:start");
    try (InputStream a2lInputStream = new FileInputStream(new File(filePath))) {
      return performCompliRvwWithDstInp(startTime, createMetaDataWithDstInp(compliDstInp, a2lFile), a2lInputStream,
          a2lFile.getId(), a2lFileName);
    }
    catch (IOException exp) {
      throw new InvalidInputException(ERR_CODE_A2L_MISSING, exp);
    }
  }


  /**
   * @param compliDstInp
   * @param vcdmDstId
   * @throws InvalidInputException
   * @throws UnAuthorizedAccessException
   */
  private void validateDstInput(final CompliDstInput compliDstInp, final Long vcdmDstId)
      throws InvalidInputException, UnAuthorizedAccessException {
    if (CommonUtils.isNull(vcdmDstId)) {
      // Group name for error code-COMPLI_REVIEW,name of error code-DST_MISSING
      throw new InvalidInputException("COMPLI_REVIEW.DST_MISSING");
    }

    if (CommonUtils.isNull(compliDstInp.getPidcElementId())) {
      // Group name for error code-FEAVAL,name of error code-PIDC_ELEMENT_MISSING
      throw new InvalidInputException("FEAVAL.PIDC_ELEMENT_MISSING");
    }

    if (!new VcdmDataSetAllLoader(getServiceData()).isValidId(vcdmDstId)) {
      // Group name for error code-COMPLI_REVIEW,name of error code-vCDM_DST_INVALID
      throw new InvalidInputException("COMPLI_REVIEW.VCDM_DST_INVALID");
    }

    // validate if necessary inputs are present for checked-out datasets
    if (!compliDstInp.isCheckedIn() &&
        (CommonUtils.isNull(compliDstInp.getAprjName()) || CommonUtils.isNull(compliDstInp.getAprjRevision()) ||
            CommonUtils.isNull(compliDstInp.getDstName()) || CommonUtils.isNull(compliDstInp.getDstRevision()))) {
      throw new InvalidInputException("COMPLI_REVIEW.DST_INPUT_MISSING_CHECKED_OUT_DST");
    }


  }


  /**
   * @param compliDstInp
   * @param a2lFile
   * @param metaData
   * @param pidcA2lId
   */
  private CompliRvwMetaDataWithDstInp createMetaDataWithDstInp(final CompliDstInput compliDstInp,
      final A2LFile a2lFile) {
    // get the input meta data
    CompliRvwMetaDataWithDstInp metaData = new CompliRvwMetaDataWithDstInp();
    metaData.setPverName(a2lFile.getSdomPverName());
    metaData.setPverRevision(String.valueOf(a2lFile.getSdomPverRevision()));
    metaData.setPverVariant(a2lFile.getSdomPverVariant());
    metaData.setA2lFileName(a2lFile.getFilename());
    metaData.setPidcA2L(compliDstInp.getPidcA2lId());
    metaData.setVcdmDstId(compliDstInp.getVcdmDstId());
    metaData.setAprjName(compliDstInp.getAprjName());
    metaData.setAprjRevision(compliDstInp.getAprjRevision());
    metaData.setCheckedIn(compliDstInp.isCheckedIn());
    metaData.setDstName(compliDstInp.getDstName());
    metaData.setDstRevision(compliDstInp.getDstRevision());
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, compliDstInp.getPidcElementId());
    metaData.setHexFilePidcElement(hexFilePidcElement);
    return metaData;
  }


  /**
   * @param startTime
   * @param metaDataWithDstInp
   * @param a2lInputStream
   * @param a2lFileName
   * @param id
   * @return
   * @throws IcdmException
   * @throws IOException
   */
  private Response performCompliRvwWithDstInp(final long startTime,
      final CompliRvwMetaDataWithDstInp metaDataWithDstInp, final InputStream a2lInputStream, final Long a2lFileId,
      final String a2lFileName)
      throws IcdmException {
    CompliDataReview dataReview = new CompliDataReview(getServiceData());
    // get the hex files from vcdm dst revision node

    VCDMInterface vcdmInterface = null;
    try {
      vcdmInterface = new VCDMInterface(EASEELogger.getInstance());
    }
    catch (Exception exp) {
      throw new DataException("VCDM Webservice login failed. Opening A2L Files is not possible. " + exp.getMessage(),
          exp);
    }
    String hexFilePath = CompliDataReview.SERVER_PATH + File.separator + "hex_download" + "_" + startTime;

    VcdmHexFileData hexFileData = vcdmInterface.exportHexFromDst(hexFilePath, metaDataWithDstInp);

    Map<String, byte[]> hexByteMap = new HashMap<>();
    Map<String, byte[]> hexByteMapCheckSum = new HashMap<>();
    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, hexFileData.getFileName());
    metaDataWithDstInp.setHexfileIdxMap(hexfileIdxMap);


    try {
      if (CommonUtils.isNull(hexFileData)) {
        throw new InvalidInputException("COMPLI_REVIEW.HEX_MISSING");
      }
      // get the Hex file Stream
      ConcurrentMap<String, InputStream> hexFileInputStreamMap = new ConcurrentHashMap<>();
      InputStream hexFileInputStream = new ByteArrayInputStream(hexFileData.getHexFileBytes());
      hexFileInputStreamMap.put(getUTFFilePath(hexFileData.getFileName()), hexFileInputStream);

      hexByteMap.put(hexFileData.getFileName(), hexFileData.getHexFileBytes());
      hexByteMapCheckSum.put(hexFileData.getFileName().toLowerCase(Locale.getDefault()), hexFileData.getHexFileBytes());
      CompliRvwBoInput compliRvwBoInput = new CompliRvwBoInput(a2lFileName, a2lFileId, metaDataWithDstInp, dataReview,
          hexFileInputStreamMap, hexByteMapCheckSum, IOUtils.toByteArray(a2lInputStream));
      return performRvwNCreateFiles(startTime, compliRvwBoInput);
    }
    catch (IcdmException exp) {
      if (null != dataReview.getCurrentTimeStamp()) {
        exp.addAdditinalInfo(WsCommonConstants.RESP_HEADER_COMPLI_RVW_ID,
            FilenameUtils.getBaseName(dataReview.getCurrentTimeStamp()));
      }
      throw exp;
    }
    catch (IOException exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
  }


  /**
   * @param multiPart
   * @param startTime
   * @param a2lFile
   * @param a2lInputStream
   * @param a2lFileId
   * @return
   * @throws InvalidInputException
   * @throws UnAuthorizedAccessException
   * @throws IcdmException
   */
  private Response performCompliReview(final FormDataMultiPart multiPart, final long startTime,
      final String a2lFileName, final InputStream a2lInputStream, final Long a2lFileId)
      throws IcdmException {

    CompliReviewInputMetaData srvInput;
    CompliDataReview dataReview = new CompliDataReview(getServiceData());
    try {
      // get the hex files
      List<FormDataBodyPart> hexFileInput = multiPart.getFields(WsCommonConstants.HEX_FILE_MUTIPART);
      if (CommonUtils.isNullOrEmpty(hexFileInput)) {
        throw new InvalidInputException("COMPLI_REVIEW.HEX_MISSING");
      }
      // get the Hex file Stream
      ConcurrentMap<String, InputStream> hexFileInputStreamMap = new ConcurrentHashMap<>();
      Map<String, byte[]> hexByteMap = new HashMap<>();
      Map<String, byte[]> hexByteMapCheckSum = new HashMap<>();
      byte[] a2lBytes;
      for (FormDataBodyPart field : hexFileInput) {
        ContentDisposition contentDisposition = field.getContentDisposition();
        String utfFilePath = getUTFFilePath(contentDisposition.getFileName());
        hexFileInputStreamMap.put(utfFilePath, field.getValueAs(InputStream.class));
        String hexFileName = contentDisposition.getFileName();
        byte[] hexFileBytes = IOUtils.toByteArray(field.getValueAs(InputStream.class));
        if (hexFileName.contains("zip")) {
          hexFileName = hexFileName.substring(0, hexFileName.length() - 8) + ".hex";
        }
        Map<String, byte[]> unzipHexFileMap = ZipUtils.unzipIfZipped(hexFileBytes, hexFileName);
        for (Entry<String, byte[]> unzipHexFile : unzipHexFileMap.entrySet()) {
          hexByteMap.put(unzipHexFile.getKey(), unzipHexFile.getValue());
          hexByteMapCheckSum.put(unzipHexFile.getKey().toLowerCase(Locale.getDefault()), unzipHexFile.getValue());
        }
      }

      // get the web flow Json file
      List<FormDataBodyPart> webFlowInput = multiPart.getFields(WsCommonConstants.META_DATA);
      if (CommonUtils.isNullOrEmpty(webFlowInput)) {
        throw new InvalidInputException("COMPLI_REVIEW.JSON_MISSING");
      }
      InputStream jsonInputStream = null;
      // get the Json file Stream
      for (FormDataBodyPart field : webFlowInput) {
        jsonInputStream = field.getValueAs(InputStream.class);
      }

      // Parse json input file
      JsonParser parser = new JsonParser();
      srvInput = parser.parseStreamToObject(jsonInputStream, CompliReviewInputMetaData.class);
      getLogger().info("JSON input - {}", srvInput);
      a2lBytes = IOUtils.toByteArray(a2lInputStream);
      CompliRvwBoInput compliRvwBoInput = new CompliRvwBoInput(a2lFileName, a2lFileId, srvInput, dataReview,
          hexFileInputStreamMap, hexByteMapCheckSum, a2lBytes);
      return performRvwNCreateFiles(startTime, compliRvwBoInput);
    }
    catch (IcdmException exp) {
      if (null != dataReview.getCurrentTimeStamp()) {
        exp.addAdditinalInfo(WsCommonConstants.RESP_HEADER_COMPLI_RVW_ID,
            FilenameUtils.getBaseName(dataReview.getCurrentTimeStamp()));
      }
      throw exp;
    }
    catch (IOException exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
  }


  /**
   * @param startTime
   * @param a2lFileName
   * @param a2lFileId
   * @param srvInput
   * @param dataReview
   * @param compliReviewHexParamResult
   * @param hexFileInputStreamMap
   * @param hexByteMapCheckSum
   * @param a2lBytes
   * @return
   * @throws IcdmException
   */
  private Response performRvwNCreateFiles(final long startTime, final CompliRvwBoInput compliRvwBoInput)
      throws IcdmException {
    CompliReviewResponse reviewOutput;
    List<Boolean> isNonSDOMBCRelease = new ArrayList<>();
    // Store Compli Review of Hex File Param Results
    CompliReviewHexParamResult compliReviewHexParamResult = new CompliReviewHexParamResult();
    // Validate and Fill input
    getLogger().debug("Validating input");
    String dbZipFolderPath = validateAndFillInput(compliRvwBoInput.getDataReview(), compliRvwBoInput.getA2lBytes(),
        getUTFFilePath(compliRvwBoInput.getA2lFileName()), compliRvwBoInput.getHexFileInputStreamMap(),
        compliRvwBoInput.getSrvInput(), compliRvwBoInput.getHexByteMapCheckSum(), isNonSDOMBCRelease);
    this.isNONSDOMBCRelease = isNonSDOMBCRelease.get(0);
    // Perform compli review
    getLogger().debug("Performing compliance review");
    reviewOutput = compliRvwBoInput.getDataReview().performReview(compliRvwBoInput.getSrvInput(),
        compliReviewHexParamResult, compliRvwBoInput.getHexByteMapCheckSum(), this.isNONSDOMBCRelease);

    return createOutputFiles(compliRvwBoInput.getSrvInput(), compliRvwBoInput.getDataReview(), reviewOutput, startTime,
        compliReviewHexParamResult, compliRvwBoInput.getA2lFileId(), dbZipFolderPath);
  }


  /*
   * Validate input files with json meta data
   */

  private String validateAndFillInput(final CompliDataReview dataReview, final byte[] a2lBytes,
      final String a2lFileName, final Map<String, InputStream> hexFileInputStreams,
      final CompliReviewInputMetaData inputMetaData, final Map<String, byte[]> hexByteMap,
      final List<Boolean> isNonSDOMBCRelease)
      throws IcdmException {
    // get Unzipped Stream map
    Map<String, InputStream> unZippedA2lStreamMap =
        ZipUtils.unzipIfZipped(new ByteArrayInputStream(a2lBytes), a2lFileName);
    String unzippedFileName = null;
    InputStream unzippedStream = null;
    // get the first element. Since Single A2l
    for (Entry<String, InputStream> unzippedA2lStream : unZippedA2lStreamMap.entrySet()) {
      unzippedFileName = unzippedA2lStream.getKey();
      unzippedStream = unzippedA2lStream.getValue();
    }
    // get unzipped HEx stream
    Map<String, InputStream> unZippedHexStream = HexFileHandler.unZipIfZippedStream(hexFileInputStreams);
    // Validate Input File

    dataReview.validateInputFiles(unzippedStream, unZippedHexStream, inputMetaData, unzippedFileName,
        isNonSDOMBCRelease);

    // Create Input Data Folder
    String inputDataPath = dataReview.createInputDataFolder(a2lBytes, hexByteMap, a2lFileName);

    // Create JSON file

    getLogger().debug("Creating json file : {}", ApicConstants.COMPLI_INPUT_METADATA_JSON_FILE_NAME);
    if (inputMetaData instanceof CompliRvwMetaDataWithDstInp) {
      // Dst meta data
      JsonWriter.createJsonFile((CompliRvwMetaDataWithDstInp) inputMetaData, inputDataPath,
          ApicConstants.COMPLI_INPUT_METADATA_JSON_FILE_NAME);
    }
    else {
      // web flow meta data
      JsonWriter.createJsonFile(inputMetaData, inputDataPath, ApicConstants.COMPLI_INPUT_METADATA_JSON_FILE_NAME);
    }

    getLogger().debug("Json file created");

    // Fill input data
    dataReview.fillInputData(unzippedStream, unZippedHexStream, unzippedFileName);

    return inputDataPath;
  }

  /**
   * @param a2lFileName
   * @param rvwInputMetadata
   * @param dataReview
   * @param reviewOutput
   * @param startTime
   * @param compliReviewHexParamResult
   * @param a2lFileId
   * @param dbZipFolderPath
   * @param isNonSDOMBCRelease
   * @return
   * @throws IcdmException
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private Response createOutputFiles(final CompliReviewInputMetaData rvwInputMetadata,
      final CompliDataReview dataReview, final CompliReviewResponse reviewOutput, final long startTime,
      final CompliReviewHexParamResult compliReviewHexParamResult, final Long a2lFileId, final String dbZipFolderPath)
      throws IcdmException {

    try {
      getLogger().info("Creating output files...");

      String repFileName = "ComplianceReviewReport_" + ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_17) + ".pdf";
      reviewOutput.setReportFileName(repFileName);
      if (this.isNONSDOMBCRelease) {
        reviewOutput.setIsNONSDOMPVER("Y");
      }
      else {
        reviewOutput.setIsNONSDOMPVER("N");
      }
      // Create JSON file
      getLogger().debug("Creating json file : {}", COMPLI_RESULT_JSON_FILE_NAME);
      JsonWriter.createJsonFile(reviewOutput, dataReview.getFolderPath(), COMPLI_RESULT_JSON_FILE_NAME);
      getLogger().debug("Json file created");

      // create Zip file
      String zipfileName = FilenameUtils.getBaseName(dataReview.getFolderPath()) + ".zip";
      getLogger().debug("zipfileName : " + zipfileName);


      // Construct input model for PDF report generation
      ComplianceReportInputModel compliRepInputMdl = new ComplianceReportInputModel();
      compliRepInputMdl.setAttrValueModMap(dataReview.getAttrValModelMap());
      compliRepInputMdl.setCompliReviewOutput(reviewOutput.getHexFileOutput());
      compliRepInputMdl.setCompliReviewInputMetaData(rvwInputMetadata);
      compliRepInputMdl.setPidcElementNameMap(dataReview.getPidcElementNameMap());
      compliRepInputMdl.setDestFilePath(dataReview.getFolderPath() + File.separator + repFileName);
      compliRepInputMdl.setOutputZipFileName(FilenameUtils.getBaseName(dbZipFolderPath));
      // set param function map
      compliRepInputMdl
          .setParamFunctionMap(getParamFunctionMap(dataReview.getA2lFileInfo(), reviewOutput.getHexFileOutput()));
      String executionID = FilenameUtils.getBaseName(dataReview.getCurrentTimeStamp());
      compliRepInputMdl.setExecutionID(executionID);
      // Create PDF report
      ComplianceReviewReport report = new ComplianceReviewReport(getServiceData(), compliRepInputMdl);
      report.createPdf(this.isNONSDOMBCRelease);

      // create zip file with a2l,hex ,pdf and response to save in db
      String outputZipfileName = createZipFile(zipfileName, dataReview);
      String zipFilePath = CompliDataReview.SERVER_PATH + zipfileName;

      // Compress output files to a single zip file
      ZipUtils.zip(Paths.get(dataReview.getFolderPath()), Paths.get(zipFilePath));
      getLogger().debug("Compressed output to zip file : {},{}", dataReview.getFolderPath(), Paths.get(zipFilePath));

      // Delete zip file's original folder
      File file = new File(dataReview.getFolderPath());
      getLogger().debug("Delete zip file's original folder (Itr 2 from createOutputFiles()){}", file.getAbsolutePath());
      delete(file);
      getLogger().debug("Deleted zip file's original folder : {}", file.getAbsolutePath());

      getLogger().info("Output files creation completed.");

      // Create service response
      getLogger().info("Building service response using zip file {} ...", zipFilePath);
      ResponseBuilder response = Response.ok(new File(zipFilePath));
      // set the Response file name
      response.header("Content-Disposition", "attachment; filename=" + zipfileName);
      // set input file path id
      response.header(WsCommonConstants.RESP_HEADER_COMPLI_RVW_ID, executionID);

      getLogger().info("Service response building completed");

      // invoke command to save the results
      long totalTime = System.currentTimeMillis() - startTime;
      invokeCmdToSaveResult(reviewOutput, rvwInputMetadata, totalTime, outputZipfileName, compliReviewHexParamResult,
          a2lFileId);
      return response.build();
    }
    catch (IOException exp) {
      throw new IcdmException("COMPLI_REVIEW.OUTPUT_FAILED", exp);
    }
  }


  private Map<String, String> getParamFunctionMap(final A2LFileInfo a2lFileInfo,
      final Map<Long, CompliReviewOutputData> compliReviewOutputDataMap) {
    Map<String, Characteristic> characteristicsMap = a2lFileInfo.getAllModulesLabels();
    Map<String, String> allParamFunctionMap = new HashMap<>();
    for (CompliReviewOutputData compliReviewOutputData : compliReviewOutputDataMap.values()) {

      Collection<String> paramCollection = new HashSet<>();

      paramCollection.addAll(compliReviewOutputData.getCompliResult().keySet());
      paramCollection.addAll(compliReviewOutputData.getQssdResult().keySet());

      Map<String, String> paramFuncMap = getParamFunctionMapForHex(characteristicsMap, paramCollection);
      allParamFunctionMap.putAll(paramFuncMap);
    }
    return allParamFunctionMap;
  }

  private Map<String, String> getParamFunctionMapForHex(final Map<String, Characteristic> characteristicsMap,
      final Collection<String> paramCollection) {
    Map<String, String> paramFunctionMap = new HashMap<>();
    for (String param : paramCollection) {
      Characteristic characteristic = characteristicsMap.get(param);
      if ((characteristic != null) && (characteristic.getDefFunction() != null)) {
        paramFunctionMap.put(param, characteristic.getDefFunction().getName());
      }
      else {
        paramFunctionMap.put(param, "-");
      }
    }

    return paramFunctionMap;
  }

  /**
   * @param zipfileName
   * @param dataReview
   * @throws IOException
   * @throws IcdmException
   * @throws UnAuthorizedAccessException
   */
  private String createZipFile(final String zipfileName, final CompliDataReview dataReview) throws IOException {

    File rootDir = new File(CompliDataReview.SERVER_PATH);

    if (!rootDir.exists()) {
      rootDir.mkdir();
    }

    String folderPath = rootDir.getAbsolutePath() + File.separator +
        FilenameUtils.getBaseName(dataReview.getFolderPath()).replace(COMPLI_REVIEW_FOLDER_NAME, "");
    String zipFilePath = folderPath + File.separator + zipfileName;

    getLogger().debug("zip file path : {}", zipFilePath);

    // Compress output files to a single zip file
    ZipUtils.zip(Paths.get(dataReview.getFolderPath()), Paths.get(zipFilePath));

    String outputZipfileName = FilenameUtils.getBaseName(folderPath) + ".zip";
    String outputZipFilePath = CompliDataReview.SERVER_PATH + File.separator + outputZipfileName;
    getLogger().debug("outputZipFilePath : " + outputZipFilePath);

    ZipUtils.zip(Paths.get(folderPath), Paths.get(outputZipFilePath));

    // Delete zip file's original folder
    getLogger().debug("Delete zip file's original folder (Itr 1 from createZipFile()){}", folderPath);
    delete(new File(folderPath));
    getLogger().debug("Folder deletion completed");

    getLogger().info("Output zip file creation completed. File : {}", outputZipfileName);

    return outputZipfileName;
  }


  /**
   * @param folderPath
   */
  // TODO any common util method available?
  private void delete(final File file) {
    if (file.isDirectory()) {

      // If the directory is not empty, the delete all children first
      if (file.list().length != 0) {
        // recursive delete
        getLogger().debug(" Deleting children of Dir - {}", file.getName());
        Arrays.stream(file.list()).map(c -> new File(file, c)).forEach(this::delete);
      }

      // check the directory again, if empty then delete it
      if (file.list().length == 0) {
        delete(file.toPath());
        getLogger().debug("  Deleted Dir - {}", file.getName());
      }
    }
    else {
      // if file, then delete it
      delete(file.toPath());
      getLogger().debug("  Deleted File - {}", file.getName());
    }

  }

  private void delete(final java.nio.file.Path path) {
    try {
      Files.delete(path);
    }
    catch (IOException e) {
      getLogger().info(e.getMessage(), e);
    }
  }

  /**
   * @param compliRvwJsonResp
   * @param inputMetaData
   * @param totalTime
   * @param outputZipfileName
   * @param compliReviewHexParamResult
   * @param a2lFileId
   * @throws IcdmException
   */
  private void invokeCmdToSaveResult(final CompliReviewResponse compliRvwJsonResp,
      final CompliReviewInputMetaData inputMetaData, final long totalTime, final String outputZipfileName,
      final CompliReviewHexParamResult compliReviewHexParamResult, final Long a2lFileId)
      throws IcdmException {

    getLogger().info("Storing compliance review results in DB...");

    CompliReviewResult compliResult = new CompliReviewResult();
    compliResult.setA2lFileId(a2lFileId);
    compliResult.setA2lFileName(inputMetaData.getA2lFileName());
    compliResult.setSdomPverName(inputMetaData.getPverName());
    if (!inputMetaData.getPverRevision().isEmpty()) {
      compliResult.setSdomPverRevision(Long.valueOf(inputMetaData.getPverRevision()));
    }
    compliResult.setSdomPverVariant(inputMetaData.getPverVariant());
    compliResult
        .setNumCompli(compliRvwJsonResp.getCompliParams().isEmpty() ? 0L : compliRvwJsonResp.getCompliParams().size());
    compliResult
        .setNumQssd(compliRvwJsonResp.getQssdParams().isEmpty() ? 0L : compliRvwJsonResp.getQssdParams().size());
    compliResult.setTimeUsed(totalTime);
    compliResult.setWebFlowJobId(inputMetaData.getWebflowID());


    Map<Long, CompliReviewResultHex> compliResultHexMap = new HashMap<>();
    setResultValues(compliRvwJsonResp, inputMetaData, compliResult, compliResultHexMap);
    // invoke the command to save into db
    CompliReviewResultCommand cmd = new CompliReviewResultCommand(getServiceData(), compliResult);
    cmd.setOutputZipfileName(outputZipfileName);
    cmd.setCompliResultHexMap(compliResultHexMap);
    cmd.setCompliRvwHexParamMap(compliReviewHexParamResult.getCompliRvwHexParamMap());

    executeCommand(cmd);

    getLogger().info("Compliance review results saved to DB. Result Master ID : {}", cmd.getObjId());
  }


  /**
   * @param compliRvwResponse
   * @param inputMetaData
   * @param compliResult
   * @param compliResultHexMap
   * @param compliRvwHexParamMap
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  // TODO move this to the BO layer
  private void setResultValues(final CompliReviewResponse compliRvwResponse,
      final CompliReviewInputMetaData inputMetaData, final CompliReviewResult compliResult,
      final Map<Long, CompliReviewResultHex> compliResultHexMap)
      throws IcdmException {

    getLogger().info("Computing results... ");
    Map<String, Long> hexFileDstIdMap = new HashMap<>();
    if (inputMetaData instanceof CompliRvwMetaDataWithDstInp) {
      // DST meta data
      String hexIndexNum = "1";
      hexFileDstIdMap.put(hexIndexNum, ((CompliRvwMetaDataWithDstInp) inputMetaData).getVcdmDstId());
    }

    else if (this.isNONSDOMBCRelease) {
      // incase of non sdom pver we call the below as it wont have revision and variant info
      hexFileDstIdMap.clear();
    }

    else {
      // webflow meta data
      CompliReviewInputMetaData webFlowInpMetaData = inputMetaData;
      CompliRvwInputVcdmDst dstFiller = new CompliRvwInputVcdmDst(webFlowInpMetaData, getServiceData());
      dstFiller.fillDstMap();
      hexFileDstIdMap = dstFiller.getHexFileDstIdMap();
    }


    // For each hex file, compute the review status
    for (Entry<Long, CompliReviewOutputData> reviewOutput : compliRvwResponse.getHexFileOutput().entrySet()) {
      CompliReviewResultHex hexObj = new CompliReviewResultHex();

      long noRuleFail = 0L;
      long ssdFail = 0L;
      long cssdFail = 0L;
      long resultOK = 0L;

      for (String prmResult : reviewOutput.getValue().getCompliResult().values()) {
        COMPLI_RESULT_FLAG flag = COMPLI_RESULT_FLAG.getTypeFromUiType(prmResult);

        switch (flag) {
          case NO_RULE:
            noRuleFail++;
            break;
          case CSSD:
            cssdFail++;
            break;
          case SSD2RV:
            ssdFail++;
            break;
          case OK:
            resultOK++;
            break;
          default:
            break;
        }
      }

      String hexfileName = reviewOutput.getValue().getHexfileName();

      hexObj.setHexFileName(hexfileName);
      hexObj.setIndexNum(reviewOutput.getKey());
      hexObj.setCompliNoRule(noRuleFail);
      hexObj.setCssdFail(cssdFail);
      hexObj.setSsd2rvFail(ssdFail);
      hexObj.setResultOk(resultOK);
      // qssd
      hexObj.setQssdFail(Long.valueOf(reviewOutput.getValue().getQssdTotalFailCount()));
      hexObj.setQssdOk(Long.valueOf(reviewOutput.getValue().getQssdPassCount()));

      hexObj.setSsdFileName(reviewOutput.getValue().getSsdFileName());
      String checkSsdReportName = reviewOutput.getValue().getCheckSSDfileNames().stream()
          .filter(reportName -> !reportName.contains("External")).findFirst().orElse(null);
      hexObj.setCheckSsdReportName(checkSsdReportName);
      hexObj.setReleaseId(reviewOutput.getValue().getReleaseId());
      hexObj.setHexChecksum(reviewOutput.getValue().getHexChecksum());
      Long vcdmDstId = hexFileDstIdMap.get(hexObj.getIndexNum().toString());
      hexObj.setVcdmDstId(vcdmDstId);
      setPIDCElementInResult(inputMetaData, compliResult, hexObj);
      compliResult.setStatus(getStatusCompliResult(noRuleFail, ssdFail, cssdFail));
      compliResultHexMap.put(reviewOutput.getKey(), hexObj);

      getLogger().info("Hex File {} - Results : Passed = {}, CSSD Failed = {}, SSD2Rv Failed = {}, NoRule Failed = {}",
          hexfileName, resultOK, cssdFail, ssdFail, noRuleFail);

    }

    getLogger().info("Result computation completed");
  }

  /**
   * @param webFlowJsonInput
   * @param compliResult
   * @param hexObj
   * @param varLoader
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void setPIDCElementInResult(final CompliReviewInputMetaData webFlowJsonInput,
      final CompliReviewResult compliResult, final CompliReviewResultHex hexObj)
      throws IcdmException {

    Long objId = webFlowJsonInput.getHexFilePidcElement().get(hexObj.getIndexNum());

    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    if (varLoader.isValidId(objId)) {
      PidcVariant variant = varLoader.getDataObjectByID(objId);
      hexObj.setVariantId(variant.getId());
      compliResult.setPidcVersionId(variant.getPidcVersionId());
    }
    else if (new PidcVersionLoader(getServiceData()).isValidId(objId)) {
      compliResult.setPidcVersionId(objId);
    }

  }

  /**
   * @param noRuleFail
   * @param ssdFail
   * @param cssdFail
   * @return
   */
  private String getStatusCompliResult(final Long noRuleFail, final Long ssdFail, final Long cssdFail) {
    return (noRuleFail > 0) || (cssdFail > 0) || (ssdFail > 0) ? "F" : "C";
  }

  /**
   * @param executionId reference id from comp hex service
   * @return the downloaded files
   * @throws IcdmException error during webservice call
   */
  @GET
  @Path(WsCommonConstants.RWS_DOWNLOAD_ALL)
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response importCompliReviewInputData(
      @QueryParam(WsCommonConstants.RWS_QP_EXECUTION_ID) final String executionId)
      throws IcdmException {
    CompliReviewInputDataProvider inputDataFetcher = new CompliReviewInputDataProvider(getServiceData());
    // Create service response
    ResponseBuilder response = Response.ok(inputDataFetcher.getInputData(executionId));
    return response.build();
  }

  /**
   * @param pverName pverName
   * @return isNONSDOM value
   * @throws IcdmException error during webservice call
   */
  @GET
  @Path(WsCommonConstants.CHECK_IS_NON_SDOM)
  @Produces({ MediaType.TEXT_PLAIN })
  @CompressData
  public Response checkIsNONSDOM(@QueryParam(WsCommonConstants.PVER_NAME) final String pverName) throws IcdmException {
    SdomPverLoader pvrLdr = new SdomPverLoader(getServiceData());
    SortedSet<String> pverVariantNamesSet = pvrLdr.getPverVariantNames(pverName);
    // Create service response
    ResponseBuilder response = Response.ok(Boolean.toString(pverVariantNamesSet.isEmpty()));
    return response.build();
  }


}
