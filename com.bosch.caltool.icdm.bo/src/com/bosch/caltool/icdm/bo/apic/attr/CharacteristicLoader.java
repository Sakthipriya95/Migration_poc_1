package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristic;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;


/**
 * Loader class for Characteristic
 *
 * @author dmo5cob
 */
public class CharacteristicLoader extends AbstractBusinessObject<Characteristic, TCharacteristic> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CharacteristicLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CHARACTERISTIC, TCharacteristic.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Characteristic createDataObject(final TCharacteristic entity) throws DataException {
    Characteristic object = new Characteristic();

    setCommonFields(object, entity);
    object.setName(getLangSpecTxt(entity.getCharNameEng(), entity.getCharNameGer()));
    object.setCharNameEng(entity.getCharNameEng());
    object.setCharNameGer(entity.getCharNameGer());
    object.setDescription(getLangSpecTxt(entity.getDescEng(), entity.getDescGer()));
    object.setDescEng(entity.getDescEng());
    object.setDescGer(entity.getDescGer());
    object.setFocusMatrixYn(entity.getFocusMatrixYN());

    return object;
  }

  /**
   * @return Set of Characteristic
   * @throws DataException error while retrieving data
   */
  public Set<Characteristic> getAllCharacteristic() throws DataException {
    Set<Characteristic> retSet = new HashSet<>();

    TypedQuery<TCharacteristic> tQuery =
        getEntMgr().createNamedQuery(TCharacteristic.NQ_FIND_ALL, TCharacteristic.class);

    for (TCharacteristic dbFcwpDef : tQuery.getResultList()) {
      retSet.add(createDataObject(dbFcwpDef));
    }
    return retSet;
  }

  /**
   * @return map of Attribute Characteristics
   * @throws DataException error while retrieving data
   */
  public Map<Long, Characteristic> getAttrMappedCharacteristics() throws DataException {

    Map<Long, Characteristic> attrCharacteristicMap = new HashMap<>();
    Map<Long, Attribute> allAttributeMap = new AttributeLoader(getServiceData()).getAllAttributes();
    // fill charactersitics
    for (Attribute attr : allAttributeMap.values()) {
      if (attr.getCharacteristicId() != null) {
        attrCharacteristicMap.put(attr.getCharacteristicId(), getDataObjectByID(attr.getCharacteristicId()));
      }
    }
    return attrCharacteristicMap;
  }
}
