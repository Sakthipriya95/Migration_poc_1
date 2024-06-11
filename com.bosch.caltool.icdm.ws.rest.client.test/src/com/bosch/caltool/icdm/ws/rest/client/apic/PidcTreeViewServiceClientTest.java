/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.StringJoiner;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSdomA2lInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeNodeChildren;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class PidcTreeViewServiceClientTest extends AbstractRestClientTest {
  /**
   *
   */
  private static final long CUST_ATTR_ID = 36L;
  private static final long CUST_ATTR_LEVEL = 1L;

  private static final long PIDC_ID = 2747L;// X_Test_002_P866_EA288
  private static final long PIDC_VERS_ID_ACTIVE = 773510915L;// Version 4
  private static final String SDOM_PVER_NAME = "M1764VDAC866";


  /**
   * Test method for {@link PidcTreeViewServiceClient#getPidcStrucAttrMaxLevel()}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPidcStrucAttrMaxLevel() throws ApicWebServiceException {
    Long maxLevel = new PidcTreeViewServiceClient().getPidcStrucAttrMaxLevel();
    assertEquals("Max level of PIDC tree", Long.valueOf(4L), maxLevel);
  }

  /**
   * Test method for {@link PidcTreeViewServiceClient#getAllLvlAttrByAttrId()}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllLvlAttrByAttrId() throws ApicWebServiceException {
    Map<Long, Attribute> retMap = new PidcTreeViewServiceClient().getAllLvlAttrByAttrId();

    assertFalse("Return not null", (retMap == null) || retMap.isEmpty());

    assertNotNull("Level 1 attribute", retMap.get(CUST_ATTR_ID));
    assertEquals("Level 1 attribute name", "Customer/Brand", retMap.get(36L).getName());
  }

  /**
   * Test method for {@link PidcTreeViewServiceClient#getAllLvlAttrByLevel()}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllLvlAttrByLevel() throws ApicWebServiceException {
    Map<Long, Attribute> retMap = new PidcTreeViewServiceClient().getAllLvlAttrByLevel();

    assertFalse("Return not null", (retMap == null) || retMap.isEmpty());

    assertNotNull("Level 1 attribute", retMap.get(CUST_ATTR_LEVEL));
    assertEquals("Level 1 attribute name", "Customer/Brand", retMap.get(CUST_ATTR_LEVEL).getName());
  }

  /**
   * Test method for {@link PidcTreeViewServiceClient#getAllPidTreeLvlAttrValueSet()}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllPidTreeLvlAttrValueSet() throws ApicWebServiceException {
    Map<Long, Map<Long, AttributeValue>> retMap = new PidcTreeViewServiceClient().getAllPidTreeLvlAttrValueSet();
    assertFalse("Return not null", (retMap == null) || retMap.isEmpty());

    Map<Long, AttributeValue> custAttrValSet = retMap.get(CUST_ATTR_ID);
    assertFalse("Customer values not null", (custAttrValSet == null) || custAttrValSet.isEmpty());
    assertNotNull("Suzuki attribute value present",
        custAttrValSet.values().stream().filter(v -> "Suzuki".equals(v.getName())).findAny().orElse(null));

  }


  /**
   * Test method for {@link PidcTreeViewServiceClient#getPidcNodeChildAvailblty(java.lang.Long, java.lang.Long )}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPidcNodeChildAvailblty() throws ApicWebServiceException {
    PidcTreeNodeChildren ret = new PidcTreeViewServiceClient().getPidcNodeChildAvailblty(PIDC_ID, PIDC_VERS_ID_ACTIVE);

    assertNotNull("Response not null", ret);

    logPidcTreeNodeChildren(ret);

    assertTrue("Other pidc versions present", ret.isOtherPidcVerPresent());
    assertTrue("SDOM PVERs present", ret.isSdomPversPresent());
    assertTrue("Is CDR present", ret.isCdrPresent());
    assertTrue("Review Questionnaires present", ret.isQuestionnairesPresent());

    assertFalse("Set of SDOM pvers not empty", ret.getPidcSdomPverSet().isEmpty());
    assertFalse("Set of variants is not empty", ret.getPidcVariants().isEmpty());

  }


  /**
   * Test method for {@link PidcTreeViewServiceClient#getPidcSdomPvers(java.lang.Long)}. SDOM PVERs are defined at
   * variant level
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPidcSdomPversVarLevel() throws ApicWebServiceException {
    Map<String, PidcSdomA2lInfo> retMap = new PidcTreeViewServiceClient().getPidcSdomPvers(PIDC_VERS_ID_ACTIVE);

    assertTrue("SDOM PVER Info available", CommonUtils.isNotEmpty(retMap));
    LOG.info("SDOM PVER Count = {}", retMap.size());

    PidcSdomA2lInfo sdomInfo = retMap.get(SDOM_PVER_NAME);
    assertNotNull("SDOM Info for SDOM PVER " + SDOM_PVER_NAME + " available", sdomInfo);

    assertEquals("Check SDOM PVer name inside object", SDOM_PVER_NAME, sdomInfo.getSdomPver().getPverName());

    Map<Long, PidcA2l> a2lMap = sdomInfo.getA2lMap();
    LOG.info("A2L files size = {}", a2lMap.size());
    assertFalse("A2L files available", a2lMap.isEmpty());
  }

  /**
   * Test method for {@link PidcTreeViewServiceClient#getPidcSdomPvers(java.lang.Long)}. SDOM PVER is defined at project
   * level
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetPidcSdomPversVersLevel() throws ApicWebServiceException {

    // PIDC Version: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version 1)
    // URL: icdm:pidvid,773519265
    Map<String, PidcSdomA2lInfo> retMap = new PidcTreeViewServiceClient().getPidcSdomPvers(773519265L);

    assertTrue("SDOM PVER Info available", CommonUtils.isNotEmpty(retMap));
    LOG.info("SDOM PVER Count = {}", retMap.size());

    assertEquals("One SDOM PVER is available", 1, retMap.size());

    // SDOM PVER to check
    String sdomPverExp = "D173307";

    PidcSdomA2lInfo sdomInfo = retMap.get(sdomPverExp);
    assertNotNull("SDOM Info for SDOM PVER " + sdomPverExp + " available", sdomInfo);

    assertEquals("Check SDOM PVer name inside object", sdomPverExp, sdomInfo.getSdomPver().getPverName());

    Map<Long, PidcA2l> a2lMap = sdomInfo.getA2lMap();
    LOG.info("A2L files size = {}", a2lMap.size());
    assertFalse("A2L files available", a2lMap.isEmpty());
  }


  /**
   * @param ret
   */
  private void logPidcTreeNodeChildren(final PidcTreeNodeChildren ret) {
    StringBuilder msg = new StringBuilder();

    msg.append("   Other pidc versions present = ").append(ret.isOtherPidcVerPresent());
    msg.append("\n   SDOM PVERs present = ").append(ret.isSdomPversPresent());
    msg.append("\n   Is CDR present = ").append(ret.isCdrPresent());
    msg.append("\n   Review Questionnaires present = ").append(ret.isQuestionnairesPresent());

    // SDOM PVERs
    StringJoiner sj = new StringJoiner(", ");
    ret.getPidcSdomPverSet().stream().map(SdomPVER::getPverName).forEach(sj::add);
    msg.append("\n   SDOM PVERs = ").append(sj.toString());

    // Variants
    sj = new StringJoiner(", ");
    ret.getPidcVariants().stream().map(PidcVariant::getName).forEach(sj::add);
    msg.append("\n   Variants = ").append(sj.toString());

    LOG.info("PidcTreeNodeChildren : \n{}", msg.toString());

  }

}
