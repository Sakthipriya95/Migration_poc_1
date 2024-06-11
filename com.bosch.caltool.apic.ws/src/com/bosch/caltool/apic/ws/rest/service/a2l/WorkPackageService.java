/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.HashMap;
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
import com.bosch.caltool.icdm.bo.wp.WorkPackageCommand;
import com.bosch.caltool.icdm.bo.wp.WorkPkgLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.model.wp.WorkPkgInput;


/**
 * Services for Work packages
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_WP)
public class WorkPackageService extends AbstractRestService {


  /**
   * Fetch Work Package with the given Id
   *
   * @param objId Primary Key
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response findById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {

    WorkPkgLoader loader = new WorkPkgLoader(getServiceData());

    // Fetch WP with given ID
    WorkPkg ret = loader.getDataObjectByID(objId);

    return Response.ok(ret).build();

  }

  /**
   * Create a WorkPackage
   *
   * @param input WorkPackage
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final WorkPkg input) throws IcdmException {

    WorkPackageCommand cmd = new WorkPackageCommand(getServiceData(), input);
    executeCommand(cmd);

    WorkPkg ret = cmd.getNewData();

    WSObjectStore.getLogger().info("WorkPackage.create completed. ID = {} ", ret.getId());

    return Response.ok(ret).build();


  }

  /**
   * Update an existing WorkPackage
   *
   * @param input WorkPackage details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final WorkPkg input) throws IcdmException {

    WorkPackageCommand cmd = new WorkPackageCommand(getServiceData(), input, true);
    executeCommand(cmd);

    WorkPkg ret = cmd.getNewData();

    WSObjectStore.getLogger().info("WorkPackage.update completed. ID = {}", ret.getId());

    return Response.ok(ret).build();


  }

  /**
   * Fetch all WorkPackages defined in the system
   *
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response findAll() throws IcdmException {

    WorkPkgLoader loader = new WorkPkgLoader(getServiceData());

    // Fetch all WorkPackages
    Set<WorkPkg> retSet = loader.findAll();

    WSObjectStore.getLogger().info("WorkPackage.findAll() completed. Number of wps = {} ", retSet.size());

    return Response.ok(retSet).build();

  }

  /**
   * @param workPkgInput
   * @param inputWps
   * @return
   * @throws IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_WP_RESPONSE_MAP)
  @CompressData
  public Response getWorkRespMap(final WorkPkgInput workPkgInput) throws IcdmException {
    WorkPkgLoader loader = new WorkPkgLoader(getServiceData());
    Map<Long, String> retSet = loader.getWorkRespMap(workPkgInput.getDivValId(), workPkgInput.getWorkPkgSet());
    return Response.ok(retSet).build();
  }

  /**
   * @param qnaireId as input
   * @return workpkg
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_WORKPKG)
  @CompressData
  public Response getWorkPkgbyQnaireID(@QueryParam(value = WsCommonConstants.RWS_QP_QNAIRE_ID) final Long qnaireId)
      throws IcdmException {
    WorkPkgLoader loader = new WorkPkgLoader(getServiceData());
    WorkPkg workPkg = loader.getWorkPkgbyQnaireID(qnaireId);
    return Response.ok(workPkg).build();

  }


  /**
   * @param qnaireIdList
   * @return
   * @throws IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_WORKPKG_NAME)
  @CompressData
  public Response getWorkPkgNameUsingQnaireIDSet(final Set<Long> qnaireIdList) throws IcdmException {

    WorkPkgLoader loader = new WorkPkgLoader(getServiceData());

    Map<Long, String> qnaireAndWrkPkgNameMap = new HashMap<>();

    for (Long qnaireId : qnaireIdList) {
      WorkPkg workPkg = loader.getWorkPkgbyQnaireID(qnaireId);
      qnaireAndWrkPkgNameMap.put(qnaireId, workPkg.getName());
    }
    return Response.ok(qnaireAndWrkPkgNameMap).build();
  }


}
