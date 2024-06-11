/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bosch.boot.ssd.api.repository.SSDReviewRepository;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;
import com.bosch.caltool.security.Decryptor;

/**
 * @author TUD1COB
 */
@Service
public class CreateSSDUserService {

  /**
   * Key to retrieve password from password webservice
   */
  @Value("${ssd.user_pass}")
  private String ssdUserPass;


  private static final ILoggerAdapter LOGGER =
      new Log4JLoggerAdapterImpl(LogManager.getLogger(CreateSSDUserService.class));

  @Autowired
  private SSDReviewRepository repo;

  /**
   * Calls the procedure 'PR_IDM_UPDATE_USER_ACCESS'
   */
  public void doCreateUser() {
    String password;
    try {
      password = CreateSSDUserService.getPassword(this.ssdUserPass);
      LOGGER.info("Procedure 'PR_IDM_UPDATE_USER_ACCESS' triggered.");
      this.repo.createSSDUserProc(password);
      LOGGER.info("Procedure finished successfully.");
    }
    catch (PasswordNotFoundException e) {
      LOGGER.error("Key: '" + this.ssdUserPass + "' not available in Password Webservice");
    }

  }

  /**
   * @param key
   * @return Decrypted password
   */
  private static String getPassword(final String key) throws PasswordNotFoundException {
    PasswordService passwordService = new PasswordService();
    String password = passwordService.getPassword(key);
    return Decryptor.INSTANCE.decrypt(password, LOGGER);
  }
}
