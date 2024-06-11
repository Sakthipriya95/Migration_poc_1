/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;


/**
 * @author bne4cob
 */
public class A2lResponsibilityCommonTest extends JUnitTest {

  /**
   * Test method for {@link A2lResponsibilityCommon#isDefaultResponsibility(A2lResponsibility)}.
   */
  @Test
  public void testIsDefaultResponsibility() {
    A2lResponsibility resp;

    // Resp Type = BOSCH
    resp = createObj("R", null, null, null, null, null);
    assertTrue("Resp Type R IS default", A2lResponsibilityCommon.isDefaultResponsibility(resp));
    resp = createObj("R", 200L, null, null, null, null);
    assertFalse("Resp Type R Not default", A2lResponsibilityCommon.isDefaultResponsibility(resp));

    // Resp Type = CUSTOMER
    resp = createObj("C", null, null, null, null, null);
    assertTrue("Resp Type C IS default", A2lResponsibilityCommon.isDefaultResponsibility(resp));
    resp = createObj("C", null, null, "LName", "DName", "Alias");
    assertFalse("Resp Type C Not default", A2lResponsibilityCommon.isDefaultResponsibility(resp));
    resp = createObj("C", null, null, null, "DName", "Alias");
    assertFalse("Resp Type C Not default", A2lResponsibilityCommon.isDefaultResponsibility(resp));
    resp = createObj("C", null, null, null, null, "AliasName");
    assertFalse("Resp Type C Not default", A2lResponsibilityCommon.isDefaultResponsibility(resp));
    resp = createObj("C", null, "FName", "LName", "DName", "Alias");
    assertFalse("Resp Type C Not default", A2lResponsibilityCommon.isDefaultResponsibility(resp));

    // Resp Type = OTHERS
    resp = createObj("O", null, null, null, null, null);
    assertTrue("Resp Type O IS default", A2lResponsibilityCommon.isDefaultResponsibility(resp));
    resp = createObj("O", null, null, null, null, "AliasName");
    assertFalse("Resp Type O Not default", A2lResponsibilityCommon.isDefaultResponsibility(resp));
    resp = createObj("O", null, "FName", "LName", "DName", "Alias");
    assertFalse("Resp Type O Not default", A2lResponsibilityCommon.isDefaultResponsibility(resp));
  }

  /**
   * Test method for {@link A2lResponsibilityCommon#getRespType(A2lResponsibility) }.
   */
  @Test
  public void testGetRespType() {
    A2lResponsibility resp;
    WpRespType respType;

    // Resp Type = BOSCH
    resp = createObj("R", 200L, null, null, null, null);
    respType = A2lResponsibilityCommon.getRespType(resp);
    assertEquals("Resp Type Check R", WpRespType.RB, respType);

    // Resp Type = CUSTOMER
    resp = createObj("C", null, "FName", "LName", "DName", "Alias");
    respType = A2lResponsibilityCommon.getRespType(resp);
    assertEquals("Resp Type Check C", WpRespType.CUSTOMER, respType);

    // Resp Type = OTHERS
    resp = createObj("O", null, "FName", "LName", "DName", "Alias");
    respType = A2lResponsibilityCommon.getRespType(resp);
    assertEquals("Resp Type Check O", WpRespType.OTHERS, respType);

    // input is null
    respType = A2lResponsibilityCommon.getRespType(null);
    assertNull("Resp Type is null", respType);
  }

  /**
   * Test method for {@link A2lResponsibilityCommon#isSame(A2lResponsibility, A2lResponsibility) }.
   */
  @Test
  public void testIsSameCommon() {
    A2lResponsibility resp1;
    A2lResponsibility resp2;

    // Same object
    resp1 = createObj(1L, "R", 200L, null, null, null, null);
    assertTrue("same object check", A2lResponsibilityCommon.isSame(resp1, resp1));

    // Same content
    resp1 = createObj(1L, "R", 200L, null, null, null, null);
    resp2 = createObj(1L, "R", 200L, null, null, null, null);
    assertTrue("same contents chk", A2lResponsibilityCommon.isSame(resp1, resp2));

    // Diff type
    resp1 = createObj(1L, "R", 200L, null, null, null, null);
    resp2 = createObj(null, "C", null, "Bebith", "Nelson", "RBEI/ETB5", "Nelson__Bebith");
    assertFalse("diff type chk", A2lResponsibilityCommon.isSame(resp1, resp2));

  }

