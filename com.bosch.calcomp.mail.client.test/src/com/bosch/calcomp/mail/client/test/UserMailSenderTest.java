/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.mail.client.test;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.calcomp.mail.client.BodyType;
import com.bosch.calcomp.mail.client.UserMailSender;
import com.bosch.calcomp.mail.client.exception.MailServiceException;
import com.bosch.calcomp.mail.client.util.MailUtil;

/**
 * @author TUD1COB
 */
public class UserMailSenderTest extends JUnitTest {

  /**
   *
   */

  private static final String TESTMAIL_ID1 = "Sarvatheertham.Muthupandi@in.bosch.com";
  private static final String TESTMAIL_ID2 = "Bebith.Nelson@in.bosch.com";
  private static final String TESTMAIL_ID3 = "balasubramaniam.deepashrangan@in.bosch.com";
  private static final String INVALID_MAILID1 = "invalid.invalid-bosch.com";
  private static final String SYSTEM_NAME = "TEST";
  private static final String USERNAME_KEY = SYSTEM_NAME + ".Mail.SenderNTId";
  private static final String FROMADDRESS_KEY = SYSTEM_NAME + ".Mail.SenderAddress";
  private static final String USERNAME = MailUtil.getConfig(USERNAME_KEY);
  private static final String FROMADDRESS = MailUtil.getConfig(FROMADDRESS_KEY);
  private static final String BODY = "<html>Hello Team, this is a Test mail</html>";
  private static final String BODY_TEXT = "Hello Team, this is a Test mail";
  private static final String SUBJECT = "Test." + "UserMailSenderTest.";
  private static final String ATTACHMENT = "././file/TestFile.txt";
  private static final String INVALID_ATTACHMENT = "././file/invalid.txt";
  /**
   * Rule to except exception
   */
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  /**
   * Testcase to send mail with all fields
   */
  @Test
  public void testSend() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    mailSender.setCcAddr(ccAddr);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    mailSender.setBccAddr(bccAddr);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    mailSender.setReplyToAddr(replyToAddr);
    mailSender.setFilePath(ATTACHMENT);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with all fields and High Priority
   */
  @Test
  public void testSendwithHighPriority() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    mailSender.setCcAddr(ccAddr);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    mailSender.setBccAddr(bccAddr);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    mailSender.setReplyToAddr(replyToAddr);
    mailSender.setFilePath(ATTACHMENT);
    mailSender.setHighPriority(true);
    assertNotNull(mailSender);
    mailSender.send();
  }


  /**
   * Testcase to send mail with multiple sender
   */
  @Test
  public void testSendwithMultipleSender() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    senderAddr.add(TESTMAIL_ID2);
    senderAddr.add(TESTMAIL_ID3);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    assertNotNull(mailSender);
    mailSender.send();
  }


  /**
   * Testcase to send mail with cc
   */
  @Test
  public void testSendwithCC() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    mailSender.setCcAddr(ccAddr);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with multiple cc
   */
  @Test
  public void testSendwithMultipleCC() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    ccAddr.add(TESTMAIL_ID2);
    mailSender.setCcAddr(ccAddr);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with bcc
   */
  @Test
  public void testSendwithBCC() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    mailSender.setBccAddr(bccAddr);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with multiple bcc
   */
  @Test
  public void testSendwithMultipleBCC() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    bccAddr.add(TESTMAIL_ID2);
    mailSender.setBccAddr(bccAddr);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with replyto address
   */
  @Test
  public void testSendwithReplyTo() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    mailSender.setReplyToAddr(replyToAddr);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with multiple replyTo address
   */
  @Test
  public void testSendwithMultipleReplyTo() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    replyToAddr.add(TESTMAIL_ID3);
    mailSender.setReplyToAddr(replyToAddr);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with bodytype as HTML
   */
  @Test
  public void testSendWithBodyTypeAsHTML() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with bodytype as text
   */
  @Test
  public void testSendWithBodyTypeAsTEXT() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY_TEXT);
    mailSender.setBodyType(BodyType.PLAIN_TEXT);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail without bodytype- Body:HTML format
   */
  @Test
  public void testSendWithoutBodyTypeHTMLBody() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail without bodytype- Body:Text
   */
  @Test
  public void testSendWithoutBodyTypeTEXTBody() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY_TEXT);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail without file
   */
  @Test
  public void testSendWithoutFile() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY_TEXT);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with file
   */
  @Test
  public void testSendWithFile() {
    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    mailSender.setFilePath(ATTACHMENT);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail without sender address
   */
  @Test
  public void testsendMailwithoutSenderAddress() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("to address is mandatory");

    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    mailSender.send();
  }

  /**
   * Test sending mail without subject
   */
  @Test
  public void testSendMailWithoutSubject() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("subject is mandatory");

    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    assertNotNull(mailSender);
    mailSender.send();
  }


  /**
   * Testcase to send mail with without body
   */
  @Test
  public void testSendMailWithoutBody() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("Body Unavailable");

    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with Invalid username
   */
  @Test
  public void testSendInvalidUsername() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("Invalid Username/Password");

    UserMailSender mailSender = new UserMailSender("invalid", new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    mailSender.setCcAddr(ccAddr);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    mailSender.setBccAddr(bccAddr);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    mailSender.setReplyToAddr(replyToAddr);
    mailSender.setFilePath(ATTACHMENT);
    mailSender.send();

  }


  /**
   * Testcase to send mail with invalid password
   */
  @Test
  public void testSendInvalidPassword() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("Invalid Username/Password");

    UserMailSender mailSender = new UserMailSender(USERNAME, "IddFS56opKCpbyRWl9q5Kg==");
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    mailSender.setCcAddr(ccAddr);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    mailSender.setBccAddr(bccAddr);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    mailSender.setReplyToAddr(replyToAddr);
    mailSender.setFilePath(ATTACHMENT);
    assertNotNull(mailSender);
    mailSender.send();
  }


  /**
   * Testcase to send mail with Invalid file path
   */
  @Test
  public void testInvalidFilepath() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("File to attach is not available in the given path");

    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    mailSender.setFilePath(INVALID_ATTACHMENT);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with invalid from address
   */
  @Test
  public void testInvalidFromAddress() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("Invalid from address");

    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(INVALID_MAILID1);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    assertNotNull(mailSender);
    mailSender.send();
  }


  /**
   * Testcase to send mail with invalid sender address
   */
  @Test
  public void testInvalidSenderAddress() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("Sender mail address is Invalid");

    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(INVALID_MAILID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    mailSender.setFilePath(ATTACHMENT);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with invalid cc address
   */
  @Test
  public void testInvalidCCAddress() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("CC mail address '" + INVALID_MAILID1 + "' is not valid");

    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(INVALID_MAILID1);
    mailSender.setCcAddr(ccAddr);
    assertNotNull(mailSender);
    mailSender.send();
  }

  /**
   * Testcase to send mail with invalid bcc address
   */
  @Test
  public void testInvalidBCCAddress() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("BCC mail address '" + INVALID_MAILID1 + "' is Invalid");

    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(INVALID_MAILID1);
    mailSender.setBccAddr(bccAddr);
    assertNotNull(mailSender);
    mailSender.send();
  }


  /**
   * Testcase to send mail with invalid to address
   */
  @Test
  public void testInvalidReplyToAddress() {
    this.exceptionRule.expect(MailServiceException.class);
    this.exceptionRule.expectMessage("ReplyTo mail address '" + INVALID_MAILID1 + "' is Invalid");

    UserMailSender mailSender = new UserMailSender(USERNAME, new PasswordProvider().getPassword());
    mailSender.setSenderAddr(FROMADDRESS);
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    mailSender.setRecipAddr(senderAddr);
    mailSender.setBody(BODY);
    mailSender.setBodyType(BodyType.HTML);
    mailSender.setSubject(
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName());
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(INVALID_MAILID1);
    mailSender.setReplyToAddr(replyToAddr);
    assertNotNull(mailSender);
    mailSender.send();
  }


}
