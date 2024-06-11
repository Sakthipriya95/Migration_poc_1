package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValueAndValidtyModel;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValuesCreationModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for PredefinedAttrValue
 *
 * @author PDH2COB
 */
public class PredefinedAttrValueServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public PredefinedAttrValueServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PREDEFINEDATTRVALUE);
  }

  /**
   * Get all PredefinedAttrValue records in system
   *
   * @param valId value id
   * @return Map. Key - id, Value - PredefinedAttrValue object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, PredefinedAttrValue> getByValueId(final Long valId) throws ApicWebServiceException {
    LOGGER.debug("Get  PredefinedAttrValue records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PREDEFINEDATTRVALUE)
        .queryParam(WsCommonConstants.RWS_ATTRIBUTE_VALUE, valId);
    GenericType<Map<Long, PredefinedAttrValue>> type = new GenericType<Map<Long, PredefinedAttrValue>>() {};
    Map<Long, PredefinedAttrValue> retMap = get(wsTarget, type);
    LOGGER.debug("PredefinedAttrValue records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get PredefinedAttrValue using its id
   *
   * @param objId object's id
   * @return PredefinedAttrValue object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PredefinedAttrValue getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PredefinedAttrValue.class);
  }

  /**
   * @param model PredefinedAttrValuesCreationModel
   * @return Set<PredefinedAttrValue>
   * @throws ApicWebServiceException exception while invoking service
   */
  public Set<PredefinedAttrValue> createPredefinedValues(final PredefinedAttrValuesCreationModel model)
      throws ApicWebServiceException {
    GenericType<Set<PredefinedAttrValue>> type = new GenericType<Set<PredefinedAttrValue>>() {};
    return create(getWsBase(), model, type);

  }


  /**
   * Get PredefinedAttrValue records
   *
   * @param valueIdSet - values of grp attrs
   * @return Rest response, with Map of PredefinedAttrValue objects for given value ids
   * @throws ApicWebServiceException exception while invoking service
   */
  public PredefinedAttrValueAndValidtyModel getPredefinedAttrValuesAndValidityForValueSet(final Set<Long> valueIdSet)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PREDEFINED_ATTRVALUE_VALIDITY_FOR_VALUE_SET);
    GenericType<PredefinedAttrValueAndValidtyModel> type = new GenericType<PredefinedAttrValueAndValidtyModel>() {};
    return post(wsTarget, valueIdSet, type);
  }


}
