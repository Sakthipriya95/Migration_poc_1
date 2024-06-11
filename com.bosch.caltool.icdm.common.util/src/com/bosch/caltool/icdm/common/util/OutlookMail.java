/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.exception.MailException;


/**
 * A Utility class used to open a new email in Microsoft Outlook
 *
 * @author jvi6cob
 */
public class OutlookMail {

  /**
   * String Builder size for VB script
   */
  private static final int SB_VBS_SIZE = 100;

  /**
   * Logger
   */
  private final ILoggerAdapter mailLoggr;
  /**
   * Flag To set priority for sending mail with high importance
   */
  private boolean highPriority;

  /**
   * Createst new Instance of this class
   *
   * @param loggr ILoggerAdapter
   */
  public OutlookMail(final ILoggerAdapter loggr) {
    this.mailLoggr = loggr;
  }

  /**
   * This method is used to compose an outlook mail with MUTIPLE receipients. Mail body format is HTML.
   *
   * @param toAddress set of destination addresses
   * @param subject subject of mail
   * @param htmlBody contents of mail in HTML format
   * @throws MailException any exception while creating email
   */
  // ICDM-1496
  /*
   * ICDM-1496 The Outlook mail is created using a VB script dynamically generated when this method is invoked. The
   * temporary script file is executed with the windows tool 'wscript' and is deleted after execution.
   */
  public void composeEmail(final Set<String> toAddress, final String subject, final String htmlBody)
      throws MailException {

    if (isLoggerDebugEnabled()) {
      this.mailLoggr.debug("Opening outlook to send mail : TO - " + toAddress + " ; Subject - " + subject);
    }

    String script = createOutlookScript(toAddress, subject, htmlBody);

    File file = createTempFile();

    if (file != null) {
      boolean writeSuccess = false;
      try (FileWriter fWriter = new FileWriter(file);) {
        // Write the vb script to a temporary file

        fWriter.write(script);
        writeSuccess = true;
      }
      catch (IOException exp) {
        throw new MailException(exp.getMessage(), exp);
      }
      finally {
        if (!writeSuccess) {
          // Delete the script file after execution
          file.delete();
        }
      }

      // Execute the vb script file using Windows 'wscript' tool
      Process process;
      try {
        process = Runtime.getRuntime().exec("wscript " + file.getPath());
        process.waitFor();
      }
      catch (IOException | InterruptedException exp) {
        throw new MailException(exp.getMessage(), exp);
      }
      finally {
        // Delete the script file after execution
        file.delete();
      }
    }

    this.mailLoggr.debug("New outlook mail created");

  }

  /**
   * @return
   * @throws MailException
   * @throws IOException
   */
  private File createTempFile() throws MailException {
    File file = null;
    try {
      file = File.createTempFile("outlookmailicdm", ".vbs");
    }
    catch (IOException exp) {
      throw new MailException(exp.getMessage(), exp);
    }

    return file;
  }

  /**
   * Create the VB script to be run for opening new mail in outlook
   *
   * @param toAddress set of destination addresses
   * @param subject subject of mail
   * @param htmlBody contents of mail in HTML format
   * @return script
   */
  private String createOutlookScript(final Set<String> toAddress, final String subject, final String htmlBody) {

    StringBuilder script = new StringBuilder(SB_VBS_SIZE);

    // Create Outlook Object
    script.append("                   On Error Resume Next:                                                     ")
        .append("                     ' Create email object                                                     ")
        .append("\n                   Set oolApp = CreateObject(\"Outlook.Application\")                        ")
        .append("\n                   Set email = oolApp.CreateItem(0)                                          ");

    // Error handling if outlook is not configured
    script.append("                   ' Error Handling                                                          ")
        .append("\n                   If Err.Number <> 0 Then                                                   ")
        .append(
            "\n                      a = MsgBox(\"Outlook is not configured. Please complete the configuration setup to send a mail.\",vbApplicationModal + vbOKOnly + vbExclamation,\"Outlook Configuration\")")
        .append("\n                      Wscript.Quit(0)                                                        ")
        .append("\n                   End If                                                                    ");
    // check whether mail is to be sent with high importance
    if (isHighPriority()) {
      script.append("\n                   email.Importance=2                                          ");
    }
    // Add recepients to the mail object
    for (String recipient : toAddress) {
      if (!CommonUtils.isEmptyString(recipient)) {
        script.append("\n             email.Recipients.Add(\"").append(recipient).append("\")                   ");
      }
    }

    // Mail Body, subject
    script.append("\n                                                                                           ")
        .append("\n                   ' Create the body of the email                                            ");

    if (!htmlBody.startsWith("<!DOCTYPE")) {
      script.append("\n               MailBody = \"<!DOCTYPE HTML PUBLIC \"\"-//W3C//DTD W3 HTML//EN\"\">\"     ");
    }

    script.append("\n                 MailBody = MailBody & \"").append(escapeVbsChars(htmlBody)).append("\"    ")
        .append("\n                                                                                             ")
        .append("\n                   ' Send the Email                                                          ")
        .append("\n                   email.Subject = \"").append(escapeVbsChars(subject)).append("\"           ")
        .append("\n                   email.HTMLBody = MailBody                                                 ")
        .append("\n                   email.Display                                                             ")
        .append("\n                   Set email = Nothing                                                       ")
        .append("\n                   Set oolApp = Nothing                                                      ")
        .append("\n                   wscript.Quit(0)                                                           ")
        .append("\n                                                                                             ");

    if (isLoggerDebugEnabled()) {
      this.mailLoggr.debug("Mail creation VB script\n" + script.toString());
    }

    return script.toString();
  }

  /**
   * Compose Outlook mail with Single receipient and subject and content. Mail body format is HTML.
   *
   * @param toAddress single to address
   * @param subject subject of mail
   * @param contents content of mail
   * @throws MailException any exception while sending email
   */
  public void composeEmail(final String toAddress, final String subject, final String contents) throws MailException {
    Set<String> toAddrList = new TreeSet<String>();
    toAddrList.add(toAddress);
    composeEmail(toAddrList, subject, contents);
  }

  /**
   * Escape VB script characters
   *
   * @param input input string
   * @return VB script supported string
   */
  private String escapeVbsChars(final String input) {
    String output = input;

    output = output.replace("\"", "\"\"");
    output = output.replace("\n", " ");
    output = output.replace("\r", " ");

    return output;
  }

  /**
   * @return true if debug mode is enabled in logger
   */
  private boolean isLoggerDebugEnabled() {
    return this.mailLoggr.getLogLevel() == ILoggerAdapter.LEVEL_DEBUG;

  }


  /**
   * @return the highPriority
   */
  public final boolean isHighPriority() {
    return this.highPriority;
  }


  /**
   * @param highPriority the highPriority to set
   */
  public final void setHighPriority(final boolean highPriority) {
    this.highPriority = highPriority;
  }

}