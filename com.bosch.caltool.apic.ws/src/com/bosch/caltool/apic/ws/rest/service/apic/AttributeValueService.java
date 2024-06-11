/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttrValueCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * Rest service for attribute value
 *
 * @author NIP4COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_ATTRIBUTE_VALUE)
public class AttributeValueService extends AbstractRestService {

  /**
   * String constant for update attribute value id
   */
  private static final String UPDATED_ATTRIBUTE_VALUE_ID = "Updated AttributeValue Id : {}";

  /**
   * get attribute values for given attributes
   *
   * @param attrId attribute id
   * @return Rest response
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getValuesByAttribute(@QueryParam(value = WsCommonConstants.RWS_QP_ATTRIBUTE_ID) final Long attrId)
      throws IcdmException {
    AttributeValueLoader attributeValueLoader = new AttributeValueLoader(getServiceData());
    Set<Long> attrIdSet = new HashSet<Long>();
    attrIdSet.add(attrId);
    // fetch attribute value
    Map<Long, Map<Long, AttributeValue>> retMap = attributeValueLoader.getValuesByAttribute(attrIdSet);
    getLogger().info("AttributeValueService.getValuesByAttributes() completed");

    return Response.ok(retMap).build();
  }

  /**
   * get attribute values for given attribute and pidc version
   *
   * @param pidcVersId pidc Version id
   * @param attrId attribute id
   * @return Rest response
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ATTR_VALUE_BY_PIDCVERSION)
  @CompressData
  public Response getValueByAttribute(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersId,
      @QueryParam(value = WsCommonConstants.RWS_QP_ATTRIBUTE_ID) final Long attrId)
      throws IcdmException {
    AttributeValueLoader attributeValueLoader = new AttributeValueLoader(getServiceData());
    // fetch attribute value
    AttributeValue retMap = attributeValueLoader.getAttributeValue(pidcVersId, attrId);
    getLogger().info("AttributeValueService.getValuesByAttributes() completed");

    return Response.ok(retMap).build();
  }

  /**
   * @param attrId
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_VALUE_DEP)
  @CompressData
  public Response getValueDependecyMap(@QueryParam(value = WsCommonConstants.RWS_QP_ATTRIBUTE_ID) final Long attrId)
      throws IcdmException {

    Map<Long, Set<AttrNValueDependency>> retMap = new HashMap<>();
    AttrNValueDependencyLoader depLoader = new AttrNValueDependencyLoader(getServiceData());
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    // fetch attribute values
    Map<Long, AttributeValue> attrValuesMap = attrValLoader.getAttrValues(attrId);
    // fetch dependencies for attr values
    for (AttributeValue attrVal : attrValuesMap.values()) {
      Set<AttrNValueDependency> valueDependencies = depLoader.getValueDependencies(attrVal.getId(), false);
      if (!valueDependencies.isEmpty()) {
        retMap.put(attrVal.getId(), valueDependencies);
      }
    }

    return Response.ok(retMap).build();
  }

  /**
   * get predefined validity for given attribute value id
   *
   * @param attrValueId
   * @return Rest response
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  @Path(WsCommonConstants.RWS_PREDEFINEDATTRVALUE)
  public Response getValuesByAttributeValue(
      @QueryParam(value = WsCommonConstants.RWS_ATTRIBUTE_VALUE) final Long attrValueId)
      throws IcdmException {
    AttributeValueLoader attributeValueLoader = new AttributeValueLoader(getServiceData());

    Map<Long, PredefinedValidity> retMap = attributeValueLoader.getPredefinedValidityForAttrValue(attrValueId);
    getLogger().info("AttributeValueService.getValuesByAttributeValue() completed");

    return Response.ok(retMap).build();
  }


  /**
   * Get AttributeValue using its id
   *
   * @param objId object's id
   * @return Rest response, with AttributeValue object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_QP_OBJ_ID)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
    AttributeValue ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
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
  @CompressData
  public Response create(final AttributeValue obj) throws IcdmException {
    AttrValueCommand cmd = new AttrValueCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    AttributeValue ret = cmd.getNewData();
    getLogger().info("Created AttributeValue Id : {}", ret.getId());
    if (cmd.getNewData().getClearingStatus().equals(CLEARING_STATUS.NOT_CLEARED.getDBText())) {
      sendMailNotification(cmd.getNewData());
    }
    return Response.ok(ret).build();
  }


  /**
   * iCDM-834 <br>
   * Send mail notification
   *
   * @throws IcdmException
   */
  private void sendMailNotification(final AttributeValue attrValue) throws IcdmException {
    Attribute attribute = new AttributeLoader(getServiceData()).getDataObjectByID(attrValue.getAttributeId());
    // Additional clearance check, if caller does not check for clearance,avaoid sending mail
    final MailHotline mailHotline = getHotlineNotifier(getServiceData().getUsername());
    // get appropriate SUBJECT from table
    mailHotline.setSubject(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_ATTR_CLEARING_SUBJECT));
    // Icdm-1154 get the Value of the Attribute for all types
    // ICDM-2624 Mail notification for grouped attribute
    mailHotline.send4Clearance(attribute.getName(), attrValue.getName(), attribute.isGroupedAttr());
  }


  /**
   * Update a AttributeValue record
   *
   * @param obj object to update
   * @return Rest response, with updated AttributeValue object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final AttributeValue obj) throws IcdmException {
    AttrValueCommand cmd = new AttrValueCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    AttributeValue ret = cmd.getNewData();
    getLogger().info(UPDATED_ATTRIBUTE_VALUE_ID, ret.getId());
    return Response.ok(ret).build();
  }


  /**
   * Update AttributeValue record - pidc name
   *
   * @param obj object to update
   * @return Rest response, with updated AttributeValue object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_UPD_PIDC_NAME)
  @CompressData
  public Response updatePidcName(final AttributeValue obj) throws IcdmException {
    AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
    AttributeValue attrValToUpd = loader.getDataObjectByID(obj.getId());
    attrValToUpd.setTextValueEng(obj.getTextValueEng());
    attrValToUpd.setTextValueGer(obj.getTextValueGer());
    attrValToUpd.setDescriptionEng(obj.getDescriptionEng());
    attrValToUpd.setDescriptionGer(obj.getDescriptionGer());
    AttrValueCommand cmd = new AttrValueCommand(getServiceData(), attrValToUpd, true, false);
    executeCommand(cmd);
    AttributeValue ret = cmd.getNewData();
    getLogger().info(UPDATED_ATTRIBUTE_VALUE_ID, ret.getId());
    return Response.ok(ret).build();
  }


  /**
   * Check if level attribute value ids are used in project id cards
   *
   * @param attrValIdList attribute value id list
   * @return boolean
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_LEVEL_ATTR_VALUE_USED)
  @CompressData
  public Response getLevelAttrValueUsedStatus(
      @QueryParam(WsCommonConstants.RWS_QP_ATTR_VALUE_ID) final List<Long> attrValIdList)
      throws IcdmException {
    PidcVersionAttributeLoader loader = new PidcVersionAttributeLoader(getServiceData());
    boolean ret = loader.isUsedInLevelAttribute(attrValIdList);
    return Response.ok(ret).build();
  }

  /**
   * Delete a AttributeValue record
   *
   * @param objId id of object to delete
   * @param unDelete true means undelete operation
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId,
      @QueryParam(WsCommonConstants.RWS_UNDELETE) final Boolean unDelete)
      throws IcdmException {
    AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
    AttributeValue obj = loader.getDataObjectByID(objId);
    AttrValueCommand cmd = new AttrValueCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);

    return Response.ok().build();
  }

  /**
   * get attribute values for given attributes
   *
   * @param attrId attribute id
   * @return Rest response
   * @throws IcdmException exception
   */
  @GET
  @Path(WsCommonConstants.RWS_GET_ALL)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getFeatureMappedAttrValues(
      @QueryParam(value = WsCommonConstants.RWS_QP_ATTRIBUTE_ID) final Long attrId)
      throws IcdmException {

    AttributeValueLoader loader = new AttributeValueLoader(getServiceData());

    SortedSet<AttributeValue> mappedAttrVal = loader.getFeatureMappedAttrValues(attrId);


    getLogger().info("AttributeValueService.getFeatureMappedAttrValues() completed");

    return Response.ok(mappedAttrVal).build();
  }

  /**
   * Delete a Pidc record
   *
   * @param pidcNameVal
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_DEL_UNDEL_PIDC)
  @CompressData
  public Response deleteUndelPidc(final AttributeValue pidcNameVal) throws IcdmException {
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    AttributeValue attrValToBeUpdated = attrValLoader.getDataObjectByID(pidcNameVal.getId());
    attrValToBeUpdated.setDeleted(pidcNameVal.isDeleted());
    AttrValueCommand attrValCmd = new AttrValueCommand(getServiceData(), attrValToBeUpdated, true, false);
    executeCommand(attrValCmd);
    AttributeValue ret = attrValCmd.getNewData();
    getLogger().info(UPDATED_ATTRIBUTE_VALUE_ID, ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Path(WsCommonConstants.RWS_MAIL_TEMPLATE)
  @CompressData
  public Response getMailContent() throws IcdmException {
    byte[] ret = new IcdmFilesLoader(getServiceData())
        .getEntityObject(Long
            .valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.DELETE_ATTR_VAL_MAIL_TEMPLATE)))
        .getTabvIcdmFileData().getFileData();

    return Response.ok(ret).build();
  }
}
