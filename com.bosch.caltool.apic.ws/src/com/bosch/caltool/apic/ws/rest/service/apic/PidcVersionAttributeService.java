/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDCVERSIONATTRIBUTE)
public class PidcVersionAttributeService extends AbstractRestService {

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

    PidcVersionAttributeLoader loader = new PidcVersionAttributeLoader(getServiceData());
    // Fetch Attribute
    PidcVersionAttribute ret = loader.getDataObjectByID(objId);
    getLogger().info("PidcVersionAttribute.findById() completed. ");
    return Response.ok(ret).build();
  }

  /**
   * Get Qnaire Config Attribute in a project
   *
   * @param pidcVersionId pidc Version Id
   * @return response with Qnaire Config attribute in the project
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_QNAIRE_CONFIG_ATTR)
  @CompressData
  public Response getQnaireConfigAttribute(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID) final Long pidcVersionId)
      throws IcdmException {

    String paramValue = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR);
    Long attId = Long.valueOf(paramValue);

    // crete Project attibute value module
    PidcVersionAttributeModel projModel =
        new ProjectAttributeLoader(getServiceData()).createModel(pidcVersionId, LOAD_LEVEL.L1_PROJ_ATTRS);
    PidcVersionAttribute ret = projModel.getPidcVersAttr(attId);

    if ((ret == null) || (ret.getValueId() == null)) {
      Attribute attr = new AttributeLoader(getServiceData()).getDataObjectByID(attId);
      throw new IcdmException("Attribute '" + attr.getName() + "' is not set in the project.");
    }

    return Response.ok(ret).build();
  }

  /**
   * Get Project Attribute by Attribute Id
   *
   * @param pidcVersionId pidc Version Id
   * @param pidcVersAttrId Attribute Id
   * @return response with Attribute Value in the project
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PIDC_VERS_ATTR_BY_ID)
  @CompressData
  public Response getPidcVersAttributeById(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID) final Long pidcVersionId,
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ATTR_ID) final String pidcVersAttrId)
      throws IcdmException {

    String paramValue = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.getType(pidcVersAttrId));
    Long attId = Long.valueOf(paramValue);

    // crete Project attibute value module
    PidcVersionAttributeModel projModel =
        new ProjectAttributeLoader(getServiceData()).createModel(pidcVersionId, LOAD_LEVEL.L1_PROJ_ATTRS);
    PidcVersionAttribute ret = projModel.getPidcVersAttr(attId);

    if ((ret == null) || (ret.getValueId() == null)) {
      Attribute attr = new AttributeLoader(getServiceData()).getDataObjectByID(attId);
      throw new IcdmException("Attribute '" + attr.getName() + "' is not set in the project.");
    }

    return Response.ok(ret).build();
  }


  /**
   * Get pidc version attribute for given attribute id and pidc version id
   *
   * @param pidcVersionId pidc Version Id
   * @param attributeId attribute ID
   * @return response with pidc version attribute in the project
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PIDC_VERSION_ATTR_FOR_ATTR)
  @CompressData
  public Response getPidcVersionAttribute(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID) final Long pidcVersionId,
      @QueryParam(WsCommonConstants.RWS_QP_ATTRIBUTE_ID) final Long attributeId)
      throws IcdmException {

    PidcVersionAttribute ret =
        new PidcVersionAttributeLoader(getServiceData()).getPidcVersionAttributeForAttr(pidcVersionId, attributeId);
    return Response.ok(ret).build();
  }

  /**
   * @param pidcVersionId pidcVersionId
   * @return pidcVersionWithDetails
   * @throws IcdmException Exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PIDC_VARIANT_ATTR_FOR_ATTR)
  @CompressData
  public Response getPidcAttributeForAttr(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcVersionId)
      throws IcdmException {
    ProjectAttributeLoader projAttrLoader = new ProjectAttributeLoader(getServiceData());
    PidcVersionWithDetails pidcVersionWithDetails = new PidcVersionWithDetails();
    // create Project attibute value model
    PidcVersionAttributeModel pidcVersionAttrModel = projAttrLoader.createModel(pidcVersionId, LOAD_LEVEL.L3_VAR_ATTRS);
    pidcVersionWithDetails.setPidcVersionAttributeMap(pidcVersionAttrModel.getPidcVersAttrMap());
    pidcVersionWithDetails.setPidcVariantAttributeMap(pidcVersionAttrModel.getAllVariantAttributeMap());
    pidcVersionWithDetails.setPidcVariantMap(pidcVersionAttrModel.getVariantMap());
    return Response.ok(pidcVersionWithDetails).build();
  }
}