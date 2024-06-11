package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristic;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristicValue;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;


/**
 * Loader class for CharacteristicValue
 *
 * @author dmo5cob
 */
public class CharacteristicValueLoader extends AbstractBusinessObject<CharacteristicValue, TCharacteristicValue> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CharacteristicValueLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CHARACTERISTIC_VALUE, TCharacteristicValue.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CharacteristicValue createDataObject(final TCharacteristicValue entity) throws DataException {
    CharacteristicValue object = new CharacteristicValue();

    setCommonFields(object, entity);

    object.setCharId(entity.getCharValId());
    object.setValNameEng(entity.getValNameEng());
    object.setValNameGer(entity.getValNameGer());
    object.setName(getLangSpecTxt(entity.getValNameEng(), entity.getValNameGer()));
    object.setDescription(getLangSpecTxt(entity.getDescEng(), entity.getDescGer()));
    object.setDescEng(entity.getDescEng());
    object.setDescGer(entity.getDescGer());

    return object;
  }

  /**
   * @param characteristicId
   * @return
   * @throws DataException
   */
  public Map<Long, CharacteristicValue> getValuesByCharacteristic(final Long characteristicId) throws DataException {
    CharacteristicLoader chLdr = new CharacteristicLoader(getServiceData());
    Map<Long, CharacteristicValue> valMap;
    CharacteristicValue chValue;

    TCharacteristic dbAttr = chLdr.getEntityObject(characteristicId);
    valMap = new HashMap<>();

    for (TCharacteristicValue entity : dbAttr.gettCharacteristicValues()) {
      chValue = createDataObject(entity);
      valMap.put(entity.getCharValId(), chValue);
    }
    return valMap;
  }

}
