/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.calcomp.commonutil.CommonUtils;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.cdr.FeatureLoader;
import com.bosch.caltool.icdm.bo.wp.WpmlWpMasterlistLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_ATTRIBUTE)
public class AttributeService extends AbstractRestService {

  /**
   * Get an attribute by ID
   *
   * @param objId Attribute ID
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response findById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {

    AttributeLoader loader = new AttributeLoader(getServiceData());
    // Fetch Attribute
    Attribute ret = loader.getDataObjectByID(objId);
    getLogger().info("AttributeService.findById() completed. ");
    return Response.ok(ret).build();
  }

  /**
   * Get all attributes
   *
   * @param includeDeleted if true, deleted attributes are also included in response
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll(@QueryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED) final boolean includeDeleted)
      throws IcdmException {

    // Create loader object
    AttributeLoader loader = new AttributeLoader(getServiceData());
    Map<Long, Attribute> retMap = loader.getAllAttributes(includeDeleted);
    getLogger().info("AttributeService.getAll() completed. Number of attributes = {}", retMap.size());

    return Response.ok(retMap).build();
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
  public Response create(final Attribute obj) throws IcdmException {
    AttributeCommand cmd = new AttributeCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    Attribute ret = cmd.getNewData();
    getLogger().info("Created Attribute Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a Attribute record
   *
   * @param obj object to update
   * @return Rest response, with updated Attribute object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final Attribute obj) throws IcdmException {
    AttributeCommand cmd = new AttributeCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    Attribute ret = cmd.getNewData();
    getLogger().info("Updated Attribute Id : {}", ret.getId());
    return Response.ok(ret).build();
  }


  /**
   * Get all attribute ids with uncleared values
   *
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_UNCLEARED_ATTRIDS)
  @CompressData
  public Response getUnClearedAttrIds() throws IcdmException {

    // Create loader object
    AttributeLoader loader = new AttributeLoader(getServiceData());
    List<Long> retSet = loader.getAllAttrsWithUnClearedVals();
    getLogger().info("AttributeService.getUnClearedAttrIds() completed. Number of attributes = {}", retSet.size());

    return Response.ok(retSet).build();


  }


  /**
   * Get all attribute ids with uncleared values
   *
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_MAPPED_ATTR)
  @CompressData
  public Response getMappedAttr() throws IcdmException {

    FeatureLoader feaLoader = new FeatureLoader(getServiceData());
    ConcurrentMap<Long, Feature> allFeatures = feaLoader.fetchAllFeatures();

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());

    SortedSet<Attribute> attrSet = attrLoader.getMappedAttr(allFeatures);

    return Response.ok(attrSet).build();
  }

  /**
   * Get Attributes Export Model
   *
   * @return response
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_ATTRIBUTE_EXPORT_MODEL)
  @CompressData
  public Response getAttrExportModel() throws UnAuthorizedAccessException, DataException {

    AttributeLoader attrLdr = new AttributeLoader(getServiceData());
    AttrExportModel attrExp = attrLdr.getAtrrExportModel();
    getLogger().debug("AttributeService.getAttrExportModel() completed.");
    return Response.ok(attrExp).build();
  }

  /**
   * Get all Quotation relevant Attributes mapped to usecase for the given list of MCR_ID
   *
   * @param mcrIds list of mcrId in WpmlWpMasterlist
   * @return Rest response, with Map of Quotation relevant Attributes - key - Attr id, value -Attribute model
   * @throws IcdmException exception while invoking service
   * @throws InvalidInputException
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_QUO_REL_ATTR_BY_MCR_ID)
  @CompressData
  public Response getQuotRelAttrByMcrId(@QueryParam(value = WsCommonConstants.RWS_QP_MCR_ID) final Set<String> mcrIds)
      throws IcdmException {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Map<Long, Attribute> attrMap = new HashMap<>();
    int invalidMcrIdCount = 0;
    for (String mcrId : mcrIds) {
      if (CommonUtils.isNullOrEmpty(new WpmlWpMasterlistLoader(getServiceData()).getWmplByMcrId(mcrId))) {
        invalidMcrIdCount++;
      }
      else {
        Map<Long, Attribute> respMap = attrLoader.getQuotRelAttrByMcrId(mcrId);
        // Set values of respMap to attrMap if it is not already available
        respMap.forEach(attrMap::putIfAbsent);
      }
    }
    if (CommonUtils.isNullOrEmpty(attrMap) && CommonUtils.isEquals(mcrIds.size(), invalidMcrIdCount)) {
      throw new InvalidInputException("Given mcrIds are invalid");
    }
    getLogger().info("Get quotation relevant Attr mapped to usecse section By McrId completed. Total records = {}",
        attrMap.size());
    return Response.ok(attrMap).build();
  }


}
