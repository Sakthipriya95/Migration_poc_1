package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedValidity;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;


/**
 * Loader class for PredefinedValidity
 *
 * @author pdh2cob
 */
public class PredefinedValidityLoader extends AbstractBusinessObject<PredefinedValidity, TPredefinedValidity> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public PredefinedValidityLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PREDEFND_VALIDITY, TPredefinedValidity.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PredefinedValidity createDataObject(final TPredefinedValidity entity) throws DataException {
    PredefinedValidity object = new PredefinedValidity();

    setCommonFields(object, entity);

    object.setGrpAttrValId(entity.getGrpAttrVal().getValueId());
    object.setValidityValueId(entity.getValidityValue().getValueId());
    object.setValidityAttrId(entity.getValidityAttribute().getAttrId());

    return object;
  }

  /**
   * Get all PredefinedValidity records in system
   *
   * @return Map. Key - id, Value - PredefinedValidity object
   * @throws DataException error while retrieving data
   */
  public Map<Long, PredefinedValidity> getAll() throws DataException {
    Map<Long, PredefinedValidity> objMap = new ConcurrentHashMap<>();
    TypedQuery<TPredefinedValidity> tQuery =
        getEntMgr().createNamedQuery(TPredefinedValidity.GET_ALL, TPredefinedValidity.class);
    List<TPredefinedValidity> dbObj = tQuery.getResultList();
    for (TPredefinedValidity entity : dbObj) {
      objMap.put(entity.getValidityId(), createDataObject(entity));
    }
    return objMap;
  }


  /**
   * @param valueIdSet
   * @return Map of Key - grp attr value id, value - Map of Key- id, value - PredefinedValidity
   * @throws DataException
   */
  public Map<Long, Map<Long, PredefinedValidity>> getPredefinedValidityForValues(final Set<Long> valueIdSet)
      throws DataException {

    Map<Long, Map<Long, PredefinedValidity>> allPredefinedValMap = new HashMap<>();
    for (Long grpAttrValueId : valueIdSet) {
      Map<Long, PredefinedValidity> predefValMapForValId = new HashMap<>();
      TabvAttrValue attrValue = new AttributeValueLoader(getServiceData()).getEntityObject(grpAttrValueId);
      List<TPredefinedValidity> predefValList = attrValue.gettGroupAttrValidity();
      if ((predefValList != null) && !predefValList.isEmpty()) {
        for (TPredefinedValidity tPredefinedValidity : predefValList) {
          predefValMapForValId.put(tPredefinedValidity.getValidityId(),
              getDataObjectByID(tPredefinedValidity.getValidityId()));
        }
        allPredefinedValMap.put(grpAttrValueId, predefValMapForValId);
      }
    }
    return allPredefinedValMap;
  }


  /**
   * Get PredefinedValidity records for a value id
   *
   * @param valueId value id
   * @return List<PredefinedValidity>
   * @throws DataException error while retrieving data
   */
  public Set<PredefinedValidity> getByValueId(final Long valueId) throws DataException {

    TypedQuery<TPredefinedValidity> tQuery = getEntMgr()
        .createNamedQuery(TPredefinedValidity.GET_BY_VALUEID, TPredefinedValidity.class).setParameter("valId", valueId);
    List<TPredefinedValidity> dbObj = tQuery.getResultList();
    Set<PredefinedValidity> objSet = new HashSet<>();
    for (TPredefinedValidity entity : dbObj) {
      objSet.add(createDataObject(entity));
    }
    return objSet;
  }
}
