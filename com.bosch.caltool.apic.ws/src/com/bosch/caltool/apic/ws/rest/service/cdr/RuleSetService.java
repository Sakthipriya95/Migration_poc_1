/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.ParameterRuleFetcher;
import com.bosch.caltool.icdm.bo.cdr.PidcRuleSetRuleFileResolver;
import com.bosch.caltool.icdm.bo.cdr.RuleSetCreationCommand;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.model.a2l.RuleSetRulesResponse;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetInputData;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;


/**
 * Get Icdm Rule sets
 *
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RULE_SET)
public class RuleSetService extends AbstractRestService {

  /**
   * @param objId rule set ID
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response get(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    // Fetch rule set
    RuleSet ret = new RuleSetLoader(getServiceData()).getDataObjectByID(objId);

    getLogger().info("RuleSetService.get() completed. Rule set  found = {}", ret.getName());

    return Response.ok(ret).build();

  }

  /**
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAllRuleSets() throws IcdmException {

    RuleSetLoader loader = new RuleSetLoader(getServiceData());

    // Fetch all rule sets
    Set<ParamCollection> retSet = loader.getAllRuleSets();

    WSObjectStore.getLogger().info("RuleSetLoader.getAllRuleSets() completed. Rule sets found = {}", retSet.size());

    return Response.ok(retSet).build();

  }


  /**
   * @param ruleSetId ruleSetId
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PARAM_RULES)
  @CompressData
  public Response getRuleSetParamRules(@QueryParam(value = WsCommonConstants.RWS_QP_ELEMENT_ID) final Long ruleSetId)
      throws IcdmException {

    ParameterRuleFetcher ruleFetcher = new ParameterRuleFetcher(getServiceData());

    // Fetch all functions
    RuleSetRulesResponse rulesOutput = ruleFetcher.createRuleSetRulesOutput(ruleSetId);

    WSObjectStore.getLogger().info("Fetching all params of the rule set completed. Parameters found = {}",
        rulesOutput.getParamMap().size());

    return Response.ok(rulesOutput).build();
  }

  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_SINGLE_PARAM_RULES)
  @CompressData
  public Response getRules(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long ruleSetId,
      final RuleSetParameter ruleSetParameter)
      throws IcdmException {

    ParameterRuleFetcher ruleFetcher = new ParameterRuleFetcher(getServiceData());

    // Fetch all functions
    RuleSetRulesResponse rulesOutput = ruleFetcher.getRulesForSingleParam(ruleSetId, ruleSetParameter);
    return Response.ok(rulesOutput).build();
  }

  /**
   * create ruleset from Admin page
   *
   * @param ruleSetmodel as input
   * @return ruleset
   * @throws IcdmException as exception
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final RuleSetInputData ruleSetmodel) throws IcdmException {
    RuleSetCreationCommand ruleSetCreationHandler = new RuleSetCreationCommand(getServiceData(), ruleSetmodel);
    executeCommand(ruleSetCreationHandler);
    return Response.ok(ruleSetCreationHandler.getRuleSetOutputData()).build();
  }

  /**
   * @param pidcElementId pidcElementId(pidc variant/version id)
   * @return response,with Rule Set object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_MANDATORY_RULE_SET_BY_PIDC)
  @CompressData
  public Response getMandateRuleSetForPIDC(
      @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcElementId)
      throws IcdmException {
    PidcRuleSetRuleFileResolver resolver = new PidcRuleSetRuleFileResolver(getServiceData());
    RuleSet ruleSet = resolver.getMandateRuleSetForPIDC(pidcElementId);
    getLogger().info(" Rule Set from Pidc = {}", ruleSet.getName());
    return Response.ok(ruleSet).build();
  }

  /**
   * @param pidcElementId pidc Element Id
   * @return ssd rule file
   * @throws IOException error during zipping the files
   * @throws IcdmException error during webservice call
   */
  @GET
  @Path(WsCommonConstants.RWS_GET_SSD_FILE_BY_PIDC)
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response getSsdFileByPidcElement(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcElementId)
      throws IOException, IcdmException {
    PidcRuleSetRuleFileResolver resolver = new PidcRuleSetRuleFileResolver(getServiceData());
    CDRRulesWithFile cdrRulesWithFile = resolver.getSsdFileByPidcElement(pidcElementId);
    String folderPath = cdrRulesWithFile.getSsdFilePath();
    String zipFileName = folderPath + ".zip";
    File file = new File(folderPath);
    if (file.exists()) {
      ZipUtils.zip(Paths.get(folderPath), Paths.get(zipFileName));
    }

    byte[] fileData = null;
    File zipFile = new File(folderPath);
    if (file.exists()) {
      try (FileInputStream fin = new FileInputStream(zipFile)) {
        fileData = readBytes(new FileInputStream(zipFile));
      }
      catch (IOException e) {
        throw new IcdmException("Error downloading file :" + e.getLocalizedMessage(), e);
      }
    }
    else {
      throw new IcdmException("Error downloading  file : ");
    }
    // Create service response
    ResponseBuilder response = Response.ok(fileData);
    return response.build();
  }

  /**
   * Adds the file bytes to map.
   *
   * @param filesStreamMap the files stream map
   * @param filePath the file path
   * @param unzipIfZippedStream the unzip if zipped stream
   * @return
   * @throws IcdmException the icdm exception
   */
  private byte[] readBytes(final InputStream unzipIfZippedStream) throws IcdmException {
    try {
      return IOUtils.toByteArray(unzipIfZippedStream);
    }
    catch (IOException exp) {
      throw new IcdmException(exp.getLocalizedMessage(), exp);
    }
  }

}
