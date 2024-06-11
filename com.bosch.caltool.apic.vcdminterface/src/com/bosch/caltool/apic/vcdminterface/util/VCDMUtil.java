package com.bosch.caltool.apic.vcdminterface.util;

import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.easee.eASEEcdm_Service.EASEEService;

public class VCDMUtil {

  private static EASEEService easeeService;
  private static VCDMUtil utilInstance;


  public static EASEEService getVCDMService(final String username, final String password, final String domain,
      final int vCDMServer) throws Exception {
    if (utilInstance == null) {
      utilInstance = new VCDMUtil();

      easeeService = new EASEEService(CDMLogger.getInstance());

      easeeService.init(username, password, domain, vCDMServer);

      if (!easeeService.isWebServiceLoggedIn()) {
        throw new Exception("Web service login failed");
      }
    }
    return utilInstance.getEaseeService();
  }

  private EASEEService getEaseeService() {
    return easeeService;
  }
}
