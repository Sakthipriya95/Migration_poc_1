/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.rm.ConsolidatedRisks;
import com.bosch.caltool.icdm.model.rm.RmCategory;
import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Test Client class for Meta Data Collection
 *
 * @author rgo7cob
 */
public class ConsolidatedRiskCatClientTest extends AbstractRestClientTest {

  /**
   *
   */
  public final Long pidcRmId = 1209217865L;
  /**
   *
   */
  public final Long pidcVersID = 773520365L;
  /**
   *
   */
  public final Long CategoryId = 862557515L;
  /**
   *
   */
  public final Long InvalidId = -1234L;

  /**
   * Test method for {@link ConsolidatedRiskCatClient#getConsolidatedRisks(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  // createPidcRmProjchar
  @Test
  public void testgetConsolidatedRisks() throws ApicWebServiceException {
    ConsolidatedRiskCatClient client = new ConsolidatedRiskCatClient();
    ConsolidatedRisks consolidatedRisks = client.getConsolidatedRisks(this.pidcRmId);
    assertFalse("Response should not be null ", (consolidatedRisks == null));

    Map<Long, RmCategory> categoryMap = consolidatedRisks.getCategoryMap();
    assertFalse("Map should not be null or empty ", (categoryMap == null) || (categoryMap.isEmpty()));
    testOutput(consolidatedRisks);

    Map<Long, RmCategoryMeasures> categoryMeasureMap = consolidatedRisks.getCategoryMeasureMap();
    assertFalse("Map should not be null or empty ", (categoryMeasureMap == null) || (categoryMeasureMap.isEmpty()));
    testOutput(consolidatedRisks);

    Map<Long, RmRiskLevel> riskLevelMap = consolidatedRisks.getRiskLevelMap();
    assertFalse("Map should not be null or empty ", (riskLevelMap == null) || (riskLevelMap.isEmpty()));
    testOutput(consolidatedRisks);

    Map<Long, Long> catRiskMap = consolidatedRisks.getCatRiskMap();
    assertFalse("Map should not be null or empty ", (catRiskMap == null) || (catRiskMap.isEmpty()));
    assertTrue("Map contais key", catRiskMap.containsKey(this.CategoryId));
//    for (Long catRisk : catRiskMap.keySet()) {
//      // search for value
//      Long value = catRiskMap.get(catRisk);
//      System.out.println("Key = " + catRisk + ", Value = " + value);
//    }
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testgetConsolidatedRisksForVersion() throws ApicWebServiceException {
    ConsolidatedRiskCatClient client = new ConsolidatedRiskCatClient();
    ConsolidatedRisks consolidatedRisksForVersion = client.getConsolidatedRisksForVersion(this.pidcVersID);
    assertFalse("Response should not be null ", (consolidatedRisksForVersion == null));

    Map<Long, RmCategory> categoryMap = consolidatedRisksForVersion.getCategoryMap();
    assertFalse("Map should not be null or empty ", (categoryMap == null) || (categoryMap.isEmpty()));
    testOutput(consolidatedRisksForVersion);

    Map<Long, RmCategoryMeasures> categoryMeasureMap = consolidatedRisksForVersion.getCategoryMeasureMap();
    assertFalse("Map should not be null or empty ", (categoryMeasureMap == null) || (categoryMeasureMap.isEmpty()));
    testOutput(consolidatedRisksForVersion);

    Map<Long, RmRiskLevel> riskLevelMap = consolidatedRisksForVersion.getRiskLevelMap();
    assertFalse("Map should not be null or empty ", (riskLevelMap == null) || (riskLevelMap.isEmpty()));
    testOutput(consolidatedRisksForVersion);

    Map<Long, Long> catRiskMap = consolidatedRisksForVersion.getCatRiskMap();
    assertFalse("Map should not be null or empty ", (catRiskMap == null) || (catRiskMap.isEmpty()));
    assertTrue("Map contais key", catRiskMap.containsKey(862557515L));
    // testOutput(consolidatedRisksForVersion);
  }


  /**
   * @param consolidatedRisksForVersion
   */
  private void testOutput(final ConsolidatedRisks consolidatedRisks) {


    // validating CategoryMap
    assertEquals("name should be equal", "0% RB-SW",
        consolidatedRisks.getCategoryMap().values().iterator().next().getName());
    assertEquals("nameEng should be equal", "0% RB-SW",
        consolidatedRisks.getCategoryMap().values().iterator().next().getNameEng());
    assertEquals("nameGre should be equal", "0% RB-SW",
        consolidatedRisks.getCategoryMap().values().iterator().next().getNameGer());
    assertEquals("Value should be equal", "0%",
        consolidatedRisks.getCategoryMap().values().iterator().next().getValue());

    // validating CategoryMeasureMap
    assertEquals("name should be equal",
        "Standard handling with extended hierarchy (PA-SH) during project acceptance, project review (Cal) and project risk management (Cal)",
        consolidatedRisks.getCategoryMeasureMap().values().iterator().next().getName());
    assertEquals("EngMeasure should be equal",
        "Standard handling with extended hierarchy (PA-SH) during project acceptance, project review (Cal) and project risk management (Cal)",
        consolidatedRisks.getCategoryMeasureMap().values().iterator().next().getEngMeasure());
    assertEquals("GerMeasure should be equal",
        "Standard-Handling mit Hierarchie-Erweiterung (PA-SH) bei Projektannahme, Projektreview (Cal) und Projektrisiko-Management (Cal)",
        consolidatedRisks.getCategoryMeasureMap().values().iterator().next().getGerMeasure());
    assertEquals("RiskLevel should be equal", Long.valueOf(862557265L),
        consolidatedRisks.getCategoryMeasureMap().values().iterator().next().getRiskLevel());

    // validating RiskLevelMap
    assertEquals("EngDesc should be equal", "not applicable",
        consolidatedRisks.getRiskLevelMap().values().iterator().next().getEngDesc());
    assertEquals("EngName should be equal", "N.A.",
        consolidatedRisks.getRiskLevelMap().values().iterator().next().getEngName());
    assertEquals("GerDesc should be equal", "nicht anwendbar",
        consolidatedRisks.getRiskLevelMap().values().iterator().next().getGerDesc());
    assertEquals("GerName should be equal", "N.A.",
        consolidatedRisks.getRiskLevelMap().values().iterator().next().getGerName());
    assertEquals("GerName should be equal", Long.valueOf(0),
        consolidatedRisks.getRiskLevelMap().values().iterator().next().getRiskWeight());
    assertEquals("GerName should be equal", "N.A.",
        consolidatedRisks.getRiskLevelMap().values().iterator().next().getName());
  }

  /**
   * @throws ApicWebServiceException web servicce error
   */
  // @Test
  // internal server error
  public void testgetConsolidatedRisksNegative() throws ApicWebServiceException {
    ConsolidatedRiskCatClient client = new ConsolidatedRiskCatClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + this.InvalidId + "' not found");
    client.getConsolidatedRisks(this.InvalidId);
    fail("expected exception not thrown");
  }

  /**
   * @throws ApicWebServiceException web servicce error
   */
  // @Test
  // internal server error
  public void testgetConsolidatedRisksForVersionNegative() throws ApicWebServiceException {
    ConsolidatedRiskCatClient client = new ConsolidatedRiskCatClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + this.InvalidId + "' not found");
    client.getConsolidatedRisksForVersion(this.InvalidId);
    fail("expected exception not thrown");
  }


}
