/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

import com.bosch.caltool.apic.ws.oslc.resources.ConstantsOSLC;
import com.bosch.caltool.apic.ws.oslc.resources.OSLCPIDCVersion;
import com.bosch.caltool.apic.ws.oslc.servlets.AbstractRestService;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.oslc.ws.db.OSLCObjectStore;

/**
 * @author mkl2cob
 */
@OslcService(ConstantsOSLC.LOCAL_SERVER_NAMESPACE)
@Path("pidcVersion")
public class OSLCPIDVersionService extends AbstractRestService {

  @Context
  private HttpServletRequest httpRequest;
  @Context
  private HttpServletResponse httpResponse;

  /**
   * @param where TODO
   * @return OSLCPIDCVersion[]
   * @throws IcdmException any error while creating data
   */
  @OslcQueryCapability(title = "PIDC Version Query Capability", label = "PIDC Version Catalog Query", resourceShape = OslcConstants.PATH_RESOURCE_SHAPES +
      "/" + ConstantsOSLC.PATH_PIDC_VERSION_RESOURCE, resourceTypes = {
          ConstantsOSLC.TYPE_PIDC_VERSION }, usages = { OslcConstants.OSLC_USAGE_DEFAULT })
  @GET
  @Produces({ OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
  public OSLCPIDCVersion[] getResources(@QueryParam("oslc.where") final String where) throws IcdmException {
    // get all the pidc versions
    Map<Long, PidcVersion> allPIDCVersions = new PidcVersionLoader(getServiceData(this.httpRequest)).getAll();
    return allPIDCVersions.values().stream().map(this::convertIntoOSLCPIDCVersionObj)
        .toArray(size -> new OSLCPIDCVersion[size]);
  }

  /**
   * @param resourceId pidc version id
   * @return OSLCPIDCVersion
   */
  @GET
  @Produces({ OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
  @Path("{resourceId}")
  public Response getResource(@PathParam("resourceId") final String resourceId) {
    OSLCObjectStore.getLogger().info("PIDCVersionService.findById() started");
    long startTime = System.currentTimeMillis();
    try {
      PidcVersion pidcVersion =
          new PidcVersionLoader(getServiceData(this.httpRequest)).getDataObjectByID(Long.parseLong(resourceId));

      OSLCPIDCVersion oslcPIDCVersion = convertIntoOSLCPIDCVersionObj(pidcVersion);

      OSLCObjectStore.getLogger().info("PIDCVersionService.findById() completed. Time Taken = {}",
          System.currentTimeMillis() - startTime);
      this.httpResponse.setHeader("OSLC-Core-Version", "2.0");
      return Response.ok(oslcPIDCVersion).build();
    }
    catch (NumberFormatException exp) {
      OSLCObjectStore.getLogger().error(exp.getMessage(), exp);
      return Response.status(Status.NOT_ACCEPTABLE).build();
    }
    catch (DataException exp) {
      OSLCObjectStore.getLogger().error(exp.getMessage(), exp);
      return Response.status(Status.NO_CONTENT).build();
    }
    catch (UnAuthorizedAccessException exp) {
      OSLCObjectStore.getLogger().error(exp.getMessage(), exp);
      return Response.status(Status.UNAUTHORIZED).build();
    }

  }

  /**
   * @param pidcVersion
   * @return
   */
  private OSLCPIDCVersion convertIntoOSLCPIDCVersionObj(final PidcVersion pidcVersion) {
    // create an instance
    OSLCPIDCVersion oslcPIDCVersion = new OSLCPIDCVersion();
    // set the fields
    oslcPIDCVersion.setIdentifier(pidcVersion.getId().toString());
    SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_15);
    try {
      oslcPIDCVersion.setLastConfirmationDate(dateFormat.parse(pidcVersion.getLastConfirmationDate()));
      oslcPIDCVersion.setLastValidDate(dateFormat.parse(pidcVersion.getLastValidDate()));
    }
    catch (ParseException exp) {
      OSLCObjectStore.getLogger().warn(exp.getMessage(), exp);
    }
    oslcPIDCVersion.setName(pidcVersion.getName());
    oslcPIDCVersion.setVersion(pidcVersion.getVersion().toString());

    return oslcPIDCVersion;

  }

  /**
   * Updating the last confirmation date
   *
   * @param resourceId pidc version id
   * @param oslcPidcVersion OSLCPIDCVersion
   * @return Response
   */
  @PUT
  @Consumes({ OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
  @Produces({ OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
  @Path("{resourceId}")
  public Response updateLastConfrmDate(@PathParam("resourceId") final String resourceId,
      final OSLCPIDCVersion oslcPidcVersion) {

    OSLCObjectStore.getLogger().info("PIDCVersionService.update started");
    long startTime = System.currentTimeMillis();

    ServiceData servData;
    try {
      // get service data
      servData = getServiceData(this.httpRequest);

      // convert oslc pidc version to com.bosch.caltool.apic.ws.common.dataobject.apic.PIDCVersion
      PidcVersionLoader pidcVersLoader = new PidcVersionLoader(servData);
      long pidcVersionId = Long.parseLong(resourceId);
      PidcVersion vers = pidcVersLoader.getDataObjectByID(pidcVersionId);
      vers.setId(Long.parseLong(resourceId));
      vers.setLastConfirmationDate(
          DateFormat.formatDateToString(oslcPidcVersion.getLastConfirmationDate(), DateFormat.DATE_FORMAT_15));
      vers.setName(oslcPidcVersion.getName());
      if (oslcPidcVersion.getVersion() == null) {
        return Response.status(Status.PRECONDITION_FAILED).entity("Vesion not found").build();
      }
      vers.setVersion(Long.parseLong(oslcPidcVersion.getVersion()));

      // create and excecute update command
      PidcVersionCommand cmd = new PidcVersionCommand(servData, vers, true, false);
      executeCommand(cmd);
      // get the updated object for return
      PidcVersion updatedObj = new PidcVersionLoader(servData).getDataObjectByID(Long.parseLong(resourceId));
      OSLCPIDCVersion updatedOSLCPIDCVers = convertIntoOSLCPIDCVersionObj(updatedObj);
      OSLCObjectStore.getLogger().info("PIDCVersionService.update completed. ID = {}; Time Taken = {}",
          updatedObj.getName(), System.currentTimeMillis() - startTime);

      // return the updated resource
      this.httpResponse.setHeader("OSLC-Core-Version", "2.0");

      return Response.ok(updatedOSLCPIDCVers).build();
    }
    catch (UnAuthorizedAccessException exp) {
      OSLCObjectStore.getLogger().error(exp.getMessage(), exp);
      return Response.status(Status.UNAUTHORIZED).entity(exp.getMessage()).build();
    }
    catch (IcdmException exp) {
      OSLCObjectStore.getLogger().error(exp.getMessage(), exp);
      return Response.status(Status.CONFLICT).entity(exp.getMessage()).build();
    }


  }

}
