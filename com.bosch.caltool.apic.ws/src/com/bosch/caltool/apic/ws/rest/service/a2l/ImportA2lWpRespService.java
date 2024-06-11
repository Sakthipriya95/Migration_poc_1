package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefinitionVersionCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpParamMappingCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpRespGrpsImportCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityCommand;
import com.bosch.caltool.icdm.bo.a2l.ImportA2lWpRespFromFC2WPCommand;
import com.bosch.caltool.icdm.bo.a2l.ImportA2lWpRespFromInputFileCommand;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespGrpsResponse;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespInput;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespResponse;


/**
 * Service class for A2lWpParamMapping
 *
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2LWPRESP_IMPORT)
public class ImportA2lWpRespService extends AbstractRestService {

  /**
   * name of unique constraint in T_A2L_RESPONSIBILITY DB table
   */
  private static final String T_PIDC_WP_RESP_UK = "T_PIDC_WP_RESP_UK";


  /**
   * Import FC2WP.
   *
   * @param input the input
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @POST
  @Path(WsCommonConstants.RWS_IMPORT_FROM_FC2WP)
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response importA2lWpRespFromFC2WP(final ImportA2lWpRespInput input) throws IcdmException {
    ImportA2lWpRespResponse response = new ImportA2lWpRespResponse();
    getLogger().info(
        "ImportA2lWpResp from FC2WP : FC2WPVersion = {}, A2lFileId = {}, VaraintGrpId = {}, WpDefVersId = {}, PidcVersionId = {}, canCreateParamMapping = {}",
        input.getFc2wpVersId(), input.getA2lFileId(), input.getVariantGrpId(), input.getWpDefVersId(),
        input.getPidcVersionId(), input.isCreateParamMapping());

    ImportA2lWpRespFromFC2WPCommand importCmd = new ImportA2lWpRespFromFC2WPCommand(getServiceData(), input);
    executeCommand(importCmd);
    // set the updated data to response
    setResponseFields(response, importCmd.getA2lWpParamMappingSet(), importCmd.getWpRespPalSet(),
        importCmd.getRespSet(), importCmd.getA2lWpCmdSet(), importCmd.getSkippedParams(), importCmd.getWpDefnVerSet());

    getLogger().info("Import of A2lWpResponsibility from FC2WP is completed");
    return Response.ok(response).build();
  }


  /**
   * Import A 2 l wp resp from excel.
   *
   * @param input the input
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @POST
  @Path(WsCommonConstants.RWS_IMPORT_FROM_EXCEL)
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response importA2lWpRespFromExcel(final ImportA2lWpRespData input) throws IcdmException {
    // Import A2LWpResp from excel file
    getLogger().info("ImportA2lWpResp - Excel : A2lFileId = {} WpDefVersId = {}", input.getA2lFileId(),
        input.getWpDefVersId());

    ImportA2lWpRespFromInputFileCommand importCmd = new ImportA2lWpRespFromInputFileCommand(getServiceData(), input);
    try {
      executeCommand(importCmd);
    }
    catch (CommandException exp) {
      int index = exp.getCause().getMessage().indexOf(T_PIDC_WP_RESP_UK);
      if (index != -1) {
        throwErrorWithDuplicateAliasNames(importCmd, exp);
      }
      throw new IcdmException(exp.getMessage(), exp);
    }

    ImportA2lWpRespResponse response = new ImportA2lWpRespResponse();
    // set the updated data to response if the update was successful
    setResponseFields(response, importCmd.getA2lWpParamMappingSet(), importCmd.getWpRespPalSet(),
        importCmd.getRespSet(), importCmd.getA2lWpCommandSet(), importCmd.getSkippedParams(),
        importCmd.getWpDefnVerSet());

    getLogger().info("Import of A2lWpResponsibility from Excel completed");

    return Response.ok(response).build();
  }


  /**
   * @param importCmd
   * @param exp
   * @throws IcdmException
   */
  private void throwErrorWithDuplicateAliasNames(final ImportA2lWpRespFromInputFileCommand importCmd,
      final CommandException exp)
      throws IcdmException {

    StringBuilder respNames = new StringBuilder();

    for (A2lResponsibilityCommand a2lRespCmd : importCmd.getRespSet()) {
      // get input data from the command
      A2lResponsibility inputA2lRespFromCmd = a2lRespCmd.getInputA2lRespData();
      // loop through the existing A2l Responsibilities in the PIDC
      for (A2lResponsibility a2lResp : importCmd.getA2lRespMap().values()) {
        // check for T_PIDC_WP_RESP_UK unique constraint
        if (isA2lRespDetailsSame(inputA2lRespFromCmd, a2lResp) &&
            CommonUtils.isNotEqual(a2lResp.getAliasName(), inputA2lRespFromCmd.getAliasName()) &&

            isUserDetailsSame(inputA2lRespFromCmd, a2lResp)) {

          respNames.append('\n').append(a2lResp.getName()).append(" : ").append(a2lResp.getAliasName());
        }
      }
    }

    throw new IcdmException("A2L_RESP_IMPORT.T_PIDC_WP_RESP_UK", exp, respNames.toString());
  }


  /**
   * @param inputA2lRespFromCmd
   * @param a2lResp
   * @return
   */
  private boolean isUserDetailsSame(final A2lResponsibility inputA2lRespFromCmd, final A2lResponsibility a2lResp) {
    return CommonUtils.isEqual(a2lResp.getUserId(), inputA2lRespFromCmd.getUserId()) &&
        CommonUtils.isEqual(a2lResp.getLDepartment(), inputA2lRespFromCmd.getLDepartment()) &&
        isUserNameSame(inputA2lRespFromCmd, a2lResp);
  }


  /**
   * @param inputA2lRespFromCmd
   * @param a2lResp
   * @return
   */
  private boolean isUserNameSame(final A2lResponsibility inputA2lRespFromCmd, final A2lResponsibility a2lResp) {
    return CommonUtils.isEqual(a2lResp.getLFirstName(), inputA2lRespFromCmd.getLFirstName()) &&
        CommonUtils.isEqual(a2lResp.getLLastName(), inputA2lRespFromCmd.getLLastName());
  }


  /**
   * @param inputA2lRespFromCmd
   * @param a2lResp
   * @return
   */
  private boolean isA2lRespDetailsSame(final A2lResponsibility inputA2lRespFromCmd, final A2lResponsibility a2lResp) {
    return CommonUtils.isEqual(a2lResp.getProjectId(), inputA2lRespFromCmd.getProjectId()) &&
        CommonUtils.isEqual(a2lResp.getRespType(), inputA2lRespFromCmd.getRespType());
  }

  /**
   * @param respCmdSet
   * @param wpRespCmdSet
   * @param paramCmdSet
   * @param response
   * @param skippedParams
   * @param wpDefnVerSet
   * @param importCmd
   * @throws DataException
   */
  private void setResponseFields(final ImportA2lWpRespResponse response,
      final Set<A2lWpParamMappingCommand> paramCmdSet, final Set<A2lWpResponsibilityCommand> wpRespCmdSet,
      final Set<A2lResponsibilityCommand> respCmdSet, final Set<A2lWorkPackageCommand> wrkPkgCmdSet,
      final Set<String> skippedParams, final Set<A2lWpDefinitionVersionCommand> wpDefnVerSet)
      throws DataException {
    for (A2lWpParamMappingCommand paramCmd : paramCmdSet) {
      response.getA2lWpParamMappingSet().add(paramCmd.getNewData());
    }
    for (A2lWpResponsibilityCommand wpRespCmd : wpRespCmdSet) {
      response.getWpRespPalSet().add(wpRespCmd.getNewData());
    }
    for (A2lResponsibilityCommand respCmd : respCmdSet) {
      response.getRespSet().add(respCmd.getNewData());
    }
    for (A2lWorkPackageCommand wpCmd : wrkPkgCmdSet) {
      response.getWrkPkgSet().add(wpCmd.getNewData());
    }
    for (A2lWpDefinitionVersionCommand versCmd : wpDefnVerSet) {
      response.getA2lWpDefnVersSet().add(versCmd.getNewData());
    }
    response.setSkippedParams(skippedParams);
  }

  /**
   * Import from A2L Groups.
   *
   * @param wpDefVersId the selected wp definition version ID
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @POST
  @Path(WsCommonConstants.RWS_IMPORT_FROM_A2L_GROUP)
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response a2lWpRespGrpsImport(final Long wpDefVersId) throws IcdmException {
    // Import from A2L groups
    getLogger().info("ImportA2lWpResp - A2l Groups : WpDefVersId = {}  ", wpDefVersId);
    ImportA2lWpRespGrpsResponse response = new ImportA2lWpRespGrpsResponse();
    A2lWpRespGrpsImportCommand importCmd = new A2lWpRespGrpsImportCommand(getServiceData(), wpDefVersId, response);
    executeCommand(importCmd);
    return Response.ok(response).build();

  }

}
