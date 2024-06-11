/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.ssd2bc;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.bosch.ssd.api.db.connector.SSDDataBaseConnector;
import com.bosch.ssd.api.jpa.TSdomBcVariantSsdStatus;
import com.bosch.ssd.api.jpa.VSdomBcSsdinfo;
import com.bosch.ssd.api.ssd2bc.model.SSD2BCInfo;

/**
 * @author vau3cob
 */
@Path("/SSD2BCService")
public class SSD2BCService {

  SSDDataBaseConnector ssd2BCDao = new SSDDataBaseConnector();

  /**
   * @return
   */
  @GET
  @Path("/bcinfo")
  @Produces(MediaType.APPLICATION_XML)
  public List<TSdomBcVariantSsdStatus> getSSD2BC() {
    EntityManager em = ssd2BCDao.createConnection();
    Query query = em.createNamedQuery("TSdomBcVariantSsdStatus.findBCSSDInfo");
    query.setParameter("elNummer", 21471);
    query.setParameter("variant", "10.3.1");
    List<TSdomBcVariantSsdStatus> results = query.getResultList();
    em.close();
    return results;
  }

  /**
   * @param elnummer
   * @param variant
   * @return
   */
  @GET
  @Path("/bcvariantinfo")
  @Produces(MediaType.APPLICATION_JSON)
  public List<TSdomBcVariantSsdStatus> getSSD2BC(@QueryParam("elnummer") long elnummer,
      @QueryParam("variant") String variant) {
    EntityManager em = ssd2BCDao.createConnection();
    Query query = em.createNamedQuery("TSdomBcVariantSsdStatus.findBCSSDInfo");
    query.setParameter("elNummer", elnummer);
    query.setParameter("variant", variant);
    List<TSdomBcVariantSsdStatus> results = query.getResultList();
    em.close();
    return results;
  }

  /**
   * @param elnummer
   * @param variant
   * @return
   */
  @GET
  @Path("/getSSD2BCInfoByNummer")
  @Produces(MediaType.APPLICATION_JSON)
  public SSD2BCInfo getSSD2BCInfoByElementNumber(@QueryParam("elnummer") long elnummer,
      @QueryParam("variant") String variant) {
    EntityManager em = ssd2BCDao.createConnection();
    Query query = em.createNamedQuery("VSdomBcSsdinfo.findBCinfo");
    query.setParameter("elnummer", elnummer);
    query.setParameter("variante", variant);
    List<VSdomBcSsdinfo> results = query.getResultList();

    SSD2BCInfo info = new SSD2BCInfo();
    for (VSdomBcSsdinfo vInfo : results) {
      info.setBcName(vInfo.getBcName());
      info.setBcNumber(vInfo.getBcNumber());
      info.setBcRevision(vInfo.getBcRevision());
      info.setBcVariant(vInfo.getBcVariant());
      info.setSsdStatus(vInfo.getSsdStatus());
      info.getAssignedNodes().add(vInfo.getNodeName());
    }
    em.close();
    return info;
  }

  /**
   * @param bcName
   * @param variant
   * @return
   */
  @GET
  @Path("/getSSD2BCInfoByName")
  @Produces(MediaType.APPLICATION_JSON)
  public SSD2BCInfo getSSD2BCInfo(@QueryParam("bcname") String bcName, @QueryParam("variant") String variant) {
    EntityManager em = ssd2BCDao.createConnection();
    Query query = em.createNamedQuery("VSdomBcSsdinfo.findBCinfoByBCName");
    query.setParameter("bcName", bcName);
    query.setParameter("variante", variant);
    List<VSdomBcSsdinfo> results = query.getResultList();

    SSD2BCInfo info = new SSD2BCInfo();
    for (VSdomBcSsdinfo vInfo : results) {
      info.setBcName(vInfo.getBcName());
      info.setBcNumber(vInfo.getBcNumber());
      info.setBcRevision(vInfo.getBcRevision());
      info.setBcVariant(vInfo.getBcVariant());
      info.setSsdStatus(vInfo.getSsdStatus());
      info.getAssignedNodes().add(vInfo.getNodeName());
    }
    em.close();
    return info;
  }
}
