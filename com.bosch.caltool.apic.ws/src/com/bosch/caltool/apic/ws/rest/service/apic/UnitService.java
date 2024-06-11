/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.apic.UnitCommand;
import com.bosch.caltool.icdm.bo.apic.UnitLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.Unit;


/**
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_UNIT)
public class UnitService extends AbstractRestService {


  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_ALL_UNITS)
  @CompressData
  public Response getAllUnits() throws IcdmException {

    // Create loader object
    UnitLoader loader = new UnitLoader(getServiceData());
    SortedSet<Unit> retSet = loader.getAllUnits();

    getLogger().info("Unitservice.getAllUnit() completed. Number of Units = {}", retSet.size());

    return Response.ok(retSet).build();
  }

  /**
   * @param units set of Units
   * @return created Units
   * @throws IcdmException the icdm exception
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final Set<Unit> units) throws IcdmException {
    List<AbstractSimpleCommand> unitCmdList = new ArrayList<>();
    for (Unit unit : units) {
      UnitCommand cmd = new UnitCommand(getServiceData(), unit);
      unitCmdList.add(cmd);
    }
    executeCommand(unitCmdList);
    Set<Unit> retSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : unitCmdList) {
      Unit newData = ((UnitCommand) cmd).getNewData();
      retSet.add(newData);
      getLogger().info("Created Unit : {}", newData.getUnitName());
    }
    return Response.ok(retSet).build();
  }
}
