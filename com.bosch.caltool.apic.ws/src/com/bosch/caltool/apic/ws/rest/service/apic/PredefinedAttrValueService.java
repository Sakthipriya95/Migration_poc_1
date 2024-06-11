package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.HashSet;
import java.util.Map;
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
import com.bosch.caltool.icdm.bo.apic.attr.MultiplePredefinedAttrValuesCommand;
import com.bosch.caltool.icdm.bo.apic.attr.PredefinedAttrValueLoader;
import com.bosch.caltool.icdm.bo.apic.attr.PredefinedValidityLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValueAndValidtyModel;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValuesCreationModel;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;


/**
 * Service class for PredefinedAttrValue
 *
 * @author PDH2COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PREDEFINEDATTRVALUE)
public class PredefinedAttrValueService extends AbstractRestService {

  /**
   * Rest web service path for PredefinedAttrValue
   */
  public final static String RWS_PREDEFINEDATTRVALUE = "predefinedattrvalue";

  /**
   * Get PredefinedAttrValue using its id
   *
   * @param objId object's id
   * @return Rest response, with PredefinedAttrValue object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PredefinedAttrValueLoader loader = new PredefinedAttrValueLoader(getServiceData());
    PredefinedAttrValue ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get PredefinedAttrValue records
   *
   * @return Rest response, with Map of PredefinedAttrValue objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PREDEFINEDATTRVALUE)
  @CompressData
  public Response getByValueId(@QueryParam(WsCommonConstants.RWS_ATTRIBUTE_VALUE) final Long valueId)
      throws IcdmException {
    PredefinedAttrValueLoader loader = new PredefinedAttrValueLoader(getServiceData());
    Map<Long, PredefinedAttrValue> retMap = loader.getByValueId(valueId);
    getLogger().info(" PredefinedAttrValue getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }


  /**
   * Get PredefinedAttrValue records
   *
   * @param valueIdSet
   * @return Rest response, with Map of PredefinedAttrValue objects for given value ids
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PREDEFINED_ATTRVALUE_VALIDITY_FOR_VALUE_SET)
  @CompressData
  public Response getPredefinedAttrValuesForValues(final Set<Long> valueIdSet) throws IcdmException {
    // fetch predefined Attr values for value id set
    PredefinedAttrValueLoader predefinedAttrValueLoader = new PredefinedAttrValueLoader(getServiceData());
    Map<Long, Map<Long, PredefinedAttrValue>> predefAttrValMap =
        predefinedAttrValueLoader.getPredefinedAttrValueForAttrValueIds(valueIdSet);

    // fetch predefined validity for value id set
    PredefinedValidityLoader predefinedValidityLoader = new PredefinedValidityLoader(getServiceData());
    Map<Long, Map<Long, PredefinedValidity>> predefinedValidityMap =
        predefinedValidityLoader.getPredefinedValidityForValues(valueIdSet);

    PredefinedAttrValueAndValidtyModel model = new PredefinedAttrValueAndValidtyModel();
    model.setPredefinedAttrValueMap(predefAttrValMap);
    model.setPredefinedValidityMap(predefinedValidityMap);

    return Response.ok(model).build();
  }


  /**
   * @param input PredefinedAttrValuesCreationModel
   * @return mapping objects with details
   * @throws IcdmException any error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response createPredefinedValues(final PredefinedAttrValuesCreationModel input) throws IcdmException {

    MultiplePredefinedAttrValuesCommand command = new MultiplePredefinedAttrValuesCommand(getServiceData(), input);
    executeCommand(command);
    PredefinedAttrValueLoader loader = new PredefinedAttrValueLoader(getServiceData());
    Set<PredefinedAttrValue> setOfCreatedVals = new HashSet<>();
    for (PredefinedAttrValue predefinedVal : input.getValuesToBeCreated()) {
      setOfCreatedVals.add(loader.getDataObjectByID(predefinedVal.getId()));
    }
    return Response.ok(setOfCreatedVals).build();
  }
}
