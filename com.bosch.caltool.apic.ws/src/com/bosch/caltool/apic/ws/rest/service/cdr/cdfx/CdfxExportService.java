/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr.cdfx;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.A2LWpDefVersionChecker;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.cdfx.CDFxDeliveryCommand;
import com.bosch.caltool.icdm.bo.cdr.cdfx.CdfxDeliveryHandler;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelivery;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDeliveryWrapper;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportInput;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CDFX_EXPORT)
public class CdfxExportService extends AbstractRestService {

  private String outputFolderName;


  /**
   * Create cdfx file using official locked review results
   *
   * @param inputModel - CdfxExportInputModel inputModel
   * @return service response as a zip file
   * @throws IcdmException error during service
   */
  @POST
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response exportCdfx(final CdfxExportInput inputModel) throws IcdmException {

    validateInput(inputModel);

    // check for wp Definition version is available
    A2LWpDefVersionChecker wpDefVerChecker = new A2LWpDefVersionChecker(getServiceData());
    wpDefVerChecker.ensureActiveWpDefVerForA2l(inputModel.getPidcA2lId());

    String tempDir = Messages.getString("SERVICE_WORK_DIR") + "//CDFX_EXPORT//";


    CDFxDeliveryWrapper cdfxDeliveryWrapper = new CDFxDeliveryWrapper();
    cdfxDeliveryWrapper.setInput(inputModel);
    cdfxDeliveryWrapper.setServerTempDir(tempDir);

    CdfxDeliveryHandler cdfxDelivery = new CdfxDeliveryHandler(cdfxDeliveryWrapper, getServiceData());

    String zipFilePath = cdfxDelivery.createOutputFiles();

    // save delivery results
    saveDelivery(cdfxDeliveryWrapper);

    // create response
    ResponseBuilder response = Response.ok(new File(zipFilePath));
    // set the Response file name
    response.header("Content-Disposition", "attachment; filename=" + this.outputFolderName + ".zip");

    return response.build();
  }

  private void validateInput(final CdfxExportInput inputModel) throws IcdmException {
    Map<Long, PidcVariant> varMap = new PidcVariantLoader(getServiceData()).getVariants(
        new PidcA2lLoader(getServiceData()).getDataObjectByID(inputModel.getPidcA2lId()).getPidcVersId(), false);

    if (CommonUtils.isNotEmpty(varMap) && CommonUtils.isNullOrEmpty(inputModel.getVariantsList())) {
      throw new IcdmException("Undeleted variants are present for given input. Please provide a variant input!");
    }

    if ((inputModel.getScope() == null) && inputModel.getWpRespModelList().isEmpty()) {
      throw new IcdmException("Scope and WP List cannot be empty. Please provide an input");
    }

  }

  /**
   * Method to save results in database
   *
   * @param cdfxDeliveryWrapper - wrapper model
   * @throws IcdmException - exception during storing
   */
  public void saveDelivery(final CDFxDeliveryWrapper cdfxDeliveryWrapper) throws IcdmException {
    Set<PidcVariant> selectedInputVariants = cdfxDeliveryWrapper.getInput().getVariantsList();
    if (CommonUtils.isNotEmpty(selectedInputVariants)) {
      for (PidcVariant variant : selectedInputVariants) {
        executeDelivery(cdfxDeliveryWrapper, variant.getId());
      }
    }
    else {
      executeDelivery(cdfxDeliveryWrapper, null);
    }
  }

  /**
   * @param cdfxDeliveryWrapper cdfxDeliveryWrapper
   * @param variantId variantId
   * @throws IcdmException IcdmException
   */
  private void executeDelivery(final CDFxDeliveryWrapper cdfxDeliveryWrapper, final Long variantId)
      throws IcdmException {
    CDFxDelivery cdfxDelivery = new CDFxDelivery();

    cdfxDelivery.setPidcA2lId(cdfxDeliveryWrapper.getInput().getPidcA2lId());
    cdfxDelivery.setVariantId(variantId);

    // set wp defn version id
    Long actVersId = new A2lWpDefnVersionLoader(getServiceData())
        .getActiveVersion(cdfxDeliveryWrapper.getInput().getPidcA2lId()).getId();
    cdfxDelivery.setWpDefnVersId(actVersId);

    cdfxDelivery.setScope(cdfxDeliveryWrapper.getInput().getScope());
    cdfxDelivery.setIsOneFilePerWpYn(
        cdfxDeliveryWrapper.getInput().isOneFilePerWpFlag() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    cdfxDelivery.setReadinessYn(
        cdfxDeliveryWrapper.getInput().isReadinessFlag() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    // insert cdfx delivery
    // inserts into other tables handled inside CDFxDeliveryCommand
    CDFxDeliveryCommand cdfxDeliveryCmd = new CDFxDeliveryCommand(getServiceData(), cdfxDelivery, false, false);
    cdfxDeliveryCmd.setCdfxDeliveryWrapper(cdfxDeliveryWrapper);
    executeCommand(cdfxDeliveryCmd);
  }

  /**
   * retrieve the cdfx readiness condition file
   *
   * @return rest response
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Path(WsCommonConstants.RWS_CDFX_READINESS_FILE)
  @CompressData
  public Response getCdfxReadinessConditionFile() throws IcdmException {
    CommonParamKey cdfxReadinessFileNodeId = null;
    String cdfxReadinessFileName = null;
    if (CommonUtils.isEqualIgnoreCase(getServiceData().getLanguage(), Language.ENGLISH.toString())) {
      cdfxReadinessFileNodeId = CommonParamKey.CDFX_READINESS_FILE_NODE_EN_ID;
      cdfxReadinessFileName = ApicConstants.CDFX_READINESS_FILE_NAME_EN;
    }
    else {
      cdfxReadinessFileNodeId = CommonParamKey.CDFX_READINESS_FILE_NODE_DE_ID;
      cdfxReadinessFileName = ApicConstants.CDFX_READINESS_FILE_NAME_DE;
    }
    Map<String, byte[]> ret = new IcdmFilesLoader(getServiceData())
        .getFiles(Long.valueOf(new CommonParamLoader(getServiceData()).getValue(cdfxReadinessFileNodeId)), "TEMPLATES");

    byte[] cdfxFile = null;
    if ((null != ret) && !ret.isEmpty()) {
      cdfxFile = ret.get(cdfxReadinessFileName);
    }

    return Response.ok(cdfxFile).build();
  }
}
