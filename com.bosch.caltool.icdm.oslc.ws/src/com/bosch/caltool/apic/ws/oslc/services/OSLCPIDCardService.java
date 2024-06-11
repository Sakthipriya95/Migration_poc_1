/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;

import com.bosch.caltool.apic.ws.oslc.resources.ConstantsOSLC;
import com.bosch.caltool.apic.ws.oslc.resources.OSLCPIDCard;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;

/**
 * @author mkl2cob
 */
@OslcService(ConstantsOSLC.LOCAL_SERVER_NAMESPACE)
@Path("pidcard")
public class OSLCPIDCardService {

  /**
   * @param where denotes the where clause in a query
   * @return OSLCPIDCard[]
   */
  @OslcQueryCapability(title = "PIDCard Query Capability", label = "PIDCard Catalog Query", resourceShape = OslcConstants.PATH_RESOURCE_SHAPES +
      "/" + ConstantsOSLC.PATH_PIDC_RESOURCE, resourceTypes = {
          ConstantsOSLC.TYPE_PID_CARD }, usages = { OslcConstants.OSLC_USAGE_DEFAULT })
  @GET
  @Produces({ OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
  public OSLCPIDCard[] getResources(@QueryParam("oslc.where") final String where) {

    // send the collection of all the pidc cards

    OSLCPIDCard[] sampleResources = null;

    // get pidcards with query
    EntityManager entMgrToUse = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
    entMgrToUse.getTransaction().begin();

    String queryStr = "select * from TabvProjectidcard pidcard where " + where;
    TypedQuery<TabvProjectidcard> query = entMgrToUse.createQuery(queryStr, TabvProjectidcard.class);

    List<TabvProjectidcard> dbPidcList = query.getResultList();

    for (TabvProjectidcard tabvProjectidcard : dbPidcList) {
      OSLCPIDCard oslcPidCard = new OSLCPIDCard();
      Long pidcId = new Long(tabvProjectidcard.getProjectId());
      oslcPidCard.setIdentifier(pidcId.toString());
      oslcPidCard.setTitle(tabvProjectidcard.getTabvAttrValue().getValueDescEng());
    }


    if (entMgrToUse.isOpen()) {
      entMgrToUse.close();
    }


    return sampleResources;
  }


}