  /**
   * Test method for {@link A2lResponsibilityCommon#isSame(A2lResponsibility, A2lResponsibility) }.
   */
  @Test
  public void testIsSameBoschType() {
    A2lResponsibility resp1;
    A2lResponsibility resp2;

    // Same content
    resp1 = createObj(1L, "R", 200L, null, null, null, null);
    resp2 = createObj(null, "R", 200L, null, null, null, null);
    assertTrue("same contents chk", A2lResponsibilityCommon.isSame(resp1, resp2));

    // Same content default
    resp1 = createObj(1L, "R", null, null, null, null, null);
    resp2 = createObj(null, "R", null, null, null, null, null);
    assertTrue("same contents chk (default resp)", A2lResponsibilityCommon.isSame(resp1, resp2));

    // Same content alias
    resp1 = createObj(1L, "R", 100L, null, null, null, "al1");
    resp2 = createObj(null, "R", null, null, null, null, "Al1");
    assertTrue("same contents chk (same alias)", A2lResponsibilityCommon.isSame(resp1, resp2));

    // Diff resps
    resp1 = createObj(1L, "R", 100L, null, null, null, "al1");
    resp2 = createObj(null, "R", 200L, null, null, null, null);
    assertFalse("Diff resps chk", A2lResponsibilityCommon.isSame(resp1, resp2));

  }

  /**
   * Test method for {@link A2lResponsibilityCommon#isSame(A2lResponsibility, A2lResponsibility) }.
   */
  @Test
  public void testIsSameCustOthType() {
    A2lResponsibility resp1;
    A2lResponsibility resp2;

    // Same content (default resp)
    resp1 = createObj(1L, "C", null, null, null, null, null);
    resp2 = createObj(null, "C", null, null, null, null, null);
    assertTrue("same contents chk (default resp)", A2lResponsibilityCommon.isSame(resp1, resp2));

    // Same content : only first name
    resp1 = createObj(1L, "C", null, "F", null, null, null);
    resp2 = createObj(null, "C", null, "f", null, null, null);
    assertTrue("same contents chk (only first name)", A2lResponsibilityCommon.isSame(resp1, resp2));

    // Same content : only first name with last name empty in one rec
    resp1 = createObj(1L, "C", null, "F", null, null, null);
    resp2 = createObj(null, "C", null, "f", "", null, null);
    assertTrue("same contents chk (only first name with one LN empty string)",
        A2lResponsibilityCommon.isSame(resp1, resp2));

    // Same content : only last name
    resp1 = createObj(1L, "C", null, null, "L", null, null);
    resp2 = createObj(null, "C", null, null, "l", null, null);
    assertTrue("same contents chk (only last name)", A2lResponsibilityCommon.isSame(resp1, resp2));

    // Same content : only last name, with first name empty string in one rec
    resp1 = createObj(1L, "C", null, null, "L", null, null);
    resp2 = createObj(null, "C", null, "", "l", null, null);
    assertTrue("same contents chk (only last name with one FN empty string)",
        A2lResponsibilityCommon.isSame(resp1, resp2));

    // Same content : both first name and last name
    resp1 = createObj(1L, "C", null, "F", "L", null, null);
    resp2 = createObj(null, "C", null, "f", "l", null, null);
    assertTrue("same contents chk (both first name and last name)", A2lResponsibilityCommon.isSame(resp1, resp2));

    // Same content alias
    resp1 = createObj(1L, "O", null, null, null, null, "al1");
    resp2 = createObj(null, "O", null, null, null, null, "Al1");
    assertTrue("same contents chk (same alias)", A2lResponsibilityCommon.isSame(resp1, resp2));

    // Diff resps
    resp1 = createObj(1L, "C", null, null, null, null, "al1");
    resp2 = createObj(null, "C", null, null, null, null, null);
    assertFalse("Diff resps chk", A2lResponsibilityCommon.isSame(resp1, resp2));

  }


  private A2lResponsibility createObj(final String type, final Long userId, final String fName, final String lName,
      final String dept, final String alias) {

    A2lResponsibility ret = new A2lResponsibility();

    ret.setAliasName(alias);
    ret.setCreatedDate("01-01-2020");
    ret.setCreatedUser("BNE4COB");
    ret.setDeleted(false);
    ret.setDescription("Resp Desc");
    ret.setId(100L);
    ret.setLDepartment(dept);
    ret.setLFirstName(fName);
    ret.setLLastName(lName);
    ret.setModifiedDate("01-01-2020");
    ret.setModifiedUser("BNE4COB");
    ret.setName("Resp name");
    ret.setProjectId(50L);
    ret.setRespType(type);
    ret.setUserId(userId);
    ret.setVersion(3L);

    return ret;
  }

  private A2lResponsibility createObj(final Long id, final String type, final Long userId, final String fName,
      final String lName, final String dept, final String alias) {
    A2lResponsibility ret = createObj(type, userId, fName, lName, dept, alias);
    ret.setId(id);
    return ret;
  }

}
