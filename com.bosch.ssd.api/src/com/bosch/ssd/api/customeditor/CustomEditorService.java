/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.customeditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.bosch.ssd.api.db.connector.SSDDataBaseConnector;
import com.bosch.ssd.api.jpa.customeditor.TempLabelsForCategory;
import com.bosch.ssd.api.jpa.customeditor.VLdb2Pavast;
import com.bosch.ssd.api.logger.SSDApiLogger;

/**
 * @author SSN9COB
 */
@Path("/customeditorservice")
public class CustomEditorService {


  SSDDataBaseConnector ssdDBConnector = new SSDDataBaseConnector();


  /**
   * @param formParams map params
   * @return result
   */
  @POST
  @Path("/getLabelCategory")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response getLabelCategory(final MultivaluedMap<String, String> formParams) {
    List<String> uniqueLabels = new ArrayList<>();
    for (Entry<String, List<String>> map : formParams.entrySet()) {
      uniqueLabels.addAll(map.getValue());
    }
    List<VLdb2Pavast> pavastInfo = checkAndGetLabelInfo(uniqueLabels);

    Map<String, String> labelCategoryMap = new HashMap<>();
    if ((pavastInfo != null)) {
      for (VLdb2Pavast pavast : pavastInfo) {
        labelCategoryMap.put(pavast.getLabel(), pavast.getCategory());
      }
    }
    SSDApiLogger.getLoggerInstance().info("Label  exist and reutrn list size is " + labelCategoryMap.size());
    return Response.status(200).entity(labelCategoryMap.toString()).build();
  }

  /*
   * Fetching data from "VLdb2Pavast" if the selected label already exists
   */
  private List<VLdb2Pavast> checkAndGetLabelInfo(final List<String> label) {
    EntityManager em = this.ssdDBConnector.createConnection();
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
      em.createNamedQuery("VLdb2Pavast.deleteTempValues").executeUpdate();
    }
    int id = 1;
    for (String labelName : label) {
      TempLabelsForCategory labelCat = new TempLabelsForCategory();
      labelCat.setLabelName(labelName.trim());
      labelCat.setSeqid(id++);
      em.persist(labelCat);
    }
    em.flush();
    em.getTransaction().commit();
    Query query = em.createNamedQuery("VLdb2Pavast.labelExistsData");
    return query.getResultList();
  }
}
