/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.comppkg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParameter;
import com.bosch.caltool.icdm.model.comppkg.CompPkgRuleResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class CompPkgServiceClientTest extends AbstractRestClientTest {

  private final static Long COMP_PKG_ID = 765920566L;

  private final static Long SSD_NODE_ID = 3530996580L;

  /**
   * @param args
   */
  @Test
  public void testGetAll() throws IOException, ApicWebServiceException {
    LOG.info("=======================================================================================================");
    LOG.info(
        "testGetAll =======================================================================================================");
    LOG.info("=======================================================================================================");

    CompPkgServiceClient servClient = new CompPkgServiceClient();
    try {
      SortedSet<CompPackage> allDefSet = servClient.getAll();
      assertFalse("CompPkg list not null or empty", (allDefSet == null) || allDefSet.isEmpty());
      LOG.info("CompPkg list size = " + allDefSet.size());
      LOG.info("First CompPkg = " + allDefSet.iterator().next().getName());

    }
    catch (Exception e) {
      assertNull("Error in WS call", e);
    }
  }

  @Test
  public void testGetCompPackageById() throws IOException, ApicWebServiceException {
    LOG.info("=======================================================================================================");
    LOG.info(
        "testGetCompPackageById =======================================================================================================");

    CompPkgServiceClient servClient = new CompPkgServiceClient();

    try {
      CompPackage compPackage = servClient.getCompPackageById(COMP_PKG_ID);
      assertNotNull("CompPkg not null", compPackage);
      LOG.info("CompPkg Name " + compPackage.getName());
      assertEquals("CompPkg Name check", "ThrVlv (DV-E)", compPackage.getName());
    }
    catch (Exception e) {
      assertNull("Error in WS call", e);
    }
  }

  /**
   * @throws IOException Exception
   * @throws ApicWebServiceException WebServiceException
   */
  public void testGetBySSDNodeId() throws IOException, ApicWebServiceException {
    LOG.info("=======================================================================================================");
    LOG.info(
        "testGetCompPkgParamSetBySSDNodeId =======================================================================================================");

    CompPkgServiceClient servClient = new CompPkgServiceClient();

    try {
      CompPackage compPackage = new CompPackage();
      compPackage.setSsdNodeId(SSD_NODE_ID);
      compPackage.setId(COMP_PKG_ID);
      Map<String, List<ReviewRule>> reviewRuleMap = servClient.getBySSDNodeId(compPackage);
      Map<String, CompPkgParameter> compPkgParamMap =
          servClient.getCompPkgParam(reviewRuleMap.keySet(), compPackage.getId());
      assertNotNull("Review Rule Map is not null ", reviewRuleMap);
      LOG.info("Review Rule Map Size " + reviewRuleMap.size());
      assertNotNull("Comp Pkg Param Set is not null ", compPkgParamMap);
      LOG.info("Comp Pkg Param Set Size " + compPkgParamMap.size());
    }
    catch (Exception e) {
      assertNull("Error in WS call", e);
    }
  }

  /**
   *
   */
  public void testGetCompPkgRule() throws IOException, ApicWebServiceException {
    LOG.info("=======================================================================================================");
    LOG.info(
        "testGetCompPkgRule =======================================================================================================");

    CompPkgServiceClient servClient = new CompPkgServiceClient();

    try {
      CompPkgRuleResponse compPkgRuleResp = servClient.getCompPkgRule(COMP_PKG_ID);
      assertNotNull("CompPkgRuleResponse is not null ", compPkgRuleResp);
      LOG.info("Comp Pkg Param Attr Map Size " + compPkgRuleResp.getAttrMap().size());
      LOG.info("Attribute Map Size " + compPkgRuleResp.getAttrObjMap().size());
      LOG.info("Comp Pkg Param Map Size " + compPkgRuleResp.getParamMap().size());
      LOG.info("Review Rule Map Size " + compPkgRuleResp.getReviewRuleMap().size());
    }
    catch (Exception e) {
      assertNull("Error in WS call", e);
    }
  }
}
