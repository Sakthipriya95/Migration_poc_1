/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.exception.MailException;


/**
 * Sends mail using SMTP <br>
 * Usage:<br>
 * Call the constructor with required params (receipients, sender, subject and content) <br>
 * Call .send() method to send the mail <br>
 */
public class MailSender {

  /**
   * CONSTANT to set as header value when mail is to be sent with high priority
   */
  private static final String HIGH_PRIORITY = "high";

  /**
   * Proxy address TODO: move to properties file
   */
  private static final String HOST_URL = "rb-smtp-int.bosch.com";

  /**
   * Port number
   */
  private static final String PORT_NO = "25";

  /**
   * Mutiple Recipients email address
   */
  private final Set<String> recipAddr;
  /**
   * Sender email adderss
   */
  private final String senderAddr;

  /**
   * @return the senderAddr
   */
  public String getSenderAddr() {
    return this.senderAddr;
  }


  /**
   * Subject line of mail
   */
  private String subject;
  /**
   * Content of mail
   */
  private String content;

  /**
   * Logger
   */
  private final ILoggerAdapter mailLoggr;
  /**
   * CC User email address
   */
  private String ccAddr;

  /**
   * Constructor for MULTIPLE receipients
   *
   * @param loggr Logger
   * @param recipAddr to addresses
   * @param senderAddr from address
   */
  public MailSender(final ILoggerAdapter loggr, final Set<String> recipAddr, final String senderAddr) {
    this.mailLoggr = loggr;
    this.recipAddr = recipAddr == null ? null : new TreeSet<>(recipAddr);
    this.senderAddr = senderAddr;
  }

  /**
   * Constructor for SINGLE receipient
   *
   * @param loggr Logger
   * @param recipAddr to address
   * @param senderAddr from address
   */
  public MailSender(final ILoggerAdapter loggr, final String recipAddr, final String senderAddr) {
    this.mailLoggr = loggr;
    this.recipAddr = new TreeSet<String>();
    this.recipAddr.add(recipAddr);
    this.senderAddr = senderAddr;
  }

  /**
   * @param subject the subject to set
   */
  public void setSubject(final String subject) {
    this.subject = subject;
  }


  /**
   * @param content the content to set
   */
  public void setContent(final String content) {
    this.content = content;
  }

  /**
   * Call this method to send the mail using SMTP
   *
   * @param highPriority flag to set mail priority header
   * @throws MailException mail exception
   */
  public void send(final boolean highPriority) throws MailException {

    if (isLoggerDebugEnabled()) {
      this.mailLoggr.debug("MailSender : Sending email TO - " + this.recipAddr + " From : " + this.senderAddr +
          ", Subject : " + this.subject);
    }

    // Get system properties
    Properties properties = System.getProperties();

    // Setup mail server
    properties.setProperty("mail.smtp.host", HOST_URL);
    properties.setProperty("mail.smtp.port", PORT_NO);

    // Get the default Session object.
    Session session = Session.getDefaultInstance(properties);

    try {
      // Create a default MimeMessage object.
      MimeMessage message = new MimeMessage(session);

      // Set From: header field of the header.
      message.setFrom(new InternetAddress(this.senderAddr));

      for (String toAddr : this.recipAddr) {
        // Set To: header field of the header.
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
      }

      // Set CC: header field
      if (CommonUtils.isNotNull(this.ccAddr)) {
        message.addRecipient(Message.RecipientType.CC, new InternetAddress(this.ccAddr));
      }

      // Set Subject: header field
      message.setSubject(this.subject);

      // Send the actual HTML message, as big as you like
      message.setContent(this.content, "text/html");
      // check if the mail is to be sent with high priority
      if (highPriority) {
        message.setHeader("X-Priority", "1");
        message.setHeader("x-msmail-priority", HIGH_PRIORITY);
      }
      // Send message
      Transport.send(message);
    }
    catch (MessagingException mex) {
      throw new MailException("Exception while sending mail!", mex);
    }
  }

  /**
   * @return the recipAddr
   */
  public Set<String> getRecipAddr() {
    return this.recipAddr;
  }

  /**
   * @return true if debug mode is enabled in logger
   */
  private boolean isLoggerDebugEnabled() {
    return this.mailLoggr.getLogLevel() == ILoggerAdapter.LEVEL_DEBUG;
  }


  /**
   * @return the ccAddr
   */
  public String getCcAddr() {
    return this.ccAddr;
  }


  /**
   * @param ccAddr the ccAddr to set
   */
  public void setCcAddr(final String ccAddr) {
    this.ccAddr = ccAddr;
  }
}
