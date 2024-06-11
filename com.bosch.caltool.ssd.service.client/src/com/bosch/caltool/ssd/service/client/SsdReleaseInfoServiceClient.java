/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.ssd.service.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.bosch.caltool.ssd.service.client.model.ProjectReleaseModel;
import com.bosch.caltool.ssd.service.client.model.SsdReleaseInfoModel;
import com.bosch.caltool.ssd.service.exception.SsdReleaseInfoException;
import com.bosch.caltool.ssd.service.properties.SsdReleasePluginProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author QRK1COB
 */
public class SsdReleaseInfoServiceClient {

//  private static final Logger LOGGER = LogManager.getLogger(SsdReleaseInfoServiceClient.class);

  private static final String ERRORSTATUS = "Error: Response status is not OK. Status Type: {}, Status code: {}";

  private static final String EEROR_IN_PROCESSING = "Error: Processing response failed. ";

  private static final String ERROR_IN_FETCHING = "Error: Fetching release details failed. ";
  
  private static final String ERROR_IN_NODE_ID_NULL = "Error: Fetching release details failed. Node Id is null/Invalid";

  private static final String STATUS_TYPE = "Status Type: ";

  private static final String STATUS_CODE = " Status code: ";

  /**
   *
   */
  public static final String SUCCESS = "success";

  /**
   *
   */
  public static final String ERROR = "error";

  /**
   *
   */
  public static final String RESULT = "result";

  /**
   * @param node nodeId
   * @return List<VLdb2ProjectReleaseEdc17Model> => release response model
   * @throws SsdReleaseInfoException SsdReleaseInfoException
   */
  public List<ProjectReleaseModel> getReleaseInfoByNodeId(final Long node) throws SsdReleaseInfoException {

    List<ProjectReleaseModel> outputModelList = null;
    Client client = ClientBuilder.newClient();
    if(node != null && node >= 0 && node <= 9999999999L) {
      
      WebTarget target = client.target(SsdReleasePluginProperties.getTargetUrl()).path("node").path(node.toString());

      Response response = target.request().get();

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        String result = response.readEntity(String.class);
        try {
          ObjectMapper objectMapper = new ObjectMapper();
          JsonNode jsonNode = objectMapper.readTree(result);
          outputModelList = setReleaseInfoToReleaseModel(jsonNode);
        }
        catch (IOException e) {
//          LOGGER.error("IOException in SsdReleaseInfoServiceClient::getReleaseInfoByNodeId, full stack trace follows: ",
//              e);
          throw new SsdReleaseInfoException(
              EEROR_IN_PROCESSING + STATUS_TYPE + response.getStatusInfo() + STATUS_CODE + response.getStatus());
        }
      }
      else {
//        LOGGER.error(ERRORSTATUS, response.getStatusInfo(), response.getStatus());
        throw new SsdReleaseInfoException(
            ERROR_IN_FETCHING + STATUS_TYPE + response.getStatusInfo() + STATUS_CODE + response.getStatus());
      }
      
    } else {
      throw new SsdReleaseInfoException(
          ERROR_IN_NODE_ID_NULL);
    }
    


