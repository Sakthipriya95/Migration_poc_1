/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.comphex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.SdomPverLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.comphex.CompHexWithCDFxProcess;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.comphex.CompHexMetaData;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;

/**
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_COMPHEX + "/" + WsCommonConstants.RWS_REVIEW)
public class CompHexWithCDFxService extends AbstractRestService {

  /**
   * Compare hex with cdfx data.
   *
   * @param multiPart the multi part
   * @param metaData the meta data
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @CompressData
  public Response compareHexWithCdfxData(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_COMP_HEX_META_DATA) final CompHexMetaData metaData)
      throws IcdmException {

    byte[] hexData = null;
    boolean pverClearingStatus = true;
    List<Boolean> isNonSDOMBCRelease = new ArrayList<>();
    /** Hex file can be downloaded from vcdm or sent as byte stream **/
    if (!metaData.isHexFromVcdm()) {
      // get the hex files
      List<FormDataBodyPart> hexFileInput = multiPart.getFields(WsCommonConstants.HEX_FILE_MUTIPART);
      if (CommonUtils.isNullOrEmpty(hexFileInput)) {
        throw new InvalidInputException("Input HEX file missing");
      }
      for (FormDataBodyPart field : hexFileInput) {
        InputStream unzippedStream = ZipUtils.unzipIfZipped(field.getValueAs(InputStream.class));
        hexData = readBytes(unzippedStream);
      }
    }
    // fetch the pver name and check if it is NON SDOM/SDOM PVER
    PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(metaData.getPidcData().getPidcA2lId());
    SdomPverLoader pvrLdr = new SdomPverLoader(getServiceData());
    SortedSet<String> pverVariantNamesSet = pvrLdr.getPverVariantNames(pidcA2l.getSdomPverName());
    String hint = null;
    if (pverVariantNamesSet.isEmpty()) {
      isNonSDOMBCRelease.add(true);
      AttributeLoader loader = new AttributeLoader(getServiceData());
      Map<Long, Attribute> attrRetMap = loader.getAllAttributes(false);
      Optional<Attribute> attributeInfo = attrRetMap.values().stream()
          .filter(attribute -> attribute.getName().equals(CDRConstants.PVER_NAME_IN_SDOM)).findAny();
      if (attributeInfo.isPresent()) {
        AttributeValueLoader attributeValueLoader = new AttributeValueLoader(getServiceData());
        Set<Long> attrIdSet = new HashSet();
        attrIdSet.add(attributeInfo.get().getId());
        // fetch attribute value
        Map<Long, Map<Long, AttributeValue>> attrValretMap = attributeValueLoader.getValuesByAttribute(attrIdSet);
        Optional<AttributeValue> attributeValueInfo = attrValretMap.get(attributeInfo.get().getId()).values().stream()
            .filter(attributeValue -> attributeValue.getName().equals(pidcA2l.getSdomPverName())).findAny();
        if (attributeValueInfo.isPresent() && !attributeValueInfo.get().getClearingStatus().equals("Y")) {
          pverClearingStatus = false;
          hint = new MessageLoader(getServiceData()).getMessage("COMPLI_REVIEW", "PVER_NOT_CLEARED",
              pidcA2l.getSdomPverName());
          CDMLogger.getInstance().error(hint);
        }
      }
    }
    else {
      isNonSDOMBCRelease.add(false);
    }

    // Perform HEX comparison
    CompHexResponse response = new CompHexWithCDFxProcess(getServiceData()).performCompare(metaData, hexData,
        isNonSDOMBCRelease, pverClearingStatus);
    if (!pverClearingStatus) {
      response.getErrorMsgSet().add(hint);
    }
    return Response.ok(response).build();
  }

  /**
   * @param referenceId reference id from comp hex service
   * @return the downloaded files
   * @throws IOException error during zipping the files
   * @throws IcdmException error during webservice call
   */
  @GET
  @Path(WsCommonConstants.RWS_DOWNLOAD_ALL)
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response downloadAllFiles(@QueryParam(WsCommonConstants.RWS_QP_REF_ID) final String referenceId)
      throws IOException, IcdmException {

    String folderPath = CompHexWithCDFxProcess.COMP_HEX_WORK_DIR + CompHexWithCDFxProcess.FILE_DELIMITER + referenceId;
    String zipFileName = folderPath + ".zip";
    File file = new File(folderPath);
    if (file.exists()) {
      ZipUtils.zip(Paths.get(folderPath), Paths.get(zipFileName));
    }

    File zipFile = new File(zipFileName);
    if (!zipFile.exists()) {
      throw new IcdmException("Error downloading output files for reference ID : " + referenceId);
    }

    byte[] fileData = null;
    try {
      fileData = FileIOUtil.toByteArray(zipFile);
    }
    catch (IOException e) {
      throw new IcdmException("Error downloading file : " + referenceId + e.getLocalizedMessage(), e);
    }

    // Create service response
    return Response.ok(fileData).build();
  }

  /**
   * Adds the file bytes to map.
   *
   * @param filesStreamMap the files stream map
   * @param filePath the file path
   * @param iStream the input stream
   * @return
   * @throws IcdmException the icdm exception
   */
  private byte[] readBytes(final InputStream iStream) throws IcdmException {
    try {
      return FileIOUtil.toByteArray(iStream);
    }
    catch (IOException exp) {
      throw new IcdmException(exp.getLocalizedMessage(), exp);
    }
  }

  /**
   * Download comp hex result.
   *
   * @param referenceId the reference id
   * @param reportType the report type
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @GET
  @Path(WsCommonConstants.RWS_DOWNLOAD_REPORT)
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response downloadCompHexResult(@QueryParam(WsCommonConstants.RWS_QP_REF_ID) final String referenceId,
      @QueryParam(WsCommonConstants.RWS_QP_REPORT_TYPE) final String reportType)
      throws IcdmException {

    String folderPath = CompHexWithCDFxProcess.COMP_HEX_WORK_DIR + CompHexWithCDFxProcess.FILE_DELIMITER + referenceId +
        CompHexWithCDFxProcess.FILE_DELIMITER;
    File file = getReportFile(new File(folderPath), reportType);

    if ((file == null) || !file.exists()) {
      throw new IcdmException(
          "Error downloading " + reportType + " file : " + referenceId + ". Reason : report not created.");
    }

    byte[] fileData = null;
    try {
      fileData = FileIOUtil.toByteArray(file);
    }
    catch (IOException e) {
      throw new IcdmException("Error downloading file : " + referenceId + ". " + e.getMessage(), e);
    }

    // Create service response
    ResponseBuilder response = Response.ok(fileData);
    return response.build();
  }

  /**
   * Gets the report file.
   *
   * @param directory the directory
   * @param reportType the report type
   * @return the report file
   */
  public File getReportFile(final File directory, final String reportType) {
    Iterator fileIt = addFilesToIterator(directory, reportType);
    if ((reportType == null) || (fileIt == null)) {
      return null;
    }
    while (fileIt.hasNext()) {
      File afile = (File) fileIt.next();
      if (reportType.equalsIgnoreCase(ApicConstants.REPORT_TYPE_EXCEL) &&
          !afile.getName().contains(ApicConstants.CSSD_EXTERNAL_REPORT_CODE)) {
        return afile;
      }
      if (reportType.equalsIgnoreCase(ApicConstants.REPORT_TYPE_PDF)) {
        return afile;
      }
    }
    return null;
  }

  /**
   * @param directory
   * @param reportType
   * @param fileIt
   * @return
   */
  private Iterator addFilesToIterator(final File directory, final String reportType) {
    Iterator fileIt = null;
    if ((reportType != null) && (reportType.equalsIgnoreCase(ApicConstants.REPORT_TYPE_PDF))) {
      String[] extensions = { "pdf" };
      fileIt = FileUtils.iterateFiles(directory, extensions, false);
    }
    else if ((reportType != null) && (reportType.equalsIgnoreCase(ApicConstants.REPORT_TYPE_EXCEL))) {
      String[] extensions = { "xlsx" };
      fileIt = FileUtils.iterateFiles(directory, extensions, true);
    }
    return fileIt;
  }
}