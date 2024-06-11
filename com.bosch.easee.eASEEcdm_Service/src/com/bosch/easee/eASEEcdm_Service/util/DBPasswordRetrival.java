/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.easee.eASEEcdm_Service.util;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.util.LoggerUtil;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;

/**
 * CDL -
 * 
 * @author VAU3COB
 */

public class DBPasswordRetrival {

  private static DBPasswordRetrival dbPwdRetrival;

  private static ILoggerAdapter logger = LoggerUtil.getLogger();;

  private DBPasswordRetrival() {

  }

  /**
   * singleton method for DBPasswordRetrival class
   * 
   * @return dbPwdRetrival
   */
  public static synchronized DBPasswordRetrival getInstance() {
    if (dbPwdRetrival == null) {
      synchronized (DBPasswordRetrival.class) {
        dbPwdRetrival = new DBPasswordRetrival();
      }
    }
    return dbPwdRetrival;
  }

  /**
   * method to get server API alias
   * 
   * @param server userID
   * @return serverName
   * @throws ConnectionException 
   */
  public String getAPIAlias(String server) throws ConnectionException {
    String password = null;
    try {
      PasswordService pwdService = new PasswordService();
      password = pwdService.getPassword(server);
    }
    catch (PasswordNotFoundException p) {
      
      logger.error("Error in webservice password retrival,Error:" + p.getMessage());
      throw new ConnectionException(p);
    
    }
    catch (Exception e) {
      
      logger.error("Error in webservice password retrival,Error:" + e.getMessage());
      throw new ConnectionException(e);
    
    }

    return password;
  }

}
