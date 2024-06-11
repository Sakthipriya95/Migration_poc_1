/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSdomPverLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSdomA2lInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeNodeChildren;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;


/**
 * @author dja7cob Service Class to load the PIDC tree view
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_TREE)
public class PidcTreeViewService extends AbstractRestService {

  /**
   * service for fetching map of all level attributes ; key - attr id, value - attribute
   *
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_ALL_LVL_ATTR_ATTRID)
  @CompressData
  public Response getAllLvlAttrByAttrId() throws IcdmException {
    // Key - Attr ID , value - Attribute
    Map<Long, Attribute> lvlAttrMap = new HashMap<>();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    // Load all level attributes
    for (Attribute attr : attrLoader.getAllLevelAttributes().values()) {
      lvlAttrMap.put(attr.getId(), attr);
    }
    return Response.ok(lvlAttrMap).build();
  }

  /**
   * service for fetching map of all level attributes ; key - attribute level, value - attribute
   *
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_ALL_LVL_ATTR_LEVEL)
  @CompressData
  public Response getAllLvlAttrByLevel() throws IcdmException {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    // key - level, value - attribute
    return Response.ok(attrLoader.getAllLevelAttributes()).build();
  }

  /**
   * service for fetching map of all level attributes and their values ; key - attr id, value - set of attribute values
   *
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_ALL_LVL_ATTR_VALUES_SET)
  @CompressData
  public Response getAllPidTreeLvlAttrValueSet() throws IcdmException {
    Map<Long, Map<Long, AttributeValue>> attrValMap =
        new AttributeValueLoader(getServiceData()).getAllPidTreeLvlAttrValMap();
    return Response.ok(attrValMap).build();
  }

  /**
   * Service for fetching all pidc versions for a pidc
   *
   * @param pidcId Pidc Id
   * @param pidcVerId Pidc version id
   * @return response Pidc tree node children
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_VER_CHILD)
  @CompressData
  public Response getPidcNodeChildAvailblty(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId,
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVerId)
      throws IcdmException {
    // Details of the children of the PIDC
    PidcTreeNodeChildren nodeChildrenAvailability = new PidcTreeNodeChildren();
    PidcVersionLoader pidcVerLoader = new PidcVersionLoader(getServiceData());
    boolean isOtherVerPresent = pidcVerLoader.isOtherPidcVerPresent(pidcId, pidcVerId);
    nodeChildrenAvailability.setOtherPidcVerPresent(isOtherVerPresent);

    SortedSet<SdomPVER> sdomPverSet = new PidcSdomPverLoader(getServiceData()).getSdomPverByPidcVers(pidcVerId);
    nodeChildrenAvailability.setPidcSdomPverSet(sdomPverSet);
    nodeChildrenAvailability.setSdomPversPresent(!sdomPverSet.isEmpty());

    CDRReviewResultLoader pidcCdrLoader = new CDRReviewResultLoader(getServiceData());
    boolean isCdrPresent = pidcCdrLoader.getPidcCdrAvailability(pidcVerId);
    nodeChildrenAvailability.setCdrPresent(isCdrPresent);

    RvwQnaireResponseLoader qnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    boolean isQnaireRespPresent = qnaireRespLoader.isPidcQnaireRespPresent(pidcVerId);
    nodeChildrenAvailability.setQuestionnairesPresent(isQnaireRespPresent);

    Set<PidcVariant> pidcVarSet = getPidcVarSet(pidcVerId);
    nodeChildrenAvailability.setPidcVariants(pidcVarSet);
    return Response.ok(nodeChildrenAvailability).build();
  }

  /**
   * Service for fetching all sdom pvers for a pidc
   *
   * @param pidcVerId Pidc version id
   * @return map of pidc sdom a2l info
   * @throws IcdmException error while retrieving data
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_SDOM_PVER)
  @CompressData
  public Response getPidcSdomPvers(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVerId)
      throws IcdmException {

    SortedSet<SdomPVER> sdomPverSet = new PidcSdomPverLoader(getServiceData()).getSdomPverByPidcVers(pidcVerId);
    Map<String, PidcSdomA2lInfo> retMap = new HashMap<>();
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());
    for (SdomPVER sdomPver : sdomPverSet) {
      // fetch the details
      Map<Long, PidcA2l> pidcA2lFiles = loader.getAllBySdomPver(pidcVerId, sdomPver.getPverName());
      PidcSdomA2lInfo sdomA2lInfo = new PidcSdomA2lInfo();
      sdomA2lInfo.setSdomPver(sdomPver);
      sdomA2lInfo.setA2lMap(pidcA2lFiles);
      retMap.put(sdomPver.getPverName(), sdomA2lInfo);
    }
    // return the SdomPver associated to the PidcVersion
    return Response.ok(retMap).build();
  }


  /**
   * @param pidcVerId pidc ver id
   * @return
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  private Set<PidcVariant> getPidcVarSet(final Long pidcVerId) throws UnAuthorizedAccessException, DataException {
    PidcVariantLoader pidcVarLoader = new PidcVariantLoader(getServiceData());
    Set<PidcVariant> pidcVarSet = new HashSet<>();
    pidcVarSet.addAll(pidcVarLoader.getVariants(pidcVerId, false).values());
    return pidcVarSet;
  }

  /**
   * service for fetching pidc structure attr max level
   *
   * @return response
   * @throws IcdmException web service exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_PIDC_MAX_STRUCT_ATTR_LVL)
  @CompressData
  public Response getPidcStrucAttrMaxLevel() throws IcdmException {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    return Response.ok(attrLoader.getMaxStructAttrLevel()).build();
  }

}
