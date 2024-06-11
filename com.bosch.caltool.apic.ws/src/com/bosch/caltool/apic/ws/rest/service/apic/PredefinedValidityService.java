package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.attr.MultiplePredefinedValidityCommand;
import com.bosch.caltool.icdm.bo.apic.attr.PredefinedValidityLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidityCreationModel;


/**
 * Service class for PredefinedValidity
 *
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PREDEFINEDVALIDITY)
public class PredefinedValidityService extends AbstractRestService {

  /**
   * Rest web service path for PredefinedValidity
   */
  public final static String RWS_PREDEFINEDVALIDITY = "predefinedvalidity";

  /**
   * Get PredefinedValidity using its id
   *
   * @param objId object's id
   * @return Rest response, with PredefinedValidity object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PredefinedValidityLoader loader = new PredefinedValidityLoader(getServiceData());
    PredefinedValidity ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get PredefinedValidity using value id
   *
   * @param valueId value id
   * @return Rest response, with PredefinedValidity object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Path(WsCommonConstants.RWS_PREDEFINEDATTRVALUE)
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getByValueId(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long valueId) throws IcdmException {
    PredefinedValidityLoader loader = new PredefinedValidityLoader(getServiceData());
    Set<PredefinedValidity> ret = loader.getByValueId(valueId);
    return Response.ok(ret).build();
  }

  /**
   * @param input PredefinedValidityCreationModel
   * @return mapping objects with details
   * @throws IcdmException any error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response createValidity(final PredefinedValidityCreationModel input) throws IcdmException {

    MultiplePredefinedValidityCommand command = new MultiplePredefinedValidityCommand(getServiceData(), input);
    executeCommand(command);
    PredefinedValidityLoader loader = new PredefinedValidityLoader(getServiceData());
    Set<PredefinedValidity> setOfCreatedValidity = new HashSet<>();
    for (PredefinedValidity predefinedValidity : input.getValidityToBeCreated()) {
      setOfCreatedValidity.add(loader.getDataObjectByID(predefinedValidity.getId()));
    }
    return Response.ok(setOfCreatedValidity).build();
  }
}
