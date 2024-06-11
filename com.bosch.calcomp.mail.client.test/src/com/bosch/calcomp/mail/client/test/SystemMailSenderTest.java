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
import com.bosch.calcomp.mail.client.SystemMailSender;
import com.bosch.calcomp.mail.client.exception.MailServiceException;

/**
 * @author TUD1COB
 */
public class SystemMailSenderTest extends JUnitTest {

  /**
   *
   */

  private static final String TESTMAIL_ID1 = "Sarvatheertham.Muthupandi@in.bosch.com";
  private static final String TESTMAIL_ID2 = "Bebith.Nelson@in.bosch.com";
  private static final String INVALID_MAILID1 = "invalid.invalid-bosch.com";
  private static final String BODY = "<html>Hello Team, this is a Test mail</html>";
  private static final String SUBJECT = "Test." + "SystemMailSenderTest.";
  private static final String ATTACHMENT = "././file/TestFile.txt";
  private static final String SYSTEM_NAME = "TEST";
  /**
   * Rule to except exception
   */
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();


  /**
   * Testcase to send mail as system user
   */
  @Test
  public void testSendAsSystem() {
    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    senderAddr.add(TESTMAIL_ID2);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    String subjectWithMethodName =
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName();
    assertNotNull(systemMailSender);
    systemMailSender.send(senderAddr, BODY, BodyType.HTML, subjectWithMethodName, ccAddr, bccAddr, replyToAddr,
        ATTACHMENT, false);
  }

  /**
   * Testcase to send mail as system user with High Priority
   */
  @Test
  public void testSendAsSystemWithHighPriority() {
    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    senderAddr.add(TESTMAIL_ID2);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    String subjectWithMethodName =
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName();
    assertNotNull(systemMailSender);
    systemMailSender.send(senderAddr, BODY, BodyType.HTML, subjectWithMethodName, ccAddr, bccAddr, replyToAddr,
        ATTACHMENT, true);
  }

  /**
   * Testcase to send mail with mandatory fields
   */
  @Test
  public void testSendAsSystemWithMandatoryFieldsonly() {
    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    String subjectWithMethodName =
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName();
    assertNotNull(systemMailSender);
    systemMailSender.send(senderAddr, BODY, BodyType.HTML, subjectWithMethodName, false);
  }

  /**
   * Testcase to send mail as system user with cc only
   */
  @Test
  public void testSendAsSystemWithCc() {
    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    senderAddr.add(TESTMAIL_ID2);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    String subjectWithMethodName =
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName();
    assertNotNull(systemMailSender);
    systemMailSender.send(senderAddr, BODY, BodyType.HTML, subjectWithMethodName, ccAddr, null, null, null, false);
  }

  /**
   * Testcase to send mail as system user with BCC only
   */
  @Test
  public void testSendAsSystemWithBcc() {
    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    senderAddr.add(TESTMAIL_ID2);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    String subjectWithMethodName =
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName();
    assertNotNull(systemMailSender);
    systemMailSender.send(senderAddr, BODY, BodyType.HTML, subjectWithMethodName, null, bccAddr, null, null, false);
  }


  /**
   * Testcase to send mail as system user with reply to only
   */
  @Test
  public void testSendAsSystemwithReplyTo() {
    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    senderAddr.add(TESTMAIL_ID2);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    String subjectWithMethodName =
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName();
    assertNotNull(systemMailSender);
    systemMailSender.send(senderAddr, BODY, BodyType.HTML, subjectWithMethodName, null, null, replyToAddr, null, false);
  }

  /**
   * Testcase to send mail with Invalid system name
   */
  @Test
  public void testSendAsSystemWithInvalidSystem() {
    this.exceptionRule.expect(MailServiceException.class);

    SystemMailSender systemMailSender = new SystemMailSender("INVALID");
    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(INVALID_MAILID1);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    systemMailSender.send(senderAddr, BODY, BodyType.HTML, SUBJECT, false);
  }


  /**
   * Testcase to send mail as system user without sender address
   */
  @Test
  public void testSendAsSystemWithoutSenderAddress() {
    this.exceptionRule.expect(MailServiceException.class);

    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    assertNotNull(systemMailSender);
    systemMailSender.send(null, BODY, BodyType.HTML, SUBJECT, ccAddr, bccAddr, replyToAddr, ATTACHMENT, false);
  }


  /**
   * Testcase to send mail as system user without body
   */
  @Test
  public void testSendAsSystemWithoutBody() {
    this.exceptionRule.expect(MailServiceException.class);

    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    senderAddr.add(TESTMAIL_ID2);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    assertNotNull(systemMailSender);
    systemMailSender.send(senderAddr, null, BodyType.HTML, SUBJECT, ccAddr, bccAddr, replyToAddr, null, false);
  }


  /**
   * Testcase to send mail as system user without body type
   */
  @Test
  public void testSendAsSystemWithoutBodyType() {

    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    senderAddr.add(TESTMAIL_ID2);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    String subjectWithMethodName =
        SUBJECT + new Object() {/* to create an anonymous inner class */}.getClass().getEnclosingMethod().getName();
    assertNotNull(systemMailSender);
    systemMailSender.send(senderAddr, BODY, null, subjectWithMethodName, ccAddr, bccAddr, replyToAddr, ATTACHMENT,
        false);
  }


  /**
   * Testcase to send mail as system user
   */
  @Test
  public void testSendAsSystemWithoutSubject() {
    this.exceptionRule.expect(MailServiceException.class);

    SystemMailSender systemMailSender = new SystemMailSender(SYSTEM_NAME);

    Set<String> senderAddr = new HashSet<>();
    senderAddr.add(TESTMAIL_ID1);
    senderAddr.add(TESTMAIL_ID2);
    Set<String> bccAddr = new HashSet<>();
    bccAddr.add(TESTMAIL_ID1);
    Set<String> ccAddr = new HashSet<>();
    ccAddr.add(TESTMAIL_ID1);
    Set<String> replyToAddr = new HashSet<>();
    replyToAddr.add(TESTMAIL_ID2);
    assertNotNull(systemMailSender);
    systemMailSender.send(senderAddr, BODY, BodyType.HTML, null, ccAddr, bccAddr, replyToAddr, ATTACHMENT, false);
  }
}
