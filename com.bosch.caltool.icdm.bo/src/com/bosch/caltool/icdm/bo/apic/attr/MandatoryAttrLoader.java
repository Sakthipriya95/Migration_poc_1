package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TMandatoryAttr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.MandatoryAttr;


/**
 * Loader class for MandatoryAttr
 *
 * @author pdh2cob
 */
public class MandatoryAttrLoader extends AbstractBusinessObject<MandatoryAttr, TMandatoryAttr> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public MandatoryAttrLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.MANDATORY_ATTR, TMandatoryAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected MandatoryAttr createDataObject(final TMandatoryAttr entity) throws DataException {
    MandatoryAttr object = new MandatoryAttr();

    setCommonFields(object, entity);

    object.setDefValueId(entity.getTabvAttrValue().getValueId());
    object.setAttrId(entity.getTabvAttribute().getAttrId());

    return object;
  }

  /**
   * Get all MandatoryAttr records in system
   *
   * @return Map. Key - id, Value - MandatoryAttr object
   * @throws DataException error while retrieving data
   */
  public Map<Long, MandatoryAttr> getAll() throws DataException {
    Map<Long, MandatoryAttr> objMap = new ConcurrentHashMap<>();
    TypedQuery<TMandatoryAttr> tQuery = getEntMgr().createNamedQuery(TMandatoryAttr.GET_ALL, TMandatoryAttr.class);
    List<TMandatoryAttr> dbObj = tQuery.getResultList();
    for (TMandatoryAttr entity : dbObj) {
      objMap.put(entity.getMaId(), createDataObject(entity));
    }
    return objMap;
  }


  /**
   * @return Map -> key - attirbute value id, value - mandatory attribute map for attribute id
   * @throws DataException
   */
  public Map<Long, Map<Long, MandatoryAttr>> getAllMandatoryAttributesForAllAttributes() throws DataException {
    Map<Long, Map<Long, MandatoryAttr>> objMap = new ConcurrentHashMap<>();

    // get all mandatory attributes from db
    Map<Long, MandatoryAttr> mandAttrMap = getAll();
    AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
    for (MandatoryAttr mandAttr : mandAttrMap.values()) {
      if (!objMap.containsKey(mandAttr.getDefValueId())) {
        // for each unique attributeid, load mandatory attributes
        List<TMandatoryAttr> mandList = loader.getMandatoryAttributesForAttributeValue(mandAttr.getDefValueId());
        if ((mandList != null) && !mandList.isEmpty()) {
          objMap.put(mandAttr.getDefValueId(), getMapFromList(mandList, mandAttrMap));
        }
      }
    }
    return objMap;

  }

  private Map<Long, MandatoryAttr> getMapFromList(final List<TMandatoryAttr> list,
      final Map<Long, MandatoryAttr> mandAttrMap) {
    Map<Long, MandatoryAttr> map = new HashMap<>();
    for (TMandatoryAttr tMandatoryAttr : list) {
      map.put(tMandatoryAttr.getMaId(), mandAttrMap.get(tMandatoryAttr.getMaId()));
    }
    return map;
  }

}
