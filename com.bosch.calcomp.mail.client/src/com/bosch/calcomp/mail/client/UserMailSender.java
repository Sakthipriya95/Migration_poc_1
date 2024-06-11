
/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

/**
 * @author TUD1COB
 */

package com.bosch.calcomp.mail.client;
/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */


import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.mail.client.exception.MailServiceException;
import com.bosch.calcomp.mail.client.util.CommonUtils;
import com.bosch.calcomp.mail.client.util.MailUtil;

/**
 * Sends mail using SMTP <br>
 * Usage:<br>
 * Call the constructor with required params (receipients, sender, subject and content) <br>
 * Call .send() method to send the mail <br>
 */
public class UserMailSender {

  private static final ILoggerAdapter LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger(UserMailSender.class));

  private static final String EMAIL_REGEX =
      "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

  /**
   * CONSTANT to set as header value when mail is to be sent with high priority
   */
  private static final String HIGH_PRIORITY = "high";

  /**
   * Proxy address
   */
  private final String hostUrl;

  /**
   * Port number
   */
  private final String portUrl;

  /**
   * tls encryption
   */
  private final String isTLS;

  /**
   * Mutiple Recipients email address
   */
  private Set<String> recipAddr = new HashSet<>();
  /**
   * Sender email adderss
   */
  private String senderAddr;
  /**
   * Subject of mail
   */
  private String subject;
  /**
   * body of mail
   */
  private String body;

  private BodyType bodyType;

  private boolean highPriority;

  /**
   * CC User email address
   */
  private Set<String> ccAddr = new HashSet<>();

  /**
   * bcc user email address
   */
  private Set<String> bccAddr = new HashSet<>();

  /**
   * replyTo user email address
   */
  private Set<String> replyToAddr = new HashSet<>();

  /**
   * Username
   */
  private String username;

  /**
   * Password
   */
  private String password;

  private String filePath;


  /**
   * @param username username to authenticate
   * @param password password to authenticate
   */
  public UserMailSender(final String username, final String password) {
    this();
    this.username = username;
    this.password = password;
  }

  /**
   * @param recipAddr Mail Address of receiver
   * @param senderAddr Mail Address of sender
   * @param username username to authenticate
   * @param password password to authenticate
   */
  public UserMailSender(final Set<String> recipAddr, final String senderAddr, final String username,
      final String password) {

    this(username, password);
    if (recipAddr != null) {
      this.recipAddr.addAll(recipAddr);
    }
    this.senderAddr = senderAddr;
  }


  /**
   * Constructor for SINGLE receipient
   *
   * @param recipAddr to address
   * @param senderAddr from address
   * @param username username to authenticate
   * @param password password to authenticate
   */
  public UserMailSender(final String recipAddr, final String senderAddr, final String username, final String password) {

    this(username, password);
    if (recipAddr != null) {
      this.recipAddr.add(recipAddr);
    }

    this.senderAddr = senderAddr;
  }

  /**
  *
  */
  private UserMailSender() {
    this.hostUrl = MailUtil.getConfig("MailConfig.HOST_URL");
    this.portUrl = MailUtil.getConfig("MailConfig.PORT_NO");
    this.isTLS = MailUtil.getConfig("MailConfig.TLS_ENABLED").equals("Y") ? "true" : "false";
  }

  /**
   * send the mail using SMTP
   *
   * @throws MailServiceException mail exception //explain
   */

  public void send() throws MailServiceException {
    // Validate if from,to,subject is available
    validateMail();

    // Get system properties
    Properties properties = System.getProperties();

    // Setup mail server
    properties.setProperty("mail.smtp.host", this.hostUrl);
    properties.setProperty("mail.smtp.port", this.portUrl);
    properties.put("mail.smtp.starttls.enable", this.isTLS);
    properties.setProperty("mail.smtp.auth", "true");


    // Get the Session object.// and pass username and password
    Authenticator authenticator = new javax.mail.Authenticator() {

      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(getUsername(), MailUtil.getDecryptedPassword(getPassword()));
      }

    };
    Session session = Session.getInstance(properties, authenticator);
    try {

      // Create a default MimeMessage object.
      MimeMessage message = new MimeMessage(session);

      // Set From
      message.setFrom(new InternetAddress(this.senderAddr));

      // Set To
      for (String toAddr : this.recipAddr) {
        if (!isValidEmail(toAddr)) {
          throw new MailServiceException("Sender mail address is Invalid");
        }
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
      }

      // Set CC
      if (CommonUtils.isSetNotEmpty(this.ccAddr)) {
        for (String ccAddress : this.ccAddr) {
          if (!isValidEmail(ccAddress)) {
            throw new MailServiceException("CC mail address is Invalid");
          }
          message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccAddress));
        }
      }

      // Set Bcc
      if (CommonUtils.isSetNotEmpty(this.bccAddr)) {
        for (String bccAddress : this.bccAddr) {
          if (!isValidEmail(bccAddress)) {
            throw new MailServiceException("BCC mail address is Invalid");
          }
          message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccAddress));
        }
      }

      // Set ReplyTo
      if (CommonUtils.isSetNotEmpty(this.replyToAddr)) {
        for (String replyToAddress : this.replyToAddr) {
          if (!isValidEmail(replyToAddress)) {
            throw new MailServiceException("ReplyTo mail address is Invalid");
          }
          message.setReplyTo(InternetAddress.parse(replyToAddress));
        }
      }

      // Set Subject
      if (CommonUtils.isNotEmpty(getSubject())) {
        message.setSubject(getSubject());
      }

      BodyType bodyContentType = getBodyType() == null ? BodyType.HTML : getBodyType();

      if (CommonUtils.isNotEmpty(getFilePath())) {
        // Files with attachments

        MimeMultipart multipart = new MimeMultipart();

        // Add the text part of the email
        MimeBodyPart textPart = new MimeBodyPart();
        String contentValue = getBody() != null ? getBody() : "";
        textPart.setContent(contentValue, bodyContentType.getType());
        multipart.addBodyPart(textPart);

        // check file is present in the given file path
        File file = new File(getFilePath());
        if (!file.exists()) {
          throw new MailServiceException("File to attach is not available in the given path");
        }

        // Add the file attachment
        MimeBodyPart filePart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);
        filePart.setDataHandler(new DataHandler(source));
        filePart.setFileName(source.getName());
        multipart.addBodyPart(filePart);

        // Add the multipart message to the email message
        message.setContent(multipart);
      }
      else {
        if (!CommonUtils.isNotEmpty(getBody())) {
          throw new MailServiceException("Body Unavailable");
        }
        message.setContent(this.body, bodyContentType.getType());
      }

      if (this.highPriority) {
        message.setHeader("X-Priority", "1");
        message.setHeader("x-msmail-priority", HIGH_PRIORITY);
      }

      // Send message
      Transport.send(message);

      LOGGER.info("Mail Sent Successfully...");

    }
    catch (AuthenticationFailedException afe) {
      throw new MailServiceException("Invalid Username/Password", afe);
    }
    catch (Exception mex) {
      throw new MailServiceException(mex.getMessage(), mex);
    }
  }

  /**
   * Validate if from,to,subject is available
   */
  private void validateMail() {

    // Validate sender
    if (CommonUtils.isEmpty(this.senderAddr)) {
      throw new MailServiceException("from address is mandatory");
    }
    if (!isValidEmail(this.senderAddr)) {
      throw new MailServiceException("Invalid from address");
    }

    // Validate To
    if (CommonUtils.isEmpty(this.recipAddr)) {
      throw new MailServiceException("to address is mandatory");
    }
    for (String toAddr : this.recipAddr) {
      if (!isValidEmail(toAddr)) {
        throw new MailServiceException("Sender mail address is Invalid");
      }
    }

    // Validate CC
    if (CommonUtils.isSetNotEmpty(this.ccAddr)) {
      for (String ccAddress : this.ccAddr) {
        if (!isValidEmail(ccAddress)) {
          throw new MailServiceException("CC mail address '" + ccAddress + "' is not valid");
        }
      }
    }

    // Validate Bcc
    if (CommonUtils.isSetNotEmpty(this.bccAddr)) {
      for (String bccAddress : this.bccAddr) {
        if (!isValidEmail(bccAddress)) {
          throw new MailServiceException("BCC mail address '" + bccAddress + "' is Invalid");
        }
      }
    }

    // Validate ReplyTo
    if (CommonUtils.isSetNotEmpty(this.replyToAddr)) {
      for (String replyToAddress : this.replyToAddr) {
        if (!isValidEmail(replyToAddress)) {
          throw new MailServiceException("ReplyTo mail address '" + replyToAddress + "' is Invalid");
        }
      }
    }

    // Validate subject
    if (CommonUtils.isEmpty(getSubject())) {
      throw new MailServiceException("subject is mandatory");
    }
  }

  /**
   * @param email to be validated
   * @return true if email is valid
   */
  private static boolean isValidEmail(final String email) {
    Matcher matcher = EMAIL_PATTERN.matcher(email);
    return matcher.matches();
  }

  /**
   * @param recipAddr to send mail
   */
  public void setRecipAddr(final Set<String> recipAddr) {
    this.recipAddr = recipAddr;
  }


  /**
   * @param senderAddr the senderAddr to set
   */
  public void setSenderAddr(final String senderAddr) {
    this.senderAddr = senderAddr;
  }


  /**
   * @return the username
   */
  private String getUsername() {
    return this.username;
  }


  /**
   * @param username the username to set
   */
  public void setUsername(final String username) {
    this.username = username;
  }


  /**
   * @return the password
   */
  private String getPassword() {
    return this.password;
  }


  /**
   * @param password the password to set
   */
  public void setPassword(final String password) {
    this.password = password;
  }


  /**
   * @param subject the subject to set
   */
  public void setSubject(final String subject) {
    this.subject = subject;
  }

  /**
   * @return the subject
   */
  public String getSubject() {
    return this.subject;
  }


  /**
   * @return the content
   */
  public String getBody() {
    return this.body;
  }


  /**
   * @param content the content to set
   */
  public void setBody(final String content) {
    this.body = content;
  }

  /**
   * @return the contentType
   */
  public BodyType getBodyType() {
    return this.bodyType;
  }


  /**
   * @param contentType the contentType to set
   */
  public void setBodyType(final BodyType contentType) {
    this.bodyType = contentType;
  }


  /**
   * @return the filePath
   */
  public String getFilePath() {
    return this.filePath;
  }


  /**
   * @param filePath the filePath to set
   */
  public void setFilePath(final String filePath) {
    this.filePath = filePath;
  }


  /**
   * @param ccAddr the ccAddr to set
   */
  public void setCcAddr(final Set<String> ccAddr) {
    this.ccAddr = ccAddr;
  }


  /**
   * @param bccAddr the bccAddr to set
   */
  public void setBccAddr(final Set<String> bccAddr) {
    this.bccAddr = bccAddr;
  }


  /**
   * @param replyToAddr the replyToAddr to set
   */
  public void setReplyToAddr(final Set<String> replyToAddr) {
    this.replyToAddr = replyToAddr;
  }


  /**
   * @return the highPriority
   */
  public boolean isHighPriority() {
    return this.highPriority;
  }


  /**
   * @param highPriority the highPriority to set
   */
  public void setHighPriority(final boolean highPriority) {
    this.highPriority = highPriority;
  }


}

