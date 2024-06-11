/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author NIP4COB
 */
public class LinkServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final Long NODE_ID_SUPGRP = 770160016L;
  /**
   *
   */
  private static final Long NODE_ID_UCS = 772276365L;

  /**
   * Testing creating links {@link LinkServiceClient#create(List)}
   */
  @Test
  public void testCreate() {
    LinkServiceClient client = new LinkServiceClient();

    Link link1 = new Link();
    link1.setNodeId(NODE_ID_UCS);
    link1.setNodeType(MODEL_TYPE.USE_CASE_SECT.getTypeCode());
    link1.setLinkUrl("http://www.gmail.com");
    link1.setDescriptionEng("Junit ENG: LINK1: CREATE" + "NEW" + getRunId());
    link1.setDescriptionGer("Junit GER :LINK1: CREATE" + "NEW" + getRunId());

    Link link2 = new Link();
    link2.setNodeId(NODE_ID_UCS);
    link2.setNodeType(MODEL_TYPE.USE_CASE_SECT.getTypeCode());
    link2.setLinkUrl("https://inside-docupedia.bosch.com/confluence/display/RBEIETB3/iCDM+Useful");
    link2.setDescriptionEng("Junit ENG:   LINK2:  CREATE" + getRunId());
    link2.setDescriptionGer("Junit GER:   LINK2:  CREATE" + getRunId());

    try {
      Set<Link> newLinksSet = client.create(Arrays.asList(link1, link2));

      assertTrue("Response test", (newLinksSet != null) && !newLinksSet.isEmpty());

      Link first = newLinksSet.iterator().next();
      testLinkGen("testCreate", first);
      assertTrue("Link ID generated", (first.getId() != null) && (first.getId() > 0L));

      // Delete the links created
      List<Link> linkIds = newLinksSet.stream().collect(Collectors.toList());

      client.delete(linkIds);
    }

    catch (ApicWebServiceException e) {
      LOG.error("Error in WS call: create", e);
      assertNull("Error in WS call", e);
    }
  }

  /**
   * @param string
   * @param first
   */
  private void testLinkGen(final String string, final Link first) {
    assertNotNull(string + " - link not null", first);
    LOG.debug("{} - Link ID - {}", string, first.getId());
    LOG.debug("{} - Link : {}", string, first.getLinkUrl());

  }

  /**
   * Test for update : {@link LinkServiceClient#update(List)}
   */
  @Test
  public void testUpdate() {
    LinkServiceClient client = new LinkServiceClient();

    Link link1 = new Link();
    link1.setNodeId(NODE_ID_SUPGRP);
    link1.setNodeType(MODEL_TYPE.SUPER_GROUP.getTypeCode());
    link1.setDescriptionEng("Junit ENG : super group new 1 " + getRunId());
    link1.setDescriptionGer("Junit GER : super group new 1" + getRunId());
    link1.setLinkUrl("http://www.amazon.in");

    Link link2 = new Link();
    link2.setNodeId(NODE_ID_SUPGRP);
    link2.setNodeType(MODEL_TYPE.SUPER_GROUP.getTypeCode());
    link2.setDescriptionEng("Junit ENG : SUPER_GROUP  new 2 " + getRunId());
    link2.setDescriptionGer("Junit GER : SUPER_GROUP  new 2 " + getRunId());
    link2.setLinkUrl("http://www.facebook.com");

    try {

      Set<Link> createdLinkSet = client.create(Arrays.asList(link1, link2));
      assertTrue("Response test", (createdLinkSet != null) && !createdLinkSet.isEmpty());

      Iterator<Link> iterator = createdLinkSet.iterator();
      Link first = iterator.next();
      Link second = iterator.next();

      long link1Id = first.getId();

      first.setDescriptionEng("Junit ENG : super group new 1 UPDATED " + getRunId());
      first.setDescriptionGer("Junit ENG : super group new 1 UPDATED " + getRunId());

      second.setDescriptionEng("Junit ENG : super group new 2 UPDATED " + getRunId());
      second.setDescriptionGer("Junit ENG : super group new 2 UPDATED " + getRunId());

      Set<Link> updatedLinkSet = client.update(Arrays.asList(first, second));
      assertTrue("Response test", (updatedLinkSet != null) && !updatedLinkSet.isEmpty());

      Link firstNew = null;

      for (Link lk : updatedLinkSet) {
        if (lk.getId().longValue() == link1Id) {
          firstNew = lk;
          break;
        }
      }

      assertNotNull("First link updated found", firstNew);
      assertEquals("Version is equal", Long.valueOf(first.getVersion() + 1), firstNew.getVersion());
      assertEquals("Check desc eng", "Junit ENG : super group new 1 UPDATED " + getRunId(),
          firstNew.getDescriptionEng());

      client.delete(Arrays.asList(first, second));


    }
    catch (ApicWebServiceException e) {
      LOG.error("Error in WS call: Update", e);
      assertNull("Error in WS call", e);
    }
  }

  /**
   * Test for delete : {@link LinkServiceClient#delete(List)}
   */
  @Test
  public void testDelete() {


    Link link1 = new Link();
    link1.setNodeId(NODE_ID_SUPGRP);
    link1.setNodeType(MODEL_TYPE.SUPER_GROUP.getTypeCode());
    link1.setDescriptionEng("Junit DEL ENG : super group new 1 " + getRunId());
    link1.setDescriptionGer("Junit DEL GER : super group new 1" + getRunId());
    link1.setLinkUrl("http://www.amazon.in");

    Link link2 = new Link();
    link2.setNodeId(NODE_ID_SUPGRP);
    link2.setNodeType(MODEL_TYPE.SUPER_GROUP.getTypeCode());
    link2.setDescriptionEng("Junit DEL ENG : SUPER_GROUP  new 2 " + getRunId());
    link2.setDescriptionGer("Junit DEL GER : SUPER_GROUP  new 2 " + getRunId());
    link2.setLinkUrl("http://www.facebook.com");

    LinkServiceClient client = new LinkServiceClient();

    try {
      Set<Link> createdLinkSet = client.create(Arrays.asList(link1, link2));
      assertTrue("Response test", (createdLinkSet != null) && !createdLinkSet.isEmpty());

      List<Link> linkIds = createdLinkSet.stream().collect(Collectors.toList());
      client.delete(linkIds);
    }
    catch (ApicWebServiceException e) {
      LOG.error("Error in WS call: Delete", e);
      assertNull("Error in WS call", e);
    }
  }

  /**
   * Test for {@link LinkServiceClient#getAllNodeIdByType()}
   */
  @Test
  public void testGetAllNodeIdByType() {
    LinkServiceClient client = new LinkServiceClient();

    try {
      Map<String, Set<Long>> retMap = client.getAllNodeIdByType();
      assertNotNull("Response should not be null", retMap);
      assertFalse("Response should have links", retMap.isEmpty());
      LOG.info("Size of map:" + retMap.size());
    }
    catch (ApicWebServiceException e) {
      LOG.error("Error in WS call:", e);
      assertNull("Error in WS call", e);
    }

  }

  /**
   * Test for {@link LinkServiceClient#getAllLinksByNode(Long, com.bosch.caltool.datamodel.core.IModelType)}
   */
  @Test
  public void testGetAllLinksByNode() {
    LinkServiceClient client = new LinkServiceClient();

    try {
      Map<Long, Link> retMap = client.getAllLinksByNode(NODE_ID_SUPGRP, MODEL_TYPE.SUPER_GROUP);
      assertNotNull("Response should not be null", retMap);
      assertFalse("Response should have links", retMap.isEmpty());
      LOG.info("Size of map:" + retMap.size());
    }
    catch (ApicWebServiceException e) {
      LOG.error("Error in WS call:", e);
      assertNull("Error in WS call", e);
    }

  }

  /**
   * Test for {@link LinkServiceClient#getNodesWithLink(com.bosch.caltool.datamodel.core.IModelType)}
   */
  @Test
  public void testGetNodesWithLink() {
    LinkServiceClient client = new LinkServiceClient();

    try {
      Set<Long> retSet = client.getNodesWithLink(MODEL_TYPE.SUPER_GROUP);
      assertNotNull("Response should not be null", retSet);
      assertFalse("Response should have links", retSet.isEmpty());
      LOG.info("Size of map:" + retSet.size());
    }
    catch (ApicWebServiceException e) {
      LOG.error("Error in WS call:", e);
      assertNull("Error in WS call", e);
    }

  }

  /**
   * Test for {@link LinkServiceClient#getHelpLinks()}
   */
  @Test
  public void testGetHelpLinks() {
    LinkServiceClient client = new LinkServiceClient();

    try {
      Map<String, Link> retMap = client.getHelpLinks();
      assertNotNull("Response should not be null", retMap);
      assertFalse("Response should have links", retMap.isEmpty());
      LOG.info("Size of map:" + retMap.size());
    }
    catch (ApicWebServiceException e) {
      LOG.error("Error in WS call:", e);
      assertNull("Error in WS call", e);
    }
  }
}
