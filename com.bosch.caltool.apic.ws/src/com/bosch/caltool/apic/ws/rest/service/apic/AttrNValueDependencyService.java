package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * Service class for AttrNValueDependency
 *
 * @author dmo5cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_ATTRNVALUEDEPENDENCY)
public class AttrNValueDependencyService extends AbstractRestService {

  /**
   * Rest web service path for AttrNValueDependency
   */
  public final static String RWS_ATTRNVALUEDEPENDENCY = "attrnvaluedependency";

  /**
   * Get AttrNValueDependency using its id
   *
   * @param objId object's id
   * @return Rest response, with AttrNValueDependency object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  // @PATH should not given for Query param
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    AttrNValueDependencyLoader loader = new AttrNValueDependencyLoader(getServiceData());
    AttrNValueDependency ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Create a AttrNValueDependency record
   *
   * @param obj object to create
   * @return Rest response, with created AttrNValueDependency object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final AttrNValueDependency obj) throws IcdmException {
    AttrNValueDependencyCommand cmd = new AttrNValueDependencyCommand(getServiceData(), obj, false);
    executeCommand(cmd);
    AttrNValueDependency ret = cmd.getNewData();
    getLogger().info("Created AttrNValueDependency Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a AttrNValueDependency record
   *
   * @param obj object to update
   * @return Rest response, with updated AttrNValueDependency object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final AttrNValueDependency obj) throws IcdmException {
    AttrNValueDependencyCommand cmd = new AttrNValueDependencyCommand(getServiceData(), obj, true);
    executeCommand(cmd);
    AttrNValueDependency ret = cmd.getNewData();
    getLogger().info("Updated AttrNValueDependency Id : {}", ret.getId());
    return Response.ok(ret).build();
  }


  /**
   * get dependencies for given attribute
   *
   * @param attrId Attribute id
   * @return Rest response
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_ATTR_DEPN)
  @CompressData
  public Response getDependenciesByAttribute(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long attrId)
      throws IcdmException {
    AttrNValueDependencyLoader attributeValueLoader = new AttrNValueDependencyLoader(getServiceData());

    Set<AttrNValueDependency> retSet = attributeValueLoader.getAttributeDependencies(attrId, true);
    getLogger().info("AttributeValueService.getDependenciesByAttribute() completed");

    return Response.ok(retSet).build();
  }

  /**
   * get dependencies for given value
   *
   * @param valueId Value id
   * @return Rest response
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_VALUE_DEPN)
  @CompressData
  public Response getDependenciesByValue(@QueryParam(value = WsCommonConstants.RWS_ATTRIBUTE_VALUE) final Long valueId)
      throws IcdmException {
    AttrNValueDependencyLoader attributeValueLoader = new AttrNValueDependencyLoader(getServiceData());

    Set<AttrNValueDependency> retSet = attributeValueLoader.getValueDependencies(valueId, true);
    getLogger().info("AttributeValueService.getDependenciesByValue() completed");

    return Response.ok(retSet).build();
  }

  /**
   * Service for fetching variants availability for a pidc version
   *
   * @return response
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_VAL_DEPN_LVL_ATTRS)
  @CompressData
  public Response getValDepnForLvlAttrVal() throws IcdmException {
    Map<Long, Map<Long, Set<Long>>> retMap = new HashMap<>();
    Map<Long, Map<Long, AttributeValue>> allPidTreeLvlAttrValMap =
        new AttributeValueLoader(getServiceData()).getAllPidTreeLvlAttrValMap();

    for (Map<Long, AttributeValue> lvlAttrValSets : allPidTreeLvlAttrValMap.values()) {
      for (AttributeValue attributeValue : lvlAttrValSets.values()) {

        AttrNValueDependencyLoader attributeValueLoader = new AttrNValueDependencyLoader(getServiceData());
        Set<AttrNValueDependency> retSet = attributeValueLoader.getValueDependencies(attributeValue.getId(), true);
        Map<Long, Set<Long>> valDepnMap = new HashMap<>();
        for (AttrNValueDependency attrNValueDependency : retSet) {
          if (!attrNValueDependency.isDeleted()) {
            Set<Long> valSet = valDepnMap.get(attrNValueDependency.getDependentAttrId());
            if (null == valSet) {
              valSet = new HashSet<>();
              valDepnMap.put(attrNValueDependency.getDependentAttrId(), valSet);
            }
            valSet.add(attrNValueDependency.getDependentValueId());
          }
        }
        retMap.put(attributeValue.getId(), valDepnMap);

      }
    }

    getLogger().info("Get value dependencies for level attribute values completed");

    return Response.ok(retMap).build();
  }
}
