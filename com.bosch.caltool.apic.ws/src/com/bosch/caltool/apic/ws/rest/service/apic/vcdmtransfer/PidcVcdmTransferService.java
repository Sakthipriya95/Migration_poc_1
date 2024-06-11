/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic.vcdmtransfer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.common.util.PasswordServiceWrapper;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.EASEELogger;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVcdmTransferInput;
import com.bosch.caltool.security.Decryptor;
import com.bosch.easee.eASEEcdm_Service.EASEEService;


/**
 * @author dmo5cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_TRANSFER_VCDM_DATA)
public class PidcVcdmTransferService extends AbstractRestService {


  /**
   * transfer pidc to vCDM
   *
   * @param input vCDM Transfer Input
   * @return response
   * @throws IcdmException exception instance
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response transferPidc(final PidcVcdmTransferInput input) throws IcdmException {

    getLogger().debug("PidcVcdmTransferService.transferPidc() : Transfering PIDC to vCDM, for PIDC {}...",
        input.getPidc().getId());

    EASEEService easeeLoggedInUserService = createEaseeServiceCurrentUser(input.getEncPwd());
    EASEEService easeeSuperService = createEaseeServiceSuperUser();

    // changes to add english language to service data
    // PIDC transfer to vCDM must happen in English
    try (ServiceData srvDataEng = new ServiceData()) {

      getServiceData().copyTo(srvDataEng, false);
      srvDataEng.setLanguage(Language.ENGLISH.getText());

      VCDMDataStore dataStore = new VCDMDataStore();
      dataStore.setPidCard(new PidcLoader(srvDataEng).getDataObjectByID(input.getPidc().getId()));

      PidcVcdmTransfer transfer = new PidcVcdmTransfer(srvDataEng, easeeLoggedInUserService, easeeSuperService);
      transfer.transferPIDC(dataStore);
      getLogger().info("transfer of PIDC finished. Status = {}", !dataStore.isErrorOccured());
    }

    Pidc ret = new PidcLoader(getServiceData()).getDataObjectByID(input.getPidc().getId());

    getLogger().info("PidcVcdmTransferService.transferPidc() : Transfer completed.");

    return Response.ok(ret).build();
  }


  /**
   * @param easeeLogger
   * @param encryptdPwd
   * @return
   * @throws IcdmException
   */
  private EASEEService createEaseeServiceCurrentUser(final String encryptdPwd) throws IcdmException {
    String wsDomain = Messages.getString("EASEEService.DOMAAIN_NAME");

    int serverType = getEaseeServerType();

    String pwd = Decryptor.getInstance().decrypt(encryptdPwd, getLogger());

    EASEEService easeeLoggedInUserService = new EASEEService(EASEELogger.getInstance());
    easeeLoggedInUserService.init(getServiceData().getUsername(), pwd, wsDomain, serverType);

    if (!easeeLoggedInUserService.isWebServiceLoggedIn()) {
      throw new IcdmException("EASEE Service login failed for user : " + getServiceData().getUsername());
    }

    return easeeLoggedInUserService;
  }


  private int getEaseeServerType() {
    int serverType = EASEEService.DGS_CDM_PRO;
    if ("DGS_CDM_QUA".equals(Messages.getString("EASEEService.WS_SERVER"))) {
      serverType = EASEEService.DGS_CDM_QUA;
    }
    return serverType;
  }


  /**
   * @param easeeLogger
   * @return
   * @throws Exception
   */
  private EASEEService createEaseeServiceSuperUser() throws IcdmException {
    final String wsUser = Messages.getString(CommonUtilConstants.EASEE_SERVICE_USER_NAME);

    // Get the passord from the Web service
    String passwordKey = Messages.getString(CommonUtilConstants.EASEE_SERVICE_USER_PASS);
    final PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(getLogger());
    final String wsPassword = Decryptor.getInstance().decrypt(passWordWrapper.getPassword(passwordKey), getLogger());

    String wsDomain = Messages.getString(CommonUtilConstants.EASEE_SERVICE_DOMAIN_NAME);

    int serverType = getEaseeServerType();

    EASEEService easeeSuperService = new EASEEService(EASEELogger.getInstance());

    easeeSuperService.init(wsUser, wsPassword, wsDomain, serverType);

    if (!easeeSuperService.isWebServiceLoggedIn()) {
      throw new IcdmException("EASEE Service login failed for user : " + wsUser);
    }

    return easeeSuperService;
  }

  /**
   * @param aprjValue APRJ name
   * @return response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response findAPRJId(@QueryParam(value = WsCommonConstants.RWS_ATTRIBUTE_VALUE) final String aprjValue)
      throws IcdmException {

    EASEEService easeeSuperService = createEaseeServiceSuperUser();

    PidcVcdmTransfer transfer = new PidcVcdmTransfer(getServiceData(), null, easeeSuperService);
    String aprjId = transfer.getAPRJID(aprjValue);

    getLogger().info("PidcVcdmTransferService.findAPRJId() completed.");
    return Response.ok(aprjId).build();
  }

}