    return outputModelList;

  }


  /**
   * @param node node
   * @param proRelId proRelId
   * @return VLdb2ProjectReleaseEdc17Model list
   * @throws SsdReleaseInfoException SsdReleaseInfoException
   */
  public List<ProjectReleaseModel> getReleaseInfoByNodeIdAndProRelId(final Long node, final Long proRelId)
      throws SsdReleaseInfoException {
    List<ProjectReleaseModel> outputModelList;
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(SsdReleasePluginProperties.getTargetUrl()).path("node").path(node.toString())
        .path("rel").path(proRelId.toString());

    Response response = target.request().get();

    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      String result = response.readEntity(String.class);
      try {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        outputModelList = setReleaseInfoToReleaseModel(jsonNode);
      }
      catch (IOException e) {
//        LOGGER.error(
//            "IOException in SsdReleaseInfoServiceClient::getReleaseInfoByNodeIdAndProRelId, full stack trace follows: ",
//            e);
        throw new SsdReleaseInfoException(
            EEROR_IN_PROCESSING + STATUS_TYPE + response.getStatusInfo() + STATUS_CODE + response.getStatus());
      }
    }
    else {
//      LOGGER.error(ERRORSTATUS, response.getStatusInfo(), response.getStatus());
      throw new SsdReleaseInfoException(
          ERROR_IN_FETCHING + STATUS_TYPE + response.getStatusInfo() + STATUS_CODE + response.getStatus());

    }

    return outputModelList;
  }

  private List<ProjectReleaseModel> setReleaseInfoToReleaseModel(final JsonNode jsonNode) {
    List<ProjectReleaseModel> outputModelList = new ArrayList<>();

    try {
      if (jsonNode.get(SUCCESS).asBoolean()) {
        JsonNode jsonArray = jsonNode.get(RESULT);
        ObjectMapper objectMapper = new ObjectMapper();
        for (JsonNode explrObject : jsonArray) {
          ProjectReleaseModel outputModel = objectMapper.treeToValue(explrObject, ProjectReleaseModel.class);
          outputModelList.add(outputModel);
        }
      }
    }
    catch (Exception e) {
//      LOGGER.error("Exception in SsdReleaseInfoServiceClient::setReleaseInfoToReleaseModel, full stack trace follows: ",
//          e);
    }

    return outputModelList;
  }


  /**
   * @param proRelId RELEASE ID
   * @return SsdReleaseInfoModel
   * @throws SsdReleaseInfoException SsdReleaseInfoException
   */
  public SsdReleaseInfoModel getReleaseDetails(final Long proRelId) throws SsdReleaseInfoException {

    SsdReleaseInfoModel ssdRel;

    if(proRelId != null && proRelId >= 0 && proRelId <= 9999999999L) {
      
      Client client = ClientBuilder.newClient();
      WebTarget target =
          client.target(SsdReleasePluginProperties.getTargetUrl()).path("releaseDetails").path(proRelId.toString());

      Response response = target.request().get();

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        String result = response.readEntity(String.class);
        try {
          ObjectMapper objectMapper = new ObjectMapper();
          JsonNode jsonNode = objectMapper.readTree(result);
          ssdRel = setReleaseDetailsToModel(jsonNode);
        }
        catch (IOException e) {
//          LOGGER.error("IOException in SsdReleaseInfoServiceClient::getReleaseDetails, full stack trace follows: ", e);
          throw new SsdReleaseInfoException(
              EEROR_IN_PROCESSING + STATUS_TYPE + response.getStatusInfo() + STATUS_CODE + response.getStatus());
        }
      }
      else {
//        LOGGER.error(ERRORSTATUS, response.getStatusInfo(), response.getStatus());
        throw new SsdReleaseInfoException(
            ERROR_IN_FETCHING + STATUS_TYPE + response.getStatusInfo() + STATUS_CODE + response.getStatus());
      }
      
    } else {
      throw new SsdReleaseInfoException(
          ERROR_IN_NODE_ID_NULL);
    }
   
    
    
    return ssdRel;

  }

  /**
   * @param jsonNode jsonNode
   * @return SsdReleaseInfoModel
   */
  private SsdReleaseInfoModel setReleaseDetailsToModel(final JsonNode jsonNode) {
    SsdReleaseInfoModel outputModel = new SsdReleaseInfoModel();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      if (jsonNode.get(SUCCESS).asBoolean()) {
        JsonNode explrNode = jsonNode.get(RESULT);
        outputModel = objectMapper.treeToValue(explrNode, SsdReleaseInfoModel.class);
      }

      return outputModel;
    }
    catch (IOException e) {
//      LOGGER.error("IOException in SsdReleaseInfoServiceClient::setReleaseDetailsToModel, full stack trace follows: ",
//          e);
    }

    return outputModel;
  }


}
