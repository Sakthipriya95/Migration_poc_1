/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoFetcher;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterPropOutput;
import com.bosch.caltool.icdm.bo.cdr.MultipleRuleSetParamCommand;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.bo.cdr.RuleSetParameterCommand;
import com.bosch.caltool.icdm.bo.cdr.RuleSetParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.RuleSetParamInput;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RulesetParamMultiInput;
import com.bosch.ssd.icdm.model.CDRRule;
import com.google.common.io.Files;


/**
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_RULEST_PARAM)
public class RuleSetParameterService extends AbstractRestService {


  /**
   * Get all parameter names of the ruleset
   *
   * @param ruleSetId Rule Set Id
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PARAM_NAMES)
  @CompressData
  public Response getAllParamNames(@QueryParam(value = WsCommonConstants.RWS_QP_RULESET_ID) final Long ruleSetId)
      throws IcdmException {

    Set<String> retSet =
        new HashSet<>(new RuleSetParameterLoader(getServiceData()).getAllRuleSetParams(ruleSetId).keySet());
    getLogger().info("RuleSetParamservice.getAllParamNames : Parameter count = {}", retSet.size());
    return Response.ok(retSet).build();
  }

  /**
   * @param objId rule set parameter ID
   * @return Response RuleSetParameter
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response get(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    // Fetch rule set
    RuleSetParameter ret = new RuleSetParameterLoader(getServiceData()).getDataObjectByID(objId);

    getLogger().info("RuleSetService.get() completed. Rule set  found = {}", ret.getName());

    return Response.ok(ret).build();

  }

  /**
   * Create a Attribute record
   *
   * @param obj object to create
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final RuleSetParameter obj) throws IcdmException {
    RuleSetParameterCommand cmd = new RuleSetParameterCommand(getServiceData(), obj, COMMAND_MODE.CREATE);
    executeCommand(cmd);
    RuleSetParameter ret = cmd.getNewData();
    getLogger().info("Created Attribute Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @param ruleSetParamSet ruleSetParamSet
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_CREATE_MULTIPLE_PARAM)
  @CompressData
  public Response createMultiple(final Set<RuleSetParameter> ruleSetParamSet) throws IcdmException {
    Set<RuleSetParameter> ruleSetParamOutput = createMultipleRuleSet(ruleSetParamSet);
    return Response.ok(ruleSetParamOutput).build();
  }

  /**
   * @param ruleSetParamInput as input
   * @return Set<RuleSetParameter>
   * @throws IcdmException Exception
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_CREATE_MULTIPLE_RULESET_PARAM_USING_A2L)
  @CompressData
  public Response createRuleSetParamUsingA2lFileID(final RuleSetParamInput ruleSetParamInput) throws IcdmException {
    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(ruleSetParamInput.getA2lFileId());
    A2LFileInfo fetchA2LFileInfo = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(a2lFile);
    Set<RuleSetParameter> ruleSetParamOutput = createRuleSetParam(ruleSetParamInput.getRuleSetId(), fetchA2LFileInfo);

    return Response.ok(ruleSetParamOutput).build();
  }

  /**
   * @param multiPart formDataMultiPart as input
   * @return Set<RuleSetParameter>
   * @throws IcdmException Exception
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path(WsCommonConstants.RWS_CREATE_MULTIPLE_RULESET_PARAM_USING_A2L)
  @CompressData
  public Response createRuleSetParamUsingA2l(final FormDataMultiPart multiPart) throws IcdmException {
    // get the a2l file
    List<FormDataBodyPart> a2lFileInput = multiPart.getFields(WsCommonConstants.A2L_FILE_MULTIPART);
    if (CommonUtils.isNullOrEmpty(a2lFileInput)) {
      throw new InvalidInputException("A2L file is mandatory");
    }

    FormDataBodyPart ruleSetIdPart = multiPart.getField(WsCommonConstants.RWS_QP_RULESET_ID);
    String ruleSetIdStr = ruleSetIdPart.getValue();
    long ruleSetId = Long.parseLong(ruleSetIdStr);

    validateInput(ruleSetId);

    InputStream a2lInputStream = null;
    String a2lFileName = null;

    // get the A2l Stream
    for (FormDataBodyPart field : a2lFileInput) {
      a2lInputStream = field.getValueAs(InputStream.class);
      a2lFileName = field.getContentDisposition().getFileName();
      getLogger().info("A2L input - {}, file name - {}", a2lInputStream.toString(), a2lFileName);
    }

    /* STEP-1 */
    // validate the file, check if input is valid a2l
    getLogger().debug("STEP-1: Validating A2L file...");
    File targetA2lFile = validateAndSaveA2lFile(a2lFileName, a2lInputStream);
    if (!targetA2lFile.exists()) {
      String errorMsg = "Error saving A2L file from filepath: " + a2lFileName;
      getLogger().error(errorMsg);
      // throw exception to cancel the job
      throw new IcdmException(errorMsg);
    }

    A2LFileInfo fetchA2LFileInfoFromFilePath =
        new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfoFromFilePath(targetA2lFile.getPath());

    Set<RuleSetParameter> ruleSetParamOutput = createRuleSetParam(ruleSetId, fetchA2LFileInfoFromFilePath);

    return Response.ok(ruleSetParamOutput).build();
  }

  /**
   * @param ruleSetId
   * @param fetchA2LFileInfoFromFilePath
   * @return
   * @throws DataException
   * @throws UnAuthorizedAccessException
   * @throws IcdmException
   */
  private Set<RuleSetParameter> createRuleSetParam(final long ruleSetId, final A2LFileInfo fetchA2LFileInfoFromFilePath)
      throws IcdmException {
    Set<RuleSetParameter> ruleSetParamSet = new HashSet<>();
    ParameterLoader parameterLoader = new ParameterLoader(getServiceData());

    Map<String, RuleSetParameter> allRuleSetParams =
        new RuleSetParameterLoader(getServiceData()).getAllRuleSetParams(ruleSetId);
    Set<String> paramNames = fetchA2LFileInfoFromFilePath.getAllModulesLabels().values().stream()
        .map(Characteristic::getName).collect(Collectors.toSet());

    Set<String> paramNamesWithVarCodedRemoved = new HashSet<>();
    for (String paramName : paramNames) {
      paramNamesWithVarCodedRemoved.add(parameterCheckAndRemoveVarCode(paramName));
    }

    ParameterPropOutput parameterPropOutput = parameterLoader.fetchCDRParamsAndFuncs(paramNamesWithVarCodedRemoved);

    for (Entry<String, Characteristic> a2lCharEntrySet : fetchA2LFileInfoFromFilePath.getAllModulesLabels()
        .entrySet()) {
      Characteristic characteristic = a2lCharEntrySet.getValue();
      String parameterCheckAndRemoveVarCode = parameterCheckAndRemoveVarCode(characteristic.getName());
      Parameter parameter = parameterPropOutput.getParamNameObjMap().get(parameterCheckAndRemoveVarCode);
      // only the parameter that is not available in the ruleset should be added
      // no need to log the not added parametets in log file as discussed with michael
      if (CommonUtils.isNotNull(parameter) && !allRuleSetParams.containsKey(parameter.getName())) {
        RuleSetParameter ruleSetParameter = new RuleSetParameter();
        ruleSetParameter.setName(parameter.getName());
        ruleSetParameter.setDescription(parameter.getLongName());
        ruleSetParameter.setLongName(parameter.getLongName());
        ruleSetParameter.setParamId(parameter.getId());
        setFunctionID(characteristic, parameter, ruleSetParameter, parameterPropOutput);
        ruleSetParameter.setRuleSetId(ruleSetId);
        ruleSetParamSet.add(ruleSetParameter);
      }
    }
    return createMultipleRuleSet(ruleSetParamSet);
  }

  /**
   * @param paramNamesWithVarCodedRemoved
   * @param paramName
   */
  private String parameterCheckAndRemoveVarCode(final String paramName) {
    boolean variantCoded = ApicUtil.isVariantCoded(paramName);
    return variantCoded ? ApicUtil.getBaseParamName(paramName) : paramName;
  }

  /**
   * @param characteristic
   * @param parameter
   * @param ruleSetParameter
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void setFunctionID(final Characteristic characteristic, final Parameter parameter,
      final RuleSetParameter ruleSetParameter, final ParameterPropOutput parameterPropOutput) {
    if (CommonUtils.isNotNull(characteristic.getDefFunction()) &&
        CommonUtils.isNotNull(characteristic.getDefFunction().getName()) &&
        !CommonUtils.isEmptyString(characteristic.getDefFunction().getName())) {
      Function function = CommonUtils.isNotEmpty(parameterPropOutput.getParamFuncObjMap()) &&
          CommonUtils.isNotNull(parameterPropOutput.getParamFuncObjMap().get(parameter.getName()))
              ? parameterPropOutput.getParamFuncObjMap().get(parameter.getName()) : null;
      if (function != null) {
        ruleSetParameter.setFuncId(function.getId());
      }
    }
    else {
      ruleSetParameter.setFuncId(-1l);
    }
  }

  /**
   * @param ruleSetId ruleSet ID
   * @throws IcdmException when validation fails
   */
  private void validateInput(final Long ruleSetId) throws IcdmException {
    new RuleSetLoader(getServiceData()).validateId(ruleSetId);
  }

  /**
   * @param ruleSetParamSet
   * @return
   * @throws UnAuthorizedAccessException
   * @throws IcdmException
   * @throws DataException
   */
  private Set<RuleSetParameter> createMultipleRuleSet(final Set<RuleSetParameter> ruleSetParamSet)
      throws IcdmException {
    RulesetParamMultiInput rulesetParamMultiInput = new RulesetParamMultiInput();
    rulesetParamMultiInput.setRuleSetParamtoInsert(ruleSetParamSet);
    Set<RuleSetParameter> ruleSetParamOutput = new HashSet<>();
    RuleSetParameterLoader ruleSetParameterLoader = new RuleSetParameterLoader(getServiceData());
    MultipleRuleSetParamCommand cmd = new MultipleRuleSetParamCommand(getServiceData(), rulesetParamMultiInput);
    executeCommand(cmd);
    for (RuleSetParameter ruleSetParameter : ruleSetParamSet) {
      ruleSetParamOutput.add(ruleSetParameterLoader.getDataObjectByID(ruleSetParameter.getId()));
    }
    return ruleSetParamOutput;
  }

  /**
   * @param a2lFileName
   * @param a2lInputStream
   * @return File
   * @throws IcdmException
   */
  private File validateAndSaveA2lFile(final String a2lFileName, final InputStream a2lInputStream) throws IcdmException {
    File targetA2lFile = new File(System.getenv("TEMP") + File.separator + a2lFileName);
    byte[] byteArray;
    try {
      byteArray = IOUtils.toByteArray(a2lInputStream);
      if ((a2lInputStream != null) && (byteArray.length > 0)) {
        Files.write(byteArray, targetA2lFile);
      }
    }
    catch (IOException e) {
      throw new IcdmException(e.getMessage(), e);
    }

    if (A2LFileInfoFetcher.INSTANCE.getA2lFileInfo(byteArray, getLogger()) == null) {
      String strErr = "Invalid a2l file uploaded : " + a2lFileName;
      getLogger().error(strErr);
      throw new IcdmException(strErr);
    }

    return targetA2lFile;
  }

  /**
   * Create a Attribute record
   *
   * @param ruleSetParamId object to delete
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long ruleSetParamId)
      throws IcdmException {
    RuleSetParameterLoader ruleSetParamLoader = new RuleSetParameterLoader(getServiceData());
    RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
    RuleSetParameter ruleSetParam = ruleSetParamLoader.getDataObjectByID(ruleSetParamId);


    RuleSet ruleSet = ruleSetLoader.getDataObjectByID(ruleSetParam.getRuleSetId());
    Long ssdNodeId = ruleSetParamLoader.getRuleSetEntity(ruleSet.getId()).getSsdNodeId();
    List<String> paramNames = new ArrayList<>();

    paramNames.add(ruleSetParam.getName());

    final Map<String, List<CDRRule>> rulesMap =
        new SSDServiceHandler(getServiceData()).readReviewRule(paramNames, ssdNodeId);
    RuleSetParameterCommand cmd = new RuleSetParameterCommand(getServiceData(), ruleSetParam, COMMAND_MODE.DELETE);
    cmd.setCdrRuleMap(rulesMap);
    executeCommand(cmd);

    return Response.ok().build();
  }

  /**
   * Create a Attribute record
   *
   * @param ruleSetParamAttrId object to delete
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_DELETE_MULTIPLE_PARAM)
  @CompressData
  public Response deleteMultiple(
      final @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) Set<Long> ruleSetParamAttrId)
      throws IcdmException {
    RulesetParamMultiInput rulesetParamMultiInput = new RulesetParamMultiInput();
    Set<RuleSetParameter> ruleSetParamtoDel = new HashSet<>();
    RuleSetParameterLoader ruleSetParamLoader = new RuleSetParameterLoader(getServiceData());
    for (Long id : ruleSetParamAttrId) {
      RuleSetParameter ruleSetParam = ruleSetParamLoader.getDataObjectByID(id);
      ruleSetParamtoDel.add(ruleSetParam);
    }
    rulesetParamMultiInput.setRuleSetParamtoDel(ruleSetParamtoDel);
    MultipleRuleSetParamCommand cmd = new MultipleRuleSetParamCommand(getServiceData(), rulesetParamMultiInput);
    executeCommand(cmd);
    return Response.ok(ruleSetParamtoDel).build();
  }


}
