/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.caldataimport.CalDataImportDataFetcher;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportInput;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportSummary;

/**
 * Get the compliance parameter of an A2L file with configured SSD Class
 *
 * @author svj7cob
 */
// Task 263282
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CAL_DATA_IMPORT)
public class CalDataImportService extends AbstractRestService {


  /**
   * server location
   */
  public static final String SERVER_PATH = Messages.getString("SERVICE_WORK_DIR") + "//A2L_COMPLI_REPORT//";

  /**
   * @param multiPart multiPart
   * @param paramColId paramColId
   * @param paramColType paramColType
   * @return
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_PARSE_FILE)
  @CompressData
  public Response parseImportfile(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_FP_PARAM_COL_ID) final String paramColId,
      @FormDataParam(WsCommonConstants.RWS_FP_PARAM_COL_TYPE) final String paramColType,
      @FormDataParam(WsCommonConstants.RWS_FP_PARAM_FUNC_VER) final String funcVersion)
      throws IcdmException {

    // get the hex files
    List<FormDataBodyPart> inputCdfxFiles = multiPart.getFields(WsCommonConstants.RWS_QP_IMPORT_FILE);
    if (CommonUtils.isNullOrEmpty(inputCdfxFiles)) {
      throw new InvalidInputException("Input Cal data files missing");
    }
    String calInputFileName = "";
    // get the Hex file Stream
    Map<String, InputStream> inputfileStreamMap = new HashMap<>();
    for (FormDataBodyPart field : inputCdfxFiles) {
      ContentDisposition contentDisposition = field.getContentDisposition();
      calInputFileName = contentDisposition.getFileName();
      inputfileStreamMap.putAll(ZipUtils.unzipIfZipped(field.getValueAs(InputStream.class), calInputFileName));
    }
    // Use the cal Data import Fetcher
    CalDataImportDataFetcher dataFetecher = new CalDataImportDataFetcher(getServiceData());

    CalDataImportData response = dataFetecher.readInputfile(inputfileStreamMap, paramColId, paramColType, funcVersion);

    getLogger().info("caldata import of input data file completed. Number of paramters = {}",
        response.getInputDataMap().size());


    return Response.ok(response).build();
  }


  /**
   * Create a AttributeValue record
   *
   * @param obj object to create
   * @return Rest response, with created AttributeValue object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_CAL_COMPARE_OBJ)
  @CompressData
  public Response create(final CalDataImportInput input) throws IcdmException {
    CalDataImportDataFetcher dataFetecher = new CalDataImportDataFetcher(getServiceData());
    CalDataImportData calImportData = dataFetecher.getCompareObjSet(input);
    return Response.ok(calImportData).build();
  }


  /**
   * Create a AttributeValue record
   *
   * @param obj object to create
   * @return Rest response, with created AttributeValue object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_SAVE_PARAMS_RULES)
  @CompressData
  public Response createParamsandRules(final CalDataImportInput input) throws IcdmException {
    CalDataImportDataFetcher dataFetecher = new CalDataImportDataFetcher(getServiceData());
    CalDataImportSummary calImportData = dataFetecher.createParamsAndRules(input);
    return Response.ok(calImportData).build();
  }


}
