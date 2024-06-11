/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.mail.client;

import java.util.Set;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.mail.client.exception.MailServiceException;
import com.bosch.calcomp.mail.client.util.MailUtil;

/**
 * @author TUD1COB
 */
public class SystemMailSender {

  private static final ILoggerAdapter LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger(SystemMailSender.class));

  private final String systemName;

  /**
   * @param systemName Name of the system. System Name should not contain space or special characters
   */
  public SystemMailSender(final String systemName) {
    this.systemName = systemName;
  }

  /**
   * @param recipAddr to address
   * @param body body of the mail
   * @param bodyType body type of the mail
   * @param subject subject of the mail
   * @param ccAddr cc address
   * @param bccAddr bcc address
   * @param replyToAddr reply to address
   * @param filePath filepath
   * @param highPriority highPriority
   */
  public void send(final Set<String> recipAddr, final String body, final BodyType bodyType, final String subject,
      final Set<String> ccAddr, final Set<String> bccAddr, final Set<String> replyToAddr, final String filePath,
      final boolean highPriority) {
    try {
      String usernameKey = this.systemName + ".Mail.SenderNTId";
      String passwordKey = this.systemName + ".Mail.SenderPassword";
      String fromAddressKey = this.systemName + ".Mail.SenderAddress";

      UserMailSender mailSender = new UserMailSender(MailUtil.getConfig(usernameKey), MailUtil.getConfig(passwordKey));
      mailSender.setSenderAddr(MailUtil.getConfig(fromAddressKey));
      mailSender.setRecipAddr(recipAddr);
      mailSender.setBody(body);
      mailSender.setBodyType(bodyType);
      mailSender.setSubject(subject);
      mailSender.setCcAddr(ccAddr);
      mailSender.setBccAddr(bccAddr);
      mailSender.setReplyToAddr(replyToAddr);
      mailSender.setFilePath(filePath);
      mailSender.setHighPriority(highPriority);
      mailSender.send();

      LOGGER.info("Mail sent Successfully");
    }
    catch (Exception e) {
      throw new MailServiceException(e.getMessage(), e);
    }

  }


  /**
   * @param recipAddr to address
   * @param body body of the mail
   * @param bodyType body type of the mail
   * @param subject subject of the mail
   */
  public void send(final Set<String> recipAddr, final String body, final BodyType bodyType, final String subject,
      final boolean highPriority) {
    send(recipAddr, body, bodyType, subject, null, null, null, null, highPriority);
  }

}
