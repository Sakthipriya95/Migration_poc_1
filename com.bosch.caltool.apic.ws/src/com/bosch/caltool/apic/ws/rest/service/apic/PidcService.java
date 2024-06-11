/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Arrays;
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
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.PidcCreationCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoaderExternal;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcTypeV2Loader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.ExternalPidcVersionWithAttributes;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationRespData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributes;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributesV2;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.uc.ProjectUsecaseModel;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC)
public class PidcService extends AbstractRestService {

  /**
   * /**
   *
   * @param objId PIDC ID
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response findById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {

    PidcLoader loader = new PidcLoader(getServiceData());

    // Fetch FC2WPVersion
    Pidc ret = loader.getDataObjectByID(objId);

    return Response.ok(ret).build();

  }


  /**
   * @param pidcID Long
   * @param pidcVersID Long
   * @param ucIdSet Set<Long>
   * @return Response
   * @throws IcdmException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_WITH_ATTRS)
  @CompressData
  public Response getPidcWithAttributesForVersion(
      @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcID,
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersID,
      @QueryParam(value = WsCommonConstants.RWS_QP_UC_ID) final Set<Long> ucIdSet)
      throws IcdmException {

    // Create loader object
    PidcLoaderExternal loader = new PidcLoaderExternal(getServiceData());
    PidcVersionWithAttributes pidcWithAttrs = loader.getProjectIdCardWithAttrs(pidcID, ucIdSet, pidcVersID);


    WSObjectStore.getLogger().info(
        "PidcService.getPidcWithAttributes() completed. Project attributes = {}, Variants = {}, Attributes = {}, Values = {}",
        pidcWithAttrs.getPidcAttributeMap().size(), pidcWithAttrs.getVariantMap().size(),
        pidcWithAttrs.getAttributeMap().size(), pidcWithAttrs.getAttributeValueMap().size());

    return Response.ok(pidcWithAttrs).build();
  }


  /**
   * @param pidcID Long
   * @param pidcVersID Long
   * @param ucIdSet Set<Long>
   * @return Response
   * @throws IcdmException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_EXT_PIDC_WITH_ATTRS)
  @CompressData
  public Response getExternalPidcWithAttributesForVersion(
      @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcID,
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersID,
      @QueryParam(value = WsCommonConstants.RWS_QP_UC_ID) final Set<Long> ucIdSet)
      throws IcdmException {

    // Create loader object
    PidcLoaderExternal loader = new PidcLoaderExternal(getServiceData());
    PidcVersionWithAttributes pidcWithAttrs = loader.getProjectIdCardWithAttrs(pidcID, ucIdSet, pidcVersID);

    ExternalPidcVersionWithAttributes extPidcWithAttrs = loader.setExtPidcVersWithDetails(pidcWithAttrs);

    getLogger().info(
        "PidcService.getExternalPidcWithAttributesForVersion() completed. Project attributes = {}, Variants = {}, Attributes = {}, Values = {}",
        pidcWithAttrs.getPidcAttributeMap().size(), pidcWithAttrs.getVariantMap().size(),
        pidcWithAttrs.getAttributeMap().size(), pidcWithAttrs.getAttributeValueMap().size());

    return Response.ok(extPidcWithAttrs).build();
  }


  /**
   * @param attrValueId as input
   * @return map of pidc users
   * @throws IcdmException as exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDCUSERS_BY_ATTR_VAL_ID)
  @CompressData
  public Response getPidcUsersUsingAttrValue(
      @QueryParam(value = WsCommonConstants.RWS_QP_ATTR_VALUE_ID) final Long attrValueId)
      throws IcdmException {
    PidcLoader loader = new PidcLoader(getServiceData());
    Map<String, Map<String, Map<String, Long>>> pidcUsersMap = loader.getPidcUsersUsingAttrValue(attrValueId);
    return Response.ok(pidcUsersMap).build();
  }

  /**
   * @param nameValID Name value id of pidc
   * @return Response
   * @throws IcdmException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_BY_NAMEVAL_ID)
  @CompressData
  public Response getByNameValueId(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long nameValID)
      throws IcdmException {
    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    return Response.ok(pidcLoader.getPidcByValID(nameValID)).build();
  }

  /**
   * @param pidccreationData Pidc creation input data
   * @return mapping objects with details
   * @throws IcdmException any error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response createPidc(final PidcCreationData pidccreationData) throws IcdmException {

    PidcCreationCommand pidcCreationCmd = new PidcCreationCommand(getServiceData(), pidccreationData);
    executeCommand(pidcCreationCmd);

    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    Pidc pidcCreated = pidcLoader.getDataObjectByID(pidccreationData.getPidc().getId());
    NodeAccessLoader nodeAccessLoader = new NodeAccessLoader(getServiceData());
    NodeAccess nodeAccessCreated = nodeAccessLoader.getDataObjectByID(pidccreationData.getCreatedNodeAccess().getId());
    PidcCreationRespData pidcData = new PidcCreationRespData();
    pidcData.setNodeAccess(nodeAccessCreated);
    pidcData.setPidc(pidcCreated);
    createMailForHotline(pidcCreated);
    return Response.ok(pidcData).build();
  }

  /**
   * ICdm-1117 create mail for Hotine for new PIDC created Send mail for Hotline
   *
   * @throws IcdmException
   * @throws DataException
   */
  private void createMailForHotline(final Pidc pidc) throws IcdmException {
    Map<Long, PidcVersionInfo> activeVersionWithStructureAttributes = new PidcVersionLoader(getServiceData())
        .getActiveVersionWithStructureAttributes(new HashSet<Long>(Arrays.asList(pidc.getId())));
    PidcVersionInfo pidcVersionInfo = activeVersionWithStructureAttributes
        .get(new PidcVersionLoader(getServiceData()).getActivePidcVersion(pidc.getId()).getId());
    final StringBuilder attrLevelStrBuilder = new StringBuilder();
    for (PidcVersionAttribute pidcVersionAttribute : pidcVersionInfo.getLevelAttrMap().values()) {
      attrLevelStrBuilder.append(pidcVersionAttribute.getName() + " - " + pidcVersionAttribute.getValue());
      attrLevelStrBuilder.append(", ");
    }
    attrLevelStrBuilder.deleteCharAt(attrLevelStrBuilder.length() - ApicConstants.COLUMN_INDEX_2);
    String username = getServiceData().getUsername();
    User currentUser = new UserLoader(getServiceData()).getDataObjectByUserName(username);
    // Implement a new method and send the mail
    final MailHotline mailHotline = getHotlineNotifier(username);
    mailHotline.setSubject(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.MAIL_NEW_PIDC));
    mailHotline.notifyNewPIDC(attrLevelStrBuilder.toString(), pidc.getName(), currentUser.getDepartment(),
        currentUser.getFirstName(), currentUser.getLastName());

  }

  /**
   * Update a PIDC record
   *
   * @param obj object to update
   * @return Rest response, with updated PIDC object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final Pidc obj) throws IcdmException {
    PidcCommand pidcCommand = new PidcCommand(getServiceData(), obj, true, false);
    executeCommand(pidcCommand);
    Pidc ret = pidcCommand.getNewData();
    getLogger().info("Updated PIDC Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @param pidcID Project Id Card Id
   * @param pidcVersID Pidc Version Id
   * @param ucIdSet Use case Id set
   * @return {@link PidcVersionWithAttributesV2} object
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_WITH_ATTRS_V2)
  @CompressData
  public Response getPidcWithAttributesForV2(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcID,
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersID,
      @QueryParam(value = WsCommonConstants.RWS_QP_UC_ID) final Set<Long> ucIdSet)
      throws IcdmException {

    // Create loader object
    PidcLoaderExternal loader = new PidcLoaderExternal(getServiceData());
    PidcVersionWithAttributesV2 pidcWithAttrs = loader.getProjectIdCardV2WithAttrs(pidcID, ucIdSet, pidcVersID);


    WSObjectStore.getLogger().info(
        "PidcService.getPidcWithAttributesV2() completed. Project attributes = {}, Variants = {}, Attributes = {}, Values = {}",
        pidcWithAttrs.getPidcAttributeMap().size(), pidcWithAttrs.getVariantMap().size(),
        pidcWithAttrs.getAttributeMap().size(), pidcWithAttrs.getAttributeValueMap().size());

    return Response.ok(pidcWithAttrs).build();
  }


  /**
   * @param pidcID Project Id Card Id
   * @param pidcVersID Pidc Version Id
   * @param ucIdSet Use case Id set
   * @return {@link PidcVersionWithAttributesV2} object
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_TYPE_V2)
  @CompressData
  public Response getPidcTypeV2(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcID,
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersID,
      @QueryParam(value = WsCommonConstants.RWS_QP_UC_ID) final Set<Long> ucIdSet)
      throws IcdmException {

    // Create loader object
    PidcLoaderExternal loader = new PidcLoaderExternal(getServiceData());
    PidcVersionWithAttributesV2 pidcWithAttrs = loader.getProjectIdCardV2WithAttrs(pidcID, ucIdSet, pidcVersID);

    PidcTypeV2Loader pidcTypeV2Loader = new PidcTypeV2Loader(getServiceData());

    return Response.ok(pidcTypeV2Loader.getProjectIdCardV2(pidcWithAttrs)).build();
  }

  /**
   * @param pidcId - project id
   * @return set of attrIds mapped to project usecase favorites
   * @throws IcdmException - exception while invoking service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_UC_ATTR)
  @CompressData
  public Response getProjectUsecaseModel(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId)
      throws IcdmException {
    ProjectUsecaseModel model = new PidcLoader(getServiceData()).getProjectUsecaseModel(pidcId);
    return Response.ok(model).build();
  }

}
