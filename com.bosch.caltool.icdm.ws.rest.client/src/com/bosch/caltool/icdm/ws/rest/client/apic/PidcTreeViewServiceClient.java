/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSdomA2lInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeNodeChildren;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob Client Class to call PidcTreeViewService methods to load the PIDC tree view
 */
public class PidcTreeViewServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public PidcTreeViewServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_TREE);
  }

  /**
   * Client method to call service for fetching pidc structure attr max level
   *
   * @return maximum Pidc stucture attribute level
   * @throws ApicWebServiceException web service exception
   */
  public Long getPidcStrucAttrMaxLevel() throws ApicWebServiceException {
    LOGGER.debug("Loading Pidc Maximum Structure attr level");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_MAX_STRUCT_ATTR_LVL);
    Long maxPidcStructLvl = get(wsTarget, Long.class);
    LOGGER.debug("Pidc Pidc Maximum Structure attr level.");
    return maxPidcStructLvl;
  }

  /**
   * Client method to call service for fetching map of all level attributes
   *
   * @return Map of level attributes ; key - attr id, value - attribute
   * @throws ApicWebServiceException web service exception
   */
  public Map<Long, Attribute> getAllLvlAttrByAttrId() throws ApicWebServiceException {
    LOGGER.debug("Started Fetching all level attributes with attribute id as key");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_ALL_LVL_ATTR_ATTRID);
    GenericType<Map<Long, Attribute>> type = new GenericType<Map<Long, Attribute>>() {};
    Map<Long, Attribute> lvlAttrMap = get(wsTarget, type);
    LOGGER.debug("Fetching all level attributes with attribute id as key completed");
    return lvlAttrMap;
  }

  /**
   * Client method to call service for fetching map of all level attributes
   *
   * @return Map of level attributes ; key - attribute level, value - attribute
   * @throws ApicWebServiceException web service exception
   */
  public Map<Long, Attribute> getAllLvlAttrByLevel() throws ApicWebServiceException {
    LOGGER.debug("Started Fetching all level attributes with level as key");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_ALL_LVL_ATTR_LEVEL);
    GenericType<Map<Long, Attribute>> type = new GenericType<Map<Long, Attribute>>() {};
    Map<Long, Attribute> lvlAttrMap = get(wsTarget, type);
    LOGGER.debug("Fetching all level attributes with level as key completed");
    return lvlAttrMap;
  }

  /**
   * Client method to call service for fetching map of all level attributes and their values
   *
   * @return Map of all level attributes and their values ; key - attr id, value - map of attribute values
   * @throws ApicWebServiceException web service exception
   */
  public Map<Long, Map<Long, AttributeValue>> getAllPidTreeLvlAttrValueSet() throws ApicWebServiceException {
    LOGGER.debug("Started Fetching all level attributes and its value set");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_ALL_LVL_ATTR_VALUES_SET);
    GenericType<Map<Long, Map<Long, AttributeValue>>> type = new GenericType<Map<Long, Map<Long, AttributeValue>>>() {};
    Map<Long, Map<Long, AttributeValue>> lvlAttrMap = get(wsTarget, type);
    LOGGER.debug("Fetching all level attributes and its value set completed");
    return lvlAttrMap;
  }

  /**
   * @param pidcId pidc id
   * @param pidcVerId pidc version id
   * @return PidcTreeNodeChildren child nodes
   * @throws ApicWebServiceException Web service exception
   */
  public PidcTreeNodeChildren getPidcNodeChildAvailblty(final Long pidcId, final Long pidcVerId)
      throws ApicWebServiceException {
    LOGGER.debug("Started fetching Pidc node children availability");
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_PIDC_VER_CHILD).queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId)
            .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVerId);
    PidcTreeNodeChildren pidcNodeChildAvailblty = get(wsTarget, PidcTreeNodeChildren.class);
    LOGGER.debug("Fetching Pidc node children availability completed");
    return pidcNodeChildAvailblty;
  }

  /**
   * @param pidcVerId pidc version id
   * @return map of PidcSdomA2lInfo, key as SDOM-PVER name
   * @throws ApicWebServiceException Web service exception
   */
  public Map<String, PidcSdomA2lInfo> getPidcSdomPvers(final Long pidcVerId) throws ApicWebServiceException {
    LOGGER.debug("Started fetching sdom pvers for pidc");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_SDOM_PVER)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVerId);
    GenericType<Map<String, PidcSdomA2lInfo>> type = new GenericType<Map<String, PidcSdomA2lInfo>>() {};
    Map<String, PidcSdomA2lInfo> pverA2lMap = get(wsTarget, type);
    LOGGER.debug("Fetching sdom pvers for pidc completed");
    return pverA2lMap;
  }

}
