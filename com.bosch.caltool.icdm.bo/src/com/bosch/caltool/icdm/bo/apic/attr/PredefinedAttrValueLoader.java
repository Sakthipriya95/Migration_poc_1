package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedAttrValue;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;


/**
 * Loader class for PredefinedAttrValue
 *
 * @author PDH2COB
 */
public class PredefinedAttrValueLoader extends AbstractBusinessObject<PredefinedAttrValue, TPredefinedAttrValue> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public PredefinedAttrValueLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PREDEFND_ATTR_VALUE, TPredefinedAttrValue.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PredefinedAttrValue createDataObject(final TPredefinedAttrValue entity) throws DataException {
    PredefinedAttrValue object = new PredefinedAttrValue();

    setCommonFields(object, entity);

    object.setGrpAttrValId(entity.getGrpAttrVal().getValueId());
    if (null != entity.getPreDefAttrVal()) {
      object.setPredefinedValueId(entity.getPreDefAttrVal().getValueId());
      object.setPredefinedValue(new AttributeValueLoader(getServiceData())
          .getDataObjectByID(entity.getPreDefAttrVal().getValueId()).getName());
    }
    object.setPredefinedAttrId(entity.getPreDefinedAttr().getAttrId());
    object.setPredefinedAttrName(
        new AttributeLoader(getServiceData()).getDataObjectByID(entity.getPreDefinedAttr().getAttrId()).getName());

    return object;
  }

  /**
   * Get PredefinedAttrValue records based on value id
   *
   * @param valueId value id
   * @return Map. Key - id, Value - PredefinedAttrValue object
   * @throws DataException error while retrieving data
   */
  public Map<Long, PredefinedAttrValue> getByValueId(final Long valueId) throws DataException {
    Map<Long, PredefinedAttrValue> objMap = new ConcurrentHashMap<>();
    TypedQuery<TPredefinedAttrValue> tQuery =
        getEntMgr().createNamedQuery(TPredefinedAttrValue.NQ_FIND_BY_VAL_ID, TPredefinedAttrValue.class)
            .setParameter("valId", valueId);
    List<TPredefinedAttrValue> dbObj = tQuery.getResultList();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    for (TPredefinedAttrValue entity : dbObj) {
      if (!attrLoader.getDataObjectByID(entity.getPreDefinedAttr().getAttrId()).isDeleted()) {
        objMap.put(entity.getPreAttrValId(), createDataObject(entity));
      }
    }
    return objMap;
  }


  /**
   * @param valueIds - grouped attribute value ids
   * @return Map of key - value id, value -> Map of - id, Value - PredefinedAttrValue object
   * @throws DataException
   */
  public Map<Long, Map<Long, PredefinedAttrValue>> getPredefinedAttrValueForAttrValueIds(final Set<Long> valueIds)
      throws DataException {
    Map<Long, Map<Long, PredefinedAttrValue>> allPredefAttrValMap = new HashMap<>();


    // Value id should not be empty
    if (!CommonUtils.isNullOrEmpty(valueIds)) {

      Map<Long, PredefinedAttrValue> predefAttrValMap;

      AttributeLoader attrLoader = new AttributeLoader(getServiceData());

      TypedQuery<TPredefinedAttrValue> tQuery =
          getEntMgr().createNamedQuery(TPredefinedAttrValue.NQ_FIND_BY_VAL_IDS, TPredefinedAttrValue.class)
              .setParameter("valIdSet", valueIds);

      // value Ids should not exceed 1000 for the above query
      for (TPredefinedAttrValue entity : tQuery.getResultList()) {
        PredefinedAttrValue predefVal = createDataObject(entity);
        long valueId = predefVal.getGrpAttrValId();

        if (allPredefAttrValMap.get(valueId) == null) {
          predefAttrValMap = new HashMap<>();
        }
        else {
          predefAttrValMap = allPredefAttrValMap.get(valueId);
        }
        if (!attrLoader.getDataObjectByID(predefVal.getPredefinedAttrId()).isDeleted()) {
          predefAttrValMap.put(predefVal.getId(), predefVal);
          allPredefAttrValMap.put(valueId, predefAttrValMap);
        }
      }
    }
    return allPredefAttrValMap;
  }


  /**
   * @param attributeId attribute id
   * @return true if the attribute is in predefined table
   */
  public boolean isPredefinedAttr(final Long attributeId) {
    TypedQuery<TPredefinedAttrValue> attrCheckQuery =
        getEntMgr().createNamedQuery(TPredefinedAttrValue.NQ_FIND_BY_ATTR_ID, TPredefinedAttrValue.class);
    attrCheckQuery.setParameter("attrId", attributeId);
    List<TPredefinedAttrValue> resultList = attrCheckQuery.getResultList();
    return !resultList.isEmpty();
  }


}
