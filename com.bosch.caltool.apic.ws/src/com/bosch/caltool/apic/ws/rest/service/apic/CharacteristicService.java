package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.attr.CharacteristicLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;


/**
 * Service class for Characteristic
 *
 * @author dmo5cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_CHARACTERISTIC)
public class CharacteristicService extends AbstractRestService {

  /**
   * Rest web service path for Characteristic
   */
  public final static String RWS_CHARACTERISTIC = "characteristic";

  /**
   * Get Characteristic using its id
   *
   * @param objId object's id
   * @return Rest response, with Characteristic object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    CharacteristicLoader loader = new CharacteristicLoader(getServiceData());
    Characteristic ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get all Characteristics
   *
   * @return Rest response
   * @throws IcdmException IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAllCharacteristics() throws IcdmException {

    // Create parameter properties loader object
    CharacteristicLoader loader = new CharacteristicLoader(getServiceData());

    // Fetch Characteristic
    Set<Characteristic> retSet = loader.getAllCharacteristic();

    WSObjectStore.getLogger().info("Characteristics.getAllCharacteristics() completed. Number of definitions = {}",
        retSet.size());

    return Response.ok(retSet).build();
  }
}
