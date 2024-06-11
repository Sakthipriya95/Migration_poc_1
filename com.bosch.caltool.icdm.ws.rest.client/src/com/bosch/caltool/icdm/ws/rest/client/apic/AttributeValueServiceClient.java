/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.apache.commons.io.IOUtils;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author NIP4COB
 */
public class AttributeValueServiceClient extends AbstractRestServiceClient {


  /**
   * constructor
   */
  public AttributeValueServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_ATTRIBUTE_VALUE);
  }

  /**
   * get all values for the given attributes
   *
   * @param attrId attribute id
   * @return Map. Key - attribute Id; value - Map.(key - AttributeValueid, value - AttributeValue)
   * @throws ApicWebServiceException error while retrieving data
   */
  public Map<Long, Map<Long, AttributeValue>> getValuesByAttribute(final Long attrId) throws ApicWebServiceException {
    LOGGER.debug("Get all AttributeValues for attribute {}", attrId);

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_ATTRIBUTE_ID, attrId);
    GenericType<Map<Long, Map<Long, AttributeValue>>> type = new GenericType<Map<Long, Map<Long, AttributeValue>>>() {};
    Map<Long, Map<Long, AttributeValue>> retMap = get(wsTarget, type);

    LOGGER.debug("AttributeValues loaded = {}", retMap.isEmpty() ? 0 : retMap.values().iterator().next().size());

    return retMap;
  }


  /**
   * get predefined validity values for given attributes
   *
   * @param attrValId attribute value id
   * @return Map. Key - PredefinedValidity Id; value - PredefinedValidity object
   * @throws ApicWebServiceException error while retrieving data
   */
  public Map<Long, PredefinedValidity> getValuesByAttributeValue(final Long attrValId) throws ApicWebServiceException {
    LOGGER.debug("Get all PredefinedValidity for attribute value {}", attrValId);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PREDEFINEDATTRVALUE)
        .queryParam(WsCommonConstants.RWS_ATTRIBUTE_VALUE, attrValId);
    GenericType<Map<Long, PredefinedValidity>> type = new GenericType<Map<Long, PredefinedValidity>>() {};
    Map<Long, PredefinedValidity> retMap = get(wsTarget, type);

    LOGGER.debug("PredefinedValidity loaded = {}", retMap.isEmpty() ? 0 : retMap.size());

    return retMap;
  }


  /**
   * get value for the given attributeId and pidcVersion
   *
   * @param pidcVersId
   * @param attrId
   * @return
   * @throws ApicWebServiceException
   */
  public AttributeValue getValueByAttribute(final Long pidcVersId, final Long attrId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ATTR_VALUE_BY_PIDCVERSION)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId)
        .queryParam(WsCommonConstants.RWS_QP_ATTRIBUTE_ID, attrId);
    AttributeValue attrValue = get(wsTarget, AttributeValue.class);
    return attrValue;
  }

  /**
   * @param attrId
   * @return
   * @throws ApicWebServiceException
   */
  public Map<Long, Set<AttrNValueDependency>> getValueDependecyMap(final Long attrId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_VALUE_DEP).queryParam(WsCommonConstants.RWS_QP_ATTRIBUTE_ID, attrId);
    GenericType<Map<Long, Set<AttrNValueDependency>>> type = new GenericType<Map<Long, Set<AttrNValueDependency>>>() {};
    return get(wsTarget, type);
  }

  /**
   * Get AttrGroup using its id
   *
   * @param objId object's id
   * @return AttributeValue object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttributeValue getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_QP_OBJ_ID).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, AttributeValue.class);
  }

  /**
   * Create a AttrGroup record
   *
   * @param obj object to create
   * @return created AttributeValue object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttributeValue create(final AttributeValue obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a AttrGroup record
   *
   * @param obj object to update
   * @return updated AttributeValue object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttributeValue update(final AttributeValue obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * @param attrId attrId
   * @return the list of attr Values
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public SortedSet<AttributeValue> getFeatureMappedAttrVal(final Long attrId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_ALL).queryParam(WsCommonConstants.RWS_QP_ATTRIBUTE_ID, attrId);
    GenericType<SortedSet<AttributeValue>> type = new GenericType<SortedSet<AttributeValue>>() {};
    return get(wsTarget, type);
  }

  /**
   * @param nameValId
   * @param nameEng
   * @param nameGer
   * @param descEng
   * @param descGer
   * @return
   * @throws ApicWebServiceException
   */
  public AttributeValue updatePidcName(final AttributeValue obj) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_UPD_PIDC_NAME);
    return update(wsTarget, obj);
  }

  /**
   * Delete a Pidc record
   *
   * @param pidcNameVal id of object to delete
   * @return
   * @throws ApicWebServiceException exception while invoking service
   */
  public AttributeValue deleteUnDelPidc(final AttributeValue pidcNameVal) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_DEL_UNDEL_PIDC);
    return update(wsTarget, pidcNameVal);
  }

  /**
   * @param dirPath
   * @return
   * @throws ApicWebServiceException
   */
  public byte[] getMailContent(final String dirPath) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MAIL_TEMPLATE);
    downloadFile(wsTarget, ApicConstants.DELETE_ATTR_VAL_MAIL_TEMP, dirPath);

    try {
      return IOUtils
          .toByteArray(new FileInputStream(dirPath + File.separator + ApicConstants.DELETE_ATTR_VAL_MAIL_TEMP));
    }
    catch (IOException e) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, e.getMessage(), e);
    }
  }


  /**
   * @param attrValueIds
   * @return true if any of the value id is used
   * @throws ApicWebServiceException
   */
  public boolean getLevelAttrValuesUsedStatus(final List<Long> attrValueIds) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_LEVEL_ATTR_VALUE_USED);
    for (Long valueId : attrValueIds) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_ATTR_VALUE_ID, valueId);
    }
    return get(wsTarget, Boolean.class);
  }

}
