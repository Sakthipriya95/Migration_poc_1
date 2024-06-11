/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.review;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.ssd.api.db.connector.SSDDataBaseConnector;
import com.bosch.ssd.api.logger.SSDApiLogger;

/**
 * @author VAU3COB
 */
@Path("/reviewservice")
public class ReviewService {

  SSDDataBaseConnector ssdDBConnector = new SSDDataBaseConnector();

  /**
   * @param reviewId ID
   * @param status review status
   * @return result
   */
  @POST
  @Path("/updateReviewStatus")
  @Produces(MediaType.APPLICATION_XML)
  public Response updateReviewStatus(@QueryParam("rvwID") String reviewId, @QueryParam("status") long status) {
   
    EntityManager em = ssdDBConnector.createConnection();
    em.getTransaction().begin();
    Query query = em.createNamedQuery("TLdb2SsdCrwReview.updateReviewStatusSSD");
    query.setParameter(1, status);
    query.setParameter(3, reviewId);
    Date date = new Date();  
    SimpleDateFormat formatdate = new SimpleDateFormat("dd-MMM-yy");
    String strDate= formatdate.format(date); 
    query.setParameter(2, strDate);
    int noOfRows = query.executeUpdate();
    em.getTransaction().commit();
    em.close();
    String result = null;
    /*
     * Review status will update only when query.executeUpdate() returns 1
     */
    if(noOfRows==1) {
      result = "Review status with review decision updated for Review ID " + reviewId;
    }
    else{
      result = "No matching record and review status is not updated";
    }
   
   SSDApiLogger.getLoggerInstance().info(result);
    return Response.status(200).entity(result).build();
  }
  
  /**
   * @param reviewId ID
   * @param status review Status
   * @param decision review decision
   * @return result
   */
  @POST
  @Path("/updateReviewStatuswithDecision")
  @Produces(MediaType.APPLICATION_XML)
  public Response updateReviewStatuswithDecision(@QueryParam("rvwID") String reviewId, @QueryParam("status") long status,@QueryParam("decision")long decision) {
    EntityManager em = ssdDBConnector.createConnection();
    em.getTransaction().begin();
    Query query = em.createNamedQuery("TLdb2SsdCrwReview.updateReviewStatusSSDwithDecision");
    query.setParameter(1, status);
    query.setParameter(2, decision);
    query.setParameter(4, reviewId);
    Date date = new Date();  
    SimpleDateFormat formatdate = new SimpleDateFormat("dd-MMM-yy");
    String strDate= formatdate.format(date);
    query.setParameter(3, strDate);
    
    int noOfRows = query.executeUpdate();
    em.getTransaction().commit();
    em.close();
    String result = null;
    /*
     * Review status will update only when query.executeUpdate() returns 1
     */
    if(noOfRows==1) {
      result = "Review status with review decision updated for Review ID " + reviewId;
    }
    else{
      result = "No matching record and review status is not updated";
    }
    SSDApiLogger.getLoggerInstance().info(result);
    return Response.status(200).entity(result).build();
  }

}
